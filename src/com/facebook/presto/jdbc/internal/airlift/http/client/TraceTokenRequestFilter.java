/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.tracetoken.TraceTokenManager;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.google.inject.Inject;
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
/*    */ public class TraceTokenRequestFilter
/*    */   implements HttpRequestFilter
/*    */ {
/*    */   public static final String TRACETOKEN_HEADER = "X-Airlift-Tracetoken";
/*    */   private final TraceTokenManager traceTokenManager;
/*    */   
/*    */   @Inject
/*    */   public TraceTokenRequestFilter(TraceTokenManager traceTokenManager)
/*    */   {
/* 33 */     this.traceTokenManager = ((TraceTokenManager)Preconditions.checkNotNull(traceTokenManager, "traceTokenManager is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Request filterRequest(Request request)
/*    */   {
/* 39 */     Preconditions.checkNotNull(request, "request is null");
/*    */     
/* 41 */     String token = this.traceTokenManager.getCurrentRequestToken();
/* 42 */     if (token == null) {
/* 43 */       return request;
/*    */     }
/*    */     
/* 46 */     return 
/*    */     
/* 48 */       Request.Builder.fromRequest(request).addHeader("X-Airlift-Tracetoken", token).build();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 54 */     if (this == obj) {
/* 55 */       return true;
/*    */     }
/* 57 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 58 */       return false;
/*    */     }
/* 60 */     TraceTokenRequestFilter o = (TraceTokenRequestFilter)obj;
/* 61 */     return this.traceTokenManager.equals(o.traceTokenManager);
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 67 */     return this.traceTokenManager.hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\TraceTokenRequestFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */