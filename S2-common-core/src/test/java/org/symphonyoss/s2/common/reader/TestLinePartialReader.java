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

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;


public class TestLinePartialReader
{
  @Test
  public void testAllCRs() throws IOException
  {
    try(LinePartialReader.Factory factory = new LinePartialReader.Factory(
        new StringReader("\r\r\r\r\r\r\r\r\r\r"), 5))
    {
    
      LinePartialReader reader = factory.getNextReader();
      
      Assert.assertEquals(null, reader);
    }
  }
  
  @Test
  public void testLeadingCRs() throws IOException
  {
    try(LinePartialReader.Factory factory = new LinePartialReader.Factory(
        new StringReader("\r\r\r\r\r\r\r\r\r\rHello"), 5))
    {
      char[] buf = new char[10];
      
      LinePartialReader reader = factory.getNextReader();
      
      Assert.assertEquals(10, reader.read(buf));
      
      Assert.assertArrayEquals("\r\r\r\r\r\r\r\r\r\r".toCharArray(), buf);
      
      Assert.assertEquals(5, reader.read(buf));
      
      assertArrayEquals("Hello".toCharArray(), buf);
      
      Assert.assertEquals(null, factory.getNextReader());
    }
  }
  
  @Test
  public void testEmbeddedCRs() throws IOException
  {
    try(LinePartialReader.Factory factory = new LinePartialReader.Factory(
        new StringReader("Hello\r\rWorld\n"), 5))
    {
      char[] buf = new char[100];
      
      LinePartialReader reader = factory.getNextReader();
      
      Assert.assertEquals(12, reader.read(buf));
      
      assertArrayEquals("Hello\r\rWorld".toCharArray(), buf);
      
      Assert.assertEquals(-1, reader.read(buf));
      
      Assert.assertEquals(null, factory.getNextReader());
    }
  }
  
  @Test
  public void testNewLine() throws IOException
  {
    testNewLine(5);
    testNewLine(1024);
  }
  
  private void testNewLine(int bufSize) throws IOException
  {
    try(LinePartialReader.Factory factory = new LinePartialReader.Factory(
        new StringReader("Hello\nThis is a test\nWith 3 lines\n"), bufSize))
    {
      char[] buf = new char[255];
      
      LinePartialReader reader = factory.getNextReader();
      
      Assert.assertEquals(5, reader.read(buf));
      
      assertArrayEquals("Hello".toCharArray(), buf);
      
      Assert.assertEquals(-1, reader.read(buf));
      
      reader = factory.getNextReader();
      
      reader.read(buf);
      
      assertArrayEquals("This is a test".toCharArray(), buf);
      
      Assert.assertEquals(-1, reader.read(buf));
      
      reader = factory.getNextReader();
      
      reader.read(buf);
      
      assertArrayEquals("With 3 lines".toCharArray(), buf);
      
      Assert.assertEquals(-1, reader.read(buf));
      
      Assert.assertEquals(null, factory.getNextReader());
    }
  }

  @Test(expected=IOException.class)
  public void testFailOnIncompleteReader() throws IOException
  {
    try(LinePartialReader.Factory factory = new LinePartialReader.Factory(
        new StringReader("\r\r\r\r\r\r\r\r\r\rHello"), 5))
    {
      char[] buf = new char[10];
      
      LinePartialReader reader = factory.getNextReader();
      
      Assert.assertEquals(10, reader.read(buf));
      
      factory.getNextReader();
    }
  }

  private void assertArrayEquals(char[] expect, char[] get)
  {
    for(int i=0 ; i<expect.length ; i++)
    {
      if(expect[i] != get[i])
        Assert.fail("Arrays not as expected");
    }
  }
}
