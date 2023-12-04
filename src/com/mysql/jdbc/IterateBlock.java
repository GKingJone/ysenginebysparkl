/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Iterator;
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
/*    */ public abstract class IterateBlock<T>
/*    */ {
/*    */   DatabaseMetaData.IteratorWithCleanup<T> iteratorWithCleanup;
/*    */   Iterator<T> javaIterator;
/* 34 */   boolean stopIterating = false;
/*    */   
/*    */   IterateBlock(DatabaseMetaData.IteratorWithCleanup<T> i) {
/* 37 */     this.iteratorWithCleanup = i;
/* 38 */     this.javaIterator = null;
/*    */   }
/*    */   
/*    */   IterateBlock(Iterator<T> i) {
/* 42 */     this.javaIterator = i;
/* 43 */     this.iteratorWithCleanup = null;
/*    */   }
/*    */   
/*    */   public void doForAll() throws SQLException {
/* 47 */     if (this.iteratorWithCleanup != null) {
/*    */       try {
/* 49 */         while (this.iteratorWithCleanup.hasNext()) {
/* 50 */           forEach(this.iteratorWithCleanup.next());
/*    */           
/* 52 */           if (this.stopIterating) {
/*    */             break;
/*    */           }
/*    */         }
/*    */       } finally {
/* 57 */         this.iteratorWithCleanup.close();
/*    */       }
/*    */     } else {
/* 60 */       while (this.javaIterator.hasNext()) {
/* 61 */         forEach(this.javaIterator.next());
/*    */         
/* 63 */         if (this.stopIterating) {
/*    */           break;
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   abstract void forEach(T paramT) throws SQLException;
/*    */   
/*    */   public final boolean fullIteration() {
/* 73 */     return !this.stopIterating;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\IterateBlock.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */