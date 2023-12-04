/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.RecordCursor;
/*    */ import com.facebook.presto.jdbc.internal.spi.RecordSet;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.Type;
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
/*    */ public class ClassLoaderSafeRecordSet
/*    */   implements RecordSet
/*    */ {
/*    */   private final RecordSet delegate;
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassLoaderSafeRecordSet(RecordSet delegate, ClassLoader classLoader)
/*    */   {
/* 32 */     this.delegate = ((RecordSet)Objects.requireNonNull(delegate, "delegate is null"));
/* 33 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public List<Type> getColumnTypes()
/*    */   {
/* 39 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 40 */     try { return this.delegate.getColumnTypes();
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 39 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 41 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public RecordCursor cursor()
/*    */   {
/* 47 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 48 */     try { return this.delegate.cursor();
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 47 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 49 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 55 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 56 */     try { return this.delegate.toString();
/*    */     }
/*    */     catch (Throwable localThrowable4)
/*    */     {
/* 55 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*    */     } finally {
/* 57 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeRecordSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */