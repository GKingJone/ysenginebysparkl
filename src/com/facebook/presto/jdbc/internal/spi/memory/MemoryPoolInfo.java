/*    */ package com.facebook.presto.jdbc.internal.spi.memory;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*    */ import com.facebook.presto.jdbc.internal.spi.QueryId;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public final class MemoryPoolInfo
/*    */ {
/*    */   private final long maxBytes;
/*    */   private final long freeBytes;
/*    */   private final Map<QueryId, Long> queryMemoryReservations;
/*    */   
/*    */   @JsonCreator
/*    */   public MemoryPoolInfo(@JsonProperty("maxBytes") long maxBytes, @JsonProperty("freeBytes") long freeBytes, @JsonProperty("queryMemoryReservations") Map<QueryId, Long> queryMemoryReservations)
/*    */   {
/* 35 */     this.maxBytes = maxBytes;
/* 36 */     this.freeBytes = freeBytes;
/* 37 */     this.queryMemoryReservations = Collections.unmodifiableMap(new HashMap(queryMemoryReservations));
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public long getMaxBytes()
/*    */   {
/* 43 */     return this.maxBytes;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public long getFreeBytes()
/*    */   {
/* 49 */     return this.freeBytes;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public Map<QueryId, Long> getQueryMemoryReservations()
/*    */   {
/* 55 */     return this.queryMemoryReservations;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 61 */     return String.format("maxBytes=%s,freeBytes=%s", new Object[] { Long.valueOf(this.maxBytes), Long.valueOf(this.freeBytes) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\memory\MemoryPoolInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */