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
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.SecretKey;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

/**
 * A public key CipherSuite.
 * 
 * @author Bruce Skingle
 *
 */
public interface IAsymmetricCipherSuite extends ICipherSuite
{
  /**
   * Get the unique ID for this cipher suite.
   * 
   * @return The unique ID of this cipher suite.
   * 
   */
  AsymmetricCipher  getId();
  
  /**
   * Validate the given KeyPair and throw an exception if it is not suitable for
   * use with this CipherSuite.
   * 
   * @param keyPair A KeyPair to validate.
   * @throws InvalidKeyException If the given KeyPair is incompatible with the
   * current CipherSuite.
   */
  void validateKey(KeyPair keyPair) throws InvalidKeyException;
  
  /**
   * Validate the given key and throw an exception if it is not suitable for
   * use with this CipherSuite.
   * 
   * @param key A key to validate.
   * @throws InvalidKeyException If the given KeyPair is incompatible with the
   * current CipherSuite.
   */
  void validateKey(PrivateKey key) throws InvalidKeyException;
  
  /**
   * Validate the given key and throw an exception if it is not suitable for
   * use with this CipherSuite.
   * 
   * @param key A key to validate.
   * @throws InvalidKeyException If the given KeyPair is incompatible with the
   * current CipherSuite.
   */
  void validateKey(PublicKey key) throws InvalidKeyException;
  
  /**
   * Return the name of the SignatureAlgorithm used by this CipherSuite.
   * @return The name of the SignatureAlgorithm used by this CipherSuite.
   */
	String getSignatureAlgorithm();
	
	PKCS10CertificationRequest createCSR(X500Name subject, PublicKey publicKey, PrivateKey privateKey, GeneralName[] subjectAlternateNames) throws OperatorCreationException, IOException;

	KeyPair generateKeyPair();
	
	/**
   * Create a self signed certificate for the given principal.
	 * @param keyPair 
   *          The public key to be associate with the certificate and the private key to
   *          sign the certificate with.
   * @param subject
   *          Subject principal.
   * @param validDays
   *          Number of days from now for which the certificate will be valid.
   * @return A self signed X509v1 certificate.
	 * @throws InvalidKeyException  If the given keys are incompatible with this CipherSuite.
   */
	public X509Certificate createSelfSignedCert(KeyPair keyPair, X500Name subject, int validDays) throws InvalidKeyException;

	/**
   * Sign the given data.
   * 
   * @param data
   *          Data to be signed
   * @param privateKey
   *          Signing key.
   * @return Encoded bytes of signature
	 * @throws InvalidKeyException  If the given keys are incompatible with this CipherSuite. 
   */
	byte[]	sign(byte[] data, PrivateKey privateKey) throws InvalidKeyException;
	
	/**
   * Sign the given data and return a base64 encoded string of the signature.
   * 
   * @param data
   *          Data to be signed
   * @param privateKey
   *          Private signing key.
   * @return Base64 encoded bytes of signature as a String
	 * @throws InvalidKeyException  If the given keys are incompatible with this CipherSuite. 
   */
	String  sign(String data, PrivateKey privateKey) throws InvalidKeyException;
	
	void verifySignature(byte[] encodedSignature, byte[] data, Certificate certificate) throws SignatureVerificationException;
	
	void verifySignature(byte[] encodedSignature, byte[] data, PublicKey publicKey) throws SignatureVerificationException;

	/**
	 * Generate a Master, self signed certificate.
	 * @param pubKey		Public key for principal.
	 * @param privKey		Private key for principal, used to sign the certificate.
	 * @param principal		The subject of the certificate.
	 * @param notBefore		Validity start date.
	 * @param notAfter		Validity end date.
	 * @param ocspUrl 		If non-null then this is added as the OCSP URL for the certificate.
	 * @param serialNumber	Certificate serial number, should be unique for this CA.
	 * @return				A self signed X509v3 certificate signing certificate.
	 */
	public X509Certificate createMasterCert(PublicKey pubKey, PrivateKey privKey, 
			X500Name principal, 
			Date notBefore, Date notAfter,
			URL ocspUrl, 
			BigInteger serialNumber);

	/**
	 * Generate an end user certificate.
	 * 
	 * @param subjectRfc822AlternativeName  If non-null then this is added as the subject's email address
	 * @param pubKey					               Public key for principal.
	 * @param principal					           The subject of the certificate.
	 * @param notBefore					           Validity start date.
	 * @param notAfter						           Validity end date.
	 * @param ocspUrl 						           If non-null then this is added as the OCSP URL for the certificate.
	 * @param caPrivKey					           Private key for the CA, used to sign the certificate.
	 * @param caCert						             The certificate relating to the caPrivKey
	 * @param serialNumber					         Certificate serial number, should be unique for this CA.
	 * @param policyOid						         Optional OID for certificate policy, needs policyUrl as well.
	 * @param policyUrl						         Optional URL for certificate policy, needs policyOid as well.
	 * @param certificateType				       Type of certificate required, one of
	 *                                      UserEncryption, UserSigning or Server
	 * 
	 * @return								An X509v3 user or server certificate signed by the given CA.
	 * @throws InvalidKeyException  If the given keys are incompatible with this CipherSuite. 
	 */
	public X509Certificate createCert(
			String subjectRfc822AlternativeName,
			PublicKey pubKey,
			X500Name principal, 
			Date notBefore, Date notAfter,
			URL ocspUrl,
			PrivateKey caPrivKey, 
			X509Certificate caCert, 
			BigInteger serialNumber,
			String policyOid, URL policyUrl,
			CertType certificateType
			) throws InvalidKeyException;
	
	/**
	 * Generate an end user certificate.
	 * 
	 * @param subjectAlternativeNames		If non-null then this array of GeneralName is added as the subject's alternative names.
	 * @param pubKey                   Public key for principal.
	 * @param principal                The subject of the certificate.
	 * @param notBefore                Validity start date.
	 * @param notAfter                 Validity end date.
	 * @param ocspUrl                  If non-null then this is added as the OCSP URL for the certificate.
	 * @param caPrivKey                Private key for the CA, used to sign the certificate.
	 * @param caCert                   The certificate relating to the caPrivKey
	 * @param serialNumber             Certificate serial number, should be unique for this CA.
	 * @param policyOid						    Optional OID for certificate policy, needs policyUrl as well.
	 * @param policyUrl						    Optional URL for certificate policy, needs policyOid as well.
	 * @param certificateType				  Type of certificate required, one of
	 * 										            UserEncryption, UserSigning or Server
	 * 
	 * @return								          An X509v3 user or server certificate signed by the given CA.
	 * @throws InvalidKeyException If the given keys are incompatible with this CipherSuite.
	 */
	X509Certificate createCert(
	    GeneralName[] subjectAlternativeNames,
	    PublicKey pubKey,
	    X500Name principal, 
	    Date notBefore, Date notAfter,
	    URL ocspUrl,
	    PrivateKey caPrivKey, 
	    X509Certificate caCert, 
	    BigInteger serialNumber,
	    String policyOid, URL policyUrl,
	    CertType certificateType
	    ) throws InvalidKeyException;
	
  /**
  * Generate an end user certificate from a CSR.
  * If principal is null then the name and subjectAlternativeNames from the CSR are used, otherwise they are ignored.
  * The validity of the CSR is checked regardless.
  * 
  * @param subjectAlternativeNames   If non-null then this array of GeneralName is added as the subject's alternative names.
  * @param csr                       A Certificate Signing Request
  * @param principal                 The subject of the certificate, if null then the subject in the CSR is used.
  * @param notBefore                 Validity start date.
  * @param notAfter                  Validity end date.
  * @param ocspUrl                   If non-null then this is added as the OCSP URL for the certificate.
  * @param caPrivKey                 Private key for the CA, used to sign the certificate.
  * @param caCert                    The certificate relating to the caPrivKey
  * @param serialNumber              Certificate serial number, should be unique for this CA.
  * @param policyOid                 Optional OID for certificate policy, needs policyUrl as well.
  * @param policyUrl                 Optional URL for certificate policy, needs policyOid as well.
  * @param certificateType           Type of certificate required, one of
  *                                  UserEncryption, UserSigning or Server
  * 
  * @return                          An X509v3 user or server certificate signed by the given CA.
   * @throws InvalidKeyException  If the given keys are incompatible with this CipherSuite. 
   * @throws SignatureVerificationException If the CSR signature is not valid or cannot ve verified.
  */
  public X509Certificate createCert(
      GeneralName[] subjectAlternativeNames,
      PKCS10CertificationRequest csr,
      X500Name principal, 
      Date notBefore, Date notAfter,
      URL ocspUrl,
      PrivateKey caPrivKey, 
      X509Certificate caCert, 
      BigInteger serialNumber,
      String policyOid, URL policyUrl,
      CertType certificateType
      ) throws InvalidKeyException, SignatureVerificationException;
			
	/**
	 * Generate an Attribute Certificate.
	 * 
	 * @param clientCert		User certificate for which this attribute applies.
	 * @param notBefore			Validity start date.
	 * @param notAfter			Validity end date.
	 * @param ocspUrl 			If non-null then this is added as the OCSP URL for the certificate.
	 * @param caPrivKey			Private key for the CA, used to sign the certificate.
	 * @param caCert			The certificate relating to the caPrivKey
	 * @param serialNumber		Certificate serial number, should be unique for this CA.
	 * @param policyOid			Optional OID for certificate policy, needs policyUrl as well.
	 * @param policyUrl			Optional URL for certificate policy, needs policyOid as well.
	 * @param attributeId		OID of the attribute.
	 * @param attributeValue	Value of the attribute.
	 * @return               An X509v2 Attribute certificate
	 */
	public X509AttributeCertificateHolder createAttributeCert(
			X509Certificate	clientCert,
			Date notBefore, Date notAfter,
			URL ocspUrl,
			PrivateKey caPrivKey, 
			X509Certificate caCert, 
			BigInteger serialNumber,
			String policyOid, URL policyUrl,
			String	attributeId,
			String	attributeValue
			);

	/**
	 * Wrap the given symmetric key by encrypting with the given user's public key.
	 * 
	 * @param key		Key to be wrapped.
	 * @param userKey	Public key of user for whom it is wrapped. Only they can unwrap using their secret key.
	 * 
	 * @return	ciphertext of wrapped key.
	 * @throws GeneralSecurityException 
	 */
	byte[] wrap(SecretKey key, PublicKey userKey) throws GeneralSecurityException;

	/**
	 * Unwrap the given encrypted SecretKey
	 * 
	 * @param cipherText				Encrypted key to be unwrapped.
	 * @param userPrivateKey			Wrapping key.
	 * @param symmetricCipherSuite		Wrapping cipherSuite
	 * @return	The SecretKey.
	 * 
	 * @throws GeneralSecurityException
	 */
	SecretKey unwrap(byte[] cipherText, PrivateKey userPrivateKey, ISymmetricCipherSuite symmetricCipherSuite) throws GeneralSecurityException;

	String		publicKeyToDER(PublicKey key) throws IOException, GeneralSecurityException;
	
	PublicKey		publicKeyFromDER(String der) throws IOException, GeneralSecurityException;
	
	String		privateKeyToDER(PrivateKey key) throws IOException, GeneralSecurityException;
	
	PrivateKey		privateKeyFromDER(String der) throws IOException, GeneralSecurityException;

  int getKeySize(PublicKey key) throws InvalidKeyException;
  int getKeySize(PrivateKey key) throws InvalidKeyException;
}
