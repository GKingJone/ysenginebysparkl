/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.google.inject.Binder;
/*    */ import com.google.inject.Module;
/*    */ import java.lang.annotation.Annotation;
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
/*    */ abstract class AbstractHttpClientModule
/*    */   implements Module
/*    */ {
/*    */   protected final String name;
/*    */   protected final Class<? extends Annotation> annotation;
/*    */   protected Binder binder;
/*    */   
/*    */   protected AbstractHttpClientModule(String name, Class<? extends Annotation> annotation)
/*    */   {
/* 34 */     this.name = ((String)Preconditions.checkNotNull(name, "name is null"));
/* 35 */     this.annotation = ((Class)Preconditions.checkNotNull(annotation, "annotation is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public final void configure(Binder binder)
/*    */   {
/* 41 */     this.binder = binder;
/* 42 */     configure();
/*    */   }
/*    */   
/*    */   public abstract void configure();
/*    */   
/*    */   public abstract void addAlias(Class<? extends Annotation> paramClass);
/*    */   
/*    */   public abstract Annotation getFilterQualifier();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\AbstractHttpClientModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */