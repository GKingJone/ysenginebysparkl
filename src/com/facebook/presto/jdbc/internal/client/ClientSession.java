/*     */ package com.facebook.presto.jdbc.internal.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collections;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
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
/*     */ public class ClientSession
/*     */ {
/*     */   private final URI server;
/*     */   private final String user;
/*     */   private final String source;
/*     */   private final String catalog;
/*     */   private final String schema;
/*     */   private final String timeZoneId;
/*     */   private final Locale locale;
/*     */   private final Map<String, String> properties;
/*     */   private final Map<String, String> preparedStatements;
/*     */   private final String transactionId;
/*     */   private final boolean debug;
/*     */   private final Duration clientRequestTimeout;
/*     */   
/*     */   public static ClientSession withCatalogAndSchema(ClientSession session, String catalog, String schema)
/*     */   {
/*  48 */     return new ClientSession(session
/*  49 */       .getServer(), session
/*  50 */       .getUser(), session
/*  51 */       .getSource(), catalog, schema, session
/*     */       
/*     */ 
/*  54 */       .getTimeZoneId(), session
/*  55 */       .getLocale(), session
/*  56 */       .getProperties(), session
/*  57 */       .getPreparedStatements(), session
/*  58 */       .getTransactionId(), session
/*  59 */       .isDebug(), session
/*  60 */       .getClientRequestTimeout());
/*     */   }
/*     */   
/*     */   public static ClientSession withProperties(ClientSession session, Map<String, String> properties)
/*     */   {
/*  65 */     return new ClientSession(session
/*  66 */       .getServer(), session
/*  67 */       .getUser(), session
/*  68 */       .getSource(), session
/*  69 */       .getCatalog(), session
/*  70 */       .getSchema(), session
/*  71 */       .getTimeZoneId(), session
/*  72 */       .getLocale(), properties, session
/*     */       
/*  74 */       .getPreparedStatements(), session
/*  75 */       .getTransactionId(), session
/*  76 */       .isDebug(), session
/*  77 */       .getClientRequestTimeout());
/*     */   }
/*     */   
/*     */   public static ClientSession withPreparedStatements(ClientSession session, Map<String, String> preparedStatements)
/*     */   {
/*  82 */     return new ClientSession(session
/*  83 */       .getServer(), session
/*  84 */       .getUser(), session
/*  85 */       .getSource(), session
/*  86 */       .getCatalog(), session
/*  87 */       .getSchema(), session
/*  88 */       .getTimeZoneId(), session
/*  89 */       .getLocale(), session
/*  90 */       .getProperties(), preparedStatements, session
/*     */       
/*  92 */       .getTransactionId(), session
/*  93 */       .isDebug(), session
/*  94 */       .getClientRequestTimeout());
/*     */   }
/*     */   
/*     */   public static ClientSession withTransactionId(ClientSession session, String transactionId)
/*     */   {
/*  99 */     return new ClientSession(session
/* 100 */       .getServer(), session
/* 101 */       .getUser(), session
/* 102 */       .getSource(), session
/* 103 */       .getCatalog(), session
/* 104 */       .getSchema(), session
/* 105 */       .getTimeZoneId(), session
/* 106 */       .getLocale(), session
/* 107 */       .getProperties(), session
/* 108 */       .getPreparedStatements(), transactionId, session
/*     */       
/* 110 */       .isDebug(), session
/* 111 */       .getClientRequestTimeout());
/*     */   }
/*     */   
/*     */   public static ClientSession stripTransactionId(ClientSession session)
/*     */   {
/* 116 */     return new ClientSession(session
/* 117 */       .getServer(), session
/* 118 */       .getUser(), session
/* 119 */       .getSource(), session
/* 120 */       .getCatalog(), session
/* 121 */       .getSchema(), session
/* 122 */       .getTimeZoneId(), session
/* 123 */       .getLocale(), session
/* 124 */       .getProperties(), session
/* 125 */       .getPreparedStatements(), null, session
/*     */       
/* 127 */       .isDebug(), session
/* 128 */       .getClientRequestTimeout());
/*     */   }
/*     */   
/*     */   public ClientSession(URI server, String user, String source, String catalog, String schema, String timeZoneId, Locale locale, Map<String, String> properties, String transactionId, boolean debug, Duration clientRequestTimeout)
/*     */   {
/* 133 */     this(server, user, source, catalog, schema, timeZoneId, locale, properties, Collections.emptyMap(), transactionId, debug, clientRequestTimeout);
/*     */   }
/*     */   
/*     */   public ClientSession(URI server, String user, String source, String catalog, String schema, String timeZoneId, Locale locale, Map<String, String> properties, Map<String, String> preparedStatements, String transactionId, boolean debug, Duration clientRequestTimeout)
/*     */   {
/* 138 */     this.server = ((URI)Objects.requireNonNull(server, "server is null"));
/* 139 */     this.user = user;
/* 140 */     this.source = source;
/* 141 */     this.catalog = catalog;
/* 142 */     this.schema = schema;
/* 143 */     this.locale = locale;
/* 144 */     this.timeZoneId = ((String)Objects.requireNonNull(timeZoneId, "timeZoneId is null"));
/* 145 */     this.transactionId = transactionId;
/* 146 */     this.debug = debug;
/* 147 */     this.properties = ImmutableMap.copyOf((Map)Objects.requireNonNull(properties, "properties is null"));
/* 148 */     this.preparedStatements = ImmutableMap.copyOf((Map)Objects.requireNonNull(preparedStatements, "preparedStatements is null"));
/* 149 */     this.clientRequestTimeout = clientRequestTimeout;
/*     */     
/*     */ 
/* 152 */     CharsetEncoder charsetEncoder = StandardCharsets.US_ASCII.newEncoder();
/* 153 */     for (Entry<String, String> entry : properties.entrySet()) {
/* 154 */       Preconditions.checkArgument(!((String)entry.getKey()).isEmpty(), "Session property name is empty");
/* 155 */       Preconditions.checkArgument(((String)entry.getKey()).indexOf('=') < 0, "Session property name must not contain '=': %s", new Object[] { entry.getKey() });
/* 156 */       Preconditions.checkArgument(charsetEncoder.canEncode((CharSequence)entry.getKey()), "Session property name is not US_ASCII: %s", new Object[] { entry.getKey() });
/* 157 */       Preconditions.checkArgument(charsetEncoder.canEncode((CharSequence)entry.getValue()), "Session property value is not US_ASCII: %s", new Object[] { entry.getValue() });
/*     */     }
/*     */   }
/*     */   
/*     */   public URI getServer()
/*     */   {
/* 163 */     return this.server;
/*     */   }
/*     */   
/*     */   public String getUser()
/*     */   {
/* 168 */     return this.user;
/*     */   }
/*     */   
/*     */   public String getSource()
/*     */   {
/* 173 */     return this.source;
/*     */   }
/*     */   
/*     */   public String getCatalog()
/*     */   {
/* 178 */     return this.catalog;
/*     */   }
/*     */   
/*     */   public String getSchema()
/*     */   {
/* 183 */     return this.schema;
/*     */   }
/*     */   
/*     */   public String getTimeZoneId()
/*     */   {
/* 188 */     return this.timeZoneId;
/*     */   }
/*     */   
/*     */   public Locale getLocale()
/*     */   {
/* 193 */     return this.locale;
/*     */   }
/*     */   
/*     */   public Map<String, String> getProperties()
/*     */   {
/* 198 */     return this.properties;
/*     */   }
/*     */   
/*     */   public Map<String, String> getPreparedStatements()
/*     */   {
/* 203 */     return this.preparedStatements;
/*     */   }
/*     */   
/*     */   public String getTransactionId()
/*     */   {
/* 208 */     return this.transactionId;
/*     */   }
/*     */   
/*     */   public boolean isDebug()
/*     */   {
/* 213 */     return this.debug;
/*     */   }
/*     */   
/*     */   public Duration getClientRequestTimeout()
/*     */   {
/* 218 */     return this.clientRequestTimeout;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 224 */     return 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 234 */       MoreObjects.toStringHelper(this).add("server", this.server).add("user", this.user).add("catalog", this.catalog).add("schema", this.schema).add("timeZone", this.timeZoneId).add("locale", this.locale).add("properties", this.properties).add("transactionId", this.transactionId).add("debug", this.debug).toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\ClientSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */