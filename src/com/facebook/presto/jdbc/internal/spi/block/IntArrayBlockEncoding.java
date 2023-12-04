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
/*    */ public class IntArrayBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/* 26 */   public static final BlockEncodingFactory<IntArrayBlockEncoding> FACTORY = new IntArrayBlockEncodingFactory();
/*    */   
/*    */   private static final String NAME = "INT_ARRAY";
/*    */   
/*    */   public String getName()
/*    */   {
/* 32 */     return "INT_ARRAY";
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
/* 45 */         sliceOutput.writeInt(block.getInt(position, 0));
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
/* 57 */     int[] values = new int[positionCount];
/* 58 */     for (int position = 0; position < positionCount; position++) {
/* 59 */       if (valueIsNull[position] == 0) {
/* 60 */         values[position] = sliceInput.readInt();
/*    */       }
/*    */     }
/*    */     
/* 64 */     return new IntArrayBlock(positionCount, valueIsNull, values);
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 70 */     return FACTORY;
/*    */   }
/*    */   
/*    */ 
/*    */   public static class IntArrayBlockEncodingFactory
/*    */     implements BlockEncodingFactory<IntArrayBlockEncoding>
/*    */   {
/*    */     public String getName()
/*    */     {
/* 79 */       return "INT_ARRAY";
/*    */     }
/*    */     
/*    */ 
/*    */     public IntArrayBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 85 */       return new IntArrayBlockEncoding();
/*    */     }
/*    */     
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, IntArrayBlockEncoding blockEncoding) {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\IntArrayBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */