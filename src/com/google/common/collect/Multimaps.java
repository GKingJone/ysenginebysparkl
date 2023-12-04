/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import com.google.common.base.Predicates;
/*      */ import com.google.common.base.Supplier;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */ public final class Multimaps
/*      */ {
/*  113 */   public static <K, V> Multimap<K, V> newMultimap(Map<K, Collection<V>> map, Supplier<? extends Collection<V>> factory) { return new CustomMultimap(map, factory); }
/*      */   
/*      */   private static class CustomMultimap<K, V> extends AbstractMapBasedMultimap<K, V> {
/*      */     transient Supplier<? extends Collection<V>> factory;
/*      */     @GwtIncompatible("java serialization not supported")
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     CustomMultimap(Map<K, Collection<V>> map, Supplier<? extends Collection<V>> factory) {
/*  121 */       super();
/*  122 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*      */     }
/*      */     
/*      */     protected Collection<V> createCollection() {
/*  126 */       return (Collection)this.factory.get();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream)
/*      */       throws IOException
/*      */     {
/*  135 */       stream.defaultWriteObject();
/*  136 */       stream.writeObject(this.factory);
/*  137 */       stream.writeObject(backingMap());
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  144 */       stream.defaultReadObject();
/*  145 */       this.factory = ((Supplier)stream.readObject());
/*  146 */       Map<K, Collection<V>> map = (Map)stream.readObject();
/*  147 */       setMap(map);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <K, V> ListMultimap<K, V> newListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> factory)
/*      */   {
/*  194 */     return new CustomListMultimap(map, factory);
/*      */   }
/*      */   
/*      */   private static class CustomListMultimap<K, V> extends AbstractListMultimap<K, V> {
/*      */     transient Supplier<? extends List<V>> factory;
/*      */     @GwtIncompatible("java serialization not supported")
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     CustomListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> factory) {
/*  203 */       super();
/*  204 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*      */     }
/*      */     
/*      */     protected List<V> createCollection() {
/*  208 */       return (List)this.factory.get();
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream) throws IOException
/*      */     {
/*  214 */       stream.defaultWriteObject();
/*  215 */       stream.writeObject(this.factory);
/*  216 */       stream.writeObject(backingMap());
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  223 */       stream.defaultReadObject();
/*  224 */       this.factory = ((Supplier)stream.readObject());
/*  225 */       Map<K, Collection<V>> map = (Map)stream.readObject();
/*  226 */       setMap(map);
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
/*      */ 
/*      */ 
/*      */   public static <K, V> SetMultimap<K, V> newSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> factory)
/*      */   {
/*  272 */     return new CustomSetMultimap(map, factory);
/*      */   }
/*      */   
/*      */   private static class CustomSetMultimap<K, V> extends AbstractSetMultimap<K, V> {
/*      */     transient Supplier<? extends Set<V>> factory;
/*      */     @GwtIncompatible("not needed in emulated source")
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     CustomSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> factory) {
/*  281 */       super();
/*  282 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*      */     }
/*      */     
/*      */     protected Set<V> createCollection() {
/*  286 */       return (Set)this.factory.get();
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream) throws IOException
/*      */     {
/*  292 */       stream.defaultWriteObject();
/*  293 */       stream.writeObject(this.factory);
/*  294 */       stream.writeObject(backingMap());
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  301 */       stream.defaultReadObject();
/*  302 */       this.factory = ((Supplier)stream.readObject());
/*  303 */       Map<K, Collection<V>> map = (Map)stream.readObject();
/*  304 */       setMap(map);
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
/*      */ 
/*      */ 
/*      */   public static <K, V> SortedSetMultimap<K, V> newSortedSetMultimap(Map<K, Collection<V>> map, Supplier<? extends SortedSet<V>> factory)
/*      */   {
/*  350 */     return new CustomSortedSetMultimap(map, factory);
/*      */   }
/*      */   
/*      */   private static class CustomSortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V> {
/*      */     transient Supplier<? extends SortedSet<V>> factory;
/*      */     transient Comparator<? super V> valueComparator;
/*      */     @GwtIncompatible("not needed in emulated source")
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     CustomSortedSetMultimap(Map<K, Collection<V>> map, Supplier<? extends SortedSet<V>> factory) {
/*  360 */       super();
/*  361 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*  362 */       this.valueComparator = ((SortedSet)factory.get()).comparator();
/*      */     }
/*      */     
/*      */     protected SortedSet<V> createCollection() {
/*  366 */       return (SortedSet)this.factory.get();
/*      */     }
/*      */     
/*      */     public Comparator<? super V> valueComparator() {
/*  370 */       return this.valueComparator;
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream) throws IOException
/*      */     {
/*  376 */       stream.defaultWriteObject();
/*  377 */       stream.writeObject(this.factory);
/*  378 */       stream.writeObject(backingMap());
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  385 */       stream.defaultReadObject();
/*  386 */       this.factory = ((Supplier)stream.readObject());
/*  387 */       this.valueComparator = ((SortedSet)this.factory.get()).comparator();
/*  388 */       Map<K, Collection<V>> map = (Map)stream.readObject();
/*  389 */       setMap(map);
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
/*      */   public static <K, V, M extends Multimap<K, V>> M invertFrom(Multimap<? extends V, ? extends K> source, M dest)
/*      */   {
/*  409 */     Preconditions.checkNotNull(dest);
/*  410 */     for (Entry<? extends V, ? extends K> entry : source.entries()) {
/*  411 */       dest.put(entry.getValue(), entry.getKey());
/*      */     }
/*  413 */     return dest;
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
/*      */   public static <K, V> Multimap<K, V> synchronizedMultimap(Multimap<K, V> multimap)
/*      */   {
/*  451 */     return Synchronized.multimap(multimap, null);
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
/*      */   public static <K, V> Multimap<K, V> unmodifiableMultimap(Multimap<K, V> delegate)
/*      */   {
/*  473 */     if (((delegate instanceof UnmodifiableMultimap)) || ((delegate instanceof ImmutableMultimap)))
/*      */     {
/*  475 */       return delegate;
/*      */     }
/*  477 */     return new UnmodifiableMultimap(delegate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <K, V> Multimap<K, V> unmodifiableMultimap(ImmutableMultimap<K, V> delegate)
/*      */   {
/*  488 */     return (Multimap)Preconditions.checkNotNull(delegate);
/*      */   }
/*      */   
/*      */   private static class UnmodifiableMultimap<K, V> extends ForwardingMultimap<K, V> implements Serializable {
/*      */     final Multimap<K, V> delegate;
/*      */     transient Collection<Entry<K, V>> entries;
/*      */     transient Multiset<K> keys;
/*      */     transient Set<K> keySet;
/*      */     transient Collection<V> values;
/*      */     transient Map<K, Collection<V>> map;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     UnmodifiableMultimap(Multimap<K, V> delegate) {
/*  501 */       this.delegate = ((Multimap)Preconditions.checkNotNull(delegate));
/*      */     }
/*      */     
/*      */     protected Multimap<K, V> delegate() {
/*  505 */       return this.delegate;
/*      */     }
/*      */     
/*      */     public void clear() {
/*  509 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public Map<K, Collection<V>> asMap() {
/*  513 */       Map<K, Collection<V>> result = this.map;
/*  514 */       if (result == null) {
/*  515 */         result = this. = Collections.unmodifiableMap(Maps.transformValues(this.delegate.asMap(), new Function()
/*      */         {
/*      */           public Collection<V> apply(Collection<V> collection)
/*      */           {
/*  519 */             return Multimaps.unmodifiableValueCollection(collection);
/*      */           }
/*      */         }));
/*      */       }
/*  523 */       return result;
/*      */     }
/*      */     
/*      */     public Collection<Entry<K, V>> entries() {
/*  527 */       Collection<Entry<K, V>> result = this.entries;
/*  528 */       if (result == null) {
/*  529 */         this.entries = (result = Multimaps.unmodifiableEntries(this.delegate.entries()));
/*      */       }
/*  531 */       return result;
/*      */     }
/*      */     
/*      */     public Collection<V> get(K key) {
/*  535 */       return Multimaps.unmodifiableValueCollection(this.delegate.get(key));
/*      */     }
/*      */     
/*      */     public Multiset<K> keys() {
/*  539 */       Multiset<K> result = this.keys;
/*  540 */       if (result == null) {
/*  541 */         this.keys = (result = Multisets.unmodifiableMultiset(this.delegate.keys()));
/*      */       }
/*  543 */       return result;
/*      */     }
/*      */     
/*      */     public Set<K> keySet() {
/*  547 */       Set<K> result = this.keySet;
/*  548 */       if (result == null) {
/*  549 */         this.keySet = (result = Collections.unmodifiableSet(this.delegate.keySet()));
/*      */       }
/*  551 */       return result;
/*      */     }
/*      */     
/*      */     public boolean put(K key, V value) {
/*  555 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean putAll(K key, Iterable<? extends V> values) {
/*  559 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*      */     {
/*  564 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean remove(Object key, Object value) {
/*  568 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public Collection<V> removeAll(Object key) {
/*  572 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public Collection<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*  577 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public Collection<V> values() {
/*  581 */       Collection<V> result = this.values;
/*  582 */       if (result == null) {
/*  583 */         this.values = (result = Collections.unmodifiableCollection(this.delegate.values()));
/*      */       }
/*  585 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class UnmodifiableListMultimap<K, V> extends UnmodifiableMultimap<K, V> implements ListMultimap<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     UnmodifiableListMultimap(ListMultimap<K, V> delegate) {
/*  594 */       super();
/*      */     }
/*      */     
/*  597 */     public ListMultimap<K, V> delegate() { return (ListMultimap)super.delegate(); }
/*      */     
/*      */     public List<V> get(K key) {
/*  600 */       return Collections.unmodifiableList(delegate().get(key));
/*      */     }
/*      */     
/*  603 */     public List<V> removeAll(Object key) { throw new UnsupportedOperationException(); }
/*      */     
/*      */     public List<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*  607 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */   
/*      */   private static class UnmodifiableSetMultimap<K, V> extends UnmodifiableMultimap<K, V> implements SetMultimap<K, V> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     UnmodifiableSetMultimap(SetMultimap<K, V> delegate) {
/*  615 */       super();
/*      */     }
/*      */     
/*  618 */     public SetMultimap<K, V> delegate() { return (SetMultimap)super.delegate(); }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public Set<V> get(K key)
/*      */     {
/*  625 */       return Collections.unmodifiableSet(delegate().get(key));
/*      */     }
/*      */     
/*  628 */     public Set<Entry<K, V>> entries() { return Maps.unmodifiableEntrySet(delegate().entries()); }
/*      */     
/*      */     public Set<V> removeAll(Object key) {
/*  631 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public Set<V> replaceValues(K key, Iterable<? extends V> values) {
/*  635 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */   
/*      */   private static class UnmodifiableSortedSetMultimap<K, V> extends UnmodifiableSetMultimap<K, V> implements SortedSetMultimap<K, V> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     UnmodifiableSortedSetMultimap(SortedSetMultimap<K, V> delegate) {
/*  643 */       super();
/*      */     }
/*      */     
/*  646 */     public SortedSetMultimap<K, V> delegate() { return (SortedSetMultimap)super.delegate(); }
/*      */     
/*      */     public SortedSet<V> get(K key) {
/*  649 */       return Collections.unmodifiableSortedSet(delegate().get(key));
/*      */     }
/*      */     
/*  652 */     public SortedSet<V> removeAll(Object key) { throw new UnsupportedOperationException(); }
/*      */     
/*      */     public SortedSet<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*  656 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public Comparator<? super V> valueComparator() {
/*  660 */       return delegate().valueComparator();
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
/*      */   public static <K, V> SetMultimap<K, V> synchronizedSetMultimap(SetMultimap<K, V> multimap)
/*      */   {
/*  679 */     return Synchronized.setMultimap(multimap, null);
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
/*      */   public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(SetMultimap<K, V> delegate)
/*      */   {
/*  702 */     if (((delegate instanceof UnmodifiableSetMultimap)) || ((delegate instanceof ImmutableSetMultimap)))
/*      */     {
/*  704 */       return delegate;
/*      */     }
/*  706 */     return new UnmodifiableSetMultimap(delegate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(ImmutableSetMultimap<K, V> delegate)
/*      */   {
/*  717 */     return (SetMultimap)Preconditions.checkNotNull(delegate);
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
/*      */   public static <K, V> SortedSetMultimap<K, V> synchronizedSortedSetMultimap(SortedSetMultimap<K, V> multimap)
/*      */   {
/*  734 */     return Synchronized.sortedSetMultimap(multimap, null);
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
/*      */   public static <K, V> SortedSetMultimap<K, V> unmodifiableSortedSetMultimap(SortedSetMultimap<K, V> delegate)
/*      */   {
/*  757 */     if ((delegate instanceof UnmodifiableSortedSetMultimap)) {
/*  758 */       return delegate;
/*      */     }
/*  760 */     return new UnmodifiableSortedSetMultimap(delegate);
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
/*      */   public static <K, V> ListMultimap<K, V> synchronizedListMultimap(ListMultimap<K, V> multimap)
/*      */   {
/*  774 */     return Synchronized.listMultimap(multimap, null);
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
/*      */   public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ListMultimap<K, V> delegate)
/*      */   {
/*  797 */     if (((delegate instanceof UnmodifiableListMultimap)) || ((delegate instanceof ImmutableListMultimap)))
/*      */     {
/*  799 */       return delegate;
/*      */     }
/*  801 */     return new UnmodifiableListMultimap(delegate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ImmutableListMultimap<K, V> delegate)
/*      */   {
/*  812 */     return (ListMultimap)Preconditions.checkNotNull(delegate);
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
/*      */   private static <V> Collection<V> unmodifiableValueCollection(Collection<V> collection)
/*      */   {
/*  825 */     if ((collection instanceof SortedSet))
/*  826 */       return Collections.unmodifiableSortedSet((SortedSet)collection);
/*  827 */     if ((collection instanceof Set))
/*  828 */       return Collections.unmodifiableSet((Set)collection);
/*  829 */     if ((collection instanceof List)) {
/*  830 */       return Collections.unmodifiableList((List)collection);
/*      */     }
/*  832 */     return Collections.unmodifiableCollection(collection);
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
/*      */   private static <K, V> Collection<Entry<K, V>> unmodifiableEntries(Collection<Entry<K, V>> entries)
/*      */   {
/*  846 */     if ((entries instanceof Set)) {
/*  847 */       return Maps.unmodifiableEntrySet((Set)entries);
/*      */     }
/*  849 */     return new Maps.UnmodifiableEntries(Collections.unmodifiableCollection(entries));
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
/*      */   @Beta
/*      */   public static <K, V> Map<K, List<V>> asMap(ListMultimap<K, V> multimap)
/*      */   {
/*  863 */     return multimap.asMap();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Beta
/*      */   public static <K, V> Map<K, Set<V>> asMap(SetMultimap<K, V> multimap)
/*      */   {
/*  876 */     return multimap.asMap();
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
/*      */   @Beta
/*      */   public static <K, V> Map<K, SortedSet<V>> asMap(SortedSetMultimap<K, V> multimap)
/*      */   {
/*  891 */     return multimap.asMap();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Beta
/*      */   public static <K, V> Map<K, Collection<V>> asMap(Multimap<K, V> multimap)
/*      */   {
/*  902 */     return multimap.asMap();
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
/*      */   public static <K, V> SetMultimap<K, V> forMap(Map<K, V> map)
/*      */   {
/*  923 */     return new MapMultimap(map);
/*      */   }
/*      */   
/*      */   private static class MapMultimap<K, V> extends AbstractMultimap<K, V> implements SetMultimap<K, V>, Serializable
/*      */   {
/*      */     final Map<K, V> map;
/*      */     private static final long serialVersionUID = 7845222491160860175L;
/*      */     
/*      */     MapMultimap(Map<K, V> map) {
/*  932 */       this.map = ((Map)Preconditions.checkNotNull(map));
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/*  937 */       return this.map.size();
/*      */     }
/*      */     
/*      */     public boolean containsKey(Object key)
/*      */     {
/*  942 */       return this.map.containsKey(key);
/*      */     }
/*      */     
/*      */     public boolean containsValue(Object value)
/*      */     {
/*  947 */       return this.map.containsValue(value);
/*      */     }
/*      */     
/*      */     public boolean containsEntry(Object key, Object value)
/*      */     {
/*  952 */       return this.map.entrySet().contains(Maps.immutableEntry(key, value));
/*      */     }
/*      */     
/*      */     public Set<V> get(final K key)
/*      */     {
/*  957 */       new Sets.ImprovedAbstractSet() {
/*      */         public Iterator<V> iterator() {
/*  959 */           new Iterator()
/*      */           {
/*      */             int i;
/*      */             
/*      */             public boolean hasNext() {
/*  964 */               return (this.i == 0) && (MapMultimap.this.map.containsKey(Multimaps.MapMultimap.1.this.val$key));
/*      */             }
/*      */             
/*      */             public V next()
/*      */             {
/*  969 */               if (!hasNext()) {
/*  970 */                 throw new NoSuchElementException();
/*      */               }
/*  972 */               this.i += 1;
/*  973 */               return (V)MapMultimap.this.map.get(Multimaps.MapMultimap.1.this.val$key);
/*      */             }
/*      */             
/*      */             public void remove()
/*      */             {
/*  978 */               CollectPreconditions.checkRemove(this.i == 1);
/*  979 */               this.i = -1;
/*  980 */               MapMultimap.this.map.remove(Multimaps.MapMultimap.1.this.val$key);
/*      */             }
/*      */           };
/*      */         }
/*      */         
/*      */         public int size() {
/*  986 */           return MapMultimap.this.map.containsKey(key) ? 1 : 0;
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*      */     public boolean put(K key, V value)
/*      */     {
/*  993 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean putAll(K key, Iterable<? extends V> values)
/*      */     {
/*  998 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*      */     {
/* 1003 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public Set<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/* 1008 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean remove(Object key, Object value)
/*      */     {
/* 1013 */       return this.map.entrySet().remove(Maps.immutableEntry(key, value));
/*      */     }
/*      */     
/*      */     public Set<V> removeAll(Object key)
/*      */     {
/* 1018 */       Set<V> values = new HashSet(2);
/* 1019 */       if (!this.map.containsKey(key)) {
/* 1020 */         return values;
/*      */       }
/* 1022 */       values.add(this.map.remove(key));
/* 1023 */       return values;
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/* 1028 */       this.map.clear();
/*      */     }
/*      */     
/*      */     public Set<K> keySet()
/*      */     {
/* 1033 */       return this.map.keySet();
/*      */     }
/*      */     
/*      */     public Collection<V> values()
/*      */     {
/* 1038 */       return this.map.values();
/*      */     }
/*      */     
/*      */     public Set<Entry<K, V>> entries()
/*      */     {
/* 1043 */       return this.map.entrySet();
/*      */     }
/*      */     
/*      */     Iterator<Entry<K, V>> entryIterator()
/*      */     {
/* 1048 */       return this.map.entrySet().iterator();
/*      */     }
/*      */     
/*      */     Map<K, Collection<V>> createAsMap()
/*      */     {
/* 1053 */       return new AsMap(this);
/*      */     }
/*      */     
/*      */     public int hashCode() {
/* 1057 */       return this.map.hashCode();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <K, V1, V2> Multimap<K, V2> transformValues(Multimap<K, V1> fromMultimap, Function<? super V1, V2> function)
/*      */   {
/* 1109 */     Preconditions.checkNotNull(function);
/* 1110 */     Maps.EntryTransformer<K, V1, V2> transformer = Maps.asEntryTransformer(function);
/* 1111 */     return transformEntries(fromMultimap, transformer);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <K, V1, V2> Multimap<K, V2> transformEntries(Multimap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */   {
/* 1172 */     return new TransformedEntriesMultimap(fromMap, transformer);
/*      */   }
/*      */   
/*      */   private static class TransformedEntriesMultimap<K, V1, V2> extends AbstractMultimap<K, V2>
/*      */   {
/*      */     final Multimap<K, V1> fromMultimap;
/*      */     final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;
/*      */     
/*      */     TransformedEntriesMultimap(Multimap<K, V1> fromMultimap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */     {
/* 1182 */       this.fromMultimap = ((Multimap)Preconditions.checkNotNull(fromMultimap));
/* 1183 */       this.transformer = ((Maps.EntryTransformer)Preconditions.checkNotNull(transformer));
/*      */     }
/*      */     
/*      */     Collection<V2> transform(K key, Collection<V1> values) {
/* 1187 */       Function<? super V1, V2> function = Maps.asValueToValueFunction(this.transformer, key);
/*      */       
/* 1189 */       if ((values instanceof List)) {
/* 1190 */         return Lists.transform((List)values, function);
/*      */       }
/* 1192 */       return Collections2.transform(values, function);
/*      */     }
/*      */     
/*      */ 
/*      */     Map<K, Collection<V2>> createAsMap()
/*      */     {
/* 1198 */       Maps.transformEntries(this.fromMultimap.asMap(), new Maps.EntryTransformer()
/*      */       {
/*      */         public Collection<V2> transformEntry(K key, Collection<V1> value)
/*      */         {
/* 1202 */           return TransformedEntriesMultimap.this.transform(key, value);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */     public void clear() {
/* 1208 */       this.fromMultimap.clear();
/*      */     }
/*      */     
/*      */     public boolean containsKey(Object key) {
/* 1212 */       return this.fromMultimap.containsKey(key);
/*      */     }
/*      */     
/*      */     Iterator<Entry<K, V2>> entryIterator()
/*      */     {
/* 1217 */       return Iterators.transform(this.fromMultimap.entries().iterator(), Maps.asEntryToEntryFunction(this.transformer));
/*      */     }
/*      */     
/*      */     public Collection<V2> get(K key)
/*      */     {
/* 1222 */       return transform(key, this.fromMultimap.get(key));
/*      */     }
/*      */     
/*      */     public boolean isEmpty() {
/* 1226 */       return this.fromMultimap.isEmpty();
/*      */     }
/*      */     
/*      */     public Set<K> keySet() {
/* 1230 */       return this.fromMultimap.keySet();
/*      */     }
/*      */     
/*      */     public Multiset<K> keys() {
/* 1234 */       return this.fromMultimap.keys();
/*      */     }
/*      */     
/*      */     public boolean put(K key, V2 value) {
/* 1238 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean putAll(K key, Iterable<? extends V2> values) {
/* 1242 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean putAll(Multimap<? extends K, ? extends V2> multimap)
/*      */     {
/* 1247 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public boolean remove(Object key, Object value)
/*      */     {
/* 1252 */       return get(key).remove(value);
/*      */     }
/*      */     
/*      */     public Collection<V2> removeAll(Object key)
/*      */     {
/* 1257 */       return transform(key, this.fromMultimap.removeAll(key));
/*      */     }
/*      */     
/*      */     public Collection<V2> replaceValues(K key, Iterable<? extends V2> values)
/*      */     {
/* 1262 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public int size() {
/* 1266 */       return this.fromMultimap.size();
/*      */     }
/*      */     
/*      */     Collection<V2> createValues()
/*      */     {
/* 1271 */       return Collections2.transform(this.fromMultimap.entries(), Maps.asEntryToValueFunction(this.transformer));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <K, V1, V2> ListMultimap<K, V2> transformValues(ListMultimap<K, V1> fromMultimap, Function<? super V1, V2> function)
/*      */   {
/* 1320 */     Preconditions.checkNotNull(function);
/* 1321 */     Maps.EntryTransformer<K, V1, V2> transformer = Maps.asEntryTransformer(function);
/* 1322 */     return transformEntries(fromMultimap, transformer);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <K, V1, V2> ListMultimap<K, V2> transformEntries(ListMultimap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */   {
/* 1380 */     return new TransformedEntriesListMultimap(fromMap, transformer);
/*      */   }
/*      */   
/*      */   private static final class TransformedEntriesListMultimap<K, V1, V2>
/*      */     extends TransformedEntriesMultimap<K, V1, V2>
/*      */     implements ListMultimap<K, V2>
/*      */   {
/*      */     TransformedEntriesListMultimap(ListMultimap<K, V1> fromMultimap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */     {
/* 1389 */       super(transformer);
/*      */     }
/*      */     
/*      */     List<V2> transform(K key, Collection<V1> values) {
/* 1393 */       return Lists.transform((List)values, Maps.asValueToValueFunction(this.transformer, key));
/*      */     }
/*      */     
/*      */     public List<V2> get(K key) {
/* 1397 */       return transform(key, this.fromMultimap.get(key));
/*      */     }
/*      */     
/*      */     public List<V2> removeAll(Object key)
/*      */     {
/* 1402 */       return transform(key, this.fromMultimap.removeAll(key));
/*      */     }
/*      */     
/*      */     public List<V2> replaceValues(K key, Iterable<? extends V2> values)
/*      */     {
/* 1407 */       throw new UnsupportedOperationException();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <K, V> ImmutableListMultimap<K, V> index(Iterable<V> values, Function<? super V, K> keyFunction)
/*      */   {
/* 1455 */     return index(values.iterator(), keyFunction);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <K, V> ImmutableListMultimap<K, V> index(Iterator<V> values, Function<? super V, K> keyFunction)
/*      */   {
/* 1503 */     Preconditions.checkNotNull(keyFunction);
/* 1504 */     ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();
/*      */     
/* 1506 */     while (values.hasNext()) {
/* 1507 */       V value = values.next();
/* 1508 */       Preconditions.checkNotNull(value, values);
/* 1509 */       builder.put(keyFunction.apply(value), value);
/*      */     }
/* 1511 */     return builder.build();
/*      */   }
/*      */   
/*      */   static class Keys<K, V> extends AbstractMultiset<K> {
/*      */     final Multimap<K, V> multimap;
/*      */     
/*      */     Keys(Multimap<K, V> multimap) {
/* 1518 */       this.multimap = multimap;
/*      */     }
/*      */     
/*      */     Iterator<Multiset.Entry<K>> entryIterator() {
/* 1522 */       new TransformedIterator(this.multimap.asMap().entrySet().iterator())
/*      */       {
/*      */ 
/*      */         Multiset.Entry<K> transform(final Entry<K, Collection<V>> backingEntry)
/*      */         {
/* 1527 */           new Multisets.AbstractEntry()
/*      */           {
/*      */             public K getElement() {
/* 1530 */               return (K)backingEntry.getKey();
/*      */             }
/*      */             
/*      */             public int getCount()
/*      */             {
/* 1535 */               return ((Collection)backingEntry.getValue()).size();
/*      */             }
/*      */           };
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*      */     int distinctElements() {
/* 1543 */       return this.multimap.asMap().size();
/*      */     }
/*      */     
/*      */ 
/* 1547 */     Set<Multiset.Entry<K>> createEntrySet() { return new KeysEntrySet(); }
/*      */     
/*      */     class KeysEntrySet extends Multisets.EntrySet<K> {
/*      */       KeysEntrySet() {}
/*      */       
/* 1552 */       Multiset<K> multiset() { return Keys.this; }
/*      */       
/*      */       public Iterator<Multiset.Entry<K>> iterator()
/*      */       {
/* 1556 */         return Keys.this.entryIterator();
/*      */       }
/*      */       
/*      */       public int size() {
/* 1560 */         return Keys.this.distinctElements();
/*      */       }
/*      */       
/*      */       public boolean isEmpty() {
/* 1564 */         return Keys.this.multimap.isEmpty();
/*      */       }
/*      */       
/*      */       public boolean contains(@Nullable Object o) {
/* 1568 */         if ((o instanceof Multiset.Entry)) {
/* 1569 */           Multiset.Entry<?> entry = (Multiset.Entry)o;
/* 1570 */           Collection<V> collection = (Collection)Keys.this.multimap.asMap().get(entry.getElement());
/* 1571 */           return (collection != null) && (collection.size() == entry.getCount());
/*      */         }
/* 1573 */         return false;
/*      */       }
/*      */       
/*      */       public boolean remove(@Nullable Object o) {
/* 1577 */         if ((o instanceof Multiset.Entry)) {
/* 1578 */           Multiset.Entry<?> entry = (Multiset.Entry)o;
/* 1579 */           Collection<V> collection = (Collection)Keys.this.multimap.asMap().get(entry.getElement());
/* 1580 */           if ((collection != null) && (collection.size() == entry.getCount())) {
/* 1581 */             collection.clear();
/* 1582 */             return true;
/*      */           }
/*      */         }
/* 1585 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean contains(@Nullable Object element) {
/* 1590 */       return this.multimap.containsKey(element);
/*      */     }
/*      */     
/*      */     public Iterator<K> iterator() {
/* 1594 */       return Maps.keyIterator(this.multimap.entries().iterator());
/*      */     }
/*      */     
/*      */     public int count(@Nullable Object element) {
/* 1598 */       Collection<V> values = (Collection)Maps.safeGet(this.multimap.asMap(), element);
/* 1599 */       return values == null ? 0 : values.size();
/*      */     }
/*      */     
/*      */     public int remove(@Nullable Object element, int occurrences) {
/* 1603 */       CollectPreconditions.checkNonnegative(occurrences, "occurrences");
/* 1604 */       if (occurrences == 0) {
/* 1605 */         return count(element);
/*      */       }
/*      */       
/* 1608 */       Collection<V> values = (Collection)Maps.safeGet(this.multimap.asMap(), element);
/*      */       
/* 1610 */       if (values == null) {
/* 1611 */         return 0;
/*      */       }
/*      */       
/* 1614 */       int oldCount = values.size();
/* 1615 */       if (occurrences >= oldCount) {
/* 1616 */         values.clear();
/*      */       } else {
/* 1618 */         Iterator<V> iterator = values.iterator();
/* 1619 */         for (int i = 0; i < occurrences; i++) {
/* 1620 */           iterator.next();
/* 1621 */           iterator.remove();
/*      */         }
/*      */       }
/* 1624 */       return oldCount;
/*      */     }
/*      */     
/*      */     public void clear() {
/* 1628 */       this.multimap.clear();
/*      */     }
/*      */     
/*      */     public Set<K> elementSet() {
/* 1632 */       return this.multimap.keySet();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static abstract class Entries<K, V>
/*      */     extends AbstractCollection<Entry<K, V>>
/*      */   {
/*      */     abstract Multimap<K, V> multimap();
/*      */     
/*      */     public int size()
/*      */     {
/* 1644 */       return multimap().size();
/*      */     }
/*      */     
/*      */     public boolean contains(@Nullable Object o) {
/* 1648 */       if ((o instanceof Map.Entry)) {
/* 1649 */         Entry<?, ?> entry = (Entry)o;
/* 1650 */         return multimap().containsEntry(entry.getKey(), entry.getValue());
/*      */       }
/* 1652 */       return false;
/*      */     }
/*      */     
/*      */     public boolean remove(@Nullable Object o) {
/* 1656 */       if ((o instanceof Map.Entry)) {
/* 1657 */         Entry<?, ?> entry = (Entry)o;
/* 1658 */         return multimap().remove(entry.getKey(), entry.getValue());
/*      */       }
/* 1660 */       return false;
/*      */     }
/*      */     
/*      */     public void clear() {
/* 1664 */       multimap().clear();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static final class AsMap<K, V>
/*      */     extends Maps.ImprovedAbstractMap<K, Collection<V>>
/*      */   {
/*      */     private final Multimap<K, V> multimap;
/*      */     
/*      */     AsMap(Multimap<K, V> multimap)
/*      */     {
/* 1676 */       this.multimap = ((Multimap)Preconditions.checkNotNull(multimap));
/*      */     }
/*      */     
/*      */     public int size() {
/* 1680 */       return this.multimap.keySet().size();
/*      */     }
/*      */     
/*      */     protected Set<Entry<K, Collection<V>>> createEntrySet() {
/* 1684 */       return new EntrySet();
/*      */     }
/*      */     
/*      */ 
/* 1688 */     void removeValuesForKey(Object key) { this.multimap.keySet().remove(key); }
/*      */     
/*      */     class EntrySet extends Maps.EntrySet<K, Collection<V>> {
/*      */       EntrySet() {}
/*      */       
/* 1693 */       Map<K, Collection<V>> map() { return AsMap.this; }
/*      */       
/*      */       public Iterator<Entry<K, Collection<V>>> iterator()
/*      */       {
/* 1697 */         Maps.asMapEntryIterator(AsMap.this.multimap.keySet(), new Function()
/*      */         {
/*      */           public Collection<V> apply(K key) {
/* 1700 */             return AsMap.this.multimap.get(key);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */       public boolean remove(Object o) {
/* 1706 */         if (!contains(o)) {
/* 1707 */           return false;
/*      */         }
/* 1709 */         Entry<?, ?> entry = (Entry)o;
/* 1710 */         AsMap.this.removeValuesForKey(entry.getKey());
/* 1711 */         return true;
/*      */       }
/*      */     }
/*      */     
/*      */     public Collection<V> get(Object key)
/*      */     {
/* 1717 */       return containsKey(key) ? this.multimap.get(key) : null;
/*      */     }
/*      */     
/*      */     public Collection<V> remove(Object key) {
/* 1721 */       return containsKey(key) ? this.multimap.removeAll(key) : null;
/*      */     }
/*      */     
/*      */     public Set<K> keySet() {
/* 1725 */       return this.multimap.keySet();
/*      */     }
/*      */     
/*      */     public boolean isEmpty() {
/* 1729 */       return this.multimap.isEmpty();
/*      */     }
/*      */     
/*      */     public boolean containsKey(Object key) {
/* 1733 */       return this.multimap.containsKey(key);
/*      */     }
/*      */     
/*      */     public void clear() {
/* 1737 */       this.multimap.clear();
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
/*      */   public static <K, V> Multimap<K, V> filterKeys(Multimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*      */   {
/* 1773 */     if ((unfiltered instanceof SetMultimap))
/* 1774 */       return filterKeys((SetMultimap)unfiltered, keyPredicate);
/* 1775 */     if ((unfiltered instanceof ListMultimap))
/* 1776 */       return filterKeys((ListMultimap)unfiltered, keyPredicate);
/* 1777 */     if ((unfiltered instanceof FilteredKeyMultimap)) {
/* 1778 */       FilteredKeyMultimap<K, V> prev = (FilteredKeyMultimap)unfiltered;
/* 1779 */       return new FilteredKeyMultimap(prev.unfiltered, Predicates.and(prev.keyPredicate, keyPredicate));
/*      */     }
/* 1781 */     if ((unfiltered instanceof FilteredMultimap)) {
/* 1782 */       FilteredMultimap<K, V> prev = (FilteredMultimap)unfiltered;
/* 1783 */       return filterFiltered(prev, Maps.keyPredicateOnEntries(keyPredicate));
/*      */     }
/* 1785 */     return new FilteredKeyMultimap(unfiltered, keyPredicate);
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
/*      */   public static <K, V> SetMultimap<K, V> filterKeys(SetMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*      */   {
/* 1821 */     if ((unfiltered instanceof FilteredKeySetMultimap)) {
/* 1822 */       FilteredKeySetMultimap<K, V> prev = (FilteredKeySetMultimap)unfiltered;
/* 1823 */       return new FilteredKeySetMultimap(prev.unfiltered(), Predicates.and(prev.keyPredicate, keyPredicate));
/*      */     }
/* 1825 */     if ((unfiltered instanceof FilteredSetMultimap)) {
/* 1826 */       FilteredSetMultimap<K, V> prev = (FilteredSetMultimap)unfiltered;
/* 1827 */       return filterFiltered(prev, Maps.keyPredicateOnEntries(keyPredicate));
/*      */     }
/* 1829 */     return new FilteredKeySetMultimap(unfiltered, keyPredicate);
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
/*      */   public static <K, V> ListMultimap<K, V> filterKeys(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*      */   {
/* 1865 */     if ((unfiltered instanceof FilteredKeyListMultimap)) {
/* 1866 */       FilteredKeyListMultimap<K, V> prev = (FilteredKeyListMultimap)unfiltered;
/* 1867 */       return new FilteredKeyListMultimap(prev.unfiltered(), Predicates.and(prev.keyPredicate, keyPredicate));
/*      */     }
/*      */     
/* 1870 */     return new FilteredKeyListMultimap(unfiltered, keyPredicate);
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
/*      */   public static <K, V> Multimap<K, V> filterValues(Multimap<K, V> unfiltered, Predicate<? super V> valuePredicate)
/*      */   {
/* 1906 */     return filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
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
/*      */   public static <K, V> SetMultimap<K, V> filterValues(SetMultimap<K, V> unfiltered, Predicate<? super V> valuePredicate)
/*      */   {
/* 1941 */     return filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
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
/*      */   public static <K, V> Multimap<K, V> filterEntries(Multimap<K, V> unfiltered, Predicate<? super Entry<K, V>> entryPredicate)
/*      */   {
/* 1974 */     Preconditions.checkNotNull(entryPredicate);
/* 1975 */     if ((unfiltered instanceof SetMultimap)) {
/* 1976 */       return filterEntries((SetMultimap)unfiltered, entryPredicate);
/*      */     }
/* 1978 */     return (unfiltered instanceof FilteredMultimap) ? filterFiltered((FilteredMultimap)unfiltered, entryPredicate) : new FilteredEntryMultimap((Multimap)Preconditions.checkNotNull(unfiltered), entryPredicate);
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
/*      */   public static <K, V> SetMultimap<K, V> filterEntries(SetMultimap<K, V> unfiltered, Predicate<? super Entry<K, V>> entryPredicate)
/*      */   {
/* 2013 */     Preconditions.checkNotNull(entryPredicate);
/* 2014 */     return (unfiltered instanceof FilteredSetMultimap) ? filterFiltered((FilteredSetMultimap)unfiltered, entryPredicate) : new FilteredEntrySetMultimap((SetMultimap)Preconditions.checkNotNull(unfiltered), entryPredicate);
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
/*      */   private static <K, V> Multimap<K, V> filterFiltered(FilteredMultimap<K, V> multimap, Predicate<? super Entry<K, V>> entryPredicate)
/*      */   {
/* 2028 */     Predicate<Entry<K, V>> predicate = Predicates.and(multimap.entryPredicate(), entryPredicate);
/*      */     
/* 2030 */     return new FilteredEntryMultimap(multimap.unfiltered(), predicate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static <K, V> SetMultimap<K, V> filterFiltered(FilteredSetMultimap<K, V> multimap, Predicate<? super Entry<K, V>> entryPredicate)
/*      */   {
/* 2042 */     Predicate<Entry<K, V>> predicate = Predicates.and(multimap.entryPredicate(), entryPredicate);
/*      */     
/* 2044 */     return new FilteredEntrySetMultimap(multimap.unfiltered(), predicate);
/*      */   }
/*      */   
/*      */   static boolean equalsImpl(Multimap<?, ?> multimap, @Nullable Object object) {
/* 2048 */     if (object == multimap) {
/* 2049 */       return true;
/*      */     }
/* 2051 */     if ((object instanceof Multimap)) {
/* 2052 */       Multimap<?, ?> that = (Multimap)object;
/* 2053 */       return multimap.asMap().equals(that.asMap());
/*      */     }
/* 2055 */     return false;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\Multimaps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */