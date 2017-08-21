/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.hash;

/* package */ abstract class AbstractHashFunction
{
  /* package */ abstract byte[] digest(byte[] bytes);

  /* package */ abstract void update(byte[] bytes);

  /* package */ abstract byte[] digest();
}
