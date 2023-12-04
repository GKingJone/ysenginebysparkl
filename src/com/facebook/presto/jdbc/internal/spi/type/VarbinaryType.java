/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
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
/*     */ public final class VarbinaryType
/*     */   extends AbstractVariableWidthType
/*     */ {
/*  26 */   public static final VarbinaryType VARBINARY = new VarbinaryType();
/*     */   
/*     */   private VarbinaryType()
/*     */   {
/*  30 */     super(TypeSignature.parseTypeSignature("varbinary"), Slice.class);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  36 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  42 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  48 */     if (block.isNull(position)) {
/*  49 */       return null;
/*     */     }
/*     */     
/*  52 */     return new SqlVarbinary(block.getSlice(position, 0, block.getLength(position)).getBytes());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  58 */     int leftLength = leftBlock.getLength(leftPosition);
/*  59 */     int rightLength = rightBlock.getLength(rightPosition);
/*  60 */     if (leftLength != rightLength) {
/*  61 */       return false;
/*     */     }
/*  63 */     return leftBlock.equals(leftPosition, 0, rightBlock, rightPosition, 0, leftLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  69 */     return block.hash(position, 0, block.getLength(position));
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  75 */     int leftLength = leftBlock.getLength(leftPosition);
/*  76 */     int rightLength = rightBlock.getLength(rightPosition);
/*  77 */     return leftBlock.compareTo(leftPosition, 0, leftLength, rightBlock, rightPosition, 0, rightLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/*  83 */     if (block.isNull(position)) {
/*  84 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/*  87 */       block.writeBytesTo(position, 0, block.getLength(position), blockBuilder);
/*  88 */       blockBuilder.closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(Block block, int position)
/*     */   {
/*  95 */     return block.getSlice(position, 0, block.getLength(position));
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value)
/*     */   {
/* 101 */     writeSlice(blockBuilder, value, 0, value.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length)
/*     */   {
/* 107 */     blockBuilder.writeBytes(value, offset, length).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 113 */     return other == VARBINARY;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 119 */     return getClass().hashCode();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\VarbinaryType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */