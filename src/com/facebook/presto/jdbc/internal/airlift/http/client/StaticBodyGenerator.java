/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import java.nio.charset.Charset;
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
/*    */ public class StaticBodyGenerator
/*    */   implements BodyGenerator
/*    */ {
/*    */   private final byte[] body;
/*    */   
/*    */   public static StaticBodyGenerator createStaticBodyGenerator(String body, Charset charset)
/*    */   {
/* 25 */     return new StaticBodyGenerator(body.getBytes(charset));
/*    */   }
/*    */   
/*    */   public static StaticBodyGenerator createStaticBodyGenerator(byte[] body)
/*    */   {
/* 30 */     return new StaticBodyGenerator(body);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected StaticBodyGenerator(byte[] body)
/*    */   {
/* 37 */     this.body = body;
/*    */   }
/*    */   
/*    */   public byte[] getBody()
/*    */   {
/* 42 */     return this.body;
/*    */   }
/*    */   
/*    */ 
/*    */   public void write(OutputStream out)
/*    */     throws Exception
/*    */   {
/* 49 */     out.write(this.body);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\StaticBodyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */