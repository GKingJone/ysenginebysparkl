/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeBindings;
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.AnnotatedElement;
/*    */ import java.lang.reflect.Modifier;
/*    */ import java.lang.reflect.Type;
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
/*    */ public abstract class Annotated
/*    */ {
/*    */   public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
/*    */   
/*    */   public abstract boolean hasAnnotation(Class<?> paramClass);
/*    */   
/*    */   public abstract boolean hasOneOf(Class<? extends Annotation>[] paramArrayOfClass);
/*    */   
/*    */   public abstract Annotated withAnnotations(AnnotationMap paramAnnotationMap);
/*    */   
/*    */   public final Annotated withFallBackAnnotationsFrom(Annotated annotated)
/*    */   {
/* 39 */     return withAnnotations(AnnotationMap.merge(getAllAnnotations(), annotated.getAllAnnotations()));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract AnnotatedElement getAnnotated();
/*    */   
/*    */ 
/*    */   protected abstract int getModifiers();
/*    */   
/*    */ 
/*    */   public final boolean isPublic()
/*    */   {
/* 52 */     return Modifier.isPublic(getModifiers());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract String getName();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract JavaType getType();
/*    */   
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public final JavaType getType(TypeBindings bogus)
/*    */   {
/* 70 */     return getType();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public Type getGenericType()
/*    */   {
/* 84 */     return getRawType();
/*    */   }
/*    */   
/*    */   public abstract Class<?> getRawType();
/*    */   
/*    */   public abstract Iterable<Annotation> annotations();
/*    */   
/*    */   protected abstract AnnotationMap getAllAnnotations();
/*    */   
/*    */   public abstract boolean equals(Object paramObject);
/*    */   
/*    */   public abstract int hashCode();
/*    */   
/*    */   public abstract String toString();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\Annotated.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */