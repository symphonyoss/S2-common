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

import com.google.protobuf.ByteString;

/**
 * A JSON object which may be mutable or immutable.
 * 
 * @author Bruce Skingle
 *
 * @param <N> The type of nodes contained within this object, either mutable or not.
 */
public interface IJsonObject<N extends IJsonDomNode> extends IJsonDomNode
{
  @Override
  MutableJsonObject newMutableCopy();
  
  /**
   * Get the attribute whose name is given.
   * 
   * @param name The name of the required attribute.
   * 
   * @return The required attribute or <code>null</code>.
   */
  @Nullable N  get(String name);
  
  /**
   * 
   * @return an iterator over the sorted names of the attributes in this object.
   */
  Iterator<String> getSortedNameIterator();
  
  /**
   * 
   * @return an iterator over the names of the attributes in this object.
   */
  Iterator<String> getNameIterator();
  
  /**
   * 
   * @return the length of the longest attribute name in this object.
   */
  int getMaxNameLen();
  
  /**
   * Return true iff the given attribute exists.
   * 
   * @param name an attribute name.
   * 
   * @return true iff the given attribute exists.
   */
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
   * Return the Integer value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not a string value.
   */
  @Nonnull Integer  getRequiredInteger(@Nonnull String name);
  
  /**
   * Return the Integer value of the given field.
   * 
   * @param name The name of the field.
   * @param defaultValue The value to be returned if the field does not exist.
   * 
   * @return The value of the field, or the defaultValue if it does not exist.
   * 
   * @throws IllegalStateException if the field exists but is not a string value.
   */
  @Nullable Integer  getInteger(@Nonnull String name, @Nullable Integer defaultValue);
  
  /**
   * Return the Long value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not a string value.
   */
  @Nonnull Long  getRequiredLong(@Nonnull String name);
  
  /**
   * Return the Long value of the given field.
   * 
   * @param name The name of the field.
   * @param defaultValue The value to be returned if the field does not exist.
   * 
   * @return The value of the field, or the defaultValue if it does not exist.
   * 
   * @throws IllegalStateException if the field exists but is not a string value.
   */
  @Nullable Long  getLong(@Nonnull String name, @Nullable Long defaultValue);
  
  /**
   * Return the Float value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not a string value.
   */
  @Nonnull Float  getRequiredFloat(@Nonnull String name);
  
  /**
   * Return the Float value of the given field.
   * 
   * @param name The name of the field.
   * @param defaultValue The value to be returned if the field does not exist.
   * 
   * @return The value of the field, or the defaultValue if it does not exist.
   * 
   * @throws IllegalStateException if the field exists but is not a string value.
   */
  @Nullable Float  getFloat(@Nonnull String name, @Nullable Float defaultValue);
  
  /**
   * Return the Double value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not a string value.
   */
  @Nonnull Double  getRequiredDouble(@Nonnull String name);
  
  /**
   * Return the Double value of the given field.
   * 
   * @param name The name of the field.
   * @param defaultValue The value to be returned if the field does not exist.
   * 
   * @return The value of the field, or the defaultValue if it does not exist.
   * 
   * @throws IllegalStateException if the field exists but is not a string value.
   */
  @Nullable Double  getDouble(@Nonnull String name, @Nullable Double defaultValue);
  
  /**
   * Return the ByteString value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not a string value.
   */
  @Nonnull ByteString  getRequiredByteString(@Nonnull String name);
  
  /**
   * Return the ByteString value of the given field.
   * 
   * @param name The name of the field.
   * @param defaultValue The value to be returned if the field does not exist.
   * 
   * @return The value of the field, or the defaultValue if it does not exist.
   * 
   * @throws IllegalStateException if the field exists but is not a string value.
   */
  @Nullable ByteString  getByteString(@Nonnull String name, @Nullable ByteString defaultValue);
  
  /**
   * Return the Boolean value of the given field.
   * 
   * @param name The name of the required field.
   * 
   * @return The value of the field.
   * 
   * @throws IllegalStateException if the field does not exist or is not a string value.
   */
  @Nonnull Boolean  getRequiredBoolean(@Nonnull String name);
  
  /**
   * Return the Boolean value of the given field.
   * 
   * @param name The name of the field.
   * @param defaultValue The value to be returned if the field does not exist.
   * 
   * @return The value of the field, or the defaultValue if it does not exist.
   * 
   * @throws IllegalStateException if the field exists but is not a string value.
   */
  @Nullable Boolean  getBoolean(@Nonnull String name, @Nullable Boolean defaultValue);
  
  /**
   * Return the object field whose name is given.
   * 
   * @param name The name of the required field.
   * 
   * @return required object.
   * 
   * @throws IllegalStateException if the field does not exist or is not an object.
   */
  @Nonnull IJsonObject<?>  getRequiredObject(@Nonnull String name);
  
  /**
   * Return the object field whose name is given.
   * 
   * @param name The name of the required field.
   * 
   * @return the required object, or null.
   * 
   * @throws IllegalStateException if the field exists but is not an object.
   */
  @Nullable IJsonObject<?>  getObject(@Nonnull String name);
}
