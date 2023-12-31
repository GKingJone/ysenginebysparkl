/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.KeyDeserializers;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.EnumResolver;
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Method;
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
/*    */ public class StdKeyDeserializers
/*    */   implements KeyDeserializers, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver)
/*    */   {
/* 34 */     return new StdKeyDeserializer.EnumKD(enumResolver, null);
/*    */   }
/*    */   
/*    */   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver, AnnotatedMethod factory)
/*    */   {
/* 39 */     return new StdKeyDeserializer.EnumKD(enumResolver, factory);
/*    */   }
/*    */   
/*    */ 
/*    */   public static KeyDeserializer constructDelegatingKeyDeserializer(DeserializationConfig config, JavaType type, JsonDeserializer<?> deser)
/*    */   {
/* 45 */     return new StdKeyDeserializer.DelegatingKD(type.getRawClass(), deser);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static KeyDeserializer findStringBasedKeyDeserializer(DeserializationConfig config, JavaType type)
/*    */   {
/* 54 */     BeanDescription beanDesc = config.introspect(type);
/*    */     
/* 56 */     Constructor<?> ctor = beanDesc.findSingleArgConstructor(new Class[] { String.class });
/* 57 */     if (ctor != null) {
/* 58 */       if (config.canOverrideAccessModifiers()) {
/* 59 */         ClassUtil.checkAndFixAccess(ctor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*    */       }
/* 61 */       return new StdKeyDeserializer.StringCtorKeyDeserializer(ctor);
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 66 */     Method m = beanDesc.findFactoryMethod(new Class[] { String.class });
/* 67 */     if (m != null) {
/* 68 */       if (config.canOverrideAccessModifiers()) {
/* 69 */         ClassUtil.checkAndFixAccess(m, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*    */       }
/* 71 */       return new StdKeyDeserializer.StringFactoryKeyDeserializer(m);
/*    */     }
/*    */     
/* 74 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
/*    */     throws JsonMappingException
/*    */   {
/* 87 */     Class<?> raw = type.getRawClass();
/*    */     
/* 89 */     if (raw.isPrimitive()) {
/* 90 */       raw = ClassUtil.wrapperType(raw);
/*    */     }
/* 92 */     return StdKeyDeserializer.forType(raw);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StdKeyDeserializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */