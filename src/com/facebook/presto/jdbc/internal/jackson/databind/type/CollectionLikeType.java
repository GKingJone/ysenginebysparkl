/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Collection;
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
/*     */ public class CollectionLikeType
/*     */   extends TypeBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _elementType;
/*     */   
/*     */   protected CollectionLikeType(Class<?> collT, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT, Object valueHandler, Object typeHandler, boolean asStatic)
/*     */   {
/*  34 */     super(collT, bindings, superClass, superInts, elemT.hashCode(), valueHandler, typeHandler, asStatic);
/*     */     
/*  36 */     this._elementType = elemT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CollectionLikeType(TypeBase base, JavaType elemT)
/*     */   {
/*  44 */     super(base);
/*  45 */     this._elementType = elemT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CollectionLikeType construct(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT)
/*     */   {
/*  53 */     return new CollectionLikeType(rawType, bindings, superClass, superInts, elemT, null, null, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static CollectionLikeType construct(Class<?> rawType, JavaType elemT)
/*     */   {
/*  65 */     TypeVariable<?>[] vars = rawType.getTypeParameters();
/*     */     TypeBindings bindings;
/*  67 */     TypeBindings bindings; if ((vars == null) || (vars.length != 1)) {
/*  68 */       bindings = TypeBindings.emptyBindings();
/*     */     } else {
/*  70 */       bindings = TypeBindings.create(rawType, elemT);
/*     */     }
/*  72 */     return new CollectionLikeType(rawType, bindings, _bogusSuperClass(rawType), null, elemT, null, null, false);
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
/*     */   public static CollectionLikeType upgradeFrom(JavaType baseType, JavaType elementType)
/*     */   {
/*  86 */     if ((baseType instanceof TypeBase)) {
/*  87 */       return new CollectionLikeType((TypeBase)baseType, elementType);
/*     */     }
/*  89 */     throw new IllegalArgumentException("Can not upgrade from an instance of " + baseType.getClass());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected JavaType _narrow(Class<?> subclass)
/*     */   {
/*  95 */     return new CollectionLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JavaType withContentType(JavaType contentType)
/*     */   {
/* 102 */     if (this._elementType == contentType) {
/* 103 */       return this;
/*     */     }
/* 105 */     return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */   public CollectionLikeType withTypeHandler(Object h)
/*     */   {
/* 111 */     return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, h, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public CollectionLikeType withContentTypeHandler(Object h)
/*     */   {
/* 118 */     return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public CollectionLikeType withValueHandler(Object h)
/*     */   {
/* 125 */     return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, h, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */   public CollectionLikeType withContentValueHandler(Object h)
/*     */   {
/* 131 */     return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public CollectionLikeType withStaticTyping()
/*     */   {
/* 138 */     if (this._asStatic) {
/* 139 */       return this;
/*     */     }
/* 141 */     return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*     */   {
/* 149 */     return new CollectionLikeType(rawType, bindings, superClass, superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isContainerType()
/*     */   {
/* 161 */     return true;
/*     */   }
/*     */   
/* 164 */   public boolean isCollectionLikeType() { return true; }
/*     */   
/*     */   public JavaType getContentType() {
/* 167 */     return this._elementType;
/*     */   }
/*     */   
/*     */   public Object getContentValueHandler() {
/* 171 */     return this._elementType.getValueHandler();
/*     */   }
/*     */   
/*     */   public Object getContentTypeHandler()
/*     */   {
/* 176 */     return this._elementType.getTypeHandler();
/*     */   }
/*     */   
/*     */   public boolean hasHandlers()
/*     */   {
/* 181 */     return (super.hasHandlers()) || (this._elementType.hasHandlers());
/*     */   }
/*     */   
/*     */   public StringBuilder getErasedSignature(StringBuilder sb)
/*     */   {
/* 186 */     return _classSignature(this._class, sb, true);
/*     */   }
/*     */   
/*     */   public StringBuilder getGenericSignature(StringBuilder sb)
/*     */   {
/* 191 */     _classSignature(this._class, sb, false);
/* 192 */     sb.append('<');
/* 193 */     this._elementType.getGenericSignature(sb);
/* 194 */     sb.append(">;");
/* 195 */     return sb;
/*     */   }
/*     */   
/*     */   protected String buildCanonicalName()
/*     */   {
/* 200 */     StringBuilder sb = new StringBuilder();
/* 201 */     sb.append(this._class.getName());
/* 202 */     if (this._elementType != null) {
/* 203 */       sb.append('<');
/* 204 */       sb.append(this._elementType.toCanonical());
/* 205 */       sb.append('>');
/*     */     }
/* 207 */     return sb.toString();
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
/*     */   public boolean isTrueCollectionType()
/*     */   {
/* 223 */     return Collection.class.isAssignableFrom(this._class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 235 */     if (o == this) return true;
/* 236 */     if (o == null) return false;
/* 237 */     if (o.getClass() != getClass()) { return false;
/*     */     }
/* 239 */     CollectionLikeType other = (CollectionLikeType)o;
/* 240 */     return (this._class == other._class) && (this._elementType.equals(other._elementType));
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 246 */     return "[collection-like type; class " + this._class.getName() + ", contains " + this._elementType + "]";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\type\CollectionLikeType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */