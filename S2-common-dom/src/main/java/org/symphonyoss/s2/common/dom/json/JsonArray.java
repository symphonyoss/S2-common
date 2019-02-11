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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.dom.DomWriter;
import org.symphonyoss.s2.common.dom.TypeAdaptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class JsonArray<N extends IJsonDomNode> implements IJsonArray<N>
{
  @Override
  public JsonArray<N> writeTo(DomWriter writer, @Nullable String terminator) throws IOException
  {
    if(isEmpty())
    {
      writer.writeItem("[]", terminator);
    }
    else
    {
      Iterator<N>  it = iterator();
      writer.openBlock("[");
      
      while(it.hasNext())
      {
        it.next().writeTo(writer, it.hasNext() ? "," : null);
      }
      writer.closeBlock("]", terminator);
    }
    
    return this;
  }

  public <T> ImmutableSet<T> asImmutableSetOf(Class<T> type)
  {
    Set<T> set = new HashSet<>();
    
    Iterator<N>  it = iterator();
    
    while(it.hasNext())
    {
      N node = it.next();
      
      // TODO: figure out error / warning handling and put this check back in
      if(!(node instanceof JsonNull))
      {
        T value = TypeAdaptor.adapt(type, node);
        
        set.add(value);
        
  //      if(!set.add(value))
  //        throw new InvalidValueException("Duplicate value in set input.");
      }
    }
    return  ImmutableSet.copyOf(set);
  }
  
  public <T> ImmutableList<T> asImmutableListOf(Class<T> type)
  {
    List<T> list = new LinkedList<>();
    
    Iterator<N>  it = iterator();
    
    while(it.hasNext())
    {
      T value = TypeAdaptor.adapt(type, it.next());
      
      list.add(value);
    }
    return  ImmutableList.copyOf(list);
  }

}
