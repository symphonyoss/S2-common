/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.fault;

import java.util.Collection;

/**
 * A type of TransactionFault resulting from an operation which, if
 * retried may be successful.
 * 
 * @author bruce.skingle
 *
 */
public class TransientTransactionFault extends TransactionFault
{
  private static final long serialVersionUID = 1L;

  public TransientTransactionFault()
  {
  }

  public TransientTransactionFault(String message)
  {
    super(message);
  }

  public TransientTransactionFault(String message, Collection<Exception> parallelCauses)
  {
    super(message, parallelCauses);
  }

  public TransientTransactionFault(Throwable cause)
  {
    super(cause);
  }

  public TransientTransactionFault(String message, Throwable cause)
  {
    super(message, cause);
  }

  public TransientTransactionFault(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
