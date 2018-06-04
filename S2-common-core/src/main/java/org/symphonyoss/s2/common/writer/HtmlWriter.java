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
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Bruce Skingle
 *
 */
public class HtmlWriter extends XmlWriter
{
  private Map<Class<?>, ITableCellFormatter<?>> tableCellFormatterMap_ = new HashMap<>();
  
  public HtmlWriter(OutputStream out)
  {
    super(out);
  }

  public HtmlWriter(Writer outputStream)
  {
    super(outputStream);
  }
  
  public <T> void addTableCellFormatter(Class<T> type, ITableCellFormatter<T> formatter)
  {
    tableCellFormatterMap_.put(type, formatter);
  }

  public void printTableRow(Object ...cells)
  {
    openElement("tr");
    for(Object cell : cells)
    {
      printTableCell(cell);
    }
    closeElement();
  }

  @SuppressWarnings("unchecked")
  public <T> void printTableCell(T cell)
  {
    ITableCellFormatter<T> formatter = null;
    
    if(cell != null)
    {
      formatter = (ITableCellFormatter<T>) 
        tableCellFormatterMap_.get(cell.getClass());
    }
    
    if(formatter == null)
      printElement("td", cell);
    else
      print(formatter.format(cell));
  }

  public void printStackTrace(Exception e)
  {
    openElement("pre");
    e.printStackTrace(this);
    closeElement();
  }
}
