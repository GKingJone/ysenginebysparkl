/*    */ package com.google.common.io;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.io.Flushable;
/*    */ import java.io.IOException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
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
/*    */ public final class Flushables
/*    */ {
/* 34 */   private static final Logger logger = Logger.getLogger(Flushables.class.getName());
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
/*    */   public static void flush(Flushable flushable, boolean swallowIOException)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 56 */       flushable.flush();
/*    */     } catch (IOException e) {
/* 58 */       if (swallowIOException) {
/* 59 */         logger.log(Level.WARNING, "IOException thrown while flushing Flushable.", e);
/*    */       }
/*    */       else {
/* 62 */         throw e;
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void flushQuietly(Flushable flushable)
/*    */   {
/*    */     try
/*    */     {
/* 75 */       flush(flushable, true);
/*    */     } catch (IOException e) {
/* 77 */       logger.log(Level.SEVERE, "IOException should not have been thrown.", e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\io\Flushables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */