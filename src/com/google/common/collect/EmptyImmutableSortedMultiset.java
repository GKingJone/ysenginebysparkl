/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
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
/*     */ final class EmptyImmutableSortedMultiset<E>
/*     */   extends ImmutableSortedMultiset<E>
/*     */ {
/*     */   private final ImmutableSortedSet<E> elementSet;
/*     */   
/*     */   EmptyImmutableSortedMultiset(Comparator<? super E> comparator)
/*     */   {
/*  34 */     this.elementSet = ImmutableSortedSet.emptySet(comparator);
/*     */   }
/*     */   
/*     */   public Entry<E> firstEntry()
/*     */   {
/*  39 */     return null;
/*     */   }
/*     */   
/*     */   public Entry<E> lastEntry()
/*     */   {
/*  44 */     return null;
/*     */   }
/*     */   
/*     */   public int count(@Nullable Object element)
/*     */   {
/*  49 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean containsAll(Collection<?> targets)
/*     */   {
/*  54 */     return targets.isEmpty();
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  59 */     return 0;
/*     */   }
/*     */   
/*     */   public ImmutableSortedSet<E> elementSet()
/*     */   {
/*  64 */     return this.elementSet;
/*     */   }
/*     */   
/*     */   Entry<E> getEntry(int index)
/*     */   {
/*  69 */     throw new AssertionError("should never be called");
/*     */   }
/*     */   
/*     */   public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */   {
/*  74 */     Preconditions.checkNotNull(upperBound);
/*  75 */     Preconditions.checkNotNull(boundType);
/*  76 */     return this;
/*     */   }
/*     */   
/*     */   public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */   {
/*  81 */     Preconditions.checkNotNull(lowerBound);
/*  82 */     Preconditions.checkNotNull(boundType);
/*  83 */     return this;
/*     */   }
/*     */   
/*     */   public UnmodifiableIterator<E> iterator()
/*     */   {
/*  88 */     return Iterators.emptyIterator();
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/*  93 */     if ((object instanceof Multiset)) {
/*  94 */       Multiset<?> other = (Multiset)object;
/*  95 */       return other.isEmpty();
/*     */     }
/*  97 */     return false;
/*     */   }
/*     */   
/*     */   boolean isPartialView()
/*     */   {
/* 102 */     return false;
/*     */   }
/*     */   
/*     */   int copyIntoArray(Object[] dst, int offset)
/*     */   {
/* 107 */     return offset;
/*     */   }
/*     */   
/*     */   public ImmutableList<E> asList()
/*     */   {
/* 112 */     return ImmutableList.of();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\EmptyImmutableSortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */