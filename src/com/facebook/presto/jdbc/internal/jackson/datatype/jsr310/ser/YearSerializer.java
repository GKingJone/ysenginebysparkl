/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
/*    */ import java.io.IOException;
/*    */ import java.time.Year;
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
/*    */ public class YearSerializer
/*    */   extends JSR310FormattedSerializerBase<Year>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 41 */   public static final YearSerializer INSTANCE = new YearSerializer();
/*    */   
/*    */   protected YearSerializer() {
/* 44 */     this(null);
/*    */   }
/*    */   
/*    */   public YearSerializer(DateTimeFormatter formatter) {
/* 48 */     super(Year.class, formatter);
/*    */   }
/*    */   
/*    */   protected YearSerializer(YearSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
/* 52 */     super(base, useTimestamp, formatter);
/*    */   }
/*    */   
/*    */   protected YearSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter)
/*    */   {
/* 57 */     return new YearSerializer(this, useTimestamp, formatter);
/*    */   }
/*    */   
/*    */   public void serialize(Year year, JsonGenerator generator, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 63 */     if (useTimestamp(provider)) {
/* 64 */       generator.writeNumber(year.getValue());
/*    */     } else {
/* 66 */       String str = this._formatter == null ? year.toString() : year.format(this._formatter);
/* 67 */       generator.writeString(str);
/*    */     }
/*    */   }
/*    */   
/*    */   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 74 */     JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
/* 75 */     if (v2 != null) {
/* 76 */       v2.numberType(JsonParser.NumberType.LONG);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\YearSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */