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
import java.util.LinkedList;

import org.apache.commons.codec.binary.Base64;

import com.google.protobuf.ByteString;

public class MutableJsonArray extends JsonArray<IJsonDomNode> implements IMutableJsonDomNode
{
  private LinkedList<IJsonDomNode> children_    = new LinkedList<>();
  
  public MutableJsonArray add(IJsonDomNode child)
  {
    children_.add(child);
    
    return this;
  }
  
  public MutableJsonArray add(Boolean value)
  {
    return add(new JsonBoolean(value));
  }
  
  public MutableJsonArray add(Long value)
  {
    return add(new JsonLong(value));
  }
  
  public MutableJsonArray add(Integer value)
  {
    return add(new JsonInteger(value));
  }
  
  public MutableJsonArray add(Double value)
  {
    return add(new JsonDouble(value));
  }
  
  public MutableJsonArray add(Float value)
  {
    return add(new JsonFloat(value));
  }
  
  public MutableJsonArray add(String value)
  {
    return add(new JsonString(value));
  }
  
  public MutableJsonArray add(ByteString value)
  {
    return add(new JsonString(Base64.encodeBase64URLSafeString(value.toByteArray())));
  }

  @Override
  public boolean isEmpty()
  {
    return children_.isEmpty();
  }

  @Override
  public Iterator<IJsonDomNode> iterator()
  {
    return children_.iterator();
  }

  @Override
  public ImmutableJsonArray immutify()
  {
    return new ImmutableJsonArray(children_);
  }
}
