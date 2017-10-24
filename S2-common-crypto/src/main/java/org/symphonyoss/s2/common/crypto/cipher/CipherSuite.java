/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.symphonyoss.s2.common.crypto.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.symphonyoss.s2.common.crypto.cipher.IAsymmetricCipherSuite;
import org.symphonyoss.s2.common.crypto.cipher.ISymmetricCipherSuite;
import org.symphonyoss.s2.common.fault.ProgramFault;

public class CipherSuite
{
  private static Map<AsymmetricCipher, IAsymmetricCipherSuite> publicMap_ = new HashMap<>();
  private static Map<SymmetricCipher, ISymmetricCipherSuite>  secretMap_ = new HashMap<>();
  
  static
  {
    if(Security.getProvider("BC") == null)
    {
      Security.addProvider(new BouncyCastleProvider());
    }
  
    try
    {
      secretMap_.put(SymmetricCipher.AES256_GCM, new Aes256GCMCipherSuite(SymmetricCipher.AES256_GCM));
      secretMap_.put(SymmetricCipher.AES128_CBC, new Aes128CipherSuite(SymmetricCipher.AES128_CBC));
      secretMap_.put(SymmetricCipher.AES192_CBC, new Aes192CipherSuite(SymmetricCipher.AES192_CBC));
      secretMap_.put(SymmetricCipher.AES256_CBC, new Aes256CipherSuite(SymmetricCipher.AES256_CBC));

      publicMap_.put(AsymmetricCipher.RSA2048, new Rsa2048CipherSuite(AsymmetricCipher.RSA2048));
      publicMap_.put(AsymmetricCipher.RSA1024, new Rsa1024CipherSuite(AsymmetricCipher.RSA1024));
      publicMap_.put(AsymmetricCipher.ECC521,  new Ecc521CipherSuite(AsymmetricCipher.ECC521));
    } catch (NoSuchAlgorithmException | NoSuchProviderException | OperatorCreationException | InvalidAlgorithmParameterException e)
    {
      throw new ProgramFault(e);
    }
  }

  public static IAsymmetricCipherSuite get(AsymmetricCipher publicCipher)
  {
    return publicMap_.get(publicCipher);
  }
  
  public static ISymmetricCipherSuite get(SymmetricCipher secretCipher)
  {
    return secretMap_.get(secretCipher);
  }
  
  public static IAsymmetricCipherSuite getAsymmetricCipher()
  {
    return publicMap_.get(AsymmetricCipher.RSA2048);
  }
  
  public static ISymmetricCipherSuite getSymmetricCipher()
  {
    return secretMap_.get(SymmetricCipher.AES256_GCM);
  }
}
