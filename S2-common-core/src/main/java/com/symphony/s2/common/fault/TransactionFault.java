/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.fault;

import java.util.Collection;

/**
 * A TransactionFault should be thrown when an error occurs which makes
 * it impossible to complete the current business transaction.
 * 
 * Where the code is being executed within the context of a thread pool,
 * the thread can be expected not to be terminated by this RuntimeException,
 * but the business transaction will fail and should not be retried.
 * 
 * @author bruce.skingle
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
   * @param message
   * @param parallelCauses
   * @return
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
  
  public TransactionFault()
  {
  }

  public TransactionFault(String message, Collection<Exception> parallelCauses)
  {
    super(message);
    
    parallelCauses_ = parallelCauses;
  }
  
  public TransactionFault(String message)
  {
    super(message);
  }

  public TransactionFault(Throwable cause)
  {
    super(cause);
  }

  public TransactionFault(String message, Throwable cause)
  {
    super(message, cause);
  }

  public TransactionFault(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public Collection<Exception> getParallelCauses()
  {
    return parallelCauses_;
  }
}
