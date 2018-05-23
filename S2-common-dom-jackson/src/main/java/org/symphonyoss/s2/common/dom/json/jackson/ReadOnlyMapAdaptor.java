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

package org.symphonyoss.s2.common.dom.json.jackson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A read only implementation of Map&lt;String, String&gt; for a JSON Object Node.
 * 
 * @author Bruce Skingle
 *
 */
public class ReadOnlyMapAdaptor implements Map<String, String>
{
  private static final String            READ_ONLY = "This is a read-only Map";
  private static final ObjectMapper      MAPPER    = new ObjectMapper();

  private ObjectNode                     objectNode_;
  private HashSet<Entry<String, String>> entrySet_;
  private HashSet<String>                keySet_;
  private List<String>                   values_;

  /**
   * Constructor.
   * 
   * @param objectNode An object node for which a Map view is required.
   */
  public ReadOnlyMapAdaptor(@Nullable ObjectNode objectNode)
  {
    objectNode_ = objectNode;
  }
  
  @Override
  public void clear()
  {
    throw new UnsupportedOperationException(READ_ONLY);
  }

  @Override
  public boolean containsKey(Object key)
  {
    return get(key) != null;
  }

  @Override
  public boolean containsValue(Object value)
  {
    if(objectNode_ == null)
      return false;
    
    if(value instanceof String)
    {
      for(String v : values())
      {
        if(v.equals(value))
          return true;
      }
    }
    return false;
  }

  @Override
  public synchronized Set<Entry<String, String>> entrySet()
  {
    if(entrySet_ == null)
    {
      createEntrySet();
    }
    return entrySet_;
  }

  private void createEntrySet()
  {
    entrySet_ = new HashSet<>();
    keySet_ = new HashSet<>();
    values_ = new ArrayList<>(size());
    
    if(objectNode_ != null)
    {
      Iterator<String> it = objectNode_.fieldNames();
      
      while(it.hasNext())
      {
        String name = it.next();
        String value = get(name);
        
        entrySet_.add(new AdaptorEntry(name, value));
        keySet_.add(name);
        values_.add(value);
      }
    }
  }
  
  private class AdaptorEntry implements Entry<String, String>
  {
    private final String key_;
    private String value_;
    
    private AdaptorEntry(String key, String value)
    {
      key_ = key;
      value_ = value;
    }

    @Override
    public String getValue()
    {
      return value_;
    }
    
    @Override
    public String setValue(String value)
    {
      String oldValue = value_;
      value_ = value;
      
      return oldValue;
    }
    
    @Override
    public String getKey()
    {
      return key_;
    }
  }

  @Override
  public String get(Object key)
  {
    if(objectNode_ != null && key instanceof String)
    {
      JsonNode node = objectNode_.get((String)key);
      
      if(node == null)
        return null;
      
      if(node.isValueNode())
        return node.asText();
      
      try
      {
        return MAPPER.writeValueAsString(node);
      }
      catch (JsonProcessingException e)
      {
        throw new IllegalStateException("Invalid JSON exception", e);
      }
    }
    
    return null;
  }

  @Override
  public boolean isEmpty()
  {
    if(objectNode_ == null)
      return true;
    
    return objectNode_.size() == 0;
  }

  @Override
  public Set<String> keySet()
  {
    if(keySet_ == null)
      createEntrySet();
    
    return keySet_;
  }

  @Override
  public String put(String arg0, String arg1)
  {
    throw new UnsupportedOperationException(READ_ONLY);
  }

  @Override
  public void putAll(Map<? extends String, ? extends String> arg0)
  {
    throw new UnsupportedOperationException(READ_ONLY);
  }

  @Override
  public String remove(Object arg0)
  {
    throw new UnsupportedOperationException(READ_ONLY);
  }

  @Override
  public int size()
  {
    if(objectNode_ == null)
      return 0;
    
    return objectNode_.size();
  }

  @Override
  public synchronized Collection<String> values()
  {
    if(values_ == null)
      createEntrySet();
    
    return values_;
  }

}
