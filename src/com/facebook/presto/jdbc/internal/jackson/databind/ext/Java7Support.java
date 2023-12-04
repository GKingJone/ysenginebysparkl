/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ext;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedParameter;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Java7Support
/*    */ {
/*    */   private static final Java7Support IMPL;
/*    */   
/*    */   static
/*    */   {
/* 21 */     Java7Support impl = null;
/*    */     try {
/* 23 */       Class<?> cls = Class.forName("com.facebook.presto.jdbc.internal.jackson.databind.ext.Java7SupportImpl");
/* 24 */       impl = (Java7Support)cls.newInstance();
/*    */     }
/*    */     catch (Throwable t) {
/* 27 */       Logger.getLogger(Java7Support.class.getName()).warning("Unable to load JDK7 types (annotations, java.nio.file.Path): no Java7 support added");
/*    */     }
/*    */     
/* 30 */     IMPL = impl; }
/*    */   public abstract JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> paramClass);
/*    */   public abstract JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> paramClass);
/*    */   public abstract Class<?> getClassJavaNioFilePath();
/* 34 */   public abstract PropertyName findConstructorName(AnnotatedParameter paramAnnotatedParameter); public abstract Boolean hasCreatorAnnotation(Annotated paramAnnotated); public abstract Boolean findTransient(Annotated paramAnnotated); public static Java7Support instance() { return IMPL; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ext\Java7Support.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */