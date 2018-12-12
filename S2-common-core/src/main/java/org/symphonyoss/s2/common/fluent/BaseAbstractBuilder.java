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

import org.symphonyoss.s2.common.fault.FaultAccumulator;

/**
 * A superclass for builders, the builder itself is fluent but the built type is not.
 * 
 * @param <T> The concrete type of this builder. 
 * @param <B> The concrete type of the built object.
 * 
 * @author Bruce Skingle
 *
 */
public abstract class BaseAbstractBuilder<T extends IBuilder<T,B>, B> extends Fluent<T> implements IBuilder<T,B>
{
  /**
   * Constructor.
   * 
   * @param type The concrete type of this builder.
   */
  public BaseAbstractBuilder(Class<T> type)
  {
    super(type);
  }
  
  protected abstract B construct();

  @Override
  final public B build()
  {
    try(FaultAccumulator faultAccumulator = new FaultAccumulator())
    {
      validate(faultAccumulator);
      
      B result = construct();
      
      return result;
    }
  }

  /**
   * Validate the settings of the builder, will be called from the build() method.
   * 
   * sub-classes which override this method should call super.valudate(faultAccumulator);
   * 
   * @param faultAccumulator An accumulator for faults. 
   */
  protected void validate(FaultAccumulator faultAccumulator)
  {
  }
}
