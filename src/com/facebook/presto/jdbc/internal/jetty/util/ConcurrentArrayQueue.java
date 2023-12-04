/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.AbstractQueue;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.atomic.AtomicIntegerArray;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConcurrentArrayQueue<T>
/*     */   extends AbstractQueue<T>
/*     */ {
/*     */   public static final int DEFAULT_BLOCK_SIZE = 512;
/*  51 */   public static final Object REMOVED_ELEMENT = new Object()
/*     */   {
/*     */ 
/*     */     public String toString()
/*     */     {
/*  56 */       return "X";
/*     */     }
/*     */   };
/*     */   
/*  60 */   private static final int HEAD_OFFSET = MemoryUtils.getIntegersPerCacheLine() - 1;
/*  61 */   private static final int TAIL_OFFSET = MemoryUtils.getIntegersPerCacheLine() * 2 - 1;
/*     */   
/*  63 */   private final AtomicReferenceArray<Block<T>> _blocks = new AtomicReferenceArray(TAIL_OFFSET + 1);
/*     */   private final int _blockSize;
/*     */   
/*     */   public ConcurrentArrayQueue()
/*     */   {
/*  68 */     this(512);
/*     */   }
/*     */   
/*     */   public ConcurrentArrayQueue(int blockSize)
/*     */   {
/*  73 */     this._blockSize = blockSize;
/*  74 */     Block<T> block = newBlock();
/*  75 */     this._blocks.set(HEAD_OFFSET, block);
/*  76 */     this._blocks.set(TAIL_OFFSET, block);
/*     */   }
/*     */   
/*     */   public int getBlockSize()
/*     */   {
/*  81 */     return this._blockSize;
/*     */   }
/*     */   
/*     */   protected Block<T> getHeadBlock()
/*     */   {
/*  86 */     return (Block)this._blocks.get(HEAD_OFFSET);
/*     */   }
/*     */   
/*     */   protected Block<T> getTailBlock()
/*     */   {
/*  91 */     return (Block)this._blocks.get(TAIL_OFFSET);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean offer(T item)
/*     */   {
/*  97 */     item = Objects.requireNonNull(item);
/*     */     
/*  99 */     Block<T> initialTailBlock = getTailBlock();
/* 100 */     Block<T> currentTailBlock = initialTailBlock;
/* 101 */     int tail = currentTailBlock.tail();
/*     */     for (;;)
/*     */     {
/* 104 */       if (tail == getBlockSize())
/*     */       {
/* 106 */         Block<T> nextTailBlock = currentTailBlock.next();
/* 107 */         if (nextTailBlock == null)
/*     */         {
/* 109 */           nextTailBlock = newBlock();
/* 110 */           if (currentTailBlock.link(nextTailBlock))
/*     */           {
/*     */ 
/* 113 */             currentTailBlock = nextTailBlock;
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 118 */             currentTailBlock = currentTailBlock.next();
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 124 */           currentTailBlock = nextTailBlock;
/*     */         }
/* 126 */         tail = currentTailBlock.tail();
/*     */ 
/*     */ 
/*     */       }
/* 130 */       else if (currentTailBlock.peek(tail) == null)
/*     */       {
/* 132 */         if (currentTailBlock.store(tail, item)) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 140 */         tail++;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 146 */         tail++;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 151 */     updateTailBlock(initialTailBlock, currentTailBlock);
/*     */     
/* 153 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private void updateTailBlock(Block<T> oldTailBlock, Block<T> newTailBlock)
/*     */   {
/* 159 */     if (oldTailBlock != newTailBlock)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 164 */       casTailBlock(oldTailBlock, newTailBlock);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean casTailBlock(Block<T> current, Block<T> update)
/*     */   {
/* 170 */     return this._blocks.compareAndSet(TAIL_OFFSET, current, update);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public T poll()
/*     */   {
/* 177 */     Block<T> initialHeadBlock = getHeadBlock();
/* 178 */     Block<T> currentHeadBlock = initialHeadBlock;
/* 179 */     int head = currentHeadBlock.head();
/* 180 */     T result = null;
/*     */     for (;;)
/*     */     {
/* 183 */       if (head == getBlockSize())
/*     */       {
/* 185 */         Block<T> nextHeadBlock = currentHeadBlock.next();
/* 186 */         if (nextHeadBlock == null) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 199 */         currentHeadBlock = nextHeadBlock;
/* 200 */         head = currentHeadBlock.head();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 205 */         Object element = currentHeadBlock.peek(head);
/* 206 */         if (element == REMOVED_ELEMENT)
/*     */         {
/*     */ 
/* 209 */           head++;
/*     */         }
/*     */         else
/*     */         {
/* 213 */           result = (T)element;
/* 214 */           if (result == null)
/*     */             break;
/* 216 */           if (currentHeadBlock.remove(head, result, true)) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 224 */           head++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */     updateHeadBlock(initialHeadBlock, currentHeadBlock);
/*     */     
/* 238 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   private void updateHeadBlock(Block<T> oldHeadBlock, Block<T> newHeadBlock)
/*     */   {
/* 244 */     if (oldHeadBlock != newHeadBlock)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 249 */       casHeadBlock(oldHeadBlock, newHeadBlock);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean casHeadBlock(Block<T> current, Block<T> update)
/*     */   {
/* 255 */     return this._blocks.compareAndSet(HEAD_OFFSET, current, update);
/*     */   }
/*     */   
/*     */ 
/*     */   public T peek()
/*     */   {
/* 261 */     Block<T> currentHeadBlock = getHeadBlock();
/* 262 */     int head = currentHeadBlock.head();
/*     */     for (;;)
/*     */     {
/* 265 */       if (head == getBlockSize())
/*     */       {
/* 267 */         Block<T> nextHeadBlock = currentHeadBlock.next();
/* 268 */         if (nextHeadBlock == null)
/*     */         {
/*     */ 
/* 271 */           return null;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 276 */         currentHeadBlock = nextHeadBlock;
/* 277 */         head = currentHeadBlock.head();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 282 */         T element = currentHeadBlock.peek(head);
/* 283 */         if (element == REMOVED_ELEMENT)
/*     */         {
/*     */ 
/* 286 */           head++;
/*     */         }
/*     */         else
/*     */         {
/* 290 */           return element;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean remove(Object o)
/*     */   {
/* 299 */     Block<T> currentHeadBlock = getHeadBlock();
/* 300 */     int head = currentHeadBlock.head();
/* 301 */     boolean result = false;
/*     */     for (;;)
/*     */     {
/* 304 */       if (head == getBlockSize())
/*     */       {
/* 306 */         Block<T> nextHeadBlock = currentHeadBlock.next();
/* 307 */         if (nextHeadBlock == null) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 315 */         currentHeadBlock = nextHeadBlock;
/* 316 */         head = currentHeadBlock.head();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 321 */         Object element = currentHeadBlock.peek(head);
/* 322 */         if (element == REMOVED_ELEMENT)
/*     */         {
/*     */ 
/* 325 */           head++;
/*     */         }
/*     */         else
/*     */         {
/* 329 */           if (element == null) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 336 */           if (element.equals(o))
/*     */           {
/*     */ 
/* 339 */             if (currentHeadBlock.remove(head, o, false))
/*     */             {
/* 341 */               result = true;
/* 342 */               break;
/*     */             }
/*     */             
/*     */ 
/* 346 */             head++;
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/* 352 */             head++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 359 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean removeAll(Collection<?> c)
/*     */   {
/* 366 */     return super.removeAll(c);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean retainAll(Collection<?> c)
/*     */   {
/* 373 */     return super.retainAll(c);
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<T> iterator()
/*     */   {
/* 379 */     final List<Object[]> blocks = new ArrayList();
/* 380 */     Block<T> currentHeadBlock = getHeadBlock();
/* 381 */     while (currentHeadBlock != null)
/*     */     {
/* 383 */       Object[] elements = currentHeadBlock.arrayCopy();
/* 384 */       blocks.add(elements);
/* 385 */       currentHeadBlock = currentHeadBlock.next();
/*     */     }
/* 387 */     new Iterator()
/*     */     {
/*     */       private int blockIndex;
/*     */       
/*     */       private int index;
/*     */       
/*     */       public boolean hasNext()
/*     */       {
/*     */         for (;;)
/*     */         {
/* 397 */           if (this.blockIndex == blocks.size()) {
/* 398 */             return false;
/*     */           }
/* 400 */           Object element = ((Object[])blocks.get(this.blockIndex))[this.index];
/*     */           
/* 402 */           if (element == null) {
/* 403 */             return false;
/*     */           }
/* 405 */           if (element != ConcurrentArrayQueue.REMOVED_ELEMENT) {
/* 406 */             return true;
/*     */           }
/* 408 */           advance();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public T next()
/*     */       {
/*     */         for (;;)
/*     */         {
/* 417 */           if (this.blockIndex == blocks.size()) {
/* 418 */             throw new NoSuchElementException();
/*     */           }
/* 420 */           Object element = ((Object[])blocks.get(this.blockIndex))[this.index];
/*     */           
/* 422 */           if (element == null) {
/* 423 */             throw new NoSuchElementException();
/*     */           }
/* 425 */           advance();
/*     */           
/* 427 */           if (element != ConcurrentArrayQueue.REMOVED_ELEMENT)
/*     */           {
/* 429 */             T e = (T)element;
/* 430 */             return e;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       private void advance()
/*     */       {
/* 437 */         if (++this.index == ConcurrentArrayQueue.this.getBlockSize())
/*     */         {
/* 439 */           this.index = 0;
/* 440 */           this.blockIndex += 1;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void remove()
/*     */       {
/* 447 */         throw new UnsupportedOperationException();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/* 455 */     Block<T> currentHeadBlock = getHeadBlock();
/* 456 */     int head = currentHeadBlock.head();
/* 457 */     int size = 0;
/*     */     for (;;)
/*     */     {
/* 460 */       if (head == getBlockSize())
/*     */       {
/* 462 */         Block<T> nextHeadBlock = currentHeadBlock.next();
/* 463 */         if (nextHeadBlock == null) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 470 */         currentHeadBlock = nextHeadBlock;
/* 471 */         head = currentHeadBlock.head();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 476 */         Object element = currentHeadBlock.peek(head);
/* 477 */         if (element == REMOVED_ELEMENT)
/*     */         {
/*     */ 
/* 480 */           head++;
/*     */         } else {
/* 482 */           if (element == null)
/*     */             break;
/* 484 */           size++;
/* 485 */           head++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 493 */     return size;
/*     */   }
/*     */   
/*     */   protected Block<T> newBlock()
/*     */   {
/* 498 */     return new Block(getBlockSize());
/*     */   }
/*     */   
/*     */   protected int getBlockCount()
/*     */   {
/* 503 */     int result = 0;
/* 504 */     Block<T> headBlock = getHeadBlock();
/* 505 */     while (headBlock != null)
/*     */     {
/* 507 */       result++;
/* 508 */       headBlock = headBlock.next();
/*     */     }
/* 510 */     return result;
/*     */   }
/*     */   
/*     */   protected static final class Block<E>
/*     */   {
/* 515 */     private static final int headOffset = MemoryUtils.getIntegersPerCacheLine() - 1;
/* 516 */     private static final int tailOffset = MemoryUtils.getIntegersPerCacheLine() * 2 - 1;
/*     */     
/*     */     private final AtomicReferenceArray<Object> elements;
/* 519 */     private final AtomicReference<Block<E>> next = new AtomicReference();
/* 520 */     private final AtomicIntegerArray indexes = new AtomicIntegerArray(ConcurrentArrayQueue.TAIL_OFFSET + 1);
/*     */     
/*     */     protected Block(int blockSize)
/*     */     {
/* 524 */       this.elements = new AtomicReferenceArray(blockSize);
/*     */     }
/*     */     
/*     */ 
/*     */     public E peek(int index)
/*     */     {
/* 530 */       return (E)this.elements.get(index);
/*     */     }
/*     */     
/*     */     public boolean store(int index, E item)
/*     */     {
/* 535 */       boolean result = this.elements.compareAndSet(index, null, item);
/* 536 */       if (result)
/* 537 */         this.indexes.incrementAndGet(tailOffset);
/* 538 */       return result;
/*     */     }
/*     */     
/*     */     public boolean remove(int index, Object item, boolean updateHead)
/*     */     {
/* 543 */       boolean result = this.elements.compareAndSet(index, item, ConcurrentArrayQueue.REMOVED_ELEMENT);
/* 544 */       if ((result) && (updateHead))
/* 545 */         this.indexes.incrementAndGet(headOffset);
/* 546 */       return result;
/*     */     }
/*     */     
/*     */     public Block<E> next()
/*     */     {
/* 551 */       return (Block)this.next.get();
/*     */     }
/*     */     
/*     */     public boolean link(Block<E> nextBlock)
/*     */     {
/* 556 */       return this.next.compareAndSet(null, nextBlock);
/*     */     }
/*     */     
/*     */     public int head()
/*     */     {
/* 561 */       return this.indexes.get(headOffset);
/*     */     }
/*     */     
/*     */     public int tail()
/*     */     {
/* 566 */       return this.indexes.get(tailOffset);
/*     */     }
/*     */     
/*     */     public Object[] arrayCopy()
/*     */     {
/* 571 */       Object[] result = new Object[this.elements.length()];
/* 572 */       for (int i = 0; i < result.length; i++)
/* 573 */         result[i] = this.elements.get(i);
/* 574 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ConcurrentArrayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */