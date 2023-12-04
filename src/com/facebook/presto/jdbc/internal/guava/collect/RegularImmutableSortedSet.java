/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
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
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class RegularImmutableSortedSet<E>
/*     */   extends ImmutableSortedSet<E>
/*     */ {
/*     */   private final transient ImmutableList<E> elements;
/*     */   
/*     */   RegularImmutableSortedSet(ImmutableList<E> elements, Comparator<? super E> comparator)
/*     */   {
/*  54 */     super(comparator);
/*  55 */     this.elements = elements;
/*  56 */     Preconditions.checkArgument(!elements.isEmpty());
/*     */   }
/*     */   
/*     */   public UnmodifiableIterator<E> iterator() {
/*  60 */     return this.elements.iterator();
/*     */   }
/*     */   
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public UnmodifiableIterator<E> descendingIterator() {
/*  65 */     return this.elements.reverse().iterator();
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  69 */     return false;
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  74 */     return this.elements.size();
/*     */   }
/*     */   
/*     */   public boolean contains(Object o) {
/*     */     try {
/*  79 */       return (o != null) && (unsafeBinarySearch(o) >= 0);
/*     */     } catch (ClassCastException e) {}
/*  81 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean containsAll(Collection<?> targets)
/*     */   {
/*  90 */     if ((targets instanceof Multiset)) {
/*  91 */       targets = ((Multiset)targets).elementSet();
/*     */     }
/*  93 */     if ((!SortedIterables.hasSameComparator(comparator(), targets)) || (targets.size() <= 1))
/*     */     {
/*  95 */       return super.containsAll(targets);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */     PeekingIterator<E> thisIterator = Iterators.peekingIterator(iterator());
/* 103 */     Iterator<?> thatIterator = targets.iterator();
/* 104 */     Object target = thatIterator.next();
/*     */     
/*     */     try
/*     */     {
/* 108 */       while (thisIterator.hasNext())
/*     */       {
/* 110 */         int cmp = unsafeCompare(thisIterator.peek(), target);
/*     */         
/* 112 */         if (cmp < 0) {
/* 113 */           thisIterator.next();
/* 114 */         } else if (cmp == 0)
/*     */         {
/* 116 */           if (!thatIterator.hasNext())
/*     */           {
/* 118 */             return true;
/*     */           }
/*     */           
/* 121 */           target = thatIterator.next();
/*     */         }
/* 123 */         else if (cmp > 0) {
/* 124 */           return false;
/*     */         }
/*     */       }
/*     */     } catch (NullPointerException e) {
/* 128 */       return false;
/*     */     } catch (ClassCastException e) {
/* 130 */       return false;
/*     */     }
/*     */     
/* 133 */     return false;
/*     */   }
/*     */   
/*     */   private int unsafeBinarySearch(Object key) throws ClassCastException {
/* 137 */     return Collections.binarySearch(this.elements, key, unsafeComparator());
/*     */   }
/*     */   
/*     */   boolean isPartialView() {
/* 141 */     return this.elements.isPartialView();
/*     */   }
/*     */   
/*     */   int copyIntoArray(Object[] dst, int offset)
/*     */   {
/* 146 */     return this.elements.copyIntoArray(dst, offset);
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object object) {
/* 150 */     if (object == this) {
/* 151 */       return true;
/*     */     }
/* 153 */     if (!(object instanceof Set)) {
/* 154 */       return false;
/*     */     }
/*     */     
/* 157 */     Set<?> that = (Set)object;
/* 158 */     if (size() != that.size()) {
/* 159 */       return false;
/*     */     }
/*     */     
/* 162 */     if (SortedIterables.hasSameComparator(this.comparator, that)) {
/* 163 */       Iterator<?> otherIterator = that.iterator();
/*     */       try {
/* 165 */         Iterator<E> iterator = iterator();
/* 166 */         while (iterator.hasNext()) {
/* 167 */           Object element = iterator.next();
/* 168 */           Object otherElement = otherIterator.next();
/* 169 */           if ((otherElement == null) || (unsafeCompare(element, otherElement) != 0))
/*     */           {
/* 171 */             return false;
/*     */           }
/*     */         }
/* 174 */         return true;
/*     */       } catch (ClassCastException e) {
/* 176 */         return false;
/*     */       } catch (NoSuchElementException e) {
/* 178 */         return false;
/*     */       }
/*     */     }
/* 181 */     return containsAll(that);
/*     */   }
/*     */   
/*     */   public E first()
/*     */   {
/* 186 */     return (E)this.elements.get(0);
/*     */   }
/*     */   
/*     */   public E last()
/*     */   {
/* 191 */     return (E)this.elements.get(size() - 1);
/*     */   }
/*     */   
/*     */   public E lower(E element)
/*     */   {
/* 196 */     int index = headIndex(element, false) - 1;
/* 197 */     return index == -1 ? null : this.elements.get(index);
/*     */   }
/*     */   
/*     */   public E floor(E element)
/*     */   {
/* 202 */     int index = headIndex(element, true) - 1;
/* 203 */     return index == -1 ? null : this.elements.get(index);
/*     */   }
/*     */   
/*     */   public E ceiling(E element)
/*     */   {
/* 208 */     int index = tailIndex(element, true);
/* 209 */     return index == size() ? null : this.elements.get(index);
/*     */   }
/*     */   
/*     */   public E higher(E element)
/*     */   {
/* 214 */     int index = tailIndex(element, false);
/* 215 */     return index == size() ? null : this.elements.get(index);
/*     */   }
/*     */   
/*     */   ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive)
/*     */   {
/* 220 */     return getSubSet(0, headIndex(toElement, inclusive));
/*     */   }
/*     */   
/*     */   int headIndex(E toElement, boolean inclusive) {
/* 224 */     return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(toElement), comparator(), inclusive ? SortedLists.KeyPresentBehavior.FIRST_AFTER : SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   ImmutableSortedSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/* 232 */     return tailSetImpl(fromElement, fromInclusive).headSetImpl(toElement, toInclusive);
/*     */   }
/*     */   
/*     */ 
/*     */   ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive)
/*     */   {
/* 238 */     return getSubSet(tailIndex(fromElement, inclusive), size());
/*     */   }
/*     */   
/*     */   int tailIndex(E fromElement, boolean inclusive) {
/* 242 */     return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(fromElement), comparator(), inclusive ? SortedLists.KeyPresentBehavior.FIRST_PRESENT : SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Comparator<Object> unsafeComparator()
/*     */   {
/* 254 */     return this.comparator;
/*     */   }
/*     */   
/*     */   ImmutableSortedSet<E> getSubSet(int newFromIndex, int newToIndex) {
/* 258 */     if ((newFromIndex == 0) && (newToIndex == size()))
/* 259 */       return this;
/* 260 */     if (newFromIndex < newToIndex) {
/* 261 */       return new RegularImmutableSortedSet(this.elements.subList(newFromIndex, newToIndex), this.comparator);
/*     */     }
/*     */     
/* 264 */     return emptySet(this.comparator);
/*     */   }
/*     */   
/*     */   int indexOf(@Nullable Object target)
/*     */   {
/* 269 */     if (target == null) {
/* 270 */       return -1;
/*     */     }
/*     */     int position;
/*     */     try {
/* 274 */       position = SortedLists.binarySearch(this.elements, target, unsafeComparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
/*     */     }
/*     */     catch (ClassCastException e) {
/* 277 */       return -1;
/*     */     }
/* 279 */     return position >= 0 ? position : -1;
/*     */   }
/*     */   
/*     */   ImmutableList<E> createAsList() {
/* 283 */     return new ImmutableSortedAsList(this, this.elements);
/*     */   }
/*     */   
/*     */   ImmutableSortedSet<E> createDescendingSet()
/*     */   {
/* 288 */     return new RegularImmutableSortedSet(this.elements.reverse(), Ordering.from(this.comparator).reverse());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\RegularImmutableSortedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */