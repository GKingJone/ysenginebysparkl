/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import java.time.Instant;
/*    */ import java.time.ZoneId;
/*    */ import java.time.ZonedDateTime;
/*    */ import java.time.format.DateTimeFormatter;
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
/*    */ public final class SqlTimeWithTimeZone
/*    */ {
/* 29 */   private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS VV");
/*    */   
/*    */   private final long millisUtc;
/*    */   private final TimeZoneKey timeZoneKey;
/*    */   
/*    */   public SqlTimeWithTimeZone(long timeWithTimeZone)
/*    */   {
/* 36 */     this.millisUtc = DateTimeEncoding.unpackMillisUtc(timeWithTimeZone);
/* 37 */     this.timeZoneKey = DateTimeEncoding.unpackZoneKey(timeWithTimeZone);
/*    */   }
/*    */   
/*    */   public SqlTimeWithTimeZone(long millisUtc, TimeZoneKey timeZoneKey)
/*    */   {
/* 42 */     this.millisUtc = millisUtc;
/* 43 */     this.timeZoneKey = timeZoneKey;
/*    */   }
/*    */   
/*    */   public SqlTimeWithTimeZone(long millisUtc, TimeZone timeZone)
/*    */   {
/* 48 */     this.millisUtc = millisUtc;
/* 49 */     this.timeZoneKey = TimeZoneKey.getTimeZoneKey(timeZone.getID());
/*    */   }
/*    */   
/*    */   public long getMillisUtc()
/*    */   {
/* 54 */     return this.millisUtc;
/*    */   }
/*    */   
/*    */   public TimeZoneKey getTimeZoneKey()
/*    */   {
/* 59 */     return this.timeZoneKey;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 65 */     return Objects.hash(new Object[] { Long.valueOf(this.millisUtc), this.timeZoneKey });
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 71 */     if (this == obj) {
/* 72 */       return true;
/*    */     }
/* 74 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 75 */       return false;
/*    */     }
/* 77 */     SqlTimeWithTimeZone other = (SqlTimeWithTimeZone)obj;
/* 78 */     return (Objects.equals(Long.valueOf(this.millisUtc), Long.valueOf(other.millisUtc))) && 
/* 79 */       (Objects.equals(this.timeZoneKey, other.timeZoneKey));
/*    */   }
/*    */   
/*    */ 
/*    */   @JsonValue
/*    */   public String toString()
/*    */   {
/* 86 */     return Instant.ofEpochMilli(this.millisUtc).atZone(ZoneId.of(this.timeZoneKey.getId())).format(formatter);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\SqlTimeWithTimeZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */