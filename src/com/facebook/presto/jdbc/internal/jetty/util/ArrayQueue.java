/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArrayQueue<E>
/*     */   extends AbstractList<E>
/*     */   implements Queue<E>
/*     */ {
/*     */   public static final int DEFAULT_CAPACITY = 64;
/*     */   public static final int DEFAULT_GROWTH = 32;
/*     */   protected final Object _lock;
/*     */   protected final int _growCapacity;
/*     */   protected Object[] _elements;
/*     */   protected int _nextE;
/*     */   protected int _nextSlot;
/*     */   protected int _size;
/*     */   
/*     */   public ArrayQueue()
/*     */   {
/*  50 */     this(64, -1);
/*     */   }
/*     */   
/*     */ 
/*     */   public ArrayQueue(Object lock)
/*     */   {
/*  56 */     this(64, -1, lock);
/*     */   }
/*     */   
/*     */ 
/*     */   public ArrayQueue(int capacity)
/*     */   {
/*  62 */     this(capacity, -1);
/*     */   }
/*     */   
/*     */ 
/*     */   public ArrayQueue(int initCapacity, int growBy)
/*     */   {
/*  68 */     this(initCapacity, growBy, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public ArrayQueue(int initCapacity, int growBy, Object lock)
/*     */   {
/*  74 */     this._lock = (lock == null ? this : lock);
/*  75 */     this._growCapacity = growBy;
/*  76 */     this._elements = new Object[initCapacity];
/*     */   }
/*     */   
/*     */ 
/*     */   public Object lock()
/*     */   {
/*  82 */     return this._lock;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int getCapacity()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/ArrayQueue:_lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 48	com/facebook/presto/jdbc/internal/jetty/util/ArrayQueue:_elements	[Ljava/lang/Object;
/*     */     //   11: arraylength
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: ireturn
/*     */     //   15: astore_2
/*     */     //   16: aload_1
/*     */     //   17: monitorexit
/*     */     //   18: aload_2
/*     */     //   19: athrow
/*     */     // Line number table:
/*     */     //   Java source line #88	-> byte code offset #0
/*     */     //   Java source line #90	-> byte code offset #7
/*     */     //   Java source line #91	-> byte code offset #15
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	20	0	this	ArrayQueue<E>
/*     */     //   5	12	1	Ljava/lang/Object;	Object
/*     */     //   15	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	14	15	finally
/*     */     //   15	18	15	finally
/*     */   }
/*     */   
/*     */   public int getNextSlotUnsafe()
/*     */   {
/* 100 */     return this._nextSlot;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean add(E e)
/*     */   {
/* 107 */     if (!offer(e))
/* 108 */       throw new IllegalStateException("Full");
/* 109 */     return true;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean offer(E e)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/ArrayQueue:_lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: aload_1
/*     */     //   9: invokevirtual 73	com/facebook/presto/jdbc/internal/jetty/util/ArrayQueue:enqueue	(Ljava/lang/Object;)Z
/*     */     //   12: aload_2
/*     */     //   13: monitorexit
/*     */     //   14: ireturn
/*     */     //   15: astore_3
/*     */     //   16: aload_2
/*     */     //   17: monitorexit
/*     */     //   18: aload_3
/*     */     //   19: athrow
/*     */     // Line number table:
/*     */     //   Java source line #115	-> byte code offset #0
/*     */     //   Java source line #117	-> byte code offset #7
/*     */     //   Java source line #118	-> byte code offset #15
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	20	0	this	ArrayQueue<E>
/*     */     //   0	20	1	e	E
/*     */     //   5	12	2	Ljava/lang/Object;	Object
/*     */     //   15	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	14	15	finally
/*     */     //   15	18	15	finally
/*     */   }
/*     */   
/*     */   protected boolean enqueue(E e)
/*     */   {
/* 124 */     if ((this._size == this._elements.length) && (!growUnsafe())) {
/* 125 */       return false;
/*     */     }
/* 127 */     this._size += 1;
/* 128 */     this._elements[(this._nextSlot++)] = e;
/* 129 */     if (this._nextSlot == this._elements.length) {
/* 130 */       this._nextSlot = 0;
/*     */     }
/* 132 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addUnsafe(E e)
/*     */   {
/* 144 */     if (!enqueue(e)) {
/* 145 */       throw new IllegalStateException("Full");
/*     */     }
/*     */   }
/*     */   
/*     */   public E element()
/*     */   {
/* 151 */     synchronized (this._lock)
/*     */     {
/* 153 */       if (isEmpty())
/* 154 */         throw new NoSuchElementException();
/* 155 */       return (E)at(this._nextE);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private E at(int index)
/*     */   {
/* 163 */     return (E)this._elements[index];
/*     */   }
/*     */   
/*     */ 
/*     */   public E peek()
/*     */   {
/* 169 */     synchronized (this._lock)
/*     */     {
/* 171 */       if (this._size == 0)
/* 172 */         return null;
/* 173 */       return (E)at(this._nextE);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public E peekUnsafe()
/*     */   {
/* 180 */     if (this._size == 0)
/* 181 */       return null;
/* 182 */     return (E)at(this._nextE);
/*     */   }
/*     */   
/*     */ 
/*     */   public E poll()
/*     */   {
/* 188 */     synchronized (this._lock)
/*     */     {
/* 190 */       if (this._size == 0)
/* 191 */         return null;
/* 192 */       return (E)dequeue();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public E pollUnsafe()
/*     */   {
/* 199 */     if (this._size == 0)
/* 200 */       return null;
/* 201 */     return (E)dequeue();
/*     */   }
/*     */   
/*     */ 
/*     */   protected E dequeue()
/*     */   {
/* 207 */     E e = at(this._nextE);
/* 208 */     this._elements[this._nextE] = null;
/* 209 */     this._size -= 1;
/* 210 */     if (++this._nextE == this._elements.length)
/* 211 */       this._nextE = 0;
/* 212 */     return e;
/*     */   }
/*     */   
/*     */ 
/*     */   public E remove()
/*     */   {
/* 218 */     synchronized (this._lock)
/*     */     {
/* 220 */       if (this._size == 0)
/* 221 */         throw new NoSuchElementException();
/* 222 */       return (E)dequeue();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 230 */     synchronized (this._lock)
/*     */     {
/* 232 */       this._size = 0;
/* 233 */       this._nextE = 0;
/* 234 */       this._nextSlot = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 242 */     synchronized (this._lock)
/*     */     {
/* 244 */       return this._size == 0;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int size()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 44	com/facebook/presto/jdbc/internal/jetty/util/ArrayQueue:_lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 75	com/facebook/presto/jdbc/internal/jetty/util/ArrayQueue:_size	I
/*     */     //   11: aload_1
/*     */     //   12: monitorexit
/*     */     //   13: ireturn
/*     */     //   14: astore_2
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: aload_2
/*     */     //   18: athrow
/*     */     // Line number table:
/*     */     //   Java source line #252	-> byte code offset #0
/*     */     //   Java source line #254	-> byte code offset #7
/*     */     //   Java source line #255	-> byte code offset #14
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	19	0	this	ArrayQueue<E>
/*     */     //   5	11	1	Ljava/lang/Object;	Object
/*     */     //   14	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	13	14	finally
/*     */     //   14	17	14	finally
/*     */   }
/*     */   
/*     */   public int sizeUnsafe()
/*     */   {
/* 261 */     return this._size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public E get(int index)
/*     */   {
/* 268 */     synchronized (this._lock)
/*     */     {
/* 270 */       if ((index < 0) || (index >= this._size))
/* 271 */         throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
/* 272 */       return (E)getUnsafe(index);
/*     */     }
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
/*     */   public E getUnsafe(int index)
/*     */   {
/* 286 */     int i = (this._nextE + index) % this._elements.length;
/* 287 */     return (E)at(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public E remove(int index)
/*     */   {
/* 294 */     synchronized (this._lock)
/*     */     {
/* 296 */       if ((index < 0) || (index >= this._size)) {
/* 297 */         throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
/*     */       }
/* 299 */       int i = (this._nextE + index) % this._elements.length;
/* 300 */       E old = at(i);
/*     */       
/* 302 */       if (i < this._nextSlot)
/*     */       {
/*     */ 
/*     */ 
/* 306 */         System.arraycopy(this._elements, i + 1, this._elements, i, this._nextSlot - i);
/* 307 */         this._nextSlot -= 1;
/* 308 */         this._size -= 1;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 314 */         System.arraycopy(this._elements, i + 1, this._elements, i, this._elements.length - i - 1);
/* 315 */         if (this._nextSlot > 0)
/*     */         {
/* 317 */           this._elements[(this._elements.length - 1)] = this._elements[0];
/* 318 */           System.arraycopy(this._elements, 1, this._elements, 0, this._nextSlot - 1);
/* 319 */           this._nextSlot -= 1;
/*     */         }
/*     */         else {
/* 322 */           this._nextSlot = (this._elements.length - 1);
/*     */         }
/* 324 */         this._size -= 1;
/*     */       }
/*     */       
/* 327 */       return old;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public E set(int index, E element)
/*     */   {
/* 335 */     synchronized (this._lock)
/*     */     {
/* 337 */       if ((index < 0) || (index >= this._size)) {
/* 338 */         throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
/*     */       }
/* 340 */       int i = this._nextE + index;
/* 341 */       if (i >= this._elements.length)
/* 342 */         i -= this._elements.length;
/* 343 */       E old = at(i);
/* 344 */       this._elements[i] = element;
/* 345 */       return old;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void add(int index, E element)
/*     */   {
/* 353 */     synchronized (this._lock)
/*     */     {
/* 355 */       if ((index < 0) || (index > this._size)) {
/* 356 */         throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
/*     */       }
/* 358 */       if ((this._size == this._elements.length) && (!growUnsafe())) {
/* 359 */         throw new IllegalStateException("Full");
/*     */       }
/* 361 */       if (index == this._size)
/*     */       {
/* 363 */         add(element);
/*     */       }
/*     */       else
/*     */       {
/* 367 */         int i = this._nextE + index;
/* 368 */         if (i >= this._elements.length) {
/* 369 */           i -= this._elements.length;
/*     */         }
/* 371 */         this._size += 1;
/* 372 */         this._nextSlot += 1;
/* 373 */         if (this._nextSlot == this._elements.length) {
/* 374 */           this._nextSlot = 0;
/*     */         }
/* 376 */         if (i < this._nextSlot)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 382 */           System.arraycopy(this._elements, i, this._elements, i + 1, this._nextSlot - i);
/* 383 */           this._elements[i] = element;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 389 */           if (this._nextSlot > 0)
/*     */           {
/* 391 */             System.arraycopy(this._elements, 0, this._elements, 1, this._nextSlot);
/* 392 */             this._elements[0] = this._elements[(this._elements.length - 1)];
/*     */           }
/*     */           
/* 395 */           System.arraycopy(this._elements, i, this._elements, i + 1, this._elements.length - i - 1);
/* 396 */           this._elements[i] = element;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resizeUnsafe(int newCapacity)
/*     */   {
/* 405 */     newCapacity = Math.max(newCapacity, this._size);
/* 406 */     Object[] elements = new Object[newCapacity];
/*     */     
/* 408 */     if (this._size > 0)
/*     */     {
/* 410 */       if (this._nextSlot > this._nextE) {
/* 411 */         System.arraycopy(this._elements, this._nextE, elements, 0, this._size);
/*     */       }
/*     */       else {
/* 414 */         int split = this._elements.length - this._nextE;
/* 415 */         System.arraycopy(this._elements, this._nextE, elements, 0, split);
/* 416 */         System.arraycopy(this._elements, 0, elements, split, this._nextSlot);
/*     */       }
/*     */     }
/* 419 */     this._elements = elements;
/* 420 */     this._nextE = 0;
/* 421 */     this._nextSlot = this._size;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean growUnsafe()
/*     */   {
/* 427 */     if (this._growCapacity <= 0)
/* 428 */       return false;
/* 429 */     resizeUnsafe(this._elements.length + this._growCapacity);
/* 430 */     return true;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ArrayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */