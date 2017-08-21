/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.fault;

/**
 * A ProgramFault should be thrown when an error occurs which is so
 * severe that the entire program (JVM) should be terminated. An immediate
 * restart of the process should NOT generally be attempted. The subclass
 * TransientProgramFault can be thrown to indicate a failure mode where an
 * immediate restart may be successful.
 * 
 * Note that this exception does not, of itself, cause termination of the
 * JVM but the fault barrier catching it should cause that to happen.
 * This may be through an abrupt or graceful termination of the application.
 * 
 * This exception should NOT be used to wrap subclasses of java.lang.Error,
 * such errors should be allowed to unwind the stack and terminate the 
 * JVM normally.
 * 
 * @author bruce.skingle
 *
 */
public class ProgramFault extends AbstractFault
{
  private static final long serialVersionUID = 1L;
  
  public ProgramFault()
  {
  }

  public ProgramFault(String message)
  {
    super(message);
  }

  public ProgramFault(Throwable cause)
  {
    super(cause);
  }

  public ProgramFault(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ProgramFault(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
