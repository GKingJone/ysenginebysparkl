/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import java.time.Instant;
/*    */ import java.time.ZoneId;
/*    */ import java.time.ZonedDateTime;
/*    */ import java.time.format.DateTimeFormatter;
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
/*    */ public final class SqlTimestamp
/*    */ {
/* 25 */   private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");
/*    */   
/*    */   private final long millisUtc;
/*    */   private final TimeZoneKey sessionTimeZoneKey;
/*    */   
/*    */   public SqlTimestamp(long millisUtc, TimeZoneKey sessionTimeZoneKey)
/*    */   {
/* 32 */     this.millisUtc = millisUtc;
/* 33 */     this.sessionTimeZoneKey = sessionTimeZoneKey;
/*    */   }
/*    */   
/*    */   public long getMillisUtc()
/*    */   {
/* 38 */     return this.millisUtc;
/*    */   }
/*    */   
/*    */   public TimeZoneKey getSessionTimeZoneKey()
/*    */   {
/* 43 */     return this.sessionTimeZoneKey;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 49 */     return Objects.hash(new Object[] { Long.valueOf(this.millisUtc), this.sessionTimeZoneKey });
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 55 */     if (this == obj) {
/* 56 */       return true;
/*    */     }
/* 58 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 59 */       return false;
/*    */     }
/* 61 */     SqlTimestamp other = (SqlTimestamp)obj;
/* 62 */     return (Objects.equals(Long.valueOf(this.millisUtc), Long.valueOf(other.millisUtc))) && 
/* 63 */       (Objects.equals(this.sessionTimeZoneKey, other.sessionTimeZoneKey));
/*    */   }
/*    */   
/*    */ 
/*    */   @JsonValue
/*    */   public String toString()
/*    */   {
/* 70 */     return Instant.ofEpochMilli(this.millisUtc).atZone(ZoneId.of(this.sessionTimeZoneKey.getId())).format(formatter);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\SqlTimestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */