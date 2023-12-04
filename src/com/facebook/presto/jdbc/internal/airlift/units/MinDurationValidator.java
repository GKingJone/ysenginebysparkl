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
/*    */ public class MinDurationValidator
/*    */   implements ConstraintValidator<MinDuration, Duration>
/*    */ {
/*    */   private Duration min;
/*    */   
/*    */   public void initialize(MinDuration duration)
/*    */   {
/* 29 */     this.min = Duration.valueOf(duration.value());
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isValid(Duration duration, ConstraintValidatorContext context)
/*    */   {
/* 35 */     return (duration == null) || (duration.compareTo(this.min) >= 0);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\MinDurationValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */