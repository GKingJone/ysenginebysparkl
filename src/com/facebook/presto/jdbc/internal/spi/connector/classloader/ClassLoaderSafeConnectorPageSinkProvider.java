/*    */ package com.facebook.presto.jdbc.internal.spi.connector.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorInsertTableHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorOutputTableHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorPageSink;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.classloader.ClassLoaderSafeConnectorPageSink;
/*    */ import com.facebook.presto.jdbc.internal.spi.classloader.ThreadContextClassLoader;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorPageSinkProvider;
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
/*    */ public final class ClassLoaderSafeConnectorPageSinkProvider
/*    */   implements ConnectorPageSinkProvider
/*    */ {
/*    */   private final ConnectorPageSinkProvider delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeConnectorPageSinkProvider(ConnectorPageSinkProvider delegate, ClassLoader classLoader)
/*    */   {
/* 35 */     this.delegate = ((ConnectorPageSinkProvider)Objects.requireNonNull(delegate, "delegate is null"));
/* 36 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public ConnectorPageSink createPageSink(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorOutputTableHandle outputTableHandle)
/*    */   {
/* 42 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 43 */     try { return new ClassLoaderSafeConnectorPageSink(this.delegate.createPageSink(transactionHandle, session, outputTableHandle), this.classLoader);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 42 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 44 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public ConnectorPageSink createPageSink(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorInsertTableHandle insertTableHandle)
/*    */   {
/* 50 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 51 */     try { return new ClassLoaderSafeConnectorPageSink(this.delegate.createPageSink(transactionHandle, session, insertTableHandle), this.classLoader);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 50 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 52 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\classloader\ClassLoaderSafeConnectorPageSinkProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */