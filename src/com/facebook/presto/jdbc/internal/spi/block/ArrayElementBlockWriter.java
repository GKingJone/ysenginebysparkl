/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
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
/*     */ public class ArrayElementBlockWriter
/*     */   extends AbstractArrayElementBlock
/*     */   implements BlockBuilder
/*     */ {
/*  23 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(ArrayElementBlockWriter.class).instanceSize();
/*     */   
/*     */   private final BlockBuilder blockBuilder;
/*     */   private final int initialBlockBuilderSize;
/*     */   private int positionsWritten;
/*     */   
/*     */   public ArrayElementBlockWriter(BlockBuilder blockBuilder, int start)
/*     */   {
/*  31 */     super(start);
/*  32 */     this.blockBuilder = blockBuilder;
/*  33 */     this.initialBlockBuilderSize = blockBuilder.getSizeInBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   protected BlockBuilder getBlock()
/*     */   {
/*  39 */     return this.blockBuilder;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/*  45 */     return this.blockBuilder.getSizeInBytes() - this.initialBlockBuilderSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/*  51 */     return INSTANCE_SIZE + this.blockBuilder.getRetainedSizeInBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeByte(int value)
/*     */   {
/*  57 */     this.blockBuilder.writeByte(value);
/*  58 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeShort(int value)
/*     */   {
/*  64 */     this.blockBuilder.writeShort(value);
/*  65 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeInt(int value)
/*     */   {
/*  71 */     this.blockBuilder.writeInt(value);
/*  72 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeLong(long value)
/*     */   {
/*  78 */     this.blockBuilder.writeLong(value);
/*  79 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeBytes(Slice source, int sourceIndex, int length)
/*     */   {
/*  85 */     this.blockBuilder.writeBytes(source, sourceIndex, length);
/*  86 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeObject(Object value)
/*     */   {
/*  92 */     this.blockBuilder.writeObject(value);
/*  93 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder beginBlockEntry()
/*     */   {
/*  99 */     return this.blockBuilder.beginBlockEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder appendNull()
/*     */   {
/* 105 */     this.blockBuilder.appendNull();
/* 106 */     entryAdded();
/* 107 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder closeEntry()
/*     */   {
/* 113 */     this.blockBuilder.closeEntry();
/* 114 */     entryAdded();
/* 115 */     return this;
/*     */   }
/*     */   
/*     */   private void entryAdded()
/*     */   {
/* 120 */     this.positionsWritten += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/* 126 */     return this.positionsWritten;
/*     */   }
/*     */   
/*     */ 
/*     */   public Block build()
/*     */   {
/* 132 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(BlockBuilderStatus blockBuilderStatus)
/*     */   {
/* 138 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 144 */     StringBuilder sb = new StringBuilder("ArrayElementBlockWriter{");
/* 145 */     sb.append("positionCount=").append(getPositionCount());
/* 146 */     sb.append('}');
/* 147 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\ArrayElementBlockWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */