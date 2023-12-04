/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.IllegalCharsetNameException;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumSet;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.concurrent.ConcurrentHashMap;
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
/*      */ public class StringUtils
/*      */ {
/*      */   public static enum SearchMode
/*      */   {
/*   52 */     ALLOW_BACKSLASH_ESCAPE,  SKIP_BETWEEN_MARKERS,  SKIP_BLOCK_COMMENTS,  SKIP_LINE_COMMENTS,  SKIP_WHITE_SPACE;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private SearchMode() {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*   62 */   public static final Set<SearchMode> SEARCH_MODE__ALL = Collections.unmodifiableSet(EnumSet.allOf(SearchMode.class));
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   67 */   public static final Set<SearchMode> SEARCH_MODE__MRK_COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   73 */   public static final Set<SearchMode> SEARCH_MODE__BSESC_COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.ALLOW_BACKSLASH_ESCAPE, SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   79 */   public static final Set<SearchMode> SEARCH_MODE__BSESC_MRK_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.ALLOW_BACKSLASH_ESCAPE, SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_WHITE_SPACE));
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   85 */   public static final Set<SearchMode> SEARCH_MODE__COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   91 */   public static final Set<SearchMode> SEARCH_MODE__MRK_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_WHITE_SPACE));
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   97 */   public static final Set<SearchMode> SEARCH_MODE__NONE = Collections.unmodifiableSet(EnumSet.noneOf(SearchMode.class));
/*      */   
/*      */ 
/*      */   private static final int NON_COMMENTS_MYSQL_VERSION_REF_LENGTH = 5;
/*      */   
/*      */   private static final int BYTE_RANGE = 256;
/*      */   
/*  104 */   private static byte[] allBytes = new byte['Ā'];
/*      */   
/*  106 */   private static char[] byteToChars = new char['Ā'];
/*      */   
/*      */   private static Method toPlainStringMethod;
/*      */   
/*      */   static final int WILD_COMPARE_MATCH_NO_WILD = 0;
/*      */   
/*      */   static final int WILD_COMPARE_MATCH_WITH_WILD = 1;
/*      */   
/*      */   static final int WILD_COMPARE_NO_MATCH = -1;
/*      */   
/*  116 */   private static final ConcurrentHashMap<String, Charset> charsetsByAlias = new ConcurrentHashMap();
/*      */   
/*  118 */   private static final String platformEncoding = System.getProperty("file.encoding");
/*      */   private static final String VALID_ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@";
/*      */   
/*      */   static Charset findCharset(String alias) throws UnsupportedEncodingException
/*      */   {
/*      */     try {
/*  124 */       Charset cs = (Charset)charsetsByAlias.get(alias);
/*      */       Charset oldCs;
/*  126 */       if (cs == null) {
/*  127 */         cs = Charset.forName(alias);
/*  128 */         oldCs = (Charset)charsetsByAlias.putIfAbsent(alias, cs);
/*  129 */         if (oldCs == null) {}
/*      */       }
/*  131 */       return oldCs;
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (UnsupportedCharsetException uce)
/*      */     {
/*      */ 
/*      */ 
/*  139 */       throw new UnsupportedEncodingException(alias);
/*      */     } catch (IllegalCharsetNameException icne) {
/*  141 */       throw new UnsupportedEncodingException(alias);
/*      */     } catch (IllegalArgumentException iae) {
/*  143 */       throw new UnsupportedEncodingException(alias);
/*      */     }
/*      */   }
/*      */   
/*      */   static {
/*  148 */     for (int i = -128; i <= 127; i++) {
/*  149 */       allBytes[(i - -128)] = ((byte)i);
/*      */     }
/*      */     
/*  152 */     String allBytesString = new String(allBytes, 0, 255);
/*      */     
/*  154 */     int allBytesStringLen = allBytesString.length();
/*      */     
/*  156 */     for (int i = 0; (i < 255) && (i < allBytesStringLen); i++) {
/*  157 */       byteToChars[i] = allBytesString.charAt(i);
/*      */     }
/*      */     try
/*      */     {
/*  161 */       toPlainStringMethod = BigDecimal.class.getMethod("toPlainString", new Class[0]);
/*      */     }
/*      */     catch (NoSuchMethodException nsme) {}
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
/*      */   public static String consistentToString(BigDecimal decimal)
/*      */   {
/*  177 */     if (decimal == null) {
/*  178 */       return null;
/*      */     }
/*      */     
/*  181 */     if (toPlainStringMethod != null) {
/*      */       try {
/*  183 */         return (String)toPlainStringMethod.invoke(decimal, (Object[])null);
/*      */       }
/*      */       catch (InvocationTargetException invokeEx) {}catch (IllegalAccessException accessEx) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  191 */     return decimal.toString();
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
/*      */   public static String dumpAsHex(byte[] byteBuffer, int length)
/*      */   {
/*  205 */     StringBuilder outputBuilder = new StringBuilder(length * 4);
/*      */     
/*  207 */     int p = 0;
/*  208 */     int rows = length / 8;
/*      */     
/*  210 */     for (int i = 0; (i < rows) && (p < length); i++) {
/*  211 */       int ptemp = p;
/*      */       
/*  213 */       for (int j = 0; j < 8; j++) {
/*  214 */         String hexVal = Integer.toHexString(byteBuffer[ptemp] & 0xFF);
/*      */         
/*  216 */         if (hexVal.length() == 1) {
/*  217 */           hexVal = "0" + hexVal;
/*      */         }
/*      */         
/*  220 */         outputBuilder.append(hexVal + " ");
/*  221 */         ptemp++;
/*      */       }
/*      */       
/*  224 */       outputBuilder.append("    ");
/*      */       
/*  226 */       for (int j = 0; j < 8; j++) {
/*  227 */         int b = 0xFF & byteBuffer[p];
/*      */         
/*  229 */         if ((b > 32) && (b < 127)) {
/*  230 */           outputBuilder.append((char)b + " ");
/*      */         } else {
/*  232 */           outputBuilder.append(". ");
/*      */         }
/*      */         
/*  235 */         p++;
/*      */       }
/*      */       
/*  238 */       outputBuilder.append("\n");
/*      */     }
/*      */     
/*  241 */     int n = 0;
/*      */     
/*  243 */     for (int i = p; i < length; i++) {
/*  244 */       String hexVal = Integer.toHexString(byteBuffer[i] & 0xFF);
/*      */       
/*  246 */       if (hexVal.length() == 1) {
/*  247 */         hexVal = "0" + hexVal;
/*      */       }
/*      */       
/*  250 */       outputBuilder.append(hexVal + " ");
/*  251 */       n++;
/*      */     }
/*      */     
/*  254 */     for (int i = n; i < 8; i++) {
/*  255 */       outputBuilder.append("   ");
/*      */     }
/*      */     
/*  258 */     outputBuilder.append("    ");
/*      */     
/*  260 */     for (int i = p; i < length; i++) {
/*  261 */       int b = 0xFF & byteBuffer[i];
/*      */       
/*  263 */       if ((b > 32) && (b < 127)) {
/*  264 */         outputBuilder.append((char)b + " ");
/*      */       } else {
/*  266 */         outputBuilder.append(". ");
/*      */       }
/*      */     }
/*      */     
/*  270 */     outputBuilder.append("\n");
/*      */     
/*  272 */     return outputBuilder.toString();
/*      */   }
/*      */   
/*      */   private static boolean endsWith(byte[] dataFrom, String suffix) {
/*  276 */     for (int i = 1; i <= suffix.length(); i++) {
/*  277 */       int dfOffset = dataFrom.length - i;
/*  278 */       int suffixOffset = suffix.length() - i;
/*  279 */       if (dataFrom[dfOffset] != suffix.charAt(suffixOffset)) {
/*  280 */         return false;
/*      */       }
/*      */     }
/*  283 */     return true;
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
/*      */   public static byte[] escapeEasternUnicodeByteStream(byte[] origBytes, String origString)
/*      */   {
/*  298 */     if (origBytes == null) {
/*  299 */       return null;
/*      */     }
/*  301 */     if (origBytes.length == 0) {
/*  302 */       return new byte[0];
/*      */     }
/*      */     
/*  305 */     int bytesLen = origBytes.length;
/*  306 */     int bufIndex = 0;
/*  307 */     int strIndex = 0;
/*      */     
/*  309 */     ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(bytesLen);
/*      */     for (;;)
/*      */     {
/*  312 */       if (origString.charAt(strIndex) == '\\')
/*      */       {
/*  314 */         bytesOut.write(origBytes[(bufIndex++)]);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  319 */         int loByte = origBytes[bufIndex];
/*      */         
/*  321 */         if (loByte < 0) {
/*  322 */           loByte += 256;
/*      */         }
/*      */         
/*      */ 
/*  326 */         bytesOut.write(loByte);
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
/*  344 */         if (loByte >= 128) {
/*  345 */           if (bufIndex < bytesLen - 1) {
/*  346 */             int hiByte = origBytes[(bufIndex + 1)];
/*      */             
/*  348 */             if (hiByte < 0) {
/*  349 */               hiByte += 256;
/*      */             }
/*      */             
/*      */ 
/*  353 */             bytesOut.write(hiByte);
/*  354 */             bufIndex++;
/*      */             
/*      */ 
/*  357 */             if (hiByte == 92) {
/*  358 */               bytesOut.write(hiByte);
/*      */             }
/*      */           }
/*  361 */         } else if ((loByte == 92) && 
/*  362 */           (bufIndex < bytesLen - 1)) {
/*  363 */           int hiByte = origBytes[(bufIndex + 1)];
/*      */           
/*  365 */           if (hiByte < 0) {
/*  366 */             hiByte += 256;
/*      */           }
/*      */           
/*  369 */           if (hiByte == 98)
/*      */           {
/*  371 */             bytesOut.write(92);
/*  372 */             bytesOut.write(98);
/*  373 */             bufIndex++;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  378 */         bufIndex++;
/*      */       }
/*      */       
/*  381 */       if (bufIndex >= bytesLen) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/*  386 */       strIndex++;
/*      */     }
/*      */     
/*  389 */     return bytesOut.toByteArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static char firstNonWsCharUc(String searchIn)
/*      */   {
/*  401 */     return firstNonWsCharUc(searchIn, 0);
/*      */   }
/*      */   
/*      */   public static char firstNonWsCharUc(String searchIn, int startAt) {
/*  405 */     if (searchIn == null) {
/*  406 */       return '\000';
/*      */     }
/*      */     
/*  409 */     int length = searchIn.length();
/*      */     
/*  411 */     for (int i = startAt; i < length; i++) {
/*  412 */       char c = searchIn.charAt(i);
/*      */       
/*  414 */       if (!Character.isWhitespace(c)) {
/*  415 */         return Character.toUpperCase(c);
/*      */       }
/*      */     }
/*      */     
/*  419 */     return '\000';
/*      */   }
/*      */   
/*      */   public static char firstAlphaCharUc(String searchIn, int startAt) {
/*  423 */     if (searchIn == null) {
/*  424 */       return '\000';
/*      */     }
/*      */     
/*  427 */     int length = searchIn.length();
/*      */     
/*  429 */     for (int i = startAt; i < length; i++) {
/*  430 */       char c = searchIn.charAt(i);
/*      */       
/*  432 */       if (Character.isLetter(c)) {
/*  433 */         return Character.toUpperCase(c);
/*      */       }
/*      */     }
/*      */     
/*  437 */     return '\000';
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
/*      */   public static String fixDecimalExponent(String dString)
/*      */   {
/*  450 */     int ePos = dString.indexOf('E');
/*      */     
/*  452 */     if (ePos == -1) {
/*  453 */       ePos = dString.indexOf('e');
/*      */     }
/*      */     
/*  456 */     if ((ePos != -1) && 
/*  457 */       (dString.length() > ePos + 1)) {
/*  458 */       char maybeMinusChar = dString.charAt(ePos + 1);
/*      */       
/*  460 */       if ((maybeMinusChar != '-') && (maybeMinusChar != '+')) {
/*  461 */         StringBuilder strBuilder = new StringBuilder(dString.length() + 1);
/*  462 */         strBuilder.append(dString.substring(0, ePos + 1));
/*  463 */         strBuilder.append('+');
/*  464 */         strBuilder.append(dString.substring(ePos + 1, dString.length()));
/*  465 */         dString = strBuilder.toString();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  470 */     return dString;
/*      */   }
/*      */   
/*      */ 
/*      */   public static byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       byte[] b;
/*      */       
/*      */       byte[] b;
/*  482 */       if (converter != null) {
/*  483 */         b = converter.toBytes(c); } else { byte[] b;
/*  484 */         if (encoding == null) {
/*  485 */           b = getBytes(c);
/*      */         } else {
/*  487 */           b = getBytes(c, encoding);
/*      */           
/*  489 */           if ((!parserKnowsUnicode) && (CharsetMapping.requiresEscapeEasternUnicode(encoding)))
/*      */           {
/*  491 */             if (encoding.equalsIgnoreCase(serverEncoding)) {} } } }
/*  492 */       return escapeEasternUnicodeByteStream(b, new String(c));
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (UnsupportedEncodingException uee)
/*      */     {
/*      */ 
/*  499 */       throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       byte[] b;
/*      */       
/*      */       byte[] b;
/*      */       
/*  513 */       if (converter != null) {
/*  514 */         b = converter.toBytes(c, offset, length); } else { byte[] b;
/*  515 */         if (encoding == null) {
/*  516 */           b = getBytes(c, offset, length);
/*      */         } else {
/*  518 */           b = getBytes(c, offset, length, encoding);
/*      */           
/*  520 */           if ((!parserKnowsUnicode) && (CharsetMapping.requiresEscapeEasternUnicode(encoding)))
/*      */           {
/*  522 */             if (encoding.equalsIgnoreCase(serverEncoding)) {} } } }
/*  523 */       return escapeEasternUnicodeByteStream(b, new String(c, offset, length));
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (UnsupportedEncodingException uee)
/*      */     {
/*      */ 
/*  530 */       throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] getBytes(char[] c, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  542 */       SingleByteCharsetConverter converter = conn != null ? conn.getCharsetConverter(encoding) : SingleByteCharsetConverter.getInstance(encoding, null);
/*      */       
/*  544 */       return getBytes(c, converter, encoding, serverEncoding, parserKnowsUnicode, exceptionInterceptor);
/*      */     } catch (UnsupportedEncodingException uee) {
/*  546 */       throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       byte[] b;
/*      */       
/*      */       byte[] b;
/*      */       
/*  560 */       if (converter != null) {
/*  561 */         b = converter.toBytes(s); } else { byte[] b;
/*  562 */         if (encoding == null) {
/*  563 */           b = getBytes(s);
/*      */         } else {
/*  565 */           b = getBytes(s, encoding);
/*      */           
/*  567 */           if ((!parserKnowsUnicode) && (CharsetMapping.requiresEscapeEasternUnicode(encoding)))
/*      */           {
/*  569 */             if (encoding.equalsIgnoreCase(serverEncoding)) {} } } }
/*  570 */       return escapeEasternUnicodeByteStream(b, s);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (UnsupportedEncodingException uee)
/*      */     {
/*      */ 
/*  577 */       throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       byte[] b;
/*      */       
/*      */       byte[] b;
/*      */       
/*  591 */       if (converter != null) {
/*  592 */         b = converter.toBytes(s, offset, length); } else { byte[] b;
/*  593 */         if (encoding == null) {
/*  594 */           b = getBytes(s, offset, length);
/*      */         } else {
/*  596 */           s = s.substring(offset, offset + length);
/*  597 */           b = getBytes(s, encoding);
/*      */           
/*  599 */           if ((!parserKnowsUnicode) && (CharsetMapping.requiresEscapeEasternUnicode(encoding)))
/*      */           {
/*  601 */             if (encoding.equalsIgnoreCase(serverEncoding)) {} } } }
/*  602 */       return escapeEasternUnicodeByteStream(b, s);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (UnsupportedEncodingException uee)
/*      */     {
/*      */ 
/*  609 */       throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] getBytes(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  621 */       SingleByteCharsetConverter converter = conn != null ? conn.getCharsetConverter(encoding) : SingleByteCharsetConverter.getInstance(encoding, null);
/*      */       
/*  623 */       return getBytes(s, converter, encoding, serverEncoding, parserKnowsUnicode, exceptionInterceptor);
/*      */     } catch (UnsupportedEncodingException uee) {
/*  625 */       throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final byte[] getBytes(String s, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  637 */       SingleByteCharsetConverter converter = conn != null ? conn.getCharsetConverter(encoding) : SingleByteCharsetConverter.getInstance(encoding, null);
/*      */       
/*  639 */       return getBytes(s, converter, encoding, serverEncoding, offset, length, parserKnowsUnicode, exceptionInterceptor);
/*      */     } catch (UnsupportedEncodingException uee) {
/*  641 */       throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static byte[] getBytesWrapped(String s, char beginWrap, char endWrap, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       byte[] b;
/*      */       
/*      */       byte[] b;
/*      */       
/*  655 */       if (converter != null) {
/*  656 */         b = converter.toBytesWrapped(s, beginWrap, endWrap); } else { byte[] b;
/*  657 */         if (encoding == null) {
/*  658 */           StringBuilder strBuilder = new StringBuilder(s.length() + 2);
/*  659 */           strBuilder.append(beginWrap);
/*  660 */           strBuilder.append(s);
/*  661 */           strBuilder.append(endWrap);
/*      */           
/*  663 */           b = getBytes(strBuilder.toString());
/*      */         } else {
/*  665 */           StringBuilder strBuilder = new StringBuilder(s.length() + 2);
/*  666 */           strBuilder.append(beginWrap);
/*  667 */           strBuilder.append(s);
/*  668 */           strBuilder.append(endWrap);
/*      */           
/*  670 */           s = strBuilder.toString();
/*  671 */           b = getBytes(s, encoding);
/*      */           
/*  673 */           if ((!parserKnowsUnicode) && (CharsetMapping.requiresEscapeEasternUnicode(encoding)))
/*      */           {
/*  675 */             if (encoding.equalsIgnoreCase(serverEncoding)) {} } } }
/*  676 */       return escapeEasternUnicodeByteStream(b, s);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (UnsupportedEncodingException uee)
/*      */     {
/*      */ 
/*  683 */       throw SQLError.createSQLException(Messages.getString("StringUtils.10") + encoding + Messages.getString("StringUtils.11"), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */   public static int getInt(byte[] buf) throws NumberFormatException
/*      */   {
/*  689 */     return getInt(buf, 0, buf.length);
/*      */   }
/*      */   
/*      */   public static int getInt(byte[] buf, int offset, int endPos) throws NumberFormatException {
/*  693 */     int base = 10;
/*      */     
/*  695 */     int s = offset;
/*      */     
/*      */ 
/*  698 */     while ((s < endPos) && (Character.isWhitespace((char)buf[s]))) {
/*  699 */       s++;
/*      */     }
/*      */     
/*  702 */     if (s == endPos) {
/*  703 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*      */ 
/*  707 */     boolean negative = false;
/*      */     
/*  709 */     if ((char)buf[s] == '-') {
/*  710 */       negative = true;
/*  711 */       s++;
/*  712 */     } else if ((char)buf[s] == '+') {
/*  713 */       s++;
/*      */     }
/*      */     
/*      */ 
/*  717 */     int save = s;
/*      */     
/*  719 */     int cutoff = Integer.MAX_VALUE / base;
/*  720 */     int cutlim = Integer.MAX_VALUE % base;
/*      */     
/*  722 */     if (negative) {
/*  723 */       cutlim++;
/*      */     }
/*      */     
/*  726 */     boolean overflow = false;
/*      */     
/*  728 */     int i = 0;
/*  730 */     for (; 
/*  730 */         s < endPos; s++) {
/*  731 */       char c = (char)buf[s];
/*      */       
/*  733 */       if (Character.isDigit(c)) {
/*  734 */         c = (char)(c - '0');
/*  735 */       } else { if (!Character.isLetter(c)) break;
/*  736 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  741 */       if (c >= base) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/*  746 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  747 */         overflow = true;
/*      */       } else {
/*  749 */         i *= base;
/*  750 */         i += c;
/*      */       }
/*      */     }
/*      */     
/*  754 */     if (s == save) {
/*  755 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*  758 */     if (overflow) {
/*  759 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*      */ 
/*  763 */     return negative ? -i : i;
/*      */   }
/*      */   
/*      */   public static long getLong(byte[] buf) throws NumberFormatException {
/*  767 */     return getLong(buf, 0, buf.length);
/*      */   }
/*      */   
/*      */   public static long getLong(byte[] buf, int offset, int endpos) throws NumberFormatException {
/*  771 */     int base = 10;
/*      */     
/*  773 */     int s = offset;
/*      */     
/*      */ 
/*  776 */     while ((s < endpos) && (Character.isWhitespace((char)buf[s]))) {
/*  777 */       s++;
/*      */     }
/*      */     
/*  780 */     if (s == endpos) {
/*  781 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*      */ 
/*  785 */     boolean negative = false;
/*      */     
/*  787 */     if ((char)buf[s] == '-') {
/*  788 */       negative = true;
/*  789 */       s++;
/*  790 */     } else if ((char)buf[s] == '+') {
/*  791 */       s++;
/*      */     }
/*      */     
/*      */ 
/*  795 */     int save = s;
/*      */     
/*  797 */     long cutoff = Long.MAX_VALUE / base;
/*  798 */     long cutlim = (int)(Long.MAX_VALUE % base);
/*      */     
/*  800 */     if (negative) {
/*  801 */       cutlim += 1L;
/*      */     }
/*      */     
/*  804 */     boolean overflow = false;
/*  805 */     long i = 0L;
/*  807 */     for (; 
/*  807 */         s < endpos; s++) {
/*  808 */       char c = (char)buf[s];
/*      */       
/*  810 */       if (Character.isDigit(c)) {
/*  811 */         c = (char)(c - '0');
/*  812 */       } else { if (!Character.isLetter(c)) break;
/*  813 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  818 */       if (c >= base) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/*  823 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  824 */         overflow = true;
/*      */       } else {
/*  826 */         i *= base;
/*  827 */         i += c;
/*      */       }
/*      */     }
/*      */     
/*  831 */     if (s == save) {
/*  832 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*  835 */     if (overflow) {
/*  836 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*      */ 
/*  840 */     return negative ? -i : i;
/*      */   }
/*      */   
/*      */   public static short getShort(byte[] buf) throws NumberFormatException {
/*  844 */     return getShort(buf, 0, buf.length);
/*      */   }
/*      */   
/*      */   public static short getShort(byte[] buf, int offset, int endpos) throws NumberFormatException {
/*  848 */     short base = 10;
/*      */     
/*  850 */     int s = offset;
/*      */     
/*      */ 
/*  853 */     while ((s < endpos) && (Character.isWhitespace((char)buf[s]))) {
/*  854 */       s++;
/*      */     }
/*      */     
/*  857 */     if (s == endpos) {
/*  858 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*      */ 
/*  862 */     boolean negative = false;
/*      */     
/*  864 */     if ((char)buf[s] == '-') {
/*  865 */       negative = true;
/*  866 */       s++;
/*  867 */     } else if ((char)buf[s] == '+') {
/*  868 */       s++;
/*      */     }
/*      */     
/*      */ 
/*  872 */     int save = s;
/*      */     
/*  874 */     short cutoff = (short)(Short.MAX_VALUE / base);
/*  875 */     short cutlim = (short)(Short.MAX_VALUE % base);
/*      */     
/*  877 */     if (negative) {
/*  878 */       cutlim = (short)(cutlim + 1);
/*      */     }
/*      */     
/*  881 */     boolean overflow = false;
/*  882 */     short i = 0;
/*  884 */     for (; 
/*  884 */         s < endpos; s++) {
/*  885 */       char c = (char)buf[s];
/*      */       
/*  887 */       if (Character.isDigit(c)) {
/*  888 */         c = (char)(c - '0');
/*  889 */       } else { if (!Character.isLetter(c)) break;
/*  890 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  895 */       if (c >= base) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/*  900 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  901 */         overflow = true;
/*      */       } else {
/*  903 */         i = (short)(i * base);
/*  904 */         i = (short)(i + c);
/*      */       }
/*      */     }
/*      */     
/*  908 */     if (s == save) {
/*  909 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*  912 */     if (overflow) {
/*  913 */       throw new NumberFormatException(toString(buf));
/*      */     }
/*      */     
/*      */ 
/*  917 */     return negative ? (short)-i : i;
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
/*      */   public static int indexOfIgnoreCase(String searchIn, String searchFor)
/*      */   {
/*  930 */     return indexOfIgnoreCase(0, searchIn, searchFor);
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
/*      */   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor)
/*      */   {
/*  945 */     if ((searchIn == null) || (searchFor == null)) {
/*  946 */       return -1;
/*      */     }
/*      */     
/*  949 */     int searchInLength = searchIn.length();
/*  950 */     int searchForLength = searchFor.length();
/*  951 */     int stopSearchingAt = searchInLength - searchForLength;
/*      */     
/*  953 */     if ((startingPosition > stopSearchingAt) || (searchForLength == 0)) {
/*  954 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*  958 */     char firstCharOfSearchForUc = Character.toUpperCase(searchFor.charAt(0));
/*  959 */     char firstCharOfSearchForLc = Character.toLowerCase(searchFor.charAt(0));
/*      */     
/*  961 */     for (int i = startingPosition; i <= stopSearchingAt; i++) {
/*  962 */       if (isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc)) {
/*      */         do {
/*  964 */           i++; } while ((i <= stopSearchingAt) && (isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc)));
/*      */       }
/*      */       
/*      */ 
/*  968 */       if ((i <= stopSearchingAt) && (startsWithIgnoreCase(searchIn, i, searchFor))) {
/*  969 */         return i;
/*      */       }
/*      */     }
/*      */     
/*  973 */     return -1;
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
/*      */   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String[] searchForSequence, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode)
/*      */   {
/* 1001 */     if ((searchIn == null) || (searchForSequence == null)) {
/* 1002 */       return -1;
/*      */     }
/*      */     
/* 1005 */     int searchInLength = searchIn.length();
/* 1006 */     int searchForLength = 0;
/* 1007 */     for (String searchForPart : searchForSequence) {
/* 1008 */       searchForLength += searchForPart.length();
/*      */     }
/*      */     
/* 1011 */     if (searchForLength == 0) {
/* 1012 */       return -1;
/*      */     }
/*      */     
/* 1015 */     int searchForWordsCount = searchForSequence.length;
/* 1016 */     searchForLength += (searchForWordsCount > 0 ? searchForWordsCount - 1 : 0);
/* 1017 */     int stopSearchingAt = searchInLength - searchForLength;
/*      */     
/* 1019 */     if (startingPosition > stopSearchingAt) {
/* 1020 */       return -1;
/*      */     }
/*      */     
/* 1023 */     if ((searchMode.contains(SearchMode.SKIP_BETWEEN_MARKERS)) && ((openingMarkers == null) || (closingMarkers == null) || (openingMarkers.length() != closingMarkers.length())))
/*      */     {
/* 1025 */       throw new IllegalArgumentException(Messages.getString("StringUtils.15", new String[] { openingMarkers, closingMarkers }));
/*      */     }
/*      */     
/* 1028 */     if ((Character.isWhitespace(searchForSequence[0].charAt(0))) && (searchMode.contains(SearchMode.SKIP_WHITE_SPACE)))
/*      */     {
/* 1030 */       searchMode = EnumSet.copyOf(searchMode);
/* 1031 */       searchMode.remove(SearchMode.SKIP_WHITE_SPACE);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1036 */     Set<SearchMode> searchMode2 = EnumSet.of(SearchMode.SKIP_WHITE_SPACE);
/* 1037 */     searchMode2.addAll(searchMode);
/* 1038 */     searchMode2.remove(SearchMode.SKIP_BETWEEN_MARKERS);
/*      */     
/* 1040 */     for (int positionOfFirstWord = startingPosition; positionOfFirstWord <= stopSearchingAt; positionOfFirstWord++) {
/* 1041 */       positionOfFirstWord = indexOfIgnoreCase(positionOfFirstWord, searchIn, searchForSequence[0], openingMarkers, closingMarkers, searchMode);
/*      */       
/* 1043 */       if ((positionOfFirstWord == -1) || (positionOfFirstWord > stopSearchingAt)) {
/* 1044 */         return -1;
/*      */       }
/*      */       
/* 1047 */       int startingPositionForNextWord = positionOfFirstWord + searchForSequence[0].length();
/* 1048 */       int wc = 0;
/* 1049 */       boolean match = true;
/* 1050 */       for (;;) { wc++; if ((wc >= searchForWordsCount) || (!match)) break;
/* 1051 */         int positionOfNextWord = indexOfNextChar(startingPositionForNextWord, searchInLength - 1, searchIn, null, null, searchMode2);
/* 1052 */         if ((startingPositionForNextWord == positionOfNextWord) || (!startsWithIgnoreCase(searchIn, positionOfNextWord, searchForSequence[wc])))
/*      */         {
/* 1054 */           match = false;
/*      */         } else {
/* 1056 */           startingPositionForNextWord = positionOfNextWord + searchForSequence[wc].length();
/*      */         }
/*      */       }
/*      */       
/* 1060 */       if (match) {
/* 1061 */         return positionOfFirstWord;
/*      */       }
/*      */     }
/*      */     
/* 1065 */     return -1;
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
/*      */   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode)
/*      */   {
/* 1088 */     if ((searchIn == null) || (searchFor == null)) {
/* 1089 */       return -1;
/*      */     }
/*      */     
/* 1092 */     int searchInLength = searchIn.length();
/* 1093 */     int searchForLength = searchFor.length();
/* 1094 */     int stopSearchingAt = searchInLength - searchForLength;
/*      */     
/* 1096 */     if ((startingPosition > stopSearchingAt) || (searchForLength == 0)) {
/* 1097 */       return -1;
/*      */     }
/*      */     
/* 1100 */     if ((searchMode.contains(SearchMode.SKIP_BETWEEN_MARKERS)) && ((openingMarkers == null) || (closingMarkers == null) || (openingMarkers.length() != closingMarkers.length())))
/*      */     {
/* 1102 */       throw new IllegalArgumentException(Messages.getString("StringUtils.15", new String[] { openingMarkers, closingMarkers }));
/*      */     }
/*      */     
/*      */ 
/* 1106 */     char firstCharOfSearchForUc = Character.toUpperCase(searchFor.charAt(0));
/* 1107 */     char firstCharOfSearchForLc = Character.toLowerCase(searchFor.charAt(0));
/*      */     
/* 1109 */     if ((Character.isWhitespace(firstCharOfSearchForLc)) && (searchMode.contains(SearchMode.SKIP_WHITE_SPACE)))
/*      */     {
/* 1111 */       searchMode = EnumSet.copyOf(searchMode);
/* 1112 */       searchMode.remove(SearchMode.SKIP_WHITE_SPACE);
/*      */     }
/*      */     
/* 1115 */     for (int i = startingPosition; i <= stopSearchingAt; i++) {
/* 1116 */       i = indexOfNextChar(i, stopSearchingAt, searchIn, openingMarkers, closingMarkers, searchMode);
/*      */       
/* 1118 */       if (i == -1) {
/* 1119 */         return -1;
/*      */       }
/*      */       
/* 1122 */       char c = searchIn.charAt(i);
/*      */       
/* 1124 */       if ((isCharEqualIgnoreCase(c, firstCharOfSearchForUc, firstCharOfSearchForLc)) && (startsWithIgnoreCase(searchIn, i, searchFor))) {
/* 1125 */         return i;
/*      */       }
/*      */     }
/*      */     
/* 1129 */     return -1;
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
/*      */   private static int indexOfNextChar(int startingPosition, int stopPosition, String searchIn, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode)
/*      */   {
/* 1152 */     if (searchIn == null) {
/* 1153 */       return -1;
/*      */     }
/*      */     
/* 1156 */     int searchInLength = searchIn.length();
/*      */     
/* 1158 */     if (startingPosition >= searchInLength) {
/* 1159 */       return -1;
/*      */     }
/*      */     
/* 1162 */     char c0 = '\000';
/* 1163 */     char c1 = searchIn.charAt(startingPosition);
/* 1164 */     char c2 = startingPosition + 1 < searchInLength ? searchIn.charAt(startingPosition + 1) : '\000';
/*      */     
/* 1166 */     for (int i = startingPosition; i <= stopPosition; i++) {
/* 1167 */       c0 = c1;
/* 1168 */       c1 = c2;
/* 1169 */       c2 = i + 2 < searchInLength ? searchIn.charAt(i + 2) : '\000';
/*      */       
/* 1171 */       boolean dashDashCommentImmediateEnd = false;
/* 1172 */       int markerIndex = -1;
/*      */       
/* 1174 */       if ((searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE)) && (c0 == '\\')) {
/* 1175 */         i++;
/*      */         
/* 1177 */         c1 = c2;
/* 1178 */         c2 = i + 2 < searchInLength ? searchIn.charAt(i + 2) : '\000';
/*      */       }
/* 1180 */       else if ((searchMode.contains(SearchMode.SKIP_BETWEEN_MARKERS)) && ((markerIndex = openingMarkers.indexOf(c0)) != -1))
/*      */       {
/* 1182 */         int nestedMarkersCount = 0;
/* 1183 */         char openingMarker = c0;
/* 1184 */         char closingMarker = closingMarkers.charAt(markerIndex);
/* 1185 */         for (;;) { i++; if ((i > stopPosition) || (((c0 = searchIn.charAt(i)) == closingMarker) && (nestedMarkersCount == 0))) break;
/* 1186 */           if (c0 == openingMarker) {
/* 1187 */             nestedMarkersCount++;
/* 1188 */           } else if (c0 == closingMarker) {
/* 1189 */             nestedMarkersCount--;
/* 1190 */           } else if ((searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE)) && (c0 == '\\')) {
/* 1191 */             i++;
/*      */           }
/*      */         }
/*      */         
/* 1195 */         c1 = i + 1 < searchInLength ? searchIn.charAt(i + 1) : '\000';
/* 1196 */         c2 = i + 2 < searchInLength ? searchIn.charAt(i + 2) : '\000';
/*      */       }
/* 1198 */       else if ((searchMode.contains(SearchMode.SKIP_BLOCK_COMMENTS)) && (c0 == '/') && (c1 == '*')) {
/* 1199 */         if (c2 != '!')
/*      */         {
/* 1201 */           i++;
/*      */           do {
/* 1203 */             do { i++; if (i > stopPosition) break; } while (searchIn.charAt(i) != '*'); } while ((i + 1 < searchInLength ? searchIn.charAt(i + 1) : 0) != 47);
/*      */           
/*      */ 
/* 1206 */           i++;
/*      */         }
/*      */         else
/*      */         {
/* 1210 */           i++;
/* 1211 */           i++;
/*      */           
/* 1213 */           for (int j = 1; 
/* 1214 */               j <= 5; j++) {
/* 1215 */             if ((i + j >= searchInLength) || (!Character.isDigit(searchIn.charAt(i + j)))) {
/*      */               break;
/*      */             }
/*      */           }
/* 1219 */           if (j == 5) {
/* 1220 */             i += 5;
/*      */           }
/*      */         }
/*      */         
/* 1224 */         c1 = i + 1 < searchInLength ? searchIn.charAt(i + 1) : '\000';
/* 1225 */         c2 = i + 2 < searchInLength ? searchIn.charAt(i + 2) : '\000';
/*      */       }
/* 1227 */       else if ((searchMode.contains(SearchMode.SKIP_BLOCK_COMMENTS)) && (c0 == '*') && (c1 == '/'))
/*      */       {
/*      */ 
/* 1230 */         i++;
/*      */         
/* 1232 */         c1 = c2;
/* 1233 */         c2 = i + 2 < searchInLength ? searchIn.charAt(i + 2) : '\000';
/*      */       } else {
/* 1235 */         if (searchMode.contains(SearchMode.SKIP_LINE_COMMENTS)) { if ((c0 == '-') && (c1 == '-')) { if (!Character.isWhitespace(c2)) if (((dashDashCommentImmediateEnd = c2 == ';' ? 1 : 0) != 0) || (c2 == 0)) {} } else if (c0 != '#') {
/*      */               break label827;
/*      */             }
/* 1238 */           if (dashDashCommentImmediateEnd)
/*      */           {
/* 1240 */             i++;
/* 1241 */             i++;
/*      */             
/* 1243 */             c1 = i + 1 < searchInLength ? searchIn.charAt(i + 1) : '\000';
/* 1244 */             c2 = i + 2 < searchInLength ? searchIn.charAt(i + 2) : '\000'; continue;
/*      */           }
/*      */           do {
/* 1247 */             i++; } while ((i <= stopPosition) && ((c0 = searchIn.charAt(i)) != '\n') && (c0 != '\r'));
/*      */           
/*      */ 
/*      */ 
/* 1251 */           c1 = i + 1 < searchInLength ? searchIn.charAt(i + 1) : '\000';
/* 1252 */           if ((c0 == '\r') && (c1 == '\n'))
/*      */           {
/* 1254 */             i++;
/* 1255 */             c1 = i + 1 < searchInLength ? searchIn.charAt(i + 1) : '\000';
/*      */           }
/* 1257 */           c2 = i + 2 < searchInLength ? searchIn.charAt(i + 2) : '\000'; continue;
/*      */         }
/*      */         label827:
/* 1260 */         if ((!searchMode.contains(SearchMode.SKIP_WHITE_SPACE)) || (!Character.isWhitespace(c0))) {
/* 1261 */           return i;
/*      */         }
/*      */       }
/*      */     }
/* 1265 */     return -1;
/*      */   }
/*      */   
/*      */   private static boolean isCharAtPosNotEqualIgnoreCase(String searchIn, int pos, char firstCharOfSearchForUc, char firstCharOfSearchForLc) {
/* 1269 */     return (Character.toLowerCase(searchIn.charAt(pos)) != firstCharOfSearchForLc) && (Character.toUpperCase(searchIn.charAt(pos)) != firstCharOfSearchForUc);
/*      */   }
/*      */   
/*      */   private static boolean isCharEqualIgnoreCase(char charToCompare, char compareToCharUC, char compareToCharLC) {
/* 1273 */     return (Character.toLowerCase(charToCompare) == compareToCharLC) || (Character.toUpperCase(charToCompare) == compareToCharUC);
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
/*      */   public static List<String> split(String stringToSplit, String delimiter, boolean trim)
/*      */   {
/* 1291 */     if (stringToSplit == null) {
/* 1292 */       return new ArrayList();
/*      */     }
/*      */     
/* 1295 */     if (delimiter == null) {
/* 1296 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/* 1299 */     StringTokenizer tokenizer = new StringTokenizer(stringToSplit, delimiter, false);
/*      */     
/* 1301 */     List<String> splitTokens = new ArrayList(tokenizer.countTokens());
/*      */     
/* 1303 */     while (tokenizer.hasMoreTokens()) {
/* 1304 */       String token = tokenizer.nextToken();
/*      */       
/* 1306 */       if (trim) {
/* 1307 */         token = token.trim();
/*      */       }
/*      */       
/* 1310 */       splitTokens.add(token);
/*      */     }
/*      */     
/* 1313 */     return splitTokens;
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
/*      */   public static List<String> split(String stringToSplit, String delimiter, String markers, String markerCloses, boolean trim)
/*      */   {
/* 1331 */     if (stringToSplit == null) {
/* 1332 */       return new ArrayList();
/*      */     }
/*      */     
/* 1335 */     if (delimiter == null) {
/* 1336 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/* 1339 */     int delimPos = 0;
/* 1340 */     int currentPos = 0;
/*      */     
/* 1342 */     List<String> splitTokens = new ArrayList();
/*      */     
/* 1344 */     while ((delimPos = indexOfIgnoreCase(currentPos, stringToSplit, delimiter, markers, markerCloses, SEARCH_MODE__MRK_COM_WS)) != -1) {
/* 1345 */       String token = stringToSplit.substring(currentPos, delimPos);
/*      */       
/* 1347 */       if (trim) {
/* 1348 */         token = token.trim();
/*      */       }
/*      */       
/* 1351 */       splitTokens.add(token);
/* 1352 */       currentPos = delimPos + 1;
/*      */     }
/*      */     
/* 1355 */     if (currentPos < stringToSplit.length()) {
/* 1356 */       String token = stringToSplit.substring(currentPos);
/*      */       
/* 1358 */       if (trim) {
/* 1359 */         token = token.trim();
/*      */       }
/*      */       
/* 1362 */       splitTokens.add(token);
/*      */     }
/*      */     
/* 1365 */     return splitTokens;
/*      */   }
/*      */   
/*      */   private static boolean startsWith(byte[] dataFrom, String chars) {
/* 1369 */     int charsLength = chars.length();
/*      */     
/* 1371 */     if (dataFrom.length < charsLength) {
/* 1372 */       return false;
/*      */     }
/* 1374 */     for (int i = 0; i < charsLength; i++) {
/* 1375 */       if (dataFrom[i] != chars.charAt(i)) {
/* 1376 */         return false;
/*      */       }
/*      */     }
/* 1379 */     return true;
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
/*      */   public static boolean startsWithIgnoreCase(String searchIn, int startAt, String searchFor)
/*      */   {
/* 1397 */     return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
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
/*      */   public static boolean startsWithIgnoreCase(String searchIn, String searchFor)
/*      */   {
/* 1412 */     return startsWithIgnoreCase(searchIn, 0, searchFor);
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
/*      */   public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor)
/*      */   {
/* 1428 */     if (searchIn == null) {
/* 1429 */       return searchFor == null;
/*      */     }
/*      */     
/* 1432 */     int beginPos = 0;
/* 1433 */     int inLength = searchIn.length();
/* 1435 */     for (; 
/* 1435 */         beginPos < inLength; beginPos++) {
/* 1436 */       char c = searchIn.charAt(beginPos);
/* 1437 */       if (Character.isLetterOrDigit(c)) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/* 1442 */     return startsWithIgnoreCase(searchIn, beginPos, searchFor);
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
/*      */   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor)
/*      */   {
/* 1457 */     return startsWithIgnoreCaseAndWs(searchIn, searchFor, 0);
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
/*      */   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos)
/*      */   {
/* 1475 */     if (searchIn == null) {
/* 1476 */       return searchFor == null;
/*      */     }
/*      */     
/* 1479 */     int inLength = searchIn.length();
/* 1481 */     for (; 
/* 1481 */         beginPos < inLength; beginPos++) {
/* 1482 */       if (!Character.isWhitespace(searchIn.charAt(beginPos))) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/* 1487 */     return startsWithIgnoreCase(searchIn, beginPos, searchFor);
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
/*      */   public static int startsWithIgnoreCaseAndWs(String searchIn, String[] searchFor)
/*      */   {
/* 1502 */     for (int i = 0; i < searchFor.length; i++) {
/* 1503 */       if (startsWithIgnoreCaseAndWs(searchIn, searchFor[i], 0)) {
/* 1504 */         return i;
/*      */       }
/*      */     }
/* 1507 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] stripEnclosure(byte[] source, String prefix, String suffix)
/*      */   {
/* 1516 */     if ((source.length >= prefix.length() + suffix.length()) && (startsWith(source, prefix)) && (endsWith(source, suffix)))
/*      */     {
/* 1518 */       int totalToStrip = prefix.length() + suffix.length();
/* 1519 */       int enclosedLength = source.length - totalToStrip;
/* 1520 */       byte[] enclosed = new byte[enclosedLength];
/*      */       
/* 1522 */       int startPos = prefix.length();
/* 1523 */       int numToCopy = enclosed.length;
/* 1524 */       System.arraycopy(source, startPos, enclosed, 0, numToCopy);
/*      */       
/* 1526 */       return enclosed;
/*      */     }
/* 1528 */     return source;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toAsciiString(byte[] buffer)
/*      */   {
/* 1540 */     return toAsciiString(buffer, 0, buffer.length);
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
/*      */   public static String toAsciiString(byte[] buffer, int startPos, int length)
/*      */   {
/* 1556 */     char[] charArray = new char[length];
/* 1557 */     int readpoint = startPos;
/*      */     
/* 1559 */     for (int i = 0; i < length; i++) {
/* 1560 */       charArray[i] = ((char)buffer[readpoint]);
/* 1561 */       readpoint++;
/*      */     }
/*      */     
/* 1564 */     return new String(charArray);
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
/*      */   public static int wildCompare(String searchIn, String searchForWildcard)
/*      */   {
/* 1582 */     if ((searchIn == null) || (searchForWildcard == null)) {
/* 1583 */       return -1;
/*      */     }
/*      */     
/* 1586 */     if (searchForWildcard.equals("%"))
/*      */     {
/* 1588 */       return 1;
/*      */     }
/*      */     
/* 1591 */     int result = -1;
/*      */     
/* 1593 */     char wildcardMany = '%';
/* 1594 */     char wildcardOne = '_';
/* 1595 */     char wildcardEscape = '\\';
/*      */     
/* 1597 */     int searchForPos = 0;
/* 1598 */     int searchForEnd = searchForWildcard.length();
/*      */     
/* 1600 */     int searchInPos = 0;
/* 1601 */     int searchInEnd = searchIn.length();
/*      */     
/* 1603 */     while (searchForPos != searchForEnd) {
/* 1604 */       char wildstrChar = searchForWildcard.charAt(searchForPos);
/*      */       
/* 1606 */       while ((searchForWildcard.charAt(searchForPos) != wildcardMany) && (wildstrChar != wildcardOne)) {
/* 1607 */         if ((searchForWildcard.charAt(searchForPos) == wildcardEscape) && (searchForPos + 1 != searchForEnd)) {
/* 1608 */           searchForPos++;
/*      */         }
/*      */         
/* 1611 */         if ((searchInPos == searchInEnd) || (Character.toUpperCase(searchForWildcard.charAt(searchForPos++)) != Character.toUpperCase(searchIn.charAt(searchInPos++))))
/*      */         {
/* 1613 */           return 1;
/*      */         }
/*      */         
/* 1616 */         if (searchForPos == searchForEnd) {
/* 1617 */           return searchInPos != searchInEnd ? 1 : 0;
/*      */         }
/*      */         
/* 1620 */         result = 1;
/*      */       }
/*      */       
/* 1623 */       if (searchForWildcard.charAt(searchForPos) == wildcardOne) {
/*      */         do {
/* 1625 */           if (searchInPos == searchInEnd)
/*      */           {
/* 1627 */             return result;
/*      */           }
/*      */           
/* 1630 */           searchInPos++;
/* 1631 */           searchForPos++; } while ((searchForPos < searchForEnd) && (searchForWildcard.charAt(searchForPos) == wildcardOne));
/*      */         
/* 1633 */         if (searchForPos == searchForEnd) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/* 1638 */       if (searchForWildcard.charAt(searchForPos) == wildcardMany)
/*      */       {
/*      */ 
/*      */ 
/* 1642 */         searchForPos++;
/* 1645 */         for (; 
/*      */             
/* 1645 */             searchForPos != searchForEnd; searchForPos++) {
/* 1646 */           if (searchForWildcard.charAt(searchForPos) != wildcardMany)
/*      */           {
/*      */ 
/*      */ 
/* 1650 */             if (searchForWildcard.charAt(searchForPos) != wildcardOne) break;
/* 1651 */             if (searchInPos == searchInEnd) {
/* 1652 */               return -1;
/*      */             }
/*      */             
/* 1655 */             searchInPos++;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1663 */         if (searchForPos == searchForEnd) {
/* 1664 */           return 0;
/*      */         }
/*      */         
/* 1667 */         if (searchInPos == searchInEnd) {
/* 1668 */           return -1;
/*      */         }
/*      */         char cmp;
/* 1671 */         if (((cmp = searchForWildcard.charAt(searchForPos)) == wildcardEscape) && (searchForPos + 1 != searchForEnd)) {
/* 1672 */           cmp = searchForWildcard.charAt(++searchForPos);
/*      */         }
/*      */         
/* 1675 */         searchForPos++;
/*      */         do
/*      */         {
/* 1678 */           while ((searchInPos != searchInEnd) && (Character.toUpperCase(searchIn.charAt(searchInPos)) != Character.toUpperCase(cmp))) {
/* 1679 */             searchInPos++;
/*      */           }
/*      */           
/* 1682 */           if (searchInPos++ == searchInEnd) {
/* 1683 */             return -1;
/*      */           }
/*      */           
/*      */ 
/* 1687 */           int tmp = wildCompare(searchIn, searchForWildcard);
/*      */           
/* 1689 */           if (tmp <= 0) {
/* 1690 */             return tmp;
/*      */           }
/*      */           
/* 1693 */         } while ((searchInPos != searchInEnd) && (searchForWildcard.charAt(0) != wildcardMany));
/*      */         
/* 1695 */         return -1;
/*      */       }
/*      */     }
/*      */     
/* 1699 */     return searchInPos != searchInEnd ? 1 : 0;
/*      */   }
/*      */   
/*      */   static byte[] s2b(String s, MySQLConnection conn) throws SQLException {
/* 1703 */     if (s == null) {
/* 1704 */       return null;
/*      */     }
/*      */     
/* 1707 */     if ((conn != null) && (conn.getUseUnicode())) {
/*      */       try {
/* 1709 */         String encoding = conn.getEncoding();
/*      */         
/* 1711 */         if (encoding == null) {
/* 1712 */           return s.getBytes();
/*      */         }
/*      */         
/* 1715 */         SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
/*      */         
/* 1717 */         if (converter != null) {
/* 1718 */           return converter.toBytes(s);
/*      */         }
/*      */         
/* 1721 */         return s.getBytes(encoding);
/*      */       } catch (UnsupportedEncodingException E) {
/* 1723 */         return s.getBytes();
/*      */       }
/*      */     }
/*      */     
/* 1727 */     return s.getBytes();
/*      */   }
/*      */   
/*      */   public static int lastIndexOf(byte[] s, char c) {
/* 1731 */     if (s == null) {
/* 1732 */       return -1;
/*      */     }
/*      */     
/* 1735 */     for (int i = s.length - 1; i >= 0; i--) {
/* 1736 */       if (s[i] == c) {
/* 1737 */         return i;
/*      */       }
/*      */     }
/*      */     
/* 1741 */     return -1;
/*      */   }
/*      */   
/*      */   public static int indexOf(byte[] s, char c) {
/* 1745 */     if (s == null) {
/* 1746 */       return -1;
/*      */     }
/*      */     
/* 1749 */     int length = s.length;
/*      */     
/* 1751 */     for (int i = 0; i < length; i++) {
/* 1752 */       if (s[i] == c) {
/* 1753 */         return i;
/*      */       }
/*      */     }
/*      */     
/* 1757 */     return -1;
/*      */   }
/*      */   
/*      */   public static boolean isNullOrEmpty(String toTest) {
/* 1761 */     return (toTest == null) || (toTest.length() == 0);
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
/*      */   public static String stripComments(String src, String stringOpens, String stringCloses, boolean slashStarComments, boolean slashSlashComments, boolean hashComments, boolean dashDashComments)
/*      */   {
/* 1786 */     if (src == null) {
/* 1787 */       return null;
/*      */     }
/*      */     
/* 1790 */     StringBuilder strBuilder = new StringBuilder(src.length());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1795 */     StringReader sourceReader = new StringReader(src);
/*      */     
/* 1797 */     int contextMarker = 0;
/* 1798 */     boolean escaped = false;
/* 1799 */     int markerTypeFound = -1;
/*      */     
/* 1801 */     int ind = 0;
/*      */     
/* 1803 */     int currentChar = 0;
/*      */     try
/*      */     {
/* 1806 */       while ((currentChar = sourceReader.read()) != -1)
/*      */       {
/* 1808 */         if ((markerTypeFound != -1) && (currentChar == stringCloses.charAt(markerTypeFound)) && (!escaped)) {
/* 1809 */           contextMarker = 0;
/* 1810 */           markerTypeFound = -1;
/* 1811 */         } else if (((ind = stringOpens.indexOf(currentChar)) != -1) && (!escaped) && (contextMarker == 0)) {
/* 1812 */           markerTypeFound = ind;
/* 1813 */           contextMarker = currentChar;
/*      */         }
/*      */         
/* 1816 */         if ((contextMarker == 0) && (currentChar == 47) && ((slashSlashComments) || (slashStarComments))) {
/* 1817 */           currentChar = sourceReader.read();
/* 1818 */           if ((currentChar == 42) && (slashStarComments)) {
/* 1819 */             int prevChar = 0;
/* 1820 */             while (((currentChar = sourceReader.read()) != 47) || (prevChar != 42)) {
/* 1821 */               if (currentChar == 13)
/*      */               {
/* 1823 */                 currentChar = sourceReader.read();
/* 1824 */                 if (currentChar == 10) {
/* 1825 */                   currentChar = sourceReader.read();
/*      */                 }
/*      */               }
/* 1828 */               else if (currentChar == 10)
/*      */               {
/* 1830 */                 currentChar = sourceReader.read();
/*      */               }
/*      */               
/* 1833 */               if (currentChar < 0) {
/*      */                 break;
/*      */               }
/* 1836 */               prevChar = currentChar;
/*      */             }
/*      */           }
/* 1839 */           if ((currentChar != 47) || (!slashSlashComments)) {}
/* 1840 */         } else { while (((currentChar = sourceReader.read()) != 10) && (currentChar != 13) && (currentChar >= 0))
/*      */           {
/*      */             continue;
/* 1843 */             if ((contextMarker == 0) && (currentChar == 35) && (hashComments)) {}
/*      */             for (;;) {
/* 1845 */               if (((currentChar = sourceReader.read()) != 10) && (currentChar != 13) && (currentChar >= 0)) {
/*      */                 continue;
/* 1847 */                 if ((contextMarker == 0) && (currentChar == 45) && (dashDashComments)) {
/* 1848 */                   currentChar = sourceReader.read();
/*      */                   
/* 1850 */                   if ((currentChar == -1) || (currentChar != 45)) {
/* 1851 */                     strBuilder.append('-');
/*      */                     
/* 1853 */                     if (currentChar == -1) break;
/* 1854 */                     strBuilder.append(currentChar); break;
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1862 */                   while (((currentChar = sourceReader.read()) != 10) && (currentChar != 13) && (currentChar >= 0)) {}
/*      */                 }
/*      */               }
/*      */             } } }
/* 1866 */         if (currentChar != -1) {
/* 1867 */           strBuilder.append((char)currentChar);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx) {}
/*      */     
/*      */ 
/* 1874 */     return strBuilder.toString();
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
/*      */   public static String sanitizeProcOrFuncName(String src)
/*      */   {
/* 1890 */     if ((src == null) || (src.equals("%"))) {
/* 1891 */       return null;
/*      */     }
/*      */     
/* 1894 */     return src;
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
/*      */   public static List<String> splitDBdotName(String src, String cat, String quotId, boolean isNoBslashEscSet)
/*      */   {
/* 1913 */     if ((src == null) || (src.equals("%"))) {
/* 1914 */       return new ArrayList();
/*      */     }
/*      */     
/* 1917 */     boolean isQuoted = indexOfIgnoreCase(0, src, quotId) > -1;
/*      */     
/* 1919 */     String retval = src;
/* 1920 */     String tmpCat = cat;
/*      */     
/* 1922 */     int trueDotIndex = -1;
/* 1923 */     if (!" ".equals(quotId))
/*      */     {
/* 1925 */       if (isQuoted) {
/* 1926 */         trueDotIndex = indexOfIgnoreCase(0, retval, quotId + "." + quotId);
/*      */       }
/*      */       else
/*      */       {
/* 1930 */         trueDotIndex = indexOfIgnoreCase(0, retval, ".");
/*      */       }
/*      */     } else {
/* 1933 */       trueDotIndex = retval.indexOf(".");
/*      */     }
/*      */     
/* 1936 */     List<String> retTokens = new ArrayList(2);
/*      */     
/* 1938 */     if (trueDotIndex != -1)
/*      */     {
/* 1940 */       if (isQuoted) {
/* 1941 */         tmpCat = toString(stripEnclosure(retval.substring(0, trueDotIndex + 1).getBytes(), quotId, quotId));
/* 1942 */         if (startsWithIgnoreCaseAndWs(tmpCat, quotId)) {
/* 1943 */           tmpCat = tmpCat.substring(1, tmpCat.length() - 1);
/*      */         }
/*      */         
/* 1946 */         retval = retval.substring(trueDotIndex + 2);
/* 1947 */         retval = toString(stripEnclosure(retval.getBytes(), quotId, quotId));
/*      */       }
/*      */       else {
/* 1950 */         tmpCat = retval.substring(0, trueDotIndex);
/* 1951 */         retval = retval.substring(trueDotIndex + 1);
/*      */       }
/*      */     }
/*      */     else {
/* 1955 */       retval = toString(stripEnclosure(retval.getBytes(), quotId, quotId));
/*      */     }
/*      */     
/* 1958 */     retTokens.add(tmpCat);
/* 1959 */     retTokens.add(retval);
/* 1960 */     return retTokens;
/*      */   }
/*      */   
/*      */   public static boolean isEmptyOrWhitespaceOnly(String str) {
/* 1964 */     if ((str == null) || (str.length() == 0)) {
/* 1965 */       return true;
/*      */     }
/*      */     
/* 1968 */     int length = str.length();
/*      */     
/* 1970 */     for (int i = 0; i < length; i++) {
/* 1971 */       if (!Character.isWhitespace(str.charAt(i))) {
/* 1972 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1976 */     return true;
/*      */   }
/*      */   
/*      */   public static String escapeQuote(String src, String quotChar) {
/* 1980 */     if (src == null) {
/* 1981 */       return null;
/*      */     }
/*      */     
/* 1984 */     src = toString(stripEnclosure(src.getBytes(), quotChar, quotChar));
/*      */     
/* 1986 */     int lastNdx = src.indexOf(quotChar);
/*      */     
/*      */ 
/*      */ 
/* 1990 */     String tmpSrc = src.substring(0, lastNdx);
/* 1991 */     tmpSrc = tmpSrc + quotChar + quotChar;
/*      */     
/* 1993 */     String tmpRest = src.substring(lastNdx + 1, src.length());
/*      */     
/* 1995 */     lastNdx = tmpRest.indexOf(quotChar);
/* 1996 */     while (lastNdx > -1)
/*      */     {
/* 1998 */       tmpSrc = tmpSrc + tmpRest.substring(0, lastNdx);
/* 1999 */       tmpSrc = tmpSrc + quotChar + quotChar;
/* 2000 */       tmpRest = tmpRest.substring(lastNdx + 1, tmpRest.length());
/*      */       
/* 2002 */       lastNdx = tmpRest.indexOf(quotChar);
/*      */     }
/*      */     
/* 2005 */     tmpSrc = tmpSrc + tmpRest;
/* 2006 */     src = tmpSrc;
/*      */     
/* 2008 */     return src;
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
/*      */ 
/*      */   public static String quoteIdentifier(String identifier, String quoteChar, boolean isPedantic)
/*      */   {
/* 2042 */     if (identifier == null) {
/* 2043 */       return null;
/*      */     }
/*      */     
/* 2046 */     identifier = identifier.trim();
/*      */     
/* 2048 */     int quoteCharLength = quoteChar.length();
/* 2049 */     if ((quoteCharLength == 0) || (" ".equals(quoteChar))) {
/* 2050 */       return identifier;
/*      */     }
/*      */     
/*      */ 
/* 2054 */     if ((!isPedantic) && (identifier.startsWith(quoteChar)) && (identifier.endsWith(quoteChar)))
/*      */     {
/* 2056 */       String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);
/*      */       
/*      */ 
/* 2059 */       int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
/* 2060 */       while (quoteCharPos >= 0) {
/* 2061 */         int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
/* 2062 */         int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
/*      */         
/* 2064 */         if (quoteCharNextPosition != quoteCharNextExpectedPos) break;
/* 2065 */         quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2072 */       if (quoteCharPos < 0) {
/* 2073 */         return identifier;
/*      */       }
/*      */     }
/*      */     
/* 2077 */     return quoteChar + identifier.replaceAll(quoteChar, new StringBuilder().append(quoteChar).append(quoteChar).toString()) + quoteChar;
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
/*      */   public static String quoteIdentifier(String identifier, boolean isPedantic)
/*      */   {
/* 2100 */     return quoteIdentifier(identifier, "`", isPedantic);
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
/*      */   public static String unQuoteIdentifier(String identifier, String quoteChar)
/*      */   {
/* 2125 */     if (identifier == null) {
/* 2126 */       return null;
/*      */     }
/*      */     
/* 2129 */     identifier = identifier.trim();
/*      */     
/* 2131 */     int quoteCharLength = quoteChar.length();
/* 2132 */     if ((quoteCharLength == 0) || (" ".equals(quoteChar))) {
/* 2133 */       return identifier;
/*      */     }
/*      */     
/*      */ 
/* 2137 */     if ((identifier.startsWith(quoteChar)) && (identifier.endsWith(quoteChar)))
/*      */     {
/* 2139 */       String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);
/*      */       
/*      */ 
/* 2142 */       int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
/* 2143 */       while (quoteCharPos >= 0) {
/* 2144 */         int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
/* 2145 */         int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
/*      */         
/* 2147 */         if (quoteCharNextPosition == quoteCharNextExpectedPos) {
/* 2148 */           quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength);
/*      */         }
/*      */         else {
/* 2151 */           return identifier;
/*      */         }
/*      */       }
/*      */       
/* 2155 */       return identifier.substring(quoteCharLength, identifier.length() - quoteCharLength).replaceAll(quoteChar + quoteChar, quoteChar);
/*      */     }
/*      */     
/* 2158 */     return identifier;
/*      */   }
/*      */   
/*      */   public static int indexOfQuoteDoubleAware(String searchIn, String quoteChar, int startFrom) {
/* 2162 */     if ((searchIn == null) || (quoteChar == null) || (quoteChar.length() == 0) || (startFrom > searchIn.length())) {
/* 2163 */       return -1;
/*      */     }
/*      */     
/* 2166 */     int lastIndex = searchIn.length() - 1;
/*      */     
/* 2168 */     int beginPos = startFrom;
/* 2169 */     int pos = -1;
/*      */     
/* 2171 */     boolean next = true;
/* 2172 */     while (next) {
/* 2173 */       pos = searchIn.indexOf(quoteChar, beginPos);
/* 2174 */       if ((pos == -1) || (pos == lastIndex) || (!searchIn.startsWith(quoteChar, pos + 1))) {
/* 2175 */         next = false;
/*      */       } else {
/* 2177 */         beginPos = pos + 2;
/*      */       }
/*      */     }
/*      */     
/* 2181 */     return pos;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toString(byte[] value, int offset, int length, String encoding)
/*      */     throws UnsupportedEncodingException
/*      */   {
/* 2193 */     Charset cs = findCharset(encoding);
/*      */     
/* 2195 */     return cs.decode(ByteBuffer.wrap(value, offset, length)).toString();
/*      */   }
/*      */   
/*      */   public static String toString(byte[] value, String encoding) throws UnsupportedEncodingException {
/* 2199 */     Charset cs = findCharset(encoding);
/*      */     
/* 2201 */     return cs.decode(ByteBuffer.wrap(value)).toString();
/*      */   }
/*      */   
/*      */   public static String toString(byte[] value, int offset, int length) {
/*      */     try {
/* 2206 */       Charset cs = findCharset(platformEncoding);
/*      */       
/* 2208 */       return cs.decode(ByteBuffer.wrap(value, offset, length)).toString();
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*      */ 
/* 2213 */     return null;
/*      */   }
/*      */   
/*      */   public static String toString(byte[] value) {
/*      */     try {
/* 2218 */       Charset cs = findCharset(platformEncoding);
/*      */       
/* 2220 */       return cs.decode(ByteBuffer.wrap(value)).toString();
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*      */ 
/* 2225 */     return null;
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(char[] value) {
/*      */     try {
/* 2230 */       return getBytes(value, 0, value.length, platformEncoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*      */ 
/* 2235 */     return null;
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(char[] value, int offset, int length) {
/*      */     try {
/* 2240 */       return getBytes(value, offset, length, platformEncoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*      */ 
/* 2245 */     return null;
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(char[] value, String encoding) throws UnsupportedEncodingException {
/* 2249 */     return getBytes(value, 0, value.length, encoding);
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(char[] value, int offset, int length, String encoding) throws UnsupportedEncodingException {
/* 2253 */     Charset cs = findCharset(encoding);
/*      */     
/* 2255 */     ByteBuffer buf = cs.encode(CharBuffer.wrap(value, offset, length));
/*      */     
/*      */ 
/* 2258 */     int encodedLen = buf.limit();
/* 2259 */     byte[] asBytes = new byte[encodedLen];
/* 2260 */     buf.get(asBytes, 0, encodedLen);
/*      */     
/* 2262 */     return asBytes;
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String value) {
/*      */     try {
/* 2267 */       return getBytes(value, 0, value.length(), platformEncoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*      */ 
/* 2272 */     return null;
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String value, int offset, int length) {
/*      */     try {
/* 2277 */       return getBytes(value, offset, length, platformEncoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*      */ 
/* 2282 */     return null;
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String value, String encoding) throws UnsupportedEncodingException {
/* 2286 */     return getBytes(value, 0, value.length(), encoding);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] getBytes(String value, int offset, int length, String encoding)
/*      */     throws UnsupportedEncodingException
/*      */   {
/* 2295 */     if (!Util.isJdbc4()) {
/* 2296 */       if ((offset != 0) || (length != value.length())) {
/* 2297 */         return value.substring(offset, offset + length).getBytes(encoding);
/*      */       }
/* 2299 */       return value.getBytes(encoding);
/*      */     }
/*      */     
/* 2302 */     Charset cs = findCharset(encoding);
/*      */     
/* 2304 */     ByteBuffer buf = cs.encode(CharBuffer.wrap(value.toCharArray(), offset, length));
/*      */     
/*      */ 
/* 2307 */     int encodedLen = buf.limit();
/* 2308 */     byte[] asBytes = new byte[encodedLen];
/* 2309 */     buf.get(asBytes, 0, encodedLen);
/*      */     
/* 2311 */     return asBytes;
/*      */   }
/*      */   
/*      */   public static final boolean isValidIdChar(char c) {
/* 2315 */     return "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@".indexOf(c) != -1;
/*      */   }
/*      */   
/* 2318 */   private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*      */   
/*      */   public static void appendAsHex(StringBuilder builder, byte[] bytes) {
/* 2321 */     builder.append("0x");
/* 2322 */     for (byte b : bytes) {
/* 2323 */       builder.append(HEX_DIGITS[(b >>> 4 & 0xF)]).append(HEX_DIGITS[(b & 0xF)]);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void appendAsHex(StringBuilder builder, int value) {
/* 2328 */     if (value == 0) {
/* 2329 */       builder.append("0x0");
/* 2330 */       return;
/*      */     }
/*      */     
/* 2333 */     int shift = 32;
/*      */     
/* 2335 */     boolean nonZeroFound = false;
/*      */     
/* 2337 */     builder.append("0x");
/*      */     do {
/* 2339 */       shift -= 4;
/* 2340 */       byte nibble = (byte)(value >>> shift & 0xF);
/* 2341 */       if (nonZeroFound) {
/* 2342 */         builder.append(HEX_DIGITS[nibble]);
/* 2343 */       } else if (nibble != 0) {
/* 2344 */         builder.append(HEX_DIGITS[nibble]);
/* 2345 */         nonZeroFound = true;
/*      */       }
/* 2347 */     } while (shift != 0);
/*      */   }
/*      */   
/*      */   public static byte[] getBytesNullTerminated(String value, String encoding) throws UnsupportedEncodingException {
/* 2351 */     Charset cs = findCharset(encoding);
/*      */     
/* 2353 */     ByteBuffer buf = cs.encode(value);
/*      */     
/* 2355 */     int encodedLen = buf.limit();
/* 2356 */     byte[] asBytes = new byte[encodedLen + 1];
/* 2357 */     buf.get(asBytes, 0, encodedLen);
/* 2358 */     asBytes[encodedLen] = 0;
/*      */     
/* 2360 */     return asBytes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isStrictlyNumeric(CharSequence cs)
/*      */   {
/* 2372 */     if ((cs == null) || (cs.length() == 0)) {
/* 2373 */       return false;
/*      */     }
/* 2375 */     for (int i = 0; i < cs.length(); i++) {
/* 2376 */       if (!Character.isDigit(cs.charAt(i))) {
/* 2377 */         return false;
/*      */       }
/*      */     }
/* 2380 */     return true;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\StringUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */