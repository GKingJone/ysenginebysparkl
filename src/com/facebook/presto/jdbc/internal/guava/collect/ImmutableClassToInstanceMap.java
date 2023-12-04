/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.primitives.Primitives;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
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
/*     */ public final class ImmutableClassToInstanceMap<B>
/*     */   extends ForwardingMap<Class<? extends B>, B>
/*     */   implements ClassToInstanceMap<B>, Serializable
/*     */ {
/*     */   private final ImmutableMap<Class<? extends B>, B> delegate;
/*     */   
/*     */   public static <B> Builder<B> builder()
/*     */   {
/*  44 */     return new Builder();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final class Builder<B>
/*     */   {
/*  65 */     private final ImmutableMap.Builder<Class<? extends B>, B> mapBuilder = ImmutableMap.builder();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public <T extends B> Builder<B> put(Class<T> key, T value)
/*     */     {
/*  73 */       this.mapBuilder.put(key, value);
/*  74 */       return this;
/*     */     }
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
/*     */     public <T extends B> Builder<B> putAll(Map<? extends Class<? extends T>, ? extends T> map)
/*     */     {
/*  88 */       for (Entry<? extends Class<? extends T>, ? extends T> entry : map.entrySet()) {
/*  89 */         Class<? extends T> type = (Class)entry.getKey();
/*  90 */         T value = entry.getValue();
/*  91 */         this.mapBuilder.put(type, cast(type, value));
/*     */       }
/*  93 */       return this;
/*     */     }
/*     */     
/*     */     private static <B, T extends B> T cast(Class<T> type, B value) {
/*  97 */       return (T)Primitives.wrap(type).cast(value);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public ImmutableClassToInstanceMap<B> build()
/*     */     {
/* 107 */       return new ImmutableClassToInstanceMap(this.mapBuilder.build(), null);
/*     */     }
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
/*     */ 
/*     */ 
/*     */   public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(Map<? extends Class<? extends S>, ? extends S> map)
/*     */   {
/* 126 */     if ((map instanceof ImmutableClassToInstanceMap))
/*     */     {
/*     */ 
/* 129 */       ImmutableClassToInstanceMap<B> cast = (ImmutableClassToInstanceMap)map;
/* 130 */       return cast;
/*     */     }
/* 132 */     return new Builder().putAll(map).build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ImmutableClassToInstanceMap(ImmutableMap<Class<? extends B>, B> delegate)
/*     */   {
/* 139 */     this.delegate = delegate;
/*     */   }
/*     */   
/*     */   protected Map<Class<? extends B>, B> delegate() {
/* 143 */     return this.delegate;
/*     */   }
/*     */   
/*     */ 
/*     */   @Nullable
/*     */   public <T extends B> T getInstance(Class<T> type)
/*     */   {
/* 150 */     return (T)this.delegate.get(Preconditions.checkNotNull(type));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public <T extends B> T putInstance(Class<T> type, T value)
/*     */   {
/* 162 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ImmutableClassToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */