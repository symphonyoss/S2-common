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

package org.symphonyoss.s2.common.crypto.examples;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.util.Date;

import org.symphonyoss.s2.common.crypto.cert.CertificateBuilder;
import org.symphonyoss.s2.common.crypto.cert.CertificateFactory;
import org.symphonyoss.s2.common.crypto.cert.ICertificate;
import org.symphonyoss.s2.common.crypto.cert.IIntermediateCertificate;
import org.symphonyoss.s2.common.crypto.cert.IOpenCertificate;
import org.symphonyoss.s2.common.crypto.cipher.CertType;

import com.symphony.s2.model.fundamental.crypto.cipher.UnknownCipherSuiteException;

public class GenerateUserCert
{
  public static void main(String[] argv)
  {
    new GenerateUserCert().run();
  }

  private void run()
  {
    // First we need an intermediate signing cert.
    
    String storeType  = "PKCS12";
    String alias      = null;      // Just take the first entry, we expect that there is only one
    char[] password   = "changeit".toCharArray();

    ICertificate intCert;
    
    try
    {
      intCert = CertificateFactory.load(
          new File("src/test/resources/int-cert.p12"), 
          storeType , alias, password);
    }
    catch (IOException | GeneralSecurityException | UnknownCipherSuiteException e)
    {
      System.err.println("Failed to load signing certificate");
      e.printStackTrace();
      return;
    }
    
    // The load method is declared to return ICertificate but the actual instance returned
    // will implement other sub-interfaces if appropriate. We need an intermediate cert
    // so let's see if we got one
    
    IIntermediateCertificate  signingCert;
    
    if(intCert instanceof IIntermediateCertificate)
    {
      signingCert = (IIntermediateCertificate) intCert;
    }
    else
    {
      System.err.println("We expected an intermediate cert but what we loaded was a " + intCert.getClass().getName());
      return;
    }
    
    /* Now generate a user cert
     * You should use a unique serial number for each certificate. The build method
     * increments the serial number so if you use the same builder to create several
     * certificates then they will have consecutive serial numbers.
     * 
     * You can call getters on the builder to retrieve the parameters.
     * 
     * You can provide a KeyPair to the builder, or if you do not it will generate one.
     * The KeyPair is automatically cleared by the build method to prevent the same
     * key being used for multiple certificate inadvertently.
     * 
     * There are defaults for all builder parameters so you don't need to set all of the
     * parameters below, but we recommend against relying on yje default values and
     * suggest that you should set all of the parameters below at a minimum.
     */
    
    String userName = "bot.user5";
    IOpenCertificate userCert;

    userCert = new CertificateBuilder(signingCert)
        .withCountryName("US")
        .withStateName("CA")
        .withOrganizationName("Symphony Communication Services, LLC.")
        .withOrgUnitName("Common Crypto Examples")
        .withCommonName(userName)
        .withNotBefore(new Date())
        .withValidityMonths(12)
        .withSerial(BigInteger.ONE)
        .withCertType(CertType.UserSigning)
        
        .build();

    System.out.println("Created cert " + userCert.getX509Certificate());
    
    // Now save the cert as a regular keystore.
    
    password   = "changeit".toCharArray();
    try
    {
      userCert.storeUserKeystore(new File("/tmp/" + userName + ".p12"), password);
    }
    catch (IOException | GeneralSecurityException e)
    {
      System.err.println("Failed to save user certificate");
      e.printStackTrace();
      return;
    }
  }
  
  /**
   * An example of how to load a certificate and private key from PEM format.
   * 
   * Assuming that your company policies forbid the storage of passwords in
   * files, and publishing them in source code, you should not do it exactly
   * like this in production.
   * 
   * This is a test certificate by the way.
   * 
   * @return An Intermediate Certificate Signing Certificate
   * @throws CertificateException        If the certificate cannot be read
   * @throws UnknownCipherSuiteException If the certificate uses an unknown cipher
   */
  private IIntermediateCertificate loadIntermediateCertFromPEM() throws CertificateException, UnknownCipherSuiteException
  {
    return (IIntermediateCertificate) CertificateFactory.loadOpenCertificate(
        "-----BEGIN CERTIFICATE-----\n" + 
        "MIIElDCCA3ygAwIBAgIBAjANBgkqhkiG9w0BAQsFADCBiTEzMDEGA1UEAxMqRGlz\n" + 
        "cG9zYWJsZSBUZXN0IFJvb3QgQ2VydGlmaWNhdGUgQXV0aG9yaXR5MSQwIgYDVQQK\n" + 
        "ExtTeW1waG9ueSBDb21tdW5pY2F0aW9ucyBMTEMxHzAdBgNVBAsTFk5PVCBGT1Ig\n" + 
        "UFJPRFVDVElPTiBVU0UxCzAJBgNVBAYTAlVTMB4XDTE2MDEyNzIwMTEwM1oXDTM2\n" + 
        "MDEyNzIwMTEwM1owgZExOzA5BgNVBAMTMkRpc3Bvc2FibGUgVGVzdCBJbnRlcm1l\n" + 
        "ZGlhdGUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5MSQwIgYDVQQKExtTeW1waG9ueSBD\n" + 
        "b21tdW5pY2F0aW9ucyBMTEMxHzAdBgNVBAsTFk5PVCBGT1IgUFJPRFVDVElPTiBV\n" + 
        "U0UxCzAJBgNVBAYTAlVTMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA\n" + 
        "n7ugslaw0I+Wa/tDecwPKa4cJMEkCDlSNE+jF5TtircXlg8AYtEmJQmjobpMH00Y\n" + 
        "eVIJN0fqusy+eU9KhrLgKBpAT4ZBlD5W0AwemBxylodx02EiadzjCWXeXTu950El\n" + 
        "IbBwXaOSTqu4sSQ03QgTDaqPsTRgvbIXeJOYBT01Av9YyiGMiWeT4H3t6cKmECzE\n" + 
        "JJbWyd6VCoDXtCZWb9V6oQDbSz4gLjW5teoBOqEM4hHzu2CUb3moJocWcwss1g+k\n" + 
        "MIdyAPgPeSOruSGY1N8ymQbaqojzVhmb7jUKn7a7GXNLh5R2hfQ/Gh2dw+llPkQo\n" + 
        "vLEUx6+fmFHEAPZ1LWjhsQIDAQABo4H8MIH5MA8GA1UdEwEB/wQFMAMBAf8wDgYD\n" + 
        "VR0PAQH/BAQDAgHGMB0GA1UdDgQWBBR51VV+y64UZlM+drSV4hC5gv8kUTCBtgYD\n" + 
        "VR0jBIGuMIGrgBTLr5tHnmoOSmEiEOnvtCfx2b3aIaGBj6SBjDCBiTEzMDEGA1UE\n" + 
        "AxMqRGlzcG9zYWJsZSBUZXN0IFJvb3QgQ2VydGlmaWNhdGUgQXV0aG9yaXR5MSQw\n" + 
        "IgYDVQQKExtTeW1waG9ueSBDb21tdW5pY2F0aW9ucyBMTEMxHzAdBgNVBAsTFk5P\n" + 
        "VCBGT1IgUFJPRFVDVElPTiBVU0UxCzAJBgNVBAYTAlVTggEBMA0GCSqGSIb3DQEB\n" + 
        "CwUAA4IBAQCgagkhvopNgUblKwMTmog/vl+qiOItcZFC5BjdP9/paexCwIM6k5to\n" + 
        "Ca6gRvxIR8orHWSKRM2pE8+pWSZsMQdO7iRHm+XaJkQkdfXUO/2PAfjj6IEdsl+e\n" + 
        "gFzz6XNqpHek88touW6+6/+rdUYJ4k63RGuirRghRikBuJsut1F37xkrWyWhUpAr\n" + 
        "XGI3MJRTrAzaHzzM1K8crdS35LjOaNihjSvsi2sTSaBiZam9sZnkKpVXWQFeuz1I\n" + 
        "6WKP8sYhzydu1v35yY0jsk3uXbeBix6dYs9IecD7CKahvu/WeXLm5C8bV6BhHXK6\n" + 
        "TamUuRgsyQk1Sa6NdScIDczVvsWFPAmD\n" + 
        "-----END CERTIFICATE-----", 
        "-----BEGIN RSA PRIVATE KEY-----\n" + 
        "Proc-Type: 4,ENCRYPTED\n" + 
        "DEK-Info: AES-256-CBC,40097A34FAEB995C1695C9D7B5B3EBF1\n" + 
        "\n" + 
        "pjVChrgm6ipHnVTiV+Vtdg2vSsNLiTHzU5+0V7RjxdcD5LuJ5ViYN3tRRYiM9cSp\n" + 
        "itQdyqHjYp6Dxc/eSUMyVNVAoehe2xikybfKPfgxKuA4M3xEj9L60qX3fON9+wsB\n" + 
        "LdrF8KBoZepAIN1djxRcUbCZqKX5Oprt64FtOKQEv7uEN8UACTZ2MCbf/pEPSmc3\n" + 
        "gOXa+zgw0QgMfzXFeGrk56PacXp+37p+kuyevCPs6By+XxLs3TQVl/yHXl3jZOVN\n" + 
        "HgxNDWFdXm+Jork0ko/XWfvOZlJ8Sm6UQ+CUbrQsgmu2hn11sI22f3qaqPJcm5db\n" + 
        "zMWF4NpKfR4bsNWSiCn6KaIIGlH0M0Dfspe9aKr4at5RoibiFHHF88fMBiQSjHGo\n" + 
        "q8AupbZU00BDZvQNtcm86MDZxI4BsEC7W7cwmgfirZz6BxCMkwDVGco7ryejmu/6\n" + 
        "Eubu5dfSNV/GDxcF8ZhnltL2AMug9f/T6/dGzzFMvKJWpifTz4Wa4LyGN7rUCE2z\n" + 
        "5ait1LQX3MGtK1SWCFLOQq/9a5NpQCMYwh9AvIFmp7QRVd1k7rmSfSrftjqXJOYI\n" + 
        "ykP9yr50Wm5Y4nCX7Tp9/OuA6EPXZq3UE5TCjca+VJZS8hY5dpja+4Xn2FooKkf7\n" + 
        "SK+AKlPZ5/CbkLcE/vZXEXusZsz/ENAQgmo3jQhfE9l/iqkJ89+5kgViqTCz+3R8\n" + 
        "7AStALAMXaMrkADhCSgfOvxLe6qZtXCpptUtMfOqmFpxD+v/FLWxI93g6jQKVT2c\n" + 
        "W7AlZBJlBdo/8MhNR1X/6QVGFE+4zdnu0t/2lT2IM4Al76uR3tBuO0cQESHrZrCs\n" + 
        "tFUh4HSHKZeOKmqgxZcsL9TcSmLeTrJYUGpD5O+2MCSFCv5TvNKsV0DUKB8JgUtT\n" + 
        "uVbQfVYKm60zaBqrxNtR/CjTo4v08kXGFVwP5O62Z0bphWYpYEzVm6Epi9lWeBns\n" + 
        "3sPP23btOzAF8W2ckS+JCB7LTy4/rWfbhvGD/jYnXxpD+NYdjXYnENJ4WOoE0srm\n" + 
        "e231+RUr8ZRM8C5iZYMoSuXXcDZzG9dPhaEXOAf/loNPN/ewd7l38T/gvQLzaeYS\n" + 
        "my/T89yCIrAjygcnjxQHKTbm0EKjI0v/Qjh7EMloDKbW/DbF8fZHS5JX2yqDjQJ8\n" + 
        "NQEr+sKSXziJp90XZSthZ2a1VHR8Kpti6FVbAEqVFxt0pynsLKsc3L2faHDt493Q\n" + 
        "aL5WnFKwRDy/IRhB5buiNu2clQ62gfbBeGu3ycS5nTLy1k0oF60Cj1PpQclkGfzY\n" + 
        "NqBoh8oJSJguL0KICkfBYKKvmEj+7GYSIft+iFcjRm58/odCN7K1iJ27QHIMjfFi\n" + 
        "wCIsW14gEMVkhL3OjeIil3/8L/9blPxijDKHzAz9iw/yuuuuzTFdbOWtX1K1kSV2\n" + 
        "GvPd8XZJgusMejA7HNJRe2OdXbupE3Xb89I6/U/dD3uLAWoCCF2t+RKS4/XoJQDZ\n" + 
        "ULM9590W6k4ijAwpL0nPl1RvWV/g27nXCghAIB7HNNafee3K6m9YsXm416Vty99k\n" + 
        "8tmCJJMjPXNq6PqmG2KUBiCSivm4wscCfgE1Iov7N6bqV8B0PnHnK5V107UnUbzB\n" + 
        "-----END RSA PRIVATE KEY-----",
        "changeit".toCharArray());

  }
}
