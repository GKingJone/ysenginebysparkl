/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Days;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Hours;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Minutes;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Months;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Years;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.PeriodFormatter;
/*    */ 
/*    */ public class PeriodDeserializer extends JodaDeserializerBase<ReadablePeriod>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   private static final PeriodFormatter DEFAULT_FORMAT = ;
/*    */   private final boolean _requireFullPeriod;
/*    */   
/*    */   public PeriodDeserializer()
/*    */   {
/* 24 */     this(true);
/*    */   }
/*    */   
/*    */   public PeriodDeserializer(boolean fullPeriod) {
/* 28 */     super(fullPeriod ? Period.class : ReadablePeriod.class);
/* 29 */     this._requireFullPeriod = fullPeriod;
/*    */   }
/*    */   
/*    */ 
/*    */   public ReadablePeriod deserialize(JsonParser jp, DeserializationContext ctxt)
/*    */     throws java.io.IOException
/*    */   {
/* 36 */     JsonToken t = jp.getCurrentToken();
/* 37 */     if (t == JsonToken.VALUE_STRING) {
/* 38 */       String str = jp.getText().trim();
/* 39 */       if (str.isEmpty()) {
/* 40 */         return null;
/*    */       }
/* 42 */       return DEFAULT_FORMAT.parsePeriod(str);
/*    */     }
/* 44 */     if (t == JsonToken.VALUE_NUMBER_INT) {
/* 45 */       return new Period(jp.getLongValue());
/*    */     }
/* 47 */     if ((t != JsonToken.START_OBJECT) && (t != JsonToken.FIELD_NAME)) {
/* 48 */       throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Number, String or Object");
/*    */     }
/*    */     
/*    */ 
/* 52 */     JsonNode treeNode = (JsonNode)jp.readValueAsTree();
/* 53 */     String periodType = treeNode.path("fieldType").path("name").asText();
/* 54 */     String periodName = treeNode.path("periodType").path("name").asText();
/*    */     
/* 56 */     int periodValue = treeNode.path(periodType).asInt();
/*    */     
/*    */     ReadablePeriod p;
/*    */     
/* 60 */     if (periodName.equals("Seconds")) {
/* 61 */       p = com.facebook.presto.jdbc.internal.joda.time.Seconds.seconds(periodValue);
/*    */     } else { ReadablePeriod p;
/* 63 */       if (periodName.equals("Minutes")) {
/* 64 */         p = Minutes.minutes(periodValue);
/*    */       } else { ReadablePeriod p;
/* 66 */         if (periodName.equals("Hours")) {
/* 67 */           p = Hours.hours(periodValue);
/*    */         } else { ReadablePeriod p;
/* 69 */           if (periodName.equals("Days")) {
/* 70 */             p = Days.days(periodValue);
/*    */           } else { ReadablePeriod p;
/* 72 */             if (periodName.equals("Weeks")) {
/* 73 */               p = com.facebook.presto.jdbc.internal.joda.time.Weeks.weeks(periodValue);
/*    */             } else { ReadablePeriod p;
/* 75 */               if (periodName.equals("Months")) {
/* 76 */                 p = Months.months(periodValue);
/*    */               } else { ReadablePeriod p;
/* 78 */                 if (periodName.equals("Years")) {
/* 79 */                   p = Years.years(periodValue);
/*    */                 } else
/* 81 */                   throw ctxt.mappingException("Don't know how to deserialize " + handledType().getName() + " using periodName '" + periodName + "'");
/*    */               }
/*    */             } } } } }
/*    */     ReadablePeriod p;
/* 85 */     if ((this._requireFullPeriod) && (!(p instanceof Period))) {
/* 86 */       p = p.toPeriod();
/*    */     }
/* 88 */     return p;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\PeriodDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */