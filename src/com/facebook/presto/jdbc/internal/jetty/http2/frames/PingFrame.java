/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*     */ 
/*     */ import java.util.Objects;
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
/*     */ public class PingFrame
/*     */   extends Frame
/*     */ {
/*     */   public static final int PING_LENGTH = 8;
/*  26 */   private static final byte[] EMPTY_PAYLOAD = new byte[8];
/*     */   
/*     */ 
/*     */   private final byte[] payload;
/*     */   
/*     */ 
/*     */   private final boolean reply;
/*     */   
/*     */ 
/*     */ 
/*     */   public PingFrame(boolean reply)
/*     */   {
/*  38 */     this(EMPTY_PAYLOAD, reply);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PingFrame(long value, boolean reply)
/*     */   {
/*  49 */     this(toBytes(value), reply);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PingFrame(byte[] payload, boolean reply)
/*     */   {
/*  60 */     super(FrameType.PING);
/*  61 */     this.payload = ((byte[])Objects.requireNonNull(payload));
/*  62 */     if (payload.length != 8)
/*  63 */       throw new IllegalArgumentException("PING payload must be 8 bytes");
/*  64 */     this.reply = reply;
/*     */   }
/*     */   
/*     */   public byte[] getPayload()
/*     */   {
/*  69 */     return this.payload;
/*     */   }
/*     */   
/*     */   public long getPayloadAsLong()
/*     */   {
/*  74 */     return toLong(this.payload);
/*     */   }
/*     */   
/*     */   public boolean isReply()
/*     */   {
/*  79 */     return this.reply;
/*     */   }
/*     */   
/*     */   private static byte[] toBytes(long value)
/*     */   {
/*  84 */     byte[] result = new byte[8];
/*  85 */     for (int i = result.length - 1; i >= 0; i--)
/*     */     {
/*  87 */       result[i] = ((byte)(int)(value & 0xFF));
/*  88 */       value >>= 8;
/*     */     }
/*  90 */     return result;
/*     */   }
/*     */   
/*     */   private static long toLong(byte[] payload)
/*     */   {
/*  95 */     long result = 0L;
/*  96 */     for (int i = 0; i < 8; i++)
/*     */     {
/*  98 */       result <<= 8;
/*  99 */       result |= payload[i] & 0xFF;
/*     */     }
/* 101 */     return result;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\PingFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */