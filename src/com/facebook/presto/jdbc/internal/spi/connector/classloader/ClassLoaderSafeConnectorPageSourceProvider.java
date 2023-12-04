/*    */ package com.facebook.presto.jdbc.internal.spi.connector.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorPageSource;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplit;
/*    */ import com.facebook.presto.jdbc.internal.spi.classloader.ThreadContextClassLoader;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorPageSourceProvider;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorTransactionHandle;
/*    */ import java.util.List;
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
/*    */ 
/*    */ public class ClassLoaderSafeConnectorPageSourceProvider
/*    */   implements ConnectorPageSourceProvider
/*    */ {
/*    */   private final ConnectorPageSourceProvider delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeConnectorPageSourceProvider(ConnectorPageSourceProvider delegate, ClassLoader classLoader)
/*    */   {
/* 36 */     this.delegate = ((ConnectorPageSourceProvider)Objects.requireNonNull(delegate, "delegate is null"));
/* 37 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public ConnectorPageSource createPageSource(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorSplit split, List<ColumnHandle> columns)
/*    */   {
/* 43 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 44 */     try { return this.delegate.createPageSource(transactionHandle, session, split, columns);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 43 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 45 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\classloader\ClassLoaderSafeConnectorPageSourceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */