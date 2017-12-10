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

package org.symphonyoss.s2.common.reader;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

/**
 * A PartialReader which reads one line from the underlying reader.
 * An end of line is zero or more \r followed by one \n.
 * 
 * @author Bruce Skingle
 *
 */
public class LinePartialReader extends PartialReader
{
  private final Factory factory_;
  private boolean atEol_ = false;
  
  public LinePartialReader(Factory factory)
  {
    factory_ = factory;
  }
  
  public boolean isAtEof()
  {
    synchronized(factory_)
    {
      return atEol_;
    }
  }
  
  private void setAtEof()
  {
    synchronized(factory_)
    {
      atEol_ = true;
    }
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException
  {
    int nBytes = 0;
    
    if(atEol_)
      return -1;
    
    while(true)
    {
      while(factory_.bufRead_ < factory_.bufLimit_ && factory_.buf_[factory_.bufRead_] == '\r')
      {
        factory_.bufRead_++;
        factory_.crCount_++;
      }
      
      if(factory_.bufRead_ < factory_.bufLimit_)
      {
        if(factory_.buf_[factory_.bufRead_] == '\n')
        {
          factory_.bufRead_++;    // step over the newline so the next reader doesn't see it
          factory_.crCount_ = 0;  // discard CRs which precede a NL
          atEol_ = true;
          
          return nBytes;
        }
        
        // Else we have some data
        while(len>0 && factory_.crCount_>0)
        {
          cbuf[off++] = '\r';
          nBytes++;
          factory_.crCount_--;
          len--;
        }
        
        while(len>0 && factory_.bufRead_ < factory_.bufLimit_)
        {
          char c = factory_.buf_[factory_.bufRead_];
          
          if(c == '\n')
          {
            factory_.crCount_ = 0;
            atEol_ = true;
            factory_.bufRead_++;
            break;
          }
          else if(c == '\r')
          {
            factory_.crCount_++;
            factory_.bufRead_++;
          }
          else
          {
            while(factory_.crCount_>0 && len>0)
            {
              cbuf[off++] = '\r';
              factory_.crCount_--;
            }
            
            if(len>0)
            {
              factory_.bufRead_++;
              cbuf[off++] = c;
              nBytes++;
              len--;
            }
          }
        }
        
        if(atEol_ || len == 0)
          return nBytes;
      }
      
      // We still need more characters and we have used up the whole buffer
      factory_.bufLimit_ = factory_.reader_.read(factory_.buf_, 0, factory_.bufSize_);
      factory_.bufRead_ = 0;
      
      if(factory_.bufLimit_ == -1)
      {
        // we hit EOF
        setAtEof();
        
        if(nBytes == 0)
          return -1;
        
        return nBytes;
      }
    }
  }

  @Override
  public void close() throws IOException
  {
  }

  public static class Factory implements Closeable
  {
    private final int         bufSize_;
    private final Reader      reader_;
    private final char[]      buf_;
    private int               bufRead_;
    private int               bufLimit_;
    private int               crCount_;
    private boolean           atEof_;
    private LinePartialReader currentReader_;
    
    public Factory(Reader reader)
    {
      this(reader, 1024);
    }
    
    public Factory(Reader reader, int bufSize)
    {
      bufSize_  = bufSize;
      reader_   = reader;
      buf_      = new char[bufSize_];
    }
    
    public synchronized LinePartialReader  getNextReader() throws IOException
    {
      if(currentReader_ != null && !currentReader_.isAtEof())
        throw new IOException("Current reader is not at EOF");
      
      if(atEof_)
        return null;

      while(true)
      {
        while(bufRead_ < bufLimit_ && buf_[bufRead_] == '\r')
        {
          bufRead_++;
          crCount_++;
        }
        
        if(bufRead_ < bufLimit_) // We read all the crs and we are not at EOF
          return currentReader_ = new LinePartialReader(this);
        
        // We read zero or more CRs and hit the end of the buffer
        bufLimit_ = reader_.read(buf_, 0, bufSize_);
        bufRead_ = 0;
        
        if(bufLimit_ == -1)
        {
          // we hit EOF
          atEof_ = true;
          reader_.close();
          return null;
        }
      }
    }

    @Override
    public void close() throws IOException
    {
      reader_.close();
    }
  }
}
