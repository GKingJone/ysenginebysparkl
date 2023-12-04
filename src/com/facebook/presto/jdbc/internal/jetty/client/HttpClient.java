/*      */ package com.facebook.presto.jdbc.internal.jetty.client;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.AuthenticationStore;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentResponse;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.Destination;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.Listener;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.http.HttpClientTransportOverHTTP;
/*      */ import com.facebook.presto.jdbc.internal.jetty.client.util.FormContentProvider;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http.HttpMethod;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http.HttpScheme;
/*      */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*      */ import com.facebook.presto.jdbc.internal.jetty.io.MappedByteBufferPool;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Fields;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Jetty;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.SocketAddressResolver;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.SocketAddressResolver.Async;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.ssl.SslContextFactory;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.thread.QueuedThreadPool;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ScheduledExecutorScheduler;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*      */ import java.net.CookieManager;
/*      */ import java.net.CookiePolicy;
/*      */ import java.net.CookieStore;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.URI;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.TimeoutException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @ManagedObject("The HTTP client")
/*      */ public class HttpClient
/*      */   extends ContainerLifeCycle
/*      */ {
/*  114 */   private static final Logger LOG = Log.getLogger(HttpClient.class);
/*      */   
/*  116 */   private final ConcurrentMap<Origin, HttpDestination> destinations = new ConcurrentHashMap();
/*  117 */   private final ProtocolHandlers handlers = new ProtocolHandlers();
/*  118 */   private final List<Request.Listener> requestListeners = new ArrayList();
/*  119 */   private final AuthenticationStore authenticationStore = new HttpAuthenticationStore();
/*  120 */   private final Set<ContentDecoder.Factory> decoderFactories = new ContentDecoderFactorySet(null);
/*  121 */   private final ProxyConfiguration proxyConfig = new ProxyConfiguration();
/*      */   private final HttpClientTransport transport;
/*      */   private final SslContextFactory sslContextFactory;
/*      */   private volatile CookieManager cookieManager;
/*      */   private volatile CookieStore cookieStore;
/*      */   private volatile Executor executor;
/*      */   private volatile ByteBufferPool byteBufferPool;
/*      */   private volatile Scheduler scheduler;
/*      */   private volatile SocketAddressResolver resolver;
/*  130 */   private volatile HttpField agentField = new HttpField(HttpHeader.USER_AGENT, "Jetty/" + Jetty.VERSION);
/*  131 */   private volatile boolean followRedirects = true;
/*  132 */   private volatile int maxConnectionsPerDestination = 64;
/*  133 */   private volatile int maxRequestsQueuedPerDestination = 1024;
/*  134 */   private volatile int requestBufferSize = 4096;
/*  135 */   private volatile int responseBufferSize = 16384;
/*  136 */   private volatile int maxRedirects = 8;
/*      */   private volatile SocketAddress bindAddress;
/*  138 */   private volatile long connectTimeout = 15000L;
/*  139 */   private volatile long addressResolutionTimeout = 15000L;
/*      */   private volatile long idleTimeout;
/*  141 */   private volatile boolean tcpNoDelay = true;
/*  142 */   private volatile boolean strictEventOrdering = false;
/*      */   private volatile HttpField encodingField;
/*  144 */   private volatile boolean removeIdleDestinations = false;
/*  145 */   private volatile boolean connectBlocking = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public HttpClient()
/*      */   {
/*  155 */     this(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public HttpClient(SslContextFactory sslContextFactory)
/*      */   {
/*  167 */     this(new HttpClientTransportOverHTTP(), sslContextFactory);
/*      */   }
/*      */   
/*      */   public HttpClient(HttpClientTransport transport, SslContextFactory sslContextFactory)
/*      */   {
/*  172 */     this.transport = transport;
/*  173 */     this.sslContextFactory = sslContextFactory;
/*      */   }
/*      */   
/*      */   public HttpClientTransport getTransport()
/*      */   {
/*  178 */     return this.transport;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SslContextFactory getSslContextFactory()
/*      */   {
/*  187 */     return this.sslContextFactory;
/*      */   }
/*      */   
/*      */   protected void doStart()
/*      */     throws Exception
/*      */   {
/*  193 */     if (this.sslContextFactory != null) {
/*  194 */       addBean(this.sslContextFactory);
/*      */     }
/*  196 */     String name = HttpClient.class.getSimpleName() + "@" + hashCode();
/*      */     
/*  198 */     if (this.executor == null)
/*      */     {
/*  200 */       QueuedThreadPool threadPool = new QueuedThreadPool();
/*  201 */       threadPool.setName(name);
/*  202 */       this.executor = threadPool;
/*      */     }
/*  204 */     addBean(this.executor);
/*      */     
/*  206 */     if (this.byteBufferPool == null)
/*  207 */       this.byteBufferPool = new MappedByteBufferPool();
/*  208 */     addBean(this.byteBufferPool);
/*      */     
/*  210 */     if (this.scheduler == null)
/*  211 */       this.scheduler = new ScheduledExecutorScheduler(name + "-scheduler", false);
/*  212 */     addBean(this.scheduler);
/*      */     
/*  214 */     this.transport.setHttpClient(this);
/*  215 */     addBean(this.transport);
/*      */     
/*  217 */     if (this.resolver == null)
/*  218 */       this.resolver = new SocketAddressResolver.Async(this.executor, this.scheduler, getAddressResolutionTimeout());
/*  219 */     addBean(this.resolver);
/*      */     
/*  221 */     this.handlers.put(new ContinueProtocolHandler());
/*  222 */     this.handlers.put(new RedirectProtocolHandler(this));
/*  223 */     this.handlers.put(new WWWAuthenticationProtocolHandler(this));
/*  224 */     this.handlers.put(new ProxyAuthenticationProtocolHandler(this));
/*      */     
/*  226 */     this.decoderFactories.add(new GZIPContentDecoder.Factory());
/*      */     
/*  228 */     this.cookieManager = newCookieManager();
/*  229 */     this.cookieStore = this.cookieManager.getCookieStore();
/*      */     
/*  231 */     super.doStart();
/*      */   }
/*      */   
/*      */   private CookieManager newCookieManager()
/*      */   {
/*  236 */     return new CookieManager(getCookieStore(), CookiePolicy.ACCEPT_ALL);
/*      */   }
/*      */   
/*      */   protected void doStop()
/*      */     throws Exception
/*      */   {
/*  242 */     this.cookieStore.removeAll();
/*  243 */     this.decoderFactories.clear();
/*  244 */     this.handlers.clear();
/*      */     
/*  246 */     for (HttpDestination destination : this.destinations.values())
/*  247 */       destination.close();
/*  248 */     this.destinations.clear();
/*      */     
/*  250 */     this.requestListeners.clear();
/*  251 */     this.authenticationStore.clearAuthentications();
/*  252 */     this.authenticationStore.clearAuthenticationResults();
/*      */     
/*  254 */     super.doStop();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<Request.Listener> getRequestListeners()
/*      */   {
/*  265 */     return this.requestListeners;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public CookieStore getCookieStore()
/*      */   {
/*  273 */     return this.cookieStore;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCookieStore(CookieStore cookieStore)
/*      */   {
/*  281 */     this.cookieStore = ((CookieStore)Objects.requireNonNull(cookieStore));
/*  282 */     this.cookieManager = newCookieManager();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   CookieManager getCookieManager()
/*      */   {
/*  293 */     return this.cookieManager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public AuthenticationStore getAuthenticationStore()
/*      */   {
/*  301 */     return this.authenticationStore;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<ContentDecoder.Factory> getContentDecoderFactories()
/*      */   {
/*  312 */     return this.decoderFactories;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ContentResponse GET(String uri)
/*      */     throws InterruptedException, ExecutionException, TimeoutException
/*      */   {
/*  327 */     return GET(URI.create(uri));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ContentResponse GET(URI uri)
/*      */     throws InterruptedException, ExecutionException, TimeoutException
/*      */   {
/*  342 */     return newRequest(uri).send();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ContentResponse FORM(String uri, Fields fields)
/*      */     throws InterruptedException, ExecutionException, TimeoutException
/*      */   {
/*  357 */     return FORM(URI.create(uri), fields);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ContentResponse FORM(URI uri, Fields fields)
/*      */     throws InterruptedException, ExecutionException, TimeoutException
/*      */   {
/*  372 */     return POST(uri).content(new FormContentProvider(fields)).send();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Request POST(String uri)
/*      */   {
/*  384 */     return POST(URI.create(uri));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Request POST(URI uri)
/*      */   {
/*  395 */     return newRequest(uri).method(HttpMethod.POST);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Request newRequest(String host, int port)
/*      */   {
/*  407 */     return newRequest(new Origin("http", host, port).asString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Request newRequest(String uri)
/*      */   {
/*  418 */     return newRequest(URI.create(uri));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Request newRequest(URI uri)
/*      */   {
/*  429 */     return newHttpRequest(newConversation(), uri);
/*      */   }
/*      */   
/*      */   protected Request copyRequest(HttpRequest oldRequest, URI newURI)
/*      */   {
/*  434 */     Request newRequest = newHttpRequest(oldRequest.getConversation(), newURI);
/*  435 */     newRequest.method(oldRequest.getMethod())
/*  436 */       .version(oldRequest.getVersion())
/*  437 */       .content(oldRequest.getContent())
/*  438 */       .idleTimeout(oldRequest.getIdleTimeout(), TimeUnit.MILLISECONDS)
/*  439 */       .timeout(oldRequest.getTimeout(), TimeUnit.MILLISECONDS)
/*  440 */       .followRedirects(oldRequest.isFollowRedirects());
/*  441 */     for (HttpField field : oldRequest.getHeaders())
/*      */     {
/*  443 */       HttpHeader header = field.getHeader();
/*      */       
/*  445 */       if ((HttpHeader.HOST != header) && 
/*      */       
/*      */ 
/*      */ 
/*  449 */         (HttpHeader.EXPECT != header) && 
/*      */         
/*      */ 
/*      */ 
/*  453 */         (HttpHeader.COOKIE != header) && 
/*      */         
/*      */ 
/*      */ 
/*  457 */         (HttpHeader.AUTHORIZATION != header) && (HttpHeader.PROXY_AUTHORIZATION != header))
/*      */       {
/*      */ 
/*      */ 
/*  461 */         String value = field.getValue();
/*  462 */         if (!newRequest.getHeaders().contains(header, value))
/*  463 */           newRequest.header(field.getName(), value);
/*      */       } }
/*  465 */     return newRequest;
/*      */   }
/*      */   
/*      */   protected HttpRequest newHttpRequest(HttpConversation conversation, URI uri)
/*      */   {
/*  470 */     return new HttpRequest(this, conversation, uri);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Destination getDestination(String scheme, String host, int port)
/*      */   {
/*  488 */     return destinationFor(scheme, host, port);
/*      */   }
/*      */   
/*      */   protected HttpDestination destinationFor(String scheme, String host, int port)
/*      */   {
/*  493 */     port = normalizePort(scheme, port);
/*      */     
/*  495 */     Origin origin = new Origin(scheme, host, port);
/*  496 */     HttpDestination destination = (HttpDestination)this.destinations.get(origin);
/*  497 */     if (destination == null)
/*      */     {
/*  499 */       destination = this.transport.newHttpDestination(origin);
/*  500 */       addManaged(destination);
/*  501 */       HttpDestination existing = (HttpDestination)this.destinations.putIfAbsent(origin, destination);
/*  502 */       if (existing != null)
/*      */       {
/*  504 */         removeBean(destination);
/*  505 */         destination = existing;
/*      */ 
/*      */ 
/*      */       }
/*  509 */       else if (LOG.isDebugEnabled()) {
/*  510 */         LOG.debug("Created {}", new Object[] { destination });
/*      */       }
/*      */     }
/*  513 */     return destination;
/*      */   }
/*      */   
/*      */   protected boolean removeDestination(HttpDestination destination)
/*      */   {
/*  518 */     removeBean(destination);
/*  519 */     return this.destinations.remove(destination.getOrigin()) != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<Destination> getDestinations()
/*      */   {
/*  527 */     return new ArrayList(this.destinations.values());
/*      */   }
/*      */   
/*      */   protected void send(HttpRequest request, List<ResponseListener> listeners)
/*      */   {
/*  532 */     String scheme = request.getScheme().toLowerCase(Locale.ENGLISH);
/*  533 */     if ((!HttpScheme.HTTP.is(scheme)) && (!HttpScheme.HTTPS.is(scheme))) {
/*  534 */       throw new IllegalArgumentException("Invalid protocol " + scheme);
/*      */     }
/*  536 */     String host = request.getHost().toLowerCase(Locale.ENGLISH);
/*  537 */     HttpDestination destination = destinationFor(scheme, host, request.getPort());
/*  538 */     destination.send(request, listeners);
/*      */   }
/*      */   
/*      */   protected void newConnection(final HttpDestination destination, final Promise<Connection> promise)
/*      */   {
/*  543 */     Origin.Address address = destination.getConnectAddress();
/*  544 */     this.resolver.resolve(address.getHost(), address.getPort(), new Promise()
/*      */     {
/*      */ 
/*      */       public void succeeded(List<InetSocketAddress> socketAddresses)
/*      */       {
/*  549 */         Map<String, Object> context = new HashMap();
/*  550 */         context.put("http.destination", destination);
/*  551 */         connect(socketAddresses, 0, context);
/*      */       }
/*      */       
/*      */ 
/*      */       public void failed(Throwable x)
/*      */       {
/*  557 */         promise.failed(x);
/*      */       }
/*      */       
/*      */       private void connect(final List<InetSocketAddress> socketAddresses, final int index, final Map<String, Object> context)
/*      */       {
/*  562 */         context.put("http.connection.promise", new Promise()
/*      */         {
/*      */ 
/*      */           public void succeeded(Connection result)
/*      */           {
/*  567 */             HttpClient.1.this.val$promise.succeeded(result);
/*      */           }
/*      */           
/*      */ 
/*      */           public void failed(Throwable x)
/*      */           {
/*  573 */             int nextIndex = index + 1;
/*  574 */             if (nextIndex == socketAddresses.size()) {
/*  575 */               HttpClient.1.this.val$promise.failed(x);
/*      */             } else
/*  577 */               HttpClient.1.this.connect(socketAddresses, nextIndex, context);
/*      */           }
/*  579 */         });
/*  580 */         HttpClient.this.transport.connect((InetSocketAddress)socketAddresses.get(index), context);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private HttpConversation newConversation()
/*      */   {
/*  587 */     return new HttpConversation();
/*      */   }
/*      */   
/*      */   public ProtocolHandlers getProtocolHandlers()
/*      */   {
/*  592 */     return this.handlers;
/*      */   }
/*      */   
/*      */   protected ProtocolHandler findProtocolHandler(Request request, Response response)
/*      */   {
/*  597 */     return this.handlers.find(request, response);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ByteBufferPool getByteBufferPool()
/*      */   {
/*  605 */     return this.byteBufferPool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setByteBufferPool(ByteBufferPool byteBufferPool)
/*      */   {
/*  613 */     this.byteBufferPool = byteBufferPool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("The timeout, in milliseconds, for connect() operations")
/*      */   public long getConnectTimeout()
/*      */   {
/*  622 */     return this.connectTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setConnectTimeout(long connectTimeout)
/*      */   {
/*  631 */     this.connectTimeout = connectTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getAddressResolutionTimeout()
/*      */   {
/*  640 */     return this.addressResolutionTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAddressResolutionTimeout(long addressResolutionTimeout)
/*      */   {
/*  654 */     this.addressResolutionTimeout = addressResolutionTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("The timeout, in milliseconds, to close idle connections")
/*      */   public long getIdleTimeout()
/*      */   {
/*  663 */     return this.idleTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setIdleTimeout(long idleTimeout)
/*      */   {
/*  671 */     this.idleTimeout = idleTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SocketAddress getBindAddress()
/*      */   {
/*  680 */     return this.bindAddress;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBindAddress(SocketAddress bindAddress)
/*      */   {
/*  690 */     this.bindAddress = bindAddress;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public HttpField getUserAgentField()
/*      */   {
/*  698 */     return this.agentField;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUserAgentField(HttpField agent)
/*      */   {
/*  706 */     if (agent.getHeader() != HttpHeader.USER_AGENT)
/*  707 */       throw new IllegalArgumentException();
/*  708 */     this.agentField = agent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("Whether HTTP redirects are followed")
/*      */   public boolean isFollowRedirects()
/*      */   {
/*  718 */     return this.followRedirects;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFollowRedirects(boolean follow)
/*      */   {
/*  727 */     this.followRedirects = follow;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Executor getExecutor()
/*      */   {
/*  735 */     return this.executor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setExecutor(Executor executor)
/*      */   {
/*  743 */     this.executor = executor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Scheduler getScheduler()
/*      */   {
/*  751 */     return this.scheduler;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setScheduler(Scheduler scheduler)
/*      */   {
/*  759 */     this.scheduler = scheduler;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SocketAddressResolver getSocketAddressResolver()
/*      */   {
/*  767 */     return this.resolver;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSocketAddressResolver(SocketAddressResolver resolver)
/*      */   {
/*  775 */     this.resolver = resolver;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("The max number of connections per each destination")
/*      */   public int getMaxConnectionsPerDestination()
/*      */   {
/*  784 */     return this.maxConnectionsPerDestination;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMaxConnectionsPerDestination(int maxConnectionsPerDestination)
/*      */   {
/*  800 */     this.maxConnectionsPerDestination = maxConnectionsPerDestination;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("The max number of requests queued per each destination")
/*      */   public int getMaxRequestsQueuedPerDestination()
/*      */   {
/*  809 */     return this.maxRequestsQueuedPerDestination;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMaxRequestsQueuedPerDestination(int maxRequestsQueuedPerDestination)
/*      */   {
/*  827 */     this.maxRequestsQueuedPerDestination = maxRequestsQueuedPerDestination;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("The request buffer size")
/*      */   public int getRequestBufferSize()
/*      */   {
/*  836 */     return this.requestBufferSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRequestBufferSize(int requestBufferSize)
/*      */   {
/*  844 */     this.requestBufferSize = requestBufferSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("The response buffer size")
/*      */   public int getResponseBufferSize()
/*      */   {
/*  853 */     return this.responseBufferSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setResponseBufferSize(int responseBufferSize)
/*      */   {
/*  861 */     this.responseBufferSize = responseBufferSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxRedirects()
/*      */   {
/*  870 */     return this.maxRedirects;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMaxRedirects(int maxRedirects)
/*      */   {
/*  879 */     this.maxRedirects = maxRedirects;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute(value="Whether the TCP_NODELAY option is enabled", name="tcpNoDelay")
/*      */   public boolean isTCPNoDelay()
/*      */   {
/*  888 */     return this.tcpNoDelay;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTCPNoDelay(boolean tcpNoDelay)
/*      */   {
/*  897 */     this.tcpNoDelay = tcpNoDelay;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public boolean isDispatchIO()
/*      */   {
/*  908 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public void setDispatchIO(boolean dispatchIO) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("Whether request/response events must be strictly ordered")
/*      */   public boolean isStrictEventOrdering()
/*      */   {
/*  936 */     return this.strictEventOrdering;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStrictEventOrdering(boolean strictEventOrdering)
/*      */   {
/*  967 */     this.strictEventOrdering = strictEventOrdering;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("Whether idle destinations are removed")
/*      */   public boolean isRemoveIdleDestinations()
/*      */   {
/*  977 */     return this.removeIdleDestinations;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRemoveIdleDestinations(boolean removeIdleDestinations)
/*      */   {
/*  994 */     this.removeIdleDestinations = removeIdleDestinations;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @ManagedAttribute("Whether the connect() operation is blocking")
/*      */   public boolean isConnectBlocking()
/*      */   {
/* 1003 */     return this.connectBlocking;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setConnectBlocking(boolean connectBlocking)
/*      */   {
/* 1018 */     this.connectBlocking = connectBlocking;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ProxyConfiguration getProxyConfiguration()
/*      */   {
/* 1026 */     return this.proxyConfig;
/*      */   }
/*      */   
/*      */   protected HttpField getAcceptEncodingField()
/*      */   {
/* 1031 */     return this.encodingField;
/*      */   }
/*      */   
/*      */   protected String normalizeHost(String host)
/*      */   {
/* 1036 */     if ((host != null) && (host.matches("\\[.*\\]")))
/* 1037 */       return host.substring(1, host.length() - 1);
/* 1038 */     return host;
/*      */   }
/*      */   
/*      */   public static int normalizePort(String scheme, int port)
/*      */   {
/* 1043 */     return HttpScheme.HTTPS.is(scheme) ? 443 : port > 0 ? port : 80;
/*      */   }
/*      */   
/*      */   public boolean isDefaultPort(String scheme, int port)
/*      */   {
/* 1048 */     return port == 443;
/*      */   }
/*      */   
/*      */   private class ContentDecoderFactorySet implements Set<ContentDecoder.Factory>
/*      */   {
/* 1053 */     private final Set<ContentDecoder.Factory> set = new HashSet();
/*      */     
/*      */     private ContentDecoderFactorySet() {}
/*      */     
/*      */     public boolean add(ContentDecoder.Factory e) {
/* 1058 */       boolean result = this.set.add(e);
/* 1059 */       invalidate();
/* 1060 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean addAll(Collection<? extends ContentDecoder.Factory> c)
/*      */     {
/* 1066 */       boolean result = this.set.addAll(c);
/* 1067 */       invalidate();
/* 1068 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 1074 */       boolean result = this.set.remove(o);
/* 1075 */       invalidate();
/* 1076 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean removeAll(Collection<?> c)
/*      */     {
/* 1082 */       boolean result = this.set.removeAll(c);
/* 1083 */       invalidate();
/* 1084 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean retainAll(Collection<?> c)
/*      */     {
/* 1090 */       boolean result = this.set.retainAll(c);
/* 1091 */       invalidate();
/* 1092 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public void clear()
/*      */     {
/* 1098 */       this.set.clear();
/* 1099 */       invalidate();
/*      */     }
/*      */     
/*      */ 
/*      */     public int size()
/*      */     {
/* 1105 */       return this.set.size();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 1111 */       return this.set.isEmpty();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 1117 */       return this.set.contains(o);
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean containsAll(Collection<?> c)
/*      */     {
/* 1123 */       return this.set.containsAll(c);
/*      */     }
/*      */     
/*      */ 
/*      */     public Iterator<ContentDecoder.Factory> iterator()
/*      */     {
/* 1129 */       final Iterator<ContentDecoder.Factory> iterator = this.set.iterator();
/* 1130 */       new Iterator()
/*      */       {
/*      */ 
/*      */         public boolean hasNext()
/*      */         {
/* 1135 */           return iterator.hasNext();
/*      */         }
/*      */         
/*      */ 
/*      */         public ContentDecoder.Factory next()
/*      */         {
/* 1141 */           return (ContentDecoder.Factory)iterator.next();
/*      */         }
/*      */         
/*      */ 
/*      */         public void remove()
/*      */         {
/* 1147 */           iterator.remove();
/* 1148 */           ContentDecoderFactorySet.this.invalidate();
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*      */ 
/*      */     public Object[] toArray()
/*      */     {
/* 1156 */       return this.set.toArray();
/*      */     }
/*      */     
/*      */ 
/*      */     public <T> T[] toArray(T[] a)
/*      */     {
/* 1162 */       return this.set.toArray(a);
/*      */     }
/*      */     
/*      */     private void invalidate()
/*      */     {
/* 1167 */       if (this.set.isEmpty())
/*      */       {
/* 1169 */         HttpClient.this.encodingField = null;
/*      */       }
/*      */       else
/*      */       {
/* 1173 */         StringBuilder value = new StringBuilder();
/* 1174 */         for (Iterator<ContentDecoder.Factory> iterator = this.set.iterator(); iterator.hasNext();)
/*      */         {
/* 1176 */           ContentDecoder.Factory decoderFactory = (ContentDecoder.Factory)iterator.next();
/* 1177 */           value.append(decoderFactory.getEncoding());
/* 1178 */           if (iterator.hasNext())
/* 1179 */             value.append(",");
/*      */         }
/* 1181 */         HttpClient.this.encodingField = new HttpField(HttpHeader.ACCEPT_ENCODING, value.toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */