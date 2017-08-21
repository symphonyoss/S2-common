/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.fault;

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
