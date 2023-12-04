/*     */ package com.mchange.v1.io;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
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
/*     */ public final class InputStreamUtils
/*     */ {
/*  31 */   private static final MLogger logger = MLog.getLogger(InputStreamUtils.class);
/*     */   
/*     */   public static boolean compare(InputStream is1, InputStream is2, long num_bytes)
/*     */     throws IOException
/*     */   {
/*  36 */     for (long num_read = 0L; num_read < num_bytes; num_read += 1L) {
/*     */       int b;
/*  38 */       if ((b = is1.read()) != is2.read())
/*  39 */         return false;
/*  40 */       if (b < 0)
/*     */         break;
/*     */     }
/*  43 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean compare(InputStream is1, InputStream is2) throws IOException
/*     */   {
/*  48 */     int b = 0;
/*  49 */     while (b >= 0)
/*  50 */       if ((b = is1.read()) != is2.read())
/*  51 */         return false;
/*  52 */     return true;
/*     */   }
/*     */   
/*     */   public static byte[] getBytes(InputStream is, int max_len) throws IOException
/*     */   {
/*  57 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(max_len);
/*  58 */     int i = 0; for (int b = is.read(); (b >= 0) && (i < max_len); i++) {
/*  59 */       baos.write(b);b = is.read(); }
/*  60 */     return baos.toByteArray();
/*     */   }
/*     */   
/*     */   public static byte[] getBytes(InputStream is) throws IOException
/*     */   {
/*  65 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  66 */     for (int b = is.read(); b >= 0; b = is.read()) baos.write(b);
/*  67 */     return baos.toByteArray();
/*     */   }
/*     */   
/*     */   public static String getContentsAsString(InputStream is, String enc) throws IOException, UnsupportedEncodingException
/*     */   {
/*  72 */     return new String(getBytes(is), enc);
/*     */   }
/*     */   
/*     */   public static String getContentsAsString(InputStream is) throws IOException
/*     */   {
/*     */     try {
/*  78 */       return getContentsAsString(is, System.getProperty("file.encoding", "8859_1"));
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/*  81 */       throw new InternalError("You have no default character encoding, and iso-8859-1 is unsupported?!?!");
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getContentsAsString(InputStream is, int max_len, String enc)
/*     */     throws IOException, UnsupportedEncodingException
/*     */   {
/*  88 */     return new String(getBytes(is, max_len), enc);
/*     */   }
/*     */   
/*     */   public static String getContentsAsString(InputStream is, int max_len) throws IOException
/*     */   {
/*     */     try {
/*  94 */       return getContentsAsString(is, max_len, System.getProperty("file.encoding", "8859_1"));
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/*  97 */       throw new InternalError("You have no default character encoding, and iso-8859-1 is unsupported?!?!");
/*     */     }
/*     */   }
/*     */   
/*     */   public static InputStream getEmptyInputStream()
/*     */   {
/* 103 */     return EMPTY_ISTREAM;
/*     */   }
/*     */   
/*     */   public static void attemptClose(InputStream is) {
/*     */     try {
/* 108 */       if (is != null) is.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 112 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 113 */         logger.log(MLevel.WARNING, "InputStream close FAILED.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void skipFully(InputStream is, long num_bytes) throws EOFException, IOException {
/* 119 */     long num_skipped = 0L;
/* 120 */     while (num_skipped < num_bytes)
/*     */     {
/* 122 */       long just_skipped = is.skip(num_bytes - num_skipped);
/* 123 */       if (just_skipped > 0L) {
/* 124 */         num_skipped += just_skipped;
/*     */       }
/*     */       else {
/* 127 */         int test_byte = is.read();
/* 128 */         if (is.read() < 0) {
/* 129 */           throw new EOFException("Skipped only " + num_skipped + " bytes to end of file.");
/*     */         }
/* 131 */         num_skipped += 1L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 138 */   private static InputStream EMPTY_ISTREAM = new ByteArrayInputStream(new byte[0]);
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\io\InputStreamUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */