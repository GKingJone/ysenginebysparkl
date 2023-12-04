package com.mysql.jdbc.profiler;

import com.mysql.jdbc.Extension;

public abstract interface ProfilerEventHandler
  extends Extension
{
  public abstract void consumeEvent(ProfilerEvent paramProfilerEvent);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\profiler\ProfilerEventHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */