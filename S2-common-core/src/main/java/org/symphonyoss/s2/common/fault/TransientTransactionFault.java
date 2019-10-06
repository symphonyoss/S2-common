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
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * A type of TransactionFault resulting from an operation which, if
 * retried may be successful.
 * 
 * This exception allows the thrower to indicate how long it thinks the caller should
 * wait before retrying.
 * 
 * @author Bruce Skingle
 *
 */
public class TransientTransactionFault extends TransactionFault
{
  private static final long serialVersionUID = 1L;

  private final TimeUnit  retryTimeUnit_;
  private final Long      retryTime_;

  /**
   * Default constructor.
   */
  public TransientTransactionFault()
  {
    retryTimeUnit_ = null;
    retryTime_ = null;
  }

  /**
   * Constructor with message.
   * 
   * @param message A message describing the detail of the fault.
   */
  public TransientTransactionFault(String message)
  {
    super(message);
    retryTimeUnit_ = null;
    retryTime_ = null;
  }

  /**
   * Constructor with message.
   * 
   * @param message       A message describing the detail of the fault.
   * @param retryTimeUnit Units of suggested delay before a retry should be attempted.
   * @param retryTime     The suggested delay before a retry should be attempted.
   */
  public TransientTransactionFault(String message, TimeUnit retryTimeUnit, Long retryTime)
  {
    super(message);
    
    retryTimeUnit_ = retryTimeUnit;
    retryTime_ = retryTime;
  }

  /**
   * Constructor with message and multiple causes.
   * 
   * @param message A message describing the detail of the fault.
   * @param parallelCauses A collection of causes which occured in parallel.
   * 
   * @see TransactionFault#create
   */
  public TransientTransactionFault(String message, Collection<Exception> parallelCauses)
  {
    super(message, parallelCauses);
    
    TimeUnit retryTimeUnit = null;
    Long retryTime = null;
    
    for(Exception cause : parallelCauses)
    {
      if(cause instanceof TransientTransactionFault)
      {
        TransientTransactionFault c = (TransientTransactionFault) cause;
        
        if(retryTime == null || retryTimeUnit==null || 
            c.getRetryTimeUnit().toMicros(c.getRetryTime()) > retryTimeUnit.toMicros(retryTime))
        {
          retryTime = c.getRetryTime();
          retryTimeUnit = c.getRetryTimeUnit();
        }
      }
    }

    retryTimeUnit_ = retryTimeUnit;
    retryTime_ = retryTime;
  }

  /**
   * Constructor with cause.
   * 
   * @param cause The underlying cause of the fault.
   */
  public TransientTransactionFault(Throwable cause)
  {
    super(cause);
    retryTimeUnit_ = null;
    retryTime_ = null;
  }

  /**
   * Constructor with message and cause.
   * 
   * @param message A message describing the detail of the fault.
   * @param cause The underlying cause of the fault.
   */
  public TransientTransactionFault(String message, Throwable cause)
  {
    super(message, cause);
    retryTimeUnit_ = null;
    retryTime_ = null;
  }

  /**
   * Constructor with cause.
   * 
   * @param cause         The underlying cause of the fault.
   * @param retryTimeUnit Units of suggested delay before a retry should be attempted.
   * @param retryTime     The suggested delay before a retry should be attempted.
   */
  public TransientTransactionFault(Throwable cause, TimeUnit retryTimeUnit, Long retryTime)
  {
    super(cause);
    
    retryTimeUnit_ = retryTimeUnit;
    retryTime_ = retryTime;
  }

  /**
   * Constructor with message and cause.
   * 
   * @param message       A message describing the detail of the fault.
   * @param cause         The underlying cause of the fault.
   * @param retryTimeUnit Units of suggested delay before a retry should be attempted.
   * @param retryTime     The suggested delay before a retry should be attempted.
   */
  public TransientTransactionFault(String message, Throwable cause, TimeUnit retryTimeUnit, Long retryTime)
  {
    super(message, cause);
    
    retryTimeUnit_ = retryTimeUnit;
    retryTime_ = retryTime;
  }
  
  /**
   * Constructor with message, cause, suppression enabled or disabled, and writable
   * stack trace enabled or disabled.
   *
   * @param message A message describing the detail of the fault.
   * @param cause The underlying cause of the fault.
   * @param enableSuppression whether or not suppression is enabled
   *                          or disabled
   * @param writableStackTrace whether or not the stack trace should
   *                           be writable
   */
  public TransientTransactionFault(String message, @Nullable Throwable cause, boolean enableSuppression,
      boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
    retryTimeUnit_ = null;
    retryTime_ = null;
  }

  /**
   * @return The units of suggested delay before a retry should be attempted.
   */
  public @Nullable TimeUnit getRetryTimeUnit()
  {
    return retryTimeUnit_;
  }

  /**
   * @return The suggested delay before a retry should be attempted.
   */
  public @Nullable Long getRetryTime()
  {
    return retryTime_;
  }
}
