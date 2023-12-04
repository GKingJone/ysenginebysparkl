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
/*    */ public class BytesContentProvider
/*    */   extends AbstractTypedContentProvider
/*    */ {
/*    */   private final byte[][] bytes;
/*    */   private final long length;
/*    */   
/*    */   public BytesContentProvider(byte[]... bytes)
/*    */   {
/* 37 */     this("application/octet-stream", bytes);
/*    */   }
/*    */   
/*    */   public BytesContentProvider(String contentType, byte[]... bytes)
/*    */   {
/* 42 */     super(contentType);
/* 43 */     this.bytes = bytes;
/* 44 */     long length = 0L;
/* 45 */     for (byte[] buffer : bytes)
/* 46 */       length += buffer.length;
/* 47 */     this.length = length;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getLength()
/*    */   {
/* 53 */     return this.length;
/*    */   }
/*    */   
/*    */ 
/*    */   public Iterator<ByteBuffer> iterator()
/*    */   {
/* 59 */     new Iterator()
/*    */     {
/*    */       private int index;
/*    */       
/*    */ 
/*    */       public boolean hasNext()
/*    */       {
/* 66 */         return this.index < BytesContentProvider.this.bytes.length;
/*    */       }
/*    */       
/*    */ 
/*    */       public ByteBuffer next()
/*    */       {
/*    */         try
/*    */         {
/* 74 */           return ByteBuffer.wrap(BytesContentProvider.this.bytes[(this.index++)]);
/*    */         }
/*    */         catch (ArrayIndexOutOfBoundsException x)
/*    */         {
/* 78 */           throw new NoSuchElementException();
/*    */         }
/*    */       }
/*    */       
/*    */ 
/*    */       public void remove()
/*    */       {
/* 85 */         throw new UnsupportedOperationException();
/*    */       }
/*    */     };
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\BytesContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */