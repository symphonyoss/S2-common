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

package org.symphonyoss.s2.common.dom.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.dom.DomWriter;

public class JsonObject extends JsonDomNode
{
  private static final String      OPEN_QUOTE   = "\"";
  private static final String      CLOSE_QUOTE  = "\":";
  private static final int         QUOTE_MARGIN = OPEN_QUOTE.length() + CLOSE_QUOTE.length() + 1;

  private Map<String, JsonDomNode> children_    = new HashMap<>();
  private LinkedList<String>       names_       = new LinkedList<>();
  private TreeSet<String>          sortedNames_ = new TreeSet<>();
  
  public JsonObject add(String name, JsonDomNode child)
  {
    if(sortedNames_.add(name))
    {
      names_.add(name);
    }
    children_.put(name, child);
    
    return this;
  }

  @Override
  public void writeTo(DomWriter writer, @Nullable String terminator) throws IOException
  {
    Iterator<String>  it;
    int               maxNameLen = 0;
    
    if(writer.isCanonicalMode())
    {
      it = sortedNames_.iterator();
    }
    else
    {
      for(String name : names_)
      {
        maxNameLen = Math.max(maxNameLen, name.length());
      }
      
      maxNameLen += QUOTE_MARGIN;
      
      while(maxNameLen % writer.getTabSize() != 0)
        maxNameLen++;
      
      it = names_.iterator();
    }
    
    writer.openBlock("{");
    
    while(it.hasNext())
    {
      String name = it.next();
      writer.writeColumn(OPEN_QUOTE, name, CLOSE_QUOTE, maxNameLen);
      children_.get(name).writeTo(writer, it.hasNext() ? "," : null);
    }
    writer.closeBlock("}", terminator);
  }

}
