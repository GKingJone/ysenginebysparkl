/*     */ package com.facebook.presto.jdbc.internal.guava.reflect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ForwardingMap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ForwardingMapEntry;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ForwardingSet;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Iterators;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Maps;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ @Beta
/*     */ public final class MutableTypeToInstanceMap<B>
/*     */   extends ForwardingMap<TypeToken<? extends B>, B>
/*     */   implements TypeToInstanceMap<B>
/*     */ {
/*     */   private final Map<TypeToken<? extends B>, B> backingMap;
/*     */   
/*     */   public MutableTypeToInstanceMap()
/*     */   {
/*  46 */     this.backingMap = Maps.newHashMap();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T extends B> T getInstance(Class<T> type) {
/*  51 */     return (T)trustedGet(TypeToken.of(type));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T extends B> T putInstance(Class<T> type, @Nullable T value)
/*     */   {
/*  57 */     return (T)trustedPut(TypeToken.of(type), value);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T extends B> T getInstance(TypeToken<T> type)
/*     */   {
/*  63 */     return (T)trustedGet(type.rejectTypeVariables());
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T extends B> T putInstance(TypeToken<T> type, @Nullable T value)
/*     */   {
/*  69 */     return (T)trustedPut(type.rejectTypeVariables(), value);
/*     */   }
/*     */   
/*     */   public B put(TypeToken<? extends B> key, B value)
/*     */   {
/*  74 */     throw new UnsupportedOperationException("Please use putInstance() instead.");
/*     */   }
/*     */   
/*     */   public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> map)
/*     */   {
/*  79 */     throw new UnsupportedOperationException("Please use putInstance() instead.");
/*     */   }
/*     */   
/*     */   public Set<Entry<TypeToken<? extends B>, B>> entrySet() {
/*  83 */     return UnmodifiableEntry.transformEntries(super.entrySet());
/*     */   }
/*     */   
/*     */   protected Map<TypeToken<? extends B>, B> delegate() {
/*  87 */     return this.backingMap;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private <T extends B> T trustedPut(TypeToken<T> type, @Nullable T value)
/*     */   {
/*  93 */     return (T)this.backingMap.put(type, value);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private <T extends B> T trustedGet(TypeToken<T> type)
/*     */   {
/*  99 */     return (T)this.backingMap.get(type);
/*     */   }
/*     */   
/*     */   private static final class UnmodifiableEntry<K, V> extends ForwardingMapEntry<K, V>
/*     */   {
/*     */     private final Entry<K, V> delegate;
/*     */     
/*     */     static <K, V> Set<Entry<K, V>> transformEntries(Set<Entry<K, V>> entries) {
/* 107 */       new ForwardingSet() {
/*     */         protected Set<Entry<K, V>> delegate() {
/* 109 */           return this.val$entries;
/*     */         }
/*     */         
/* 112 */         public Iterator<Entry<K, V>> iterator() { return UnmodifiableEntry.transformEntries(super.iterator()); }
/*     */         
/*     */         public Object[] toArray() {
/* 115 */           return standardToArray();
/*     */         }
/*     */         
/* 118 */         public <T> T[] toArray(T[] array) { return standardToArray(array); }
/*     */       };
/*     */     }
/*     */     
/*     */     private static <K, V> Iterator<Entry<K, V>> transformEntries(Iterator<Entry<K, V>> entries)
/*     */     {
/* 124 */       Iterators.transform(entries, new Function() {
/*     */         public Entry<K, V> apply(Entry<K, V> entry) {
/* 126 */           return new UnmodifiableEntry(entry, null);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     private UnmodifiableEntry(Entry<K, V> delegate) {
/* 132 */       this.delegate = ((Entry)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */     
/*     */     protected Entry<K, V> delegate() {
/* 136 */       return this.delegate;
/*     */     }
/*     */     
/*     */     public V setValue(V value) {
/* 140 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\reflect\MutableTypeToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */