/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
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

package org.symphonyoss.s2.common.writer;

import java.io.IOException;
import java.io.Writer;

/**
 * 
 * @author Bruce Skingle
 */
public class CountedWriter extends Writer
{
	private	long			offset_ = 0L;
	private Writer	  out_;
	private int				counted_ = 0;
	
	public	CountedWriter(Writer out)
	{
		out_ = out;
	}
	
	@Override
	public void write(int b) throws IOException
	{
		if(counted_ == 0)
			offset_++;
		
		counted_++;
		out_.write(b);
		counted_--;
	}

	@Override
	public void write(char[] b, int off, int len) throws IOException
	{
		if(counted_ == 0)
			offset_ += len;
		
		counted_++;
		out_.write(b, off, len);
		counted_--;
	}

	@Override
	public void write(char[] b) throws IOException
	{
		if(counted_ == 0)
			offset_ += b.length;
		
		counted_++;
		out_.write(b);
		counted_--;
	}

	public long getOffset()
	{
		return offset_;
	}

	public void	beginUncounted()
	{
		counted_++;
	}
	
	public void	endUncounted()
	{
		counted_--;
	}

	@Override
	public void close() throws IOException
	{
		out_.close();
	}

	@Override
	public void flush() throws IOException
	{
		out_.flush();
	}
	
	
}
