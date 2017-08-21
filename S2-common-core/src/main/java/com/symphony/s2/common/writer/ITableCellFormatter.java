/*
 * Copyright 2016-2017  Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.writer;

@FunctionalInterface
public interface ITableCellFormatter<T>
{
  String format(T value);
}
