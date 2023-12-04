/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.io.SerializedString;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*    */ 
/*    */ public class GuavaUnwrappingOptionalBeanPropertyWriter extends UnwrappingBeanPropertyWriter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public GuavaUnwrappingOptionalBeanPropertyWriter(BeanPropertyWriter base, NameTransformer transformer)
/*    */   {
/* 17 */     super(base, transformer);
/*    */   }
/*    */   
/*    */   protected GuavaUnwrappingOptionalBeanPropertyWriter(UnwrappingBeanPropertyWriter base, NameTransformer transformer, SerializedString name)
/*    */   {
/* 22 */     super(base, transformer, name);
/*    */   }
/*    */   
/*    */ 
/*    */   protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName)
/*    */   {
/* 28 */     return new GuavaUnwrappingOptionalBeanPropertyWriter(this, transformer, newName);
/*    */   }
/*    */   
/*    */   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
/*    */     throws Exception
/*    */   {
/* 34 */     if (this._nullSerializer == null) {
/* 35 */       Object value = get(bean);
/* 36 */       if ((value == null) || (Optional.absent().equals(value))) {
/* 37 */         return;
/*    */       }
/*    */     }
/* 40 */     super.serializeAsField(bean, gen, prov);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\ser\GuavaUnwrappingOptionalBeanPropertyWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */