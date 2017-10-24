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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.util.Arrays;

/* package */ class AesCipherSuite extends AbstractSymmetricCipherSuite
{
	private static final String	KEY_ALGORITHM										  = "AES";
	private static final String	PROVIDER										     	= "BC";
	protected static final String	AES_CBC_PKCS7Padding_CIPHER			= "AES/CBC/PKCS7Padding";
	protected static final String	AES_GCM_NoPadding_CIPHER	      = "AES/GCM/NoPadding";

	private String 			    cipher_;
	private int					   keySize_;
	private KeyGenerator		keyGen_;
	protected SecureRandom		rand_	= new SecureRandom();

	public AesCipherSuite(SymmetricCipher id, String cipherMode, int keySize) throws NoSuchAlgorithmException, NoSuchProviderException
	{
		super(id);
		cipher_ = cipherMode;
		keySize_ = keySize;
		keyGen_	= KeyGenerator.getInstance(KEY_ALGORITHM, PROVIDER);
		keyGen_.init(keySize_, rand_);
	}

	@Override
	public String getKeyAlgorithm()
	{
		return KEY_ALGORITHM;
	}

	@Override
	public int getKeySize()
	{
		return keySize_;
	}

	@Override
	public Cipher getCipher()
			throws GeneralSecurityException {
		return Cipher.getInstance(cipher_, PROVIDER);
	}


	@Override
	public SecretKey generateKey()
	{
		return keyGen_.generateKey();
	}

	@Override
	public byte[] encrypt(SecretKey secretKey, byte[] data) throws GeneralSecurityException
	{
		Cipher 	cipher = getCipher();
		byte[]	iv = new byte[cipher.getBlockSize()];
		rand_.nextBytes(iv);

		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

		byte[]	encryptedData = cipher.doFinal(data);

		byte[] cipherText =  Arrays.concatenate(iv, encryptedData);

		return cipherText;
	}

	@Override
	public byte[] decrypt(SecretKey secretKey, byte[] cipherText) throws GeneralSecurityException
	{
		try {
			Cipher cipher = getCipher();
			GenericCipherText genericCipherText = new GenericCipherText(getId(), cipher, cipherText);
			byte[] iv = genericCipherText.getIv();
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			byte[] clearText = cipher.doFinal(genericCipherText.getCipherText());
			return clearText;
		} catch(Exception e){
			throw new GeneralSecurityException("decryption failed: " + e.getMessage());
		}
	}
	
	@Override
	public	EncryptedOutputStream	createEncryptedOutputStream(SecretKey key) throws IOException
	{
		if(key == null)
			throw new IOException("Null Key");
		
		return new EncryptedOutputStream(key, new ByteArrayOutputStream());
	}
	
    public class	EncryptedOutputStream extends AbstractEncryptedOutputStream
    {
    	private SecretKey				key_;
    	private ByteArrayOutputStream	bout_;

		private	EncryptedOutputStream(SecretKey key, ByteArrayOutputStream bout) throws IOException
    	{
    		super(bout);
    		
    		key_ = key;
    		bout_ = bout;
    	}

		@Override
    public	byte[]	getCipherText() throws GeneralSecurityException
		{
			return encrypt(key_, bout_.toByteArray());
		}
		
		@Override
		public void close() throws IOException
		{
			bout_.close();
			super.close();
		}
    }
    
	@Override
	public DecryptedInputStream createDecryptedInputStream(SecretKey key, byte[] encryptedData) throws GeneralSecurityException, IOException
	{
		if(key == null)
			throw new IOException("Null Key");
		
		if(encryptedData == null)
			throw new IOException("Null CipherText");
		
		byte[]	rData = decrypt(key, encryptedData);
		
		//printData(System.out, "Decrypted", rData);
		
		ByteArrayInputStream	bin = new ByteArrayInputStream(rData);
		
		return new DecryptedInputStream(bin);
	}
    
    public class	DecryptedInputStream extends AbstractDecryptedInputStream
    {
    	private ByteArrayInputStream	bin_;

		private DecryptedInputStream(ByteArrayInputStream bin) throws IOException
		{
			super(bin);
			bin_ = bin;
		}

		@Override
		public void close() throws IOException
		{
			bin_.close();
			super.close();
		}
    }

	@Override
	public byte[] wrap(PrivateKey key, SecretKey userKey) throws GeneralSecurityException
	{
		Cipher cipher = getCipher();
        
    cipher.init(Cipher.WRAP_MODE, userKey, rand_);
        
    return cipher.wrap(key);
	}
	
	@Override
	public PrivateKey unwrap(byte[] cipherText, SecretKey userKey, IAsymmetricCipherSuite cipherSuite) throws GeneralSecurityException
	{
		Cipher	cipher = getCipher();
        
        cipher.init(Cipher.UNWRAP_MODE, userKey, rand_);
        
        return (PrivateKey)cipher.unwrap(cipherText, cipherSuite.getKeyAlgorithm(), Cipher.PRIVATE_KEY);
	}

}
