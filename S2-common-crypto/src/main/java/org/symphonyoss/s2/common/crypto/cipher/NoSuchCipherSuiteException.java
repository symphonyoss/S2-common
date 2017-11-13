/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 * 
 * Includes public domain material developed by Immutify Limited.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The SSF licenses this file
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

package org.symphonyoss.s2.common.crypto.cipher;

public class NoSuchCipherSuiteException extends Exception
{
	private static final long	serialVersionUID	= 1L;

	public NoSuchCipherSuiteException()
	{
	}

	public NoSuchCipherSuiteException(String message)
	{
		super(message);
	}

	public NoSuchCipherSuiteException(Throwable cause)
	{
		super(cause);
	}

	public NoSuchCipherSuiteException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
