/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package com.symphony.s2.common.hash;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.protobuf.ByteString;
import com.symphony.s2.common.exception.BadFormatException;
import com.symphony.s2.common.fault.CodingFault;
import com.symphony.s2.common.fault.TransactionFault;

/**
 * A Hash is an identifier for objects in the system.
 * 
 * The definitive value of a Hash is represented as a byte array, but
 * other standard representations are also provided, including a String
 * containing Hex.
 * 
 * The algorithm used to generate a Hash may need to change over time,
 * initially we are using SHA256, which is defined to be hashTypeId 0.
 * 
 * In order to ensure that Hash values do not collide as and when other
 * hash algorithms are introduced, we append the hash type identifier
 * to the hash value. We append, rather than prefix, the actual hash to
 * ensure that we have an even distribution of values when hashes are
 * created so that they can be used efficiently as keys for systems
 * such as HBase.
 * 
 * In order that it is possible to validate encoded values we finally
 * append the length of the hashTypeId in digits.
 * 
 * For example the SHA256 hash
 * 
 * 7bd288af2fdff17b525c4e1ff722f005639190a468807d084ca65dc4a3925eba
 * 
 * would be represented in byte[] as:
 * 
 * 7b d2 88 af ... 5e ba 00 01
 * 
 * Which is the hash value followed by the hashTypeId 0 followed by the
 * length of the hashTypeId which is 1 byte
 * 
 * and it would be represented in String HEX as
 * 
 * 7bd288af2fdff17b525c4e1ff722f005639190a468807d084ca65dc4a3925eba01
 * 
 * Which is the hash value followed by the hashTypeId 0 as a single Hex
 * digit followed by the length of the hashTypeId in hex digits, which is 1 byte.
 * 
 * This class also provides static factory methods for all hash types.
 * NB. The order of elements in digests_ is significant, do not delete any entry from the array,
 * when adding new digest algorithms change the value of defaultDigestTypeId_ if the new algorithm
 * is to be used as the format for new Hash values.
 * 
 * This class is expected to be called frequently and has been optimised for
 * performance at the expense of some duplication of code between methods.
 * 
 * @author bruce.skingle
 *
 */
@Immutable
//@SuppressFBWarnings(value="JCIP_FIELD_ISNT_FINAL_IN_IMMUTABLE_CLASS",
//  justification="Immutable but with lazy instantiation")
public class Hash implements JsonSerializable, Comparable<Hash>
{
  /* package */ static final byte[]        NIL_BYTE_HASH       = new byte[] { 0 };
  public        static final String        NIL_STRING_HASH     = "0";
  public        static final ByteString    NIL_BYTESTRING_HASH = ByteString.copyFrom(NIL_BYTE_HASH);
  public        static final Hash          NIL_HASH            = new Hash(NIL_BYTE_HASH, HashType.getNilHashType(), NIL_STRING_HASH, NIL_BYTESTRING_HASH);
  
//  public static final Hash ZONE_ALL_OBJECTS_SEQUENCE_ID; 
  public static final Hash GLOBAL_ENVIRONMENT_ID;

  public static int getDefaultHashTypeId()
  {
    return HashType.defaultHashTypeId_;
  }
  
  private static final int[]        hexCharToInt_        = new int[]
  {
      /* 00 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 10 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 20 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 30 */  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1,
      /* 40 */ -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 50 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 60 */ -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 70 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 80 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* 90 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* A0 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* B0 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* C0 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* D0 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      /* E0 */ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
      
  };
  
  private static final char[]       hexIntToChar_         = new char[]
  {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };
  
  static
  {
    try
    {
      HashFactory type1HashFactory = new HashFactory(1);
      
//      ZONE_ALL_OBJECTS_SEQUENCE_ID = type1HashFactory.getHashOf("ZONE_ALL_OBJECTS_SEQUENCE_IDZONE_ALL_OBJECTS_SEQUENCE_IDZONE_ALL_OBJECTS_SEQUENCE_ID".getBytes());
      GLOBAL_ENVIRONMENT_ID = type1HashFactory.getCompositeHashOf("GLOBAL_ENVIRONMENT_IDGLOBAL_ENVIRONMENT_IDGLOBAL_ENVIRONMENT_IDGLOBAL_ENVIRONMENT_IDGLOBAL_ENVIRONMENT_ID");

    }
    catch (BadFormatException e)
    {
      throw new CodingFault(e);
    }
  }
  

  
  private final byte[]     hashBytes_;
  private final HashType   hashType_;
  private final String     hashString_;
  private final ByteString hashByteString_;
  private final String     hashStringBase64_;
  

  private Hash(byte[] hashBytes, HashType hashType, String hashString, ByteString hashByteString)
  {
    hashBytes_ = hashBytes;
    hashType_ = hashType;
    hashString_ = hashString;
    hashByteString_ = hashByteString;
    hashStringBase64_ = Base64.encodeBase64String(hashBytes_);
  }

  /**
   * Create a Hash of the given Hash Type and digest bytes.
   * 
   * The provided digest value may not be zero length or null, use
   * ZERO_HASH to represent a nil Hash value.
   * 
   * @param typeId          The required Hash Type
   * @param rawDigestBytes  The raw digest value as a byte array.
   * 
   * @throws BadFormatException If the parameters are invalid.
   */
  /* package */ Hash(int typeId, @Nonnull byte[] rawDigestBytes) throws BadFormatException
  {
    if(rawDigestBytes == null || rawDigestBytes.length == 0)
      throw new BadFormatException("Null or zero length rawDigest passed, use ZERO_HASH if you really mean null");
    
    hashType_ = HashType.getHashType(typeId);
    
    if(rawDigestBytes.length != hashType_.byteLen_)
      throw new BadFormatException("Hash Type " + typeId + " digest values are " + 
          hashType_.byteLen_ + " bytes but " + rawDigestBytes.length + " were passed.");
    
    hashBytes_ = hashType_.encode(typeId, rawDigestBytes);
    hashByteString_ = ByteString.copyFrom(hashBytes_);
    hashString_ = convertBytesToString(hashType_, hashBytes_);
    hashStringBase64_ = Base64.encodeBase64String(hashBytes_);
  }
  
  /**
   * Create a Hash object from the byte representation.
   * 
   * @param hashBytes    The byte[] representation of a Hash.
   * @throws BadFormatException  If the given string is not a valid hash representation.
   */
  public Hash(byte[] hashBytes) throws BadFormatException
  {
    hashByteString_ = ByteString.copyFrom(hashBytes);
    hashBytes_ = hashByteString_.toByteArray();
    hashType_ = getTypeFromHashBytes(hashBytes);
    hashString_ = convertBytesToString(hashType_, hashBytes_);
    hashStringBase64_ = Base64.encodeBase64String(hashBytes_);
  }

  /**
   * Create a Hash object from the ByteString representation.
   * 
   * @param byteString    The ByteString representation of a Hash.
   * @throws BadFormatException  If the given value is not a valid hash representation.
   */
  public Hash(ByteString byteString) throws BadFormatException
  {
    hashByteString_ = byteString;
    hashBytes_ = byteString.toByteArray();
    hashType_ = getTypeFromHashBytes(hashBytes_);
    hashString_ = convertBytesToString(hashType_, hashBytes_);
    hashStringBase64_ = Base64.encodeBase64String(hashBytes_);
  }
  
  /*
   * Static because its called from constructors
   */
  private static String convertBytesToString(HashType hashType, byte[] hashBytes)
  {
    StringBuilder s = new StringBuilder();
    
    for(int i=0 ; i<hashType.byteLen_ ; i++)
    {
      byte b = hashBytes[i];
      
      s.append(hexIntToChar_[(b & 0xF0) >> 4]);
      s.append(hexIntToChar_[b & 0x0F]);
    }
    
    s.append(hashType.typeIdAndLengthAsString_);
    
    return s.toString();
  }
  
  /*
   * Static because its called from constructors
   */
  private static HashType getTypeFromHashBytes(byte[] hashBytes) throws BadFormatException
  {
    if(hashBytes == null)
      throw new BadFormatException("Hash value is null");
    
    if(hashBytes.length == 0 || (hashBytes.length == 1 && hashBytes[0] == 0))
    {
      return HashType.getNilHashType();
    }
    
    if(hashBytes.length < 3)
      throw new BadFormatException("Hash value is too short");
    
    int len = hashBytes.length;
    int typeIdLen = 0xFF & hashBytes[--len];  // now 0 <= typeIdLen <= 255
    
    if(typeIdLen > len)
      throw new BadFormatException("Hash value is too short");
    
    // Max valid typeIdLen is actually 15 but if the value passed is above that
    // we will fail to find the HashType in a moment so don't bother to check here
    // to save a few fractions of a second.
    
    int typeId;
    
    if(typeIdLen == 1)
    {
      typeId = 0xFF & hashBytes[--len];
    }
    else
    {
      typeId = 0;
      
      while(typeIdLen-- > 0)
        typeId = typeId * 256 + 0xFF & hashBytes[--len];
    }
    
    // throws BadFormatException if typeId is invalid
    HashType hashType = HashType.getHashType(typeId);
    
    if(len != hashType.byteLen_)
      throw new BadFormatException("HashType " + typeId + " values are " + hashType.byteLen_ +
          " bytes but this value is " + len + " bytes.");
    
    return hashType;
  }
  
  /**
   * Create a Hash object from the Hex string representation.
   * 
   * @param hashHexString    The hex representation of an Hash.
   * @throws BadFormatException  If the given string is not a valid hash representation.
   */
  public Hash(String hashHexString) throws BadFormatException
  {
    if(hashHexString == null)
      throw new BadFormatException("Hash value is null");
    
    hashHexString = hashHexString.trim();
    
    if(NIL_STRING_HASH.equals(hashHexString))
    {
      hashBytes_ = NIL_BYTE_HASH;
      hashByteString_ = NIL_BYTESTRING_HASH;
      hashType_ = HashType.getNilHashType();
    }
    else
    {
      if(hashHexString.length() < 3)
        throw new BadFormatException("Hash value is too short");
      
      char[] hexChars = hashHexString.toCharArray();
      int len = hexChars.length;
      int typeIdLen = hexValue(hexChars[--len]);
      
      if(typeIdLen > len)
        throw new BadFormatException("Hash value is too short");
      
      int typeId;
      
      if(typeIdLen == 1)
      {
        typeId = hexValue(hexChars[--len]);
      }
      else
      {
        typeId = 0;
        
        while(typeIdLen-- > 0)
          typeId = typeId * 16 + hexValue(hexChars[--len]);
      }
      
      // throws BadFormatException if typeId is invalid
      hashType_ = HashType.getHashType(typeId);
      
      // Multiply by 2 ensures that the value we have is of even length
      if(len != 2 * hashType_.byteLen_)
        throw new BadFormatException("HashType " + typeId + " values are " + hashType_.byteLen_ +
            " bytes but this value is " + len + " bytes.");
      
      int typeIdByteLen = hashType_.typeIdAsBytes_.length;
      int byteLen = hashType_.byteLen_ + typeIdByteLen + 1;
      hashBytes_ = new byte[byteLen];
      
      hashBytes_[--byteLen] = (byte) hashType_.typeIdAsBytes_.length;
      
      while(typeIdByteLen>0)
        hashBytes_[--byteLen] = hashType_.typeIdAsBytes_[--typeIdByteLen];
      
      while(byteLen>0)
      {
        hashBytes_[--byteLen] = (byte) (hexValue(hexChars[--len]) + 16 * hexValue(hexChars[--len]));
      }
      hashByteString_ = ByteString.copyFrom(hashBytes_);
    }
    
    hashString_ = hashHexString;
    hashStringBase64_ = Base64.encodeBase64String(hashBytes_);
  }
  
  private int hexValue(char c) throws BadFormatException
  {
    int v = hexCharToInt_[c];
    
    if(v == -1)
      throw new BadFormatException("Invalid Hex character \"" + c + "\"");
    
    return v;
  }

  /* package */ byte[] toBytes()
  {
    return hashBytes_;
  }

  @Override
  public @Nonnull String toString()
  {
    return hashString_;
  }

  /**
   * Return the ByteString representation of this Hash.
   * 
   * Does not involve a copy operation.
   * 
   * @return The ByteString representation of this Hash.
   */
  public @Nonnull ByteString toByteString()
  {
    return hashByteString_;
  }

  @Override
  public boolean equals(Object anObject)
  {
    if(anObject == null)
      return false;
    
    if(anObject instanceof Hash)
      return hashString_.equals(((Hash) anObject).hashString_);
    
    return false;
  }

  @Deprecated
  public boolean matches(ByteString other)
  {
    if(other.size() != hashBytes_.length)
      return false;
    
    for(int i=0 ; i<hashBytes_.length ; i++)
      if(hashBytes_[i] != other.byteAt(i))
        return false;
    
    return true;
  }

  @Override
  public int hashCode()
  {
    return hashString_.hashCode();
  }

  @Override
  public int compareTo(Hash o)
  {
    return hashString_.compareTo(o.toString());
  }

  @Override
  public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException
  {
    if(gen == null)
      throw new NullPointerException("GsonGenerator may not be null");
    
    gen.writeString(toString());
  }

  @Override
  public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
      throws IOException
  {
    if(gen == null)
      throw new NullPointerException("GsonGenerator may not be null");
    
    gen.writeString(toString());
  }

  public static @Nullable Hash newNullableInstance(ByteString byteString) throws BadFormatException
  {
    if(byteString.isEmpty())
      return null;
    
    return new Hash(byteString);
  }
  
  public static @Nonnull Hash newInstance(ByteString byteString)
  {
    if(byteString == null || byteString.isEmpty())
    {
      return NIL_HASH;
//      throw new TransactionFault("Unexpected null hash value");
    }
    
    try
    {
      return new Hash(byteString);
    } catch (BadFormatException e)
    {
      throw new TransactionFault(e);
    }
  }
  

  
  public static @Nonnull Hash newInstance(byte[] bytes)
  {
    if(bytes == null || bytes.length == 0)
    {
      return NIL_HASH;
//      throw new TransactionFault("Unexpected null hash value");
    }
    
    try
    {
      return new Hash(bytes);
    } catch (BadFormatException e)
    {
      throw new TransactionFault(e);
    }
  }
  
  public static @Nonnull Hash newInstance(String string)
  {
    if(string == null || string.isEmpty())
    {
      return NIL_HASH;
//      throw new TransactionFault("Unexpected null hash value");
    }
    
    try
    {
      return new Hash(string);
    } catch (BadFormatException e)
    {
      throw new TransactionFault(e);
    }
  }
  
  public static @Nonnull ByteString asByteString(Hash hash)
  {
    if(hash == null)
      return NIL_BYTESTRING_HASH;
    
    return hash.toByteString();
  }

  public String toStringBase64()
  {
    return hashStringBase64_;
  }
}
