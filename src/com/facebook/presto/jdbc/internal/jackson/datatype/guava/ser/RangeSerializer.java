/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.BoundType;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Range;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RangeSerializer
/*     */   extends StdSerializer<Range<?>>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final JavaType _rangeType;
/*     */   protected final JsonSerializer<Object> _endpointSerializer;
/*     */   
/*     */   public RangeSerializer(JavaType type)
/*     */   {
/*  34 */     this(type, null);
/*     */   }
/*     */   
/*     */   public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer)
/*     */   {
/*  39 */     super(type);
/*  40 */     this._rangeType = type;
/*  41 */     this._endpointSerializer = endpointSer;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean isEmpty(Range<?> value)
/*     */   {
/*  47 */     return isEmpty(null, value);
/*     */   }
/*     */   
/*     */   public boolean isEmpty(SerializerProvider prov, Range<?> value)
/*     */   {
/*  52 */     return value.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  59 */     if (this._endpointSerializer == null) {
/*  60 */       JavaType endpointType = this._rangeType.containedTypeOrUnknown(0);
/*     */       
/*  62 */       if ((endpointType != null) && (!endpointType.hasRawClass(Object.class))) {
/*  63 */         JsonSerializer<?> ser = prov.findValueSerializer(endpointType, property);
/*  64 */         return new RangeSerializer(this._rangeType, ser);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*  70 */     else if ((this._endpointSerializer instanceof ContextualSerializer)) {
/*  71 */       JsonSerializer<?> cs = ((ContextualSerializer)this._endpointSerializer).createContextual(prov, property);
/*  72 */       if (cs != this._endpointSerializer) {
/*  73 */         return new RangeSerializer(this._rangeType, cs);
/*     */       }
/*     */     }
/*  76 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialize(Range<?> value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/*  89 */     gen.writeStartObject();
/*  90 */     _writeContents(value, gen, provider);
/*  91 */     gen.writeEndObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serializeWithType(Range<?> value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 101 */     typeSer.writeTypePrefixForObject(value, gen);
/* 102 */     _writeContents(value, gen, provider);
/* 103 */     typeSer.writeTypeSuffixForObject(value, gen);
/*     */   }
/*     */   
/*     */ 
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 110 */     if (visitor != null) {
/* 111 */       JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
/* 112 */       if ((objectVisitor != null) && 
/* 113 */         (this._endpointSerializer != null)) {
/* 114 */         JavaType endpointType = this._rangeType.containedType(0);
/* 115 */         JavaType btType = visitor.getProvider().constructType(BoundType.class);
/* 116 */         JsonSerializer<?> btSer = visitor.getProvider().findValueSerializer(btType, null);
/*     */         
/* 118 */         objectVisitor.property("lowerEndpoint", this._endpointSerializer, endpointType);
/* 119 */         objectVisitor.property("lowerBoundType", btSer, btType);
/* 120 */         objectVisitor.property("upperEndpoint", this._endpointSerializer, endpointType);
/* 121 */         objectVisitor.property("upperBoundType", btSer, btType);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void _writeContents(Range<?> value, JsonGenerator jgen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 130 */     if (value.hasLowerBound()) {
/* 131 */       if (this._endpointSerializer != null) {
/* 132 */         jgen.writeFieldName("lowerEndpoint");
/* 133 */         this._endpointSerializer.serialize(value.lowerEndpoint(), jgen, provider);
/*     */       } else {
/* 135 */         provider.defaultSerializeField("lowerEndpoint", value.lowerEndpoint(), jgen);
/*     */       }
/* 137 */       provider.defaultSerializeField("lowerBoundType", value.lowerBoundType(), jgen);
/*     */     }
/* 139 */     if (value.hasUpperBound()) {
/* 140 */       if (this._endpointSerializer != null) {
/* 141 */         jgen.writeFieldName("upperEndpoint");
/* 142 */         this._endpointSerializer.serialize(value.upperEndpoint(), jgen, provider);
/*     */       } else {
/* 144 */         provider.defaultSerializeField("upperEndpoint", value.upperEndpoint(), jgen);
/*     */       }
/* 146 */       provider.defaultSerializeField("upperBoundType", value.upperBoundType(), jgen);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\ser\RangeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */