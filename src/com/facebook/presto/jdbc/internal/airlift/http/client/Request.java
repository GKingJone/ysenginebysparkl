/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ArrayListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*     */ import java.net.URI;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public class Request
/*     */ {
/*     */   private final URI uri;
/*     */   private final String method;
/*     */   private final ListMultimap<String, String> headers;
/*     */   private final BodyGenerator bodyGenerator;
/*     */   
/*     */   public Request(URI uri, String method, ListMultimap<String, String> headers, BodyGenerator bodyGenerator)
/*     */   {
/*  42 */     Preconditions.checkNotNull(uri, "uri is null");
/*  43 */     Preconditions.checkNotNull(uri.getHost(), "uri does not have a host: %s", new Object[] { uri });
/*  44 */     Preconditions.checkNotNull(method, "method is null");
/*  45 */     Preconditions.checkNotNull(uri.getScheme(), "uri does not have a scheme: %s", new Object[] { uri });
/*  46 */     String scheme = uri.getScheme().toLowerCase();
/*  47 */     Preconditions.checkArgument((!"http".equals(scheme)) || (!"https".equals(scheme)), "uri scheme must be http or https: %s", new Object[] { uri });
/*     */     
/*  49 */     this.uri = validateUri(uri);
/*  50 */     this.method = method;
/*  51 */     this.headers = ImmutableListMultimap.copyOf(headers);
/*  52 */     this.bodyGenerator = bodyGenerator;
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/*  56 */     return new Builder();
/*     */   }
/*     */   
/*     */   public URI getUri()
/*     */   {
/*  61 */     return this.uri;
/*     */   }
/*     */   
/*     */   public String getMethod()
/*     */   {
/*  66 */     return this.method;
/*     */   }
/*     */   
/*     */   public String getHeader(String name)
/*     */   {
/*  71 */     List<String> values = this.headers.get(name);
/*  72 */     if ((values != null) && (!values.isEmpty())) {
/*  73 */       return (String)values.get(0);
/*     */     }
/*  75 */     return null;
/*     */   }
/*     */   
/*     */   public ListMultimap<String, String> getHeaders()
/*     */   {
/*  80 */     return this.headers;
/*     */   }
/*     */   
/*     */   public BodyGenerator getBodyGenerator()
/*     */   {
/*  85 */     return this.bodyGenerator;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  91 */     return 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  96 */       MoreObjects.toStringHelper(this).add("uri", this.uri).add("method", this.method).add("headers", this.headers).add("bodyGenerator", this.bodyGenerator).toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 102 */     if (!(o instanceof Request)) {
/* 103 */       return false;
/*     */     }
/* 105 */     Request r = (Request)o;
/* 106 */     return (Objects.equal(this.uri, r.uri)) && 
/* 107 */       (Objects.equal(this.method, r.method)) && 
/* 108 */       (Objects.equal(this.headers, r.headers)) && 
/* 109 */       (Objects.equal(this.bodyGenerator, r.bodyGenerator));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 115 */   public int hashCode() { return Objects.hashCode(new Object[] { this.uri, this.method, this.headers, this.bodyGenerator }); }
/*     */   
/*     */   @Beta
/*     */   public static class Builder {
/*     */     private URI uri;
/*     */     private String method;
/*     */     
/* 122 */     public static Builder prepareHead() { return new Builder().setMethod("HEAD"); }
/*     */     
/*     */     public static Builder prepareGet()
/*     */     {
/* 126 */       return new Builder().setMethod("GET");
/*     */     }
/*     */     
/*     */     public static Builder preparePost() {
/* 130 */       return new Builder().setMethod("POST");
/*     */     }
/*     */     
/*     */     public static Builder preparePut() {
/* 134 */       return new Builder().setMethod("PUT");
/*     */     }
/*     */     
/*     */     public static Builder prepareDelete() {
/* 138 */       return new Builder().setMethod("DELETE");
/*     */     }
/*     */     
/*     */     public static Builder fromRequest(Request request) {
/* 142 */       Builder requestBuilder = new Builder();
/* 143 */       requestBuilder.setMethod(request.getMethod());
/* 144 */       requestBuilder.setBodyGenerator(request.getBodyGenerator());
/* 145 */       requestBuilder.setUri(request.getUri());
/*     */       
/* 147 */       for (Map.Entry<String, String> entry : request.getHeaders().entries()) {
/* 148 */         requestBuilder.addHeader((String)entry.getKey(), (String)entry.getValue());
/*     */       }
/* 150 */       return requestBuilder;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 155 */     private final ListMultimap<String, String> headers = ArrayListMultimap.create();
/*     */     private BodyGenerator bodyGenerator;
/*     */     
/*     */     public Builder setUri(URI uri)
/*     */     {
/* 160 */       this.uri = Request.validateUri(uri);
/* 161 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setMethod(String method)
/*     */     {
/* 166 */       this.method = method;
/* 167 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setHeader(String name, String value)
/*     */     {
/* 172 */       this.headers.removeAll(name);
/* 173 */       this.headers.put(name, value);
/* 174 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addHeader(String name, String value)
/*     */     {
/* 179 */       this.headers.put(name, value);
/* 180 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setBodyGenerator(BodyGenerator bodyGenerator)
/*     */     {
/* 185 */       this.bodyGenerator = bodyGenerator;
/* 186 */       return this;
/*     */     }
/*     */     
/*     */     public Request build() {
/* 190 */       return new Request(this.uri, this.method, this.headers, this.bodyGenerator);
/*     */     }
/*     */   }
/*     */   
/*     */   private static URI validateUri(URI uri)
/*     */   {
/* 196 */     Preconditions.checkArgument(uri.getPort() != 0, "Cannot make requests to HTTP port 0");
/* 197 */     return uri;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */