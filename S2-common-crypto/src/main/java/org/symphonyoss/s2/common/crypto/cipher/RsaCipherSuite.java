/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 * 
 * Includes public domain material developed by Immutify Limited.
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
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.bouncycastle.operator.OperatorCreationException;



/* package */ class RsaCipherSuite extends AbstractAsymmetricCipherSuite implements IAsymmetricCipherSuite
{
	private static final String	KEY_ALGORITHM			= "RSA";
	private static final String	SIGNATURE_ALGORITHM		= "SHA256WithRSA";
	private static final String	PROVIDER				= "BC";
	private static final String	WRAPPING_CIPHER_SPEC	= "RSA/NONE/OAEPWithSHA256AndMGF1Padding";
	
	private int					keySize_;
	private KeyPairGenerator	keyGen_				= KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);
	private SecureRandom		rand_				= new SecureRandom();

	public RsaCipherSuite(AsymmetricCipher id, int keySize) throws NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException
	{
	  super(id);
		keySize_ = keySize;
		keyGen_.initialize(getKeySize(), rand_);
	}

	@Override
	public String getSignatureAlgorithm()
	{
		return SIGNATURE_ALGORITHM;
	}

	@Override
	public int getKeySize()
	{
		return keySize_;
	}

	@Override
	public String getKeyAlgorithm()
	{
		return KEY_ALGORITHM;
	}

	@Override
	public Cipher getCipher() throws GeneralSecurityException {
		return Cipher.getInstance(WRAPPING_CIPHER_SPEC, PROVIDER);
	}

	@Override
	public KeyPair generateKeyPair()
	{
		return keyGen_.generateKeyPair();
	}

	@Override
	public byte[] wrap(SecretKey key, PublicKey userKey) throws GeneralSecurityException
	{
		Cipher	cipher = getCipher();
        
    cipher.init(Cipher.WRAP_MODE, userKey, rand_);
        
    return cipher.wrap(key);
	}
	
	@Override
	public SecretKey	unwrap(byte[] cipherText, PrivateKey userPrivateKey, ISymmetricCipherSuite symmetricCipherSuite) throws GeneralSecurityException
	{
		Cipher	cipher =  getCipher();
        
    cipher.init(Cipher.UNWRAP_MODE, userPrivateKey, rand_);
    
    return (SecretKey)cipher.unwrap(cipherText, symmetricCipherSuite.getKeyAlgorithm(), Cipher.SECRET_KEY);
	}

  @Override
  public int getKeySize(PublicKey key) throws InvalidKeyException
  {
    if(key instanceof RSAPublicKey)
    {
      return ((RSAPublicKey)key).getModulus().bitLength();
    }
    
    throw new InvalidKeyException("Not an RSA Key");
  }
  
  @Override
  public int getKeySize(PrivateKey key) throws InvalidKeyException
  {
    if(key instanceof RSAPrivateKey)
    {
      return ((RSAPrivateKey)key).getModulus().bitLength();
    }
    
    throw new InvalidKeyException("Not an RSA Key");
  }
}
