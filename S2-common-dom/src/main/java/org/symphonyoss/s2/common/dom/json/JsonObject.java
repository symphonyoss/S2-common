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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.dom.DomWriter;
import org.symphonyoss.s2.common.dom.TypeAdaptor;
import org.symphonyoss.s2.common.type.provider.IBooleanProvider;
import org.symphonyoss.s2.common.type.provider.IByteStringProvider;
import org.symphonyoss.s2.common.type.provider.IDoubleProvider;
import org.symphonyoss.s2.common.type.provider.IFloatProvider;
import org.symphonyoss.s2.common.type.provider.IIntegerProvider;
import org.symphonyoss.s2.common.type.provider.ILongProvider;
import org.symphonyoss.s2.common.type.provider.IStringProvider;

import com.google.protobuf.ByteString;

public abstract class JsonObject<N extends IJsonDomNode> implements IJsonObject<N>
{
  public static final String      OPEN_QUOTE   = "\"";
  public static final String      CLOSE_QUOTE  = "\":";
  public static final int         QUOTE_MARGIN = OPEN_QUOTE.length() + CLOSE_QUOTE.length() + 1;

  public abstract MutableJsonObject  mutify();
  
  @Override
  public JsonObject<N> writeTo(DomWriter writer, @Nullable String terminator) throws IOException
  {
    Iterator<String>  it;
    int maxNameLen;
    
    if(writer.isCanonicalMode())
    {
      it = getSortedNameIterator();
      maxNameLen = 0;
    }
    else
    {
      maxNameLen = getMaxNameLen();
      
      while(maxNameLen % writer.getTabSize() != 0)
        maxNameLen++;
      
      it = getNameIterator();
    }
    
    writer.openBlock("{");
    
    while(it.hasNext())
    {
      String name = it.next();
      writer.writeColumn(OPEN_QUOTE, name, CLOSE_QUOTE, maxNameLen);
      
      nullSafeGet(name).writeTo(writer, it.hasNext() ? "," : null);
    }
    writer.closeBlock("}", terminator);
    
    return this;
  }
  
  /*
   * We only call this method with keys which we know to be present as they are 
   * iterated from one of the lists of keys. This method is needed to avoid a compile
   * error from the checker framework (build with the checker maven profile).
   */
  @SuppressWarnings("nullness")
  private IJsonDomNode nullSafeGet(String name)
  {
    return get(name);
  }
  
  
  
  @Override
  public IJsonObject<?> getObject(String name)
  {
    N node = get(name);
    
    if(node == null)
      return null;
    
    if(node instanceof IJsonObject)
      return (IJsonObject<?>) node;
    
    throw new IllegalStateException("\"" + name + "\" is not an object");
  }

  @Override
  public IJsonObject<?> getRequiredObject(String name)
  {
    IJsonObject<?> object = getObject(name);
    
    if(object == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    return object;
  }
  
  @Override
  public String getString(String name, String defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof IStringProvider)
      return ((IStringProvider) node).asString();
    
    throw new IllegalStateException("\"" + name + "\" is not a String");
  }
  
  @Override
  public String getRequiredString(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IStringProvider)
      return ((IStringProvider) node).asString();
    
    throw new IllegalStateException("\"" + name + "\" is not a String");
  }
  
  @Override
  public Integer getInteger(String name, Integer defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof IIntegerProvider)
      return ((IIntegerProvider) node).asInteger();
    
    throw new IllegalStateException("\"" + name + "\" is not a Integer");
  }
  
  @Override
  public Integer getRequiredInteger(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IIntegerProvider)
      return ((IIntegerProvider) node).asInteger();
    
    throw new IllegalStateException("\"" + name + "\" is not a Integer");
  }
  
  @Override
  public Long getLong(String name, Long defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof ILongProvider)
      return ((ILongProvider) node).asLong();
    
    throw new IllegalStateException("\"" + name + "\" is not a Long");
  }
  
  @Override
  public Long getRequiredLong(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof ILongProvider)
      return ((ILongProvider) node).asLong();
    
    throw new IllegalStateException("\"" + name + "\" is not a Long");
  }
  
  @Override
  public Float getFloat(String name, Float defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof IFloatProvider)
      return ((IFloatProvider) node).asFloat();
    
    throw new IllegalStateException("\"" + name + "\" is not a Float");
  }
  
  @Override
  public Float getRequiredFloat(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IFloatProvider)
      return ((IFloatProvider) node).asFloat();
    
    throw new IllegalStateException("\"" + name + "\" is not a Float");
  }
  
  @Override
  public Double getDouble(String name, Double defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof IDoubleProvider)
      return ((IDoubleProvider) node).asDouble();
    
    throw new IllegalStateException("\"" + name + "\" is not a Double");
  }
  
  @Override
  public Double getRequiredDouble(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IDoubleProvider)
      return ((IDoubleProvider) node).asDouble();
    
    throw new IllegalStateException("\"" + name + "\" is not a Double");
  }
  
  @Override
  public ByteString getByteString(String name, ByteString defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof IByteStringProvider)
      return ((IByteStringProvider) node).asByteString();
    
    throw new IllegalStateException("\"" + name + "\" is not a ByteString");
  }
  
  @Override
  public ByteString getRequiredByteString(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IByteStringProvider)
      return ((IByteStringProvider) node).asByteString();
    
    throw new IllegalStateException("\"" + name + "\" is not a ByteString");
  }
  
  @Override
  public Boolean getBoolean(String name, Boolean defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof IBooleanProvider)
      return ((IBooleanProvider) node).asBoolean();
    
    throw new IllegalStateException("\"" + name + "\" is not a Boolean");
  }
  
  @Override
  public Boolean getRequiredBoolean(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IBooleanProvider)
      return ((IBooleanProvider) node).asBoolean();
    
    throw new IllegalStateException("\"" + name + "\" is not a Boolean");
  }

  /**
   * Return the string value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalArgumentException if the field does not exist or is not a string value.
   * 
   * @deprecated call getRequiredString() instead.
   */
  @Deprecated
  public String getString(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalArgumentException("\"" + name + "\" does not exist");
    
    if(node instanceof IStringProvider)
      return ((IStringProvider) node).asString();
    
    throw new IllegalArgumentException("\"" + name + "\" is not a String");
  }

  public <T> List<T> getListOf(Class<T> type, String name)
  {
    List<T> result = new LinkedList<>();
    IJsonDomNode node = get(name); 
    
    if(node instanceof IJsonArray)
    {
      for(IJsonDomNode v : ((IJsonArray<?>)node))
        result.add( TypeAdaptor.adapt(type, v));
    }
    
    return result;
  }
}
