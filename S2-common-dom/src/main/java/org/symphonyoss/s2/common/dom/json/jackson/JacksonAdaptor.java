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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.symphonyoss.s2.common.dom.json.IJsonDomNode;
import org.symphonyoss.s2.common.dom.json.JsonBoolean;
import org.symphonyoss.s2.common.dom.json.JsonInteger;
import org.symphonyoss.s2.common.dom.json.JsonNull;
import org.symphonyoss.s2.common.dom.json.JsonNumber;
import org.symphonyoss.s2.common.dom.json.JsonString;
import org.symphonyoss.s2.common.dom.json.MutableJsonArray;
import org.symphonyoss.s2.common.dom.json.MutableJsonObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class JacksonAdaptor
{
  static Map<Class<?>, IJacksonNodeAdaptor> adaptorMap_ = new HashMap<>();
  
  // We can't use lambdas as this is java7 for the benefit of SBE. Sigh.
  static
  {
    adaptorMap_.put(BooleanNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return new JsonBoolean(n.asBoolean());
          }});
    
    adaptorMap_.put(TextNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return new JsonString(n.asText());
          }});
    
    adaptorMap_.put(DoubleNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return new JsonNumber(n.asDouble());
          }});
    
    adaptorMap_.put(IntNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return new JsonInteger(n.asInt());
          }});
    
    adaptorMap_.put(ObjectNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            MutableJsonObject obj = new MutableJsonObject();
            Iterator<String> it = n.fieldNames();
            
            while(it.hasNext())
            {
              String childName = it.next();
              JsonNode childNode = n.get(childName);
              
              if(!childNode.isNull())
              {
                System.err.println("childName=" + childName + ", childNode=" + childNode + ", class=" + childNode.getClass().getName());
  //              add(childName, process(childNode));
                obj.add(childName, JacksonAdaptor.adapt(childNode));
              }
            }
            return obj;
          }});
    
    adaptorMap_.put(ArrayNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
          MutableJsonArray array = new MutableJsonArray();
            Iterator<JsonNode> it = n.iterator();
            
            while(it.hasNext())
            {
              JsonNode childNode = it.next();
              
              if(childNode.isNull())
              {
                array.add(JsonNull.INSTANCE);
              }
              else
              {
                System.err.println("childNode=" + childNode + ", class=" + childNode.getClass().getName());
  //              add(childName, process(childNode));
                array.add(JacksonAdaptor.adapt(childNode));
              }
            }
            return array;
          }});
  }
  
  public static IJsonDomNode  adapt(JsonNode node)
  {
    IJacksonNodeAdaptor adaptor = adaptorMap_.get(node.getClass());
    
    if(adaptor == null)
    {
//      throw new CodingFault("Unknown Jackson node type \"" + node.getClass().getName() + "\"");
      
      System.err.println("Unknown Jackson node type \"" + node.getClass().getName() + "\"");
      adaptor = adaptorMap_.get(TextNode.class);
    }
    
    return adaptor.adapt(node);
  }
}
