/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.VirtualBeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
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
/*    */ public class AttributePropertyWriter
/*    */   extends VirtualBeanPropertyWriter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final String _attrName;
/*    */   
/*    */   protected AttributePropertyWriter(String attrName, BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType)
/*    */   {
/* 35 */     this(attrName, propDef, contextAnnotations, declaredType, propDef.findInclusion());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected AttributePropertyWriter(String attrName, BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType, JsonInclude.Value inclusion)
/*    */   {
/* 42 */     super(propDef, contextAnnotations, declaredType, null, null, null, inclusion);
/*    */     
/*    */ 
/* 45 */     this._attrName = attrName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static AttributePropertyWriter construct(String attrName, BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType)
/*    */   {
/* 53 */     return new AttributePropertyWriter(attrName, propDef, contextAnnotations, declaredType);
/*    */   }
/*    */   
/*    */   protected AttributePropertyWriter(AttributePropertyWriter base)
/*    */   {
/* 58 */     super(base);
/* 59 */     this._attrName = base._attrName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config, AnnotatedClass declaringClass, BeanPropertyDefinition propDef, JavaType type)
/*    */   {
/* 69 */     throw new IllegalStateException("Should not be called on this type");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Object value(Object bean, JsonGenerator jgen, SerializerProvider prov)
/*    */     throws Exception
/*    */   {
/* 80 */     return prov.getAttribute(this._attrName);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\AttributePropertyWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */