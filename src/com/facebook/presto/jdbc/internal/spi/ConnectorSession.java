/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.security.Identity;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.TimeZoneKey;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface ConnectorSession
/*    */ {
/*    */   public abstract String getQueryId();
/*    */   
/*    */   public String getUser()
/*    */   {
/* 27 */     return getIdentity().getUser();
/*    */   }
/*    */   
/*    */   public abstract Identity getIdentity();
/*    */   
/*    */   public abstract TimeZoneKey getTimeZoneKey();
/*    */   
/*    */   public abstract Locale getLocale();
/*    */   
/*    */   public abstract long getStartTime();
/*    */   
/*    */   public abstract <T> T getProperty(String paramString, Class<T> paramClass);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */