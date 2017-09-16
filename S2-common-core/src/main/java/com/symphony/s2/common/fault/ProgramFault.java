/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
