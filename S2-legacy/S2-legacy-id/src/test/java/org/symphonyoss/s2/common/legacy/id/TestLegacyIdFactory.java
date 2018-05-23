/*
 *
 *
 * Copyright 2018 Symphony Communication Services, LLC.
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

package org.symphonyoss.s2.common.legacy.id;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

public class TestLegacyIdFactory
{
  private static final String TENANT_ID = "MyTenant";
  private static final byte[] MESSAGE_ID = Base64.decodeBase64("xjbP0HZYa8xSyPqH19BFxX///p49T8mWbQ==");
  private static final Object EXPECTED = "NTdNYe1p6iQDVUdq3rRJWG77_PLCq7iMwYPLO-dqS4sBAQ";
  
  private final LegacyIdFactory factory_ = new LegacyIdFactory();
  
  @Test
  public void TestRoundTrip()
  {
    assertEquals(EXPECTED, factory_.messageId(TENANT_ID, MESSAGE_ID).toString());
    assertEquals(EXPECTED, factory_.messageId(TENANT_ID, ImmutableByteArray.newInstance(MESSAGE_ID)).toString());
  }

}
