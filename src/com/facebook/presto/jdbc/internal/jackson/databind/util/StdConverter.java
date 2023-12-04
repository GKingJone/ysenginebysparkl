/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
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
/*    */ public abstract class StdConverter<IN, OUT>
/*    */   implements Converter<IN, OUT>
/*    */ {
/*    */   public abstract OUT convert(IN paramIN);
/*    */   
/*    */   public JavaType getInputType(TypeFactory typeFactory)
/*    */   {
/* 27 */     return _findConverterType(typeFactory).containedType(0);
/*    */   }
/*    */   
/*    */   public JavaType getOutputType(TypeFactory typeFactory)
/*    */   {
/* 32 */     return _findConverterType(typeFactory).containedType(1);
/*    */   }
/*    */   
/*    */   protected JavaType _findConverterType(TypeFactory tf) {
/* 36 */     JavaType thisType = tf.constructType(getClass());
/* 37 */     JavaType convType = thisType.findSuperType(Converter.class);
/* 38 */     if ((convType == null) || (convType.containedTypeCount() < 2)) {
/* 39 */       throw new IllegalStateException("Can not find OUT type parameter for Converter of type " + getClass().getName());
/*    */     }
/* 41 */     return convType;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\StdConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */