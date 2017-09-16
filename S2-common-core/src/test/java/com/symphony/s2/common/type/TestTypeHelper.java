/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
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

package com.symphony.s2.common.type;

import java.time.Instant;

import org.junit.Test;
import org.junit.Assert;

import com.google.protobuf.ByteString;
import com.symphony.s2.common.type.TypeHelper;

public class TestTypeHelper
{
  @Test
  public void testInstant()
  {
    Instant now = Instant.now();
    ByteString bytes = TypeHelper.convertToByteString(now);
    
    Instant then = TypeHelper.newInstant(bytes);
    
    Assert.assertEquals(now, then);
    
  }
}
