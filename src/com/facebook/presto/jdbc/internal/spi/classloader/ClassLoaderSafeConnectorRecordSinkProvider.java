/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorInsertTableHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorOutputTableHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorRecordSinkProvider;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.RecordSink;
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
/*    */ public final class ClassLoaderSafeConnectorRecordSinkProvider
/*    */   implements ConnectorRecordSinkProvider
/*    */ {
/*    */   private final ConnectorRecordSinkProvider delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeConnectorRecordSinkProvider(ConnectorRecordSinkProvider delegate, ClassLoader classLoader)
/*    */   {
/* 32 */     this.delegate = ((ConnectorRecordSinkProvider)Objects.requireNonNull(delegate, "delegate is null"));
/* 33 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public RecordSink getRecordSink(ConnectorSession session, ConnectorOutputTableHandle tableHandle)
/*    */   {
/* 39 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 40 */     try { return this.delegate.getRecordSink(session, tableHandle);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 39 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 41 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public RecordSink getRecordSink(ConnectorSession session, ConnectorInsertTableHandle tableHandle)
/*    */   {
/* 47 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 48 */     try { return this.delegate.getRecordSink(session, tableHandle);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 47 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 49 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorRecordSinkProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */