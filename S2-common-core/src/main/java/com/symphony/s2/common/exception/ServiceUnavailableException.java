/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.exception;

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
 * @author bruce.skingle
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
