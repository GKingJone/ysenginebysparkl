/*      */ package com.facebook.presto.jdbc.internal.guava.collect;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Predicates;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.PriorityQueue;
/*      */ import java.util.Queue;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Iterators
/*      */ {
/*   72 */   static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR = new UnmodifiableListIterator()
/*      */   {
/*      */     public boolean hasNext()
/*      */     {
/*   76 */       return false;
/*      */     }
/*      */     
/*      */     public Object next() {
/*   80 */       throw new NoSuchElementException();
/*      */     }
/*      */     
/*      */     public boolean hasPrevious() {
/*   84 */       return false;
/*      */     }
/*      */     
/*      */     public Object previous() {
/*   88 */       throw new NoSuchElementException();
/*      */     }
/*      */     
/*      */     public int nextIndex() {
/*   92 */       return 0;
/*      */     }
/*      */     
/*      */     public int previousIndex() {
/*   96 */       return -1;
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <T> UnmodifiableIterator<T> emptyIterator()
/*      */   {
/*  112 */     return emptyListIterator();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static <T> UnmodifiableListIterator<T> emptyListIterator()
/*      */   {
/*  124 */     return EMPTY_LIST_ITERATOR;
/*      */   }
/*      */   
/*  127 */   private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator()
/*      */   {
/*      */     public boolean hasNext() {
/*  130 */       return false;
/*      */     }
/*      */     
/*      */     public Object next() {
/*  134 */       throw new NoSuchElementException();
/*      */     }
/*      */     
/*      */     public void remove() {
/*  138 */       CollectPreconditions.checkRemove(false);
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static <T> Iterator<T> emptyModifiableIterator()
/*      */   {
/*  151 */     return EMPTY_MODIFIABLE_ITERATOR;
/*      */   }
/*      */   
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> unmodifiableIterator(Iterator<T> iterator)
/*      */   {
/*  157 */     Preconditions.checkNotNull(iterator);
/*  158 */     if ((iterator instanceof UnmodifiableIterator)) {
/*  159 */       return (UnmodifiableIterator)iterator;
/*      */     }
/*  161 */     new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/*  164 */         return this.val$iterator.hasNext();
/*      */       }
/*      */       
/*      */       public T next() {
/*  168 */         return (T)this.val$iterator.next();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <T> UnmodifiableIterator<T> unmodifiableIterator(UnmodifiableIterator<T> iterator)
/*      */   {
/*  181 */     return (UnmodifiableIterator)Preconditions.checkNotNull(iterator);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int size(Iterator<?> iterator)
/*      */   {
/*  190 */     int count = 0;
/*  191 */     while (iterator.hasNext()) {
/*  192 */       iterator.next();
/*  193 */       count++;
/*      */     }
/*  195 */     return count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean contains(Iterator<?> iterator, @Nullable Object element)
/*      */   {
/*  202 */     return any(iterator, Predicates.equalTo(element));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean removeAll(Iterator<?> removeFrom, Collection<?> elementsToRemove)
/*      */   {
/*  216 */     return removeIf(removeFrom, Predicates.in(elementsToRemove));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> boolean removeIf(Iterator<T> removeFrom, Predicate<? super T> predicate)
/*      */   {
/*  232 */     Preconditions.checkNotNull(predicate);
/*  233 */     boolean modified = false;
/*  234 */     while (removeFrom.hasNext()) {
/*  235 */       if (predicate.apply(removeFrom.next())) {
/*  236 */         removeFrom.remove();
/*  237 */         modified = true;
/*      */       }
/*      */     }
/*  240 */     return modified;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean retainAll(Iterator<?> removeFrom, Collection<?> elementsToRetain)
/*      */   {
/*  254 */     return removeIf(removeFrom, Predicates.not(Predicates.in(elementsToRetain)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean elementsEqual(Iterator<?> iterator1, Iterator<?> iterator2)
/*      */   {
/*  269 */     while (iterator1.hasNext()) {
/*  270 */       if (!iterator2.hasNext()) {
/*  271 */         return false;
/*      */       }
/*  273 */       Object o1 = iterator1.next();
/*  274 */       Object o2 = iterator2.next();
/*  275 */       if (!Objects.equal(o1, o2)) {
/*  276 */         return false;
/*      */       }
/*      */     }
/*  279 */     return !iterator2.hasNext();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toString(Iterator<?> iterator)
/*      */   {
/*  288 */     return ']';
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> T getOnlyElement(Iterator<T> iterator)
/*      */   {
/*  302 */     T first = iterator.next();
/*  303 */     if (!iterator.hasNext()) {
/*  304 */       return first;
/*      */     }
/*      */     
/*  307 */     StringBuilder sb = new StringBuilder();
/*  308 */     String str1 = String.valueOf(String.valueOf(first));sb.append(31 + str1.length() + "expected one element but was: <" + str1);
/*  309 */     for (int i = 0; (i < 4) && (iterator.hasNext()); i++) {
/*  310 */       String str2 = String.valueOf(String.valueOf(iterator.next()));sb.append(2 + str2.length() + ", " + str2);
/*      */     }
/*  312 */     if (iterator.hasNext()) {
/*  313 */       sb.append(", ...");
/*      */     }
/*  315 */     sb.append('>');
/*      */     
/*  317 */     throw new IllegalArgumentException(sb.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   public static <T> T getOnlyElement(Iterator<? extends T> iterator, @Nullable T defaultValue)
/*      */   {
/*  329 */     return (T)(iterator.hasNext() ? getOnlyElement(iterator) : defaultValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("Array.newInstance(Class, int)")
/*      */   public static <T> T[] toArray(Iterator<? extends T> iterator, Class<T> type)
/*      */   {
/*  344 */     List<T> list = Lists.newArrayList(iterator);
/*  345 */     return Iterables.toArray(list, type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator)
/*      */   {
/*  358 */     Preconditions.checkNotNull(addTo);
/*  359 */     Preconditions.checkNotNull(iterator);
/*  360 */     boolean wasModified = false;
/*  361 */     while (iterator.hasNext()) {
/*  362 */       wasModified |= addTo.add(iterator.next());
/*      */     }
/*  364 */     return wasModified;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int frequency(Iterator<?> iterator, @Nullable Object element)
/*      */   {
/*  375 */     return size(filter(iterator, Predicates.equalTo(element)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> cycle(Iterable<T> iterable)
/*      */   {
/*  393 */     Preconditions.checkNotNull(iterable);
/*  394 */     new Iterator() {
/*  395 */       Iterator<T> iterator = Iterators.emptyIterator();
/*      */       Iterator<T> removeFrom;
/*      */       
/*      */       public boolean hasNext()
/*      */       {
/*  400 */         if (!this.iterator.hasNext()) {
/*  401 */           this.iterator = this.val$iterable.iterator();
/*      */         }
/*  403 */         return this.iterator.hasNext();
/*      */       }
/*      */       
/*      */       public T next() {
/*  407 */         if (!hasNext()) {
/*  408 */           throw new NoSuchElementException();
/*      */         }
/*  410 */         this.removeFrom = this.iterator;
/*  411 */         return (T)this.iterator.next();
/*      */       }
/*      */       
/*      */       public void remove() {
/*  415 */         CollectPreconditions.checkRemove(this.removeFrom != null);
/*  416 */         this.removeFrom.remove();
/*  417 */         this.removeFrom = null;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> cycle(T... elements)
/*      */   {
/*  436 */     return cycle(Lists.newArrayList(elements));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b)
/*      */   {
/*  454 */     return concat(ImmutableList.of(a, b).iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c)
/*      */   {
/*  473 */     return concat(ImmutableList.of(a, b, c).iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c, Iterator<? extends T> d)
/*      */   {
/*  493 */     return concat(ImmutableList.of(a, b, c, d).iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T>... inputs)
/*      */   {
/*  512 */     return concat(ImmutableList.copyOf(inputs).iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends Iterator<? extends T>> inputs)
/*      */   {
/*  531 */     Preconditions.checkNotNull(inputs);
/*  532 */     new Iterator() {
/*  533 */       Iterator<? extends T> current = Iterators.emptyIterator();
/*      */       
/*      */ 
/*      */ 
/*      */       Iterator<? extends T> removeFrom;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean hasNext()
/*      */       {
/*      */         boolean currentHasNext;
/*      */         
/*      */ 
/*  547 */         while ((!(currentHasNext = ((Iterator)Preconditions.checkNotNull(this.current)).hasNext())) && (this.val$inputs.hasNext())) {
/*  548 */           this.current = ((Iterator)this.val$inputs.next());
/*      */         }
/*  550 */         return currentHasNext;
/*      */       }
/*      */       
/*      */       public T next() {
/*  554 */         if (!hasNext()) {
/*  555 */           throw new NoSuchElementException();
/*      */         }
/*  557 */         this.removeFrom = this.current;
/*  558 */         return (T)this.current.next();
/*      */       }
/*      */       
/*      */       public void remove() {
/*  562 */         CollectPreconditions.checkRemove(this.removeFrom != null);
/*  563 */         this.removeFrom.remove();
/*  564 */         this.removeFrom = null;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> UnmodifiableIterator<List<T>> partition(Iterator<T> iterator, int size)
/*      */   {
/*  586 */     return partitionImpl(iterator, size, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> UnmodifiableIterator<List<T>> paddedPartition(Iterator<T> iterator, int size)
/*      */   {
/*  607 */     return partitionImpl(iterator, size, true);
/*      */   }
/*      */   
/*      */   private static <T> UnmodifiableIterator<List<T>> partitionImpl(Iterator<T> iterator, final int size, final boolean pad)
/*      */   {
/*  612 */     Preconditions.checkNotNull(iterator);
/*  613 */     Preconditions.checkArgument(size > 0);
/*  614 */     new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/*  617 */         return this.val$iterator.hasNext();
/*      */       }
/*      */       
/*      */       public List<T> next() {
/*  621 */         if (!hasNext()) {
/*  622 */           throw new NoSuchElementException();
/*      */         }
/*  624 */         Object[] array = new Object[size];
/*  625 */         for (int count = 0; 
/*  626 */             (count < size) && (this.val$iterator.hasNext()); count++) {
/*  627 */           array[count] = this.val$iterator.next();
/*      */         }
/*  629 */         for (int i = count; i < size; i++) {
/*  630 */           array[i] = null;
/*      */         }
/*      */         
/*      */ 
/*  634 */         List<T> list = Collections.unmodifiableList(Arrays.asList(array));
/*      */         
/*  636 */         return (pad) || (count == size) ? list : list.subList(0, count);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> filter(Iterator<T> unfiltered, final Predicate<? super T> predicate)
/*      */   {
/*  646 */     Preconditions.checkNotNull(unfiltered);
/*  647 */     Preconditions.checkNotNull(predicate);
/*  648 */     new AbstractIterator() {
/*      */       protected T computeNext() {
/*  650 */         while (this.val$unfiltered.hasNext()) {
/*  651 */           T element = this.val$unfiltered.next();
/*  652 */           if (predicate.apply(element)) {
/*  653 */             return element;
/*      */           }
/*      */         }
/*  656 */         return (T)endOfData();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("Class.isInstance")
/*      */   public static <T> UnmodifiableIterator<T> filter(Iterator<?> unfiltered, Class<T> type)
/*      */   {
/*  675 */     return filter(unfiltered, Predicates.instanceOf(type));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> boolean any(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  684 */     return indexOf(iterator, predicate) != -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> boolean all(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  694 */     Preconditions.checkNotNull(predicate);
/*  695 */     while (iterator.hasNext()) {
/*  696 */       T element = iterator.next();
/*  697 */       if (!predicate.apply(element)) {
/*  698 */         return false;
/*      */       }
/*      */     }
/*  701 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> T find(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  717 */     return (T)filter(iterator, predicate).next();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   public static <T> T find(Iterator<? extends T> iterator, Predicate<? super T> predicate, @Nullable T defaultValue)
/*      */   {
/*  733 */     return (T)getNext(filter(iterator, predicate), defaultValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Optional<T> tryFind(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  751 */     UnmodifiableIterator<T> filteredIterator = filter(iterator, predicate);
/*  752 */     return filteredIterator.hasNext() ? Optional.of(filteredIterator.next()) : Optional.absent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> int indexOf(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  775 */     Preconditions.checkNotNull(predicate, "predicate");
/*  776 */     for (int i = 0; iterator.hasNext(); i++) {
/*  777 */       T current = iterator.next();
/*  778 */       if (predicate.apply(current)) {
/*  779 */         return i;
/*      */       }
/*      */     }
/*  782 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <F, T> Iterator<T> transform(Iterator<F> fromIterator, final Function<? super F, ? extends T> function)
/*      */   {
/*  795 */     Preconditions.checkNotNull(function);
/*  796 */     new TransformedIterator(fromIterator)
/*      */     {
/*      */       T transform(F from) {
/*  799 */         return (T)function.apply(from);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> T get(Iterator<T> iterator, int position)
/*      */   {
/*  815 */     checkNonnegative(position);
/*  816 */     int skipped = advance(iterator, position);
/*  817 */     if (!iterator.hasNext()) {
/*  818 */       int i = position;int j = skipped;throw new IndexOutOfBoundsException(91 + "position (" + i + ") must be less than the number of elements that remained (" + j + ")");
/*      */     }
/*      */     
/*      */ 
/*  822 */     return (T)iterator.next();
/*      */   }
/*      */   
/*      */   static void checkNonnegative(int position) {
/*  826 */     if (position < 0) {
/*  827 */       int i = position;throw new IndexOutOfBoundsException(43 + "position (" + i + ") must not be negative");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   public static <T> T get(Iterator<? extends T> iterator, int position, @Nullable T defaultValue)
/*      */   {
/*  849 */     checkNonnegative(position);
/*  850 */     advance(iterator, position);
/*  851 */     return (T)getNext(iterator, defaultValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   public static <T> T getNext(Iterator<? extends T> iterator, @Nullable T defaultValue)
/*      */   {
/*  865 */     return (T)(iterator.hasNext() ? iterator.next() : defaultValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> T getLast(Iterator<T> iterator)
/*      */   {
/*      */     for (;;)
/*      */     {
/*  876 */       T current = iterator.next();
/*  877 */       if (!iterator.hasNext()) {
/*  878 */         return current;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   public static <T> T getLast(Iterator<? extends T> iterator, @Nullable T defaultValue)
/*      */   {
/*  893 */     return (T)(iterator.hasNext() ? getLast(iterator) : defaultValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int advance(Iterator<?> iterator, int numberToAdvance)
/*      */   {
/*  904 */     Preconditions.checkNotNull(iterator);
/*  905 */     Preconditions.checkArgument(numberToAdvance >= 0, "numberToAdvance must be nonnegative");
/*      */     
/*      */ 
/*  908 */     for (int i = 0; (i < numberToAdvance) && (iterator.hasNext()); i++) {
/*  909 */       iterator.next();
/*      */     }
/*  911 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> limit(final Iterator<T> iterator, int limitSize)
/*      */   {
/*  928 */     Preconditions.checkNotNull(iterator);
/*  929 */     Preconditions.checkArgument(limitSize >= 0, "limit is negative");
/*  930 */     new Iterator()
/*      */     {
/*      */       private int count;
/*      */       
/*      */       public boolean hasNext() {
/*  935 */         return (this.count < this.val$limitSize) && (iterator.hasNext());
/*      */       }
/*      */       
/*      */       public T next()
/*      */       {
/*  940 */         if (!hasNext()) {
/*  941 */           throw new NoSuchElementException();
/*      */         }
/*  943 */         this.count += 1;
/*  944 */         return (T)iterator.next();
/*      */       }
/*      */       
/*      */       public void remove()
/*      */       {
/*  949 */         iterator.remove();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterator<T> consumingIterator(Iterator<T> iterator)
/*      */   {
/*  968 */     Preconditions.checkNotNull(iterator);
/*  969 */     new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/*  972 */         return this.val$iterator.hasNext();
/*      */       }
/*      */       
/*      */       public T next()
/*      */       {
/*  977 */         T next = this.val$iterator.next();
/*  978 */         this.val$iterator.remove();
/*  979 */         return next;
/*      */       }
/*      */       
/*      */       public String toString()
/*      */       {
/*  984 */         return "Iterators.consumingIterator(...)";
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   static <T> T pollNext(Iterator<T> iterator)
/*      */   {
/*  995 */     if (iterator.hasNext()) {
/*  996 */       T result = iterator.next();
/*  997 */       iterator.remove();
/*  998 */       return result;
/*      */     }
/* 1000 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static void clear(Iterator<?> iterator)
/*      */   {
/* 1010 */     Preconditions.checkNotNull(iterator);
/* 1011 */     while (iterator.hasNext()) {
/* 1012 */       iterator.next();
/* 1013 */       iterator.remove();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> forArray(T... array)
/*      */   {
/* 1031 */     return forArray(array, 0, array.length, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static <T> UnmodifiableListIterator<T> forArray(final T[] array, final int offset, int length, int index)
/*      */   {
/* 1043 */     Preconditions.checkArgument(length >= 0);
/* 1044 */     int end = offset + length;
/*      */     
/*      */ 
/* 1047 */     Preconditions.checkPositionIndexes(offset, end, array.length);
/* 1048 */     Preconditions.checkPositionIndex(index, length);
/* 1049 */     if (length == 0) {
/* 1050 */       return emptyListIterator();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1058 */     new AbstractIndexedListIterator(length, index) {
/*      */       protected T get(int index) {
/* 1060 */         return (T)array[(offset + index)];
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> singletonIterator(@Nullable T value)
/*      */   {
/* 1073 */     new UnmodifiableIterator() {
/*      */       boolean done;
/*      */       
/*      */       public boolean hasNext() {
/* 1077 */         return !this.done;
/*      */       }
/*      */       
/*      */       public T next() {
/* 1081 */         if (this.done) {
/* 1082 */           throw new NoSuchElementException();
/*      */         }
/* 1084 */         this.done = true;
/* 1085 */         return (T)this.val$value;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> forEnumeration(Enumeration<T> enumeration)
/*      */   {
/* 1100 */     Preconditions.checkNotNull(enumeration);
/* 1101 */     new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/* 1104 */         return this.val$enumeration.hasMoreElements();
/*      */       }
/*      */       
/*      */       public T next() {
/* 1108 */         return (T)this.val$enumeration.nextElement();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Enumeration<T> asEnumeration(Iterator<T> iterator)
/*      */   {
/* 1121 */     Preconditions.checkNotNull(iterator);
/* 1122 */     new Enumeration()
/*      */     {
/*      */       public boolean hasMoreElements() {
/* 1125 */         return this.val$iterator.hasNext();
/*      */       }
/*      */       
/*      */       public T nextElement() {
/* 1129 */         return (T)this.val$iterator.next();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */   private static class PeekingImpl<E>
/*      */     implements PeekingIterator<E>
/*      */   {
/*      */     private final Iterator<? extends E> iterator;
/*      */     private boolean hasPeeked;
/*      */     private E peekedElement;
/*      */     
/*      */     public PeekingImpl(Iterator<? extends E> iterator)
/*      */     {
/* 1144 */       this.iterator = ((Iterator)Preconditions.checkNotNull(iterator));
/*      */     }
/*      */     
/*      */     public boolean hasNext()
/*      */     {
/* 1149 */       return (this.hasPeeked) || (this.iterator.hasNext());
/*      */     }
/*      */     
/*      */     public E next()
/*      */     {
/* 1154 */       if (!this.hasPeeked) {
/* 1155 */         return (E)this.iterator.next();
/*      */       }
/* 1157 */       E result = this.peekedElement;
/* 1158 */       this.hasPeeked = false;
/* 1159 */       this.peekedElement = null;
/* 1160 */       return result;
/*      */     }
/*      */     
/*      */     public void remove()
/*      */     {
/* 1165 */       Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
/* 1166 */       this.iterator.remove();
/*      */     }
/*      */     
/*      */     public E peek()
/*      */     {
/* 1171 */       if (!this.hasPeeked) {
/* 1172 */         this.peekedElement = this.iterator.next();
/* 1173 */         this.hasPeeked = true;
/*      */       }
/* 1175 */       return (E)this.peekedElement;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> iterator)
/*      */   {
/* 1219 */     if ((iterator instanceof PeekingImpl))
/*      */     {
/*      */ 
/*      */ 
/* 1223 */       PeekingImpl<T> peeking = (PeekingImpl)iterator;
/* 1224 */       return peeking;
/*      */     }
/* 1226 */     return new PeekingImpl(iterator);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <T> PeekingIterator<T> peekingIterator(PeekingIterator<T> iterator)
/*      */   {
/* 1237 */     return (PeekingIterator)Preconditions.checkNotNull(iterator);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Beta
/*      */   public static <T> UnmodifiableIterator<T> mergeSorted(Iterable<? extends Iterator<? extends T>> iterators, Comparator<? super T> comparator)
/*      */   {
/* 1257 */     Preconditions.checkNotNull(iterators, "iterators");
/* 1258 */     Preconditions.checkNotNull(comparator, "comparator");
/*      */     
/* 1260 */     return new MergingIterator(iterators, comparator);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class MergingIterator<T>
/*      */     extends UnmodifiableIterator<T>
/*      */   {
/*      */     final Queue<PeekingIterator<T>> queue;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MergingIterator(Iterable<? extends Iterator<? extends T>> iterators, final Comparator<? super T> itemComparator)
/*      */     {
/* 1279 */       Comparator<PeekingIterator<T>> heapComparator = new Comparator()
/*      */       {
/*      */         public int compare(PeekingIterator<T> o1, PeekingIterator<T> o2)
/*      */         {
/* 1283 */           return itemComparator.compare(o1.peek(), o2.peek());
/*      */         }
/*      */         
/* 1286 */       };
/* 1287 */       this.queue = new PriorityQueue(2, heapComparator);
/*      */       
/* 1289 */       for (Iterator<? extends T> iterator : iterators) {
/* 1290 */         if (iterator.hasNext()) {
/* 1291 */           this.queue.add(Iterators.peekingIterator(iterator));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean hasNext()
/*      */     {
/* 1298 */       return !this.queue.isEmpty();
/*      */     }
/*      */     
/*      */     public T next()
/*      */     {
/* 1303 */       PeekingIterator<T> nextIter = (PeekingIterator)this.queue.remove();
/* 1304 */       T next = nextIter.next();
/* 1305 */       if (nextIter.hasNext()) {
/* 1306 */         this.queue.add(nextIter);
/*      */       }
/* 1308 */       return next;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static <T> ListIterator<T> cast(Iterator<T> iterator)
/*      */   {
/* 1316 */     return (ListIterator)iterator;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\Iterators.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */