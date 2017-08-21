package com.symphony.s2.common.exception;

public class NoSuchObjectException extends StorageException
{
  private static final long serialVersionUID = 1L;

  public NoSuchObjectException()
  {}

  public NoSuchObjectException(String message)
  {
    super(message);
  }

  public NoSuchObjectException(Throwable cause)
  {
    super(cause);
  }

  public NoSuchObjectException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public NoSuchObjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
