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

import javax.crypto.Cipher;

/**
 * Created by sergey on 8/27/16.
 */
/* package */ class GenericCipherText
{

  private final byte[] iv;
  private final byte[] authData;
  private final byte[] cipherText;

  public GenericCipherText(SymmetricCipher id, Cipher cipher, byte[] cipherData) throws GeneralSecurityException
  {
    if (cipherData == null)
    {
      throw new GeneralSecurityException("cipherText cannot be null");
    }
    int index = 0;
    int ciphertextLength;
    switch (id)
    {
      case AES256_GCM:
        // iv + authData + cipherText
        this.iv = new byte[cipher.getBlockSize()];
        System.arraycopy(cipherData, index, iv, 0, iv.length);
        index += iv.length;
        authData = new byte[cipher.getBlockSize()];
        System.arraycopy(cipherData, index, authData, 0, authData.length);
        index += authData.length;
        break;
      case AES128_CBC:
      case AES192_CBC:
      case AES256_CBC:
      default:
        this.authData = null;
        this.iv = new byte[cipher.getBlockSize()];
        System.arraycopy(cipherData, index, iv, 0, iv.length);
        index += iv.length;
    }
    ciphertextLength = cipherData.length - index;
    if (ciphertextLength <= 0)
    {
      throw new GeneralSecurityException("cipherText cannot be empty");
    }
    cipherText = new byte[ciphertextLength];
    System.arraycopy(cipherData, index, cipherText, 0, ciphertextLength);

  }

  public byte[] getIv()
  {
    return iv;
  }

  public byte[] getAuthData()
  {
    return authData;
  }

  public byte[] getCipherText()
  {
    return cipherText;
  }
}
