/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
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
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public class ImmutableSetMultimap<K, V>
/*     */   extends ImmutableMultimap<K, V>
/*     */   implements SetMultimap<K, V>
/*     */ {
/*     */   private final transient ImmutableSet<V> emptySet;
/*     */   private transient ImmutableSetMultimap<V, K> inverse;
/*     */   private transient ImmutableSet<Entry<K, V>> entries;
/*     */   @GwtIncompatible("not needed in emulated source.")
/*     */   private static final long serialVersionUID = 0L;
/*     */   
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of()
/*     */   {
/*  73 */     return EmptyImmutableSetMultimap.INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1)
/*     */   {
/*  80 */     Builder<K, V> builder = builder();
/*  81 */     builder.put(k1, v1);
/*  82 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  91 */     Builder<K, V> builder = builder();
/*  92 */     builder.put(k1, v1);
/*  93 */     builder.put(k2, v2);
/*  94 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/* 104 */     Builder<K, V> builder = builder();
/* 105 */     builder.put(k1, v1);
/* 106 */     builder.put(k2, v2);
/* 107 */     builder.put(k3, v3);
/* 108 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 118 */     Builder<K, V> builder = builder();
/* 119 */     builder.put(k1, v1);
/* 120 */     builder.put(k2, v2);
/* 121 */     builder.put(k3, v3);
/* 122 */     builder.put(k4, v4);
/* 123 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 133 */     Builder<K, V> builder = builder();
/* 134 */     builder.put(k1, v1);
/* 135 */     builder.put(k2, v2);
/* 136 */     builder.put(k3, v3);
/* 137 */     builder.put(k4, v4);
/* 138 */     builder.put(k5, v5);
/* 139 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> Builder<K, V> builder()
/*     */   {
/* 148 */     return new Builder();
/*     */   }
/*     */   
/*     */   private static class BuilderMultimap<K, V> extends AbstractMapBasedMultimap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     BuilderMultimap()
/*     */     {
/* 157 */       super();
/*     */     }
/*     */     
/* 160 */     Collection<V> createCollection() { return Sets.newLinkedHashSet(); }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final class Builder<K, V>
/*     */     extends ImmutableMultimap.Builder<K, V>
/*     */   {
/*     */     public Builder()
/*     */     {
/* 190 */       this.builderMultimap = new BuilderMultimap();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 198 */       this.builderMultimap.put(Preconditions.checkNotNull(key), Preconditions.checkNotNull(value));
/* 199 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<K, V> put(Entry<? extends K, ? extends V> entry)
/*     */     {
/* 208 */       this.builderMultimap.put(Preconditions.checkNotNull(entry.getKey()), Preconditions.checkNotNull(entry.getValue()));
/*     */       
/* 210 */       return this;
/*     */     }
/*     */     
/*     */     public Builder<K, V> putAll(K key, Iterable<? extends V> values) {
/* 214 */       Collection<V> collection = this.builderMultimap.get(Preconditions.checkNotNull(key));
/* 215 */       for (V value : values) {
/* 216 */         collection.add(Preconditions.checkNotNull(value));
/*     */       }
/* 218 */       return this;
/*     */     }
/*     */     
/*     */     public Builder<K, V> putAll(K key, V... values) {
/* 222 */       return putAll(key, Arrays.asList(values));
/*     */     }
/*     */     
/*     */ 
/*     */     public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap)
/*     */     {
/* 228 */       for (Entry<? extends K, ? extends Collection<? extends V>> entry : multimap.asMap().entrySet()) {
/* 229 */         putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */       }
/* 231 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator)
/*     */     {
/* 241 */       this.keyComparator = ((Comparator)Preconditions.checkNotNull(keyComparator));
/* 242 */       return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator)
/*     */     {
/* 259 */       super.orderValuesBy(valueComparator);
/* 260 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public ImmutableSetMultimap<K, V> build()
/*     */     {
/* 267 */       if (this.keyComparator != null) {
/* 268 */         Multimap<K, V> sortedCopy = new BuilderMultimap();
/* 269 */         List<Entry<K, Collection<V>>> entries = Lists.newArrayList(this.builderMultimap.asMap().entrySet());
/*     */         
/* 271 */         Collections.sort(entries, Ordering.from(this.keyComparator).onKeys());
/*     */         
/*     */ 
/* 274 */         for (Entry<K, Collection<V>> entry : entries) {
/* 275 */           sortedCopy.putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */         }
/* 277 */         this.builderMultimap = sortedCopy;
/*     */       }
/* 279 */       return ImmutableSetMultimap.copyOf(this.builderMultimap, this.valueComparator);
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
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 299 */     return copyOf(multimap, null);
/*     */   }
/*     */   
/*     */ 
/*     */   private static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap, Comparator<? super V> valueComparator)
/*     */   {
/* 305 */     Preconditions.checkNotNull(multimap);
/* 306 */     if ((multimap.isEmpty()) && (valueComparator == null)) {
/* 307 */       return of();
/*     */     }
/*     */     
/* 310 */     if ((multimap instanceof ImmutableSetMultimap))
/*     */     {
/* 312 */       ImmutableSetMultimap<K, V> kvMultimap = (ImmutableSetMultimap)multimap;
/*     */       
/* 314 */       if (!kvMultimap.isPartialView()) {
/* 315 */         return kvMultimap;
/*     */       }
/*     */     }
/*     */     
/* 319 */     ImmutableMap.Builder<K, ImmutableSet<V>> builder = ImmutableMap.builder();
/* 320 */     int size = 0;
/*     */     
/*     */ 
/* 323 */     for (Entry<? extends K, ? extends Collection<? extends V>> entry : multimap.asMap().entrySet()) {
/* 324 */       K key = entry.getKey();
/* 325 */       Collection<? extends V> values = (Collection)entry.getValue();
/* 326 */       ImmutableSet<V> set = valueSet(valueComparator, values);
/* 327 */       if (!set.isEmpty()) {
/* 328 */         builder.put(key, set);
/* 329 */         size += set.size();
/*     */       }
/*     */     }
/*     */     
/* 333 */     return new ImmutableSetMultimap(builder.build(), size, valueComparator);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ImmutableSetMultimap(ImmutableMap<K, ImmutableSet<V>> map, int size, @Nullable Comparator<? super V> valueComparator)
/*     */   {
/* 345 */     super(map, size);
/* 346 */     this.emptySet = emptySet(valueComparator);
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
/*     */   public ImmutableSet<V> get(@Nullable K key)
/*     */   {
/* 359 */     ImmutableSet<V> set = (ImmutableSet)this.map.get(key);
/* 360 */     return (ImmutableSet)MoreObjects.firstNonNull(set, this.emptySet);
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
/*     */   public ImmutableSetMultimap<V, K> inverse()
/*     */   {
/* 376 */     ImmutableSetMultimap<V, K> result = this.inverse;
/* 377 */     return result == null ? (this.inverse = invert()) : result;
/*     */   }
/*     */   
/*     */   private ImmutableSetMultimap<V, K> invert() {
/* 381 */     Builder<V, K> builder = builder();
/* 382 */     for (Entry<K, V> entry : entries()) {
/* 383 */       builder.put(entry.getValue(), entry.getKey());
/*     */     }
/* 385 */     ImmutableSetMultimap<V, K> invertedMultimap = builder.build();
/* 386 */     invertedMultimap.inverse = this;
/* 387 */     return invertedMultimap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public ImmutableSet<V> removeAll(Object key)
/*     */   {
/* 397 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public ImmutableSet<V> replaceValues(K key, Iterable<? extends V> values)
/*     */   {
/* 408 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableSet<Entry<K, V>> entries()
/*     */   {
/* 419 */     ImmutableSet<Entry<K, V>> result = this.entries;
/* 420 */     return result == null ? (this.entries = new EntrySet(this)) : result;
/*     */   }
/*     */   
/*     */   private static final class EntrySet<K, V> extends ImmutableSet<Entry<K, V>>
/*     */   {
/*     */     private final transient ImmutableSetMultimap<K, V> multimap;
/*     */     
/*     */     EntrySet(ImmutableSetMultimap<K, V> multimap)
/*     */     {
/* 429 */       this.multimap = multimap;
/*     */     }
/*     */     
/*     */     public boolean contains(@Nullable Object object)
/*     */     {
/* 434 */       if ((object instanceof Map.Entry)) {
/* 435 */         Entry<?, ?> entry = (Entry)object;
/* 436 */         return this.multimap.containsEntry(entry.getKey(), entry.getValue());
/*     */       }
/* 438 */       return false;
/*     */     }
/*     */     
/*     */     public int size()
/*     */     {
/* 443 */       return this.multimap.size();
/*     */     }
/*     */     
/*     */     public UnmodifiableIterator<Entry<K, V>> iterator()
/*     */     {
/* 448 */       return this.multimap.entryIterator();
/*     */     }
/*     */     
/*     */     boolean isPartialView()
/*     */     {
/* 453 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static <V> ImmutableSet<V> valueSet(@Nullable Comparator<? super V> valueComparator, Collection<? extends V> values)
/*     */   {
/* 460 */     return valueComparator == null ? ImmutableSet.copyOf(values) : ImmutableSortedSet.copyOf(valueComparator, values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static <V> ImmutableSet<V> emptySet(@Nullable Comparator<? super V> valueComparator)
/*     */   {
/* 467 */     return valueComparator == null ? ImmutableSet.of() : ImmutableSortedSet.emptySet(valueComparator);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 478 */     stream.defaultWriteObject();
/* 479 */     stream.writeObject(valueComparator());
/* 480 */     Serialization.writeMultimap(this, stream);
/*     */   }
/*     */   
/*     */   @Nullable
/* 484 */   Comparator<? super V> valueComparator() { return (this.emptySet instanceof ImmutableSortedSet) ? ((ImmutableSortedSet)this.emptySet).comparator() : null; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 494 */     stream.defaultReadObject();
/* 495 */     Comparator<Object> valueComparator = (Comparator)stream.readObject();
/*     */     
/* 497 */     int keyCount = stream.readInt();
/* 498 */     if (keyCount < 0) {
/* 499 */       int i = keyCount;throw new InvalidObjectException(29 + "Invalid key count " + i);
/*     */     }
/* 501 */     Object builder = ImmutableMap.builder();
/*     */     
/* 503 */     int tmpSize = 0;
/*     */     
/* 505 */     for (int i = 0; i < keyCount; i++) {
/* 506 */       Object key = stream.readObject();
/* 507 */       int valueCount = stream.readInt();
/* 508 */       if (valueCount <= 0) {
/* 509 */         int j = valueCount;throw new InvalidObjectException(31 + "Invalid value count " + j);
/*     */       }
/*     */       
/* 512 */       Object[] array = new Object[valueCount];
/* 513 */       for (int j = 0; j < valueCount; j++) {
/* 514 */         array[j] = stream.readObject();
/*     */       }
/* 516 */       ImmutableSet<Object> valueSet = valueSet(valueComparator, Arrays.asList(array));
/* 517 */       if (valueSet.size() != array.length) {
/* 518 */         String str = String.valueOf(String.valueOf(key));throw new InvalidObjectException(40 + str.length() + "Duplicate key-value pairs exist for key " + str);
/*     */       }
/*     */       
/* 521 */       ((ImmutableMap.Builder)builder).put(key, valueSet);
/* 522 */       tmpSize += valueCount;
/*     */     }
/*     */     ImmutableMap<Object, ImmutableSet<Object>> tmpMap;
/*     */     try
/*     */     {
/* 527 */       tmpMap = ((ImmutableMap.Builder)builder).build();
/*     */     } catch (IllegalArgumentException e) {
/* 529 */       throw ((InvalidObjectException)new InvalidObjectException(e.getMessage()).initCause(e));
/*     */     }
/*     */     
/*     */ 
/* 533 */     ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, tmpMap);
/* 534 */     ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, tmpSize);
/* 535 */     ImmutableMultimap.FieldSettersHolder.EMPTY_SET_FIELD_SETTER.set(this, emptySet(valueComparator));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ImmutableSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */