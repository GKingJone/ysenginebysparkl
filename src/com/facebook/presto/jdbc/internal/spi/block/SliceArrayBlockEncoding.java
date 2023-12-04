/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
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
/*     */ public class SliceArrayBlockEncoding
/*     */   implements BlockEncoding
/*     */ {
/*  27 */   public static final BlockEncodingFactory<SliceArrayBlockEncoding> FACTORY = new SliceArrayBlockEncodingFactory();
/*     */   
/*     */   private static final String NAME = "SLICE_ARRAY";
/*     */   
/*     */   public String getName()
/*     */   {
/*  33 */     return "SLICE_ARRAY";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*     */   {
/*  40 */     SliceArrayBlock sliceArrayBlock = (SliceArrayBlock)block;
/*     */     
/*  42 */     int positionCount = sliceArrayBlock.getPositionCount();
/*  43 */     sliceOutput.appendInt(positionCount);
/*     */     
/*     */     int length;
/*  46 */     for (int position = 0; position < positionCount; position++) {
/*  47 */       length = 0;
/*  48 */       if (!sliceArrayBlock.isNull(position)) {
/*  49 */         length = sliceArrayBlock.getLength(position);
/*     */       }
/*  51 */       sliceOutput.appendInt(length);
/*     */     }
/*     */     
/*  54 */     EncoderUtil.encodeNullsAsBits(sliceOutput, sliceArrayBlock);
/*     */     
/*  56 */     for (Slice value : sliceArrayBlock.getValues()) {
/*  57 */       if (value != null) {
/*  58 */         sliceOutput.writeBytes(value);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Block readBlock(SliceInput sliceInput)
/*     */   {
/*  66 */     int positionCount = sliceInput.readInt();
/*     */     
/*     */ 
/*  69 */     int[] offsets = new int[positionCount + 1];
/*  70 */     int offset = 0;
/*  71 */     for (int position = 0; position < positionCount; position++) {
/*  72 */       offset += sliceInput.readInt();
/*  73 */       offsets[(position + 1)] = offset;
/*     */     }
/*     */     
/*  76 */     boolean[] valueIsNull = EncoderUtil.decodeNullBits(sliceInput, positionCount);
/*     */     
/*  78 */     Slice[] values = new Slice[positionCount];
/*  79 */     for (int position = 0; position < positionCount; position++) {
/*  80 */       if (valueIsNull[position] == 0) {
/*  81 */         values[position] = sliceInput.readSlice(offsets[(position + 1)] - offsets[position]);
/*     */       }
/*     */     }
/*     */     
/*  85 */     return new SliceArrayBlock(positionCount, values);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncodingFactory getFactory()
/*     */   {
/*  91 */     return FACTORY;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class SliceArrayBlockEncodingFactory
/*     */     implements BlockEncodingFactory<SliceArrayBlockEncoding>
/*     */   {
/*     */     public String getName()
/*     */     {
/* 100 */       return "SLICE_ARRAY";
/*     */     }
/*     */     
/*     */ 
/*     */     public SliceArrayBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*     */     {
/* 106 */       return new SliceArrayBlockEncoding();
/*     */     }
/*     */     
/*     */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, SliceArrayBlockEncoding blockEncoding) {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\SliceArrayBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */