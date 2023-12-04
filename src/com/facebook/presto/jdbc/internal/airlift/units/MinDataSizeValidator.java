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
/*    */ public class MinDataSizeValidator
/*    */   implements ConstraintValidator<MinDataSize, DataSize>
/*    */ {
/*    */   private DataSize min;
/*    */   
/*    */   public void initialize(MinDataSize dataSize)
/*    */   {
/* 27 */     this.min = DataSize.valueOf(dataSize.value());
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isValid(DataSize dataSize, ConstraintValidatorContext context)
/*    */   {
/* 33 */     return (dataSize == null) || (dataSize.compareTo(this.min) >= 0);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\MinDataSizeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */