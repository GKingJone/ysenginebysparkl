/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.annotation;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JacksonAnnotation;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer.None;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter.None;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.annotation.Target;
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
/*     */ @Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PARAMETER})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JsonSerialize
/*     */ {
/*     */   Class<? extends JsonSerializer> using() default JsonSerializer.None.class;
/*     */   
/*     */   Class<? extends JsonSerializer> contentUsing() default JsonSerializer.None.class;
/*     */   
/*     */   Class<? extends JsonSerializer> keyUsing() default JsonSerializer.None.class;
/*     */   
/*     */   Class<? extends JsonSerializer> nullsUsing() default JsonSerializer.None.class;
/*     */   
/*     */   Class<?> as() default Void.class;
/*     */   
/*     */   Class<?> keyAs() default Void.class;
/*     */   
/*     */   Class<?> contentAs() default Void.class;
/*     */   
/*     */   Typing typing() default Typing.DEFAULT_TYPING;
/*     */   
/*     */   Class<? extends Converter> converter() default None.class;
/*     */   
/*     */   Class<? extends Converter> contentConverter() default None.class;
/*     */   
/*     */   @Deprecated
/*     */   Inclusion include() default Inclusion.DEFAULT_INCLUSION;
/*     */   
/*     */   @Deprecated
/*     */   public static enum Inclusion
/*     */   {
/* 195 */     ALWAYS, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 201 */     NON_NULL, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 211 */     NON_DEFAULT, 
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
/* 230 */     NON_EMPTY, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 238 */     DEFAULT_INCLUSION;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private Inclusion() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static enum Typing
/*     */   {
/* 253 */     DYNAMIC, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 259 */     STATIC, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 267 */     DEFAULT_TYPING;
/*     */     
/*     */     private Typing() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\annotation\JsonSerialize.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */