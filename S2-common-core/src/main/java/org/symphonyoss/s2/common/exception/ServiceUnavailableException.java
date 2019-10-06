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

package org.symphonyoss.s2.common.exception;

/**
 * An exception indicating that the service throwing the exception is unavailable.
 * This should only be thrown if the service has been unable to answer the 
 * request at all, if any data has been returned or any side effect of the
 * request will persist then this exception must not be thrown and a 
 * TransactionFault should be thrown instead.
 * 
 * On catching this exception the caller should attempt to use an alternative
 * service implementation if there is one.
 * 
 * @author Bruce Skingle
 *
 */
public class ServiceUnavailableException extends S2Exception
{
  private static final long serialVersionUID = 1L;

  public ServiceUnavailableException()
  {
  }

  public ServiceUnavailableException(String message)
  {
    super(message);
  }

  public ServiceUnavailableException(Throwable cause)
  {
    super(cause);
  }

  public ServiceUnavailableException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ServiceUnavailableException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
