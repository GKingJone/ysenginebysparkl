/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.io.SerializedString;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*    */ import java.util.Optional;
/*    */ 
/*    */ public class Jdk8UnwrappingOptionalBeanPropertyWriter
/*    */   extends UnwrappingBeanPropertyWriter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public Jdk8UnwrappingOptionalBeanPropertyWriter(BeanPropertyWriter base, NameTransformer transformer)
/*    */   {
/* 18 */     super(base, transformer);
/*    */   }
/*    */   
/*    */   protected Jdk8UnwrappingOptionalBeanPropertyWriter(UnwrappingBeanPropertyWriter base, NameTransformer transformer, SerializedString name)
/*    */   {
/* 23 */     super(base, transformer, name);
/*    */   }
/*    */   
/*    */ 
/*    */   protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName)
/*    */   {
/* 29 */     return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, transformer, newName);
/*    */   }
/*    */   
/*    */   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
/*    */     throws Exception
/*    */   {
/* 35 */     if (this._nullSerializer == null) {
/* 36 */       Object value = get(bean);
/* 37 */       if ((value == null) || (Optional.empty().equals(value))) {
/* 38 */         return;
/*    */       }
/*    */     }
/* 41 */     super.serializeAsField(bean, gen, prov);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\Jdk8UnwrappingOptionalBeanPropertyWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */