/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
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
/*     */ public class HostPortHttpField
/*     */   extends HttpField
/*     */ {
/*     */   private final String _host;
/*     */   private final int _port;
/*     */   
/*     */   public HostPortHttpField(String authority)
/*     */   {
/*  36 */     this(HttpHeader.HOST, HttpHeader.HOST.asString(), authority);
/*     */   }
/*     */   
/*     */   public HostPortHttpField(HttpHeader header, String name, String authority)
/*     */   {
/*  41 */     super(header, name, authority);
/*  42 */     if ((authority == null) || (authority.length() == 0)) {
/*  43 */       throw new IllegalArgumentException("No Authority");
/*     */     }
/*     */     try {
/*  46 */       if (authority.charAt(0) == '[')
/*     */       {
/*     */ 
/*  49 */         int close = authority.lastIndexOf(']');
/*  50 */         if (close < 0)
/*  51 */           throw new BadMessageException(400, "Bad ipv6");
/*  52 */         this._host = authority.substring(0, close + 1);
/*     */         
/*  54 */         if (authority.length() > close + 1)
/*     */         {
/*  56 */           if (authority.charAt(close + 1) != ':')
/*  57 */             throw new BadMessageException(400, "Bad ipv6 port");
/*  58 */           this._port = StringUtil.toInt(authority, close + 2);
/*     */         }
/*     */         else {
/*  61 */           this._port = 0;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  66 */         int c = authority.lastIndexOf(':');
/*  67 */         if (c >= 0)
/*     */         {
/*  69 */           this._host = authority.substring(0, c);
/*  70 */           this._port = StringUtil.toInt(authority, c + 1);
/*     */         }
/*     */         else
/*     */         {
/*  74 */           this._host = authority;
/*  75 */           this._port = 0;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (BadMessageException bm)
/*     */     {
/*  81 */       throw bm;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  85 */       throw new BadMessageException(400, "Bad HostPort", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHost()
/*     */   {
/*  95 */     return this._host;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 104 */     return this._port;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HostPortHttpField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */