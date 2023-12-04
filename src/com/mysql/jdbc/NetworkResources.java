/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.net.Socket;
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
/*    */ class NetworkResources
/*    */ {
/*    */   private final Socket mysqlConnection;
/*    */   private final InputStream mysqlInput;
/*    */   private final OutputStream mysqlOutput;
/*    */   
/*    */   protected NetworkResources(Socket mysqlConnection, InputStream mysqlInput, OutputStream mysqlOutput)
/*    */   {
/* 37 */     this.mysqlConnection = mysqlConnection;
/* 38 */     this.mysqlInput = mysqlInput;
/* 39 */     this.mysqlOutput = mysqlOutput;
/*    */   }
/*    */   
/*    */   protected final void forceClose()
/*    */   {
/*    */     try
/*    */     {
/*    */       try
/*    */       {
/* 48 */         if (this.mysqlInput != null) {
/* 49 */           this.mysqlInput.close();
/*    */         }
/*    */       } finally {
/* 52 */         if ((this.mysqlConnection != null) && (!this.mysqlConnection.isClosed()) && (!this.mysqlConnection.isInputShutdown())) {
/*    */           try {
/* 54 */             this.mysqlConnection.shutdownInput();
/*    */           }
/*    */           catch (UnsupportedOperationException ex) {}
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (IOException ioEx) {}
/*    */     
/*    */     try
/*    */     {
/*    */       try
/*    */       {
/* 66 */         if (this.mysqlOutput != null) {
/* 67 */           this.mysqlOutput.close();
/*    */         }
/*    */       } finally {
/* 70 */         if ((this.mysqlConnection != null) && (!this.mysqlConnection.isClosed()) && (!this.mysqlConnection.isOutputShutdown())) {
/*    */           try {
/* 72 */             this.mysqlConnection.shutdownOutput();
/*    */           }
/*    */           catch (UnsupportedOperationException ex) {}
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (IOException ioEx) {}
/*    */     
/*    */ 
/*    */     try
/*    */     {
/* 83 */       if (this.mysqlConnection != null) {
/* 84 */         this.mysqlConnection.close();
/*    */       }
/*    */     }
/*    */     catch (IOException ioEx) {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\NetworkResources.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */