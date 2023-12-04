package com.facebook.presto.jdbc.internal.jetty.util.component;

import java.util.Collection;

public abstract interface Container
{
  public abstract boolean addBean(Object paramObject);
  
  public abstract Collection<Object> getBeans();
  
  public abstract <T> Collection<T> getBeans(Class<T> paramClass);
  
  public abstract <T> T getBean(Class<T> paramClass);
  
  public abstract boolean removeBean(Object paramObject);
  
  public abstract void addEventListener(Listener paramListener);
  
  public abstract void removeEventListener(Listener paramListener);
  
  public static abstract interface InheritedListener
    extends Listener
  {}
  
  public static abstract interface Listener
  {
    public abstract void beanAdded(Container paramContainer, Object paramObject);
    
    public abstract void beanRemoved(Container paramContainer, Object paramObject);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\Container.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */