/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty.Std;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
/*    */ import java.io.IOException;
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
/*    */ public class ValueInjector
/*    */   extends BeanProperty.Std
/*    */ {
/*    */   protected final Object _valueId;
/*    */   
/*    */   public ValueInjector(PropertyName propName, JavaType type, Annotations contextAnnotations, AnnotatedMember mutator, Object valueId)
/*    */   {
/* 31 */     super(propName, type, null, contextAnnotations, mutator, PropertyMetadata.STD_OPTIONAL);
/*    */     
/* 33 */     this._valueId = valueId;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public ValueInjector(String propName, JavaType type, Annotations contextAnnotations, AnnotatedMember mutator, Object valueId)
/*    */   {
/* 41 */     this(new PropertyName(propName), type, contextAnnotations, mutator, valueId);
/*    */   }
/*    */   
/*    */   public Object findValue(DeserializationContext context, Object beanInstance)
/*    */   {
/* 46 */     return context.findInjectableValue(this._valueId, this, beanInstance);
/*    */   }
/*    */   
/*    */   public void inject(DeserializationContext context, Object beanInstance)
/*    */     throws IOException
/*    */   {
/* 52 */     this._member.setValue(beanInstance, findValue(context, beanInstance));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\ValueInjector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */