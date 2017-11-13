/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
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

package org.symphonyoss.s2.common.crypto.cert;

import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.GeneralName;
import org.symphonyoss.s2.common.crypto.cipher.CertType;
import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.time.DateUtils;

/**
 * A builder for certificates.
 * 
 * The builder maintains a complete set of parameters for creating new certificates.
 * All parameters have valid defaults so 
 * 
 * <code>
 * new CertificateBuilder(signingCert).build();
 * </code>
 * 
 * will not fail, but it would be unwise to rely on the defaults and callers should
 * carefully consider initializing all values.
 * 
 * CertificateBuilder instances are reusable and the <code>withXXX()</code> methods
 * all return the existing builder, not a copy.
 * 
 * Each certificate should have a unique serial number. The build method
 * increments the serial number so if you use the same builder to create several
 * certificates then they will have consecutive serial numbers.
 * 
 * You can provide a KeyPair to the builder, or if you do not it will generate
 * one. The KeyPair is automatically cleared by the build method to prevent the
 * same key being used for multiple certificate inadvertently.
 * 
 * @author Bruce Skingle
 *
 */
public class CertificateBuilder
{
  private final IIntermediateCertificate signingCert_;
  
  private X500NameStyle   template_     = BCStyle.INSTANCE;
  private List<RDN>       rdns_         = new ArrayList<>();
  private Date            notBefore_    = new Date();
  private Date            notAfter_     = DateUtils.getDateOffsetMonths(notBefore_, 12);
  private BigInteger      serial_       = BigInteger.ONE;
  private String          countryName_;
  private String          stateName_;
  private String          orgName_;
  private List<String>    orgUnitNames_ = new ArrayList<>();
  private String          commonName_;
  private CertType        certType_     = CertType.UserSigning;
  private KeyPair         keyPair_;
  private URL             ocspUrl_;

  /**
   * Create a CertificateBuilder for the given certificate signing certificate.
   * 
   * @param signingCert a certificate signing certificate.
   */
  public CertificateBuilder(IIntermediateCertificate signingCert)
  {
    signingCert_ = signingCert;
  }

  /**
   * Create a CertificateBuilder for the given certificate signing certificate.
   * 
   * @param signingCert a certificate signing certificate.
   * @param other Another CertificateBuilder from which other parameters should be initialized.
   */
  public CertificateBuilder(IIntermediateCertificate signingCert, CertificateBuilder other)
  {
    signingCert_ = signingCert;
    notBefore_ = other.notBefore_;
    notAfter_ = other.notAfter_;
    serial_ = other.serial_;
    countryName_ = other.countryName_;
    stateName_ = other.stateName_;
    orgName_ = other.orgName_;
    orgUnitNames_.addAll(other.orgUnitNames_);
    commonName_ = other.commonName_;
    certType_ = other.certType_;
    ocspUrl_ = other.ocspUrl_;
  }

  public CertificateBuilder withCertType(CertType certType)
  {
    certType_ = certType;
    return this;
  }

  public CertificateBuilder withNotBefore(Date notBefore)
  {
    notBefore_ = notBefore;
    return this;
  }
  
  public CertificateBuilder withNotAfter(Date notAfter)
  {
    notAfter_ = notAfter;
    return this;
  }
  
  public CertificateBuilder withValidityMonths(int months)
  {
    notAfter_ = DateUtils.getDateOffsetMonths(notBefore_, months);
    return this;
  }
  
  public CertificateBuilder withValidityDays(int days)
  {
    notAfter_ = DateUtils.getDateOffsetDays(notBefore_, days);
    return this;
  }
  
  public CertificateBuilder withSerial(BigInteger serial)
  {
    serial_ = serial;
    return this;
  }
  
  public CertificateBuilder withOrgName(String orgName)
  {
    orgName_ = orgName;
    return this;
  }
  
  public CertificateBuilder  withCountryName(String countryName)
  {
    countryName_ = countryName;
    return this;
  }
  
  public CertificateBuilder  withStateName(String stateName)
  {
    stateName_ = stateName;
    return this;
  }
  
  public CertificateBuilder  withOrganizationName(String orgName)
  {
    orgName_ = orgName;
    return this;
  }
  
  public CertificateBuilder  withOrgUnitName(String orgUnitName)
  {
    orgUnitNames_.clear();
    orgUnitNames_.add(orgUnitName);
    return this;
  }
  
  public CertificateBuilder  addOrgUnitName(String orgUnitName)
  {
    orgUnitNames_.add(orgUnitName);
    return this;
  }
  
  public CertificateBuilder  withCommonName(String commonName)
  {
    commonName_ = commonName;
    return this;
  }
  
  public CertificateBuilder  withOcspUrl(URL ocspUrl)
  {
    ocspUrl_ = ocspUrl;
    return this;
  }
  
  /**
   * Set the KeyPair to be used for the next certificate creation.
   * 
   * @param keyPair The KeyPair which is to be bound to the certificate.
   * 
   * @return  The current CertificateBuilder. (Fluent interface)
   * 
   * @throws InvalidKeyException If the given KeyPair is incompatible with the
   * CipherSuirte of the signing cert associated with this builder.
   */
  public CertificateBuilder  withKeyPair(KeyPair keyPair) throws InvalidKeyException
  {
    signingCert_.getAsymmetricCipherSuite().validateKey(keyPair);
    
    keyPair_ = keyPair;
    return this;
  }

  private X500Name buildX500Name()
  {
    rdns_.clear();
    
    if(countryName_ != null)
      addRDN(BCStyle.C, countryName_);
    
    if(stateName_ != null)
      addRDN(BCStyle.ST, stateName_);
    
    if(orgName_ != null)
      addRDN(BCStyle.O, orgName_);
    
    for(String s : orgUnitNames_)
      addRDN(BCStyle.OU, s);
    
    if(commonName_ != null)
      addRDN(BCStyle.CN, commonName_);
    
    if(rdns_.isEmpty())
      addRDN(BCStyle.CN, "No Name");
    
    RDN[] vals = rdns_.toArray(new RDN[rdns_.size()]);

    return new X500Name(template_, vals);
  }

  private CertificateBuilder addRDN(ASN1ObjectIdentifier oid, String value)
  {
      return addRDN(oid, template_.stringToValue(oid, value));
  }
  
  private CertificateBuilder addRDN(ASN1ObjectIdentifier oid, ASN1Encodable value)
  {
      rdns_.add(new RDN(oid, value));

      return this;
  }
  
  public Date getNotBefore()
  {
    return notBefore_;
  }

  public Date getNotAfter()
  {
    return notAfter_;
  }

  public BigInteger getSerial()
  {
    return serial_;
  }

  public String getCountryName()
  {
    return countryName_;
  }

  public String getStateName()
  {
    return stateName_;
  }

  public String getOrgName()
  {
    return orgName_;
  }

  public List<String> getOrgUnitNames()
  {
    return orgUnitNames_;
  }

  public String getCommonName()
  {
    return commonName_;
  }

  public CertType getCertType()
  {
    return certType_;
  }

  public KeyPair getKeyPair()
  {
    return keyPair_;
  }

  public URL getOcspUrl()
  {
    return ocspUrl_;
  }

  public IOpenCertificate build()
  {
    if(keyPair_ == null)
      keyPair_ = signingCert_.getAsymmetricCipherSuite().generateKeyPair();
    
    X500Name principal = buildX500Name();
    
    GeneralName[] subjectAlternativeNames = null;
    
    List<X509Certificate> signerCerts = signingCert_.getX509CertificateChain();
    X509Certificate[] certs = new X509Certificate[1 + signerCerts.size()];
    
    for(int index=0 ; index<signerCerts.size() ; index++)
      certs[index+1] = signerCerts.get(index);
    

    try
    {
      certs[0] = signingCert_.getAsymmetricCipherSuite().createCert(subjectAlternativeNames,
          keyPair_.getPublic(), principal, 
          notBefore_, notAfter_, 
          ocspUrl_,
          signingCert_.getPrivateKey(), 
          signingCert_.getX509Certificate(), 
          serial_,
          //S2Oid.getOid(Policy.Internal), S2Oid.getUrl(Policy.Internal),
          null, null,
          certType_);
    }
    catch (InvalidKeyException e)
    {
      throw new CodingFault(e);
    }
    
    
    ICertificate result = CertificateFactory.typedCertificate(new Certificate(signingCert_.getAsymmetricCipherSuite(), certs, keyPair_));
          
    keyPair_ = null;
    serial_ = serial_.add(BigInteger.ONE);
    
    return (IOpenCertificate)result;
  }
}
