/*    */ package com.facebook.presto.jdbc.internal.joda.time;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
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
/*    */ public class IllegalInstantException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   private static final long serialVersionUID = 2858712538216L;
/*    */   
/*    */   public IllegalInstantException(String paramString)
/*    */   {
/* 53 */     super(paramString);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public IllegalInstantException(long paramLong, String paramString)
/*    */   {
/* 63 */     super(createMessage(paramLong, paramString));
/*    */   }
/*    */   
/*    */   private static String createMessage(long paramLong, String paramString) {
/* 67 */     String str1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").print(new Instant(paramLong));
/* 68 */     String str2 = paramString != null ? " (" + paramString + ")" : "";
/* 69 */     return "Illegal instant due to time zone offset transition (daylight savings time 'gap'): " + str1 + str2;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean isIllegalInstant(Throwable paramThrowable)
/*    */   {
/* 80 */     if ((paramThrowable instanceof IllegalInstantException)) {
/* 81 */       return true;
/*    */     }
/* 83 */     if ((paramThrowable.getCause() != null) && (paramThrowable.getCause() != paramThrowable)) {
/* 84 */       return isIllegalInstant(paramThrowable.getCause());
/*    */     }
/* 86 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\IllegalInstantException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */