/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.i18n;

import java.util.Locale;

import com.symphony.s2.common.exception.BadFormatException;
import com.symphony.s2.common.fault.TransactionFault;

public class Language
{
  public static final Language SYSTEM = newInstance("en");
  
  private final String languageTag_;
  private final String language_;
  private final String region_;
  private final String variant_;
  private final Locale locale_;
  
  public Language(String languageTag) throws BadFormatException
  {
    String[] parts = languageTag.split("-");
    
    if(parts.length > 3)
      throw new BadFormatException("Language tag is of format language-region-variant e.g. en-GB");
    
    languageTag_ = languageTag;
    language_ = parts[0];
    
    if(parts.length > 2)
    {
      region_ = parts[1];
      variant_ = parts[2];
      locale_ = new Locale(language_, region_, variant_);
    }
    else if(parts.length > 1)
    {
      region_ = parts[1];
      variant_ = "";
      locale_ = new Locale(language_, region_);
    }
    else
    {
      region_ = "";
      variant_ = "";
      locale_ = new Locale(language_);
    }
  }
  
  public Language(String language, String region)
  {
    language_ = language;
    region_ = region;
    variant_ = "";
    languageTag_ = language_ + "-" + region_;
    locale_ = new Locale(language_, region_);
  }
  
  public Language(String language, String region, String variant)
  {
    language_ = language;
    region_ = region;
    variant_ = variant;
    languageTag_ = language_ + "-" + region_ + "-" + variant_;
    locale_ = new Locale(language_, region_, variant_);
  }

  public String getLanguage()
  {
    return language_;
  }

  public String getRegion()
  {
    return region_;
  }

  public String getVariant()
  {
    return variant_;
  }

  public Locale getLocale()
  {
    return locale_;
  }

  public static Language newInstance(String languageTag)
  {
    try
    {
      return new Language(languageTag);
    }
    catch (BadFormatException e)
    {
      throw new TransactionFault(e);
    }
  }

  public String asString()
  {
    return languageTag_;
  }
  
  @Override
  public String toString()
  {
    return languageTag_;
  }
}
