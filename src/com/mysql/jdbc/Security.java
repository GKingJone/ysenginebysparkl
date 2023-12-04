/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
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
/*     */ public class Security
/*     */ {
/*     */   private static final char PVERSION41_CHAR = '*';
/*     */   private static final int SHA1_HASH_SIZE = 20;
/*     */   
/*     */   private static int charVal(char c)
/*     */   {
/*  42 */     return (c >= 'A') && (c <= 'Z') ? c - 'A' + 10 : (c >= '0') && (c <= '9') ? c - '0' : c - 'a' + 10;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] createKeyFromOldPassword(String passwd)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  71 */     passwd = makeScrambledPassword(passwd);
/*     */     
/*     */ 
/*  74 */     int[] salt = getSaltFromPassword(passwd);
/*     */     
/*     */ 
/*  77 */     return getBinaryPassword(salt, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] getBinaryPassword(int[] salt, boolean usingNewPasswords)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  88 */     int val = 0;
/*     */     
/*  90 */     byte[] binaryPassword = new byte[20];
/*     */     
/*  92 */     if (usingNewPasswords) {
/*  93 */       int pos = 0;
/*     */       
/*  95 */       for (int i = 0; i < 4; i++) {
/*  96 */         val = salt[i];
/*     */         
/*  98 */         for (int t = 3; t >= 0; t--) {
/*  99 */           binaryPassword[(pos++)] = ((byte)(val & 0xFF));
/* 100 */           val >>= 8;
/*     */         }
/*     */       }
/*     */       
/* 104 */       return binaryPassword;
/*     */     }
/*     */     
/* 107 */     int offset = 0;
/*     */     
/* 109 */     for (int i = 0; i < 2; i++) {
/* 110 */       val = salt[i];
/*     */       
/* 112 */       for (int t = 3; t >= 0; t--) {
/* 113 */         binaryPassword[(t + offset)] = ((byte)(val % 256));
/* 114 */         val >>= 8;
/*     */       }
/*     */       
/* 117 */       offset += 4;
/*     */     }
/*     */     
/* 120 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/*     */     
/* 122 */     md.update(binaryPassword, 0, 8);
/*     */     
/* 124 */     return md.digest();
/*     */   }
/*     */   
/*     */   private static int[] getSaltFromPassword(String password) {
/* 128 */     int[] result = new int[6];
/*     */     
/* 130 */     if ((password == null) || (password.length() == 0)) {
/* 131 */       return result;
/*     */     }
/*     */     
/* 134 */     if (password.charAt(0) == '*')
/*     */     {
/* 136 */       String saltInHex = password.substring(1, 5);
/*     */       
/* 138 */       int val = 0;
/*     */       
/* 140 */       for (int i = 0; i < 4; i++) {
/* 141 */         val = (val << 4) + charVal(saltInHex.charAt(i));
/*     */       }
/*     */       
/* 144 */       return result;
/*     */     }
/*     */     
/* 147 */     int resultPos = 0;
/* 148 */     int pos = 0;
/* 149 */     int length = password.length();
/*     */     
/* 151 */     while (pos < length) {
/* 152 */       int val = 0;
/*     */       
/* 154 */       for (int i = 0; i < 8; i++) {
/* 155 */         val = (val << 4) + charVal(password.charAt(pos++));
/*     */       }
/*     */       
/* 158 */       result[(resultPos++)] = val;
/*     */     }
/*     */     
/* 161 */     return result;
/*     */   }
/*     */   
/*     */   private static String longToHex(long val) {
/* 165 */     String longHex = Long.toHexString(val);
/*     */     
/* 167 */     int length = longHex.length();
/*     */     
/* 169 */     if (length < 8) {
/* 170 */       int padding = 8 - length;
/* 171 */       StringBuilder buf = new StringBuilder();
/*     */       
/* 173 */       for (int i = 0; i < padding; i++) {
/* 174 */         buf.append("0");
/*     */       }
/*     */       
/* 177 */       buf.append(longHex);
/*     */       
/* 179 */       return buf.toString();
/*     */     }
/*     */     
/* 182 */     return longHex.substring(0, 8);
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
/*     */   static String makeScrambledPassword(String password)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 199 */     long[] passwordHash = Util.hashPre41Password(password);
/* 200 */     StringBuilder scramble = new StringBuilder();
/*     */     
/* 202 */     scramble.append(longToHex(passwordHash[0]));
/* 203 */     scramble.append(longToHex(passwordHash[1]));
/*     */     
/* 205 */     return scramble.toString();
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
/*     */   public static void xorString(byte[] from, byte[] to, byte[] scramble, int length)
/*     */   {
/* 223 */     int pos = 0;
/* 224 */     int scrambleLength = scramble.length;
/*     */     
/* 226 */     while (pos < length) {
/* 227 */       to[pos] = ((byte)(from[pos] ^ scramble[(pos % scrambleLength)]));
/* 228 */       pos++;
/*     */     }
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
/*     */   static byte[] passwordHashStage1(String password)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 244 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/* 245 */     StringBuilder cleansedPassword = new StringBuilder();
/*     */     
/* 247 */     int passwordLength = password.length();
/*     */     
/* 249 */     for (int i = 0; i < passwordLength; i++) {
/* 250 */       char c = password.charAt(i);
/*     */       
/* 252 */       if ((c != ' ') && (c != '\t'))
/*     */       {
/*     */ 
/*     */ 
/* 256 */         cleansedPassword.append(c);
/*     */       }
/*     */     }
/* 259 */     return md.digest(StringUtils.getBytes(cleansedPassword.toString()));
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
/*     */   static byte[] passwordHashStage2(byte[] hashedPassword, byte[] salt)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 276 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/*     */     
/*     */ 
/* 279 */     md.update(salt, 0, 4);
/*     */     
/* 281 */     md.update(hashedPassword, 0, 20);
/*     */     
/* 283 */     return md.digest();
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
/*     */ 
/*     */   public static byte[] scramble411(String password, String seed, String passwordEncoding)
/*     */     throws NoSuchAlgorithmException, UnsupportedEncodingException
/*     */   {
/* 304 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/*     */     
/* 306 */     byte[] passwordHashStage1 = md.digest((passwordEncoding == null) || (passwordEncoding.length() == 0) ? StringUtils.getBytes(password) : StringUtils.getBytes(password, passwordEncoding));
/*     */     
/* 308 */     md.reset();
/*     */     
/* 310 */     byte[] passwordHashStage2 = md.digest(passwordHashStage1);
/* 311 */     md.reset();
/*     */     
/* 313 */     byte[] seedAsBytes = StringUtils.getBytes(seed, "ASCII");
/* 314 */     md.update(seedAsBytes);
/* 315 */     md.update(passwordHashStage2);
/*     */     
/* 317 */     byte[] toBeXord = md.digest();
/*     */     
/* 319 */     int numToXor = toBeXord.length;
/*     */     
/* 321 */     for (int i = 0; i < numToXor; i++) {
/* 322 */       toBeXord[i] = ((byte)(toBeXord[i] ^ passwordHashStage1[i]));
/*     */     }
/*     */     
/* 325 */     return toBeXord;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Security.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */