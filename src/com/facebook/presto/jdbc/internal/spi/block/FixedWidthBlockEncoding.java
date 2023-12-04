/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TypeManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FixedWidthBlockEncoding
/*     */   implements BlockEncoding
/*     */ {
/*  28 */   public static final BlockEncodingFactory<FixedWidthBlockEncoding> FACTORY = new FixedWidthBlockEncodingFactory();
/*     */   private static final String NAME = "FIXED_WIDTH";
/*     */   private final int fixedSize;
/*     */   
/*     */   public FixedWidthBlockEncoding(int fixedSize)
/*     */   {
/*  34 */     if (fixedSize < 0) {
/*  35 */       throw new IllegalArgumentException("fixedSize is negative");
/*     */     }
/*  37 */     this.fixedSize = fixedSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  43 */     return "FIXED_WIDTH";
/*     */   }
/*     */   
/*     */   public int getFixedSize()
/*     */   {
/*  48 */     return this.fixedSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*     */   {
/*  54 */     AbstractFixedWidthBlock fixedWidthBlock = (AbstractFixedWidthBlock)block;
/*     */     
/*  56 */     int positionCount = fixedWidthBlock.getPositionCount();
/*  57 */     sliceOutput.appendInt(positionCount);
/*     */     
/*     */ 
/*  60 */     EncoderUtil.encodeNullsAsBits(sliceOutput, fixedWidthBlock);
/*     */     
/*  62 */     Slice slice = fixedWidthBlock.getRawSlice();
/*  63 */     sliceOutput
/*  64 */       .appendInt(slice.length())
/*  65 */       .writeBytes(slice);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block readBlock(SliceInput sliceInput)
/*     */   {
/*  71 */     int positionCount = sliceInput.readInt();
/*     */     
/*  73 */     boolean[] valueIsNull = EncoderUtil.decodeNullBits(sliceInput, positionCount);
/*     */     
/*  75 */     int blockSize = sliceInput.readInt();
/*  76 */     Slice slice = sliceInput.readSlice(blockSize);
/*     */     
/*  78 */     return new FixedWidthBlock(this.fixedSize, positionCount, slice, Slices.wrappedBooleanArray(valueIsNull));
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncodingFactory getFactory()
/*     */   {
/*  84 */     return FACTORY;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class FixedWidthBlockEncodingFactory
/*     */     implements BlockEncodingFactory<FixedWidthBlockEncoding>
/*     */   {
/*     */     public String getName()
/*     */     {
/*  93 */       return "FIXED_WIDTH";
/*     */     }
/*     */     
/*     */ 
/*     */     public FixedWidthBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*     */     {
/*  99 */       int entrySize = input.readInt();
/* 100 */       return new FixedWidthBlockEncoding(entrySize);
/*     */     }
/*     */     
/*     */ 
/*     */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, FixedWidthBlockEncoding blockEncoding)
/*     */     {
/* 106 */       output.writeInt(blockEncoding.getFixedSize());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\FixedWidthBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */