/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorPageSink;
/*    */ import com.facebook.presto.jdbc.internal.spi.Page;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*    */ import java.util.Collection;
/*    */ import java.util.Objects;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ public class ClassLoaderSafeConnectorPageSink
/*    */   implements ConnectorPageSink
/*    */ {
/*    */   private final ConnectorPageSink delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeConnectorPageSink(ConnectorPageSink delegate, ClassLoader classLoader)
/*    */   {
/* 34 */     this.delegate = ((ConnectorPageSink)Objects.requireNonNull(delegate, "delegate is null"));
/* 35 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public CompletableFuture<?> appendPage(Page page, Block sampleWeightBlock)
/*    */   {
/* 41 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 42 */     try { return this.delegate.appendPage(page, sampleWeightBlock);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 41 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 43 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public Collection<Slice> finish()
/*    */   {
/* 49 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 50 */     try { return this.delegate.finish();
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 49 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 51 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public void abort()
/*    */   {
/* 57 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 58 */     try { this.delegate.abort();
/*    */     }
/*    */     catch (Throwable localThrowable1)
/*    */     {
/* 57 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*    */     } finally {
/* 59 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorPageSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */