/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.AsyncContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.AsyncContentProvider.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OutputStreamContentProvider
/*     */   implements AsyncContentProvider, Callback, Closeable
/*     */ {
/*  78 */   private final DeferredContentProvider deferred = new DeferredContentProvider(new ByteBuffer[0]);
/*  79 */   private final OutputStream output = new DeferredOutputStream(null);
/*     */   
/*     */ 
/*     */   public boolean isNonBlocking()
/*     */   {
/*  84 */     return this.deferred.isNonBlocking();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/*  90 */     return this.deferred.getLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<ByteBuffer> iterator()
/*     */   {
/*  96 */     return this.deferred.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setListener(AsyncContentProvider.Listener listener)
/*     */   {
/* 102 */     this.deferred.setListener(listener);
/*     */   }
/*     */   
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 107 */     return this.output;
/*     */   }
/*     */   
/*     */   protected void write(ByteBuffer buffer)
/*     */   {
/* 112 */     this.deferred.offer(buffer);
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 118 */     this.deferred.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public void succeeded()
/*     */   {
/* 124 */     this.deferred.succeeded();
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable failure)
/*     */   {
/* 130 */     this.deferred.failed(failure);
/*     */   }
/*     */   
/*     */   private class DeferredOutputStream extends OutputStream
/*     */   {
/*     */     private DeferredOutputStream() {}
/*     */     
/*     */     public void write(int b) throws IOException {
/* 138 */       write(new byte[] { (byte)b }, 0, 1);
/*     */     }
/*     */     
/*     */     public void write(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 144 */       OutputStreamContentProvider.this.write(ByteBuffer.wrap(b, off, len));
/* 145 */       flush();
/*     */     }
/*     */     
/*     */     public void flush()
/*     */       throws IOException
/*     */     {
/* 151 */       OutputStreamContentProvider.this.deferred.flush();
/*     */     }
/*     */     
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 157 */       OutputStreamContentProvider.this.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\OutputStreamContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */