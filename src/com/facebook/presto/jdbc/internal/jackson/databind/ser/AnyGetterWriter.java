/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.MapSerializer;
/*    */ import java.util.Map;
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
/*    */ public class AnyGetterWriter
/*    */ {
/*    */   protected final BeanProperty _property;
/*    */   protected final AnnotatedMember _accessor;
/*    */   protected JsonSerializer<Object> _serializer;
/*    */   protected MapSerializer _mapSerializer;
/*    */   
/*    */   public AnyGetterWriter(BeanProperty property, AnnotatedMember accessor, JsonSerializer<?> serializer)
/*    */   {
/* 32 */     this._accessor = accessor;
/* 33 */     this._property = property;
/* 34 */     this._serializer = serializer;
/* 35 */     if ((serializer instanceof MapSerializer)) {
/* 36 */       this._mapSerializer = ((MapSerializer)serializer);
/*    */     }
/*    */   }
/*    */   
/*    */   public void getAndSerialize(Object bean, JsonGenerator gen, SerializerProvider provider)
/*    */     throws Exception
/*    */   {
/* 43 */     Object value = this._accessor.getValue(bean);
/* 44 */     if (value == null) {
/* 45 */       return;
/*    */     }
/* 47 */     if (!(value instanceof Map)) {
/* 48 */       provider.reportMappingProblem("Value returned by 'any-getter' %s() not java.util.Map but %s", new Object[] { this._accessor.getName(), value.getClass().getName() });
/*    */     }
/*    */     
/*    */ 
/* 52 */     if (this._mapSerializer != null) {
/* 53 */       this._mapSerializer.serializeFields((Map)value, gen, provider);
/* 54 */       return;
/*    */     }
/* 56 */     this._serializer.serialize(value, gen, provider);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void getAndFilter(Object bean, JsonGenerator gen, SerializerProvider provider, PropertyFilter filter)
/*    */     throws Exception
/*    */   {
/* 66 */     Object value = this._accessor.getValue(bean);
/* 67 */     if (value == null) {
/* 68 */       return;
/*    */     }
/* 70 */     if (!(value instanceof Map)) {
/* 71 */       provider.reportMappingProblem("Value returned by 'any-getter' (%s()) not java.util.Map but %s", new Object[] { this._accessor.getName(), value.getClass().getName() });
/*    */     }
/*    */     
/*    */ 
/* 75 */     if (this._mapSerializer != null) {
/* 76 */       this._mapSerializer.serializeFilteredFields((Map)value, gen, provider, filter, null);
/* 77 */       return;
/*    */     }
/*    */     
/* 80 */     this._serializer.serialize(value, gen, provider);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void resolve(SerializerProvider provider)
/*    */     throws JsonMappingException
/*    */   {
/* 88 */     if ((this._serializer instanceof ContextualSerializer)) {
/* 89 */       JsonSerializer<?> ser = provider.handlePrimaryContextualization(this._serializer, this._property);
/* 90 */       this._serializer = ser;
/* 91 */       if ((ser instanceof MapSerializer)) {
/* 92 */         this._mapSerializer = ((MapSerializer)ser);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\AnyGetterWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */