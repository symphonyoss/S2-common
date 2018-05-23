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

public class MutableJsonList extends MutableJsonArray<MutableJsonList>
{
  private LinkedList<IJsonDomNode> children_    = new LinkedList<>();
  
  public MutableJsonList add(IJsonDomNode child)
  {
    children_.add(child);
    
    return this;
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
  public ImmutableJsonList immutify()
  {
    return new ImmutableJsonList(children_);
  }

  @Override
  public MutableJsonList newMutableCopy()
  {
    MutableJsonList result = new MutableJsonList();
    
    for(IJsonDomNode child : children_)
      result.add(child.newMutableCopy());
    
    return result;
  }
}
