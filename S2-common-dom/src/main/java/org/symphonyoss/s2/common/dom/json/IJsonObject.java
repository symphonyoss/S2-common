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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IJsonObject<N extends IJsonDomNode> extends IJsonDomNode
{
  @Nullable N  get(String name);
  Iterator<String> getSortedNameIterator();
  Iterator<String> getNameIterator();
  int getMaxNameLen();
  boolean containsKey(String name);
  
  /**
   * Return the string value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not a string value.
   */
  @Nonnull String  getRequiredString(@Nonnull String name);
  
  /**
   * Return the string value of the given field.
   * 
   * @param name The name of the field.
   * @param defaultValue The value to be returned if the field does not exist.
   * 
   * @return The value of the field, or the defaultValue if it does not exist.
   * 
   * @throws IllegalStateException if the field exists but is not a string value.
   */
  @Nullable String  getString(@Nonnull String name, @Nullable String defaultValue);
  
  /**
   * Return the object field whose name is given.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not an object.
   */
  @Nonnull IJsonObject<?>  getRequiredObject(@Nonnull String name);
}
