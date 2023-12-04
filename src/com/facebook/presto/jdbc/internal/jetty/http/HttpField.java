/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Objects;
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
/*     */ public class HttpField
/*     */ {
/*     */   private static final String __zeroquality = "q=0";
/*     */   private final HttpHeader _header;
/*     */   private final String _name;
/*     */   private final String _value;
/*  35 */   private int hash = 0;
/*     */   
/*     */   public HttpField(HttpHeader header, String name, String value)
/*     */   {
/*  39 */     this._header = header;
/*  40 */     this._name = name;
/*  41 */     this._value = value;
/*     */   }
/*     */   
/*     */   public HttpField(HttpHeader header, String value)
/*     */   {
/*  46 */     this(header, header.asString(), value);
/*     */   }
/*     */   
/*     */   public HttpField(HttpHeader header, HttpHeaderValue value)
/*     */   {
/*  51 */     this(header, header.asString(), value.asString());
/*     */   }
/*     */   
/*     */   public HttpField(String name, String value)
/*     */   {
/*  56 */     this((HttpHeader)HttpHeader.CACHE.get(name), name, value);
/*     */   }
/*     */   
/*     */   public HttpHeader getHeader()
/*     */   {
/*  61 */     return this._header;
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/*  66 */     return this._name;
/*     */   }
/*     */   
/*     */   public String getValue()
/*     */   {
/*  71 */     return this._value;
/*     */   }
/*     */   
/*     */   public int getIntValue()
/*     */   {
/*  76 */     return Integer.valueOf(this._value).intValue();
/*     */   }
/*     */   
/*     */   public long getLongValue()
/*     */   {
/*  81 */     return Long.valueOf(this._value).longValue();
/*     */   }
/*     */   
/*     */   public String[] getValues()
/*     */   {
/*  86 */     ArrayList<String> list = new ArrayList();
/*  87 */     int state = 0;
/*  88 */     int start = 0;
/*  89 */     int end = 0;
/*  90 */     StringBuilder builder = new StringBuilder();
/*     */     
/*  92 */     for (int i = 0; i < this._value.length(); i++)
/*     */     {
/*  94 */       char c = this._value.charAt(i);
/*  95 */       switch (state)
/*     */       {
/*     */       case 0: 
/*  98 */         switch (c)
/*     */         {
/*     */         case '"': 
/* 101 */           state = 2;
/* 102 */           break;
/*     */         
/*     */         case ',': 
/*     */           break;
/*     */         
/*     */         case '\t': 
/*     */         case ' ': 
/*     */           break;
/*     */         
/*     */         default: 
/* 112 */           start = i;
/* 113 */           end = i;
/* 114 */           state = 1;
/*     */         }
/* 116 */         break;
/*     */       
/*     */       case 1: 
/* 119 */         switch (c)
/*     */         {
/*     */         case ',': 
/* 122 */           list.add(this._value.substring(start, end + 1));
/* 123 */           state = 0;
/* 124 */           break;
/*     */         
/*     */         case '\t': 
/*     */         case ' ': 
/*     */           break;
/*     */         
/*     */         default: 
/* 131 */           end = i;
/*     */         }
/* 133 */         break;
/*     */       
/*     */       case 2: 
/* 136 */         switch (c)
/*     */         {
/*     */         case '\\': 
/* 139 */           state = 3;
/* 140 */           break;
/*     */         
/*     */         case '"': 
/* 143 */           list.add(builder.toString());
/* 144 */           builder.setLength(0);
/* 145 */           state = 4;
/* 146 */           break;
/*     */         
/*     */         default: 
/* 149 */           builder.append(c);
/*     */         }
/* 151 */         break;
/*     */       
/*     */       case 3: 
/* 154 */         builder.append(c);
/* 155 */         state = 2;
/* 156 */         break;
/*     */       
/*     */       case 4: 
/* 159 */         switch (c)
/*     */         {
/*     */         case '\t': 
/*     */         case ' ': 
/*     */           break;
/*     */         
/*     */         case ',': 
/* 166 */           state = 0;
/* 167 */           break;
/*     */         
/*     */         default: 
/* 170 */           throw new IllegalArgumentException("c=" + c);
/*     */         }
/*     */         
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/* 177 */     switch (state)
/*     */     {
/*     */     case 0: 
/*     */       break;
/*     */     case 1: 
/* 182 */       list.add(this._value.substring(start, end + 1));
/* 183 */       break;
/*     */     case 4: 
/*     */       break;
/*     */     case 2: case 3: 
/*     */     default: 
/* 188 */       throw new IllegalArgumentException("state=" + state);
/*     */     }
/*     */     
/* 191 */     return (String[])list.toArray(new String[list.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean contains(String search)
/*     */   {
/* 203 */     if (search == null)
/* 204 */       return this._value == null;
/* 205 */     if (search.length() == 0)
/* 206 */       return false;
/* 207 */     if (this._value == null) {
/* 208 */       return false;
/*     */     }
/* 210 */     search = StringUtil.asciiToLowerCase(search);
/*     */     
/* 212 */     int state = 0;
/* 213 */     int match = 0;
/* 214 */     int param = 0;
/*     */     
/* 216 */     for (int i = 0; i < this._value.length(); i++)
/*     */     {
/* 218 */       char c = this._value.charAt(i);
/* 219 */       switch (state)
/*     */       {
/*     */       case 0: 
/* 222 */         switch (c)
/*     */         {
/*     */         case '"': 
/* 225 */           match = 0;
/* 226 */           state = 2;
/* 227 */           break;
/*     */         
/*     */         case ',': 
/*     */           break;
/*     */         
/*     */         case ';': 
/* 233 */           param = -1;
/* 234 */           match = -1;
/* 235 */           state = 5;
/* 236 */           break;
/*     */         
/*     */         case '\t': 
/*     */         case ' ': 
/*     */           break;
/*     */         
/*     */         default: 
/* 243 */           match = Character.toLowerCase(c) == search.charAt(0) ? 1 : -1;
/* 244 */           state = 1; }
/* 245 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case 1: 
/* 250 */         switch (c)
/*     */         {
/*     */ 
/*     */         case ',': 
/* 254 */           if (match == search.length())
/* 255 */             return true;
/* 256 */           state = 0;
/* 257 */           break;
/*     */         
/*     */         case ';': 
/* 260 */           param = match >= 0 ? 0 : -1;
/* 261 */           state = 5;
/* 262 */           break;
/*     */         
/*     */         default: 
/* 265 */           if (match > 0)
/*     */           {
/* 267 */             if (match < search.length()) {
/* 268 */               match = Character.toLowerCase(c) == search.charAt(match) ? match + 1 : -1;
/* 269 */             } else if ((c != ' ') && (c != '\t')) {
/* 270 */               match = -1;
/*     */             }
/*     */           }
/*     */           break;
/*     */         }
/*     */         
/*     */         break;
/*     */       case 2: 
/* 278 */         switch (c)
/*     */         {
/*     */         case '\\': 
/* 281 */           state = 3;
/* 282 */           break;
/*     */         
/*     */         case '"': 
/* 285 */           state = 4;
/* 286 */           break;
/*     */         
/*     */         default: 
/* 289 */           if (match >= 0)
/*     */           {
/* 291 */             if (match < search.length()) {
/* 292 */               match = Character.toLowerCase(c) == search.charAt(match) ? match + 1 : -1;
/*     */             } else
/* 294 */               match = -1;
/*     */           }
/*     */           break;
/*     */         }
/*     */         break;
/*     */       case 3: 
/* 300 */         if (match >= 0)
/*     */         {
/* 302 */           if (match < search.length()) {
/* 303 */             match = Character.toLowerCase(c) == search.charAt(match) ? match + 1 : -1;
/*     */           } else
/* 305 */             match = -1;
/*     */         }
/* 307 */         state = 2;
/* 308 */         break;
/*     */       
/*     */       case 4: 
/* 311 */         switch (c)
/*     */         {
/*     */         case '\t': 
/*     */         case ' ': 
/*     */           break;
/*     */         
/*     */         case ';': 
/* 318 */           state = 5;
/* 319 */           break;
/*     */         
/*     */ 
/*     */         case ',': 
/* 323 */           if (match == search.length())
/* 324 */             return true;
/* 325 */           state = 0;
/* 326 */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 330 */           match = -1;
/*     */         }
/* 332 */         break;
/*     */       
/*     */       case 5: 
/* 335 */         switch (c)
/*     */         {
/*     */ 
/*     */         case ',': 
/* 339 */           if ((param != "q=0".length()) && (match == search.length()))
/* 340 */             return true;
/* 341 */           param = 0;
/* 342 */           state = 0;
/* 343 */           break;
/*     */         
/*     */         case '\t': 
/*     */         case ' ': 
/*     */           break;
/*     */         
/*     */         default: 
/* 350 */           if (param >= 0)
/*     */           {
/* 352 */             if (param < "q=0".length()) {
/* 353 */               param = Character.toLowerCase(c) == "q=0".charAt(param) ? param + 1 : -1;
/* 354 */             } else if ((c != '0') && (c != '.'))
/* 355 */               param = -1;
/*     */           }
/*     */           break;
/*     */         }
/*     */         
/*     */         break;
/*     */       default: 
/* 362 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 366 */     return (param != "q=0".length()) && (match == search.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 373 */     String v = getValue();
/* 374 */     return getName() + ": " + (v == null ? "" : v);
/*     */   }
/*     */   
/*     */   public boolean isSameName(HttpField field)
/*     */   {
/* 379 */     if (field == null)
/* 380 */       return false;
/* 381 */     if (field == this)
/* 382 */       return true;
/* 383 */     if ((this._header != null) && (this._header == field.getHeader()))
/* 384 */       return true;
/* 385 */     if (this._name.equalsIgnoreCase(field.getName()))
/* 386 */       return true;
/* 387 */     return false;
/*     */   }
/*     */   
/*     */   private int nameHashCode()
/*     */   {
/* 392 */     int h = this.hash;
/* 393 */     int len = this._name.length();
/* 394 */     if ((h == 0) && (len > 0))
/*     */     {
/* 396 */       for (int i = 0; i < len; i++)
/*     */       {
/*     */ 
/* 399 */         char c = this._name.charAt(i);
/*     */         
/* 401 */         if ((c >= 'a') && (c <= 'z'))
/* 402 */           c = (char)(c - ' ');
/* 403 */         h = 31 * h + c;
/*     */       }
/* 405 */       this.hash = h;
/*     */     }
/* 407 */     return h;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 413 */     if (this._header == null)
/* 414 */       return this._value.hashCode() ^ nameHashCode();
/* 415 */     return this._value.hashCode() ^ this._header.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 421 */     if (o == this)
/* 422 */       return true;
/* 423 */     if (!(o instanceof HttpField))
/* 424 */       return false;
/* 425 */     HttpField field = (HttpField)o;
/* 426 */     if (this._header != field.getHeader())
/* 427 */       return false;
/* 428 */     if (!this._name.equalsIgnoreCase(field.getName()))
/* 429 */       return false;
/* 430 */     if ((this._value == null) && (field.getValue() != null))
/* 431 */       return false;
/* 432 */     return Objects.equals(this._value, field.getValue());
/*     */   }
/*     */   
/*     */   public static class IntValueHttpField extends HttpField
/*     */   {
/*     */     private final int _int;
/*     */     
/*     */     public IntValueHttpField(HttpHeader header, String name, String value, int intValue)
/*     */     {
/* 441 */       super(name, value);
/* 442 */       this._int = intValue;
/*     */     }
/*     */     
/*     */     public IntValueHttpField(HttpHeader header, String name, String value)
/*     */     {
/* 447 */       this(header, name, value, Integer.valueOf(value).intValue());
/*     */     }
/*     */     
/*     */     public IntValueHttpField(HttpHeader header, String name, int intValue)
/*     */     {
/* 452 */       this(header, name, Integer.toString(intValue), intValue);
/*     */     }
/*     */     
/*     */     public IntValueHttpField(HttpHeader header, int value)
/*     */     {
/* 457 */       this(header, header.asString(), value);
/*     */     }
/*     */     
/*     */ 
/*     */     public int getIntValue()
/*     */     {
/* 463 */       return this._int;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getLongValue()
/*     */     {
/* 469 */       return this._int;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LongValueHttpField extends HttpField
/*     */   {
/*     */     private final long _long;
/*     */     
/*     */     public LongValueHttpField(HttpHeader header, String name, String value, long longValue)
/*     */     {
/* 479 */       super(name, value);
/* 480 */       this._long = longValue;
/*     */     }
/*     */     
/*     */     public LongValueHttpField(HttpHeader header, String name, String value)
/*     */     {
/* 485 */       this(header, name, value, Long.valueOf(value).longValue());
/*     */     }
/*     */     
/*     */     public LongValueHttpField(HttpHeader header, String name, long value)
/*     */     {
/* 490 */       this(header, name, Long.toString(value), value);
/*     */     }
/*     */     
/*     */     public LongValueHttpField(HttpHeader header, long value)
/*     */     {
/* 495 */       this(header, header.asString(), value);
/*     */     }
/*     */     
/*     */ 
/*     */     public int getIntValue()
/*     */     {
/* 501 */       return (int)this._long;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getLongValue()
/*     */     {
/* 507 */       return this._long;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */