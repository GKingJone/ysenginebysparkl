/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
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
/*     */ class DescendingImmutableSortedSet<E>
/*     */   extends ImmutableSortedSet<E>
/*     */ {
/*     */   private final ImmutableSortedSet<E> forward;
/*     */   
/*     */   DescendingImmutableSortedSet(ImmutableSortedSet<E> forward)
/*     */   {
/*  32 */     super(Ordering.from(forward.comparator()).reverse());
/*  33 */     this.forward = forward;
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  38 */     return this.forward.size();
/*     */   }
/*     */   
/*     */   public UnmodifiableIterator<E> iterator()
/*     */   {
/*  43 */     return this.forward.descendingIterator();
/*     */   }
/*     */   
/*     */   ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive)
/*     */   {
/*  48 */     return this.forward.tailSet(toElement, inclusive).descendingSet();
/*     */   }
/*     */   
/*     */ 
/*     */   ImmutableSortedSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/*  54 */     return this.forward.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
/*     */   }
/*     */   
/*     */   ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive)
/*     */   {
/*  59 */     return this.forward.headSet(fromElement, inclusive).descendingSet();
/*     */   }
/*     */   
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> descendingSet()
/*     */   {
/*  65 */     return this.forward;
/*     */   }
/*     */   
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public UnmodifiableIterator<E> descendingIterator()
/*     */   {
/*  71 */     return this.forward.iterator();
/*     */   }
/*     */   
/*     */   @GwtIncompatible("NavigableSet")
/*     */   ImmutableSortedSet<E> createDescendingSet()
/*     */   {
/*  77 */     throw new AssertionError("should never be called");
/*     */   }
/*     */   
/*     */   public E lower(E element)
/*     */   {
/*  82 */     return (E)this.forward.higher(element);
/*     */   }
/*     */   
/*     */   public E floor(E element)
/*     */   {
/*  87 */     return (E)this.forward.ceiling(element);
/*     */   }
/*     */   
/*     */   public E ceiling(E element)
/*     */   {
/*  92 */     return (E)this.forward.floor(element);
/*     */   }
/*     */   
/*     */   public E higher(E element)
/*     */   {
/*  97 */     return (E)this.forward.lower(element);
/*     */   }
/*     */   
/*     */   int indexOf(@Nullable Object target)
/*     */   {
/* 102 */     int index = this.forward.indexOf(target);
/* 103 */     if (index == -1) {
/* 104 */       return index;
/*     */     }
/* 106 */     return size() - 1 - index;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/* 112 */     return this.forward.isPartialView();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\DescendingImmutableSortedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */