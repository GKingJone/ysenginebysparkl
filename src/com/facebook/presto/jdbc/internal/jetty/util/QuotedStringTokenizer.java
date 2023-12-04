/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QuotedStringTokenizer
/*     */   extends StringTokenizer
/*     */ {
/*     */   private static final String __delim = "\t\n\r";
/*     */   private String _string;
/*  43 */   private String _delim = "\t\n\r";
/*  44 */   private boolean _returnQuotes = false;
/*  45 */   private boolean _returnDelimiters = false;
/*     */   private StringBuffer _token;
/*  47 */   private boolean _hasToken = false;
/*  48 */   private int _i = 0;
/*  49 */   private int _lastStart = 0;
/*  50 */   private boolean _double = true;
/*  51 */   private boolean _single = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public QuotedStringTokenizer(String str, String delim, boolean returnDelimiters, boolean returnQuotes)
/*     */   {
/*  59 */     super("");
/*  60 */     this._string = str;
/*  61 */     if (delim != null)
/*  62 */       this._delim = delim;
/*  63 */     this._returnDelimiters = returnDelimiters;
/*  64 */     this._returnQuotes = returnQuotes;
/*     */     
/*  66 */     if ((this._delim.indexOf('\'') >= 0) || 
/*  67 */       (this._delim.indexOf('"') >= 0)) {
/*  68 */       throw new Error("Can't use quotes as delimiters: " + this._delim);
/*     */     }
/*  70 */     this._token = new StringBuffer(this._string.length() > 1024 ? 512 : this._string.length() / 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public QuotedStringTokenizer(String str, String delim, boolean returnDelimiters)
/*     */   {
/*  78 */     this(str, delim, returnDelimiters, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public QuotedStringTokenizer(String str, String delim)
/*     */   {
/*  85 */     this(str, delim, false, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public QuotedStringTokenizer(String str)
/*     */   {
/*  91 */     this(str, null, false, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasMoreTokens()
/*     */   {
/*  99 */     if (this._hasToken) {
/* 100 */       return true;
/*     */     }
/* 102 */     this._lastStart = this._i;
/*     */     
/* 104 */     int state = 0;
/* 105 */     boolean escape = false;
/* 106 */     while (this._i < this._string.length())
/*     */     {
/* 108 */       char c = this._string.charAt(this._i++);
/*     */       
/* 110 */       switch (state)
/*     */       {
/*     */       case 0: 
/* 113 */         if (this._delim.indexOf(c) >= 0)
/*     */         {
/* 115 */           if (this._returnDelimiters)
/*     */           {
/* 117 */             this._token.append(c);
/* 118 */             return this._hasToken = 1;
/*     */           }
/*     */         }
/* 121 */         else if ((c == '\'') && (this._single))
/*     */         {
/* 123 */           if (this._returnQuotes)
/* 124 */             this._token.append(c);
/* 125 */           state = 2;
/*     */         }
/* 127 */         else if ((c == '"') && (this._double))
/*     */         {
/* 129 */           if (this._returnQuotes)
/* 130 */             this._token.append(c);
/* 131 */           state = 3;
/*     */         }
/*     */         else
/*     */         {
/* 135 */           this._token.append(c);
/* 136 */           this._hasToken = true;
/* 137 */           state = 1;
/*     */         }
/* 139 */         break;
/*     */       
/*     */       case 1: 
/* 142 */         this._hasToken = true;
/* 143 */         if (this._delim.indexOf(c) >= 0)
/*     */         {
/* 145 */           if (this._returnDelimiters)
/* 146 */             this._i -= 1;
/* 147 */           return this._hasToken;
/*     */         }
/* 149 */         if ((c == '\'') && (this._single))
/*     */         {
/* 151 */           if (this._returnQuotes)
/* 152 */             this._token.append(c);
/* 153 */           state = 2;
/*     */         }
/* 155 */         else if ((c == '"') && (this._double))
/*     */         {
/* 157 */           if (this._returnQuotes)
/* 158 */             this._token.append(c);
/* 159 */           state = 3;
/*     */         }
/*     */         else
/*     */         {
/* 163 */           this._token.append(c);
/*     */         }
/* 165 */         break;
/*     */       
/*     */       case 2: 
/* 168 */         this._hasToken = true;
/* 169 */         if (escape)
/*     */         {
/* 171 */           escape = false;
/* 172 */           this._token.append(c);
/*     */         }
/* 174 */         else if (c == '\'')
/*     */         {
/* 176 */           if (this._returnQuotes)
/* 177 */             this._token.append(c);
/* 178 */           state = 1;
/*     */         }
/* 180 */         else if (c == '\\')
/*     */         {
/* 182 */           if (this._returnQuotes)
/* 183 */             this._token.append(c);
/* 184 */           escape = true;
/*     */         }
/*     */         else
/*     */         {
/* 188 */           this._token.append(c);
/*     */         }
/* 190 */         break;
/*     */       
/*     */       case 3: 
/* 193 */         this._hasToken = true;
/* 194 */         if (escape)
/*     */         {
/* 196 */           escape = false;
/* 197 */           this._token.append(c);
/*     */         }
/* 199 */         else if (c == '"')
/*     */         {
/* 201 */           if (this._returnQuotes)
/* 202 */             this._token.append(c);
/* 203 */           state = 1;
/*     */         }
/* 205 */         else if (c == '\\')
/*     */         {
/* 207 */           if (this._returnQuotes)
/* 208 */             this._token.append(c);
/* 209 */           escape = true;
/*     */         }
/*     */         else
/*     */         {
/* 213 */           this._token.append(c);
/*     */         }
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/* 219 */     return this._hasToken;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String nextToken()
/*     */     throws NoSuchElementException
/*     */   {
/* 227 */     if ((!hasMoreTokens()) || (this._token == null))
/* 228 */       throw new NoSuchElementException();
/* 229 */     String t = this._token.toString();
/* 230 */     this._token.setLength(0);
/* 231 */     this._hasToken = false;
/* 232 */     return t;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String nextToken(String delim)
/*     */     throws NoSuchElementException
/*     */   {
/* 240 */     this._delim = delim;
/* 241 */     this._i = this._lastStart;
/* 242 */     this._token.setLength(0);
/* 243 */     this._hasToken = false;
/* 244 */     return nextToken();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasMoreElements()
/*     */   {
/* 251 */     return hasMoreTokens();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object nextElement()
/*     */     throws NoSuchElementException
/*     */   {
/* 259 */     return nextToken();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int countTokens()
/*     */   {
/* 268 */     return -1;
/*     */   }
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
/*     */   public static String quoteIfNeeded(String s, String delim)
/*     */   {
/* 283 */     if (s == null)
/* 284 */       return null;
/* 285 */     if (s.length() == 0) {
/* 286 */       return "\"\"";
/*     */     }
/*     */     
/* 289 */     for (int i = 0; i < s.length(); i++)
/*     */     {
/* 291 */       char c = s.charAt(i);
/* 292 */       if ((c == '\\') || (c == '"') || (c == '\'') || (Character.isWhitespace(c)) || (delim.indexOf(c) >= 0))
/*     */       {
/* 294 */         StringBuffer b = new StringBuffer(s.length() + 8);
/* 295 */         quote(b, s);
/* 296 */         return b.toString();
/*     */       }
/*     */     }
/*     */     
/* 300 */     return s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String quote(String s)
/*     */   {
/* 313 */     if (s == null)
/* 314 */       return null;
/* 315 */     if (s.length() == 0) {
/* 316 */       return "\"\"";
/*     */     }
/* 318 */     StringBuffer b = new StringBuffer(s.length() + 8);
/* 319 */     quote(b, s);
/* 320 */     return b.toString();
/*     */   }
/*     */   
/*     */ 
/* 324 */   private static final char[] escapes = new char[32];
/*     */   
/*     */   static {
/* 327 */     Arrays.fill(escapes, 65535);
/* 328 */     escapes[8] = 'b';
/* 329 */     escapes[9] = 't';
/* 330 */     escapes[10] = 'n';
/* 331 */     escapes[12] = 'f';
/* 332 */     escapes[13] = 'r';
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void quoteOnly(Appendable buffer, String input)
/*     */   {
/* 343 */     if (input == null) {
/* 344 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 348 */       buffer.append('"');
/* 349 */       for (int i = 0; i < input.length(); i++)
/*     */       {
/* 351 */         char c = input.charAt(i);
/* 352 */         if ((c == '"') || (c == '\\'))
/* 353 */           buffer.append('\\');
/* 354 */         buffer.append(c);
/*     */       }
/* 356 */       buffer.append('"');
/*     */     }
/*     */     catch (IOException x)
/*     */     {
/* 360 */       throw new RuntimeException(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void quote(Appendable buffer, String input)
/*     */   {
/* 372 */     if (input == null) {
/* 373 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 377 */       buffer.append('"');
/* 378 */       for (int i = 0; i < input.length(); i++)
/*     */       {
/* 380 */         char c = input.charAt(i);
/* 381 */         if (c >= ' ')
/*     */         {
/* 383 */           if ((c == '"') || (c == '\\'))
/* 384 */             buffer.append('\\');
/* 385 */           buffer.append(c);
/*     */         }
/*     */         else
/*     */         {
/* 389 */           char escape = escapes[c];
/* 390 */           if (escape == 65535)
/*     */           {
/*     */ 
/* 393 */             buffer.append('\\').append('u').append('0').append('0');
/* 394 */             if (c < '\020')
/* 395 */               buffer.append('0');
/* 396 */             buffer.append(Integer.toString(c, 16));
/*     */           }
/*     */           else
/*     */           {
/* 400 */             buffer.append('\\').append(escape);
/*     */           }
/*     */         }
/*     */       }
/* 404 */       buffer.append('"');
/*     */     }
/*     */     catch (IOException x)
/*     */     {
/* 408 */       throw new RuntimeException(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String unquoteOnly(String s)
/*     */   {
/* 416 */     return unquoteOnly(s, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String unquoteOnly(String s, boolean lenient)
/*     */   {
/* 428 */     if (s == null)
/* 429 */       return null;
/* 430 */     if (s.length() < 2) {
/* 431 */       return s;
/*     */     }
/* 433 */     char first = s.charAt(0);
/* 434 */     char last = s.charAt(s.length() - 1);
/* 435 */     if ((first != last) || ((first != '"') && (first != '\''))) {
/* 436 */       return s;
/*     */     }
/* 438 */     StringBuilder b = new StringBuilder(s.length() - 2);
/* 439 */     boolean escape = false;
/* 440 */     for (int i = 1; i < s.length() - 1; i++)
/*     */     {
/* 442 */       char c = s.charAt(i);
/*     */       
/* 444 */       if (escape)
/*     */       {
/* 446 */         escape = false;
/* 447 */         if ((lenient) && (!isValidEscaping(c)))
/*     */         {
/* 449 */           b.append('\\');
/*     */         }
/* 451 */         b.append(c);
/*     */       }
/* 453 */       else if (c == '\\')
/*     */       {
/* 455 */         escape = true;
/*     */       }
/*     */       else
/*     */       {
/* 459 */         b.append(c);
/*     */       }
/*     */     }
/*     */     
/* 463 */     return b.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public static String unquote(String s)
/*     */   {
/* 469 */     return unquote(s, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String unquote(String s, boolean lenient)
/*     */   {
/* 480 */     if (s == null)
/* 481 */       return null;
/* 482 */     if (s.length() < 2) {
/* 483 */       return s;
/*     */     }
/* 485 */     char first = s.charAt(0);
/* 486 */     char last = s.charAt(s.length() - 1);
/* 487 */     if ((first != last) || ((first != '"') && (first != '\''))) {
/* 488 */       return s;
/*     */     }
/* 490 */     StringBuilder b = new StringBuilder(s.length() - 2);
/* 491 */     boolean escape = false;
/* 492 */     for (int i = 1; i < s.length() - 1; i++)
/*     */     {
/* 494 */       char c = s.charAt(i);
/*     */       
/* 496 */       if (escape)
/*     */       {
/* 498 */         escape = false;
/* 499 */         switch (c)
/*     */         {
/*     */         case 'n': 
/* 502 */           b.append('\n');
/* 503 */           break;
/*     */         case 'r': 
/* 505 */           b.append('\r');
/* 506 */           break;
/*     */         case 't': 
/* 508 */           b.append('\t');
/* 509 */           break;
/*     */         case 'f': 
/* 511 */           b.append('\f');
/* 512 */           break;
/*     */         case 'b': 
/* 514 */           b.append('\b');
/* 515 */           break;
/*     */         case '\\': 
/* 517 */           b.append('\\');
/* 518 */           break;
/*     */         case '/': 
/* 520 */           b.append('/');
/* 521 */           break;
/*     */         case '"': 
/* 523 */           b.append('"');
/* 524 */           break;
/*     */         case 'u': 
/* 526 */           b.append(
/*     */           
/*     */ 
/*     */ 
/* 530 */             (char)((TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 24) + (TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 16) + (TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 8) + TypeUtil.convertHexDigit((byte)s.charAt(i++))));
/*     */           
/*     */ 
/* 533 */           break;
/*     */         default: 
/* 535 */           if ((lenient) && (!isValidEscaping(c)))
/*     */           {
/* 537 */             b.append('\\');
/*     */           }
/* 539 */           b.append(c);break;
/*     */         }
/*     */       }
/* 542 */       else if (c == '\\')
/*     */       {
/* 544 */         escape = true;
/*     */       }
/*     */       else
/*     */       {
/* 548 */         b.append(c);
/*     */       }
/*     */     }
/*     */     
/* 552 */     return b.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidEscaping(char c)
/*     */   {
/* 564 */     return (c == 'n') || (c == 'r') || (c == 't') || (c == 'f') || (c == 'b') || (c == '\\') || (c == '/') || (c == '"') || (c == 'u');
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isQuoted(String s)
/*     */   {
/* 572 */     return (s != null) && (s.length() > 0) && (s.charAt(0) == '"') && (s.charAt(s.length() - 1) == '"');
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getDouble()
/*     */   {
/* 581 */     return this._double;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDouble(boolean d)
/*     */   {
/* 590 */     this._double = d;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getSingle()
/*     */   {
/* 599 */     return this._single;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSingle(boolean single)
/*     */   {
/* 608 */     this._single = single;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\QuotedStringTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */