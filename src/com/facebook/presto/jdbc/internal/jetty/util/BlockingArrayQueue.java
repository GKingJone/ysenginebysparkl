/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.ListIterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlockingArrayQueue<E>
/*     */   extends AbstractList<E>
/*     */   implements BlockingQueue<E>
/*     */ {
/*  56 */   private static final int HEAD_OFFSET = MemoryUtils.getIntegersPerCacheLine() - 1;
/*     */   
/*     */ 
/*     */ 
/*  60 */   private static final int TAIL_OFFSET = HEAD_OFFSET + MemoryUtils.getIntegersPerCacheLine();
/*     */   
/*     */ 
/*     */   public static final int DEFAULT_CAPACITY = 128;
/*     */   
/*     */ 
/*     */   public static final int DEFAULT_GROWTH = 64;
/*     */   
/*     */ 
/*     */   private final int _maxCapacity;
/*     */   
/*     */ 
/*     */   private final int _growCapacity;
/*     */   
/*     */ 
/*  75 */   private final int[] _indexes = new int[TAIL_OFFSET + 1];
/*  76 */   private final Lock _tailLock = new ReentrantLock();
/*  77 */   private final AtomicInteger _size = new AtomicInteger();
/*  78 */   private final Lock _headLock = new ReentrantLock();
/*  79 */   private final Condition _notEmpty = this._headLock.newCondition();
/*     */   
/*     */ 
/*     */ 
/*     */   private Object[] _elements;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockingArrayQueue()
/*     */   {
/*  90 */     this._elements = new Object[''];
/*  91 */     this._growCapacity = 64;
/*  92 */     this._maxCapacity = Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockingArrayQueue(int maxCapacity)
/*     */   {
/* 103 */     this._elements = new Object[maxCapacity];
/* 104 */     this._growCapacity = -1;
/* 105 */     this._maxCapacity = maxCapacity;
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
/*     */   public BlockingArrayQueue(int capacity, int growBy)
/*     */   {
/* 118 */     this._elements = new Object[capacity];
/* 119 */     this._growCapacity = growBy;
/* 120 */     this._maxCapacity = Integer.MAX_VALUE;
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
/*     */   public BlockingArrayQueue(int capacity, int growBy, int maxCapacity)
/*     */   {
/* 135 */     if (capacity > maxCapacity)
/* 136 */       throw new IllegalArgumentException();
/* 137 */     this._elements = new Object[capacity];
/* 138 */     this._growCapacity = growBy;
/* 139 */     this._maxCapacity = maxCapacity;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void clear()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   4: invokeinterface 84 1 0
/*     */     //   9: aload_0
/*     */     //   10: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   13: invokeinterface 84 1 0
/*     */     //   18: aload_0
/*     */     //   19: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   22: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   25: iconst_0
/*     */     //   26: iastore
/*     */     //   27: aload_0
/*     */     //   28: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   31: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   34: iconst_0
/*     */     //   35: iastore
/*     */     //   36: aload_0
/*     */     //   37: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   40: iconst_0
/*     */     //   41: invokevirtual 89	java/util/concurrent/atomic/AtomicInteger:set	(I)V
/*     */     //   44: aload_0
/*     */     //   45: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   48: invokeinterface 92 1 0
/*     */     //   53: goto +15 -> 68
/*     */     //   56: astore_1
/*     */     //   57: aload_0
/*     */     //   58: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   61: invokeinterface 92 1 0
/*     */     //   66: aload_1
/*     */     //   67: athrow
/*     */     //   68: aload_0
/*     */     //   69: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   72: invokeinterface 92 1 0
/*     */     //   77: goto +15 -> 92
/*     */     //   80: astore_2
/*     */     //   81: aload_0
/*     */     //   82: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   85: invokeinterface 92 1 0
/*     */     //   90: aload_2
/*     */     //   91: athrow
/*     */     //   92: return
/*     */     // Line number table:
/*     */     //   Java source line #150	-> byte code offset #0
/*     */     //   Java source line #154	-> byte code offset #9
/*     */     //   Java source line #157	-> byte code offset #18
/*     */     //   Java source line #158	-> byte code offset #27
/*     */     //   Java source line #159	-> byte code offset #36
/*     */     //   Java source line #163	-> byte code offset #44
/*     */     //   Java source line #164	-> byte code offset #53
/*     */     //   Java source line #163	-> byte code offset #56
/*     */     //   Java source line #168	-> byte code offset #68
/*     */     //   Java source line #169	-> byte code offset #77
/*     */     //   Java source line #168	-> byte code offset #80
/*     */     //   Java source line #170	-> byte code offset #92
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	93	0	this	BlockingArrayQueue<E>
/*     */     //   56	11	1	localObject1	Object
/*     */     //   80	11	2	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	44	56	finally
/*     */     //   9	68	80	finally
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/* 175 */     return this._size.get();
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<E> iterator()
/*     */   {
/* 181 */     return listIterator();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public E poll()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   4: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   7: ifne +5 -> 12
/*     */     //   10: aconst_null
/*     */     //   11: areturn
/*     */     //   12: aconst_null
/*     */     //   13: astore_1
/*     */     //   14: aload_0
/*     */     //   15: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   18: invokeinterface 84 1 0
/*     */     //   23: aload_0
/*     */     //   24: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   27: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   30: ifle +62 -> 92
/*     */     //   33: aload_0
/*     */     //   34: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   37: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   40: iaload
/*     */     //   41: istore_2
/*     */     //   42: aload_0
/*     */     //   43: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   46: iload_2
/*     */     //   47: aaload
/*     */     //   48: astore_1
/*     */     //   49: aload_0
/*     */     //   50: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   53: iload_2
/*     */     //   54: aconst_null
/*     */     //   55: aastore
/*     */     //   56: aload_0
/*     */     //   57: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   60: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   63: iload_2
/*     */     //   64: iconst_1
/*     */     //   65: iadd
/*     */     //   66: aload_0
/*     */     //   67: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   70: arraylength
/*     */     //   71: irem
/*     */     //   72: iastore
/*     */     //   73: aload_0
/*     */     //   74: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   77: invokevirtual 110	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */     //   80: ifle +12 -> 92
/*     */     //   83: aload_0
/*     */     //   84: getfield 59	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_notEmpty	Ljava/util/concurrent/locks/Condition;
/*     */     //   87: invokeinterface 115 1 0
/*     */     //   92: aload_0
/*     */     //   93: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   96: invokeinterface 92 1 0
/*     */     //   101: goto +15 -> 116
/*     */     //   104: astore_3
/*     */     //   105: aload_0
/*     */     //   106: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   109: invokeinterface 92 1 0
/*     */     //   114: aload_3
/*     */     //   115: athrow
/*     */     //   116: aload_1
/*     */     //   117: areturn
/*     */     // Line number table:
/*     */     //   Java source line #192	-> byte code offset #0
/*     */     //   Java source line #193	-> byte code offset #10
/*     */     //   Java source line #195	-> byte code offset #12
/*     */     //   Java source line #197	-> byte code offset #14
/*     */     //   Java source line #200	-> byte code offset #23
/*     */     //   Java source line #202	-> byte code offset #33
/*     */     //   Java source line #203	-> byte code offset #42
/*     */     //   Java source line #204	-> byte code offset #49
/*     */     //   Java source line #205	-> byte code offset #56
/*     */     //   Java source line #206	-> byte code offset #73
/*     */     //   Java source line #207	-> byte code offset #83
/*     */     //   Java source line #212	-> byte code offset #92
/*     */     //   Java source line #213	-> byte code offset #101
/*     */     //   Java source line #212	-> byte code offset #104
/*     */     //   Java source line #214	-> byte code offset #116
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	118	0	this	BlockingArrayQueue<E>
/*     */     //   13	104	1	e	E
/*     */     //   41	23	2	head	int
/*     */     //   104	11	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   23	92	104	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public E peek()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   4: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   7: ifne +5 -> 12
/*     */     //   10: aconst_null
/*     */     //   11: areturn
/*     */     //   12: aconst_null
/*     */     //   13: astore_1
/*     */     //   14: aload_0
/*     */     //   15: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   18: invokeinterface 84 1 0
/*     */     //   23: aload_0
/*     */     //   24: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   27: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   30: ifle +17 -> 47
/*     */     //   33: aload_0
/*     */     //   34: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   37: aload_0
/*     */     //   38: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   41: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   44: iaload
/*     */     //   45: aaload
/*     */     //   46: astore_1
/*     */     //   47: aload_0
/*     */     //   48: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   51: invokeinterface 92 1 0
/*     */     //   56: goto +15 -> 71
/*     */     //   59: astore_2
/*     */     //   60: aload_0
/*     */     //   61: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   64: invokeinterface 92 1 0
/*     */     //   69: aload_2
/*     */     //   70: athrow
/*     */     //   71: aload_1
/*     */     //   72: areturn
/*     */     // Line number table:
/*     */     //   Java source line #221	-> byte code offset #0
/*     */     //   Java source line #222	-> byte code offset #10
/*     */     //   Java source line #224	-> byte code offset #12
/*     */     //   Java source line #226	-> byte code offset #14
/*     */     //   Java source line #229	-> byte code offset #23
/*     */     //   Java source line #230	-> byte code offset #33
/*     */     //   Java source line #234	-> byte code offset #47
/*     */     //   Java source line #235	-> byte code offset #56
/*     */     //   Java source line #234	-> byte code offset #59
/*     */     //   Java source line #236	-> byte code offset #71
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	73	0	this	BlockingArrayQueue<E>
/*     */     //   13	59	1	e	E
/*     */     //   59	11	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   23	47	59	finally
/*     */   }
/*     */   
/*     */   public E remove()
/*     */   {
/* 242 */     E e = poll();
/* 243 */     if (e == null)
/* 244 */       throw new NoSuchElementException();
/* 245 */     return e;
/*     */   }
/*     */   
/*     */ 
/*     */   public E element()
/*     */   {
/* 251 */     E e = peek();
/* 252 */     if (e == null)
/* 253 */       throw new NoSuchElementException();
/* 254 */     return e;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean offer(E e)
/*     */   {
/* 264 */     Objects.requireNonNull(e);
/*     */     
/* 266 */     boolean notEmpty = false;
/* 267 */     this._tailLock.lock();
/*     */     try
/*     */     {
/* 270 */       int size = this._size.get();
/* 271 */       boolean bool1; if (size >= this._maxCapacity) {
/* 272 */         return false;
/*     */       }
/*     */       
/* 275 */       if (size == this._elements.length)
/*     */       {
/* 277 */         this._headLock.lock();
/*     */         try
/*     */         {
/* 280 */           if (!grow()) {
/* 281 */             bool1 = false;
/*     */             
/*     */ 
/*     */ 
/* 285 */             this._headLock.unlock();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 297 */             return bool1;
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 285 */           this._headLock.unlock();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 290 */       int tail = this._indexes[TAIL_OFFSET];
/* 291 */       this._elements[tail] = e;
/* 292 */       this._indexes[TAIL_OFFSET] = ((tail + 1) % this._elements.length);
/* 293 */       notEmpty = this._size.getAndIncrement() == 0;
/*     */     }
/*     */     finally
/*     */     {
/* 297 */       this._tailLock.unlock();
/*     */     }
/*     */     
/* 300 */     if (notEmpty)
/*     */     {
/* 302 */       this._headLock.lock();
/*     */       try
/*     */       {
/* 305 */         this._notEmpty.signal();
/*     */       }
/*     */       finally
/*     */       {
/* 309 */         this._headLock.unlock();
/*     */       }
/*     */     }
/*     */     
/* 313 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean add(E e)
/*     */   {
/* 319 */     if (offer(e)) {
/* 320 */       return true;
/*     */     }
/* 322 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */ 
/*     */   public void put(E o)
/*     */     throws InterruptedException
/*     */   {
/* 329 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean offer(E o, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 336 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public E take()
/*     */     throws InterruptedException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aconst_null
/*     */     //   1: astore_1
/*     */     //   2: aload_0
/*     */     //   3: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   6: invokeinterface 170 1 0
/*     */     //   11: aload_0
/*     */     //   12: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   15: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   18: ifne +15 -> 33
/*     */     //   21: aload_0
/*     */     //   22: getfield 59	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_notEmpty	Ljava/util/concurrent/locks/Condition;
/*     */     //   25: invokeinterface 173 1 0
/*     */     //   30: goto -19 -> 11
/*     */     //   33: goto +15 -> 48
/*     */     //   36: astore_2
/*     */     //   37: aload_0
/*     */     //   38: getfield 59	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_notEmpty	Ljava/util/concurrent/locks/Condition;
/*     */     //   41: invokeinterface 115 1 0
/*     */     //   46: aload_2
/*     */     //   47: athrow
/*     */     //   48: aload_0
/*     */     //   49: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   52: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   55: iaload
/*     */     //   56: istore_2
/*     */     //   57: aload_0
/*     */     //   58: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   61: iload_2
/*     */     //   62: aaload
/*     */     //   63: astore_1
/*     */     //   64: aload_0
/*     */     //   65: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   68: iload_2
/*     */     //   69: aconst_null
/*     */     //   70: aastore
/*     */     //   71: aload_0
/*     */     //   72: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   75: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   78: iload_2
/*     */     //   79: iconst_1
/*     */     //   80: iadd
/*     */     //   81: aload_0
/*     */     //   82: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   85: arraylength
/*     */     //   86: irem
/*     */     //   87: iastore
/*     */     //   88: aload_0
/*     */     //   89: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   92: invokevirtual 110	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */     //   95: ifle +12 -> 107
/*     */     //   98: aload_0
/*     */     //   99: getfield 59	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_notEmpty	Ljava/util/concurrent/locks/Condition;
/*     */     //   102: invokeinterface 115 1 0
/*     */     //   107: aload_0
/*     */     //   108: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   111: invokeinterface 92 1 0
/*     */     //   116: goto +15 -> 131
/*     */     //   119: astore_3
/*     */     //   120: aload_0
/*     */     //   121: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   124: invokeinterface 92 1 0
/*     */     //   129: aload_3
/*     */     //   130: athrow
/*     */     //   131: aload_1
/*     */     //   132: areturn
/*     */     // Line number table:
/*     */     //   Java source line #343	-> byte code offset #0
/*     */     //   Java source line #345	-> byte code offset #2
/*     */     //   Java source line #350	-> byte code offset #11
/*     */     //   Java source line #352	-> byte code offset #21
/*     */     //   Java source line #359	-> byte code offset #33
/*     */     //   Java source line #355	-> byte code offset #36
/*     */     //   Java source line #357	-> byte code offset #37
/*     */     //   Java source line #358	-> byte code offset #46
/*     */     //   Java source line #361	-> byte code offset #48
/*     */     //   Java source line #362	-> byte code offset #57
/*     */     //   Java source line #363	-> byte code offset #64
/*     */     //   Java source line #364	-> byte code offset #71
/*     */     //   Java source line #366	-> byte code offset #88
/*     */     //   Java source line #367	-> byte code offset #98
/*     */     //   Java source line #371	-> byte code offset #107
/*     */     //   Java source line #372	-> byte code offset #116
/*     */     //   Java source line #371	-> byte code offset #119
/*     */     //   Java source line #373	-> byte code offset #131
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	133	0	this	BlockingArrayQueue<E>
/*     */     //   1	131	1	e	E
/*     */     //   36	11	2	ie	InterruptedException
/*     */     //   56	23	2	head	int
/*     */     //   119	11	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   11	33	36	java/lang/InterruptedException
/*     */     //   11	107	119	finally
/*     */   }
/*     */   
/*     */   public E poll(long time, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 380 */     long nanos = unit.toNanos(time);
/* 381 */     E e = null;
/*     */     
/* 383 */     this._headLock.lockInterruptibly();
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/* 388 */         while (this._size.get() == 0)
/*     */         {
/* 390 */           if (nanos <= 0L)
/* 391 */             return null;
/* 392 */           nanos = this._notEmpty.awaitNanos(nanos);
/*     */         }
/*     */       }
/*     */       catch (InterruptedException x)
/*     */       {
/* 397 */         this._notEmpty.signal();
/* 398 */         throw x;
/*     */       }
/*     */       
/* 401 */       int head = this._indexes[HEAD_OFFSET];
/* 402 */       e = this._elements[head];
/* 403 */       this._elements[head] = null;
/* 404 */       this._indexes[HEAD_OFFSET] = ((head + 1) % this._elements.length);
/*     */       
/* 406 */       if (this._size.decrementAndGet() > 0) {
/* 407 */         this._notEmpty.signal();
/*     */       }
/*     */     }
/*     */     finally {
/* 411 */       this._headLock.unlock();
/*     */     }
/* 413 */     return e;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean remove(Object o)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   4: invokeinterface 84 1 0
/*     */     //   9: aload_0
/*     */     //   10: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   13: invokeinterface 84 1 0
/*     */     //   18: aload_0
/*     */     //   19: invokevirtual 191	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:isEmpty	()Z
/*     */     //   22: ifeq +25 -> 47
/*     */     //   25: iconst_0
/*     */     //   26: istore_2
/*     */     //   27: aload_0
/*     */     //   28: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   31: invokeinterface 92 1 0
/*     */     //   36: aload_0
/*     */     //   37: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   40: invokeinterface 92 1 0
/*     */     //   45: iload_2
/*     */     //   46: ireturn
/*     */     //   47: aload_0
/*     */     //   48: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   51: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   54: iaload
/*     */     //   55: istore_2
/*     */     //   56: aload_0
/*     */     //   57: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   60: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   63: iaload
/*     */     //   64: istore_3
/*     */     //   65: aload_0
/*     */     //   66: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   69: arraylength
/*     */     //   70: istore 4
/*     */     //   72: iload_2
/*     */     //   73: istore 5
/*     */     //   75: aload_0
/*     */     //   76: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   79: iload 5
/*     */     //   81: aaload
/*     */     //   82: aload_1
/*     */     //   83: invokestatic 195	java/util/Objects:equals	(Ljava/lang/Object;Ljava/lang/Object;)Z
/*     */     //   86: ifeq +52 -> 138
/*     */     //   89: aload_0
/*     */     //   90: iload 5
/*     */     //   92: iload_2
/*     */     //   93: if_icmplt +10 -> 103
/*     */     //   96: iload 5
/*     */     //   98: iload_2
/*     */     //   99: isub
/*     */     //   100: goto +10 -> 110
/*     */     //   103: iload 4
/*     */     //   105: iload_2
/*     */     //   106: isub
/*     */     //   107: iload 5
/*     */     //   109: iadd
/*     */     //   110: invokevirtual 198	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:remove	(I)Ljava/lang/Object;
/*     */     //   113: pop
/*     */     //   114: iconst_1
/*     */     //   115: istore 6
/*     */     //   117: aload_0
/*     */     //   118: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   121: invokeinterface 92 1 0
/*     */     //   126: aload_0
/*     */     //   127: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   130: invokeinterface 92 1 0
/*     */     //   135: iload 6
/*     */     //   137: ireturn
/*     */     //   138: iinc 5 1
/*     */     //   141: iload 5
/*     */     //   143: iload 4
/*     */     //   145: if_icmpne +6 -> 151
/*     */     //   148: iconst_0
/*     */     //   149: istore 5
/*     */     //   151: iload 5
/*     */     //   153: iload_3
/*     */     //   154: if_icmpne -79 -> 75
/*     */     //   157: iconst_0
/*     */     //   158: istore 6
/*     */     //   160: aload_0
/*     */     //   161: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   164: invokeinterface 92 1 0
/*     */     //   169: aload_0
/*     */     //   170: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   173: invokeinterface 92 1 0
/*     */     //   178: iload 6
/*     */     //   180: ireturn
/*     */     //   181: astore 7
/*     */     //   183: aload_0
/*     */     //   184: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   187: invokeinterface 92 1 0
/*     */     //   192: aload 7
/*     */     //   194: athrow
/*     */     //   195: astore 8
/*     */     //   197: aload_0
/*     */     //   198: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   201: invokeinterface 92 1 0
/*     */     //   206: aload 8
/*     */     //   208: athrow
/*     */     // Line number table:
/*     */     //   Java source line #420	-> byte code offset #0
/*     */     //   Java source line #424	-> byte code offset #9
/*     */     //   Java source line #427	-> byte code offset #18
/*     */     //   Java source line #428	-> byte code offset #25
/*     */     //   Java source line #451	-> byte code offset #27
/*     */     //   Java source line #456	-> byte code offset #36
/*     */     //   Java source line #430	-> byte code offset #47
/*     */     //   Java source line #431	-> byte code offset #56
/*     */     //   Java source line #432	-> byte code offset #65
/*     */     //   Java source line #434	-> byte code offset #72
/*     */     //   Java source line #437	-> byte code offset #75
/*     */     //   Java source line #439	-> byte code offset #89
/*     */     //   Java source line #440	-> byte code offset #114
/*     */     //   Java source line #451	-> byte code offset #117
/*     */     //   Java source line #456	-> byte code offset #126
/*     */     //   Java source line #442	-> byte code offset #138
/*     */     //   Java source line #443	-> byte code offset #141
/*     */     //   Java source line #444	-> byte code offset #148
/*     */     //   Java source line #445	-> byte code offset #151
/*     */     //   Java source line #446	-> byte code offset #157
/*     */     //   Java source line #451	-> byte code offset #160
/*     */     //   Java source line #456	-> byte code offset #169
/*     */     //   Java source line #451	-> byte code offset #181
/*     */     //   Java source line #456	-> byte code offset #195
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	209	0	this	BlockingArrayQueue<E>
/*     */     //   0	209	1	o	Object
/*     */     //   26	20	2	bool1	boolean
/*     */     //   55	52	2	head	int
/*     */     //   64	90	3	tail	int
/*     */     //   70	74	4	capacity	int
/*     */     //   73	79	5	i	int
/*     */     //   115	64	6	bool2	boolean
/*     */     //   181	12	7	localObject1	Object
/*     */     //   195	12	8	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	27	181	finally
/*     */     //   47	117	181	finally
/*     */     //   138	160	181	finally
/*     */     //   181	183	181	finally
/*     */     //   9	36	195	finally
/*     */     //   47	126	195	finally
/*     */     //   138	169	195	finally
/*     */     //   181	197	195	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int remainingCapacity()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   4: invokeinterface 84 1 0
/*     */     //   9: aload_0
/*     */     //   10: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   13: invokeinterface 84 1 0
/*     */     //   18: aload_0
/*     */     //   19: invokevirtual 203	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:getCapacity	()I
/*     */     //   22: aload_0
/*     */     //   23: invokevirtual 205	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:size	()I
/*     */     //   26: isub
/*     */     //   27: istore_1
/*     */     //   28: aload_0
/*     */     //   29: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   32: invokeinterface 92 1 0
/*     */     //   37: aload_0
/*     */     //   38: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   41: invokeinterface 92 1 0
/*     */     //   46: iload_1
/*     */     //   47: ireturn
/*     */     //   48: astore_2
/*     */     //   49: aload_0
/*     */     //   50: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   53: invokeinterface 92 1 0
/*     */     //   58: aload_2
/*     */     //   59: athrow
/*     */     //   60: astore_3
/*     */     //   61: aload_0
/*     */     //   62: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   65: invokeinterface 92 1 0
/*     */     //   70: aload_3
/*     */     //   71: athrow
/*     */     // Line number table:
/*     */     //   Java source line #464	-> byte code offset #0
/*     */     //   Java source line #468	-> byte code offset #9
/*     */     //   Java source line #471	-> byte code offset #18
/*     */     //   Java source line #475	-> byte code offset #28
/*     */     //   Java source line #480	-> byte code offset #37
/*     */     //   Java source line #475	-> byte code offset #48
/*     */     //   Java source line #480	-> byte code offset #60
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	72	0	this	BlockingArrayQueue<E>
/*     */     //   27	20	1	i	int
/*     */     //   48	11	2	localObject1	Object
/*     */     //   60	11	3	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	28	48	finally
/*     */     //   9	37	60	finally
/*     */     //   48	61	60	finally
/*     */   }
/*     */   
/*     */   public int drainTo(Collection<? super E> c)
/*     */   {
/* 487 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public int drainTo(Collection<? super E> c, int maxElements)
/*     */   {
/* 493 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public E get(int index)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   4: invokeinterface 84 1 0
/*     */     //   9: aload_0
/*     */     //   10: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   13: invokeinterface 84 1 0
/*     */     //   18: iload_1
/*     */     //   19: iflt +14 -> 33
/*     */     //   22: iload_1
/*     */     //   23: aload_0
/*     */     //   24: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   27: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   30: if_icmplt +47 -> 77
/*     */     //   33: new 214	java/lang/IndexOutOfBoundsException
/*     */     //   36: dup
/*     */     //   37: new 216	java/lang/StringBuilder
/*     */     //   40: dup
/*     */     //   41: invokespecial 217	java/lang/StringBuilder:<init>	()V
/*     */     //   44: ldc -37
/*     */     //   46: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   49: iload_1
/*     */     //   50: invokevirtual 226	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   53: ldc -28
/*     */     //   55: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   58: aload_0
/*     */     //   59: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   62: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   65: ldc -23
/*     */     //   67: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   70: invokevirtual 237	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   73: invokespecial 240	java/lang/IndexOutOfBoundsException:<init>	(Ljava/lang/String;)V
/*     */     //   76: athrow
/*     */     //   77: aload_0
/*     */     //   78: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   81: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   84: iaload
/*     */     //   85: iload_1
/*     */     //   86: iadd
/*     */     //   87: istore_2
/*     */     //   88: aload_0
/*     */     //   89: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   92: arraylength
/*     */     //   93: istore_3
/*     */     //   94: iload_2
/*     */     //   95: iload_3
/*     */     //   96: if_icmplt +7 -> 103
/*     */     //   99: iload_2
/*     */     //   100: iload_3
/*     */     //   101: isub
/*     */     //   102: istore_2
/*     */     //   103: aload_0
/*     */     //   104: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   107: iload_2
/*     */     //   108: aaload
/*     */     //   109: astore 4
/*     */     //   111: aload_0
/*     */     //   112: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   115: invokeinterface 92 1 0
/*     */     //   120: aload_0
/*     */     //   121: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   124: invokeinterface 92 1 0
/*     */     //   129: aload 4
/*     */     //   131: areturn
/*     */     //   132: astore 5
/*     */     //   134: aload_0
/*     */     //   135: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   138: invokeinterface 92 1 0
/*     */     //   143: aload 5
/*     */     //   145: athrow
/*     */     //   146: astore 6
/*     */     //   148: aload_0
/*     */     //   149: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   152: invokeinterface 92 1 0
/*     */     //   157: aload 6
/*     */     //   159: athrow
/*     */     // Line number table:
/*     */     //   Java source line #505	-> byte code offset #0
/*     */     //   Java source line #509	-> byte code offset #9
/*     */     //   Java source line #512	-> byte code offset #18
/*     */     //   Java source line #513	-> byte code offset #33
/*     */     //   Java source line #514	-> byte code offset #77
/*     */     //   Java source line #515	-> byte code offset #88
/*     */     //   Java source line #516	-> byte code offset #94
/*     */     //   Java source line #517	-> byte code offset #99
/*     */     //   Java source line #518	-> byte code offset #103
/*     */     //   Java source line #522	-> byte code offset #111
/*     */     //   Java source line #527	-> byte code offset #120
/*     */     //   Java source line #522	-> byte code offset #132
/*     */     //   Java source line #527	-> byte code offset #146
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	160	0	this	BlockingArrayQueue<E>
/*     */     //   0	160	1	index	int
/*     */     //   87	21	2	i	int
/*     */     //   93	8	3	capacity	int
/*     */     //   109	21	4	localObject1	Object
/*     */     //   132	12	5	localObject2	Object
/*     */     //   146	12	6	localObject3	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	111	132	finally
/*     */     //   132	134	132	finally
/*     */     //   9	120	146	finally
/*     */     //   132	148	146	finally
/*     */   }
/*     */   
/*     */   public void add(int index, E e)
/*     */   {
/* 534 */     if (e == null) {
/* 535 */       throw new NullPointerException();
/*     */     }
/* 537 */     this._tailLock.lock();
/*     */     
/*     */     try
/*     */     {
/* 541 */       this._headLock.lock();
/*     */       try
/*     */       {
/* 544 */         int size = this._size.get();
/*     */         
/* 546 */         if ((index < 0) || (index > size)) {
/* 547 */           throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
/*     */         }
/* 549 */         if (index == size)
/*     */         {
/* 551 */           add(e);
/*     */         }
/*     */         else
/*     */         {
/* 555 */           if ((this._indexes[TAIL_OFFSET] == this._indexes[HEAD_OFFSET]) && 
/* 556 */             (!grow())) {
/* 557 */             throw new IllegalStateException("full");
/*     */           }
/*     */           
/* 560 */           int i = this._indexes[HEAD_OFFSET] + index;
/* 561 */           int capacity = this._elements.length;
/*     */           
/* 563 */           if (i >= capacity) {
/* 564 */             i -= capacity;
/*     */           }
/* 566 */           this._size.incrementAndGet();
/* 567 */           int tail = this._indexes[TAIL_OFFSET];
/* 568 */           this._indexes[TAIL_OFFSET] = (tail = (tail + 1) % capacity);
/*     */           
/* 570 */           if (i < tail)
/*     */           {
/* 572 */             System.arraycopy(this._elements, i, this._elements, i + 1, tail - i);
/* 573 */             this._elements[i] = e;
/*     */           }
/*     */           else
/*     */           {
/* 577 */             if (tail > 0)
/*     */             {
/* 579 */               System.arraycopy(this._elements, 0, this._elements, 1, tail);
/* 580 */               this._elements[0] = this._elements[(capacity - 1)];
/*     */             }
/*     */             
/* 583 */             System.arraycopy(this._elements, i, this._elements, i + 1, capacity - i - 1);
/* 584 */             this._elements[i] = e;
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 590 */         this._headLock.unlock();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 595 */       this._tailLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public E set(int index, E e)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_2
/*     */     //   1: invokestatic 137	java/util/Objects:requireNonNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   4: pop
/*     */     //   5: aload_0
/*     */     //   6: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   9: invokeinterface 84 1 0
/*     */     //   14: aload_0
/*     */     //   15: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   18: invokeinterface 84 1 0
/*     */     //   23: iload_1
/*     */     //   24: iflt +14 -> 38
/*     */     //   27: iload_1
/*     */     //   28: aload_0
/*     */     //   29: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   32: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   35: if_icmplt +47 -> 82
/*     */     //   38: new 214	java/lang/IndexOutOfBoundsException
/*     */     //   41: dup
/*     */     //   42: new 216	java/lang/StringBuilder
/*     */     //   45: dup
/*     */     //   46: invokespecial 217	java/lang/StringBuilder:<init>	()V
/*     */     //   49: ldc -37
/*     */     //   51: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   54: iload_1
/*     */     //   55: invokevirtual 226	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   58: ldc -28
/*     */     //   60: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   63: aload_0
/*     */     //   64: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   67: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   70: ldc -23
/*     */     //   72: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   75: invokevirtual 237	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   78: invokespecial 240	java/lang/IndexOutOfBoundsException:<init>	(Ljava/lang/String;)V
/*     */     //   81: athrow
/*     */     //   82: aload_0
/*     */     //   83: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   86: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   89: iaload
/*     */     //   90: iload_1
/*     */     //   91: iadd
/*     */     //   92: istore_3
/*     */     //   93: aload_0
/*     */     //   94: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   97: arraylength
/*     */     //   98: istore 4
/*     */     //   100: iload_3
/*     */     //   101: iload 4
/*     */     //   103: if_icmplt +8 -> 111
/*     */     //   106: iload_3
/*     */     //   107: iload 4
/*     */     //   109: isub
/*     */     //   110: istore_3
/*     */     //   111: aload_0
/*     */     //   112: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   115: iload_3
/*     */     //   116: aaload
/*     */     //   117: astore 5
/*     */     //   119: aload_0
/*     */     //   120: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   123: iload_3
/*     */     //   124: aload_2
/*     */     //   125: aastore
/*     */     //   126: aload 5
/*     */     //   128: astore 6
/*     */     //   130: aload_0
/*     */     //   131: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   134: invokeinterface 92 1 0
/*     */     //   139: aload_0
/*     */     //   140: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   143: invokeinterface 92 1 0
/*     */     //   148: aload 6
/*     */     //   150: areturn
/*     */     //   151: astore 7
/*     */     //   153: aload_0
/*     */     //   154: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   157: invokeinterface 92 1 0
/*     */     //   162: aload 7
/*     */     //   164: athrow
/*     */     //   165: astore 8
/*     */     //   167: aload_0
/*     */     //   168: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   171: invokeinterface 92 1 0
/*     */     //   176: aload 8
/*     */     //   178: athrow
/*     */     // Line number table:
/*     */     //   Java source line #603	-> byte code offset #0
/*     */     //   Java source line #605	-> byte code offset #5
/*     */     //   Java source line #609	-> byte code offset #14
/*     */     //   Java source line #612	-> byte code offset #23
/*     */     //   Java source line #613	-> byte code offset #38
/*     */     //   Java source line #615	-> byte code offset #82
/*     */     //   Java source line #616	-> byte code offset #93
/*     */     //   Java source line #617	-> byte code offset #100
/*     */     //   Java source line #618	-> byte code offset #106
/*     */     //   Java source line #619	-> byte code offset #111
/*     */     //   Java source line #620	-> byte code offset #119
/*     */     //   Java source line #621	-> byte code offset #126
/*     */     //   Java source line #625	-> byte code offset #130
/*     */     //   Java source line #630	-> byte code offset #139
/*     */     //   Java source line #625	-> byte code offset #151
/*     */     //   Java source line #630	-> byte code offset #165
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	179	0	this	BlockingArrayQueue<E>
/*     */     //   0	179	1	index	int
/*     */     //   0	179	2	e	E
/*     */     //   92	32	3	i	int
/*     */     //   98	10	4	capacity	int
/*     */     //   117	10	5	old	E
/*     */     //   128	21	6	?	E
/*     */     //   151	12	7	localObject1	Object
/*     */     //   165	12	8	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   23	130	151	finally
/*     */     //   151	153	151	finally
/*     */     //   14	139	165	finally
/*     */     //   151	167	165	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public E remove(int index)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   4: invokeinterface 84 1 0
/*     */     //   9: aload_0
/*     */     //   10: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   13: invokeinterface 84 1 0
/*     */     //   18: iload_1
/*     */     //   19: iflt +14 -> 33
/*     */     //   22: iload_1
/*     */     //   23: aload_0
/*     */     //   24: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   27: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   30: if_icmplt +47 -> 77
/*     */     //   33: new 214	java/lang/IndexOutOfBoundsException
/*     */     //   36: dup
/*     */     //   37: new 216	java/lang/StringBuilder
/*     */     //   40: dup
/*     */     //   41: invokespecial 217	java/lang/StringBuilder:<init>	()V
/*     */     //   44: ldc -37
/*     */     //   46: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   49: iload_1
/*     */     //   50: invokevirtual 226	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   53: ldc -28
/*     */     //   55: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   58: aload_0
/*     */     //   59: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   62: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   65: ldc -23
/*     */     //   67: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   70: invokevirtual 237	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   73: invokespecial 240	java/lang/IndexOutOfBoundsException:<init>	(Ljava/lang/String;)V
/*     */     //   76: athrow
/*     */     //   77: aload_0
/*     */     //   78: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   81: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   84: iaload
/*     */     //   85: iload_1
/*     */     //   86: iadd
/*     */     //   87: istore_2
/*     */     //   88: aload_0
/*     */     //   89: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   92: arraylength
/*     */     //   93: istore_3
/*     */     //   94: iload_2
/*     */     //   95: iload_3
/*     */     //   96: if_icmplt +7 -> 103
/*     */     //   99: iload_2
/*     */     //   100: iload_3
/*     */     //   101: isub
/*     */     //   102: istore_2
/*     */     //   103: aload_0
/*     */     //   104: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   107: iload_2
/*     */     //   108: aaload
/*     */     //   109: astore 4
/*     */     //   111: aload_0
/*     */     //   112: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   115: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   118: iaload
/*     */     //   119: istore 5
/*     */     //   121: iload_2
/*     */     //   122: iload 5
/*     */     //   124: if_icmpge +37 -> 161
/*     */     //   127: aload_0
/*     */     //   128: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   131: iload_2
/*     */     //   132: iconst_1
/*     */     //   133: iadd
/*     */     //   134: aload_0
/*     */     //   135: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   138: iload_2
/*     */     //   139: iload 5
/*     */     //   141: iload_2
/*     */     //   142: isub
/*     */     //   143: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   146: aload_0
/*     */     //   147: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   150: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   153: dup2
/*     */     //   154: iaload
/*     */     //   155: iconst_1
/*     */     //   156: isub
/*     */     //   157: iastore
/*     */     //   158: goto +97 -> 255
/*     */     //   161: aload_0
/*     */     //   162: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   165: iload_2
/*     */     //   166: iconst_1
/*     */     //   167: iadd
/*     */     //   168: aload_0
/*     */     //   169: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   172: iload_2
/*     */     //   173: iload_3
/*     */     //   174: iload_2
/*     */     //   175: isub
/*     */     //   176: iconst_1
/*     */     //   177: isub
/*     */     //   178: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   181: aload_0
/*     */     //   182: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   185: iload_3
/*     */     //   186: iconst_1
/*     */     //   187: isub
/*     */     //   188: aload_0
/*     */     //   189: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   192: iconst_0
/*     */     //   193: aaload
/*     */     //   194: aastore
/*     */     //   195: iload 5
/*     */     //   197: ifle +33 -> 230
/*     */     //   200: aload_0
/*     */     //   201: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   204: iconst_1
/*     */     //   205: aload_0
/*     */     //   206: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   209: iconst_0
/*     */     //   210: iload 5
/*     */     //   212: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   215: aload_0
/*     */     //   216: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   219: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   222: dup2
/*     */     //   223: iaload
/*     */     //   224: iconst_1
/*     */     //   225: isub
/*     */     //   226: iastore
/*     */     //   227: goto +14 -> 241
/*     */     //   230: aload_0
/*     */     //   231: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   234: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   237: iload_3
/*     */     //   238: iconst_1
/*     */     //   239: isub
/*     */     //   240: iastore
/*     */     //   241: aload_0
/*     */     //   242: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   245: aload_0
/*     */     //   246: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   249: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   252: iaload
/*     */     //   253: aconst_null
/*     */     //   254: aastore
/*     */     //   255: aload_0
/*     */     //   256: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   259: invokevirtual 110	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */     //   262: pop
/*     */     //   263: aload 4
/*     */     //   265: astore 6
/*     */     //   267: aload_0
/*     */     //   268: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   271: invokeinterface 92 1 0
/*     */     //   276: aload_0
/*     */     //   277: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   280: invokeinterface 92 1 0
/*     */     //   285: aload 6
/*     */     //   287: areturn
/*     */     //   288: astore 7
/*     */     //   290: aload_0
/*     */     //   291: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   294: invokeinterface 92 1 0
/*     */     //   299: aload 7
/*     */     //   301: athrow
/*     */     //   302: astore 8
/*     */     //   304: aload_0
/*     */     //   305: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   308: invokeinterface 92 1 0
/*     */     //   313: aload 8
/*     */     //   315: athrow
/*     */     // Line number table:
/*     */     //   Java source line #639	-> byte code offset #0
/*     */     //   Java source line #643	-> byte code offset #9
/*     */     //   Java source line #646	-> byte code offset #18
/*     */     //   Java source line #647	-> byte code offset #33
/*     */     //   Java source line #649	-> byte code offset #77
/*     */     //   Java source line #650	-> byte code offset #88
/*     */     //   Java source line #651	-> byte code offset #94
/*     */     //   Java source line #652	-> byte code offset #99
/*     */     //   Java source line #653	-> byte code offset #103
/*     */     //   Java source line #655	-> byte code offset #111
/*     */     //   Java source line #656	-> byte code offset #121
/*     */     //   Java source line #658	-> byte code offset #127
/*     */     //   Java source line #659	-> byte code offset #146
/*     */     //   Java source line #663	-> byte code offset #161
/*     */     //   Java source line #664	-> byte code offset #181
/*     */     //   Java source line #665	-> byte code offset #195
/*     */     //   Java source line #667	-> byte code offset #200
/*     */     //   Java source line #668	-> byte code offset #215
/*     */     //   Java source line #672	-> byte code offset #230
/*     */     //   Java source line #674	-> byte code offset #241
/*     */     //   Java source line #677	-> byte code offset #255
/*     */     //   Java source line #679	-> byte code offset #263
/*     */     //   Java source line #683	-> byte code offset #267
/*     */     //   Java source line #688	-> byte code offset #276
/*     */     //   Java source line #683	-> byte code offset #288
/*     */     //   Java source line #688	-> byte code offset #302
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	316	0	this	BlockingArrayQueue<E>
/*     */     //   0	316	1	index	int
/*     */     //   87	88	2	i	int
/*     */     //   93	145	3	capacity	int
/*     */     //   109	155	4	old	E
/*     */     //   119	92	5	tail	int
/*     */     //   265	21	6	?	E
/*     */     //   288	12	7	localObject1	Object
/*     */     //   302	12	8	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	267	288	finally
/*     */     //   288	290	288	finally
/*     */     //   9	276	302	finally
/*     */     //   288	304	302	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public ListIterator<E> listIterator(int index)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   4: invokeinterface 84 1 0
/*     */     //   9: aload_0
/*     */     //   10: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   13: invokeinterface 84 1 0
/*     */     //   18: aload_0
/*     */     //   19: invokevirtual 205	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:size	()I
/*     */     //   22: anewarray 61	java/lang/Object
/*     */     //   25: astore_2
/*     */     //   26: aload_0
/*     */     //   27: invokevirtual 205	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:size	()I
/*     */     //   30: ifle +79 -> 109
/*     */     //   33: aload_0
/*     */     //   34: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   37: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   40: iaload
/*     */     //   41: istore_3
/*     */     //   42: aload_0
/*     */     //   43: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   46: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   49: iaload
/*     */     //   50: istore 4
/*     */     //   52: iload_3
/*     */     //   53: iload 4
/*     */     //   55: if_icmpge +20 -> 75
/*     */     //   58: aload_0
/*     */     //   59: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   62: iload_3
/*     */     //   63: aload_2
/*     */     //   64: iconst_0
/*     */     //   65: iload 4
/*     */     //   67: iload_3
/*     */     //   68: isub
/*     */     //   69: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   72: goto +37 -> 109
/*     */     //   75: aload_0
/*     */     //   76: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   79: arraylength
/*     */     //   80: iload_3
/*     */     //   81: isub
/*     */     //   82: istore 5
/*     */     //   84: aload_0
/*     */     //   85: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   88: iload_3
/*     */     //   89: aload_2
/*     */     //   90: iconst_0
/*     */     //   91: iload 5
/*     */     //   93: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   96: aload_0
/*     */     //   97: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   100: iconst_0
/*     */     //   101: aload_2
/*     */     //   102: iload 5
/*     */     //   104: iload 4
/*     */     //   106: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   109: new 10	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue$Itr
/*     */     //   112: dup
/*     */     //   113: aload_0
/*     */     //   114: aload_2
/*     */     //   115: iload_1
/*     */     //   116: invokespecial 266	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue$Itr:<init>	(Lcom/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue;[Ljava/lang/Object;I)V
/*     */     //   119: astore_3
/*     */     //   120: aload_0
/*     */     //   121: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   124: invokeinterface 92 1 0
/*     */     //   129: aload_0
/*     */     //   130: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   133: invokeinterface 92 1 0
/*     */     //   138: aload_3
/*     */     //   139: areturn
/*     */     //   140: astore 6
/*     */     //   142: aload_0
/*     */     //   143: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   146: invokeinterface 92 1 0
/*     */     //   151: aload 6
/*     */     //   153: athrow
/*     */     //   154: astore 7
/*     */     //   156: aload_0
/*     */     //   157: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   160: invokeinterface 92 1 0
/*     */     //   165: aload 7
/*     */     //   167: athrow
/*     */     // Line number table:
/*     */     //   Java source line #696	-> byte code offset #0
/*     */     //   Java source line #700	-> byte code offset #9
/*     */     //   Java source line #703	-> byte code offset #18
/*     */     //   Java source line #704	-> byte code offset #26
/*     */     //   Java source line #706	-> byte code offset #33
/*     */     //   Java source line #707	-> byte code offset #42
/*     */     //   Java source line #708	-> byte code offset #52
/*     */     //   Java source line #710	-> byte code offset #58
/*     */     //   Java source line #714	-> byte code offset #75
/*     */     //   Java source line #715	-> byte code offset #84
/*     */     //   Java source line #716	-> byte code offset #96
/*     */     //   Java source line #719	-> byte code offset #109
/*     */     //   Java source line #723	-> byte code offset #120
/*     */     //   Java source line #728	-> byte code offset #129
/*     */     //   Java source line #723	-> byte code offset #140
/*     */     //   Java source line #728	-> byte code offset #154
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	168	0	this	BlockingArrayQueue<E>
/*     */     //   0	168	1	index	int
/*     */     //   25	90	2	elements	Object[]
/*     */     //   41	98	3	head	int
/*     */     //   50	55	4	tail	int
/*     */     //   82	21	5	chunk	int
/*     */     //   140	12	6	localObject1	Object
/*     */     //   154	12	7	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	120	140	finally
/*     */     //   140	142	140	finally
/*     */     //   9	129	154	finally
/*     */     //   140	156	154	finally
/*     */   }
/*     */   
/*     */   public int getCapacity()
/*     */   {
/* 741 */     this._tailLock.lock();
/*     */     try
/*     */     {
/* 744 */       return this._elements.length;
/*     */     }
/*     */     finally
/*     */     {
/* 748 */       this._tailLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxCapacity()
/*     */   {
/* 757 */     return this._maxCapacity;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private boolean grow()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 65	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_growCapacity	I
/*     */     //   4: ifgt +5 -> 9
/*     */     //   7: iconst_0
/*     */     //   8: ireturn
/*     */     //   9: aload_0
/*     */     //   10: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   13: invokeinterface 84 1 0
/*     */     //   18: aload_0
/*     */     //   19: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   22: invokeinterface 84 1 0
/*     */     //   27: aload_0
/*     */     //   28: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   31: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   34: iaload
/*     */     //   35: istore_1
/*     */     //   36: aload_0
/*     */     //   37: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   40: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   43: iaload
/*     */     //   44: istore_2
/*     */     //   45: aload_0
/*     */     //   46: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   49: arraylength
/*     */     //   50: istore_3
/*     */     //   51: iload_3
/*     */     //   52: aload_0
/*     */     //   53: getfield 65	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_growCapacity	I
/*     */     //   56: iadd
/*     */     //   57: anewarray 61	java/lang/Object
/*     */     //   60: astore 4
/*     */     //   62: iload_1
/*     */     //   63: iload_2
/*     */     //   64: if_icmpge +24 -> 88
/*     */     //   67: iload_2
/*     */     //   68: iload_1
/*     */     //   69: isub
/*     */     //   70: istore 5
/*     */     //   72: aload_0
/*     */     //   73: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   76: iload_1
/*     */     //   77: aload 4
/*     */     //   79: iconst_0
/*     */     //   80: iload 5
/*     */     //   82: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   85: goto +62 -> 147
/*     */     //   88: iload_1
/*     */     //   89: iload_2
/*     */     //   90: if_icmpgt +13 -> 103
/*     */     //   93: aload_0
/*     */     //   94: getfield 49	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_size	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   97: invokevirtual 99	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   100: ifle +44 -> 144
/*     */     //   103: iload_3
/*     */     //   104: iload_2
/*     */     //   105: iadd
/*     */     //   106: iload_1
/*     */     //   107: isub
/*     */     //   108: istore 5
/*     */     //   110: iload_3
/*     */     //   111: iload_1
/*     */     //   112: isub
/*     */     //   113: istore 6
/*     */     //   115: aload_0
/*     */     //   116: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   119: iload_1
/*     */     //   120: aload 4
/*     */     //   122: iconst_0
/*     */     //   123: iload 6
/*     */     //   125: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   128: aload_0
/*     */     //   129: getfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   132: iconst_0
/*     */     //   133: aload 4
/*     */     //   135: iload 6
/*     */     //   137: iload_2
/*     */     //   138: invokestatic 259	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   141: goto +6 -> 147
/*     */     //   144: iconst_0
/*     */     //   145: istore 5
/*     */     //   147: aload_0
/*     */     //   148: aload 4
/*     */     //   150: putfield 63	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   153: aload_0
/*     */     //   154: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   157: getstatic 86	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:HEAD_OFFSET	I
/*     */     //   160: iconst_0
/*     */     //   161: iastore
/*     */     //   162: aload_0
/*     */     //   163: getfield 39	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_indexes	[I
/*     */     //   166: getstatic 37	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:TAIL_OFFSET	I
/*     */     //   169: iload 5
/*     */     //   171: iastore
/*     */     //   172: iconst_1
/*     */     //   173: istore 6
/*     */     //   175: aload_0
/*     */     //   176: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   179: invokeinterface 92 1 0
/*     */     //   184: aload_0
/*     */     //   185: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   188: invokeinterface 92 1 0
/*     */     //   193: iload 6
/*     */     //   195: ireturn
/*     */     //   196: astore 7
/*     */     //   198: aload_0
/*     */     //   199: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_headLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   202: invokeinterface 92 1 0
/*     */     //   207: aload 7
/*     */     //   209: athrow
/*     */     //   210: astore 8
/*     */     //   212: aload_0
/*     */     //   213: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/BlockingArrayQueue:_tailLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   216: invokeinterface 92 1 0
/*     */     //   221: aload 8
/*     */     //   223: athrow
/*     */     // Line number table:
/*     */     //   Java source line #766	-> byte code offset #0
/*     */     //   Java source line #767	-> byte code offset #7
/*     */     //   Java source line #769	-> byte code offset #9
/*     */     //   Java source line #773	-> byte code offset #18
/*     */     //   Java source line #776	-> byte code offset #27
/*     */     //   Java source line #777	-> byte code offset #36
/*     */     //   Java source line #779	-> byte code offset #45
/*     */     //   Java source line #781	-> byte code offset #51
/*     */     //   Java source line #783	-> byte code offset #62
/*     */     //   Java source line #785	-> byte code offset #67
/*     */     //   Java source line #786	-> byte code offset #72
/*     */     //   Java source line #788	-> byte code offset #88
/*     */     //   Java source line #790	-> byte code offset #103
/*     */     //   Java source line #791	-> byte code offset #110
/*     */     //   Java source line #792	-> byte code offset #115
/*     */     //   Java source line #793	-> byte code offset #128
/*     */     //   Java source line #794	-> byte code offset #141
/*     */     //   Java source line #797	-> byte code offset #144
/*     */     //   Java source line #800	-> byte code offset #147
/*     */     //   Java source line #801	-> byte code offset #153
/*     */     //   Java source line #802	-> byte code offset #162
/*     */     //   Java source line #803	-> byte code offset #172
/*     */     //   Java source line #807	-> byte code offset #175
/*     */     //   Java source line #812	-> byte code offset #184
/*     */     //   Java source line #807	-> byte code offset #196
/*     */     //   Java source line #812	-> byte code offset #210
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	224	0	this	BlockingArrayQueue<E>
/*     */     //   35	85	1	head	int
/*     */     //   44	94	2	tail	int
/*     */     //   50	61	3	capacity	int
/*     */     //   60	89	4	elements	Object[]
/*     */     //   70	11	5	newTail	int
/*     */     //   108	3	5	newTail	int
/*     */     //   145	25	5	newTail	int
/*     */     //   113	81	6	cut	int
/*     */     //   196	12	7	localObject1	Object
/*     */     //   210	12	8	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   27	175	196	finally
/*     */     //   196	198	196	finally
/*     */     //   18	184	210	finally
/*     */     //   196	212	210	finally
/*     */   }
/*     */   
/*     */   private class Itr
/*     */     implements ListIterator<E>
/*     */   {
/*     */     private final Object[] _elements;
/*     */     private int _cursor;
/*     */     
/*     */     public Itr(Object[] elements, int offset)
/*     */     {
/* 823 */       this._elements = elements;
/* 824 */       this._cursor = offset;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 830 */       return this._cursor < this._elements.length;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public E next()
/*     */     {
/* 837 */       return (E)this._elements[(this._cursor++)];
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean hasPrevious()
/*     */     {
/* 843 */       return this._cursor > 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public E previous()
/*     */     {
/* 850 */       return (E)this._elements[(--this._cursor)];
/*     */     }
/*     */     
/*     */ 
/*     */     public int nextIndex()
/*     */     {
/* 856 */       return this._cursor + 1;
/*     */     }
/*     */     
/*     */ 
/*     */     public int previousIndex()
/*     */     {
/* 862 */       return this._cursor - 1;
/*     */     }
/*     */     
/*     */ 
/*     */     public void remove()
/*     */     {
/* 868 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */     public void set(E e)
/*     */     {
/* 874 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */     public void add(E e)
/*     */     {
/* 880 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\BlockingArrayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */