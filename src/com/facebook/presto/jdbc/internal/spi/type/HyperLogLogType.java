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
/*    */ 
/*    */ 
/*    */ public class HyperLogLogType
/*    */   extends AbstractVariableWidthType
/*    */ {
/* 30 */   public static final HyperLogLogType HYPER_LOG_LOG = new HyperLogLogType();
/*    */   
/*    */   @JsonCreator
/*    */   public HyperLogLogType()
/*    */   {
/* 35 */     super(TypeSignature.parseTypeSignature("HyperLogLog"), Slice.class);
/*    */   }
/*    */   
/*    */ 
/*    */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*    */   {
/* 41 */     if (block.isNull(position)) {
/* 42 */       blockBuilder.appendNull();
/*    */     }
/*    */     else {
/* 45 */       block.writeBytesTo(position, 0, block.getLength(position), blockBuilder);
/* 46 */       blockBuilder.closeEntry();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public Slice getSlice(Block block, int position)
/*    */   {
/* 53 */     return block.getSlice(position, 0, block.getLength(position));
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeSlice(BlockBuilder blockBuilder, Slice value)
/*    */   {
/* 59 */     writeSlice(blockBuilder, value, 0, value.length());
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length)
/*    */   {
/* 65 */     blockBuilder.writeBytes(value, offset, length).closeEntry();
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 71 */     if (block.isNull(position)) {
/* 72 */       return null;
/*    */     }
/*    */     
/* 75 */     return new SqlVarbinary(block.getSlice(position, 0, block.getLength(position)).getBytes());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\HyperLogLogType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */