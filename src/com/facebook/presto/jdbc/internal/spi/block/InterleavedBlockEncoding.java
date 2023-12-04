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
/*    */ public class InterleavedBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/* 23 */   public static final BlockEncodingFactory<InterleavedBlockEncoding> FACTORY = new InterleavedBlockEncodingFactory();
/*    */   
/*    */   private static final String NAME = "INTERLEAVED";
/*    */   private final BlockEncoding[] individualBlockEncodings;
/*    */   
/*    */   public InterleavedBlockEncoding(BlockEncoding[] individualBlockEncodings)
/*    */   {
/* 30 */     this.individualBlockEncodings = individualBlockEncodings;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 36 */     return "INTERLEAVED";
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*    */   {
/* 42 */     AbstractInterleavedBlock interleavedBlock = (AbstractInterleavedBlock)block;
/*    */     
/* 44 */     if (interleavedBlock.getBlockCount() != this.individualBlockEncodings.length)
/*    */     {
/* 46 */       throw new IllegalArgumentException("argument block differs in length (" + interleavedBlock.getBlockCount() + ") with this encoding (" + this.individualBlockEncodings.length + ")");
/*    */     }
/*    */     
/* 49 */     Block[] subBlocks = interleavedBlock.computeSerializableSubBlocks();
/* 50 */     for (int i = 0; i < subBlocks.length; i++) {
/* 51 */       this.individualBlockEncodings[i].writeBlock(sliceOutput, subBlocks[i]);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public Block readBlock(SliceInput sliceInput)
/*    */   {
/* 58 */     Block[] individualBlocks = new Block[this.individualBlockEncodings.length];
/* 59 */     for (int i = 0; i < this.individualBlockEncodings.length; i++) {
/* 60 */       individualBlocks[i] = this.individualBlockEncodings[i].readBlock(sliceInput);
/*    */     }
/* 62 */     return new InterleavedBlock(individualBlocks);
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 68 */     return FACTORY;
/*    */   }
/*    */   
/*    */ 
/*    */   public static class InterleavedBlockEncodingFactory
/*    */     implements BlockEncodingFactory<InterleavedBlockEncoding>
/*    */   {
/*    */     public String getName()
/*    */     {
/* 77 */       return "INTERLEAVED";
/*    */     }
/*    */     
/*    */ 
/*    */     public InterleavedBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 83 */       int individualBlockEncodingsCount = input.readInt();
/* 84 */       BlockEncoding[] individualBlockEncodings = new BlockEncoding[individualBlockEncodingsCount];
/* 85 */       for (int i = 0; i < individualBlockEncodingsCount; i++) {
/* 86 */         individualBlockEncodings[i] = serde.readBlockEncoding(input);
/*    */       }
/* 88 */       return new InterleavedBlockEncoding(individualBlockEncodings);
/*    */     }
/*    */     
/*    */ 
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, InterleavedBlockEncoding blockEncoding)
/*    */     {
/* 94 */       output.appendInt(blockEncoding.individualBlockEncodings.length);
/* 95 */       for (BlockEncoding individualBlockEncoding : blockEncoding.individualBlockEncodings) {
/* 96 */         serde.writeBlockEncoding(output, individualBlockEncoding);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\InterleavedBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */