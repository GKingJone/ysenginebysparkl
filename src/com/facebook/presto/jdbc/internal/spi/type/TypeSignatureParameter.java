/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
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
/*     */ public class TypeSignatureParameter
/*     */ {
/*     */   private final ParameterKind kind;
/*     */   private final Object value;
/*     */   
/*     */   public static TypeSignatureParameter of(TypeSignature typeSignature)
/*     */   {
/*  29 */     return new TypeSignatureParameter(ParameterKind.TYPE, typeSignature);
/*     */   }
/*     */   
/*     */   public static TypeSignatureParameter of(long longLiteral)
/*     */   {
/*  34 */     return new TypeSignatureParameter(ParameterKind.LONG, Long.valueOf(longLiteral));
/*     */   }
/*     */   
/*     */   public static TypeSignatureParameter of(NamedTypeSignature namedTypeSignature)
/*     */   {
/*  39 */     return new TypeSignatureParameter(ParameterKind.NAMED_TYPE, namedTypeSignature);
/*     */   }
/*     */   
/*     */   public static TypeSignatureParameter of(String variable)
/*     */   {
/*  44 */     return new TypeSignatureParameter(ParameterKind.VARIABLE, variable);
/*     */   }
/*     */   
/*     */   private TypeSignatureParameter(ParameterKind kind, Object value)
/*     */   {
/*  49 */     this.kind = ((ParameterKind)Objects.requireNonNull(kind, "kind is null"));
/*  50 */     this.value = Objects.requireNonNull(value, "value is null");
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  56 */     return this.value.toString();
/*     */   }
/*     */   
/*     */   public ParameterKind getKind()
/*     */   {
/*  61 */     return this.kind;
/*     */   }
/*     */   
/*     */   public boolean isTypeSignature()
/*     */   {
/*  66 */     return this.kind == ParameterKind.TYPE;
/*     */   }
/*     */   
/*     */   public boolean isLongLiteral()
/*     */   {
/*  71 */     return this.kind == ParameterKind.LONG;
/*     */   }
/*     */   
/*     */   public boolean isNamedTypeSignature()
/*     */   {
/*  76 */     return this.kind == ParameterKind.NAMED_TYPE;
/*     */   }
/*     */   
/*     */   public boolean isVariable()
/*     */   {
/*  81 */     return this.kind == ParameterKind.VARIABLE;
/*     */   }
/*     */   
/*     */   private <A> A getValue(ParameterKind expectedParameterKind, Class<A> target)
/*     */   {
/*  86 */     if (this.kind != expectedParameterKind) {
/*  87 */       throw new IllegalArgumentException(String.format("ParameterKind is [%s] but expected [%s]", new Object[] { this.kind, expectedParameterKind }));
/*     */     }
/*  89 */     return (A)target.cast(this.value);
/*     */   }
/*     */   
/*     */   public TypeSignature getTypeSignature()
/*     */   {
/*  94 */     return (TypeSignature)getValue(ParameterKind.TYPE, TypeSignature.class);
/*     */   }
/*     */   
/*     */   public Long getLongLiteral()
/*     */   {
/*  99 */     return (Long)getValue(ParameterKind.LONG, Long.class);
/*     */   }
/*     */   
/*     */   public NamedTypeSignature getNamedTypeSignature()
/*     */   {
/* 104 */     return (NamedTypeSignature)getValue(ParameterKind.NAMED_TYPE, NamedTypeSignature.class);
/*     */   }
/*     */   
/*     */   public String getVariable()
/*     */   {
/* 109 */     return (String)getValue(ParameterKind.VARIABLE, String.class);
/*     */   }
/*     */   
/*     */   public Optional<TypeSignature> getTypeSignatureOrNamedTypeSignature()
/*     */   {
/* 114 */     switch (this.kind) {
/*     */     case TYPE: 
/* 116 */       return Optional.of(getTypeSignature());
/*     */     case NAMED_TYPE: 
/* 118 */       return Optional.of(getNamedTypeSignature().getTypeSignature());
/*     */     }
/* 120 */     return Optional.empty();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCalculated()
/*     */   {
/* 126 */     switch (this.kind) {
/*     */     case TYPE: 
/* 128 */       return getTypeSignature().isCalculated();
/*     */     case NAMED_TYPE: 
/* 130 */       return getNamedTypeSignature().getTypeSignature().isCalculated();
/*     */     case LONG: 
/* 132 */       return false;
/*     */     case VARIABLE: 
/* 134 */       return true;
/*     */     }
/* 136 */     throw new IllegalArgumentException("Unexpected parameter kind: " + this.kind);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 143 */     if (this == o) {
/* 144 */       return true;
/*     */     }
/* 146 */     if ((o == null) || (getClass() != o.getClass())) {
/* 147 */       return false;
/*     */     }
/*     */     
/* 150 */     TypeSignatureParameter other = (TypeSignatureParameter)o;
/*     */     
/* 152 */     return (Objects.equals(this.kind, other.kind)) && 
/* 153 */       (Objects.equals(this.value, other.value));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 159 */     return Objects.hash(new Object[] { this.kind, this.value });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TypeSignatureParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */