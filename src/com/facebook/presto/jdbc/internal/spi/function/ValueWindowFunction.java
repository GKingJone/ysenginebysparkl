/*    */ package com.facebook.presto.jdbc.internal.spi.function;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
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
/*    */ public abstract class ValueWindowFunction
/*    */   implements WindowFunction
/*    */ {
/*    */   protected WindowIndex windowIndex;
/*    */   private int currentPosition;
/*    */   
/*    */   public final void reset(WindowIndex windowIndex)
/*    */   {
/* 28 */     this.windowIndex = windowIndex;
/* 29 */     this.currentPosition = 0;
/*    */     
/* 31 */     reset();
/*    */   }
/*    */   
/*    */ 
/*    */   public final void processRow(BlockBuilder output, int peerGroupStart, int peerGroupEnd, int frameStart, int frameEnd)
/*    */   {
/* 37 */     processRow(output, frameStart, frameEnd, this.currentPosition);
/*    */     
/* 39 */     this.currentPosition += 1;
/*    */   }
/*    */   
/*    */   public void reset() {}
/*    */   
/*    */   public abstract void processRow(BlockBuilder paramBlockBuilder, int paramInt1, int paramInt2, int paramInt3);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\ValueWindowFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */