/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.fault;

/**
 * A type of ProgramFault resulting from faulty code.
 * It is appropriate to throw this fault from a supposedly
 * unreachable catch block or from the default case of
 * a switch statement with cases for all values of an 
 * enumeration for example.
 * 
 * @author bruce.skingle
 *
 */
public class CodingFault extends ProgramFault
{
  private static final long serialVersionUID = 1L;

  public CodingFault()
  {
  }
  
  public CodingFault(String message)
  {
    super(message);
  }

  public CodingFault(Throwable cause)
  {
    super(cause);
  }

  public CodingFault(String message, Throwable cause)
  {
    super(message, cause);
  }
}
