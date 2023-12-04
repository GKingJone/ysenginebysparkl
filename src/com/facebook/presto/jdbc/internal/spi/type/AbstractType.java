/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public abstract class AbstractType
/*     */   implements Type
/*     */ {
/*     */   private final TypeSignature signature;
/*     */   private final Class<?> javaType;
/*     */   
/*     */   protected AbstractType(TypeSignature signature, Class<?> javaType)
/*     */   {
/*  32 */     this.signature = signature;
/*  33 */     this.javaType = javaType;
/*     */   }
/*     */   
/*     */ 
/*     */   public final TypeSignature getTypeSignature()
/*     */   {
/*  39 */     return this.signature;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDisplayName()
/*     */   {
/*  45 */     return this.signature.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public final Class<?> getJavaType()
/*     */   {
/*  51 */     return this.javaType;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<Type> getTypeParameters()
/*     */   {
/*  57 */     return Collections.unmodifiableList(new ArrayList());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  63 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  69 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  75 */     throw new UnsupportedOperationException(getTypeSignature() + " type is not comparable");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  81 */     throw new UnsupportedOperationException(getTypeSignature() + " type is not comparable");
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  87 */     throw new UnsupportedOperationException(getTypeSignature() + " type is not orderable");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getBoolean(Block block, int position)
/*     */   {
/*  93 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBoolean(BlockBuilder blockBuilder, boolean value)
/*     */   {
/*  99 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(Block block, int position)
/*     */   {
/* 105 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeLong(BlockBuilder blockBuilder, long value)
/*     */   {
/* 111 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public double getDouble(Block block, int position)
/*     */   {
/* 117 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeDouble(BlockBuilder blockBuilder, double value)
/*     */   {
/* 123 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(Block block, int position)
/*     */   {
/* 129 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value)
/*     */   {
/* 135 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length)
/*     */   {
/* 141 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObject(Block block, int position)
/*     */   {
/* 147 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeObject(BlockBuilder blockBuilder, Object value)
/*     */   {
/* 153 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 159 */     return getTypeSignature().toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 165 */     if (this == o) {
/* 166 */       return true;
/*     */     }
/* 168 */     if ((o == null) || (getClass() != o.getClass())) {
/* 169 */       return false;
/*     */     }
/*     */     
/* 172 */     return getTypeSignature().equals(((Type)o).getTypeSignature());
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 178 */     return this.signature.hashCode();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\AbstractType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */