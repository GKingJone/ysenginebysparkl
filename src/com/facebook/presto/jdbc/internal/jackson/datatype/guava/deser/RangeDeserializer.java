/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.BoundType;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Range;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.util.RangeFactory;
/*     */ import java.io.IOException;
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
/*     */ public class RangeDeserializer
/*     */   extends StdDeserializer<Range<?>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _rangeType;
/*     */   protected final JsonDeserializer<Object> _endpointDeserializer;
/*     */   private BoundType _defaultBoundType;
/*     */   
/*     */   @Deprecated
/*     */   public RangeDeserializer(JavaType rangeType)
/*     */   {
/*  50 */     this(null, rangeType);
/*     */   }
/*     */   
/*     */   public RangeDeserializer(BoundType defaultBoundType, JavaType rangeType) {
/*  54 */     this(rangeType, null);
/*  55 */     this._defaultBoundType = defaultBoundType;
/*     */   }
/*     */   
/*     */ 
/*     */   public RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser)
/*     */   {
/*  61 */     super(rangeType);
/*  62 */     this._rangeType = rangeType;
/*  63 */     this._endpointDeserializer = endpointDeser;
/*     */   }
/*     */   
/*     */ 
/*     */   public RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser, BoundType defaultBoundType)
/*     */   {
/*  69 */     super(rangeType);
/*  70 */     this._rangeType = rangeType;
/*  71 */     this._endpointDeserializer = endpointDeser;
/*  72 */     this._defaultBoundType = defaultBoundType;
/*     */   }
/*     */   
/*     */   public JavaType getValueType() {
/*  76 */     return this._rangeType;
/*     */   }
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  82 */     if (this._endpointDeserializer == null) {
/*  83 */       JavaType endpointType = this._rangeType.containedType(0);
/*  84 */       if (endpointType == null) {
/*  85 */         endpointType = TypeFactory.unknownType();
/*     */       }
/*  87 */       JsonDeserializer<Object> deser = ctxt.findContextualValueDeserializer(endpointType, property);
/*  88 */       return new RangeDeserializer(this._rangeType, deser, this._defaultBoundType);
/*     */     }
/*  90 */     return this;
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
/*     */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 104 */     return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Range<?> deserialize(JsonParser parser, DeserializationContext context)
/*     */     throws IOException
/*     */   {
/* 112 */     JsonToken t = parser.getCurrentToken();
/* 113 */     if (t == JsonToken.START_OBJECT) {
/* 114 */       t = parser.nextToken();
/*     */     }
/*     */     
/* 117 */     Comparable<?> lowerEndpoint = null;
/* 118 */     Comparable<?> upperEndpoint = null;
/* 119 */     BoundType lowerBoundType = this._defaultBoundType;
/* 120 */     BoundType upperBoundType = this._defaultBoundType;
/* 122 */     for (; 
/* 122 */         t != JsonToken.END_OBJECT; t = parser.nextToken()) {
/* 123 */       expect(parser, JsonToken.FIELD_NAME, t);
/* 124 */       String fieldName = parser.getCurrentName();
/*     */       try {
/* 126 */         if (fieldName.equals("lowerEndpoint")) {
/* 127 */           parser.nextToken();
/* 128 */           lowerEndpoint = deserializeEndpoint(parser, context);
/* 129 */         } else if (fieldName.equals("upperEndpoint")) {
/* 130 */           parser.nextToken();
/* 131 */           upperEndpoint = deserializeEndpoint(parser, context);
/* 132 */         } else if (fieldName.equals("lowerBoundType")) {
/* 133 */           parser.nextToken();
/* 134 */           lowerBoundType = deserializeBoundType(parser);
/* 135 */         } else if (fieldName.equals("upperBoundType")) {
/* 136 */           parser.nextToken();
/* 137 */           upperBoundType = deserializeBoundType(parser);
/*     */         } else {
/* 139 */           throw context.mappingException("Unexpected Range field: " + fieldName);
/*     */         }
/*     */       } catch (IllegalStateException e) {
/* 142 */         throw JsonMappingException.from(parser, e.getMessage());
/*     */       }
/*     */     }
/*     */     try {
/* 146 */       if ((lowerEndpoint != null) && (upperEndpoint != null)) {
/* 147 */         Preconditions.checkState(lowerEndpoint.getClass() == upperEndpoint.getClass(), "Endpoint types are not the same - 'lowerEndpoint' deserialized to [%s], and 'upperEndpoint' deserialized to [%s].", new Object[] { lowerEndpoint.getClass().getName(), upperEndpoint.getClass().getName() });
/*     */         
/*     */ 
/*     */ 
/* 151 */         Preconditions.checkState(lowerBoundType != null, "'lowerEndpoint' field found, but not 'lowerBoundType'");
/* 152 */         Preconditions.checkState(upperBoundType != null, "'upperEndpoint' field found, but not 'upperBoundType'");
/* 153 */         return RangeFactory.range(lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
/*     */       }
/* 155 */       if (lowerEndpoint != null) {
/* 156 */         Preconditions.checkState(lowerBoundType != null, "'lowerEndpoint' field found, but not 'lowerBoundType'");
/* 157 */         return RangeFactory.downTo(lowerEndpoint, lowerBoundType);
/*     */       }
/* 159 */       if (upperEndpoint != null) {
/* 160 */         Preconditions.checkState(upperBoundType != null, "'upperEndpoint' field found, but not 'upperBoundType'");
/* 161 */         return RangeFactory.upTo(upperEndpoint, upperBoundType);
/*     */       }
/* 163 */       return RangeFactory.all();
/*     */     } catch (IllegalStateException e) {
/* 165 */       throw JsonMappingException.from(parser, e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   private BoundType deserializeBoundType(JsonParser parser) throws IOException
/*     */   {
/* 171 */     expect(parser, JsonToken.VALUE_STRING, parser.getCurrentToken());
/* 172 */     String name = parser.getText();
/*     */     try {
/* 174 */       return BoundType.valueOf(name);
/*     */     } catch (IllegalArgumentException e) {
/* 176 */       throw new IllegalStateException("[" + name + "] is not a valid BoundType name.");
/*     */     }
/*     */   }
/*     */   
/*     */   private Comparable<?> deserializeEndpoint(JsonParser parser, DeserializationContext context) throws IOException
/*     */   {
/* 182 */     Object obj = this._endpointDeserializer.deserialize(parser, context);
/* 183 */     if (!(obj instanceof Comparable)) {
/* 184 */       throw context.mappingException(String.format("Field [%s] deserialized to [%s], which does not implement Comparable.", new Object[] { parser.getCurrentName(), obj.getClass().getName() }));
/*     */     }
/*     */     
/*     */ 
/* 188 */     return (Comparable)obj;
/*     */   }
/*     */   
/*     */   private void expect(JsonParser p, JsonToken expected, JsonToken actual) throws JsonMappingException
/*     */   {
/* 193 */     if (actual != expected) {
/* 194 */       throw JsonMappingException.from(p, "Expecting " + expected + ", found " + actual);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\RangeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */