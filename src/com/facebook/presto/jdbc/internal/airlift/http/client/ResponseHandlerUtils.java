/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*    */ import java.io.IOException;
/*    */ import java.net.ConnectException;
/*    */ import java.net.URI;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ResponseHandlerUtils
/*    */ {
/*    */   public static RuntimeException propagate(Request request, Throwable exception)
/*    */   {
/* 16 */     if ((exception instanceof ConnectException)) {
/* 17 */       throw new RuntimeIOException("Server refused connection: " + request.getUri().toASCIIString(), (ConnectException)exception);
/*    */     }
/* 19 */     if ((exception instanceof IOException)) {
/* 20 */       throw new RuntimeIOException((IOException)exception);
/*    */     }
/* 22 */     throw Throwables.propagate(exception);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\ResponseHandlerUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */