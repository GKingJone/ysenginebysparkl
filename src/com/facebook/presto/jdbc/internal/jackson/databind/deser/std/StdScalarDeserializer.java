/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class StdScalarDeserializer<T>
/*    */   extends StdDeserializer<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 18 */   protected StdScalarDeserializer(Class<?> vc) { super(vc); }
/* 19 */   protected StdScalarDeserializer(JavaType valueType) { super(valueType); }
/*    */   
/*    */   protected StdScalarDeserializer(StdScalarDeserializer<?> src) {
/* 22 */     super(src);
/*    */   }
/*    */   
/*    */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 26 */     return typeDeserializer.deserializeTypedFromScalar(jp, ctxt);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StdScalarDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */