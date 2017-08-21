/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.exception;

public class ConnectionException extends S2Exception
{
  private static final long serialVersionUID = 1L;

  public ConnectionException()
  {
  }

  public ConnectionException(String message)
  {
    super(message);
  }

  public ConnectionException(Throwable cause)
  {
    super(cause);
  }

  public ConnectionException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
