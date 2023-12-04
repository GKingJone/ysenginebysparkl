/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.module;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.AbstractTypeResolver;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ClassKey;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Modifier;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleAbstractTypeResolver
/*    */   extends AbstractTypeResolver
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 8635483102371490919L;
/* 38 */   protected final HashMap<ClassKey, Class<?>> _mappings = new HashMap();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public <T> SimpleAbstractTypeResolver addMapping(Class<T> superType, Class<? extends T> subType)
/*    */   {
/* 55 */     if (superType == subType) {
/* 56 */       throw new IllegalArgumentException("Can not add mapping from class to itself");
/*    */     }
/* 58 */     if (!superType.isAssignableFrom(subType)) {
/* 59 */       throw new IllegalArgumentException("Can not add mapping from class " + superType.getName() + " to " + subType.getName() + ", as latter is not a subtype of former");
/*    */     }
/*    */     
/* 62 */     if (!Modifier.isAbstract(superType.getModifiers())) {
/* 63 */       throw new IllegalArgumentException("Can not add mapping from class " + superType.getName() + " since it is not abstract");
/*    */     }
/*    */     
/* 66 */     this._mappings.put(new ClassKey(superType), subType);
/* 67 */     return this;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public JavaType findTypeMapping(DeserializationConfig config, JavaType type)
/*    */   {
/* 74 */     Class<?> src = type.getRawClass();
/* 75 */     Class<?> dst = (Class)this._mappings.get(new ClassKey(src));
/* 76 */     if (dst == null) {
/* 77 */       return null;
/*    */     }
/*    */     
/* 80 */     return config.getTypeFactory().constructSpecializedType(type, dst);
/*    */   }
/*    */   
/*    */ 
/*    */   @Deprecated
/*    */   public JavaType resolveAbstractType(DeserializationConfig config, JavaType type)
/*    */   {
/* 87 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public JavaType resolveAbstractType(DeserializationConfig config, BeanDescription typeDesc)
/*    */   {
/* 94 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\module\SimpleAbstractTypeResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */