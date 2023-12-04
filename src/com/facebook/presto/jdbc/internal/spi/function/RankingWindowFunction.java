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
/*    */ public abstract class RankingWindowFunction
/*    */   implements WindowFunction
/*    */ {
/*    */   protected WindowIndex windowIndex;
/*    */   private int currentPeerGroupStart;
/*    */   private int currentPosition;
/*    */   
/*    */   public final void reset(WindowIndex windowIndex)
/*    */   {
/* 29 */     this.windowIndex = windowIndex;
/* 30 */     this.currentPeerGroupStart = -1;
/* 31 */     this.currentPosition = 0;
/*    */     
/* 33 */     reset();
/*    */   }
/*    */   
/*    */ 
/*    */   public final void processRow(BlockBuilder output, int peerGroupStart, int peerGroupEnd, int frameStart, int frameEnd)
/*    */   {
/* 39 */     boolean newPeerGroup = false;
/* 40 */     if (peerGroupStart != this.currentPeerGroupStart) {
/* 41 */       this.currentPeerGroupStart = peerGroupStart;
/* 42 */       newPeerGroup = true;
/*    */     }
/*    */     
/* 45 */     int peerGroupCount = peerGroupEnd - peerGroupStart + 1;
/*    */     
/* 47 */     processRow(output, newPeerGroup, peerGroupCount, this.currentPosition);
/*    */     
/* 49 */     this.currentPosition += 1;
/*    */   }
/*    */   
/*    */   public void reset() {}
/*    */   
/*    */   public abstract void processRow(BlockBuilder paramBlockBuilder, boolean paramBoolean, int paramInt1, int paramInt2);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\RankingWindowFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */