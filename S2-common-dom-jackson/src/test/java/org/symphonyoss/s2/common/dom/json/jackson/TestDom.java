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

package org.symphonyoss.s2.common.dom.json.jackson;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.symphonyoss.s2.common.dom.DomSerializer;
import org.symphonyoss.s2.common.dom.json.IJsonDomNode;
import org.symphonyoss.s2.common.dom.json.MutableJsonList;
import org.symphonyoss.s2.common.dom.json.MutableJsonObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDom
{
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
  public void testJackson() throws JsonProcessingException, IOException
  {
    DomSerializer serializer = DomSerializer.newBuilder()
        .withCanonicalMode(true)
        .build();
    
    String json = serializer.serialize(createObject(2));
    
    System.out.println("Canonical JSON:");
    System.out.println(json);
    
    ObjectMapper mapper = new ObjectMapper();
    
    JsonNode tree = mapper.readTree(json.getBytes());
    
    System.out.println("Jackson DOM:");
    System.out.println(tree);
    
    IJsonDomNode adaptor = JacksonAdaptor.adapt(tree);
    
    test(json, 
        serializer.serialize(adaptor));
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
