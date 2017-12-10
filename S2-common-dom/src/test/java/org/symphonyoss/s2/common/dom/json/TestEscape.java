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

import org.junit.Assert;
import org.junit.Test;

public class TestEscape
{
  @Test
  public void testEscape()
  {
    test("\"<html>\"", "<html>");
    test("\"Hello \\\"you A\\\"\"", "Hello \"you \101\"");
    test("\"This\\nIs\\ryet\\tanother\\ftest\"", "This\nIs\ryet\tanother\ftest");
    test("\"BackSpace\\b\"", "BackSpace\u0008");
    test("\"Bell\\0007\"", "Bell\u0007");
    test("\"Tab\\t\"", "Tab\u0009");
    test("\"Hello \\0000\\0000\\0001\\0002\\0003\\0004\\0005\\0006\\0007\\b\\t\\n\\000b\\f\\r\\000e\\000f\\0010\\0011\\0012\\0013\\0014\"",
          "Hello \000" + new String(new char[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}));
    test("\"\\0000\\0001\\0002\\0003\\0004\\0005\\0006\\0007\\b\\t\\0014\\0015\\0016\\0017\\0018\\0019\\001a\\001b\\001c\\001d\\001e\"",
          new String(new char[] {0,1,2,3,4,5,6,7,8,9,20,21,22,23,24,25,26,27,28,29,30}));
    test("\"\\0000\\0001\\0002\\0003\\0004\\0005\\0006\\0007\\b\\t\\001e\\001f !\\\"#$%&'\\001e\"",
          new String(new char[] {0,1,2,3,4,5,6,7,8,9,30,31,32,33,34,35,36,37,38,39,30}));
  }

  private void test(String expect, String src)
  {
    String got = JsonString.quote(src);
    
   // System.out.format("%-20s -> %s%n", src, got);
    
    if(!expect.equals(got))
    {
      String msg = String.format("Expected:%s%nReceived:%s", expect, got);
      
      System.err.println(msg);
      Assert.fail(msg);
    }
  }
}
