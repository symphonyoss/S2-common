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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v2AttributeCertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.fault.TransactionFault;

/* package */ abstract class AbstractAsymmetricCipherSuite extends AbstractCipherSuite implements IAsymmetricCipherSuite
{
  private static final String         BC         = "BC";
  private AlgorithmIdentifier         sigAlgId_  = new DefaultSignatureAlgorithmIdentifierFinder()
      .find(getSignatureAlgorithm());
  private AlgorithmIdentifier         digAlgId_  = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId_);
  private JcaX509CertificateConverter converter_ = new JcaX509CertificateConverter().setProvider(BC);
  private JcaX509ExtensionUtils       extensionUtils_;
  private AsymmetricCipher            id_;

  public AbstractAsymmetricCipherSuite(AsymmetricCipher id)
  {
    try
    {
      id_ = id;
      DigestCalculator digestCalculator = new JcaDigestCalculatorProviderBuilder().setProvider(BC).build().get(digAlgId_);
  
      extensionUtils_ = new JcaX509ExtensionUtils(digestCalculator);
    }
    catch(OperatorCreationException e)
    {
      throw new CodingFault(e);
    }
  }

  @Override
  public AsymmetricCipher getId()
  {
    return id_;
  }

  @Override
	public X509Certificate createSelfSignedCert(KeyPair keyPair, X500Name subject, int validDays) throws InvalidKeyException
	{
		X509v1CertificateBuilder	builder = new JcaX509v1CertificateBuilder(
				subject, BigInteger.valueOf(1), 
				new Date(System.currentTimeMillis()),
				new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * validDays)),
				subject, keyPair.getPublic());
		
		validateKey(keyPair);
		
		try
		{
    		JcaContentSignerBuilder		signerBuilder 	= new JcaContentSignerBuilder(getSignatureAlgorithm()).setProvider(BC);
    		ContentSigner 				signer 			= signerBuilder.build(keyPair.getPrivate());
    		X509CertificateHolder 		holder 			= builder.build(signer);
            X509Certificate 			cert 			= converter_.getCertificate(holder);
    		
    		return cert;
		}
		catch(OperatorCreationException | CertificateException e)
		{
		  throw new TransactionFault(e);
		}
	}

	@Override
	public PKCS10CertificationRequest createCSR(X500Name subject, PublicKey publicKey, PrivateKey privateKey, GeneralName[] subjectAlternateNames) throws OperatorCreationException, IOException
	{
		SubjectPublicKeyInfo 				publicKeyInfo 	= SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		PKCS10CertificationRequestBuilder	builder 		= new PKCS10CertificationRequestBuilder(subject, publicKeyInfo);
		JcaContentSignerBuilder				signerBuilder 	= new JcaContentSignerBuilder(getSignatureAlgorithm()).setProvider(BC);
		ContentSigner						signer 			= signerBuilder.build(privateKey);
		
		if(subjectAlternateNames != null)
		{
			ExtensionsGenerator extGen = new ExtensionsGenerator();
			
			extGen.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(
					subjectAlternateNames));
			builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());
		}
		
		return builder.build(signer);
	}
	
	
	@Override
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
	    ) throws InvalidKeyException, SignatureVerificationException
	{
    // Validate the CSR
    JcaPKCS10CertificationRequest csrHolder = new JcaPKCS10CertificationRequest(csr);
    PublicKey publicKey;
    
    try
    {
      publicKey = csrHolder.getPublicKey();
      
      if (!csrHolder.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(publicKey)))
      {
        throw new SignatureVerificationException("CSR Signature is invalid");
      }
    }
    catch (OperatorCreationException | PKCSException | InvalidKeyException | NoSuchAlgorithmException e)
    {
      throw new SignatureVerificationException("Unable to verify CSR Signature", e);
    }
    
    if(principal == null)
    {
      principal = csrHolder.getSubject();
      
      Attribute[] certAttributes = csrHolder.getAttributes();
      for (Attribute attribute : certAttributes)
      {
        if (attribute.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest))
        {
          Extensions    extensions = Extensions.getInstance(attribute.getAttrValues().getObjectAt(0));
          GeneralNames  gns = GeneralNames.fromExtensions(extensions, Extension.subjectAlternativeName);
          subjectAlternativeNames = gns.getNames();
        }
      }
    }
    
    return createCert(subjectAlternativeNames, publicKey, principal, notBefore, notAfter, ocspUrl, caPrivKey, caCert, serialNumber, policyOid, policyUrl, certificateType);
	}
	
	@Override
  public String sign(String data, PrivateKey privateKey) throws InvalidKeyException
  {
    byte[] sigBytes = sign(data.getBytes(StandardCharsets.UTF_8), privateKey);
    
    return new String(Base64.encode(sigBytes));
  }

	@Override
	public byte[]	sign(byte[] data, PrivateKey privateKey) throws InvalidKeyException
	{
	  try
	  {
  		  Signature	signature = Signature.getInstance(getSignatureAlgorithm());
  		
    		signature.initSign(privateKey);
    		signature.update(data);
    
    		return signature.sign();
	  }
    catch(NoSuchAlgorithmException | SignatureException e)
    {
      throw new TransactionFault(e);
    }
  }

	@Override
  public void	verifySignature(byte[] encodedSignature, byte[] data, Certificate certificate) throws SignatureVerificationException
	{
		try
		{
			Signature	signature = Signature.getInstance(getSignatureAlgorithm());
			
			signature.initVerify(certificate);
			signature.update(data);
			
			if(!signature.verify(encodedSignature))
				throw new SignatureVerificationException("Signature verification failed");
		}
		catch(NoSuchAlgorithmException | InvalidKeyException | SignatureException e)
		{
			throw new SignatureVerificationException("Signature verification failed", e);
		}
	}
	
	@Override
  public void	verifySignature(byte[] encodedSignature, byte[] data, PublicKey publicKey) throws SignatureVerificationException
	{
		try
		{
			Signature	signature = Signature.getInstance(getSignatureAlgorithm());
			
			signature.initVerify(publicKey);
			signature.update(data);
			
			if(!signature.verify(encodedSignature))
				throw new SignatureVerificationException("Signature verification failed");
		}
		catch(NoSuchAlgorithmException | InvalidKeyException | SignatureException e)
		{
			throw new SignatureVerificationException("Signature verification failed", e);
		}
	}
	
	@Override
	public X509Certificate createMasterCert(PublicKey pubKey, PrivateKey privKey, 
			X500Name principal, 
			Date notBefore, Date notAfter,
			URL ocspUrl, 
			BigInteger serialNumber)
	{
	  try
    	{
    		JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(principal, serialNumber, notBefore, notAfter, principal, pubKey);
    		
    		builder.addExtension(Extension.subjectKeyIdentifier, false, extensionUtils_.createSubjectKeyIdentifier(pubKey));
    		
    		builder.addExtension(
    				Extension.basicConstraints,
    	            true,
    	            new BasicConstraints(1));
    	        
    		builder.addExtension(
          Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));

      // OCSP address

      if (ocspUrl != null)
      {
        GeneralName location = new GeneralName(GeneralName.uniformResourceIdentifier, ocspUrl.toString());
        AuthorityInformationAccess auth = new AuthorityInformationAccess(AccessDescription.id_ad_ocsp, location);
        builder.addExtension(Extension.authorityInfoAccess, false, auth);
      }

      JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(getSignatureAlgorithm()).setProvider(BC);
      ContentSigner           signer        = signerBuilder.build(privKey);
      X509CertificateHolder 	 holder        = builder.build(signer);
    		
    		return converter_.getCertificate(holder);
    	}
    catch(IOException | OperatorCreationException | CertificateException e)
    {
      throw new TransactionFault(e);
    }
  }
	
	@Override
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
			) throws InvalidKeyException
	{
    	GeneralName[] subjectAlternativeNames = null;
    	
    	if(subjectRfc822AlternativeName != null)
          {
    		subjectAlternativeNames = new GeneralName[1];
    		subjectAlternativeNames[0] = new GeneralName(GeneralName.rfc822Name, new DERIA5String(subjectRfc822AlternativeName));
          	//genNames[1] = new GeneralName(GeneralName.directoryName, new X500Name("C=US,O=Cyberdyne,OU=PKI,CN=SecureCA"));
          }
    	
    	return createCert(subjectAlternativeNames,
    			pubKey, principal,
    			notBefore, notAfter, 
    			ocspUrl, caPrivKey, caCert, 
    			serialNumber, policyOid, policyUrl, 
    			certificateType);
  }

	@Override
	public X509Certificate createCert(
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
			) throws InvalidKeyException
  {
    validateKey(pubKey);
    validateKey(caPrivKey);
    try
    {
      JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(caCert, serialNumber, notBefore, notAfter,
          principal, pubKey);

      builder.addExtension(Extension.subjectKeyIdentifier, false, extensionUtils_.createSubjectKeyIdentifier(pubKey));
      builder.addExtension(Extension.authorityKeyIdentifier, false,
          extensionUtils_.createAuthorityKeyIdentifier(caCert));

      switch (certificateType)
      {
        case UserEncryption:
          builder.addExtension(Extension.keyUsage, true,
              new KeyUsage(KeyUsage.dataEncipherment | KeyUsage.keyEncipherment));

          builder.addExtension(Extension.extendedKeyUsage, true,
              new ExtendedKeyUsage(KeyPurposeId.id_kp_emailProtection));
          break;

        case Node:
        case UserSigning:
          builder.addExtension(Extension.keyUsage, true,
              new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation));

          builder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
          break;

        case Server:
          builder.addExtension(Extension.keyUsage, true,
              new KeyUsage(KeyUsage.dataEncipherment | KeyUsage.keyEncipherment | KeyUsage.digitalSignature));

          builder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
          break;

        case Intermediate:
          builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));

          builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
          break;

        case ANY:
        case Master:
        case NewAccount:
        case UserAttribute:
        default:
          throw new IllegalStateException("Invalid Certificate Type " + certificateType.toString());

      }

      if (subjectAlternativeNames != null)
      {
        builder.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(subjectAlternativeNames));
      }
      // OCSP address

      if (ocspUrl != null)
      {
        GeneralName location = new GeneralName(GeneralName.uniformResourceIdentifier, ocspUrl.toString());
        AuthorityInformationAccess auth = new AuthorityInformationAccess(AccessDescription.id_ad_ocsp, location);
        builder.addExtension(Extension.authorityInfoAccess, false, auth);
      }

      // Certificate Policies
      if (policyOid != null && policyUrl != null)
      {
        ASN1ObjectIdentifier objectId = new ASN1ObjectIdentifier(policyOid);
        PolicyQualifierInfo policyQualifierInfo = new PolicyQualifierInfo(policyUrl.toString());

        DERSequence qualifiers = new DERSequence(policyQualifierInfo);

        ASN1EncodableVector poliVec = new ASN1EncodableVector();

        poliVec.add(objectId);
        poliVec.add(qualifiers);

        DERSequence poliSequence = new DERSequence(poliVec);
        DERSequence certificatePolicies = new DERSequence(poliSequence);

        builder.addExtension(Extension.certificatePolicies, false, certificatePolicies);
      }

      JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(caCert.getSigAlgName()).setProvider(BC);
      ContentSigner signer = signerBuilder.build(caPrivKey);
      X509CertificateHolder holder = builder.build(signer);

      return converter_.getCertificate(holder);
    }
    catch (IOException | OperatorCreationException | CertificateException e)
    {
      throw new TransactionFault(e);
    }
  }
	
	@Override
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
			)
  {
	  try
    {
      X500Name clientName = X500Name.getInstance(clientCert.getSubjectX500Principal().getEncoded());
      X500Name issuerName = X500Name.getInstance(caCert.getSubjectX500Principal().getEncoded());
      X509v2AttributeCertificateBuilder builder = new X509v2AttributeCertificateBuilder(
          new AttributeCertificateHolder(clientName), new AttributeCertificateIssuer(issuerName), serialNumber, notBefore,
          notAfter);
  
      // OCSP address
  
      if (ocspUrl != null)
      {
        GeneralName location = new GeneralName(GeneralName.uniformResourceIdentifier, ocspUrl.toString());
        AuthorityInformationAccess auth = new AuthorityInformationAccess(AccessDescription.id_ad_ocsp, location);
        builder.addExtension(Extension.authorityInfoAccess, false, auth);
      }
  
      // Certificate Policies
      if (policyOid != null && policyUrl != null)
      {
        ASN1ObjectIdentifier objectId = new ASN1ObjectIdentifier(policyOid);
        PolicyQualifierInfo policyQualifierInfo = new PolicyQualifierInfo(policyUrl.toString());
  
        DERSequence qualifiers = new DERSequence(policyQualifierInfo);
  
        ASN1EncodableVector poliVec = new ASN1EncodableVector();
  
        poliVec.add(objectId);
        poliVec.add(qualifiers);
  
        DERSequence poliSequence = new DERSequence(poliVec);
        DERSequence certificatePolicies = new DERSequence(poliSequence);
  
        builder.addExtension(Extension.certificatePolicies, false, certificatePolicies);
      }
  
      ASN1EncodableVector v;
  
      v = new ASN1EncodableVector();
  
      v.add(new ASN1ObjectIdentifier(attributeId));
      v.add(new DERIA5String(attributeValue));
  
      GeneralName generalName = new GeneralName(GeneralName.otherName, new DERSequence(v));
  
      ASN1EncodableVector roleSyntax = new ASN1EncodableVector();
      roleSyntax.add(generalName);
  
      builder.addAttribute(new ASN1ObjectIdentifier(attributeId), new DERSequence(roleSyntax));
  
      // Target targetName = new Target(Target.targetName, clientName);
      //
      // Target[] targets = new Target[1];
      // targets[0] = targetName;
      // TargetInformation targetInformation = new TargetInformation(targets);
      //
      // builder.addExtension(Extension.targetInformation, true,
      // targetInformation);
  
      JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(caCert.getSigAlgName()).setProvider(BC);
      ContentSigner signer = signerBuilder.build(caPrivKey);
  
      return builder.build(signer);
    }
	  catch(IOException | OperatorCreationException e)
    {
      throw new TransactionFault(e);
    }
  }
	
	@Override
  public String		publicKeyToDER(PublicKey key) throws IOException, GeneralSecurityException
	{
	  try
	  (
    		ByteArrayOutputStream	bos       = new ByteArrayOutputStream();
    		JcaPEMWriter				    pemWriter = new JcaPEMWriter(new OutputStreamWriter(bos));
		)
	  {
    		pemWriter.writeObject(key);
    		
    		pemWriter.close();
    		
    		return new String(bos.toByteArray());
	  }
	}
	
	@Override
  public PublicKey		publicKeyFromDER(String der) throws IOException, GeneralSecurityException
	{
	  try
	  (
    		StringReader			reader = new StringReader(der);
    		PEMParser				pemParser = new PEMParser(reader);
		)
		{
			Object o = pemParser.readObject();
			
			if (o == null || !(o instanceof SubjectPublicKeyInfo))
	    {
			  throw new IOException("Not an OpenSSL public key");
	    }
			
			return new JcaPEMKeyConverter().setProvider("BC").getPublicKey((SubjectPublicKeyInfo)o);
		}
	}
	
	@Override
  public String		privateKeyToDER(PrivateKey key) throws IOException, GeneralSecurityException
	{
	  try
	  (
    		ByteArrayOutputStream	bos       = new ByteArrayOutputStream();
    		JcaPEMWriter				  pemWriter = new JcaPEMWriter(new OutputStreamWriter(bos));
		)
	  {
    		pemWriter.writeObject(key);
    		
    		pemWriter.close();
    		
    		return new String(bos.toByteArray());
	  }
	}
	
	@Override
  public PrivateKey		privateKeyFromDER(String der) throws IOException, GeneralSecurityException
	{
	  try
	  (
    		StringReader			reader = new StringReader(der);
    		PEMParser				pemParser = new PEMParser(reader);
		)
		{
			Object o = pemParser.readObject();
			
			if (o == null || !(o instanceof PEMKeyPair))
	        {
	        	throw new IOException("Not an OpenSSL key");
	        }
			
			KeyPair kp = new JcaPEMKeyConverter().setProvider("BC").getKeyPair((PEMKeyPair)o);
	        return kp.getPrivate();
		}
	}

  @Override
  public void validateKey(KeyPair keyPair) throws InvalidKeyException
  {
    validateKey(keyPair.getPublic());
  }

  @Override
  public void validateKey(PrivateKey key) throws InvalidKeyException
  {
    if(getKeySize(key) != getKeySize())
      throw new InvalidKeyException("Incorrect key size");
  }

  @Override
  public void validateKey(PublicKey key) throws InvalidKeyException
  {
    if(getKeySize(key) != getKeySize())
      throw new InvalidKeyException("Incorrect key size");
  }
}
