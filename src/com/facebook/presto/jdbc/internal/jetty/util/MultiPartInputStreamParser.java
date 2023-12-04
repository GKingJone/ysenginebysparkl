/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import javax.servlet.MultipartConfigElement;
/*     */ import javax.servlet.http.Part;
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
/*     */ public class MultiPartInputStreamParser
/*     */ {
/*  57 */   private static final Logger LOG = Log.getLogger(MultiPartInputStreamParser.class);
/*  58 */   public static final MultipartConfigElement __DEFAULT_MULTIPART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
/*     */   
/*     */   protected InputStream _in;
/*     */   protected MultipartConfigElement _config;
/*     */   protected String _contentType;
/*     */   protected MultiMap<Part> _parts;
/*     */   protected Exception _err;
/*     */   protected File _tmpDir;
/*     */   protected File _contextTmpDir;
/*     */   protected boolean _deleteOnExit;
/*     */   protected boolean _writeFilesWithFilenames;
/*     */   
/*     */   public class MultiPart
/*     */     implements Part
/*     */   {
/*     */     protected String _name;
/*     */     protected String _filename;
/*     */     protected File _file;
/*     */     protected OutputStream _out;
/*     */     protected ByteArrayOutputStream2 _bout;
/*     */     protected String _contentType;
/*     */     protected MultiMap<String> _headers;
/*  80 */     protected long _size = 0L;
/*  81 */     protected boolean _temporary = true;
/*     */     
/*     */     public MultiPart(String name, String filename)
/*     */       throws IOException
/*     */     {
/*  86 */       this._name = name;
/*  87 */       this._filename = filename;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/*  93 */       return String.format("Part{n=%s,fn=%s,ct=%s,s=%d,t=%b,f=%s}", new Object[] { this._name, this._filename, this._contentType, Long.valueOf(this._size), Boolean.valueOf(this._temporary), this._file });
/*     */     }
/*     */     
/*     */     protected void setContentType(String contentType) {
/*  97 */       this._contentType = contentType;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void open()
/*     */       throws IOException
/*     */     {
/* 107 */       if ((MultiPartInputStreamParser.this.isWriteFilesWithFilenames()) && (this._filename != null) && (this._filename.trim().length() > 0))
/*     */       {
/* 109 */         createFile();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 115 */         this._out = (this._bout = new ByteArrayOutputStream2());
/*     */       }
/*     */     }
/*     */     
/*     */     protected void close()
/*     */       throws IOException
/*     */     {
/* 122 */       this._out.close();
/*     */     }
/*     */     
/*     */ 
/*     */     protected void write(int b)
/*     */       throws IOException
/*     */     {
/* 129 */       if ((MultiPartInputStreamParser.this._config.getMaxFileSize() > 0L) && (this._size + 1L > MultiPartInputStreamParser.this._config.getMaxFileSize())) {
/* 130 */         throw new IllegalStateException("Multipart Mime part " + this._name + " exceeds max filesize");
/*     */       }
/* 132 */       if ((MultiPartInputStreamParser.this._config.getFileSizeThreshold() > 0) && (this._size + 1L > MultiPartInputStreamParser.this._config.getFileSizeThreshold()) && (this._file == null)) {
/* 133 */         createFile();
/*     */       }
/* 135 */       this._out.write(b);
/* 136 */       this._size += 1L;
/*     */     }
/*     */     
/*     */     protected void write(byte[] bytes, int offset, int length)
/*     */       throws IOException
/*     */     {
/* 142 */       if ((MultiPartInputStreamParser.this._config.getMaxFileSize() > 0L) && (this._size + length > MultiPartInputStreamParser.this._config.getMaxFileSize())) {
/* 143 */         throw new IllegalStateException("Multipart Mime part " + this._name + " exceeds max filesize");
/*     */       }
/* 145 */       if ((MultiPartInputStreamParser.this._config.getFileSizeThreshold() > 0) && (this._size + length > MultiPartInputStreamParser.this._config.getFileSizeThreshold()) && (this._file == null)) {
/* 146 */         createFile();
/*     */       }
/* 148 */       this._out.write(bytes, offset, length);
/* 149 */       this._size += length;
/*     */     }
/*     */     
/*     */     protected void createFile()
/*     */       throws IOException
/*     */     {
/* 155 */       this._file = File.createTempFile("MultiPart", "", MultiPartInputStreamParser.this._tmpDir);
/*     */       
/* 157 */       if (MultiPartInputStreamParser.this._deleteOnExit)
/* 158 */         this._file.deleteOnExit();
/* 159 */       FileOutputStream fos = new FileOutputStream(this._file);
/* 160 */       BufferedOutputStream bos = new BufferedOutputStream(fos);
/*     */       
/* 162 */       if ((this._size > 0L) && (this._out != null))
/*     */       {
/*     */ 
/* 165 */         this._out.flush();
/* 166 */         this._bout.writeTo(bos);
/* 167 */         this._out.close();
/* 168 */         this._bout = null;
/*     */       }
/* 170 */       this._out = bos;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setHeaders(MultiMap<String> headers)
/*     */     {
/* 177 */       this._headers = headers;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getContentType()
/*     */     {
/* 185 */       return this._contentType;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getHeader(String name)
/*     */     {
/* 193 */       if (name == null)
/* 194 */         return null;
/* 195 */       return (String)this._headers.getValue(name.toLowerCase(Locale.ENGLISH), 0);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Collection<String> getHeaderNames()
/*     */     {
/* 203 */       return this._headers.keySet();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Collection<String> getHeaders(String name)
/*     */     {
/* 211 */       return this._headers.getValues(name);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public InputStream getInputStream()
/*     */       throws IOException
/*     */     {
/* 219 */       if (this._file != null)
/*     */       {
/*     */ 
/* 222 */         return new BufferedInputStream(new FileInputStream(this._file));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 227 */       return new ByteArrayInputStream(this._bout.getBuf(), 0, this._bout.size());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getSubmittedFileName()
/*     */     {
/* 238 */       return getContentDispositionFilename();
/*     */     }
/*     */     
/*     */     public byte[] getBytes()
/*     */     {
/* 243 */       if (this._bout != null)
/* 244 */         return this._bout.toByteArray();
/* 245 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getName()
/*     */     {
/* 253 */       return this._name;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getSize()
/*     */     {
/* 261 */       return this._size;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void write(String fileName)
/*     */       throws IOException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 69	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_file	Ljava/io/File;
/*     */       //   4: ifnonnull +95 -> 99
/*     */       //   7: aload_0
/*     */       //   8: iconst_0
/*     */       //   9: putfield 42	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_temporary	Z
/*     */       //   12: aload_0
/*     */       //   13: new 155	java/io/File
/*     */       //   16: dup
/*     */       //   17: aload_0
/*     */       //   18: getfield 35	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser;
/*     */       //   21: getfield 153	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser:_tmpDir	Ljava/io/File;
/*     */       //   24: aload_1
/*     */       //   25: invokespecial 255	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
/*     */       //   28: putfield 69	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_file	Ljava/io/File;
/*     */       //   31: aconst_null
/*     */       //   32: astore_2
/*     */       //   33: new 172	java/io/BufferedOutputStream
/*     */       //   36: dup
/*     */       //   37: new 167	java/io/FileOutputStream
/*     */       //   40: dup
/*     */       //   41: aload_0
/*     */       //   42: getfield 69	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_file	Ljava/io/File;
/*     */       //   45: invokespecial 170	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
/*     */       //   48: invokespecial 175	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
/*     */       //   51: astore_2
/*     */       //   52: aload_0
/*     */       //   53: getfield 98	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_bout	Lcom/facebook/presto/jdbc/internal/jetty/util/ByteArrayOutputStream2;
/*     */       //   56: aload_2
/*     */       //   57: invokevirtual 181	com/facebook/presto/jdbc/internal/jetty/util/ByteArrayOutputStream2:writeTo	(Ljava/io/OutputStream;)V
/*     */       //   60: aload_2
/*     */       //   61: invokevirtual 256	java/io/BufferedOutputStream:flush	()V
/*     */       //   64: aload_2
/*     */       //   65: ifnull +7 -> 72
/*     */       //   68: aload_2
/*     */       //   69: invokevirtual 257	java/io/BufferedOutputStream:close	()V
/*     */       //   72: aload_0
/*     */       //   73: aconst_null
/*     */       //   74: putfield 98	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_bout	Lcom/facebook/presto/jdbc/internal/jetty/util/ByteArrayOutputStream2;
/*     */       //   77: goto +19 -> 96
/*     */       //   80: astore_3
/*     */       //   81: aload_2
/*     */       //   82: ifnull +7 -> 89
/*     */       //   85: aload_2
/*     */       //   86: invokevirtual 257	java/io/BufferedOutputStream:close	()V
/*     */       //   89: aload_0
/*     */       //   90: aconst_null
/*     */       //   91: putfield 98	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_bout	Lcom/facebook/presto/jdbc/internal/jetty/util/ByteArrayOutputStream2;
/*     */       //   94: aload_3
/*     */       //   95: athrow
/*     */       //   96: goto +50 -> 146
/*     */       //   99: aload_0
/*     */       //   100: iconst_0
/*     */       //   101: putfield 42	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_temporary	Z
/*     */       //   104: aload_0
/*     */       //   105: getfield 69	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_file	Ljava/io/File;
/*     */       //   108: invokevirtual 263	java/io/File:toPath	()Ljava/nio/file/Path;
/*     */       //   111: astore_2
/*     */       //   112: aload_2
/*     */       //   113: aload_1
/*     */       //   114: invokeinterface 269 2 0
/*     */       //   119: astore_3
/*     */       //   120: aload_2
/*     */       //   121: aload_3
/*     */       //   122: iconst_1
/*     */       //   123: anewarray 271	java/nio/file/CopyOption
/*     */       //   126: dup
/*     */       //   127: iconst_0
/*     */       //   128: getstatic 277	java/nio/file/StandardCopyOption:REPLACE_EXISTING	Ljava/nio/file/StandardCopyOption;
/*     */       //   131: aastore
/*     */       //   132: invokestatic 283	java/nio/file/Files:move	(Ljava/nio/file/Path;Ljava/nio/file/Path;[Ljava/nio/file/CopyOption;)Ljava/nio/file/Path;
/*     */       //   135: pop
/*     */       //   136: aload_0
/*     */       //   137: aload_3
/*     */       //   138: invokeinterface 287 1 0
/*     */       //   143: putfield 69	com/facebook/presto/jdbc/internal/jetty/util/MultiPartInputStreamParser$MultiPart:_file	Ljava/io/File;
/*     */       //   146: return
/*     */       // Line number table:
/*     */       //   Java source line #269	-> byte code offset #0
/*     */       //   Java source line #271	-> byte code offset #7
/*     */       //   Java source line #274	-> byte code offset #12
/*     */       //   Java source line #276	-> byte code offset #31
/*     */       //   Java source line #279	-> byte code offset #33
/*     */       //   Java source line #280	-> byte code offset #52
/*     */       //   Java source line #281	-> byte code offset #60
/*     */       //   Java source line #285	-> byte code offset #64
/*     */       //   Java source line #286	-> byte code offset #68
/*     */       //   Java source line #287	-> byte code offset #72
/*     */       //   Java source line #288	-> byte code offset #77
/*     */       //   Java source line #285	-> byte code offset #80
/*     */       //   Java source line #286	-> byte code offset #85
/*     */       //   Java source line #287	-> byte code offset #89
/*     */       //   Java source line #289	-> byte code offset #96
/*     */       //   Java source line #293	-> byte code offset #99
/*     */       //   Java source line #295	-> byte code offset #104
/*     */       //   Java source line #296	-> byte code offset #112
/*     */       //   Java source line #297	-> byte code offset #120
/*     */       //   Java source line #298	-> byte code offset #136
/*     */       //   Java source line #300	-> byte code offset #146
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	147	0	this	MultiPart
/*     */       //   0	147	1	fileName	String
/*     */       //   32	54	2	bos	BufferedOutputStream
/*     */       //   111	10	2	src	java.nio.file.Path
/*     */       //   80	15	3	localObject	Object
/*     */       //   119	19	3	target	java.nio.file.Path
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   33	64	80	finally
/*     */     }
/*     */     
/*     */     public void delete()
/*     */       throws IOException
/*     */     {
/* 309 */       if ((this._file != null) && (this._file.exists())) {
/* 310 */         this._file.delete();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void cleanUp()
/*     */       throws IOException
/*     */     {
/* 320 */       if ((this._temporary) && (this._file != null) && (this._file.exists())) {
/* 321 */         this._file.delete();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public File getFile()
/*     */     {
/* 331 */       return this._file;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getContentDispositionFilename()
/*     */     {
/* 341 */       return this._filename;
/*     */     }
/*     */   }
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
/*     */   public MultiPartInputStreamParser(InputStream in, String contentType, MultipartConfigElement config, File contextTmpDir)
/*     */   {
/* 356 */     this._in = new ReadLineInputStream(in);
/* 357 */     this._contentType = contentType;
/* 358 */     this._config = config;
/* 359 */     this._contextTmpDir = contextTmpDir;
/* 360 */     if (this._contextTmpDir == null) {
/* 361 */       this._contextTmpDir = new File(System.getProperty("java.io.tmpdir"));
/*     */     }
/* 363 */     if (this._config == null) {
/* 364 */       this._config = new MultipartConfigElement(this._contextTmpDir.getAbsolutePath());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<Part> getParsedParts()
/*     */   {
/* 373 */     if (this._parts == null) {
/* 374 */       return Collections.emptyList();
/*     */     }
/* 376 */     Collection<List<Part>> values = this._parts.values();
/* 377 */     List<Part> parts = new ArrayList();
/* 378 */     for (List<Part> o : values)
/*     */     {
/* 380 */       List<Part> asList = LazyList.getList(o, false);
/* 381 */       parts.addAll(asList);
/*     */     }
/* 383 */     return parts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deleteParts()
/*     */     throws MultiException
/*     */   {
/* 394 */     Collection<Part> parts = getParsedParts();
/* 395 */     MultiException err = new MultiException();
/* 396 */     for (Part p : parts)
/*     */     {
/*     */       try
/*     */       {
/* 400 */         ((MultiPart)p).cleanUp();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 404 */         err.add(e);
/*     */       }
/*     */     }
/* 407 */     this._parts.clear();
/*     */     
/* 409 */     err.ifExceptionThrowMulti();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<Part> getParts()
/*     */     throws IOException
/*     */   {
/* 422 */     parse();
/* 423 */     throwIfError();
/*     */     
/*     */ 
/* 426 */     Collection<List<Part>> values = this._parts.values();
/* 427 */     List<Part> parts = new ArrayList();
/* 428 */     for (List<Part> o : values)
/*     */     {
/* 430 */       List<Part> asList = LazyList.getList(o, false);
/* 431 */       parts.addAll(asList);
/*     */     }
/* 433 */     return parts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Part getPart(String name)
/*     */     throws IOException
/*     */   {
/* 447 */     parse();
/* 448 */     throwIfError();
/* 449 */     return (Part)this._parts.getValue(name, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void throwIfError()
/*     */     throws IOException
/*     */   {
/* 460 */     if (this._err != null)
/*     */     {
/* 462 */       if ((this._err instanceof IOException))
/* 463 */         throw ((IOException)this._err);
/* 464 */       if ((this._err instanceof IllegalStateException))
/* 465 */         throw ((IllegalStateException)this._err);
/* 466 */       throw new IllegalStateException(this._err);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void parse()
/*     */   {
/* 477 */     if ((this._parts != null) || (this._err != null)) {
/* 478 */       return;
/*     */     }
/*     */     
/* 481 */     long total = 0L;
/* 482 */     this._parts = new MultiMap();
/*     */     
/*     */ 
/* 485 */     if ((this._contentType == null) || (!this._contentType.startsWith("multipart/form-data"))) {
/* 486 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 492 */       if (this._config.getLocation() == null) {
/* 493 */         this._tmpDir = this._contextTmpDir;
/* 494 */       } else if ("".equals(this._config.getLocation())) {
/* 495 */         this._tmpDir = this._contextTmpDir;
/*     */       }
/*     */       else {
/* 498 */         File f = new File(this._config.getLocation());
/* 499 */         if (f.isAbsolute()) {
/* 500 */           this._tmpDir = f;
/*     */         } else {
/* 502 */           this._tmpDir = new File(this._contextTmpDir, this._config.getLocation());
/*     */         }
/*     */       }
/* 505 */       if (!this._tmpDir.exists()) {
/* 506 */         this._tmpDir.mkdirs();
/*     */       }
/* 508 */       String contentTypeBoundary = "";
/* 509 */       int bstart = this._contentType.indexOf("boundary=");
/* 510 */       if (bstart >= 0)
/*     */       {
/* 512 */         int bend = this._contentType.indexOf(";", bstart);
/* 513 */         bend = bend < 0 ? this._contentType.length() : bend;
/* 514 */         contentTypeBoundary = QuotedStringTokenizer.unquote(value(this._contentType.substring(bstart, bend)).trim());
/*     */       }
/*     */       
/* 517 */       String boundary = "--" + contentTypeBoundary;
/* 518 */       String lastBoundary = boundary + "--";
/* 519 */       byte[] byteBoundary = lastBoundary.getBytes(StandardCharsets.ISO_8859_1);
/*     */       
/*     */ 
/* 522 */       String line = null;
/*     */       try
/*     */       {
/* 525 */         line = ((ReadLineInputStream)this._in).readLine();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 529 */         LOG.warn("Badly formatted multipart request", new Object[0]);
/* 530 */         throw e;
/*     */       }
/*     */       
/* 533 */       if (line == null) {
/* 534 */         throw new IOException("Missing content for multipart request");
/*     */       }
/* 536 */       boolean badFormatLogged = false;
/* 537 */       line = line.trim();
/* 538 */       while ((line != null) && (!line.equals(boundary)) && (!line.equals(lastBoundary)))
/*     */       {
/* 540 */         if (!badFormatLogged)
/*     */         {
/* 542 */           LOG.warn("Badly formatted multipart request", new Object[0]);
/* 543 */           badFormatLogged = true;
/*     */         }
/* 545 */         line = ((ReadLineInputStream)this._in).readLine();
/* 546 */         line = line == null ? line : line.trim();
/*     */       }
/*     */       
/* 549 */       if (line == null) {
/* 550 */         throw new IOException("Missing initial multi part boundary");
/*     */       }
/*     */       
/* 553 */       if (line.equals(lastBoundary)) {
/* 554 */         return;
/*     */       }
/*     */       
/* 557 */       boolean lastPart = false;
/*     */       
/* 559 */       while (!lastPart)
/*     */       {
/* 561 */         String contentDisposition = null;
/* 562 */         String contentType = null;
/* 563 */         String contentTransferEncoding = null;
/*     */         
/* 565 */         MultiMap<String> headers = new MultiMap();
/*     */         for (;;)
/*     */         {
/* 568 */           line = ((ReadLineInputStream)this._in).readLine();
/*     */           
/*     */ 
/* 571 */           if (line == null) {
/*     */             break label1411;
/*     */           }
/*     */           
/* 575 */           if ("".equals(line)) {
/*     */             break;
/*     */           }
/* 578 */           total += line.length();
/* 579 */           if ((this._config.getMaxRequestSize() > 0L) && (total > this._config.getMaxRequestSize())) {
/* 580 */             throw new IllegalStateException("Request exceeds maxRequestSize (" + this._config.getMaxRequestSize() + ")");
/*     */           }
/*     */           
/* 583 */           int c = line.indexOf(':', 0);
/* 584 */           if (c > 0)
/*     */           {
/* 586 */             String key = line.substring(0, c).trim().toLowerCase(Locale.ENGLISH);
/* 587 */             String value = line.substring(c + 1, line.length()).trim();
/* 588 */             headers.put(key, value);
/* 589 */             if (key.equalsIgnoreCase("content-disposition"))
/* 590 */               contentDisposition = value;
/* 591 */             if (key.equalsIgnoreCase("content-type"))
/* 592 */               contentType = value;
/* 593 */             if (key.equals("content-transfer-encoding")) {
/* 594 */               contentTransferEncoding = value;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 599 */         boolean form_data = false;
/* 600 */         if (contentDisposition == null)
/*     */         {
/* 602 */           throw new IOException("Missing content-disposition");
/*     */         }
/*     */         
/* 605 */         QuotedStringTokenizer tok = new QuotedStringTokenizer(contentDisposition, ";", false, true);
/* 606 */         String name = null;
/* 607 */         String filename = null;
/* 608 */         while (tok.hasMoreTokens())
/*     */         {
/* 610 */           String t = tok.nextToken().trim();
/* 611 */           String tl = t.toLowerCase(Locale.ENGLISH);
/* 612 */           if (t.startsWith("form-data")) {
/* 613 */             form_data = true;
/* 614 */           } else if (tl.startsWith("name=")) {
/* 615 */             name = value(t);
/* 616 */           } else if (tl.startsWith("filename=")) {
/* 617 */             filename = filenameValue(t);
/*     */           }
/*     */         }
/*     */         
/* 621 */         if ((form_data) && 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 630 */           (name != null))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 636 */           MultiPart part = new MultiPart(name, filename);
/* 637 */           part.setHeaders(headers);
/* 638 */           part.setContentType(contentType);
/* 639 */           this._parts.add(name, part);
/* 640 */           part.open();
/*     */           
/* 642 */           InputStream partInput = null;
/* 643 */           if ("base64".equalsIgnoreCase(contentTransferEncoding))
/*     */           {
/* 645 */             partInput = new Base64InputStream((ReadLineInputStream)this._in);
/*     */           }
/* 647 */           else if ("quoted-printable".equalsIgnoreCase(contentTransferEncoding))
/*     */           {
/* 649 */             partInput = new FilterInputStream(this._in)
/*     */             {
/*     */               public int read()
/*     */                 throws IOException
/*     */               {
/* 654 */                 int c = this.in.read();
/* 655 */                 if ((c >= 0) && (c == 61))
/*     */                 {
/* 657 */                   int hi = this.in.read();
/* 658 */                   int lo = this.in.read();
/* 659 */                   if ((hi < 0) || (lo < 0))
/*     */                   {
/* 661 */                     throw new IOException("Unexpected end to quoted-printable byte");
/*     */                   }
/* 663 */                   char[] chars = { (char)hi, (char)lo };
/* 664 */                   c = Integer.parseInt(new String(chars), 16);
/*     */                 }
/* 666 */                 return c;
/*     */               }
/*     */               
/*     */             };
/*     */           } else {
/* 671 */             partInput = this._in;
/*     */           }
/*     */           
/*     */           try
/*     */           {
/* 676 */             int state = -2;
/*     */             
/* 678 */             boolean cr = false;
/* 679 */             boolean lf = false;
/*     */             
/*     */ 
/*     */             for (;;)
/*     */             {
/* 684 */               int b = 0;
/* 685 */               int c; while ((c = state != -2 ? state : partInput.read()) != -1)
/*     */               {
/* 687 */                 total += 1L;
/* 688 */                 if ((this._config.getMaxRequestSize() > 0L) && (total > this._config.getMaxRequestSize())) {
/* 689 */                   throw new IllegalStateException("Request exceeds maxRequestSize (" + this._config.getMaxRequestSize() + ")");
/*     */                 }
/* 691 */                 state = -2;
/*     */                 
/*     */ 
/* 694 */                 if ((c == 13) || (c == 10))
/*     */                 {
/* 696 */                   if (c != 13)
/*     */                     break;
/* 698 */                   partInput.mark(1);
/* 699 */                   int tmp = partInput.read();
/* 700 */                   if (tmp != 10) {
/* 701 */                     partInput.reset();
/*     */                   } else
/* 703 */                     state = tmp;
/* 704 */                   break;
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 709 */                 if ((b >= 0) && (b < byteBoundary.length) && (c == byteBoundary[b]))
/*     */                 {
/* 711 */                   b++;
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/*     */ 
/* 717 */                   if (cr) {
/* 718 */                     part.write(13);
/*     */                   }
/* 720 */                   if (lf) {
/* 721 */                     part.write(10);
/*     */                   }
/* 723 */                   cr = lf = 0;
/* 724 */                   if (b > 0) {
/* 725 */                     part.write(byteBoundary, 0, b);
/*     */                   }
/* 727 */                   b = -1;
/* 728 */                   part.write(c);
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/* 733 */               if (((b > 0) && (b < byteBoundary.length - 2)) || (b == byteBoundary.length - 1))
/*     */               {
/* 735 */                 if (cr) {
/* 736 */                   part.write(13);
/*     */                 }
/* 738 */                 if (lf) {
/* 739 */                   part.write(10);
/*     */                 }
/* 741 */                 cr = lf = 0;
/* 742 */                 part.write(byteBoundary, 0, b);
/* 743 */                 b = -1;
/*     */               }
/*     */               
/*     */ 
/* 747 */               if ((b > 0) || (c == -1))
/*     */               {
/*     */ 
/* 750 */                 if (b == byteBoundary.length)
/* 751 */                   lastPart = true;
/* 752 */                 if (state != 10) break;
/* 753 */                 state = -2; break;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 758 */               if (cr) {
/* 759 */                 part.write(13);
/*     */               }
/* 761 */               if (lf) {
/* 762 */                 part.write(10);
/*     */               }
/* 764 */               cr = c == 13;
/* 765 */               lf = (c == 10) || (state == 10);
/* 766 */               if (state == 10) {
/* 767 */                 state = -2;
/*     */               }
/*     */             }
/*     */           }
/*     */           finally {
/* 772 */             part.close();
/*     */           } } }
/*     */       label1411:
/* 775 */       if (lastPart)
/*     */       {
/* 777 */         while (line != null) {
/* 778 */           line = ((ReadLineInputStream)this._in).readLine();
/*     */         }
/*     */       }
/* 781 */       throw new IOException("Incomplete parts");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 785 */       this._err = e;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDeleteOnExit(boolean deleteOnExit)
/*     */   {
/* 791 */     this._deleteOnExit = deleteOnExit;
/*     */   }
/*     */   
/*     */   public void setWriteFilesWithFilenames(boolean writeFilesWithFilenames)
/*     */   {
/* 796 */     this._writeFilesWithFilenames = writeFilesWithFilenames;
/*     */   }
/*     */   
/*     */   public boolean isWriteFilesWithFilenames()
/*     */   {
/* 801 */     return this._writeFilesWithFilenames;
/*     */   }
/*     */   
/*     */   public boolean isDeleteOnExit()
/*     */   {
/* 806 */     return this._deleteOnExit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String value(String nameEqualsValue)
/*     */   {
/* 813 */     int idx = nameEqualsValue.indexOf('=');
/* 814 */     String value = nameEqualsValue.substring(idx + 1).trim();
/* 815 */     return QuotedStringTokenizer.unquoteOnly(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String filenameValue(String nameEqualsValue)
/*     */   {
/* 822 */     int idx = nameEqualsValue.indexOf('=');
/* 823 */     String value = nameEqualsValue.substring(idx + 1).trim();
/*     */     
/* 825 */     if (value.matches(".??[a-z,A-Z]\\:\\\\[^\\\\].*"))
/*     */     {
/*     */ 
/*     */ 
/* 829 */       char first = value.charAt(0);
/* 830 */       if ((first == '"') || (first == '\''))
/* 831 */         value = value.substring(1);
/* 832 */       char last = value.charAt(value.length() - 1);
/* 833 */       if ((last == '"') || (last == '\'')) {
/* 834 */         value = value.substring(0, value.length() - 1);
/*     */       }
/* 836 */       return value;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 843 */     return QuotedStringTokenizer.unquoteOnly(value, true);
/*     */   }
/*     */   
/*     */ 
/*     */   private static class Base64InputStream
/*     */     extends InputStream
/*     */   {
/*     */     ReadLineInputStream _in;
/*     */     
/*     */     String _line;
/*     */     byte[] _buffer;
/*     */     int _pos;
/*     */     
/*     */     public Base64InputStream(ReadLineInputStream rlis)
/*     */     {
/* 858 */       this._in = rlis;
/*     */     }
/*     */     
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 864 */       if ((this._buffer == null) || (this._pos >= this._buffer.length))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 870 */         this._line = this._in.readLine();
/* 871 */         if (this._line == null)
/* 872 */           return -1;
/* 873 */         if (this._line.startsWith("--")) {
/* 874 */           this._buffer = (this._line + "\r\n").getBytes();
/* 875 */         } else if (this._line.length() == 0) {
/* 876 */           this._buffer = "\r\n".getBytes();
/*     */         }
/*     */         else {
/* 879 */           ByteArrayOutputStream baos = new ByteArrayOutputStream(4 * this._line.length() / 3 + 2);
/* 880 */           B64Code.decode(this._line, baos);
/* 881 */           baos.write(13);
/* 882 */           baos.write(10);
/* 883 */           this._buffer = baos.toByteArray();
/*     */         }
/*     */         
/* 886 */         this._pos = 0;
/*     */       }
/*     */       
/* 889 */       return this._buffer[(this._pos++)];
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\MultiPartInputStreamParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */