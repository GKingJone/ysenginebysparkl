/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ public class MetaData
/*     */   implements Iterable<HttpField>
/*     */ {
/*     */   private HttpVersion _httpVersion;
/*     */   private HttpFields _fields;
/*     */   private long _contentLength;
/*     */   
/*     */   public MetaData(HttpVersion version, HttpFields fields)
/*     */   {
/*  32 */     this(version, fields, Long.MIN_VALUE);
/*     */   }
/*     */   
/*     */   public MetaData(HttpVersion version, HttpFields fields, long contentLength)
/*     */   {
/*  37 */     this._httpVersion = version;
/*  38 */     this._fields = fields;
/*  39 */     this._contentLength = contentLength;
/*     */   }
/*     */   
/*     */   protected void recycle()
/*     */   {
/*  44 */     this._httpVersion = null;
/*  45 */     if (this._fields != null)
/*  46 */       this._fields.clear();
/*  47 */     this._contentLength = Long.MIN_VALUE;
/*     */   }
/*     */   
/*     */   public boolean isRequest()
/*     */   {
/*  52 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isResponse()
/*     */   {
/*  57 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpVersion getVersion()
/*     */   {
/*  65 */     return this._httpVersion;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHttpVersion(HttpVersion httpVersion)
/*     */   {
/*  73 */     this._httpVersion = httpVersion;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpFields getFields()
/*     */   {
/*  81 */     return this._fields;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getContentLength()
/*     */   {
/*  89 */     if (this._contentLength == Long.MIN_VALUE)
/*     */     {
/*  91 */       if (this._fields != null)
/*     */       {
/*  93 */         HttpField field = this._fields.getField(HttpHeader.CONTENT_LENGTH);
/*  94 */         this._contentLength = (field == null ? -1L : field.getLongValue());
/*     */       }
/*     */     }
/*  97 */     return this._contentLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<HttpField> iterator()
/*     */   {
/* 106 */     HttpFields fields = getFields();
/* 107 */     return fields == null ? Collections.emptyIterator() : fields.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 113 */     StringBuilder out = new StringBuilder();
/* 114 */     for (HttpField field : this)
/* 115 */       out.append(field).append(System.lineSeparator());
/* 116 */     return out.toString();
/*     */   }
/*     */   
/*     */   public static class Request extends MetaData
/*     */   {
/*     */     private String _method;
/*     */     private HttpURI _uri;
/*     */     
/*     */     public Request(HttpFields fields)
/*     */     {
/* 126 */       this(null, null, null, fields);
/*     */     }
/*     */     
/*     */     public Request(String method, HttpURI uri, HttpVersion version, HttpFields fields)
/*     */     {
/* 131 */       this(method, uri, version, fields, Long.MIN_VALUE);
/*     */     }
/*     */     
/*     */     public Request(String method, HttpURI uri, HttpVersion version, HttpFields fields, long contentLength)
/*     */     {
/* 136 */       super(fields, contentLength);
/* 137 */       this._method = method;
/* 138 */       this._uri = uri;
/*     */     }
/*     */     
/*     */     public Request(String method, HttpScheme scheme, HostPortHttpField hostPort, String uri, HttpVersion version, HttpFields fields)
/*     */     {
/* 143 */       this(method, new HttpURI(scheme == null ? null : scheme.asString(), hostPort.getHost(), hostPort.getPort(), uri), version, fields);
/*     */     }
/*     */     
/*     */     public Request(String method, HttpScheme scheme, HostPortHttpField hostPort, String uri, HttpVersion version, HttpFields fields, long contentLength)
/*     */     {
/* 148 */       this(method, new HttpURI(scheme == null ? null : scheme.asString(), hostPort.getHost(), hostPort.getPort(), uri), version, fields, contentLength);
/*     */     }
/*     */     
/*     */     public Request(String method, String scheme, HostPortHttpField hostPort, String uri, HttpVersion version, HttpFields fields, long contentLength)
/*     */     {
/* 153 */       this(method, new HttpURI(scheme, hostPort.getHost(), hostPort.getPort(), uri), version, fields, contentLength);
/*     */     }
/*     */     
/*     */     public Request(Request request)
/*     */     {
/* 158 */       this(request.getMethod(), new HttpURI(request.getURI()), request.getVersion(), new HttpFields(request.getFields()), request.getContentLength());
/*     */     }
/*     */     
/*     */ 
/*     */     public void recycle()
/*     */     {
/* 164 */       super.recycle();
/* 165 */       this._method = null;
/* 166 */       if (this._uri != null) {
/* 167 */         this._uri.clear();
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isRequest()
/*     */     {
/* 173 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getMethod()
/*     */     {
/* 181 */       return this._method;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setMethod(String method)
/*     */     {
/* 189 */       this._method = method;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public HttpURI getURI()
/*     */     {
/* 197 */       return this._uri;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getURIString()
/*     */     {
/* 205 */       return this._uri == null ? null : this._uri.toString();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setURI(HttpURI uri)
/*     */     {
/* 213 */       this._uri = uri;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 219 */       HttpFields fields = getFields();
/* 220 */       return String.format("%s{u=%s,%s,h=%d}", new Object[] {
/* 221 */         getMethod(), getURI(), getVersion(), Integer.valueOf(fields == null ? -1 : fields.size()) });
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Response extends MetaData
/*     */   {
/*     */     private int _status;
/*     */     private String _reason;
/*     */     
/*     */     public Response()
/*     */     {
/* 232 */       this(null, 0, null);
/*     */     }
/*     */     
/*     */     public Response(HttpVersion version, int status, HttpFields fields)
/*     */     {
/* 237 */       this(version, status, fields, Long.MIN_VALUE);
/*     */     }
/*     */     
/*     */     public Response(HttpVersion version, int status, HttpFields fields, long contentLength)
/*     */     {
/* 242 */       super(fields, contentLength);
/* 243 */       this._status = status;
/*     */     }
/*     */     
/*     */     public Response(HttpVersion version, int status, String reason, HttpFields fields, long contentLength)
/*     */     {
/* 248 */       super(fields, contentLength);
/* 249 */       this._reason = reason;
/* 250 */       this._status = status;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isResponse()
/*     */     {
/* 256 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public int getStatus()
/*     */     {
/* 264 */       return this._status;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getReason()
/*     */     {
/* 272 */       return this._reason;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setStatus(int status)
/*     */     {
/* 280 */       this._status = status;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setReason(String reason)
/*     */     {
/* 288 */       this._reason = reason;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 294 */       HttpFields fields = getFields();
/* 295 */       return String.format("%s{s=%d,h=%d}", new Object[] { getVersion(), Integer.valueOf(getStatus()), Integer.valueOf(fields == null ? -1 : fields.size()) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\MetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */