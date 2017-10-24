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

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.bouncycastle.util.Arrays;

/**
 * Created by sergey on 8/27/16.
 */
/* package */ class Aes256GCMCipherSuite extends AesCipherSuite
{

  public Aes256GCMCipherSuite(SymmetricCipher id) throws NoSuchAlgorithmException, NoSuchProviderException
  {
    super(id, AES_GCM_NoPadding_CIPHER, 256);
  }

  @Override
  public byte[] encrypt(SecretKey secretKey, byte[] data) throws GeneralSecurityException
  {
    Cipher cipher = getCipher();
    byte[] iv = new byte[cipher.getBlockSize()];
    rand_.nextBytes(iv);
    GCMParameterSpec spec = new GCMParameterSpec(cipher.getBlockSize() * 8, iv);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
    byte[] authData = new byte[cipher.getBlockSize()];
    rand_.nextBytes(authData);
    cipher.updateAAD(authData);
    byte[] encryptedData = cipher.doFinal(data);
    byte[] cipherText = Arrays.concatenate(iv, authData);
    cipherText = Arrays.concatenate(cipherText, encryptedData);
    return cipherText;
  }

  @Override
  public byte[] decrypt(SecretKey key, byte[] cipherText) throws GeneralSecurityException
  {
    Cipher cipher = getCipher();
    GenericCipherText genericCipherText = new GenericCipherText(getId(), cipher, cipherText);
    GCMParameterSpec spec = new GCMParameterSpec(cipher.getBlockSize() * 8, genericCipherText.getIv());
    cipher.init(Cipher.DECRYPT_MODE, key, spec);
    cipher.updateAAD(genericCipherText.getAuthData());

    byte[] clearText = cipher.doFinal(genericCipherText.getCipherText());

    return clearText;
  }

}
