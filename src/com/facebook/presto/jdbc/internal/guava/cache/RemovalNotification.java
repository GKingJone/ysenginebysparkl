/*    */ package com.facebook.presto.jdbc.internal.guava.cache;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.util.Map.Entry;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class RemovalNotification<K, V>
/*    */   implements Map.Entry<K, V>
/*    */ {
/*    */   @Nullable
/*    */   private final K key;
/*    */   @Nullable
/*    */   private final V value;
/*    */   private final RemovalCause cause;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   RemovalNotification(@Nullable K key, @Nullable V value, RemovalCause cause)
/*    */   {
/* 48 */     this.key = key;
/* 49 */     this.value = value;
/* 50 */     this.cause = ((RemovalCause)Preconditions.checkNotNull(cause));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public RemovalCause getCause()
/*    */   {
/* 57 */     return this.cause;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean wasEvicted()
/*    */   {
/* 65 */     return this.cause.wasEvicted();
/*    */   }
/*    */   
/*    */   @Nullable
/* 69 */   public K getKey() { return (K)this.key; }
/*    */   
/*    */   @Nullable
/*    */   public V getValue() {
/* 73 */     return (V)this.value;
/*    */   }
/*    */   
/*    */   public final V setValue(V value) {
/* 77 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 81 */     if ((object instanceof Map.Entry)) {
/* 82 */       Map.Entry<?, ?> that = (Map.Entry)object;
/* 83 */       return (Objects.equal(getKey(), that.getKey())) && (Objects.equal(getValue(), that.getValue()));
/*    */     }
/*    */     
/* 86 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 90 */     K k = getKey();
/* 91 */     V v = getValue();
/* 92 */     return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 99 */     String str1 = String.valueOf(String.valueOf(getKey()));String str2 = String.valueOf(String.valueOf(getValue()));return 1 + str1.length() + str2.length() + str1 + "=" + str2;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\RemovalNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */