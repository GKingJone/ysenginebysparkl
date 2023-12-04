/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Splitter;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
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
/*     */ public class CacheControl
/*     */ {
/*     */   private static final String SEPARATOR = ",";
/*     */   private static final String COMPLEX_HEADER_EXPRESSION = "(([\\w-]+=\"[^\"]*\")|([\\w-]+=[\\w]+)|([\\w-]+))";
/*  40 */   private static final Pattern COMPLEX_HEADER_PATTERN = Pattern.compile("(([\\w-]+=\"[^\"]*\")|([\\w-]+=[\\w]+)|([\\w-]+))");
/*     */   
/*     */   private static final String PUBLIC = "public";
/*     */   
/*     */   private static final String PRIVATE = "private";
/*     */   private static final String NO_CACHE = "no-cache";
/*     */   private static final String NO_STORE = "no-store";
/*     */   private static final String NO_TRANSFORM = "no-transform";
/*     */   private static final String MUST_REVALIDATE = "must-revalidate";
/*     */   private static final String PROXY_REVALIDATE = "proxy-revalidate";
/*     */   private static final String MAX_AGE = "max-age";
/*     */   private static final String SMAX_AGE = "s-maxage";
/*  52 */   private int maxAge = -1;
/*  53 */   private int sMaxAge = -1;
/*  54 */   private boolean isPrivate = false;
/*  55 */   private boolean noCache = false;
/*  56 */   private boolean noStore = false;
/*  57 */   private boolean noTransform = true;
/*  58 */   private boolean mustRevalidate = false;
/*  59 */   private boolean proxyRevalidate = false;
/*  60 */   private Map<String, String> cacheExtensions = null;
/*  61 */   private List<String> noCacheFields = null;
/*  62 */   private List<String> privateFields = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, String> getCacheExtension()
/*     */   {
/*  70 */     if (this.cacheExtensions == null) {
/*  71 */       this.cacheExtensions = new HashMap();
/*     */     }
/*  73 */     return this.cacheExtensions;
/*     */   }
/*     */   
/*     */   public int getMaxAge()
/*     */   {
/*  78 */     return this.maxAge;
/*     */   }
/*     */   
/*     */   public List<String> getNoCacheFields()
/*     */   {
/*  83 */     if (this.noCacheFields == null) {
/*  84 */       this.noCacheFields = new ArrayList();
/*     */     }
/*  86 */     return this.noCacheFields;
/*     */   }
/*     */   
/*     */   public List<String> getPrivateFields()
/*     */   {
/*  91 */     if (this.privateFields == null) {
/*  92 */       this.privateFields = new ArrayList();
/*     */     }
/*  94 */     return this.privateFields;
/*     */   }
/*     */   
/*     */   public int getSMaxAge()
/*     */   {
/*  99 */     return this.sMaxAge;
/*     */   }
/*     */   
/*     */   public boolean isMustRevalidate()
/*     */   {
/* 104 */     return this.mustRevalidate;
/*     */   }
/*     */   
/*     */   public boolean isNoCache()
/*     */   {
/* 109 */     return this.noCache;
/*     */   }
/*     */   
/*     */   public boolean isNoStore()
/*     */   {
/* 114 */     return this.noStore;
/*     */   }
/*     */   
/*     */   public boolean isNoTransform()
/*     */   {
/* 119 */     return this.noTransform;
/*     */   }
/*     */   
/*     */   public boolean isPrivate()
/*     */   {
/* 124 */     return this.isPrivate;
/*     */   }
/*     */   
/*     */   public boolean isProxyRevalidate()
/*     */   {
/* 129 */     return this.proxyRevalidate;
/*     */   }
/*     */   
/*     */   public void setMaxAge(int maxAge)
/*     */   {
/* 134 */     this.maxAge = maxAge;
/*     */   }
/*     */   
/*     */   public void setMustRevalidate(boolean mustRevalidate)
/*     */   {
/* 139 */     this.mustRevalidate = mustRevalidate;
/*     */   }
/*     */   
/*     */   public void setNoCache(boolean noCache)
/*     */   {
/* 144 */     this.noCache = noCache;
/*     */   }
/*     */   
/*     */   public void setNoStore(boolean noStore)
/*     */   {
/* 149 */     this.noStore = noStore;
/*     */   }
/*     */   
/*     */   public void setNoTransform(boolean noTransform)
/*     */   {
/* 154 */     this.noTransform = noTransform;
/*     */   }
/*     */   
/*     */   public void setPrivate(boolean isPrivate)
/*     */   {
/* 159 */     this.isPrivate = isPrivate;
/*     */   }
/*     */   
/*     */   public void setProxyRevalidate(boolean proxyRevalidate)
/*     */   {
/* 164 */     this.proxyRevalidate = proxyRevalidate;
/*     */   }
/*     */   
/*     */   public void setSMaxAge(int sMaxAge)
/*     */   {
/* 169 */     this.sMaxAge = sMaxAge;
/*     */   }
/*     */   
/*     */   public static CacheControl valueOf(String string)
/*     */   {
/* 174 */     CacheControl cacheControl = new CacheControl();
/*     */     
/*     */ 
/* 177 */     cacheControl.setNoTransform(false);
/*     */     
/* 179 */     List<String> tokens = getTokens(string);
/* 180 */     for (String rawToken : tokens) {
/* 181 */       String token = rawToken.trim();
/* 182 */       if (token.startsWith("max-age")) {
/* 183 */         cacheControl.setMaxAge(Integer.parseInt(token.substring("max-age".length() + 1)));
/*     */       }
/* 185 */       else if (token.startsWith("s-maxage")) {
/* 186 */         cacheControl.setSMaxAge(Integer.parseInt(token.substring("s-maxage".length() + 1)));
/*     */       }
/* 188 */       else if (!token.startsWith("public"))
/*     */       {
/*     */ 
/* 191 */         if (token.startsWith("no-store")) {
/* 192 */           cacheControl.setNoStore(true);
/*     */         }
/* 194 */         else if (token.startsWith("no-transform")) {
/* 195 */           cacheControl.setNoTransform(true);
/*     */         }
/* 197 */         else if (token.startsWith("must-revalidate")) {
/* 198 */           cacheControl.setMustRevalidate(true);
/*     */         }
/* 200 */         else if (token.startsWith("proxy-revalidate")) {
/* 201 */           cacheControl.setProxyRevalidate(true);
/*     */         }
/* 203 */         else if (token.startsWith("private")) {
/* 204 */           cacheControl.setPrivate(true);
/* 205 */           addFields(cacheControl.getPrivateFields(), token);
/*     */         }
/* 207 */         else if (token.startsWith("no-cache")) {
/* 208 */           cacheControl.setNoCache(true);
/* 209 */           addFields(cacheControl.getNoCacheFields(), token);
/*     */         }
/*     */         else {
/* 212 */           List<String> pair = ImmutableList.copyOf(Splitter.on("=").limit(2).split(token));
/* 213 */           if (pair.size() == 2) {
/* 214 */             cacheControl.getCacheExtension().put(pair.get(0), pair.get(1));
/*     */           }
/*     */           else {
/* 217 */             cacheControl.getCacheExtension().put(pair.get(0), "");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 222 */     return cacheControl;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 227 */     StringBuilder buffer = new StringBuilder();
/* 228 */     if (isPrivate()) {
/* 229 */       buffer.append("private");
/* 230 */       handleFields(getPrivateFields(), buffer);
/* 231 */       buffer.append(",");
/*     */     }
/* 233 */     if (isNoCache()) {
/* 234 */       buffer.append("no-cache");
/* 235 */       handleFields(getNoCacheFields(), buffer);
/* 236 */       buffer.append(",");
/*     */     }
/* 238 */     if (isNoStore()) {
/* 239 */       buffer.append("no-store").append(",");
/*     */     }
/* 241 */     if (isNoTransform()) {
/* 242 */       buffer.append("no-transform").append(",");
/*     */     }
/* 244 */     if (isMustRevalidate()) {
/* 245 */       buffer.append("must-revalidate").append(",");
/*     */     }
/* 247 */     if (isProxyRevalidate()) {
/* 248 */       buffer.append("proxy-revalidate").append(",");
/*     */     }
/* 250 */     if (getMaxAge() != -1) {
/* 251 */       buffer.append("max-age").append('=').append(getMaxAge()).append(",");
/*     */     }
/* 253 */     if (getSMaxAge() != -1) {
/* 254 */       buffer.append("s-maxage").append('=').append(getSMaxAge()).append(",");
/*     */     }
/*     */     
/* 257 */     Map<String, String> extension = getCacheExtension();
/* 258 */     for (Entry<String, String> entry : extension.entrySet()) {
/* 259 */       buffer.append((String)entry.getKey());
/* 260 */       String value = (String)entry.getValue();
/* 261 */       if (value != null) {
/* 262 */         buffer.append("=");
/* 263 */         if (value.indexOf(' ') != -1) {
/* 264 */           buffer.append('"').append(value).append('"');
/*     */         }
/*     */         else {
/* 267 */           buffer.append(value);
/*     */         }
/*     */       }
/* 270 */       buffer.append(",");
/*     */     }
/*     */     
/* 273 */     String string = buffer.toString();
/* 274 */     if (string.endsWith(",")) {
/* 275 */       string = string.substring(0, string.length() - 1);
/*     */     }
/* 277 */     return string;
/*     */   }
/*     */   
/*     */   private static void handleFields(List<String> fields, StringBuilder buffer) {
/* 281 */     if (fields.isEmpty()) {
/* 282 */       return;
/*     */     }
/* 284 */     buffer.append('=');
/* 285 */     buffer.append('"');
/* 286 */     for (Iterator<String> it = fields.iterator(); it.hasNext();) {
/* 287 */       buffer.append((String)it.next());
/* 288 */       if (it.hasNext()) {
/* 289 */         buffer.append(',');
/*     */       }
/*     */     }
/* 292 */     buffer.append('"');
/*     */   }
/*     */   
/*     */   private static List<String> getTokens(String string)
/*     */   {
/* 297 */     if (string.contains("\"")) {
/* 298 */       List<String> values = new ArrayList(4);
/* 299 */       Matcher m = COMPLEX_HEADER_PATTERN.matcher(string);
/* 300 */       while (m.find()) {
/* 301 */         String val = m.group().trim();
/* 302 */         if (val.length() > 0) {
/* 303 */           values.add(val);
/*     */         }
/*     */       }
/* 306 */       return ImmutableList.copyOf(values);
/*     */     }
/*     */     
/* 309 */     return ImmutableList.copyOf(Splitter.on(",").split(string));
/*     */   }
/*     */   
/*     */ 
/*     */   private static void addFields(List<String> fields, String token)
/*     */   {
/* 315 */     int i = token.indexOf('=');
/* 316 */     if (i != -1) {
/* 317 */       String f = i == token.length() + 1 ? "" : token.substring(i + 1);
/* 318 */       if ((f.length() >= 2) && (f.startsWith("\"")) && (f.endsWith("\""))) {
/* 319 */         f = f.length() == 2 ? "" : f.substring(1, f.length() - 1);
/* 320 */         if (f.length() > 0) {
/* 321 */           List<String> values = ImmutableList.copyOf(Splitter.on(",").split(f));
/* 322 */           for (String v : values) {
/* 323 */             fields.add(v.trim());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 333 */     return Objects.hashCode(new Object[] { Integer.valueOf(this.maxAge), 
/* 334 */       Integer.valueOf(this.sMaxAge), 
/* 335 */       Boolean.valueOf(this.isPrivate), 
/* 336 */       Boolean.valueOf(this.noCache), 
/* 337 */       Boolean.valueOf(this.noStore), 
/* 338 */       Boolean.valueOf(this.noTransform), 
/* 339 */       Boolean.valueOf(this.mustRevalidate), 
/* 340 */       Boolean.valueOf(this.proxyRevalidate), this.cacheExtensions, this.noCacheFields, this.privateFields });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 349 */     if (this == obj) {
/* 350 */       return true;
/*     */     }
/* 352 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 353 */       return false;
/*     */     }
/*     */     
/* 356 */     CacheControl other = (CacheControl)obj;
/* 357 */     return (Objects.equal(Integer.valueOf(this.maxAge), Integer.valueOf(other.maxAge))) && 
/* 358 */       (Objects.equal(Integer.valueOf(this.sMaxAge), Integer.valueOf(other.sMaxAge))) && 
/* 359 */       (Objects.equal(Boolean.valueOf(this.isPrivate), Boolean.valueOf(other.isPrivate))) && 
/* 360 */       (Objects.equal(Boolean.valueOf(this.noCache), Boolean.valueOf(other.noCache))) && 
/* 361 */       (Objects.equal(Boolean.valueOf(this.noStore), Boolean.valueOf(other.noStore))) && 
/* 362 */       (Objects.equal(Boolean.valueOf(this.noTransform), Boolean.valueOf(other.noTransform))) && 
/* 363 */       (Objects.equal(Boolean.valueOf(this.mustRevalidate), Boolean.valueOf(other.mustRevalidate))) && 
/* 364 */       (Objects.equal(Boolean.valueOf(this.proxyRevalidate), Boolean.valueOf(other.proxyRevalidate))) && 
/* 365 */       (Objects.equal(this.cacheExtensions, other.cacheExtensions)) && 
/* 366 */       (Objects.equal(this.noCacheFields, other.noCacheFields)) && 
/* 367 */       (Objects.equal(this.privateFields, other.privateFields));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\CacheControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */