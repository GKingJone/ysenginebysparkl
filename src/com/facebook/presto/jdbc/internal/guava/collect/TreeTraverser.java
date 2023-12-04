/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
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
/*     */ public abstract class TreeTraverser<T>
/*     */ {
/*     */   public abstract Iterable<T> children(T paramT);
/*     */   
/*     */   public final FluentIterable<T> preOrderTraversal(final T root)
/*     */   {
/*  70 */     Preconditions.checkNotNull(root);
/*  71 */     new FluentIterable()
/*     */     {
/*     */       public UnmodifiableIterator<T> iterator() {
/*  74 */         return TreeTraverser.this.preOrderIterator(root);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   UnmodifiableIterator<T> preOrderIterator(T root)
/*     */   {
/*  81 */     return new PreOrderIterator(root);
/*     */   }
/*     */   
/*     */   private final class PreOrderIterator extends UnmodifiableIterator<T> {
/*     */     private final Deque<Iterator<T>> stack;
/*     */     
/*     */     PreOrderIterator() {
/*  88 */       this.stack = new ArrayDeque();
/*  89 */       this.stack.addLast(Iterators.singletonIterator(Preconditions.checkNotNull(root)));
/*     */     }
/*     */     
/*     */     public boolean hasNext()
/*     */     {
/*  94 */       return !this.stack.isEmpty();
/*     */     }
/*     */     
/*     */     public T next()
/*     */     {
/*  99 */       Iterator<T> itr = (Iterator)this.stack.getLast();
/* 100 */       T result = Preconditions.checkNotNull(itr.next());
/* 101 */       if (!itr.hasNext()) {
/* 102 */         this.stack.removeLast();
/*     */       }
/* 104 */       Iterator<T> childItr = TreeTraverser.this.children(result).iterator();
/* 105 */       if (childItr.hasNext()) {
/* 106 */         this.stack.addLast(childItr);
/*     */       }
/* 108 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final FluentIterable<T> postOrderTraversal(final T root)
/*     */   {
/* 120 */     Preconditions.checkNotNull(root);
/* 121 */     new FluentIterable()
/*     */     {
/*     */       public UnmodifiableIterator<T> iterator() {
/* 124 */         return TreeTraverser.this.postOrderIterator(root);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   UnmodifiableIterator<T> postOrderIterator(T root)
/*     */   {
/* 131 */     return new PostOrderIterator(root);
/*     */   }
/*     */   
/*     */   private static final class PostOrderNode<T> {
/*     */     final T root;
/*     */     final Iterator<T> childIterator;
/*     */     
/*     */     PostOrderNode(T root, Iterator<T> childIterator) {
/* 139 */       this.root = Preconditions.checkNotNull(root);
/* 140 */       this.childIterator = ((Iterator)Preconditions.checkNotNull(childIterator));
/*     */     }
/*     */   }
/*     */   
/*     */   private final class PostOrderIterator extends AbstractIterator<T> {
/*     */     private final ArrayDeque<PostOrderNode<T>> stack;
/*     */     
/*     */     PostOrderIterator() {
/* 148 */       this.stack = new ArrayDeque();
/* 149 */       this.stack.addLast(expand(root));
/*     */     }
/*     */     
/*     */     protected T computeNext()
/*     */     {
/* 154 */       while (!this.stack.isEmpty()) {
/* 155 */         PostOrderNode<T> top = (PostOrderNode)this.stack.getLast();
/* 156 */         if (top.childIterator.hasNext()) {
/* 157 */           T child = top.childIterator.next();
/* 158 */           this.stack.addLast(expand(child));
/*     */         } else {
/* 160 */           this.stack.removeLast();
/* 161 */           return (T)top.root;
/*     */         }
/*     */       }
/* 164 */       return (T)endOfData();
/*     */     }
/*     */     
/*     */     private PostOrderNode<T> expand(T t) {
/* 168 */       return new PostOrderNode(t, TreeTraverser.this.children(t).iterator());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final FluentIterable<T> breadthFirstTraversal(final T root)
/*     */   {
/* 180 */     Preconditions.checkNotNull(root);
/* 181 */     new FluentIterable()
/*     */     {
/*     */       public UnmodifiableIterator<T> iterator() {
/* 184 */         return new BreadthFirstIterator(TreeTraverser.this, root);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   private final class BreadthFirstIterator extends UnmodifiableIterator<T> implements PeekingIterator<T>
/*     */   {
/*     */     private final Queue<T> queue;
/*     */     
/*     */     BreadthFirstIterator() {
/* 194 */       this.queue = new ArrayDeque();
/* 195 */       this.queue.add(root);
/*     */     }
/*     */     
/*     */     public boolean hasNext()
/*     */     {
/* 200 */       return !this.queue.isEmpty();
/*     */     }
/*     */     
/*     */     public T peek()
/*     */     {
/* 205 */       return (T)this.queue.element();
/*     */     }
/*     */     
/*     */     public T next()
/*     */     {
/* 210 */       T result = this.queue.remove();
/* 211 */       Iterables.addAll(this.queue, TreeTraverser.this.children(result));
/* 212 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\TreeTraverser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */