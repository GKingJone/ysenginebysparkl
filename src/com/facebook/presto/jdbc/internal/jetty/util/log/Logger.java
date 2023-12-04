package com.facebook.presto.jdbc.internal.jetty.util.log;

public abstract interface Logger
{
  public abstract String getName();
  
  public abstract void warn(String paramString, Object... paramVarArgs);
  
  public abstract void warn(Throwable paramThrowable);
  
  public abstract void warn(String paramString, Throwable paramThrowable);
  
  public abstract void info(String paramString, Object... paramVarArgs);
  
  public abstract void info(Throwable paramThrowable);
  
  public abstract void info(String paramString, Throwable paramThrowable);
  
  public abstract boolean isDebugEnabled();
  
  public abstract void setDebugEnabled(boolean paramBoolean);
  
  public abstract void debug(String paramString, Object... paramVarArgs);
  
  public abstract void debug(String paramString, long paramLong);
  
  public abstract void debug(Throwable paramThrowable);
  
  public abstract void debug(String paramString, Throwable paramThrowable);
  
  public abstract Logger getLogger(String paramString);
  
  public abstract void ignore(Throwable paramThrowable);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */