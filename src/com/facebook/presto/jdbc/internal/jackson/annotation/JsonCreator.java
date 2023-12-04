/*    */ package com.facebook.presto.jdbc.internal.jackson.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.annotation.Retention;
/*    */ import java.lang.annotation.RetentionPolicy;
/*    */ import java.lang.annotation.Target;
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
/*    */ @Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR})
/*    */ @Retention(RetentionPolicy.RUNTIME)
/*    */ @JacksonAnnotation
/*    */ public @interface JsonCreator
/*    */ {
/*    */   Mode mode() default Mode.DEFAULT;
/*    */   
/*    */   public static enum Mode
/*    */   {
/* 73 */     DEFAULT, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 80 */     DELEGATING, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 90 */     PROPERTIES, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 97 */     DISABLED;
/*    */     
/*    */     private Mode() {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\annotation\JsonCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */