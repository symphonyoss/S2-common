/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.writer;

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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
