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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

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
  public MutableJsonObject newMutableCopy()
  {
    MutableJsonObject result = new MutableJsonObject();
    
    for(String name : names_)
      result.add(name, children_.get(name).newMutableCopy());
    
    return result;
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
    
    return add(name, new JsonBase64String(Base64.encodeBase64String(value.toByteArray())));
  }
  
  public MutableJsonObject addIfNotNull(String name, ImmutableByteArray value)
  {
    if(value == null)
      return this;
    
    return add(name, new JsonBase64String(value.toBase64String()));
  }
  
  public MutableJsonObject addIfNotNull(String name, JsonArray<IJsonDomNode> value)
  {
    if(value == null)
      return this;
    
    return add(name, value);
  }
  
  public MutableJsonObject addCollectionOfDomNode(String name, List<? extends IJsonDomNodeProvider> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(IJsonDomNodeProvider v : value)
      array.add(v.getJsonDomNode());
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfBoolean(String name, List<Boolean> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(Boolean v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfLong(String name, List<Long> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(Long v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfInteger(String name, List<Integer> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(Integer v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfDouble(String name, List<Double> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(Double v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfFloat(String name, List<Float> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(Float v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfString(String name, List<String> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(String v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfByteString(String name, List<ByteString> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(ByteString v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfImmutableByteArray(String name, List<ImmutableByteArray> value)
  {
    MutableJsonList array = new MutableJsonList();
    
    for(ImmutableByteArray v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfDomNode(String name, Set<? extends IJsonDomNodeProvider> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(IJsonDomNodeProvider v : value)
      array.add(v.getJsonDomNode());
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfBoolean(String name, Set<Boolean> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(Boolean v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfLong(String name, Set<Long> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(Long v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfInteger(String name, Set<Integer> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(Integer v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfDouble(String name, Set<Double> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(Double v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfFloat(String name, Set<Float> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(Float v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfString(String name, Set<String> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(String v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfByteString(String name, Set<ByteString> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(ByteString v : value)
      array.add(v);
    
    return add(name, array);
  }
  
  public MutableJsonObject addCollectionOfImmutableByteArray(String name, Set<ImmutableByteArray> value)
  {
    MutableJsonSet array = new MutableJsonSet();
    
    for(ImmutableByteArray v : value)
      array.add(v);
    
    return add(name, array);
  }

  public void clear()
  {
    children_.clear();
    names_.clear();
    sortedNames_.clear();
  }
  
  /**
   * Add all attributes of the given object to this object.
   * 
   * @param other Another object to merge with the current object.
   * 
   * @return this (fluent method).
   */
  public MutableJsonObject addAll(MutableJsonObject other)
  {
    Iterator<String> it = other.getNameIterator();
    
    while(it.hasNext())
    {
      String name = it.next();
      IJsonDomNode element = other.get(name);
      
      if(element instanceof IJsonObject)
      {
        IJsonDomNode localElement = get(name);
        
        if(localElement instanceof IJsonObject)
        {
          // The current is an object so merge the child objects
          ((MutableJsonObject) localElement).addAll((MutableJsonObject) element);
        }
        else
        {
          // The current attribute is not an object (or is not present) so replace whatever is there with the new object.
          add(name, element);
        }
      }
      else
      {
        add(name, element);
      }
    }
    
    return this;
  }
}
