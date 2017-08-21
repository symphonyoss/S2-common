/*
 * Copyright 2017 Symphony Communication Services, LLC.
 * All Rights Reserved
 */

package com.symphony.s2.common.exception;

public class BadFormatException extends S2Exception
{
  private static final long serialVersionUID = 1L;

  public BadFormatException()
  {
  }

  public BadFormatException(String message)
  {
    super(message);
  }

  public BadFormatException(Throwable cause)
  {
    super(cause);
  }

  public BadFormatException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public BadFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
