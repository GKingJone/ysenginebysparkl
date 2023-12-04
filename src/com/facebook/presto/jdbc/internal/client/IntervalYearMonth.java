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
/*    */ public final class IntervalYearMonth
/*    */ {
/*    */   private static final String INT_MIN_VALUE = "-178956970-8";
/* 28 */   private static final Pattern FORMAT = Pattern.compile("(\\d+)-(\\d+)");
/*    */   
/*    */ 
/*    */   public static int toMonths(int year, int months)
/*    */   {
/*    */     try
/*    */     {
/* 35 */       return Math.addExact(Math.multiplyExact(year, 12), months);
/*    */     }
/*    */     catch (ArithmeticException e) {
/* 38 */       throw new IllegalArgumentException(e);
/*    */     }
/*    */   }
/*    */   
/*    */   public static String formatMonths(int months)
/*    */   {
/* 44 */     if (months == Integer.MIN_VALUE) {
/* 45 */       return "-178956970-8";
/*    */     }
/*    */     
/* 48 */     String sign = "";
/* 49 */     if (months < 0) {
/* 50 */       sign = "-";
/* 51 */       months = -months;
/*    */     }
/*    */     
/* 54 */     return String.format("%s%d-%d", new Object[] { sign, Integer.valueOf(months / 12), Integer.valueOf(months % 12) });
/*    */   }
/*    */   
/*    */   public static int parseMonths(String value)
/*    */   {
/* 59 */     if (value.equals("-178956970-8")) {
/* 60 */       return Integer.MIN_VALUE;
/*    */     }
/*    */     
/* 63 */     int signum = 1;
/* 64 */     if (value.startsWith("-")) {
/* 65 */       signum = -1;
/* 66 */       value = value.substring(1);
/*    */     }
/*    */     
/* 69 */     Matcher matcher = FORMAT.matcher(value);
/* 70 */     if (!matcher.matches()) {
/* 71 */       throw new IllegalArgumentException("Invalid year-month interval: " + value);
/*    */     }
/*    */     
/* 74 */     int years = Integer.parseInt(matcher.group(1));
/* 75 */     int months = Integer.parseInt(matcher.group(2));
/*    */     
/* 77 */     return toMonths(years, months) * signum;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\IntervalYearMonth.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */