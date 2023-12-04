/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorInsertTableHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorOutputTableHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorPageSink;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorPageSinkProvider;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
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
/* 32 */     this.delegate = ((ConnectorPageSinkProvider)Objects.requireNonNull(delegate, "delegate is null"));
/* 33 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public ConnectorPageSink createPageSink(ConnectorSession session, ConnectorOutputTableHandle outputTableHandle)
/*    */   {
/* 39 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 40 */     try { return new ClassLoaderSafeConnectorPageSink(this.delegate.createPageSink(session, outputTableHandle), this.classLoader);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 39 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 41 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public ConnectorPageSink createPageSink(ConnectorSession session, ConnectorInsertTableHandle insertTableHandle)
/*    */   {
/* 47 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 48 */     try { return new ClassLoaderSafeConnectorPageSink(this.delegate.createPageSink(session, insertTableHandle), this.classLoader);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 47 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 49 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorPageSinkProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */