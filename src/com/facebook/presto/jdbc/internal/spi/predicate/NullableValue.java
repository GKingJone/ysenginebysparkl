/*     */ package com.facebook.presto.jdbc.internal.spi.predicate;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
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
/*     */ public final class NullableValue
/*     */ {
/*     */   private final Type type;
/*     */   private final Object value;
/*     */   
/*     */   public NullableValue(Type type, Object value)
/*     */   {
/*  33 */     Objects.requireNonNull(type, "type is null");
/*  34 */     if ((value != null) && (!Primitives.wrap(type.getJavaType()).isInstance(value))) {
/*  35 */       throw new IllegalArgumentException(String.format("Object '%s' does not match type %s", new Object[] { value, type.getJavaType() }));
/*     */     }
/*     */     
/*  38 */     this.type = type;
/*  39 */     this.value = value;
/*     */   }
/*     */   
/*     */   public static NullableValue of(Type type, Object value)
/*     */   {
/*  44 */     Objects.requireNonNull(value, "value is null");
/*  45 */     return new NullableValue(type, value);
/*     */   }
/*     */   
/*     */   public static NullableValue asNull(Type type)
/*     */   {
/*  50 */     return new NullableValue(type, null);
/*     */   }
/*     */   
/*     */ 
/*     */   @JsonCreator
/*     */   public static NullableValue fromSerializable(@JsonProperty("serializable") Serializable serializable)
/*     */   {
/*  57 */     Type type = serializable.getType();
/*  58 */     Block block = serializable.getBlock();
/*  59 */     return new NullableValue(type, block == null ? null : Utils.blockToNativeValue(type, block));
/*     */   }
/*     */   
/*     */ 
/*     */   @JsonProperty
/*     */   public Serializable getSerializable()
/*     */   {
/*  66 */     return new Serializable(this.type, this.value == null ? null : Utils.nativeValueToBlock(this.type, this.value));
/*     */   }
/*     */   
/*     */   public Block asBlock()
/*     */   {
/*  71 */     return Utils.nativeValueToBlock(this.type, this.value);
/*     */   }
/*     */   
/*     */   public Type getType()
/*     */   {
/*  76 */     return this.type;
/*     */   }
/*     */   
/*     */   public boolean isNull()
/*     */   {
/*  81 */     return this.value == null;
/*     */   }
/*     */   
/*     */   public Object getValue()
/*     */   {
/*  86 */     return this.value;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  92 */     int hash = Objects.hash(new Object[] { this.type });
/*  93 */     if (this.value != null) {
/*  94 */       hash = hash * 31 + (int)this.type.hash(Utils.nativeValueToBlock(this.type, this.value), 0);
/*     */     }
/*  96 */     return hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 102 */     if (this == obj) {
/* 103 */       return true;
/*     */     }
/* 105 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 106 */       return false;
/*     */     }
/* 108 */     NullableValue other = (NullableValue)obj;
/* 109 */     if (Objects.equals(this.type, other.type)) if ((this.value == null ? 1 : 0) != (other.value == null ? 1 : 0)) {} return (this.value == null) || 
/*     */     
/* 111 */       (this.type.equalTo(Utils.nativeValueToBlock(this.type, this.value), 0, Utils.nativeValueToBlock(other.type, other.value), 0));
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 117 */     StringBuilder sb = new StringBuilder("NullableValue{");
/* 118 */     sb.append("type=").append(this.type);
/* 119 */     sb.append(", value=").append(this.value);
/* 120 */     sb.append('}');
/* 121 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public static class Serializable
/*     */   {
/*     */     private final Type type;
/*     */     
/*     */     private final Block block;
/*     */     
/*     */     @JsonCreator
/*     */     public Serializable(@JsonProperty("type") Type type, @JsonProperty("block") Block block)
/*     */     {
/* 134 */       this.type = ((Type)Objects.requireNonNull(type, "type is null"));
/* 135 */       this.block = block;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public Type getType()
/*     */     {
/* 141 */       return this.type;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public Block getBlock()
/*     */     {
/* 147 */       return this.block;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\NullableValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */