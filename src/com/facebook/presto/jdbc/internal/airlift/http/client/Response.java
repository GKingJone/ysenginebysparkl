/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
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
/*    */ @Beta
/*    */ public abstract interface Response
/*    */ {
/*    */   public abstract int getStatusCode();
/*    */   
/*    */   public abstract String getStatusMessage();
/*    */   
/*    */   @Nullable
/*    */   public String getHeader(String name)
/*    */   {
/* 36 */     List<String> values = getHeaders(name);
/* 37 */     return values.isEmpty() ? null : (String)values.get(0);
/*    */   }
/*    */   
/*    */   public List<String> getHeaders(String name)
/*    */   {
/* 42 */     return getHeaders().get(HeaderName.of(name));
/*    */   }
/*    */   
/*    */   public abstract ListMultimap<HeaderName, String> getHeaders();
/*    */   
/*    */   public abstract long getBytesRead();
/*    */   
/*    */   public abstract InputStream getInputStream()
/*    */     throws IOException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\Response.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */