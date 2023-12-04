/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.configuration.ConfigDefaults;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.google.inject.Binder;
/*     */ import com.google.inject.binder.LinkedBindingBuilder;
/*     */ import com.google.inject.multibindings.Multibinder;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.Collection;
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
/*     */ public class HttpClientBinder
/*     */ {
/*     */   private final Binder binder;
/*     */   
/*     */   private HttpClientBinder(Binder binder)
/*     */   {
/*  37 */     this.binder = ((Binder)Preconditions.checkNotNull(binder, "binder is null")).skipSources(new Class[] { getClass() });
/*     */   }
/*     */   
/*     */   public static HttpClientBinder httpClientBinder(Binder binder)
/*     */   {
/*  42 */     return new HttpClientBinder(binder);
/*     */   }
/*     */   
/*     */   public HttpClientBindingBuilder bindHttpClient(String name, Class<? extends Annotation> annotation)
/*     */   {
/*  47 */     Preconditions.checkNotNull(name, "name is null");
/*  48 */     Preconditions.checkNotNull(annotation, "annotation is null");
/*  49 */     return createBindingBuilder(new HttpClientModule(name, annotation));
/*     */   }
/*     */   
/*     */   private HttpClientBindingBuilder createBindingBuilder(HttpClientModule module)
/*     */   {
/*  54 */     this.binder.install(module);
/*  55 */     return new HttpClientBindingBuilder(module, 
/*  56 */       Multibinder.newSetBinder(this.binder, HttpRequestFilter.class, module.getFilterQualifier()));
/*     */   }
/*     */   
/*     */   public static class HttpClientBindingBuilder
/*     */   {
/*     */     private final HttpClientModule module;
/*     */     private final Multibinder<HttpRequestFilter> multibinder;
/*     */     
/*     */     public HttpClientBindingBuilder(HttpClientModule module, Multibinder<HttpRequestFilter> multibinder)
/*     */     {
/*  66 */       this.module = module;
/*  67 */       this.multibinder = multibinder;
/*     */     }
/*     */     
/*     */     public HttpClientBindingBuilder withAlias(Class<? extends Annotation> alias)
/*     */     {
/*  72 */       this.module.addAlias(alias);
/*  73 */       return this;
/*     */     }
/*     */     
/*     */     public HttpClientBindingBuilder withAliases(Collection<Class<? extends Annotation>> aliases)
/*     */     {
/*  78 */       for (Class<? extends Annotation> annotation : aliases) {
/*  79 */         this.module.addAlias(annotation);
/*     */       }
/*  81 */       return this;
/*     */     }
/*     */     
/*     */     public HttpClientBindingBuilder withConfigDefaults(ConfigDefaults<HttpClientConfig> configDefaults)
/*     */     {
/*  86 */       this.module.withConfigDefaults(configDefaults);
/*  87 */       return this;
/*     */     }
/*     */     
/*     */     public LinkedBindingBuilder<HttpRequestFilter> addFilterBinding()
/*     */     {
/*  92 */       return this.multibinder.addBinding();
/*     */     }
/*     */     
/*     */     public HttpClientBindingBuilder withFilter(Class<? extends HttpRequestFilter> filterClass)
/*     */     {
/*  97 */       this.multibinder.addBinding().to(filterClass);
/*  98 */       return this;
/*     */     }
/*     */     
/*     */     public HttpClientBindingBuilder withTracing()
/*     */     {
/* 103 */       return withFilter(TraceTokenRequestFilter.class);
/*     */     }
/*     */     
/*     */     public HttpClientBindingBuilder withPrivateIoThreadPool()
/*     */     {
/* 108 */       this.module.withPrivateIoThreadPool();
/* 109 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HttpClientBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */