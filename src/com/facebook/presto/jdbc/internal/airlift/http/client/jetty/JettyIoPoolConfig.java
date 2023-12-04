/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client.jetty;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.configuration.Config;
/*    */ import com.facebook.presto.jdbc.internal.airlift.configuration.LegacyConfig;
/*    */ import javax.validation.constraints.Min;
/*    */ 
/*    */ 
/*    */ public class JettyIoPoolConfig
/*    */ {
/* 10 */   private int maxThreads = 200;
/* 11 */   private int minThreads = 8;
/*    */   
/*    */   @Min(1L)
/*    */   public int getMaxThreads()
/*    */   {
/* 16 */     return this.maxThreads;
/*    */   }
/*    */   
/*    */   @Config("http-client.max-threads")
/*    */   @LegacyConfig({"http-client.threads"})
/*    */   public JettyIoPoolConfig setMaxThreads(int maxThreads)
/*    */   {
/* 23 */     this.maxThreads = maxThreads;
/* 24 */     return this;
/*    */   }
/*    */   
/*    */   @Min(1L)
/*    */   public int getMinThreads()
/*    */   {
/* 30 */     return this.minThreads;
/*    */   }
/*    */   
/*    */   @Config("http-client.min-threads")
/*    */   public JettyIoPoolConfig setMinThreads(int minThreads)
/*    */   {
/* 36 */     this.minThreads = minThreads;
/* 37 */     return this;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\jetty\JettyIoPoolConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */