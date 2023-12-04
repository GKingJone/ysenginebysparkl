/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.configuration.ConfigBinder;
/*     */ import com.facebook.presto.jdbc.internal.airlift.configuration.ConfigDefaults;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.jetty.JettyHttpClient;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.jetty.JettyIoPool;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.jetty.JettyIoPoolConfig;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.spnego.KerberosConfig;
/*     */ import com.facebook.presto.jdbc.internal.airlift.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.google.inject.Binder;
/*     */ import com.google.inject.Inject;
/*     */ import com.google.inject.Injector;
/*     */ import com.google.inject.Key;
/*     */ import com.google.inject.Provider;
/*     */ import com.google.inject.Scopes;
/*     */ import com.google.inject.TypeLiteral;
/*     */ import com.google.inject.binder.AnnotatedBindingBuilder;
/*     */ import com.google.inject.binder.LinkedBindingBuilder;
/*     */ import com.google.inject.binder.ScopedBindingBuilder;
/*     */ import com.google.inject.multibindings.Multibinder;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import javax.annotation.PreDestroy;
/*     */ import org.weakref.jmx.guice.AnnotatedExportBinder;
/*     */ import org.weakref.jmx.guice.ExportBinder;
/*     */ import org.weakref.jmx.guice.NamedExportBinder;
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
/*     */ public class HttpClientModule
/*     */   extends AbstractHttpClientModule
/*     */ {
/*  52 */   private static final Logger log = Logger.get(HttpClientModule.class);
/*     */   
/*     */   protected HttpClientModule(String name, Class<? extends Annotation> annotation)
/*     */   {
/*  56 */     super(name, annotation);
/*     */   }
/*     */   
/*     */ 
/*     */   public Annotation getFilterQualifier()
/*     */   {
/*  62 */     return filterQualifier(this.annotation);
/*     */   }
/*     */   
/*     */   void withConfigDefaults(ConfigDefaults<HttpClientConfig> configDefaults)
/*     */   {
/*  67 */     ConfigBinder.configBinder(this.binder).bindConfigDefaults(HttpClientConfig.class, this.annotation, configDefaults);
/*     */   }
/*     */   
/*     */   void withPrivateIoThreadPool()
/*     */   {
/*  72 */     ConfigBinder.configBinder(this.binder).bindConfig(JettyIoPoolConfig.class, this.annotation, this.name);
/*  73 */     this.binder.bind(JettyIoPoolManager.class).annotatedWith(this.annotation).toInstance(new JettyIoPoolManager(this.name, this.annotation, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void configure()
/*     */   {
/*  80 */     ConfigBinder.configBinder(this.binder).bindConfig(KerberosConfig.class);
/*  81 */     ConfigBinder.configBinder(this.binder).bindConfig(HttpClientConfig.class, this.annotation, this.name);
/*     */     
/*     */ 
/*  84 */     ConfigBinder.configBinder(this.binder).bindConfig(JettyIoPoolConfig.class);
/*  85 */     this.binder.bind(JettyIoPoolManager.class).to(SharedJettyIoPoolManager.class).in(Scopes.SINGLETON);
/*     */     
/*     */ 
/*  88 */     this.binder.bind(HttpClient.class).annotatedWith(this.annotation).toProvider(new HttpClientProvider(this.name, this.annotation, null)).in(Scopes.SINGLETON);
/*     */     
/*     */ 
/*  91 */     Multibinder.newSetBinder(this.binder, HttpRequestFilter.class, filterQualifier(this.annotation));
/*     */     
/*     */ 
/*  94 */     ExportBinder.newExporter(this.binder).export(HttpClient.class).annotatedWith(this.annotation).withGeneratedName();
/*     */   }
/*     */   
/*     */ 
/*     */   public void addAlias(Class<? extends Annotation> alias)
/*     */   {
/* 100 */     this.binder.bind(HttpClient.class).annotatedWith(alias).to(Key.get(HttpClient.class, this.annotation));
/*     */   }
/*     */   
/*     */   private static class HttpClientProvider
/*     */     implements Provider<HttpClient>
/*     */   {
/*     */     private final String name;
/*     */     private final Class<? extends Annotation> annotation;
/*     */     private Injector injector;
/*     */     
/*     */     private HttpClientProvider(String name, Class<? extends Annotation> annotation)
/*     */     {
/* 112 */       this.name = name;
/* 113 */       this.annotation = annotation;
/*     */     }
/*     */     
/*     */     @Inject
/*     */     public void setInjector(Injector injector)
/*     */     {
/* 119 */       this.injector = injector;
/*     */     }
/*     */     
/*     */ 
/*     */     public HttpClient get()
/*     */     {
/* 125 */       KerberosConfig kerberosConfig = (KerberosConfig)this.injector.getInstance(KerberosConfig.class);
/*     */       
/* 127 */       HttpClientConfig config = (HttpClientConfig)this.injector.getInstance(Key.get(HttpClientConfig.class, this.annotation));
/* 128 */       Set<HttpRequestFilter> filters = (Set)this.injector.getInstance(HttpClientModule.filterKey(this.annotation));
/*     */       JettyIoPoolManager ioPoolProvider;
/*     */       JettyIoPoolManager ioPoolProvider;
/* 131 */       if (this.injector.getExistingBinding(Key.get(JettyIoPoolManager.class, this.annotation)) != null) {
/* 132 */         HttpClientModule.log.debug("HttpClient %s uses private IO thread pool", new Object[] { this.name });
/* 133 */         ioPoolProvider = (JettyIoPoolManager)this.injector.getInstance(Key.get(JettyIoPoolManager.class, this.annotation));
/*     */       }
/*     */       else {
/* 136 */         HttpClientModule.log.debug("HttpClient %s uses shared IO thread pool", new Object[] { this.name });
/* 137 */         ioPoolProvider = (JettyIoPoolManager)this.injector.getInstance(JettyIoPoolManager.class);
/*     */       }
/*     */       
/* 140 */       JettyHttpClient client = new JettyHttpClient(config, kerberosConfig, Optional.of(ioPoolProvider.get()), ImmutableList.copyOf(filters));
/* 141 */       ioPoolProvider.addClient(client);
/* 142 */       return client;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SharedJettyIoPoolManager
/*     */     extends JettyIoPoolManager
/*     */   {
/*     */     private SharedJettyIoPoolManager()
/*     */     {
/* 151 */       super(null, null);
/*     */     }
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   public static class JettyIoPoolManager
/*     */   {
/* 158 */     private final List<JettyHttpClient> clients = new ArrayList();
/*     */     private final String name;
/*     */     private final Class<? extends Annotation> annotation;
/* 161 */     private final AtomicBoolean destroyed = new AtomicBoolean();
/*     */     private JettyIoPool pool;
/*     */     private Injector injector;
/*     */     
/*     */     private JettyIoPoolManager(String name, Class<? extends Annotation> annotation)
/*     */     {
/* 167 */       this.name = name;
/* 168 */       this.annotation = annotation;
/*     */     }
/*     */     
/*     */     public void addClient(JettyHttpClient client)
/*     */     {
/* 173 */       this.clients.add(client);
/*     */     }
/*     */     
/*     */     public boolean isDestroyed()
/*     */     {
/* 178 */       return this.destroyed.get();
/*     */     }
/*     */     
/*     */     @Inject
/*     */     public void setInjector(Injector injector)
/*     */     {
/* 184 */       this.injector = injector;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     @PreDestroy
/*     */     public void destroy()
/*     */     {
/* 192 */       for (JettyHttpClient client : this.clients) {
/* 193 */         client.close();
/*     */       }
/* 195 */       if (this.pool != null) {
/* 196 */         this.pool.close();
/* 197 */         this.pool = null;
/*     */       }
/* 199 */       this.destroyed.set(true);
/*     */     }
/*     */     
/*     */     public JettyIoPool get()
/*     */     {
/* 204 */       if (this.pool == null) {
/* 205 */         JettyIoPoolConfig config = (JettyIoPoolConfig)this.injector.getInstance(HttpClientModule.keyFromNullable(JettyIoPoolConfig.class, this.annotation));
/* 206 */         this.pool = new JettyIoPool(this.name, config);
/*     */       }
/* 208 */       return this.pool;
/*     */     }
/*     */   }
/*     */   
/*     */   private static <T> Key<T> keyFromNullable(Class<T> type, Class<? extends Annotation> annotation)
/*     */   {
/* 214 */     return annotation != null ? Key.get(type, annotation) : Key.get(type);
/*     */   }
/*     */   
/*     */   private static Key<Set<HttpRequestFilter>> filterKey(Class<? extends Annotation> annotation)
/*     */   {
/* 219 */     Key.get(new TypeLiteral() {}, filterQualifier(annotation));
/*     */   }
/*     */   
/*     */   private static CompositeQualifier filterQualifier(Class<? extends Annotation> annotation)
/*     */   {
/* 224 */     return CompositeQualifierImpl.compositeQualifier(new Class[] { annotation, HttpClient.class });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HttpClientModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */