/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public class InputStreamContentProvider
/*     */   implements ContentProvider, Callback, Closeable
/*     */ {
/*  56 */   private static final Logger LOG = Log.getLogger(InputStreamContentProvider.class);
/*     */   
/*  58 */   private final InputStreamContentProviderIterator iterator = new InputStreamContentProviderIterator(null);
/*     */   private final InputStream stream;
/*     */   private final int bufferSize;
/*     */   private final boolean autoClose;
/*     */   
/*     */   public InputStreamContentProvider(InputStream stream)
/*     */   {
/*  65 */     this(stream, 4096);
/*     */   }
/*     */   
/*     */   public InputStreamContentProvider(InputStream stream, int bufferSize)
/*     */   {
/*  70 */     this(stream, bufferSize, true);
/*     */   }
/*     */   
/*     */   public InputStreamContentProvider(InputStream stream, int bufferSize, boolean autoClose)
/*     */   {
/*  75 */     this.stream = stream;
/*  76 */     this.bufferSize = bufferSize;
/*  77 */     this.autoClose = autoClose;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/*  83 */     return -1L;
/*     */   }
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
/*     */   protected ByteBuffer onRead(byte[] buffer, int offset, int length)
/*     */   {
/* 102 */     if (length <= 0)
/* 103 */       return BufferUtil.EMPTY_BUFFER;
/* 104 */     return ByteBuffer.wrap(buffer, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void onReadFailure(Throwable failure) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<ByteBuffer> iterator()
/*     */   {
/* 120 */     return this.iterator;
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 126 */     if (this.autoClose)
/*     */     {
/*     */       try
/*     */       {
/* 130 */         this.stream.close();
/*     */       }
/*     */       catch (IOException x)
/*     */       {
/* 134 */         LOG.ignore(x);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void failed(Throwable failure)
/*     */   {
/* 143 */     close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private class InputStreamContentProviderIterator
/*     */     implements Iterator<ByteBuffer>, Closeable
/*     */   {
/*     */     private Throwable failure;
/*     */     
/*     */ 
/*     */ 
/*     */     private ByteBuffer buffer;
/*     */     
/*     */ 
/*     */ 
/*     */     private Boolean hasNext;
/*     */     
/*     */ 
/*     */ 
/*     */     private InputStreamContentProviderIterator() {}
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/*     */       try
/*     */       {
/* 172 */         if (this.hasNext != null) {
/* 173 */           return this.hasNext.booleanValue();
/*     */         }
/* 175 */         byte[] bytes = new byte[InputStreamContentProvider.this.bufferSize];
/* 176 */         int read = InputStreamContentProvider.this.stream.read(bytes);
/* 177 */         if (InputStreamContentProvider.LOG.isDebugEnabled())
/* 178 */           InputStreamContentProvider.LOG.debug("Read {} bytes from {}", new Object[] { Integer.valueOf(read), InputStreamContentProvider.this.stream });
/* 179 */         if (read > 0)
/*     */         {
/* 181 */           this.hasNext = Boolean.TRUE;
/* 182 */           this.buffer = InputStreamContentProvider.this.onRead(bytes, 0, read);
/* 183 */           return true;
/*     */         }
/* 185 */         if (read < 0)
/*     */         {
/* 187 */           this.hasNext = Boolean.FALSE;
/* 188 */           this.buffer = null;
/* 189 */           close();
/* 190 */           return false;
/*     */         }
/*     */         
/*     */ 
/* 194 */         this.hasNext = Boolean.TRUE;
/* 195 */         this.buffer = BufferUtil.EMPTY_BUFFER;
/* 196 */         return true;
/*     */ 
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 201 */         if (InputStreamContentProvider.LOG.isDebugEnabled())
/* 202 */           InputStreamContentProvider.LOG.debug(x);
/* 203 */         if (this.failure == null)
/*     */         {
/* 205 */           this.failure = x;
/* 206 */           InputStreamContentProvider.this.onReadFailure(x);
/*     */           
/*     */ 
/* 209 */           this.hasNext = Boolean.TRUE;
/* 210 */           this.buffer = null;
/* 211 */           close();
/* 212 */           return true;
/*     */         }
/* 214 */         throw new IllegalStateException();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public ByteBuffer next()
/*     */     {
/* 221 */       if (this.failure != null)
/*     */       {
/*     */ 
/* 224 */         this.hasNext = Boolean.FALSE;
/* 225 */         this.buffer = null;
/* 226 */         throw ((NoSuchElementException)new NoSuchElementException().initCause(this.failure));
/*     */       }
/* 228 */       if (!hasNext()) {
/* 229 */         throw new NoSuchElementException();
/*     */       }
/* 231 */       ByteBuffer result = this.buffer;
/* 232 */       if (result == null)
/*     */       {
/* 234 */         this.hasNext = Boolean.FALSE;
/* 235 */         this.buffer = null;
/* 236 */         throw new NoSuchElementException();
/*     */       }
/*     */       
/*     */ 
/* 240 */       this.hasNext = null;
/* 241 */       this.buffer = null;
/* 242 */       return result;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void remove()
/*     */     {
/* 249 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */     public void close()
/*     */     {
/* 255 */       InputStreamContentProvider.this.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\InputStreamContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */