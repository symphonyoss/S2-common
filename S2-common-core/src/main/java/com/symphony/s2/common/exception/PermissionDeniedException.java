package com.symphony.s2.common.exception;

public class PermissionDeniedException extends S2Exception
{
  private static final long serialVersionUID = 1L;

  public PermissionDeniedException()
  {}

  public PermissionDeniedException(String message)
  {
    super(message);
  }

  public PermissionDeniedException(Throwable cause)
  {
    super(cause);
  }

  public PermissionDeniedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public PermissionDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
