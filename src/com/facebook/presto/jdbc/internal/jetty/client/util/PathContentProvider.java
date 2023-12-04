/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.AccessDeniedException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
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
/*     */ public class PathContentProvider
/*     */   extends AbstractTypedContentProvider
/*     */ {
/*  48 */   private static final Logger LOG = Log.getLogger(PathContentProvider.class);
/*     */   private final Path filePath;
/*     */   private final long fileSize;
/*     */   private final int bufferSize;
/*     */   private ByteBufferPool bufferPool;
/*     */   
/*     */   public PathContentProvider(Path filePath)
/*     */     throws IOException
/*     */   {
/*  57 */     this(filePath, 4096);
/*     */   }
/*     */   
/*     */   public PathContentProvider(Path filePath, int bufferSize) throws IOException
/*     */   {
/*  62 */     this("application/octet-stream", filePath, bufferSize);
/*     */   }
/*     */   
/*     */   public PathContentProvider(String contentType, Path filePath) throws IOException
/*     */   {
/*  67 */     this(contentType, filePath, 4096);
/*     */   }
/*     */   
/*     */   public PathContentProvider(String contentType, Path filePath, int bufferSize) throws IOException
/*     */   {
/*  72 */     super(contentType);
/*  73 */     if (!Files.isRegularFile(filePath, new LinkOption[0]))
/*  74 */       throw new NoSuchFileException(filePath.toString());
/*  75 */     if (!Files.isReadable(filePath))
/*  76 */       throw new AccessDeniedException(filePath.toString());
/*  77 */     this.filePath = filePath;
/*  78 */     this.fileSize = Files.size(filePath);
/*  79 */     this.bufferSize = bufferSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/*  85 */     return this.fileSize;
/*     */   }
/*     */   
/*     */   public ByteBufferPool getByteBufferPool()
/*     */   {
/*  90 */     return this.bufferPool;
/*     */   }
/*     */   
/*     */   public void setByteBufferPool(ByteBufferPool byteBufferPool)
/*     */   {
/*  95 */     this.bufferPool = byteBufferPool;
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<ByteBuffer> iterator()
/*     */   {
/* 101 */     return new PathIterator(null);
/*     */   }
/*     */   
/*     */   private class PathIterator implements Iterator<ByteBuffer>, Closeable
/*     */   {
/*     */     private ByteBuffer buffer;
/*     */     private SeekableByteChannel channel;
/*     */     private long position;
/*     */     
/*     */     private PathIterator() {}
/*     */     
/*     */     public boolean hasNext() {
/* 113 */       return this.position < PathContentProvider.this.getLength();
/*     */     }
/*     */     
/*     */ 
/*     */     public ByteBuffer next()
/*     */     {
/*     */       try
/*     */       {
/* 121 */         if (this.channel == null)
/*     */         {
/*     */ 
/*     */ 
/* 125 */           this.buffer = (PathContentProvider.this.bufferPool == null ? ByteBuffer.allocateDirect(PathContentProvider.this.bufferSize) : PathContentProvider.this.bufferPool.acquire(PathContentProvider.this.bufferSize, true));
/* 126 */           this.channel = Files.newByteChannel(PathContentProvider.this.filePath, new OpenOption[] { StandardOpenOption.READ });
/* 127 */           if (PathContentProvider.LOG.isDebugEnabled()) {
/* 128 */             PathContentProvider.LOG.debug("Opened file {}", new Object[] { PathContentProvider.this.filePath });
/*     */           }
/*     */         }
/* 131 */         this.buffer.clear();
/* 132 */         int read = this.channel.read(this.buffer);
/* 133 */         if (read < 0) {
/* 134 */           throw new NoSuchElementException();
/*     */         }
/* 136 */         if (PathContentProvider.LOG.isDebugEnabled()) {
/* 137 */           PathContentProvider.LOG.debug("Read {} bytes from {}", new Object[] { Integer.valueOf(read), PathContentProvider.this.filePath });
/*     */         }
/* 139 */         this.position += read;
/*     */         
/* 141 */         this.buffer.flip();
/* 142 */         return this.buffer;
/*     */       }
/*     */       catch (NoSuchElementException x)
/*     */       {
/* 146 */         close();
/* 147 */         throw x;
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 151 */         close();
/* 152 */         throw ((NoSuchElementException)new NoSuchElementException().initCause(x));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void close()
/*     */     {
/*     */       try
/*     */       {
/* 161 */         if ((PathContentProvider.this.bufferPool != null) && (this.buffer != null))
/* 162 */           PathContentProvider.this.bufferPool.release(this.buffer);
/* 163 */         if (this.channel != null) {
/* 164 */           this.channel.close();
/*     */         }
/*     */       }
/*     */       catch (Throwable x) {
/* 168 */         PathContentProvider.LOG.ignore(x);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\PathContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */