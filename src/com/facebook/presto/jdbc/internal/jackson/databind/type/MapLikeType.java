/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Map;
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
/*     */ public class MapLikeType
/*     */   extends TypeBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _keyType;
/*     */   protected final JavaType _valueType;
/*     */   
/*     */   protected MapLikeType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic)
/*     */   {
/*  39 */     super(mapType, bindings, superClass, superInts, keyT.hashCode() ^ valueT.hashCode(), valueHandler, typeHandler, asStatic);
/*     */     
/*  41 */     this._keyType = keyT;
/*  42 */     this._valueType = valueT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected MapLikeType(TypeBase base, JavaType keyT, JavaType valueT)
/*     */   {
/*  49 */     super(base);
/*  50 */     this._keyType = keyT;
/*  51 */     this._valueType = valueT;
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
/*     */   public static MapLikeType upgradeFrom(JavaType baseType, JavaType keyT, JavaType valueT)
/*     */   {
/*  65 */     if ((baseType instanceof TypeBase)) {
/*  66 */       return new MapLikeType((TypeBase)baseType, keyT, valueT);
/*     */     }
/*  68 */     throw new IllegalArgumentException("Can not upgrade from an instance of " + baseType.getClass());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static MapLikeType construct(Class<?> rawType, JavaType keyT, JavaType valueT)
/*     */   {
/*  78 */     TypeVariable<?>[] vars = rawType.getTypeParameters();
/*     */     TypeBindings bindings;
/*  80 */     TypeBindings bindings; if ((vars == null) || (vars.length != 2)) {
/*  81 */       bindings = TypeBindings.emptyBindings();
/*     */     } else {
/*  83 */       bindings = TypeBindings.create(rawType, keyT, valueT);
/*     */     }
/*  85 */     return new MapLikeType(rawType, bindings, _bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected JavaType _narrow(Class<?> subclass)
/*     */   {
/*  93 */     return new MapLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MapLikeType withKeyType(JavaType keyType)
/*     */   {
/* 102 */     if (keyType == this._keyType) {
/* 103 */       return this;
/*     */     }
/* 105 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JavaType withContentType(JavaType contentType)
/*     */   {
/* 112 */     if (this._valueType == contentType) {
/* 113 */       return this;
/*     */     }
/* 115 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MapLikeType withTypeHandler(Object h)
/*     */   {
/* 122 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MapLikeType withContentTypeHandler(Object h)
/*     */   {
/* 129 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MapLikeType withValueHandler(Object h)
/*     */   {
/* 136 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MapLikeType withContentValueHandler(Object h)
/*     */   {
/* 143 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MapLikeType withStaticTyping()
/*     */   {
/* 150 */     if (this._asStatic) {
/* 151 */       return this;
/*     */     }
/* 153 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*     */   {
/* 161 */     return new MapLikeType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String buildCanonicalName()
/*     */   {
/* 167 */     StringBuilder sb = new StringBuilder();
/* 168 */     sb.append(this._class.getName());
/* 169 */     if (this._keyType != null) {
/* 170 */       sb.append('<');
/* 171 */       sb.append(this._keyType.toCanonical());
/* 172 */       sb.append(',');
/* 173 */       sb.append(this._valueType.toCanonical());
/* 174 */       sb.append('>');
/*     */     }
/* 176 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isContainerType()
/*     */   {
/* 187 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isMapLikeType()
/*     */   {
/* 192 */     return true;
/*     */   }
/*     */   
/*     */   public JavaType getKeyType()
/*     */   {
/* 197 */     return this._keyType;
/*     */   }
/*     */   
/*     */   public JavaType getContentType()
/*     */   {
/* 202 */     return this._valueType;
/*     */   }
/*     */   
/*     */   public Object getContentValueHandler()
/*     */   {
/* 207 */     return this._valueType.getValueHandler();
/*     */   }
/*     */   
/*     */   public Object getContentTypeHandler()
/*     */   {
/* 212 */     return this._valueType.getTypeHandler();
/*     */   }
/*     */   
/*     */   public boolean hasHandlers()
/*     */   {
/* 217 */     return (super.hasHandlers()) || (this._valueType.hasHandlers()) || (this._keyType.hasHandlers());
/*     */   }
/*     */   
/*     */ 
/*     */   public StringBuilder getErasedSignature(StringBuilder sb)
/*     */   {
/* 223 */     return _classSignature(this._class, sb, true);
/*     */   }
/*     */   
/*     */   public StringBuilder getGenericSignature(StringBuilder sb)
/*     */   {
/* 228 */     _classSignature(this._class, sb, false);
/* 229 */     sb.append('<');
/* 230 */     this._keyType.getGenericSignature(sb);
/* 231 */     this._valueType.getGenericSignature(sb);
/* 232 */     sb.append(">;");
/* 233 */     return sb;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MapLikeType withKeyTypeHandler(Object h)
/*     */   {
/* 243 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */   public MapLikeType withKeyValueHandler(Object h)
/*     */   {
/* 249 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTrueMapType()
/*     */   {
/* 260 */     return Map.class.isAssignableFrom(this._class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 271 */     return String.format("[map-like type; class %s, %s -> %s]", new Object[] { this._class.getName(), this._keyType, this._valueType });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 277 */     if (o == this) return true;
/* 278 */     if (o == null) return false;
/* 279 */     if (o.getClass() != getClass()) { return false;
/*     */     }
/* 281 */     MapLikeType other = (MapLikeType)o;
/* 282 */     return (this._class == other._class) && (this._keyType.equals(other._keyType)) && (this._valueType.equals(other._valueType));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\type\MapLikeType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */