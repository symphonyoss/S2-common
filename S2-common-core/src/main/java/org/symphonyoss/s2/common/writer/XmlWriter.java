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
 * A Writer based on IndentedWriter which adds functions to format XML.
 * 
 * @author Bruce Skingle
 *
 */
public class XmlWriter extends AbstractXmlWriter<XmlWriter>
{
  public XmlWriter(Writer out, boolean closeFlag, int tabSize, String indentString)
  {
    super(out, closeFlag, tabSize, indentString);
  }
  
  public static class Builder extends AbstractXmlWriter.Builder<XmlWriter>
  {
    public Builder(Writer writer)
    {
      super(writer);
    }

    public Builder(OutputStream out)
    {
      super(out);
    }

    @Override
    public XmlWriter build()
    {
      return new XmlWriter(getWriter(), isCloseFlag(), getTabSize(), getIndentString());
    }
  }
  
  public static Builder newBuilder(Writer writer)
  {
    return new Builder(writer);
  }
  
  public static Builder newBuilder(OutputStream out)
  {
    return new Builder(out);
  }
}
