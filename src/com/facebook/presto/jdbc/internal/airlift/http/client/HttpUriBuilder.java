/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Ascii;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.CharMatcher;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Charsets;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Splitter;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Iterables;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.LinkedListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.net.HostAndPort;
/*     */ import com.facebook.presto.jdbc.internal.guava.primitives.Bytes;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.CharacterCodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public class HttpUriBuilder
/*     */ {
/*     */   private String scheme;
/*     */   private String host;
/*  34 */   private int port = -1;
/*  35 */   private String path = "";
/*  36 */   private final ListMultimap<String, String> params = LinkedListMultimap.create();
/*     */   
/*  38 */   private static final byte[] PCHAR = { 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 46, 95, 126, 33, 36, 39, 40, 41, 42, 43, 44, 59, 61, 58, 64 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  45 */   private static final byte[] ALLOWED_PATH_CHARS = Bytes.concat(new byte[][] { PCHAR, { 47, 38 } });
/*  46 */   private static final byte[] ALLOWED_QUERY_CHARS = Bytes.concat(new byte[][] { PCHAR, { 47, 63 } });
/*     */   
/*     */ 
/*     */   private HttpUriBuilder() {}
/*     */   
/*     */ 
/*     */   private HttpUriBuilder(URI previous)
/*     */   {
/*  54 */     this.scheme = previous.getScheme();
/*  55 */     this.host = previous.getHost();
/*  56 */     this.port = previous.getPort();
/*  57 */     this.path = percentDecode(previous.getRawPath());
/*  58 */     this.params.putAll(parseParams(previous.getRawQuery()));
/*     */   }
/*     */   
/*     */   public static HttpUriBuilder uriBuilder()
/*     */   {
/*  63 */     return new HttpUriBuilder();
/*     */   }
/*     */   
/*     */   public static HttpUriBuilder uriBuilderFrom(URI uri)
/*     */   {
/*  68 */     Preconditions.checkNotNull(uri, "uri is null");
/*     */     
/*  70 */     return new HttpUriBuilder(uri);
/*     */   }
/*     */   
/*     */   public HttpUriBuilder scheme(String scheme)
/*     */   {
/*  75 */     Preconditions.checkNotNull(scheme, "scheme is null");
/*     */     
/*  77 */     this.scheme = scheme;
/*  78 */     return this;
/*     */   }
/*     */   
/*     */   public HttpUriBuilder host(String host)
/*     */   {
/*  83 */     Preconditions.checkNotNull(host, "host is null");
/*  84 */     Preconditions.checkArgument(!host.startsWith("["), "host starts with a bracket");
/*  85 */     Preconditions.checkArgument(!host.endsWith("]"), "host ends with a bracket");
/*  86 */     if (host.contains(":")) {
/*  87 */       host = "[" + host + "]";
/*     */     }
/*  89 */     this.host = host;
/*  90 */     return this;
/*     */   }
/*     */   
/*     */   public HttpUriBuilder port(int port)
/*     */   {
/*  95 */     Preconditions.checkArgument((port >= 1) && (port <= 65535), "port must be in the range 1-65535");
/*  96 */     this.port = port;
/*  97 */     return this;
/*     */   }
/*     */   
/*     */   public HttpUriBuilder defaultPort()
/*     */   {
/* 102 */     this.port = -1;
/* 103 */     return this;
/*     */   }
/*     */   
/*     */   public HttpUriBuilder hostAndPort(HostAndPort hostAndPort)
/*     */   {
/* 108 */     Preconditions.checkNotNull(hostAndPort, "hostAndPort is null");
/* 109 */     this.host = bracketedHostString(hostAndPort);
/* 110 */     this.port = (hostAndPort.hasPort() ? hostAndPort.getPort() : -1);
/* 111 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpUriBuilder replacePath(String path)
/*     */   {
/* 119 */     Preconditions.checkNotNull(path, "path is null");
/*     */     
/* 121 */     if ((!path.equals("")) && (!path.startsWith("/"))) {
/* 122 */       path = "/" + path;
/*     */     }
/*     */     
/* 125 */     this.path = path;
/* 126 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpUriBuilder appendPath(String path)
/*     */   {
/* 137 */     Preconditions.checkNotNull(path, "path is null");
/*     */     
/* 139 */     StringBuilder builder = new StringBuilder(this.path);
/* 140 */     if (!this.path.endsWith("/")) {
/* 141 */       builder.append('/');
/*     */     }
/*     */     
/* 144 */     if (path.startsWith("/")) {
/* 145 */       path = path.substring(1);
/*     */     }
/*     */     
/* 148 */     builder.append(path);
/*     */     
/* 150 */     this.path = builder.toString();
/*     */     
/* 152 */     return this;
/*     */   }
/*     */   
/*     */   public HttpUriBuilder replaceParameter(String name, String... values)
/*     */   {
/* 157 */     return replaceParameter(name, Arrays.asList(values));
/*     */   }
/*     */   
/*     */   public HttpUriBuilder replaceParameter(String name, Iterable<String> values)
/*     */   {
/* 162 */     Preconditions.checkNotNull(name, "name is null");
/*     */     
/* 164 */     this.params.removeAll(name);
/* 165 */     addParameter(name, values);
/*     */     
/* 167 */     return this;
/*     */   }
/*     */   
/*     */   public HttpUriBuilder addParameter(String name, String... values)
/*     */   {
/* 172 */     return addParameter(name, Arrays.asList(values));
/*     */   }
/*     */   
/*     */   public HttpUriBuilder addParameter(String name, Iterable<String> values)
/*     */   {
/* 177 */     Preconditions.checkNotNull(name, "name is null");
/*     */     
/* 179 */     if (Iterables.isEmpty(values)) {
/* 180 */       this.params.put(name, null);
/*     */     }
/*     */     
/* 183 */     for (String value : values) {
/* 184 */       this.params.put(name, value);
/*     */     }
/*     */     
/* 187 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 193 */     StringBuilder builder = new StringBuilder();
/* 194 */     builder.append(this.scheme);
/* 195 */     builder.append("://");
/* 196 */     if (this.host != null) {
/* 197 */       builder.append(this.host);
/*     */     }
/* 199 */     if (this.port != -1) {
/* 200 */       builder.append(':');
/* 201 */       builder.append(this.port);
/*     */     }
/*     */     
/* 204 */     String path = this.path;
/* 205 */     if ((path.equals("")) && (!this.params.isEmpty())) {
/* 206 */       path = "/";
/*     */     }
/*     */     
/* 209 */     builder.append(encode(path, ALLOWED_PATH_CHARS));
/*     */     Iterator<Map.Entry<String, String>> iterator;
/* 211 */     if (!this.params.isEmpty()) {
/* 212 */       builder.append('?');
/*     */       
/* 214 */       for (iterator = this.params.entries().iterator(); iterator.hasNext();) {
/* 215 */         Map.Entry<String, String> entry = (Map.Entry)iterator.next();
/*     */         
/* 217 */         builder.append(encode((String)entry.getKey(), ALLOWED_QUERY_CHARS));
/* 218 */         if (entry.getValue() != null) {
/* 219 */           builder.append('=');
/* 220 */           builder.append(encode((String)entry.getValue(), ALLOWED_QUERY_CHARS));
/*     */         }
/*     */         
/* 223 */         if (iterator.hasNext()) {
/* 224 */           builder.append('&');
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 229 */     return builder.toString();
/*     */   }
/*     */   
/*     */   public URI build()
/*     */   {
/* 234 */     Preconditions.checkState(this.scheme != null, "scheme has not been set");
/* 235 */     return URI.create(toString());
/*     */   }
/*     */   
/*     */   private ListMultimap<String, String> parseParams(String query)
/*     */   {
/* 240 */     LinkedListMultimap<String, String> result = LinkedListMultimap.create();
/*     */     
/* 242 */     if (query != null)
/*     */     {
/*     */ 
/* 245 */       Iterable<String> pairs = Splitter.on("&").omitEmptyStrings().split(query);
/*     */       
/* 247 */       for (String pair : pairs) {
/* 248 */         String[] parts = pair.split("=", 2);
/* 249 */         result.put(percentDecode(parts[0]), percentDecode(parts[1]));
/*     */       }
/*     */     }
/*     */     
/* 253 */     return result;
/*     */   }
/*     */   
/*     */   private String encode(String input, byte... allowed)
/*     */   {
/* 258 */     StringBuilder builder = new StringBuilder();
/*     */     
/* 260 */     ByteBuffer buffer = Charsets.UTF_8.encode(input);
/* 261 */     while (buffer.remaining() > 0) {
/* 262 */       byte b = buffer.get();
/*     */       
/* 264 */       if (Bytes.contains(allowed, b)) {
/* 265 */         builder.append((char)b);
/*     */       }
/*     */       else {
/* 268 */         builder.append('%');
/* 269 */         builder.append(Ascii.toUpperCase(Character.forDigit(b >>> 4 & 0xF, 16)));
/* 270 */         builder.append(Ascii.toUpperCase(Character.forDigit(b & 0xF, 16)));
/*     */       }
/*     */     }
/*     */     
/* 274 */     return builder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String percentDecode(String encoded)
/*     */   {
/* 282 */     Preconditions.checkArgument(CharMatcher.ASCII.matchesAllOf(encoded), "string must be ASCII");
/*     */     
/* 284 */     ByteArrayOutputStream out = new ByteArrayOutputStream(encoded.length());
/* 285 */     for (int i = 0; i < encoded.length(); i++) {
/* 286 */       char c = encoded.charAt(i);
/*     */       
/* 288 */       if (c == '%') {
/* 289 */         Preconditions.checkArgument(i + 2 < encoded.length(), "percent encoded value is truncated");
/*     */         
/* 291 */         int high = Character.digit(encoded.charAt(i + 1), 16);
/* 292 */         int low = Character.digit(encoded.charAt(i + 2), 16);
/*     */         
/* 294 */         Preconditions.checkArgument((high != -1) && (low != -1), "percent encoded value is not a valid hex string: ", new Object[] { encoded.substring(i, i + 2) });
/*     */         
/* 296 */         int value = high << 4 | low;
/* 297 */         out.write(value);
/* 298 */         i += 2;
/*     */       }
/*     */       else {
/* 301 */         out.write(c);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 306 */       return 
/*     */       
/*     */ 
/* 309 */         Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(out.toByteArray())).toString();
/*     */     }
/*     */     catch (CharacterCodingException e) {
/* 312 */       throw new IllegalArgumentException("input does not represent a proper UTF8-encoded string");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static String bracketedHostString(HostAndPort hostAndPort)
/*     */   {
/* 319 */     String host = hostAndPort.getHostText();
/* 320 */     if (hostAndPort.toString().startsWith("[")) {
/* 321 */       return "[" + host + "]";
/*     */     }
/* 323 */     return host;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HttpUriBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */