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

package org.symphonyoss.s2.common.dom;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nullable;

public class DomWriter extends DomConsumer<DomWriter> implements IDomWriterOrBuilder, Closeable
{
  private final DomWriterProxy out_;
  private final boolean        doNotClose_;
    
  public DomWriter(Writer out)
  {
    this(out, false, false, false);
  }
  
  public DomWriter(OutputStream out)
  {
    this(new OutputStreamWriter(out), 
        out == System.out || out == System.err, // set doNotClose for System streams
        false, false);
  }
  
  private DomWriter(Writer out, boolean doNotClose, boolean compactMode, boolean canonicalMode)
  {
    super(compactMode, canonicalMode);
    out_ = compactMode ? new CompactWriter(out) : new IndentedWriter(out, canonicalMode);
    doNotClose_ = doNotClose;
  }
  
  public static class Builder extends DomConsumer.Builder<Builder> implements IDomWriterOrBuilder
  {
    private Object        out_;
    private boolean       doNotClose_;
    
    private Builder(Writer out)
    {
      out_ = out;
    }
    
    private Builder(Writer out, IDomConsumerOrBuilder initial)
    {
      super(initial);
      out_ = out;
    }

    private Builder(OutputStream out)
    {
      out_ = out;
    }

    private Builder(OutputStream out, IDomConsumerOrBuilder initial)
    {
      super(initial);
      out_ = out;
    }

    @Override
    public boolean isDoNotClose()
    {
      return doNotClose_;
    }

    public Builder withDoNotClose(boolean doNotClose)
    {
      doNotClose_ = doNotClose;
      return this;
    }
    
    public DomWriter  build()
    {
      return new DomWriter(
          out_ instanceof Writer ? (Writer)out_ :
            new OutputStreamWriter((OutputStream)out_, StandardCharsets.UTF_8), 
          doNotClose_, isCompactMode(), isCanonicalMode());
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
  
  public static Builder newBuilder(Writer writer, IDomConsumerOrBuilder initial)
  {
    return new Builder(writer, initial);
  }
  
  public static Builder newBuilder(OutputStream out, IDomConsumerOrBuilder initial)
  {
    return new Builder(out, initial);
  }

  @Override
  public boolean isDoNotClose()
  {
    return doNotClose_;
  }

  public DomWriter write(IDomNode node) throws IOException
  {
    node.writeTo(this, null);
    return this;
  }
  
  public DomWriter write(String str) throws IOException
  {
    out_.write(str);
    return this;
  }

  public DomWriter writeItem(String item) throws IOException
  {
    out_.writeItem(item, null);
    
    return this;
  }

  public DomWriter writeItem(String item, @Nullable String terminator) throws IOException
  {
    out_.writeItem(item, terminator);
    
    return this;
  }
  
  public DomWriter newline() throws IOException
  {
    out_.newline();
    return this;
  }

  public DomWriter indent(int i)
  {
    out_.indent(i);
    return this;
  }

  public DomWriter outdent(int i)
  {
    out_.outdent(i);
    return this;
  }

  public DomWriter indent()
  {
    out_.indent();
    return this;
  }

  public DomWriter outdent()
  {
    out_.outdent();
    return this;
  }

  public DomWriter openBlock(String s) throws IOException
  {
    out_.openBlock(s);
    return this;
  }

  public DomWriter closeBlock(String s, @Nullable String terminator) throws IOException
  {
    out_.closeBlock(s, terminator);
    return this;
  }

  @Override
  public void close() throws IOException
  {
    if(doNotClose_)
      out_.flush();
    else
      out_.close();
  }

  public DomWriter flush() throws IOException
  {
    out_.flush();
    return this;
  }
  
  public int getTabSize()
  {
    return out_.getTabSize();
  }

  public DomWriter writeColumn(String openQuote, String name, String closeQuote, int width) throws IOException
  {
    out_.write(openQuote);
    out_.write(name);
    out_.write(closeQuote);
    
    if(!isCompactMode())
    {
      width = width - openQuote.length() - name.length() - closeQuote.length();
      
      while(width-- > 0)
        out_.write(' ');
    }
    return this;
  }
}

abstract class DomWriterProxy extends Writer
{
  protected final Writer        out_;

  public abstract void  newline() throws IOException;
  public abstract int   getTabSize();
  public abstract void  indent(int i);
  public abstract void  outdent(int i);
  
  public DomWriterProxy(Writer out)
  {
    out_ = out;
  }
  
  public void  indent()
  {
    indent(1);
  }

  public void  outdent()
  {
    outdent(1);
  }
  
  public void writeItem(String item) throws IOException
  {
    writeItem(item, null);
  }
  
  public void writeItem(String item, @Nullable String terminator) throws IOException
  {
    write(item);
    
    if(terminator != null)
      write(terminator);
    
    newline();
  }
  
  public void openBlock(String s) throws IOException
  {
    writeItem(s);
    indent();
  }
  
  public void closeBlock(String s, @Nullable String terminator) throws IOException
  {
    outdent();
    writeItem(s, terminator);
  }

  @Override
  public void flush() throws IOException
  {
    out_.flush();
  }

  @Override
  public void close() throws IOException
  {
    out_.close();
  }
}

class CompactWriter extends DomWriterProxy
{
  public CompactWriter(Writer out)
  {
    super(out);
  }

  @Override
  public void newline() throws IOException
  {
  }

  @Override
  public void indent(int i)
  {
  }
  
  @Override
  public void outdent(int i)
  {
  }
  
  @Override
  public int getTabSize()
  {
    return 1;
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException
  {
    out_.write(cbuf, off, len);
  }
}

class IndentedWriter extends DomWriterProxy
{
  private final char[]          NL;  

  
  private int                 indent_      = 0;
  private int                 tabSize_     = 2;
  private boolean             startOfLine_ = true;

  public IndentedWriter(Writer out)
  {
    super(out);
    NL = System.getProperty("line.separator").toCharArray();
  }
  
  public IndentedWriter(Writer out, boolean canonicalMode)
  {
    super(out);
    NL = new char[] {'\n'};
  }

  @Override
  public void newline() throws IOException
  {
    write(NL);
  }

  private void doIndent(boolean force) throws IOException
  {
    if(force || startOfLine_)
    {
      for(int i=0 ; i<indent_ ; i++)
        out_.write(' ');
      
      startOfLine_ = false;
    }
  }
  
  @Override
  public void indent(int i)
  {
    indent_ += (i * tabSize_);
  }
  
  @Override
  public void outdent(int i)
  {
    indent_ -= (i * tabSize_);
  }
  
  @Override
  public int getTabSize()
  {
    return tabSize_;
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException
  {
    int blockOff = off;
    int blockLen = 0;
    
    doIndent(false);
    
    for(int i=off ; i<len ; i++)
    {
      if(cbuf[i] == '\n')
      {
        out_.write(cbuf, blockOff, blockLen+1);
        blockOff = i+1;
        blockLen = 0;
        
        if(i<len-1)
          doIndent(true);
        else
          startOfLine_ = true;
      }
      else
      {
        blockLen++;
      }
    }
    if(blockLen > 0)
      out_.write(cbuf, blockOff, blockLen);
  }
}
