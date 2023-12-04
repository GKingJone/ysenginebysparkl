/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer;
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
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public class TokenBufferDeserializer
/*    */   extends StdScalarDeserializer<TokenBuffer>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public TokenBufferDeserializer()
/*    */   {
/* 27 */     super(TokenBuffer.class);
/*    */   }
/*    */   
/*    */   public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 31 */     return createBufferInstance(p).deserialize(p, ctxt);
/*    */   }
/*    */   
/*    */   protected TokenBuffer createBufferInstance(JsonParser p) {
/* 35 */     return new TokenBuffer(p);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\TokenBufferDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */