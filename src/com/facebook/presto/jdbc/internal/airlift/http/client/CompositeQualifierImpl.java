/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.util.Arrays;
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
/*    */ class CompositeQualifierImpl
/*    */   implements CompositeQualifier
/*    */ {
/*    */   private final Class<?>[] value;
/*    */   
/*    */   public static CompositeQualifier compositeQualifier(Class<?>... classes)
/*    */   {
/* 29 */     return new CompositeQualifierImpl(classes);
/*    */   }
/*    */   
/*    */   private CompositeQualifierImpl(Class<?>... classes)
/*    */   {
/* 34 */     this.value = classes;
/*    */   }
/*    */   
/*    */ 
/*    */   public Class<?>[] value()
/*    */   {
/* 40 */     return this.value;
/*    */   }
/*    */   
/*    */ 
/*    */   public Class<? extends Annotation> annotationType()
/*    */   {
/* 46 */     return CompositeQualifier.class;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 52 */     return 127 * "value".hashCode() ^ Arrays.hashCode(this.value);
/*    */   }
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 57 */     if (!(o instanceof CompositeQualifier)) {
/* 58 */       return false;
/*    */     }
/* 60 */     CompositeQualifier other = (CompositeQualifier)o;
/* 61 */     return Arrays.equals(this.value, other.value());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\CompositeQualifierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */