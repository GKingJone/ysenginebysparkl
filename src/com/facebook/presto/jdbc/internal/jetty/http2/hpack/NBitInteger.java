/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*     */ 
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class NBitInteger
/*     */ {
/*     */   public static int octectsNeeded(int n, int i)
/*     */   {
/*  27 */     if (n == 8)
/*     */     {
/*  29 */       int nbits = 255;
/*  30 */       i -= nbits;
/*  31 */       if (i < 0)
/*  32 */         return 1;
/*  33 */       if (i == 0)
/*  34 */         return 2;
/*  35 */       int lz = Integer.numberOfLeadingZeros(i);
/*  36 */       int log = 32 - lz;
/*  37 */       return 1 + (log + 6) / 7;
/*     */     }
/*     */     
/*  40 */     int nbits = 255 >>> 8 - n;
/*  41 */     i -= nbits;
/*  42 */     if (i < 0)
/*  43 */       return 0;
/*  44 */     if (i == 0)
/*  45 */       return 1;
/*  46 */     int lz = Integer.numberOfLeadingZeros(i);
/*  47 */     int log = 32 - lz;
/*  48 */     return (log + 6) / 7;
/*     */   }
/*     */   
/*     */   public static void encode(ByteBuffer buf, int n, int i)
/*     */   {
/*  53 */     if (n == 8)
/*     */     {
/*  55 */       if (i < 255)
/*     */       {
/*  57 */         buf.put((byte)i);
/*     */       }
/*     */       else
/*     */       {
/*  61 */         buf.put((byte)-1);
/*     */         
/*  63 */         int length = i - 255;
/*     */         for (;;)
/*     */         {
/*  66 */           if ((length & 0xFFFFFF80) == 0)
/*     */           {
/*  68 */             buf.put((byte)length);
/*  69 */             return;
/*     */           }
/*     */           
/*     */ 
/*  73 */           buf.put((byte)(length & 0x7F | 0x80));
/*  74 */           length >>>= 7;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  81 */       int p = buf.position() - 1;
/*  82 */       int bits = 255 >>> 8 - n;
/*     */       
/*  84 */       if (i < bits)
/*     */       {
/*  86 */         buf.put(p, (byte)(buf.get(p) & (bits ^ 0xFFFFFFFF) | i));
/*     */       }
/*     */       else
/*     */       {
/*  90 */         buf.put(p, (byte)(buf.get(p) | bits));
/*     */         
/*  92 */         int length = i - bits;
/*     */         for (;;)
/*     */         {
/*  95 */           if ((length & 0xFFFFFF80) == 0)
/*     */           {
/*  97 */             buf.put((byte)length);
/*  98 */             return;
/*     */           }
/*     */           
/*     */ 
/* 102 */           buf.put((byte)(length & 0x7F | 0x80));
/* 103 */           length >>>= 7;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static int decode(ByteBuffer buffer, int n)
/*     */   {
/* 112 */     if (n == 8)
/*     */     {
/* 114 */       int nbits = 255;
/*     */       
/* 116 */       int i = buffer.get() & 0xFF;
/*     */       
/* 118 */       if (i == nbits)
/*     */       {
/* 120 */         int m = 1;
/*     */         int b;
/*     */         do
/*     */         {
/* 124 */           b = 0xFF & buffer.get();
/* 125 */           i += (b & 0x7F) * m;
/* 126 */           m *= 128;
/*     */         }
/* 128 */         while ((b & 0x80) == 128);
/*     */       }
/* 130 */       return i;
/*     */     }
/*     */     
/* 133 */     int nbits = 255 >>> 8 - n;
/*     */     
/* 135 */     int i = buffer.get(buffer.position() - 1) & nbits;
/*     */     
/* 137 */     if (i == nbits)
/*     */     {
/* 139 */       int m = 1;
/*     */       int b;
/*     */       do
/*     */       {
/* 143 */         b = 0xFF & buffer.get();
/* 144 */         i += (b & 0x7F) * m;
/* 145 */         m *= 128;
/*     */       }
/* 147 */       while ((b & 0x80) == 128);
/*     */     }
/* 149 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\NBitInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */