/*    */ package com.facebook.presto.jdbc;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.client.IntervalDayTime;
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
/*    */ public class PrestoIntervalDayTime
/*    */ {
/*    */   private final long milliSeconds;
/*    */   
/*    */   public PrestoIntervalDayTime(long milliSeconds)
/*    */   {
/* 27 */     this.milliSeconds = milliSeconds;
/*    */   }
/*    */   
/*    */   public PrestoIntervalDayTime(int day, int hour, int minute, int second, int millis)
/*    */   {
/* 32 */     this.milliSeconds = IntervalDayTime.toMillis(day, hour, minute, second, millis);
/*    */   }
/*    */   
/*    */   public long getMilliSeconds()
/*    */   {
/* 37 */     return this.milliSeconds;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 43 */     return Objects.hash(new Object[] { Long.valueOf(this.milliSeconds) });
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 49 */     if (this == obj) {
/* 50 */       return true;
/*    */     }
/* 52 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 53 */       return false;
/*    */     }
/* 55 */     PrestoIntervalDayTime other = (PrestoIntervalDayTime)obj;
/* 56 */     return Objects.equals(Long.valueOf(this.milliSeconds), Long.valueOf(other.milliSeconds));
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     return IntervalDayTime.formatMillis(this.milliSeconds);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoIntervalDayTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */