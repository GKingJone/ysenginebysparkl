/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ public final class DateTimeEncoding
/*    */ {
/*    */   private static final int TIME_ZONE_MASK = 4095;
/*    */   private static final int MILLIS_SHIFT = 12;
/*    */   
/*    */   private static long pack(long millisUtc, short timeZoneKey)
/*    */   {
/* 31 */     return millisUtc << 12 | timeZoneKey & 0xFFF;
/*    */   }
/*    */   
/*    */   public static long packDateTimeWithZone(long millisUtc, String zoneId)
/*    */   {
/* 36 */     return packDateTimeWithZone(millisUtc, TimeZoneKey.getTimeZoneKey(zoneId));
/*    */   }
/*    */   
/*    */   public static long packDateTimeWithZone(long millisUtc, int offsetMinutes)
/*    */   {
/* 41 */     return packDateTimeWithZone(millisUtc, TimeZoneKey.getTimeZoneKeyForOffset(offsetMinutes));
/*    */   }
/*    */   
/*    */   public static long packDateTimeWithZone(long millisUtc, TimeZoneKey timeZoneKey)
/*    */   {
/* 46 */     Objects.requireNonNull(timeZoneKey, "timeZoneKey is null");
/* 47 */     return pack(millisUtc, timeZoneKey.getKey());
/*    */   }
/*    */   
/*    */   public static long unpackMillisUtc(long dateTimeWithTimeZone)
/*    */   {
/* 52 */     return dateTimeWithTimeZone >> 12;
/*    */   }
/*    */   
/*    */   public static TimeZoneKey unpackZoneKey(long dateTimeWithTimeZone)
/*    */   {
/* 57 */     return TimeZoneKey.getTimeZoneKey((short)(int)(dateTimeWithTimeZone & 0xFFF));
/*    */   }
/*    */   
/*    */   public static long updateMillisUtc(long newMillsUtc, long dateTimeWithTimeZone)
/*    */   {
/* 62 */     return pack(newMillsUtc, (short)(int)(dateTimeWithTimeZone & 0xFFF));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\DateTimeEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */