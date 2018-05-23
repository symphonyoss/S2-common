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

package org.symphonyoss.s2.common.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.concurrent.NotThreadSafe;

import org.symphonyoss.s2.common.fault.CodingFault;

@NotThreadSafe
/* package */ class DigestHashFunction extends AbstractHashFunction
{
  private final MessageDigest digest_;
  
  /* package */ DigestHashFunction(String digestId)
  {
    try
    {
      digest_ =  MessageDigest.getInstance(digestId);
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new CodingFault(e);
    }
  }

  @Override
  /* package */ byte[] digest(byte[] bytes)
  {
    return digest_.digest(bytes);
  }
  
  @Override
  /* package */ void update(byte b)
  {
    digest_.update(b);
  }

  @Override
  /* package */ void update(byte[] bytes)
  {
    digest_.update(bytes);
  }

  @Override
  /* package */ byte[] digest()
  {
    return digest_.digest();
  }
}
