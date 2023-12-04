/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
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
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ class RegularImmutableBiMap<K, V>
/*     */   extends ImmutableBiMap<K, V>
/*     */ {
/*     */   static final double MAX_LOAD_FACTOR = 1.2D;
/*     */   private final transient ImmutableMapEntry<K, V>[] keyTable;
/*     */   private final transient ImmutableMapEntry<K, V>[] valueTable;
/*     */   private final transient ImmutableMapEntry<K, V>[] entries;
/*     */   private final transient int mask;
/*     */   private final transient int hashCode;
/*     */   private transient ImmutableBiMap<V, K> inverse;
/*     */   
/*     */   RegularImmutableBiMap(ImmutableMapEntry.TerminalEntry<?, ?>... entriesToAdd)
/*     */   {
/*  46 */     this(entriesToAdd.length, entriesToAdd);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   RegularImmutableBiMap(int n, ImmutableMapEntry.TerminalEntry<?, ?>[] entriesToAdd)
/*     */   {
/*  56 */     int tableSize = Hashing.closedTableSize(n, 1.2D);
/*  57 */     this.mask = (tableSize - 1);
/*  58 */     ImmutableMapEntry<K, V>[] keyTable = createEntryArray(tableSize);
/*  59 */     ImmutableMapEntry<K, V>[] valueTable = createEntryArray(tableSize);
/*  60 */     ImmutableMapEntry<K, V>[] entries = createEntryArray(n);
/*  61 */     int hashCode = 0;
/*     */     
/*  63 */     for (int i = 0; i < n; i++)
/*     */     {
/*  65 */       ImmutableMapEntry.TerminalEntry<K, V> entry = entriesToAdd[i];
/*  66 */       K key = entry.getKey();
/*  67 */       V value = entry.getValue();
/*     */       
/*  69 */       int keyHash = key.hashCode();
/*  70 */       int valueHash = value.hashCode();
/*  71 */       int keyBucket = Hashing.smear(keyHash) & this.mask;
/*  72 */       int valueBucket = Hashing.smear(valueHash) & this.mask;
/*     */       
/*  74 */       ImmutableMapEntry<K, V> nextInKeyBucket = keyTable[keyBucket];
/*  75 */       for (ImmutableMapEntry<K, V> keyEntry = nextInKeyBucket; keyEntry != null; 
/*  76 */           keyEntry = keyEntry.getNextInKeyBucket()) {
/*  77 */         checkNoConflict(!key.equals(keyEntry.getKey()), "key", entry, keyEntry);
/*     */       }
/*  79 */       ImmutableMapEntry<K, V> nextInValueBucket = valueTable[valueBucket];
/*  80 */       for (ImmutableMapEntry<K, V> valueEntry = nextInValueBucket; valueEntry != null; 
/*  81 */           valueEntry = valueEntry.getNextInValueBucket()) {
/*  82 */         checkNoConflict(!value.equals(valueEntry.getValue()), "value", entry, valueEntry);
/*     */       }
/*  84 */       ImmutableMapEntry<K, V> newEntry = (nextInKeyBucket == null) && (nextInValueBucket == null) ? entry : new NonTerminalBiMapEntry(entry, nextInKeyBucket, nextInValueBucket);
/*     */       
/*     */ 
/*     */ 
/*  88 */       keyTable[keyBucket] = newEntry;
/*  89 */       valueTable[valueBucket] = newEntry;
/*  90 */       entries[i] = newEntry;
/*  91 */       hashCode += (keyHash ^ valueHash);
/*     */     }
/*     */     
/*  94 */     this.keyTable = keyTable;
/*  95 */     this.valueTable = valueTable;
/*  96 */     this.entries = entries;
/*  97 */     this.hashCode = hashCode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   RegularImmutableBiMap(Map.Entry<?, ?>[] entriesToAdd)
/*     */   {
/* 104 */     int n = entriesToAdd.length;
/* 105 */     int tableSize = Hashing.closedTableSize(n, 1.2D);
/* 106 */     this.mask = (tableSize - 1);
/* 107 */     ImmutableMapEntry<K, V>[] keyTable = createEntryArray(tableSize);
/* 108 */     ImmutableMapEntry<K, V>[] valueTable = createEntryArray(tableSize);
/* 109 */     ImmutableMapEntry<K, V>[] entries = createEntryArray(n);
/* 110 */     int hashCode = 0;
/*     */     
/* 112 */     for (int i = 0; i < n; i++)
/*     */     {
/* 114 */       Map.Entry<K, V> entry = entriesToAdd[i];
/* 115 */       K key = entry.getKey();
/* 116 */       V value = entry.getValue();
/* 117 */       CollectPreconditions.checkEntryNotNull(key, value);
/* 118 */       int keyHash = key.hashCode();
/* 119 */       int valueHash = value.hashCode();
/* 120 */       int keyBucket = Hashing.smear(keyHash) & this.mask;
/* 121 */       int valueBucket = Hashing.smear(valueHash) & this.mask;
/*     */       
/* 123 */       ImmutableMapEntry<K, V> nextInKeyBucket = keyTable[keyBucket];
/* 124 */       for (ImmutableMapEntry<K, V> keyEntry = nextInKeyBucket; keyEntry != null; 
/* 125 */           keyEntry = keyEntry.getNextInKeyBucket()) {
/* 126 */         checkNoConflict(!key.equals(keyEntry.getKey()), "key", entry, keyEntry);
/*     */       }
/* 128 */       ImmutableMapEntry<K, V> nextInValueBucket = valueTable[valueBucket];
/* 129 */       for (ImmutableMapEntry<K, V> valueEntry = nextInValueBucket; valueEntry != null; 
/* 130 */           valueEntry = valueEntry.getNextInValueBucket()) {
/* 131 */         checkNoConflict(!value.equals(valueEntry.getValue()), "value", entry, valueEntry);
/*     */       }
/* 133 */       ImmutableMapEntry<K, V> newEntry = (nextInKeyBucket == null) && (nextInValueBucket == null) ? new ImmutableMapEntry.TerminalEntry(key, value) : new NonTerminalBiMapEntry(key, value, nextInKeyBucket, nextInValueBucket);
/*     */       
/*     */ 
/*     */ 
/* 137 */       keyTable[keyBucket] = newEntry;
/* 138 */       valueTable[valueBucket] = newEntry;
/* 139 */       entries[i] = newEntry;
/* 140 */       hashCode += (keyHash ^ valueHash);
/*     */     }
/*     */     
/* 143 */     this.keyTable = keyTable;
/* 144 */     this.valueTable = valueTable;
/* 145 */     this.entries = entries;
/* 146 */     this.hashCode = hashCode;
/*     */   }
/*     */   
/*     */   private static final class NonTerminalBiMapEntry<K, V> extends ImmutableMapEntry<K, V> {
/*     */     @Nullable
/*     */     private final ImmutableMapEntry<K, V> nextInKeyBucket;
/*     */     @Nullable
/*     */     private final ImmutableMapEntry<K, V> nextInValueBucket;
/*     */     
/* 155 */     NonTerminalBiMapEntry(K key, V value, @Nullable ImmutableMapEntry<K, V> nextInKeyBucket, @Nullable ImmutableMapEntry<K, V> nextInValueBucket) { super(value);
/* 156 */       this.nextInKeyBucket = nextInKeyBucket;
/* 157 */       this.nextInValueBucket = nextInValueBucket;
/*     */     }
/*     */     
/*     */ 
/*     */     NonTerminalBiMapEntry(ImmutableMapEntry<K, V> contents, @Nullable ImmutableMapEntry<K, V> nextInKeyBucket, @Nullable ImmutableMapEntry<K, V> nextInValueBucket)
/*     */     {
/* 163 */       super();
/* 164 */       this.nextInKeyBucket = nextInKeyBucket;
/* 165 */       this.nextInValueBucket = nextInValueBucket;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     ImmutableMapEntry<K, V> getNextInKeyBucket()
/*     */     {
/* 171 */       return this.nextInKeyBucket;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     ImmutableMapEntry<K, V> getNextInValueBucket()
/*     */     {
/* 177 */       return this.nextInValueBucket;
/*     */     }
/*     */   }
/*     */   
/*     */   private static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(int length)
/*     */   {
/* 183 */     return new ImmutableMapEntry[length];
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public V get(@Nullable Object key)
/*     */   {
/* 189 */     if (key == null) {
/* 190 */       return null;
/*     */     }
/* 192 */     int bucket = Hashing.smear(key.hashCode()) & this.mask;
/* 193 */     for (ImmutableMapEntry<K, V> entry = this.keyTable[bucket]; entry != null; 
/* 194 */         entry = entry.getNextInKeyBucket()) {
/* 195 */       if (key.equals(entry.getKey())) {
/* 196 */         return (V)entry.getValue();
/*     */       }
/*     */     }
/* 199 */     return null;
/*     */   }
/*     */   
/*     */   ImmutableSet<Map.Entry<K, V>> createEntrySet()
/*     */   {
/* 204 */     new ImmutableMapEntrySet()
/*     */     {
/*     */       ImmutableMap<K, V> map() {
/* 207 */         return RegularImmutableBiMap.this;
/*     */       }
/*     */       
/*     */       public UnmodifiableIterator<Map.Entry<K, V>> iterator()
/*     */       {
/* 212 */         return asList().iterator();
/*     */       }
/*     */       
/*     */       ImmutableList<Map.Entry<K, V>> createAsList()
/*     */       {
/* 217 */         return new RegularImmutableAsList(this, RegularImmutableBiMap.this.entries);
/*     */       }
/*     */       
/*     */       boolean isHashCodeFast()
/*     */       {
/* 222 */         return true;
/*     */       }
/*     */       
/*     */       public int hashCode()
/*     */       {
/* 227 */         return RegularImmutableBiMap.this.hashCode;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   boolean isPartialView()
/*     */   {
/* 234 */     return false;
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/* 239 */     return this.entries.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ImmutableBiMap<V, K> inverse()
/*     */   {
/* 246 */     ImmutableBiMap<V, K> result = this.inverse;
/* 247 */     return result == null ? (this.inverse = new Inverse(null)) : result;
/*     */   }
/*     */   
/*     */   private final class Inverse extends ImmutableBiMap<V, K> {
/*     */     private Inverse() {}
/*     */     
/*     */     public int size() {
/* 254 */       return inverse().size();
/*     */     }
/*     */     
/*     */     public ImmutableBiMap<K, V> inverse()
/*     */     {
/* 259 */       return RegularImmutableBiMap.this;
/*     */     }
/*     */     
/*     */     public K get(@Nullable Object value)
/*     */     {
/* 264 */       if (value == null) {
/* 265 */         return null;
/*     */       }
/* 267 */       int bucket = Hashing.smear(value.hashCode()) & RegularImmutableBiMap.this.mask;
/* 268 */       for (ImmutableMapEntry<K, V> entry = RegularImmutableBiMap.this.valueTable[bucket]; entry != null; 
/* 269 */           entry = entry.getNextInValueBucket()) {
/* 270 */         if (value.equals(entry.getValue())) {
/* 271 */           return (K)entry.getKey();
/*     */         }
/*     */       }
/* 274 */       return null;
/*     */     }
/*     */     
/*     */     ImmutableSet<Map.Entry<V, K>> createEntrySet()
/*     */     {
/* 279 */       return new InverseEntrySet();
/*     */     }
/*     */     
/*     */     final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
/*     */       InverseEntrySet() {}
/*     */       
/* 285 */       ImmutableMap<V, K> map() { return Inverse.this; }
/*     */       
/*     */ 
/*     */       boolean isHashCodeFast()
/*     */       {
/* 290 */         return true;
/*     */       }
/*     */       
/*     */       public int hashCode()
/*     */       {
/* 295 */         return RegularImmutableBiMap.this.hashCode;
/*     */       }
/*     */       
/*     */       public UnmodifiableIterator<Map.Entry<V, K>> iterator()
/*     */       {
/* 300 */         return asList().iterator();
/*     */       }
/*     */       
/*     */       ImmutableList<Map.Entry<V, K>> createAsList()
/*     */       {
/* 305 */         new ImmutableAsList()
/*     */         {
/*     */           public Map.Entry<V, K> get(int index) {
/* 308 */             Map.Entry<K, V> entry = RegularImmutableBiMap.this.entries[index];
/* 309 */             return Maps.immutableEntry(entry.getValue(), entry.getKey());
/*     */           }
/*     */           
/*     */           ImmutableCollection<Map.Entry<V, K>> delegateCollection()
/*     */           {
/* 314 */             return InverseEntrySet.this;
/*     */           }
/*     */         };
/*     */       }
/*     */     }
/*     */     
/*     */     boolean isPartialView()
/*     */     {
/* 322 */       return false;
/*     */     }
/*     */     
/*     */     Object writeReplace()
/*     */     {
/* 327 */       return new InverseSerializedForm(RegularImmutableBiMap.this);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class InverseSerializedForm<K, V> implements Serializable {
/*     */     private final ImmutableBiMap<K, V> forward;
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/* 335 */     InverseSerializedForm(ImmutableBiMap<K, V> forward) { this.forward = forward; }
/*     */     
/*     */     Object readResolve()
/*     */     {
/* 339 */       return this.forward.inverse();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\RegularImmutableBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */