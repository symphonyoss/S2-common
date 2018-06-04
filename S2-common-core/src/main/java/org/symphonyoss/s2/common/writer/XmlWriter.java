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
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;


/**
 * A Writer based on IndentedWriter which adds functions to format XML.
 * 
 * @author Bruce Skingle
 */
public class XmlWriter extends IndentedWriter
{
	private		Stack<String>		elementStack_ = new Stack<String>();
	
	/**
	 * Constructor.
	 * @param outputStream An OutputStream to which the formatted output will be sent.
	 */
	public XmlWriter(Writer outputStream)
	{
		super(outputStream);
	}
	
	public XmlWriter(OutputStream out)
  {
    super(out);
  }

  /**
	 * Open an XML element with the given name. A call to closeElement() will output
	 * the appropriate XML closing tag. This class remembers the tag names.
	 * 
	 * @param name	Name of the XML element to open.
	 */
	public void		openElement(String name)
	{
		elementStack_.push(name);
		println("<" + name + ">");
		indent();
	}
	
	/**
	 * Open an XML element with the given name, and attributes. A call to closeElement() will output
	 * the appropriate XML closing tag. This class remembers the tag names.
	 * 
	 * @param name	Name of the XML element to open.
	 * @param attributes	A map of name value pairs which will be used to add attributes to
	 * the element.
	 */	
	public void		openElement(String name, Map<String, String> attributes)
	{
		elementStack_.push(name);
		print("<" + name);
		
		for(Entry<String, String> entry : attributes.entrySet())
		{
			print(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
		}
		println(">");
		indent();
	}
	
	protected void		startElement(String name, String ... attributes)
	{
		println("<" + name);
		indent();
		
		continueElement(attributes);
	}
	
	protected void startOpenElement(String name, String ... attributes)
	{
	  startElement(name, attributes);
	  elementStack_.push(name);
	}
	
	protected void   continueElement(String ... attributes)
  {
    int   i=0;
    
    while(i<attributes.length)
    {
      if(i < attributes.length - 1)
        println(attributes[i++] + "=\"" + attributes[i++] + "\"");
      else
        println(attributes[i++]);
    }
  }
	
	protected void finishElement(String ... attributes)
	{
	  continueElement(attributes);
    println(">");
	}
	
	protected void finishEmptyElement(String ... attributes)
  {
    continueElement(attributes);
    println("/>");
  }
	
	/**
	 * Open an XML element with the given name, and attributes. A call to closeElement() will output
	 * the appropriate XML closing tag. This class remembers the tag names.
	 * 
	 * The String parameters are taken to be alternatively names and values. Any odd value
	 * at the end of the list is added as a valueless attribute.
	 * @param name		Name of the element.
	 * @param attributes	Attributes in name value pairs.
	 */
	public void		openElement(String name, String ... attributes)
	{
		startOpenElement(name, attributes);
		println(">");
		
	}
	
	/**
	 * Output an element with the given content (value). The opening and closing tags are
	 * output in a single operation.
	 * 
	 * @param name		Name of the element.
	 * @param value		Contents of the element.
	 * @param attributes	Alternate names and values of attributes for the element.
	 */
	public void		printElement(String name, String value, String ... attributes)
	{
		startElement(name, attributes);
		if(value != null)
			println(">" + escape(value) + "</"+ name + ">");
		else
			println("/>");
		outdent();
	}
	
	/**
	 * Close an element previously created with openElement().
	 */
	public void		closeElement()
	{
		outdent();
		println("</" + elementStack_.pop() + ">");
	}

	/**
	 * Output a complete element with the given content.
	 * @param attributeName		Name of element.
	 * @param value				Content of element.
	 */
	public void printElement(String attributeName, Object value)
	{
		println("<" + attributeName + ">" + (value==null ? "" : escape(value.toString())) + "</"+ attributeName + ">");
	}
	
	/**
	 * Output a complete element with the given content and attributes.
	 * @param attributeName		Name of element.
	 * @param value				Content of element.
	 * @param attributes	A map of name value pairs which will be used to add attributes to
	 * the element.
	 */
	public void printElement(String attributeName, String value, Map<String, String> attributes)
	{
		print("<" + attributeName);
		
		for(Entry<String, String> entry : attributes.entrySet())
		{
			print(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
		}
		println(">" + (value==null ? "" : value) + "</"+ attributeName + ">");
	}
	
	public static String	escape(String in)
	{
		StringBuffer	out = new StringBuffer();

		for(char c : in.toCharArray())
		{
			switch(c)
			{
				case '<':		out.append("&lt;");			break;
				case '>':		out.append("&gt;");			break;
				case '&':		out.append("&amp;");		break;
				default:		out.append(c);
			}
		}
		
		return out.toString();
	}

	public void printElement(String name)
	{
		println("<"+ name + "/>");
	}
	
	public void printComment(String comment)
	{
		println("<!-- " + comment + " -->");
	}
}
