/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The SSF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.s2.common.crypto.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.annotation.Nullable;

import com.symphony.s2.model.fundamental.crypto.cipher.UnknownCipherSuiteException;

/**
 * Public factory class for Certificates.
 * 
 * @author Bruce Skingle
 *
 */
public class CertificateFactory
{
  /**
   * Create a certificate from the given PEM format cert chain.
   *  
   * @param certChain A sequence of X509Certificate objects in PEM format.
   * 
   * @return  An ICertificate.
   * @throws CertificateException           If the input is invalid.
   * @throws UnknownCipherSuiteException  If the certificate is not a supported type.
   */
  public static ICertificate loadCertificate(String certChain) throws CertificateException, UnknownCipherSuiteException
  {
    return typedCertificate(new Certificate(certChain));
  }
  
  public static ICertificate typedCertificate(Certificate cert)
  {
    boolean signingCert = false;
    boolean certSigningCert = false;
    boolean masterSigningCert = false;
    boolean keyWrappingCert = false;
    boolean openCert = cert.getPrivateKey() != null;
    
    X509Certificate x509Cert = cert.getX509Certificate();
    
    boolean[] keyUsage = x509Cert.getKeyUsage();
  
    keyWrappingCert = openCert && hasKeyUsage(keyUsage, 2);
    signingCert = hasKeyUsage(keyUsage, 0);
    certSigningCert = openCert && hasKeyUsage(keyUsage, 5) && x509Cert.getBasicConstraints()>=0;
    masterSigningCert = openCert && certSigningCert && x509Cert.getBasicConstraints()>0;
  
    if(masterSigningCert)
    {
      if(signingCert)
      {
        if(keyWrappingCert)
        {
          return new MasterSigningWrappingCert(cert);
        }
        else
        {
          return new MasterSigningCert(cert);
        }
      }
      else
      {
        if(keyWrappingCert)
        {
          return new MasterWrappingCert(cert);
        }
        else
        {
          return new MasterCert(cert);
        }
      }
    }
    else if(certSigningCert)
    {
      if(signingCert)
      {
        if(keyWrappingCert)
        {
          return new IntSigningWrappingCert(cert);
        }
        else
        {
          return new IntSigningCert(cert);
        }
      }
      else
      {
        if(keyWrappingCert)
        {
          return new IntWrappingCert(cert);
        }
        else
        {
          return new IntCert(cert);
        }
      }
    }
    else
    {
      if(signingCert)
      {
        if(keyWrappingCert)
        {
          return new SigningWrappingCert(cert);
        }
        else if(openCert)
        {
          return new SigningCert(cert);
        }
        else
        {
          return new SignatureVerificationCert(cert);
        }
      }
      else
      {
        if(keyWrappingCert)
        {
          return new WrappingCert(cert);
        }
        else if(cert.getPrivateKey() != null)
        {
          return new OpenCert(cert);
        }
        else
        {
          return cert;
        }
      }
    }
  }

  private static boolean hasKeyUsage(boolean[] keyUsage, int i)
  {
    return keyUsage == null || (keyUsage.length >= i && keyUsage[i]);
  }

  /**
   * Create an open certificate from the given PEM format cert chain, KeyPair and passPhrase.
   * @param certChain     A sequence of X509Certificate objects in PEM format.
   * @param encryptedKey  A KeyPair in PEM format.
   * @param passPhrase    A passPhrase to decrypt the KeyPair. May be null if the KeyPair is unencrypted.
   * @return              An IOpenCertificate.
   * @throws CertificateException         If the input is invalid or cannot be read or processed.
   * @throws UnknownCipherSuiteException  If the certificate is not a supported type.
   */
  public static IOpenCertificate loadOpenCertificate(String certChain, String encryptedKey, @Nullable char[] passPhrase) throws CertificateException, UnknownCipherSuiteException
  {
    return (IOpenCertificate)typedCertificate(new Certificate(certChain, encryptedKey, passPhrase));
  }
  

  
  public static ICertificate load(File file, String storeType, @Nullable String alias, char[] password) throws IOException, GeneralSecurityException, UnknownCipherSuiteException
  {
    try(FileInputStream in = new FileInputStream(file))
    {
      return load(in, storeType, alias, password);
    }
  }
  

  
  public static ICertificate load(InputStream in, String storeType, @Nullable String alias, char[] password) throws IOException, GeneralSecurityException, UnknownCipherSuiteException
  {
    KeyStore keyStore = KeyStore.getInstance(storeType);
    
    keyStore.load(in, password);
    
    if(alias == null)
    {
      Enumeration<String> it = keyStore.aliases();
      
      if(it.hasMoreElements())
        alias = it.nextElement();
      else
        throw new IOException("Keystore is empty");
    }
    
    PrivateKey privateKey = null;
    
    if(keyStore.isKeyEntry(alias))
    {
      Key key = keyStore.getKey(alias, password);
      
      if(key instanceof PrivateKey)
        privateKey = (PrivateKey) key;
      else
        throw new CertificateException("Alias \"" + alias + "\" is a " + key.getClass().getName() + " entry.");
    }
    
    java.security.cert.Certificate[] certChain = keyStore.getCertificateChain(alias);
    X509Certificate[] x509CertChain = new X509Certificate[certChain.length];
    
    for(int i=0 ; i<certChain.length ; i++)
    {
      if(!(certChain[i] instanceof X509Certificate))
        throw new CertificateException("Alias \"" + alias + "\" is a " + certChain[i].getClass().getName() + " entry.");
      
      x509CertChain[i] = (X509Certificate) certChain[i];
    }
    
    {
      return CertificateFactory.typedCertificate(new Certificate(x509CertChain, privateKey));
    }
    
    
  }
}
