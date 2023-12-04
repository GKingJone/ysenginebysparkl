/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
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
/*    */ public class ArrayBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/* 24 */   public static final BlockEncodingFactory<ArrayBlockEncoding> FACTORY = new ArrayBlockEncodingFactory();
/*    */   
/*    */   private static final String NAME = "ARRAY";
/*    */   private final BlockEncoding valueBlockEncoding;
/*    */   
/*    */   public ArrayBlockEncoding(BlockEncoding valueBlockEncoding)
/*    */   {
/* 31 */     this.valueBlockEncoding = valueBlockEncoding;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 37 */     return "ARRAY";
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*    */   {
/* 43 */     AbstractArrayBlock arrayBlock = (AbstractArrayBlock)block;
/*    */     
/* 45 */     this.valueBlockEncoding.writeBlock(sliceOutput, arrayBlock.getValues());
/* 46 */     sliceOutput.appendInt(arrayBlock.getOffsetBase());
/* 47 */     int positionCount = arrayBlock.getPositionCount();
/* 48 */     sliceOutput.appendInt(positionCount);
/* 49 */     sliceOutput.writeBytes(arrayBlock.getOffsets(), 0, positionCount * 4);
/* 50 */     EncoderUtil.encodeNullsAsBits(sliceOutput, block);
/*    */   }
/*    */   
/*    */ 
/*    */   public Block readBlock(SliceInput sliceInput)
/*    */   {
/* 56 */     Block values = this.valueBlockEncoding.readBlock(sliceInput);
/* 57 */     int offsetBase = sliceInput.readInt();
/* 58 */     int positionCount = sliceInput.readInt();
/* 59 */     byte[] offsets = new byte[positionCount * 4];
/* 60 */     sliceInput.readBytes(offsets);
/* 61 */     boolean[] valueIsNull = EncoderUtil.decodeNullBits(sliceInput, positionCount);
/* 62 */     return new ArrayBlock(values, Slices.wrappedBuffer(offsets), offsetBase, Slices.wrappedBooleanArray(valueIsNull));
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 68 */     return FACTORY;
/*    */   }
/*    */   
/*    */ 
/*    */   public static class ArrayBlockEncodingFactory
/*    */     implements BlockEncodingFactory<ArrayBlockEncoding>
/*    */   {
/*    */     public String getName()
/*    */     {
/* 77 */       return "ARRAY";
/*    */     }
/*    */     
/*    */ 
/*    */     public ArrayBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 83 */       BlockEncoding valueBlockEncoding = serde.readBlockEncoding(input);
/* 84 */       return new ArrayBlockEncoding(valueBlockEncoding);
/*    */     }
/*    */     
/*    */ 
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, ArrayBlockEncoding blockEncoding)
/*    */     {
/* 90 */       serde.writeBlockEncoding(output, blockEncoding.valueBlockEncoding);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\ArrayBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */