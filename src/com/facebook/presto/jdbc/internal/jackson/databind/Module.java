/*    */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonFactory.Feature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.Feature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Versioned;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MutableConfigOverride;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.BeanDeserializerModifier;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DeserializationProblemHandler;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.Deserializers;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.KeyDeserializers;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiators;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ClassIntrospector;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanSerializerModifier;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.Serializers;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeModifier;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Module
/*    */   implements Versioned
/*    */ {
/*    */   public abstract String getModuleName();
/*    */   
/*    */   public abstract Version version();
/*    */   
/*    */   public Object getTypeId()
/*    */   {
/* 59 */     return getClass().getName();
/*    */   }
/*    */   
/*    */   public abstract void setupModule(SetupContext paramSetupContext);
/*    */   
/*    */   public static abstract interface SetupContext
/*    */   {
/*    */     public abstract Version getMapperVersion();
/*    */     
/*    */     public abstract <C extends ObjectCodec> C getOwner();
/*    */     
/*    */     public abstract TypeFactory getTypeFactory();
/*    */     
/*    */     public abstract boolean isEnabled(MapperFeature paramMapperFeature);
/*    */     
/*    */     public abstract boolean isEnabled(DeserializationFeature paramDeserializationFeature);
/*    */     
/*    */     public abstract boolean isEnabled(SerializationFeature paramSerializationFeature);
/*    */     
/*    */     public abstract boolean isEnabled(JsonFactory.Feature paramFeature);
/*    */     
/*    */     public abstract boolean isEnabled(JsonParser.Feature paramFeature);
/*    */     
/*    */     public abstract boolean isEnabled(JsonGenerator.Feature paramFeature);
/*    */     
/*    */     public abstract MutableConfigOverride configOverride(Class<?> paramClass);
/*    */     
/*    */     public abstract void addDeserializers(Deserializers paramDeserializers);
/*    */     
/*    */     public abstract void addKeyDeserializers(KeyDeserializers paramKeyDeserializers);
/*    */     
/*    */     public abstract void addSerializers(Serializers paramSerializers);
/*    */     
/*    */     public abstract void addKeySerializers(Serializers paramSerializers);
/*    */     
/*    */     public abstract void addBeanDeserializerModifier(BeanDeserializerModifier paramBeanDeserializerModifier);
/*    */     
/*    */     public abstract void addBeanSerializerModifier(BeanSerializerModifier paramBeanSerializerModifier);
/*    */     
/*    */     public abstract void addAbstractTypeResolver(AbstractTypeResolver paramAbstractTypeResolver);
/*    */     
/*    */     public abstract void addTypeModifier(TypeModifier paramTypeModifier);
/*    */     
/*    */     public abstract void addValueInstantiators(ValueInstantiators paramValueInstantiators);
/*    */     
/*    */     public abstract void setClassIntrospector(ClassIntrospector paramClassIntrospector);
/*    */     
/*    */     public abstract void insertAnnotationIntrospector(AnnotationIntrospector paramAnnotationIntrospector);
/*    */     
/*    */     public abstract void appendAnnotationIntrospector(AnnotationIntrospector paramAnnotationIntrospector);
/*    */     
/*    */     public abstract void registerSubtypes(Class<?>... paramVarArgs);
/*    */     
/*    */     public abstract void registerSubtypes(NamedType... paramVarArgs);
/*    */     
/*    */     public abstract void setMixInAnnotations(Class<?> paramClass1, Class<?> paramClass2);
/*    */     
/*    */     public abstract void addDeserializationProblemHandler(DeserializationProblemHandler paramDeserializationProblemHandler);
/*    */     
/*    */     public abstract void setNamingStrategy(PropertyNamingStrategy paramPropertyNamingStrategy);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\Module.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */