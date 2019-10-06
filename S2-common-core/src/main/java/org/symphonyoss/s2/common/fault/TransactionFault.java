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

import javax.annotation.Nullable;

/**
 * A TransactionFault should be thrown when an error occurs which makes
 * it impossible to complete the current business transaction.
 * 
 * Where the code is being executed within the context of a thread pool,
 * the thread can be expected not to be terminated by this RuntimeException,
 * but the business transaction will fail and should not be retried.
 * 
 * This would include cases where an HTTP 500 Server Error status has been returned.
 * 
 * @author Bruce Skingle
 *
 */
public class TransactionFault extends AbstractFault
{
  private static final long serialVersionUID = 1L;
  
  private Collection<Exception> parallelCauses_;
  
  /**
   * Create a parallel TransactionFault for the given parallel causes.
   * 
   * If any of the causes are FatalTransactionFaults then the result is
   * a FatalTransactionFault, otherwise if all of the parallel causes are
   * TransientTransactionExceptions then the result is also a 
   * TransientTransactionException, otherwise it is a TransactionException.
   * 
   * @param message A message for the combined fault.
   * @param parallelCauses  A Collection of parallel causes.
   * @return A TransactionFault reflecting a number of parallel causes.
   */
  public static TransactionFault  create(String message, Collection<Exception> parallelCauses)
  {
    boolean trans = true;
    boolean fatal = false;
    
    for(Exception cause : parallelCauses)
    {
      if(cause instanceof FatalTransactionFault)
      {
        fatal = true;
        trans = false;
      }
      else if(! (cause instanceof TransientTransactionFault))
      {
        trans = false;
      }
    }
    
    if(fatal)
      return new FatalTransactionFault(message, parallelCauses);
    
    if(trans)
      return new TransientTransactionFault(message, parallelCauses);
    
    return new TransactionFault(message, parallelCauses);
  }
  
  /**
   * Default constructor.
   */
  public TransactionFault()
  {
  }

  /**
   * Constructor with message and multiple causes.
   * 
   * @param message A message describing the detail of the fault.
   * @param parallelCauses A collection of causes which occured in parallel.
   * 
   * @see TransactionFault#create
   */
  public TransactionFault(String message, Collection<Exception> parallelCauses)
  {
    super(message);
    
    parallelCauses_ = parallelCauses;
  }
  
  /**
   * Constructor with message.
   * 
   * @param message A message describing the detail of the fault.
   */
  public TransactionFault(String message)
  {
    super(message);
  }

  /**
   * Constructor with cause.
   * 
   * @param cause The underlying cause of the fault.
   */
  public TransactionFault(Throwable cause)
  {
    super(cause);
  }

  /**
   * Constructor with message and cause.
   * 
   * @param message A message describing the detail of the fault.
   * @param cause The underlying cause of the fault.
   */
  public TransactionFault(String message, Throwable cause)
  {
    super(message, cause);
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
  public TransactionFault(String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * 
   * @return The parallel causes of this fault.
   */
  public Collection<Exception> getParallelCauses()
  {
    return parallelCauses_;
  }
}
