/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
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
/*    */ public abstract class NonTypedScalarSerializerBase<T>
/*    */   extends StdScalarSerializer<T>
/*    */ {
/*    */   protected NonTypedScalarSerializerBase(Class<T> t)
/*    */   {
/* 21 */     super(t);
/*    */   }
/*    */   
/*    */   protected NonTypedScalarSerializerBase(Class<?> t, boolean bogus) {
/* 25 */     super(t, bogus);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public final void serializeWithType(T value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 33 */     serialize(value, gen, provider);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\NonTypedScalarSerializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */