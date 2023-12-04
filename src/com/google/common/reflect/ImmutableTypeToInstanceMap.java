/*     */ package com.google.common.reflect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.collect.ForwardingMap;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import java.util.Map;
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
/*     */ @Beta
/*     */ public final class ImmutableTypeToInstanceMap<B>
/*     */   extends ForwardingMap<TypeToken<? extends B>, B>
/*     */   implements TypeToInstanceMap<B>
/*     */ {
/*     */   private final ImmutableMap<TypeToken<? extends B>, B> delegate;
/*     */   
/*     */   public static <B> ImmutableTypeToInstanceMap<B> of()
/*     */   {
/*  38 */     return new ImmutableTypeToInstanceMap(ImmutableMap.of());
/*     */   }
/*     */   
/*     */   public static <B> Builder<B> builder()
/*     */   {
/*  43 */     return new Builder(null);
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
/*     */   @Beta
/*     */   public static final class Builder<B>
/*     */   {
/*  64 */     private final ImmutableMap.Builder<TypeToken<? extends B>, B> mapBuilder = ImmutableMap.builder();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public <T extends B> Builder<B> put(Class<T> key, T value)
/*     */     {
/*  74 */       this.mapBuilder.put(TypeToken.of(key), value);
/*  75 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public <T extends B> Builder<B> put(TypeToken<T> key, T value)
/*     */     {
/*  83 */       this.mapBuilder.put(key.rejectTypeVariables(), value);
/*  84 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public ImmutableTypeToInstanceMap<B> build()
/*     */     {
/*  94 */       return new ImmutableTypeToInstanceMap(this.mapBuilder.build(), null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private ImmutableTypeToInstanceMap(ImmutableMap<TypeToken<? extends B>, B> delegate)
/*     */   {
/* 101 */     this.delegate = delegate;
/*     */   }
/*     */   
/*     */   public <T extends B> T getInstance(TypeToken<T> type) {
/* 105 */     return (T)trustedGet(type.rejectTypeVariables());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T extends B> T putInstance(TypeToken<T> type, T value)
/*     */   {
/* 114 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public <T extends B> T getInstance(Class<T> type) {
/* 118 */     return (T)trustedGet(TypeToken.of(type));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T extends B> T putInstance(Class<T> type, T value)
/*     */   {
/* 127 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   protected Map<TypeToken<? extends B>, B> delegate() {
/* 131 */     return this.delegate;
/*     */   }
/*     */   
/*     */   private <T extends B> T trustedGet(TypeToken<T> type)
/*     */   {
/* 136 */     return (T)this.delegate.get(type);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\reflect\ImmutableTypeToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */