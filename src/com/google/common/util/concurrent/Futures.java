/*      */ package com.google.common.util.concurrent;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Optional;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.collect.ImmutableCollection;
/*      */ import com.google.common.collect.ImmutableList;
/*      */ import com.google.common.collect.Lists;
/*      */ import com.google.common.collect.Ordering;
/*      */ import com.google.common.collect.Sets;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.UndeclaredThrowableException;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.CancellationException;
/*      */ import java.util.concurrent.CountDownLatch;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.TimeoutException;
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
/*      */ 
/*      */ @Beta
/*      */ public final class Futures
/*      */ {
/*      */   public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> future, Function<Exception, X> mapper)
/*      */   {
/*   88 */     return new MappingCheckedFuture((ListenableFuture)Preconditions.checkNotNull(future), mapper);
/*      */   }
/*      */   
/*      */   private static abstract class ImmediateFuture<V>
/*      */     implements ListenableFuture<V>
/*      */   {
/*   94 */     private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());
/*      */     
/*      */ 
/*      */     public void addListener(Runnable listener, Executor executor)
/*      */     {
/*   99 */       Preconditions.checkNotNull(listener, "Runnable was null.");
/*  100 */       Preconditions.checkNotNull(executor, "Executor was null.");
/*      */       try {
/*  102 */         executor.execute(listener);
/*      */       }
/*      */       catch (RuntimeException e)
/*      */       {
/*  106 */         log.log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean cancel(boolean mayInterruptIfRunning)
/*      */     {
/*  113 */       return false;
/*      */     }
/*      */     
/*      */     public abstract V get()
/*      */       throws ExecutionException;
/*      */     
/*      */     public V get(long timeout, TimeUnit unit) throws ExecutionException
/*      */     {
/*  121 */       Preconditions.checkNotNull(unit);
/*  122 */       return (V)get();
/*      */     }
/*      */     
/*      */     public boolean isCancelled()
/*      */     {
/*  127 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  132 */     public boolean isDone() { return true; }
/*      */   }
/*      */   
/*      */   private static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V> {
/*      */     @Nullable
/*      */     private final V value;
/*      */     
/*      */     ImmediateSuccessfulFuture(@Nullable V value) {
/*  140 */       super();
/*  141 */       this.value = value;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/*  146 */       return (V)this.value;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateSuccessfulCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
/*      */     @Nullable
/*      */     private final V value;
/*      */     
/*      */     ImmediateSuccessfulCheckedFuture(@Nullable V value) {
/*  155 */       super();
/*  156 */       this.value = value;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/*  161 */       return (V)this.value;
/*      */     }
/*      */     
/*      */     public V checkedGet()
/*      */     {
/*  166 */       return (V)this.value;
/*      */     }
/*      */     
/*      */     public V checkedGet(long timeout, TimeUnit unit)
/*      */     {
/*  171 */       Preconditions.checkNotNull(unit);
/*  172 */       return (V)this.value;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateFailedFuture<V> extends ImmediateFuture<V> {
/*      */     private final Throwable thrown;
/*      */     
/*      */     ImmediateFailedFuture(Throwable thrown) {
/*  180 */       super();
/*  181 */       this.thrown = thrown;
/*      */     }
/*      */     
/*      */     public V get() throws ExecutionException
/*      */     {
/*  186 */       throw new ExecutionException(this.thrown);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateCancelledFuture<V> extends ImmediateFuture<V> {
/*      */     private final CancellationException thrown;
/*      */     
/*      */     ImmediateCancelledFuture() {
/*  194 */       super();
/*  195 */       this.thrown = new CancellationException("Immediate cancelled future.");
/*      */     }
/*      */     
/*      */     public boolean isCancelled()
/*      */     {
/*  200 */       return true;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/*  205 */       throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ImmediateFailedCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X>
/*      */   {
/*      */     private final X thrown;
/*      */     
/*      */     ImmediateFailedCheckedFuture(X thrown)
/*      */     {
/*  215 */       super();
/*  216 */       this.thrown = thrown;
/*      */     }
/*      */     
/*      */     public V get() throws ExecutionException
/*      */     {
/*  221 */       throw new ExecutionException(this.thrown);
/*      */     }
/*      */     
/*      */     public V checkedGet() throws Exception
/*      */     {
/*  226 */       throw this.thrown;
/*      */     }
/*      */     
/*      */     public V checkedGet(long timeout, TimeUnit unit) throws Exception
/*      */     {
/*  231 */       Preconditions.checkNotNull(unit);
/*  232 */       throw this.thrown;
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
/*  243 */     return new ImmediateSuccessfulFuture(value);
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
/*  256 */     return new ImmediateSuccessfulCheckedFuture(value);
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
/*  270 */     Preconditions.checkNotNull(throwable);
/*  271 */     return new ImmediateFailedFuture(throwable);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <V> ListenableFuture<V> immediateCancelledFuture()
/*      */   {
/*  281 */     return new ImmediateCancelledFuture();
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
/*  296 */     Preconditions.checkNotNull(exception);
/*  297 */     return new ImmediateFailedCheckedFuture(exception);
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
/*  375 */     return withFallback(input, fallback, MoreExecutors.sameThreadExecutor());
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
/*  439 */     Preconditions.checkNotNull(fallback);
/*  440 */     return new FallbackFuture(input, fallback, executor);
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
/*  454 */       this.running = input;
/*  455 */       Futures.addCallback(this.running, new FutureCallback()
/*      */       {
/*      */         public void onSuccess(V value) {
/*  458 */           FallbackFuture.this.set(value);
/*      */         }
/*      */         
/*      */         public void onFailure(Throwable t)
/*      */         {
/*  463 */           if (FallbackFuture.this.isCancelled()) {
/*  464 */             return;
/*      */           }
/*      */           try {
/*  467 */             FallbackFuture.this.running = fallback.create(t);
/*  468 */             if (FallbackFuture.this.isCancelled()) {
/*  469 */               FallbackFuture.this.running.cancel(FallbackFuture.this.wasInterrupted());
/*  470 */               return;
/*      */             }
/*  472 */             Futures.addCallback(FallbackFuture.this.running, new FutureCallback()
/*      */             {
/*      */               public void onSuccess(V value) {
/*  475 */                 FallbackFuture.this.set(value);
/*      */               }
/*      */               
/*      */               public void onFailure(Throwable t)
/*      */               {
/*  480 */                 if (FallbackFuture.this.running.isCancelled()) {
/*  481 */                   FallbackFuture.this.cancel(false);
/*      */                 } else
/*  483 */                   FallbackFuture.this.setException(t); } }, MoreExecutors.sameThreadExecutor());
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  488 */             FallbackFuture.this.setException(e); } } }, executor);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean cancel(boolean mayInterruptIfRunning)
/*      */     {
/*  496 */       if (super.cancel(mayInterruptIfRunning)) {
/*  497 */         this.running.cancel(mayInterruptIfRunning);
/*  498 */         return true;
/*      */       }
/*  500 */       return false;
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
/*  561 */     return transform(input, function, MoreExecutors.sameThreadExecutor());
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
/*  606 */     ChainingListenableFuture<I, O> output = new ChainingListenableFuture(function, input, null);
/*      */     
/*  608 */     input.addListener(output, executor);
/*  609 */     return output;
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
/*      */   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function)
/*      */   {
/*  667 */     return transform(input, function, MoreExecutors.sameThreadExecutor());
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
/*  709 */     Preconditions.checkNotNull(function);
/*  710 */     AsyncFunction<I, O> wrapperFunction = new AsyncFunction()
/*      */     {
/*      */       public ListenableFuture<O> apply(I input) {
/*  713 */         O output = this.val$function.apply(input);
/*  714 */         return Futures.immediateFuture(output);
/*      */       }
/*  716 */     };
/*  717 */     return transform(input, wrapperFunction, executor);
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
/*  745 */     Preconditions.checkNotNull(input);
/*  746 */     Preconditions.checkNotNull(function);
/*  747 */     new Future()
/*      */     {
/*      */       public boolean cancel(boolean mayInterruptIfRunning)
/*      */       {
/*  751 */         return this.val$input.cancel(mayInterruptIfRunning);
/*      */       }
/*      */       
/*      */       public boolean isCancelled()
/*      */       {
/*  756 */         return this.val$input.isCancelled();
/*      */       }
/*      */       
/*      */       public boolean isDone()
/*      */       {
/*  761 */         return this.val$input.isDone();
/*      */       }
/*      */       
/*      */       public O get() throws InterruptedException, ExecutionException
/*      */       {
/*  766 */         return (O)applyTransformation(this.val$input.get());
/*      */       }
/*      */       
/*      */       public O get(long timeout, TimeUnit unit)
/*      */         throws InterruptedException, ExecutionException, TimeoutException
/*      */       {
/*  772 */         return (O)applyTransformation(this.val$input.get(timeout, unit));
/*      */       }
/*      */       
/*      */       private O applyTransformation(I input) throws ExecutionException {
/*      */         try {
/*  777 */           return (O)function.apply(input);
/*      */         } catch (Throwable t) {
/*  779 */           throw new ExecutionException(t);
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
/*  811 */     private final CountDownLatch outputCreated = new CountDownLatch(1);
/*      */     
/*      */ 
/*      */     private ChainingListenableFuture(AsyncFunction<? super I, ? extends O> function, ListenableFuture<? extends I> inputFuture)
/*      */     {
/*  816 */       this.function = ((AsyncFunction)Preconditions.checkNotNull(function));
/*  817 */       this.inputFuture = ((ListenableFuture)Preconditions.checkNotNull(inputFuture));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean cancel(boolean mayInterruptIfRunning)
/*      */     {
/*  826 */       if (super.cancel(mayInterruptIfRunning))
/*      */       {
/*      */ 
/*  829 */         cancel(this.inputFuture, mayInterruptIfRunning);
/*  830 */         cancel(this.outputFuture, mayInterruptIfRunning);
/*  831 */         return true;
/*      */       }
/*  833 */       return false;
/*      */     }
/*      */     
/*      */     private void cancel(@Nullable Future<?> future, boolean mayInterruptIfRunning)
/*      */     {
/*  838 */       if (future != null) {
/*  839 */         future.cancel(mayInterruptIfRunning);
/*      */       }
/*      */     }
/*      */     
/*      */     public void run()
/*      */     {
/*      */       try {
/*      */         I sourceResult;
/*      */         try {
/*  848 */           sourceResult = Uninterruptibles.getUninterruptibly(this.inputFuture);
/*      */ 
/*      */         }
/*      */         catch (CancellationException e)
/*      */         {
/*  853 */           cancel(false); return;
/*      */         }
/*      */         catch (ExecutionException e)
/*      */         {
/*  857 */           setException(e.getCause()); return;
/*      */         }
/*      */         
/*      */ 
/*  861 */         final ListenableFuture<? extends O> outputFuture = this.outputFuture = this.function.apply(sourceResult);
/*      */         
/*  863 */         if (isCancelled()) {
/*  864 */           outputFuture.cancel(wasInterrupted());
/*  865 */           this.outputFuture = null;
/*      */         }
/*      */         else {
/*  868 */           outputFuture.addListener(new Runnable()
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  872 */                 ChainingListenableFuture.this.set(Uninterruptibles.getUninterruptibly(outputFuture));
/*      */ 
/*      */               }
/*      */               catch (CancellationException e)
/*      */               {
/*  877 */                 ChainingListenableFuture.this.cancel(false);
/*      */               }
/*      */               catch (ExecutionException e)
/*      */               {
/*  881 */                 ChainingListenableFuture.this.setException(e.getCause());
/*      */               }
/*      */               finally {
/*  884 */                 ChainingListenableFuture.this.outputFuture = null; } } }, MoreExecutors.sameThreadExecutor());
/*      */         }
/*      */         
/*      */       }
/*      */       catch (UndeclaredThrowableException e)
/*      */       {
/*  890 */         setException(e.getCause());
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  894 */         setException(t);
/*      */       }
/*      */       finally {
/*  897 */         this.function = null;
/*  898 */         this.inputFuture = null;
/*      */         
/*  900 */         this.outputCreated.countDown();
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
/*      */   public static <V> ListenableFuture<V> dereference(ListenableFuture<? extends ListenableFuture<? extends V>> nested)
/*      */   {
/*  928 */     return transform(nested, DEREFERENCER);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  934 */   private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction()
/*      */   {
/*      */     public ListenableFuture<Object> apply(ListenableFuture<Object> input) {
/*  937 */       return input;
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
/*  960 */     return listFuture(ImmutableList.copyOf(futures), true, MoreExecutors.sameThreadExecutor());
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
/*      */   @Beta
/*      */   public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> futures)
/*      */   {
/*  983 */     return listFuture(ImmutableList.copyOf(futures), true, MoreExecutors.sameThreadExecutor());
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
/*      */   public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> future)
/*      */   {
/*  997 */     return new NonCancellationPropagatingFuture(future);
/*      */   }
/*      */   
/*      */ 
/*      */   private static class NonCancellationPropagatingFuture<V>
/*      */     extends AbstractFuture<V>
/*      */   {
/*      */     NonCancellationPropagatingFuture(final ListenableFuture<V> delegate)
/*      */     {
/* 1006 */       Preconditions.checkNotNull(delegate);
/* 1007 */       Futures.addCallback(delegate, new FutureCallback()
/*      */       {
/*      */         public void onSuccess(V result) {
/* 1010 */           NonCancellationPropagatingFuture.this.set(result);
/*      */         }
/*      */         
/*      */         public void onFailure(Throwable t)
/*      */         {
/* 1015 */           if (delegate.isCancelled()) {
/* 1016 */             NonCancellationPropagatingFuture.this.cancel(false);
/*      */           } else
/* 1018 */             NonCancellationPropagatingFuture.this.setException(t); } }, MoreExecutors.sameThreadExecutor());
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
/* 1043 */     return listFuture(ImmutableList.copyOf(futures), false, MoreExecutors.sameThreadExecutor());
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
/*      */   public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> futures)
/*      */   {
/* 1065 */     return listFuture(ImmutableList.copyOf(futures), false, MoreExecutors.sameThreadExecutor());
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
/*      */   public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback)
/*      */   {
/* 1120 */     addCallback(future, callback, MoreExecutors.sameThreadExecutor());
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
/* 1162 */     Preconditions.checkNotNull(callback);
/* 1163 */     Runnable callbackListener = new Runnable()
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         V value;
/*      */         try
/*      */         {
/* 1170 */           value = Uninterruptibles.getUninterruptibly(this.val$future);
/*      */         } catch (ExecutionException e) {
/* 1172 */           callback.onFailure(e.getCause());
/* 1173 */           return;
/*      */         } catch (RuntimeException e) {
/* 1175 */           callback.onFailure(e);
/* 1176 */           return;
/*      */         } catch (Error e) {
/* 1178 */           callback.onFailure(e);
/* 1179 */           return;
/*      */         }
/* 1181 */         callback.onSuccess(value);
/*      */       }
/* 1183 */     };
/* 1184 */     future.addListener(callbackListener, executor);
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
/* 1236 */     Preconditions.checkNotNull(future);
/* 1237 */     Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
/*      */     
/*      */     try
/*      */     {
/* 1241 */       return (V)future.get();
/*      */     } catch (InterruptedException e) {
/* 1243 */       Thread.currentThread().interrupt();
/* 1244 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (ExecutionException e) {
/* 1246 */       wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
/* 1247 */       throw new AssertionError();
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
/* 1302 */     Preconditions.checkNotNull(future);
/* 1303 */     Preconditions.checkNotNull(unit);
/* 1304 */     Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
/*      */     
/*      */     try
/*      */     {
/* 1308 */       return (V)future.get(timeout, unit);
/*      */     } catch (InterruptedException e) {
/* 1310 */       Thread.currentThread().interrupt();
/* 1311 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (TimeoutException e) {
/* 1313 */       throw newWithCause(exceptionClass, e);
/*      */     } catch (ExecutionException e) {
/* 1315 */       wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
/* 1316 */       throw new AssertionError();
/*      */     }
/*      */   }
/*      */   
/*      */   private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable cause, Class<X> exceptionClass) throws Exception
/*      */   {
/* 1322 */     if ((cause instanceof Error)) {
/* 1323 */       throw new ExecutionError((Error)cause);
/*      */     }
/* 1325 */     if ((cause instanceof RuntimeException)) {
/* 1326 */       throw new UncheckedExecutionException(cause);
/*      */     }
/* 1328 */     throw newWithCause(exceptionClass, cause);
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
/* 1369 */     Preconditions.checkNotNull(future);
/*      */     try {
/* 1371 */       return (V)Uninterruptibles.getUninterruptibly(future);
/*      */     } catch (ExecutionException e) {
/* 1373 */       wrapAndThrowUnchecked(e.getCause());
/* 1374 */       throw new AssertionError();
/*      */     }
/*      */   }
/*      */   
/*      */   private static void wrapAndThrowUnchecked(Throwable cause) {
/* 1379 */     if ((cause instanceof Error)) {
/* 1380 */       throw new ExecutionError((Error)cause);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1387 */     throw new UncheckedExecutionException(cause);
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
/* 1411 */     List<Constructor<X>> constructors = Arrays.asList(exceptionClass.getConstructors());
/*      */     
/* 1413 */     for (Constructor<X> constructor : preferringStrings(constructors)) {
/* 1414 */       X instance = (Exception)newFromConstructor(constructor, cause);
/* 1415 */       if (instance != null) {
/* 1416 */         if (instance.getCause() == null) {
/* 1417 */           instance.initCause(cause);
/*      */         }
/* 1419 */         return instance;
/*      */       }
/*      */     }
/* 1422 */     throw new IllegalArgumentException("No appropriate constructor for exception of type " + exceptionClass + " in response to chained exception", cause);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> constructors)
/*      */   {
/* 1429 */     return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
/*      */   }
/*      */   
/* 1432 */   private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function()
/*      */   {
/*      */     public Boolean apply(Constructor<?> input) {
/* 1435 */       return Boolean.valueOf(Arrays.asList(input.getParameterTypes()).contains(String.class));
/*      */     }
/* 1432 */   }).reverse();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   private static <X> X newFromConstructor(Constructor<X> constructor, Throwable cause)
/*      */   {
/* 1441 */     Class<?>[] paramTypes = constructor.getParameterTypes();
/* 1442 */     Object[] params = new Object[paramTypes.length];
/* 1443 */     for (int i = 0; i < paramTypes.length; i++) {
/* 1444 */       Class<?> paramType = paramTypes[i];
/* 1445 */       if (paramType.equals(String.class)) {
/* 1446 */         params[i] = cause.toString();
/* 1447 */       } else if (paramType.equals(Throwable.class)) {
/* 1448 */         params[i] = cause;
/*      */       } else {
/* 1450 */         return null;
/*      */       }
/*      */     }
/*      */     try {
/* 1454 */       return (X)constructor.newInstance(params);
/*      */     } catch (IllegalArgumentException e) {
/* 1456 */       return null;
/*      */     } catch (InstantiationException e) {
/* 1458 */       return null;
/*      */     } catch (IllegalAccessException e) {
/* 1460 */       return null;
/*      */     } catch (InvocationTargetException e) {}
/* 1462 */     return null;
/*      */   }
/*      */   
/*      */   private static abstract interface FutureCombiner<V, C>
/*      */   {
/*      */     public abstract C combine(List<Optional<V>> paramList);
/*      */   }
/*      */   
/*      */   private static class CombinedFuture<V, C> extends AbstractFuture<C> {
/* 1471 */     private static final Logger logger = Logger.getLogger(CombinedFuture.class.getName());
/*      */     
/*      */     ImmutableCollection<? extends ListenableFuture<? extends V>> futures;
/*      */     
/*      */     final boolean allMustSucceed;
/*      */     final AtomicInteger remaining;
/*      */     FutureCombiner<V, C> combiner;
/*      */     List<Optional<V>> values;
/* 1479 */     final Object seenExceptionsLock = new Object();
/*      */     
/*      */     Set<Throwable> seenExceptions;
/*      */     
/*      */ 
/*      */     CombinedFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed, Executor listenerExecutor, FutureCombiner<V, C> combiner)
/*      */     {
/* 1486 */       this.futures = futures;
/* 1487 */       this.allMustSucceed = allMustSucceed;
/* 1488 */       this.remaining = new AtomicInteger(futures.size());
/* 1489 */       this.combiner = combiner;
/* 1490 */       this.values = Lists.newArrayListWithCapacity(futures.size());
/* 1491 */       init(listenerExecutor);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void init(Executor listenerExecutor)
/*      */     {
/* 1499 */       addListener(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/* 1503 */           if (CombinedFuture.this.isCancelled()) {
/* 1504 */             for (ListenableFuture<?> future : CombinedFuture.this.futures) {
/* 1505 */               future.cancel(CombinedFuture.this.wasInterrupted());
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1510 */           CombinedFuture.this.futures = null;
/*      */           
/*      */ 
/*      */ 
/* 1514 */           CombinedFuture.this.values = null;
/*      */           
/*      */ 
/* 1517 */           CombinedFuture.this.combiner = null; } }, MoreExecutors.sameThreadExecutor());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1524 */       if (this.futures.isEmpty()) {
/* 1525 */         set(this.combiner.combine(ImmutableList.of()));
/* 1526 */         return;
/*      */       }
/*      */       
/*      */ 
/* 1530 */       for (int i = 0; i < this.futures.size(); i++) {
/* 1531 */         this.values.add(null);
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
/* 1542 */       int i = 0;
/* 1543 */       for (final ListenableFuture<? extends V> listenable : this.futures) {
/* 1544 */         final int index = i++;
/* 1545 */         listenable.addListener(new Runnable()
/*      */         {
/*      */ 
/* 1548 */           public void run() { CombinedFuture.this.setOneValue(index, listenable); } }, listenerExecutor);
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
/* 1561 */       boolean visibleFromOutputFuture = false;
/* 1562 */       boolean firstTimeSeeingThisException = true;
/* 1563 */       if (this.allMustSucceed)
/*      */       {
/*      */ 
/* 1566 */         visibleFromOutputFuture = super.setException(throwable);
/*      */         
/* 1568 */         synchronized (this.seenExceptionsLock) {
/* 1569 */           if (this.seenExceptions == null) {
/* 1570 */             this.seenExceptions = Sets.newHashSet();
/*      */           }
/* 1572 */           firstTimeSeeingThisException = this.seenExceptions.add(throwable);
/*      */         }
/*      */       }
/*      */       
/* 1576 */       if (((throwable instanceof Error)) || ((this.allMustSucceed) && (!visibleFromOutputFuture) && (firstTimeSeeingThisException)))
/*      */       {
/* 1578 */         logger.log(Level.SEVERE, "input future failed.", throwable);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setOneValue(int index, Future<? extends V> future)
/*      */     {
/* 1586 */       List<Optional<V>> localValues = this.values;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1594 */       if ((isDone()) || (localValues == null))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1599 */         Preconditions.checkState((this.allMustSucceed) || (isCancelled()), "Future was done before all dependencies completed");
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1604 */         Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
/*      */         
/* 1606 */         V returnValue = Uninterruptibles.getUninterruptibly(future);
/* 1607 */         if (localValues != null)
/* 1608 */           localValues.set(index, Optional.fromNullable(returnValue));
/*      */       } catch (CancellationException e) { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1611 */         if (this.allMustSucceed)
/*      */         {
/*      */ 
/* 1614 */           cancel(false); }
/*      */       } catch (ExecutionException e) { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1617 */         setExceptionAndMaybeLog(e.getCause()); } catch (Throwable t) { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1619 */         setExceptionAndMaybeLog(t); } finally { int newRemaining;
/*      */         FutureCombiner<V, C> localCombiner;
/* 1621 */         int newRemaining = this.remaining.decrementAndGet();
/* 1622 */         Preconditions.checkState(newRemaining >= 0, "Less than 0 remaining futures");
/* 1623 */         if (newRemaining == 0) {
/* 1624 */           FutureCombiner<V, C> localCombiner = this.combiner;
/* 1625 */           if ((localCombiner != null) && (localValues != null)) {
/* 1626 */             set(localCombiner.combine(localValues));
/*      */           } else {
/* 1628 */             Preconditions.checkState(isDone());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static <V> ListenableFuture<List<V>> listFuture(ImmutableList<ListenableFuture<? extends V>> futures, boolean allMustSucceed, Executor listenerExecutor)
/*      */   {
/* 1640 */     new CombinedFuture(futures, allMustSucceed, listenerExecutor, new FutureCombiner()
/*      */     {
/*      */ 
/*      */       public List<V> combine(List<Optional<V>> values)
/*      */       {
/* 1645 */         List<V> result = Lists.newArrayList();
/* 1646 */         for (Optional<V> element : values) {
/* 1647 */           result.add(element != null ? element.orNull() : null);
/*      */         }
/* 1649 */         return Collections.unmodifiableList(result);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class MappingCheckedFuture<V, X extends Exception>
/*      */     extends AbstractCheckedFuture<V, X>
/*      */   {
/*      */     final Function<Exception, X> mapper;
/*      */     
/*      */ 
/*      */ 
/*      */     MappingCheckedFuture(ListenableFuture<V> delegate, Function<Exception, X> mapper)
/*      */     {
/* 1665 */       super();
/*      */       
/* 1667 */       this.mapper = ((Function)Preconditions.checkNotNull(mapper));
/*      */     }
/*      */     
/*      */     protected X mapException(Exception e)
/*      */     {
/* 1672 */       return (Exception)this.mapper.apply(e);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\Futures.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */