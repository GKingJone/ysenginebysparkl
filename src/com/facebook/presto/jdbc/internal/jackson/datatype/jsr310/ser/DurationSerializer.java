/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonValueFormat;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.DecimalUtils;
/*    */ import java.io.IOException;
/*    */ import java.time.Duration;
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
/*    */ public class DurationSerializer
/*    */   extends JSR310FormattedSerializerBase<Duration>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 44 */   public static final DurationSerializer INSTANCE = new DurationSerializer();
/*    */   
/*    */   private DurationSerializer() {
/* 47 */     super(Duration.class);
/*    */   }
/*    */   
/*    */   protected DurationSerializer(DurationSerializer base, Boolean useTimestamp, DateTimeFormatter dtf)
/*    */   {
/* 52 */     super(base, useTimestamp, dtf);
/*    */   }
/*    */   
/*    */   protected DurationSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf)
/*    */   {
/* 57 */     return new DurationSerializer(this, useTimestamp, dtf);
/*    */   }
/*    */   
/*    */   public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 63 */     if (useTimestamp(provider)) {
/* 64 */       if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
/* 65 */         generator.writeNumber(DecimalUtils.toBigDecimal(duration
/* 66 */           .getSeconds(), duration.getNano()));
/*    */       }
/*    */       else {
/* 69 */         generator.writeNumber(duration.toMillis());
/*    */       }
/*    */     }
/*    */     else {
/* 73 */       generator.writeString(duration.toString());
/*    */     }
/*    */   }
/*    */   
/*    */   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 80 */     JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
/* 81 */     if (v2 != null) {
/* 82 */       v2.numberType(JsonParser.NumberType.LONG);
/* 83 */       SerializerProvider provider = visitor.getProvider();
/* 84 */       if ((provider == null) || (!provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)))
/*    */       {
/*    */ 
/* 87 */         v2.format(JsonValueFormat.UTC_MILLISEC);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\DurationSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */