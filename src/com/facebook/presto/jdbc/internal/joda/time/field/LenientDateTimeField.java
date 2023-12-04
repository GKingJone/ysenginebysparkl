/*    */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
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
/*    */ public class LenientDateTimeField
/*    */   extends DelegatedDateTimeField
/*    */ {
/*    */   private static final long serialVersionUID = 8714085824173290599L;
/*    */   private final Chronology iBase;
/*    */   
/*    */   public static DateTimeField getInstance(DateTimeField paramDateTimeField, Chronology paramChronology)
/*    */   {
/* 44 */     if (paramDateTimeField == null) {
/* 45 */       return null;
/*    */     }
/* 47 */     if ((paramDateTimeField instanceof StrictDateTimeField)) {
/* 48 */       paramDateTimeField = ((StrictDateTimeField)paramDateTimeField).getWrappedField();
/*    */     }
/* 50 */     if (paramDateTimeField.isLenient()) {
/* 51 */       return paramDateTimeField;
/*    */     }
/* 53 */     return new LenientDateTimeField(paramDateTimeField, paramChronology);
/*    */   }
/*    */   
/*    */   protected LenientDateTimeField(DateTimeField paramDateTimeField, Chronology paramChronology) {
/* 57 */     super(paramDateTimeField);
/* 58 */     this.iBase = paramChronology;
/*    */   }
/*    */   
/*    */   public final boolean isLenient() {
/* 62 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public long set(long paramLong, int paramInt)
/*    */   {
/* 72 */     long l1 = this.iBase.getZone().convertUTCToLocal(paramLong);
/* 73 */     long l2 = FieldUtils.safeSubtract(paramInt, get(paramLong));
/* 74 */     l1 = getType().getField(this.iBase.withUTC()).add(l1, l2);
/* 75 */     return this.iBase.getZone().convertLocalToUTC(l1, false, paramLong);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\LenientDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */