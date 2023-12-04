/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.Closeable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collections;
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
/*     */ public class HttpContent
/*     */   implements Callback, Closeable
/*     */ {
/*  68 */   private static final Logger LOG = Log.getLogger(HttpContent.class);
/*  69 */   private static final ByteBuffer AFTER = ByteBuffer.allocate(0);
/*  70 */   private static final ByteBuffer CLOSE = ByteBuffer.allocate(0);
/*     */   
/*     */   private final ContentProvider provider;
/*     */   private final Iterator<ByteBuffer> iterator;
/*     */   private ByteBuffer buffer;
/*     */   private ByteBuffer content;
/*     */   private boolean last;
/*     */   
/*     */   public HttpContent(ContentProvider provider)
/*     */   {
/*  80 */     this.provider = provider;
/*  81 */     this.iterator = (provider == null ? Collections.emptyIterator() : provider.iterator());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasContent()
/*     */   {
/*  89 */     return this.provider != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLast()
/*     */   {
/*  97 */     return this.last;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteBuffer getByteBuffer()
/*     */   {
/* 105 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteBuffer getContent()
/*     */   {
/* 113 */     return this.content;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean advance()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/client/HttpContent:iterator	Ljava/util/Iterator;
/*     */     //   4: instanceof 61
/*     */     //   7: ifeq +34 -> 41
/*     */     //   10: aload_0
/*     */     //   11: getfield 44	com/facebook/presto/jdbc/internal/jetty/client/HttpContent:iterator	Ljava/util/Iterator;
/*     */     //   14: checkcast 61	com/facebook/presto/jdbc/internal/jetty/client/Synchronizable
/*     */     //   17: invokeinterface 65 1 0
/*     */     //   22: dup
/*     */     //   23: astore_1
/*     */     //   24: monitorenter
/*     */     //   25: aload_0
/*     */     //   26: aload_0
/*     */     //   27: getfield 44	com/facebook/presto/jdbc/internal/jetty/client/HttpContent:iterator	Ljava/util/Iterator;
/*     */     //   30: invokespecial 68	com/facebook/presto/jdbc/internal/jetty/client/HttpContent:advance	(Ljava/util/Iterator;)Z
/*     */     //   33: aload_1
/*     */     //   34: monitorexit
/*     */     //   35: ireturn
/*     */     //   36: astore_2
/*     */     //   37: aload_1
/*     */     //   38: monitorexit
/*     */     //   39: aload_2
/*     */     //   40: athrow
/*     */     //   41: aload_0
/*     */     //   42: aload_0
/*     */     //   43: getfield 44	com/facebook/presto/jdbc/internal/jetty/client/HttpContent:iterator	Ljava/util/Iterator;
/*     */     //   46: invokespecial 68	com/facebook/presto/jdbc/internal/jetty/client/HttpContent:advance	(Ljava/util/Iterator;)Z
/*     */     //   49: ireturn
/*     */     // Line number table:
/*     */     //   Java source line #129	-> byte code offset #0
/*     */     //   Java source line #131	-> byte code offset #10
/*     */     //   Java source line #133	-> byte code offset #25
/*     */     //   Java source line #134	-> byte code offset #36
/*     */     //   Java source line #138	-> byte code offset #41
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	50	0	this	HttpContent
/*     */     //   23	15	1	Ljava/lang/Object;	Object
/*     */     //   36	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   25	35	36	finally
/*     */     //   36	39	36	finally
/*     */   }
/*     */   
/*     */   private boolean advance(Iterator<ByteBuffer> iterator)
/*     */   {
/* 144 */     boolean hasNext = iterator.hasNext();
/* 145 */     ByteBuffer bytes = hasNext ? (ByteBuffer)iterator.next() : null;
/* 146 */     boolean hasMore = (hasNext) && (iterator.hasNext());
/* 147 */     boolean wasLast = this.last;
/* 148 */     this.last = (!hasMore);
/*     */     
/* 150 */     if (hasNext)
/*     */     {
/* 152 */       this.buffer = bytes;
/* 153 */       this.content = (bytes == null ? null : bytes.slice());
/* 154 */       if (LOG.isDebugEnabled())
/* 155 */         LOG.debug("Advanced content to {} chunk {}", new Object[] { hasMore ? "next" : "last", String.valueOf(bytes) });
/* 156 */       return bytes != null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 161 */     if (wasLast)
/*     */     {
/* 163 */       this.buffer = (this.content = AFTER);
/* 164 */       if (LOG.isDebugEnabled()) {
/* 165 */         LOG.debug("Advanced content past last chunk", new Object[0]);
/*     */       }
/*     */     }
/*     */     else {
/* 169 */       this.buffer = (this.content = CLOSE);
/* 170 */       if (LOG.isDebugEnabled())
/* 171 */         LOG.debug("Advanced content to last chunk", new Object[0]);
/*     */     }
/* 173 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isConsumed()
/*     */   {
/* 182 */     return this.buffer == AFTER;
/*     */   }
/*     */   
/*     */ 
/*     */   public void succeeded()
/*     */   {
/* 188 */     if (isConsumed())
/* 189 */       return;
/* 190 */     if (this.buffer == CLOSE)
/* 191 */       return;
/* 192 */     if ((this.iterator instanceof Callback)) {
/* 193 */       ((Callback)this.iterator).succeeded();
/*     */     }
/*     */   }
/*     */   
/*     */   public void failed(Throwable x)
/*     */   {
/* 199 */     if (isConsumed())
/* 200 */       return;
/* 201 */     if (this.buffer == CLOSE)
/* 202 */       return;
/* 203 */     if ((this.iterator instanceof Callback)) {
/* 204 */       ((Callback)this.iterator).failed(x);
/*     */     }
/*     */   }
/*     */   
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 212 */       if ((this.iterator instanceof Closeable)) {
/* 213 */         ((Closeable)this.iterator).close();
/*     */       }
/*     */     }
/*     */     catch (Throwable x) {
/* 217 */       LOG.ignore(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 224 */     return String.format("%s@%x - has=%b,last=%b,consumed=%b,buffer=%s", new Object[] {
/* 225 */       getClass().getSimpleName(), 
/* 226 */       Integer.valueOf(hashCode()), 
/* 227 */       Boolean.valueOf(hasContent()), 
/* 228 */       Boolean.valueOf(isLast()), 
/* 229 */       Boolean.valueOf(isConsumed()), 
/* 230 */       BufferUtil.toDetailString(getContent()) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */