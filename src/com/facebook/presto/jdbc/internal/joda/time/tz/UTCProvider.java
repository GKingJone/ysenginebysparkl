/*    */ package com.facebook.presto.jdbc.internal.joda.time.tz;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
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
/*    */ public final class UTCProvider
/*    */   implements Provider
/*    */ {
/* 36 */   private static final Set<String> AVAILABLE_IDS = Collections.singleton("UTC");
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
/*    */   public DateTimeZone getZone(String paramString)
/*    */   {
/* 50 */     if ("UTC".equalsIgnoreCase(paramString)) {
/* 51 */       return DateTimeZone.UTC;
/*    */     }
/* 53 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Set<String> getAvailableIDs()
/*    */   {
/* 60 */     return AVAILABLE_IDS;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\UTCProvider.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */