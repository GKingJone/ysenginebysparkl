/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonValueFormat;
/*    */ import java.io.IOException;
/*    */ import java.time.LocalDate;
/*    */ import java.time.format.DateTimeFormatter;
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
/*    */ public class LocalDateSerializer
/*    */   extends JSR310FormattedSerializerBase<LocalDate>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 40 */   public static final LocalDateSerializer INSTANCE = new LocalDateSerializer();
/*    */   
/*    */   protected LocalDateSerializer() {
/* 43 */     super(LocalDate.class);
/*    */   }
/*    */   
/*    */   protected LocalDateSerializer(LocalDateSerializer base, Boolean useTimestamp, DateTimeFormatter dtf)
/*    */   {
/* 48 */     super(base, useTimestamp, dtf);
/*    */   }
/*    */   
/*    */   public LocalDateSerializer(DateTimeFormatter formatter) {
/* 52 */     super(LocalDate.class, formatter);
/*    */   }
/*    */   
/*    */   protected LocalDateSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf)
/*    */   {
/* 57 */     return new LocalDateSerializer(this, useTimestamp, dtf);
/*    */   }
/*    */   
/*    */   public void serialize(LocalDate date, JsonGenerator generator, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 63 */     if (useTimestamp(provider)) {
/* 64 */       generator.writeStartArray();
/* 65 */       generator.writeNumber(date.getYear());
/* 66 */       generator.writeNumber(date.getMonthValue());
/* 67 */       generator.writeNumber(date.getDayOfMonth());
/* 68 */       generator.writeEndArray();
/*    */     } else {
/* 70 */       String str = this._formatter == null ? date.toString() : date.format(this._formatter);
/* 71 */       generator.writeString(str);
/*    */     }
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 78 */     SerializerProvider provider = visitor.getProvider();
/* 79 */     boolean useTimestamp = (provider != null) && (useTimestamp(provider));
/* 80 */     if (useTimestamp) {
/* 81 */       _acceptTimestampVisitor(visitor, typeHint);
/*    */     } else {
/* 83 */       JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
/* 84 */       if (v2 != null) {
/* 85 */         v2.format(JsonValueFormat.DATE);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\LocalDateSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */