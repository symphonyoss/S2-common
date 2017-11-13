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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * A PrintWriter with added indenting functionality.
 * 
 * @author Bruce Skingle
 *
 */
public class IndentedWriter extends PrintWriter
{
  public static final int newlineLength_    = System.getProperty("line.separator").length();

  private boolean         startOfLine_      = true;
  private int             indent_           = 0;
  private AlignedBlock    alignedBlock_     = null;
  private boolean         printOffsets_     = false;
  private boolean         oNlCr_            = false;
  private String          linePrefix_;
  private int             linePrefixIndent_ = 9999;
	
	
	public IndentedWriter(Writer out)
	{
		super(out instanceof CountedWriter ? out : new CountedWriter(out));
	}
	
	public IndentedWriter(OutputStream out)
  {
    super(new CountedWriter(new OutputStreamWriter(out)));
  }

	public void setPrintOffsets(boolean printOffsets)
	{
		printOffsets_ = printOffsets;
	}

	public void setLinePrefix(String linePrefix)
	{
		linePrefix_ = linePrefix;
		linePrefixIndent_ = linePrefix == null ? 9999 : indent_;
	}

	/**
   * Add a row to an aligned block, each Object represents a piece of text which should be aligned.
   * @param args    Variable number of values to align.
   */
	public void	align(Object... args)
	{
		if(alignedBlock_ == null)
		{
			alignedBlock_ = new AlignedBlock(this);
		}
		alignedBlock_.align(args);
	}
	
	/**
	 * Prints the aligned block, with all strings aligned in columns dependent on the order in which they were declared in Align
	 * @param separator text added to the end of each line except the last line
	 * @param terminator text added to the end of the last line of the block
	 */
	public	void	printAlignedBlock(String separator, String terminator)
	{
		if(alignedBlock_ != null)
		{
			alignedBlock_.print(separator, terminator);
			alignedBlock_ = null;
		}
	}
	/**
	 * Prints the alignedblock without any separators or terminators 
	 */
	public	void	printAlignedBlock()
	{
		printAlignedBlock(null, null);
	}
	
	/**
	 * prints an open curly bracket and indents the following line
	 */	
	public void		openBlock()
	{
		println("{");
		indent();
	}
	
	/**
	 * outdents the line and prints a close curly bracket on the following line
	 */		
	public	void	closeBlock()
	{
		outdent();
		println("}");
	}
	
	/**
	 * Prints a string, an open curly bracket on its own line and indents the following line
	 * @param s preceding string
	 */
	public void		openBlock(String s)
	{
		println(s);
		println("{");
		indent();
	}
	
	/**
	 * outdents the current line and prints a close curly bracket on the next line, followed by a string
	 * @param s succeeding string
	 */
	public	void	closeBlock(String s)
	{
		outdent();
		println("}" + s);
	}
	
	/**
	 * Ends one block (outdent followed by a close curly bracket on the following line) prints a string
	 * on its own line and begins a new block (open curly bracket followed by an indent on the following 
	 * line
	 * @param s intermediate string
	 */
	public void		continueBlock(String s)
	{
		outdent();
		println("}");
		println(s);
		println("{");
		indent();
	}
	
	/**
	 * Increases the indent on the current line
	 */
	public void		indent()
	{
		indent_++;
	}
	
	/**
	 * Decreases the indent on the current line
	 */
	public void		outdent()
	{
		indent_--;
	}
	
	
	private	void	doIndent(int tempIndent)
	{
		if(printOffsets_)
		{
			((CountedWriter)super.out).beginUncounted();
			super.print(String.format("%10d ", getOffset()));
			((CountedWriter)super.out).endUncounted();
		}
		
		int		i;
		
		tempIndent += indent_;
		
		for(i=0 ; i<tempIndent && i<linePrefixIndent_; i++)
		{
			super.print('\t');
		}
		
		if(linePrefix_ != null)
			super.print(linePrefix_);

		while(i++<tempIndent)
		{
			super.print('\t');
		}
		startOfLine_ = false;
	}

	
	/**
	 * Indent the current line and print a boolean on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param b boolean to be printed
	 */
	public void print(int tempIndent, boolean b)
	{
		print(tempIndent, "" + b);
	}

	/**
	 * Indent the current line and print a character on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param c character to be printed
	 */
	public void print(int tempIndent, char c)
	{
		if(startOfLine_)
		{
			doIndent(tempIndent);
		}
		super.print(c);
	}

	/**
	 * Indent the current line and print a character array on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param s character array to be printed
	 */
	public void print(int tempIndent, char[] s)
	{
		if(startOfLine_)
		{
			doIndent(tempIndent);
		}
		super.print(s);
	}

	/**
	 * Indent the current line and print a double on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param d double to be printed
	 */
	public void print(int tempIndent, double d)
	{
		print(tempIndent, "" + d);
	}

	/**
	 * Indent the current line and print a floating point number on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param f floating point number to be printed
	 */
	public void print(int tempIndent, float f)
	{
		print(tempIndent, "" + f);
	}

	/**
	 * Indent the current line and print an integer on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param i integer to be printed
	 */
	public void print(int tempIndent, int i)
	{
		print(tempIndent, "" + i);
	}

	/**
	 * Indent the current line and print an object on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param obj object to be printed
	 */
	public void print(int tempIndent, Object obj)
	{
		print(tempIndent, "" + obj);
	}

	/**
	 * Indent the current line and print a string on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param s string to be printed
	 */
	public void print(int tempIndent, String s)
	{
		if(startOfLine_)
		{
			doIndent(tempIndent);
		}
		super.print(s);
	}

	/**
	 * Indent the current line and print a long integer on the same line
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param l long integer to be printed
	 */
	public void print(int tempIndent, long l)
	{
		print(tempIndent, "" + l);
	}

	/**
	 * Prints a linebreak 
	 */
	@Override
  public void println()
	{
		super.println();
		startOfLine_ = true;
	}
	
	/**
	 * Print a linebreak unless we are at the start of a line.
	 */
	public void forceNewLine()
	{
		if(!startOfLine_)
			println();
	}

	/**
	 * Indent the current line and print a boolean on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x boolean to be printed
	 */
	public void println(int tempIndent, boolean x)
	{
		println(tempIndent, "" + x);
	}

	/**
	 * Indent the current line and print a character on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x character to be printed
	 */
	public void println(int tempIndent, char x)
	{
		if(startOfLine_)
		{
			doIndent(tempIndent);
		}
		super.println(x);
		startOfLine_ = true;
	}

	/**
	 * Indent the current line and print a character array on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x character array to be printed
	 */
	public void println(int tempIndent, char[] x)
	{
		if(startOfLine_)
		{
			doIndent(tempIndent);
		}
		super.println(x);
		startOfLine_ = true;
	}

	/**
	 * Indent the current line and print a double on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x double to be printed
	 */
	public void println(int tempIndent, double x)
	{
		println(tempIndent, "" + x);
	}

	/**
	 * Indent the current line and print a floating point number
	 * on the same line, then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x floating point number to be printed
	 */
	public void println(int tempIndent, float x)
	{
		println(tempIndent, "" + x);
	}

	/**
	 * Indent the current line and print an integer on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x integer to be printed
	 */
	public void println(int tempIndent, int x)
	{
		println(tempIndent, "" + x);
	}

	/**
	 * Indent the current line and print an object on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x object to be printed
	 */
	public void println(int tempIndent, Object x)
	{
		println(tempIndent, "" + x);
	}

	/**
	 * Indent the current line and print a string on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x string to be printed
	 */
	public void println(int tempIndent, String x)
	{
		if(startOfLine_)
		{
			doIndent(tempIndent);
		}
		super.println(x);
		if(oNlCr_)
			super.write('\r');
		startOfLine_ = true;
	}

	/**
	 * Indent the current line and print a long integer on the same line, 
	 * then print a line break
	 * @param tempIndent magnitude of the indent (number of tab spaces)
	 * @param x long integer to be printed
	 */
	public void println(int tempIndent, long x)
	{
		println(tempIndent, "" + x);
	}
	
	
	/**
	 * print a boolean with no indent
	 * @param b boolean to print
	 */
	@Override
  public void print(boolean b)
	{
		print(0, b);
	}

	/**
	 * print a character with no indent
	 * @param c character to print
	 */
	@Override
  public void print(char c)
	{
		print(0, c);
	}

	/**
	 * print a character array with no indent
	 * @param s character array to print
	 */
	@Override
  public void print(char[] s)
	{
		print(0, s);
	}

	/**
	 * print a double with no indent
	 * @param d double to print
	 */
	@Override
  public void print(double d)
	{
		print(0, d);
	}

	/**
	 * print a floating point number with no indent
	 * @param f floating point number to print
	 */
	@Override
  public void print(float f)
	{
		print(0, f);
	}

	/**
	 * print an integer with no indent
	 * @param i integer to print
	 */
	@Override
  public void print(int i)
	{
		print(0, i);
	}

	/**
	 * print an object with no indent
	 * @param obj object to print
	 */
	@Override
  public void print(Object obj)
	{
		print(0, obj);
	}

	/**
	 * print a string with no indent
	 * @param s string to print
	 */
	@Override
  public void print(String s)
	{
		print(0, s);
	}

	/**
	 * print a long integer with no indent
	 * @param l long integer to print
	 */
	@Override
  public void print(long l)
	{
		print(0, l);
	}



	/**
	 * print a boolean with no indent with a line break
	 * @param x boolean to print
	 */
	@Override
  public void println(boolean x)
	{
		println(0, x);
	}

	/**
	 * print a character with no indent with a line break
	 * @param x character to print
	 */
	@Override
  public void println(char x)
	{
		println(0, x);
	}

	/**
	 * print a character array with no indent with a line break
	 * @param x character array to print
	 */
	@Override
  public void println(char[] x)
	{
		println(0, x);
	}

	/**
	 * print a double with no indent with a line break
	 * @param x double to print
	 */
	@Override
  public void println(double x)
	{
		println(0, x);
	}

	/**
	 * print a floating point number with no indent with a line break
	 * @param x floating point number to print
	 */
	@Override
  public void println(float x)
	{
		println(0, x);
	}

	/**
	 * print an integer with no indent with a line break
	 * @param x integer to print
	 */
	@Override
  public void println(int x)
	{
		println(0, x);
	}

	/**
	 * print an object with no indent with a line break
	 * @param x object to print
	 */
	@Override
  public void println(Object x)
	{
		println(0, x);
	}

	/**
	 * print a string with no indent with a line break
	 * @param x string to print
	 */
	@Override
  public void println(String x)
	{
		println(0, x);
	}

	/**
	 * Print multiple strings, each with a line break.
	 * 
	 * @param strings multiple strings to print.
	 */
	public void printlines(String ...strings)
	{
		for(String s : strings)
			println(s);
	}

	/**
	 * print a long integer with no indent with a line break
	 * @param x long integer to print
	 */
	@Override
  public void println(long x)
	{
		println(0, x);
	}

	/**
	 * Prints all strings in a collection on individual lines
	 * @param str collection of strings
	 */
	public void	println(Collection<String> str)
	{
		for(String s : str)
		{
			println(0, s);
		}
	}

	/**
	 * Convenience method for calling string.format and printing
	 * @param pattern A format string as per String.format(), a null value is equivalent to ""
	 * @param arguments Additional arguments to be formatted.
	 */
	public void print(String pattern, Object... arguments)
	{
	  if(pattern != null)
	    print(String.format(pattern, arguments));
	}
	
	/**
	 * Convenience method for calling string.format and printing 
	 * with line breaks
	 * @param pattern A format string as per String.format(), a null value is equivalent to ""
   * @param arguments Additional arguments to be formatted.
   */
	public void println(String pattern, Object... arguments)
	{
	  if(pattern == null)
	    println();
	  else
	    println(String.format(pattern, arguments));
	}
	
	/**
	 * Calls string.format, indents and prints
	 * 
	 * @param indent   The number of indent stops to apply at the start of a line.
	 * @param pattern A format string as per String.format(), a null value is equivalent to ""
   * @param arguments Additional arguments to be formatted.
   */
	public void print(int indent, String pattern, Object... arguments)
	{
		print(indent, pattern == null ? "" : String.format(pattern, arguments));
	}
	
	/**
	 * Calls string.format, indents and prints with line break
	 * @param indent   The number of indent stops to apply at the start of a line.
   * @param pattern A format string as per String.format(), a null value is equivalent to ""
   * @param arguments Additional arguments to be formatted.
   */
	public void println(int indent, String pattern, Object... arguments)
	{
		println(indent, String.format(pattern, arguments));
	}
	
	

	public long getOffset()
	{
		return ((CountedWriter)super.out).getOffset();
	}

	public void setONlCr(boolean b)
	{
		oNlCr_ = b;
	}
	
	
}
