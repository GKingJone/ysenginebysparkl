/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpUriBuilder;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Splitter;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Strings;
/*     */ import com.facebook.presto.jdbc.internal.guava.net.HostAndPort;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class PrestoDriverUri
/*     */ {
/*     */   private static final String JDBC_URL_START = "jdbc:";
/*  37 */   private static final Splitter QUERY_SPLITTER = Splitter.on('&').omitEmptyStrings();
/*  38 */   private static final Splitter ARG_SPLITTER = Splitter.on('=').limit(2);
/*     */   
/*     */   private final HostAndPort address;
/*     */   
/*     */   private final URI uri;
/*     */   
/*     */   private String catalog;
/*     */   private String schema;
/*     */   private final boolean useSecureConnection;
/*     */   
/*     */   public PrestoDriverUri(String url)
/*     */     throws SQLException
/*     */   {
/*  51 */     this(parseDriverUrl(url));
/*     */   }
/*     */   
/*     */   private PrestoDriverUri(URI uri)
/*     */     throws SQLException
/*     */   {
/*  57 */     this.uri = ((URI)Objects.requireNonNull(uri, "uri is null"));
/*  58 */     this.address = HostAndPort.fromParts(uri.getHost(), uri.getPort());
/*     */     
/*  60 */     Map<String, String> params = parseParameters(uri.getQuery());
/*  61 */     this.useSecureConnection = Boolean.parseBoolean((String)params.get("secure"));
/*     */     
/*  63 */     initCatalogAndSchema();
/*     */   }
/*     */   
/*     */   public URI getJdbcUri()
/*     */   {
/*  68 */     return this.uri;
/*     */   }
/*     */   
/*     */   public String getSchema()
/*     */   {
/*  73 */     return this.schema;
/*     */   }
/*     */   
/*     */   public String getCatalog()
/*     */   {
/*  78 */     return this.catalog;
/*     */   }
/*     */   
/*     */   public URI getHttpUri()
/*     */   {
/*  83 */     return buildHttpUri();
/*     */   }
/*     */   
/*     */   private static Map<String, String> parseParameters(String query)
/*     */   {
/*  88 */     Map<String, String> result = new HashMap();
/*     */     
/*  90 */     if (query != null) {
/*  91 */       Iterable<String> queryArgs = QUERY_SPLITTER.split(query);
/*  92 */       for (String queryArg : queryArgs) {
/*  93 */         List<String> parts = ARG_SPLITTER.splitToList(queryArg);
/*  94 */         result.put(parts.get(0), parts.get(1));
/*     */       }
/*     */     }
/*     */     
/*  98 */     return result;
/*     */   }
/*     */   
/*     */   private static URI parseDriverUrl(String url)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 106 */       uri = new URI(url.substring("jdbc:".length()));
/*     */     } catch (URISyntaxException e) {
/*     */       URI uri;
/* 109 */       throw new SQLException("Invalid JDBC URL: " + url, e); }
/*     */     URI uri;
/* 111 */     if (Strings.isNullOrEmpty(uri.getHost())) {
/* 112 */       throw new SQLException("No host specified: " + url);
/*     */     }
/* 114 */     if (uri.getPort() == -1) {
/* 115 */       throw new SQLException("No port number specified: " + url);
/*     */     }
/* 117 */     if ((uri.getPort() < 1) || (uri.getPort() > 65535)) {
/* 118 */       throw new SQLException("Invalid port number: " + url);
/*     */     }
/* 120 */     return uri;
/*     */   }
/*     */   
/*     */   private URI buildHttpUri()
/*     */   {
/* 125 */     String scheme = (this.address.getPort() == 443) || (this.useSecureConnection) ? "https" : "http";
/*     */     
/* 127 */     return HttpUriBuilder.uriBuilder()
/* 128 */       .scheme(scheme)
/* 129 */       .host(this.address.getHostText()).port(this.address.getPort())
/* 130 */       .build();
/*     */   }
/*     */   
/*     */   private void initCatalogAndSchema()
/*     */     throws SQLException
/*     */   {
/* 136 */     String path = this.uri.getPath();
/* 137 */     if ((Strings.isNullOrEmpty(this.uri.getPath())) || (path.equals("/"))) {
/* 138 */       return;
/*     */     }
/*     */     
/*     */ 
/* 142 */     if (!path.startsWith("/")) {
/* 143 */       throw new SQLException("Path does not start with a slash: " + this.uri);
/*     */     }
/* 145 */     path = path.substring(1);
/*     */     
/* 147 */     List<String> parts = Splitter.on("/").splitToList(path);
/*     */     
/* 149 */     if (((String)parts.get(parts.size() - 1)).isEmpty()) {
/* 150 */       parts = parts.subList(0, parts.size() - 1);
/*     */     }
/*     */     
/* 153 */     if (parts.size() > 2) {
/* 154 */       throw new SQLException("Invalid path segments in URL: " + this.uri);
/*     */     }
/*     */     
/* 157 */     if (((String)parts.get(0)).isEmpty()) {
/* 158 */       throw new SQLException("Catalog name is empty: " + this.uri);
/*     */     }
/* 160 */     this.catalog = ((String)parts.get(0));
/*     */     
/* 162 */     if (parts.size() > 1) {
/* 163 */       if (((String)parts.get(1)).isEmpty()) {
/* 164 */         throw new SQLException("Schema name is empty: " + this.uri);
/*     */       }
/* 166 */       this.schema = ((String)parts.get(1));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoDriverUri.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */