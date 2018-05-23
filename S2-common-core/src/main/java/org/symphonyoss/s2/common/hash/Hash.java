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

package org.symphonyoss.s2.common.hash;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.binary.Base64;
import org.symphonyoss.s2.common.exception.InvalidValueException;
import org.symphonyoss.s2.common.fault.TransactionFault;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

import com.google.protobuf.ByteString;

/**
 * A Hash is an identifier for objects in the system.
 * 
 * The definitive value of a Hash is represented as a byte array, but
 * other standard representations are also provided, including a String
 * containing Hex, and two flavours of Base64.
 * 
 * The preferred representations are binary (byte[]) or URLSafeBase64,
 * and the toString() method now (with effect from 0.1.15) returns the
 * same value as toStringURLSafeBase64(). 
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
public class Hash implements Comparable<Hash>
{
  /* package */ static final ImmutableByteArray NIL_BYTE_HASH       = ImmutableByteArray.newInstance(new byte[] { 0 });
  /* package */ static final String             NIL_STRING_HASH     = "0";
  /* package */ static final ByteString         NIL_BYTESTRING_HASH = NIL_BYTE_HASH.toByteString();
  
  /** The NIL (zero) Hash. Use in preference to null values */
  public        static final Hash          NIL_HASH            = new Hash(NIL_BYTE_HASH, HashType.getNilHashType(), NIL_STRING_HASH);
  
  /**
   * Return the default HashType ID.
   * 
   * @return the default HashType ID.
   */
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
  
  private final ImmutableByteArray hashBytes_;
  private final HashType           hashType_;
  private final String             hashString_;

  private Hash(ImmutableByteArray hashBytes, HashType hashType, String hashString)
  {
    hashBytes_ = hashBytes;
    hashType_ = hashType;
    hashString_ = hashString;
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
   * @throws InvalidValueException If the parameters are invalid.
   */
  /* package */ Hash(int typeId, @Nonnull byte[] rawDigestBytes) throws InvalidValueException
  {
    if(rawDigestBytes == null || rawDigestBytes.length == 0)
      throw new InvalidValueException("Null or zero length rawDigest passed, use ZERO_HASH if you really mean null");
    
    hashType_ = HashType.getHashType(typeId);
    
    if(rawDigestBytes.length != hashType_.byteLen_)
      throw new InvalidValueException("Hash Type " + typeId + " digest values are " + 
          hashType_.byteLen_ + " bytes but " + rawDigestBytes.length + " were passed.");
    
    hashBytes_ = ImmutableByteArray.newInstance(hashType_.encode(typeId, rawDigestBytes));
    hashString_ = convertBytesToString(hashType_, hashBytes_);
  }
  
  /**
   * Create a Hash object from the byte representation.
   * 
   * @param hashBytes    The byte[] representation of a Hash.
   * @throws InvalidValueException  If the given string is not a valid hash representation.
   */
  public Hash(byte[] hashBytes) throws InvalidValueException
  {
    this(ImmutableByteArray.newInstance(hashBytes));
  }
  
  /**
   * Create a Hash object from the byte representation.
   * 
   * @param hashBytes    The byte[] representation of a Hash.
   * @throws InvalidValueException  If the given string is not a valid hash representation.
   */
  public Hash(ImmutableByteArray hashBytes) throws InvalidValueException
  {
    hashBytes_ = hashBytes;
    hashType_ = getTypeFromHashBytes(hashBytes);
    hashString_ = convertBytesToString(hashType_, hashBytes_);
  }

  /**
   * Create a Hash object from the ByteString representation.
   * 
   * @param byteString    The ByteString representation of a Hash.
   * @throws InvalidValueException  If the given value is not a valid hash representation.
   */
  public Hash(ByteString byteString) throws InvalidValueException
  {
    hashBytes_ = ImmutableByteArray.newInstance(byteString);
    hashType_ = getTypeFromHashBytes(hashBytes_);
    hashString_ = convertBytesToString(hashType_, hashBytes_);
  }
  
  /*
   * Static because its called from constructors
   */
  private static String convertBytesToString(HashType hashType, ImmutableByteArray hashBytes)
  {
    StringBuilder s = new StringBuilder();
    
    for(int i=0 ; i<hashType.byteLen_ ; i++)
    {
      byte b = hashBytes.byteAt(i);
      
      s.append(hexIntToChar_[(b & 0xF0) >> 4]);
      s.append(hexIntToChar_[b & 0x0F]);
    }
    
    s.append(hashType.typeIdAndLengthAsString_);
    
    return s.toString();
  }
  
  /*
   * Static because its called from constructors
   */
  private static HashType getTypeFromHashBytes(ImmutableByteArray hashBytes) throws InvalidValueException
  {
    if(hashBytes == null)
      throw new InvalidValueException("Hash value is null");
    
    if(hashBytes.length() == 0 || (hashBytes.length() == 1 && hashBytes.byteAt(0) == 0))
    {
      return HashType.getNilHashType();
    }
    
    if(hashBytes.length() < 3)
      throw new InvalidValueException("Hash value is too short");
    
    int len = hashBytes.length();
    int typeIdLen = 0xFF & hashBytes.byteAt(--len);  // now 0 <= typeIdLen <= 255
    
    if(typeIdLen > len)
      throw new InvalidValueException("Hash value is too short");
    
    // Max valid typeIdLen is actually 15 but if the value passed is above that
    // we will fail to find the HashType in a moment so don't bother to check here
    // to save a few fractions of a second.
    
    int typeId;
    
    if(typeIdLen == 1)
    {
      typeId = 0xFF & hashBytes.byteAt(--len);
    }
    else
    {
      typeId = 0;
      
      while(typeIdLen-- > 0)
        typeId = typeId * 256 + 0xFF & hashBytes.byteAt(--len);
    }
    
    // throws BadFormatException if typeId is invalid
    HashType hashType = HashType.getHashType(typeId);
    
    if(len != hashType.byteLen_)
      throw new InvalidValueException("HashType " + typeId + " values are " + hashType.byteLen_ +
          " bytes but this value is " + len + " bytes.");
    
    return hashType;
  }
  
  /**
   * Create a Hash object from the Hex string representation.
   * 
   * @param hashHexString    The hex representation of an Hash.
   * @return The Hash represented by the given hex string.
   * @throws InvalidValueException  If the given string is not a valid hash representation.
   */
  public static Hash ofHexString(String hashHexString) throws InvalidValueException
  {
    if(hashHexString == null)
      throw new InvalidValueException("Hash value is null");
    
    hashHexString = hashHexString.trim();
    
    if(NIL_STRING_HASH.equals(hashHexString))
    {
      return NIL_HASH;
    }

    if(hashHexString.length() < 3)
      throw new InvalidValueException("Hash value is too short");
    
    char[] hexChars = hashHexString.toCharArray();
    int len = hexChars.length;
    int typeIdLen = hexValue(hexChars[--len]);
    
    if(typeIdLen > len)
      throw new InvalidValueException("Hash value is too short");
    
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
    HashType hashType = HashType.getHashType(typeId);
    
    // Multiply by 2 ensures that the value we have is of even length
    if(len != 2 * hashType.byteLen_)
      throw new InvalidValueException("HashType " + typeId + " values are " + hashType.byteLen_ +
          " bytes but this value is " + len + " bytes.");
    
    int typeIdByteLen = hashType.typeIdAsBytes_.length;
    int byteLen = hashType.byteLen_ + typeIdByteLen + 1;
    byte[] hashBytes = new byte[byteLen];
    
    hashBytes[--byteLen] = (byte) hashType.typeIdAsBytes_.length;
    
    while(typeIdByteLen>0)
      hashBytes[--byteLen] = hashType.typeIdAsBytes_[--typeIdByteLen];
    
    while(byteLen>0)
    {
      hashBytes[--byteLen] = (byte) (hexValue(hexChars[--len]) + 16 * hexValue(hexChars[--len]));
    }
    
    return new Hash(ImmutableByteArray.newInstance(hashBytes), hashType, hashHexString);
  }
  
  private static int hexValue(char c) throws InvalidValueException
  {
    int v = hexCharToInt_[c];
    
    if(v == -1)
      throw new InvalidValueException("Invalid Hex character \"" + c + "\"");
    
    return v;
  }
  
  /**
   * Return the Hash represented by the given Base64 string, which may be in
   * either the standard or the URLSafe format of Base64.
   * 
   * @param base64String The BAe64 representation of a Hash, which may be in
   * either the standard or the URLSafe format of Base64.
   * 
   * @return The Hash represented by the given value.
   * 
   * @throws InvalidValueException If the given value is not a valid Hash value.
   */
  public static Hash ofBase64String(String base64String) throws InvalidValueException
  {
    return new Hash(Base64.decodeBase64(base64String));
  }

  
  /**
   * Return the byte[] representation of this Hash as an ImmutableByteArray.
   * 
   * Does not involve a copy operation.
   * 
   * @return The ImmutableByteArray representation of this Hash.
   */
  public ImmutableByteArray toImmutableByteArray()
  {
    return hashBytes_;
  }
  
  /**
   * Return the integer hash type of this hash.
   * 
   * @return The integer hash type of this hash.
   */
  public int getTypeId()
  {
    return hashType_.getHashTypeId();
  }

  @Override
  public @Nonnull String toString()
  {
    return hashBytes_.toBase64UrlSafeString();
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
    return hashBytes_.toByteString();
  }

  @Override
  public boolean equals(Object anObject)
  {
    if(anObject == null)
      return false;
    
    if(anObject instanceof Hash)
      return toStringUrlSafeBase64().equals(((Hash) anObject).toStringUrlSafeBase64());
    
    return false;
  }

  @Override
  public int hashCode()
  {
    return toStringUrlSafeBase64().hashCode();
  }

  @Override
  public int compareTo(Hash o)
  {
    return toStringUrlSafeBase64().compareTo(o.toStringUrlSafeBase64());
  }
  
  /**
   * Create a Hash object from the byteString representation.
   * 
   * @param byteString    The byteString representation of a Hash.
   * @return  a Hash object from the byteString representation.
   * 
   * @throws TransactionFault  If the given string is not a valid hash representation.
   */
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
    } catch (InvalidValueException e)
    {
      throw new TransactionFault(e);
    }
  }
  
  /**
   * Create a Hash object from the byte representation.
   * 
   * @param bytes    The byte[] representation of a Hash.
   * @return  a Hash object from the byte representation.
   * 
   * @throws TransactionFault  If the given string is not a valid hash representation.
   */
  public static @Nonnull Hash newInstance(byte[] bytes)
  {
    if(bytes == null || bytes.length == 0)
    {
      return NIL_HASH;
    }
    
    try
    {
      return new Hash(bytes);
    } catch (InvalidValueException e)
    {
      throw new TransactionFault(e);
    }
  }
  
  /**
   * Return the Hash represented by the given (Base64) String value.
   * 
   * @param string  the Hash represented by the given (Base64) String value.
   * 
   * @return the Hash represented by the given (Base64) String value.
   */
  public static @Nonnull Hash newInstance(String string)
  {
    if(string == null || string.isEmpty())
    {
      return NIL_HASH;
    }
    
    try
    {
      return ofBase64String(string);
    } catch (InvalidValueException e)
    {
      throw new TransactionFault(e);
    }
  }
  
  /**
   * Return the ByteString representation of the given Hash value.
   * 
   * @param hash  A Hash value.
   * 
   * @return The ByteString representation of the given value.
   */
  public static @Nonnull ByteString asByteString(Hash hash)
  {
    if(hash == null)
      return NIL_BYTESTRING_HASH;
    
    return hash.toByteString();
  }

  /**
   * Return the Hex encoding of this Hash value.
   * 
   * @return the Hex encoding of this Hash value.
   */
  public String toStringHex()
  {
    return hashString_;
  }

  /**
   * Return the value of this Hash as a Base64 String.
   * 
   * @return the value of this Hash as a Base64 String.
   */
  public String toStringBase64()
  {
    return hashBytes_.toBase64String();
  }

  /**
   * Return the value of this Hash as a URL safe Base64 String.
   * 
   * @return the value of this Hash as a URL safe Base64 String.
   */
  public String toStringUrlSafeBase64()
  {
    return hashBytes_.toBase64UrlSafeString();
  }

  /**
   * Return the value of the given Hash as a ByteString.
   * 
   * @param hash A Hash value
   * 
   * @return the value of the given Hash as a ByteString.
   */
  public static ByteString toByteString(Hash hash)
  {
    return hash.toByteString();
  }
  
  /**
   * Return a Hash value decoded from the given ByteString value.
   * 
   * @param byteString An encoded Hash.
   * 
   * @return The Hash represented by the given encoding.
   * 
   * @throws InvalidValueException If the given encoding is invalid.
   */
  public static Hash build(ByteString byteString) throws InvalidValueException
  {
    return new Hash(byteString);
  }

  /**
   * Return the value of the given Hash as a ByteString.
   * 
   * @param hash A Hash value
   * 
   * @return the value of the given Hash as a ImmutableByteArray.
   */
  public static ImmutableByteArray toImmutableByteArray(Hash hash)
  {
    return hash.toImmutableByteArray();
  }
  
  /**
   * Return a Hash value decoded from the given ImmutableByteArray value.
   * 
   * @param byteString An encoded Hash.
   * 
   * @return The Hash represented by the given encoding.
   * 
   * @throws InvalidValueException If the given encoding is invalid.
   */
  public static Hash build(ImmutableByteArray byteString) throws InvalidValueException
  {
    return new Hash(byteString);
  }
}
