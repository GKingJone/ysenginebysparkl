/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
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
/*    */ 
/*    */ public class P4HyperLogLogType
/*    */   extends AbstractVariableWidthType
/*    */ {
/* 28 */   public static final P4HyperLogLogType P4_HYPER_LOG_LOG = new P4HyperLogLogType();
/*    */   
/*    */   @JsonCreator
/*    */   public P4HyperLogLogType()
/*    */   {
/* 33 */     super(TypeSignature.parseTypeSignature("P4HyperLogLog"), Slice.class);
/*    */   }
/*    */   
/*    */ 
/*    */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*    */   {
/* 39 */     HyperLogLogType.HYPER_LOG_LOG.appendTo(block, position, blockBuilder);
/*    */   }
/*    */   
/*    */ 
/*    */   public Slice getSlice(Block block, int position)
/*    */   {
/* 45 */     return HyperLogLogType.HYPER_LOG_LOG.getSlice(block, position);
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeSlice(BlockBuilder blockBuilder, Slice value)
/*    */   {
/* 51 */     HyperLogLogType.HYPER_LOG_LOG.writeSlice(blockBuilder, value);
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length)
/*    */   {
/* 57 */     HyperLogLogType.HYPER_LOG_LOG.writeSlice(blockBuilder, value, offset, length);
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 63 */     return HyperLogLogType.HYPER_LOG_LOG.getObjectValue(session, block, position);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\P4HyperLogLogType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */