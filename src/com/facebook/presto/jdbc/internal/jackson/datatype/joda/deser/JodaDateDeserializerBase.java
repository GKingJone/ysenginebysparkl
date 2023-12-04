/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class JodaDateDeserializerBase<T>
/*    */   extends JodaDeserializerBase<T>
/*    */   implements ContextualDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JacksonJodaDateFormat _format;
/*    */   
/*    */   protected JodaDateDeserializerBase(Class<?> type, JacksonJodaDateFormat format)
/*    */   {
/* 25 */     super(type);
/* 26 */     this._format = format;
/*    */   }
/*    */   
/*    */ 
/*    */   public abstract JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat paramJacksonJodaDateFormat);
/*    */   
/*    */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty prop)
/*    */     throws JsonMappingException
/*    */   {
/* 35 */     JsonFormat.Value ann = findFormatOverrides(ctxt, prop, handledType());
/* 36 */     if (ann != null) {
/* 37 */       JacksonJodaDateFormat format = this._format;
/*    */       
/*    */       Boolean useTimestamp;
/*    */       Boolean useTimestamp;
/* 41 */       if (ann.getShape().isNumeric()) {
/* 42 */         useTimestamp = Boolean.TRUE; } else { Boolean useTimestamp;
/* 43 */         if (ann.getShape() == JsonFormat.Shape.STRING) {
/* 44 */           useTimestamp = Boolean.FALSE; } else { Boolean useTimestamp;
/* 45 */           if (ann.getShape() == JsonFormat.Shape.ARRAY)
/*    */           {
/* 47 */             useTimestamp = Boolean.TRUE;
/*    */           } else
/* 49 */             useTimestamp = null;
/*    */         }
/*    */       }
/* 52 */       if (useTimestamp != null) {
/* 53 */         format = format.withUseTimestamp(useTimestamp);
/*    */       }
/*    */       
/* 56 */       format = format.with(ann);
/* 57 */       if (format != this._format) {
/* 58 */         return withFormat(format);
/*    */       }
/*    */     }
/* 61 */     return this;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\JodaDateDeserializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */