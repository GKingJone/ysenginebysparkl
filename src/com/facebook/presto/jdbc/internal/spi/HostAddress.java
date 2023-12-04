/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URI;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Objects;
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
/*     */ public class HostAddress
/*     */ {
/*     */   private static final int NO_PORT = -1;
/*     */   private final String host;
/*     */   private final int port;
/*     */   
/*     */   private HostAddress(String host, int port)
/*     */   {
/*  76 */     this.host = host;
/*  77 */     this.port = port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHostText()
/*     */   {
/*  89 */     return this.host;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasPort()
/*     */   {
/*  97 */     return this.port >= 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 109 */     if (!hasPort()) {
/* 110 */       throw new IllegalStateException("no port");
/*     */     }
/* 112 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPortOrDefault(int defaultPort)
/*     */   {
/* 120 */     return hasPort() ? this.port : defaultPort;
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
/*     */   public static HostAddress fromParts(String host, int port)
/*     */   {
/* 136 */     if (!isValidPort(port)) {
/* 137 */       throw new IllegalArgumentException("Port is invalid: " + port);
/*     */     }
/* 139 */     HostAddress parsedHost = fromString(host);
/* 140 */     if (parsedHost.hasPort()) {
/* 141 */       throw new IllegalArgumentException("host contains a port declaration: " + host);
/*     */     }
/* 143 */     return new HostAddress(parsedHost.host, port);
/*     */   }
/*     */   
/* 146 */   private static final Pattern BRACKET_PATTERN = Pattern.compile("^\\[(.*:.*)\\](?::(\\d*))?$");
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
/*     */   @JsonCreator
/*     */   public static HostAddress fromString(String hostPortString)
/*     */   {
/* 161 */     if (hostPortString == null) {
/* 162 */       throw new NullPointerException("hostPortString is null");
/*     */     }
/*     */     
/* 165 */     String portString = null;
/*     */     String host;
/* 167 */     if (hostPortString.startsWith("["))
/*     */     {
/* 169 */       Matcher matcher = BRACKET_PATTERN.matcher(hostPortString);
/* 170 */       if (!matcher.matches()) {
/* 171 */         throw new IllegalArgumentException("Invalid bracketed host/port: " + hostPortString);
/*     */       }
/* 173 */       String host = matcher.group(1);
/* 174 */       portString = matcher.group(2);
/*     */     }
/*     */     else {
/* 177 */       int colonPos = hostPortString.indexOf(':');
/* 178 */       if ((colonPos >= 0) && (hostPortString.indexOf(':', colonPos + 1) == -1))
/*     */       {
/* 180 */         String host = hostPortString.substring(0, colonPos);
/* 181 */         portString = hostPortString.substring(colonPos + 1);
/*     */       }
/*     */       else
/*     */       {
/* 185 */         host = hostPortString;
/*     */       }
/*     */     }
/*     */     
/* 189 */     int port = -1;
/* 190 */     if ((portString != null) && (portString.length() != 0))
/*     */     {
/*     */ 
/* 193 */       if (portString.startsWith("+")) {
/* 194 */         throw new IllegalArgumentException("Unparseable port number: " + hostPortString);
/*     */       }
/*     */       try {
/* 197 */         port = Integer.parseInt(portString);
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 200 */         throw new IllegalArgumentException("Unparseable port number: " + hostPortString);
/*     */       }
/* 202 */       if (!isValidPort(port)) {
/* 203 */         throw new IllegalArgumentException("Port number out of range: " + hostPortString);
/*     */       }
/*     */     }
/*     */     
/* 207 */     return new HostAddress(host, port);
/*     */   }
/*     */   
/*     */   public static HostAddress fromUri(URI httpUri)
/*     */   {
/* 212 */     String host = httpUri.getHost();
/* 213 */     int port = httpUri.getPort();
/* 214 */     if (port < 0) {
/* 215 */       return fromString(host);
/*     */     }
/*     */     
/* 218 */     return fromParts(host, port);
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
/*     */   public HostAddress withDefaultPort(int defaultPort)
/*     */   {
/* 234 */     if (!isValidPort(defaultPort)) {
/* 235 */       throw new IllegalArgumentException("Port number out of range: " + defaultPort);
/*     */     }
/*     */     
/* 238 */     if ((hasPort()) || (this.port == defaultPort)) {
/* 239 */       return this;
/*     */     }
/* 241 */     return new HostAddress(this.host, defaultPort);
/*     */   }
/*     */   
/*     */   public InetAddress toInetAddress()
/*     */     throws UnknownHostException
/*     */   {
/* 247 */     return InetAddress.getByName(getHostText());
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 253 */     return Objects.hash(new Object[] { this.host, Integer.valueOf(this.port) });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 259 */     if (this == obj) {
/* 260 */       return true;
/*     */     }
/* 262 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 263 */       return false;
/*     */     }
/* 265 */     HostAddress other = (HostAddress)obj;
/* 266 */     return (Objects.equals(this.host, other.host)) && 
/* 267 */       (Objects.equals(Integer.valueOf(this.port), Integer.valueOf(other.port)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @JsonValue
/*     */   public String toString()
/*     */   {
/* 277 */     StringBuilder builder = new StringBuilder(this.host.length() + 7);
/* 278 */     if (this.host.indexOf(':') >= 0) {
/* 279 */       builder.append('[').append(this.host).append(']');
/*     */     }
/*     */     else {
/* 282 */       builder.append(this.host);
/*     */     }
/* 284 */     if (hasPort()) {
/* 285 */       builder.append(':').append(this.port);
/*     */     }
/* 287 */     return builder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidPort(int port)
/*     */   {
/* 295 */     return (port >= 0) && (port <= 65535);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\HostAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */