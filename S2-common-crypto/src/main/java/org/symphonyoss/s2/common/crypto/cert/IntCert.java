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

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import com.symphony.s2.model.fundamental.crypto.cipher.IAsymmetricCipherSuite;

/* package */ class IntCert extends Certificate implements IIntermediateCertificate
{
  /* package */ IntCert(Certificate other)
  {
    super(other);
  }

  /* package */ IntCert(IAsymmetricCipherSuite asymmetricCipherSuite, X509Certificate x509Certificate,
      List<X509Certificate> x509CertificateChain, PublicKey publicKey, PrivateKey privateKey)
  {
    super(asymmetricCipherSuite, x509Certificate, x509CertificateChain, publicKey, privateKey);
  }

  /* package */ IntCert(IAsymmetricCipherSuite asymmetricCipherSuite, X509Certificate[] certs, KeyPair keyPair)
  {
    super(asymmetricCipherSuite, certs, keyPair);
  }
}
