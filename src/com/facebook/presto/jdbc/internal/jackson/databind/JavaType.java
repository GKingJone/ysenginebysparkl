/*     */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.type.ResolvedType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeBindings;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Type;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JavaType
/*     */   extends ResolvedType
/*     */   implements Serializable, Type
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Class<?> _class;
/*     */   protected final int _hash;
/*     */   protected final Object _valueHandler;
/*     */   protected final Object _typeHandler;
/*     */   protected final boolean _asStatic;
/*     */   
/*     */   protected JavaType(Class<?> raw, int additionalHash, Object valueHandler, Object typeHandler, boolean asStatic)
/*     */   {
/*  79 */     this._class = raw;
/*  80 */     this._hash = (raw.getName().hashCode() + additionalHash);
/*  81 */     this._valueHandler = valueHandler;
/*  82 */     this._typeHandler = typeHandler;
/*  83 */     this._asStatic = asStatic;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JavaType(JavaType base)
/*     */   {
/*  93 */     this._class = base._class;
/*  94 */     this._hash = base._hash;
/*  95 */     this._valueHandler = base._valueHandler;
/*  96 */     this._typeHandler = base._typeHandler;
/*  97 */     this._asStatic = base._asStatic;
/*     */   }
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
/*     */   public abstract JavaType withTypeHandler(Object paramObject);
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
/*     */   public abstract JavaType withContentTypeHandler(Object paramObject);
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
/*     */   public abstract JavaType withValueHandler(Object paramObject);
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
/*     */   public abstract JavaType withContentValueHandler(Object paramObject);
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
/*     */   public abstract JavaType withContentType(JavaType paramJavaType);
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
/*     */   public abstract JavaType withStaticTyping();
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
/*     */   public abstract JavaType refine(Class<?> paramClass, TypeBindings paramTypeBindings, JavaType paramJavaType, JavaType[] paramArrayOfJavaType);
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
/*     */   @Deprecated
/*     */   public JavaType forcedNarrowBy(Class<?> subclass)
/*     */   {
/* 191 */     if (subclass == this._class) {
/* 192 */       return this;
/*     */     }
/* 194 */     JavaType result = _narrow(subclass);
/*     */     
/* 196 */     if (this._valueHandler != result.getValueHandler()) {
/* 197 */       result = result.withValueHandler(this._valueHandler);
/*     */     }
/* 199 */     if (this._typeHandler != result.getTypeHandler()) {
/* 200 */       result = result.withTypeHandler(this._typeHandler);
/*     */     }
/* 202 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected abstract JavaType _narrow(Class<?> paramClass);
/*     */   
/*     */ 
/*     */ 
/*     */   public final Class<?> getRawClass()
/*     */   {
/* 215 */     return this._class;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean hasRawClass(Class<?> clz)
/*     */   {
/* 223 */     return this._class == clz;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasContentType()
/*     */   {
/* 233 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean isTypeOrSubTypeOf(Class<?> clz)
/*     */   {
/* 240 */     return (this._class == clz) || (clz.isAssignableFrom(this._class));
/*     */   }
/*     */   
/*     */   public boolean isAbstract()
/*     */   {
/* 245 */     return Modifier.isAbstract(this._class.getModifiers());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isConcrete()
/*     */   {
/* 255 */     int mod = this._class.getModifiers();
/* 256 */     if ((mod & 0x600) == 0) {
/* 257 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 262 */     return this._class.isPrimitive();
/*     */   }
/*     */   
/*     */   public boolean isThrowable() {
/* 266 */     return Throwable.class.isAssignableFrom(this._class);
/*     */   }
/*     */   
/* 269 */   public boolean isArrayType() { return false; }
/*     */   
/*     */   public final boolean isEnumType() {
/* 272 */     return this._class.isEnum();
/*     */   }
/*     */   
/* 275 */   public final boolean isInterface() { return this._class.isInterface(); }
/*     */   
/*     */   public final boolean isPrimitive() {
/* 278 */     return this._class.isPrimitive();
/*     */   }
/*     */   
/* 281 */   public final boolean isFinal() { return Modifier.isFinal(this._class.getModifiers()); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean isContainerType();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCollectionLikeType()
/*     */   {
/* 296 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isMapLikeType()
/*     */   {
/* 304 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isJavaLangObject()
/*     */   {
/* 315 */     return this._class == Object.class;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean useStaticType()
/*     */   {
/* 325 */     return this._asStatic;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasGenericTypes()
/*     */   {
/* 334 */     return containedTypeCount() > 0;
/*     */   }
/*     */   
/* 337 */   public JavaType getKeyType() { return null; }
/*     */   
/*     */   public JavaType getContentType() {
/* 340 */     return null;
/*     */   }
/*     */   
/* 343 */   public JavaType getReferencedType() { return null; }
/*     */   
/*     */ 
/*     */   public abstract int containedTypeCount();
/*     */   
/*     */ 
/*     */   public abstract JavaType containedType(int paramInt);
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public abstract String containedTypeName(int paramInt);
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> getParameterSource()
/*     */   {
/* 358 */     return null;
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType containedTypeOrUnknown(int index)
/*     */   {
/* 384 */     JavaType t = containedType(index);
/* 385 */     return t == null ? TypeFactory.unknownType() : t;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract TypeBindings getBindings();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JavaType findSuperType(Class<?> paramClass);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JavaType getSuperClass();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<JavaType> getInterfaces();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JavaType[] findTypeParameters(Class<?> paramClass);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T getValueHandler()
/*     */   {
/* 438 */     return (T)this._valueHandler;
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getTypeHandler()
/*     */   {
/* 444 */     return (T)this._typeHandler;
/*     */   }
/*     */   
/*     */   public Object getContentValueHandler()
/*     */   {
/* 449 */     return null;
/*     */   }
/*     */   
/*     */   public Object getContentTypeHandler()
/*     */   {
/* 454 */     return null;
/*     */   }
/*     */   
/*     */   public boolean hasValueHandler()
/*     */   {
/* 459 */     return this._valueHandler != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasHandlers()
/*     */   {
/* 470 */     return (this._typeHandler != null) || (this._valueHandler != null);
/*     */   }
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
/*     */   public String getGenericSignature()
/*     */   {
/* 490 */     StringBuilder sb = new StringBuilder(40);
/* 491 */     getGenericSignature(sb);
/* 492 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract StringBuilder getGenericSignature(StringBuilder paramStringBuilder);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getErasedSignature()
/*     */   {
/* 511 */     StringBuilder sb = new StringBuilder(40);
/* 512 */     getErasedSignature(sb);
/* 513 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract StringBuilder getErasedSignature(StringBuilder paramStringBuilder);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String toString();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean equals(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 542 */     return this._hash;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\JavaType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */