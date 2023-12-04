/*    */ package com.facebook.presto.jdbc.internal.airlift.units;
/*    */ 
/*    */ import javax.validation.ConstraintValidator;
/*    */ import javax.validation.ConstraintValidatorContext;
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
/*    */ public class MaxDurationValidator
/*    */   implements ConstraintValidator<MaxDuration, Duration>
/*    */ {
/*    */   private Duration max;
/*    */   
/*    */   public void initialize(MaxDuration duration)
/*    */   {
/* 29 */     this.max = Duration.valueOf(duration.value());
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isValid(Duration duration, ConstraintValidatorContext context)
/*    */   {
/* 35 */     return (duration == null) || (duration.compareTo(this.max) <= 0);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\MaxDurationValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */