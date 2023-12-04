/*    */ package com.facebook.presto.jdbc.internal.spi.connector.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.BucketFunction;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplit;
/*    */ import com.facebook.presto.jdbc.internal.spi.Node;
/*    */ import com.facebook.presto.jdbc.internal.spi.classloader.ThreadContextClassLoader;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorNodePartitioningProvider;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorPartitioningHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorTransactionHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
/*    */ import java.util.function.ToIntFunction;
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
/*    */ public final class ClassLoaderSafeNodePartitioningProvider
/*    */   implements ConnectorNodePartitioningProvider
/*    */ {
/*    */   private final ConnectorNodePartitioningProvider delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeNodePartitioningProvider(ConnectorNodePartitioningProvider delegate, ClassLoader classLoader)
/*    */   {
/* 40 */     this.delegate = ((ConnectorNodePartitioningProvider)Objects.requireNonNull(delegate, "delegate is null"));
/* 41 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BucketFunction getBucketFunction(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorPartitioningHandle partitioningHandle, List<Type> partitionChannelTypes, int bucketCount)
/*    */   {
/* 52 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 53 */     try { return this.delegate.getBucketFunction(transactionHandle, session, partitioningHandle, partitionChannelTypes, bucketCount);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 52 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 54 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public Map<Integer, Node> getBucketToNode(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorPartitioningHandle partitioningHandle)
/*    */   {
/* 60 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 61 */     try { return this.delegate.getBucketToNode(transactionHandle, session, partitioningHandle);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 60 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 62 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public ToIntFunction<ConnectorSplit> getSplitBucketFunction(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorPartitioningHandle partitioningHandle)
/*    */   {
/* 68 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 69 */     try { return this.delegate.getSplitBucketFunction(transactionHandle, session, partitioningHandle);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 68 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 70 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\classloader\ClassLoaderSafeNodePartitioningProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */