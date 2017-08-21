package com.symphony.s2.common.exception;

public class S2Exception extends Exception
{
  private static final long serialVersionUID = 1L;

  public S2Exception()
  {
  }

  public S2Exception(String message)
  {
    super(message);
  }

  public S2Exception(Throwable cause)
  {
    super(cause);
  }

  public S2Exception(String message, Throwable cause)
  {
    super(message, cause);
  }

  public S2Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
