/*    */ package com.facebook.presto.jdbc.internal.jetty.http;
/*    */ 
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.Arrays;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Http1FieldPreEncoder
/*    */   implements HttpFieldPreEncoder
/*    */ {
/*    */   public HttpVersion getHttpVersion()
/*    */   {
/* 39 */     return HttpVersion.HTTP_1_0;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] getEncodedField(HttpHeader header, String headerString, String value)
/*    */   {
/* 49 */     if (header != null)
/*    */     {
/* 51 */       int cbl = header.getBytesColonSpace().length;
/* 52 */       byte[] bytes = Arrays.copyOf(header.getBytesColonSpace(), cbl + value.length() + 2);
/* 53 */       System.arraycopy(value.getBytes(StandardCharsets.ISO_8859_1), 0, bytes, cbl, value.length());
/* 54 */       bytes[(bytes.length - 2)] = 13;
/* 55 */       bytes[(bytes.length - 1)] = 10;
/* 56 */       return bytes;
/*    */     }
/*    */     
/* 59 */     byte[] n = headerString.getBytes(StandardCharsets.ISO_8859_1);
/* 60 */     byte[] v = value.getBytes(StandardCharsets.ISO_8859_1);
/* 61 */     byte[] bytes = Arrays.copyOf(n, n.length + 2 + v.length + 2);
/* 62 */     bytes[n.length] = 58;
/* 63 */     bytes[n.length] = 32;
/* 64 */     bytes[(bytes.length - 2)] = 13;
/* 65 */     bytes[(bytes.length - 1)] = 10;
/*    */     
/* 67 */     return bytes;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\Http1FieldPreEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */