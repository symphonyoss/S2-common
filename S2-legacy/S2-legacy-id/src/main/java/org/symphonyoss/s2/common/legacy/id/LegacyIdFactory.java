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

/**
 * A factory to create inferred Hash values each of which is
 * a 2.0 ID which acts as a mirror for an object in the legacy (Symphony1.5) space.
 * 
 * Many methods take a podId which it uses to generate a globally unique ID for each
 * object across all pods. This means that if the same threadId happens to exist in 2
 * separate pods and we create a 2.0 Sequence to represent those two threads that the
 * 2.0 IDs will not collide in a multi tenant environment.
 * 
 * Note that where we refer to userId in this class that we expect the "external" userId
 * which already includes the podId and is already globally unique. In this case the 
 * podId is not included again.
 * 
 * @author Bruce Skingle
 *
 */
public class LegacyIdFactory
{
  /**
   * Create a 2.0 Hash (ID) for the given messageId.
   * 
   * @param messageId   A messageId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash messageId(byte[] messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.MESSAGE_ID, messageId);
  }
  
  /**
   * Create a 2.0 Hash (ID) for the given threadId.
   * 
   * @param threadId    A threadId
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash threadId(byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.THREAD_ID, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the read receipt of the given messageId for the given userId.
   * 
   * @param userId      The external (globally unique) user ID or the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash readReceiptId(long userId, byte[] messageId, byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.READ_RECEIPT_ID, userId, messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for the delivery receipt of the given messageId for the given userId.
   * 
   * @param userId      The external (globally unique) user ID or the user who read the message.
   * @param messageId   The id of the message read.
   * @param threadId    The threadId of the message read.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash deliveryReceiptId(long userId, byte[] messageId, byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.DELIVERY_RECEIPT_ID, userId, messageId, threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for a MaestroMessage.
   * 
   * @param payloadClassName  The type of the MaestroPayload.
   * @param fromPod           The originating pod id.
   * @param messageId         The message ID.
   * @param threadId          The thread ID.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash maestroId(String typeName, Integer fromPod, byte[] messageId, @Nullable byte[] threadId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.MAESTRO_MESSAGE_ID, typeName,
        fromPod == null ? 0 : fromPod,
        messageId, threadId == null ? new byte[0] : threadId);
  }

  /**
   * Create a 2.0 Hash (ID) for an offline notification event.
   * 
   * This event means that a user was offline when a message was delivered and an email
   * was sent to them.
   * 
   * @param toUserId          The id of the offline user.
   * @param messageId         The message ID.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash offlineNoticeId(long toUserId, byte[] messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.OFFLINE_NOTICE_ID, toUserId, messageId);
  }

  /**
   * Create a 2.0 Hash (ID) for a DeleteEvent.
   * 
   * @param requesterId       The id of the requesting user.
   * @param deleteMessageId   The message ID of the deleted message.
   * @param messageId         The message ID of the event.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash deleteEventId(long requesterId, byte[] deleteMessageId, byte[] messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.DELETE_EVENT_ID, requesterId, deleteMessageId, messageId);
  }

  /**
   * Create a 2.0 Hash (ID) for a DownloadAttachmentEvent.
   * 
   * @param downloadedByUserId  The user ID of the user downloading the attachment.
   * @param messageId           The message ID of the attachment.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash downloadAttachmentEventId(long downloadedByUserId, byte[] messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.DOWNLOAD_ATTACHMENT_EVENT_ID, downloadedByUserId, messageId);
  }

  /**
   * Create a 2.0 Hash (ID) for a LikeEvent.
   * 
   * @param liker               The user ID of the user sending the like.
   * @param likedMessageId      The message ID of liked message.
   * @param messageId           The message ID of the event.
   * @return            The 2.0 object ID for the mirror of the given ID.
   * @throws NullPointerException if any parameter is null.
   */
  public Hash likeEventId(long liker, byte[] likedMessageId, byte[] messageId)
  {
    return HashProvider.getCompositeHashOf(LegacyId.LIKE_EVENT_ID, liker, likedMessageId, messageId);
  }
}
