/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.Listener.Adapter;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Locale;
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
/*     */ public abstract class BufferingResponseListener
/*     */   extends Adapter
/*     */ {
/*     */   private final int maxLength;
/*     */   private volatile ByteBuffer buffer;
/*     */   private volatile String mediaType;
/*     */   private volatile String encoding;
/*     */   
/*     */   public BufferingResponseListener()
/*     */   {
/*  53 */     this(2097152);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BufferingResponseListener(int maxLength)
/*     */   {
/*  63 */     this.maxLength = maxLength;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onHeaders(Response response)
/*     */   {
/*  69 */     super.onHeaders(response);
/*     */     
/*  71 */     HttpFields headers = response.getHeaders();
/*  72 */     long length = headers.getLongField(HttpHeader.CONTENT_LENGTH.asString());
/*  73 */     if (length > this.maxLength)
/*     */     {
/*  75 */       response.abort(new IllegalArgumentException("Buffering capacity exceeded"));
/*  76 */       return;
/*     */     }
/*     */     
/*  79 */     this.buffer = BufferUtil.allocate(length > 0L ? (int)length : 1024);
/*     */     
/*  81 */     String contentType = headers.get(HttpHeader.CONTENT_TYPE);
/*  82 */     if (contentType != null)
/*     */     {
/*  84 */       String media = contentType;
/*     */       
/*  86 */       String charset = "charset=";
/*  87 */       int index = contentType.toLowerCase(Locale.ENGLISH).indexOf(charset);
/*  88 */       if (index > 0)
/*     */       {
/*  90 */         media = contentType.substring(0, index);
/*  91 */         String encoding = contentType.substring(index + charset.length());
/*     */         
/*  93 */         int semicolon = encoding.indexOf(';');
/*  94 */         if (semicolon > 0)
/*  95 */           encoding = encoding.substring(0, semicolon).trim();
/*  96 */         this.encoding = encoding;
/*     */       }
/*     */       
/*  99 */       int semicolon = media.indexOf(';');
/* 100 */       if (semicolon > 0)
/* 101 */         media = media.substring(0, semicolon).trim();
/* 102 */       this.mediaType = media;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onContent(Response response, ByteBuffer content)
/*     */   {
/* 109 */     int length = content.remaining();
/* 110 */     if (length > BufferUtil.space(this.buffer))
/*     */     {
/* 112 */       int requiredCapacity = this.buffer == null ? 0 : this.buffer.capacity() + length;
/* 113 */       if (requiredCapacity > this.maxLength) {
/* 114 */         response.abort(new IllegalArgumentException("Buffering capacity exceeded"));
/*     */       }
/* 116 */       int newCapacity = Math.min(Integer.highestOneBit(requiredCapacity) << 1, this.maxLength);
/* 117 */       this.buffer = BufferUtil.ensureCapacity(this.buffer, newCapacity);
/*     */     }
/* 119 */     BufferUtil.append(this.buffer, content);
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract void onComplete(Result paramResult);
/*     */   
/*     */   public String getMediaType()
/*     */   {
/* 127 */     return this.mediaType;
/*     */   }
/*     */   
/*     */   public String getEncoding()
/*     */   {
/* 132 */     return this.encoding;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getContent()
/*     */   {
/* 141 */     if (this.buffer == null)
/* 142 */       return new byte[0];
/* 143 */     return BufferUtil.toArray(this.buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getContentAsString()
/*     */   {
/* 153 */     String encoding = this.encoding;
/* 154 */     if (encoding == null)
/* 155 */       return getContentAsString(StandardCharsets.UTF_8);
/* 156 */     return getContentAsString(encoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getContentAsString(String encoding)
/*     */   {
/* 166 */     if (this.buffer == null)
/* 167 */       return null;
/* 168 */     return BufferUtil.toString(this.buffer, Charset.forName(encoding));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getContentAsString(Charset encoding)
/*     */   {
/* 178 */     if (this.buffer == null)
/* 179 */       return null;
/* 180 */     return BufferUtil.toString(this.buffer, encoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream getContentAsInputStream()
/*     */   {
/* 188 */     if (this.buffer == null)
/* 189 */       return new ByteArrayInputStream(new byte[0]);
/* 190 */     return new ByteArrayInputStream(this.buffer.array(), this.buffer.arrayOffset(), this.buffer.remaining());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\BufferingResponseListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */