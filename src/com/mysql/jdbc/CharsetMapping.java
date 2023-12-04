/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CharsetMapping
/*     */ {
/*     */   public static final int MAP_SIZE = 255;
/*     */   public static final String[] COLLATION_INDEX_TO_COLLATION_NAME;
/*     */   public static final MysqlCharset[] COLLATION_INDEX_TO_CHARSET;
/*     */   public static final Map<String, MysqlCharset> CHARSET_NAME_TO_CHARSET;
/*     */   public static final Map<String, Integer> CHARSET_NAME_TO_COLLATION_INDEX;
/*     */   private static final Map<String, List<MysqlCharset>> JAVA_ENCODING_UC_TO_MYSQL_CHARSET;
/*     */   private static final Set<String> MULTIBYTE_ENCODINGS;
/*     */   private static final Map<String, String> ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET;
/*     */   private static final Set<String> ESCAPE_ENCODINGS;
/*     */   public static final Set<Integer> UTF8MB4_INDEXES;
/*     */   private static final String MYSQL_CHARSET_NAME_armscii8 = "armscii8";
/*     */   private static final String MYSQL_CHARSET_NAME_ascii = "ascii";
/*     */   private static final String MYSQL_CHARSET_NAME_big5 = "big5";
/*     */   private static final String MYSQL_CHARSET_NAME_binary = "binary";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1250 = "cp1250";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1251 = "cp1251";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1256 = "cp1256";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1257 = "cp1257";
/*     */   private static final String MYSQL_CHARSET_NAME_cp850 = "cp850";
/*     */   private static final String MYSQL_CHARSET_NAME_cp852 = "cp852";
/*     */   private static final String MYSQL_CHARSET_NAME_cp866 = "cp866";
/*     */   private static final String MYSQL_CHARSET_NAME_cp932 = "cp932";
/*     */   private static final String MYSQL_CHARSET_NAME_dec8 = "dec8";
/*     */   private static final String MYSQL_CHARSET_NAME_eucjpms = "eucjpms";
/*     */   private static final String MYSQL_CHARSET_NAME_euckr = "euckr";
/*     */   private static final String MYSQL_CHARSET_NAME_gb18030 = "gb18030";
/*     */   private static final String MYSQL_CHARSET_NAME_gb2312 = "gb2312";
/*     */   private static final String MYSQL_CHARSET_NAME_gbk = "gbk";
/*     */   private static final String MYSQL_CHARSET_NAME_geostd8 = "geostd8";
/*     */   private static final String MYSQL_CHARSET_NAME_greek = "greek";
/*     */   private static final String MYSQL_CHARSET_NAME_hebrew = "hebrew";
/*     */   private static final String MYSQL_CHARSET_NAME_hp8 = "hp8";
/*     */   private static final String MYSQL_CHARSET_NAME_keybcs2 = "keybcs2";
/*     */   private static final String MYSQL_CHARSET_NAME_koi8r = "koi8r";
/*     */   private static final String MYSQL_CHARSET_NAME_koi8u = "koi8u";
/*     */   private static final String MYSQL_CHARSET_NAME_latin1 = "latin1";
/*     */   private static final String MYSQL_CHARSET_NAME_latin2 = "latin2";
/*     */   private static final String MYSQL_CHARSET_NAME_latin5 = "latin5";
/*     */   private static final String MYSQL_CHARSET_NAME_latin7 = "latin7";
/*     */   private static final String MYSQL_CHARSET_NAME_macce = "macce";
/*     */   private static final String MYSQL_CHARSET_NAME_macroman = "macroman";
/*     */   private static final String MYSQL_CHARSET_NAME_sjis = "sjis";
/*     */   private static final String MYSQL_CHARSET_NAME_swe7 = "swe7";
/*     */   private static final String MYSQL_CHARSET_NAME_tis620 = "tis620";
/*     */   private static final String MYSQL_CHARSET_NAME_ucs2 = "ucs2";
/*     */   private static final String MYSQL_CHARSET_NAME_ujis = "ujis";
/*     */   private static final String MYSQL_CHARSET_NAME_utf16 = "utf16";
/*     */   private static final String MYSQL_CHARSET_NAME_utf16le = "utf16le";
/*     */   private static final String MYSQL_CHARSET_NAME_utf32 = "utf32";
/*     */   private static final String MYSQL_CHARSET_NAME_utf8 = "utf8";
/*     */   private static final String MYSQL_CHARSET_NAME_utf8mb4 = "utf8mb4";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_cp1251cias = "cp1251cias";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_cp1251csas = "cp1251csas";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_croat = "croat";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_czech = "czech";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_danish = "danish";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_dos = "dos";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_estonia = "estonia";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_euc_kr = "euc_kr";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_german1 = "german1";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_hungarian = "hungarian";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_koi8_ru = "koi8_ru";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_koi8_ukr = "koi8_ukr";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_latin1_de = "latin1_de";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_latvian = "latvian";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_latvian1 = "latvian1";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_usa7 = "usa7";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_win1250 = "win1250";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_win1251 = "win1251";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_win1251ukr = "win1251ukr";
/*     */   private static final String NOT_USED = "latin1";
/*     */   public static final int MYSQL_COLLATION_INDEX_utf8 = 33;
/*     */   public static final int MYSQL_COLLATION_INDEX_binary = 63;
/* 128 */   private static int numberOfEncodingsConfigured = 0;
/*     */   
/*     */   static
/*     */   {
/* 132 */     MysqlCharset[] charset = { new MysqlCharset("usa7", 1, 0, new String[] { "US-ASCII" }, 4, 0), new MysqlCharset("ascii", 1, 0, new String[] { "US-ASCII", "ASCII" }), new MysqlCharset("big5", 2, 0, new String[] { "Big5" }), new MysqlCharset("gbk", 2, 0, new String[] { "GBK" }), new MysqlCharset("sjis", 2, 0, new String[] { "SHIFT_JIS", "Cp943", "WINDOWS-31J" }), new MysqlCharset("cp932", 2, 1, new String[] { "WINDOWS-31J" }), new MysqlCharset("gb2312", 2, 0, new String[] { "GB2312" }), new MysqlCharset("ujis", 3, 0, new String[] { "EUC_JP" }), new MysqlCharset("eucjpms", 3, 0, new String[] { "EUC_JP_Solaris" }, 5, 0, 3), new MysqlCharset("gb18030", 4, 0, new String[] { "GB18030" }, 5, 7, 4), new MysqlCharset("euc_kr", 2, 0, new String[] { "EUC_KR" }, 4, 0), new MysqlCharset("euckr", 2, 0, new String[] { "EUC-KR" }), new MysqlCharset("latin1", 1, 1, new String[] { "Cp1252", "ISO8859_1" }), new MysqlCharset("swe7", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("hp8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("dec8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("armscii8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("geostd8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("latin2", 1, 0, new String[] { "ISO8859_2" }), new MysqlCharset("czech", 1, 0, new String[] { "ISO8859_2" }, 4, 0), new MysqlCharset("hungarian", 1, 0, new String[] { "ISO8859_2" }, 4, 0), new MysqlCharset("croat", 1, 0, new String[] { "ISO8859_2" }, 4, 0), new MysqlCharset("greek", 1, 0, new String[] { "ISO8859_7", "greek" }), new MysqlCharset("latin7", 1, 0, new String[] { "ISO-8859-13" }), new MysqlCharset("hebrew", 1, 0, new String[] { "ISO8859_8" }), new MysqlCharset("latin5", 1, 0, new String[] { "ISO8859_9" }), new MysqlCharset("latvian", 1, 0, new String[] { "ISO8859_13" }, 4, 0), new MysqlCharset("latvian1", 1, 0, new String[] { "ISO8859_13" }, 4, 0), new MysqlCharset("estonia", 1, 1, new String[] { "ISO8859_13" }, 4, 0), new MysqlCharset("cp850", 1, 0, new String[] { "Cp850", "Cp437" }), new MysqlCharset("dos", 1, 0, new String[] { "Cp850", "Cp437" }, 4, 0), new MysqlCharset("cp852", 1, 0, new String[] { "Cp852" }), new MysqlCharset("keybcs2", 1, 0, new String[] { "Cp852" }), new MysqlCharset("cp866", 1, 0, new String[] { "Cp866" }), new MysqlCharset("koi8_ru", 1, 0, new String[] { "KOI8_R" }, 4, 0), new MysqlCharset("koi8r", 1, 1, new String[] { "KOI8_R" }), new MysqlCharset("koi8u", 1, 0, new String[] { "KOI8_R" }), new MysqlCharset("koi8_ukr", 1, 0, new String[] { "KOI8_R" }, 4, 0), new MysqlCharset("tis620", 1, 0, new String[] { "TIS620" }), new MysqlCharset("cp1250", 1, 0, new String[] { "Cp1250" }), new MysqlCharset("win1250", 1, 0, new String[] { "Cp1250" }, 4, 0), new MysqlCharset("cp1251", 1, 1, new String[] { "Cp1251" }), new MysqlCharset("win1251", 1, 0, new String[] { "Cp1251" }, 4, 0), new MysqlCharset("cp1251cias", 1, 0, new String[] { "Cp1251" }, 4, 0), new MysqlCharset("cp1251csas", 1, 0, new String[] { "Cp1251" }, 4, 0), new MysqlCharset("win1251ukr", 1, 0, new String[] { "Cp1251" }, 4, 0), new MysqlCharset("cp1256", 1, 0, new String[] { "Cp1256" }), new MysqlCharset("cp1257", 1, 0, new String[] { "Cp1257" }), new MysqlCharset("macroman", 1, 0, new String[] { "MacRoman" }), new MysqlCharset("macce", 1, 0, new String[] { "MacCentralEurope" }), new MysqlCharset("utf8", 3, 1, new String[] { "UTF-8" }), new MysqlCharset("utf8mb4", 4, 0, new String[] { "UTF-8" }), new MysqlCharset("ucs2", 2, 0, new String[] { "UnicodeBig" }), new MysqlCharset("binary", 1, 1, new String[] { "ISO8859_1" }), new MysqlCharset("latin1_de", 1, 0, new String[] { "ISO8859_1" }, 4, 0), new MysqlCharset("german1", 1, 0, new String[] { "ISO8859_1" }, 4, 0), new MysqlCharset("danish", 1, 0, new String[] { "ISO8859_1" }, 4, 0), new MysqlCharset("utf16", 4, 0, new String[] { "UTF-16" }), new MysqlCharset("utf16le", 4, 0, new String[] { "UTF-16LE" }), new MysqlCharset("utf32", 4, 0, new String[] { "UTF-32" }) };
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
/* 216 */     HashMap<String, MysqlCharset> charsetNameToMysqlCharsetMap = new HashMap();
/* 217 */     HashMap<String, List<MysqlCharset>> javaUcToMysqlCharsetMap = new HashMap();
/* 218 */     Set<String> tempMultibyteEncodings = new HashSet();
/* 219 */     Set<String> tempEscapeEncodings = new HashSet();
/* 220 */     for (int i = 0; i < charset.length; i++) {
/* 221 */       String charsetName = charset[i].charsetName;
/*     */       
/* 223 */       charsetNameToMysqlCharsetMap.put(charsetName, charset[i]);
/*     */       
/* 225 */       numberOfEncodingsConfigured += charset[i].javaEncodingsUc.size();
/*     */       
/* 227 */       for (String encUC : charset[i].javaEncodingsUc)
/*     */       {
/*     */ 
/* 230 */         List<MysqlCharset> charsets = (List)javaUcToMysqlCharsetMap.get(encUC);
/* 231 */         if (charsets == null) {
/* 232 */           charsets = new ArrayList();
/* 233 */           javaUcToMysqlCharsetMap.put(encUC, charsets);
/*     */         }
/* 235 */         charsets.add(charset[i]);
/*     */         
/*     */ 
/* 238 */         if (charset[i].mblen > 1) {
/* 239 */           tempMultibyteEncodings.add(encUC);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 246 */       if ((charsetName.equals("big5")) || (charsetName.equals("gbk")) || (charsetName.equals("sjis"))) {
/* 247 */         tempEscapeEncodings.addAll(charset[i].javaEncodingsUc);
/*     */       }
/*     */     }
/*     */     
/* 251 */     CHARSET_NAME_TO_CHARSET = Collections.unmodifiableMap(charsetNameToMysqlCharsetMap);
/* 252 */     JAVA_ENCODING_UC_TO_MYSQL_CHARSET = Collections.unmodifiableMap(javaUcToMysqlCharsetMap);
/* 253 */     MULTIBYTE_ENCODINGS = Collections.unmodifiableSet(tempMultibyteEncodings);
/* 254 */     ESCAPE_ENCODINGS = Collections.unmodifiableSet(tempEscapeEncodings);
/*     */     
/*     */ 
/* 257 */     Collation[] collation = new Collation['ÿ'];
/* 258 */     collation[1] = new Collation(1, "big5_chinese_ci", 1, "big5");
/* 259 */     collation[84] = new Collation(84, "big5_bin", 0, "big5");
/*     */     
/* 261 */     collation[2] = new Collation(2, "latin2_czech_cs", 0, "latin2");
/* 262 */     collation[9] = new Collation(9, "latin2_general_ci", 1, "latin2");
/* 263 */     collation[21] = new Collation(21, "latin2_hungarian_ci", 0, "latin2");
/* 264 */     collation[27] = new Collation(27, "latin2_croatian_ci", 0, "latin2");
/* 265 */     collation[77] = new Collation(77, "latin2_bin", 0, "latin2");
/*     */     
/* 267 */     collation[4] = new Collation(4, "cp850_general_ci", 1, "cp850");
/* 268 */     collation[80] = new Collation(80, "cp850_bin", 0, "cp850");
/*     */     
/* 270 */     collation[5] = new Collation(5, "latin1_german1_ci", 1, "latin1");
/* 271 */     collation[8] = new Collation(8, "latin1_swedish_ci", 0, "latin1");
/* 272 */     collation[15] = new Collation(15, "latin1_danish_ci", 0, "latin1");
/* 273 */     collation[31] = new Collation(31, "latin1_german2_ci", 0, "latin1");
/* 274 */     collation[47] = new Collation(47, "latin1_bin", 0, "latin1");
/* 275 */     collation[48] = new Collation(48, "latin1_general_ci", 0, "latin1");
/* 276 */     collation[49] = new Collation(49, "latin1_general_cs", 0, "latin1");
/* 277 */     collation[76] = new Collation(76, "not_implemented", 0, "latin1");
/* 278 */     collation[94] = new Collation(94, "latin1_spanish_ci", 0, "latin1");
/* 279 */     collation[100] = new Collation(100, "not_implemented", 0, "latin1");
/* 280 */     collation[125] = new Collation(125, "not_implemented", 0, "latin1");
/* 281 */     collation[126] = new Collation(126, "not_implemented", 0, "latin1");
/* 282 */     collation[127] = new Collation(127, "not_implemented", 0, "latin1");
/* 283 */     collation[''] = new Collation(152, "not_implemented", 0, "latin1");
/* 284 */     collation[''] = new Collation(153, "not_implemented", 0, "latin1");
/* 285 */     collation[''] = new Collation(154, "not_implemented", 0, "latin1");
/* 286 */     collation[''] = new Collation(155, "not_implemented", 0, "latin1");
/* 287 */     collation[''] = new Collation(156, "not_implemented", 0, "latin1");
/* 288 */     collation[''] = new Collation(157, "not_implemented", 0, "latin1");
/* 289 */     collation[''] = new Collation(158, "not_implemented", 0, "latin1");
/* 290 */     collation['¸'] = new Collation(184, "not_implemented", 0, "latin1");
/* 291 */     collation['¹'] = new Collation(185, "not_implemented", 0, "latin1");
/* 292 */     collation['º'] = new Collation(186, "not_implemented", 0, "latin1");
/* 293 */     collation['»'] = new Collation(187, "not_implemented", 0, "latin1");
/* 294 */     collation['¼'] = new Collation(188, "not_implemented", 0, "latin1");
/* 295 */     collation['½'] = new Collation(189, "not_implemented", 0, "latin1");
/* 296 */     collation['¾'] = new Collation(190, "not_implemented", 0, "latin1");
/* 297 */     collation['¿'] = new Collation(191, "not_implemented", 0, "latin1");
/* 298 */     collation['Ø'] = new Collation(216, "not_implemented", 0, "latin1");
/* 299 */     collation['Ù'] = new Collation(217, "not_implemented", 0, "latin1");
/* 300 */     collation['Ú'] = new Collation(218, "not_implemented", 0, "latin1");
/* 301 */     collation['Û'] = new Collation(219, "not_implemented", 0, "latin1");
/* 302 */     collation['Ü'] = new Collation(220, "not_implemented", 0, "latin1");
/* 303 */     collation['Ý'] = new Collation(221, "not_implemented", 0, "latin1");
/* 304 */     collation['Þ'] = new Collation(222, "not_implemented", 0, "latin1");
/* 305 */     collation['ø'] = new Collation(248, "gb18030_chinese_ci", 1, "gb18030");
/* 306 */     collation['ù'] = new Collation(249, "gb18030_bin", 0, "gb18030");
/* 307 */     collation['ú'] = new Collation(250, "gb18030_unicode_520_ci", 0, "gb18030");
/* 308 */     collation['û'] = new Collation(251, "not_implemented", 0, "latin1");
/* 309 */     collation['ü'] = new Collation(252, "not_implemented", 0, "latin1");
/* 310 */     collation['ý'] = new Collation(253, "not_implemented", 0, "latin1");
/* 311 */     collation['þ'] = new Collation(254, "not_implemented", 0, "latin1");
/* 312 */     collation[10] = new Collation(10, "swe7_swedish_ci", 0, "swe7");
/* 313 */     collation[82] = new Collation(82, "swe7_bin", 0, "swe7");
/* 314 */     collation[6] = new Collation(6, "hp8_english_ci", 0, "hp8");
/* 315 */     collation[72] = new Collation(72, "hp8_bin", 0, "hp8");
/* 316 */     collation[3] = new Collation(3, "dec8_swedish_ci", 0, "dec8");
/* 317 */     collation[69] = new Collation(69, "dec8_bin", 0, "dec8");
/* 318 */     collation[32] = new Collation(32, "armscii8_general_ci", 0, "armscii8");
/* 319 */     collation[64] = new Collation(64, "armscii8_bin", 0, "armscii8");
/* 320 */     collation[92] = new Collation(92, "geostd8_general_ci", 0, "geostd8");
/* 321 */     collation[93] = new Collation(93, "geostd8_bin", 0, "geostd8");
/*     */     
/* 323 */     collation[7] = new Collation(7, "koi8r_general_ci", 0, "koi8r");
/* 324 */     collation[74] = new Collation(74, "koi8r_bin", 0, "koi8r");
/*     */     
/* 326 */     collation[11] = new Collation(11, "ascii_general_ci", 0, "ascii");
/* 327 */     collation[65] = new Collation(65, "ascii_bin", 0, "ascii");
/*     */     
/* 329 */     collation[12] = new Collation(12, "ujis_japanese_ci", 0, "ujis");
/* 330 */     collation[91] = new Collation(91, "ujis_bin", 0, "ujis");
/*     */     
/* 332 */     collation[13] = new Collation(13, "sjis_japanese_ci", 0, "sjis");
/* 333 */     collation[14] = new Collation(14, "cp1251_bulgarian_ci", 0, "cp1251");
/* 334 */     collation[16] = new Collation(16, "hebrew_general_ci", 0, "hebrew");
/* 335 */     collation[17] = new Collation(17, "latin1_german1_ci", 0, "win1251");
/* 336 */     collation[18] = new Collation(18, "tis620_thai_ci", 0, "tis620");
/* 337 */     collation[19] = new Collation(19, "euckr_korean_ci", 0, "euckr");
/* 338 */     collation[20] = new Collation(20, "latin7_estonian_cs", 0, "latin7");
/* 339 */     collation[22] = new Collation(22, "koi8u_general_ci", 0, "koi8u");
/* 340 */     collation[23] = new Collation(23, "cp1251_ukrainian_ci", 0, "cp1251");
/* 341 */     collation[24] = new Collation(24, "gb2312_chinese_ci", 0, "gb2312");
/* 342 */     collation[25] = new Collation(25, "greek_general_ci", 0, "greek");
/* 343 */     collation[26] = new Collation(26, "cp1250_general_ci", 1, "cp1250");
/* 344 */     collation[28] = new Collation(28, "gbk_chinese_ci", 1, "gbk");
/* 345 */     collation[29] = new Collation(29, "cp1257_lithuanian_ci", 0, "cp1257");
/* 346 */     collation[30] = new Collation(30, "latin5_turkish_ci", 1, "latin5");
/* 347 */     collation[33] = new Collation(33, "utf8_general_ci", 1, "utf8");
/* 348 */     collation[34] = new Collation(34, "cp1250_czech_cs", 0, "cp1250");
/* 349 */     collation[35] = new Collation(35, "ucs2_general_ci", 1, "ucs2");
/* 350 */     collation[36] = new Collation(36, "cp866_general_ci", 1, "cp866");
/* 351 */     collation[37] = new Collation(37, "keybcs2_general_ci", 1, "keybcs2");
/* 352 */     collation[38] = new Collation(38, "macce_general_ci", 1, "macce");
/* 353 */     collation[39] = new Collation(39, "macroman_general_ci", 1, "macroman");
/* 354 */     collation[40] = new Collation(40, "cp852_general_ci", 1, "cp852");
/* 355 */     collation[41] = new Collation(41, "latin7_general_ci", 1, "latin7");
/* 356 */     collation[42] = new Collation(42, "latin7_general_cs", 0, "latin7");
/* 357 */     collation[43] = new Collation(43, "macce_bin", 0, "macce");
/* 358 */     collation[44] = new Collation(44, "cp1250_croatian_ci", 0, "cp1250");
/* 359 */     collation[45] = new Collation(45, "utf8mb4_general_ci", 1, "utf8mb4");
/* 360 */     collation[46] = new Collation(46, "utf8mb4_bin", 0, "utf8mb4");
/* 361 */     collation[50] = new Collation(50, "cp1251_bin", 0, "cp1251");
/* 362 */     collation[51] = new Collation(51, "cp1251_general_ci", 1, "cp1251");
/* 363 */     collation[52] = new Collation(52, "cp1251_general_cs", 0, "cp1251");
/* 364 */     collation[53] = new Collation(53, "macroman_bin", 0, "macroman");
/* 365 */     collation[54] = new Collation(54, "utf16_general_ci", 1, "utf16");
/* 366 */     collation[55] = new Collation(55, "utf16_bin", 0, "utf16");
/* 367 */     collation[56] = new Collation(56, "utf16le_general_ci", 1, "utf16le");
/* 368 */     collation[57] = new Collation(57, "cp1256_general_ci", 1, "cp1256");
/* 369 */     collation[58] = new Collation(58, "cp1257_bin", 0, "cp1257");
/* 370 */     collation[59] = new Collation(59, "cp1257_general_ci", 1, "cp1257");
/* 371 */     collation[60] = new Collation(60, "utf32_general_ci", 1, "utf32");
/* 372 */     collation[61] = new Collation(61, "utf32_bin", 0, "utf32");
/* 373 */     collation[62] = new Collation(62, "utf16le_bin", 0, "utf16le");
/* 374 */     collation[63] = new Collation(63, "binary", 1, "binary");
/* 375 */     collation[66] = new Collation(66, "cp1250_bin", 0, "cp1250");
/* 376 */     collation[67] = new Collation(67, "cp1256_bin", 0, "cp1256");
/* 377 */     collation[68] = new Collation(68, "cp866_bin", 0, "cp866");
/* 378 */     collation[70] = new Collation(70, "greek_bin", 0, "greek");
/* 379 */     collation[71] = new Collation(71, "hebrew_bin", 0, "hebrew");
/* 380 */     collation[73] = new Collation(73, "keybcs2_bin", 0, "keybcs2");
/* 381 */     collation[75] = new Collation(75, "koi8u_bin", 0, "koi8u");
/* 382 */     collation[78] = new Collation(78, "latin5_bin", 0, "latin5");
/* 383 */     collation[79] = new Collation(79, "latin7_bin", 0, "latin7");
/* 384 */     collation[81] = new Collation(81, "cp852_bin", 0, "cp852");
/* 385 */     collation[83] = new Collation(83, "utf8_bin", 0, "utf8");
/* 386 */     collation[85] = new Collation(85, "euckr_bin", 0, "euckr");
/* 387 */     collation[86] = new Collation(86, "gb2312_bin", 0, "gb2312");
/* 388 */     collation[87] = new Collation(87, "gbk_bin", 0, "gbk");
/* 389 */     collation[88] = new Collation(88, "sjis_bin", 0, "sjis");
/* 390 */     collation[89] = new Collation(89, "tis620_bin", 0, "tis620");
/* 391 */     collation[90] = new Collation(90, "ucs2_bin", 0, "ucs2");
/* 392 */     collation[95] = new Collation(95, "cp932_japanese_ci", 1, "cp932");
/* 393 */     collation[96] = new Collation(96, "cp932_bin", 0, "cp932");
/* 394 */     collation[97] = new Collation(97, "eucjpms_japanese_ci", 1, "eucjpms");
/* 395 */     collation[98] = new Collation(98, "eucjpms_bin", 0, "eucjpms");
/* 396 */     collation[99] = new Collation(99, "cp1250_polish_ci", 0, "cp1250");
/* 397 */     collation[101] = new Collation(101, "utf16_unicode_ci", 0, "utf16");
/* 398 */     collation[102] = new Collation(102, "utf16_icelandic_ci", 0, "utf16");
/* 399 */     collation[103] = new Collation(103, "utf16_latvian_ci", 0, "utf16");
/* 400 */     collation[104] = new Collation(104, "utf16_romanian_ci", 0, "utf16");
/* 401 */     collation[105] = new Collation(105, "utf16_slovenian_ci", 0, "utf16");
/* 402 */     collation[106] = new Collation(106, "utf16_polish_ci", 0, "utf16");
/* 403 */     collation[107] = new Collation(107, "utf16_estonian_ci", 0, "utf16");
/* 404 */     collation[108] = new Collation(108, "utf16_spanish_ci", 0, "utf16");
/* 405 */     collation[109] = new Collation(109, "utf16_swedish_ci", 0, "utf16");
/* 406 */     collation[110] = new Collation(110, "utf16_turkish_ci", 0, "utf16");
/* 407 */     collation[111] = new Collation(111, "utf16_czech_ci", 0, "utf16");
/* 408 */     collation[112] = new Collation(112, "utf16_danish_ci", 0, "utf16");
/* 409 */     collation[113] = new Collation(113, "utf16_lithuanian_ci", 0, "utf16");
/* 410 */     collation[114] = new Collation(114, "utf16_slovak_ci", 0, "utf16");
/* 411 */     collation[115] = new Collation(115, "utf16_spanish2_ci", 0, "utf16");
/* 412 */     collation[116] = new Collation(116, "utf16_roman_ci", 0, "utf16");
/* 413 */     collation[117] = new Collation(117, "utf16_persian_ci", 0, "utf16");
/* 414 */     collation[118] = new Collation(118, "utf16_esperanto_ci", 0, "utf16");
/* 415 */     collation[119] = new Collation(119, "utf16_hungarian_ci", 0, "utf16");
/* 416 */     collation[120] = new Collation(120, "utf16_sinhala_ci", 0, "utf16");
/* 417 */     collation[121] = new Collation(121, "utf16_german2_ci", 0, "utf16");
/* 418 */     collation[122] = new Collation(122, "utf16_croatian_ci", 0, "utf16");
/* 419 */     collation[123] = new Collation(123, "utf16_unicode_520_ci", 0, "utf16");
/* 420 */     collation[124] = new Collation(124, "utf16_vietnamese_ci", 0, "utf16");
/* 421 */     collation[''] = new Collation(128, "ucs2_unicode_ci", 0, "ucs2");
/* 422 */     collation[''] = new Collation(129, "ucs2_icelandic_ci", 0, "ucs2");
/* 423 */     collation[''] = new Collation(130, "ucs2_latvian_ci", 0, "ucs2");
/* 424 */     collation[''] = new Collation(131, "ucs2_romanian_ci", 0, "ucs2");
/* 425 */     collation[''] = new Collation(132, "ucs2_slovenian_ci", 0, "ucs2");
/* 426 */     collation[''] = new Collation(133, "ucs2_polish_ci", 0, "ucs2");
/* 427 */     collation[''] = new Collation(134, "ucs2_estonian_ci", 0, "ucs2");
/* 428 */     collation[''] = new Collation(135, "ucs2_spanish_ci", 0, "ucs2");
/* 429 */     collation[''] = new Collation(136, "ucs2_swedish_ci", 0, "ucs2");
/* 430 */     collation[''] = new Collation(137, "ucs2_turkish_ci", 0, "ucs2");
/* 431 */     collation[''] = new Collation(138, "ucs2_czech_ci", 0, "ucs2");
/* 432 */     collation[''] = new Collation(139, "ucs2_danish_ci", 0, "ucs2");
/* 433 */     collation[''] = new Collation(140, "ucs2_lithuanian_ci", 0, "ucs2");
/* 434 */     collation[''] = new Collation(141, "ucs2_slovak_ci", 0, "ucs2");
/* 435 */     collation[''] = new Collation(142, "ucs2_spanish2_ci", 0, "ucs2");
/* 436 */     collation[''] = new Collation(143, "ucs2_roman_ci", 0, "ucs2");
/* 437 */     collation[''] = new Collation(144, "ucs2_persian_ci", 0, "ucs2");
/* 438 */     collation[''] = new Collation(145, "ucs2_esperanto_ci", 0, "ucs2");
/* 439 */     collation[''] = new Collation(146, "ucs2_hungarian_ci", 0, "ucs2");
/* 440 */     collation[''] = new Collation(147, "ucs2_sinhala_ci", 0, "ucs2");
/* 441 */     collation[''] = new Collation(148, "ucs2_german2_ci", 0, "ucs2");
/* 442 */     collation[''] = new Collation(149, "ucs2_croatian_ci", 0, "ucs2");
/* 443 */     collation[''] = new Collation(150, "ucs2_unicode_520_ci", 0, "ucs2");
/* 444 */     collation[''] = new Collation(151, "ucs2_vietnamese_ci", 0, "ucs2");
/* 445 */     collation[''] = new Collation(159, "ucs2_general_mysql500_ci", 0, "ucs2");
/* 446 */     collation[' '] = new Collation(160, "utf32_unicode_ci", 0, "utf32");
/* 447 */     collation['¡'] = new Collation(161, "utf32_icelandic_ci", 0, "utf32");
/* 448 */     collation['¢'] = new Collation(162, "utf32_latvian_ci", 0, "utf32");
/* 449 */     collation['£'] = new Collation(163, "utf32_romanian_ci", 0, "utf32");
/* 450 */     collation['¤'] = new Collation(164, "utf32_slovenian_ci", 0, "utf32");
/* 451 */     collation['¥'] = new Collation(165, "utf32_polish_ci", 0, "utf32");
/* 452 */     collation['¦'] = new Collation(166, "utf32_estonian_ci", 0, "utf32");
/* 453 */     collation['§'] = new Collation(167, "utf32_spanish_ci", 0, "utf32");
/* 454 */     collation['¨'] = new Collation(168, "utf32_swedish_ci", 0, "utf32");
/* 455 */     collation['©'] = new Collation(169, "utf32_turkish_ci", 0, "utf32");
/* 456 */     collation['ª'] = new Collation(170, "utf32_czech_ci", 0, "utf32");
/* 457 */     collation['«'] = new Collation(171, "utf32_danish_ci", 0, "utf32");
/* 458 */     collation['¬'] = new Collation(172, "utf32_lithuanian_ci", 0, "utf32");
/* 459 */     collation['­'] = new Collation(173, "utf32_slovak_ci", 0, "utf32");
/* 460 */     collation['®'] = new Collation(174, "utf32_spanish2_ci", 0, "utf32");
/* 461 */     collation['¯'] = new Collation(175, "utf32_roman_ci", 0, "utf32");
/* 462 */     collation['°'] = new Collation(176, "utf32_persian_ci", 0, "utf32");
/* 463 */     collation['±'] = new Collation(177, "utf32_esperanto_ci", 0, "utf32");
/* 464 */     collation['²'] = new Collation(178, "utf32_hungarian_ci", 0, "utf32");
/* 465 */     collation['³'] = new Collation(179, "utf32_sinhala_ci", 0, "utf32");
/* 466 */     collation['´'] = new Collation(180, "utf32_german2_ci", 0, "utf32");
/* 467 */     collation['µ'] = new Collation(181, "utf32_croatian_ci", 0, "utf32");
/* 468 */     collation['¶'] = new Collation(182, "utf32_unicode_520_ci", 0, "utf32");
/* 469 */     collation['·'] = new Collation(183, "utf32_vietnamese_ci", 0, "utf32");
/* 470 */     collation['À'] = new Collation(192, "utf8_unicode_ci", 0, "utf8");
/* 471 */     collation['Á'] = new Collation(193, "utf8_icelandic_ci", 0, "utf8");
/* 472 */     collation['Â'] = new Collation(194, "utf8_latvian_ci", 0, "utf8");
/* 473 */     collation['Ã'] = new Collation(195, "utf8_romanian_ci", 0, "utf8");
/* 474 */     collation['Ä'] = new Collation(196, "utf8_slovenian_ci", 0, "utf8");
/* 475 */     collation['Å'] = new Collation(197, "utf8_polish_ci", 0, "utf8");
/* 476 */     collation['Æ'] = new Collation(198, "utf8_estonian_ci", 0, "utf8");
/* 477 */     collation['Ç'] = new Collation(199, "utf8_spanish_ci", 0, "utf8");
/* 478 */     collation['È'] = new Collation(200, "utf8_swedish_ci", 0, "utf8");
/* 479 */     collation['É'] = new Collation(201, "utf8_turkish_ci", 0, "utf8");
/* 480 */     collation['Ê'] = new Collation(202, "utf8_czech_ci", 0, "utf8");
/* 481 */     collation['Ë'] = new Collation(203, "utf8_danish_ci", 0, "utf8");
/* 482 */     collation['Ì'] = new Collation(204, "utf8_lithuanian_ci", 0, "utf8");
/* 483 */     collation['Í'] = new Collation(205, "utf8_slovak_ci", 0, "utf8");
/* 484 */     collation['Î'] = new Collation(206, "utf8_spanish2_ci", 0, "utf8");
/* 485 */     collation['Ï'] = new Collation(207, "utf8_roman_ci", 0, "utf8");
/* 486 */     collation['Ð'] = new Collation(208, "utf8_persian_ci", 0, "utf8");
/* 487 */     collation['Ñ'] = new Collation(209, "utf8_esperanto_ci", 0, "utf8");
/* 488 */     collation['Ò'] = new Collation(210, "utf8_hungarian_ci", 0, "utf8");
/* 489 */     collation['Ó'] = new Collation(211, "utf8_sinhala_ci", 0, "utf8");
/* 490 */     collation['Ô'] = new Collation(212, "utf8_german2_ci", 0, "utf8");
/* 491 */     collation['Õ'] = new Collation(213, "utf8_croatian_ci", 0, "utf8");
/* 492 */     collation['Ö'] = new Collation(214, "utf8_unicode_520_ci", 0, "utf8");
/* 493 */     collation['×'] = new Collation(215, "utf8_vietnamese_ci", 0, "utf8");
/* 494 */     collation['ß'] = new Collation(223, "utf8_general_mysql500_ci", 0, "utf8");
/* 495 */     collation['à'] = new Collation(224, "utf8mb4_unicode_ci", 0, "utf8mb4");
/* 496 */     collation['á'] = new Collation(225, "utf8mb4_icelandic_ci", 0, "utf8mb4");
/* 497 */     collation['â'] = new Collation(226, "utf8mb4_latvian_ci", 0, "utf8mb4");
/* 498 */     collation['ã'] = new Collation(227, "utf8mb4_romanian_ci", 0, "utf8mb4");
/* 499 */     collation['ä'] = new Collation(228, "utf8mb4_slovenian_ci", 0, "utf8mb4");
/* 500 */     collation['å'] = new Collation(229, "utf8mb4_polish_ci", 0, "utf8mb4");
/* 501 */     collation['æ'] = new Collation(230, "utf8mb4_estonian_ci", 0, "utf8mb4");
/* 502 */     collation['ç'] = new Collation(231, "utf8mb4_spanish_ci", 0, "utf8mb4");
/* 503 */     collation['è'] = new Collation(232, "utf8mb4_swedish_ci", 0, "utf8mb4");
/* 504 */     collation['é'] = new Collation(233, "utf8mb4_turkish_ci", 0, "utf8mb4");
/* 505 */     collation['ê'] = new Collation(234, "utf8mb4_czech_ci", 0, "utf8mb4");
/* 506 */     collation['ë'] = new Collation(235, "utf8mb4_danish_ci", 0, "utf8mb4");
/* 507 */     collation['ì'] = new Collation(236, "utf8mb4_lithuanian_ci", 0, "utf8mb4");
/* 508 */     collation['í'] = new Collation(237, "utf8mb4_slovak_ci", 0, "utf8mb4");
/* 509 */     collation['î'] = new Collation(238, "utf8mb4_spanish2_ci", 0, "utf8mb4");
/* 510 */     collation['ï'] = new Collation(239, "utf8mb4_roman_ci", 0, "utf8mb4");
/* 511 */     collation['ð'] = new Collation(240, "utf8mb4_persian_ci", 0, "utf8mb4");
/* 512 */     collation['ñ'] = new Collation(241, "utf8mb4_esperanto_ci", 0, "utf8mb4");
/* 513 */     collation['ò'] = new Collation(242, "utf8mb4_hungarian_ci", 0, "utf8mb4");
/* 514 */     collation['ó'] = new Collation(243, "utf8mb4_sinhala_ci", 0, "utf8mb4");
/* 515 */     collation['ô'] = new Collation(244, "utf8mb4_german2_ci", 0, "utf8mb4");
/* 516 */     collation['õ'] = new Collation(245, "utf8mb4_croatian_ci", 0, "utf8mb4");
/* 517 */     collation['ö'] = new Collation(246, "utf8mb4_unicode_520_ci", 0, "utf8mb4");
/* 518 */     collation['÷'] = new Collation(247, "utf8mb4_vietnamese_ci", 0, "utf8mb4");
/*     */     
/* 520 */     COLLATION_INDEX_TO_COLLATION_NAME = new String['ÿ'];
/* 521 */     COLLATION_INDEX_TO_CHARSET = new MysqlCharset['ÿ'];
/* 522 */     Map<String, Integer> charsetNameToCollationIndexMap = new TreeMap();
/* 523 */     Map<String, Integer> charsetNameToCollationPriorityMap = new TreeMap();
/* 524 */     Set<Integer> tempUTF8MB4Indexes = new HashSet();
/*     */     
/* 526 */     for (int i = 1; i < 255; i++) {
/* 527 */       COLLATION_INDEX_TO_COLLATION_NAME[i] = collation[i].collationName;
/* 528 */       COLLATION_INDEX_TO_CHARSET[i] = collation[i].mysqlCharset;
/* 529 */       String charsetName = collation[i].mysqlCharset.charsetName;
/*     */       
/* 531 */       if ((!charsetNameToCollationIndexMap.containsKey(charsetName)) || (((Integer)charsetNameToCollationPriorityMap.get(charsetName)).intValue() < collation[i].priority)) {
/* 532 */         charsetNameToCollationIndexMap.put(charsetName, Integer.valueOf(i));
/* 533 */         charsetNameToCollationPriorityMap.put(charsetName, Integer.valueOf(collation[i].priority));
/*     */       }
/*     */       
/*     */ 
/* 537 */       if (charsetName.equals("utf8mb4")) {
/* 538 */         tempUTF8MB4Indexes.add(Integer.valueOf(i));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 543 */     for (int i = 1; i < 255; i++) {
/* 544 */       if (COLLATION_INDEX_TO_COLLATION_NAME[i] == null) {
/* 545 */         throw new RuntimeException("Assertion failure: No mapping from charset index " + i + " to a mysql collation");
/*     */       }
/* 547 */       if (COLLATION_INDEX_TO_COLLATION_NAME[i] == null) {
/* 548 */         throw new RuntimeException("Assertion failure: No mapping from charset index " + i + " to a Java character set");
/*     */       }
/*     */     }
/*     */     
/* 552 */     CHARSET_NAME_TO_COLLATION_INDEX = Collections.unmodifiableMap(charsetNameToCollationIndexMap);
/* 553 */     UTF8MB4_INDEXES = Collections.unmodifiableSet(tempUTF8MB4Indexes);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 558 */     Map<String, String> tempMap = new HashMap();
/* 559 */     tempMap.put("czech", "latin2");
/* 560 */     tempMap.put("danish", "latin1");
/* 561 */     tempMap.put("dutch", "latin1");
/* 562 */     tempMap.put("english", "latin1");
/* 563 */     tempMap.put("estonian", "latin7");
/* 564 */     tempMap.put("french", "latin1");
/* 565 */     tempMap.put("german", "latin1");
/* 566 */     tempMap.put("greek", "greek");
/* 567 */     tempMap.put("hungarian", "latin2");
/* 568 */     tempMap.put("italian", "latin1");
/* 569 */     tempMap.put("japanese", "ujis");
/* 570 */     tempMap.put("japanese-sjis", "sjis");
/* 571 */     tempMap.put("korean", "euckr");
/* 572 */     tempMap.put("norwegian", "latin1");
/* 573 */     tempMap.put("norwegian-ny", "latin1");
/* 574 */     tempMap.put("polish", "latin2");
/* 575 */     tempMap.put("portuguese", "latin1");
/* 576 */     tempMap.put("romanian", "latin2");
/* 577 */     tempMap.put("russian", "koi8r");
/* 578 */     tempMap.put("serbian", "cp1250");
/* 579 */     tempMap.put("slovak", "latin2");
/* 580 */     tempMap.put("spanish", "latin1");
/* 581 */     tempMap.put("swedish", "latin1");
/* 582 */     tempMap.put("ukrainian", "koi8u");
/* 583 */     ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET = Collections.unmodifiableMap(tempMap);
/*     */   }
/*     */   
/*     */   public static final String getMysqlCharsetForJavaEncoding(String javaEncoding, Connection conn) throws SQLException
/*     */   {
/*     */     try {
/* 589 */       List<MysqlCharset> mysqlCharsets = (List)JAVA_ENCODING_UC_TO_MYSQL_CHARSET.get(javaEncoding.toUpperCase(Locale.ENGLISH));
/*     */       
/* 591 */       if (mysqlCharsets != null) {
/* 592 */         Iterator<MysqlCharset> iter = mysqlCharsets.iterator();
/*     */         
/* 594 */         MysqlCharset versionedProp = null;
/*     */         
/* 596 */         while (iter.hasNext()) {
/* 597 */           MysqlCharset charset = (MysqlCharset)iter.next();
/*     */           
/* 599 */           if (conn == null)
/*     */           {
/*     */ 
/* 602 */             return charset.charsetName;
/*     */           }
/*     */           
/* 605 */           if ((versionedProp == null) || (versionedProp.major < charset.major) || (versionedProp.minor < charset.minor) || (versionedProp.subminor < charset.subminor) || (versionedProp.priority < charset.priority))
/*     */           {
/* 607 */             if (charset.isOkayForVersion(conn)) {
/* 608 */               versionedProp = charset;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 613 */         if (versionedProp != null) {
/* 614 */           return versionedProp.charsetName;
/*     */         }
/*     */       }
/*     */       
/* 618 */       return null;
/*     */     } catch (SQLException ex) {
/* 620 */       throw ex;
/*     */     } catch (RuntimeException ex) {
/* 622 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 623 */       sqlEx.initCause(ex);
/* 624 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */   
/*     */   public static int getCollationIndexForJavaEncoding(String javaEncoding, java.sql.Connection conn) throws SQLException
/*     */   {
/* 630 */     String charsetName = getMysqlCharsetForJavaEncoding(javaEncoding, (Connection)conn);
/* 631 */     if (charsetName != null) {
/* 632 */       Integer ci = (Integer)CHARSET_NAME_TO_COLLATION_INDEX.get(charsetName);
/* 633 */       if (ci != null) {
/* 634 */         return ci.intValue();
/*     */       }
/*     */     }
/* 637 */     return 0;
/*     */   }
/*     */   
/*     */   public static String getMysqlCharsetNameForCollationIndex(Integer collationIndex) {
/* 641 */     if ((collationIndex != null) && (collationIndex.intValue() > 0) && (collationIndex.intValue() < 255)) {
/* 642 */       return COLLATION_INDEX_TO_CHARSET[collationIndex.intValue()].charsetName;
/*     */     }
/* 644 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getJavaEncodingForMysqlCharset(String mysqlCharsetName, String javaEncoding)
/*     */   {
/* 662 */     String res = javaEncoding;
/* 663 */     MysqlCharset cs = (MysqlCharset)CHARSET_NAME_TO_CHARSET.get(mysqlCharsetName);
/* 664 */     if (cs != null) {
/* 665 */       res = cs.getMatchingJavaEncoding(javaEncoding);
/*     */     }
/* 667 */     return res;
/*     */   }
/*     */   
/*     */   public static String getJavaEncodingForMysqlCharset(String mysqlCharsetName) {
/* 671 */     return getJavaEncodingForMysqlCharset(mysqlCharsetName, null);
/*     */   }
/*     */   
/*     */   public static String getJavaEncodingForCollationIndex(Integer collationIndex, String javaEncoding) {
/* 675 */     if ((collationIndex != null) && (collationIndex.intValue() > 0) && (collationIndex.intValue() < 255)) {
/* 676 */       MysqlCharset cs = COLLATION_INDEX_TO_CHARSET[collationIndex.intValue()];
/* 677 */       return cs.getMatchingJavaEncoding(javaEncoding);
/*     */     }
/* 679 */     return null;
/*     */   }
/*     */   
/*     */   public static String getJavaEncodingForCollationIndex(Integer collationIndex) {
/* 683 */     return getJavaEncodingForCollationIndex(collationIndex, null);
/*     */   }
/*     */   
/*     */   static final int getNumberOfCharsetsConfigured() {
/* 687 */     return numberOfEncodingsConfigured;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final String getCharacterEncodingForErrorMessages(ConnectionImpl conn)
/*     */     throws SQLException
/*     */   {
/* 707 */     if (conn.versionMeetsMinimum(5, 5, 0)) {
/* 708 */       String errorMessageCharsetName = conn.getServerVariable("jdbc.local.character_set_results");
/* 709 */       if (errorMessageCharsetName != null) {
/* 710 */         String javaEncoding = getJavaEncodingForMysqlCharset(errorMessageCharsetName);
/* 711 */         if (javaEncoding != null) {
/* 712 */           return javaEncoding;
/*     */         }
/*     */       }
/*     */       
/* 716 */       return "UTF-8";
/*     */     }
/*     */     
/* 719 */     String errorMessageFile = conn.getServerVariable("language");
/*     */     
/* 721 */     if ((errorMessageFile == null) || (errorMessageFile.length() == 0))
/*     */     {
/* 723 */       return "Cp1252";
/*     */     }
/*     */     
/* 726 */     int endWithoutSlash = errorMessageFile.length();
/*     */     
/* 728 */     if ((errorMessageFile.endsWith("/")) || (errorMessageFile.endsWith("\\"))) {
/* 729 */       endWithoutSlash--;
/*     */     }
/*     */     
/* 732 */     int lastSlashIndex = errorMessageFile.lastIndexOf('/', endWithoutSlash - 1);
/*     */     
/* 734 */     if (lastSlashIndex == -1) {
/* 735 */       lastSlashIndex = errorMessageFile.lastIndexOf('\\', endWithoutSlash - 1);
/*     */     }
/*     */     
/* 738 */     if (lastSlashIndex == -1) {
/* 739 */       lastSlashIndex = 0;
/*     */     }
/*     */     
/* 742 */     if ((lastSlashIndex == endWithoutSlash) || (endWithoutSlash < lastSlashIndex))
/*     */     {
/* 744 */       return "Cp1252";
/*     */     }
/*     */     
/* 747 */     errorMessageFile = errorMessageFile.substring(lastSlashIndex + 1, endWithoutSlash);
/*     */     
/* 749 */     String errorMessageEncodingMysql = (String)ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET.get(errorMessageFile);
/*     */     
/* 751 */     if (errorMessageEncodingMysql == null)
/*     */     {
/* 753 */       return "Cp1252";
/*     */     }
/*     */     
/* 756 */     String javaEncoding = getJavaEncodingForMysqlCharset(errorMessageEncodingMysql);
/*     */     
/* 758 */     if (javaEncoding == null)
/*     */     {
/* 760 */       return "Cp1252";
/*     */     }
/*     */     
/* 763 */     return javaEncoding;
/*     */   }
/*     */   
/*     */   static final boolean requiresEscapeEasternUnicode(String javaEncodingName) {
/* 767 */     return ESCAPE_ENCODINGS.contains(javaEncodingName.toUpperCase(Locale.ENGLISH));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final boolean isMultibyteCharset(String javaEncodingName)
/*     */   {
/* 776 */     return MULTIBYTE_ENCODINGS.contains(javaEncodingName.toUpperCase(Locale.ENGLISH));
/*     */   }
/*     */   
/*     */   public static int getMblen(String charsetName) {
/* 780 */     if (charsetName != null) {
/* 781 */       MysqlCharset cs = (MysqlCharset)CHARSET_NAME_TO_CHARSET.get(charsetName);
/* 782 */       if (cs != null) {
/* 783 */         return cs.mblen;
/*     */       }
/*     */     }
/* 786 */     return 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\CharsetMapping.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */