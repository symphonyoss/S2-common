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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.symphonyoss.s2.common.dom.TypeAdaptor;

import com.google.common.collect.ImmutableSet;

public abstract class JsonSet<N extends IJsonDomNode> extends JsonArray<N> implements IJsonSet<N>
{
  public <T> ImmutableSet<T> asImmutableSetOf(Class<T> type)
  {
    Set<T> set = new HashSet<>();
    
    Iterator<N>  it = iterator();
    
    while(it.hasNext())
    {
      T value = TypeAdaptor.adapt(type, it.next());
      
      if(!set.add(value))
        throw new IllegalArgumentException("Duplicate value in set input.");
    }
    return  ImmutableSet.copyOf(set);
  }
}
