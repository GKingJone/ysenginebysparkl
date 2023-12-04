/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
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
/*    */ public class ProtocolHandlers
/*    */ {
/* 32 */   private final Map<String, ProtocolHandler> handlers = new LinkedHashMap();
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
/*    */   public ProtocolHandler put(ProtocolHandler protocolHandler)
/*    */   {
/* 50 */     return (ProtocolHandler)this.handlers.put(protocolHandler.getName(), protocolHandler);
/*    */   }
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
/*    */   public ProtocolHandler remove(String name)
/*    */   {
/* 64 */     return (ProtocolHandler)this.handlers.remove(name);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void clear()
/*    */   {
/* 72 */     this.handlers.clear();
/*    */   }
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
/*    */   public ProtocolHandler find(Request request, Response response)
/*    */   {
/* 87 */     for (ProtocolHandler handler : this.handlers.values())
/*    */     {
/* 89 */       if (handler.accept(request, response))
/* 90 */         return handler;
/*    */     }
/* 92 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\ProtocolHandlers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */