/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.util.concurrent.atomic.AtomicReference;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AtomicReferenceDeserializer
/*    */   extends ReferenceTypeDeserializer<AtomicReference<Object>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   @Deprecated
/*    */   public AtomicReferenceDeserializer(JavaType fullType)
/*    */   {
/* 21 */     this(fullType, null, null);
/*    */   }
/*    */   
/*    */ 
/*    */   public AtomicReferenceDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 27 */     super(fullType, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AtomicReferenceDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 38 */     return new AtomicReferenceDeserializer(this._fullType, typeDeser, valueDeser);
/*    */   }
/*    */   
/*    */   public AtomicReference<Object> getNullValue(DeserializationContext ctxt)
/*    */   {
/* 43 */     return new AtomicReference();
/*    */   }
/*    */   
/*    */   public AtomicReference<Object> referenceValue(Object contents)
/*    */   {
/* 48 */     return new AtomicReference(contents);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\AtomicReferenceDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */