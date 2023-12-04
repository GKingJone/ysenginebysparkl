/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FailingDeserializer
/*    */   extends StdDeserializer<Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final String _message;
/*    */   
/*    */   public FailingDeserializer(String m)
/*    */   {
/* 21 */     super(Object.class);
/* 22 */     this._message = m;
/*    */   }
/*    */   
/*    */   public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws JsonMappingException
/*    */   {
/* 27 */     ctxt.reportMappingException(this._message, new Object[0]);
/* 28 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\FailingDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */