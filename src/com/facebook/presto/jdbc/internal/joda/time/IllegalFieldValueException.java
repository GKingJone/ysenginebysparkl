/*     */ package com.facebook.presto.jdbc.internal.joda.time;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IllegalFieldValueException
/*     */   extends IllegalArgumentException
/*     */ {
/*     */   private static final long serialVersionUID = 6305711765985447737L;
/*     */   
/*     */ 
/*     */ 
/*     */   private final DateTimeFieldType iDateTimeFieldType;
/*     */   
/*     */ 
/*     */ 
/*     */   private final DurationFieldType iDurationFieldType;
/*     */   
/*     */ 
/*     */   private final String iFieldName;
/*     */   
/*     */ 
/*     */   private final Number iNumberValue;
/*     */   
/*     */ 
/*     */   private final String iStringValue;
/*     */   
/*     */ 
/*     */   private final Number iLowerBound;
/*     */   
/*     */ 
/*     */   private final Number iUpperBound;
/*     */   
/*     */ 
/*     */   private String iMessage;
/*     */   
/*     */ 
/*     */ 
/*     */   private static String createMessage(String paramString1, Number paramNumber1, Number paramNumber2, Number paramNumber3, String paramString2)
/*     */   {
/*  41 */     StringBuilder localStringBuilder = new StringBuilder().append("Value ").append(paramNumber1).append(" for ").append(paramString1).append(' ');
/*     */     
/*     */ 
/*  44 */     if (paramNumber2 == null) {
/*  45 */       if (paramNumber3 == null) {
/*  46 */         localStringBuilder.append("is not supported");
/*     */       } else {
/*  48 */         localStringBuilder.append("must not be larger than ").append(paramNumber3);
/*     */       }
/*  50 */     } else if (paramNumber3 == null) {
/*  51 */       localStringBuilder.append("must not be smaller than ").append(paramNumber2);
/*     */     } else {
/*  53 */       localStringBuilder.append("must be in the range [").append(paramNumber2).append(',').append(paramNumber3).append(']');
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  59 */     if (paramString2 != null) {
/*  60 */       localStringBuilder.append(": ").append(paramString2);
/*     */     }
/*     */     
/*  63 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String createMessage(String paramString1, String paramString2)
/*     */   {
/*  74 */     StringBuffer localStringBuffer = new StringBuffer().append("Value ");
/*     */     
/*  76 */     if (paramString2 == null) {
/*  77 */       localStringBuffer.append("null");
/*     */     } else {
/*  79 */       localStringBuffer.append('"');
/*  80 */       localStringBuffer.append(paramString2);
/*  81 */       localStringBuffer.append('"');
/*     */     }
/*     */     
/*  84 */     localStringBuffer.append(" for ").append(paramString1).append(' ').append("is not supported");
/*     */     
/*  86 */     return localStringBuffer.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IllegalFieldValueException(DateTimeFieldType paramDateTimeFieldType, Number paramNumber1, Number paramNumber2, Number paramNumber3)
/*     */   {
/* 108 */     super(createMessage(paramDateTimeFieldType.getName(), paramNumber1, paramNumber2, paramNumber3, null));
/* 109 */     this.iDateTimeFieldType = paramDateTimeFieldType;
/* 110 */     this.iDurationFieldType = null;
/* 111 */     this.iFieldName = paramDateTimeFieldType.getName();
/* 112 */     this.iNumberValue = paramNumber1;
/* 113 */     this.iStringValue = null;
/* 114 */     this.iLowerBound = paramNumber2;
/* 115 */     this.iUpperBound = paramNumber3;
/* 116 */     this.iMessage = super.getMessage();
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
/*     */   public IllegalFieldValueException(DateTimeFieldType paramDateTimeFieldType, Number paramNumber, String paramString)
/*     */   {
/* 129 */     super(createMessage(paramDateTimeFieldType.getName(), paramNumber, null, null, paramString));
/* 130 */     this.iDateTimeFieldType = paramDateTimeFieldType;
/* 131 */     this.iDurationFieldType = null;
/* 132 */     this.iFieldName = paramDateTimeFieldType.getName();
/* 133 */     this.iNumberValue = paramNumber;
/* 134 */     this.iStringValue = null;
/* 135 */     this.iLowerBound = null;
/* 136 */     this.iUpperBound = null;
/* 137 */     this.iMessage = super.getMessage();
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
/*     */   public IllegalFieldValueException(DurationFieldType paramDurationFieldType, Number paramNumber1, Number paramNumber2, Number paramNumber3)
/*     */   {
/* 150 */     super(createMessage(paramDurationFieldType.getName(), paramNumber1, paramNumber2, paramNumber3, null));
/* 151 */     this.iDateTimeFieldType = null;
/* 152 */     this.iDurationFieldType = paramDurationFieldType;
/* 153 */     this.iFieldName = paramDurationFieldType.getName();
/* 154 */     this.iNumberValue = paramNumber1;
/* 155 */     this.iStringValue = null;
/* 156 */     this.iLowerBound = paramNumber2;
/* 157 */     this.iUpperBound = paramNumber3;
/* 158 */     this.iMessage = super.getMessage();
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
/*     */   public IllegalFieldValueException(String paramString, Number paramNumber1, Number paramNumber2, Number paramNumber3)
/*     */   {
/* 171 */     super(createMessage(paramString, paramNumber1, paramNumber2, paramNumber3, null));
/* 172 */     this.iDateTimeFieldType = null;
/* 173 */     this.iDurationFieldType = null;
/* 174 */     this.iFieldName = paramString;
/* 175 */     this.iNumberValue = paramNumber1;
/* 176 */     this.iStringValue = null;
/* 177 */     this.iLowerBound = paramNumber2;
/* 178 */     this.iUpperBound = paramNumber3;
/* 179 */     this.iMessage = super.getMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IllegalFieldValueException(DateTimeFieldType paramDateTimeFieldType, String paramString)
/*     */   {
/* 189 */     super(createMessage(paramDateTimeFieldType.getName(), paramString));
/* 190 */     this.iDateTimeFieldType = paramDateTimeFieldType;
/* 191 */     this.iDurationFieldType = null;
/* 192 */     this.iFieldName = paramDateTimeFieldType.getName();
/* 193 */     this.iStringValue = paramString;
/* 194 */     this.iNumberValue = null;
/* 195 */     this.iLowerBound = null;
/* 196 */     this.iUpperBound = null;
/* 197 */     this.iMessage = super.getMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IllegalFieldValueException(DurationFieldType paramDurationFieldType, String paramString)
/*     */   {
/* 207 */     super(createMessage(paramDurationFieldType.getName(), paramString));
/* 208 */     this.iDateTimeFieldType = null;
/* 209 */     this.iDurationFieldType = paramDurationFieldType;
/* 210 */     this.iFieldName = paramDurationFieldType.getName();
/* 211 */     this.iStringValue = paramString;
/* 212 */     this.iNumberValue = null;
/* 213 */     this.iLowerBound = null;
/* 214 */     this.iUpperBound = null;
/* 215 */     this.iMessage = super.getMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IllegalFieldValueException(String paramString1, String paramString2)
/*     */   {
/* 225 */     super(createMessage(paramString1, paramString2));
/* 226 */     this.iDateTimeFieldType = null;
/* 227 */     this.iDurationFieldType = null;
/* 228 */     this.iFieldName = paramString1;
/* 229 */     this.iStringValue = paramString2;
/* 230 */     this.iNumberValue = null;
/* 231 */     this.iLowerBound = null;
/* 232 */     this.iUpperBound = null;
/* 233 */     this.iMessage = super.getMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFieldType getDateTimeFieldType()
/*     */   {
/* 243 */     return this.iDateTimeFieldType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getDurationFieldType()
/*     */   {
/* 252 */     return this.iDurationFieldType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getFieldName()
/*     */   {
/* 261 */     return this.iFieldName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Number getIllegalNumberValue()
/*     */   {
/* 270 */     return this.iNumberValue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getIllegalStringValue()
/*     */   {
/* 279 */     return this.iStringValue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getIllegalValueAsString()
/*     */   {
/* 288 */     String str = this.iStringValue;
/* 289 */     if (str == null) {
/* 290 */       str = String.valueOf(this.iNumberValue);
/*     */     }
/* 292 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Number getLowerBound()
/*     */   {
/* 301 */     return this.iLowerBound;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Number getUpperBound()
/*     */   {
/* 310 */     return this.iUpperBound;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/* 314 */     return this.iMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void prependMessage(String paramString)
/*     */   {
/* 323 */     if (this.iMessage == null) {
/* 324 */       this.iMessage = paramString;
/* 325 */     } else if (paramString != null) {
/* 326 */       this.iMessage = (paramString + ": " + this.iMessage);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\IllegalFieldValueException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */