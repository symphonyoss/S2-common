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
import org.symphonyoss.s2.common.dom.json.ImmutableJsonObject;
import org.symphonyoss.s2.common.dom.json.JsonBoolean;
import org.symphonyoss.s2.common.dom.json.JsonDouble;
import org.symphonyoss.s2.common.dom.json.JsonInteger;
import org.symphonyoss.s2.common.dom.json.JsonLong;
import org.symphonyoss.s2.common.dom.json.JsonString;
import org.symphonyoss.s2.common.dom.json.MutableJsonArray;
import org.symphonyoss.s2.common.dom.json.MutableJsonDom;
import org.symphonyoss.s2.common.dom.json.MutableJsonObject;

public class TestDom
{
  @Test
  public void testJsonDom() throws IOException
  {
    DomSerializer serializer = DomSerializer.newBuilder().build();
       
    test("{}\n", serializer.serialize(new MutableJsonDom()));
    test("[\n" + 
        "  \"Hello World\"\n" + 
        "]\n", serializer.serialize(new MutableJsonDom().add(new JsonString("Hello World"))));
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
    
    test("{}", serializer.serialize(new MutableJsonDom()));
    test("[\"Hello World\"]", serializer.serialize(new MutableJsonDom().add(new JsonString("Hello World"))));
    test("[0]", serializer.serialize(new MutableJsonDom().add(new JsonInteger(0))));
    test("[9223372036854775807]", serializer.serialize(new MutableJsonDom().add(new JsonLong(Long.MAX_VALUE))));
    test("[-9223372036854775808]", serializer.serialize(new MutableJsonDom().add(new JsonLong(Long.MIN_VALUE))));
    test("[9.223372036854776E18]", serializer.serialize(new MutableJsonDom().add(new JsonDouble((double)Long.MAX_VALUE))));
    test("[-9.223372036854776E18]", serializer.serialize(new MutableJsonDom().add(new JsonDouble((double)Long.MIN_VALUE))));
    test("[1.7976931348623157E308]", serializer.serialize(new MutableJsonDom().add(new JsonDouble(Double.MAX_VALUE))));
    test("[4.9E-324]", serializer.serialize(new MutableJsonDom().add(new JsonDouble(Double.MIN_VALUE))));
    test("[true]", serializer.serialize(new MutableJsonDom().add(new JsonBoolean(true))));
    test("[false]", serializer.serialize(new MutableJsonDom().add(new JsonBoolean(false))));
    test("{\"Bruce\":\"Skingle\",\"MauritzioVeryLongNameDude\":\"Green\",\"Mike\":\"Harmon\"}", serializer.serialize(getJsonDom()));
    test("[{\"Bruce\":\"Skingle\",\"MauritzioVeryLongNameDude\":\"Green\",\"Mike\":\"Harmon\"},\"Another String\"]", serializer.serialize(getJsonDom().add(new JsonString("Another String"))));
  }

  private MutableJsonDom getJsonDom()
  {
    MutableJsonDom dom = new MutableJsonDom()
        .add(new MutableJsonObject().add("Bruce", new JsonString("Skingle"))
            .add("Mike", new JsonString("Harmon"))
            .add("MauritzioVeryLongNameDude", new JsonString("Green")));
    
    return dom;
}
  
  @Test
  public void testImmutify()
  {
    DomSerializer serializer = DomSerializer.newBuilder()
        .withCanonicalMode(true)
        .build();
    
    MutableJsonObject mutableObject = createObject(1);
    
    String expected = "{\"1 four\":true,\"1 one\":1,\"1 three\":[3,4],\"1 two\":\"2\"}";
    
    test(expected, 
        serializer.serialize(mutableObject));
    
    ImmutableJsonObject immutableObject = mutableObject.immutify();
    
    test(expected, 
        serializer.serialize(immutableObject));
  }
  
  @Test
  public void testNested()
  {
     DomSerializer serializer = DomSerializer.newBuilder()
         .withCanonicalMode(true)
         .build();
     
     String expected = "{\"3 one\":{\"2 one\":{\"1 four\":true,\"1 one\":1,\"1 three\":[3,4],\"1 two\":\"2\"},\"2 three\":[{\"1 four\":true,\"1 one\":9,\"1 three\":[11,12],\"1 two\":\"10\"},{\"1 four\":true,\"1 one\":13,\"1 three\":[15,16],\"1 two\":\"14\"}],\"2 two\":{\"1 four\":true,\"1 one\":5,\"1 three\":[7,8],\"1 two\":\"6\"}},\"3 three\":[{\"2 one\":{\"1 four\":true,\"1 one\":33,\"1 three\":[35,36],\"1 two\":\"34\"},\"2 three\":[{\"1 four\":true,\"1 one\":41,\"1 three\":[43,44],\"1 two\":\"42\"},{\"1 four\":true,\"1 one\":45,\"1 three\":[47,48],\"1 two\":\"46\"}],\"2 two\":{\"1 four\":true,\"1 one\":37,\"1 three\":[39,40],\"1 two\":\"38\"}},{\"2 one\":{\"1 four\":true,\"1 one\":49,\"1 three\":[51,52],\"1 two\":\"50\"},\"2 three\":[{\"1 four\":true,\"1 one\":57,\"1 three\":[59,60],\"1 two\":\"58\"},{\"1 four\":true,\"1 one\":61,\"1 three\":[63,64],\"1 two\":\"62\"}],\"2 two\":{\"1 four\":true,\"1 one\":53,\"1 three\":[55,56],\"1 two\":\"54\"}}],\"3 two\":{\"2 one\":{\"1 four\":true,\"1 one\":17,\"1 three\":[19,20],\"1 two\":\"18\"},\"2 three\":[{\"1 four\":true,\"1 one\":25,\"1 three\":[27,28],\"1 two\":\"26\"},{\"1 four\":true,\"1 one\":29,\"1 three\":[31,32],\"1 two\":\"30\"}],\"2 two\":{\"1 four\":true,\"1 one\":21,\"1 three\":[23,24],\"1 two\":\"22\"}}}";
     
     MutableJsonObject mutableObject = createObject(3);
     
     test(expected, 
         serializer.serialize(mutableObject));
     
     ImmutableJsonObject immutableObject = mutableObject.immutify();
     
     test(expected, 
         serializer.serialize(immutableObject));
  }
  
  private int nestCount = 1;
  
  private MutableJsonObject createObject(int l)
  {
    if(l == 1)
    {
      return new MutableJsonObject()
          .addIfNotNull(l + " one", nestCount++)
          .addIfNotNull(l + " two", String.valueOf(nestCount++))
          .add(l + " three", new MutableJsonArray()
              .add(nestCount++)
              .add(nestCount++))
          .addIfNotNull(l + " four", Boolean.TRUE);
    }
    
    return new MutableJsonObject()
        .add(l + " one", createObject(l - 1))
        .add(l + " two", createObject(l - 1))
        .add(l + " three", new MutableJsonArray()
            .add(createObject(l - 1))
            .add(createObject(l - 1)));
      
  }
}
