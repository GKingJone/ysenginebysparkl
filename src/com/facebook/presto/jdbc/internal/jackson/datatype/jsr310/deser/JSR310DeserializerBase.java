/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdScalarDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.time.DateTimeException;
/*     */ import java.time.format.DateTimeParseException;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class JSR310DeserializerBase<T>
/*     */   extends StdScalarDeserializer<T>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   protected JSR310DeserializerBase(Class<T> supportedType)
/*     */   {
/*  43 */     super(supportedType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/*  51 */     return typeDeserializer.deserializeTypedFromAny(parser, context);
/*     */   }
/*     */   
/*     */   protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context, JsonToken exp, String unit)
/*     */     throws IOException
/*     */   {
/*  57 */     throw context.wrongTokenException(parser, exp, 
/*  58 */       String.format("Expected %s for '%s' of %s value", new Object[] {exp
/*  59 */       .name(), unit, handledType().getName() }));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context, JsonToken... expTypes)
/*     */     throws IOException
/*     */   {
/*  67 */     String msg = String.format("Unexpected token (%s), expected one of %s for %s value", new Object[] {parser
/*  68 */       .getCurrentToken(), 
/*  69 */       Arrays.asList(expTypes).toString(), 
/*  70 */       handledType().getName() });
/*  71 */     throw JsonMappingException.from(parser, msg);
/*     */   }
/*     */   
/*     */   protected <BOGUS> BOGUS _rethrowDateTimeException(JsonParser p, DeserializationContext context, DateTimeException e0, String value)
/*     */     throws JsonMappingException
/*     */   {
/*     */     JsonMappingException e;
/*  78 */     if ((e0 instanceof DateTimeParseException)) {
/*  79 */       JsonMappingException e = context.weirdStringException(value, handledType(), e0.getMessage());
/*  80 */       e.initCause(e0);
/*     */     } else {
/*  82 */       e = JsonMappingException.from(p, 
/*  83 */         String.format("Failed to deserialize %s: (%s) %s", new Object[] {
/*  84 */         handledType().getName(), e0.getClass().getName(), e0.getMessage() }), e0);
/*     */     }
/*  86 */     throw e;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DateTimeException _peelDTE(DateTimeException e)
/*     */   {
/*     */     for (;;)
/*     */     {
/*  98 */       Throwable t = e.getCause();
/*  99 */       if ((t == null) || (!(t instanceof DateTimeException))) break;
/* 100 */       e = (DateTimeException)t;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 105 */     return e;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\JSR310DeserializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */