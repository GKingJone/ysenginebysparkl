/*     */ package com.facebook.presto.jdbc.internal.jackson.annotation;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ @Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PARAMETER})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JsonInclude
/*     */ {
/*     */   Include value() default Include.ALWAYS;
/*     */   
/*     */   Include content() default Include.ALWAYS;
/*     */   
/*     */   public static enum Include
/*     */   {
/*  79 */     ALWAYS, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  85 */     NON_NULL, 
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
/* 100 */     NON_ABSENT, 
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
/* 147 */     NON_EMPTY, 
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
/* 170 */     NON_DEFAULT, 
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
/* 181 */     USE_DEFAULTS;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private Include() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class Value
/*     */     implements JacksonAnnotationValue<JsonInclude>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 204 */     protected static final Value EMPTY = new Value(Include.USE_DEFAULTS, Include.USE_DEFAULTS);
/*     */     protected final Include _valueInclusion;
/*     */     protected final Include _contentInclusion;
/*     */     
/*     */     public Value(JsonInclude src)
/*     */     {
/* 210 */       this(src.value(), src.content());
/*     */     }
/*     */     
/*     */     protected Value(Include vi, Include ci) {
/* 214 */       this._valueInclusion = (vi == null ? Include.USE_DEFAULTS : vi);
/* 215 */       this._contentInclusion = (ci == null ? Include.USE_DEFAULTS : ci);
/*     */     }
/*     */     
/*     */     public static Value empty() {
/* 219 */       return EMPTY;
/*     */     }
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
/*     */     public static Value merge(Value base, Value overrides)
/*     */     {
/* 235 */       return base == null ? overrides : base.withOverrides(overrides);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public static Value mergeAll(Value... values)
/*     */     {
/* 244 */       Value result = null;
/* 245 */       for (Value curr : values) {
/* 246 */         if (curr != null) {
/* 247 */           result = result == null ? curr : result.withOverrides(curr);
/*     */         }
/*     */       }
/* 250 */       return result;
/*     */     }
/*     */     
/*     */     protected Object readResolve()
/*     */     {
/* 255 */       if ((this._valueInclusion == Include.USE_DEFAULTS) && (this._contentInclusion == Include.USE_DEFAULTS))
/*     */       {
/* 257 */         return EMPTY;
/*     */       }
/* 259 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Value withOverrides(Value overrides)
/*     */     {
/* 269 */       if ((overrides == null) || (overrides == EMPTY)) {
/* 270 */         return this;
/*     */       }
/* 272 */       Include vi = overrides._valueInclusion;
/* 273 */       Include ci = overrides._contentInclusion;
/*     */       
/* 275 */       boolean viDiff = (vi != this._valueInclusion) && (vi != Include.USE_DEFAULTS);
/* 276 */       boolean ciDiff = (ci != this._contentInclusion) && (ci != Include.USE_DEFAULTS);
/*     */       
/* 278 */       if (viDiff) {
/* 279 */         if (ciDiff) {
/* 280 */           return new Value(vi, ci);
/*     */         }
/* 282 */         return new Value(vi, this._contentInclusion); }
/* 283 */       if (ciDiff) {
/* 284 */         return new Value(this._valueInclusion, ci);
/*     */       }
/* 286 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public static Value construct(Include valueIncl, Include contentIncl)
/*     */     {
/* 293 */       if (((valueIncl == Include.USE_DEFAULTS) || (valueIncl == null)) && ((contentIncl == Include.USE_DEFAULTS) || (contentIncl == null)))
/*     */       {
/* 295 */         return EMPTY;
/*     */       }
/* 297 */       return new Value(valueIncl, contentIncl);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public static Value from(JsonInclude src)
/*     */     {
/* 305 */       if (src == null) {
/* 306 */         return null;
/*     */       }
/* 308 */       Include vi = src.value();
/* 309 */       Include ci = src.content();
/*     */       
/* 311 */       if ((vi == Include.USE_DEFAULTS) && (ci == Include.USE_DEFAULTS)) {
/* 312 */         return EMPTY;
/*     */       }
/* 314 */       return new Value(vi, ci);
/*     */     }
/*     */     
/*     */     public Value withValueInclusion(Include incl) {
/* 318 */       return incl == this._valueInclusion ? this : new Value(incl, this._contentInclusion);
/*     */     }
/*     */     
/*     */     public Value withContentInclusion(Include incl) {
/* 322 */       return incl == this._contentInclusion ? this : new Value(this._valueInclusion, incl);
/*     */     }
/*     */     
/*     */     public Class<JsonInclude> valueFor()
/*     */     {
/* 327 */       return JsonInclude.class;
/*     */     }
/*     */     
/*     */     public Include getValueInclusion() {
/* 331 */       return this._valueInclusion;
/*     */     }
/*     */     
/*     */     public Include getContentInclusion() {
/* 335 */       return this._contentInclusion;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 340 */       return String.format("[value=%s,content=%s]", new Object[] { this._valueInclusion, this._contentInclusion });
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 345 */       return (this._valueInclusion.hashCode() << 2) + this._contentInclusion.hashCode();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/* 351 */       if (o == this) return true;
/* 352 */       if (o == null) return false;
/* 353 */       if (o.getClass() != getClass()) return false;
/* 354 */       Value other = (Value)o;
/*     */       
/* 356 */       return (other._valueInclusion == this._valueInclusion) && (other._contentInclusion == this._contentInclusion);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\annotation\JsonInclude.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */