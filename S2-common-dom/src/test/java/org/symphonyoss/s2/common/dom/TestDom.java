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

package org.symphonyoss.s2.common.dom;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.symphonyoss.s2.common.dom.json.JsonBoolean;
import org.symphonyoss.s2.common.dom.json.JsonDom;
import org.symphonyoss.s2.common.dom.json.JsonInteger;
import org.symphonyoss.s2.common.dom.json.JsonNumber;
import org.symphonyoss.s2.common.dom.json.JsonObject;
import org.symphonyoss.s2.common.dom.json.JsonString;

public class TestDom
{
  @Test
  public void testJsonDom() throws IOException
  {
    DomSerializer serializer = DomSerializer.newBuilder().build();
       
    test("{}\n", serializer.serialize(new JsonDom()));
    test("[\n" + 
        "  \"Hello World\"\n" + 
        "]\n", serializer.serialize(new JsonDom().add(new JsonString("Hello World"))));
    test("{\n" + 
        "  \"Bruce\":                      \"Skingle\",\n" + 
        "  \"Mike\":                       \"Harmon\",\n" + 
        "  \"MauritzioVeryLongNameDude\":  \"Green\"\n" + 
        "}\n", serializer.serialize(getJsonDom()));
    
    test("[\n" + 
        "  {\n" + 
        "    \"Bruce\":                      \"Skingle\",\n" + 
        "    \"Mike\":                       \"Harmon\",\n" + 
        "    \"MauritzioVeryLongNameDude\":  \"Green\"\n" + 
        "  },\n" + 
        "  \"Another String\"\n" + 
        "]\n", serializer.serialize(getJsonDom().add(new JsonString("Another String"))));
  }
  
  private void test(String expected, String received)
  {
    if(!expected.equals(received))
    {
      System.out.format("expected %s%nreceived %s%n", expected, received);
      System.out.flush();
      
      Assert.assertEquals(expected, received);
    }
  }

  @Test
  public void testCanonicalJsonDom() throws IOException
  {
    DomSerializer serializer = DomSerializer.newBuilder()
        .withCanonicalMode(true)
        .build();
    
    test("{}", serializer.serialize(new JsonDom()));
    test("[\"Hello World\"]", serializer.serialize(new JsonDom().add(new JsonString("Hello World"))));
    test("[0]", serializer.serialize(new JsonDom().add(new JsonInteger(0))));
    test("[9223372036854775807]", serializer.serialize(new JsonDom().add(new JsonInteger(Long.MAX_VALUE))));
    test("[-9223372036854775808]", serializer.serialize(new JsonDom().add(new JsonInteger(Long.MIN_VALUE))));
    test("[9223372036854775807]", serializer.serialize(new JsonDom().add(new JsonNumber(Long.MAX_VALUE))));
    test("[-9223372036854775808]", serializer.serialize(new JsonDom().add(new JsonNumber(Long.MIN_VALUE))));
    test("[1.79769e+308]", serializer.serialize(new JsonDom().add(new JsonNumber(Double.MAX_VALUE))));
    test("[4.90000e-324]", serializer.serialize(new JsonDom().add(new JsonNumber(Double.MIN_VALUE))));
    test("[true]", serializer.serialize(new JsonDom().add(new JsonBoolean(true))));
    test("[false]", serializer.serialize(new JsonDom().add(new JsonBoolean(false))));
    test("{\"Bruce\":\"Skingle\",\"MauritzioVeryLongNameDude\":\"Green\",\"Mike\":\"Harmon\"}", serializer.serialize(getJsonDom()));
    test("[{\"Bruce\":\"Skingle\",\"MauritzioVeryLongNameDude\":\"Green\",\"Mike\":\"Harmon\"},\"Another String\"]", serializer.serialize(getJsonDom().add(new JsonString("Another String"))));
  }

  private JsonDom getJsonDom()
  {
    JsonDom dom = new JsonDom()
        .add(new JsonObject().add("Bruce", new JsonString("Skingle"))
            .add("Mike", new JsonString("Harmon"))
            .add("MauritzioVeryLongNameDude", new JsonString("Green")));
    
    return dom;
  }
}
