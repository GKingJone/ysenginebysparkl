/*    */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
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
/*    */ public class ByteBufferContentProvider
/*    */   extends AbstractTypedContentProvider
/*    */ {
/*    */   private final ByteBuffer[] buffers;
/*    */   private final int length;
/*    */   
/*    */   public ByteBufferContentProvider(ByteBuffer... buffers)
/*    */   {
/* 41 */     this("application/octet-stream", buffers);
/*    */   }
/*    */   
/*    */   public ByteBufferContentProvider(String contentType, ByteBuffer... buffers)
/*    */   {
/* 46 */     super(contentType);
/* 47 */     this.buffers = buffers;
/* 48 */     int length = 0;
/* 49 */     for (ByteBuffer buffer : buffers)
/* 50 */       length += buffer.remaining();
/* 51 */     this.length = length;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getLength()
/*    */   {
/* 57 */     return this.length;
/*    */   }
/*    */   
/*    */ 
/*    */   public Iterator<ByteBuffer> iterator()
/*    */   {
/* 63 */     new Iterator()
/*    */     {
/*    */       private int index;
/*    */       
/*    */ 
/*    */       public boolean hasNext()
/*    */       {
/* 70 */         return this.index < ByteBufferContentProvider.this.buffers.length;
/*    */       }
/*    */       
/*    */ 
/*    */       public ByteBuffer next()
/*    */       {
/*    */         try
/*    */         {
/* 78 */           ByteBuffer buffer = ByteBufferContentProvider.this.buffers[this.index];
/* 79 */           ByteBufferContentProvider.this.buffers[this.index] = buffer.slice();
/* 80 */           this.index += 1;
/* 81 */           return buffer;
/*    */         }
/*    */         catch (ArrayIndexOutOfBoundsException x)
/*    */         {
/* 85 */           throw new NoSuchElementException();
/*    */         }
/*    */       }
/*    */       
/*    */ 
/*    */       public void remove()
/*    */       {
/* 92 */         throw new UnsupportedOperationException();
/*    */       }
/*    */     };
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\ByteBufferContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */