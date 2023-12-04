/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication.HeaderInfo;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Attributes;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class DigestAuthentication
/*     */   extends AbstractAuthentication
/*     */ {
/*  53 */   private static final Pattern PARAM_PATTERN = Pattern.compile("([^=]+)=(.*)");
/*     */   
/*     */ 
/*     */ 
/*     */   private final String user;
/*     */   
/*     */ 
/*     */   private final String password;
/*     */   
/*     */ 
/*     */ 
/*     */   public DigestAuthentication(URI uri, String realm, String user, String password)
/*     */   {
/*  66 */     super(uri, realm);
/*  67 */     this.user = user;
/*  68 */     this.password = password;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/*  74 */     return "Digest";
/*     */   }
/*     */   
/*     */ 
/*     */   public Authentication.Result authenticate(Request request, ContentResponse response, Authentication.HeaderInfo headerInfo, Attributes context)
/*     */   {
/*  80 */     Map<String, String> params = parseParameters(headerInfo.getParameters());
/*  81 */     String nonce = (String)params.get("nonce");
/*  82 */     if ((nonce == null) || (nonce.length() == 0))
/*  83 */       return null;
/*  84 */     String opaque = (String)params.get("opaque");
/*  85 */     String algorithm = (String)params.get("algorithm");
/*  86 */     if (algorithm == null)
/*  87 */       algorithm = "MD5";
/*  88 */     MessageDigest digester = getMessageDigest(algorithm);
/*  89 */     if (digester == null)
/*  90 */       return null;
/*  91 */     String serverQOP = (String)params.get("qop");
/*  92 */     String clientQOP = null;
/*  93 */     if (serverQOP != null)
/*     */     {
/*  95 */       List<String> serverQOPValues = StringUtil.csvSplit(null, serverQOP, 0, serverQOP.length());
/*  96 */       if (serverQOPValues.contains("auth")) {
/*  97 */         clientQOP = "auth";
/*  98 */       } else if (serverQOPValues.contains("auth-int")) {
/*  99 */         clientQOP = "auth-int";
/*     */       }
/*     */     }
/* 102 */     return new DigestResult(headerInfo.getHeader(), response.getContent(), getRealm(), this.user, this.password, algorithm, nonce, clientQOP, opaque);
/*     */   }
/*     */   
/*     */   private Map<String, String> parseParameters(String wwwAuthenticate)
/*     */   {
/* 107 */     Map<String, String> result = new HashMap();
/* 108 */     List<String> parts = splitParams(wwwAuthenticate);
/* 109 */     for (String part : parts)
/*     */     {
/* 111 */       Matcher matcher = PARAM_PATTERN.matcher(part);
/* 112 */       if (matcher.matches())
/*     */       {
/* 114 */         String name = matcher.group(1).trim().toLowerCase(Locale.ENGLISH);
/* 115 */         String value = matcher.group(2).trim();
/* 116 */         if ((value.startsWith("\"")) && (value.endsWith("\"")))
/* 117 */           value = value.substring(1, value.length() - 1);
/* 118 */         result.put(name, value);
/*     */       }
/*     */     }
/* 121 */     return result;
/*     */   }
/*     */   
/*     */   private List<String> splitParams(String paramString)
/*     */   {
/* 126 */     List<String> result = new ArrayList();
/* 127 */     int start = 0;
/* 128 */     for (int i = 0; i < paramString.length(); i++)
/*     */     {
/* 130 */       int quotes = 0;
/* 131 */       char ch = paramString.charAt(i);
/* 132 */       switch (ch)
/*     */       {
/*     */       case '\\': 
/* 135 */         i++;
/* 136 */         break;
/*     */       case '"': 
/* 138 */         quotes++;
/* 139 */         break;
/*     */       case ',': 
/* 141 */         if (quotes % 2 == 0)
/*     */         {
/* 143 */           String element = paramString.substring(start, i).trim();
/* 144 */           if (element.length() > 0)
/* 145 */             result.add(element);
/* 146 */           start = i + 1;
/*     */         }
/*     */         
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/* 153 */     result.add(paramString.substring(start, paramString.length()).trim());
/* 154 */     return result;
/*     */   }
/*     */   
/*     */   private MessageDigest getMessageDigest(String algorithm)
/*     */   {
/*     */     try
/*     */     {
/* 161 */       return MessageDigest.getInstance(algorithm);
/*     */     }
/*     */     catch (NoSuchAlgorithmException x) {}
/*     */     
/* 165 */     return null;
/*     */   }
/*     */   
/*     */   private class DigestResult
/*     */     implements Authentication.Result
/*     */   {
/* 171 */     private final AtomicInteger nonceCount = new AtomicInteger();
/*     */     private final HttpHeader header;
/*     */     private final byte[] content;
/*     */     private final String realm;
/*     */     private final String user;
/*     */     private final String password;
/*     */     private final String algorithm;
/*     */     private final String nonce;
/*     */     private final String qop;
/*     */     private final String opaque;
/*     */     
/*     */     public DigestResult(HttpHeader header, byte[] content, String realm, String user, String password, String algorithm, String nonce, String qop, String opaque)
/*     */     {
/* 184 */       this.header = header;
/* 185 */       this.content = content;
/* 186 */       this.realm = realm;
/* 187 */       this.user = user;
/* 188 */       this.password = password;
/* 189 */       this.algorithm = algorithm;
/* 190 */       this.nonce = nonce;
/* 191 */       this.qop = qop;
/* 192 */       this.opaque = opaque;
/*     */     }
/*     */     
/*     */ 
/*     */     public URI getURI()
/*     */     {
/* 198 */       return DigestAuthentication.this.getURI();
/*     */     }
/*     */     
/*     */ 
/*     */     public void apply(Request request)
/*     */     {
/* 204 */       MessageDigest digester = DigestAuthentication.this.getMessageDigest(this.algorithm);
/* 205 */       if (digester == null) {
/* 206 */         return;
/*     */       }
/* 208 */       String A1 = this.user + ":" + this.realm + ":" + this.password;
/* 209 */       String hashA1 = toHexString(digester.digest(A1.getBytes(StandardCharsets.ISO_8859_1)));
/*     */       
/* 211 */       URI uri = request.getURI();
/* 212 */       String A2 = request.getMethod() + ":" + uri;
/* 213 */       if ("auth-int".equals(this.qop))
/* 214 */         A2 = A2 + ":" + toHexString(digester.digest(this.content));
/* 215 */       String hashA2 = toHexString(digester.digest(A2.getBytes(StandardCharsets.ISO_8859_1)));
/*     */       String A3;
/*     */       String nonceCount;
/*     */       String clientNonce;
/*     */       String A3;
/* 220 */       if (this.qop != null)
/*     */       {
/* 222 */         String nonceCount = nextNonceCount();
/* 223 */         String clientNonce = newClientNonce();
/* 224 */         A3 = hashA1 + ":" + this.nonce + ":" + nonceCount + ":" + clientNonce + ":" + this.qop + ":" + hashA2;
/*     */       }
/*     */       else
/*     */       {
/* 228 */         nonceCount = null;
/* 229 */         clientNonce = null;
/* 230 */         A3 = hashA1 + ":" + this.nonce + ":" + hashA2;
/*     */       }
/* 232 */       String hashA3 = toHexString(digester.digest(A3.getBytes(StandardCharsets.ISO_8859_1)));
/*     */       
/* 234 */       StringBuilder value = new StringBuilder("Digest");
/* 235 */       value.append(" username=\"").append(this.user).append("\"");
/* 236 */       value.append(", realm=\"").append(this.realm).append("\"");
/* 237 */       value.append(", nonce=\"").append(this.nonce).append("\"");
/* 238 */       if (this.opaque != null)
/* 239 */         value.append(", opaque=\"").append(this.opaque).append("\"");
/* 240 */       value.append(", algorithm=\"").append(this.algorithm).append("\"");
/* 241 */       value.append(", uri=\"").append(uri).append("\"");
/* 242 */       if (this.qop != null)
/*     */       {
/* 244 */         value.append(", qop=\"").append(this.qop).append("\"");
/* 245 */         value.append(", nc=\"").append(nonceCount).append("\"");
/* 246 */         value.append(", cnonce=\"").append(clientNonce).append("\"");
/*     */       }
/* 248 */       value.append(", response=\"").append(hashA3).append("\"");
/*     */       
/* 250 */       request.header(this.header, value.toString());
/*     */     }
/*     */     
/*     */     private String nextNonceCount()
/*     */     {
/* 255 */       String padding = "00000000";
/* 256 */       String next = Integer.toHexString(this.nonceCount.incrementAndGet()).toLowerCase(Locale.ENGLISH);
/* 257 */       return padding.substring(0, padding.length() - next.length()) + next;
/*     */     }
/*     */     
/*     */     private String newClientNonce()
/*     */     {
/* 262 */       Random random = new Random();
/* 263 */       byte[] bytes = new byte[8];
/* 264 */       random.nextBytes(bytes);
/* 265 */       return toHexString(bytes);
/*     */     }
/*     */     
/*     */     private String toHexString(byte[] bytes)
/*     */     {
/* 270 */       return TypeUtil.toHexString(bytes).toLowerCase(Locale.ENGLISH);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\DigestAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */