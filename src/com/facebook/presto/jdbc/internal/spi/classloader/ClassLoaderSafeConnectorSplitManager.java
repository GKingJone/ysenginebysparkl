/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplitManager;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplitSource;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableLayoutHandle;
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
/* 31 */     this.delegate = ((ConnectorSplitManager)Objects.requireNonNull(delegate, "delegate is null"));
/* 32 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public ConnectorSplitSource getSplits(ConnectorSession session, ConnectorTableLayoutHandle layout)
/*    */   {
/* 38 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 39 */     try { return this.delegate.getSplits(session, layout);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 38 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 40 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 46 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 47 */     try { return this.delegate.toString();
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 46 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 48 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorSplitManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */