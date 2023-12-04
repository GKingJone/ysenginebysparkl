/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
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
/*     */ public class TypeParameter
/*     */ {
/*     */   private final ParameterKind kind;
/*     */   private final Object value;
/*     */   
/*     */   private TypeParameter(ParameterKind kind, Object value)
/*     */   {
/*  28 */     this.kind = kind;
/*  29 */     this.value = value;
/*     */   }
/*     */   
/*     */   public static TypeParameter of(Type type)
/*     */   {
/*  34 */     return new TypeParameter(ParameterKind.TYPE, type);
/*     */   }
/*     */   
/*     */   public static TypeParameter of(long longLiteral)
/*     */   {
/*  39 */     return new TypeParameter(ParameterKind.LONG, Long.valueOf(longLiteral));
/*     */   }
/*     */   
/*     */   public static TypeParameter of(NamedType namedType)
/*     */   {
/*  44 */     return new TypeParameter(ParameterKind.NAMED_TYPE, namedType);
/*     */   }
/*     */   
/*     */   public static TypeParameter of(String variable)
/*     */   {
/*  49 */     return new TypeParameter(ParameterKind.VARIABLE, variable);
/*     */   }
/*     */   
/*     */   public static TypeParameter of(TypeSignatureParameter parameter, TypeManager typeManager)
/*     */   {
/*  54 */     switch (parameter.getKind()) {
/*     */     case TYPE: 
/*  56 */       Type type = typeManager.getType(parameter.getTypeSignature());
/*  57 */       if (type == null) {
/*  58 */         return null;
/*     */       }
/*  60 */       return of(type);
/*     */     
/*     */     case LONG: 
/*  63 */       return of(parameter.getLongLiteral().longValue());
/*     */     case NAMED_TYPE: 
/*  65 */       Type type = typeManager.getType(parameter.getNamedTypeSignature().getTypeSignature());
/*  66 */       if (type == null) {
/*  67 */         return null;
/*     */       }
/*  69 */       return of(new NamedType(parameter
/*  70 */         .getNamedTypeSignature().getName(), type));
/*     */     
/*     */ 
/*     */     case VARIABLE: 
/*  74 */       return of(parameter.getVariable());
/*     */     }
/*  76 */     throw new UnsupportedOperationException(String.format("Unsupported parameter [%s]", new Object[] { parameter }));
/*     */   }
/*     */   
/*     */ 
/*     */   public ParameterKind getKind()
/*     */   {
/*  82 */     return this.kind;
/*     */   }
/*     */   
/*     */   public <A> A getValue(ParameterKind expectedParameterKind, Class<A> target)
/*     */   {
/*  87 */     if (this.kind != expectedParameterKind) {
/*  88 */       throw new AssertionError(String.format("ParameterKind is [%s] but expected [%s]", new Object[] { this.kind, expectedParameterKind }));
/*     */     }
/*  90 */     return (A)target.cast(this.value);
/*     */   }
/*     */   
/*     */   public boolean isLongLiteral()
/*     */   {
/*  95 */     return this.kind == ParameterKind.LONG;
/*     */   }
/*     */   
/*     */   public Type getType()
/*     */   {
/* 100 */     return (Type)getValue(ParameterKind.TYPE, Type.class);
/*     */   }
/*     */   
/*     */   public Long getLongLiteral()
/*     */   {
/* 105 */     return (Long)getValue(ParameterKind.LONG, Long.class);
/*     */   }
/*     */   
/*     */   public NamedType getNamedType()
/*     */   {
/* 110 */     return (NamedType)getValue(ParameterKind.NAMED_TYPE, NamedType.class);
/*     */   }
/*     */   
/*     */   public String getVariable()
/*     */   {
/* 115 */     return (String)getValue(ParameterKind.VARIABLE, String.class);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 121 */     return this.value.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 127 */     if (this == o) {
/* 128 */       return true;
/*     */     }
/* 130 */     if ((o == null) || (getClass() != o.getClass())) {
/* 131 */       return false;
/*     */     }
/*     */     
/* 134 */     TypeParameter other = (TypeParameter)o;
/*     */     
/* 136 */     return (Objects.equals(this.kind, other.kind)) && 
/* 137 */       (Objects.equals(this.value, other.value));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 143 */     return Objects.hash(new Object[] { this.kind, this.value });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TypeParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */