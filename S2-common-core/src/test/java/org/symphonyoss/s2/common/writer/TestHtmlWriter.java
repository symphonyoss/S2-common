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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.junit.Assert;
import org.junit.Test;

public class TestHtmlWriter
{
  @Test
  public void testBuilder()
  {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(bout);
    
    HtmlWriter out = HtmlWriter.newBuilder(writer).build();
    
    out.openElement("table");
    out.printTableRow("This", "is a test");
    out.closeElement();
    out.close();
    
    Assert.assertEquals("<table>\n" + 
        "  <tr>\n" + 
        "    <td>This</td>\n" + 
        "    <td>is a test</td>\n" + 
        "  </tr>\n" + 
        "</table>\n", bout.toString());
    
    bout = new ByteArrayOutputStream();
    writer = new OutputStreamWriter(bout);
    out = HtmlWriter.newBuilder(writer).withTabSize(8).build();
    
    out.openElement("table");
    out.printTableRow("This", "is another", "test");
    out.closeElement();
    out.close();
    
    Assert.assertEquals("<table>\n" + 
        "        <tr>\n" + 
        "                <td>This</td>\n" + 
        "                <td>is another</td>\n" + 
        "                <td>test</td>\n" + 
        "        </tr>\n" + 
        "</table>\n", bout.toString());
  }
}
