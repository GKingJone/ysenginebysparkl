/*    */ package com.google.common.io;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.VisibleForTesting;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public final class Closeables
/*    */ {
/*    */   @VisibleForTesting
/* 37 */   static final Logger logger = Logger.getLogger(Closeables.class.getName());
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
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void close(@Nullable Closeable closeable, boolean swallowIOException)
/*    */     throws IOException
/*    */   {
/* 73 */     if (closeable == null) {
/* 74 */       return;
/*    */     }
/*    */     try {
/* 77 */       closeable.close();
/*    */     } catch (IOException e) {
/* 79 */       if (swallowIOException) {
/* 80 */         logger.log(Level.WARNING, "IOException thrown while closing Closeable.", e);
/*    */       }
/*    */       else {
/* 83 */         throw e;
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\io\Closeables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */