/*      */ package com.facebook.presto.jdbc.internal.jetty.util;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.StringWriter;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.List;
/*      */ import java.util.Map.Entry;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UrlEncoded
/*      */   extends MultiMap<String>
/*      */   implements Cloneable
/*      */ {
/*   62 */   static final Logger LOG = Log.getLogger(UrlEncoded.class);
/*      */   public static final Charset ENCODING;
/*      */   
/*      */   static
/*      */   {
/*      */     Charset encoding;
/*      */     try
/*      */     {
/*   70 */       String charset = System.getProperty("com.facebook.presto.jdbc.internal.jetty.util.UrlEncoding.charset");
/*   71 */       encoding = charset == null ? StandardCharsets.UTF_8 : Charset.forName(charset);
/*      */     }
/*      */     catch (Exception e) {
/*      */       Charset encoding;
/*   75 */       LOG.warn(e);
/*   76 */       encoding = StandardCharsets.UTF_8;
/*      */     }
/*   78 */     ENCODING = encoding;
/*      */   }
/*      */   
/*      */ 
/*      */   public UrlEncoded(UrlEncoded url)
/*      */   {
/*   84 */     super(url);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UrlEncoded(String query)
/*      */   {
/*   94 */     decodeTo(query, this, ENCODING);
/*      */   }
/*      */   
/*      */ 
/*      */   public void decode(String query)
/*      */   {
/*  100 */     decodeTo(query, this, ENCODING);
/*      */   }
/*      */   
/*      */ 
/*      */   public void decode(String query, Charset charset)
/*      */   {
/*  106 */     decodeTo(query, this, charset);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String encode()
/*      */   {
/*  115 */     return encode(ENCODING, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String encode(Charset charset)
/*      */   {
/*  125 */     return encode(charset, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized String encode(Charset charset, boolean equalsForNullValue)
/*      */   {
/*  138 */     return encode(this, charset, equalsForNullValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String encode(MultiMap<String> map, Charset charset, boolean equalsForNullValue)
/*      */   {
/*  151 */     if (charset == null) {
/*  152 */       charset = ENCODING;
/*      */     }
/*  154 */     StringBuilder result = new StringBuilder(128);
/*      */     
/*  156 */     boolean delim = false;
/*  157 */     for (Map.Entry<String, List<String>> entry : map.entrySet())
/*      */     {
/*  159 */       String key = ((String)entry.getKey()).toString();
/*  160 */       List<String> list = (List)entry.getValue();
/*  161 */       int s = list.size();
/*      */       
/*  163 */       if (delim)
/*      */       {
/*  165 */         result.append('&');
/*      */       }
/*      */       
/*  168 */       if (s == 0)
/*      */       {
/*  170 */         result.append(encodeString(key, charset));
/*  171 */         if (equalsForNullValue) {
/*  172 */           result.append('=');
/*      */         }
/*      */       }
/*      */       else {
/*  176 */         for (int i = 0; i < s; i++)
/*      */         {
/*  178 */           if (i > 0)
/*  179 */             result.append('&');
/*  180 */           String val = (String)list.get(i);
/*  181 */           result.append(encodeString(key, charset));
/*      */           
/*  183 */           if (val != null)
/*      */           {
/*  185 */             String str = val.toString();
/*  186 */             if (str.length() > 0)
/*      */             {
/*  188 */               result.append('=');
/*  189 */               result.append(encodeString(str, charset));
/*      */             }
/*  191 */             else if (equalsForNullValue) {
/*  192 */               result.append('=');
/*      */             }
/*  194 */           } else if (equalsForNullValue) {
/*  195 */             result.append('=');
/*      */           }
/*      */         } }
/*  198 */       delim = true;
/*      */     }
/*  200 */     return result.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void decodeTo(String content, MultiMap<String> map, String charset)
/*      */   {
/*  211 */     decodeTo(content, map, charset == null ? null : Charset.forName(charset));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void decodeTo(String content, MultiMap<String> map, Charset charset)
/*      */   {
/*  222 */     if (charset == null) {
/*  223 */       charset = ENCODING;
/*      */     }
/*  225 */     if (charset == StandardCharsets.UTF_8)
/*      */     {
/*  227 */       decodeUtf8To(content, 0, content.length(), map);
/*  228 */       return;
/*      */     }
/*      */     
/*  231 */     synchronized (map)
/*      */     {
/*  233 */       String key = null;
/*  234 */       String value = null;
/*  235 */       int mark = -1;
/*  236 */       boolean encoded = false;
/*  237 */       for (int i = 0; i < content.length(); i++)
/*      */       {
/*  239 */         char c = content.charAt(i);
/*  240 */         switch (c)
/*      */         {
/*      */         case '&': 
/*  243 */           int l = i - mark - 1;
/*      */           
/*  245 */           value = encoded ? decodeString(content, mark + 1, l, charset) : l == 0 ? "" : content.substring(mark + 1, i);
/*  246 */           mark = i;
/*  247 */           encoded = false;
/*  248 */           if (key != null)
/*      */           {
/*  250 */             map.add(key, value);
/*      */           }
/*  252 */           else if ((value != null) && (value.length() > 0))
/*      */           {
/*  254 */             map.add(value, "");
/*      */           }
/*  256 */           key = null;
/*  257 */           value = null;
/*  258 */           break;
/*      */         case '=': 
/*  260 */           if (key == null)
/*      */           {
/*  262 */             key = encoded ? decodeString(content, mark + 1, i - mark - 1, charset) : content.substring(mark + 1, i);
/*  263 */             mark = i;
/*  264 */             encoded = false; }
/*  265 */           break;
/*      */         case '+': 
/*  267 */           encoded = true;
/*  268 */           break;
/*      */         case '%': 
/*  270 */           encoded = true;
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  275 */       if (key != null)
/*      */       {
/*  277 */         int l = content.length() - mark - 1;
/*  278 */         value = encoded ? decodeString(content, mark + 1, l, charset) : l == 0 ? "" : content.substring(mark + 1);
/*  279 */         map.add(key, value);
/*      */       }
/*  281 */       else if (mark < content.length())
/*      */       {
/*      */ 
/*      */ 
/*  285 */         key = encoded ? decodeString(content, mark + 1, content.length() - mark - 1, charset) : content.substring(mark + 1);
/*  286 */         if ((key != null) && (key.length() > 0))
/*      */         {
/*  288 */           map.add(key, "");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void decodeUtf8To(String query, MultiMap<String> map)
/*      */   {
/*  297 */     decodeUtf8To(query, 0, query.length(), map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void decodeUtf8To(String query, int offset, int length, MultiMap<String> map)
/*      */   {
/*  309 */     Utf8StringBuilder buffer = new Utf8StringBuilder();
/*  310 */     synchronized (map)
/*      */     {
/*  312 */       String key = null;
/*  313 */       String value = null;
/*      */       
/*  315 */       int end = offset + length;
/*  316 */       for (int i = offset; i < end; i++)
/*      */       {
/*  318 */         char c = query.charAt(i);
/*      */         try
/*      */         {
/*  321 */           switch (c)
/*      */           {
/*      */           case '&': 
/*  324 */             value = buffer.toReplacedString();
/*  325 */             buffer.reset();
/*  326 */             if (key != null)
/*      */             {
/*  328 */               map.add(key, value);
/*      */             }
/*  330 */             else if ((value != null) && (value.length() > 0))
/*      */             {
/*  332 */               map.add(value, "");
/*      */             }
/*  334 */             key = null;
/*  335 */             value = null;
/*  336 */             break;
/*      */           
/*      */           case '=': 
/*  339 */             if (key != null)
/*      */             {
/*  341 */               buffer.append(c);
/*      */             }
/*      */             else {
/*  344 */               key = buffer.toReplacedString();
/*  345 */               buffer.reset(); }
/*  346 */             break;
/*      */           
/*      */           case '+': 
/*  349 */             buffer.append((byte)32);
/*  350 */             break;
/*      */           
/*      */           case '%': 
/*  353 */             if (i + 2 < end)
/*      */             {
/*  355 */               if ('u' == query.charAt(i + 1))
/*      */               {
/*  357 */                 i++;
/*  358 */                 if (i + 4 < end)
/*      */                 {
/*  360 */                   char top = query.charAt(++i);
/*  361 */                   char hi = query.charAt(++i);
/*  362 */                   char lo = query.charAt(++i);
/*  363 */                   char bot = query.charAt(++i);
/*  364 */                   buffer.getStringBuilder().append(Character.toChars((TypeUtil.convertHexDigit(top) << 12) + (TypeUtil.convertHexDigit(hi) << 8) + (TypeUtil.convertHexDigit(lo) << 4) + TypeUtil.convertHexDigit(bot)));
/*      */                 }
/*      */                 else
/*      */                 {
/*  368 */                   buffer.getStringBuilder().append(65533);
/*  369 */                   i = end;
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*  374 */                 char hi = query.charAt(++i);
/*  375 */                 char lo = query.charAt(++i);
/*  376 */                 buffer.append((byte)((TypeUtil.convertHexDigit(hi) << 4) + TypeUtil.convertHexDigit(lo)));
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/*  381 */               buffer.getStringBuilder().append(65533);
/*  382 */               i = end;
/*      */             }
/*  384 */             break;
/*      */           
/*      */           default: 
/*  387 */             buffer.append(c);
/*      */           }
/*      */           
/*      */         }
/*      */         catch (Utf8Appendable.NotUtf8Exception e)
/*      */         {
/*  393 */           LOG.warn(e.toString(), new Object[0]);
/*  394 */           LOG.debug(e);
/*      */         }
/*      */         catch (NumberFormatException e)
/*      */         {
/*  398 */           buffer.append(Utf8Appendable.REPLACEMENT_UTF8, 0, 3);
/*  399 */           LOG.warn(e.toString(), new Object[0]);
/*  400 */           LOG.debug(e);
/*      */         }
/*      */       }
/*      */       
/*  404 */       if (key != null)
/*      */       {
/*  406 */         value = buffer.toReplacedString();
/*  407 */         buffer.reset();
/*  408 */         map.add(key, value);
/*      */       }
/*  410 */       else if (buffer.length() > 0)
/*      */       {
/*  412 */         map.add(buffer.toReplacedString(), "");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void decode88591To(InputStream in, MultiMap<String> map, int maxLength, int maxKeys)
/*      */     throws IOException
/*      */   {
/*  429 */     synchronized (map)
/*      */     {
/*  431 */       StringBuffer buffer = new StringBuffer();
/*  432 */       String key = null;
/*  433 */       String value = null;
/*      */       
/*      */ 
/*      */ 
/*  437 */       int totalLength = 0;
/*  438 */       int b; while ((b = in.read()) >= 0)
/*      */       {
/*  440 */         switch ((char)b)
/*      */         {
/*      */         case '&': 
/*  443 */           value = buffer.length() == 0 ? "" : buffer.toString();
/*  444 */           buffer.setLength(0);
/*  445 */           if (key != null)
/*      */           {
/*  447 */             map.add(key, value);
/*      */           }
/*  449 */           else if ((value != null) && (value.length() > 0))
/*      */           {
/*  451 */             map.add(value, "");
/*      */           }
/*  453 */           key = null;
/*  454 */           value = null;
/*  455 */           if ((maxKeys > 0) && (map.size() > maxKeys)) {
/*  456 */             throw new IllegalStateException("Form too many keys");
/*      */           }
/*      */           break;
/*      */         case '=': 
/*  460 */           if (key != null)
/*      */           {
/*  462 */             buffer.append((char)b);
/*      */           }
/*      */           else {
/*  465 */             key = buffer.toString();
/*  466 */             buffer.setLength(0); }
/*  467 */           break;
/*      */         
/*      */         case '+': 
/*  470 */           buffer.append(' ');
/*  471 */           break;
/*      */         
/*      */         case '%': 
/*  474 */           int code0 = in.read();
/*  475 */           if (117 == code0)
/*      */           {
/*  477 */             int code1 = in.read();
/*  478 */             if (code1 >= 0)
/*      */             {
/*  480 */               int code2 = in.read();
/*  481 */               if (code2 >= 0)
/*      */               {
/*  483 */                 int code3 = in.read();
/*  484 */                 if (code3 >= 0) {
/*  485 */                   buffer.append(Character.toChars((TypeUtil.convertHexDigit(code0) << 12) + (TypeUtil.convertHexDigit(code1) << 8) + (TypeUtil.convertHexDigit(code2) << 4) + TypeUtil.convertHexDigit(code3)));
/*      */                 }
/*      */               }
/*      */             }
/*  489 */           } else if (code0 >= 0)
/*      */           {
/*  491 */             int code1 = in.read();
/*  492 */             if (code1 >= 0)
/*  493 */               buffer.append((char)((TypeUtil.convertHexDigit(code0) << 4) + TypeUtil.convertHexDigit(code1))); }
/*  494 */           break;
/*      */         
/*      */ 
/*      */         default: 
/*  498 */           buffer.append((char)b);
/*      */         }
/*      */         
/*  501 */         if (maxLength >= 0) { totalLength++; if (totalLength > maxLength)
/*  502 */             throw new IllegalStateException("Form too large");
/*      */         }
/*      */       }
/*  505 */       if (key != null)
/*      */       {
/*  507 */         value = buffer.length() == 0 ? "" : buffer.toString();
/*  508 */         buffer.setLength(0);
/*  509 */         map.add(key, value);
/*      */       }
/*  511 */       else if (buffer.length() > 0)
/*      */       {
/*  513 */         map.add(buffer.toString(), "");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void decodeUtf8To(InputStream in, MultiMap<String> map, int maxLength, int maxKeys)
/*      */     throws IOException
/*      */   {
/*  529 */     synchronized (map)
/*      */     {
/*  531 */       Utf8StringBuilder buffer = new Utf8StringBuilder();
/*  532 */       String key = null;
/*  533 */       String value = null;
/*      */       
/*      */ 
/*      */ 
/*  537 */       int totalLength = 0;
/*  538 */       int b; while ((b = in.read()) >= 0)
/*      */       {
/*      */         try
/*      */         {
/*  542 */           switch ((char)b)
/*      */           {
/*      */           case '&': 
/*  545 */             value = buffer.toReplacedString();
/*  546 */             buffer.reset();
/*  547 */             if (key != null)
/*      */             {
/*  549 */               map.add(key, value);
/*      */             }
/*  551 */             else if ((value != null) && (value.length() > 0))
/*      */             {
/*  553 */               map.add(value, "");
/*      */             }
/*  555 */             key = null;
/*  556 */             value = null;
/*  557 */             if ((maxKeys > 0) && (map.size() > maxKeys)) {
/*  558 */               throw new IllegalStateException("Form too many keys");
/*      */             }
/*      */             break;
/*      */           case '=': 
/*  562 */             if (key != null)
/*      */             {
/*  564 */               buffer.append((byte)b);
/*      */             }
/*      */             else {
/*  567 */               key = buffer.toReplacedString();
/*  568 */               buffer.reset(); }
/*  569 */             break;
/*      */           
/*      */           case '+': 
/*  572 */             buffer.append((byte)32);
/*  573 */             break;
/*      */           
/*      */           case '%': 
/*  576 */             int code0 = in.read();
/*  577 */             boolean decoded = false;
/*  578 */             if (117 == code0)
/*      */             {
/*  580 */               code0 = in.read();
/*  581 */               if (code0 >= 0)
/*      */               {
/*  583 */                 int code1 = in.read();
/*  584 */                 if (code1 >= 0)
/*      */                 {
/*  586 */                   int code2 = in.read();
/*  587 */                   if (code2 >= 0)
/*      */                   {
/*  589 */                     int code3 = in.read();
/*  590 */                     if (code3 >= 0)
/*      */                     {
/*  592 */                       buffer.getStringBuilder().append(
/*  593 */                         Character.toChars((TypeUtil.convertHexDigit(code0) << 12) + (TypeUtil.convertHexDigit(code1) << 8) + (TypeUtil.convertHexDigit(code2) << 4) + TypeUtil.convertHexDigit(code3)));
/*  594 */                       decoded = true;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*  600 */             else if (code0 >= 0)
/*      */             {
/*  602 */               int code1 = in.read();
/*  603 */               if (code1 >= 0)
/*      */               {
/*  605 */                 buffer.append((byte)((TypeUtil.convertHexDigit(code0) << 4) + TypeUtil.convertHexDigit(code1)));
/*  606 */                 decoded = true;
/*      */               }
/*      */             }
/*      */             
/*  610 */             if (!decoded) {
/*  611 */               buffer.getStringBuilder().append(65533);
/*      */             }
/*      */             
/*      */             break;
/*      */           default: 
/*  616 */             buffer.append((byte)b);
/*      */           }
/*      */           
/*      */         }
/*      */         catch (Utf8Appendable.NotUtf8Exception e)
/*      */         {
/*  622 */           LOG.warn(e.toString(), new Object[0]);
/*  623 */           LOG.debug(e);
/*      */         }
/*      */         catch (NumberFormatException e)
/*      */         {
/*  627 */           buffer.append(Utf8Appendable.REPLACEMENT_UTF8, 0, 3);
/*  628 */           LOG.warn(e.toString(), new Object[0]);
/*  629 */           LOG.debug(e);
/*      */         }
/*  631 */         if (maxLength >= 0) { totalLength++; if (totalLength > maxLength)
/*  632 */             throw new IllegalStateException("Form too large");
/*      */         }
/*      */       }
/*  635 */       if (key != null)
/*      */       {
/*  637 */         value = buffer.toReplacedString();
/*  638 */         buffer.reset();
/*  639 */         map.add(key, value);
/*      */       }
/*  641 */       else if (buffer.length() > 0)
/*      */       {
/*  643 */         map.add(buffer.toReplacedString(), "");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void decodeUtf16To(InputStream in, MultiMap<String> map, int maxLength, int maxKeys)
/*      */     throws IOException
/*      */   {
/*  651 */     InputStreamReader input = new InputStreamReader(in, StandardCharsets.UTF_16);
/*  652 */     StringWriter buf = new StringWriter(8192);
/*  653 */     IO.copy(input, buf, maxLength);
/*      */     
/*      */ 
/*  656 */     decodeTo(buf.getBuffer().toString(), map, StandardCharsets.UTF_16);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void decodeTo(InputStream in, MultiMap<String> map, String charset, int maxLength, int maxKeys)
/*      */     throws IOException
/*      */   {
/*  671 */     if (charset == null)
/*      */     {
/*  673 */       if (ENCODING.equals(StandardCharsets.UTF_8)) {
/*  674 */         decodeUtf8To(in, map, maxLength, maxKeys);
/*      */       } else {
/*  676 */         decodeTo(in, map, ENCODING, maxLength, maxKeys);
/*      */       }
/*  678 */     } else if ("utf-8".equalsIgnoreCase(charset)) {
/*  679 */       decodeUtf8To(in, map, maxLength, maxKeys);
/*  680 */     } else if ("iso-8859-1".equalsIgnoreCase(charset)) {
/*  681 */       decode88591To(in, map, maxLength, maxKeys);
/*  682 */     } else if ("utf-16".equalsIgnoreCase(charset)) {
/*  683 */       decodeUtf16To(in, map, maxLength, maxKeys);
/*      */     } else {
/*  685 */       decodeTo(in, map, Charset.forName(charset), maxLength, maxKeys);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void decodeTo(InputStream in, MultiMap<String> map, Charset charset, int maxLength, int maxKeys)
/*      */     throws IOException
/*      */   {
/*  701 */     if (charset == null) {
/*  702 */       charset = ENCODING;
/*      */     }
/*  704 */     if (StandardCharsets.UTF_8.equals(charset))
/*      */     {
/*  706 */       decodeUtf8To(in, map, maxLength, maxKeys);
/*  707 */       return;
/*      */     }
/*      */     
/*  710 */     if (StandardCharsets.ISO_8859_1.equals(charset))
/*      */     {
/*  712 */       decode88591To(in, map, maxLength, maxKeys);
/*  713 */       return;
/*      */     }
/*      */     
/*  716 */     if (StandardCharsets.UTF_16.equals(charset))
/*      */     {
/*  718 */       decodeUtf16To(in, map, maxLength, maxKeys);
/*  719 */       return;
/*      */     }
/*      */     
/*  722 */     synchronized (map)
/*      */     {
/*  724 */       String key = null;
/*  725 */       String value = null;
/*      */       
/*      */ 
/*      */ 
/*  729 */       int totalLength = 0;
/*      */       
/*  731 */       ByteArrayOutputStream2 output = new ByteArrayOutputStream2();Throwable localThrowable3 = null;
/*      */       try {
/*  733 */         int size = 0;
/*      */         int c;
/*  735 */         while ((c = in.read()) > 0)
/*      */         {
/*  737 */           switch ((char)c)
/*      */           {
/*      */           case '&': 
/*  740 */             size = output.size();
/*  741 */             value = size == 0 ? "" : output.toString(charset);
/*  742 */             output.setCount(0);
/*  743 */             if (key != null)
/*      */             {
/*  745 */               map.add(key, value);
/*      */             }
/*  747 */             else if ((value != null) && (value.length() > 0))
/*      */             {
/*  749 */               map.add(value, "");
/*      */             }
/*  751 */             key = null;
/*  752 */             value = null;
/*  753 */             if ((maxKeys > 0) && (map.size() > maxKeys))
/*  754 */               throw new IllegalStateException("Form too many keys");
/*      */             break;
/*      */           case '=': 
/*  757 */             if (key != null)
/*      */             {
/*  759 */               output.write(c);
/*      */             }
/*      */             else {
/*  762 */               size = output.size();
/*  763 */               key = size == 0 ? "" : output.toString(charset);
/*  764 */               output.setCount(0); }
/*  765 */             break;
/*      */           case '+': 
/*  767 */             output.write(32);
/*  768 */             break;
/*      */           case '%': 
/*  770 */             int code0 = in.read();
/*  771 */             if (117 == code0)
/*      */             {
/*  773 */               int code1 = in.read();
/*  774 */               if (code1 >= 0)
/*      */               {
/*  776 */                 int code2 = in.read();
/*  777 */                 if (code2 >= 0)
/*      */                 {
/*  779 */                   int code3 = in.read();
/*  780 */                   if (code3 >= 0) {
/*  781 */                     output.write(new String(Character.toChars((TypeUtil.convertHexDigit(code0) << 12) + (TypeUtil.convertHexDigit(code1) << 8) + (TypeUtil.convertHexDigit(code2) << 4) + TypeUtil.convertHexDigit(code3))).getBytes(charset));
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*  786 */             else if (code0 >= 0)
/*      */             {
/*  788 */               int code1 = in.read();
/*  789 */               if (code1 >= 0)
/*  790 */                 output.write((TypeUtil.convertHexDigit(code0) << 4) + TypeUtil.convertHexDigit(code1)); }
/*  791 */             break;
/*      */           
/*      */           default: 
/*  794 */             output.write(c);
/*      */           }
/*      */           
/*      */           
/*  798 */           totalLength++;
/*  799 */           if ((maxLength >= 0) && (totalLength > maxLength)) {
/*  800 */             throw new IllegalStateException("Form too large");
/*      */           }
/*      */         }
/*  803 */         size = output.size();
/*  804 */         if (key != null)
/*      */         {
/*  806 */           value = size == 0 ? "" : output.toString(charset);
/*  807 */           output.setCount(0);
/*  808 */           map.add(key, value);
/*      */         }
/*  810 */         else if (size > 0) {
/*  811 */           map.add(output.toString(charset), "");
/*      */         }
/*      */       }
/*      */       catch (Throwable localThrowable1)
/*      */       {
/*  731 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  812 */         if (output != null) { if (localThrowable3 != null) try { output.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { output.close();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String decodeString(String encoded)
/*      */   {
/*  825 */     return decodeString(encoded, 0, encoded.length(), ENCODING);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String decodeString(String encoded, int offset, int length, Charset charset)
/*      */   {
/*  840 */     if ((charset == null) || (StandardCharsets.UTF_8.equals(charset)))
/*      */     {
/*  842 */       Utf8StringBuffer buffer = null;
/*      */       
/*  844 */       for (int i = 0; i < length; i++)
/*      */       {
/*  846 */         char c = encoded.charAt(offset + i);
/*  847 */         if ((c < 0) || (c > 'ÿ'))
/*      */         {
/*  849 */           if (buffer == null)
/*      */           {
/*  851 */             buffer = new Utf8StringBuffer(length);
/*  852 */             buffer.getStringBuffer().append(encoded, offset, offset + i + 1);
/*      */           }
/*      */           else {
/*  855 */             buffer.getStringBuffer().append(c);
/*      */           }
/*  857 */         } else if (c == '+')
/*      */         {
/*  859 */           if (buffer == null)
/*      */           {
/*  861 */             buffer = new Utf8StringBuffer(length);
/*  862 */             buffer.getStringBuffer().append(encoded, offset, offset + i);
/*      */           }
/*      */           
/*  865 */           buffer.getStringBuffer().append(' ');
/*      */         }
/*  867 */         else if (c == '%')
/*      */         {
/*  869 */           if (buffer == null)
/*      */           {
/*  871 */             buffer = new Utf8StringBuffer(length);
/*  872 */             buffer.getStringBuffer().append(encoded, offset, offset + i);
/*      */           }
/*      */           
/*  875 */           if (i + 2 < length)
/*      */           {
/*      */             try
/*      */             {
/*  879 */               if ('u' == encoded.charAt(offset + i + 1))
/*      */               {
/*  881 */                 if (i + 5 < length)
/*      */                 {
/*  883 */                   int o = offset + i + 2;
/*  884 */                   i += 5;
/*  885 */                   String unicode = new String(Character.toChars(TypeUtil.parseInt(encoded, o, 4, 16)));
/*  886 */                   buffer.getStringBuffer().append(unicode);
/*      */                 }
/*      */                 else
/*      */                 {
/*  890 */                   i = length;
/*  891 */                   buffer.getStringBuffer().append(65533);
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*  896 */                 int o = offset + i + 1;
/*  897 */                 i += 2;
/*  898 */                 byte b = (byte)TypeUtil.parseInt(encoded, o, 2, 16);
/*  899 */                 buffer.append(b);
/*      */               }
/*      */             }
/*      */             catch (Utf8Appendable.NotUtf8Exception e)
/*      */             {
/*  904 */               LOG.warn(e.toString(), new Object[0]);
/*  905 */               LOG.debug(e);
/*      */             }
/*      */             catch (NumberFormatException e)
/*      */             {
/*  909 */               LOG.warn(e.toString(), new Object[0]);
/*  910 */               LOG.debug(e);
/*  911 */               buffer.getStringBuffer().append(65533);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  916 */             buffer.getStringBuffer().append(65533);
/*  917 */             i = length;
/*      */           }
/*      */         }
/*  920 */         else if (buffer != null) {
/*  921 */           buffer.getStringBuffer().append(c);
/*      */         }
/*      */       }
/*  924 */       if (buffer == null)
/*      */       {
/*  926 */         if ((offset == 0) && (encoded.length() == length))
/*  927 */           return encoded;
/*  928 */         return encoded.substring(offset, offset + length);
/*      */       }
/*      */       
/*  931 */       return buffer.toReplacedString();
/*      */     }
/*      */     
/*      */ 
/*  935 */     StringBuffer buffer = null;
/*      */     
/*  937 */     for (int i = 0; i < length; i++)
/*      */     {
/*  939 */       char c = encoded.charAt(offset + i);
/*  940 */       if ((c < 0) || (c > 'ÿ'))
/*      */       {
/*  942 */         if (buffer == null)
/*      */         {
/*  944 */           buffer = new StringBuffer(length);
/*  945 */           buffer.append(encoded, offset, offset + i + 1);
/*      */         }
/*      */         else {
/*  948 */           buffer.append(c);
/*      */         }
/*  950 */       } else if (c == '+')
/*      */       {
/*  952 */         if (buffer == null)
/*      */         {
/*  954 */           buffer = new StringBuffer(length);
/*  955 */           buffer.append(encoded, offset, offset + i);
/*      */         }
/*      */         
/*  958 */         buffer.append(' ');
/*      */       }
/*  960 */       else if (c == '%')
/*      */       {
/*  962 */         if (buffer == null)
/*      */         {
/*  964 */           buffer = new StringBuffer(length);
/*  965 */           buffer.append(encoded, offset, offset + i);
/*      */         }
/*      */         
/*  968 */         byte[] ba = new byte[length];
/*  969 */         int n = 0;
/*  970 */         while ((c >= 0) && (c <= 'ÿ'))
/*      */         {
/*  972 */           if (c == '%')
/*      */           {
/*  974 */             if (i + 2 < length)
/*      */             {
/*      */               try
/*      */               {
/*  978 */                 if ('u' == encoded.charAt(offset + i + 1))
/*      */                 {
/*  980 */                   if (i + 6 < length)
/*      */                   {
/*  982 */                     int o = offset + i + 2;
/*  983 */                     i += 6;
/*  984 */                     String unicode = new String(Character.toChars(TypeUtil.parseInt(encoded, o, 4, 16)));
/*  985 */                     byte[] reencoded = unicode.getBytes(charset);
/*  986 */                     System.arraycopy(reencoded, 0, ba, n, reencoded.length);
/*  987 */                     n += reencoded.length;
/*      */                   }
/*      */                   else
/*      */                   {
/*  991 */                     ba[(n++)] = 63;
/*  992 */                     i = length;
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/*  997 */                   int o = offset + i + 1;
/*  998 */                   i += 3;
/*  999 */                   ba[n] = ((byte)TypeUtil.parseInt(encoded, o, 2, 16));
/* 1000 */                   n++;
/*      */                 }
/*      */               }
/*      */               catch (Exception e)
/*      */               {
/* 1005 */                 LOG.warn(e.toString(), new Object[0]);
/* 1006 */                 LOG.debug(e);
/* 1007 */                 ba[(n++)] = 63;
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 1012 */               ba[(n++)] = 63;
/* 1013 */               i = length;
/*      */             }
/*      */           }
/* 1016 */           else if (c == '+')
/*      */           {
/* 1018 */             ba[(n++)] = 32;
/* 1019 */             i++;
/*      */           }
/*      */           else
/*      */           {
/* 1023 */             ba[(n++)] = ((byte)c);
/* 1024 */             i++;
/*      */           }
/*      */           
/* 1027 */           if (i >= length)
/*      */             break;
/* 1029 */           c = encoded.charAt(offset + i);
/*      */         }
/*      */         
/* 1032 */         i--;
/* 1033 */         buffer.append(new String(ba, 0, n, charset));
/*      */ 
/*      */       }
/* 1036 */       else if (buffer != null) {
/* 1037 */         buffer.append(c);
/*      */       }
/*      */     }
/* 1040 */     if (buffer == null)
/*      */     {
/* 1042 */       if ((offset == 0) && (encoded.length() == length))
/* 1043 */         return encoded;
/* 1044 */       return encoded.substring(offset, offset + length);
/*      */     }
/*      */     
/* 1047 */     return buffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String encodeString(String string)
/*      */   {
/* 1059 */     return encodeString(string, ENCODING);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String encodeString(String string, Charset charset)
/*      */   {
/* 1070 */     if (charset == null)
/* 1071 */       charset = ENCODING;
/* 1072 */     byte[] bytes = null;
/* 1073 */     bytes = string.getBytes(charset);
/*      */     
/* 1075 */     int len = bytes.length;
/* 1076 */     byte[] encoded = new byte[bytes.length * 3];
/* 1077 */     int n = 0;
/* 1078 */     boolean noEncode = true;
/*      */     
/* 1080 */     for (int i = 0; i < len; i++)
/*      */     {
/* 1082 */       byte b = bytes[i];
/*      */       
/* 1084 */       if (b == 32)
/*      */       {
/* 1086 */         noEncode = false;
/* 1087 */         encoded[(n++)] = 43;
/*      */       }
/* 1089 */       else if (((b >= 97) && (b <= 122)) || ((b >= 65) && (b <= 90)) || ((b >= 48) && (b <= 57)))
/*      */       {
/*      */ 
/*      */ 
/* 1093 */         encoded[(n++)] = b;
/*      */       }
/*      */       else
/*      */       {
/* 1097 */         noEncode = false;
/* 1098 */         encoded[(n++)] = 37;
/* 1099 */         byte nibble = (byte)((b & 0xF0) >> 4);
/* 1100 */         if (nibble >= 10) {
/* 1101 */           encoded[(n++)] = ((byte)(65 + nibble - 10));
/*      */         } else
/* 1103 */           encoded[(n++)] = ((byte)(48 + nibble));
/* 1104 */         nibble = (byte)(b & 0xF);
/* 1105 */         if (nibble >= 10) {
/* 1106 */           encoded[(n++)] = ((byte)(65 + nibble - 10));
/*      */         } else {
/* 1108 */           encoded[(n++)] = ((byte)(48 + nibble));
/*      */         }
/*      */       }
/*      */     }
/* 1112 */     if (noEncode) {
/* 1113 */       return string;
/*      */     }
/* 1115 */     return new String(encoded, 0, n, charset);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object clone()
/*      */   {
/* 1125 */     return new UrlEncoded(this);
/*      */   }
/*      */   
/*      */   public UrlEncoded() {}
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\UrlEncoded.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */