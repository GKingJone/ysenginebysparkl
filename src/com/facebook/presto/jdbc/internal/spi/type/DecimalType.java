/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*     */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DecimalType
/*     */   extends AbstractType
/*     */   implements FixedWidthType
/*     */ {
/*     */   public static final int DEFAULT_SCALE = 0;
/*     */   public static final int DEFAULT_PRECISION = 38;
/*     */   private final int precision;
/*     */   private final int scale;
/*     */   
/*     */   public static DecimalType createDecimalType(int precision, int scale)
/*     */   {
/*  36 */     if (precision <= 17) {
/*  37 */       return new ShortDecimalType(precision, scale);
/*     */     }
/*     */     
/*  40 */     return new LongDecimalType(precision, scale);
/*     */   }
/*     */   
/*     */ 
/*     */   public static DecimalType createDecimalType(int precision)
/*     */   {
/*  46 */     return createDecimalType(precision, 0);
/*     */   }
/*     */   
/*     */   public static DecimalType createDecimalType()
/*     */   {
/*  51 */     return createDecimalType(38, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   DecimalType(int precision, int scale, Class<?> javaType)
/*     */   {
/*  59 */     super(new TypeSignature("decimal", buildTypeParameters(precision, scale)), javaType);
/*  60 */     this.precision = precision;
/*  61 */     this.scale = scale;
/*     */   }
/*     */   
/*     */ 
/*     */   public final boolean isComparable()
/*     */   {
/*  67 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public final boolean isOrderable()
/*     */   {
/*  73 */     return true;
/*     */   }
/*     */   
/*     */   public int getPrecision()
/*     */   {
/*  78 */     return this.precision;
/*     */   }
/*     */   
/*     */   public int getScale()
/*     */   {
/*  83 */     return this.scale;
/*     */   }
/*     */   
/*     */   public boolean isShort()
/*     */   {
/*  88 */     return this.precision <= 17;
/*     */   }
/*     */   
/*     */   void validatePrecisionScale(int precision, int scale, int maxPrecision)
/*     */   {
/*  93 */     if ((precision <= 0) || (precision > maxPrecision)) {
/*  94 */       throw new PrestoException(StandardErrorCode.INVALID_FUNCTION_ARGUMENT, "DECIMAL precision must be in range [1, 38]");
/*     */     }
/*     */     
/*  97 */     if ((scale < 0) || (scale > precision)) {
/*  98 */       throw new PrestoException(StandardErrorCode.INVALID_FUNCTION_ARGUMENT, "DECIMAL scale must be in range [0, precision]");
/*     */     }
/*     */   }
/*     */   
/*     */   private static List<TypeSignatureParameter> buildTypeParameters(int precision, int scale)
/*     */   {
/* 104 */     List<TypeSignatureParameter> typeParameters = new ArrayList();
/* 105 */     typeParameters.add(TypeSignatureParameter.of(precision));
/* 106 */     typeParameters.add(TypeSignatureParameter.of(scale));
/* 107 */     return Collections.unmodifiableList(typeParameters);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\DecimalType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */