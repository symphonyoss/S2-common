/*
 *
 *
 * Copyright 2019 Symphony Communication Services, LLC.
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

package org.symphonyoss.s2.common.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for ImmutableByteArray
 * 
 * @author Bruce Skingle
 *
 */
public class TestImmutableByteArray
{
  /**
   * Test implementation of Comparable
   */
  @Test
  public void testCompare()
  {
    doCompare("aa", "bb");
    doCompare("bb", "aa");
    doCompare("aa", "aa");
    doCompare("aa1", "aa");
    doCompare("aa", "aa1");
    doCompare("Hello", "World");
    doCompare("Hello", "");
    doCompare("", "World");
  }

  private void doCompare(String a, String b)
  {
    ImmutableByteArray ia = ImmutableByteArray.newInstance(a);
    ImmutableByteArray ib = ImmutableByteArray.newInstance(b);
    
    System.err.println("ia " + ia.getClass());
    
    int ic = ia.compareTo(ib);
    int c = a.compareTo(b);
    
    if(c == 0)
      assertEquals(0, ic);
    else if(c < 0)
      assertTrue(ic < 0);
    else
      assertTrue(ic > 0);
    
  }
  
  
}
