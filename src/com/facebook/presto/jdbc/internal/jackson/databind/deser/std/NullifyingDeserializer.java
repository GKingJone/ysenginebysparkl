/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NullifyingDeserializer
/*    */   extends StdDeserializer<Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 20 */   public static final NullifyingDeserializer instance = new NullifyingDeserializer();
/*    */   
/* 22 */   public NullifyingDeserializer() { super(Object.class); }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 35 */     if (p.hasToken(JsonToken.FIELD_NAME)) {
/*    */       for (;;) {
/* 37 */         JsonToken t = p.nextToken();
/* 38 */         if ((t == null) || (t == JsonToken.END_OBJECT)) {
/*    */           break;
/*    */         }
/* 41 */         p.skipChildren();
/*    */       }
/*    */     }
/* 44 */     p.skipChildren();
/*    */     
/* 46 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*    */     throws IOException
/*    */   {
/* 55 */     switch (p.getCurrentTokenId()) {
/*    */     case 1: 
/*    */     case 3: 
/*    */     case 5: 
/* 59 */       return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*    */     }
/* 61 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\NullifyingDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */