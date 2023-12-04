/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.time.DateTimeException;
/*     */ import java.time.Period;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZoneOffset;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSR310StringParsableDeserializer
/*     */   extends JSR310DeserializerBase<Object>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected static final int TYPE_PERIOD = 1;
/*     */   protected static final int TYPE_ZONE_ID = 2;
/*     */   protected static final int TYPE_ZONE_OFFSET = 3;
/*  52 */   public static final JsonDeserializer<Period> PERIOD = createDeserializer(Period.class, 1);
/*     */   
/*     */ 
/*  55 */   public static final JsonDeserializer<ZoneId> ZONE_ID = createDeserializer(ZoneId.class, 2);
/*     */   
/*     */ 
/*  58 */   public static final JsonDeserializer<ZoneOffset> ZONE_OFFSET = createDeserializer(ZoneOffset.class, 3);
/*     */   
/*     */   protected final int _valueType;
/*     */   
/*     */ 
/*     */   protected JSR310StringParsableDeserializer(Class<?> supportedType, int valueId)
/*     */   {
/*  65 */     super(supportedType);
/*  66 */     this._valueType = valueId;
/*     */   }
/*     */   
/*     */   protected static <T> JsonDeserializer<T> createDeserializer(Class<T> type, int typeId)
/*     */   {
/*  71 */     return new JSR310StringParsableDeserializer(type, typeId);
/*     */   }
/*     */   
/*     */   public Object deserialize(JsonParser parser, DeserializationContext context)
/*     */     throws IOException
/*     */   {
/*  77 */     if (parser.hasToken(JsonToken.VALUE_STRING)) {
/*  78 */       String string = parser.getText().trim();
/*  79 */       if (string.length() == 0) {
/*  80 */         return null;
/*     */       }
/*     */       try {
/*  83 */         switch (this._valueType) {
/*     */         case 1: 
/*  85 */           return Period.parse(string);
/*     */         case 2: 
/*  87 */           return ZoneId.of(string);
/*     */         case 3: 
/*  89 */           return ZoneOffset.of(string);
/*     */         }
/*     */       } catch (DateTimeException e) {
/*  92 */         _rethrowDateTimeException(parser, context, e, string);
/*     */       }
/*     */     }
/*  95 */     if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT))
/*     */     {
/*     */ 
/*  98 */       return parser.getEmbeddedObject();
/*     */     }
/* 100 */     throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer deserializer)
/*     */     throws IOException
/*     */   {
/* 111 */     JsonToken t = parser.getCurrentToken();
/* 112 */     if ((t != null) && (t.isScalarValue())) {
/* 113 */       return deserialize(parser, context);
/*     */     }
/* 115 */     return deserializer.deserializeTypedFromAny(parser, context);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\JSR310StringParsableDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */