/*    */ package com.facebook.presto.jdbc.internal.joda.time;
/*    */ 
/*    */ import java.util.SimpleTimeZone;
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
/*    */ final class UTCDateTimeZone
/*    */   extends DateTimeZone
/*    */ {
/* 26 */   static final DateTimeZone INSTANCE = new UTCDateTimeZone();
/*    */   private static final long serialVersionUID = -3513011772763289092L;
/*    */   
/*    */   public UTCDateTimeZone() {
/* 30 */     super("UTC");
/*    */   }
/*    */   
/*    */   public String getNameKey(long paramLong)
/*    */   {
/* 35 */     return "UTC";
/*    */   }
/*    */   
/*    */   public int getOffset(long paramLong)
/*    */   {
/* 40 */     return 0;
/*    */   }
/*    */   
/*    */   public int getStandardOffset(long paramLong)
/*    */   {
/* 45 */     return 0;
/*    */   }
/*    */   
/*    */   public int getOffsetFromLocal(long paramLong)
/*    */   {
/* 50 */     return 0;
/*    */   }
/*    */   
/*    */   public boolean isFixed()
/*    */   {
/* 55 */     return true;
/*    */   }
/*    */   
/*    */   public long nextTransition(long paramLong)
/*    */   {
/* 60 */     return paramLong;
/*    */   }
/*    */   
/*    */   public long previousTransition(long paramLong)
/*    */   {
/* 65 */     return paramLong;
/*    */   }
/*    */   
/*    */   public TimeZone toTimeZone()
/*    */   {
/* 70 */     return new SimpleTimeZone(0, getID());
/*    */   }
/*    */   
/*    */   public boolean equals(Object paramObject)
/*    */   {
/* 75 */     return paramObject instanceof UTCDateTimeZone;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 80 */     return getID().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\UTCDateTimeZone.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */