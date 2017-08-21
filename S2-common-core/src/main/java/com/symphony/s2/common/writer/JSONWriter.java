/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.writer;

import java.io.OutputStream;
import java.io.Writer;

public class JSONWriter extends IndentedWriter
{

  public JSONWriter(OutputStream out)
  {
    super(out);
  }

  public JSONWriter(Writer out)
  {
    super(out);
  }

  public void alignAttribute(String name, Object value)
  {
    align("\"" + escape(name) + "\":", "\"" + (value == null ? "" : escape(value)) + "\"");
  }
  
  public void openAttribute(String name)
  {
    println("\"" + escape(name) + "\": ");
    indent();
  }
  
  public void closeAttribute()
  {
    outdent();
  }
  
  private String escape(Object value)
  {
    if(value instanceof String)
    {
      String result =  ((String) value).replaceAll("\"", "\\\\\"");
      
      return result;
    }
    else
    {
      return value.toString();
    }
  }

  public void printAlignedAttributes()
  {
    printAlignedBlock(",", null);
  }

  public void openObject(String name)
  {
    println("\"" + name + "\": {");
    indent();
  }
  
  public void openObject()
  {
    println("{");
    indent();
  }

  public void closeObject()
  {
    printAlignedAttributes();
    outdent();
    println("}");
  }
  
  public void closeObject(String suffix)
  {
    printAlignedAttributes();
    outdent();
    println("}" + suffix);
  }
  
  public void openArray(String name)
  {
    println("\"" + name + "\": [");
    indent();
  }
  
  public void openArray()
  {
    println("[");
    indent();
  }

  public void closeArray()
  {
    printAlignedAttributes();
    outdent();
    println("]");
  }
  
  public void closeArray(String suffix)
  {
    printAlignedAttributes();
    outdent();
    println("]" + suffix);
  }

  public void printQuotedString(String s)
  {
    print("\"" + escape(s) + "\"");
  }
}
