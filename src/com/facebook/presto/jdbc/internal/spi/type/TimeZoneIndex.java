/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.TimeZone;
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
/*    */ public final class TimeZoneIndex
/*    */ {
/* 30 */   private static final TimeZone[] TIME_ZONES = new TimeZone[TimeZoneKey.MAX_TIME_ZONE_KEY + 1];
/* 31 */   static { for (TimeZoneKey timeZoneKey : TimeZoneKey.getTimeZoneKeys()) {
/* 32 */       String zoneId = timeZoneKey.getId();
/*    */       TimeZone timeZone;
/*    */       TimeZone timeZone;
/* 35 */       if ((zoneId.charAt(0) == '-') || (zoneId.charAt(0) == '+')) {
/* 36 */         timeZone = TimeZone.getTimeZone("GMT" + zoneId);
/*    */       }
/*    */       else {
/* 39 */         timeZone = TimeZone.getTimeZone(zoneId);
/*    */       }
/* 41 */       TIME_ZONES[timeZoneKey.getKey()] = timeZone;
/*    */     }
/*    */   }
/*    */   
/*    */   public static TimeZone getTimeZoneForKey(TimeZoneKey timeZoneKey)
/*    */   {
/* 47 */     Objects.requireNonNull(timeZoneKey, "timeZoneKey is null");
/* 48 */     return (TimeZone)TIME_ZONES[timeZoneKey.getKey()].clone();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TimeZoneIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */