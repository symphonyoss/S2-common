/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 * 
 * Includes public domain material developed by Immutify Limited.
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

package org.symphonyoss.s2.common.crypto.cipher;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import javax.crypto.SecretKey;

public interface ISymmetricCipherSuite extends ICipherSuite
{
  /**
   * Get the unique ID for this cipher suite.
   * 
   * @return The unique ID of this cipher suite.
   * 
   */
  SymmetricCipher  getId();
  
	SecretKey generateKey();

	/**
	 * Encrypt the given binary data with the given key and return a Base64 
	 * encoded String containing the encrypted form.
	 * 
	 * @param secretKey	Encryption key
	 * @param data		Plain text.
	 * @return			  ciphertext
	 * 
	 * @throws GeneralSecurityException
	 */
	byte[] encrypt(SecretKey secretKey, byte[] data) throws GeneralSecurityException;

	/**
	 * Decrypt the given Base64 encoded cipher test using the given key.
	 * 
	 * @param key			Decryption key.
	 * @param cipherText	ciphertext.
	 * @return				Plaintext.
	 * 
	 * @throws GeneralSecurityException
	 */
	byte[] decrypt(SecretKey key, byte[] cipherText) throws GeneralSecurityException;

	IDecryptedInputStream createDecryptedInputStream(SecretKey key, byte[] encryptedData) throws GeneralSecurityException, IOException;

	IEncryptedOutputStream createEncryptedOutputStream(SecretKey key) throws IOException;
	
	/**
	 * Wrap the given asymmetric key by encrypting with the given user's secret key.
	 * 
	 * @param key		Key to be wrapped.
	 * @param userKey	Secret key of user for whom it is wrapped.
	 * 
	 * @return	ciphertext of wrapped key.
	 * @throws GeneralSecurityException 
	 */
	byte[] wrap(PrivateKey key, SecretKey userKey) throws GeneralSecurityException;

	/**
	 * Unwrap the given encrypted PrivateKey
	 * 
	 * @param cipherText				Encrypted key to be unwrapped.
	 * @param userKey					Wrapping key.
	 * @param cipherSuite				Wrapping cipherSuite
	 * @return	The SecretKey.
	 * 
	 * @throws GeneralSecurityException
	 */
	PrivateKey unwrap(byte[] cipherText, SecretKey userKey, IAsymmetricCipherSuite cipherSuite) throws GeneralSecurityException;

  int getKeySize(SecretKey key);
}
