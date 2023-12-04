/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*     */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Decimals
/*     */ {
/*     */   public static final int SIZE_OF_LONG_DECIMAL = 16;
/*     */   public static final int MAX_PRECISION = 38;
/*     */   public static final int MAX_SHORT_PRECISION = 17;
/*  45 */   public static final BigInteger MAX_DECIMAL_UNSCALED_VALUE = new BigInteger(new String(new char[38])
/*     */   
/*  47 */     .replace("\000", "9"));
/*  48 */   public static final BigInteger MIN_DECIMAL_UNSCALED_VALUE = MAX_DECIMAL_UNSCALED_VALUE.negate();
/*     */   
/*  50 */   private static final Pattern DECIMAL_PATTERN = Pattern.compile("(\\+?|-?)((0*)(\\d*))(\\.(\\d+))?");
/*     */   
/*     */   private static final int LONG_POWERS_OF_TEN_TABLE_LENGTH = 18;
/*     */   private static final int BIG_INTEGER_POWERS_OF_TEN_TABLE_LENGTH = 100;
/*  54 */   private static final long[] LONG_POWERS_OF_TEN = new long[18];
/*  55 */   private static final BigInteger[] BIG_INTEGER_POWERS_OF_TEN = new BigInteger[100];
/*     */   
/*     */   static {
/*  58 */     for (int i = 0; i < LONG_POWERS_OF_TEN.length; i++) {
/*  59 */       LONG_POWERS_OF_TEN[i] = Math.round(Math.pow(10.0D, i));
/*     */     }
/*     */     
/*  62 */     for (int i = 0; i < BIG_INTEGER_POWERS_OF_TEN.length; i++) {
/*  63 */       BIG_INTEGER_POWERS_OF_TEN[i] = BigInteger.TEN.pow(i);
/*     */     }
/*     */   }
/*     */   
/*     */   public static long longTenToNth(int n)
/*     */   {
/*  69 */     return LONG_POWERS_OF_TEN[n];
/*     */   }
/*     */   
/*     */   public static BigInteger bigIntegerTenToNth(int n)
/*     */   {
/*  74 */     return BIG_INTEGER_POWERS_OF_TEN[n];
/*     */   }
/*     */   
/*     */   public static DecimalParseResult parse(String stringValue)
/*     */   {
/*  79 */     return parse(stringValue, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public static DecimalParseResult parseIncludeLeadingZerosInPrecision(String stringValue)
/*     */   {
/*  85 */     return parse(stringValue, true);
/*     */   }
/*     */   
/*     */   private static DecimalParseResult parse(String stringValue, boolean includeLeadingZerosInPrecision)
/*     */   {
/*  90 */     Matcher matcher = DECIMAL_PATTERN.matcher(stringValue);
/*  91 */     if (!matcher.matches()) {
/*  92 */       throw new IllegalArgumentException("invalid decimal value '" + stringValue + "'");
/*     */     }
/*     */     
/*  95 */     String sign = getMatcherGroup(matcher, 1);
/*  96 */     if (sign.isEmpty()) {
/*  97 */       sign = "+";
/*     */     }
/*  99 */     String leadingZeros = getMatcherGroup(matcher, 3);
/* 100 */     String integralPart = getMatcherGroup(matcher, 4);
/* 101 */     String fractionalPart = getMatcherGroup(matcher, 6);
/*     */     
/* 103 */     int scale = fractionalPart.length();
/*     */     int precision;
/* 105 */     int precision; if (includeLeadingZerosInPrecision) {
/* 106 */       precision = leadingZeros.length() + integralPart.length() + scale;
/*     */     }
/*     */     else {
/* 109 */       precision = integralPart.length() + scale;
/* 110 */       if (precision == 0) {
/* 111 */         precision = 1;
/*     */       }
/*     */     }
/*     */     
/* 115 */     String unscaledValue = sign + leadingZeros + integralPart + fractionalPart;
/*     */     Object value;
/* 117 */     Object value; if (precision <= 17) {
/* 118 */       value = Long.valueOf(Long.parseLong(unscaledValue));
/*     */     }
/*     */     else {
/* 121 */       value = encodeUnscaledValue(new BigInteger(unscaledValue));
/*     */     }
/* 123 */     return new DecimalParseResult(value, DecimalType.createDecimalType(precision, scale));
/*     */   }
/*     */   
/*     */   private static String getMatcherGroup(Matcher matcher, int group)
/*     */   {
/* 128 */     String groupValue = matcher.group(group);
/* 129 */     if (groupValue == null) {
/* 130 */       groupValue = "";
/*     */     }
/* 132 */     return groupValue;
/*     */   }
/*     */   
/*     */   public static Slice encodeUnscaledValue(BigInteger unscaledValue)
/*     */   {
/* 137 */     Slice result = Slices.allocate(16);
/* 138 */     byte[] bytes = unscaledValue.toByteArray();
/* 139 */     if (unscaledValue.signum() < 0)
/*     */     {
/*     */ 
/* 142 */       result.fill((byte)-1);
/*     */     }
/* 144 */     result.setBytes(16 - bytes.length, bytes);
/* 145 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Slice encodeUnscaledValue(long unscaledValue)
/*     */   {
/* 153 */     Slice result = Slices.allocate(16);
/* 154 */     if (unscaledValue < 0L) {
/* 155 */       result.setLong(0, -1L);
/*     */     }
/* 157 */     result.setLong(8, Long.reverseBytes(unscaledValue));
/* 158 */     return result;
/*     */   }
/*     */   
/*     */   public static Slice encodeScaledValue(BigDecimal value)
/*     */   {
/* 163 */     return encodeUnscaledValue(value.unscaledValue());
/*     */   }
/*     */   
/*     */   public static BigInteger decodeUnscaledValue(Slice valueSlice)
/*     */   {
/* 168 */     return new BigInteger(valueSlice.getBytes());
/*     */   }
/*     */   
/*     */   public static String toString(long unscaledValue, int scale)
/*     */   {
/* 173 */     return toString(Long.toString(unscaledValue), scale);
/*     */   }
/*     */   
/*     */   public static String toString(Slice unscaledValue, int scale)
/*     */   {
/* 178 */     return toString(decodeUnscaledValue(unscaledValue), scale);
/*     */   }
/*     */   
/*     */   public static String toString(BigInteger unscaledValue, int scale)
/*     */   {
/* 183 */     return toString(unscaledValue.toString(), scale);
/*     */   }
/*     */   
/*     */   private static String toString(String unscaledValueString, int scale)
/*     */   {
/* 188 */     StringBuilder resultBuilder = new StringBuilder();
/*     */     
/* 190 */     if (unscaledValueString.startsWith("-")) {
/* 191 */       resultBuilder.append("-");
/* 192 */       unscaledValueString = unscaledValueString.substring(1);
/*     */     }
/*     */     
/*     */ 
/* 196 */     if (unscaledValueString.length() <= scale) {
/* 197 */       resultBuilder.append("0");
/*     */     }
/*     */     else {
/* 200 */       resultBuilder.append(unscaledValueString.substring(0, unscaledValueString.length() - scale));
/*     */     }
/*     */     
/*     */ 
/* 204 */     if (scale > 0) {
/* 205 */       resultBuilder.append(".");
/* 206 */       if (unscaledValueString.length() < scale)
/*     */       {
/* 208 */         for (int i = 0; i < scale - unscaledValueString.length(); i++) {
/* 209 */           resultBuilder.append("0");
/*     */         }
/* 211 */         resultBuilder.append(unscaledValueString);
/*     */       }
/*     */       else
/*     */       {
/* 215 */         resultBuilder.append(unscaledValueString.substring(unscaledValueString.length() - scale));
/*     */       }
/*     */     }
/* 218 */     return resultBuilder.toString();
/*     */   }
/*     */   
/*     */   public static boolean overflows(long value, int precision)
/*     */   {
/* 223 */     if (precision > 17) {
/* 224 */       throw new IllegalArgumentException("expected precision to be less than 17");
/*     */     }
/* 226 */     return Math.abs(value) >= longTenToNth(precision);
/*     */   }
/*     */   
/*     */   public static boolean overflows(BigInteger value, int precision)
/*     */   {
/* 231 */     return value.abs().compareTo(bigIntegerTenToNth(precision)) >= 0;
/*     */   }
/*     */   
/*     */   public static boolean overflows(BigInteger value)
/*     */   {
/* 236 */     return (value.compareTo(MAX_DECIMAL_UNSCALED_VALUE) > 0) || (value.compareTo(MIN_DECIMAL_UNSCALED_VALUE) < 0);
/*     */   }
/*     */   
/*     */   public static boolean overflows(BigDecimal value, long precision)
/*     */   {
/* 241 */     return value.precision() > precision;
/*     */   }
/*     */   
/*     */   public static void checkOverflow(BigInteger value)
/*     */   {
/* 246 */     if (overflows(value)) {
/* 247 */       throw new PrestoException(StandardErrorCode.NUMERIC_VALUE_OUT_OF_RANGE, String.format("Value is out of range: %s", new Object[] { value.toString() }));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeBigDecimal(DecimalType decimalType, BlockBuilder blockBuilder, BigDecimal value)
/*     */   {
/* 253 */     decimalType.writeSlice(blockBuilder, encodeScaledValue(value));
/*     */   }
/*     */   
/*     */   public static BigDecimal rescale(BigDecimal value, DecimalType type)
/*     */   {
/* 258 */     value = value.setScale(type.getScale(), 7);
/*     */     
/* 260 */     if (value.precision() > type.getPrecision()) {
/* 261 */       throw new IllegalArgumentException("decimal precision larger than column precision");
/*     */     }
/* 263 */     return value;
/*     */   }
/*     */   
/*     */   public static void writeShortDecimal(BlockBuilder blockBuilder, long value)
/*     */   {
/* 268 */     blockBuilder.writeLong(value).closeEntry();
/*     */   }
/*     */   
/*     */   public static long rescale(long value, int fromScale, int toScale)
/*     */   {
/* 273 */     if (toScale < fromScale) {
/* 274 */       throw new IllegalArgumentException("target scale must be larger than source scale");
/*     */     }
/* 276 */     return value * longTenToNth(toScale - fromScale);
/*     */   }
/*     */   
/*     */   public static BigInteger rescale(BigInteger value, int fromScale, int toScale)
/*     */   {
/* 281 */     if (toScale < fromScale) {
/* 282 */       throw new IllegalArgumentException("target scale must be larger than source scale");
/*     */     }
/* 284 */     return value.multiply(bigIntegerTenToNth(toScale - fromScale));
/*     */   }
/*     */   
/*     */   public static boolean isShortDecimal(Type type)
/*     */   {
/* 289 */     return type instanceof ShortDecimalType;
/*     */   }
/*     */   
/*     */   public static boolean isLongDecimal(Type type)
/*     */   {
/* 294 */     return type instanceof LongDecimalType;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\Decimals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */