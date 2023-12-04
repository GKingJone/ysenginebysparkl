/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingMapEntry<K, V>
/*     */   extends ForwardingObject
/*     */   implements Map.Entry<K, V>
/*     */ {
/*     */   protected abstract Map.Entry<K, V> delegate();
/*     */   
/*     */   public K getKey()
/*     */   {
/*  66 */     return (K)delegate().getKey();
/*     */   }
/*     */   
/*     */   public V getValue()
/*     */   {
/*  71 */     return (V)delegate().getValue();
/*     */   }
/*     */   
/*     */   public V setValue(V value)
/*     */   {
/*  76 */     return (V)delegate().setValue(value);
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object object) {
/*  80 */     return delegate().equals(object);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*  84 */     return delegate().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean standardEquals(@Nullable Object object)
/*     */   {
/*  96 */     if ((object instanceof Map.Entry)) {
/*  97 */       Map.Entry<?, ?> that = (Map.Entry)object;
/*  98 */       return (Objects.equal(getKey(), that.getKey())) && (Objects.equal(getValue(), that.getValue()));
/*     */     }
/*     */     
/* 101 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int standardHashCode()
/*     */   {
/* 112 */     K k = getKey();
/* 113 */     V v = getValue();
/* 114 */     return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   protected String standardToString()
/*     */   {
/* 126 */     String str1 = String.valueOf(String.valueOf(getKey()));String str2 = String.valueOf(String.valueOf(getValue()));return 1 + str1.length() + str2.length() + str1 + "=" + str2;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingMapEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */