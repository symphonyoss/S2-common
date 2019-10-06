/*
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.fault.TransactionFault;

/**
 * General certificate utility functions.
 * 
 * @author Bruce Skingle
 *
 */
/* package */ class CertificateUtils
{
  private static      JcaX509CertificateConverter   x509Converter_  = new JcaX509CertificateConverter().setProvider("BC");

  /**
   * Return the PEM encoded string representation of the given certificates.
   * 
   * @param x509CertificateChain An array of X509Certificate objects
   * 
   * @return The PEM encoding of the given certificates.
   */
  public static String encode(X509Certificate[] x509CertificateChain)
  {
    try
    {
      ByteArrayOutputStream bos       = new ByteArrayOutputStream();
      try( JcaPEMWriter          pemWriter = new JcaPEMWriter(new OutputStreamWriter(bos))) {

        for(X509Certificate cert : x509CertificateChain)
          pemWriter.writeObject(cert);
      }

      return bos.toString(StandardCharsets.UTF_8.name());
    }
    catch(IOException e)
    {
      throw new TransactionFault("Failed to encode certificate", e);
    }
  }
  
  /**
   * Decode the given PEM format sequence of certificates.
   * 
   * @param certData A String containing one or more certificates.
   * 
   * @return The corresponding X509Certificate objects.
   * 
   * @throws CertificateException If the input is invalid.
   */
  public static List<X509Certificate>  decode(String certData) throws CertificateException
  {
    try( PEMParser pemReader = new PEMParser(new StringReader(certData)) )
    {
      List<X509Certificate> result        = new ArrayList<>();
      Object                certificate;
      
      while((certificate = pemReader.readObject()) != null)
      {
        if(certificate instanceof X509Certificate)
          result.add((X509Certificate)certificate);
        else if(certificate instanceof X509CertificateHolder)
        {
          synchronized(x509Converter_)
          {
            result.add(x509Converter_.getCertificate((X509CertificateHolder)certificate));
          }
        }
        else
          throw new CertificateException("Certificate decode resulted in " + certificate.getClass());

      }
      return result;
    }
    catch(IOException e)
    {
      throw new TransactionFault("Failed to decode certificate", e);
    }
  }
  
  /**
   * Return the common name element of the given X509Principal.
   * 
   * @param principal An X509Principal.
   * 
   * @return The Common Name.
   */
  public static String getCommonName(X500Principal principal)
  {
    // parse the CN out from the DN (distinguished name)
    Pattern p = Pattern.compile("(^|,)CN=([^,]*)(,|$)");
    Matcher m = p.matcher(principal.getName());

    m.find();

    try
    {
      return m.group(2);
    }
    catch(IllegalStateException e)
    {
      return principal.getName();
    }
  }
  
  /**
   * Return the fingerprint of the given certificate.
   * 
   * The fingerprint is the Hex string representation of the SHA-1 hash of the 
   * DER encoding of the certificate.
   * 
   * @param cert An X509Certificate.
   * @return The fingerprint of the given certificate.
   * 
   * @throws CertificateEncodingException If the DER encoding of the certificate cannot be 
   * calculated.
   */
  public static String getFingerPrint(X509Certificate cert) throws CertificateEncodingException
  {
    try
    {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      byte[] der = cert.getEncoded();
      md.update(der);
      byte[] digest = md.digest();
      return toHex(digest);
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new CodingFault("Cannot get SHA-1", e);
    }
  }

  /**
   * Return the given byte array as a Hex string.
   * 
   * @param bytes and array of bytes
   * @return The Hex encoding of the given bytes.
   */
  public static String toHex(byte bytes[])
  {
    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    StringBuffer buf = new StringBuffer(bytes.length * 2);

    for (int i = 0; i < bytes.length; ++i)
    {
      buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
      buf.append(hexDigits[bytes[i] & 0x0f]);
    }

    return buf.toString();
  }
}
