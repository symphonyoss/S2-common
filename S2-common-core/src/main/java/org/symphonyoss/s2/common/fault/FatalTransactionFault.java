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

package org.symphonyoss.s2.common.fault;

import java.util.Collection;

/**
 * A type of TransactionFault resulting from an operation which
 * should not be retried.
 * 
 * @author bruce.skingle
 *
 */
public class FatalTransactionFault extends TransactionFault
{
  private static final long serialVersionUID = 1L;

  public FatalTransactionFault()
  {
  }

  public FatalTransactionFault(String message)
  {
    super(message);
  }

  public FatalTransactionFault(String message, Collection<Exception> parallelCauses)
  {
    super(message, parallelCauses);
  }

  public FatalTransactionFault(Throwable cause)
  {
    super(cause);
  }

  public FatalTransactionFault(String message, Throwable cause)
  {
    super(message, cause);
  }

  public FatalTransactionFault(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
