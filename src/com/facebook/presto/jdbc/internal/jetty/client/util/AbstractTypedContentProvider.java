/*    */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider.Typed;
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
/*    */ public abstract class AbstractTypedContentProvider
/*    */   implements ContentProvider.Typed
/*    */ {
/*    */   private final String contentType;
/*    */   
/*    */   protected AbstractTypedContentProvider(String contentType)
/*    */   {
/* 29 */     this.contentType = contentType;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getContentType()
/*    */   {
/* 35 */     return this.contentType;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\AbstractTypedContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */