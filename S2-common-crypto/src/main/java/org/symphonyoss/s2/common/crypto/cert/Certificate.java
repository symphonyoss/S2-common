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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.symphonyoss.s2.common.crypto.cipher.CertType;

import com.google.protobuf.ByteString;
import com.symphony.s2.model.fundamental.crypto.cipher.CipherSuite;
import com.symphony.s2.model.fundamental.crypto.cipher.IAsymmetricCipherSuite;
import com.symphony.s2.model.fundamental.crypto.cipher.SignatureVerificationException;
import com.symphony.s2.model.fundamental.crypto.cipher.UnknownCipherSuiteException;

/**
 * Concrete implementation of all types of certificate.
 * 
 * Various subclasses which implement specific certificate interfaces such as IMasterCertificate
 * expose the appropriate implementation methods in this class.
 * 
 * @author Bruce Skingle
 *
 */
@Immutable
/* package */ class Certificate implements ICertificate
{
  private static final String PKCS12 = "PKCS12";
  private static final String DEFAULT_ALIAS = "1";
  private final IAsymmetricCipherSuite asymmetricCipherSuite_;
  private final X509Certificate        x509Certificate_;
  private final List<X509Certificate>  x509CertificateChain_;
  private final PublicKey              publicKey_;
  private final PrivateKey             privateKey_;

  
  /* package */ Certificate(IAsymmetricCipherSuite asymmetricCipherSuite, X509Certificate x509Certificate,
      List<X509Certificate> x509CertificateChain, PublicKey publicKey, PrivateKey privateKey)
  {
    asymmetricCipherSuite_ = asymmetricCipherSuite;
    x509Certificate_ = x509Certificate;
    x509CertificateChain_ = x509CertificateChain;
    publicKey_ = publicKey;
    privateKey_ = privateKey;
  }
  
  /* package */ Certificate(IAsymmetricCipherSuite asymmetricCipherSuite, X509Certificate[] certs, KeyPair keyPair)
  {
    asymmetricCipherSuite_ = asymmetricCipherSuite;
    x509Certificate_ = certs[0];
    List<X509Certificate> certChain = new ArrayList<>(certs.length);
    
    for(X509Certificate cert : certs)
      certChain.add(cert);
    
    x509CertificateChain_ = Collections.unmodifiableList(certChain);
    publicKey_ = keyPair.getPublic();
    privateKey_ = keyPair.getPrivate();
  }

  /* package */ Certificate(String certChain) throws UnknownCipherSuiteException, CertificateException
  {
    if(certChain.isEmpty())
      throw new CertificateException("Cert chain is empty");
    
    x509CertificateChain_ = Collections.unmodifiableList(CertificateUtils.decode(certChain));

    x509Certificate_ = x509CertificateChain_.get(0);
    publicKey_ = x509Certificate_.getPublicKey();
    
    asymmetricCipherSuite_ = CipherSuite.get(publicKey_);
    privateKey_ = null;
  }
  
  /* package */ Certificate(Certificate other)
  {
    asymmetricCipherSuite_ = other.asymmetricCipherSuite_;
    x509Certificate_ = other.x509Certificate_;
    x509CertificateChain_ = other.x509CertificateChain_;
    publicKey_ = other.publicKey_;
    privateKey_ = other.privateKey_;
  }
  
  /* package */ Certificate(String certChain, String encryptedKey, @Nullable char[] passPhrase) throws CertificateException, UnknownCipherSuiteException
  {
    if(certChain.isEmpty())
      throw new CertificateException("Cert chain is empty");
    
    x509CertificateChain_ = Collections.unmodifiableList(CertificateUtils.decode(certChain));

    x509Certificate_ = x509CertificateChain_.get(0);
    publicKey_ = x509Certificate_.getPublicKey();
    
    asymmetricCipherSuite_ = CipherSuite.get(publicKey_);
    
    try( PEMParser pemReader = new PEMParser(new StringReader(encryptedKey)) )
    {
      Object object = pemReader.readObject();
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

      KeyPair kp;
      if (object instanceof PEMEncryptedKeyPair)
      {
        if(passPhrase == null)
          throw new CertificateException("Key is encrypted and passPhrase is null");
        
        // Encrypted key - we will use provided password
        PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) object;
        // uses the password to decrypt the key
        PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(passPhrase);
        kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
      }
      else if (object instanceof PEMKeyPair)
      {
        // Unencrypted key - no password needed
        PEMKeyPair ukp = (PEMKeyPair) object;
        kp = converter.getKeyPair(ukp);
      }
      else if(object == null)
        throw new CertificateException("Unable to read private key.");
      else
        throw new CertificateException("Unable to read private key, read a " + object.getClass());
      
      if(!kp.getPublic().equals(getPublicKey()))
        throw new CertificateException("KeyPair does not match public key in given certificate.");
      
      privateKey_ = kp.getPrivate();
    }
    catch (IOException e)
    {
      throw new CertificateException(e);
    }
  }
  
  /* package */ Certificate(X509Certificate[] certChain, PrivateKey privateKey) throws CertificateException, UnknownCipherSuiteException
  {
    if(certChain.length == 0)
      throw new CertificateException("Cert chain is empty");
    
    List<X509Certificate> certList = new ArrayList<>(certChain.length);
    
    for(X509Certificate c : certChain)
      certList.add(c);
    
    x509CertificateChain_ = Collections.unmodifiableList(certList);

    x509Certificate_ = x509CertificateChain_.get(0);
    publicKey_ = x509Certificate_.getPublicKey();
    
    asymmetricCipherSuite_ = CipherSuite.get(publicKey_);
    
    privateKey_ = privateKey;
  }
  
  public IIntermediateCertificate createIntermediateCert(CertificateBuilder builder) throws CertificateException
  {
    IOpenCertificate cert = builder
        .withCertType(CertType.Intermediate)
        .build();
    
    return (IIntermediateCertificate) cert;
  }
  
  public ISigningCertificate createUserSigningCert(CertificateBuilder builder) throws CertificateException
  {
    IOpenCertificate cert = builder
        .withCertType(CertType.UserSigning)
        .build();
    
    return (ISigningCertificate) cert;
  }

  @Override
  public List<X509Certificate> getX509CertificateChain()
  {
    return x509CertificateChain_;
  }

  @Override
  public PublicKey getPublicKey()
  {
    return publicKey_;
  }
  
  public PrivateKey getPrivateKey()
  {
    return privateKey_;
  }

  @Override
  public X509Certificate getX509Certificate()
  {
    return x509Certificate_;
  }
  
  public byte[] sign(byte[] data) throws GeneralSecurityException
  {
    return getAsymmetricCipherSuite().sign(data, privateKey_);
  }

  public void verifySignature(ByteString signature, byte[] payload) throws SignatureVerificationException
  {
    verifySignature(signature.toByteArray(), payload);
  }

  public void verifySignature(byte[] signature, byte[] payload) throws SignatureVerificationException
  {
    getAsymmetricCipherSuite().verifySignature(signature, payload, getPublicKey());
  }

  @Override
  public IAsymmetricCipherSuite getAsymmetricCipherSuite()
  {
    return asymmetricCipherSuite_;
  }
  
//  public IOpenCertificate createIntermediateCert(
//      X500NameBuilder nameBuilder,
//      Date notBefore, Date notAfter,
//      BigInteger certificateId) 
//  {
//    try
//    {
//
//      X500Name principal = nameBuilder.build();
//      
//      GeneralName[] subjectAlternativeNames = null;
//  
//      KeyPair pair = getAsymmetricCipherSuite().generateKeyPair();
//
//      X509Certificate[] certs = createCertChain();
//    
//      certs[0] = getAsymmetricCipherSuite().createCert(subjectAlternativeNames, pair.getPublic(), principal, 
//          notBefore, notAfter, 
//          null, //environment.getOcspUrl(),
//          getPrivateKey(), 
//          getX509Certificate(), 
//          certificateId,
//          //S2Oid.getOid(Policy.Internal), S2Oid.getUrl(Policy.Internal),
//          null, null,
//          CertType.Intermediate);
//            
//      
//      
//      return new IntCert(getAsymmetricCipherSuite(), certs, pair);
//    }
//    catch( CertIOException | OperatorCreationException | CertificateException e)
//    {
//      throw new ProgramFault("Failed to create certificate", e);
//    }
//  }
  
//  private X509Certificate[] createCertChain()
//  {
//    List<X509Certificate> signerCerts = getX509CertificateChain();
//    X509Certificate[] certs = new X509Certificate[1 + signerCerts.size()];
//    
//    for(int index=0 ; index<signerCerts.size() ; index++)
//      certs[index+1] = signerCerts.get(index);
//    
//    return certs;
//  }
  
  public void storeUserKeystore(File file, char[] password) throws IOException, GeneralSecurityException
  {
    store(file, PKCS12, DEFAULT_ALIAS, password);
  }
  
  public void store(File file, String storeType, String alias, char[] password) throws IOException, GeneralSecurityException
  {
    KeyStore keyStore = KeyStore.getInstance(storeType);
    
    keyStore.load(null);
    
    if(privateKey_ == null)
    {
      keyStore.setCertificateEntry(alias, x509Certificate_);
    }
    else
    {
      keyStore.setKeyEntry(alias, privateKey_, password,
          x509CertificateChain_.toArray(new X509Certificate[x509CertificateChain_.size()]));
    }
    
    try(FileOutputStream out = new FileOutputStream(file))
    {
      keyStore.store(out, password);
    }
  }
}
