package com.symphony.s2.common.exception;

public class StorageException extends S2Exception
{
  private static final long serialVersionUID = 1L;

  public StorageException()
  {}

  public StorageException(String message)
  {
    super(message);
  }

  public StorageException(Throwable cause)
  {
    super(cause);
  }

  public StorageException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public StorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
