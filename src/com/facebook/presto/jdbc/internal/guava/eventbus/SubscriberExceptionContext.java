/*    */ package com.facebook.presto.jdbc.internal.guava.eventbus;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SubscriberExceptionContext
/*    */ {
/*    */   private final EventBus eventBus;
/*    */   private final Object event;
/*    */   private final Object subscriber;
/*    */   private final Method subscriberMethod;
/*    */   
/*    */   SubscriberExceptionContext(EventBus eventBus, Object event, Object subscriber, Method subscriberMethod)
/*    */   {
/* 42 */     this.eventBus = ((EventBus)Preconditions.checkNotNull(eventBus));
/* 43 */     this.event = Preconditions.checkNotNull(event);
/* 44 */     this.subscriber = Preconditions.checkNotNull(subscriber);
/* 45 */     this.subscriberMethod = ((Method)Preconditions.checkNotNull(subscriberMethod));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public EventBus getEventBus()
/*    */   {
/* 53 */     return this.eventBus;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Object getEvent()
/*    */   {
/* 60 */     return this.event;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Object getSubscriber()
/*    */   {
/* 67 */     return this.subscriber;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Method getSubscriberMethod()
/*    */   {
/* 74 */     return this.subscriberMethod;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\eventbus\SubscriberExceptionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */