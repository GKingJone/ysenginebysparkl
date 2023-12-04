package com.mchange.v2.log;

import java.util.ResourceBundle;

public abstract interface MLogger
{
  public abstract ResourceBundle getResourceBundle();
  
  public abstract String getResourceBundleName();
  
  public abstract void setFilter(Object paramObject)
    throws SecurityException;
  
  public abstract Object getFilter();
  
  public abstract void log(MLevel paramMLevel, String paramString);
  
  public abstract void log(MLevel paramMLevel, String paramString, Object paramObject);
  
  public abstract void log(MLevel paramMLevel, String paramString, Object[] paramArrayOfObject);
  
  public abstract void log(MLevel paramMLevel, String paramString, Throwable paramThrowable);
  
  public abstract void logp(MLevel paramMLevel, String paramString1, String paramString2, String paramString3);
  
  public abstract void logp(MLevel paramMLevel, String paramString1, String paramString2, String paramString3, Object paramObject);
  
  public abstract void logp(MLevel paramMLevel, String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject);
  
  public abstract void logp(MLevel paramMLevel, String paramString1, String paramString2, String paramString3, Throwable paramThrowable);
  
  public abstract void logrb(MLevel paramMLevel, String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract void logrb(MLevel paramMLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject);
  
  public abstract void logrb(MLevel paramMLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object[] paramArrayOfObject);
  
  public abstract void logrb(MLevel paramMLevel, String paramString1, String paramString2, String paramString3, String paramString4, Throwable paramThrowable);
  
  public abstract void entering(String paramString1, String paramString2);
  
  public abstract void entering(String paramString1, String paramString2, Object paramObject);
  
  public abstract void entering(String paramString1, String paramString2, Object[] paramArrayOfObject);
  
  public abstract void exiting(String paramString1, String paramString2);
  
  public abstract void exiting(String paramString1, String paramString2, Object paramObject);
  
  public abstract void throwing(String paramString1, String paramString2, Throwable paramThrowable);
  
  public abstract void severe(String paramString);
  
  public abstract void warning(String paramString);
  
  public abstract void info(String paramString);
  
  public abstract void config(String paramString);
  
  public abstract void fine(String paramString);
  
  public abstract void finer(String paramString);
  
  public abstract void finest(String paramString);
  
  public abstract void setLevel(MLevel paramMLevel)
    throws SecurityException;
  
  public abstract MLevel getLevel();
  
  public abstract boolean isLoggable(MLevel paramMLevel);
  
  public abstract String getName();
  
  public abstract void addHandler(Object paramObject)
    throws SecurityException;
  
  public abstract void removeHandler(Object paramObject)
    throws SecurityException;
  
  public abstract Object[] getHandlers();
  
  public abstract void setUseParentHandlers(boolean paramBoolean);
  
  public abstract boolean getUseParentHandlers();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\MLogger.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */