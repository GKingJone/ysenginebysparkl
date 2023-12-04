/*     */ package com.mysql.fabric.proto.xmlrpc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Random;
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
/*     */ public class DigestAuthentication
/*     */ {
/*  41 */   private static Random random = new Random();
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getChallengeHeader(String url)
/*     */     throws IOException
/*     */   {
/*  48 */     HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
/*  49 */     conn.setDoOutput(true);
/*  50 */     conn.getOutputStream().close();
/*     */     try {
/*  52 */       conn.getInputStream().close();
/*     */     } catch (IOException ex) {
/*  54 */       if (401 == conn.getResponseCode())
/*     */       {
/*     */ 
/*     */ 
/*  58 */         String hdr = conn.getHeaderField("WWW-Authenticate");
/*  59 */         if ((hdr != null) && (!"".equals(hdr)))
/*  60 */           return hdr;
/*     */       } else {
/*  62 */         if (400 == conn.getResponseCode())
/*     */         {
/*  64 */           throw new IOException("Fabric returns status 400. If authentication is disabled on the Fabric node, omit the `fabricUsername' and `fabricPassword' properties from your connection.");
/*     */         }
/*     */         
/*  67 */         throw ex;
/*     */       }
/*     */     }
/*  70 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String calculateMD5RequestDigest(String uri, String username, String password, String realm, String nonce, String nc, String cnonce, String qop)
/*     */   {
/*  78 */     String reqA1 = username + ":" + realm + ":" + password;
/*     */     
/*  80 */     String reqA2 = "POST:" + uri;
/*     */     
/*  82 */     String hashA1 = checksumMD5(reqA1);
/*  83 */     String hashA2 = checksumMD5(reqA2);
/*  84 */     String requestDigest = digestMD5(hashA1, nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + hashA2);
/*     */     
/*  86 */     return requestDigest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String checksumMD5(String data)
/*     */   {
/*  93 */     MessageDigest md5 = null;
/*     */     try {
/*  95 */       md5 = MessageDigest.getInstance("MD5");
/*     */     } catch (NoSuchAlgorithmException ex) {
/*  97 */       throw new RuntimeException("Unable to create MD5 instance", ex);
/*     */     }
/*     */     
/* 100 */     return hexEncode(md5.digest(data.getBytes()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String digestMD5(String secret, String data)
/*     */   {
/* 107 */     return checksumMD5(secret + ":" + data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String hexEncode(byte[] data)
/*     */   {
/* 114 */     StringBuilder sb = new StringBuilder();
/* 115 */     for (int i = 0; i < data.length; i++) {
/* 116 */       sb.append(String.format("%02x", new Object[] { Byte.valueOf(data[i]) }));
/*     */     }
/* 118 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String serializeDigestResponse(Map<String, String> paramMap)
/*     */   {
/* 127 */     StringBuilder sb = new StringBuilder("Digest ");
/*     */     
/* 129 */     boolean prefixComma = false;
/* 130 */     for (Entry<String, String> entry : paramMap.entrySet()) {
/* 131 */       if (!prefixComma) {
/* 132 */         prefixComma = true;
/*     */       } else {
/* 134 */         sb.append(", ");
/*     */       }
/* 136 */       sb.append((String)entry.getKey());
/* 137 */       sb.append("=");
/* 138 */       sb.append((String)entry.getValue());
/*     */     }
/*     */     
/* 141 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Map<String, String> parseDigestChallenge(String headerValue)
/*     */   {
/* 150 */     if (!headerValue.startsWith("Digest ")) {
/* 151 */       throw new IllegalArgumentException("Header is not a digest challenge");
/*     */     }
/*     */     
/* 154 */     String params = headerValue.substring(7);
/* 155 */     Map<String, String> paramMap = new HashMap();
/* 156 */     for (String param : params.split(",\\s*")) {
/* 157 */       String[] pieces = param.split("=");
/* 158 */       paramMap.put(pieces[0], pieces[1].replaceAll("^\"(.*)\"$", "$1"));
/*     */     }
/* 160 */     return paramMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String generateCnonce(String nonce, String nc)
/*     */   {
/* 171 */     byte[] buf = new byte[8];
/* 172 */     random.nextBytes(buf);
/* 173 */     for (int i = 0; i < 8; i++) {
/* 174 */       buf[i] = ((byte)(32 + buf[i] % 95));
/*     */     }
/*     */     
/* 177 */     String combo = String.format("%s:%s:%s:%s", new Object[] { nonce, nc, new Date().toGMTString(), new String(buf) });
/* 178 */     MessageDigest sha1 = null;
/*     */     try {
/* 180 */       sha1 = MessageDigest.getInstance("SHA-1");
/*     */     } catch (NoSuchAlgorithmException ex) {
/* 182 */       throw new RuntimeException("Unable to create SHA-1 instance", ex);
/*     */     }
/*     */     
/* 185 */     return hexEncode(sha1.digest(combo.getBytes()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String quoteParam(String param)
/*     */   {
/* 193 */     if ((param.contains("\"")) || (param.contains("'"))) {
/* 194 */       throw new IllegalArgumentException("Invalid character in parameter");
/*     */     }
/* 196 */     return "\"" + param + "\"";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String generateAuthorizationHeader(Map<String, String> digestChallenge, String username, String password)
/*     */   {
/* 204 */     String nonce = (String)digestChallenge.get("nonce");
/* 205 */     String nc = "00000001";
/* 206 */     String cnonce = generateCnonce(nonce, nc);
/* 207 */     String qop = "auth";
/* 208 */     String uri = "/RPC2";
/* 209 */     String realm = (String)digestChallenge.get("realm");
/* 210 */     String opaque = (String)digestChallenge.get("opaque");
/*     */     
/* 212 */     String requestDigest = calculateMD5RequestDigest(uri, username, password, realm, nonce, nc, cnonce, qop);
/* 213 */     Map<String, String> digestResponseMap = new HashMap();
/* 214 */     digestResponseMap.put("algorithm", "MD5");
/* 215 */     digestResponseMap.put("username", quoteParam(username));
/* 216 */     digestResponseMap.put("realm", quoteParam(realm));
/* 217 */     digestResponseMap.put("nonce", quoteParam(nonce));
/* 218 */     digestResponseMap.put("uri", quoteParam(uri));
/* 219 */     digestResponseMap.put("qop", qop);
/* 220 */     digestResponseMap.put("nc", nc);
/* 221 */     digestResponseMap.put("cnonce", quoteParam(cnonce));
/* 222 */     digestResponseMap.put("response", quoteParam(requestDigest));
/* 223 */     digestResponseMap.put("opaque", quoteParam(opaque));
/*     */     
/* 225 */     return serializeDigestResponse(digestResponseMap);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\proto\xmlrpc\DigestAuthentication.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */