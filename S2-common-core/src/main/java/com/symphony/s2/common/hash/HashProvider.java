package com.symphony.s2.common.hash;

import javax.annotation.Nonnull;

import com.symphony.s2.common.exception.BadFormatException;
import com.symphony.s2.common.fault.CodingFault;

/**
 * A thread safe static alternative to creating a ThreadFactory.
 * 
 * Note that the methods on this class are synchronized, it would be more
 * performant to create a ThreadFactory (which is not thread-safe) as
 * opposed to using this class.
 * 
 * @author bruce.skingle
 *
 */
public class HashProvider
{
  private static HashFactory[] factories_;
  
  static
  {
    factories_= new HashFactory[HashType.hashTypes_.length];
    
    try
    {
      for(int i=1 ; i<factories_.length ; i++)
      {
        factories_[i] = new HashFactory(i);
      }
    }
    catch (BadFormatException e)
    {
      // "Can't happen"
      throw new CodingFault(e);
    }
  }
  
  public static @Nonnull Hash getHashOf(byte[] bytes)
  {
    HashFactory factory = factories_[HashType.defaultHashTypeId_];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  public static @Nonnull Hash getHashOf(int hashType, byte[] bytes) throws BadFormatException
  {
    if(hashType < 1 || hashType >= factories_.length)
      throw new BadFormatException("Invalid hash type ID " + hashType);
    
    HashFactory factory = factories_[hashType];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  public static @Nonnull Hash getCompositeHashOf(int hashType, Object ...parts) throws BadFormatException
  {
    if(hashType < 1 || hashType >= factories_.length)
      throw new BadFormatException("Invalid hash type ID " + hashType);
    
    HashFactory factory = factories_[hashType];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
  
  public static @Nonnull Hash getCompositeHashOf(Object ...parts)
  {
    HashFactory factory = factories_[HashType.defaultHashTypeId_];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
  
  public static @Nonnull Hash getType1CompositeHashOf(Object ...parts)
  {
    HashFactory factory = factories_[1];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
}
