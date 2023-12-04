/*    */ package com.facebook.presto.jdbc;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpRequestFilter;
/*    */ import com.facebook.presto.jdbc.internal.airlift.http.client.Request;
/*    */ import com.facebook.presto.jdbc.internal.airlift.http.client.Request.Builder;
/*    */ import java.util.Objects;
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
/*    */ class UserAgentRequestFilter
/*    */   implements HttpRequestFilter
/*    */ {
/*    */   private final String userAgent;
/*    */   
/*    */   public UserAgentRequestFilter(String userAgent)
/*    */   {
/* 30 */     this.userAgent = ((String)Objects.requireNonNull(userAgent, "userAgent is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Request filterRequest(Request request)
/*    */   {
/* 36 */     return 
/*    */     
/* 38 */       Builder.fromRequest(request).addHeader("User-Agent", this.userAgent).build();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\UserAgentRequestFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */