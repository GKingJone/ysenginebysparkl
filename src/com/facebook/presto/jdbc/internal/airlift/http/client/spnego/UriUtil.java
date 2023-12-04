/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client.spnego;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ 
/*    */ class UriUtil
/*    */ {
/*    */   public static URI normalizedUri(URI uri)
/*    */   {
/*    */     try
/*    */     {
/* 13 */       return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
/*    */     }
/*    */     catch (URISyntaxException e) {
/* 16 */       throw Throwables.propagate(e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\spnego\UriUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */