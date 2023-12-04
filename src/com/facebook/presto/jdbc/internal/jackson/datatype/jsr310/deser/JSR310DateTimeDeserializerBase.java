/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import java.util.Locale;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ public abstract class JSR310DateTimeDeserializerBase<T>
/*    */   extends JSR310DeserializerBase<T>
/*    */   implements ContextualDeserializer
/*    */ {
/*    */   protected final DateTimeFormatter _formatter;
/*    */   
/*    */   protected JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f)
/*    */   {
/* 22 */     super(supportedType);
/* 23 */     this._formatter = f;
/*    */   }
/*    */   
/*    */ 
/*    */   protected abstract JsonDeserializer<T> withDateFormat(DateTimeFormatter paramDateTimeFormatter);
/*    */   
/*    */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*    */     throws JsonMappingException
/*    */   {
/* 32 */     JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
/* 33 */     if ((format != null) && 
/* 34 */       (format.hasPattern())) {
/* 35 */       String pattern = format.getPattern();
/* 36 */       Locale locale = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
/*    */       DateTimeFormatter df;
/* 38 */       DateTimeFormatter df; if (locale == null) {
/* 39 */         df = DateTimeFormatter.ofPattern(pattern);
/*    */       } else {
/* 41 */         df = DateTimeFormatter.ofPattern(pattern, locale);
/*    */       }
/*    */       
/*    */ 
/* 45 */       if (format.hasTimeZone()) {
/* 46 */         df = df.withZone(format.getTimeZone().toZoneId());
/*    */       }
/* 48 */       return withDateFormat(df);
/*    */     }
/*    */     
/*    */ 
/* 52 */     return this;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\JSR310DateTimeDeserializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */