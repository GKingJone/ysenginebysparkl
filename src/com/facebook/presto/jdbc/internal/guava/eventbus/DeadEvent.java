/*    */ package com.facebook.presto.jdbc.internal.guava.eventbus;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public class DeadEvent
/*    */ {
/*    */   private final Object source;
/*    */   private final Object event;
/*    */   
/*    */   public DeadEvent(Object source, Object event)
/*    */   {
/* 47 */     this.source = Preconditions.checkNotNull(source);
/* 48 */     this.event = Preconditions.checkNotNull(event);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object getSource()
/*    */   {
/* 58 */     return this.source;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object getEvent()
/*    */   {
/* 68 */     return this.event;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\eventbus\DeadEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */