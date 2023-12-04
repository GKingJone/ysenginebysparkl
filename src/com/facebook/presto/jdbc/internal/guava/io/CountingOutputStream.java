/*    */ package com.facebook.presto.jdbc.internal.guava.io;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import javax.annotation.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public final class CountingOutputStream
/*    */   extends FilterOutputStream
/*    */ {
/*    */   private long count;
/*    */   
/*    */   public CountingOutputStream(@Nullable OutputStream out)
/*    */   {
/* 44 */     super(out);
/*    */   }
/*    */   
/*    */   public long getCount()
/*    */   {
/* 49 */     return this.count;
/*    */   }
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 53 */     this.out.write(b, off, len);
/* 54 */     this.count += len;
/*    */   }
/*    */   
/*    */   public void write(int b) throws IOException {
/* 58 */     this.out.write(b);
/* 59 */     this.count += 1L;
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 66 */     this.out.close();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\CountingOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */