/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class HttpCookie
/*     */ {
/*     */   private final String _name;
/*     */   private final String _value;
/*     */   private final String _comment;
/*     */   private final String _domain;
/*     */   private final long _maxAge;
/*     */   private final String _path;
/*     */   private final boolean _secure;
/*     */   private final int _version;
/*     */   private final boolean _httpOnly;
/*     */   private final long _expiration;
/*     */   
/*     */   public HttpCookie(String name, String value)
/*     */   {
/*  38 */     this(name, value, -1L);
/*     */   }
/*     */   
/*     */   public HttpCookie(String name, String value, String domain, String path)
/*     */   {
/*  43 */     this(name, value, domain, path, -1L, false, false);
/*     */   }
/*     */   
/*     */   public HttpCookie(String name, String value, long maxAge)
/*     */   {
/*  48 */     this(name, value, null, null, maxAge, false, false);
/*     */   }
/*     */   
/*     */   public HttpCookie(String name, String value, String domain, String path, long maxAge, boolean httpOnly, boolean secure)
/*     */   {
/*  53 */     this(name, value, domain, path, maxAge, httpOnly, secure, null, 0);
/*     */   }
/*     */   
/*     */   public HttpCookie(String name, String value, String domain, String path, long maxAge, boolean httpOnly, boolean secure, String comment, int version)
/*     */   {
/*  58 */     this._name = name;
/*  59 */     this._value = value;
/*  60 */     this._domain = domain;
/*  61 */     this._path = path;
/*  62 */     this._maxAge = maxAge;
/*  63 */     this._httpOnly = httpOnly;
/*  64 */     this._secure = secure;
/*  65 */     this._comment = comment;
/*  66 */     this._version = version;
/*  67 */     this._expiration = (maxAge < 0L ? -1L : System.nanoTime() + TimeUnit.SECONDS.toNanos(maxAge));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  75 */     return this._name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  83 */     return this._value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getComment()
/*     */   {
/*  91 */     return this._comment;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDomain()
/*     */   {
/*  99 */     return this._domain;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getMaxAge()
/*     */   {
/* 107 */     return this._maxAge;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 115 */     return this._path;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSecure()
/*     */   {
/* 123 */     return this._secure;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getVersion()
/*     */   {
/* 131 */     return this._version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isHttpOnly()
/*     */   {
/* 139 */     return this._httpOnly;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isExpired(long timeNanos)
/*     */   {
/* 148 */     return (this._expiration >= 0L) && (timeNanos >= this._expiration);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String asString()
/*     */   {
/* 156 */     StringBuilder builder = new StringBuilder();
/* 157 */     builder.append(getName()).append("=").append(getValue());
/* 158 */     if (getDomain() != null)
/* 159 */       builder.append(";$Domain=").append(getDomain());
/* 160 */     if (getPath() != null)
/* 161 */       builder.append(";$Path=").append(getPath());
/* 162 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpCookie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */