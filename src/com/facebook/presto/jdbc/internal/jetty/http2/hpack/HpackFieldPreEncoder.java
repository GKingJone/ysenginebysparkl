/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFieldPreEncoder;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.EnumSet;
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
/*    */ public class HpackFieldPreEncoder
/*    */   implements HttpFieldPreEncoder
/*    */ {
/*    */   public HttpVersion getHttpVersion()
/*    */   {
/* 42 */     return HttpVersion.HTTP_2;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] getEncodedField(HttpHeader header, String name, String value)
/*    */   {
/* 52 */     boolean not_indexed = HpackEncoder.__DO_NOT_INDEX.contains(header);
/*    */     
/* 54 */     ByteBuffer buffer = BufferUtil.allocate(name.length() + value.length() + 10);
/* 55 */     BufferUtil.clearToFill(buffer);
/*    */     int bits;
/*    */     boolean huffman;
/*    */     int bits;
/* 59 */     if (not_indexed)
/*    */     {
/*    */ 
/* 62 */       boolean never_index = HpackEncoder.__NEVER_INDEX.contains(header);
/* 63 */       boolean huffman = !HpackEncoder.__DO_NOT_HUFFMAN.contains(header);
/* 64 */       buffer.put((byte)(never_index ? 16 : 0));
/* 65 */       bits = 4;
/*    */     } else { int bits;
/* 67 */       if ((header == HttpHeader.CONTENT_LENGTH) && (value.length() > 1))
/*    */       {
/*    */ 
/* 70 */         buffer.put((byte)0);
/* 71 */         boolean huffman = true;
/* 72 */         bits = 4;
/*    */ 
/*    */       }
/*    */       else
/*    */       {
/* 77 */         buffer.put((byte)64);
/* 78 */         huffman = !HpackEncoder.__DO_NOT_HUFFMAN.contains(header);
/* 79 */         bits = 6;
/*    */       }
/*    */     }
/* 82 */     int name_idx = HpackContext.staticIndex(header);
/* 83 */     if (name_idx > 0) {
/* 84 */       NBitInteger.encode(buffer, bits, name_idx);
/*    */     }
/*    */     else {
/* 87 */       buffer.put((byte)Byte.MIN_VALUE);
/* 88 */       NBitInteger.encode(buffer, 7, Huffman.octetsNeededLC(name));
/* 89 */       Huffman.encodeLC(buffer, name);
/*    */     }
/*    */     
/* 92 */     HpackEncoder.encodeValue(buffer, huffman, value);
/*    */     
/* 94 */     BufferUtil.flipToFlush(buffer, 0);
/* 95 */     return BufferUtil.toArray(buffer);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\HpackFieldPreEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */