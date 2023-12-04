/*    */ package com.facebook.presto.jdbc.internal.jetty.util.component;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.IO;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.resource.Resource;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileDestroyable
/*    */   implements Destroyable
/*    */ {
/* 34 */   private static final Logger LOG = Log.getLogger(FileDestroyable.class);
/* 35 */   final List<File> _files = new ArrayList();
/*    */   
/*    */ 
/*    */   public FileDestroyable() {}
/*    */   
/*    */   public FileDestroyable(String file)
/*    */     throws IOException
/*    */   {
/* 43 */     this._files.add(Resource.newResource(file).getFile());
/*    */   }
/*    */   
/*    */   public FileDestroyable(File file)
/*    */   {
/* 48 */     this._files.add(file);
/*    */   }
/*    */   
/*    */   public void addFile(String file) throws IOException
/*    */   {
/* 53 */     Resource r = Resource.newResource(file);Throwable localThrowable3 = null;
/*    */     try {
/* 55 */       this._files.add(r.getFile());
/*    */     }
/*    */     catch (Throwable localThrowable1)
/*    */     {
/* 53 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*    */     }
/*    */     finally {
/* 56 */       if (r != null) if (localThrowable3 != null) try { r.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else r.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public void addFile(File file) {
/* 61 */     this._files.add(file);
/*    */   }
/*    */   
/*    */   public void addFiles(Collection<File> files)
/*    */   {
/* 66 */     this._files.addAll(files);
/*    */   }
/*    */   
/*    */   public void removeFile(String file) throws IOException
/*    */   {
/* 71 */     Resource r = Resource.newResource(file);Throwable localThrowable3 = null;
/*    */     try {
/* 73 */       this._files.remove(r.getFile());
/*    */     }
/*    */     catch (Throwable localThrowable1)
/*    */     {
/* 71 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*    */     }
/*    */     finally {
/* 74 */       if (r != null) if (localThrowable3 != null) try { r.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else r.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public void removeFile(File file) {
/* 79 */     this._files.remove(file);
/*    */   }
/*    */   
/*    */ 
/*    */   public void destroy()
/*    */   {
/* 85 */     for (File file : this._files)
/*    */     {
/* 87 */       if (file.exists())
/*    */       {
/* 89 */         if (LOG.isDebugEnabled())
/* 90 */           LOG.debug("Destroy {}", new Object[] { file });
/* 91 */         IO.delete(file);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\FileDestroyable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */