/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public class LazyBlock
/*     */   implements Block
/*     */ {
/*  26 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(LazyBlock.class).instanceSize();
/*     */   
/*     */   private final int positionCount;
/*     */   
/*     */   private LazyBlockLoader<LazyBlock> loader;
/*     */   private Block block;
/*     */   
/*     */   public LazyBlock(int positionCount, LazyBlockLoader<LazyBlock> loader)
/*     */   {
/*  35 */     this.positionCount = positionCount;
/*  36 */     this.loader = ((LazyBlockLoader)Objects.requireNonNull(loader, "loader is null"));
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/*  42 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/*  48 */     assureLoaded();
/*  49 */     return this.block.getLength(position);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getByte(int position, int offset)
/*     */   {
/*  55 */     assureLoaded();
/*  56 */     return this.block.getByte(position, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/*  62 */     assureLoaded();
/*  63 */     return this.block.getShort(position, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/*  69 */     assureLoaded();
/*  70 */     return this.block.getInt(position, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/*  76 */     assureLoaded();
/*  77 */     return this.block.getLong(position, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(int position, int offset, int length)
/*     */   {
/*  83 */     assureLoaded();
/*  84 */     return this.block.getSlice(position, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getObject(int position, Class<T> clazz)
/*     */   {
/*  90 */     assureLoaded();
/*  91 */     return (T)this.block.getObject(position, clazz);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean bytesEqual(int position, int offset, Slice otherSlice, int otherOffset, int length)
/*     */   {
/*  97 */     assureLoaded();
/*  98 */     return this.block.bytesEqual(position, offset, otherSlice, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int bytesCompare(int position, int offset, int length, Slice otherSlice, int otherOffset, int otherLength)
/*     */   {
/* 104 */     assureLoaded();
/* 105 */     return this.block.bytesCompare(position, offset, length, otherSlice, otherOffset, otherLength);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeBytesTo(int position, int offset, int length, BlockBuilder blockBuilder)
/*     */   {
/* 116 */     assureLoaded();
/* 117 */     this.block.writeBytesTo(position, offset, length, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 123 */     assureLoaded();
/* 124 */     this.block.writePositionTo(position, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(int position, int offset, Block otherBlock, int otherPosition, int otherOffset, int length)
/*     */   {
/* 130 */     assureLoaded();
/* 131 */     return this.block.equals(position, offset, otherBlock, otherPosition, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long hash(int position, int offset, int length)
/*     */   {
/* 142 */     assureLoaded();
/* 143 */     return this.block.hash(position, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(int leftPosition, int leftOffset, int leftLength, Block rightBlock, int rightPosition, int rightOffset, int rightLength)
/*     */   {
/* 149 */     assureLoaded();
/* 150 */     return this.block.compareTo(leftPosition, leftOffset, leftLength, rightBlock, rightPosition, rightOffset, rightLength);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 162 */     assureLoaded();
/* 163 */     return this.block.getSingleValueBlock(position);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/* 169 */     assureLoaded();
/* 170 */     return this.block.getSizeInBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/* 176 */     assureLoaded();
/* 177 */     return INSTANCE_SIZE + this.block.getRetainedSizeInBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/* 183 */     assureLoaded();
/* 184 */     return new LazyBlockEncoding(this.block.getEncoding());
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/* 190 */     assureLoaded();
/* 191 */     return this.block.copyPositions(positions);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 197 */     assureLoaded();
/* 198 */     return this.block.getRegion(positionOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int position, int length)
/*     */   {
/* 204 */     assureLoaded();
/* 205 */     return this.block.copyRegion(position, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 211 */     assureLoaded();
/* 212 */     return this.block.isNull(position);
/*     */   }
/*     */   
/*     */   public Block getBlock()
/*     */   {
/* 217 */     assureLoaded();
/* 218 */     return this.block;
/*     */   }
/*     */   
/*     */   public void setBlock(Block block)
/*     */   {
/* 223 */     if (this.block != null) {
/* 224 */       throw new IllegalStateException("block already set");
/*     */     }
/* 226 */     this.block = ((Block)Objects.requireNonNull(block, "block is null"));
/*     */   }
/*     */   
/*     */ 
/*     */   public void assureLoaded()
/*     */   {
/* 232 */     if (this.block != null) {
/* 233 */       return;
/*     */     }
/* 235 */     this.loader.load(this);
/*     */     
/* 237 */     if (this.block == null) {
/* 238 */       throw new IllegalArgumentException("Lazy block loader did not load this block");
/*     */     }
/*     */     
/*     */ 
/* 242 */     this.loader = null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\LazyBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */