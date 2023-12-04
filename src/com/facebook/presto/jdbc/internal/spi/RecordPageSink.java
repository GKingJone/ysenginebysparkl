/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.BigintType;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
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
/*    */ public class RecordPageSink
/*    */   implements ConnectorPageSink
/*    */ {
/*    */   private final RecordSink recordSink;
/*    */   
/*    */   public RecordPageSink(RecordSink recordSink)
/*    */   {
/* 34 */     this.recordSink = ((RecordSink)Objects.requireNonNull(recordSink, "recordSink is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Collection<Slice> finish()
/*    */   {
/* 40 */     return this.recordSink.commit();
/*    */   }
/*    */   
/*    */ 
/*    */   public void abort()
/*    */   {
/* 46 */     this.recordSink.rollback();
/*    */   }
/*    */   
/*    */ 
/*    */   public CompletableFuture<?> appendPage(Page page, Block sampleWeightBlock)
/*    */   {
/* 52 */     Block[] blocks = page.getBlocks();
/* 53 */     List<Type> columnTypes = this.recordSink.getColumnTypes();
/*    */     
/* 55 */     for (int position = 0; position < page.getPositionCount(); position++) {
/* 56 */       long sampleWeight = 1L;
/* 57 */       if (sampleWeightBlock != null) {
/* 58 */         sampleWeight = BigintType.BIGINT.getLong(sampleWeightBlock, position);
/*    */       }
/* 60 */       this.recordSink.beginRecord(sampleWeight);
/* 61 */       for (int i = 0; i < blocks.length; i++) {
/* 62 */         writeField(position, blocks[i], (Type)columnTypes.get(i));
/*    */       }
/* 64 */       this.recordSink.finishRecord();
/*    */     }
/* 66 */     return NOT_BLOCKED;
/*    */   }
/*    */   
/*    */   private void writeField(int position, Block block, Type type)
/*    */   {
/* 71 */     if (block.isNull(position)) {
/* 72 */       this.recordSink.appendNull();
/* 73 */       return;
/*    */     }
/* 75 */     if (type.getJavaType() == Boolean.TYPE) {
/* 76 */       this.recordSink.appendBoolean(type.getBoolean(block, position));
/*    */     }
/* 78 */     else if (type.getJavaType() == Long.TYPE) {
/* 79 */       this.recordSink.appendLong(type.getLong(block, position));
/*    */     }
/* 81 */     else if (type.getJavaType() == Double.TYPE) {
/* 82 */       this.recordSink.appendDouble(type.getDouble(block, position));
/*    */     }
/* 84 */     else if (type.getJavaType() == Slice.class) {
/* 85 */       this.recordSink.appendString(type.getSlice(block, position).getBytes());
/*    */     }
/*    */     else {
/* 88 */       this.recordSink.appendObject(type.getObject(block, position));
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\RecordPageSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */