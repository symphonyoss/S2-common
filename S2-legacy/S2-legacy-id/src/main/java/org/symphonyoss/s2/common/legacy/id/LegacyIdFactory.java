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

package org.symphonyoss.s2.common.legacy.id;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.hash.Hash;
import org.symphonyoss.s2.common.hash.HashProvider;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

/**
 * A factory to create inferred Hash values each of which is
 * a 2.0 ID which acts as a mirror for an object in the legacy (Symphony1.5) space.
 *
 * Many methods take a tenantId which it uses to generate a globally unique ID for each
 * object across all pods. This means that if the same threadId happens to exist in 2
 * separate pods and we create a 2.0 Sequence to represent those two threads that the
 * 2.0 IDs will not collide in a multi tenant environment.
 *
 * This also means that where the same message goes xpod that events about the same message
 * originating from different pods will have different event IDs. In the comments below
 * where it says "originating pod" it means the pod where the event originates which may
 * be different from the pod where the message originated.
 *
 * User IDs
 * ========
 * User IDs in SBE contain a 36 bit per pod user Id and a 27 bit podId
 * which are OR'ed together to form a single 63 bit value which is stored in a long integer,
 * the most significant (sign) bit, in bit position 0, is always zero the podId is in bit
 * positions 1-28 and the per tenant user Id is in bit positions 29-63.
 * 
 * Internal pod IDs are not unique so these userIds are not globally unique and
 * we therefore have a separate "external podId" and code in
 * https://github.com/SymphonyOSF/SBE/blob/dev/commons/runtimecommons/src/main/java/com/symphony/runtime/commons/util/UserIDUtil.java
 * to manipulate these IDs to replace the "internal podId" with an "externalPodId"
 * 
 * In the 2.0 space we use a globally unique hash to refer to a user called the principalId
 * which is constructed from the external userId, i.e. the userId with the podId part replaced
 * with the pod's externalPodId.
 * 
 * This class takes an internal and external podId in its constructor which allows it to map internal userId
 * values to their external equivalents where necessary. Where a userId value is passed into methods in this
 * class it is always safe to pass either internal or external values.
 *
 * 63 bit userId      0x7FFFFFFFFFFFFFFFL
 *                      0111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111
 * 27 bit podId       0x7FFFFFF000000000L
 *                      0111 1111 1111 1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000
 * 36 bit user part   0x0000000FFFFFFFFFL
 *                      0000 0000 0000 0000 0000 0000 0000 1111 1111 1111 1111 1111 1111 1111 1111 1111
 * @author Bruce Skingle
 *
 */
public class LegacyIdFactory
{
  /** All bits which may be non-zero in an SBE userId */
  public static final long USER_ID_MASK       = 0x7FFFFFFFFFFFFFFFL;  // 0111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111
  
  /** Bits representing the pod part in an SBE userId */
  public static final long USER_ID_POD_MASK   = 0x7FFFFFF000000000L;  // 0111 1111 1111 1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000
  
  /** Bits representing the user part in an SBE userId */
  public static final long USER_ID_USER_MASK  = 0x0000000FFFFFFFFFL;  // 0000 0000 0000 0000 0000 0000 0000 1111 1111 1111 1111 1111 1111 1111 1111 1111
  
  /** Number of bits by which the pod part of an SBE userId are shifted */
  public static final int  USER_ID_POD_SHIFT  = 36;

  private final IUserIdFunction userIdFunction_;
  
  /**
   * Constructor.
   * 
   * @param internalPodId The pod's internal pod ID
   * @param externalPodId The pod's external pod ID.
   */
  public LegacyIdFactory(long internalPodId, long externalPodId)
  {
    /*
     * If the internal and external pod IDs are the same, which they almost always are, then there is nothing to do, so
     * we use a closure as an optimisation as it allows us to test for the common case once, here.
     */
    if(internalPodId == externalPodId)
    {
      // The internal and external IDs are the same, so this is the identity function.
      
      userIdFunction_ = (x) -> x;
    }
    else
    {
      final long internalPodIdPart = internalPodId << USER_ID_POD_SHIFT;
      final long externalPodIdPart = externalPodId << USER_ID_POD_SHIFT;
      
      // If the podId is our internal podId then replace it with our external podId.
      
      userIdFunction_ = (internalOrExternalUserId) ->
      {
        if((internalOrExternalUserId & USER_ID_POD_MASK) == internalPodIdPart)
        {
          long e = externalPodIdPart;
          
          return (internalOrExternalUserId & USER_ID_USER_MASK) | externalPodIdPart;
        }
        else
        {
          return internalOrExternalUserId;
        }
      };
    }
  }

  /**
   * Return the externalUserId for the given userId which may be internal or external.
   * 
   * @param internalOrExternalUserId A userId which may be internal or external.
   * 
   * @return The externalUserId for the given internalOrExternalUserId.
   */
  public long toExternalUserId(long internalOrExternalUserId)
  {
    return userIdFunction_.map(internalOrExternalUserId);
  }

//  /**
//   * Extract the local user part from an internal or external userId.
//   * 
//   * @param internalOrExternalUserId  An SBE user ID, it makes no difference
//   *                                  if its the internal or external version as
//   *                                  we will extract away the podId part.
//   * @return                          The local user part of the given ID.
//   */
//  public long extractUserId(long internalOrExternalUserId)
//  {
//    return internalOrExternalUserId & USER_ID_USER_MASK;
//  }
//  
//
//  /**
//   * Extract the pod part from an internal or external userId.
//   * 
//   * @param internalOrExternalUserId  An SBE user ID.
//   * @return                          The local user part of the given ID.
//   */
//  public long extractPodId(long internalOrExternalUserId)
//  {
//    return (internalOrExternalUserId & USER_ID_POD_MASK) >> USER_ID_POD_SHIFT;
//  }
//  
//  /**
//   * Extract the pod part from an internal or external userId.
//   * 
//   * @param internalOrExternalUserId  An SBE user ID.
//   * @param podId                     The podId to overlay into the internalOrExternalUserId.
//   * @return                          The local user part of the given ID.
//   */
//  public long replacePodId(long internalOrExternalUserId, long podId)
//  {
//    return (internalOrExternalUserId & USER_ID_POD_MASK) | (podId << USER_ID_POD_SHIFT);
//  }
  
  /**
   * Create a 2.0 Hash (ID) for the given userId.
   *
   * @param internalOrExternalUserId An SBE userId
   * @return            The 2.0 object ID for the mirror of the given ID.
   */
  public Hash userId(long internalOrExternalUserId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.USER_ID, toExternalUserId(internalOrExternalUserId));
  }
  
  /**
   * Create a 2.0 Hash (ID) for the given userId, as seen from another tenant.
   *
   * @param subjectTenantId    The tenant ID of the pod to which the user belongs.
   * @param subjectUserId      An internal or external userId
   * @param viewTenantId       The tenant ID of the pod from which this view is visible.
   * 
   * @return            The 2.0 object ID for the mirror of the given ID.
   */
  public Hash externalUserId(String subjectTenantId, long subjectUserId, String viewTenantId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.USER_ID, subjectTenantId, subjectUserId, viewTenantId);
  }

  /**
   * Create a 2.0 Hash (ID) for the given signal messageId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param messageId   A messageId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash signalId(String tenantId, ImmutableByteArray messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.SIGNAL_ID, tenantId, messageId);
  }

  /**
   * Create a 2.0 Hash (ID) for the given presence change message.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param messageId   A messageId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash presenceChangeId(String tenantId, ImmutableByteArray messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.PRESENCE_CHANGE_ID, tenantId, messageId);
  }

  /**
   * Create a 2.0 Hash (ID) for the given bookmark event.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param messageId   A messageId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash bookmarkId(String tenantId, ImmutableByteArray messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.BOOKMARK_ID, tenantId, messageId);
  }
  
  /**
   * Create a 2.0 Hash (ID) for the given messageId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param messageId   A messageId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash messageId(String tenantId, byte[] messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.MESSAGE_ID, tenantId, messageId);
  }
  
  /**
   * Create a 2.0 Hash (ID) for the given messageId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param messageId   A messageId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash messageId(String tenantId, ImmutableByteArray messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.MESSAGE_ID, tenantId, messageId);
  }

  /**
   * Create a 2.0 Hash (ID) for the given threadId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param threadId    A threadId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash threadId(String tenantId, byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.THREAD_ID, tenantId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the given threadId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param threadId    A threadId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash threadId(String tenantId, ImmutableByteArray threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.THREAD_ID, tenantId, threadId);
  }
  
 /**
  * Create a 2.0 Hash (ID) for an object status of the given messageId for the given tenantId.
  *
  * @param tenantId    The tenant ID of the pod where this event originated.
  * @param messageId   The id of the message read.
  * @param threadId    The threadId of the message read.
  * @return            The 2.0 object ID for the mirror of the given ID.
  * @throws NullPointerException if any parameter is null.
  */
 public Hash objectStatusId(String tenantId, ImmutableByteArray messageId, ImmutableByteArray threadId)
 {
   return HashProvider.getCompositeHashOf(LegacyId.OBJECT_STATUS_ID, tenantId, messageId, threadId);
 }

  /**
   * Create a 2.0 Hash (ID) for the read receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param internalOrExternalUserId The SBE userId of the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash readReceiptId(String tenantId, long internalOrExternalUserId, byte[] messageId, byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.READ_RECEIPT_ID, tenantId, toExternalUserId(internalOrExternalUserId), messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the read receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param internalOrExternalUserId The SBE userId of the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash readReceiptId(String tenantId, long internalOrExternalUserId, ImmutableByteArray messageId, ImmutableByteArray threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.READ_RECEIPT_ID, tenantId, toExternalUserId(internalOrExternalUserId), messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the delivery receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param internalOrExternalUserId The SBE userId of the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash deliveryReceiptId(String tenantId, long internalOrExternalUserId, byte[] messageId, byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.DELIVERY_RECEIPT_ID, tenantId, toExternalUserId(internalOrExternalUserId), messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the delivery receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param internalOrExternalUserId The SBE userId of the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash deliveryReceiptId(String tenantId, long internalOrExternalUserId, ImmutableByteArray messageId, ImmutableByteArray threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.DELIVERY_RECEIPT_ID, tenantId, toExternalUserId(internalOrExternalUserId), messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for a MaestroMessage.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param messageId         The message ID.
   * @param threadId          The thread ID.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash maestroId(String tenantId, byte[] messageId, @Nullable byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.MAESTRO_MESSAGE_ID, tenantId,
        messageId, threadId == null ? new byte[0] : threadId);
  }
  
  /**
  * Create a 2.0 Hash (ID) for a MaestroMessage.
  *
  * @param tenantId    The tenant ID of the pod where this event originated.
  * @param messageId         The message ID.
  * @param threadId          The thread ID.
  * @return            The 2.0 object ID for the mirror of the given ID.
  * @throws NullPointerException if any parameter is null.
  */
   public Hash maestroId(String tenantId, ImmutableByteArray messageId, @Nullable ImmutableByteArray threadId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.MAESTRO_MESSAGE_ID, tenantId,
         messageId, threadId == null ? new byte[0] : threadId);
   }

   /**
    * Create a 2.0 Hash (ID) for an offline notification event.
    *
    * @param tenantId    The tenant ID of the pod where this event originated.
    * This event means that a user was offline when a message was delivered and an email
    * was sent to them.
    *
    * @param toUserId          The id of the offline user.
    * @param messageId         The message ID.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash offlineNoticeId(String tenantId, long toUserId, byte[] messageId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.OFFLINE_NOTICE_ID, tenantId, toUserId, messageId);
   }

   /**
    * Create a 2.0 Hash (ID) for an offline notification event.
    *
    * @param tenantId    The tenant ID of the pod where this event originated.
    * This event means that a user was offline when a message was delivered and an email
    * was sent to them.
    *
    * @param toUserId          The id of the offline user.
    * @param messageId         The message ID.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash offlineNoticeId(String tenantId, long toUserId, ImmutableByteArray messageId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.OFFLINE_NOTICE_ID, tenantId, toUserId, messageId);
   }

   /**
    * Create a 2.0 Hash (ID) for a DeleteEvent.
    *
    * @param tenantId          The tenant ID of the pod where this event originated.
    * @param requesterId       The id of the requesting user.
    * @param deleteMessageId   The message ID of the deleted message.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash deleteEventId(String tenantId, long requesterId, byte[] deleteMessageId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.DELETE_EVENT_ID, tenantId, requesterId, deleteMessageId);
   }

   /**
    * Create a 2.0 Hash (ID) for a DeleteEvent.
    *
    * @param tenantId          The tenant ID of the pod where this event originated.
    * @param requesterId       The id of the requesting user.
    * @param deleteMessageId   The message ID of the deleted message.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash deleteEventId(String tenantId, long requesterId, ImmutableByteArray deleteMessageId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.DELETE_EVENT_ID, tenantId, requesterId, deleteMessageId);
   }

   /**
    * Create a 2.0 Hash (ID) for a DownloadAttachmentEvent.
    *
    * @param tenantId            The tenant ID of the pod where this event originated.
    * @param downloadedByUserId  The user ID of the user downloading the attachment.
    * @param messageId           The message ID of the attachment.
    * @param fileId              The file ID of the attachment.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash downloadAttachmentEventId(String tenantId, long downloadedByUserId, byte[] messageId, String fileId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.DOWNLOAD_ATTACHMENT_EVENT_ID, tenantId, downloadedByUserId, messageId, fileId);
   }

   /**
    * Create a 2.0 Hash (ID) for a DownloadAttachmentEvent.
    *
    * @param tenantId            The tenant ID of the pod where this event originated.
    * @param downloadedByUserId  The user ID of the user downloading the attachment.
    * @param messageId           The message ID of the attachment.
    * @param fileId              The file ID of the attachment.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash downloadAttachmentEventId(String tenantId, long downloadedByUserId, ImmutableByteArray messageId, String fileId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.DOWNLOAD_ATTACHMENT_EVENT_ID, tenantId, downloadedByUserId, messageId, fileId);
   }

   /**
    * Create a 2.0 Hash (ID) for a LikeEvent.
    *
    * @param tenantId            The tenant ID of the pod where this event originated.
    * @param liker               The user ID of the user sending the like.
    * @param likedMessageId      The message ID of liked message.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash likeEventId(String tenantId, long liker, byte[] likedMessageId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.LIKE_EVENT_ID, tenantId, liker, likedMessageId);
   }

   /**
    * Create a 2.0 Hash (ID) for a LikeEvent.
    *
    * @param tenantId            The tenant ID of the pod where this event originated.
    * @param liker               The user ID of the user sending the like.
    * @param likedMessageId      The message ID of liked message.
    * @return            The 2.0 object ID for the mirror of the given ID.
    * @throws NullPointerException if any parameter is null.
    */
   public Hash likeEventId(String tenantId, long liker, ImmutableByteArray likedMessageId)
   {
     return HashProvider.getCompositeHashOf(LegacyId.LIKE_EVENT_ID, tenantId, liker, likedMessageId);
   }
}
