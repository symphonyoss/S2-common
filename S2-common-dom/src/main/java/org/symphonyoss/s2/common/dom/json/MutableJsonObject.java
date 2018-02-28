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

package org.symphonyoss.s2.common.dom.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;

import com.google.protobuf.ByteString;

public class MutableJsonObject extends JsonObject<IJsonDomNode> implements IMutableJsonDomNode
{
  private Map<String, IJsonDomNode> children_    = new HashMap<>();
  private LinkedList<String>        names_       = new LinkedList<>();
  private TreeSet<String>           sortedNames_ = new TreeSet<>();
  
  @Override
  public ImmutableJsonObject immutify()
  {
    return new ImmutableJsonObject(children_,  names_ , sortedNames_);
  }
  
  @Override
  public int getMaxNameLen()
  {
    int maxNameLen = 0;

    Iterator<String> it = getNameIterator();
    
    while(it.hasNext())
    {
      maxNameLen = Math.max(maxNameLen, it.next().length());
    }
    
    return maxNameLen + QUOTE_MARGIN;
  }
  
  @Override
  public boolean containsKey(String name)
  {
    return children_.containsKey(name);
  }
  
  @Override
  public @Nullable IJsonDomNode  get(String name)
  {
    return children_.get(name);
  }
  
  @Override
  public Iterator<String> getSortedNameIterator()
  {
    return sortedNames_.iterator();
  }
  
  @Override
  public Iterator<String> getNameIterator()
  {
    return names_.iterator();
  }
  
  public MutableJsonObject addIfNotNull(String name, IJsonDomNode child)
  {
    if(child == null)
      return this;
    
    return add(name, child);
  }
  
  public MutableJsonObject add(String name, IJsonDomNode child)
  {
    if(sortedNames_.add(name))
    {
      names_.add(name);
    }
    children_.put(name, child);
    
    return this;
  }
  
  public MutableJsonObject addIfNotNull(String name, Boolean value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonBoolean(value));
  }
  
  public MutableJsonObject addIfNotNull(String name, Long value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonLong(value));
  }
  
  public MutableJsonObject addIfNotNull(String name, Integer value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonInteger(value));
  }
  
  public MutableJsonObject addIfNotNull(String name, Double value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonDouble(value));
  }
  
  public MutableJsonObject addIfNotNull(String name, Float value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonFloat(value));
  }
  
  public MutableJsonObject addIfNotNull(String name, String value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonString(value));
  }
  
  public MutableJsonObject addIfNotNull(String name, ByteString value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonBase64String(Base64.encodeBase64URLSafeString(value.toByteArray())));
  }
  
  public MutableJsonObject addIfNotNull(String name, JsonArray<IJsonDomNode> value)
  {
    if(value == null)
      return this;
    
    return add(name, value);
  }
  
  public MutableJsonObject addCollectionOfDomNode(String name, Collection<? extends IJsonDomNodeProvider> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(IJsonDomNodeProvider v : value)
      array.add(v.getJsonDomNode());
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfBoolean(String name, Collection<Boolean> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(Boolean v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfLong(String name, Collection<Long> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(Long v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfInteger(String name, Collection<Integer> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(Integer v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfDouble(String name, Collection<Double> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(Double v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfFloat(String name, Collection<Float> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(Float v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfString(String name, Collection<String> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(String v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfByteString(String name, Collection<ByteString> value)
  {
    MutableJsonArray array = new MutableJsonArray();
    
    for(ByteString v : value)
      array.add(v);
    
    return add(name, array);
  }
}
