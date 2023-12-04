/*    */ package com.facebook.presto.jdbc.internal.jackson.core.io;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParseException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
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
/*    */ public class JsonEOFException
/*    */   extends JsonParseException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JsonToken _token;
/*    */   
/*    */   public JsonEOFException(JsonParser p, JsonToken token, String msg)
/*    */   {
/* 26 */     super(p, msg);
/* 27 */     this._token = token;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonToken getTokenBeingDecoded()
/*    */   {
/* 35 */     return this._token;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\io\JsonEOFException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */