/*     */ package com.facebook.presto.jdbc.internal.guava.eventbus;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Executor;
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
/*     */ public class AsyncEventBus
/*     */   extends EventBus
/*     */ {
/*     */   private final Executor executor;
/*  38 */   private final ConcurrentLinkedQueue<EventWithSubscriber> eventsToDispatch = new ConcurrentLinkedQueue();
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
/*     */   public AsyncEventBus(String identifier, Executor executor)
/*     */   {
/*  51 */     super(identifier);
/*  52 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
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
/*     */   public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler)
/*     */   {
/*  67 */     super(subscriberExceptionHandler);
/*  68 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AsyncEventBus(Executor executor)
/*     */   {
/*  80 */     super("default");
/*  81 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */   }
/*     */   
/*     */   void enqueueEvent(Object event, EventSubscriber subscriber)
/*     */   {
/*  86 */     this.eventsToDispatch.offer(new EventWithSubscriber(event, subscriber));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dispatchQueuedEvents()
/*     */   {
/*     */     for (;;)
/*     */     {
/*  97 */       EventWithSubscriber eventWithSubscriber = (EventWithSubscriber)this.eventsToDispatch.poll();
/*  98 */       if (eventWithSubscriber == null) {
/*     */         break;
/*     */       }
/*     */       
/* 102 */       dispatch(eventWithSubscriber.event, eventWithSubscriber.subscriber);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void dispatch(final Object event, final EventSubscriber subscriber)
/*     */   {
/* 111 */     Preconditions.checkNotNull(event);
/* 112 */     Preconditions.checkNotNull(subscriber);
/* 113 */     this.executor.execute(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 117 */         AsyncEventBus.this.dispatch(event, subscriber);
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\eventbus\AsyncEventBus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */