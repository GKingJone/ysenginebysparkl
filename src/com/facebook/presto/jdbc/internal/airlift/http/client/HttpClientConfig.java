/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.configuration.Config;
/*     */ import com.facebook.presto.jdbc.internal.airlift.configuration.ConfigDescription;
/*     */ import com.facebook.presto.jdbc.internal.airlift.configuration.LegacyConfig;
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.DataSize;
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.DataSize.Unit;
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.MinDuration;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.net.HostAndPort;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.validation.constraints.Min;
/*     */ import javax.validation.constraints.NotNull;
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
/*     */ @Beta
/*     */ public class HttpClientConfig
/*     */ {
/*     */   public static final String JAVAX_NET_SSL_KEY_STORE = "javax.net.ssl.keyStore";
/*     */   public static final String JAVAX_NET_SSL_KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
/*     */   public static final String JAVAX_NET_SSL_TRUST_STORE = "javax.net.ssl.trustStore";
/*     */   public static final String JAVAX_NET_SSL_TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
/*     */   private boolean http2Enabled;
/*  42 */   private Duration connectTimeout = new Duration(1.0D, TimeUnit.SECONDS);
/*  43 */   private Duration requestTimeout = new Duration(5.0D, TimeUnit.MINUTES);
/*  44 */   private Duration idleTimeout = new Duration(1.0D, TimeUnit.MINUTES);
/*     */   private Duration keepAliveInterval;
/*  46 */   private int maxConnections = 200;
/*  47 */   private int maxConnectionsPerServer = 20;
/*  48 */   private int maxRequestsQueuedPerDestination = 1024;
/*  49 */   private DataSize maxContentLength = new DataSize(16.0D, DataSize.Unit.MEGABYTE);
/*     */   private HostAndPort socksProxy;
/*  51 */   private String keyStorePath = System.getProperty("javax.net.ssl.keyStore");
/*  52 */   private String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
/*  53 */   private String trustStorePath = System.getProperty("javax.net.ssl.trustStore");
/*  54 */   private String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
/*     */   private boolean authenticationEnabled;
/*     */   private String kerberosPrincipal;
/*     */   private String kerberosRemoteServiceName;
/*     */   
/*     */   public boolean isHttp2Enabled()
/*     */   {
/*  61 */     return this.http2Enabled;
/*     */   }
/*     */   
/*     */   @Config("http-client.http2.enabled")
/*     */   @ConfigDescription("Enable the HTTP/2 transport")
/*     */   public HttpClientConfig setHttp2Enabled(boolean http2Enabled)
/*     */   {
/*  68 */     this.http2Enabled = http2Enabled;
/*  69 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @MinDuration("0ms")
/*     */   public Duration getConnectTimeout()
/*     */   {
/*  76 */     return this.connectTimeout;
/*     */   }
/*     */   
/*     */   @Config("http-client.connect-timeout")
/*     */   public HttpClientConfig setConnectTimeout(Duration connectTimeout)
/*     */   {
/*  82 */     this.connectTimeout = connectTimeout;
/*  83 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @MinDuration("0ms")
/*     */   public Duration getRequestTimeout()
/*     */   {
/*  90 */     return this.requestTimeout;
/*     */   }
/*     */   
/*     */   @Config("http-client.request-timeout")
/*     */   public HttpClientConfig setRequestTimeout(Duration requestTimeout)
/*     */   {
/*  96 */     this.requestTimeout = requestTimeout;
/*  97 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @MinDuration("0ms")
/*     */   public Duration getIdleTimeout()
/*     */   {
/* 104 */     return this.idleTimeout;
/*     */   }
/*     */   
/*     */   @Config("http-client.idle-timeout")
/*     */   @LegacyConfig({"http-client.read-timeout"})
/*     */   public HttpClientConfig setIdleTimeout(Duration idleTimeout)
/*     */   {
/* 111 */     this.idleTimeout = idleTimeout;
/* 112 */     return this;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Duration getKeepAliveInterval()
/*     */   {
/* 118 */     return this.keepAliveInterval;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   @Config("http-client.keep-alive-interval")
/*     */   public HttpClientConfig setKeepAliveInterval(Duration keepAliveInterval)
/*     */   {
/* 125 */     this.keepAliveInterval = keepAliveInterval;
/* 126 */     return this;
/*     */   }
/*     */   
/*     */   @Min(1L)
/*     */   public int getMaxConnections()
/*     */   {
/* 132 */     return this.maxConnections;
/*     */   }
/*     */   
/*     */   @Config("http-client.max-connections")
/*     */   public HttpClientConfig setMaxConnections(int maxConnections)
/*     */   {
/* 138 */     this.maxConnections = maxConnections;
/* 139 */     return this;
/*     */   }
/*     */   
/*     */   @Min(1L)
/*     */   public int getMaxConnectionsPerServer()
/*     */   {
/* 145 */     return this.maxConnectionsPerServer;
/*     */   }
/*     */   
/*     */   @Config("http-client.max-connections-per-server")
/*     */   public HttpClientConfig setMaxConnectionsPerServer(int maxConnectionsPerServer)
/*     */   {
/* 151 */     this.maxConnectionsPerServer = maxConnectionsPerServer;
/* 152 */     return this;
/*     */   }
/*     */   
/*     */   @Min(1L)
/*     */   public int getMaxRequestsQueuedPerDestination()
/*     */   {
/* 158 */     return this.maxRequestsQueuedPerDestination;
/*     */   }
/*     */   
/*     */   @Config("http-client.max-requests-queued-per-destination")
/*     */   public HttpClientConfig setMaxRequestsQueuedPerDestination(int maxRequestsQueuedPerDestination)
/*     */   {
/* 164 */     this.maxRequestsQueuedPerDestination = maxRequestsQueuedPerDestination;
/* 165 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public DataSize getMaxContentLength()
/*     */   {
/* 171 */     return this.maxContentLength;
/*     */   }
/*     */   
/*     */   @Config("http-client.max-content-length")
/*     */   public HttpClientConfig setMaxContentLength(DataSize maxContentLength)
/*     */   {
/* 177 */     this.maxContentLength = maxContentLength;
/* 178 */     return this;
/*     */   }
/*     */   
/*     */   public HostAndPort getSocksProxy()
/*     */   {
/* 183 */     return this.socksProxy;
/*     */   }
/*     */   
/*     */   @Config("http-client.socks-proxy")
/*     */   public HttpClientConfig setSocksProxy(HostAndPort socksProxy)
/*     */   {
/* 189 */     this.socksProxy = socksProxy;
/* 190 */     return this;
/*     */   }
/*     */   
/*     */   public String getKeyStorePath()
/*     */   {
/* 195 */     return this.keyStorePath;
/*     */   }
/*     */   
/*     */   @Config("http-client.key-store-path")
/*     */   public HttpClientConfig setKeyStorePath(String keyStorePath)
/*     */   {
/* 201 */     this.keyStorePath = keyStorePath;
/* 202 */     return this;
/*     */   }
/*     */   
/*     */   public String getKeyStorePassword()
/*     */   {
/* 207 */     return this.keyStorePassword;
/*     */   }
/*     */   
/*     */   @Config("http-client.key-store-password")
/*     */   public HttpClientConfig setKeyStorePassword(String keyStorePassword)
/*     */   {
/* 213 */     this.keyStorePassword = keyStorePassword;
/* 214 */     return this;
/*     */   }
/*     */   
/*     */   public String getTrustStorePath()
/*     */   {
/* 219 */     return this.trustStorePath;
/*     */   }
/*     */   
/*     */   @Config("http-client.trust-store-path")
/*     */   public HttpClientConfig setTrustStorePath(String trustStorePath)
/*     */   {
/* 225 */     this.trustStorePath = trustStorePath;
/* 226 */     return this;
/*     */   }
/*     */   
/*     */   public String getTrustStorePassword()
/*     */   {
/* 231 */     return this.trustStorePassword;
/*     */   }
/*     */   
/*     */   @Config("http-client.trust-store-password")
/*     */   public HttpClientConfig setTrustStorePassword(String trustStorePassword)
/*     */   {
/* 237 */     this.trustStorePassword = trustStorePassword;
/* 238 */     return this;
/*     */   }
/*     */   
/*     */   public boolean getAuthenticationEnabled()
/*     */   {
/* 243 */     return this.authenticationEnabled;
/*     */   }
/*     */   
/*     */   @Config("http-client.authentication.enabled")
/*     */   @ConfigDescription("Enable client authentication")
/*     */   public HttpClientConfig setAuthenticationEnabled(boolean enabled)
/*     */   {
/* 250 */     this.authenticationEnabled = enabled;
/* 251 */     return this;
/*     */   }
/*     */   
/*     */   public String getKerberosPrincipal()
/*     */   {
/* 256 */     return this.kerberosPrincipal;
/*     */   }
/*     */   
/*     */   @Config("http-client.authentication.krb5.principal")
/*     */   @ConfigDescription("Set kerberos client principal")
/*     */   public HttpClientConfig setKerberosPrincipal(String kerberosClientPrincipal)
/*     */   {
/* 263 */     this.kerberosPrincipal = kerberosClientPrincipal;
/* 264 */     return this;
/*     */   }
/*     */   
/*     */   public String getKerberosRemoteServiceName()
/*     */   {
/* 269 */     return this.kerberosRemoteServiceName;
/*     */   }
/*     */   
/*     */   @Config("http-client.authentication.krb5.remote-service-name")
/*     */   @ConfigDescription("Set kerberos service principal name")
/*     */   public HttpClientConfig setKerberosRemoteServiceName(String serviceName)
/*     */   {
/* 276 */     this.kerberosRemoteServiceName = serviceName;
/* 277 */     return this;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HttpClientConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */