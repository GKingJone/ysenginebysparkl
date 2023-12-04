/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.BadMessageException;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HostPortHttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpScheme;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
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
/*     */ public class MetaDataBuilder
/*     */ {
/*     */   private final int _maxSize;
/*     */   private int _size;
/*     */   private int _status;
/*     */   private String _method;
/*     */   private HttpScheme _scheme;
/*     */   private HostPortHttpField _authority;
/*     */   private String _path;
/*  41 */   private long _contentLength = Long.MIN_VALUE;
/*     */   
/*  43 */   private HttpFields _fields = new HttpFields(10);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MetaDataBuilder(int maxHeadersSize)
/*     */   {
/*  51 */     this._maxSize = maxHeadersSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxSize()
/*     */   {
/*  59 */     return this._maxSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getSize()
/*     */   {
/*  67 */     return this._size;
/*     */   }
/*     */   
/*     */   public void emit(HttpField field)
/*     */   {
/*  72 */     int field_size = field.getName().length() + field.getValue().length();
/*  73 */     this._size += field_size;
/*  74 */     if (this._size > this._maxSize) {
/*  75 */       throw new BadMessageException(413, "Header size " + this._size + ">" + this._maxSize);
/*     */     }
/*  77 */     if ((field instanceof StaticTableHttpField))
/*     */     {
/*  79 */       StaticTableHttpField value = (StaticTableHttpField)field;
/*  80 */       switch (field.getHeader())
/*     */       {
/*     */       case C_STATUS: 
/*  83 */         this._status = ((Integer)value.getStaticValue()).intValue();
/*  84 */         break;
/*     */       
/*     */       case C_METHOD: 
/*  87 */         this._method = field.getValue();
/*  88 */         break;
/*     */       
/*     */       case C_SCHEME: 
/*  91 */         this._scheme = ((HttpScheme)value.getStaticValue());
/*  92 */         break;
/*     */       
/*     */       default: 
/*  95 */         throw new IllegalArgumentException(field.getName());
/*     */       }
/*     */     }
/*  98 */     else if (field.getHeader() != null)
/*     */     {
/* 100 */       switch (field.getHeader())
/*     */       {
/*     */       case C_STATUS: 
/* 103 */         this._status = field.getIntValue();
/* 104 */         break;
/*     */       
/*     */       case C_METHOD: 
/* 107 */         this._method = field.getValue();
/* 108 */         break;
/*     */       
/*     */       case C_SCHEME: 
/* 111 */         this._scheme = ((HttpScheme)HttpScheme.CACHE.get(field.getValue()));
/* 112 */         break;
/*     */       
/*     */       case C_AUTHORITY: 
/* 115 */         this._authority = ((field instanceof HostPortHttpField) ? (HostPortHttpField)field : new AuthorityHttpField(field.getValue()));
/* 116 */         break;
/*     */       
/*     */ 
/*     */       case HOST: 
/* 120 */         if (this._authority == null)
/* 121 */           this._authority = ((field instanceof HostPortHttpField) ? (HostPortHttpField)field : new AuthorityHttpField(field.getValue()));
/* 122 */         this._fields.add(field);
/* 123 */         break;
/*     */       
/*     */       case C_PATH: 
/* 126 */         this._path = field.getValue();
/* 127 */         break;
/*     */       
/*     */       case CONTENT_LENGTH: 
/* 130 */         this._contentLength = field.getLongValue();
/* 131 */         this._fields.add(field);
/* 132 */         break;
/*     */       
/*     */       default: 
/* 135 */         if (field.getName().charAt(0) == ':') break;
/* 136 */         this._fields.add(field);break;
/*     */       
/*     */       }
/*     */       
/*     */     }
/* 141 */     else if (field.getName().charAt(0) != ':') {
/* 142 */       this._fields.add(field);
/*     */     }
/*     */   }
/*     */   
/*     */   public MetaData build()
/*     */   {
/*     */     try
/*     */     {
/* 150 */       HttpFields fields = this._fields;
/* 151 */       this._fields = new HttpFields(Math.max(10, fields.size() + 5));
/*     */       Object localObject1;
/* 153 */       if (this._method != null)
/* 154 */         return new Request(this._method, this._scheme, this._authority, this._path, HttpVersion.HTTP_2, fields, this._contentLength);
/* 155 */       if (this._status != 0)
/* 156 */         return new Response(HttpVersion.HTTP_2, this._status, fields, this._contentLength);
/* 157 */       return new MetaData(HttpVersion.HTTP_2, fields, this._contentLength);
/*     */     }
/*     */     finally
/*     */     {
/* 161 */       this._status = 0;
/* 162 */       this._method = null;
/* 163 */       this._scheme = null;
/* 164 */       this._authority = null;
/* 165 */       this._path = null;
/* 166 */       this._size = 0;
/* 167 */       this._contentLength = Long.MIN_VALUE;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkSize(int length, boolean huffman)
/*     */   {
/* 179 */     if (huffman)
/* 180 */       length = length * 4 / 3;
/* 181 */     if (this._size + length > this._maxSize) {
/* 182 */       throw new BadMessageException(413, "Header size " + (this._size + length) + ">" + this._maxSize);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\MetaDataBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */