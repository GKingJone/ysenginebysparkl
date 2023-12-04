/*    */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Instant;
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
/*    */ class GJCacheKey
/*    */ {
/*    */   private final DateTimeZone zone;
/*    */   private final Instant cutoverInstant;
/*    */   private final int minDaysInFirstWeek;
/*    */   
/*    */   GJCacheKey(DateTimeZone paramDateTimeZone, Instant paramInstant, int paramInt)
/*    */   {
/* 30 */     this.zone = paramDateTimeZone;
/* 31 */     this.cutoverInstant = paramInstant;
/* 32 */     this.minDaysInFirstWeek = paramInt;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 38 */     int i = 1;
/* 39 */     i = 31 * i + (this.cutoverInstant == null ? 0 : this.cutoverInstant.hashCode());
/* 40 */     i = 31 * i + this.minDaysInFirstWeek;
/* 41 */     i = 31 * i + (this.zone == null ? 0 : this.zone.hashCode());
/* 42 */     return i;
/*    */   }
/*    */   
/*    */   public boolean equals(Object paramObject)
/*    */   {
/* 47 */     if (this == paramObject) {
/* 48 */       return true;
/*    */     }
/* 50 */     if (paramObject == null) {
/* 51 */       return false;
/*    */     }
/* 53 */     if (!(paramObject instanceof GJCacheKey)) {
/* 54 */       return false;
/*    */     }
/* 56 */     GJCacheKey localGJCacheKey = (GJCacheKey)paramObject;
/* 57 */     if (this.cutoverInstant == null) {
/* 58 */       if (localGJCacheKey.cutoverInstant != null) {
/* 59 */         return false;
/*    */       }
/* 61 */     } else if (!this.cutoverInstant.equals(localGJCacheKey.cutoverInstant)) {
/* 62 */       return false;
/*    */     }
/* 64 */     if (this.minDaysInFirstWeek != localGJCacheKey.minDaysInFirstWeek) {
/* 65 */       return false;
/*    */     }
/* 67 */     if (this.zone == null) {
/* 68 */       if (localGJCacheKey.zone != null) {
/* 69 */         return false;
/*    */       }
/* 71 */     } else if (!this.zone.equals(localGJCacheKey.zone)) {
/* 72 */       return false;
/*    */     }
/* 74 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\GJCacheKey.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */