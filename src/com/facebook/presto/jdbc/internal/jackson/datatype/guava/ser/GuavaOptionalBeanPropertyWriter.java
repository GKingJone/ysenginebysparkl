/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*    */ 
/*    */ public class GuavaOptionalBeanPropertyWriter extends BeanPropertyWriter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected GuavaOptionalBeanPropertyWriter(BeanPropertyWriter base)
/*    */   {
/* 15 */     super(base);
/*    */   }
/*    */   
/*    */   protected GuavaOptionalBeanPropertyWriter(BeanPropertyWriter base, PropertyName newName) {
/* 19 */     super(base, newName);
/*    */   }
/*    */   
/*    */   protected BeanPropertyWriter _new(PropertyName newName)
/*    */   {
/* 24 */     return new GuavaOptionalBeanPropertyWriter(this, newName);
/*    */   }
/*    */   
/*    */   public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper)
/*    */   {
/* 29 */     return new GuavaUnwrappingOptionalBeanPropertyWriter(this, unwrapper);
/*    */   }
/*    */   
/*    */   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
/*    */     throws Exception
/*    */   {
/* 35 */     if (this._nullSerializer == null) {
/* 36 */       Object value = get(bean);
/* 37 */       if ((value == null) || (com.facebook.presto.jdbc.internal.guava.base.Optional.absent().equals(value))) {
/* 38 */         return;
/*    */       }
/*    */     }
/* 41 */     super.serializeAsField(bean, gen, prov);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\ser\GuavaOptionalBeanPropertyWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */