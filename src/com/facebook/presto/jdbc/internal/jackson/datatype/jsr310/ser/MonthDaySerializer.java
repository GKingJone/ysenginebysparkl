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
/*    */ import java.time.MonthDay;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MonthDaySerializer
/*    */   extends JSR310FormattedSerializerBase<MonthDay>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 45 */   public static final MonthDaySerializer INSTANCE = new MonthDaySerializer();
/*    */   
/*    */   private MonthDaySerializer() {
/* 48 */     this(null);
/*    */   }
/*    */   
/*    */   public MonthDaySerializer(DateTimeFormatter formatter) {
/* 52 */     super(MonthDay.class, formatter);
/*    */   }
/*    */   
/*    */   private MonthDaySerializer(MonthDaySerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
/* 56 */     super(base, useTimestamp, formatter);
/*    */   }
/*    */   
/*    */   protected MonthDaySerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter)
/*    */   {
/* 61 */     return new MonthDaySerializer(this, useTimestamp, formatter);
/*    */   }
/*    */   
/*    */   public void serialize(MonthDay value, JsonGenerator generator, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 67 */     if (_useTimestampExplicitOnly(provider)) {
/* 68 */       generator.writeStartArray();
/* 69 */       generator.writeNumber(value.getMonthValue());
/* 70 */       generator.writeNumber(value.getDayOfMonth());
/* 71 */       generator.writeEndArray();
/*    */     } else {
/* 73 */       String str = this._formatter == null ? value.toString() : value.format(this._formatter);
/* 74 */       generator.writeString(str);
/*    */     }
/*    */   }
/*    */   
/*    */   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 81 */     SerializerProvider provider = visitor.getProvider();
/* 82 */     boolean useTimestamp = (provider != null) && (_useTimestampExplicitOnly(provider));
/* 83 */     if (useTimestamp) {
/* 84 */       _acceptTimestampVisitor(visitor, typeHint);
/*    */     } else {
/* 86 */       JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
/* 87 */       if (v2 != null) {
/* 88 */         v2.format(JsonValueFormat.DATE_TIME);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\MonthDaySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */