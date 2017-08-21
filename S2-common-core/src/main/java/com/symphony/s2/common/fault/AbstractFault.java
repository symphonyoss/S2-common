/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.fault;

/**
 * Root class of the Fault hierarchy.
 * 
 * @author bruce.skingle
 *
 */
public abstract class AbstractFault extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public AbstractFault()
  {
  }

  public AbstractFault(String message)
  {
    super(message);
  }

  public AbstractFault(Throwable cause)
  {
    super(cause);
  }

  public AbstractFault(String message, Throwable cause)
  {
    super(message, cause);
  }

  public AbstractFault(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
