/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Optional;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Queue;
/*      */ import java.util.RandomAccess;
/*      */ import java.util.Set;
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
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Iterables
/*      */ {
/*      */   public static <T> Iterable<T> unmodifiableIterable(Iterable<T> iterable)
/*      */   {
/*   66 */     Preconditions.checkNotNull(iterable);
/*   67 */     if (((iterable instanceof UnmodifiableIterable)) || ((iterable instanceof ImmutableCollection)))
/*      */     {
/*   69 */       return iterable;
/*      */     }
/*   71 */     return new UnmodifiableIterable(iterable, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <E> Iterable<E> unmodifiableIterable(ImmutableCollection<E> iterable)
/*      */   {
/*   82 */     return (Iterable)Preconditions.checkNotNull(iterable);
/*      */   }
/*      */   
/*      */   private static final class UnmodifiableIterable<T> extends FluentIterable<T> {
/*      */     private final Iterable<T> iterable;
/*      */     
/*      */     private UnmodifiableIterable(Iterable<T> iterable) {
/*   89 */       this.iterable = iterable;
/*      */     }
/*      */     
/*      */     public Iterator<T> iterator()
/*      */     {
/*   94 */       return Iterators.unmodifiableIterator(this.iterable.iterator());
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/*   99 */       return this.iterable.toString();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int size(Iterable<?> iterable)
/*      */   {
/*  108 */     return (iterable instanceof Collection) ? ((Collection)iterable).size() : Iterators.size(iterable.iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean contains(Iterable<?> iterable, @Nullable Object element)
/*      */   {
/*  118 */     if ((iterable instanceof Collection)) {
/*  119 */       Collection<?> collection = (Collection)iterable;
/*  120 */       return Collections2.safeContains(collection, element);
/*      */     }
/*  122 */     return Iterators.contains(iterable.iterator(), element);
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
/*      */   public static boolean removeAll(Iterable<?> removeFrom, Collection<?> elementsToRemove)
/*      */   {
/*  138 */     return (removeFrom instanceof Collection) ? ((Collection)removeFrom).removeAll((Collection)Preconditions.checkNotNull(elementsToRemove)) : Iterators.removeAll(removeFrom.iterator(), elementsToRemove);
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
/*      */   public static boolean retainAll(Iterable<?> removeFrom, Collection<?> elementsToRetain)
/*      */   {
/*  156 */     return (removeFrom instanceof Collection) ? ((Collection)removeFrom).retainAll((Collection)Preconditions.checkNotNull(elementsToRetain)) : Iterators.retainAll(removeFrom.iterator(), elementsToRetain);
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
/*      */   public static <T> boolean removeIf(Iterable<T> removeFrom, Predicate<? super T> predicate)
/*      */   {
/*  176 */     if (((removeFrom instanceof RandomAccess)) && ((removeFrom instanceof List))) {
/*  177 */       return removeIfFromRandomAccessList((List)removeFrom, (Predicate)Preconditions.checkNotNull(predicate));
/*      */     }
/*      */     
/*  180 */     return Iterators.removeIf(removeFrom.iterator(), predicate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static <T> boolean removeIfFromRandomAccessList(List<T> list, Predicate<? super T> predicate)
/*      */   {
/*  187 */     int from = 0;
/*  188 */     int to = 0;
/*  190 */     for (; 
/*  190 */         from < list.size(); from++) {
/*  191 */       T element = list.get(from);
/*  192 */       if (!predicate.apply(element)) {
/*  193 */         if (from > to) {
/*      */           try {
/*  195 */             list.set(to, element);
/*      */           } catch (UnsupportedOperationException e) {
/*  197 */             slowRemoveIfForRemainingElements(list, predicate, to, from);
/*  198 */             return true;
/*      */           }
/*      */         }
/*  201 */         to++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  206 */     list.subList(to, list.size()).clear();
/*  207 */     return from != to;
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
/*      */   private static <T> void slowRemoveIfForRemainingElements(List<T> list, Predicate<? super T> predicate, int to, int from)
/*      */   {
/*  222 */     for (int n = list.size() - 1; n > from; n--) {
/*  223 */       if (predicate.apply(list.get(n))) {
/*  224 */         list.remove(n);
/*      */       }
/*      */     }
/*      */     
/*  228 */     for (int n = from - 1; n >= to; n--) {
/*  229 */       list.remove(n);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   static <T> T removeFirstMatching(Iterable<T> removeFrom, Predicate<? super T> predicate)
/*      */   {
/*  238 */     Preconditions.checkNotNull(predicate);
/*  239 */     Iterator<T> iterator = removeFrom.iterator();
/*  240 */     while (iterator.hasNext()) {
/*  241 */       T next = iterator.next();
/*  242 */       if (predicate.apply(next)) {
/*  243 */         iterator.remove();
/*  244 */         return next;
/*      */       }
/*      */     }
/*  247 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean elementsEqual(Iterable<?> iterable1, Iterable<?> iterable2)
/*      */   {
/*  259 */     if (((iterable1 instanceof Collection)) && ((iterable2 instanceof Collection))) {
/*  260 */       Collection<?> collection1 = (Collection)iterable1;
/*  261 */       Collection<?> collection2 = (Collection)iterable2;
/*  262 */       if (collection1.size() != collection2.size()) {
/*  263 */         return false;
/*      */       }
/*      */     }
/*  266 */     return Iterators.elementsEqual(iterable1.iterator(), iterable2.iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toString(Iterable<?> iterable)
/*      */   {
/*  274 */     return Iterators.toString(iterable.iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> T getOnlyElement(Iterable<T> iterable)
/*      */   {
/*  285 */     return (T)Iterators.getOnlyElement(iterable.iterator());
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
/*      */   public static <T> T getOnlyElement(Iterable<? extends T> iterable, @Nullable T defaultValue)
/*      */   {
/*  298 */     return (T)Iterators.getOnlyElement(iterable.iterator(), defaultValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("Array.newInstance(Class, int)")
/*      */   public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type)
/*      */   {
/*  311 */     Collection<? extends T> collection = toCollection(iterable);
/*  312 */     T[] array = ObjectArrays.newArray(type, collection.size());
/*  313 */     return collection.toArray(array);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static Object[] toArray(Iterable<?> iterable)
/*      */   {
/*  324 */     return toCollection(iterable).toArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static <E> Collection<E> toCollection(Iterable<E> iterable)
/*      */   {
/*  333 */     return (iterable instanceof Collection) ? (Collection)iterable : Lists.newArrayList(iterable.iterator());
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
/*      */   public static <T> boolean addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd)
/*      */   {
/*  346 */     if ((elementsToAdd instanceof Collection)) {
/*  347 */       Collection<? extends T> c = Collections2.cast(elementsToAdd);
/*  348 */       return addTo.addAll(c);
/*      */     }
/*  350 */     return Iterators.addAll(addTo, ((Iterable)Preconditions.checkNotNull(elementsToAdd)).iterator());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int frequency(Iterable<?> iterable, @Nullable Object element)
/*      */   {
/*  361 */     if ((iterable instanceof Multiset))
/*  362 */       return ((Multiset)iterable).count(element);
/*  363 */     if ((iterable instanceof Set)) {
/*  364 */       return ((Set)iterable).contains(element) ? 1 : 0;
/*      */     }
/*  366 */     return Iterators.frequency(iterable.iterator(), element);
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
/*      */   public static <T> Iterable<T> cycle(Iterable<T> iterable)
/*      */   {
/*  387 */     Preconditions.checkNotNull(iterable);
/*  388 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  391 */         return Iterators.cycle(this.val$iterable);
/*      */       }
/*      */       
/*  394 */       public String toString() { return this.val$iterable.toString() + " (cycled)"; }
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterable<T> cycle(T... elements)
/*      */   {
/*  418 */     return cycle(Lists.newArrayList(elements));
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
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b)
/*      */   {
/*  431 */     return concat(ImmutableList.of(a, b));
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
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c)
/*      */   {
/*  445 */     return concat(ImmutableList.of(a, b, c));
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
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c, Iterable<? extends T> d)
/*      */   {
/*  461 */     return concat(ImmutableList.of(a, b, c, d));
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
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T>... inputs)
/*      */   {
/*  475 */     return concat(ImmutableList.copyOf(inputs));
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
/*      */   public static <T> Iterable<T> concat(Iterable<? extends Iterable<? extends T>> inputs)
/*      */   {
/*  490 */     Preconditions.checkNotNull(inputs);
/*  491 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  494 */         return Iterators.concat(Iterables.iterators(this.val$inputs));
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static <T> Iterator<Iterator<? extends T>> iterators(Iterable<? extends Iterable<? extends T>> iterables)
/*      */   {
/*  504 */     new TransformedIterator(iterables.iterator())
/*      */     {
/*      */       Iterator<? extends T> transform(Iterable<? extends T> from)
/*      */       {
/*  508 */         return from.iterator();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterable<List<T>> partition(Iterable<T> iterable, final int size)
/*      */   {
/*  535 */     Preconditions.checkNotNull(iterable);
/*  536 */     Preconditions.checkArgument(size > 0);
/*  537 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<List<T>> iterator() {
/*  540 */         return Iterators.partition(this.val$iterable.iterator(), size);
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
/*      */ 
/*      */ 
/*      */   public static <T> Iterable<List<T>> paddedPartition(Iterable<T> iterable, final int size)
/*      */   {
/*  564 */     Preconditions.checkNotNull(iterable);
/*  565 */     Preconditions.checkArgument(size > 0);
/*  566 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<List<T>> iterator() {
/*  569 */         return Iterators.paddedPartition(this.val$iterable.iterator(), size);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterable<T> filter(Iterable<T> unfiltered, final Predicate<? super T> predicate)
/*      */   {
/*  580 */     Preconditions.checkNotNull(unfiltered);
/*  581 */     Preconditions.checkNotNull(predicate);
/*  582 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  585 */         return Iterators.filter(this.val$unfiltered.iterator(), predicate);
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
/*      */   public static <T> Iterable<T> filter(Iterable<?> unfiltered, final Class<T> type)
/*      */   {
/*  604 */     Preconditions.checkNotNull(unfiltered);
/*  605 */     Preconditions.checkNotNull(type);
/*  606 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  609 */         return Iterators.filter(this.val$unfiltered.iterator(), type);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> boolean any(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  619 */     return Iterators.any(iterable.iterator(), predicate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> boolean all(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  628 */     return Iterators.all(iterable.iterator(), predicate);
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
/*      */   public static <T> T find(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  642 */     return (T)Iterators.find(iterable.iterator(), predicate);
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
/*      */   public static <T> T find(Iterable<? extends T> iterable, Predicate<? super T> predicate, @Nullable T defaultValue)
/*      */   {
/*  656 */     return (T)Iterators.find(iterable.iterator(), predicate, defaultValue);
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
/*      */   public static <T> Optional<T> tryFind(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  671 */     return Iterators.tryFind(iterable.iterator(), predicate);
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
/*      */   public static <T> int indexOf(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  687 */     return Iterators.indexOf(iterable.iterator(), predicate);
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
/*      */   public static <F, T> Iterable<T> transform(Iterable<F> fromIterable, final Function<? super F, ? extends T> function)
/*      */   {
/*  704 */     Preconditions.checkNotNull(fromIterable);
/*  705 */     Preconditions.checkNotNull(function);
/*  706 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  709 */         return Iterators.transform(this.val$fromIterable.iterator(), function);
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
/*      */   public static <T> T get(Iterable<T> iterable, int position)
/*      */   {
/*  723 */     Preconditions.checkNotNull(iterable);
/*  724 */     return (T)((iterable instanceof List) ? ((List)iterable).get(position) : Iterators.get(iterable.iterator(), position));
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
/*      */   @Nullable
/*      */   public static <T> T get(Iterable<? extends T> iterable, int position, @Nullable T defaultValue)
/*      */   {
/*  744 */     Preconditions.checkNotNull(iterable);
/*  745 */     Iterators.checkNonnegative(position);
/*  746 */     if ((iterable instanceof List)) {
/*  747 */       List<? extends T> list = Lists.cast(iterable);
/*  748 */       return (T)(position < list.size() ? list.get(position) : defaultValue);
/*      */     }
/*  750 */     Iterator<? extends T> iterator = iterable.iterator();
/*  751 */     Iterators.advance(iterator, position);
/*  752 */     return (T)Iterators.getNext(iterator, defaultValue);
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
/*      */   @Nullable
/*      */   public static <T> T getFirst(Iterable<? extends T> iterable, @Nullable T defaultValue)
/*      */   {
/*  771 */     return (T)Iterators.getNext(iterable.iterator(), defaultValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> T getLast(Iterable<T> iterable)
/*      */   {
/*  782 */     if ((iterable instanceof List)) {
/*  783 */       List<T> list = (List)iterable;
/*  784 */       if (list.isEmpty()) {
/*  785 */         throw new NoSuchElementException();
/*      */       }
/*  787 */       return (T)getLastInNonemptyList(list);
/*      */     }
/*      */     
/*  790 */     return (T)Iterators.getLast(iterable.iterator());
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
/*      */   public static <T> T getLast(Iterable<? extends T> iterable, @Nullable T defaultValue)
/*      */   {
/*  803 */     if ((iterable instanceof Collection)) {
/*  804 */       Collection<? extends T> c = Collections2.cast(iterable);
/*  805 */       if (c.isEmpty())
/*  806 */         return defaultValue;
/*  807 */       if ((iterable instanceof List)) {
/*  808 */         return (T)getLastInNonemptyList(Lists.cast(iterable));
/*      */       }
/*      */     }
/*      */     
/*  812 */     return (T)Iterators.getLast(iterable.iterator(), defaultValue);
/*      */   }
/*      */   
/*      */   private static <T> T getLastInNonemptyList(List<T> list) {
/*  816 */     return (T)list.get(list.size() - 1);
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
/*      */   public static <T> Iterable<T> skip(Iterable<T> iterable, final int numberToSkip)
/*      */   {
/*  841 */     Preconditions.checkNotNull(iterable);
/*  842 */     Preconditions.checkArgument(numberToSkip >= 0, "number to skip cannot be negative");
/*      */     
/*  844 */     if ((iterable instanceof List)) {
/*  845 */       List<T> list = (List)iterable;
/*  846 */       new FluentIterable()
/*      */       {
/*      */         public Iterator<T> iterator()
/*      */         {
/*  850 */           int toSkip = Math.min(this.val$list.size(), numberToSkip);
/*  851 */           return this.val$list.subList(toSkip, this.val$list.size()).iterator();
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*  856 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  859 */         final Iterator<T> iterator = this.val$iterable.iterator();
/*      */         
/*  861 */         Iterators.advance(iterator, numberToSkip);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  868 */         new Iterator() {
/*  869 */           boolean atStart = true;
/*      */           
/*      */           public boolean hasNext()
/*      */           {
/*  873 */             return iterator.hasNext();
/*      */           }
/*      */           
/*      */           public T next()
/*      */           {
/*  878 */             T result = iterator.next();
/*  879 */             this.atStart = false;
/*  880 */             return result;
/*      */           }
/*      */           
/*      */           public void remove()
/*      */           {
/*  885 */             CollectPreconditions.checkRemove(!this.atStart);
/*  886 */             iterator.remove();
/*      */           }
/*      */         };
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
/*      */   public static <T> Iterable<T> limit(Iterable<T> iterable, final int limitSize)
/*      */   {
/*  907 */     Preconditions.checkNotNull(iterable);
/*  908 */     Preconditions.checkArgument(limitSize >= 0, "limit is negative");
/*  909 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  912 */         return Iterators.limit(this.val$iterable.iterator(), limitSize);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> Iterable<T> consumingIterable(Iterable<T> iterable)
/*      */   {
/*  937 */     if ((iterable instanceof Queue)) {
/*  938 */       new FluentIterable()
/*      */       {
/*      */         public Iterator<T> iterator() {
/*  941 */           return new ConsumingQueueIterator((Queue)this.val$iterable, null);
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*  946 */     Preconditions.checkNotNull(iterable);
/*      */     
/*  948 */     new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  951 */         return Iterators.consumingIterator(this.val$iterable.iterator());
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */   private static class ConsumingQueueIterator<T> extends AbstractIterator<T> {
/*      */     private final Queue<T> queue;
/*      */     
/*      */     private ConsumingQueueIterator(Queue<T> queue) {
/*  960 */       this.queue = queue;
/*      */     }
/*      */     
/*      */     public T computeNext() {
/*      */       try {
/*  965 */         return (T)this.queue.remove();
/*      */       } catch (NoSuchElementException e) {}
/*  967 */       return (T)endOfData();
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
/*      */   public static boolean isEmpty(Iterable<?> iterable)
/*      */   {
/*  984 */     if ((iterable instanceof Collection)) {
/*  985 */       return ((Collection)iterable).isEmpty();
/*      */     }
/*  987 */     return !iterable.iterator().hasNext();
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
/*      */   @Beta
/*      */   public static <T> Iterable<T> mergeSorted(Iterable<? extends Iterable<? extends T>> iterables, final Comparator<? super T> comparator)
/*      */   {
/* 1006 */     Preconditions.checkNotNull(iterables, "iterables");
/* 1007 */     Preconditions.checkNotNull(comparator, "comparator");
/* 1008 */     Iterable<T> iterable = new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/* 1011 */         return Iterators.mergeSorted(Iterables.transform(this.val$iterables, Iterables.access$300()), comparator);
/*      */       }
/*      */       
/*      */ 
/* 1015 */     };
/* 1016 */     return new UnmodifiableIterable(iterable, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static <T> Function<Iterable<? extends T>, Iterator<? extends T>> toIterator()
/*      */   {
/* 1023 */     new Function()
/*      */     {
/*      */       public Iterator<? extends T> apply(Iterable<? extends T> iterable) {
/* 1026 */         return iterable.iterator();
/*      */       }
/*      */     };
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\Iterables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */