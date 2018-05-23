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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

import org.symphonyoss.s2.common.dom.DomSerializer;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

import com.google.common.collect.ImmutableList;

/**
 * An immutable node from a JSON DOM tree.
 * 
 * @author Bruce Skingle
 *
 */
public class ImmutableJsonDom extends JsonDom<IImmutableJsonDomNode> implements IImmutableJsonDomNode
{
  protected static final DomSerializer               SERIALIZER = DomSerializer.newBuilder().withCanonicalMode(true)
      .build();

  private final ImmutableList<IImmutableJsonDomNode> children_;
  private final @Nonnull String                      asString_;
  private final @Nonnull ImmutableByteArray          asBytes_;
  
  /**
   * Create a node with the given children, if the children are immutable then they are added directly
   * otherwise their immutify() method is called and the immutable equivalent is added.
   *  
   * @param children  The children of the required node.
   */
  public ImmutableJsonDom(Collection<IJsonDomNode> children)
  {
    ArrayList<IImmutableJsonDomNode> c = new ArrayList<>(children.size());
    
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
    children_ = ImmutableList.copyOf(c);
    asString_ = SERIALIZER.serialize(this);
    asBytes_ = ImmutableByteArray.newInstance(asString_);
  }

  @Override
  public IImmutableJsonDomNode immutify()
  {
    return this;
  }
  
  @Override
  public MutableJsonDom newMutableCopy()
  {
    MutableJsonDom result = new MutableJsonDom();
    
    for(IImmutableJsonDomNode child : children_)
      result.add(child.newMutableCopy());
    
    return result;
  }

  @Override
  public int size()
  {
    return children_.size();
  }

  @Override
  public boolean isEmpty()
  {
    return children_.isEmpty();
  }

  @Override
  public IImmutableJsonDomNode getFirst()
  {
    return children_.get(0);
  }

  @Override
  public Iterator<IImmutableJsonDomNode> iterator()
  {
    return children_.iterator();
  }
  
  @Override
  public @Nonnull ImmutableByteArray serialize()
  {
    return asBytes_;
  }
  
  @Override
  public @Nonnull String toString()
  {
    return asString_;
  }
  
  @Override
  public int hashCode()
  {
    return asString_.hashCode();
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof ImmutableJsonDom && asString_.equals(((ImmutableJsonDom)other).asString_);
  }

  @Override
  public int compareTo(IImmutableJsonDomNode other)
  {
    return asString_.compareTo(other.toString());
  }
}
