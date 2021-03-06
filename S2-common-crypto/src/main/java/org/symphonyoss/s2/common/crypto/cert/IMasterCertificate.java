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

import java.security.cert.CertificateException;

/**
 * A certificate capable of creating signing certificates, which must be open
 * (i.e. the private key must be available) and which is implicitly also capable
 * of creating end user certificates.
 * 
 * @author Bruce Skingle
 *
 */
public interface IMasterCertificate extends IIntermediateCertificate
{
  /**
   * Create a certificate signing certificate.
   * 
   * @param builder A CertificateBuilder configured with the parameters for the new certificate.
   * NB the certType will be forced to Intermediate by this method.
   * 
   * @return A certificate signing certificate.
   * @throws CertificateException If the request fails.
   */
  IIntermediateCertificate createIntermediateCert(CertificateBuilder builder) throws CertificateException;
}
