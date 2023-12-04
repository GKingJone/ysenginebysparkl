/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.exc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
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
/*     */ public class InvalidFormatException
/*     */   extends JsonMappingException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Object _value;
/*     */   protected final Class<?> _targetType;
/*     */   
/*     */   @Deprecated
/*     */   public InvalidFormatException(String msg, Object value, Class<?> targetType)
/*     */   {
/*  43 */     super(null, msg);
/*  44 */     this._value = value;
/*  45 */     this._targetType = targetType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public InvalidFormatException(String msg, JsonLocation loc, Object value, Class<?> targetType)
/*     */   {
/*  55 */     super(null, msg, loc);
/*  56 */     this._value = value;
/*  57 */     this._targetType = targetType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InvalidFormatException(JsonParser p, String msg, Object value, Class<?> targetType)
/*     */   {
/*  66 */     super(p, msg);
/*  67 */     this._value = value;
/*  68 */     this._targetType = targetType;
/*     */   }
/*     */   
/*     */ 
/*     */   public static InvalidFormatException from(JsonParser p, String msg, Object value, Class<?> targetType)
/*     */   {
/*  74 */     return new InvalidFormatException(p, msg, value, targetType);
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
/*     */ 
/*     */   public Object getValue()
/*     */   {
/*  90 */     return this._value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?> getTargetType()
/*     */   {
/* 100 */     return this._targetType;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\exc\InvalidFormatException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */