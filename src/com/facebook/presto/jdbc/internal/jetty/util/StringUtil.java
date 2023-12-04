/*      */ package com.facebook.presto.jdbc.internal.jetty.util;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
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
/*      */ public class StringUtil
/*      */ {
/*   40 */   private static final Logger LOG = Log.getLogger(StringUtil.class);
/*      */   
/*      */ 
/*   43 */   private static final Trie<String> CHARSETS = new ArrayTrie(256);
/*      */   
/*      */   public static final String ALL_INTERFACES = "0.0.0.0";
/*      */   
/*      */   public static final String CRLF = "\r\n";
/*      */   
/*      */   @Deprecated
/*   50 */   public static final String __LINE_SEPARATOR = System.lineSeparator();
/*      */   
/*      */   public static final String __ISO_8859_1 = "iso-8859-1";
/*      */   public static final String __UTF8 = "utf-8";
/*      */   public static final String __UTF16 = "utf-16";
/*      */   
/*      */   static
/*      */   {
/*   58 */     CHARSETS.put("utf-8", "utf-8");
/*   59 */     CHARSETS.put("utf8", "utf-8");
/*   60 */     CHARSETS.put("utf-16", "utf-16");
/*   61 */     CHARSETS.put("utf16", "utf-16");
/*   62 */     CHARSETS.put("iso-8859-1", "iso-8859-1");
/*   63 */     CHARSETS.put("iso_8859_1", "iso-8859-1");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String normalizeCharset(String s)
/*      */   {
/*   74 */     String n = (String)CHARSETS.get(s);
/*   75 */     return n == null ? s : n;
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
/*      */   public static String normalizeCharset(String s, int offset, int length)
/*      */   {
/*   88 */     String n = (String)CHARSETS.get(s, offset, length);
/*   89 */     return n == null ? s.substring(offset, offset + length) : n;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*   94 */   public static final char[] lowercases = { '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '' };
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
/*      */   public static String asciiToLowerCase(String s)
/*      */   {
/*  120 */     char[] c = null;
/*  121 */     int i = s.length();
/*      */     
/*      */ 
/*  124 */     while (i-- > 0)
/*      */     {
/*  126 */       char c1 = s.charAt(i);
/*  127 */       if (c1 <= '')
/*      */       {
/*  129 */         char c2 = lowercases[c1];
/*  130 */         if (c1 != c2)
/*      */         {
/*  132 */           c = s.toCharArray();
/*  133 */           c[i] = c2;
/*  134 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  139 */     while (i-- > 0)
/*      */     {
/*  141 */       if (c[i] <= '') {
/*  142 */         c[i] = lowercases[c[i]];
/*      */       }
/*      */     }
/*  145 */     return c == null ? s : new String(c);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean startsWithIgnoreCase(String s, String w)
/*      */   {
/*  152 */     if (w == null) {
/*  153 */       return true;
/*      */     }
/*  155 */     if ((s == null) || (s.length() < w.length())) {
/*  156 */       return false;
/*      */     }
/*  158 */     for (int i = 0; i < w.length(); i++)
/*      */     {
/*  160 */       char c1 = s.charAt(i);
/*  161 */       char c2 = w.charAt(i);
/*  162 */       if (c1 != c2)
/*      */       {
/*  164 */         if (c1 <= '')
/*  165 */           c1 = lowercases[c1];
/*  166 */         if (c2 <= '')
/*  167 */           c2 = lowercases[c2];
/*  168 */         if (c1 != c2)
/*  169 */           return false;
/*      */       }
/*      */     }
/*  172 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean endsWithIgnoreCase(String s, String w)
/*      */   {
/*  178 */     if (w == null) {
/*  179 */       return true;
/*      */     }
/*  181 */     if (s == null) {
/*  182 */       return false;
/*      */     }
/*  184 */     int sl = s.length();
/*  185 */     int wl = w.length();
/*      */     
/*  187 */     if (sl < wl) {
/*  188 */       return false;
/*      */     }
/*  190 */     for (int i = wl; i-- > 0;)
/*      */     {
/*  192 */       char c1 = s.charAt(--sl);
/*  193 */       char c2 = w.charAt(i);
/*  194 */       if (c1 != c2)
/*      */       {
/*  196 */         if (c1 <= '')
/*  197 */           c1 = lowercases[c1];
/*  198 */         if (c2 <= '')
/*  199 */           c2 = lowercases[c2];
/*  200 */         if (c1 != c2)
/*  201 */           return false;
/*      */       }
/*      */     }
/*  204 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int indexFrom(String s, String chars)
/*      */   {
/*  216 */     for (int i = 0; i < s.length(); i++)
/*  217 */       if (chars.indexOf(s.charAt(i)) >= 0)
/*  218 */         return i;
/*  219 */     return -1;
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
/*      */   public static String replace(String s, String sub, String with)
/*      */   {
/*  232 */     int c = 0;
/*  233 */     int i = s.indexOf(sub, c);
/*  234 */     if (i == -1) {
/*  235 */       return s;
/*      */     }
/*  237 */     StringBuilder buf = new StringBuilder(s.length() + with.length());
/*      */     
/*      */     do
/*      */     {
/*  241 */       buf.append(s.substring(c, i));
/*  242 */       buf.append(with);
/*  243 */       c = i + sub.length();
/*  244 */     } while ((i = s.indexOf(sub, c)) != -1);
/*      */     
/*  246 */     if (c < s.length()) {
/*  247 */       buf.append(s.substring(c, s.length()));
/*      */     }
/*  249 */     return buf.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static String unquote(String s)
/*      */   {
/*  260 */     return QuotedStringTokenizer.unquote(s);
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
/*      */ 
/*      */   public static void append(StringBuilder buf, String s, int offset, int length)
/*      */   {
/*  276 */     synchronized (buf)
/*      */     {
/*  278 */       int end = offset + length;
/*  279 */       for (int i = offset; i < end; i++)
/*      */       {
/*  281 */         if (i >= s.length())
/*      */           break;
/*  283 */         buf.append(s.charAt(i));
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
/*      */   public static void append(StringBuilder buf, byte b, int base)
/*      */   {
/*  299 */     int bi = 0xFF & b;
/*  300 */     int c = 48 + bi / base % base;
/*  301 */     if (c > 57)
/*  302 */       c = 97 + (c - 48 - 10);
/*  303 */     buf.append((char)c);
/*  304 */     c = 48 + bi % base;
/*  305 */     if (c > 57)
/*  306 */       c = 97 + (c - 48 - 10);
/*  307 */     buf.append((char)c);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void append2digits(StringBuffer buf, int i)
/*      */   {
/*  319 */     if (i < 100)
/*      */     {
/*  321 */       buf.append((char)(i / 10 + 48));
/*  322 */       buf.append((char)(i % 10 + 48));
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
/*      */   public static void append2digits(StringBuilder buf, int i)
/*      */   {
/*  335 */     if (i < 100)
/*      */     {
/*  337 */       buf.append((char)(i / 10 + 48));
/*  338 */       buf.append((char)(i % 10 + 48));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String nonNull(String s)
/*      */   {
/*  349 */     if (s == null)
/*  350 */       return "";
/*  351 */     return s;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean equals(String s, char[] buf, int offset, int length)
/*      */   {
/*  357 */     if (s.length() != length)
/*  358 */       return false;
/*  359 */     for (int i = 0; i < length; i++)
/*  360 */       if (buf[(offset + i)] != s.charAt(i))
/*  361 */         return false;
/*  362 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public static String toUTF8String(byte[] b, int offset, int length)
/*      */   {
/*  368 */     return new String(b, offset, length, StandardCharsets.UTF_8);
/*      */   }
/*      */   
/*      */ 
/*      */   public static String toString(byte[] b, int offset, int length, String charset)
/*      */   {
/*      */     try
/*      */     {
/*  376 */       return new String(b, offset, length, charset);
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/*  380 */       throw new IllegalArgumentException(e);
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
/*      */   public static int indexOfControlChars(String str)
/*      */   {
/*  414 */     if (str == null)
/*      */     {
/*  416 */       return -1;
/*      */     }
/*  418 */     int len = str.length();
/*  419 */     for (int i = 0; i < len; i++)
/*      */     {
/*  421 */       if (Character.isISOControl(str.codePointAt(i)))
/*      */       {
/*      */ 
/*  424 */         return i;
/*      */       }
/*      */     }
/*      */     
/*  428 */     return -1;
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
/*      */   public static boolean isBlank(String str)
/*      */   {
/*  454 */     if (str == null)
/*      */     {
/*  456 */       return true;
/*      */     }
/*  458 */     int len = str.length();
/*  459 */     for (int i = 0; i < len; i++)
/*      */     {
/*  461 */       if (!Character.isWhitespace(str.codePointAt(i)))
/*      */       {
/*      */ 
/*  464 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  468 */     return true;
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
/*      */   public static boolean isNotBlank(String str)
/*      */   {
/*  494 */     if (str == null)
/*      */     {
/*  496 */       return false;
/*      */     }
/*  498 */     int len = str.length();
/*  499 */     for (int i = 0; i < len; i++)
/*      */     {
/*  501 */       if (!Character.isWhitespace(str.codePointAt(i)))
/*      */       {
/*      */ 
/*  504 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  508 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isUTF8(String charset)
/*      */   {
/*  514 */     return ("utf-8".equalsIgnoreCase(charset)) || ("utf-8".equalsIgnoreCase(normalizeCharset(charset)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String printable(String name)
/*      */   {
/*  521 */     if (name == null)
/*  522 */       return null;
/*  523 */     StringBuilder buf = new StringBuilder(name.length());
/*  524 */     for (int i = 0; i < name.length(); i++)
/*      */     {
/*  526 */       char c = name.charAt(i);
/*  527 */       if (!Character.isISOControl(c))
/*  528 */         buf.append(c);
/*      */     }
/*  530 */     return buf.toString();
/*      */   }
/*      */   
/*      */ 
/*      */   public static String printable(byte[] b)
/*      */   {
/*  536 */     StringBuilder buf = new StringBuilder();
/*  537 */     for (int i = 0; i < b.length; i++)
/*      */     {
/*  539 */       char c = (char)b[i];
/*  540 */       if ((Character.isWhitespace(c)) || ((c > ' ') && (c < ''))) {
/*  541 */         buf.append(c);
/*      */       }
/*      */       else {
/*  544 */         buf.append("0x");
/*  545 */         TypeUtil.toHex(b[i], buf);
/*      */       }
/*      */     }
/*  548 */     return buf.toString();
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String s)
/*      */   {
/*  553 */     return s.getBytes(StandardCharsets.ISO_8859_1);
/*      */   }
/*      */   
/*      */   public static byte[] getUtf8Bytes(String s)
/*      */   {
/*  558 */     return s.getBytes(StandardCharsets.UTF_8);
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String s, String charset)
/*      */   {
/*      */     try
/*      */     {
/*  565 */       return s.getBytes(charset);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  569 */       LOG.warn(e); }
/*  570 */     return s.getBytes();
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
/*      */ 
/*      */ 
/*      */   public static String sidBytesToString(byte[] sidBytes)
/*      */   {
/*  587 */     StringBuilder sidString = new StringBuilder();
/*      */     
/*      */ 
/*  590 */     sidString.append("S-");
/*      */     
/*      */ 
/*  593 */     sidString.append(Byte.toString(sidBytes[0])).append('-');
/*      */     
/*  595 */     StringBuilder tmpBuilder = new StringBuilder();
/*      */     
/*      */ 
/*  598 */     for (int i = 2; i <= 7; i++)
/*      */     {
/*  600 */       tmpBuilder.append(Integer.toHexString(sidBytes[i] & 0xFF));
/*      */     }
/*      */     
/*  603 */     sidString.append(Long.parseLong(tmpBuilder.toString(), 16));
/*      */     
/*      */ 
/*  606 */     int subAuthorityCount = sidBytes[1];
/*      */     
/*      */ 
/*  609 */     for (int i = 0; i < subAuthorityCount; i++)
/*      */     {
/*  611 */       int offset = i * 4;
/*  612 */       tmpBuilder.setLength(0);
/*      */       
/*  614 */       tmpBuilder.append(String.format("%02X%02X%02X%02X", new Object[] {
/*  615 */         Integer.valueOf(sidBytes[(11 + offset)] & 0xFF), 
/*  616 */         Integer.valueOf(sidBytes[(10 + offset)] & 0xFF), 
/*  617 */         Integer.valueOf(sidBytes[(9 + offset)] & 0xFF), 
/*  618 */         Integer.valueOf(sidBytes[(8 + offset)] & 0xFF) }));
/*  619 */       sidString.append('-').append(Long.parseLong(tmpBuilder.toString(), 16));
/*      */     }
/*      */     
/*  622 */     return sidString.toString();
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
/*      */   public static byte[] sidStringToBytes(String sidString)
/*      */   {
/*  636 */     String[] sidTokens = sidString.split("-");
/*      */     
/*  638 */     int subAuthorityCount = sidTokens.length - 3;
/*      */     
/*  640 */     int byteCount = 0;
/*  641 */     byte[] sidBytes = new byte[8 + 4 * subAuthorityCount];
/*      */     
/*      */ 
/*  644 */     sidBytes[(byteCount++)] = ((byte)Integer.parseInt(sidTokens[1]));
/*      */     
/*      */ 
/*  647 */     sidBytes[(byteCount++)] = ((byte)subAuthorityCount);
/*      */     
/*      */ 
/*  650 */     String hexStr = Long.toHexString(Long.parseLong(sidTokens[2]));
/*      */     
/*  652 */     while (hexStr.length() < 12)
/*      */     {
/*  654 */       hexStr = "0" + hexStr;
/*      */     }
/*      */     
/*      */ 
/*  658 */     for (int i = 0; i < hexStr.length(); i += 2)
/*      */     {
/*  660 */       sidBytes[(byteCount++)] = ((byte)Integer.parseInt(hexStr.substring(i, i + 2), 16));
/*      */     }
/*      */     
/*      */ 
/*  664 */     for (int i = 3; i < sidTokens.length; i++)
/*      */     {
/*  666 */       hexStr = Long.toHexString(Long.parseLong(sidTokens[i]));
/*      */       
/*  668 */       while (hexStr.length() < 8)
/*      */       {
/*  670 */         hexStr = "0" + hexStr;
/*      */       }
/*      */       
/*      */ 
/*  674 */       for (int j = hexStr.length(); j > 0; j -= 2)
/*      */       {
/*  676 */         sidBytes[(byteCount++)] = ((byte)Integer.parseInt(hexStr.substring(j - 2, j), 16));
/*      */       }
/*      */     }
/*      */     
/*  680 */     return sidBytes;
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
/*      */   public static int toInt(String string, int from)
/*      */   {
/*  693 */     int val = 0;
/*  694 */     boolean started = false;
/*  695 */     boolean minus = false;
/*      */     
/*  697 */     for (int i = from; i < string.length(); i++)
/*      */     {
/*  699 */       char b = string.charAt(i);
/*  700 */       if (b <= ' ')
/*      */       {
/*  702 */         if (started) {
/*      */           break;
/*      */         }
/*  705 */       } else if ((b >= '0') && (b <= '9'))
/*      */       {
/*  707 */         val = val * 10 + (b - '0');
/*  708 */         started = true;
/*      */       } else {
/*  710 */         if ((b != '-') || (started))
/*      */           break;
/*  712 */         minus = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  718 */     if (started)
/*  719 */       return minus ? -val : val;
/*  720 */     throw new NumberFormatException(string);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static long toLong(String string)
/*      */   {
/*  732 */     long val = 0L;
/*  733 */     boolean started = false;
/*  734 */     boolean minus = false;
/*      */     
/*  736 */     for (int i = 0; i < string.length(); i++)
/*      */     {
/*  738 */       char b = string.charAt(i);
/*  739 */       if (b <= ' ')
/*      */       {
/*  741 */         if (started) {
/*      */           break;
/*      */         }
/*  744 */       } else if ((b >= '0') && (b <= '9'))
/*      */       {
/*  746 */         val = val * 10L + (b - '0');
/*  747 */         started = true;
/*      */       } else {
/*  749 */         if ((b != '-') || (started))
/*      */           break;
/*  751 */         minus = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  757 */     if (started)
/*  758 */       return minus ? -val : val;
/*  759 */     throw new NumberFormatException(string);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String truncate(String str, int maxSize)
/*      */   {
/*  771 */     if (str == null)
/*      */     {
/*  773 */       return null;
/*      */     }
/*      */     
/*  776 */     if (str.length() <= maxSize)
/*      */     {
/*  778 */       return str;
/*      */     }
/*      */     
/*  781 */     return str.substring(0, maxSize);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String[] arrayFromString(String s)
/*      */   {
/*  791 */     if (s == null) {
/*  792 */       return new String[0];
/*      */     }
/*  794 */     if ((!s.startsWith("[")) || (!s.endsWith("]")))
/*  795 */       throw new IllegalArgumentException();
/*  796 */     if (s.length() == 2) {
/*  797 */       return new String[0];
/*      */     }
/*  799 */     return csvSplit(s, 1, s.length() - 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String[] csvSplit(String s)
/*      */   {
/*  809 */     if (s == null)
/*  810 */       return null;
/*  811 */     return csvSplit(s, 0, s.length());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String[] csvSplit(String s, int off, int len)
/*      */   {
/*  823 */     if (s == null)
/*  824 */       return null;
/*  825 */     if ((off < 0) || (len < 0) || (off > s.length())) {
/*  826 */       throw new IllegalArgumentException();
/*      */     }
/*  828 */     List<String> list = new ArrayList();
/*  829 */     csvSplit(list, s, off, len);
/*  830 */     return (String[])list.toArray(new String[list.size()]);
/*      */   }
/*      */   
/*  833 */   static enum CsvSplitState { PRE_DATA,  QUOTE,  SLOSH,  DATA,  WHITE,  POST_DATA;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private CsvSplitState() {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<String> csvSplit(List<String> list, String s, int off, int len)
/*      */   {
/*  851 */     if (list == null)
/*  852 */       list = new ArrayList();
/*  853 */     CsvSplitState state = CsvSplitState.PRE_DATA;
/*  854 */     StringBuilder out = new StringBuilder();
/*  855 */     int last = -1;
/*  856 */     while (len > 0)
/*      */     {
/*  858 */       char ch = s.charAt(off++);
/*  859 */       len--;
/*      */       
/*  861 */       switch (state)
/*      */       {
/*      */       case PRE_DATA: 
/*  864 */         if (!Character.isWhitespace(ch))
/*      */         {
/*      */ 
/*  867 */           if ('"' == ch)
/*      */           {
/*  869 */             state = CsvSplitState.QUOTE;
/*      */ 
/*      */ 
/*      */           }
/*  873 */           else if (',' == ch)
/*      */           {
/*  875 */             list.add("");
/*      */           }
/*      */           else
/*      */           {
/*  879 */             state = CsvSplitState.DATA;
/*  880 */             out.append(ch); } }
/*  881 */         break;
/*      */       
/*      */       case DATA: 
/*  884 */         if (Character.isWhitespace(ch))
/*      */         {
/*  886 */           last = out.length();
/*  887 */           out.append(ch);
/*  888 */           state = CsvSplitState.WHITE;
/*      */ 
/*      */ 
/*      */         }
/*  892 */         else if (',' == ch)
/*      */         {
/*  894 */           list.add(out.toString());
/*  895 */           out.setLength(0);
/*  896 */           state = CsvSplitState.PRE_DATA;
/*      */         }
/*      */         else
/*      */         {
/*  900 */           out.append(ch); }
/*  901 */         break;
/*      */       
/*      */       case WHITE: 
/*  904 */         if (Character.isWhitespace(ch))
/*      */         {
/*  906 */           out.append(ch);
/*      */ 
/*      */ 
/*      */         }
/*  910 */         else if (',' == ch)
/*      */         {
/*  912 */           out.setLength(last);
/*  913 */           list.add(out.toString());
/*  914 */           out.setLength(0);
/*  915 */           state = CsvSplitState.PRE_DATA;
/*      */         }
/*      */         else
/*      */         {
/*  919 */           state = CsvSplitState.DATA;
/*  920 */           out.append(ch);
/*  921 */           last = -1; }
/*  922 */         break;
/*      */       
/*      */       case QUOTE: 
/*  925 */         if ('\\' == ch)
/*      */         {
/*  927 */           state = CsvSplitState.SLOSH;
/*      */ 
/*      */         }
/*  930 */         else if ('"' == ch)
/*      */         {
/*  932 */           list.add(out.toString());
/*  933 */           out.setLength(0);
/*  934 */           state = CsvSplitState.POST_DATA;
/*      */         }
/*      */         else {
/*  937 */           out.append(ch); }
/*  938 */         break;
/*      */       
/*      */       case SLOSH: 
/*  941 */         out.append(ch);
/*  942 */         state = CsvSplitState.QUOTE;
/*  943 */         break;
/*      */       
/*      */       case POST_DATA: 
/*  946 */         if (',' == ch)
/*      */         {
/*  948 */           state = CsvSplitState.PRE_DATA;
/*      */         }
/*      */         
/*      */         break;
/*      */       }
/*      */       
/*      */     }
/*  955 */     switch (state)
/*      */     {
/*      */     case PRE_DATA: 
/*      */     case POST_DATA: 
/*      */       break;
/*      */     
/*      */     case DATA: 
/*      */     case QUOTE: 
/*      */     case SLOSH: 
/*  964 */       list.add(out.toString());
/*  965 */       break;
/*      */     
/*      */     case WHITE: 
/*  968 */       out.setLength(last);
/*  969 */       list.add(out.toString());
/*      */     }
/*      */     
/*      */     
/*  973 */     return list;
/*      */   }
/*      */   
/*      */   public static String sanitizeXmlString(String html)
/*      */   {
/*  978 */     if (html == null) {
/*  979 */       return null;
/*      */     }
/*  981 */     for (int i = 0; 
/*      */         
/*      */ 
/*  984 */         i < html.length(); i++)
/*      */     {
/*  986 */       char c = html.charAt(i);
/*      */       
/*  988 */       switch (c)
/*      */       {
/*      */       case '"': 
/*      */       case '&': 
/*      */       case '\'': 
/*      */       case '<': 
/*      */       case '>': 
/*      */         break;
/*      */       
/*      */       default: 
/*  998 */         if ((Character.isISOControl(c)) && (!Character.isWhitespace(c))) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */     }
/* 1004 */     if (i == html.length()) {
/* 1005 */       return html;
/*      */     }
/*      */     
/* 1008 */     StringBuilder out = new StringBuilder(html.length() * 4 / 3);
/* 1009 */     out.append(html, 0, i);
/* 1012 */     for (; 
/*      */         
/* 1012 */         i < html.length(); i++)
/*      */     {
/* 1014 */       char c = html.charAt(i);
/*      */       
/* 1016 */       switch (c)
/*      */       {
/*      */       case '&': 
/* 1019 */         out.append("&amp;");
/* 1020 */         break;
/*      */       case '<': 
/* 1022 */         out.append("&lt;");
/* 1023 */         break;
/*      */       case '>': 
/* 1025 */         out.append("&gt;");
/* 1026 */         break;
/*      */       case '\'': 
/* 1028 */         out.append("&apos;");
/* 1029 */         break;
/*      */       case '"': 
/* 1031 */         out.append("&quot;");
/* 1032 */         break;
/*      */       
/*      */       default: 
/* 1035 */         if ((Character.isISOControl(c)) && (!Character.isWhitespace(c))) {
/* 1036 */           out.append('?');
/*      */         } else
/* 1038 */           out.append(c);
/*      */         break; }
/*      */     }
/* 1041 */     return out.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String valueOf(Object object)
/*      */   {
/* 1053 */     return object == null ? null : String.valueOf(object);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\StringUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */