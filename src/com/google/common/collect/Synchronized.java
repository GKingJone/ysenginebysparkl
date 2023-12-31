/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.annotations.VisibleForTesting;
/*      */ import com.google.common.base.Preconditions;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Collection;
/*      */ import java.util.Deque;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NavigableMap;
/*      */ import java.util.NavigableSet;
/*      */ import java.util.Queue;
/*      */ import java.util.RandomAccess;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
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
/*      */ @GwtCompatible(emulated=true)
/*      */ final class Synchronized
/*      */ {
/*      */   static class SynchronizedObject
/*      */     implements Serializable
/*      */   {
/*      */     final Object delegate;
/*      */     final Object mutex;
/*      */     @GwtIncompatible("not needed in emulated source")
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedObject(Object delegate, @Nullable Object mutex)
/*      */     {
/*   68 */       this.delegate = Preconditions.checkNotNull(delegate);
/*   69 */       this.mutex = (mutex == null ? this : mutex);
/*      */     }
/*      */     
/*      */     Object delegate() {
/*   73 */       return this.delegate;
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public String toString()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedObject:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: getfield 3	com/google/common/collect/Synchronized$SynchronizedObject:delegate	Ljava/lang/Object;
/*      */       //   11: invokevirtual 5	java/lang/Object:toString	()Ljava/lang/String;
/*      */       //   14: aload_1
/*      */       //   15: monitorexit
/*      */       //   16: areturn
/*      */       //   17: astore_2
/*      */       //   18: aload_1
/*      */       //   19: monitorexit
/*      */       //   20: aload_2
/*      */       //   21: athrow
/*      */       // Line number table:
/*      */       //   Java source line #79	-> byte code offset #0
/*      */       //   Java source line #80	-> byte code offset #7
/*      */       //   Java source line #81	-> byte code offset #17
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	22	0	this	SynchronizedObject
/*      */       //   5	14	1	Ljava/lang/Object;	Object
/*      */       //   17	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	16	17	finally
/*      */       //   17	20	17	finally
/*      */     }
/*      */     
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream)
/*      */       throws IOException
/*      */     {
/*   91 */       synchronized (this.mutex) {
/*   92 */         stream.defaultWriteObject();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static <E> Collection<E> collection(Collection<E> collection, @Nullable Object mutex)
/*      */   {
/*  102 */     return new SynchronizedCollection(collection, mutex, null);
/*      */   }
/*      */   
/*      */   @VisibleForTesting
/*      */   static class SynchronizedCollection<E> extends SynchronizedObject implements Collection<E> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*  109 */     private SynchronizedCollection(Collection<E> delegate, @Nullable Object mutex) { super(mutex); }
/*      */     
/*      */ 
/*      */     Collection<E> delegate()
/*      */     {
/*  114 */       return (Collection)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean add(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 7 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #119	-> byte code offset #0
/*      */       //   Java source line #120	-> byte code offset #7
/*      */       //   Java source line #121	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean addAll(Collection<? extends E> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #126	-> byte code offset #0
/*      */       //   Java source line #127	-> byte code offset #7
/*      */       //   Java source line #128	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	c	Collection<? extends E>
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/*  133 */       synchronized (this.mutex) {
/*  134 */         delegate().clear();
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean contains(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 10 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #140	-> byte code offset #0
/*      */       //   Java source line #141	-> byte code offset #7
/*      */       //   Java source line #142	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	o	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean containsAll(Collection<?> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 11 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #147	-> byte code offset #0
/*      */       //   Java source line #148	-> byte code offset #7
/*      */       //   Java source line #149	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	c	Collection<?>
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean isEmpty()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: invokeinterface 12 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #154	-> byte code offset #0
/*      */       //   Java source line #155	-> byte code offset #7
/*      */       //   Java source line #156	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedCollection<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     public Iterator<E> iterator()
/*      */     {
/*  161 */       return delegate().iterator();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean remove(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 14 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #166	-> byte code offset #0
/*      */       //   Java source line #167	-> byte code offset #7
/*      */       //   Java source line #168	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	o	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean removeAll(Collection<?> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 15 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #173	-> byte code offset #0
/*      */       //   Java source line #174	-> byte code offset #7
/*      */       //   Java source line #175	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	c	Collection<?>
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean retainAll(Collection<?> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 16 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #180	-> byte code offset #0
/*      */       //   Java source line #181	-> byte code offset #7
/*      */       //   Java source line #182	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	c	Collection<?>
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int size()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: invokeinterface 17 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #187	-> byte code offset #0
/*      */       //   Java source line #188	-> byte code offset #7
/*      */       //   Java source line #189	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedCollection<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Object[] toArray()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: invokeinterface 18 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #194	-> byte code offset #0
/*      */       //   Java source line #195	-> byte code offset #7
/*      */       //   Java source line #196	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedCollection<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public <T> T[] toArray(T[] a)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 19 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #201	-> byte code offset #0
/*      */       //   Java source line #202	-> byte code offset #7
/*      */       //   Java source line #203	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedCollection<E>
/*      */       //   0	25	1	a	T[]
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */   }
/*      */   
/*      */   @VisibleForTesting
/*      */   static <E> Set<E> set(Set<E> set, @Nullable Object mutex)
/*      */   {
/*  210 */     return new SynchronizedSet(set, mutex);
/*      */   }
/*      */   
/*      */   static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedSet(Set<E> delegate, @Nullable Object mutex) {
/*  217 */       super(mutex, null);
/*      */     }
/*      */     
/*      */     Set<E> delegate() {
/*  221 */       return (Set)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean equals(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_1
/*      */       //   1: aload_0
/*      */       //   2: if_acmpne +5 -> 7
/*      */       //   5: iconst_1
/*      */       //   6: ireturn
/*      */       //   7: aload_0
/*      */       //   8: getfield 4	com/google/common/collect/Synchronized$SynchronizedSet:mutex	Ljava/lang/Object;
/*      */       //   11: dup
/*      */       //   12: astore_2
/*      */       //   13: monitorenter
/*      */       //   14: aload_0
/*      */       //   15: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSet:delegate	()Ljava/util/Set;
/*      */       //   18: aload_1
/*      */       //   19: invokeinterface 6 2 0
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: ireturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #225	-> byte code offset #0
/*      */       //   Java source line #226	-> byte code offset #5
/*      */       //   Java source line #228	-> byte code offset #7
/*      */       //   Java source line #229	-> byte code offset #14
/*      */       //   Java source line #230	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedSet<E>
/*      */       //   0	32	1	o	Object
/*      */       //   12	17	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   14	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int hashCode()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSet:delegate	()Ljava/util/Set;
/*      */       //   11: invokeinterface 7 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #234	-> byte code offset #0
/*      */       //   Java source line #235	-> byte code offset #7
/*      */       //   Java source line #236	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSet<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   private static <E> SortedSet<E> sortedSet(SortedSet<E> set, @Nullable Object mutex)
/*      */   {
/*  244 */     return new SynchronizedSortedSet(set, mutex);
/*      */   }
/*      */   
/*      */   static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*  250 */     SynchronizedSortedSet(SortedSet<E> delegate, @Nullable Object mutex) { super(mutex); }
/*      */     
/*      */     SortedSet<E> delegate()
/*      */     {
/*  254 */       return (SortedSet)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public java.util.Comparator<? super E> comparator()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
/*      */       //   11: invokeinterface 6 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #259	-> byte code offset #0
/*      */       //   Java source line #260	-> byte code offset #7
/*      */       //   Java source line #261	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSortedSet<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedSet<E> subSet(E fromElement, E toElement)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 7 3 0
/*      */       //   18: aload_0
/*      */       //   19: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   22: invokestatic 8	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
/*      */       //   25: aload_3
/*      */       //   26: monitorexit
/*      */       //   27: areturn
/*      */       //   28: astore 4
/*      */       //   30: aload_3
/*      */       //   31: monitorexit
/*      */       //   32: aload 4
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #266	-> byte code offset #0
/*      */       //   Java source line #267	-> byte code offset #7
/*      */       //   Java source line #268	-> byte code offset #28
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	SynchronizedSortedSet<E>
/*      */       //   0	35	1	fromElement	E
/*      */       //   0	35	2	toElement	E
/*      */       //   5	26	3	Ljava/lang/Object;	Object
/*      */       //   28	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	27	28	finally
/*      */       //   28	32	28	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedSet<E> headSet(E toElement)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 9 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 8	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #273	-> byte code offset #0
/*      */       //   Java source line #274	-> byte code offset #7
/*      */       //   Java source line #275	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedSortedSet<E>
/*      */       //   0	32	1	toElement	E
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedSet<E> tailSet(E fromElement)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 10 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 8	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #280	-> byte code offset #0
/*      */       //   Java source line #281	-> byte code offset #7
/*      */       //   Java source line #282	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedSortedSet<E>
/*      */       //   0	32	1	fromElement	E
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E first()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
/*      */       //   11: invokeinterface 11 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #287	-> byte code offset #0
/*      */       //   Java source line #288	-> byte code offset #7
/*      */       //   Java source line #289	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSortedSet<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E last()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
/*      */       //   11: invokeinterface 12 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #294	-> byte code offset #0
/*      */       //   Java source line #295	-> byte code offset #7
/*      */       //   Java source line #296	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSortedSet<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   private static <E> List<E> list(List<E> list, @Nullable Object mutex)
/*      */   {
/*  303 */     return (list instanceof RandomAccess) ? new SynchronizedRandomAccessList(list, mutex) : new SynchronizedList(list, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedList(List<E> delegate, @Nullable Object mutex) {
/*  311 */       super(mutex, null);
/*      */     }
/*      */     
/*      */     List<E> delegate() {
/*  315 */       return (List)super.delegate();
/*      */     }
/*      */     
/*      */     public void add(int index, E element)
/*      */     {
/*  320 */       synchronized (this.mutex) {
/*  321 */         delegate().add(index, element);
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean addAll(int index, Collection<? extends E> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: iload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 7 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #327	-> byte code offset #0
/*      */       //   Java source line #328	-> byte code offset #7
/*      */       //   Java source line #329	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedList<E>
/*      */       //   0	28	1	index	int
/*      */       //   0	28	2	c	Collection<? extends E>
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E get(int index)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: iload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #334	-> byte code offset #0
/*      */       //   Java source line #335	-> byte code offset #7
/*      */       //   Java source line #336	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedList<E>
/*      */       //   0	25	1	index	int
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int indexOf(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 9 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #341	-> byte code offset #0
/*      */       //   Java source line #342	-> byte code offset #7
/*      */       //   Java source line #343	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedList<E>
/*      */       //   0	25	1	o	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int lastIndexOf(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 10 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #348	-> byte code offset #0
/*      */       //   Java source line #349	-> byte code offset #7
/*      */       //   Java source line #350	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedList<E>
/*      */       //   0	25	1	o	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public ListIterator<E> listIterator()
/*      */     {
/*  355 */       return delegate().listIterator();
/*      */     }
/*      */     
/*      */     public ListIterator<E> listIterator(int index)
/*      */     {
/*  360 */       return delegate().listIterator(index);
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E remove(int index)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: iload_1
/*      */       //   12: invokeinterface 13 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #365	-> byte code offset #0
/*      */       //   Java source line #366	-> byte code offset #7
/*      */       //   Java source line #367	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedList<E>
/*      */       //   0	25	1	index	int
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E set(int index, E element)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: iload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 14 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #372	-> byte code offset #0
/*      */       //   Java source line #373	-> byte code offset #7
/*      */       //   Java source line #374	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedList<E>
/*      */       //   0	28	1	index	int
/*      */       //   0	28	2	element	E
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public List<E> subList(int fromIndex, int toIndex)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: iload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 15 3 0
/*      */       //   18: aload_0
/*      */       //   19: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   22: invokestatic 16	com/google/common/collect/Synchronized:access$200	(Ljava/util/List;Ljava/lang/Object;)Ljava/util/List;
/*      */       //   25: aload_3
/*      */       //   26: monitorexit
/*      */       //   27: areturn
/*      */       //   28: astore 4
/*      */       //   30: aload_3
/*      */       //   31: monitorexit
/*      */       //   32: aload 4
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #379	-> byte code offset #0
/*      */       //   Java source line #380	-> byte code offset #7
/*      */       //   Java source line #381	-> byte code offset #28
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	SynchronizedList<E>
/*      */       //   0	35	1	fromIndex	int
/*      */       //   0	35	2	toIndex	int
/*      */       //   5	26	3	Ljava/lang/Object;	Object
/*      */       //   28	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	27	28	finally
/*      */       //   28	32	28	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean equals(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_1
/*      */       //   1: aload_0
/*      */       //   2: if_acmpne +5 -> 7
/*      */       //   5: iconst_1
/*      */       //   6: ireturn
/*      */       //   7: aload_0
/*      */       //   8: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   11: dup
/*      */       //   12: astore_2
/*      */       //   13: monitorenter
/*      */       //   14: aload_0
/*      */       //   15: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   18: aload_1
/*      */       //   19: invokeinterface 17 2 0
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: ireturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #385	-> byte code offset #0
/*      */       //   Java source line #386	-> byte code offset #5
/*      */       //   Java source line #388	-> byte code offset #7
/*      */       //   Java source line #389	-> byte code offset #14
/*      */       //   Java source line #390	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedList<E>
/*      */       //   0	32	1	o	Object
/*      */       //   12	17	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   14	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int hashCode()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
/*      */       //   11: invokeinterface 18 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #394	-> byte code offset #0
/*      */       //   Java source line #395	-> byte code offset #7
/*      */       //   Java source line #396	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedList<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   private static class SynchronizedRandomAccessList<E>
/*      */     extends SynchronizedList<E>
/*      */     implements RandomAccess
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedRandomAccessList(List<E> list, @Nullable Object mutex)
/*      */     {
/*  405 */       super(mutex);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static <E> Multiset<E> multiset(Multiset<E> multiset, @Nullable Object mutex)
/*      */   {
/*  412 */     if (((multiset instanceof SynchronizedMultiset)) || ((multiset instanceof ImmutableMultiset)))
/*      */     {
/*  414 */       return multiset;
/*      */     }
/*  416 */     return new SynchronizedMultiset(multiset, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedMultiset<E> extends SynchronizedCollection<E> implements Multiset<E> {
/*      */     transient Set<E> elementSet;
/*      */     transient Set<Entry<E>> entrySet;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedMultiset(Multiset<E> delegate, @Nullable Object mutex) {
/*  425 */       super(mutex, null);
/*      */     }
/*      */     
/*      */     Multiset<E> delegate() {
/*  429 */       return (Multiset)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int count(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 6 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #434	-> byte code offset #0
/*      */       //   Java source line #435	-> byte code offset #7
/*      */       //   Java source line #436	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMultiset<E>
/*      */       //   0	25	1	o	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int add(E e, int n)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
/*      */       //   11: aload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 7 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #441	-> byte code offset #0
/*      */       //   Java source line #442	-> byte code offset #7
/*      */       //   Java source line #443	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultiset<E>
/*      */       //   0	28	1	e	E
/*      */       //   0	28	2	n	int
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int remove(Object o, int n)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
/*      */       //   11: aload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 8 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #448	-> byte code offset #0
/*      */       //   Java source line #449	-> byte code offset #7
/*      */       //   Java source line #450	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultiset<E>
/*      */       //   0	28	1	o	Object
/*      */       //   0	28	2	n	int
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int setCount(E element, int count)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
/*      */       //   11: aload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 9 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #455	-> byte code offset #0
/*      */       //   Java source line #456	-> byte code offset #7
/*      */       //   Java source line #457	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultiset<E>
/*      */       //   0	28	1	element	E
/*      */       //   0	28	2	count	int
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean setCount(E element, int oldCount, int newCount)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore 4
/*      */       //   7: monitorenter
/*      */       //   8: aload_0
/*      */       //   9: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
/*      */       //   12: aload_1
/*      */       //   13: iload_2
/*      */       //   14: iload_3
/*      */       //   15: invokeinterface 10 4 0
/*      */       //   20: aload 4
/*      */       //   22: monitorexit
/*      */       //   23: ireturn
/*      */       //   24: astore 5
/*      */       //   26: aload 4
/*      */       //   28: monitorexit
/*      */       //   29: aload 5
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #462	-> byte code offset #0
/*      */       //   Java source line #463	-> byte code offset #8
/*      */       //   Java source line #464	-> byte code offset #24
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedMultiset<E>
/*      */       //   0	32	1	element	E
/*      */       //   0	32	2	oldCount	int
/*      */       //   0	32	3	newCount	int
/*      */       //   5	22	4	Ljava/lang/Object;	Object
/*      */       //   24	6	5	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   8	23	24	finally
/*      */       //   24	29	24	finally
/*      */     }
/*      */     
/*      */     public Set<E> elementSet()
/*      */     {
/*  469 */       synchronized (this.mutex) {
/*  470 */         if (this.elementSet == null) {
/*  471 */           this.elementSet = Synchronized.typePreservingSet(delegate().elementSet(), this.mutex);
/*      */         }
/*  473 */         return this.elementSet;
/*      */       }
/*      */     }
/*      */     
/*      */     public Set<Entry<E>> entrySet()
/*      */     {
/*  479 */       synchronized (this.mutex) {
/*  480 */         if (this.entrySet == null) {
/*  481 */           this.entrySet = Synchronized.typePreservingSet(delegate().entrySet(), this.mutex);
/*      */         }
/*  483 */         return this.entrySet;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean equals(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_1
/*      */       //   1: aload_0
/*      */       //   2: if_acmpne +5 -> 7
/*      */       //   5: iconst_1
/*      */       //   6: ireturn
/*      */       //   7: aload_0
/*      */       //   8: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
/*      */       //   11: dup
/*      */       //   12: astore_2
/*      */       //   13: monitorenter
/*      */       //   14: aload_0
/*      */       //   15: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
/*      */       //   18: aload_1
/*      */       //   19: invokeinterface 16 2 0
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: ireturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #488	-> byte code offset #0
/*      */       //   Java source line #489	-> byte code offset #5
/*      */       //   Java source line #491	-> byte code offset #7
/*      */       //   Java source line #492	-> byte code offset #14
/*      */       //   Java source line #493	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedMultiset<E>
/*      */       //   0	32	1	o	Object
/*      */       //   12	17	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   14	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int hashCode()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
/*      */       //   11: invokeinterface 17 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #497	-> byte code offset #0
/*      */       //   Java source line #498	-> byte code offset #7
/*      */       //   Java source line #499	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedMultiset<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   static <K, V> Multimap<K, V> multimap(Multimap<K, V> multimap, @Nullable Object mutex)
/*      */   {
/*  507 */     if (((multimap instanceof SynchronizedMultimap)) || ((multimap instanceof ImmutableMultimap)))
/*      */     {
/*  509 */       return multimap;
/*      */     }
/*  511 */     return new SynchronizedMultimap(multimap, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedMultimap<K, V> extends SynchronizedObject implements Multimap<K, V>
/*      */   {
/*      */     transient Set<K> keySet;
/*      */     transient Collection<V> valuesCollection;
/*      */     transient Collection<Entry<K, V>> entries;
/*      */     transient Map<K, Collection<V>> asMap;
/*      */     transient Multiset<K> keys;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     Multimap<K, V> delegate() {
/*  524 */       return (Multimap)super.delegate();
/*      */     }
/*      */     
/*      */     SynchronizedMultimap(Multimap<K, V> delegate, @Nullable Object mutex) {
/*  528 */       super(mutex);
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int size()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: invokeinterface 6 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #533	-> byte code offset #0
/*      */       //   Java source line #534	-> byte code offset #7
/*      */       //   Java source line #535	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedMultimap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean isEmpty()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: invokeinterface 7 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #540	-> byte code offset #0
/*      */       //   Java source line #541	-> byte code offset #7
/*      */       //   Java source line #542	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedMultimap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean containsKey(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #547	-> byte code offset #0
/*      */       //   Java source line #548	-> byte code offset #7
/*      */       //   Java source line #549	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean containsValue(Object value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 9 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #554	-> byte code offset #0
/*      */       //   Java source line #555	-> byte code offset #7
/*      */       //   Java source line #556	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	25	1	value	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean containsEntry(Object key, Object value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 10 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #561	-> byte code offset #0
/*      */       //   Java source line #562	-> byte code offset #7
/*      */       //   Java source line #563	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	28	1	key	Object
/*      */       //   0	28	2	value	Object
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Collection<V> get(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 11 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 12	com/google/common/collect/Synchronized:access$400	(Ljava/util/Collection;Ljava/lang/Object;)Ljava/util/Collection;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #568	-> byte code offset #0
/*      */       //   Java source line #569	-> byte code offset #7
/*      */       //   Java source line #570	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean put(K key, V value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 13 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #575	-> byte code offset #0
/*      */       //   Java source line #576	-> byte code offset #7
/*      */       //   Java source line #577	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	value	V
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean putAll(K key, Iterable<? extends V> values)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 14 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #582	-> byte code offset #0
/*      */       //   Java source line #583	-> byte code offset #7
/*      */       //   Java source line #584	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	values	Iterable<? extends V>
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 15 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #589	-> byte code offset #0
/*      */       //   Java source line #590	-> byte code offset #7
/*      */       //   Java source line #591	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	25	1	multimap	Multimap<? extends K, ? extends V>
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Collection<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 16 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #596	-> byte code offset #0
/*      */       //   Java source line #597	-> byte code offset #7
/*      */       //   Java source line #598	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	values	Iterable<? extends V>
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean remove(Object key, Object value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 17 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: ireturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #603	-> byte code offset #0
/*      */       //   Java source line #604	-> byte code offset #7
/*      */       //   Java source line #605	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	28	1	key	Object
/*      */       //   0	28	2	value	Object
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Collection<V> removeAll(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 18 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #610	-> byte code offset #0
/*      */       //   Java source line #611	-> byte code offset #7
/*      */       //   Java source line #612	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/*  617 */       synchronized (this.mutex) {
/*  618 */         delegate().clear();
/*      */       }
/*      */     }
/*      */     
/*      */     public Set<K> keySet()
/*      */     {
/*  624 */       synchronized (this.mutex) {
/*  625 */         if (this.keySet == null) {
/*  626 */           this.keySet = Synchronized.typePreservingSet(delegate().keySet(), this.mutex);
/*      */         }
/*  628 */         return this.keySet;
/*      */       }
/*      */     }
/*      */     
/*      */     public Collection<V> values()
/*      */     {
/*  634 */       synchronized (this.mutex) {
/*  635 */         if (this.valuesCollection == null) {
/*  636 */           this.valuesCollection = Synchronized.collection(delegate().values(), this.mutex);
/*      */         }
/*  638 */         return this.valuesCollection;
/*      */       }
/*      */     }
/*      */     
/*      */     public Collection<Entry<K, V>> entries()
/*      */     {
/*  644 */       synchronized (this.mutex) {
/*  645 */         if (this.entries == null) {
/*  646 */           this.entries = Synchronized.typePreservingCollection(delegate().entries(), this.mutex);
/*      */         }
/*  648 */         return this.entries;
/*      */       }
/*      */     }
/*      */     
/*      */     public Map<K, Collection<V>> asMap()
/*      */     {
/*  654 */       synchronized (this.mutex) {
/*  655 */         if (this.asMap == null) {
/*  656 */           this.asMap = new SynchronizedAsMap(delegate().asMap(), this.mutex);
/*      */         }
/*  658 */         return this.asMap;
/*      */       }
/*      */     }
/*      */     
/*      */     public Multiset<K> keys()
/*      */     {
/*  664 */       synchronized (this.mutex) {
/*  665 */         if (this.keys == null) {
/*  666 */           this.keys = Synchronized.multiset(delegate().keys(), this.mutex);
/*      */         }
/*  668 */         return this.keys;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean equals(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_1
/*      */       //   1: aload_0
/*      */       //   2: if_acmpne +5 -> 7
/*      */       //   5: iconst_1
/*      */       //   6: ireturn
/*      */       //   7: aload_0
/*      */       //   8: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   11: dup
/*      */       //   12: astore_2
/*      */       //   13: monitorenter
/*      */       //   14: aload_0
/*      */       //   15: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   18: aload_1
/*      */       //   19: invokeinterface 35 2 0
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: ireturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #673	-> byte code offset #0
/*      */       //   Java source line #674	-> byte code offset #5
/*      */       //   Java source line #676	-> byte code offset #7
/*      */       //   Java source line #677	-> byte code offset #14
/*      */       //   Java source line #678	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedMultimap<K, V>
/*      */       //   0	32	1	o	Object
/*      */       //   12	17	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   14	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int hashCode()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
/*      */       //   11: invokeinterface 36 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #682	-> byte code offset #0
/*      */       //   Java source line #683	-> byte code offset #7
/*      */       //   Java source line #684	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedMultimap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> multimap, @Nullable Object mutex)
/*      */   {
/*  692 */     if (((multimap instanceof SynchronizedListMultimap)) || ((multimap instanceof ImmutableListMultimap)))
/*      */     {
/*  694 */       return multimap;
/*      */     }
/*  696 */     return new SynchronizedListMultimap(multimap, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedListMultimap<K, V> extends SynchronizedMultimap<K, V> implements ListMultimap<K, V> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedListMultimap(ListMultimap<K, V> delegate, @Nullable Object mutex) {
/*  703 */       super(mutex);
/*      */     }
/*      */     
/*  706 */     ListMultimap<K, V> delegate() { return (ListMultimap)super.delegate(); }
/*      */     
/*      */     /* Error */
/*      */     public List<V> get(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedListMultimap:delegate	()Lcom/google/common/collect/ListMultimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 6 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 7	com/google/common/collect/Synchronized:access$200	(Ljava/util/List;Ljava/lang/Object;)Ljava/util/List;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #709	-> byte code offset #0
/*      */       //   Java source line #710	-> byte code offset #7
/*      */       //   Java source line #711	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedListMultimap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public List<V> removeAll(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedListMultimap:delegate	()Lcom/google/common/collect/ListMultimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #714	-> byte code offset #0
/*      */       //   Java source line #715	-> byte code offset #7
/*      */       //   Java source line #716	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedListMultimap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public List<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedListMultimap:delegate	()Lcom/google/common/collect/ListMultimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 9 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #720	-> byte code offset #0
/*      */       //   Java source line #721	-> byte code offset #7
/*      */       //   Java source line #722	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedListMultimap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	values	Iterable<? extends V>
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */   }
/*      */   
/*      */   static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> multimap, @Nullable Object mutex)
/*      */   {
/*  729 */     if (((multimap instanceof SynchronizedSetMultimap)) || ((multimap instanceof ImmutableSetMultimap)))
/*      */     {
/*  731 */       return multimap;
/*      */     }
/*  733 */     return new SynchronizedSetMultimap(multimap, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedSetMultimap<K, V> extends SynchronizedMultimap<K, V> implements SetMultimap<K, V>
/*      */   {
/*      */     transient Set<Entry<K, V>> entrySet;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedSetMultimap(SetMultimap<K, V> delegate, @Nullable Object mutex) {
/*  742 */       super(mutex);
/*      */     }
/*      */     
/*  745 */     SetMultimap<K, V> delegate() { return (SetMultimap)super.delegate(); }
/*      */     
/*      */     /* Error */
/*      */     public Set<V> get(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSetMultimap:delegate	()Lcom/google/common/collect/SetMultimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 6 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 7	com/google/common/collect/Synchronized:set	(Ljava/util/Set;Ljava/lang/Object;)Ljava/util/Set;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #748	-> byte code offset #0
/*      */       //   Java source line #749	-> byte code offset #7
/*      */       //   Java source line #750	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedSetMultimap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Set<V> removeAll(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSetMultimap:delegate	()Lcom/google/common/collect/SetMultimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #753	-> byte code offset #0
/*      */       //   Java source line #754	-> byte code offset #7
/*      */       //   Java source line #755	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedSetMultimap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Set<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSetMultimap:delegate	()Lcom/google/common/collect/SetMultimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 9 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #759	-> byte code offset #0
/*      */       //   Java source line #760	-> byte code offset #7
/*      */       //   Java source line #761	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedSetMultimap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	values	Iterable<? extends V>
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     public Set<Entry<K, V>> entries()
/*      */     {
/*  764 */       synchronized (this.mutex) {
/*  765 */         if (this.entrySet == null) {
/*  766 */           this.entrySet = Synchronized.set(delegate().entries(), this.mutex);
/*      */         }
/*  768 */         return this.entrySet;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> multimap, @Nullable Object mutex)
/*      */   {
/*  776 */     if ((multimap instanceof SynchronizedSortedSetMultimap)) {
/*  777 */       return multimap;
/*      */     }
/*  779 */     return new SynchronizedSortedSetMultimap(multimap, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedSortedSetMultimap<K, V> extends SynchronizedSetMultimap<K, V> implements SortedSetMultimap<K, V> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedSortedSetMultimap(SortedSetMultimap<K, V> delegate, @Nullable Object mutex) {
/*  786 */       super(mutex);
/*      */     }
/*      */     
/*  789 */     SortedSetMultimap<K, V> delegate() { return (SortedSetMultimap)super.delegate(); }
/*      */     
/*      */     /* Error */
/*      */     public SortedSet<V> get(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 6 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 7	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #792	-> byte code offset #0
/*      */       //   Java source line #793	-> byte code offset #7
/*      */       //   Java source line #794	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedSortedSetMultimap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedSet<V> removeAll(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #797	-> byte code offset #0
/*      */       //   Java source line #798	-> byte code offset #7
/*      */       //   Java source line #799	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedSortedSetMultimap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedSet<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 9 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #803	-> byte code offset #0
/*      */       //   Java source line #804	-> byte code offset #7
/*      */       //   Java source line #805	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedSortedSetMultimap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	values	Iterable<? extends V>
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public java.util.Comparator<? super V> valueComparator()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
/*      */       //   11: invokeinterface 10 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #809	-> byte code offset #0
/*      */       //   Java source line #810	-> byte code offset #7
/*      */       //   Java source line #811	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSortedSetMultimap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   private static <E> Collection<E> typePreservingCollection(Collection<E> collection, @Nullable Object mutex)
/*      */   {
/*  818 */     if ((collection instanceof SortedSet)) {
/*  819 */       return sortedSet((SortedSet)collection, mutex);
/*      */     }
/*  821 */     if ((collection instanceof Set)) {
/*  822 */       return set((Set)collection, mutex);
/*      */     }
/*  824 */     if ((collection instanceof List)) {
/*  825 */       return list((List)collection, mutex);
/*      */     }
/*  827 */     return collection(collection, mutex);
/*      */   }
/*      */   
/*      */   private static <E> Set<E> typePreservingSet(Set<E> set, @Nullable Object mutex)
/*      */   {
/*  832 */     if ((set instanceof SortedSet)) {
/*  833 */       return sortedSet((SortedSet)set, mutex);
/*      */     }
/*  835 */     return set(set, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedAsMapEntries<K, V> extends SynchronizedSet<Entry<K, Collection<V>>>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedAsMapEntries(Set<Entry<K, Collection<V>>> delegate, @Nullable Object mutex) {
/*  843 */       super(mutex);
/*      */     }
/*      */     
/*      */     public Iterator<Entry<K, Collection<V>>> iterator()
/*      */     {
/*  848 */       final Iterator<Entry<K, Collection<V>>> iterator = super.iterator();
/*  849 */       new ForwardingIterator() {
/*      */         protected Iterator<Entry<K, Collection<V>>> delegate() {
/*  851 */           return iterator;
/*      */         }
/*      */         
/*      */         public Entry<K, Collection<V>> next() {
/*  855 */           final Entry<K, Collection<V>> entry = (Entry)super.next();
/*  856 */           new ForwardingMapEntry() {
/*      */             protected Entry<K, Collection<V>> delegate() {
/*  858 */               return entry;
/*      */             }
/*      */             
/*  861 */             public Collection<V> getValue() { return Synchronized.typePreservingCollection((Collection)entry.getValue(), SynchronizedAsMapEntries.this.mutex); }
/*      */           };
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Object[] toArray()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   11: invokestatic 7	com/google/common/collect/ObjectArrays:toArrayImpl	(Ljava/util/Collection;)[Ljava/lang/Object;
/*      */       //   14: aload_1
/*      */       //   15: monitorexit
/*      */       //   16: areturn
/*      */       //   17: astore_2
/*      */       //   18: aload_1
/*      */       //   19: monitorexit
/*      */       //   20: aload_2
/*      */       //   21: athrow
/*      */       // Line number table:
/*      */       //   Java source line #871	-> byte code offset #0
/*      */       //   Java source line #872	-> byte code offset #7
/*      */       //   Java source line #873	-> byte code offset #17
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	22	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   5	14	1	Ljava/lang/Object;	Object
/*      */       //   17	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	16	17	finally
/*      */       //   17	20	17	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public <T> T[] toArray(T[] array)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   11: aload_1
/*      */       //   12: invokestatic 8	com/google/common/collect/ObjectArrays:toArrayImpl	(Ljava/util/Collection;[Ljava/lang/Object;)[Ljava/lang/Object;
/*      */       //   15: aload_2
/*      */       //   16: monitorexit
/*      */       //   17: areturn
/*      */       //   18: astore_3
/*      */       //   19: aload_2
/*      */       //   20: monitorexit
/*      */       //   21: aload_3
/*      */       //   22: athrow
/*      */       // Line number table:
/*      */       //   Java source line #876	-> byte code offset #0
/*      */       //   Java source line #877	-> byte code offset #7
/*      */       //   Java source line #878	-> byte code offset #18
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	23	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   0	23	1	array	T[]
/*      */       //   5	15	2	Ljava/lang/Object;	Object
/*      */       //   18	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	17	18	finally
/*      */       //   18	21	18	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean contains(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   11: aload_1
/*      */       //   12: invokestatic 9	com/google/common/collect/Maps:containsEntryImpl	(Ljava/util/Collection;Ljava/lang/Object;)Z
/*      */       //   15: aload_2
/*      */       //   16: monitorexit
/*      */       //   17: ireturn
/*      */       //   18: astore_3
/*      */       //   19: aload_2
/*      */       //   20: monitorexit
/*      */       //   21: aload_3
/*      */       //   22: athrow
/*      */       // Line number table:
/*      */       //   Java source line #881	-> byte code offset #0
/*      */       //   Java source line #882	-> byte code offset #7
/*      */       //   Java source line #883	-> byte code offset #18
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	23	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   0	23	1	o	Object
/*      */       //   5	15	2	Ljava/lang/Object;	Object
/*      */       //   18	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	17	18	finally
/*      */       //   18	21	18	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean containsAll(Collection<?> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   11: aload_1
/*      */       //   12: invokestatic 10	com/google/common/collect/Collections2:containsAllImpl	(Ljava/util/Collection;Ljava/util/Collection;)Z
/*      */       //   15: aload_2
/*      */       //   16: monitorexit
/*      */       //   17: ireturn
/*      */       //   18: astore_3
/*      */       //   19: aload_2
/*      */       //   20: monitorexit
/*      */       //   21: aload_3
/*      */       //   22: athrow
/*      */       // Line number table:
/*      */       //   Java source line #886	-> byte code offset #0
/*      */       //   Java source line #887	-> byte code offset #7
/*      */       //   Java source line #888	-> byte code offset #18
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	23	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   0	23	1	c	Collection<?>
/*      */       //   5	15	2	Ljava/lang/Object;	Object
/*      */       //   18	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	17	18	finally
/*      */       //   18	21	18	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean equals(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_1
/*      */       //   1: aload_0
/*      */       //   2: if_acmpne +5 -> 7
/*      */       //   5: iconst_1
/*      */       //   6: ireturn
/*      */       //   7: aload_0
/*      */       //   8: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   11: dup
/*      */       //   12: astore_2
/*      */       //   13: monitorenter
/*      */       //   14: aload_0
/*      */       //   15: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   18: aload_1
/*      */       //   19: invokestatic 11	com/google/common/collect/Sets:equalsImpl	(Ljava/util/Set;Ljava/lang/Object;)Z
/*      */       //   22: aload_2
/*      */       //   23: monitorexit
/*      */       //   24: ireturn
/*      */       //   25: astore_3
/*      */       //   26: aload_2
/*      */       //   27: monitorexit
/*      */       //   28: aload_3
/*      */       //   29: athrow
/*      */       // Line number table:
/*      */       //   Java source line #891	-> byte code offset #0
/*      */       //   Java source line #892	-> byte code offset #5
/*      */       //   Java source line #894	-> byte code offset #7
/*      */       //   Java source line #895	-> byte code offset #14
/*      */       //   Java source line #896	-> byte code offset #25
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	30	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   0	30	1	o	Object
/*      */       //   12	15	2	Ljava/lang/Object;	Object
/*      */       //   25	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   14	24	25	finally
/*      */       //   25	28	25	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean remove(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   11: aload_1
/*      */       //   12: invokestatic 12	com/google/common/collect/Maps:removeEntryImpl	(Ljava/util/Collection;Ljava/lang/Object;)Z
/*      */       //   15: aload_2
/*      */       //   16: monitorexit
/*      */       //   17: ireturn
/*      */       //   18: astore_3
/*      */       //   19: aload_2
/*      */       //   20: monitorexit
/*      */       //   21: aload_3
/*      */       //   22: athrow
/*      */       // Line number table:
/*      */       //   Java source line #899	-> byte code offset #0
/*      */       //   Java source line #900	-> byte code offset #7
/*      */       //   Java source line #901	-> byte code offset #18
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	23	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   0	23	1	o	Object
/*      */       //   5	15	2	Ljava/lang/Object;	Object
/*      */       //   18	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	17	18	finally
/*      */       //   18	21	18	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean removeAll(Collection<?> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   11: invokeinterface 13 1 0
/*      */       //   16: aload_1
/*      */       //   17: invokestatic 14	com/google/common/collect/Iterators:removeAll	(Ljava/util/Iterator;Ljava/util/Collection;)Z
/*      */       //   20: aload_2
/*      */       //   21: monitorexit
/*      */       //   22: ireturn
/*      */       //   23: astore_3
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: aload_3
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #904	-> byte code offset #0
/*      */       //   Java source line #905	-> byte code offset #7
/*      */       //   Java source line #906	-> byte code offset #23
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   0	28	1	c	Collection<?>
/*      */       //   5	20	2	Ljava/lang/Object;	Object
/*      */       //   23	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	22	23	finally
/*      */       //   23	26	23	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean retainAll(Collection<?> c)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 5	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 6	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
/*      */       //   11: invokeinterface 13 1 0
/*      */       //   16: aload_1
/*      */       //   17: invokestatic 15	com/google/common/collect/Iterators:retainAll	(Ljava/util/Iterator;Ljava/util/Collection;)Z
/*      */       //   20: aload_2
/*      */       //   21: monitorexit
/*      */       //   22: ireturn
/*      */       //   23: astore_3
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: aload_3
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #909	-> byte code offset #0
/*      */       //   Java source line #910	-> byte code offset #7
/*      */       //   Java source line #911	-> byte code offset #23
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedAsMapEntries<K, V>
/*      */       //   0	28	1	c	Collection<?>
/*      */       //   5	20	2	Ljava/lang/Object;	Object
/*      */       //   23	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	22	23	finally
/*      */       //   23	26	23	finally
/*      */     }
/*      */   }
/*      */   
/*      */   @VisibleForTesting
/*      */   static <K, V> Map<K, V> map(Map<K, V> map, @Nullable Object mutex)
/*      */   {
/*  919 */     return new SynchronizedMap(map, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedMap<K, V> extends SynchronizedObject implements Map<K, V> {
/*      */     transient Set<K> keySet;
/*      */     transient Collection<V> values;
/*      */     transient Set<Entry<K, V>> entrySet;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedMap(Map<K, V> delegate, @Nullable Object mutex) {
/*  929 */       super(mutex);
/*      */     }
/*      */     
/*      */     Map<K, V> delegate()
/*      */     {
/*  934 */       return (Map)super.delegate();
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/*  939 */       synchronized (this.mutex) {
/*  940 */         delegate().clear();
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean containsKey(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 7 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #946	-> byte code offset #0
/*      */       //   Java source line #947	-> byte code offset #7
/*      */       //   Java source line #948	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean containsValue(Object value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #953	-> byte code offset #0
/*      */       //   Java source line #954	-> byte code offset #7
/*      */       //   Java source line #955	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMap<K, V>
/*      */       //   0	25	1	value	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public Set<Entry<K, V>> entrySet()
/*      */     {
/*  960 */       synchronized (this.mutex) {
/*  961 */         if (this.entrySet == null) {
/*  962 */           this.entrySet = Synchronized.set(delegate().entrySet(), this.mutex);
/*      */         }
/*  964 */         return this.entrySet;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public V get(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 12 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #970	-> byte code offset #0
/*      */       //   Java source line #971	-> byte code offset #7
/*      */       //   Java source line #972	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean isEmpty()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: invokeinterface 13 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #977	-> byte code offset #0
/*      */       //   Java source line #978	-> byte code offset #7
/*      */       //   Java source line #979	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedMap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     public Set<K> keySet()
/*      */     {
/*  984 */       synchronized (this.mutex) {
/*  985 */         if (this.keySet == null) {
/*  986 */           this.keySet = Synchronized.set(delegate().keySet(), this.mutex);
/*      */         }
/*  988 */         return this.keySet;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public V put(K key, V value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 16 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #994	-> byte code offset #0
/*      */       //   Java source line #995	-> byte code offset #7
/*      */       //   Java source line #996	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedMap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	value	V
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     public void putAll(Map<? extends K, ? extends V> map)
/*      */     {
/* 1001 */       synchronized (this.mutex) {
/* 1002 */         delegate().putAll(map);
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public V remove(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 18 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1008	-> byte code offset #0
/*      */       //   Java source line #1009	-> byte code offset #7
/*      */       //   Java source line #1010	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedMap<K, V>
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int size()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: invokeinterface 19 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1015	-> byte code offset #0
/*      */       //   Java source line #1016	-> byte code offset #7
/*      */       //   Java source line #1017	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedMap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     public Collection<V> values()
/*      */     {
/* 1022 */       synchronized (this.mutex) {
/* 1023 */         if (this.values == null) {
/* 1024 */           this.values = Synchronized.collection(delegate().values(), this.mutex);
/*      */         }
/* 1026 */         return this.values;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean equals(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_1
/*      */       //   1: aload_0
/*      */       //   2: if_acmpne +5 -> 7
/*      */       //   5: iconst_1
/*      */       //   6: ireturn
/*      */       //   7: aload_0
/*      */       //   8: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   11: dup
/*      */       //   12: astore_2
/*      */       //   13: monitorenter
/*      */       //   14: aload_0
/*      */       //   15: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   18: aload_1
/*      */       //   19: invokeinterface 23 2 0
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: ireturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1031	-> byte code offset #0
/*      */       //   Java source line #1032	-> byte code offset #5
/*      */       //   Java source line #1034	-> byte code offset #7
/*      */       //   Java source line #1035	-> byte code offset #14
/*      */       //   Java source line #1036	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedMap<K, V>
/*      */       //   0	32	1	o	Object
/*      */       //   12	17	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   14	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int hashCode()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
/*      */       //   11: invokeinterface 24 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1040	-> byte code offset #0
/*      */       //   Java source line #1041	-> byte code offset #7
/*      */       //   Java source line #1042	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedMap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> sortedMap, @Nullable Object mutex)
/*      */   {
/* 1050 */     return new SynchronizedSortedMap(sortedMap, mutex);
/*      */   }
/*      */   
/*      */   static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedSortedMap(SortedMap<K, V> delegate, @Nullable Object mutex) {
/* 1057 */       super(mutex);
/*      */     }
/*      */     
/*      */     SortedMap<K, V> delegate() {
/* 1061 */       return (SortedMap)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public java.util.Comparator<? super K> comparator()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
/*      */       //   11: invokeinterface 6 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1065	-> byte code offset #0
/*      */       //   Java source line #1066	-> byte code offset #7
/*      */       //   Java source line #1067	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSortedMap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public K firstKey()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
/*      */       //   11: invokeinterface 7 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1071	-> byte code offset #0
/*      */       //   Java source line #1072	-> byte code offset #7
/*      */       //   Java source line #1073	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSortedMap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedMap<K, V> headMap(K toKey)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 9	com/google/common/collect/Synchronized:sortedMap	(Ljava/util/SortedMap;Ljava/lang/Object;)Ljava/util/SortedMap;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1077	-> byte code offset #0
/*      */       //   Java source line #1078	-> byte code offset #7
/*      */       //   Java source line #1079	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedSortedMap<K, V>
/*      */       //   0	32	1	toKey	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public K lastKey()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
/*      */       //   11: invokeinterface 10 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1083	-> byte code offset #0
/*      */       //   Java source line #1084	-> byte code offset #7
/*      */       //   Java source line #1085	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedSortedMap<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedMap<K, V> subMap(K fromKey, K toKey)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 11 3 0
/*      */       //   18: aload_0
/*      */       //   19: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   22: invokestatic 9	com/google/common/collect/Synchronized:sortedMap	(Ljava/util/SortedMap;Ljava/lang/Object;)Ljava/util/SortedMap;
/*      */       //   25: aload_3
/*      */       //   26: monitorexit
/*      */       //   27: areturn
/*      */       //   28: astore 4
/*      */       //   30: aload_3
/*      */       //   31: monitorexit
/*      */       //   32: aload 4
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1089	-> byte code offset #0
/*      */       //   Java source line #1090	-> byte code offset #7
/*      */       //   Java source line #1091	-> byte code offset #28
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	SynchronizedSortedMap<K, V>
/*      */       //   0	35	1	fromKey	K
/*      */       //   0	35	2	toKey	K
/*      */       //   5	26	3	Ljava/lang/Object;	Object
/*      */       //   28	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	27	28	finally
/*      */       //   28	32	28	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public SortedMap<K, V> tailMap(K fromKey)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 12 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 9	com/google/common/collect/Synchronized:sortedMap	(Ljava/util/SortedMap;Ljava/lang/Object;)Ljava/util/SortedMap;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1095	-> byte code offset #0
/*      */       //   Java source line #1096	-> byte code offset #7
/*      */       //   Java source line #1097	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedSortedMap<K, V>
/*      */       //   0	32	1	fromKey	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */   }
/*      */   
/*      */   static <K, V> BiMap<K, V> biMap(BiMap<K, V> bimap, @Nullable Object mutex)
/*      */   {
/* 1104 */     if (((bimap instanceof SynchronizedBiMap)) || ((bimap instanceof ImmutableBiMap)))
/*      */     {
/* 1106 */       return bimap;
/*      */     }
/* 1108 */     return new SynchronizedBiMap(bimap, mutex, null, null);
/*      */   }
/*      */   
/*      */   @VisibleForTesting
/*      */   static class SynchronizedBiMap<K, V> extends SynchronizedMap<K, V> implements BiMap<K, V>, Serializable {
/*      */     private transient Set<V> valueSet;
/*      */     private transient BiMap<V, K> inverse;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     private SynchronizedBiMap(BiMap<K, V> delegate, @Nullable Object mutex, @Nullable BiMap<V, K> inverse) {
/* 1118 */       super(mutex);
/* 1119 */       this.inverse = inverse;
/*      */     }
/*      */     
/*      */     BiMap<K, V> delegate() {
/* 1123 */       return (BiMap)super.delegate();
/*      */     }
/*      */     
/*      */     public Set<V> values() {
/* 1127 */       synchronized (this.mutex) {
/* 1128 */         if (this.valueSet == null) {
/* 1129 */           this.valueSet = Synchronized.set(delegate().values(), this.mutex);
/*      */         }
/* 1131 */         return this.valueSet;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public V forcePut(K key, V value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 6	com/google/common/collect/Synchronized$SynchronizedBiMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 8	com/google/common/collect/Synchronized$SynchronizedBiMap:delegate	()Lcom/google/common/collect/BiMap;
/*      */       //   11: aload_1
/*      */       //   12: aload_2
/*      */       //   13: invokeinterface 11 3 0
/*      */       //   18: aload_3
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore 4
/*      */       //   23: aload_3
/*      */       //   24: monitorexit
/*      */       //   25: aload 4
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1137	-> byte code offset #0
/*      */       //   Java source line #1138	-> byte code offset #7
/*      */       //   Java source line #1139	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	SynchronizedBiMap<K, V>
/*      */       //   0	28	1	key	K
/*      */       //   0	28	2	value	V
/*      */       //   5	19	3	Ljava/lang/Object;	Object
/*      */       //   21	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	25	21	finally
/*      */     }
/*      */     
/*      */     public BiMap<V, K> inverse()
/*      */     {
/* 1144 */       synchronized (this.mutex) {
/* 1145 */         if (this.inverse == null) {
/* 1146 */           this.inverse = new SynchronizedBiMap(delegate().inverse(), this.mutex, this);
/*      */         }
/*      */         
/* 1149 */         return this.inverse;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static class SynchronizedAsMap<K, V> extends SynchronizedMap<K, Collection<V>>
/*      */   {
/*      */     transient Set<Entry<K, Collection<V>>> asMapEntrySet;
/*      */     transient Collection<Collection<V>> asMapValues;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedAsMap(Map<K, Collection<V>> delegate, @Nullable Object mutex)
/*      */     {
/* 1162 */       super(mutex);
/*      */     }
/*      */     
/*      */     public Collection<V> get(Object key) {
/* 1166 */       synchronized (this.mutex) {
/* 1167 */         Collection<V> collection = (Collection)super.get(key);
/* 1168 */         return collection == null ? null : Synchronized.typePreservingCollection(collection, this.mutex);
/*      */       }
/*      */     }
/*      */     
/*      */     public Set<Entry<K, Collection<V>>> entrySet()
/*      */     {
/* 1174 */       synchronized (this.mutex) {
/* 1175 */         if (this.asMapEntrySet == null) {
/* 1176 */           this.asMapEntrySet = new SynchronizedAsMapEntries(delegate().entrySet(), this.mutex);
/*      */         }
/*      */         
/* 1179 */         return this.asMapEntrySet;
/*      */       }
/*      */     }
/*      */     
/*      */     public Collection<Collection<V>> values() {
/* 1184 */       synchronized (this.mutex) {
/* 1185 */         if (this.asMapValues == null) {
/* 1186 */           this.asMapValues = new SynchronizedAsMapValues(delegate().values(), this.mutex);
/*      */         }
/*      */         
/* 1189 */         return this.asMapValues;
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean containsValue(Object o)
/*      */     {
/* 1195 */       return values().contains(o);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class SynchronizedAsMapValues<V> extends SynchronizedCollection<Collection<V>>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedAsMapValues(Collection<Collection<V>> delegate, @Nullable Object mutex)
/*      */     {
/* 1205 */       super(mutex, null);
/*      */     }
/*      */     
/*      */     public Iterator<Collection<V>> iterator()
/*      */     {
/* 1210 */       final Iterator<Collection<V>> iterator = super.iterator();
/* 1211 */       new ForwardingIterator() {
/*      */         protected Iterator<Collection<V>> delegate() {
/* 1213 */           return iterator;
/*      */         }
/*      */         
/* 1216 */         public Collection<V> next() { return Synchronized.typePreservingCollection((Collection)super.next(), SynchronizedAsMapValues.this.mutex); }
/*      */       };
/*      */     }
/*      */   }
/*      */   
/*      */   @GwtIncompatible("NavigableSet")
/*      */   @VisibleForTesting
/*      */   static class SynchronizedNavigableSet<E> extends SynchronizedSortedSet<E> implements NavigableSet<E>
/*      */   {
/*      */     transient NavigableSet<E> descendingSet;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedNavigableSet(NavigableSet<E> delegate, @Nullable Object mutex) {
/* 1229 */       super(mutex);
/*      */     }
/*      */     
/*      */     NavigableSet<E> delegate() {
/* 1233 */       return (NavigableSet)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E ceiling(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 6 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1237	-> byte code offset #0
/*      */       //   Java source line #1238	-> byte code offset #7
/*      */       //   Java source line #1239	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableSet<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public Iterator<E> descendingIterator()
/*      */     {
/* 1243 */       return delegate().descendingIterator();
/*      */     }
/*      */     
/*      */ 
/*      */     public NavigableSet<E> descendingSet()
/*      */     {
/* 1249 */       synchronized (this.mutex) {
/* 1250 */         if (this.descendingSet == null) {
/* 1251 */           NavigableSet<E> dS = Synchronized.navigableSet(delegate().descendingSet(), this.mutex);
/*      */           
/* 1253 */           this.descendingSet = dS;
/* 1254 */           return dS;
/*      */         }
/* 1256 */         return this.descendingSet;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E floor(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 11 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1261	-> byte code offset #0
/*      */       //   Java source line #1262	-> byte code offset #7
/*      */       //   Java source line #1263	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableSet<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public NavigableSet<E> headSet(E toElement, boolean inclusive)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: aload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 12 3 0
/*      */       //   18: aload_0
/*      */       //   19: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   22: invokestatic 10	com/google/common/collect/Synchronized:navigableSet	(Ljava/util/NavigableSet;Ljava/lang/Object;)Ljava/util/NavigableSet;
/*      */       //   25: aload_3
/*      */       //   26: monitorexit
/*      */       //   27: areturn
/*      */       //   28: astore 4
/*      */       //   30: aload_3
/*      */       //   31: monitorexit
/*      */       //   32: aload 4
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1267	-> byte code offset #0
/*      */       //   Java source line #1268	-> byte code offset #7
/*      */       //   Java source line #1270	-> byte code offset #28
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	SynchronizedNavigableSet<E>
/*      */       //   0	35	1	toElement	E
/*      */       //   0	35	2	inclusive	boolean
/*      */       //   5	26	3	Ljava/lang/Object;	Object
/*      */       //   28	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	27	28	finally
/*      */       //   28	32	28	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E higher(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 13 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1274	-> byte code offset #0
/*      */       //   Java source line #1275	-> byte code offset #7
/*      */       //   Java source line #1276	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableSet<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E lower(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 14 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1280	-> byte code offset #0
/*      */       //   Java source line #1281	-> byte code offset #7
/*      */       //   Java source line #1282	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableSet<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E pollFirst()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: invokeinterface 15 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1286	-> byte code offset #0
/*      */       //   Java source line #1287	-> byte code offset #7
/*      */       //   Java source line #1288	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedNavigableSet<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E pollLast()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: invokeinterface 16 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1292	-> byte code offset #0
/*      */       //   Java source line #1293	-> byte code offset #7
/*      */       //   Java source line #1294	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedNavigableSet<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore 5
/*      */       //   7: monitorenter
/*      */       //   8: aload_0
/*      */       //   9: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   12: aload_1
/*      */       //   13: iload_2
/*      */       //   14: aload_3
/*      */       //   15: iload 4
/*      */       //   17: invokeinterface 17 5 0
/*      */       //   22: aload_0
/*      */       //   23: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   26: invokestatic 10	com/google/common/collect/Synchronized:navigableSet	(Ljava/util/NavigableSet;Ljava/lang/Object;)Ljava/util/NavigableSet;
/*      */       //   29: aload 5
/*      */       //   31: monitorexit
/*      */       //   32: areturn
/*      */       //   33: astore 6
/*      */       //   35: aload 5
/*      */       //   37: monitorexit
/*      */       //   38: aload 6
/*      */       //   40: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1299	-> byte code offset #0
/*      */       //   Java source line #1300	-> byte code offset #8
/*      */       //   Java source line #1302	-> byte code offset #33
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	41	0	this	SynchronizedNavigableSet<E>
/*      */       //   0	41	1	fromElement	E
/*      */       //   0	41	2	fromInclusive	boolean
/*      */       //   0	41	3	toElement	E
/*      */       //   0	41	4	toInclusive	boolean
/*      */       //   5	31	5	Ljava/lang/Object;	Object
/*      */       //   33	6	6	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   8	32	33	finally
/*      */       //   33	38	33	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
/*      */       //   11: aload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 18 3 0
/*      */       //   18: aload_0
/*      */       //   19: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
/*      */       //   22: invokestatic 10	com/google/common/collect/Synchronized:navigableSet	(Ljava/util/NavigableSet;Ljava/lang/Object;)Ljava/util/NavigableSet;
/*      */       //   25: aload_3
/*      */       //   26: monitorexit
/*      */       //   27: areturn
/*      */       //   28: astore 4
/*      */       //   30: aload_3
/*      */       //   31: monitorexit
/*      */       //   32: aload 4
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1306	-> byte code offset #0
/*      */       //   Java source line #1307	-> byte code offset #7
/*      */       //   Java source line #1309	-> byte code offset #28
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	SynchronizedNavigableSet<E>
/*      */       //   0	35	1	fromElement	E
/*      */       //   0	35	2	inclusive	boolean
/*      */       //   5	26	3	Ljava/lang/Object;	Object
/*      */       //   28	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	27	28	finally
/*      */       //   28	32	28	finally
/*      */     }
/*      */     
/*      */     public SortedSet<E> headSet(E toElement)
/*      */     {
/* 1313 */       return headSet(toElement, false);
/*      */     }
/*      */     
/*      */     public SortedSet<E> subSet(E fromElement, E toElement) {
/* 1317 */       return subSet(fromElement, true, toElement, false);
/*      */     }
/*      */     
/*      */     public SortedSet<E> tailSet(E fromElement) {
/* 1321 */       return tailSet(fromElement, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("NavigableSet")
/*      */   static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet, @Nullable Object mutex)
/*      */   {
/* 1330 */     return new SynchronizedNavigableSet(navigableSet, mutex);
/*      */   }
/*      */   
/*      */   @GwtIncompatible("NavigableSet")
/*      */   static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet) {
/* 1335 */     return navigableSet(navigableSet, null);
/*      */   }
/*      */   
/*      */   @GwtIncompatible("NavigableMap")
/*      */   static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap)
/*      */   {
/* 1341 */     return navigableMap(navigableMap, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("NavigableMap")
/* 1347 */   static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap, @Nullable Object mutex) { return new SynchronizedNavigableMap(navigableMap, mutex); }
/*      */   
/*      */   @GwtIncompatible("NavigableMap")
/*      */   @VisibleForTesting
/*      */   static class SynchronizedNavigableMap<K, V> extends SynchronizedSortedMap<K, V> implements NavigableMap<K, V> { transient NavigableSet<K> descendingKeySet;
/*      */     transient NavigableMap<K, V> descendingMap;
/*      */     transient NavigableSet<K> navigableKeySet;
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/* 1356 */     SynchronizedNavigableMap(NavigableMap<K, V> delegate, @Nullable Object mutex) { super(mutex); }
/*      */     
/*      */     NavigableMap<K, V> delegate()
/*      */     {
/* 1360 */       return (NavigableMap)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> ceilingEntry(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 6 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1364	-> byte code offset #0
/*      */       //   Java source line #1365	-> byte code offset #7
/*      */       //   Java source line #1366	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public K ceilingKey(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1370	-> byte code offset #0
/*      */       //   Java source line #1371	-> byte code offset #7
/*      */       //   Java source line #1372	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	25	1	key	K
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public NavigableSet<K> descendingKeySet()
/*      */     {
/* 1378 */       synchronized (this.mutex) {
/* 1379 */         if (this.descendingKeySet == null) {
/* 1380 */           return this.descendingKeySet = Synchronized.navigableSet(delegate().descendingKeySet(), this.mutex);
/*      */         }
/*      */         
/* 1383 */         return this.descendingKeySet;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public NavigableMap<K, V> descendingMap()
/*      */     {
/* 1390 */       synchronized (this.mutex) {
/* 1391 */         if (this.descendingMap == null) {
/* 1392 */           return this.descendingMap = Synchronized.navigableMap(delegate().descendingMap(), this.mutex);
/*      */         }
/*      */         
/* 1395 */         return this.descendingMap;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> firstEntry()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: invokeinterface 15 1 0
/*      */       //   16: aload_0
/*      */       //   17: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   20: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   23: aload_1
/*      */       //   24: monitorexit
/*      */       //   25: areturn
/*      */       //   26: astore_2
/*      */       //   27: aload_1
/*      */       //   28: monitorexit
/*      */       //   29: aload_2
/*      */       //   30: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1400	-> byte code offset #0
/*      */       //   Java source line #1401	-> byte code offset #7
/*      */       //   Java source line #1402	-> byte code offset #26
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	31	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   5	23	1	Ljava/lang/Object;	Object
/*      */       //   26	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	25	26	finally
/*      */       //   26	29	26	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> floorEntry(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 16 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1406	-> byte code offset #0
/*      */       //   Java source line #1407	-> byte code offset #7
/*      */       //   Java source line #1408	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public K floorKey(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 17 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1412	-> byte code offset #0
/*      */       //   Java source line #1413	-> byte code offset #7
/*      */       //   Java source line #1414	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	25	1	key	K
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public NavigableMap<K, V> headMap(K toKey, boolean inclusive)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 18 3 0
/*      */       //   18: aload_0
/*      */       //   19: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   22: invokestatic 14	com/google/common/collect/Synchronized:navigableMap	(Ljava/util/NavigableMap;Ljava/lang/Object;)Ljava/util/NavigableMap;
/*      */       //   25: aload_3
/*      */       //   26: monitorexit
/*      */       //   27: areturn
/*      */       //   28: astore 4
/*      */       //   30: aload_3
/*      */       //   31: monitorexit
/*      */       //   32: aload 4
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1418	-> byte code offset #0
/*      */       //   Java source line #1419	-> byte code offset #7
/*      */       //   Java source line #1421	-> byte code offset #28
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	35	1	toKey	K
/*      */       //   0	35	2	inclusive	boolean
/*      */       //   5	26	3	Ljava/lang/Object;	Object
/*      */       //   28	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	27	28	finally
/*      */       //   28	32	28	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> higherEntry(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 19 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1425	-> byte code offset #0
/*      */       //   Java source line #1426	-> byte code offset #7
/*      */       //   Java source line #1427	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public K higherKey(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 20 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1431	-> byte code offset #0
/*      */       //   Java source line #1432	-> byte code offset #7
/*      */       //   Java source line #1433	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	25	1	key	K
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> lastEntry()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: invokeinterface 21 1 0
/*      */       //   16: aload_0
/*      */       //   17: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   20: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   23: aload_1
/*      */       //   24: monitorexit
/*      */       //   25: areturn
/*      */       //   26: astore_2
/*      */       //   27: aload_1
/*      */       //   28: monitorexit
/*      */       //   29: aload_2
/*      */       //   30: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1437	-> byte code offset #0
/*      */       //   Java source line #1438	-> byte code offset #7
/*      */       //   Java source line #1439	-> byte code offset #26
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	31	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   5	23	1	Ljava/lang/Object;	Object
/*      */       //   26	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	25	26	finally
/*      */       //   26	29	26	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> lowerEntry(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 22 2 0
/*      */       //   17: aload_0
/*      */       //   18: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   21: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: areturn
/*      */       //   27: astore_3
/*      */       //   28: aload_2
/*      */       //   29: monitorexit
/*      */       //   30: aload_3
/*      */       //   31: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1443	-> byte code offset #0
/*      */       //   Java source line #1444	-> byte code offset #7
/*      */       //   Java source line #1445	-> byte code offset #27
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	32	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	32	1	key	K
/*      */       //   5	24	2	Ljava/lang/Object;	Object
/*      */       //   27	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	26	27	finally
/*      */       //   27	30	27	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public K lowerKey(K key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 23 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1449	-> byte code offset #0
/*      */       //   Java source line #1450	-> byte code offset #7
/*      */       //   Java source line #1451	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	25	1	key	K
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public Set<K> keySet()
/*      */     {
/* 1455 */       return navigableKeySet();
/*      */     }
/*      */     
/*      */ 
/*      */     public NavigableSet<K> navigableKeySet()
/*      */     {
/* 1461 */       synchronized (this.mutex) {
/* 1462 */         if (this.navigableKeySet == null) {
/* 1463 */           return this.navigableKeySet = Synchronized.navigableSet(delegate().navigableKeySet(), this.mutex);
/*      */         }
/*      */         
/* 1466 */         return this.navigableKeySet;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> pollFirstEntry()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: invokeinterface 27 1 0
/*      */       //   16: aload_0
/*      */       //   17: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   20: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   23: aload_1
/*      */       //   24: monitorexit
/*      */       //   25: areturn
/*      */       //   26: astore_2
/*      */       //   27: aload_1
/*      */       //   28: monitorexit
/*      */       //   29: aload_2
/*      */       //   30: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1471	-> byte code offset #0
/*      */       //   Java source line #1472	-> byte code offset #7
/*      */       //   Java source line #1473	-> byte code offset #26
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	31	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   5	23	1	Ljava/lang/Object;	Object
/*      */       //   26	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	25	26	finally
/*      */       //   26	29	26	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Entry<K, V> pollLastEntry()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: invokeinterface 28 1 0
/*      */       //   16: aload_0
/*      */       //   17: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   20: invokestatic 7	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
/*      */       //   23: aload_1
/*      */       //   24: monitorexit
/*      */       //   25: areturn
/*      */       //   26: astore_2
/*      */       //   27: aload_1
/*      */       //   28: monitorexit
/*      */       //   29: aload_2
/*      */       //   30: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1477	-> byte code offset #0
/*      */       //   Java source line #1478	-> byte code offset #7
/*      */       //   Java source line #1479	-> byte code offset #26
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	31	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   5	23	1	Ljava/lang/Object;	Object
/*      */       //   26	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	25	26	finally
/*      */       //   26	29	26	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore 5
/*      */       //   7: monitorenter
/*      */       //   8: aload_0
/*      */       //   9: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   12: aload_1
/*      */       //   13: iload_2
/*      */       //   14: aload_3
/*      */       //   15: iload 4
/*      */       //   17: invokeinterface 29 5 0
/*      */       //   22: aload_0
/*      */       //   23: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   26: invokestatic 14	com/google/common/collect/Synchronized:navigableMap	(Ljava/util/NavigableMap;Ljava/lang/Object;)Ljava/util/NavigableMap;
/*      */       //   29: aload 5
/*      */       //   31: monitorexit
/*      */       //   32: areturn
/*      */       //   33: astore 6
/*      */       //   35: aload 5
/*      */       //   37: monitorexit
/*      */       //   38: aload 6
/*      */       //   40: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1484	-> byte code offset #0
/*      */       //   Java source line #1485	-> byte code offset #8
/*      */       //   Java source line #1488	-> byte code offset #33
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	41	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	41	1	fromKey	K
/*      */       //   0	41	2	fromInclusive	boolean
/*      */       //   0	41	3	toKey	K
/*      */       //   0	41	4	toInclusive	boolean
/*      */       //   5	31	5	Ljava/lang/Object;	Object
/*      */       //   33	6	6	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   8	32	33	finally
/*      */       //   33	38	33	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_3
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
/*      */       //   11: aload_1
/*      */       //   12: iload_2
/*      */       //   13: invokeinterface 30 3 0
/*      */       //   18: aload_0
/*      */       //   19: getfield 4	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
/*      */       //   22: invokestatic 14	com/google/common/collect/Synchronized:navigableMap	(Ljava/util/NavigableMap;Ljava/lang/Object;)Ljava/util/NavigableMap;
/*      */       //   25: aload_3
/*      */       //   26: monitorexit
/*      */       //   27: areturn
/*      */       //   28: astore 4
/*      */       //   30: aload_3
/*      */       //   31: monitorexit
/*      */       //   32: aload 4
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1492	-> byte code offset #0
/*      */       //   Java source line #1493	-> byte code offset #7
/*      */       //   Java source line #1495	-> byte code offset #28
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	SynchronizedNavigableMap<K, V>
/*      */       //   0	35	1	fromKey	K
/*      */       //   0	35	2	inclusive	boolean
/*      */       //   5	26	3	Ljava/lang/Object;	Object
/*      */       //   28	5	4	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	27	28	finally
/*      */       //   28	32	28	finally
/*      */     }
/*      */     
/*      */     public SortedMap<K, V> headMap(K toKey)
/*      */     {
/* 1499 */       return headMap(toKey, false);
/*      */     }
/*      */     
/*      */     public SortedMap<K, V> subMap(K fromKey, K toKey) {
/* 1503 */       return subMap(fromKey, true, toKey, false);
/*      */     }
/*      */     
/*      */     public SortedMap<K, V> tailMap(K fromKey) {
/* 1507 */       return tailMap(fromKey, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("works but is needed only for NavigableMap")
/*      */   private static <K, V> Map.Entry<K, V> nullableSynchronizedEntry(@Nullable Map.Entry<K, V> entry, @Nullable Object mutex)
/*      */   {
/* 1516 */     if (entry == null) {
/* 1517 */       return null;
/*      */     }
/* 1519 */     return new SynchronizedEntry(entry, mutex);
/*      */   }
/*      */   
/*      */   @GwtIncompatible("works but is needed only for NavigableMap")
/*      */   private static class SynchronizedEntry<K, V> extends SynchronizedObject implements Entry<K, V> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedEntry(Entry<K, V> delegate, @Nullable Object mutex) {
/* 1527 */       super(mutex);
/*      */     }
/*      */     
/*      */     Entry<K, V> delegate()
/*      */     {
/* 1532 */       return (Entry)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean equals(Object obj)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 6 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1536	-> byte code offset #0
/*      */       //   Java source line #1537	-> byte code offset #7
/*      */       //   Java source line #1538	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedEntry<K, V>
/*      */       //   0	25	1	obj	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public int hashCode()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
/*      */       //   11: invokeinterface 7 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: ireturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1542	-> byte code offset #0
/*      */       //   Java source line #1543	-> byte code offset #7
/*      */       //   Java source line #1544	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedEntry<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public K getKey()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
/*      */       //   11: invokeinterface 8 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1548	-> byte code offset #0
/*      */       //   Java source line #1549	-> byte code offset #7
/*      */       //   Java source line #1550	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedEntry<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public V getValue()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
/*      */       //   11: invokeinterface 9 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1554	-> byte code offset #0
/*      */       //   Java source line #1555	-> byte code offset #7
/*      */       //   Java source line #1556	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedEntry<K, V>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public V setValue(V value)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 10 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1560	-> byte code offset #0
/*      */       //   Java source line #1561	-> byte code offset #7
/*      */       //   Java source line #1562	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedEntry<K, V>
/*      */       //   0	25	1	value	V
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */   }
/*      */   
/*      */   static <E> Queue<E> queue(Queue<E> queue, @Nullable Object mutex)
/*      */   {
/* 1569 */     return (queue instanceof SynchronizedQueue) ? queue : new SynchronizedQueue(queue, mutex);
/*      */   }
/*      */   
/*      */   private static class SynchronizedQueue<E> extends SynchronizedCollection<E> implements Queue<E>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedQueue(Queue<E> delegate, @Nullable Object mutex)
/*      */     {
/* 1578 */       super(mutex, null);
/*      */     }
/*      */     
/*      */     Queue<E> delegate() {
/* 1582 */       return (Queue)super.delegate();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E element()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
/*      */       //   11: invokeinterface 6 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1587	-> byte code offset #0
/*      */       //   Java source line #1588	-> byte code offset #7
/*      */       //   Java source line #1589	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedQueue<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean offer(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 7 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1594	-> byte code offset #0
/*      */       //   Java source line #1595	-> byte code offset #7
/*      */       //   Java source line #1596	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedQueue<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E peek()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
/*      */       //   11: invokeinterface 8 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1601	-> byte code offset #0
/*      */       //   Java source line #1602	-> byte code offset #7
/*      */       //   Java source line #1603	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedQueue<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E poll()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
/*      */       //   11: invokeinterface 9 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1608	-> byte code offset #0
/*      */       //   Java source line #1609	-> byte code offset #7
/*      */       //   Java source line #1610	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedQueue<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E remove()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
/*      */       //   11: invokeinterface 10 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1615	-> byte code offset #0
/*      */       //   Java source line #1616	-> byte code offset #7
/*      */       //   Java source line #1617	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedQueue<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */   
/*      */   @GwtIncompatible("Deque")
/*      */   static <E> Deque<E> deque(Deque<E> deque, @Nullable Object mutex)
/*      */   {
/* 1625 */     return new SynchronizedDeque(deque, mutex);
/*      */   }
/*      */   
/*      */   @GwtIncompatible("Deque")
/*      */   private static final class SynchronizedDeque<E> extends SynchronizedQueue<E> implements Deque<E> {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     SynchronizedDeque(Deque<E> delegate, @Nullable Object mutex) {
/* 1633 */       super(mutex);
/*      */     }
/*      */     
/*      */     Deque<E> delegate() {
/* 1637 */       return (Deque)super.delegate();
/*      */     }
/*      */     
/*      */     public void addFirst(E e)
/*      */     {
/* 1642 */       synchronized (this.mutex) {
/* 1643 */         delegate().addFirst(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public void addLast(E e)
/*      */     {
/* 1649 */       synchronized (this.mutex) {
/* 1650 */         delegate().addLast(e);
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean offerFirst(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 8 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1656	-> byte code offset #0
/*      */       //   Java source line #1657	-> byte code offset #7
/*      */       //   Java source line #1658	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedDeque<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean offerLast(E e)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 9 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1663	-> byte code offset #0
/*      */       //   Java source line #1664	-> byte code offset #7
/*      */       //   Java source line #1665	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedDeque<E>
/*      */       //   0	25	1	e	E
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E removeFirst()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 10 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1670	-> byte code offset #0
/*      */       //   Java source line #1671	-> byte code offset #7
/*      */       //   Java source line #1672	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E removeLast()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 11 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1677	-> byte code offset #0
/*      */       //   Java source line #1678	-> byte code offset #7
/*      */       //   Java source line #1679	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E pollFirst()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 12 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1684	-> byte code offset #0
/*      */       //   Java source line #1685	-> byte code offset #7
/*      */       //   Java source line #1686	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E pollLast()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 13 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1691	-> byte code offset #0
/*      */       //   Java source line #1692	-> byte code offset #7
/*      */       //   Java source line #1693	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E getFirst()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 14 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1698	-> byte code offset #0
/*      */       //   Java source line #1699	-> byte code offset #7
/*      */       //   Java source line #1700	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E getLast()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 15 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1705	-> byte code offset #0
/*      */       //   Java source line #1706	-> byte code offset #7
/*      */       //   Java source line #1707	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E peekFirst()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 16 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1712	-> byte code offset #0
/*      */       //   Java source line #1713	-> byte code offset #7
/*      */       //   Java source line #1714	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E peekLast()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 17 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1719	-> byte code offset #0
/*      */       //   Java source line #1720	-> byte code offset #7
/*      */       //   Java source line #1721	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean removeFirstOccurrence(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 18 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1726	-> byte code offset #0
/*      */       //   Java source line #1727	-> byte code offset #7
/*      */       //   Java source line #1728	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedDeque<E>
/*      */       //   0	25	1	o	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean removeLastOccurrence(Object o)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 19 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: ireturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1733	-> byte code offset #0
/*      */       //   Java source line #1734	-> byte code offset #7
/*      */       //   Java source line #1735	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	SynchronizedDeque<E>
/*      */       //   0	25	1	o	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public void push(E e)
/*      */     {
/* 1740 */       synchronized (this.mutex) {
/* 1741 */         delegate().push(e);
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public E pop()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 21 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1747	-> byte code offset #0
/*      */       //   Java source line #1748	-> byte code offset #7
/*      */       //   Java source line #1749	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Iterator<E> descendingIterator()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 4	com/google/common/collect/Synchronized$SynchronizedDeque:mutex	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: invokevirtual 5	com/google/common/collect/Synchronized$SynchronizedDeque:delegate	()Ljava/util/Deque;
/*      */       //   11: invokeinterface 22 1 0
/*      */       //   16: aload_1
/*      */       //   17: monitorexit
/*      */       //   18: areturn
/*      */       //   19: astore_2
/*      */       //   20: aload_1
/*      */       //   21: monitorexit
/*      */       //   22: aload_2
/*      */       //   23: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1754	-> byte code offset #0
/*      */       //   Java source line #1755	-> byte code offset #7
/*      */       //   Java source line #1756	-> byte code offset #19
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	24	0	this	SynchronizedDeque<E>
/*      */       //   5	16	1	Ljava/lang/Object;	Object
/*      */       //   19	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	18	19	finally
/*      */       //   19	22	19	finally
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\Synchronized.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */