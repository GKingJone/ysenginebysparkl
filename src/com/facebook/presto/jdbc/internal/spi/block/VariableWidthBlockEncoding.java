/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
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
/*    */ 
/*    */ public class VariableWidthBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/* 29 */   public static final BlockEncodingFactory<VariableWidthBlockEncoding> FACTORY = new VariableWidthBlockEncodingFactory();
/*    */   
/*    */   private static final String NAME = "VARIABLE_WIDTH";
/*    */   
/*    */   public String getName()
/*    */   {
/* 35 */     return "VARIABLE_WIDTH";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*    */   {
/* 42 */     AbstractVariableWidthBlock variableWidthBlock = (AbstractVariableWidthBlock)block;
/*    */     
/* 44 */     int positionCount = variableWidthBlock.getPositionCount();
/* 45 */     sliceOutput.appendInt(positionCount);
/*    */     
/*    */ 
/* 48 */     int totalLength = 0;
/* 49 */     for (int position = 0; position < positionCount; position++) {
/* 50 */       int length = variableWidthBlock.getLength(position);
/* 51 */       totalLength += length;
/* 52 */       sliceOutput.appendInt(totalLength);
/*    */     }
/*    */     
/* 55 */     EncoderUtil.encodeNullsAsBits(sliceOutput, variableWidthBlock);
/*    */     
/* 57 */     sliceOutput
/* 58 */       .appendInt(totalLength)
/* 59 */       .writeBytes(variableWidthBlock.getRawSlice(0), variableWidthBlock.getPositionOffset(0), totalLength);
/*    */   }
/*    */   
/*    */ 
/*    */   public Block readBlock(SliceInput sliceInput)
/*    */   {
/* 65 */     int positionCount = sliceInput.readInt();
/*    */     
/* 67 */     int[] offsets = new int[positionCount + 1];
/* 68 */     sliceInput.readBytes(Slices.wrappedIntArray(offsets), 4, positionCount * 4);
/*    */     
/* 70 */     boolean[] valueIsNull = EncoderUtil.decodeNullBits(sliceInput, positionCount);
/*    */     
/* 72 */     int blockSize = sliceInput.readInt();
/* 73 */     Slice slice = sliceInput.readSlice(blockSize);
/*    */     
/* 75 */     return new VariableWidthBlock(positionCount, slice, offsets, valueIsNull);
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 81 */     return FACTORY;
/*    */   }
/*    */   
/*    */ 
/*    */   public static class VariableWidthBlockEncodingFactory
/*    */     implements BlockEncodingFactory<VariableWidthBlockEncoding>
/*    */   {
/*    */     public String getName()
/*    */     {
/* 90 */       return "VARIABLE_WIDTH";
/*    */     }
/*    */     
/*    */ 
/*    */     public VariableWidthBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 96 */       return new VariableWidthBlockEncoding();
/*    */     }
/*    */     
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, VariableWidthBlockEncoding blockEncoding) {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\VariableWidthBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */