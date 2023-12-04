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
/*    */ public class MaxDataSizeValidator
/*    */   implements ConstraintValidator<MaxDataSize, DataSize>
/*    */ {
/*    */   private DataSize max;
/*    */   
/*    */   public void initialize(MaxDataSize dataSize)
/*    */   {
/* 27 */     this.max = DataSize.valueOf(dataSize.value());
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isValid(DataSize dataSize, ConstraintValidatorContext context)
/*    */   {
/* 33 */     return (dataSize == null) || (dataSize.compareTo(this.max) <= 0);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\MaxDataSizeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */