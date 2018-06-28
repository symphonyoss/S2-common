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

/**
 * Constants used for the construction of inferred Hash values to generate
 * a 2.0 ID which acts as a mirror for an object in the legacy (Symphony1.5) space.
 * 
 * Although these values are declared public callers should use an instance of
 * LegacyIdFactory to generate IDs.
 * 
 * @author Bruce Skingle
 *
 */
@SuppressWarnings("javadoc")
public class LegacyId
{
  public static final String MESSAGE_INGESTION_INFO       = "MessageIngestionInfo";
  public static final String OBJECT_STATUS_MESSAGE        = "ObjectStatusMessage";

  public static final String MESSAGE_ID                   = "MessageID";
  public static final String OBJECT_STATUS_ID             = "ObjectStatusID";
  public static final String THREAD_ID                    = "ThreadID";
  public static final String READ_RECEIPT_ID              = "ReadReceiptID";
  public static final String DELIVERY_RECEIPT_ID          = "DeliveryReceiptID";
  public static final String MAESTRO_MESSAGE_ID           = "MaestroMessageID";
  public static final String OFFLINE_NOTICE_ID            = "OfflineNoticeID";
  public static final String DELETE_EVENT_ID              = "DeleteEventID";
  public static final String DOWNLOAD_ATTACHMENT_EVENT_ID = "DownloadAttachmentEventID";
  public static final String LIKE_EVENT_ID                = "LikeEventID";
  public static final String USER_ID                      = "UserID";
  public static final String SIGNAL_ID                    = "SignalID";
}
