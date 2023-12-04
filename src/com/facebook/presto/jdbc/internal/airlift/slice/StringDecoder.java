/*    */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.CharBuffer;
/*    */ import java.nio.charset.CharacterCodingException;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CharsetDecoder;
/*    */ import java.nio.charset.CoderResult;
/*    */ import java.nio.charset.CodingErrorAction;
/*    */ import java.util.IdentityHashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
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
/*    */ final class StringDecoder
/*    */ {
/* 30 */   private static final ThreadLocal<Map<Charset, CharsetDecoder>> decoders = new ThreadLocal()
/*    */   {
/*    */ 
/*    */     protected Map<Charset, CharsetDecoder> initialValue()
/*    */     {
/* 35 */       return new IdentityHashMap();
/*    */     }
/*    */   };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static String decodeString(ByteBuffer src, Charset charset)
/*    */   {
/* 44 */     CharsetDecoder decoder = getDecoder(charset);
/* 45 */     CharBuffer dst = CharBuffer.allocate((int)(src.remaining() * decoder.maxCharsPerByte()));
/*    */     try {
/* 47 */       CoderResult cr = decoder.decode(src, dst, true);
/* 48 */       if (!cr.isUnderflow()) {
/* 49 */         cr.throwException();
/*    */       }
/* 51 */       cr = decoder.flush(dst);
/* 52 */       if (!cr.isUnderflow()) {
/* 53 */         cr.throwException();
/*    */       }
/*    */     }
/*    */     catch (CharacterCodingException x) {
/* 57 */       throw new IllegalStateException(x);
/*    */     }
/* 59 */     return dst.flip().toString();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private static CharsetDecoder getDecoder(Charset charset)
/*    */   {
/* 67 */     Objects.requireNonNull(charset, "charset is null");
/*    */     
/* 69 */     Map<Charset, CharsetDecoder> map = (Map)decoders.get();
/* 70 */     CharsetDecoder d = (CharsetDecoder)map.get(charset);
/* 71 */     if (d != null) {
/* 72 */       d.reset();
/* 73 */       d.onMalformedInput(CodingErrorAction.REPLACE);
/* 74 */       d.onUnmappableCharacter(CodingErrorAction.REPLACE);
/* 75 */       return d;
/*    */     }
/*    */     
/* 78 */     d = charset.newDecoder();
/* 79 */     d.onMalformedInput(CodingErrorAction.REPLACE);
/* 80 */     d.onUnmappableCharacter(CodingErrorAction.REPLACE);
/* 81 */     map.put(charset, d);
/* 82 */     return d;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\StringDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */