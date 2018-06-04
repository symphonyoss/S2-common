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

import java.util.ArrayList;

/**
 * A block of tabulated text.
 * Rows can be added with the align() method, each string representing the text for an aligned column.
 * The print method outputs the block, optionally outputting a separator. 
 * @author Bruce Skingle
 */
public class AlignedBlock
{
	private static final int										TAB_SIZE	= 8;

	private		IndentedWriter									out_;
	private		ArrayList<Integer>									maxColumnLength_ = new ArrayList<Integer>();
	private		ArrayList<String[]>									rows_ = new ArrayList<String[]>();

	/**
	 * Constructor.
	 * 
	 * @param out	An IndentedWriter to which the block will be output.
	 */
	public AlignedBlock(IndentedWriter out)
	{
		out_ = out;
	}
	
	/**
	 * Add a row to the block, each Object represents a piece of text which should be aligned.
	 * @param args		Variable number of values to align.
	 */
	public	void	align(Object... args)
	{
		int		i=0;
		String	s[] = new String[args.length];
		
		for(int ii=0 ; ii<args.length ; ii++)
			if(args[ii] != null)
				s[ii] = args[ii].toString();
		
		while(i<s.length && i<maxColumnLength_.size())
		{
			int		l = (s[i]==null ? 0 : s[i].length());
			
			if(maxColumnLength_.get(i) < l)
			{
				maxColumnLength_.set(i, l);
			}
			
			i++;
		}
		
		while(i<s.length)
		{
			maxColumnLength_.add((s[i]==null ? 0 : s[i].length()));
			i++;
		}
		
		rows_.add(s);
	}
	
	/**
	 * Outputs the block, with the given separator appended to each line except the last, and the given
	 * terminator appended to the last line.
	 * @param separator		text added to each line except the last/
	 * @param terminator	text added to the last line.
	 */
	public	void	print(String separator, String terminator)
	{
		for(int i=0 ; i<maxColumnLength_.size() ; i++)
		{
			maxColumnLength_.set(i, (((maxColumnLength_.get(i) / TAB_SIZE) + 1) * TAB_SIZE));
		}
		
		for(int r=0 ; r<rows_.size() ; r++)
		{
			String[] s = rows_.get(r);
			
			for(int i=0 ; i<s.length ; i++)
			{
				out_.print(s[i]);
				
				if(i<s.length - 1)
				{
					for(int l= (s[i]==null ? 0 : s[i].length()) ; l<maxColumnLength_.get(i) ; l++)
					{
						out_.print(' ');
					}
				}
			}
			if(separator != null && r < rows_.size() - 1)
				out_.println(separator);
			else if(terminator != null && r == rows_.size() - 1)
				out_.println(terminator);
			else
				out_.println();
		}
	}
	
	/**
	 * Output the block without additional separators.
	 */
	public	void	print()
	{
		print(null, null);
	}
}

