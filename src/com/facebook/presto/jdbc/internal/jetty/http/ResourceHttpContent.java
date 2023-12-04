/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResourceHttpContent
/*     */   implements HttpContent
/*     */ {
/*     */   final Resource _resource;
/*     */   final String _contentType;
/*     */   final int _maxBuffer;
/*     */   HttpContent _gzip;
/*     */   String _etag;
/*     */   
/*     */   public ResourceHttpContent(Resource resource, String contentType)
/*     */   {
/*  48 */     this(resource, contentType, -1, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public ResourceHttpContent(Resource resource, String contentType, int maxBuffer)
/*     */   {
/*  54 */     this(resource, contentType, maxBuffer, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public ResourceHttpContent(Resource resource, String contentType, int maxBuffer, HttpContent gzip)
/*     */   {
/*  60 */     this._resource = resource;
/*  61 */     this._contentType = contentType;
/*  62 */     this._maxBuffer = maxBuffer;
/*  63 */     this._gzip = gzip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getContentTypeValue()
/*     */   {
/*  70 */     return this._contentType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpField getContentType()
/*     */   {
/*  77 */     return this._contentType == null ? null : new HttpField(HttpHeader.CONTENT_TYPE, this._contentType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpField getContentEncoding()
/*     */   {
/*  84 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getContentEncodingValue()
/*     */   {
/*  91 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getCharacterEncoding()
/*     */   {
/*  98 */     return this._contentType == null ? null : MimeTypes.getCharsetFromContentType(this._contentType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MimeTypes.Type getMimeType()
/*     */   {
/* 105 */     return this._contentType == null ? null : (MimeTypes.Type)MimeTypes.CACHE.get(MimeTypes.getContentTypeWithoutCharset(this._contentType));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpField getLastModified()
/*     */   {
/* 112 */     long lm = this._resource.lastModified();
/* 113 */     return lm >= 0L ? new HttpField(HttpHeader.LAST_MODIFIED, DateGenerator.formatDate(lm)) : null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getLastModifiedValue()
/*     */   {
/* 120 */     long lm = this._resource.lastModified();
/* 121 */     return lm >= 0L ? DateGenerator.formatDate(lm) : null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ByteBuffer getDirectBuffer()
/*     */   {
/* 128 */     if ((this._resource.length() <= 0L) || ((this._maxBuffer > 0) && (this._maxBuffer < this._resource.length()))) {
/* 129 */       return null;
/*     */     }
/*     */     try {
/* 132 */       return BufferUtil.toBuffer(this._resource, true);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 136 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpField getETag()
/*     */   {
/* 144 */     return new HttpField(HttpHeader.ETAG, getETagValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getETagValue()
/*     */   {
/* 151 */     return this._resource.getWeakETag();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ByteBuffer getIndirectBuffer()
/*     */   {
/* 158 */     if ((this._resource.length() <= 0L) || ((this._maxBuffer > 0) && (this._maxBuffer < this._resource.length()))) {
/* 159 */       return null;
/*     */     }
/*     */     try {
/* 162 */       return BufferUtil.toBuffer(this._resource, false);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 166 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpField getContentLength()
/*     */   {
/* 174 */     long l = this._resource.length();
/* 175 */     return l == -1L ? null : new HttpField.LongValueHttpField(HttpHeader.CONTENT_LENGTH, this._resource.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getContentLengthValue()
/*     */   {
/* 182 */     return this._resource.length();
/*     */   }
/*     */   
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 189 */     return this._resource.getInputStream();
/*     */   }
/*     */   
/*     */ 
/*     */   public ReadableByteChannel getReadableByteChannel()
/*     */     throws IOException
/*     */   {
/* 196 */     return this._resource.getReadableByteChannel();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Resource getResource()
/*     */   {
/* 203 */     return this._resource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void release()
/*     */   {
/* 210 */     this._resource.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 217 */     return String.format("%s@%x{r=%s,gz=%b}", new Object[] { getClass().getSimpleName(), Integer.valueOf(hashCode()), this._resource, Boolean.valueOf(this._gzip != null ? 1 : false) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpContent getGzipContent()
/*     */   {
/* 224 */     return this._gzip == null ? null : new GzipHttpContent(this, this._gzip);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\ResourceHttpContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */