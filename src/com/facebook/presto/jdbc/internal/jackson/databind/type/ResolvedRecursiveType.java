/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResolvedRecursiveType
/*     */   extends TypeBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected JavaType _referencedType;
/*     */   
/*     */   public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings)
/*     */   {
/*  17 */     super(erasedType, bindings, null, null, 0, null, null, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setReference(JavaType ref)
/*     */   {
/*  23 */     if (this._referencedType != null) {
/*  24 */       throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
/*     */     }
/*  26 */     this._referencedType = ref;
/*     */   }
/*     */   
/*  29 */   public JavaType getSelfReferencedType() { return this._referencedType; }
/*     */   
/*     */   public StringBuilder getGenericSignature(StringBuilder sb)
/*     */   {
/*  33 */     return this._referencedType.getGenericSignature(sb);
/*     */   }
/*     */   
/*     */   public StringBuilder getErasedSignature(StringBuilder sb)
/*     */   {
/*  38 */     return this._referencedType.getErasedSignature(sb);
/*     */   }
/*     */   
/*     */   public JavaType withContentType(JavaType contentType)
/*     */   {
/*  43 */     return this;
/*     */   }
/*     */   
/*     */   public JavaType withTypeHandler(Object h)
/*     */   {
/*  48 */     return this;
/*     */   }
/*     */   
/*     */   public JavaType withContentTypeHandler(Object h)
/*     */   {
/*  53 */     return this;
/*     */   }
/*     */   
/*     */   public JavaType withValueHandler(Object h)
/*     */   {
/*  58 */     return this;
/*     */   }
/*     */   
/*     */   public JavaType withContentValueHandler(Object h)
/*     */   {
/*  63 */     return this;
/*     */   }
/*     */   
/*     */   public JavaType withStaticTyping()
/*     */   {
/*  68 */     return this;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected JavaType _narrow(Class<?> subclass)
/*     */   {
/*  74 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*     */   {
/*  80 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isContainerType()
/*     */   {
/*  85 */     return false;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  90 */     StringBuilder sb = new StringBuilder(40).append("[recursive type; ");
/*     */     
/*  92 */     if (this._referencedType == null) {
/*  93 */       sb.append("UNRESOLVED");
/*     */     }
/*     */     else
/*     */     {
/*  97 */       sb.append(this._referencedType.getRawClass().getName());
/*     */     }
/*  99 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 104 */     if (o == this) return true;
/* 105 */     if (o == null) { return false;
/*     */     }
/* 107 */     if (this._referencedType == null) {
/* 108 */       return false;
/*     */     }
/* 110 */     return (o.getClass() == getClass()) && (this._referencedType.equals(((ResolvedRecursiveType)o).getSelfReferencedType()));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\type\ResolvedRecursiveType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */