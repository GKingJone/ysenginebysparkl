/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
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
/*     */ abstract class AbstractMultiset<E>
/*     */   extends AbstractCollection<E>
/*     */   implements Multiset<E>
/*     */ {
/*     */   private transient Set<E> elementSet;
/*     */   private transient Set<Entry<E>> entrySet;
/*     */   
/*     */   public int size()
/*     */   {
/*  52 */     return Multisets.sizeImpl(this);
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  56 */     return entrySet().isEmpty();
/*     */   }
/*     */   
/*     */   public boolean contains(@Nullable Object element) {
/*  60 */     return count(element) > 0;
/*     */   }
/*     */   
/*     */   public Iterator<E> iterator() {
/*  64 */     return Multisets.iteratorImpl(this);
/*     */   }
/*     */   
/*     */   public int count(@Nullable Object element)
/*     */   {
/*  69 */     for (Entry<E> entry : entrySet()) {
/*  70 */       if (Objects.equal(entry.getElement(), element)) {
/*  71 */         return entry.getCount();
/*     */       }
/*     */     }
/*  74 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean add(@Nullable E element)
/*     */   {
/*  80 */     add(element, 1);
/*  81 */     return true;
/*     */   }
/*     */   
/*     */   public int add(@Nullable E element, int occurrences)
/*     */   {
/*  86 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public boolean remove(@Nullable Object element) {
/*  90 */     return remove(element, 1) > 0;
/*     */   }
/*     */   
/*     */   public int remove(@Nullable Object element, int occurrences)
/*     */   {
/*  95 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public int setCount(@Nullable E element, int count)
/*     */   {
/* 100 */     return Multisets.setCountImpl(this, element, count);
/*     */   }
/*     */   
/*     */   public boolean setCount(@Nullable E element, int oldCount, int newCount)
/*     */   {
/* 105 */     return Multisets.setCountImpl(this, element, oldCount, newCount);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean addAll(Collection<? extends E> elementsToAdd)
/*     */   {
/* 117 */     return Multisets.addAllImpl(this, elementsToAdd);
/*     */   }
/*     */   
/*     */   public boolean removeAll(Collection<?> elementsToRemove) {
/* 121 */     return Multisets.removeAllImpl(this, elementsToRemove);
/*     */   }
/*     */   
/*     */   public boolean retainAll(Collection<?> elementsToRetain) {
/* 125 */     return Multisets.retainAllImpl(this, elementsToRetain);
/*     */   }
/*     */   
/*     */   public void clear() {
/* 129 */     Iterators.clear(entryIterator());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<E> elementSet()
/*     */   {
/* 138 */     Set<E> result = this.elementSet;
/* 139 */     if (result == null) {
/* 140 */       this.elementSet = (result = createElementSet());
/*     */     }
/* 142 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 150 */   Set<E> createElementSet() { return new ElementSet(); }
/*     */   
/*     */   abstract Iterator<Entry<E>> entryIterator();
/*     */   
/*     */   class ElementSet extends Multisets.ElementSet<E> { ElementSet() {}
/*     */     
/* 156 */     Multiset<E> multiset() { return AbstractMultiset.this; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   abstract int distinctElements();
/*     */   
/*     */ 
/*     */ 
/*     */   public Set<Entry<E>> entrySet()
/*     */   {
/* 167 */     Set<Entry<E>> result = this.entrySet;
/* 168 */     return result == null ? (this.entrySet = createEntrySet()) : result;
/*     */   }
/*     */   
/*     */   class EntrySet extends Multisets.EntrySet<E> { EntrySet() {}
/*     */     
/* 173 */     Multiset<E> multiset() { return AbstractMultiset.this; }
/*     */     
/*     */     public Iterator<Entry<E>> iterator()
/*     */     {
/* 177 */       return AbstractMultiset.this.entryIterator();
/*     */     }
/*     */     
/*     */     public int size() {
/* 181 */       return AbstractMultiset.this.distinctElements();
/*     */     }
/*     */   }
/*     */   
/*     */   Set<Entry<E>> createEntrySet() {
/* 186 */     return new EntrySet();
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
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 199 */     return Multisets.equalsImpl(this, object);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 209 */     return entrySet().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 219 */     return entrySet().toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\AbstractMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */