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
/*    */ public class ByteArrayBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/* 26 */   public static final BlockEncodingFactory<ByteArrayBlockEncoding> FACTORY = new ByteArrayBlockEncodingFactory();
/*    */   
/*    */   private static final String NAME = "BYTE_ARRAY";
/*    */   
/*    */   public String getName()
/*    */   {
/* 32 */     return "BYTE_ARRAY";
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
/* 45 */         sliceOutput.writeByte(block.getByte(position, 0));
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
/* 57 */     byte[] values = new byte[positionCount];
/* 58 */     for (int position = 0; position < positionCount; position++) {
/* 59 */       if (valueIsNull[position] == 0) {
/* 60 */         values[position] = sliceInput.readByte();
/*    */       }
/*    */     }
/*    */     
/* 64 */     return new ByteArrayBlock(positionCount, valueIsNull, values);
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 70 */     return FACTORY;
/*    */   }
/*    */   
/*    */ 
/*    */   public static class ByteArrayBlockEncodingFactory
/*    */     implements BlockEncodingFactory<ByteArrayBlockEncoding>
/*    */   {
/*    */     public String getName()
/*    */     {
/* 79 */       return "BYTE_ARRAY";
/*    */     }
/*    */     
/*    */ 
/*    */     public ByteArrayBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 85 */       return new ByteArrayBlockEncoding();
/*    */     }
/*    */     
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, ByteArrayBlockEncoding blockEncoding) {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\ByteArrayBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */