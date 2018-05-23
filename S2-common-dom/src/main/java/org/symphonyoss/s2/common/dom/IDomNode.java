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

import java.io.IOException;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.dom.json.IJsonDomNode;

/**
 * A node in an abstract DOM tree, which may or may not be mutable.
 * 
 * @author Bruce Skingle
 *
 */
public interface IDomNode
{
  /**
   * Return an immutable version of this node.
   * 
   * @return an immutable version of this node.
   */
  IImmutableDomNode immutify();
  
  /**
   * Return a mutable version of this node.
   * 
   * @return a mutable version of this node.
   */
  IJsonDomNode newMutableCopy();
  
  /**
   * Write the serialized form of this node to the given writer.
   * 
   * @param writer      The destination for the serialized form of this node.
   * @param terminator  A terminator to be added, or <code>null</code>
   * 
   * @return  this (Fluent API)
   * @throws IOException   If there is a problem writing the output.
   */
  IDomNode writeTo(DomWriter writer, @Nullable String terminator) throws IOException;
}
