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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.symphonyoss.s2.common.dom.DomSerializer;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

import com.google.common.collect.ImmutableSet;

@Immutable
public class ImmutableJsonSet extends JsonSet<IImmutableJsonDomNode> implements IImmutableJsonDomNode
{
  protected static final DomSerializer SERIALIZER = DomSerializer.newBuilder().withCanonicalMode(true).build();

  private final ImmutableSet<IImmutableJsonDomNode>   children_;
  private String                      asString_;
  private ImmutableByteArray          asBytes_;
  
  public ImmutableJsonSet(Set<IJsonDomNode> children)
  {
    TreeSet<IImmutableJsonDomNode> c = new TreeSet<>();
    
    for(IJsonDomNode child : children)
    {
      if(child instanceof IImmutableJsonDomNode)
      {
        c.add((IImmutableJsonDomNode) child);
      }
      else
      {
        c.add(((IMutableJsonDomNode)child).immutify());
      }
    }
    children_ = ImmutableSet.copyOf(c);
  }

  @Override
  public boolean isEmpty()
  {
    return children_.isEmpty();
  }

  @Override
  public Iterator<IImmutableJsonDomNode> iterator()
  {
    return children_.iterator();
  }

  @Override
  public ImmutableJsonSet immutify()
  {
    return this;
  }

  @Override
  public MutableJsonSet newMutableCopy()
  {
    MutableJsonSet result = new MutableJsonSet();
    
    for(IJsonDomNode child : children_)
      result.add(child.newMutableCopy());
    
    return result;
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
    return other instanceof ImmutableJsonSet && toString().equals(((ImmutableJsonSet)other).toString());
  }

  @Override
  public int compareTo(IImmutableJsonDomNode other)
  {
    return toString().compareTo(other.toString());
  }
}
