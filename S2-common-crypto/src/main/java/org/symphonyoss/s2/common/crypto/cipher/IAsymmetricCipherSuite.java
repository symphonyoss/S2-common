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
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.SecretKey;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;

public interface IAsymmetricCipherSuite extends ICipherSuite
{
  /**
   * Get the unique ID for this cipher suite.
   * 
   * @return The unique ID of this cipher suite.
   * 
   */
  AsymmetricCipher  getId();
  
	String getSignatureAlgorithm();
	
	PKCS10CertificationRequest createCSR(X500Name subject, PublicKey publicKey, PrivateKey privateKey, GeneralName[] subjectAlternateNames) throws OperatorCreationException, IOException;

	KeyPair generateKeyPair();
	
	/**
	 * Create a self signed certificate for the given principal.
	 * 
	 * @param pubKey	Public key.
	 * @param privKey	Private Key.
	 * @param subject	Subject principal.
	 * @param validDays	Number of days from now for which the certificate will be valid.
	 * @return	A self signed X509v1 certificate.
	 * @throws OperatorCreationException 
	 * @throws NoSuchProviderException 
	 * @throws CertificateException 
	 * @throws IOException 
	 */
	public X509Certificate createSelfSignedCert(PublicKey pubKey, PrivateKey privKey, X500Name subject, int validDays) throws OperatorCreationException, CertificateException, NoSuchProviderException, IOException;

	/**
   * Sign the given data.
   * @param data  Data to be signed
   * @return  Encoded bytes of signature
   * @throws NoSuchAlgorithmException 
   * @throws InvalidKeyException 
   * @throws SignatureException 
   */
	byte[]	sign(byte[] data, PrivateKey privateKey) throws GeneralSecurityException;
	
	/**
   * Sign the given data and return a base64 encoded string of the signature.
   * @param data  Data to be signed
   * @return  Base64 encoded bytes of signature as a String
   * @throws NoSuchAlgorithmException 
   * @throws InvalidKeyException 
   * @throws SignatureException 
   */
	String  sign(String data, PrivateKey privateKey) throws GeneralSecurityException;
	
	void	verifySignature(byte[] encodedSignature, byte[] data, Certificate certificate) throws SignatureVerificationException;
	
	void 	verifySignature(byte[] encodedSignature, byte[] data, PublicKey publicKey) throws SignatureVerificationException;

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
	 * @throws CertIOException 
	 * @throws OperatorCreationException 
	 * @throws CertificateException 
	 */
	public X509Certificate createMasterCert(PublicKey pubKey, PrivateKey privKey, 
			X500Name principal, 
			Date notBefore, Date notAfter,
			String ocspUrl, 
			BigInteger serialNumber)
	throws CertIOException, OperatorCreationException, CertificateException;

	/**
	 * Generate an end user certificate.
	 * 
	 * @param subjectRfc822AlternativeName	If non-null then this is added as the subject's email address
	 * @param pubKey						Public key for principal.
	 * @param principal						The subject of the certificate.
	 * @param notBefore						Validity start date.
	 * @param notAfter						Validity end date.
	 * @param ocspUrl 						If non-null then this is added as the OCSP URL for the certificate.
	 * @param caPrivKey						Private key for the CA, used to sign the certificate.
	 * @param caCert						The certificate relating to the caPrivKey
	 * @param serialNumber					Certificate serial number, should be unique for this CA.
	 * @param policyOid						Optional OID for certificate policy, needs policyUrl as well.
	 * @param policyUrl						Optional URL for certificate policy, needs policyOid as well.
	 * @param certificateType				Type of certificate required, one of
	 * 										UserEncryption, UserSigning or Server
	 * 
	 * @return								An X509v3 user or server certificate signed by the given CA.
	 * 
	 * @throws CertIOException 
	 * @throws CertificateEncodingException 
	 * @throws OperatorCreationException 
	 * @throws CertificateException 
	 */
	public X509Certificate createCert(
			String subjectRfc822AlternativeName,
			PublicKey pubKey,
			X500Name principal, 
			Date notBefore, Date notAfter,
			String ocspUrl,
			PrivateKey caPrivKey, 
			X509Certificate caCert, 
			BigInteger serialNumber,
			String policyOid, String policyUrl,
			CertType certificateType

			) 
	throws CertIOException, CertificateEncodingException, OperatorCreationException, CertificateException ;
	
	/**
	 * Generate an end user certificate.
	 * 
	 * @param subjectAlternativeNames		If non-null then this array of GeneralName is added as the subject's alternative names.
	 * @param pubKey						Public key for principal.
	 * @param principal						The subject of the certificate.
	 * @param notBefore						Validity start date.
	 * @param notAfter						Validity end date.
	 * @param ocspUrl 						If non-null then this is added as the OCSP URL for the certificate.
	 * @param caPrivKey						Private key for the CA, used to sign the certificate.
	 * @param caCert						The certificate relating to the caPrivKey
	 * @param serialNumber					Certificate serial number, should be unique for this CA.
	 * @param policyOid						Optional OID for certificate policy, needs policyUrl as well.
	 * @param policyUrl						Optional URL for certificate policy, needs policyOid as well.
	 * @param certificateType				Type of certificate required, one of
	 * 										UserEncryption, UserSigning or Server
	 * 
	 * @return								An X509v3 user or server certificate signed by the given CA.
	 * @throws CertIOException 
	 * @throws OperatorCreationException 
	 * @throws CertificateException 

	 */
	X509Certificate createCert(
			GeneralName[] subjectAlternativeNames,
			PublicKey pubKey,
			X500Name principal, 
			Date notBefore, Date notAfter,
			String ocspUrl,
			PrivateKey caPrivKey, 
			X509Certificate caCert, 
			BigInteger serialNumber,
			String policyOid, String policyUrl,
			CertType certificateType

			) throws CertIOException, OperatorCreationException, CertificateException;
	
  /**
  * Generate an end user certificate from a CSR.
  * If principal is null then the name and subjectAlternativeNames from the CSR are used, otherwise they are ignored.
  * The validity of the CSR is checked regardless.
  * 
  * @param subjectAlternativeNames   If non-null then this array of GeneralName is added as the subject's alternative names.
  * @param pubKey                    Public key for principal.
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
   * @throws CertIOException 
   * @throws OperatorCreationException 
   * @throws CertificateException 
   * @throws NoSuchAlgorithmException 
   * @throws InvalidKeyException 
   * @throws PKCSException 
   */
  public X509Certificate createCert(
      GeneralName[] subjectAlternativeNames,
      PKCS10CertificationRequest csr,
      X500Name principal, 
      Date notBefore, Date notAfter,
      String ocspUrl,
      PrivateKey caPrivKey, 
      X509Certificate caCert, 
      BigInteger serialNumber,
      String policyOid, String policyUrl,
      CertType certificateType
      ) throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, OperatorCreationException, PKCSException, CertIOException;
			
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
	 * @return
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws OperatorCreationException 
 
	 */
	public X509AttributeCertificateHolder createAttributeCert(
			X509Certificate	clientCert,
			Date notBefore, Date notAfter,
			String ocspUrl,
			PrivateKey caPrivKey, 
			X509Certificate caCert, 
			BigInteger serialNumber,
			String policyOid, String policyUrl,
			String	attributeId,
			String	attributeValue
			) 
	throws OperatorCreationException, CertificateException, IOException;

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

  int getKeySize(PublicKey key) throws UnknownCipherSuiteException;
}
