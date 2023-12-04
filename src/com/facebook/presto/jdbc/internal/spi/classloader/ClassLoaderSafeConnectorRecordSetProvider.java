/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorRecordSetProvider;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplit;
/*    */ import com.facebook.presto.jdbc.internal.spi.RecordSet;
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
/*    */ public class ClassLoaderSafeConnectorRecordSetProvider
/*    */   implements ConnectorRecordSetProvider
/*    */ {
/*    */   private final ConnectorRecordSetProvider delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeConnectorRecordSetProvider(ConnectorRecordSetProvider delegate, ClassLoader classLoader)
/*    */   {
/* 34 */     this.delegate = ((ConnectorRecordSetProvider)Objects.requireNonNull(delegate, "delegate is null"));
/* 35 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public RecordSet getRecordSet(ConnectorSession session, ConnectorSplit split, List<? extends ColumnHandle> columns)
/*    */   {
/* 41 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 42 */     try { return new ClassLoaderSafeRecordSet(this.delegate.getRecordSet(session, split, columns), this.classLoader);
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 41 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 43 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 49 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 50 */     try { return this.delegate.toString();
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 49 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 51 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorRecordSetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */