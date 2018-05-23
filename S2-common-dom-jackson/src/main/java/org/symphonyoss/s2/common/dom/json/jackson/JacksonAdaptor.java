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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.symphonyoss.s2.common.dom.json.IJsonDomNode;
import org.symphonyoss.s2.common.dom.json.JsonBase64String;
import org.symphonyoss.s2.common.dom.json.JsonBoolean;
import org.symphonyoss.s2.common.dom.json.JsonDouble;
import org.symphonyoss.s2.common.dom.json.JsonFloat;
import org.symphonyoss.s2.common.dom.json.JsonInteger;
import org.symphonyoss.s2.common.dom.json.JsonLong;
import org.symphonyoss.s2.common.dom.json.JsonNull;
import org.symphonyoss.s2.common.dom.json.JsonString;
import org.symphonyoss.s2.common.dom.json.MutableJsonList;
import org.symphonyoss.s2.common.dom.json.MutableJsonObject;
import org.symphonyoss.s2.common.exception.InvalidValueException;
import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Adaptor to produce IJsonDomNode types from a Jackson parse tree.
 * 
 * @author Bruce Skingle
 *
 */
public class JacksonAdaptor
{
  static Map<Class<?>, IJacksonNodeAdaptor> adaptorMap_ = new HashMap<>();
  
  private static final ObjectMapper MAPPER = new ObjectMapper();
  
  // We can't use lambdas as this is java7 for the benefit of SBE. Sigh.
  static
  {
    adaptorMap_.put(BooleanNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return new JsonBoolean(n.asBoolean());
          }});
    
    adaptorMap_.put(TextNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            String s = n.asText();
            
            if(Base64.isBase64(s))
              return new JsonBase64String(s);
            
            return new JsonString(s);
          }});
    
    adaptorMap_.put(DoubleNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            double d = n.asDouble();
            float f = (float)d;
            
            if(d == f)
              return new JsonFloat(f);
            else
              return new JsonDouble(d);
          }});
    
    adaptorMap_.put(FloatNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return new JsonFloat((float)n.asDouble());
          }});
    
    adaptorMap_.put(LongNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            long l = n.asLong();
            int i = (int)l;
            
            if(i == l)
              return new JsonInteger(i);
            else
              return new JsonLong(l);
          }});
    
    adaptorMap_.put(IntNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return new JsonInteger(n.asInt());
          }});
    
    adaptorMap_.put(ObjectNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
            return JacksonAdaptor.adaptObject((ObjectNode) n);
            
          }});
    
    adaptorMap_.put(ArrayNode.class, 
        new IJacksonNodeAdaptor(){@Override public IJsonDomNode adapt(JsonNode n){
          MutableJsonList array = new MutableJsonList();
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
                array.add(JacksonAdaptor.adapt(childNode));
              }
            }
            return array;
          }});
  }
  
  /**
   * Return the IJsonDomNode equivalent of the given Jackson JsonNode.
   *  
   * @param node A Jackson object.
   * 
   * @return the IJsonDomNode equivalent of the given Jackson JsonNode. 
   */
  public static IJsonDomNode  adapt(JsonNode node)
  {
    IJacksonNodeAdaptor adaptor = adaptorMap_.get(node.getClass());
    
    if(adaptor == null)
    {
      throw new CodingFault("Unknown Jackson node type \"" + node.getClass().getName() + "\"");
    }
    
    return adaptor.adapt(node);
  }
  
  /**
   * Parse the give input and return a Mutable JSON object.
   * 
   * @param input JSON in byte array form.
   * 
   * @return a MutableJsonObject representing the input.
   * 
   * @throws InvalidValueException If the input is not a JSON Object.
   */
  public static MutableJsonObject parseObject(ImmutableByteArray input) throws InvalidValueException
  {
    try
    {
      return adaptObject((ObjectNode) MAPPER.readTree(input.getInputStream()));
    }
    catch (ClassCastException | IOException e)
    {
      throw new InvalidValueException("Unable to parse input", e);
    }
  }
  
  /**
   * Adapt the given Jackson object into a MutableJsonObject.
   * 
   * @param object A Jackson JSON object.
   * 
   * @return The MutableJsonObject equivalent of the given input.
   */
  public static MutableJsonObject adaptObject(ObjectNode object)
  {
    MutableJsonObject obj = new MutableJsonObject();
    Iterator<String> it = object.fieldNames();
    
    while(it.hasNext())
    {
      String childName = it.next();
      JsonNode childNode = object.get(childName);
      
      if(!childNode.isNull())
      {
        obj.add(childName, JacksonAdaptor.adapt(childNode));
      }
    }
    return obj;
  }
}
