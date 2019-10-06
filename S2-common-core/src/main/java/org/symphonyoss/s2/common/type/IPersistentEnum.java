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

package org.symphonyoss.s2.common.type;

/**
 * An enum which can be persisted as an integer value.
 * 
 * All Java enum values have an integer representation which can be accessed
 * via the ordinal() method, but these values are set by the order of
 * declaration of the enum constants and may or may not be durable over
 * different versions of the code, and it is therefore dangerous to use the
 * ordinal value as a means of persisting data structures.
 * 
 * The intention of the IPersistentEnum interface is that it allows a class to
 * explicitly declare a durable integer value for each enum constant.
 * 
 * For an example implementation of this Interface see ExamplePersistentEnum.
 * 
 * @author Bruce Skingle
 *
 * param T The concrete type of the enum
 */
public interface IPersistentEnum //<T extends IPersistentEnum<T>>
{
  /*
   * There is no elegant way to express this constraint as a Java Interface but
   * implementors of this interface MUST declare a static method called newInstance
   * which takes an integer value and returns the enum constant which that value
   * represents, or throws an InvalidPersistentEnumException
   * 
   * static T newInstance(int value) throws InvalidPersistentEnumException;
   */

  /**
   * Return an integer value which represents this enum value and which can be
   * used to persist an instance of it. Implementors of this Interface MUS 
   * provide a method 
   * 
   * static T newInstance(int value) throws IllegalArgumentException;
   * 
   * which can be used to retrieve the enum constant from this value.
   * 
   * @return  An integer value which represents this enum value.
   */
  int toInt();

}
