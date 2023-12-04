/*    */ package com.facebook.presto.jdbc.internal.client;
/*    */ 
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
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
/*    */ public final class IntervalDayTime
/*    */ {
/*    */   private static final long MILLIS_IN_SECOND = 1000L;
/*    */   private static final long MILLIS_IN_MINUTE = 60000L;
/*    */   private static final long MILLIS_IN_HOUR = 3600000L;
/*    */   private static final long MILLIS_IN_DAY = 86400000L;
/*    */   private static final String LONG_MIN_VALUE = "-106751991167 07:12:55.808";
/* 33 */   private static final Pattern FORMAT = Pattern.compile("(\\d+) (\\d+):(\\d+):(\\d+).(\\d+)");
/*    */   
/*    */ 
/*    */   public static long toMillis(long day, long hour, long minute, long second, long millis)
/*    */   {
/*    */     try
/*    */     {
/* 40 */       long value = millis;
/* 41 */       value = Math.addExact(value, Math.multiplyExact(day, 86400000L));
/* 42 */       value = Math.addExact(value, Math.multiplyExact(hour, 3600000L));
/* 43 */       value = Math.addExact(value, Math.multiplyExact(minute, 60000L));
/* 44 */       return Math.addExact(value, Math.multiplyExact(second, 1000L));
/*    */     }
/*    */     catch (ArithmeticException e)
/*    */     {
/* 48 */       throw new IllegalArgumentException(e);
/*    */     }
/*    */   }
/*    */   
/*    */   public static String formatMillis(long millis)
/*    */   {
/* 54 */     if (millis == Long.MIN_VALUE) {
/* 55 */       return "-106751991167 07:12:55.808";
/*    */     }
/* 57 */     String sign = "";
/* 58 */     if (millis < 0L) {
/* 59 */       sign = "-";
/* 60 */       millis = -millis;
/*    */     }
/*    */     
/* 63 */     long day = millis / 86400000L;
/* 64 */     millis %= 86400000L;
/* 65 */     long hour = millis / 3600000L;
/* 66 */     millis %= 3600000L;
/* 67 */     long minute = millis / 60000L;
/* 68 */     millis %= 60000L;
/* 69 */     long second = millis / 1000L;
/* 70 */     millis %= 1000L;
/*    */     
/* 72 */     return String.format("%s%d %02d:%02d:%02d.%03d", new Object[] { sign, Long.valueOf(day), Long.valueOf(hour), Long.valueOf(minute), Long.valueOf(second), Long.valueOf(millis) });
/*    */   }
/*    */   
/*    */   public static long parseMillis(String value)
/*    */   {
/* 77 */     if (value.equals("-106751991167 07:12:55.808")) {
/* 78 */       return Long.MIN_VALUE;
/*    */     }
/*    */     
/* 81 */     long signum = 1L;
/* 82 */     if (value.startsWith("-")) {
/* 83 */       signum = -1L;
/* 84 */       value = value.substring(1);
/*    */     }
/*    */     
/* 87 */     Matcher matcher = FORMAT.matcher(value);
/* 88 */     if (!matcher.matches()) {
/* 89 */       throw new IllegalArgumentException("Invalid day-time interval: " + value);
/*    */     }
/*    */     
/* 92 */     long days = Long.parseLong(matcher.group(1));
/* 93 */     long hours = Long.parseLong(matcher.group(2));
/* 94 */     long minutes = Long.parseLong(matcher.group(3));
/* 95 */     long seconds = Long.parseLong(matcher.group(4));
/* 96 */     long millis = Long.parseLong(matcher.group(5));
/*    */     
/* 98 */     return toMillis(days, hours, minutes, seconds, millis) * signum;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\IntervalDayTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */