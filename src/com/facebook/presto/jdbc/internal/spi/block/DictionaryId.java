/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.atomic.AtomicLong;
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
/*    */ public class DictionaryId
/*    */ {
/* 22 */   private static final UUID nodeId = ;
/* 23 */   private static final AtomicLong sequenceGenerator = new AtomicLong();
/*    */   
/*    */   private final long mostSignificantBits;
/*    */   private final long leastSignificantBits;
/*    */   private final long sequenceId;
/*    */   
/*    */   public static DictionaryId randomDictionaryId()
/*    */   {
/* 31 */     return new DictionaryId(nodeId.getMostSignificantBits(), nodeId.getLeastSignificantBits(), sequenceGenerator.getAndIncrement());
/*    */   }
/*    */   
/*    */   public DictionaryId(long mostSignificantBits, long leastSignificantBits, long sequenceId)
/*    */   {
/* 36 */     this.mostSignificantBits = mostSignificantBits;
/* 37 */     this.leastSignificantBits = leastSignificantBits;
/* 38 */     this.sequenceId = sequenceId;
/*    */   }
/*    */   
/*    */   public long getMostSignificantBits()
/*    */   {
/* 43 */     return this.mostSignificantBits;
/*    */   }
/*    */   
/*    */   public long getLeastSignificantBits()
/*    */   {
/* 48 */     return this.leastSignificantBits;
/*    */   }
/*    */   
/*    */   public long getSequenceId()
/*    */   {
/* 53 */     return this.sequenceId;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 59 */     if (this == o) {
/* 60 */       return true;
/*    */     }
/* 62 */     if ((o == null) || (getClass() != o.getClass())) {
/* 63 */       return false;
/*    */     }
/* 65 */     DictionaryId that = (DictionaryId)o;
/* 66 */     return (this.mostSignificantBits == that.mostSignificantBits) && (this.leastSignificantBits == that.leastSignificantBits) && (this.sequenceId == that.sequenceId);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 74 */     return Objects.hash(new Object[] { Long.valueOf(this.mostSignificantBits), Long.valueOf(this.leastSignificantBits), Long.valueOf(this.sequenceId) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\DictionaryId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */