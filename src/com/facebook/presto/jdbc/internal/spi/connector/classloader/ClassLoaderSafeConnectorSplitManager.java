/*    */ package com.facebook.presto.jdbc.internal.spi.connector.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplitSource;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableLayoutHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.classloader.ThreadContextClassLoader;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorSplitManager;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorTransactionHandle;
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
/*    */ public final class ClassLoaderSafeConnectorSplitManager
/*    */   implements ConnectorSplitManager
/*    */ {
/*    */   private final ConnectorSplitManager delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeConnectorSplitManager(ConnectorSplitManager delegate, ClassLoader classLoader)
/*    */   {
/* 33 */     this.delegate = ((ConnectorSplitManager)Objects.requireNonNull(delegate, "delegate is null"));
/* 34 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public ConnectorSplitSource getSplits(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorTableLayoutHandle layout)
/*    */   {
/* 40 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 41 */     try { return this.delegate.getSplits(transactionHandle, session, layout);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 40 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 42 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\classloader\ClassLoaderSafeConnectorSplitManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */