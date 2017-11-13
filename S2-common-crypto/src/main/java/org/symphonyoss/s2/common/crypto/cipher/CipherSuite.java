/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.fault.ProgramFault;

/**
 * Access point for CipherSuites.
 * 
 * @author Bruce Skingle
 */
public class CipherSuite
{
  private static final Map<String, IAsymmetricCipherSuite>               publicAlgoMap_     = new HashMap<>();
  private static final Map<String, ISymmetricCipherSuite>                secretAlgoMap_     = new HashMap<>();
  private static final Map<String, Map<Integer, IAsymmetricCipherSuite>> publicAlgoSizeMap_ = new HashMap<>();
  private static final Map<String, Map<Integer, ISymmetricCipherSuite>>  secretAlgoSizeMap_ = new HashMap<>();

  private static final Map<AsymmetricCipher, IAsymmetricCipherSuite>     publicMap_         = new HashMap<>();
  private static final Map<SymmetricCipher, ISymmetricCipherSuite>       secretMap_         = new HashMap<>();
  private static final List<IAsymmetricCipherSuite>                      publicList_;
  private static final List<ISymmetricCipherSuite>                       secretList_;
  private static final ISymmetricCipherSuite                             defaultSymmetricCipherSuite_;
  private static final IAsymmetricCipherSuite                            defaultAsymmetricCipherSuite_;

  private static List<IAsymmetricCipherSuite>                            publicBuildList_   = new ArrayList<>();
  private static List<ISymmetricCipherSuite>                             secretBuildList_   = new ArrayList<>();

  static
  {
    if(Security.getProvider("BC") == null)
    {
      Security.addProvider(new BouncyCastleProvider());
    }
  
    try
    {
      /*
       * The order of initialization is significant.
       * 
       * When calling get(SecretKey key) or get(PublicKey key), if there are multiple
       * implementations with the same key size then the first one registered will be
       * returned.
       */
      defaultSymmetricCipherSuite_ = new Aes256GCMCipherSuite(SymmetricCipher.AES256_GCM);
      defaultAsymmetricCipherSuite_ = new Rsa2048CipherSuite(AsymmetricCipher.RSA2048);
      
      register(defaultSymmetricCipherSuite_);
      register(new Aes128CipherSuite(SymmetricCipher.AES128_CBC));
      register(new Aes192CipherSuite(SymmetricCipher.AES192_CBC));
      register(new Aes256CipherSuite(SymmetricCipher.AES256_CBC));

      register(defaultAsymmetricCipherSuite_);
      register(new Rsa1024CipherSuite(AsymmetricCipher.RSA1024));
      register(new Ecc521CipherSuite(AsymmetricCipher.ECC521));
    } catch (NoSuchAlgorithmException | NoSuchProviderException | OperatorCreationException | InvalidAlgorithmParameterException e)
    {
      throw new ProgramFault(e);
    }
    
    publicList_ = Collections.unmodifiableList(publicBuildList_);
    secretList_ = Collections.unmodifiableList(secretBuildList_);
  }
  
  private static void register(ISymmetricCipherSuite cipherSuite)
  {
    secretMap_.put(cipherSuite.getId(), cipherSuite);
    
    secretAlgoMap_.put(cipherSuite.getKeyAlgorithm(), cipherSuite);
    
    Map<Integer, ISymmetricCipherSuite> map = secretAlgoSizeMap_.get(cipherSuite.getKeyAlgorithm());
    
    if(map == null)
    {
      map = new HashMap<>();
      secretAlgoSizeMap_.put(cipherSuite.getKeyAlgorithm(), map);
    }
    
    if(!map.containsKey(cipherSuite.getKeySize()))
      map.put(cipherSuite.getKeySize(), cipherSuite);
    
    secretBuildList_.add(cipherSuite);
  }
  
  private static void register(IAsymmetricCipherSuite cipherSuite)
  {
    publicMap_.put(cipherSuite.getId(), cipherSuite);
    
    publicAlgoMap_.put(cipherSuite.getKeyAlgorithm(), cipherSuite);
    
    Map<Integer, IAsymmetricCipherSuite> map = publicAlgoSizeMap_.get(cipherSuite.getKeyAlgorithm());
    
    if(map == null)
    {
      map = new HashMap<>();
      publicAlgoSizeMap_.put(cipherSuite.getKeyAlgorithm(), map);
    }
    
    if(!map.containsKey(cipherSuite.getKeySize()))
      map.put(cipherSuite.getKeySize(), cipherSuite);
    
    publicBuildList_.add(cipherSuite);
  }

  /**
   * Return the cipher suite implementation for the given key.
   * 
   * @param key A PublicKey
   * @return The implementation of the required cipher suite.
   * @throws UnknownCipherSuiteException If the given key does not match any known CipherSuite
   */
  public static @Nonnull IAsymmetricCipherSuite get(PublicKey key) throws UnknownCipherSuiteException
  {
    IAsymmetricCipherSuite cipherSuite = publicAlgoMap_.get(key.getAlgorithm());
    
    if(cipherSuite == null)
      throw new UnknownCipherSuiteException("Unknown algorithm \"" + key.getAlgorithm() + "\"");
    
    int keySize;
    try
    {
      keySize = cipherSuite.getKeySize(key);
    }
    catch (InvalidKeyException e)
    {
      throw new UnknownCipherSuiteException("Unsupported key size \"" + key.getAlgorithm() + "\"");

    }
    cipherSuite = publicAlgoSizeMap_.get(key.getAlgorithm()).get(keySize);
        
    if(cipherSuite == null)
      throw new UnknownCipherSuiteException("Unsupported key size \"" + key.getAlgorithm() + "\" " + keySize);
    
    return cipherSuite;
  }

  /**
   * Return the cipher suite implementation for the given ID.
   * 
   * @param key A SecretKey
   * @return The implementation of the required cipher suite.
   * @throws UnknownCipherSuiteException if the key does not match any cipher suite
   */
  public static @Nonnull ISymmetricCipherSuite get(SecretKey key) throws UnknownCipherSuiteException
  {
    ISymmetricCipherSuite cipherSuite = secretAlgoMap_.get(key.getAlgorithm());
    
    if(cipherSuite == null)
      throw new UnknownCipherSuiteException("Unknown algorithm \"" + key.getAlgorithm() + "\"");
    
    int keySize = cipherSuite.getKeySize(key);
    cipherSuite = secretAlgoSizeMap_.get(key.getAlgorithm()).get(keySize);
        
    if(cipherSuite == null)
      throw new UnknownCipherSuiteException("Unsupported key size \"" + key.getAlgorithm() + "\" " + keySize);
    
    return cipherSuite;
  }
  
  /**
   * Return the cipher suite implementation for the given ID.
   * 
   * @param id The ID of the required cipher suite.
   * @return The implementation of the required cipher suite.
   */
  public static @Nonnull IAsymmetricCipherSuite get(AsymmetricCipher id)
  {
    if(!publicMap_.containsKey(id))
      throw new CodingFault("No cipher suite for " + id);
    
    return publicMap_.get(id);
  }

  /**
   * Return the cipher suite implementation for the given ID.
   * 
   * @param id The ID of the required cipher suite.
   * @return The implementation of the required cipher suite.
   */
  public static @Nonnull ISymmetricCipherSuite get(SymmetricCipher id)
  {
    if(!secretMap_.containsKey(id))
      throw new CodingFault("No cipher suite for " + id);
    
    return secretMap_.get(id);
  }
  
  /**
   * Return the default Asymmetric cipher suite.
   * 
   * @return the default Asymmetric cipher suite.
   */
  public static @Nonnull IAsymmetricCipherSuite getAsymmetricCipher()
  {
    return defaultAsymmetricCipherSuite_;
  }
  
  /**
   * Return the default Symmetric cipher suite.
   * 
   * @return the default Symmetric cipher suite.
   */
  public static @Nonnull ISymmetricCipherSuite getSymmetricCipher()
  {
    return defaultSymmetricCipherSuite_;
  }

  /**
   * 
   * @return an UnModifiableList of all AsymetricCipherSuites
   */
  public static List<IAsymmetricCipherSuite> getAllAsymetricCipherSuites()
  {
    return publicList_;
  }

  /**
   * 
   * @return an UnModifiableList of all SymetricCipherSuites
   */
  public static List<ISymmetricCipherSuite> getAllSymetricCipherSuites()
  {
    return secretList_;
  }
}
