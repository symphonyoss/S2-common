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

package org.symphonyoss.s2.common.fault;

import java.io.Closeable;
import java.util.Collection;

/**
 * A fault accumulator.
 * 
 * The error method can be called multiple times to register one or more faults, when the accumulator
 * is closed, if the error method has been called then an IllegalStateException is thrown containing
 * the accumulated error messages.
 * 
 * @author Bruce Skingle
 *
 */
public class FaultAccumulator implements Closeable
{
  private StringBuilder builder = null;
  
  /**
   * Record the given error.
   * 
   * @param message An error message.
   * 
   * @return this (fluent method)
   */
  public FaultAccumulator error(String message)
  {
    if(builder == null)
      builder = new StringBuilder();
    else
      builder.append(", ");
    
    builder.append(message);
    
    return this;
  }
  
  /**
   * Record an error if more than the given number of values is non-null.
   * 
   * @param names   The names of the values to be used in an error if reported.
   * @param min     The minimum number of values which may be defined (non-null)
   * @param max     The maximum number of values which may be defined (non-null)
   * @param values  A list of values. 
   * 
   * @return this (fluent method)
   */
  public FaultAccumulator checkValueCount(String names, int min, int max, Object ...values)
  {
    int cnt=0;
    
    for(Object value : values)
      if(value != null)
        cnt++;
    
    if(cnt < min)
      error("at least " + min + " of " + names + " must be given.");
    
    if(cnt > max)
      error("at most " + max + " of " + names + " may be given.");
    
    return this;
  }
  
  /**
   * Record an error if the given object is null.
   * 
   * @param value A value which must not be null.
   * @param name  The name of the value to be used in an error if reported.
   * 
   * @return this (fluent method)
   */
  public FaultAccumulator checkNotNull(Object value, String name)
  {
    if(value == null)
      error(name + " is required.");
    
    return this;
  }
  
  /**
   * Record an error if the given collection is null or empty.
   * 
   * @param value A value which must not be null.
   * @param name  The name of the value to be used in an error if reported.
   * 
   * @return this (fluent method)
   */
  public FaultAccumulator checkNotNull(Collection<?> value, String name)
  {
    if(value == null || value.isEmpty())
      error("At least one " + name + " is required.");
    
    return this;
  }

  @Override
  public void close()
  {
    if(builder != null)
      throw new IllegalStateException(builder.toString());
  }
}
