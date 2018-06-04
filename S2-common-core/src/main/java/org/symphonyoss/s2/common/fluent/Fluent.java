/*
 *
 *
 * Copyright 2018 Symphony Communication Services, LLC.
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

package org.symphonyoss.s2.common.fluent;

import org.symphonyoss.s2.common.fault.CodingFault;

/**
 * A superclass for fluent classes.
 * 
 * Fluent methods should return <code>self()</code>
 * 
 * @author Bruce Skingle
 *
 * @param <T> The concrete type returned by fluent methods.
 */
public class Fluent<T extends IFluent<T>> implements IFluent<T>
{
  private final T self_;
  
  /**
   * Constructor.
   * 
   * @param type The concrete type returned by fluent methods.
   */
  public Fluent(Class<T> type)
  {
    if (!(type.isInstance(this)))
      throw new CodingFault("Class is declared to be " + type + " in type parameter T but it is not.");

    @SuppressWarnings("unchecked")
    T s = (T) this;

    self_ = s;
  }

  @Override
  public T self()
  {
    return self_;
  }
}
