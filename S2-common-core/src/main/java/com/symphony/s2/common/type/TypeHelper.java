/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.type;

import java.nio.ByteBuffer;
import java.time.Instant;

import com.google.protobuf.ByteString;
import com.symphony.s2.common.fault.TransactionFault;
import com.symphony.s2.common.hash.Hash;
import com.symphony.s2.common.i18n.Language;

public class TypeHelper
{
  public static Hash newHash(ByteString byteString)
  {
    return Hash.newInstance(byteString);
  }

  public static Hash newHash(String string)
  {
    return Hash.newInstance(string);
  }
  
  public static ByteString convertToByteString(Hash hash)
  {
    return hash.toByteString();
  }

  public static Language newLanguage(String languageTag)
  {
    return Language.newInstance(languageTag);
  }

  public static String convertToString(Language language)
  {
    return language.toString();
  }
  
  public static Instant newInstant(ByteString byteString)
  {
    if(byteString.size() != 16)
      throw new TransactionFault("Instant must be 16 bytes");
    
    ByteBuffer  bytes = ByteBuffer.allocate(16);
    
    byteString.copyTo(bytes);
    bytes.flip();

    return Instant.ofEpochSecond(bytes.getLong(), bytes.getLong());
  }
  
  public static ByteString convertToByteString(Instant instant)
  {
    return convertToInstantByteString(instant.getEpochSecond(), instant.getNano());
  }
  
  public static ByteString convertToInstantByteString(long secs, long nanos)
  {
    ByteBuffer  bytes = ByteBuffer.allocate(16);
    bytes.putLong(secs);
    bytes.putLong(nanos);
    bytes.flip();
    
    return ByteString.copyFrom(bytes);
  }

  public static Instant newInstant(String s)
  {
    String[] parts = s.split("\\.");
    
    if(parts.length != 2)
      throw new TransactionFault("Instant values are SSS.NNN where SSS is seconds and NNN is ns");
    
    try
    {
      return Instant.ofEpochSecond(Long.parseLong(parts[0]), Long.parseLong(parts[0]));
    }
    catch(NumberFormatException e)
    {
      throw new TransactionFault("Invalid Instant value \"" + s + "\"", e);
    }
  }
  
  public static String convertToString(Instant instant)
  {
    return convertToInstantString(instant.getEpochSecond(), instant.getNano());
  }
  
  public static String convertToInstantString(long secs, long nanos)
  {
    return String.format("%d.%d", secs, nanos);
  }
}
