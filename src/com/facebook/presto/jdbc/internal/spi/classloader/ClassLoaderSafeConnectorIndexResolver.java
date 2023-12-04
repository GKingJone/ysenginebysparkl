/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorIndex;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorIndexHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorIndexResolver;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorResolvedIndex;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
/*    */ import java.util.Set;
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
/*    */ public class ClassLoaderSafeConnectorIndexResolver
/*    */   implements ConnectorIndexResolver
/*    */ {
/*    */   private final ConnectorIndexResolver delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeConnectorIndexResolver(ConnectorIndexResolver delegate, ClassLoader classLoader)
/*    */   {
/* 38 */     this.delegate = ((ConnectorIndexResolver)Objects.requireNonNull(delegate, "delegate is null"));
/* 39 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ConnectorResolvedIndex resolveIndex(ConnectorSession session, ConnectorTableHandle tableHandle, Set<ColumnHandle> indexableColumns, Set<ColumnHandle> outputColumns, TupleDomain<ColumnHandle> tupleDomain)
/*    */   {
/* 50 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 51 */     try { return this.delegate.resolveIndex(session, tableHandle, indexableColumns, outputColumns, tupleDomain);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 50 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 52 */       if (ignored != null) { if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { ignored.close();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ConnectorResolvedIndex resolveIndex(ConnectorSession session, ConnectorTableHandle tableHandle, Set<ColumnHandle> indexableColumns, TupleDomain<ColumnHandle> tupleDomain)
/*    */   {
/* 62 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 63 */     try { return this.delegate.resolveIndex(session, tableHandle, indexableColumns, tupleDomain);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 62 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 64 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public ConnectorIndex getIndex(ConnectorSession session, ConnectorIndexHandle indexHandle, List<ColumnHandle> lookupSchema, List<ColumnHandle> outputSchema)
/*    */   {
/* 70 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 71 */     try { return this.delegate.getIndex(session, indexHandle, lookupSchema, outputSchema);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 70 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 72 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorIndexResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */