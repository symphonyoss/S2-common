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
import org.symphonyoss.s2.common.dom.json.MutableJsonList;
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
    
    test("{}\n", serializer.serialize(new MutableJsonDom()));
    test("[\n" + 
        "  \"Hello World\"\n" + 
        "]\n", serializer.serialize(new MutableJsonDom().add(new JsonString("Hello World"))));
    test("[\n  0\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonInteger(0))));
    test("[\n  9223372036854775807\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonLong(Long.MAX_VALUE))));
    test("[\n  -9223372036854775808\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonLong(Long.MIN_VALUE))));
    test("[\n  9.223372036854776E18\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonDouble((double)Long.MAX_VALUE))));
    test("[\n  -9.223372036854776E18\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonDouble((double)Long.MIN_VALUE))));
    test("[\n  1.7976931348623157E308\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonDouble(Double.MAX_VALUE))));
    test("[\n  4.9E-324\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonDouble(Double.MIN_VALUE))));
    test("[\n  true\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonBoolean(true))));
    test("[\n  false\n]\n", serializer.serialize(new MutableJsonDom().add(new JsonBoolean(false))));
    test("{\n" + 
        "  \"Bruce\":\"Skingle\",\n" + 
        "  \"MauritzioVeryLongNameDude\":\"Green\",\n" + 
        "  \"Mike\":\"Harmon\"\n" + 
        "}\n", serializer.serialize(getJsonDom()));
    test("[\n" + 
        "  {\n" + 
        "    \"Bruce\":\"Skingle\",\n" + 
        "    \"MauritzioVeryLongNameDude\":\"Green\",\n" + 
        "    \"Mike\":\"Harmon\"\n" + 
        "  },\n" + 
        "  \"Another String\"\n" + 
        "]\n", serializer.serialize(getJsonDom().add(new JsonString("Another String"))));
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
    
    String expected = "{\n" + 
        "  \"1 four\":true,\n" + 
        "  \"1 one\":1,\n" + 
        "  \"1 three\":[\n" + 
        "    3,\n" + 
        "    4\n" + 
        "  ],\n" + 
        "  \"1 two\":\"2\"\n" + 
        "}\n";
    
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
     
     String expected = "{\n" + 
         "  \"3 one\":{\n" + 
         "    \"2 one\":{\n" + 
         "      \"1 four\":true,\n" + 
         "      \"1 one\":1,\n" + 
         "      \"1 three\":[\n" + 
         "        3,\n" + 
         "        4\n" + 
         "      ],\n" + 
         "      \"1 two\":\"2\"\n" + 
         "    },\n" + 
         "    \"2 three\":[\n" + 
         "      {\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":9,\n" + 
         "        \"1 three\":[\n" + 
         "          11,\n" + 
         "          12\n" + 
         "        ],\n" + 
         "        \"1 two\":\"10\"\n" + 
         "      },\n" + 
         "      {\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":13,\n" + 
         "        \"1 three\":[\n" + 
         "          15,\n" + 
         "          16\n" + 
         "        ],\n" + 
         "        \"1 two\":\"14\"\n" + 
         "      }\n" + 
         "    ],\n" + 
         "    \"2 two\":{\n" + 
         "      \"1 four\":true,\n" + 
         "      \"1 one\":5,\n" + 
         "      \"1 three\":[\n" + 
         "        7,\n" + 
         "        8\n" + 
         "      ],\n" + 
         "      \"1 two\":\"6\"\n" + 
         "    }\n" + 
         "  },\n" + 
         "  \"3 three\":[\n" + 
         "    {\n" + 
         "      \"2 one\":{\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":33,\n" + 
         "        \"1 three\":[\n" + 
         "          35,\n" + 
         "          36\n" + 
         "        ],\n" + 
         "        \"1 two\":\"34\"\n" + 
         "      },\n" + 
         "      \"2 three\":[\n" + 
         "        {\n" + 
         "          \"1 four\":true,\n" + 
         "          \"1 one\":41,\n" + 
         "          \"1 three\":[\n" + 
         "            43,\n" + 
         "            44\n" + 
         "          ],\n" + 
         "          \"1 two\":\"42\"\n" + 
         "        },\n" + 
         "        {\n" + 
         "          \"1 four\":true,\n" + 
         "          \"1 one\":45,\n" + 
         "          \"1 three\":[\n" + 
         "            47,\n" + 
         "            48\n" + 
         "          ],\n" + 
         "          \"1 two\":\"46\"\n" + 
         "        }\n" + 
         "      ],\n" + 
         "      \"2 two\":{\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":37,\n" + 
         "        \"1 three\":[\n" + 
         "          39,\n" + 
         "          40\n" + 
         "        ],\n" + 
         "        \"1 two\":\"38\"\n" + 
         "      }\n" + 
         "    },\n" + 
         "    {\n" + 
         "      \"2 one\":{\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":49,\n" + 
         "        \"1 three\":[\n" + 
         "          51,\n" + 
         "          52\n" + 
         "        ],\n" + 
         "        \"1 two\":\"50\"\n" + 
         "      },\n" + 
         "      \"2 three\":[\n" + 
         "        {\n" + 
         "          \"1 four\":true,\n" + 
         "          \"1 one\":57,\n" + 
         "          \"1 three\":[\n" + 
         "            59,\n" + 
         "            60\n" + 
         "          ],\n" + 
         "          \"1 two\":\"58\"\n" + 
         "        },\n" + 
         "        {\n" + 
         "          \"1 four\":true,\n" + 
         "          \"1 one\":61,\n" + 
         "          \"1 three\":[\n" + 
         "            63,\n" + 
         "            64\n" + 
         "          ],\n" + 
         "          \"1 two\":\"62\"\n" + 
         "        }\n" + 
         "      ],\n" + 
         "      \"2 two\":{\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":53,\n" + 
         "        \"1 three\":[\n" + 
         "          55,\n" + 
         "          56\n" + 
         "        ],\n" + 
         "        \"1 two\":\"54\"\n" + 
         "      }\n" + 
         "    }\n" + 
         "  ],\n" + 
         "  \"3 two\":{\n" + 
         "    \"2 one\":{\n" + 
         "      \"1 four\":true,\n" + 
         "      \"1 one\":17,\n" + 
         "      \"1 three\":[\n" + 
         "        19,\n" + 
         "        20\n" + 
         "      ],\n" + 
         "      \"1 two\":\"18\"\n" + 
         "    },\n" + 
         "    \"2 three\":[\n" + 
         "      {\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":25,\n" + 
         "        \"1 three\":[\n" + 
         "          27,\n" + 
         "          28\n" + 
         "        ],\n" + 
         "        \"1 two\":\"26\"\n" + 
         "      },\n" + 
         "      {\n" + 
         "        \"1 four\":true,\n" + 
         "        \"1 one\":29,\n" + 
         "        \"1 three\":[\n" + 
         "          31,\n" + 
         "          32\n" + 
         "        ],\n" + 
         "        \"1 two\":\"30\"\n" + 
         "      }\n" + 
         "    ],\n" + 
         "    \"2 two\":{\n" + 
         "      \"1 four\":true,\n" + 
         "      \"1 one\":21,\n" + 
         "      \"1 three\":[\n" + 
         "        23,\n" + 
         "        24\n" + 
         "      ],\n" + 
         "      \"1 two\":\"22\"\n" + 
         "    }\n" + 
         "  }\n" + 
         "}\n";
     
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
          .add(l + " three", new MutableJsonList()
              .add(nestCount++)
              .add(nestCount++))
          .addIfNotNull(l + " four", Boolean.TRUE);
    }
    
    return new MutableJsonObject()
        .add(l + " one", createObject(l - 1))
        .add(l + " two", createObject(l - 1))
        .add(l + " three", new MutableJsonList()
            .add(createObject(l - 1))
            .add(createObject(l - 1)));
      
  }
}
