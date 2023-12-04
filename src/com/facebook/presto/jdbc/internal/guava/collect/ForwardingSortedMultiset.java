/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.Set;
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
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ForwardingSortedMultiset<E>
/*     */   extends ForwardingMultiset<E>
/*     */   implements SortedMultiset<E>
/*     */ {
/*     */   protected abstract SortedMultiset<E> delegate();
/*     */   
/*     */   public NavigableSet<E> elementSet()
/*     */   {
/*  54 */     return (NavigableSet)super.elementSet();
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
/*     */   protected class StandardElementSet
/*     */     extends SortedMultisets.NavigableElementSet<E>
/*     */   {
/*     */     public StandardElementSet()
/*     */     {
/*  71 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   public Comparator<? super E> comparator()
/*     */   {
/*  77 */     return delegate().comparator();
/*     */   }
/*     */   
/*     */   public SortedMultiset<E> descendingMultiset()
/*     */   {
/*  82 */     return delegate().descendingMultiset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract class StandardDescendingMultiset
/*     */     extends DescendingMultiset<E>
/*     */   {
/*     */     public StandardDescendingMultiset() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     SortedMultiset<E> forwardMultiset()
/*     */     {
/* 102 */       return ForwardingSortedMultiset.this;
/*     */     }
/*     */   }
/*     */   
/*     */   public Entry<E> firstEntry()
/*     */   {
/* 108 */     return delegate().firstEntry();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Entry<E> standardFirstEntry()
/*     */   {
/* 118 */     Iterator<Entry<E>> entryIterator = entrySet().iterator();
/* 119 */     if (!entryIterator.hasNext()) {
/* 120 */       return null;
/*     */     }
/* 122 */     Entry<E> entry = (Entry)entryIterator.next();
/* 123 */     return Multisets.immutableEntry(entry.getElement(), entry.getCount());
/*     */   }
/*     */   
/*     */   public Entry<E> lastEntry()
/*     */   {
/* 128 */     return delegate().lastEntry();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Entry<E> standardLastEntry()
/*     */   {
/* 139 */     Iterator<Entry<E>> entryIterator = descendingMultiset().entrySet().iterator();
/*     */     
/*     */ 
/* 142 */     if (!entryIterator.hasNext()) {
/* 143 */       return null;
/*     */     }
/* 145 */     Entry<E> entry = (Entry)entryIterator.next();
/* 146 */     return Multisets.immutableEntry(entry.getElement(), entry.getCount());
/*     */   }
/*     */   
/*     */   public Entry<E> pollFirstEntry()
/*     */   {
/* 151 */     return delegate().pollFirstEntry();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Entry<E> standardPollFirstEntry()
/*     */   {
/* 161 */     Iterator<Entry<E>> entryIterator = entrySet().iterator();
/* 162 */     if (!entryIterator.hasNext()) {
/* 163 */       return null;
/*     */     }
/* 165 */     Entry<E> entry = (Entry)entryIterator.next();
/* 166 */     entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
/* 167 */     entryIterator.remove();
/* 168 */     return entry;
/*     */   }
/*     */   
/*     */   public Entry<E> pollLastEntry()
/*     */   {
/* 173 */     return delegate().pollLastEntry();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Entry<E> standardPollLastEntry()
/*     */   {
/* 184 */     Iterator<Entry<E>> entryIterator = descendingMultiset().entrySet().iterator();
/*     */     
/*     */ 
/* 187 */     if (!entryIterator.hasNext()) {
/* 188 */       return null;
/*     */     }
/* 190 */     Entry<E> entry = (Entry)entryIterator.next();
/* 191 */     entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
/* 192 */     entryIterator.remove();
/* 193 */     return entry;
/*     */   }
/*     */   
/*     */   public SortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */   {
/* 198 */     return delegate().headMultiset(upperBound, boundType);
/*     */   }
/*     */   
/*     */ 
/*     */   public SortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType)
/*     */   {
/* 204 */     return delegate().subMultiset(lowerBound, lowerBoundType, upperBound, upperBoundType);
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
/*     */   protected SortedMultiset<E> standardSubMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType)
/*     */   {
/* 217 */     return tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
/*     */   }
/*     */   
/*     */   public SortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */   {
/* 222 */     return delegate().tailMultiset(lowerBound, boundType);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingSortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */