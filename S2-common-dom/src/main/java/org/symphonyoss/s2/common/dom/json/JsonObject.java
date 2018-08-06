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
import java.util.Iterator;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.dom.DomWriter;
import org.symphonyoss.s2.common.dom.IStringProvider;
import org.symphonyoss.s2.common.exception.InvalidValueException;

public abstract class JsonObject<N extends IJsonDomNode> implements IJsonObject<N>
{
  public static final String      OPEN_QUOTE   = "\"";
  public static final String      CLOSE_QUOTE  = "\":";
  public static final int         QUOTE_MARGIN = OPEN_QUOTE.length() + CLOSE_QUOTE.length() + 1;

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
  public IJsonObject<?> getRequiredObject(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IJsonObject)
      return (IJsonObject<?>) node;
    
    throw new IllegalStateException("\"" + name + "\" is not an object");
  }
  
  @Override
  public String getString(String name, String defaultValue)
  {
    N node = get(name);
    
    if(node == null)
      return defaultValue;
    
    if(node instanceof IStringProvider)
      return ((JsonString) node).getValue();
    
    throw new IllegalStateException("\"" + name + "\" is not a String");
  }
  
  @Override
  public String getRequiredString(String name)
  {
    N node = get(name);
    
    if(node == null)
      throw new IllegalStateException("\"" + name + "\" does not exist");
    
    if(node instanceof IStringProvider)
      return ((JsonString) node).getValue();
    
    throw new IllegalStateException("\"" + name + "\" is not a String");
  }

  /**
   * Return the string value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws InvalidValueException if the field does not exist or is not a string value.
   * 
   * @deprecated call getRequiredString() instead.
   */
  @Deprecated
  public String getString(String name) throws InvalidValueException
  {
    N node = get(name);
    
    if(node == null)
      throw new InvalidValueException("\"" + name + "\" does not exist");
    
    if(node instanceof IStringProvider)
      return ((JsonString) node).getValue();
    
    throw new InvalidValueException("\"" + name + "\" is not a String");
  }
}
