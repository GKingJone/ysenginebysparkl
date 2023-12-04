/*    */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.StandardCharsets;
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
/*    */ public class StringContentProvider
/*    */   extends BytesContentProvider
/*    */ {
/*    */   public StringContentProvider(String content)
/*    */   {
/* 36 */     this(content, StandardCharsets.UTF_8);
/*    */   }
/*    */   
/*    */   public StringContentProvider(String content, String encoding)
/*    */   {
/* 41 */     this(content, Charset.forName(encoding));
/*    */   }
/*    */   
/*    */   public StringContentProvider(String content, Charset charset)
/*    */   {
/* 46 */     this("text/plain;charset=" + charset.name(), content, charset);
/*    */   }
/*    */   
/*    */   public StringContentProvider(String contentType, String content, Charset charset)
/*    */   {
/* 51 */     super(contentType, new byte[][] { content.getBytes(charset) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\StringContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */