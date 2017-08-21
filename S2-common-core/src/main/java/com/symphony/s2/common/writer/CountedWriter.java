/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 * 
 * Includes public domain material developed by Immutify Limited.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.writer;

import java.io.IOException;
import java.io.Writer;

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
