package org.symphonyoss.s2.common.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 * A simple named thread factory.
 * 
 * @author Bruce Skingle
 *
 */
public class NamedThreadFactory implements ThreadFactory
{
  private int id_ = 0;
  private final String name_;
  private boolean daemon_;

  /**
   * CReate a factory for non-deaemon threads with the given name.
   * 
   * @param name The name prefix for threads created by this factory. An integer will be appended to this name
   */
  public NamedThreadFactory(String name)
  {
    this(name, true);
  }
  
  /**
   * CReate a factory for nthreads with the given name.
   * 
   * @param name The name prefix for threads created by this factory. An integer will be appended to this name
   * @param daemon If true then create daemon threads.
   */
  public NamedThreadFactory(String name, boolean daemon)
  {
    name_ = name;
    daemon_ = daemon;
  }

  @Override
  public Thread newThread(Runnable target)
  {
    id_++;
    Thread t = new Thread((ThreadGroup)null, target, name_ + "-" + id_);
    
    if(daemon_)
      t.setDaemon(true);
    
    return t;
  }
}
