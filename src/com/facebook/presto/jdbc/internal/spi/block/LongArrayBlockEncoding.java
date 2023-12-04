/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.TypeManager;
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
/*    */ public class LongArrayBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/* 26 */   public static final BlockEncodingFactory<LongArrayBlockEncoding> FACTORY = new LongArrayBlockEncodingFactory();
/*    */   
/*    */   private static final String NAME = "LONG_ARRAY";
/*    */   
/*    */   public String getName()
/*    */   {
/* 32 */     return "LONG_ARRAY";
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*    */   {
/* 38 */     int positionCount = block.getPositionCount();
/* 39 */     sliceOutput.appendInt(positionCount);
/*    */     
/* 41 */     EncoderUtil.encodeNullsAsBits(sliceOutput, block);
/*    */     
/* 43 */     for (int position = 0; position < positionCount; position++) {
/* 44 */       if (!block.isNull(position)) {
/* 45 */         sliceOutput.writeLong(block.getLong(position, 0));
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public Block readBlock(SliceInput sliceInput)
/*    */   {
/* 53 */     int positionCount = sliceInput.readInt();
/*    */     
/* 55 */     boolean[] valueIsNull = EncoderUtil.decodeNullBits(sliceInput, positionCount);
/*    */     
/* 57 */     long[] values = new long[positionCount];
/* 58 */     for (int position = 0; position < positionCount; position++) {
/* 59 */       if (valueIsNull[position] == 0) {
/* 60 */         values[position] = sliceInput.readLong();
/*    */       }
/*    */     }
/*    */     
/* 64 */     return new LongArrayBlock(positionCount, valueIsNull, values);
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 70 */     return FACTORY;
/*    */   }
/*    */   
/*    */ 
/*    */   public static class LongArrayBlockEncodingFactory
/*    */     implements BlockEncodingFactory<LongArrayBlockEncoding>
/*    */   {
/*    */     public String getName()
/*    */     {
/* 79 */       return "LONG_ARRAY";
/*    */     }
/*    */     
/*    */ 
/*    */     public LongArrayBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 85 */       return new LongArrayBlockEncoding();
/*    */     }
/*    */     
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, LongArrayBlockEncoding blockEncoding) {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\LongArrayBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */