/*     */ package com.mchange.v2.lang;
/*     */ 
/*     */ import com.mchange.v1.util.StringTokenizerUtils;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
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
/*     */ public final class VersionUtils
/*     */ {
/*  31 */   private static final MLogger logger = MLog.getLogger(VersionUtils.class);
/*     */   
/*  33 */   private static final int[] DFLT_VERSION_ARRAY = { 1, 1 };
/*     */   
/*     */   private static final int[] JDK_VERSION_ARRAY;
/*     */   
/*     */   private static final int JDK_VERSION;
/*     */   private static final Integer NUM_BITS;
/*     */   
/*     */   static
/*     */   {
/*  42 */     String vstr = System.getProperty("java.version");
/*     */     int[] v;
/*  44 */     int[] v; if (vstr == null)
/*     */     {
/*  46 */       if (logger.isLoggable(MLevel.WARNING))
/*  47 */         logger.warning("Could not find java.version System property. Defaulting to JDK 1.1");
/*  48 */       v = DFLT_VERSION_ARRAY;
/*     */     }
/*     */     else {
/*     */       try {
/*  52 */         v = extractVersionNumberArray(vstr, "._");
/*     */       }
/*     */       catch (NumberFormatException e) {
/*  55 */         if (logger.isLoggable(MLevel.WARNING))
/*  56 */           logger.warning("java.version ''" + vstr + "'' could not be parsed. Defaulting to JDK 1.1.");
/*  57 */         v = DFLT_VERSION_ARRAY;
/*     */       }
/*     */     }
/*  60 */     int jdkv = 0;
/*  61 */     if (v.length > 0)
/*  62 */       jdkv += v[0] * 10;
/*  63 */     if (v.length > 1) {
/*  64 */       jdkv += v[1];
/*     */     }
/*  66 */     JDK_VERSION_ARRAY = v;
/*  67 */     JDK_VERSION = jdkv;
/*     */     
/*     */ 
/*     */     Integer tmpNumBits;
/*     */     
/*     */     try
/*     */     {
/*  74 */       String numBitsStr = System.getProperty("sun.arch.data.model");
/*  75 */       Integer tmpNumBits; if (numBitsStr == null) {
/*  76 */         tmpNumBits = null;
/*     */       } else {
/*  78 */         tmpNumBits = new Integer(numBitsStr);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  82 */       tmpNumBits = null;
/*     */     }
/*     */     
/*  85 */     if ((tmpNumBits == null) || (tmpNumBits.intValue() == 32) || (tmpNumBits.intValue() == 64)) {
/*  86 */       NUM_BITS = tmpNumBits;
/*     */     }
/*     */     else {
/*  89 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  90 */         logger.warning("Determined a surprising jvmNumerOfBits: " + tmpNumBits + ". Setting jvmNumberOfBits to unknown (null).");
/*     */       }
/*  92 */       NUM_BITS = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Integer jvmNumberOfBits()
/*     */   {
/* 102 */     return NUM_BITS;
/*     */   }
/*     */   
/* 105 */   public static boolean isJavaVersion10() { return JDK_VERSION == 10; }
/*     */   
/*     */   public static boolean isJavaVersion11() {
/* 108 */     return JDK_VERSION == 11;
/*     */   }
/*     */   
/* 111 */   public static boolean isJavaVersion12() { return JDK_VERSION == 12; }
/*     */   
/*     */   public static boolean isJavaVersion13() {
/* 114 */     return JDK_VERSION == 13;
/*     */   }
/*     */   
/* 117 */   public static boolean isJavaVersion14() { return JDK_VERSION == 14; }
/*     */   
/*     */   public static boolean isJavaVersion15() {
/* 120 */     return JDK_VERSION == 15;
/*     */   }
/*     */   
/* 123 */   public static boolean isAtLeastJavaVersion10() { return JDK_VERSION >= 10; }
/*     */   
/*     */   public static boolean isAtLeastJavaVersion11() {
/* 126 */     return JDK_VERSION >= 11;
/*     */   }
/*     */   
/* 129 */   public static boolean isAtLeastJavaVersion12() { return JDK_VERSION >= 12; }
/*     */   
/*     */   public static boolean isAtLeastJavaVersion13() {
/* 132 */     return JDK_VERSION >= 13;
/*     */   }
/*     */   
/* 135 */   public static boolean isAtLeastJavaVersion14() { return JDK_VERSION >= 14; }
/*     */   
/*     */   public static boolean isAtLeastJavaVersion15() {
/* 138 */     return JDK_VERSION >= 15;
/*     */   }
/*     */   
/*     */   public static int[] extractVersionNumberArray(String versionString, String delims) throws NumberFormatException
/*     */   {
/* 143 */     String[] intStrs = StringTokenizerUtils.tokenizeToArray(versionString, delims, false);
/* 144 */     int len = intStrs.length;
/* 145 */     int[] out = new int[len];
/* 146 */     for (int i = 0; i < len; i++)
/* 147 */       out[i] = Integer.parseInt(intStrs[i]);
/* 148 */     return out;
/*     */   }
/*     */   
/*     */   public boolean prefixMatches(int[] pfx, int[] fullVersion)
/*     */   {
/* 153 */     if (pfx.length > fullVersion.length) {
/* 154 */       return false;
/*     */     }
/*     */     
/* 157 */     int i = 0; for (int len = pfx.length; i < len; i++)
/* 158 */       if (pfx[i] != fullVersion[i])
/* 159 */         return false;
/* 160 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int lexicalCompareVersionNumberArrays(int[] a, int[] b)
/*     */   {
/* 166 */     int alen = a.length;
/* 167 */     int blen = b.length;
/* 168 */     for (int i = 0; i < alen; i++)
/*     */     {
/* 170 */       if (i == blen)
/* 171 */         return 1;
/* 172 */       if (a[i] > b[i])
/* 173 */         return 1;
/* 174 */       if (a[i] < b[i])
/* 175 */         return -1;
/*     */     }
/* 177 */     if (blen > alen) {
/* 178 */       return -1;
/*     */     }
/* 180 */     return 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\lang\VersionUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */