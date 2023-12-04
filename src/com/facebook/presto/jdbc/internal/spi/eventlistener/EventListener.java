package com.facebook.presto.jdbc.internal.spi.eventlistener;

public abstract interface EventListener
{
  public void queryCreated(QueryCreatedEvent queryCreatedEvent) {}
  
  public void queryCompleted(QueryCompletedEvent queryCompletedEvent) {}
  
  public void splitCompleted(SplitCompletedEvent splitCompletedEvent) {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\EventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */