/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.hash;

import javax.annotation.Nullable;

/* package */ abstract class AbstractHashType
{
  private final AbstractHashFunctionFactory        hashFunctionFactory_;
  
  /* package */ AbstractHashType(@Nullable AbstractHashFunctionFactory hashFunctionFactory)
  {
    hashFunctionFactory_ = hashFunctionFactory;
    
    // If any digestId is invalid let's find out sooner rather than later.
    if(hashFunctionFactory_ != null)
      createHashFunction();
  }

  /* package */ AbstractHashFunction createHashFunction()
  {
    return hashFunctionFactory_.createHashFunction();
  }
}
