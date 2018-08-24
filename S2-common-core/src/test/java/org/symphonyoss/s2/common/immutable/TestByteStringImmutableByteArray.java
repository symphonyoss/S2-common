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

package org.symphonyoss.s2.common.immutable;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class TestByteStringImmutableByteArray
{
  @Test
  public void testText()
  {
    ByteString input = ByteString.copyFrom("HelloWorld".getBytes(StandardCharsets.UTF_8));

    ImmutableByteArray a = ImmutableByteArray.newInstance(input);
    String base64 = a.toBase64String();
    String urlSafe = a.toBase64UrlSafeString();
    
    assertEquals("SGVsbG9Xb3JsZA==", base64);

    assertEquals("SGVsbG9Xb3JsZA", urlSafe);
  }


  @Test
  public void testBinary()
  {
    String base64Input = "qgYis5r3xcj61QjW477JEaZQx+pC4AEbFEmgWiFUE5wUje4BAbAGAcIGGUeYaEZe0YYgrUqsihl1X09///6vUPLxZnTYBsgB4AaACA==";
    ByteString input = ByteString.copyFrom(Base64.decodeBase64(base64Input));

    ImmutableByteArray a = ImmutableByteArray.newInstance(input);
    String base64 = a.toBase64String();
    String urlSafe = a.toBase64UrlSafeString();
    
    assertEquals(base64Input, base64);

    assertEquals("qgYis5r3xcj61QjW477JEaZQx-pC4AEbFEmgWiFUE5wUje4BAbAGAcIGGUeYaEZe0YYgrUqsihl1X09___6vUPLxZnTYBsgB4AaACA", urlSafe);
  }

}
