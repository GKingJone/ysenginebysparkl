/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*    */ import java.util.Collection;
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
/*    */ public abstract interface ConnectorPageSink
/*    */ {
/* 24 */   public static final CompletableFuture<?> NOT_BLOCKED = CompletableFuture.completedFuture(null);
/*    */   
/*    */   public abstract CompletableFuture<?> appendPage(Page paramPage, Block paramBlock);
/*    */   
/*    */   public abstract Collection<Slice> finish();
/*    */   
/*    */   public abstract void abort();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorPageSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */