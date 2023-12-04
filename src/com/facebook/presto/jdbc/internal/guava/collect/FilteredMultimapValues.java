/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Predicates;
/*    */ import java.util.AbstractCollection;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
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
/*    */ @GwtCompatible
/*    */ final class FilteredMultimapValues<K, V>
/*    */   extends AbstractCollection<V>
/*    */ {
/*    */   private final FilteredMultimap<K, V> multimap;
/*    */   
/*    */   FilteredMultimapValues(FilteredMultimap<K, V> multimap)
/*    */   {
/* 42 */     this.multimap = ((FilteredMultimap)Preconditions.checkNotNull(multimap));
/*    */   }
/*    */   
/*    */   public Iterator<V> iterator()
/*    */   {
/* 47 */     return Maps.valueIterator(this.multimap.entries().iterator());
/*    */   }
/*    */   
/*    */   public boolean contains(@Nullable Object o)
/*    */   {
/* 52 */     return this.multimap.containsValue(o);
/*    */   }
/*    */   
/*    */   public int size()
/*    */   {
/* 57 */     return this.multimap.size();
/*    */   }
/*    */   
/*    */   public boolean remove(@Nullable Object o)
/*    */   {
/* 62 */     Predicate<? super Map.Entry<K, V>> entryPredicate = this.multimap.entryPredicate();
/* 63 */     Iterator<Map.Entry<K, V>> unfilteredItr = this.multimap.unfiltered().entries().iterator();
/* 64 */     while (unfilteredItr.hasNext()) {
/* 65 */       Map.Entry<K, V> entry = (Map.Entry)unfilteredItr.next();
/* 66 */       if ((entryPredicate.apply(entry)) && (Objects.equal(entry.getValue(), o))) {
/* 67 */         unfilteredItr.remove();
/* 68 */         return true;
/*    */       }
/*    */     }
/* 71 */     return false;
/*    */   }
/*    */   
/*    */   public boolean removeAll(Collection<?> c)
/*    */   {
/* 76 */     return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.in(c))));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean retainAll(Collection<?> c)
/*    */   {
/* 84 */     return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(c)))));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void clear()
/*    */   {
/* 92 */     this.multimap.clear();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\FilteredMultimapValues.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */