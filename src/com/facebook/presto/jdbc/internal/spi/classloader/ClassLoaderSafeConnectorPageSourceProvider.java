/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorPageSource;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorPageSourceProvider;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplit;
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
/* 34 */     this.delegate = ((ConnectorPageSourceProvider)Objects.requireNonNull(delegate, "delegate is null"));
/* 35 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ConnectorPageSource createPageSource(ConnectorSession session, ConnectorSplit split, List<ColumnHandle> columns)
/*    */   {
/* 42 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 43 */     try { return this.delegate.createPageSource(session, split, columns);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 42 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 44 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 50 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 51 */     try { return this.delegate.toString();
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 50 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 52 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorPageSourceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */