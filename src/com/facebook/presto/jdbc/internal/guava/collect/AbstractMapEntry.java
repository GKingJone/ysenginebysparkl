/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
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
/*    */ 
/*    */ @GwtCompatible
/*    */ abstract class AbstractMapEntry<K, V>
/*    */   implements Map.Entry<K, V>
/*    */ {
/*    */   public abstract K getKey();
/*    */   
/*    */   public abstract V getValue();
/*    */   
/*    */   public V setValue(V value)
/*    */   {
/* 43 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 47 */     if ((object instanceof Map.Entry)) {
/* 48 */       Map.Entry<?, ?> that = (Map.Entry)object;
/* 49 */       return (Objects.equal(getKey(), that.getKey())) && (Objects.equal(getValue(), that.getValue()));
/*    */     }
/*    */     
/* 52 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 56 */     K k = getKey();
/* 57 */     V v = getValue();
/* 58 */     return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 65 */     String str1 = String.valueOf(String.valueOf(getKey()));String str2 = String.valueOf(String.valueOf(getValue()));return 1 + str1.length() + str2.length() + str1 + "=" + str2;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\AbstractMapEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */