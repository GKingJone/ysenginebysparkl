/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*     */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
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
/*     */ 
/*     */ 
/*     */ public final class CharType
/*     */   extends AbstractVariableWidthType
/*     */ {
/*     */   public static final int MAX_LENGTH = 65536;
/*     */   private final int length;
/*     */   
/*     */   public static CharType createCharType(long length)
/*     */   {
/*  38 */     return new CharType(length);
/*     */   }
/*     */   
/*     */   private CharType(long length)
/*     */   {
/*  43 */     super(new TypeSignature("char", 
/*     */     
/*     */ 
/*  46 */       Collections.singletonList(TypeSignatureParameter.of(length))), Slice.class);
/*     */     
/*     */ 
/*  49 */     if ((length < 0L) || (length > 65536L)) {
/*  50 */       throw new PrestoException(StandardErrorCode.INVALID_FUNCTION_ARGUMENT, String.format("CHAR length scale must be in range [0, %s]", new Object[] { Integer.valueOf(65536) }));
/*     */     }
/*  52 */     this.length = ((int)length);
/*     */   }
/*     */   
/*     */   public int getLength()
/*     */   {
/*  57 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  63 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  69 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  75 */     if (block.isNull(position)) {
/*  76 */       return null;
/*     */     }
/*     */     
/*  79 */     StringBuilder builder = new StringBuilder(this.length);
/*  80 */     String value = block.getSlice(position, 0, block.getLength(position)).toStringUtf8();
/*  81 */     builder.append(value);
/*  82 */     for (int i = value.length(); i < this.length; i++) {
/*  83 */       builder.append(' ');
/*     */     }
/*     */     
/*  86 */     return builder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  92 */     int leftLength = leftBlock.getLength(leftPosition);
/*  93 */     int rightLength = rightBlock.getLength(rightPosition);
/*  94 */     if (leftLength != rightLength) {
/*  95 */       return false;
/*     */     }
/*  97 */     return leftBlock.equals(leftPosition, 0, rightBlock, rightPosition, 0, leftLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/* 103 */     return block.hash(position, 0, block.getLength(position));
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/* 109 */     int leftLength = leftBlock.getLength(leftPosition);
/* 110 */     int rightLength = rightBlock.getLength(rightPosition);
/* 111 */     return leftBlock.compareTo(leftPosition, 0, leftLength, rightBlock, rightPosition, 0, rightLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/* 117 */     if (block.isNull(position)) {
/* 118 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/* 121 */       block.writeBytesTo(position, 0, block.getLength(position), blockBuilder);
/* 122 */       blockBuilder.closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(Block block, int position)
/*     */   {
/* 129 */     return block.getSlice(position, 0, block.getLength(position));
/*     */   }
/*     */   
/*     */   public void writeString(BlockBuilder blockBuilder, String value)
/*     */   {
/* 134 */     writeSlice(blockBuilder, Slices.utf8Slice(value));
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value)
/*     */   {
/* 140 */     writeSlice(blockBuilder, value, 0, value.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length)
/*     */   {
/* 146 */     blockBuilder.writeBytes(value, offset, length).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 152 */     if (this == o) {
/* 153 */       return true;
/*     */     }
/* 155 */     if ((o == null) || (getClass() != o.getClass())) {
/* 156 */       return false;
/*     */     }
/*     */     
/* 159 */     CharType other = (CharType)o;
/*     */     
/* 161 */     return Objects.equals(Integer.valueOf(this.length), Integer.valueOf(other.length));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 167 */     return Objects.hash(new Object[] { Integer.valueOf(this.length) });
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDisplayName()
/*     */   {
/* 173 */     return getTypeSignature().toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 179 */     return getDisplayName();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\CharType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */