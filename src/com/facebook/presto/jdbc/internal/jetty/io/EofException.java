/*    */ package com.facebook.presto.jdbc.internal.jetty.io;
/*    */ 
/*    */ import java.io.EOFException;
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
/*    */ public class EofException
/*    */   extends EOFException
/*    */ {
/*    */   public EofException() {}
/*    */   
/*    */   public EofException(String reason)
/*    */   {
/* 38 */     super(reason);
/*    */   }
/*    */   
/*    */   public EofException(Throwable th)
/*    */   {
/* 43 */     if (th != null) {
/* 44 */       initCause(th);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\EofException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */