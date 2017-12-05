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

/**
 * 
 * @author Bruce Skingle
 *
 * @deprecated Use org.symphonyoss.s2.common.dom instead
 */
@Deprecated
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
    if(value instanceof Number)
      align("\"" + escape(name) + "\":", value);
    else
      align("\"" + escape(name) + "\":", (value == null ? "null" : "\"" + escape(value) + "\""));
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
