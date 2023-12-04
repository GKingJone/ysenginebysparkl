/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanSerializerModifier;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GuavaBeanSerializerModifier
/*    */   extends BeanSerializerModifier
/*    */ {
/*    */   public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
/*    */   {
/* 22 */     for (int i = 0; i < beanProperties.size(); i++) {
/* 23 */       BeanPropertyWriter writer = (BeanPropertyWriter)beanProperties.get(i);
/* 24 */       if (Optional.class.isAssignableFrom(writer.getType().getRawClass())) {
/* 25 */         beanProperties.set(i, new GuavaOptionalBeanPropertyWriter(writer));
/*    */       }
/*    */     }
/* 28 */     return beanProperties;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\ser\GuavaBeanSerializerModifier.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */