/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.Id;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DatabindContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.EnumMap;
/*     */ import java.util.EnumSet;
/*     */ 
/*     */ public class ClassNameIdResolver
/*     */   extends TypeIdResolverBase
/*     */ {
/*     */   public ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory)
/*     */   {
/*  20 */     super(baseType, typeFactory);
/*     */   }
/*     */   
/*     */   public JsonTypeInfo.Id getMechanism() {
/*  24 */     return JsonTypeInfo.Id.CLASS;
/*     */   }
/*     */   
/*     */ 
/*     */   public void registerSubtype(Class<?> type, String name) {}
/*     */   
/*     */   public String idFromValue(Object value)
/*     */   {
/*  32 */     return _idFrom(value, value.getClass(), this._typeFactory);
/*     */   }
/*     */   
/*     */   public String idFromValueAndType(Object value, Class<?> type)
/*     */   {
/*  37 */     return _idFrom(value, type, this._typeFactory);
/*     */   }
/*     */   
/*     */   public JavaType typeFromId(DatabindContext context, String id) throws IOException
/*     */   {
/*  42 */     return _typeFromId(id, context);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JavaType _typeFromId(String id, DatabindContext ctxt)
/*     */     throws IOException
/*     */   {
/*  51 */     TypeFactory tf = ctxt.getTypeFactory();
/*  52 */     if (id.indexOf('<') > 0)
/*     */     {
/*  54 */       return tf.constructFromCanonical(id);
/*     */     }
/*     */     Class<?> cls;
/*     */     try {
/*  58 */       cls = tf.findClass(id);
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/*  62 */       if ((ctxt instanceof DeserializationContext)) {
/*  63 */         DeserializationContext dctxt = (DeserializationContext)ctxt;
/*     */         
/*  65 */         return dctxt.handleUnknownTypeId(this._baseType, id, this, "no such class found");
/*     */       }
/*     */       
/*  68 */       return null;
/*     */     } catch (Exception e) {
/*  70 */       throw new IllegalArgumentException("Invalid type id '" + id + "' (for id type 'Id.class'): " + e.getMessage(), e);
/*     */     }
/*  72 */     return tf.constructSpecializedType(this._baseType, cls);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final String _idFrom(Object value, Class<?> cls, TypeFactory typeFactory)
/*     */   {
/*  84 */     if ((Enum.class.isAssignableFrom(cls)) && 
/*  85 */       (!cls.isEnum())) {
/*  86 */       cls = cls.getSuperclass();
/*     */     }
/*     */     
/*  89 */     String str = cls.getName();
/*  90 */     if (str.startsWith("java.util"))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */       if ((value instanceof EnumSet)) {
/*  98 */         Class<?> enumClass = ClassUtil.findEnumType((EnumSet)value);
/*     */         
/* 100 */         str = typeFactory.constructCollectionType(EnumSet.class, enumClass).toCanonical();
/* 101 */       } else if ((value instanceof EnumMap)) {
/* 102 */         Class<?> enumClass = ClassUtil.findEnumType((EnumMap)value);
/* 103 */         Class<?> valueClass = Object.class;
/*     */         
/* 105 */         str = typeFactory.constructMapType(EnumMap.class, enumClass, valueClass).toCanonical();
/*     */       } else {
/* 107 */         String end = str.substring(9);
/* 108 */         if (((end.startsWith(".Arrays$")) || (end.startsWith(".Collections$"))) && (str.indexOf("List") >= 0))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 116 */           str = "java.util.ArrayList";
/*     */         }
/*     */       }
/* 119 */     } else if (str.indexOf('$') >= 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 127 */       Class<?> outer = ClassUtil.getOuterClass(cls);
/* 128 */       if (outer != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 133 */         Class<?> staticType = this._baseType.getRawClass();
/* 134 */         if (ClassUtil.getOuterClass(staticType) == null)
/*     */         {
/* 136 */           cls = this._baseType.getRawClass();
/* 137 */           str = cls.getName();
/*     */         }
/*     */       }
/*     */     }
/* 141 */     return str;
/*     */   }
/*     */   
/*     */   public String getDescForKnownTypeIds()
/*     */   {
/* 146 */     return "class name used as type id";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\impl\ClassNameIdResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */