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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

/**
 * Any type of IDomNode which is <B>immutable</B>.
 * 
 * N.B. The mutable form of this is called IMutableDomNode
 * 
 * @author Bruce Skingle
 *
 */
@Immutable
public interface IImmutableDomNode extends IDomNode
{
  /**
   * Return the serialized form of this node.
   * 
   * The contents of the bytes are UTF-8 encoded characters.
   * 
   * @return the serialized form of this node.
   */
  @Nonnull ImmutableByteArray serialize();
}
