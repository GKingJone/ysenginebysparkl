/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*    */ import java.io.Serializable;
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
/*    */ @GwtCompatible(emulated=true)
/*    */ abstract class ImmutableMapEntrySet<K, V>
/*    */   extends ImmutableSet<Map.Entry<K, V>>
/*    */ {
/*    */   abstract ImmutableMap<K, V> map();
/*    */   
/*    */   public int size()
/*    */   {
/* 41 */     return map().size();
/*    */   }
/*    */   
/*    */   public boolean contains(@Nullable Object object)
/*    */   {
/* 46 */     if ((object instanceof Map.Entry)) {
/* 47 */       Map.Entry<?, ?> entry = (Map.Entry)object;
/* 48 */       V value = map().get(entry.getKey());
/* 49 */       return (value != null) && (value.equals(entry.getValue()));
/*    */     }
/* 51 */     return false;
/*    */   }
/*    */   
/*    */   boolean isPartialView()
/*    */   {
/* 56 */     return map().isPartialView();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("serialization")
/* 62 */   Object writeReplace() { return new EntrySetSerializedForm(map()); }
/*    */   
/*    */   @GwtIncompatible("serialization")
/*    */   private static class EntrySetSerializedForm<K, V> implements Serializable {
/*    */     final ImmutableMap<K, V> map;
/*    */     private static final long serialVersionUID = 0L;
/*    */     
/* 69 */     EntrySetSerializedForm(ImmutableMap<K, V> map) { this.map = map; }
/*    */     
/*    */     Object readResolve() {
/* 72 */       return this.map.entrySet();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ImmutableMapEntrySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */