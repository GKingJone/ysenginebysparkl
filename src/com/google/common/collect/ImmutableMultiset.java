/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableMultiset<E>
/*     */   extends ImmutableCollection<E>
/*     */   implements Multiset<E>
/*     */ {
/*  55 */   private static final ImmutableMultiset<Object> EMPTY = new RegularImmutableMultiset(ImmutableMap.of(), 0);
/*     */   
/*     */ 
/*     */   private transient ImmutableSet<Entry<E>> entrySet;
/*     */   
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of()
/*     */   {
/*  63 */     return EMPTY;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E element)
/*     */   {
/*  74 */     return copyOfInternal(new Object[] { element });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2)
/*     */   {
/*  85 */     return copyOfInternal(new Object[] { e1, e2 });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3)
/*     */   {
/*  96 */     return copyOfInternal(new Object[] { e1, e2, e3 });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 107 */     return copyOfInternal(new Object[] { e1, e2, e3, e4 });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 118 */     return copyOfInternal(new Object[] { e1, e2, e3, e4, e5 });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E... others)
/*     */   {
/* 130 */     return new Builder().add(e1).add(e2).add(e3).add(e4).add(e5).add(e6).add(others).build();
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
/*     */   public static <E> ImmutableMultiset<E> copyOf(E[] elements)
/*     */   {
/* 152 */     return copyOf(Arrays.asList(elements));
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
/*     */   public static <E> ImmutableMultiset<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 174 */     if ((elements instanceof ImmutableMultiset))
/*     */     {
/* 176 */       ImmutableMultiset<E> result = (ImmutableMultiset)elements;
/* 177 */       if (!result.isPartialView()) {
/* 178 */         return result;
/*     */       }
/*     */     }
/*     */     
/* 182 */     Multiset<? extends E> multiset = (elements instanceof Multiset) ? Multisets.cast(elements) : LinkedHashMultiset.create(elements);
/*     */     
/*     */ 
/*     */ 
/* 186 */     return copyOfInternal(multiset);
/*     */   }
/*     */   
/*     */   private static <E> ImmutableMultiset<E> copyOfInternal(E... elements) {
/* 190 */     return copyOf(Arrays.asList(elements));
/*     */   }
/*     */   
/*     */   private static <E> ImmutableMultiset<E> copyOfInternal(Multiset<? extends E> multiset)
/*     */   {
/* 195 */     return copyFromEntries(multiset.entrySet());
/*     */   }
/*     */   
/*     */   static <E> ImmutableMultiset<E> copyFromEntries(Collection<? extends Entry<? extends E>> entries)
/*     */   {
/* 200 */     long size = 0L;
/* 201 */     ImmutableMap.Builder<E, Integer> builder = ImmutableMap.builder();
/* 202 */     for (Entry<? extends E> entry : entries) {
/* 203 */       int count = entry.getCount();
/* 204 */       if (count > 0)
/*     */       {
/*     */ 
/* 207 */         builder.put(entry.getElement(), Integer.valueOf(count));
/* 208 */         size += count;
/*     */       }
/*     */     }
/*     */     
/* 212 */     if (size == 0L) {
/* 213 */       return of();
/*     */     }
/* 215 */     return new RegularImmutableMultiset(builder.build(), Ints.saturatedCast(size));
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
/*     */   public static <E> ImmutableMultiset<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 231 */     Multiset<E> multiset = LinkedHashMultiset.create();
/* 232 */     Iterators.addAll(multiset, elements);
/* 233 */     return copyOfInternal(multiset);
/*     */   }
/*     */   
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator()
/*     */   {
/* 239 */     final Iterator<Entry<E>> entryIterator = entrySet().iterator();
/* 240 */     new UnmodifiableIterator()
/*     */     {
/*     */       int remaining;
/*     */       E element;
/*     */       
/*     */       public boolean hasNext() {
/* 246 */         return (this.remaining > 0) || (entryIterator.hasNext());
/*     */       }
/*     */       
/*     */       public E next()
/*     */       {
/* 251 */         if (this.remaining <= 0) {
/* 252 */           Entry<E> entry = (Entry)entryIterator.next();
/* 253 */           this.element = entry.getElement();
/* 254 */           this.remaining = entry.getCount();
/*     */         }
/* 256 */         this.remaining -= 1;
/* 257 */         return (E)this.element;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public boolean contains(@Nullable Object object)
/*     */   {
/* 264 */     return count(object) > 0;
/*     */   }
/*     */   
/*     */   public boolean containsAll(Collection<?> targets)
/*     */   {
/* 269 */     return elementSet().containsAll(targets);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final int add(E element, int occurrences)
/*     */   {
/* 281 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final int remove(Object element, int occurrences)
/*     */   {
/* 293 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final int setCount(E element, int count)
/*     */   {
/* 305 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final boolean setCount(E element, int oldCount, int newCount)
/*     */   {
/* 317 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   @GwtIncompatible("not present in emulated superclass")
/*     */   int copyIntoArray(Object[] dst, int offset)
/*     */   {
/* 323 */     for (Entry<E> entry : entrySet()) {
/* 324 */       Arrays.fill(dst, offset, offset + entry.getCount(), entry.getElement());
/* 325 */       offset += entry.getCount();
/*     */     }
/* 327 */     return offset;
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object object) {
/* 331 */     return Multisets.equalsImpl(this, object);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 335 */     return Sets.hashCodeImpl(entrySet());
/*     */   }
/*     */   
/*     */   public String toString() {
/* 339 */     return entrySet().toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ImmutableSet<Entry<E>> entrySet()
/*     */   {
/* 346 */     ImmutableSet<Entry<E>> es = this.entrySet;
/* 347 */     return es == null ? (this.entrySet = createEntrySet()) : es;
/*     */   }
/*     */   
/*     */ 
/* 351 */   private final ImmutableSet<Entry<E>> createEntrySet() { return isEmpty() ? ImmutableSet.of() : new EntrySet(null); }
/*     */   
/*     */   abstract Entry<E> getEntry(int paramInt);
/*     */   
/*     */   private final class EntrySet extends ImmutableSet<Entry<E>> { private static final long serialVersionUID = 0L;
/*     */     
/*     */     private EntrySet() {}
/*     */     
/* 359 */     boolean isPartialView() { return ImmutableMultiset.this.isPartialView(); }
/*     */     
/*     */ 
/*     */     public UnmodifiableIterator<Entry<E>> iterator()
/*     */     {
/* 364 */       return asList().iterator();
/*     */     }
/*     */     
/*     */     ImmutableList<Entry<E>> createAsList()
/*     */     {
/* 369 */       new ImmutableAsList()
/*     */       {
/*     */         public Entry<E> get(int index) {
/* 372 */           return ImmutableMultiset.this.getEntry(index);
/*     */         }
/*     */         
/*     */         ImmutableCollection<Entry<E>> delegateCollection()
/*     */         {
/* 377 */           return EntrySet.this;
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public int size()
/*     */     {
/* 384 */       return ImmutableMultiset.this.elementSet().size();
/*     */     }
/*     */     
/*     */     public boolean contains(Object o)
/*     */     {
/* 389 */       if ((o instanceof Multiset.Entry)) {
/* 390 */         Entry<?> entry = (Entry)o;
/* 391 */         if (entry.getCount() <= 0) {
/* 392 */           return false;
/*     */         }
/* 394 */         int count = ImmutableMultiset.this.count(entry.getElement());
/* 395 */         return count == entry.getCount();
/*     */       }
/* 397 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 402 */       return ImmutableMultiset.this.hashCode();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     Object writeReplace()
/*     */     {
/* 409 */       return new EntrySetSerializedForm(ImmutableMultiset.this);
/*     */     }
/*     */   }
/*     */   
/*     */   static class EntrySetSerializedForm<E> implements Serializable
/*     */   {
/*     */     final ImmutableMultiset<E> multiset;
/*     */     
/*     */     EntrySetSerializedForm(ImmutableMultiset<E> multiset)
/*     */     {
/* 419 */       this.multiset = multiset;
/*     */     }
/*     */     
/*     */ 
/* 423 */     Object readResolve() { return this.multiset.entrySet(); }
/*     */   }
/*     */   
/*     */   private static class SerializedForm implements Serializable {
/*     */     final Object[] elements;
/*     */     final int[] counts;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     SerializedForm(Multiset<?> multiset) {
/* 432 */       int distinct = multiset.entrySet().size();
/* 433 */       this.elements = new Object[distinct];
/* 434 */       this.counts = new int[distinct];
/* 435 */       int i = 0;
/* 436 */       for (Entry<?> entry : multiset.entrySet()) {
/* 437 */         this.elements[i] = entry.getElement();
/* 438 */         this.counts[i] = entry.getCount();
/* 439 */         i++;
/*     */       }
/*     */     }
/*     */     
/*     */     Object readResolve() {
/* 444 */       LinkedHashMultiset<Object> multiset = LinkedHashMultiset.create(this.elements.length);
/*     */       
/* 446 */       for (int i = 0; i < this.elements.length; i++) {
/* 447 */         multiset.add(this.elements[i], this.counts[i]);
/*     */       }
/* 449 */       return ImmutableMultiset.copyOf(multiset);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 458 */     return new SerializedForm(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> Builder<E> builder()
/*     */   {
/* 466 */     return new Builder();
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
/*     */   public static class Builder<E>
/*     */     extends ImmutableCollection.Builder<E>
/*     */   {
/*     */     final Multiset<E> contents;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder()
/*     */     {
/* 495 */       this(LinkedHashMultiset.create());
/*     */     }
/*     */     
/*     */     Builder(Multiset<E> contents) {
/* 499 */       this.contents = contents;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 510 */       this.contents.add(Preconditions.checkNotNull(element));
/* 511 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<E> addCopies(E element, int occurrences)
/*     */     {
/* 528 */       this.contents.add(Preconditions.checkNotNull(element), occurrences);
/* 529 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<E> setCount(E element, int count)
/*     */     {
/* 543 */       this.contents.setCount(Preconditions.checkNotNull(element), count);
/* 544 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<E> add(E... elements)
/*     */     {
/* 556 */       super.add(elements);
/* 557 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 570 */       if ((elements instanceof Multiset)) {
/* 571 */         Multiset<? extends E> multiset = Multisets.cast(elements);
/* 572 */         for (Entry<? extends E> entry : multiset.entrySet()) {
/* 573 */           addCopies(entry.getElement(), entry.getCount());
/*     */         }
/*     */       } else {
/* 576 */         super.addAll(elements);
/*     */       }
/* 578 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 590 */       super.addAll(elements);
/* 591 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public ImmutableMultiset<E> build()
/*     */     {
/* 599 */       return ImmutableMultiset.copyOf(this.contents);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ImmutableMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */