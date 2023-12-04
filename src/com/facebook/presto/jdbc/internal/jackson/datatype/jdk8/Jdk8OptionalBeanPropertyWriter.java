/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*    */ import java.util.Optional;
/*    */ 
/*    */ public class Jdk8OptionalBeanPropertyWriter extends BeanPropertyWriter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Jdk8OptionalBeanPropertyWriter(BeanPropertyWriter base)
/*    */   {
/* 16 */     super(base);
/*    */   }
/*    */   
/*    */   protected Jdk8OptionalBeanPropertyWriter(BeanPropertyWriter base, PropertyName newName) {
/* 20 */     super(base, newName);
/*    */   }
/*    */   
/*    */   protected BeanPropertyWriter _new(PropertyName newName)
/*    */   {
/* 25 */     return new Jdk8OptionalBeanPropertyWriter(this, newName);
/*    */   }
/*    */   
/*    */   public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper)
/*    */   {
/* 30 */     return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, unwrapper);
/*    */   }
/*    */   
/*    */   public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
/*    */     throws Exception
/*    */   {
/* 36 */     if (this._nullSerializer == null) {
/* 37 */       Object value = get(bean);
/* 38 */       if ((value == null) || (Optional.empty().equals(value))) {
/* 39 */         return;
/*    */       }
/*    */     }
/* 42 */     super.serializeAsField(bean, jgen, prov);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\Jdk8OptionalBeanPropertyWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */