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
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Immutable
public class ImmutableJsonObject extends JsonObject<IImmutableJsonDomNode> implements IImmutableJsonDomNode
{
  private final ImmutableMap<String, IImmutableJsonDomNode> children_;
  private final ImmutableList<String>                       names_;
  private final ImmutableSet<String>                        sortedNames_;
  private final int                                         maxNameLen_;
  
  public ImmutableJsonObject(Map<String, IJsonDomNode> children, LinkedList<String> names, TreeSet<String> sortedNames)
  {
    Map<String, IImmutableJsonDomNode> c = new HashMap<>(children.size());
    
    for(Entry<String, IJsonDomNode> entry : children.entrySet())
    {
      IJsonDomNode child = entry.getValue();
      
      if(child instanceof IImmutableJsonDomNode)
      {
        c.put(entry.getKey(), (IImmutableJsonDomNode) child);
      }
      else
      {
        c.put(entry.getKey(), ((IMutableJsonDomNode)child).immutify());
      }
    }
    
    children_     = ImmutableMap.copyOf(c);
    names_        = ImmutableList.copyOf(names);
    sortedNames_  = ImmutableSet.copyOf(sortedNames);
    
    int maxNameLen = 0;

    for(String name : sortedNames_)
    {
      maxNameLen = Math.max(maxNameLen, name.length());
    }
    
    maxNameLen_ = maxNameLen + QUOTE_MARGIN;
  }

  @Override
  public int getMaxNameLen()
  {
    return maxNameLen_;
  }
  
  @Override
  public @Nullable IImmutableJsonDomNode  get(String name)
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
}
