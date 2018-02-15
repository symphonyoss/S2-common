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

package org.symphonyoss.s2.common.dom;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

@Immutable
@ThreadSafe
public class DomConsumer<T extends DomConsumer<T>> implements IDomConsumerOrBuilder
{
  private final boolean       compactMode_;
  private final boolean       canonicalMode_;
  
  protected DomConsumer(boolean compactMode, boolean canonicalMode)
  {
    compactMode_ = compactMode; // Canonical implies compact
    canonicalMode_ = canonicalMode;
  }
  
  public static class Builder<B extends Builder<B>> implements IDomConsumerOrBuilder
  {
    private boolean       compactMode_;
    private boolean       canonicalMode_;

    protected Builder()
    {
    }
    
    protected Builder(IDomConsumerOrBuilder initial)
    {
      compactMode_ = initial.isCompactMode();
      canonicalMode_ = initial.isCanonicalMode();
    }

    @Override
    public boolean isCompactMode()
    {
      return compactMode_;
    }

    @Override
    public boolean isCanonicalMode()
    {
      return canonicalMode_;
    }

    @SuppressWarnings("unchecked")
    public B withCompactMode(boolean compactMode)
    {
      compactMode_ = compactMode;
      return (B)this;
    }

    @SuppressWarnings("unchecked")
    public B withCanonicalMode(boolean canonicalMode)
    {
      canonicalMode_ = canonicalMode;
      return (B)this;
    }
  }

  @Override
  public boolean isCompactMode()
  {
    return compactMode_;
  }
  
  @Override
  public boolean isCanonicalMode()
  {
    return canonicalMode_;
  }
}
