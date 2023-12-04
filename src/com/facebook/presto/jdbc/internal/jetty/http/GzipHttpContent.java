/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.resource.Resource;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ReadableByteChannel;
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
/*     */ public class GzipHttpContent
/*     */   implements HttpContent
/*     */ {
/*     */   private final HttpContent _content;
/*     */   private final HttpContent _contentGz;
/*     */   public static final String ETAG_GZIP = "--gzip";
/*     */   public static final String ETAG_GZIP_QUOTE = "--gzip\"";
/*  36 */   public static final PreEncodedHttpField CONTENT_ENCODING_GZIP = new PreEncodedHttpField(HttpHeader.CONTENT_ENCODING, "gzip");
/*     */   
/*     */   public static String removeGzipFromETag(String etag)
/*     */   {
/*  40 */     if (etag == null)
/*  41 */       return null;
/*  42 */     int i = etag.indexOf("--gzip\"");
/*  43 */     if (i < 0)
/*  44 */       return etag;
/*  45 */     return etag.substring(0, i) + '"';
/*     */   }
/*     */   
/*     */   public GzipHttpContent(HttpContent content, HttpContent contentGz)
/*     */   {
/*  50 */     this._content = content;
/*  51 */     this._contentGz = contentGz;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  57 */     return this._content.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  63 */     return this._content.equals(obj);
/*     */   }
/*     */   
/*     */ 
/*     */   public Resource getResource()
/*     */   {
/*  69 */     return this._content.getResource();
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpField getETag()
/*     */   {
/*  75 */     return new HttpField(HttpHeader.ETAG, getETagValue());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getETagValue()
/*     */   {
/*  81 */     return this._content.getResource().getWeakETag("--gzip");
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpField getLastModified()
/*     */   {
/*  87 */     return this._content.getLastModified();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getLastModifiedValue()
/*     */   {
/*  93 */     return this._content.getLastModifiedValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpField getContentType()
/*     */   {
/*  99 */     return this._content.getContentType();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getContentTypeValue()
/*     */   {
/* 105 */     return this._content.getContentTypeValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpField getContentEncoding()
/*     */   {
/* 111 */     return CONTENT_ENCODING_GZIP;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getContentEncodingValue()
/*     */   {
/* 117 */     return CONTENT_ENCODING_GZIP.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCharacterEncoding()
/*     */   {
/* 123 */     return this._content.getCharacterEncoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public MimeTypes.Type getMimeType()
/*     */   {
/* 129 */     return this._content.getMimeType();
/*     */   }
/*     */   
/*     */ 
/*     */   public void release()
/*     */   {
/* 135 */     this._content.release();
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer getIndirectBuffer()
/*     */   {
/* 141 */     return this._contentGz.getIndirectBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer getDirectBuffer()
/*     */   {
/* 147 */     return this._contentGz.getDirectBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpField getContentLength()
/*     */   {
/* 153 */     return this._contentGz.getContentLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getContentLengthValue()
/*     */   {
/* 159 */     return this._contentGz.getContentLengthValue();
/*     */   }
/*     */   
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 165 */     return this._contentGz.getInputStream();
/*     */   }
/*     */   
/*     */   public ReadableByteChannel getReadableByteChannel()
/*     */     throws IOException
/*     */   {
/* 171 */     return this._contentGz.getReadableByteChannel();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 177 */     return String.format("GzipHttpContent@%x{r=%s|%s,lm=%s|%s,ct=%s}", new Object[] { Integer.valueOf(hashCode()), this._content
/* 178 */       .getResource(), this._contentGz.getResource(), 
/* 179 */       Long.valueOf(this._content.getResource().lastModified()), Long.valueOf(this._contentGz.getResource().lastModified()), 
/* 180 */       getContentType() });
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpContent getGzipContent()
/*     */   {
/* 186 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\GzipHttpContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */