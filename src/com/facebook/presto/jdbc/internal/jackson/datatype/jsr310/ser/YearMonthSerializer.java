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
/*    */ import java.time.YearMonth;
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
/*    */ public class YearMonthSerializer
/*    */   extends JSR310FormattedSerializerBase<YearMonth>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 42 */   public static final YearMonthSerializer INSTANCE = new YearMonthSerializer();
/*    */   
/*    */   private YearMonthSerializer() {
/* 45 */     this(null);
/*    */   }
/*    */   
/*    */   public YearMonthSerializer(DateTimeFormatter formatter) {
/* 49 */     super(YearMonth.class, formatter);
/*    */   }
/*    */   
/*    */   private YearMonthSerializer(YearMonthSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
/* 53 */     super(base, useTimestamp, formatter);
/*    */   }
/*    */   
/*    */   protected YearMonthSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter)
/*    */   {
/* 58 */     return new YearMonthSerializer(this, useTimestamp, formatter);
/*    */   }
/*    */   
/*    */   public void serialize(YearMonth yearMonth, JsonGenerator generator, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 64 */     if (useTimestamp(provider)) {
/* 65 */       generator.writeStartArray();
/* 66 */       generator.writeNumber(yearMonth.getYear());
/* 67 */       generator.writeNumber(yearMonth.getMonthValue());
/* 68 */       generator.writeEndArray();
/*    */     } else {
/* 70 */       String str = this._formatter == null ? yearMonth.toString() : yearMonth.format(this._formatter);
/* 71 */       generator.writeString(str);
/*    */     }
/*    */   }
/*    */   
/*    */   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 78 */     SerializerProvider provider = visitor.getProvider();
/* 79 */     boolean useTimestamp = (provider != null) && (useTimestamp(provider));
/* 80 */     if (useTimestamp) {
/* 81 */       _acceptTimestampVisitor(visitor, typeHint);
/*    */     } else {
/* 83 */       JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
/* 84 */       if (v2 != null) {
/* 85 */         v2.format(JsonValueFormat.DATE_TIME);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\YearMonthSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */