/*    */ package com.facebook.presto.jdbc.internal.jackson.core;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JsonGenerationException
/*    */   extends JsonProcessingException
/*    */ {
/*    */   private static final long serialVersionUID = 123L;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected transient JsonGenerator _processor;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public JsonGenerationException(Throwable rootCause)
/*    */   {
/* 23 */     super(rootCause);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public JsonGenerationException(String msg) {
/* 28 */     super(msg, (JsonLocation)null);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public JsonGenerationException(String msg, Throwable rootCause) {
/* 33 */     super(msg, null, rootCause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public JsonGenerationException(Throwable rootCause, JsonGenerator g)
/*    */   {
/* 40 */     super(rootCause);
/* 41 */     this._processor = g;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public JsonGenerationException(String msg, JsonGenerator g)
/*    */   {
/* 48 */     super(msg, (JsonLocation)null);
/* 49 */     this._processor = g;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g)
/*    */   {
/* 56 */     super(msg, null, rootCause);
/* 57 */     this._processor = g;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonGenerationException withGenerator(JsonGenerator g)
/*    */   {
/* 67 */     this._processor = g;
/* 68 */     return this;
/*    */   }
/*    */   
/*    */   public JsonGenerator getProcessor() {
/* 72 */     return this._processor;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\JsonGenerationException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */