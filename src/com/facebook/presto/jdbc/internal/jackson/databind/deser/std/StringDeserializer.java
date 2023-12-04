/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public final class StringDeserializer
/*    */   extends StdScalarDeserializer<String>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   public static final StringDeserializer instance = new StringDeserializer();
/*    */   
/* 21 */   public StringDeserializer() { super(String.class); }
/*    */   
/*    */   public boolean isCachable()
/*    */   {
/* 25 */     return true;
/*    */   }
/*    */   
/*    */   public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
/*    */   {
/* 30 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 31 */       return p.getText();
/*    */     }
/* 33 */     JsonToken t = p.getCurrentToken();
/*    */     
/* 35 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/* 36 */       p.nextToken();
/* 37 */       String parsed = _parseString(p, ctxt);
/* 38 */       if (p.nextToken() != JsonToken.END_ARRAY) {
/* 39 */         handleMissingEndArrayForSingle(p, ctxt);
/*    */       }
/* 41 */       return parsed;
/*    */     }
/*    */     
/* 44 */     if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 45 */       Object ob = p.getEmbeddedObject();
/* 46 */       if (ob == null) {
/* 47 */         return null;
/*    */       }
/* 49 */       if ((ob instanceof byte[])) {
/* 50 */         return ctxt.getBase64Variant().encode((byte[])ob, false);
/*    */       }
/*    */       
/* 53 */       return ob.toString();
/*    */     }
/*    */     
/* 56 */     String text = p.getValueAsString();
/* 57 */     if (text != null) {
/* 58 */       return text;
/*    */     }
/* 60 */     return (String)ctxt.handleUnexpectedToken(this._valueClass, p);
/*    */   }
/*    */   
/*    */ 
/*    */   public String deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*    */     throws IOException
/*    */   {
/* 67 */     return deserialize(p, ctxt);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StringDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */