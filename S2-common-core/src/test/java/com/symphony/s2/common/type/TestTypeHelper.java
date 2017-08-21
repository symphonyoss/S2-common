/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.type;

import java.time.Instant;

import org.junit.Test;
import org.junit.Assert;

import com.google.protobuf.ByteString;
import com.symphony.s2.common.type.TypeHelper;

public class TestTypeHelper
{
  @Test
  public void testInstant()
  {
    Instant now = Instant.now();
    ByteString bytes = TypeHelper.convertToByteString(now);
    
    Instant then = TypeHelper.newInstant(bytes);
    
    Assert.assertEquals(now, then);
    
  }
}
