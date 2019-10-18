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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.symphonyoss.s2.common.dom.DomSerializer;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Immutable
public class ImmutableJsonObject extends JsonObject<IImmutableJsonDomNode> implements IImmutableJsonDomNode
{
  protected static final DomSerializer SERIALIZER = DomSerializer.newBuilder().withCanonicalMode(true).build();

//  private final ImmutableMap<String, IImmutableJsonDomNode> children_;
//  private final ImmutableList<String>                       names_;
  private final IImmutableJsonDomNode[]      children_;
//  private final ImmutableSet<String>                        sortedNames_;

  private final String[]                     sortedNames_;
  private final int                          maxNameLen_;
  private String                             asString_;
  private ImmutableByteArray                 asBytes_;
  
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
    
//    children_     = ImmutableMap.copyOf(c);
//    names_        = ImmutableList.copyOf(names);
//    sortedNames_  = ImmutableSet.copyOf(sortedNames);
    sortedNames_  = new String[sortedNames.size()];
    children_     = new IImmutableJsonDomNode[sortedNames.size()];
    
    int maxNameLen = 0;
    int i=0;

    for(String name : sortedNames)
    {
      sortedNames_[i] = name = name.intern();
      maxNameLen      = Math.max(maxNameLen, name.length());
      children_[i++]  = c.get(name);
    }
    
    maxNameLen_ = maxNameLen + QUOTE_MARGIN;
  }

  @Override
  public IImmutableJsonDomNode immutify()
  {
    return this;
  }
  
  @Override
  public MutableJsonObject newMutableCopy()
  {
    MutableJsonObject result = new MutableJsonObject();

    int i=0;
    
    for(String name : sortedNames_)
    {
      result.add(name, children_[i++].newMutableCopy());
    }
    
    return result;
  }
  
  @Override
  public MutableJsonObject  mutify()
  {
    return newMutableCopy();
  }

  @Override
  public int getMaxNameLen()
  {
    return maxNameLen_;
  }
  
  @Override
  public boolean containsKey(String name)
  {
//    return children_.containsKey(name);
    for(String n : sortedNames_)
    {
      if(n.equals(name))
        return true;
    }
    return false;
  }

  @Override
  public @Nullable IImmutableJsonDomNode  get(String name)
  {
//    return children_.get(name);
    
    int i=0;
    
    for(String n : sortedNames_)
    {
      if(n.equals(name))
        return children_[i];
      
      i++;
    }
    
    return null;
  }
  
  @Override
  public Iterator<String> getSortedNameIterator()
  {
//    return sortedNames_.iterator();
    return new ArrayIterator(sortedNames_);
  }
  
  @Override
  public Iterator<String> getNameIterator()
  {
//    return sortedNames_.iterator();
    return new ArrayIterator(sortedNames_);
  }
  
  class ArrayIterator implements Iterator<String>
  {
    private int index_ = 0;
    private String[] array_;
    
    public ArrayIterator(String[] array)
    {
      array_ = array;
    }

    @Override
    public boolean hasNext()
    {
      return index_ < array_.length;
    }

    @Override
    public String next()
    {
      return array_[index_++];
    }
    
  }
  
  @Override
  public @Nonnull ImmutableByteArray serialize()
  {
    if(asBytes_ == null)
      asBytes_ = ImmutableByteArray.newInstance(toString());
    
    return asBytes_;
  }
  
  @Override
  public synchronized @Nonnull String toString()
  {
    if(asString_ == null)
      asString_ = SERIALIZER.serialize(this);
    
    return asString_;
  }
  
  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof ImmutableJsonObject && toString().equals(((ImmutableJsonObject)other).toString());
  }

  @Override
  public int compareTo(IImmutableJsonDomNode other)
  {
    return toString().compareTo(other.toString());
  }
}
