/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package org.symphonyoss.s2.common.legacy.id;

/**
 * A function to map user IDs from internal to external.
 * 
 * @author Bruce Skingle
 *
 */
@FunctionalInterface
public interface IUserIdFunction
{
  /**
   * Map the given internal or external user ID to its external form.
   * 
   * @param userId A user ID
   * @return The external form of that user ID.
   */
  long map(long userId);
}
