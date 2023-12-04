/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.FilterWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
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
/*     */ public class MultiPartWriter
/*     */   extends FilterWriter
/*     */ {
/*     */   private static final String __CRLF = "\r\n";
/*     */   private static final String __DASHDASH = "--";
/*     */   public static final String MULTIPART_MIXED = "multipart/mixed";
/*     */   public static final String MULTIPART_X_MIXED_REPLACE = "multipart/x-mixed-replace";
/*     */   private String boundary;
/*  45 */   private boolean inPart = false;
/*     */   
/*     */ 
/*     */   public MultiPartWriter(Writer out)
/*     */     throws IOException
/*     */   {
/*  51 */     super(out);
/*     */     
/*  53 */     this.boundary = ("jetty" + System.identityHashCode(this) + Long.toString(System.currentTimeMillis(), 36));
/*     */     
/*  55 */     this.inPart = false;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 29	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:inPart	Z
/*     */     //   4: ifeq +12 -> 16
/*     */     //   7: aload_0
/*     */     //   8: getfield 71	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:out	Ljava/io/Writer;
/*     */     //   11: ldc 9
/*     */     //   13: invokevirtual 77	java/io/Writer:write	(Ljava/lang/String;)V
/*     */     //   16: aload_0
/*     */     //   17: getfield 71	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:out	Ljava/io/Writer;
/*     */     //   20: ldc 12
/*     */     //   22: invokevirtual 77	java/io/Writer:write	(Ljava/lang/String;)V
/*     */     //   25: aload_0
/*     */     //   26: getfield 71	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:out	Ljava/io/Writer;
/*     */     //   29: aload_0
/*     */     //   30: getfield 64	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:boundary	Ljava/lang/String;
/*     */     //   33: invokevirtual 77	java/io/Writer:write	(Ljava/lang/String;)V
/*     */     //   36: aload_0
/*     */     //   37: getfield 71	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:out	Ljava/io/Writer;
/*     */     //   40: ldc 12
/*     */     //   42: invokevirtual 77	java/io/Writer:write	(Ljava/lang/String;)V
/*     */     //   45: aload_0
/*     */     //   46: getfield 71	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:out	Ljava/io/Writer;
/*     */     //   49: ldc 9
/*     */     //   51: invokevirtual 77	java/io/Writer:write	(Ljava/lang/String;)V
/*     */     //   54: aload_0
/*     */     //   55: iconst_0
/*     */     //   56: putfield 29	com/facebook/presto/jdbc/internal/jetty/util/MultiPartWriter:inPart	Z
/*     */     //   59: aload_0
/*     */     //   60: invokespecial 79	java/io/FilterWriter:close	()V
/*     */     //   63: goto +10 -> 73
/*     */     //   66: astore_1
/*     */     //   67: aload_0
/*     */     //   68: invokespecial 79	java/io/FilterWriter:close	()V
/*     */     //   71: aload_1
/*     */     //   72: athrow
/*     */     //   73: return
/*     */     // Line number table:
/*     */     //   Java source line #68	-> byte code offset #0
/*     */     //   Java source line #69	-> byte code offset #7
/*     */     //   Java source line #70	-> byte code offset #16
/*     */     //   Java source line #71	-> byte code offset #25
/*     */     //   Java source line #72	-> byte code offset #36
/*     */     //   Java source line #73	-> byte code offset #45
/*     */     //   Java source line #74	-> byte code offset #54
/*     */     //   Java source line #78	-> byte code offset #59
/*     */     //   Java source line #79	-> byte code offset #63
/*     */     //   Java source line #78	-> byte code offset #66
/*     */     //   Java source line #80	-> byte code offset #73
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	74	0	this	MultiPartWriter
/*     */     //   66	6	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	59	66	finally
/*     */   }
/*     */   
/*     */   public String getBoundary()
/*     */   {
/*  85 */     return this.boundary;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startPart(String contentType)
/*     */     throws IOException
/*     */   {
/*  96 */     if (this.inPart)
/*  97 */       this.out.write("\r\n");
/*  98 */     this.out.write("--");
/*  99 */     this.out.write(this.boundary);
/* 100 */     this.out.write("\r\n");
/* 101 */     this.out.write("Content-Type: ");
/* 102 */     this.out.write(contentType);
/* 103 */     this.out.write("\r\n");
/* 104 */     this.out.write("\r\n");
/* 105 */     this.inPart = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void endPart()
/*     */     throws IOException
/*     */   {
/* 115 */     if (this.inPart)
/* 116 */       this.out.write("\r\n");
/* 117 */     this.inPart = false;
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
/* 130 */       this.out.write("\r\n");
/* 131 */     this.out.write("--");
/* 132 */     this.out.write(this.boundary);
/* 133 */     this.out.write("\r\n");
/* 134 */     this.out.write("Content-Type: ");
/* 135 */     this.out.write(contentType);
/* 136 */     this.out.write("\r\n");
/* 137 */     for (int i = 0; (headers != null) && (i < headers.length); i++)
/*     */     {
/* 139 */       this.out.write(headers[i]);
/* 140 */       this.out.write("\r\n");
/*     */     }
/* 142 */     this.out.write("\r\n");
/* 143 */     this.inPart = true;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\MultiPartWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */