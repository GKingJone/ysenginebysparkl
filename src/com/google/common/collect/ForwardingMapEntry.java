/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
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
/* 126 */     return getKey() + "=" + getValue();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ForwardingMapEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */