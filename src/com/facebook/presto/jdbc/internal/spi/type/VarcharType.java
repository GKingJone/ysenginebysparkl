/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import java.util.Collections;
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
/*     */ public final class VarcharType
/*     */   extends AbstractVariableWidthType
/*     */ {
/*     */   public static final int MAX_LENGTH = Integer.MAX_VALUE;
/*  30 */   public static final VarcharType VARCHAR = new VarcharType(Integer.MAX_VALUE);
/*     */   public static final String VARCHAR_MAX_LENGTH = "varchar(2147483647)";
/*     */   private final int length;
/*     */   
/*     */   public static VarcharType createUnboundedVarcharType() {
/*  35 */     return VARCHAR;
/*     */   }
/*     */   
/*     */   public static VarcharType createVarcharType(int length)
/*     */   {
/*  40 */     return new VarcharType(length);
/*     */   }
/*     */   
/*     */   public static TypeSignature getParametrizedVarcharSignature(String param)
/*     */   {
/*  45 */     return new TypeSignature("varchar", new TypeSignatureParameter[] { TypeSignatureParameter.of(param) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private VarcharType(int length)
/*     */   {
/*  52 */     super(new TypeSignature("varchar", 
/*     */     
/*     */ 
/*  55 */       Collections.singletonList(TypeSignatureParameter.of(length))), Slice.class);
/*     */     
/*     */ 
/*  58 */     if (length < 0) {
/*  59 */       throw new IllegalArgumentException("Invalid VARCHAR length " + length);
/*     */     }
/*  61 */     this.length = length;
/*     */   }
/*     */   
/*     */   public int getLength()
/*     */   {
/*  66 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  72 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  78 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  84 */     if (block.isNull(position)) {
/*  85 */       return null;
/*     */     }
/*     */     
/*  88 */     return block.getSlice(position, 0, block.getLength(position)).toStringUtf8();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  94 */     int leftLength = leftBlock.getLength(leftPosition);
/*  95 */     int rightLength = rightBlock.getLength(rightPosition);
/*  96 */     if (leftLength != rightLength) {
/*  97 */       return false;
/*     */     }
/*  99 */     return leftBlock.equals(leftPosition, 0, rightBlock, rightPosition, 0, leftLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/* 105 */     return block.hash(position, 0, block.getLength(position));
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/* 111 */     int leftLength = leftBlock.getLength(leftPosition);
/* 112 */     int rightLength = rightBlock.getLength(rightPosition);
/* 113 */     return leftBlock.compareTo(leftPosition, 0, leftLength, rightBlock, rightPosition, 0, rightLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/* 119 */     if (block.isNull(position)) {
/* 120 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/* 123 */       block.writeBytesTo(position, 0, block.getLength(position), blockBuilder);
/* 124 */       blockBuilder.closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(Block block, int position)
/*     */   {
/* 131 */     return block.getSlice(position, 0, block.getLength(position));
/*     */   }
/*     */   
/*     */   public void writeString(BlockBuilder blockBuilder, String value)
/*     */   {
/* 136 */     writeSlice(blockBuilder, Slices.utf8Slice(value));
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value)
/*     */   {
/* 142 */     writeSlice(blockBuilder, value, 0, value.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length)
/*     */   {
/* 148 */     blockBuilder.writeBytes(value, offset, length).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 154 */     if (this == o) {
/* 155 */       return true;
/*     */     }
/* 157 */     if ((o == null) || (getClass() != o.getClass())) {
/* 158 */       return false;
/*     */     }
/*     */     
/* 161 */     VarcharType other = (VarcharType)o;
/*     */     
/* 163 */     return Objects.equals(Integer.valueOf(this.length), Integer.valueOf(other.length));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 169 */     return Objects.hash(new Object[] { Integer.valueOf(this.length) });
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDisplayName()
/*     */   {
/* 175 */     if (this.length == Integer.MAX_VALUE) {
/* 176 */       return getTypeSignature().getBase();
/*     */     }
/*     */     
/* 179 */     return getTypeSignature().toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 185 */     return getDisplayName();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\VarcharType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */