/*     */ package com.mchange.v1.util;
/*     */ 
/*     */ import com.mchange.v2.lang.ObjectUtils;
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
/*     */ public final class ArrayUtils
/*     */ {
/*     */   public static int indexOf(Object[] array, Object o)
/*     */   {
/*  36 */     int i = 0; for (int len = array.length; i < len; i++)
/*  37 */       if (o.equals(array[i])) return i;
/*  38 */     return -1;
/*     */   }
/*     */   
/*     */   public static int identityIndexOf(Object[] array, Object o)
/*     */   {
/*  43 */     int i = 0; for (int len = array.length; i < len; i++)
/*  44 */       if (o == array[i]) return i;
/*  45 */     return -1;
/*     */   }
/*     */   
/*     */   public static boolean startsWith(byte[] checkMe, byte[] maybePrefix)
/*     */   {
/*  50 */     int cm_len = checkMe.length;
/*  51 */     int mp_len = maybePrefix.length;
/*  52 */     if (cm_len < mp_len)
/*  53 */       return false;
/*  54 */     for (int i = 0; i < mp_len; i++)
/*  55 */       if (checkMe[i] != maybePrefix[i])
/*  56 */         return false;
/*  57 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int hashArray(Object[] oo)
/*     */   {
/*  65 */     int len = oo.length;
/*  66 */     int out = len;
/*  67 */     for (int i = 0; i < len; i++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  72 */       int elem_hash = ObjectUtils.hashOrZero(oo[i]);
/*  73 */       int rot = i % 32;
/*  74 */       int rot_hash = elem_hash >>> rot;
/*  75 */       rot_hash |= elem_hash << 32 - rot;
/*  76 */       out ^= rot_hash;
/*     */     }
/*  78 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int hashArray(int[] ii)
/*     */   {
/*  86 */     int len = ii.length;
/*  87 */     int out = len;
/*  88 */     for (int i = 0; i < len; i++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  93 */       int elem_hash = ii[i];
/*  94 */       int rot = i % 32;
/*  95 */       int rot_hash = elem_hash >>> rot;
/*  96 */       rot_hash |= elem_hash << 32 - rot;
/*  97 */       out ^= rot_hash;
/*     */     }
/*  99 */     return out;
/*     */   }
/*     */   
/*     */   public static int hashOrZeroArray(Object[] oo) {
/* 103 */     return oo == null ? 0 : hashArray(oo);
/*     */   }
/*     */   
/* 106 */   public static int hashOrZeroArray(int[] ii) { return ii == null ? 0 : hashArray(ii); }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static String stringifyContents(Object[] array)
/*     */   {
/* 113 */     StringBuffer sb = new StringBuffer();
/* 114 */     sb.append("[ ");
/* 115 */     int i = 0; for (int len = array.length; i < len; i++)
/*     */     {
/* 117 */       if (i != 0)
/* 118 */         sb.append(", ");
/* 119 */       sb.append(array[i].toString());
/*     */     }
/* 121 */     sb.append(" ]");
/* 122 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String toString(String[] strings, int guessed_len)
/*     */   {
/* 129 */     StringBuffer sb = new StringBuffer(guessed_len);
/* 130 */     boolean first = true;
/* 131 */     sb.append('[');
/* 132 */     int i = 0; for (int len = strings.length; i < len; i++)
/*     */     {
/* 134 */       if (first) {
/* 135 */         first = false;
/*     */       } else
/* 137 */         sb.append(',');
/* 138 */       sb.append(strings[i]);
/*     */     }
/* 140 */     sb.append(']');
/* 141 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static String toString(boolean[] arr)
/*     */   {
/* 146 */     String[] strings = new String[arr.length];
/* 147 */     int chars = 0;
/* 148 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 150 */       String str = String.valueOf(arr[i]);
/* 151 */       chars += str.length();
/* 152 */       strings[i] = str;
/*     */     }
/* 154 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(byte[] arr)
/*     */   {
/* 159 */     String[] strings = new String[arr.length];
/* 160 */     int chars = 0;
/* 161 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 163 */       String str = String.valueOf(arr[i]);
/* 164 */       chars += str.length();
/* 165 */       strings[i] = str;
/*     */     }
/* 167 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(char[] arr)
/*     */   {
/* 172 */     String[] strings = new String[arr.length];
/* 173 */     int chars = 0;
/* 174 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 176 */       String str = String.valueOf(arr[i]);
/* 177 */       chars += str.length();
/* 178 */       strings[i] = str;
/*     */     }
/* 180 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(short[] arr)
/*     */   {
/* 185 */     String[] strings = new String[arr.length];
/* 186 */     int chars = 0;
/* 187 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 189 */       String str = String.valueOf(arr[i]);
/* 190 */       chars += str.length();
/* 191 */       strings[i] = str;
/*     */     }
/* 193 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(int[] arr)
/*     */   {
/* 198 */     String[] strings = new String[arr.length];
/* 199 */     int chars = 0;
/* 200 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 202 */       String str = String.valueOf(arr[i]);
/* 203 */       chars += str.length();
/* 204 */       strings[i] = str;
/*     */     }
/* 206 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(long[] arr)
/*     */   {
/* 211 */     String[] strings = new String[arr.length];
/* 212 */     int chars = 0;
/* 213 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 215 */       String str = String.valueOf(arr[i]);
/* 216 */       chars += str.length();
/* 217 */       strings[i] = str;
/*     */     }
/* 219 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(float[] arr)
/*     */   {
/* 224 */     String[] strings = new String[arr.length];
/* 225 */     int chars = 0;
/* 226 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 228 */       String str = String.valueOf(arr[i]);
/* 229 */       chars += str.length();
/* 230 */       strings[i] = str;
/*     */     }
/* 232 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(double[] arr)
/*     */   {
/* 237 */     String[] strings = new String[arr.length];
/* 238 */     int chars = 0;
/* 239 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/* 241 */       String str = String.valueOf(arr[i]);
/* 242 */       chars += str.length();
/* 243 */       strings[i] = str;
/*     */     }
/* 245 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */   
/*     */   public static String toString(Object[] arr)
/*     */   {
/* 250 */     String[] strings = new String[arr.length];
/* 251 */     int chars = 0;
/* 252 */     int i = 0; for (int len = arr.length; i < len; i++)
/*     */     {
/*     */ 
/* 255 */       Object o = arr[i];
/* 256 */       String str; String str; if ((o instanceof Object[])) {
/* 257 */         str = toString((Object[])o); } else { String str;
/* 258 */         if ((o instanceof double[])) {
/* 259 */           str = toString((double[])o); } else { String str;
/* 260 */           if ((o instanceof float[])) {
/* 261 */             str = toString((float[])o); } else { String str;
/* 262 */             if ((o instanceof long[])) {
/* 263 */               str = toString((long[])o); } else { String str;
/* 264 */               if ((o instanceof int[])) {
/* 265 */                 str = toString((int[])o); } else { String str;
/* 266 */                 if ((o instanceof short[])) {
/* 267 */                   str = toString((short[])o); } else { String str;
/* 268 */                   if ((o instanceof char[])) {
/* 269 */                     str = toString((char[])o); } else { String str;
/* 270 */                     if ((o instanceof byte[])) {
/* 271 */                       str = toString((byte[])o); } else { String str;
/* 272 */                       if ((o instanceof boolean[])) {
/* 273 */                         str = toString((boolean[])o);
/*     */                       } else
/* 275 */                         str = String.valueOf(arr[i]); } } } } } } } }
/* 276 */       chars += str.length();
/* 277 */       strings[i] = str;
/*     */     }
/* 279 */     return toString(strings, chars + arr.length + 1);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\util\ArrayUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */