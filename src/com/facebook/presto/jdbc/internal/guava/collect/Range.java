/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.SortedSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Range<C extends Comparable>
/*     */   implements Predicate<C>, Serializable
/*     */ {
/* 117 */   private static final Function<Range, Cut> LOWER_BOUND_FN = new Function()
/*     */   {
/*     */     public Cut apply(Range range) {
/* 120 */       return range.lowerBound;
/*     */     }
/*     */   };
/*     */   
/*     */   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> lowerBoundFn()
/*     */   {
/* 126 */     return LOWER_BOUND_FN;
/*     */   }
/*     */   
/* 129 */   private static final Function<Range, Cut> UPPER_BOUND_FN = new Function()
/*     */   {
/*     */     public Cut apply(Range range) {
/* 132 */       return range.upperBound;
/*     */     }
/*     */   };
/*     */   
/*     */   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> upperBoundFn()
/*     */   {
/* 138 */     return UPPER_BOUND_FN;
/*     */   }
/*     */   
/* 141 */   static final Ordering<Range<?>> RANGE_LEX_ORDERING = new Ordering()
/*     */   {
/*     */     public int compare(Range<?> left, Range<?> right) {
/* 144 */       return ComparisonChain.start().compare(left.lowerBound, right.lowerBound).compare(left.upperBound, right.upperBound).result();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static <C extends Comparable<?>> Range<C> create(Cut<C> lowerBound, Cut<C> upperBound)
/*     */   {
/* 153 */     return new Range(lowerBound, upperBound);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> open(C lower, C upper)
/*     */   {
/* 165 */     return create(Cut.aboveValue(lower), Cut.belowValue(upper));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> closed(C lower, C upper)
/*     */   {
/* 177 */     return create(Cut.belowValue(lower), Cut.aboveValue(upper));
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
/*     */   public static <C extends Comparable<?>> Range<C> closedOpen(C lower, C upper)
/*     */   {
/* 190 */     return create(Cut.belowValue(lower), Cut.belowValue(upper));
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
/*     */   public static <C extends Comparable<?>> Range<C> openClosed(C lower, C upper)
/*     */   {
/* 203 */     return create(Cut.aboveValue(lower), Cut.aboveValue(upper));
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
/*     */   public static <C extends Comparable<?>> Range<C> range(C lower, BoundType lowerType, C upper, BoundType upperType)
/*     */   {
/* 217 */     Preconditions.checkNotNull(lowerType);
/* 218 */     Preconditions.checkNotNull(upperType);
/*     */     
/* 220 */     Cut<C> lowerBound = lowerType == BoundType.OPEN ? Cut.aboveValue(lower) : Cut.belowValue(lower);
/*     */     
/*     */ 
/* 223 */     Cut<C> upperBound = upperType == BoundType.OPEN ? Cut.belowValue(upper) : Cut.aboveValue(upper);
/*     */     
/*     */ 
/* 226 */     return create(lowerBound, upperBound);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> lessThan(C endpoint)
/*     */   {
/* 236 */     return create(Cut.belowAll(), Cut.belowValue(endpoint));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> atMost(C endpoint)
/*     */   {
/* 246 */     return create(Cut.belowAll(), Cut.aboveValue(endpoint));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> upTo(C endpoint, BoundType boundType)
/*     */   {
/* 257 */     switch (boundType) {
/*     */     case OPEN: 
/* 259 */       return lessThan(endpoint);
/*     */     case CLOSED: 
/* 261 */       return atMost(endpoint);
/*     */     }
/* 263 */     throw new AssertionError();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> greaterThan(C endpoint)
/*     */   {
/* 274 */     return create(Cut.aboveValue(endpoint), Cut.aboveAll());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> atLeast(C endpoint)
/*     */   {
/* 284 */     return create(Cut.belowValue(endpoint), Cut.aboveAll());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> downTo(C endpoint, BoundType boundType)
/*     */   {
/* 295 */     switch (boundType) {
/*     */     case OPEN: 
/* 297 */       return greaterThan(endpoint);
/*     */     case CLOSED: 
/* 299 */       return atLeast(endpoint);
/*     */     }
/* 301 */     throw new AssertionError();
/*     */   }
/*     */   
/*     */ 
/* 305 */   private static final Range<Comparable> ALL = new Range(Cut.belowAll(), Cut.aboveAll());
/*     */   
/*     */   final Cut<C> lowerBound;
/*     */   
/*     */   final Cut<C> upperBound;
/*     */   
/*     */   private static final long serialVersionUID = 0L;
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> all()
/*     */   {
/* 315 */     return ALL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> singleton(C value)
/*     */   {
/* 326 */     return closed(value, value);
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
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> values)
/*     */   {
/* 342 */     Preconditions.checkNotNull(values);
/* 343 */     if ((values instanceof ContiguousSet)) {
/* 344 */       return ((ContiguousSet)values).range();
/*     */     }
/* 346 */     Iterator<C> valueIterator = values.iterator();
/* 347 */     C min = (Comparable)Preconditions.checkNotNull(valueIterator.next());
/* 348 */     C max = min;
/* 349 */     while (valueIterator.hasNext()) {
/* 350 */       C value = (Comparable)Preconditions.checkNotNull(valueIterator.next());
/* 351 */       min = (Comparable)Ordering.natural().min(min, value);
/* 352 */       max = (Comparable)Ordering.natural().max(max, value);
/*     */     }
/* 354 */     return closed(min, max);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private Range(Cut<C> lowerBound, Cut<C> upperBound)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 176	java/lang/Object:<init>	()V
/*     */     //   4: aload_1
/*     */     //   5: aload_2
/*     */     //   6: invokevirtual 180	com/facebook/presto/jdbc/internal/guava/collect/Cut:compareTo	(Lcom/facebook/presto/jdbc/internal/guava/collect/Cut;)I
/*     */     //   9: ifgt +17 -> 26
/*     */     //   12: aload_1
/*     */     //   13: invokestatic 118	com/facebook/presto/jdbc/internal/guava/collect/Cut:aboveAll	()Lcom/facebook/presto/jdbc/internal/guava/collect/Cut;
/*     */     //   16: if_acmpeq +10 -> 26
/*     */     //   19: aload_2
/*     */     //   20: invokestatic 92	com/facebook/presto/jdbc/internal/guava/collect/Cut:belowAll	()Lcom/facebook/presto/jdbc/internal/guava/collect/Cut;
/*     */     //   23: if_acmpne +43 -> 66
/*     */     //   26: new 182	java/lang/IllegalArgumentException
/*     */     //   29: dup
/*     */     //   30: ldc -72
/*     */     //   32: aload_1
/*     */     //   33: aload_2
/*     */     //   34: invokestatic 188	com/facebook/presto/jdbc/internal/guava/collect/Range:toString	(Lcom/facebook/presto/jdbc/internal/guava/collect/Cut;Lcom/facebook/presto/jdbc/internal/guava/collect/Cut;)Ljava/lang/String;
/*     */     //   37: invokestatic 194	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   40: dup
/*     */     //   41: invokevirtual 197	java/lang/String:length	()I
/*     */     //   44: ifeq +9 -> 53
/*     */     //   47: invokevirtual 201	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   50: goto +12 -> 62
/*     */     //   53: pop
/*     */     //   54: new 190	java/lang/String
/*     */     //   57: dup_x1
/*     */     //   58: swap
/*     */     //   59: invokespecial 204	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */     //   62: invokespecial 205	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
/*     */     //   65: athrow
/*     */     //   66: aload_0
/*     */     //   67: aload_1
/*     */     //   68: invokestatic 78	com/facebook/presto/jdbc/internal/guava/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   71: checkcast 54	com/facebook/presto/jdbc/internal/guava/collect/Cut
/*     */     //   74: putfield 207	com/facebook/presto/jdbc/internal/guava/collect/Range:lowerBound	Lcom/facebook/presto/jdbc/internal/guava/collect/Cut;
/*     */     //   77: aload_0
/*     */     //   78: aload_2
/*     */     //   79: invokestatic 78	com/facebook/presto/jdbc/internal/guava/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   82: checkcast 54	com/facebook/presto/jdbc/internal/guava/collect/Cut
/*     */     //   85: putfield 209	com/facebook/presto/jdbc/internal/guava/collect/Range:upperBound	Lcom/facebook/presto/jdbc/internal/guava/collect/Cut;
/*     */     //   88: return
/*     */     // Line number table:
/*     */     //   Java source line #360	-> byte code offset #0
/*     */     //   Java source line #361	-> byte code offset #4
/*     */     //   Java source line #363	-> byte code offset #26
/*     */     //   Java source line #365	-> byte code offset #66
/*     */     //   Java source line #366	-> byte code offset #77
/*     */     //   Java source line #367	-> byte code offset #88
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	89	0	this	Range<C>
/*     */     //   0	89	1	lowerBound	Cut<C>
/*     */     //   0	89	2	upperBound	Cut<C>
/*     */   }
/*     */   
/*     */   public boolean hasLowerBound()
/*     */   {
/* 373 */     return this.lowerBound != Cut.belowAll();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public C lowerEndpoint()
/*     */   {
/* 383 */     return this.lowerBound.endpoint();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BoundType lowerBoundType()
/*     */   {
/* 394 */     return this.lowerBound.typeAsLowerBound();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasUpperBound()
/*     */   {
/* 401 */     return this.upperBound != Cut.aboveAll();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public C upperEndpoint()
/*     */   {
/* 411 */     return this.upperBound.endpoint();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BoundType upperBoundType()
/*     */   {
/* 422 */     return this.upperBound.typeAsUpperBound();
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
/*     */   public boolean isEmpty()
/*     */   {
/* 435 */     return this.lowerBound.equals(this.upperBound);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean contains(C value)
/*     */   {
/* 444 */     Preconditions.checkNotNull(value);
/*     */     
/* 446 */     return (this.lowerBound.isLessThan(value)) && (!this.upperBound.isLessThan(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public boolean apply(C input)
/*     */   {
/* 456 */     return contains(input);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean containsAll(Iterable<? extends C> values)
/*     */   {
/* 464 */     if (Iterables.isEmpty(values)) {
/* 465 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 469 */     if ((values instanceof SortedSet)) {
/* 470 */       SortedSet<? extends C> set = cast(values);
/* 471 */       Comparator<?> comparator = set.comparator();
/* 472 */       if ((Ordering.natural().equals(comparator)) || (comparator == null)) {
/* 473 */         return (contains((Comparable)set.first())) && (contains((Comparable)set.last()));
/*     */       }
/*     */     }
/*     */     
/* 477 */     for (C value : values) {
/* 478 */       if (!contains(value)) {
/* 479 */         return false;
/*     */       }
/*     */     }
/* 482 */     return true;
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
/*     */   public boolean encloses(Range<C> other)
/*     */   {
/* 510 */     return (this.lowerBound.compareTo(other.lowerBound) <= 0) && (this.upperBound.compareTo(other.upperBound) >= 0);
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
/*     */   public boolean isConnected(Range<C> other)
/*     */   {
/* 539 */     return (this.lowerBound.compareTo(other.upperBound) <= 0) && (other.lowerBound.compareTo(this.upperBound) <= 0);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Range<C> intersection(Range<C> connectedRange)
/*     */   {
/* 560 */     int lowerCmp = this.lowerBound.compareTo(connectedRange.lowerBound);
/* 561 */     int upperCmp = this.upperBound.compareTo(connectedRange.upperBound);
/* 562 */     if ((lowerCmp >= 0) && (upperCmp <= 0))
/* 563 */       return this;
/* 564 */     if ((lowerCmp <= 0) && (upperCmp >= 0)) {
/* 565 */       return connectedRange;
/*     */     }
/* 567 */     Cut<C> newLower = lowerCmp >= 0 ? this.lowerBound : connectedRange.lowerBound;
/* 568 */     Cut<C> newUpper = upperCmp <= 0 ? this.upperBound : connectedRange.upperBound;
/* 569 */     return create(newLower, newUpper);
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
/*     */ 
/*     */ 
/*     */   public Range<C> span(Range<C> other)
/*     */   {
/* 585 */     int lowerCmp = this.lowerBound.compareTo(other.lowerBound);
/* 586 */     int upperCmp = this.upperBound.compareTo(other.upperBound);
/* 587 */     if ((lowerCmp <= 0) && (upperCmp >= 0))
/* 588 */       return this;
/* 589 */     if ((lowerCmp >= 0) && (upperCmp <= 0)) {
/* 590 */       return other;
/*     */     }
/* 592 */     Cut<C> newLower = lowerCmp <= 0 ? this.lowerBound : other.lowerBound;
/* 593 */     Cut<C> newUpper = upperCmp >= 0 ? this.upperBound : other.upperBound;
/* 594 */     return create(newLower, newUpper);
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
/*     */   public Range<C> canonical(DiscreteDomain<C> domain)
/*     */   {
/* 623 */     Preconditions.checkNotNull(domain);
/* 624 */     Cut<C> lower = this.lowerBound.canonical(domain);
/* 625 */     Cut<C> upper = this.upperBound.canonical(domain);
/* 626 */     return (lower == this.lowerBound) && (upper == this.upperBound) ? this : create(lower, upper);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 637 */     if ((object instanceof Range)) {
/* 638 */       Range<?> other = (Range)object;
/* 639 */       return (this.lowerBound.equals(other.lowerBound)) && (this.upperBound.equals(other.upperBound));
/*     */     }
/*     */     
/* 642 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 647 */     return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 655 */     return toString(this.lowerBound, this.upperBound);
/*     */   }
/*     */   
/*     */   private static String toString(Cut<?> lowerBound, Cut<?> upperBound) {
/* 659 */     StringBuilder sb = new StringBuilder(16);
/* 660 */     lowerBound.describeAsLowerBound(sb);
/* 661 */     sb.append('‥');
/* 662 */     upperBound.describeAsUpperBound(sb);
/* 663 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static <T> SortedSet<T> cast(Iterable<T> iterable)
/*     */   {
/* 670 */     return (SortedSet)iterable;
/*     */   }
/*     */   
/*     */   Object readResolve() {
/* 674 */     if (equals(ALL)) {
/* 675 */       return all();
/*     */     }
/* 677 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   static int compareOrThrow(Comparable left, Comparable right)
/*     */   {
/* 683 */     return left.compareTo(right);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\Range.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */