/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.exception;

public class TypeMismatchException extends S2Exception
{
  private static final long serialVersionUID = 1L;

  public TypeMismatchException()
  {
  }

  public TypeMismatchException(String message)
  {
    super(message);
  }

  public TypeMismatchException(Throwable cause)
  {
    super(cause);
  }

  public TypeMismatchException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public TypeMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
