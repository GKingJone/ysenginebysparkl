/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public class InterleavedBlock
/*     */   extends AbstractInterleavedBlock
/*     */ {
/*  23 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(InterleavedBlock.class).instanceSize();
/*     */   
/*     */   private final Block[] blocks;
/*     */   
/*     */   private final InterleavedBlockEncoding blockEncoding;
/*     */   private final int start;
/*     */   private final int positionCount;
/*     */   private final int retainedSizeInBytes;
/*     */   private final AtomicInteger sizeInBytes;
/*     */   
/*     */   public InterleavedBlock(Block[] blocks)
/*     */   {
/*  35 */     super(blocks.length);
/*  36 */     this.blocks = blocks;
/*     */     
/*  38 */     int sizeInBytes = 0;
/*  39 */     int retainedSizeInBytes = INSTANCE_SIZE;
/*  40 */     int positionCount = 0;
/*  41 */     int firstSubBlockPositionCount = blocks[0].getPositionCount();
/*  42 */     for (int i = 0; i < getBlockCount(); i++) {
/*  43 */       sizeInBytes += blocks[i].getSizeInBytes();
/*  44 */       retainedSizeInBytes += blocks[i].getRetainedSizeInBytes();
/*  45 */       positionCount += blocks[i].getPositionCount();
/*     */       
/*  47 */       if (firstSubBlockPositionCount != blocks[i].getPositionCount()) {
/*  48 */         throw new IllegalArgumentException("length of sub blocks differ: block 0: " + firstSubBlockPositionCount + ", block " + i + ": " + blocks[i].getPositionCount());
/*     */       }
/*     */     }
/*     */     
/*  52 */     this.blockEncoding = computeBlockEncoding();
/*  53 */     this.start = 0;
/*  54 */     this.positionCount = positionCount;
/*  55 */     this.sizeInBytes = new AtomicInteger(sizeInBytes);
/*  56 */     this.retainedSizeInBytes = retainedSizeInBytes;
/*     */   }
/*     */   
/*     */   private InterleavedBlock(Block[] blocks, int start, int positionCount, int retainedSizeInBytes, InterleavedBlockEncoding blockEncoding)
/*     */   {
/*  61 */     super(blocks.length);
/*  62 */     this.blocks = blocks;
/*  63 */     this.start = start;
/*  64 */     this.positionCount = positionCount;
/*  65 */     this.retainedSizeInBytes = retainedSizeInBytes;
/*  66 */     this.blockEncoding = blockEncoding;
/*  67 */     this.sizeInBytes = new AtomicInteger(-1);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int position, int length)
/*     */   {
/*  73 */     validateRange(position, length);
/*  74 */     return new InterleavedBlock(this.blocks, toAbsolutePosition(position), length, this.retainedSizeInBytes, this.blockEncoding);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Block getBlock(int blockIndex)
/*     */   {
/*  80 */     if (blockIndex < 0) {
/*  81 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */     
/*  84 */     return this.blocks[blockIndex];
/*     */   }
/*     */   
/*     */ 
/*     */   protected int toAbsolutePosition(int position)
/*     */   {
/*  90 */     return position + this.start;
/*     */   }
/*     */   
/*     */ 
/*     */   public InterleavedBlockEncoding getEncoding()
/*     */   {
/*  96 */     return this.blockEncoding;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/* 102 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/* 108 */     int sizeInBytes = this.sizeInBytes.get();
/* 109 */     if (sizeInBytes < 0) {
/* 110 */       sizeInBytes = 0;
/* 111 */       for (int i = 0; i < getBlockCount(); i++) {
/* 112 */         sizeInBytes += this.blocks[i].getRegion(this.start / this.blocks.length, this.positionCount / this.blocks.length).getSizeInBytes();
/*     */       }
/* 114 */       this.sizeInBytes.set(sizeInBytes);
/*     */     }
/* 116 */     return sizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/* 122 */     return this.retainedSizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 128 */     StringBuilder sb = new StringBuilder("InterleavedBlock{");
/* 129 */     sb.append("columns=").append(getBlockCount());
/* 130 */     sb.append(", positionCountPerBlock=").append(getPositionCount() / getBlockCount());
/* 131 */     sb.append('}');
/* 132 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\InterleavedBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */