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
 * Note that where we refer to userId in this class that we expect the "external" userId
 * which already includes the tenantId and is already globally unique. In this case the
 * tenantId is not included again.
 *
 * @author Bruce Skingle
 *
 */
public class LegacyIdFactory
{
  /**
   * Create a 2.0 Hash (ID) for the given userId.
   *
   * @param userId      An external userId
   * @return            The 2.0 object ID for the mirror of the given ID.
   */
  public Hash userId(long userId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.USER_ID, userId);
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
   * Create a 2.0 Hash (ID) for the read receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param userId      The external (globally unique) user ID or the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash readReceiptId(String tenantId, long userId, byte[] messageId, byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.READ_RECEIPT_ID, tenantId, userId, messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the read receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param userId      The external (globally unique) user ID or the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash readReceiptId(String tenantId, long userId, ImmutableByteArray messageId, ImmutableByteArray threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.READ_RECEIPT_ID, tenantId, userId, messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the delivery receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param userId      The external (globally unique) user ID or the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash deliveryReceiptId(String tenantId, long userId, byte[] messageId, byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.DELIVERY_RECEIPT_ID, tenantId, userId, messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the delivery receipt of the given messageId for the given userId.
   *
   * @param tenantId    The tenant ID of the pod where this event originated.
   * @param userId      The external (globally unique) user ID or the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash deliveryReceiptId(String tenantId, long userId, ImmutableByteArray messageId, ImmutableByteArray threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.DELIVERY_RECEIPT_ID, tenantId, userId, messageId, threadId);
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
