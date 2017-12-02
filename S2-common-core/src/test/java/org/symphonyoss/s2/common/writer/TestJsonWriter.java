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

package org.symphonyoss.s2.common.writer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.junit.Assert;
import org.junit.Test;

public class TestJsonWriter
{
  @Test
  public void testBuilder()
  {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(bout);
    
    JsonWriter out = JsonWriter.newBuilder(writer).build();
    
    out.openObject("test");
    out.alignAttribute("name", "Value");
    out.closeObject();
    out.close();
    
    System.out.println(bout.toString());
    Assert.assertEquals("\"test\": {\n" + 
        "  \"name\": \"Value\"\n" + 
        "}\n", bout.toString());
    
    bout = new ByteArrayOutputStream();
    writer = new OutputStreamWriter(bout);
    out = JsonWriter.newBuilder(writer).withTabSize(8).build();
    
    out.openObject("test");
    out.alignAttribute("name", "Value");
    out.alignAttribute("A very long name with spaces in", "A Value");
    out.closeObject();
    out.close();
    
    System.out.println(bout.toString());
    Assert.assertEquals("\"test\": {\n" + 
        "        \"name\":                                 \"Value\",\n" + 
        "        \"A very long name with spaces in\":      \"A Value\"\n" + 
        "}\n", bout.toString());
  }
}
