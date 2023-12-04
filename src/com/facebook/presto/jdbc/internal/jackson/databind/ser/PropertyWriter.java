/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ConcreteBeanPropertyBase;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*    */ import java.io.Serializable;
/*    */ import java.lang.annotation.Annotation;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class PropertyWriter
/*    */   extends ConcreteBeanPropertyBase
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected PropertyWriter(PropertyMetadata md)
/*    */   {
/* 27 */     super(md);
/*    */   }
/*    */   
/*    */   protected PropertyWriter(BeanPropertyDefinition propDef) {
/* 31 */     super(propDef.getMetadata());
/*    */   }
/*    */   
/*    */   protected PropertyWriter(PropertyWriter base) {
/* 35 */     super(base);
/*    */   }
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
/*    */   public abstract String getName();
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
/*    */   public abstract PropertyName getFullName();
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
/*    */   public <A extends Annotation> A findAnnotation(Class<A> acls)
/*    */   {
/* 71 */     A ann = getAnnotation(acls);
/* 72 */     if (ann == null) {
/* 73 */       ann = getContextAnnotation(acls);
/*    */     }
/* 75 */     return ann;
/*    */   }
/*    */   
/*    */   public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
/*    */   
/*    */   public abstract <A extends Annotation> A getContextAnnotation(Class<A> paramClass);
/*    */   
/*    */   public abstract void serializeAsField(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*    */     throws Exception;
/*    */   
/*    */   public abstract void serializeAsOmittedField(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*    */     throws Exception;
/*    */   
/*    */   public abstract void serializeAsElement(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*    */     throws Exception;
/*    */   
/*    */   public abstract void serializeAsPlaceholder(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*    */     throws Exception;
/*    */   
/*    */   public abstract void depositSchemaProperty(JsonObjectFormatVisitor paramJsonObjectFormatVisitor, SerializerProvider paramSerializerProvider)
/*    */     throws JsonMappingException;
/*    */   
/*    */   @Deprecated
/*    */   public abstract void depositSchemaProperty(ObjectNode paramObjectNode, SerializerProvider paramSerializerProvider)
/*    */     throws JsonMappingException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\PropertyWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */