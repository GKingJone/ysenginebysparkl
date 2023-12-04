/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.hpack.HpackDecoder;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*    */ import java.nio.ByteBuffer;
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
/*    */ public class HeaderBlockParser
/*    */ {
/*    */   private final ByteBufferPool byteBufferPool;
/*    */   private final HpackDecoder hpackDecoder;
/*    */   private ByteBuffer blockBuffer;
/*    */   
/*    */   public HeaderBlockParser(ByteBufferPool byteBufferPool, HpackDecoder hpackDecoder)
/*    */   {
/* 36 */     this.byteBufferPool = byteBufferPool;
/* 37 */     this.hpackDecoder = hpackDecoder;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public MetaData parse(ByteBuffer buffer, int blockLength)
/*    */   {
/* 46 */     int accumulated = this.blockBuffer == null ? 0 : this.blockBuffer.position();
/* 47 */     int remaining = blockLength - accumulated;
/*    */     
/* 49 */     if (buffer.remaining() < remaining)
/*    */     {
/* 51 */       if (this.blockBuffer == null)
/*    */       {
/* 53 */         this.blockBuffer = this.byteBufferPool.acquire(blockLength, false);
/* 54 */         BufferUtil.clearToFill(this.blockBuffer);
/*    */       }
/* 56 */       this.blockBuffer.put(buffer);
/* 57 */       return null;
/*    */     }
/*    */     
/*    */ 
/* 61 */     int limit = buffer.limit();
/* 62 */     buffer.limit(buffer.position() + remaining);
/*    */     ByteBuffer toDecode;
/* 64 */     ByteBuffer toDecode; if (this.blockBuffer != null)
/*    */     {
/* 66 */       this.blockBuffer.put(buffer);
/* 67 */       BufferUtil.flipToFlush(this.blockBuffer, 0);
/* 68 */       toDecode = this.blockBuffer;
/*    */     }
/*    */     else
/*    */     {
/* 72 */       toDecode = buffer;
/*    */     }
/*    */     
/* 75 */     MetaData result = this.hpackDecoder.decode(toDecode);
/*    */     
/* 77 */     buffer.limit(limit);
/*    */     
/* 79 */     if (this.blockBuffer != null)
/*    */     {
/* 81 */       this.byteBufferPool.release(this.blockBuffer);
/* 82 */       this.blockBuffer = null;
/*    */     }
/*    */     
/* 85 */     return result;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\HeaderBlockParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */