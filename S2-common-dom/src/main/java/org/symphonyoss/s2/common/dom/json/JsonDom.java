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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.symphonyoss.s2.common.dom.DomWriter;

public class JsonDom extends JsonDomNode
{
  private List<JsonDomNode>  children_ = new LinkedList<>();
  
  public JsonDom add(JsonDomNode node)
  {
    children_.add(node);
    
    return this;
  }

  @Override
  public void writeTo(DomWriter writer, String terminator) throws IOException
  {
    if(children_.isEmpty())
    {
      writer.writeItem("{}", terminator);
    }
    else if(children_.size()==1)
    {
      JsonDomNode node = children_.get(0);
      
      if(node instanceof JsonObject)
      {
        node.writeTo(writer, terminator);
      }
      else
      {
        writer.openBlock("[");
        node.writeTo(writer, terminator);
        writer.closeBlock("]", terminator);
      }
    }
    else
    {
      writer.openBlock("[");
      
      Iterator<JsonDomNode> it = children_.iterator();
      while(it.hasNext())
      {
        it.next().writeTo(writer, it.hasNext() ? "," : null);
      }
      writer.closeBlock("]", terminator);
    }
  }

}
