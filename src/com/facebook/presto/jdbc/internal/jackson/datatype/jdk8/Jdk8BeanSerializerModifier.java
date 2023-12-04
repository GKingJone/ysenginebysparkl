/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanSerializerModifier;
/*    */ import java.util.List;
/*    */ import java.util.Optional;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Jdk8BeanSerializerModifier
/*    */   extends BeanSerializerModifier
/*    */ {
/*    */   public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
/*    */   {
/* 23 */     for (int i = 0; i < beanProperties.size(); i++) {
/* 24 */       BeanPropertyWriter writer = (BeanPropertyWriter)beanProperties.get(i);
/* 25 */       JavaType type = writer.getType();
/* 26 */       if (type.isTypeOrSubTypeOf(Optional.class)) {
/* 27 */         beanProperties.set(i, new Jdk8OptionalBeanPropertyWriter(writer));
/*    */       }
/*    */     }
/* 30 */     return beanProperties;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\Jdk8BeanSerializerModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */