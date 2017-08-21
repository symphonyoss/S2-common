/*
 * Copyright 2017 Symphony Communication Services, LLC.
 * All Rights Reserved
 */

package com.symphony.s2.common.hash;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.symphony.s2.common.exception.BadFormatException;
import com.symphony.s2.common.fault.CodingFault;
import com.symphony.s2.common.fault.ProgramFault;

/**
 * A factory for Hash objects.
 * 
 * THIS CLASS IS NOT THREAD SAFE.
 * 
 * @author bruce.skingle
 *
 */
@NotThreadSafe
public class HashFactory
{
  private final int                  typeId_;
  private final AbstractHashFunction hashFunction_;

  public HashFactory()
  {
    typeId_ = HashType.defaultHashTypeId_;
    hashFunction_ = HashType.getDefaultHashType().createHashFunction();
  }
  
  public HashFactory(int typeId) throws BadFormatException
  {
    typeId_ = typeId;
    hashFunction_ = HashType.getHashType(typeId).createHashFunction();
  }
  
  public int    getHashTypeId()
  {
    return typeId_;
  }
  
  public @Nonnull Hash   getHashOf(byte[] bytes)
  {
    try
    {
      return new Hash(typeId_, hashFunction_.digest(bytes));
    }
    catch (BadFormatException e)
    {
      throw new ProgramFault("Unexpected hash error", e);
    }
  }
  
  public @Nonnull Hash   getCompositeHashOf(Object ...parts)
  {
 // debug  Logger log_ = LoggerFactory.getLogger(getClass());
 // debug  log_.debug("getCompositeHashOf");
    
    for(Object part : parts)
    {
      if(part instanceof Hash)
      {
// debug        
//        StringBuilder b = new StringBuilder();
//        
//        for(byte bb : ((Hash) part).toBytes())
//        {
//          b.append(String.format("%02X ", 0xFF & bb));
//        }
//        
//        log_.debug("  HASH " + part + " " + b.toString());
        
        if(Hash.NIL_HASH.equals(part))
          throw new CodingFault("NIL_HASH (null value) included as element of composite hash");
        
        hashFunction_.update(((Hash) part).toBytes());
      }
      else if(part instanceof byte[])
      {
        hashFunction_.update(((byte[]) part));
      }
//
//      else if(part instanceof ByteString)
//      {
//        hashFunction_.update(((ByteString) part));
//      }
      else
      {
     // debug  log_.debug("  OBJ  " + part);
        hashFunction_.update(part.toString().getBytes());
      }
    }
    
    try
    {
      byte[] rawDigestBytes = hashFunction_.digest();
      
      return new Hash(typeId_, rawDigestBytes);
    }
    catch (BadFormatException e)
    {
      throw new ProgramFault("Unexpected hash error", e);
    }
  }
}
