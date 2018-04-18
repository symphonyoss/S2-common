/*
 *
 *
 * Copyright 2018 Symphony Communication Services, LLC.
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
import java.io.InputStream;

import com.google.protobuf.ByteString;

public class ByteStringInputStream extends InputStream
{
  private final ByteString input_;
  
  private int pos_ = 0;

  public ByteStringInputStream(ByteString input)
  {
    input_ = input;
  }

  @Override
  public int read() throws IOException
  {
    if(pos_ < input_.size())
      return input_.byteAt(pos_++);
    
    return -1;
  }
}
