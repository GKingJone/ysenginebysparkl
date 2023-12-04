/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class QuotedCSV
/*     */   implements Iterable<String>
/*     */ {
/*     */   private static enum State
/*     */   {
/*  35 */     VALUE,  PARAM_NAME,  PARAM_VALUE;
/*     */     private State() {} }
/*  37 */   private final List<String> _values = new ArrayList();
/*     */   
/*     */   private final boolean _keepQuotes;
/*     */   
/*     */   public QuotedCSV(String... values)
/*     */   {
/*  43 */     this(true, values);
/*     */   }
/*     */   
/*     */ 
/*     */   public QuotedCSV(boolean keepQuotes, String... values)
/*     */   {
/*  49 */     this._keepQuotes = keepQuotes;
/*  50 */     for (String v : values) {
/*  51 */       addValue(v);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addValue(String value)
/*     */   {
/*  57 */     StringBuffer buffer = new StringBuffer();
/*     */     
/*  59 */     int l = value.length();
/*  60 */     State state = State.VALUE;
/*  61 */     boolean quoted = false;
/*  62 */     boolean sloshed = false;
/*  63 */     int nws_length = 0;
/*  64 */     int last_length = 0;
/*  65 */     for (int i = 0; i <= l; i++)
/*     */     {
/*  67 */       char c = i == l ? '\000' : value.charAt(i);
/*     */       
/*     */ 
/*  70 */       if ((quoted) && (c != 0))
/*     */       {
/*  72 */         if (sloshed) {
/*  73 */           sloshed = false;
/*     */         }
/*     */         else {
/*  76 */           switch (c)
/*     */           {
/*     */           case '\\': 
/*  79 */             sloshed = true;
/*  80 */             if (this._keepQuotes) break;
/*  81 */             break;
/*     */           
/*     */           case '"': 
/*  84 */             quoted = false;
/*  85 */             if (!this._keepQuotes) {
/*     */               continue;
/*     */             }
/*     */           }
/*     */           
/*     */         }
/*  91 */         buffer.append(c);
/*  92 */         nws_length = buffer.length();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  97 */         switch (c)
/*     */         {
/*     */         case '\t': 
/*     */         case ' ': 
/* 101 */           if (buffer.length() > last_length) {
/* 102 */             buffer.append(c);
/*     */           }
/*     */           break;
/*     */         case '"': 
/* 106 */           quoted = true;
/* 107 */           if (this._keepQuotes)
/* 108 */             buffer.append(c);
/* 109 */           nws_length = buffer.length();
/* 110 */           break;
/*     */         
/*     */         case ';': 
/* 113 */           buffer.setLength(nws_length);
/* 114 */           buffer.append(c);
/* 115 */           nws_length++;last_length = nws_length;
/* 116 */           state = State.PARAM_NAME;
/* 117 */           break;
/*     */         
/*     */         case '\000': 
/*     */         case ',': 
/* 121 */           if (nws_length > 0)
/*     */           {
/* 123 */             buffer.setLength(nws_length);
/* 124 */             this._values.add(buffer.toString());
/*     */           }
/* 126 */           buffer.setLength(0);
/* 127 */           last_length = 0;
/* 128 */           nws_length = 0;
/* 129 */           state = State.VALUE;
/* 130 */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 134 */           switch (state)
/*     */           {
/*     */ 
/*     */           case VALUE: 
/* 138 */             buffer.append(c);
/* 139 */             nws_length = buffer.length();
/* 140 */             break;
/*     */           
/*     */ 
/*     */ 
/*     */           case PARAM_NAME: 
/* 145 */             if (c == '=')
/*     */             {
/* 147 */               buffer.setLength(nws_length);
/* 148 */               buffer.append(c);
/* 149 */               nws_length++;last_length = nws_length;
/* 150 */               state = State.PARAM_VALUE;
/*     */             }
/*     */             else {
/* 153 */               buffer.append(c);
/* 154 */               nws_length = buffer.length();
/*     */             }
/* 156 */             break;
/*     */           
/*     */ 
/*     */ 
/*     */           case PARAM_VALUE: 
/* 161 */             buffer.append(c);
/* 162 */             nws_length = buffer.length();
/*     */           }
/*     */           
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public List<String> getValues()
/*     */   {
/* 173 */     return this._values;
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<String> iterator()
/*     */   {
/* 179 */     return this._values.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public static String unquote(String s)
/*     */   {
/* 185 */     int l = s.length();
/* 186 */     if ((s == null) || (l == 0)) {
/* 187 */       return s;
/*     */     }
/*     */     
/* 190 */     for (int i = 0; 
/* 191 */         i < l; i++)
/*     */     {
/* 193 */       char c = s.charAt(i);
/* 194 */       if (c == '"')
/*     */         break;
/*     */     }
/* 197 */     if (i == l) {
/* 198 */       return s;
/*     */     }
/* 200 */     boolean quoted = true;
/* 201 */     boolean sloshed = false;
/* 202 */     StringBuffer buffer = new StringBuffer();
/* 203 */     buffer.append(s, 0, i);
/* 204 */     i++;
/* 205 */     for (; i < l; i++)
/*     */     {
/* 207 */       char c = s.charAt(i);
/* 208 */       if (quoted)
/*     */       {
/* 210 */         if (sloshed)
/*     */         {
/* 212 */           buffer.append(c);
/* 213 */           sloshed = false;
/*     */         }
/* 215 */         else if (c == '"') {
/* 216 */           quoted = false;
/* 217 */         } else if (c == '\\') {
/* 218 */           sloshed = true;
/*     */         } else {
/* 220 */           buffer.append(c);
/*     */         }
/* 222 */       } else if (c == '"') {
/* 223 */         quoted = true;
/*     */       } else
/* 225 */         buffer.append(c);
/*     */     }
/* 227 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\QuotedCSV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */