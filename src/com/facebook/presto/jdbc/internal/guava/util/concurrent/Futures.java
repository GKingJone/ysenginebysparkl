/*      */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList.Builder;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Lists;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Ordering;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Queues;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Sets;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.UndeclaredThrowableException;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.Callable;
/*      */ import java.util.concurrent.CancellationException;
/*      */ import java.util.concurrent.ConcurrentLinkedQueue;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.TimeoutException;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
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
/*      */ @Beta
/*      */ public final class Futures
/*      */ {
/*      */   public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> future, Function<? super Exception, X> mapper)
/*      */   {
/*   92 */     return new MappingCheckedFuture((ListenableFuture)Preconditions.checkNotNull(future), mapper);
/*      */   }
/*      */   
/*      */   private static abstract class ImmediateFuture<V>
/*      */     implements ListenableFuture<V>
/*      */   {
/*   98 */     private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());
/*      */     
/*      */ 
/*      */     public void addListener(Runnable listener, Executor executor)
/*      */     {
/*  103 */       Preconditions.checkNotNull(listener, "Runnable was null.");
/*  104 */       Preconditions.checkNotNull(executor, "Executor was null.");
/*      */       try {
/*  106 */         executor.execute(listener);
/*      */       }
/*      */       catch (RuntimeException e)
/*      */       {
/*  110 */         String str1 = String.valueOf(String.valueOf(listener));String str2 = String.valueOf(String.valueOf(executor));log.log(Level.SEVERE, 57 + str1.length() + str2.length() + "RuntimeException while executing runnable " + str1 + " with executor " + str2, e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean cancel(boolean mayInterruptIfRunning)
/*      */     {
/*  117 */       return false;
/*      */     }
/*      */     
/*      */     public abstract V get()
/*      */       throws ExecutionException;
/*      */     
/*      */     public V get(long timeout, TimeUnit unit) throws ExecutionException
/*      */     {
/*  125 */       Preconditions.checkNotNull(unit);
/*  126 */       return (V)get();
/*      */     }
/*      */     
/*      */     public boolean isCancelled()
/*      */     {
/*  131 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  136 */     public boolean isDone() { return true; }
/*      */   }
/*      */   
/*      */   private static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V> {
/*      */     @Nullable
/*      */     private final V value;
/*      */     
/*      */     ImmediateSuccessfulFuture(@Nullable V value) {
/*  144 */       super();
/*  145 */       this.value = value;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/*  150 */       return (V)this.value;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateSuccessfulCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
/*      */     @Nullable
/*      */     private final V value;
/*      */     
/*      */     ImmediateSuccessfulCheckedFuture(@Nullable V value) {
/*  159 */       super();
/*  160 */       this.value = value;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/*  165 */       return (V)this.value;
/*      */     }
/*      */     
/*      */     public V checkedGet()
/*      */     {
/*  170 */       return (V)this.value;
/*      */     }
/*      */     
/*      */     public V checkedGet(long timeout, TimeUnit unit)
/*      */     {
/*  175 */       Preconditions.checkNotNull(unit);
/*  176 */       return (V)this.value;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateFailedFuture<V> extends ImmediateFuture<V> {
/*      */     private final Throwable thrown;
/*      */     
/*      */     ImmediateFailedFuture(Throwable thrown) {
/*  184 */       super();
/*  185 */       this.thrown = thrown;
/*      */     }
/*      */     
/*      */     public V get() throws ExecutionException
/*      */     {
/*  190 */       throw new ExecutionException(this.thrown);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateCancelledFuture<V> extends ImmediateFuture<V> {
/*      */     private final CancellationException thrown;
/*      */     
/*      */     ImmediateCancelledFuture() {
/*  198 */       super();
/*  199 */       this.thrown = new CancellationException("Immediate cancelled future.");
/*      */     }
/*      */     
/*      */     public boolean isCancelled()
/*      */     {
/*  204 */       return true;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/*  209 */       throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateFailedCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X>
/*      */   {
/*      */     private final X thrown;
/*      */     
/*      */     ImmediateFailedCheckedFuture(X thrown)
/*      */     {
/*  219 */       super();
/*  220 */       this.thrown = thrown;
/*      */     }
/*      */     
/*      */     public V get() throws ExecutionException
/*      */     {
/*  225 */       throw new ExecutionException(this.thrown);
/*      */     }
/*      */     
/*      */     public V checkedGet() throws Exception
/*      */     {
/*  230 */       throw this.thrown;
/*      */     }
/*      */     
/*      */     public V checkedGet(long timeout, TimeUnit unit) throws Exception
/*      */     {
/*  235 */       Preconditions.checkNotNull(unit);
/*  236 */       throw this.thrown;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <V> ListenableFuture<V> immediateFuture(@Nullable V value)
/*      */   {
/*  247 */     return new ImmediateSuccessfulFuture(value);
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
/*      */   public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V value)
/*      */   {
/*  260 */     return new ImmediateSuccessfulCheckedFuture(value);
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
/*      */   public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable)
/*      */   {
/*  274 */     Preconditions.checkNotNull(throwable);
/*  275 */     return new ImmediateFailedFuture(throwable);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <V> ListenableFuture<V> immediateCancelledFuture()
/*      */   {
/*  285 */     return new ImmediateCancelledFuture();
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
/*      */   public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X exception)
/*      */   {
/*  300 */     Preconditions.checkNotNull(exception);
/*  301 */     return new ImmediateFailedCheckedFuture(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> input, FutureFallback<? extends V> fallback)
/*      */   {
/*  379 */     return withFallback(input, fallback, MoreExecutors.directExecutor());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> input, FutureFallback<? extends V> fallback, Executor executor)
/*      */   {
/*  443 */     Preconditions.checkNotNull(fallback);
/*  444 */     return new FallbackFuture(input, fallback, executor);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class FallbackFuture<V>
/*      */     extends AbstractFuture<V>
/*      */   {
/*      */     private volatile ListenableFuture<? extends V> running;
/*      */     
/*      */ 
/*      */ 
/*      */     FallbackFuture(ListenableFuture<? extends V> input, final FutureFallback<? extends V> fallback, Executor executor)
/*      */     {
/*  458 */       this.running = input;
/*  459 */       Futures.addCallback(this.running, new FutureCallback()
/*      */       {
/*      */         public void onSuccess(V value) {
/*  462 */           FallbackFuture.this.set(value);
/*      */         }
/*      */         
/*      */         public void onFailure(Throwable t)
/*      */         {
/*  467 */           if (FallbackFuture.this.isCancelled()) {
/*  468 */             return;
/*      */           }
/*      */           try {
/*  471 */             FallbackFuture.this.running = fallback.create(t);
/*  472 */             if (FallbackFuture.this.isCancelled()) {
/*  473 */               FallbackFuture.this.running.cancel(FallbackFuture.this.wasInterrupted());
/*  474 */               return;
/*      */             }
/*  476 */             Futures.addCallback(FallbackFuture.this.running, new FutureCallback()
/*      */             {
/*      */               public void onSuccess(V value) {
/*  479 */                 FallbackFuture.this.set(value);
/*      */               }
/*      */               
/*      */               public void onFailure(Throwable t)
/*      */               {
/*  484 */                 if (FallbackFuture.this.running.isCancelled()) {
/*  485 */                   FallbackFuture.this.cancel(false);
/*      */                 } else
/*  487 */                   FallbackFuture.this.setException(t); } }, MoreExecutors.directExecutor());
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  492 */             FallbackFuture.this.setException(e); } } }, executor);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean cancel(boolean mayInterruptIfRunning)
/*      */     {
/*  500 */       if (super.cancel(mayInterruptIfRunning)) {
/*  501 */         this.running.cancel(mayInterruptIfRunning);
/*  502 */         return true;
/*      */       }
/*  504 */       return false;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function)
/*      */   {
/*  565 */     ChainingListenableFuture<I, O> output = new ChainingListenableFuture(function, input, null);
/*      */     
/*  567 */     input.addListener(output, MoreExecutors.directExecutor());
/*  568 */     return output;
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
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor)
/*      */   {
/*  613 */     Preconditions.checkNotNull(executor);
/*  614 */     ChainingListenableFuture<I, O> output = new ChainingListenableFuture(function, input, null);
/*      */     
/*  616 */     input.addListener(rejectionPropagatingRunnable(output, output, executor), MoreExecutors.directExecutor());
/*  617 */     return output;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Runnable rejectionPropagatingRunnable(final AbstractFuture<?> outputFuture, final Runnable delegateTask, Executor delegateExecutor)
/*      */   {
/*  628 */     new Runnable() {
/*      */       public void run() {
/*  630 */         final AtomicBoolean thrownFromDelegate = new AtomicBoolean(true);
/*      */         try {
/*  632 */           this.val$delegateExecutor.execute(new Runnable() {
/*      */             public void run() {
/*  634 */               thrownFromDelegate.set(false);
/*  635 */               Futures.1.this.val$delegateTask.run();
/*      */             }
/*      */           });
/*      */         } catch (RejectedExecutionException e) {
/*  639 */           if (thrownFromDelegate.get())
/*      */           {
/*  641 */             outputFuture.setException(e);
/*      */           }
/*      */         }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function)
/*      */   {
/*  705 */     Preconditions.checkNotNull(function);
/*  706 */     ChainingListenableFuture<I, O> output = new ChainingListenableFuture(asAsyncFunction(function), input, null);
/*      */     
/*  708 */     input.addListener(output, MoreExecutors.directExecutor());
/*  709 */     return output;
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
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor)
/*      */   {
/*  751 */     Preconditions.checkNotNull(function);
/*  752 */     return transform(input, asAsyncFunction(function), executor);
/*      */   }
/*      */   
/*      */ 
/*      */   private static <I, O> AsyncFunction<I, O> asAsyncFunction(Function<? super I, ? extends O> function)
/*      */   {
/*  758 */     new AsyncFunction() {
/*      */       public ListenableFuture<O> apply(I input) {
/*  760 */         O output = this.val$function.apply(input);
/*  761 */         return Futures.immediateFuture(output);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <I, O> Future<O> lazyTransform(Future<I> input, final Function<? super I, ? extends O> function)
/*      */   {
/*  791 */     Preconditions.checkNotNull(input);
/*  792 */     Preconditions.checkNotNull(function);
/*  793 */     new Future()
/*      */     {
/*      */       public boolean cancel(boolean mayInterruptIfRunning)
/*      */       {
/*  797 */         return this.val$input.cancel(mayInterruptIfRunning);
/*      */       }
/*      */       
/*      */       public boolean isCancelled()
/*      */       {
/*  802 */         return this.val$input.isCancelled();
/*      */       }
/*      */       
/*      */       public boolean isDone()
/*      */       {
/*  807 */         return this.val$input.isDone();
/*      */       }
/*      */       
/*      */       public O get() throws InterruptedException, ExecutionException
/*      */       {
/*  812 */         return (O)applyTransformation(this.val$input.get());
/*      */       }
/*      */       
/*      */       public O get(long timeout, TimeUnit unit)
/*      */         throws InterruptedException, ExecutionException, TimeoutException
/*      */       {
/*  818 */         return (O)applyTransformation(this.val$input.get(timeout, unit));
/*      */       }
/*      */       
/*      */       private O applyTransformation(I input) throws ExecutionException {
/*      */         try {
/*  823 */           return (O)function.apply(input);
/*      */         } catch (Throwable t) {
/*  825 */           throw new ExecutionException(t);
/*      */         }
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class ChainingListenableFuture<I, O>
/*      */     extends AbstractFuture<O>
/*      */     implements Runnable
/*      */   {
/*      */     private AsyncFunction<? super I, ? extends O> function;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private ListenableFuture<? extends I> inputFuture;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private volatile ListenableFuture<? extends O> outputFuture;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private ChainingListenableFuture(AsyncFunction<? super I, ? extends O> function, ListenableFuture<? extends I> inputFuture)
/*      */     {
/*  861 */       this.function = ((AsyncFunction)Preconditions.checkNotNull(function));
/*  862 */       this.inputFuture = ((ListenableFuture)Preconditions.checkNotNull(inputFuture));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean cancel(boolean mayInterruptIfRunning)
/*      */     {
/*  871 */       if (super.cancel(mayInterruptIfRunning))
/*      */       {
/*      */ 
/*  874 */         cancel(this.inputFuture, mayInterruptIfRunning);
/*  875 */         cancel(this.outputFuture, mayInterruptIfRunning);
/*  876 */         return true;
/*      */       }
/*  878 */       return false;
/*      */     }
/*      */     
/*      */     private void cancel(@Nullable Future<?> future, boolean mayInterruptIfRunning)
/*      */     {
/*  883 */       if (future != null) {
/*  884 */         future.cancel(mayInterruptIfRunning);
/*      */       }
/*      */     }
/*      */     
/*      */     public void run()
/*      */     {
/*      */       try {
/*      */         I sourceResult;
/*      */         try {
/*  893 */           sourceResult = Uninterruptibles.getUninterruptibly(this.inputFuture);
/*      */ 
/*      */         }
/*      */         catch (CancellationException e)
/*      */         {
/*  898 */           cancel(false); return;
/*      */         }
/*      */         catch (ExecutionException e)
/*      */         {
/*  902 */           setException(e.getCause()); return;
/*      */         }
/*      */         
/*      */ 
/*  906 */         final ListenableFuture<? extends O> outputFuture = this.outputFuture = (ListenableFuture)Preconditions.checkNotNull(this.function.apply(sourceResult), "AsyncFunction may not return null.");
/*      */         
/*      */ 
/*  909 */         if (isCancelled()) {
/*  910 */           outputFuture.cancel(wasInterrupted());
/*  911 */           this.outputFuture = null;
/*      */         }
/*      */         else {
/*  914 */           outputFuture.addListener(new Runnable()
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  918 */                 ChainingListenableFuture.this.set(Uninterruptibles.getUninterruptibly(outputFuture));
/*      */ 
/*      */               }
/*      */               catch (CancellationException e)
/*      */               {
/*  923 */                 ChainingListenableFuture.this.cancel(false);
/*      */               }
/*      */               catch (ExecutionException e)
/*      */               {
/*  927 */                 ChainingListenableFuture.this.setException(e.getCause());
/*      */               }
/*      */               finally {
/*  930 */                 ChainingListenableFuture.this.outputFuture = null; } } }, MoreExecutors.directExecutor());
/*      */         }
/*      */         
/*      */       }
/*      */       catch (UndeclaredThrowableException e)
/*      */       {
/*  936 */         setException(e.getCause());
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  940 */         setException(t);
/*      */       }
/*      */       finally {
/*  943 */         this.function = null;
/*  944 */         this.inputFuture = null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <V> ListenableFuture<V> dereference(ListenableFuture<? extends ListenableFuture<? extends V>> nested)
/*      */   {
/*  973 */     return transform(nested, DEREFERENCER);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  979 */   private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction()
/*      */   {
/*      */     public ListenableFuture<Object> apply(ListenableFuture<Object> input) {
/*  982 */       return input;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... futures)
/*      */   {
/* 1005 */     return listFuture(ImmutableList.copyOf(futures), true, MoreExecutors.directExecutor());
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
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> futures)
/*      */   {
/* 1027 */     return listFuture(ImmutableList.copyOf(futures), true, MoreExecutors.directExecutor());
/*      */   }
/*      */   
/*      */   private static final class WrappedCombiner<T> implements Callable<T> {
/*      */     final Callable<T> delegate;
/*      */     CombinerFuture<T> outputFuture;
/*      */     
/*      */     WrappedCombiner(Callable<T> delegate) {
/* 1035 */       this.delegate = ((Callable)Preconditions.checkNotNull(delegate));
/*      */     }
/*      */     
/*      */     public T call() throws Exception {
/*      */       try {
/* 1040 */         return (T)this.delegate.call();
/*      */       } catch (ExecutionException e) {
/* 1042 */         this.outputFuture.setException(e.getCause());
/*      */       } catch (CancellationException e) {
/* 1044 */         this.outputFuture.cancel(false);
/*      */       }
/*      */       
/*      */ 
/* 1048 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class CombinerFuture<V> extends ListenableFutureTask<V> {
/*      */     ImmutableList<ListenableFuture<?>> futures;
/*      */     
/*      */     CombinerFuture(Callable<V> callable, ImmutableList<ListenableFuture<?>> futures) {
/* 1056 */       super();
/* 1057 */       this.futures = futures;
/*      */     }
/*      */     
/*      */     public boolean cancel(boolean mayInterruptIfRunning) {
/* 1061 */       ImmutableList<ListenableFuture<?>> futures = this.futures;
/* 1062 */       if (super.cancel(mayInterruptIfRunning)) {
/* 1063 */         for (ListenableFuture<?> future : futures) {
/* 1064 */           future.cancel(mayInterruptIfRunning);
/*      */         }
/* 1066 */         return true;
/*      */       }
/* 1068 */       return false;
/*      */     }
/*      */     
/*      */     protected void done() {
/* 1072 */       super.done();
/* 1073 */       this.futures = null;
/*      */     }
/*      */     
/*      */     protected void setException(Throwable t) {
/* 1077 */       super.setException(t);
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
/*      */   public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> future)
/*      */   {
/* 1091 */     return new NonCancellationPropagatingFuture(future);
/*      */   }
/*      */   
/*      */ 
/*      */   private static class NonCancellationPropagatingFuture<V>
/*      */     extends AbstractFuture<V>
/*      */   {
/*      */     NonCancellationPropagatingFuture(final ListenableFuture<V> delegate)
/*      */     {
/* 1100 */       Preconditions.checkNotNull(delegate);
/* 1101 */       Futures.addCallback(delegate, new FutureCallback()
/*      */       {
/*      */         public void onSuccess(V result) {
/* 1104 */           NonCancellationPropagatingFuture.this.set(result);
/*      */         }
/*      */         
/*      */         public void onFailure(Throwable t)
/*      */         {
/* 1109 */           if (delegate.isCancelled()) {
/* 1110 */             NonCancellationPropagatingFuture.this.cancel(false);
/*      */           } else
/* 1112 */             NonCancellationPropagatingFuture.this.setException(t); } }, MoreExecutors.directExecutor());
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
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... futures)
/*      */   {
/* 1137 */     return listFuture(ImmutableList.copyOf(futures), false, MoreExecutors.directExecutor());
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
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> futures)
/*      */   {
/* 1158 */     return listFuture(ImmutableList.copyOf(futures), false, MoreExecutors.directExecutor());
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
/*      */   @Beta
/*      */   public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> futures)
/*      */   {
/* 1179 */     ConcurrentLinkedQueue<AsyncSettableFuture<T>> delegates = Queues.newConcurrentLinkedQueue();
/*      */     
/* 1181 */     ImmutableList.Builder<ListenableFuture<T>> listBuilder = ImmutableList.builder();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1192 */     SerializingExecutor executor = new SerializingExecutor(MoreExecutors.directExecutor());
/* 1193 */     for (final ListenableFuture<? extends T> future : futures) {
/* 1194 */       AsyncSettableFuture<T> delegate = AsyncSettableFuture.create();
/*      */       
/* 1196 */       delegates.add(delegate);
/* 1197 */       future.addListener(new Runnable()
/*      */       {
/* 1199 */         public void run() { ((AsyncSettableFuture)this.val$delegates.remove()).setFuture(future); } }, executor);
/*      */       
/*      */ 
/* 1202 */       listBuilder.add(delegate);
/*      */     }
/* 1204 */     return listBuilder.build();
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
/*      */   public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback)
/*      */   {
/* 1258 */     addCallback(future, callback, MoreExecutors.directExecutor());
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
/*      */   public static <V> void addCallback(ListenableFuture<V> future, final FutureCallback<? super V> callback, Executor executor)
/*      */   {
/* 1300 */     Preconditions.checkNotNull(callback);
/* 1301 */     Runnable callbackListener = new Runnable()
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         V value;
/*      */         try
/*      */         {
/* 1308 */           value = Uninterruptibles.getUninterruptibly(this.val$future);
/*      */         } catch (ExecutionException e) {
/* 1310 */           callback.onFailure(e.getCause());
/* 1311 */           return;
/*      */         } catch (RuntimeException e) {
/* 1313 */           callback.onFailure(e);
/* 1314 */           return;
/*      */         } catch (Error e) {
/* 1316 */           callback.onFailure(e);
/* 1317 */           return;
/*      */         }
/* 1319 */         callback.onSuccess(value);
/*      */       }
/* 1321 */     };
/* 1322 */     future.addListener(callbackListener, executor);
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
/*      */   public static <V, X extends Exception> V get(Future<V> future, Class<X> exceptionClass)
/*      */     throws Exception
/*      */   {
/* 1374 */     Preconditions.checkNotNull(future);
/* 1375 */     Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
/*      */     
/*      */     try
/*      */     {
/* 1379 */       return (V)future.get();
/*      */     } catch (InterruptedException e) {
/* 1381 */       Thread.currentThread().interrupt();
/* 1382 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (ExecutionException e) {
/* 1384 */       wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
/* 1385 */       throw new AssertionError();
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
/*      */ 
/*      */ 
/*      */   public static <V, X extends Exception> V get(Future<V> future, long timeout, TimeUnit unit, Class<X> exceptionClass)
/*      */     throws Exception
/*      */   {
/* 1440 */     Preconditions.checkNotNull(future);
/* 1441 */     Preconditions.checkNotNull(unit);
/* 1442 */     Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
/*      */     
/*      */     try
/*      */     {
/* 1446 */       return (V)future.get(timeout, unit);
/*      */     } catch (InterruptedException e) {
/* 1448 */       Thread.currentThread().interrupt();
/* 1449 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (TimeoutException e) {
/* 1451 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (ExecutionException e) {
/* 1453 */       wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
/* 1454 */       throw new AssertionError();
/*      */     }
/*      */   }
/*      */   
/*      */   private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable cause, Class<X> exceptionClass) throws Exception
/*      */   {
/* 1460 */     if ((cause instanceof Error)) {
/* 1461 */       throw new ExecutionError((Error)cause);
/*      */     }
/* 1463 */     if ((cause instanceof RuntimeException)) {
/* 1464 */       throw new UncheckedExecutionException(cause);
/*      */     }
/* 1466 */     throw newWithCause(exceptionClass, cause);
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
/*      */   public static <V> V getUnchecked(Future<V> future)
/*      */   {
/* 1507 */     Preconditions.checkNotNull(future);
/*      */     try {
/* 1509 */       return (V)Uninterruptibles.getUninterruptibly(future);
/*      */     } catch (ExecutionException e) {
/* 1511 */       wrapAndThrowUnchecked(e.getCause());
/* 1512 */       throw new AssertionError();
/*      */     }
/*      */   }
/*      */   
/*      */   private static void wrapAndThrowUnchecked(Throwable cause) {
/* 1517 */     if ((cause instanceof Error)) {
/* 1518 */       throw new ExecutionError((Error)cause);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1525 */     throw new UncheckedExecutionException(cause);
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
/*      */   private static <X extends Exception> X newWithCause(Class<X> exceptionClass, Throwable cause)
/*      */   {
/* 1549 */     List<Constructor<X>> constructors = Arrays.asList(exceptionClass.getConstructors());
/*      */     
/* 1551 */     for (Constructor<X> constructor : preferringStrings(constructors)) {
/* 1552 */       X instance = (Exception)newFromConstructor(constructor, cause);
/* 1553 */       if (instance != null) {
/* 1554 */         if (instance.getCause() == null) {
/* 1555 */           instance.initCause(cause);
/*      */         }
/* 1557 */         return instance;
/*      */       }
/*      */     }
/* 1560 */     ??? = String.valueOf(String.valueOf(exceptionClass));throw new IllegalArgumentException(82 + ???.length() + "No appropriate constructor for exception of type " + ??? + " in response to chained exception", cause);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> constructors)
/*      */   {
/* 1567 */     return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
/*      */   }
/*      */   
/* 1570 */   private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function()
/*      */   {
/*      */     public Boolean apply(Constructor<?> input) {
/* 1573 */       return Boolean.valueOf(Arrays.asList(input.getParameterTypes()).contains(String.class));
/*      */     }
/* 1570 */   }).reverse();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   private static <X> X newFromConstructor(Constructor<X> constructor, Throwable cause)
/*      */   {
/* 1579 */     Class<?>[] paramTypes = constructor.getParameterTypes();
/* 1580 */     Object[] params = new Object[paramTypes.length];
/* 1581 */     for (int i = 0; i < paramTypes.length; i++) {
/* 1582 */       Class<?> paramType = paramTypes[i];
/* 1583 */       if (paramType.equals(String.class)) {
/* 1584 */         params[i] = cause.toString();
/* 1585 */       } else if (paramType.equals(Throwable.class)) {
/* 1586 */         params[i] = cause;
/*      */       } else {
/* 1588 */         return null;
/*      */       }
/*      */     }
/*      */     try {
/* 1592 */       return (X)constructor.newInstance(params);
/*      */     } catch (IllegalArgumentException e) {
/* 1594 */       return null;
/*      */     } catch (InstantiationException e) {
/* 1596 */       return null;
/*      */     } catch (IllegalAccessException e) {
/* 1598 */       return null;
/*      */     } catch (InvocationTargetException e) {}
/* 1600 */     return null;
/*      */   }
/*      */   
/*      */   private static abstract interface FutureCombiner<V, C>
/*      */   {
/*      */     public abstract C combine(List<Optional<V>> paramList);
/*      */   }
/*      */   
/*      */   private static class CombinedFuture<V, C> extends AbstractFuture<C> {
/* 1609 */     private static final Logger logger = Logger.getLogger(CombinedFuture.class.getName());
/*      */     
/*      */     ImmutableCollection<? extends ListenableFuture<? extends V>> futures;
/*      */     
/*      */     final boolean allMustSucceed;
/*      */     final AtomicInteger remaining;
/*      */     FutureCombiner<V, C> combiner;
/*      */     List<Optional<V>> values;
/* 1617 */     final Object seenExceptionsLock = new Object();
/*      */     
/*      */     Set<Throwable> seenExceptions;
/*      */     
/*      */ 
/*      */     CombinedFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed, Executor listenerExecutor, FutureCombiner<V, C> combiner)
/*      */     {
/* 1624 */       this.futures = futures;
/* 1625 */       this.allMustSucceed = allMustSucceed;
/* 1626 */       this.remaining = new AtomicInteger(futures.size());
/* 1627 */       this.combiner = combiner;
/* 1628 */       this.values = Lists.newArrayListWithCapacity(futures.size());
/* 1629 */       init(listenerExecutor);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void init(Executor listenerExecutor)
/*      */     {
/* 1637 */       addListener(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/* 1641 */           if (CombinedFuture.this.isCancelled()) {
/* 1642 */             for (ListenableFuture<?> future : CombinedFuture.this.futures) {
/* 1643 */               future.cancel(CombinedFuture.this.wasInterrupted());
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1648 */           CombinedFuture.this.futures = null;
/*      */           
/*      */ 
/*      */ 
/* 1652 */           CombinedFuture.this.values = null;
/*      */           
/*      */ 
/* 1655 */           CombinedFuture.this.combiner = null; } }, MoreExecutors.directExecutor());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1662 */       if (this.futures.isEmpty()) {
/* 1663 */         set(this.combiner.combine(ImmutableList.of()));
/* 1664 */         return;
/*      */       }
/*      */       
/*      */ 
/* 1668 */       for (int i = 0; i < this.futures.size(); i++) {
/* 1669 */         this.values.add(null);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1680 */       int i = 0;
/* 1681 */       for (final ListenableFuture<? extends V> listenable : this.futures) {
/* 1682 */         final int index = i++;
/* 1683 */         listenable.addListener(new Runnable()
/*      */         {
/*      */ 
/* 1686 */           public void run() { CombinedFuture.this.setOneValue(index, listenable); } }, listenerExecutor);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setExceptionAndMaybeLog(Throwable throwable)
/*      */     {
/* 1699 */       boolean visibleFromOutputFuture = false;
/* 1700 */       boolean firstTimeSeeingThisException = true;
/* 1701 */       if (this.allMustSucceed)
/*      */       {
/*      */ 
/* 1704 */         visibleFromOutputFuture = super.setException(throwable);
/*      */         
/* 1706 */         synchronized (this.seenExceptionsLock) {
/* 1707 */           if (this.seenExceptions == null) {
/* 1708 */             this.seenExceptions = Sets.newHashSet();
/*      */           }
/* 1710 */           firstTimeSeeingThisException = this.seenExceptions.add(throwable);
/*      */         }
/*      */       }
/*      */       
/* 1714 */       if (((throwable instanceof Error)) || ((this.allMustSucceed) && (!visibleFromOutputFuture) && (firstTimeSeeingThisException)))
/*      */       {
/* 1716 */         logger.log(Level.SEVERE, "input future failed.", throwable);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setOneValue(int index, Future<? extends V> future)
/*      */     {
/* 1724 */       List<Optional<V>> localValues = this.values;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1732 */       if ((isDone()) || (localValues == null))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1737 */         Preconditions.checkState((this.allMustSucceed) || (isCancelled()), "Future was done before all dependencies completed");
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1742 */         Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
/*      */         
/* 1744 */         V returnValue = Uninterruptibles.getUninterruptibly(future);
/* 1745 */         if (localValues != null)
/* 1746 */           localValues.set(index, Optional.fromNullable(returnValue));
/*      */       } catch (CancellationException e) { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1749 */         if (this.allMustSucceed)
/*      */         {
/*      */ 
/* 1752 */           cancel(false); }
/*      */       } catch (ExecutionException e) { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1755 */         setExceptionAndMaybeLog(e.getCause()); } catch (Throwable t) { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1757 */         setExceptionAndMaybeLog(t); } finally { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1759 */         int newRemaining = this.remaining.decrementAndGet();
/* 1760 */         Preconditions.checkState(newRemaining >= 0, "Less than 0 remaining futures");
/* 1761 */         if (newRemaining == 0) {
/* 1762 */           FutureCombiner<V, C> localCombiner = this.combiner;
/* 1763 */           if ((localCombiner != null) && (localValues != null)) {
/* 1764 */             set(localCombiner.combine(localValues));
/*      */           } else {
/* 1766 */             Preconditions.checkState(isDone());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static <V> ListenableFuture<List<V>> listFuture(ImmutableList<ListenableFuture<? extends V>> futures, boolean allMustSucceed, Executor listenerExecutor)
/*      */   {
/* 1777 */     new CombinedFuture(futures, allMustSucceed, listenerExecutor, new FutureCombiner()
/*      */     {
/*      */ 
/*      */       public List<V> combine(List<Optional<V>> values)
/*      */       {
/* 1782 */         List<V> result = Lists.newArrayList();
/* 1783 */         for (Optional<V> element : values) {
/* 1784 */           result.add(element != null ? element.orNull() : null);
/*      */         }
/* 1786 */         return Collections.unmodifiableList(result);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class MappingCheckedFuture<V, X extends Exception>
/*      */     extends AbstractCheckedFuture<V, X>
/*      */   {
/*      */     final Function<? super Exception, X> mapper;
/*      */     
/*      */ 
/*      */ 
/*      */     MappingCheckedFuture(ListenableFuture<V> delegate, Function<? super Exception, X> mapper)
/*      */     {
/* 1802 */       super();
/*      */       
/* 1804 */       this.mapper = ((Function)Preconditions.checkNotNull(mapper));
/*      */     }
/*      */     
/*      */     protected X mapException(Exception e)
/*      */     {
/* 1809 */       return (Exception)this.mapper.apply(e);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\Futures.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */