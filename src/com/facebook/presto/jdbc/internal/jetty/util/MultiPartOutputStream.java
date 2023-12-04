/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class MultiPartOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*  35 */   private static final byte[] __CRLF = { 13, 10 };
/*  36 */   private static final byte[] __DASHDASH = { 45, 45 };
/*     */   
/*     */   public static final String MULTIPART_MIXED = "multipart/mixed";
/*     */   
/*     */   public static final String MULTIPART_X_MIXED_REPLACE = "multipart/x-mixed-replace";
/*     */   
/*     */   private final String boundary;
/*     */   
/*     */   private final byte[] boundaryBytes;
/*     */   
/*  46 */   private boolean inPart = false;
/*     */   
/*     */ 
/*     */   public MultiPartOutputStream(OutputStream out)
/*     */     throws IOException
/*     */   {
/*  52 */     super(out);
/*     */     
/*     */ 
/*  55 */     this.boundary = ("jetty" + System.identityHashCode(this) + Long.toString(System.currentTimeMillis(), 36));
/*  56 */     this.boundaryBytes = this.boundary.getBytes(StandardCharsets.ISO_8859_1);
/*     */   }
/*     */   
/*     */   public MultiPartOutputStream(OutputStream out, String boundary)
/*     */     throws IOException
/*     */   {
/*  62 */     super(out);
/*     */     
/*  64 */     this.boundary = boundary;
/*  65 */     this.boundaryBytes = boundary.getBytes(StandardCharsets.ISO_8859_1);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 27	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:inPart	Z
/*     */     //   4: ifeq +13 -> 17
/*     */     //   7: aload_0
/*     */     //   8: getfield 84	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:out	Ljava/io/OutputStream;
/*     */     //   11: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:__CRLF	[B
/*     */     //   14: invokevirtual 92	java/io/OutputStream:write	([B)V
/*     */     //   17: aload_0
/*     */     //   18: getfield 84	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:out	Ljava/io/OutputStream;
/*     */     //   21: getstatic 94	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:__DASHDASH	[B
/*     */     //   24: invokevirtual 92	java/io/OutputStream:write	([B)V
/*     */     //   27: aload_0
/*     */     //   28: getfield 84	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:out	Ljava/io/OutputStream;
/*     */     //   31: aload_0
/*     */     //   32: getfield 76	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:boundaryBytes	[B
/*     */     //   35: invokevirtual 92	java/io/OutputStream:write	([B)V
/*     */     //   38: aload_0
/*     */     //   39: getfield 84	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:out	Ljava/io/OutputStream;
/*     */     //   42: getstatic 94	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:__DASHDASH	[B
/*     */     //   45: invokevirtual 92	java/io/OutputStream:write	([B)V
/*     */     //   48: aload_0
/*     */     //   49: getfield 84	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:out	Ljava/io/OutputStream;
/*     */     //   52: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:__CRLF	[B
/*     */     //   55: invokevirtual 92	java/io/OutputStream:write	([B)V
/*     */     //   58: aload_0
/*     */     //   59: iconst_0
/*     */     //   60: putfield 27	com/facebook/presto/jdbc/internal/jetty/util/MultiPartOutputStream:inPart	Z
/*     */     //   63: aload_0
/*     */     //   64: invokespecial 96	java/io/FilterOutputStream:close	()V
/*     */     //   67: goto +10 -> 77
/*     */     //   70: astore_1
/*     */     //   71: aload_0
/*     */     //   72: invokespecial 96	java/io/FilterOutputStream:close	()V
/*     */     //   75: aload_1
/*     */     //   76: athrow
/*     */     //   77: return
/*     */     // Line number table:
/*     */     //   Java source line #78	-> byte code offset #0
/*     */     //   Java source line #79	-> byte code offset #7
/*     */     //   Java source line #80	-> byte code offset #17
/*     */     //   Java source line #81	-> byte code offset #27
/*     */     //   Java source line #82	-> byte code offset #38
/*     */     //   Java source line #83	-> byte code offset #48
/*     */     //   Java source line #84	-> byte code offset #58
/*     */     //   Java source line #88	-> byte code offset #63
/*     */     //   Java source line #89	-> byte code offset #67
/*     */     //   Java source line #88	-> byte code offset #70
/*     */     //   Java source line #90	-> byte code offset #77
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	78	0	this	MultiPartOutputStream
/*     */     //   70	6	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	63	70	finally
/*     */   }
/*     */   
/*     */   public String getBoundary()
/*     */   {
/*  95 */     return this.boundary;
/*     */   }
/*     */   
/*  98 */   public OutputStream getOut() { return this.out; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startPart(String contentType)
/*     */     throws IOException
/*     */   {
/* 108 */     if (this.inPart)
/* 109 */       this.out.write(__CRLF);
/* 110 */     this.inPart = true;
/* 111 */     this.out.write(__DASHDASH);
/* 112 */     this.out.write(this.boundaryBytes);
/* 113 */     this.out.write(__CRLF);
/* 114 */     if (contentType != null)
/* 115 */       this.out.write(("Content-Type: " + contentType).getBytes(StandardCharsets.ISO_8859_1));
/* 116 */     this.out.write(__CRLF);
/* 117 */     this.out.write(__CRLF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startPart(String contentType, String[] headers)
/*     */     throws IOException
/*     */   {
/* 129 */     if (this.inPart)
/* 130 */       this.out.write(__CRLF);
/* 131 */     this.inPart = true;
/* 132 */     this.out.write(__DASHDASH);
/* 133 */     this.out.write(this.boundaryBytes);
/* 134 */     this.out.write(__CRLF);
/* 135 */     if (contentType != null)
/* 136 */       this.out.write(("Content-Type: " + contentType).getBytes(StandardCharsets.ISO_8859_1));
/* 137 */     this.out.write(__CRLF);
/* 138 */     for (int i = 0; (headers != null) && (i < headers.length); i++)
/*     */     {
/* 140 */       this.out.write(headers[i].getBytes(StandardCharsets.ISO_8859_1));
/* 141 */       this.out.write(__CRLF);
/*     */     }
/* 143 */     this.out.write(__CRLF);
/*     */   }
/*     */   
/*     */ 
/*     */   public void write(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 150 */     this.out.write(b, off, len);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\MultiPartOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */