/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ import java.util.NoSuchElementException;
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
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ContiguousSet<C extends Comparable>
/*     */   extends ImmutableSortedSet<C>
/*     */ {
/*     */   final DiscreteDomain<C> domain;
/*     */   
/*     */   public static <C extends Comparable> ContiguousSet<C> create(Range<C> range, DiscreteDomain<C> domain)
/*     */   {
/*  54 */     Preconditions.checkNotNull(range);
/*  55 */     Preconditions.checkNotNull(domain);
/*  56 */     Range<C> effectiveRange = range;
/*     */     try {
/*  58 */       if (!range.hasLowerBound()) {
/*  59 */         effectiveRange = effectiveRange.intersection(Range.atLeast(domain.minValue()));
/*     */       }
/*  61 */       if (!range.hasUpperBound()) {
/*  62 */         effectiveRange = effectiveRange.intersection(Range.atMost(domain.maxValue()));
/*     */       }
/*     */     } catch (NoSuchElementException e) {
/*  65 */       throw new IllegalArgumentException(e);
/*     */     }
/*     */     
/*     */ 
/*  69 */     boolean empty = (effectiveRange.isEmpty()) || (Range.compareOrThrow(range.lowerBound.leastValueAbove(domain), range.upperBound.greatestValueBelow(domain)) > 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  74 */     return empty ? new EmptyContiguousSet(domain) : new RegularContiguousSet(effectiveRange, domain);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   ContiguousSet(DiscreteDomain<C> domain)
/*     */   {
/*  82 */     super(Ordering.natural());
/*  83 */     this.domain = domain;
/*     */   }
/*     */   
/*     */   public ContiguousSet<C> headSet(C toElement) {
/*  87 */     return headSetImpl((Comparable)Preconditions.checkNotNull(toElement), false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ContiguousSet<C> headSet(C toElement, boolean inclusive)
/*     */   {
/*  95 */     return headSetImpl((Comparable)Preconditions.checkNotNull(toElement), inclusive);
/*     */   }
/*     */   
/*     */   public ContiguousSet<C> subSet(C fromElement, C toElement) {
/*  99 */     Preconditions.checkNotNull(fromElement);
/* 100 */     Preconditions.checkNotNull(toElement);
/* 101 */     Preconditions.checkArgument(comparator().compare(fromElement, toElement) <= 0);
/* 102 */     return subSetImpl(fromElement, true, toElement, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ContiguousSet<C> subSet(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive)
/*     */   {
/* 111 */     Preconditions.checkNotNull(fromElement);
/* 112 */     Preconditions.checkNotNull(toElement);
/* 113 */     Preconditions.checkArgument(comparator().compare(fromElement, toElement) <= 0);
/* 114 */     return subSetImpl(fromElement, fromInclusive, toElement, toInclusive);
/*     */   }
/*     */   
/*     */   public ContiguousSet<C> tailSet(C fromElement) {
/* 118 */     return tailSetImpl((Comparable)Preconditions.checkNotNull(fromElement), true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ContiguousSet<C> tailSet(C fromElement, boolean inclusive)
/*     */   {
/* 126 */     return tailSetImpl((Comparable)Preconditions.checkNotNull(fromElement), inclusive);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract ContiguousSet<C> headSetImpl(C paramC, boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract ContiguousSet<C> subSetImpl(C paramC1, boolean paramBoolean1, C paramC2, boolean paramBoolean2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract ContiguousSet<C> tailSetImpl(C paramC, boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ContiguousSet<C> intersection(ContiguousSet<C> paramContiguousSet);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Range<C> range();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Range<C> range(BoundType paramBoundType1, BoundType paramBoundType2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 170 */     return range().toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet.Builder<E> builder()
/*     */   {
/* 182 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ContiguousSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */