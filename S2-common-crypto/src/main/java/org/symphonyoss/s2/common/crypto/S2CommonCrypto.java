/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package org.symphonyoss.s2.common.crypto;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class S2CommonCrypto
{

  private static boolean initialized_;

  public synchronized static void init()
  {
    if(!initialized_)
    {
      if(Security.getProvider("BC") == null)
      {
        Security.addProvider(new BouncyCastleProvider());
      }
      initialized_ = true;
    }
  }

}
