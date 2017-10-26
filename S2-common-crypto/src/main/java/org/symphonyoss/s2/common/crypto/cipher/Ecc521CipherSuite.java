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

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.OperatorCreationException;

/* package */ class Ecc521CipherSuite extends AbstractAsymmetricCipherSuite implements IAsymmetricCipherSuite
{
	private static final String	KEY_ALGORITHM			= "EC";
	private static final String	SIGNATURE_ALGORITHM		= "SHA512withECDSA";
	private static final String	PROVIDER				= "BC";
	private static final String	CURVE_NAME				= "secp521r1";
	private static final String	WRAPPING_CIPHER_SPEC	= "ECIESwithAES";
	
	private ECGenParameterSpec	ecGenSpec;
	private KeyPairGenerator	keyGen_;
	private SecureRandom		rand_;
	
	public Ecc521CipherSuite(AsymmetricCipher id) throws NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException, InvalidAlgorithmParameterException
	{
	  super(id);
		rand_		= new SecureRandom();
		ecGenSpec	= new ECGenParameterSpec(CURVE_NAME);
		keyGen_		= KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);
		
		keyGen_.initialize(ecGenSpec, rand_);
	}
	
	@Override
	public String getKeyAlgorithm()
	{
		return KEY_ALGORITHM;
	}

	@Override
	public Cipher getCipher() throws GeneralSecurityException {
		return  Cipher.getInstance(WRAPPING_CIPHER_SPEC, PROVIDER);
	}

	@Override
	public int getKeySize()
	{
		return 521;
	}

	@Override
	public KeyPair generateKeyPair()
	{
		return keyGen_.generateKeyPair();
	}

	@Override
	public byte[] wrap(SecretKey key, PublicKey userKey) throws GeneralSecurityException
	{
		Cipher cipher = getCipher();
        
    cipher.init(Cipher.ENCRYPT_MODE, userKey, rand_);
    
    return cipher.doFinal(key.getEncoded());
	}

	@Override
	public SecretKey unwrap(byte[] cipherText, PrivateKey userPrivateKey, ISymmetricCipherSuite symmetricCipherSuite) throws GeneralSecurityException
	{
		Cipher cipher = getCipher();
        
    cipher.init(Cipher.DECRYPT_MODE, userPrivateKey, rand_);
    
    byte[] encoded = cipher.doFinal(cipherText);
    return new SecretKeySpec(encoded, symmetricCipherSuite.getKeyAlgorithm());
    }

	@Override
	public String getSignatureAlgorithm()
	{
		return SIGNATURE_ALGORITHM;
	}

	@Override
  public PublicKey		publicKeyFromDER(String der) throws IOException, GeneralSecurityException
	{
	  try(
        StringReader    reader = new StringReader(der);
        PEMParser       pemParser = new PEMParser(reader);
    )
		{
			Object o = pemParser.readObject();
			
			if (o == null || !(o instanceof SubjectPublicKeyInfo))
	        {
	        	throw new IOException("Not an OpenSSL public key");
	        }
			
			return  KeyFactory.getInstance("EC", "BC").generatePublic(new X509EncodedKeySpec(((SubjectPublicKeyInfo)o).getEncoded()));
		}
	}
	
	@Override
  public PrivateKey		privateKeyFromDER(String der) throws IOException, GeneralSecurityException
	{
		try(
		    StringReader    reader = new StringReader(der);
		    PEMParser       pemParser = new PEMParser(reader);
		)
		{
			Object o = pemParser.readObject();
			
			if (o == null || !(o instanceof PEMKeyPair))
	        {
	        	throw new IOException("Not an OpenSSL key");
	        }
			return  KeyFactory.getInstance("EC", "BC").generatePrivate(new PKCS8EncodedKeySpec(((PEMKeyPair)o).getPrivateKeyInfo().getEncoded()));
		}
	}

  @Override
  public int getKeySize(PublicKey key) throws UnknownCipherSuiteException
  {
    if(key instanceof ECPublicKey)
    {
      ECParameterSpec spec = ((ECPublicKey)key).getParams();
      
      if(spec == null)
        throw new UnknownCipherSuiteException("Key has null parameter spec");
      
      return spec.getOrder().bitLength();
    }
    
    throw new UnknownCipherSuiteException("Not an Elliptic Curve Key");
  }
}
