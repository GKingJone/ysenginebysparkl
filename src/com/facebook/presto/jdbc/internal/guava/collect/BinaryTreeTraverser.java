/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.BitSet;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class BinaryTreeTraverser<T>
/*     */   extends TreeTraverser<T>
/*     */ {
/*     */   public abstract Optional<T> leftChild(T paramT);
/*     */   
/*     */   public abstract Optional<T> rightChild(T paramT);
/*     */   
/*     */   public final Iterable<T> children(final T root)
/*     */   {
/*  59 */     Preconditions.checkNotNull(root);
/*  60 */     new FluentIterable()
/*     */     {
/*     */       public Iterator<T> iterator() {
/*  63 */         new AbstractIterator()
/*     */         {
/*     */           boolean doneLeft;
/*     */           boolean doneRight;
/*     */           
/*     */           protected T computeNext() {
/*  69 */             if (!this.doneLeft) {
/*  70 */               this.doneLeft = true;
/*  71 */               Optional<T> left = BinaryTreeTraverser.this.leftChild(BinaryTreeTraverser.1.this.val$root);
/*  72 */               if (left.isPresent()) {
/*  73 */                 return (T)left.get();
/*     */               }
/*     */             }
/*  76 */             if (!this.doneRight) {
/*  77 */               this.doneRight = true;
/*  78 */               Optional<T> right = BinaryTreeTraverser.this.rightChild(BinaryTreeTraverser.1.this.val$root);
/*  79 */               if (right.isPresent()) {
/*  80 */                 return (T)right.get();
/*     */               }
/*     */             }
/*  83 */             return (T)endOfData();
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   UnmodifiableIterator<T> preOrderIterator(T root)
/*     */   {
/*  92 */     return new PreOrderIterator(root);
/*     */   }
/*     */   
/*     */   private final class PreOrderIterator
/*     */     extends UnmodifiableIterator<T>
/*     */     implements PeekingIterator<T>
/*     */   {
/*     */     private final Deque<T> stack;
/*     */     
/*     */     PreOrderIterator()
/*     */     {
/* 103 */       this.stack = new ArrayDeque();
/* 104 */       this.stack.addLast(root);
/*     */     }
/*     */     
/*     */     public boolean hasNext()
/*     */     {
/* 109 */       return !this.stack.isEmpty();
/*     */     }
/*     */     
/*     */     public T next()
/*     */     {
/* 114 */       T result = this.stack.removeLast();
/* 115 */       BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(result));
/* 116 */       BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(result));
/* 117 */       return result;
/*     */     }
/*     */     
/*     */     public T peek()
/*     */     {
/* 122 */       return (T)this.stack.getLast();
/*     */     }
/*     */   }
/*     */   
/*     */   UnmodifiableIterator<T> postOrderIterator(T root)
/*     */   {
/* 128 */     return new PostOrderIterator(root);
/*     */   }
/*     */   
/*     */   private final class PostOrderIterator
/*     */     extends UnmodifiableIterator<T>
/*     */   {
/*     */     private final Deque<T> stack;
/*     */     private final BitSet hasExpanded;
/*     */     
/*     */     PostOrderIterator()
/*     */     {
/* 139 */       this.stack = new ArrayDeque();
/* 140 */       this.stack.addLast(root);
/* 141 */       this.hasExpanded = new BitSet();
/*     */     }
/*     */     
/*     */     public boolean hasNext()
/*     */     {
/* 146 */       return !this.stack.isEmpty();
/*     */     }
/*     */     
/*     */     public T next()
/*     */     {
/*     */       for (;;) {
/* 152 */         T node = this.stack.getLast();
/* 153 */         boolean expandedNode = this.hasExpanded.get(this.stack.size() - 1);
/* 154 */         if (expandedNode) {
/* 155 */           this.stack.removeLast();
/* 156 */           this.hasExpanded.clear(this.stack.size());
/* 157 */           return node;
/*     */         }
/* 159 */         this.hasExpanded.set(this.stack.size() - 1);
/* 160 */         BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(node));
/* 161 */         BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(node));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final FluentIterable<T> inOrderTraversal(final T root)
/*     */   {
/* 170 */     Preconditions.checkNotNull(root);
/* 171 */     new FluentIterable()
/*     */     {
/*     */       public UnmodifiableIterator<T> iterator() {
/* 174 */         return new InOrderIterator(BinaryTreeTraverser.this, root);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   private final class InOrderIterator extends AbstractIterator<T> {
/*     */     private final Deque<T> stack;
/*     */     private final BitSet hasExpandedLeft;
/*     */     
/*     */     InOrderIterator() {
/* 184 */       this.stack = new ArrayDeque();
/* 185 */       this.hasExpandedLeft = new BitSet();
/* 186 */       this.stack.addLast(root);
/*     */     }
/*     */     
/*     */     protected T computeNext()
/*     */     {
/* 191 */       while (!this.stack.isEmpty()) {
/* 192 */         T node = this.stack.getLast();
/* 193 */         if (this.hasExpandedLeft.get(this.stack.size() - 1)) {
/* 194 */           this.stack.removeLast();
/* 195 */           this.hasExpandedLeft.clear(this.stack.size());
/* 196 */           BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(node));
/* 197 */           return node;
/*     */         }
/* 199 */         this.hasExpandedLeft.set(this.stack.size() - 1);
/* 200 */         BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(node));
/*     */       }
/*     */       
/* 203 */       return (T)endOfData();
/*     */     }
/*     */   }
/*     */   
/*     */   private static <T> void pushIfPresent(Deque<T> stack, Optional<T> node) {
/* 208 */     if (node.isPresent()) {
/* 209 */       stack.addLast(node.get());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\BinaryTreeTraverser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */