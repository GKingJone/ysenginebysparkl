/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.PeriodFormatter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ 
/*     */ public class PeriodSerializer
/*     */   extends JodaSerializerBase<ReadablePeriod>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JacksonJodaPeriodFormat _format;
/*     */   
/*     */   public PeriodSerializer()
/*     */   {
/*  31 */     this(FormatConfig.DEFAULT_PERIOD_FORMAT);
/*     */   }
/*     */   
/*     */   protected PeriodSerializer(JacksonJodaPeriodFormat format) {
/*  35 */     super(ReadablePeriod.class);
/*  36 */     this._format = format;
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  52 */     if (property != null) {
/*  53 */       JsonFormat.Value ann = findFormatOverrides(prov, property, handledType());
/*  54 */       if (ann != null) {
/*  55 */         JacksonJodaPeriodFormat format = this._format;
/*     */         
/*     */         Boolean useTimestamp;
/*     */         
/*     */         Boolean useTimestamp;
/*  60 */         if (ann.getShape().isNumeric()) {
/*  61 */           useTimestamp = Boolean.TRUE; } else { Boolean useTimestamp;
/*  62 */           if (ann.getShape() == JsonFormat.Shape.STRING) {
/*  63 */             useTimestamp = Boolean.FALSE; } else { Boolean useTimestamp;
/*  64 */             if (ann.getShape() == JsonFormat.Shape.ARRAY)
/*     */             {
/*  66 */               useTimestamp = Boolean.TRUE;
/*     */             } else
/*  68 */               useTimestamp = null;
/*     */           }
/*     */         }
/*  71 */         if (useTimestamp != null) {
/*  72 */           format = format.withUseTimestamp(useTimestamp);
/*     */         }
/*     */         
/*  75 */         format = format.withFormat(ann.getPattern().trim());
/*  76 */         format = format.withLocale(ann.getLocale());
/*  77 */         if (format != this._format) {
/*  78 */           return new PeriodSerializer(format);
/*     */         }
/*     */       }
/*     */     }
/*  82 */     return this;
/*     */   }
/*     */   
/*     */   public void serialize(ReadablePeriod value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*  88 */     gen.writeString(this._format.createFormatter(provider).print(value));
/*     */   }
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*     */   {
/*  93 */     return createSchemaNode("string", true);
/*     */   }
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/*  99 */     JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
/*     */     
/*     */ 
/* 102 */     if (v2 != null) {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\PeriodSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */