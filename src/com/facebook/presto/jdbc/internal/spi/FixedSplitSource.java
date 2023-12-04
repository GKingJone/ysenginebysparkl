/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ public class FixedSplitSource
/*    */   implements ConnectorSplitSource
/*    */ {
/*    */   private final List<ConnectorSplit> splits;
/*    */   private int offset;
/*    */   
/*    */   public FixedSplitSource(Iterable<? extends ConnectorSplit> splits)
/*    */   {
/* 31 */     if (splits == null) {
/* 32 */       throw new NullPointerException("splits is null");
/*    */     }
/* 34 */     List<ConnectorSplit> splitsList = new ArrayList();
/* 35 */     for (ConnectorSplit split : splits) {
/* 36 */       splitsList.add(split);
/*    */     }
/* 38 */     this.splits = Collections.unmodifiableList(splitsList);
/*    */   }
/*    */   
/*    */ 
/*    */   public CompletableFuture<List<ConnectorSplit>> getNextBatch(int maxSize)
/*    */   {
/* 44 */     int remainingSplits = this.splits.size() - this.offset;
/* 45 */     int size = Math.min(remainingSplits, maxSize);
/* 46 */     List<ConnectorSplit> results = this.splits.subList(this.offset, this.offset + size);
/* 47 */     this.offset += size;
/*    */     
/* 49 */     return CompletableFuture.completedFuture(results);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isFinished()
/*    */   {
/* 55 */     return this.offset >= this.splits.size();
/*    */   }
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\FixedSplitSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */