/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ArrayNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.EnumValues;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
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
/*     */ @JacksonStdImpl
/*     */ public class EnumSerializer
/*     */   extends StdScalarSerializer<Enum<?>>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final EnumValues _values;
/*     */   protected final Boolean _serializeAsIndex;
/*     */   
/*     */   @Deprecated
/*     */   public EnumSerializer(EnumValues v)
/*     */   {
/*  61 */     this(v, null);
/*     */   }
/*     */   
/*     */   public EnumSerializer(EnumValues v, Boolean serializeAsIndex)
/*     */   {
/*  66 */     super(v.getEnumClass(), false);
/*  67 */     this._values = v;
/*  68 */     this._serializeAsIndex = serializeAsIndex;
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
/*     */ 
/*     */ 
/*     */   public static EnumSerializer construct(Class<?> enumClass, SerializationConfig config, BeanDescription beanDesc, JsonFormat.Value format)
/*     */   {
/*  85 */     EnumValues v = EnumValues.constructFromName(config, enumClass);
/*  86 */     Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true);
/*  87 */     return new EnumSerializer(v, serializeAsIndex);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  99 */     if (property != null) {
/* 100 */       JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/*     */       
/* 102 */       if (format != null) {
/* 103 */         Boolean serializeAsIndex = _isShapeWrittenUsingIndex(property.getType().getRawClass(), format, false);
/* 104 */         if (serializeAsIndex != this._serializeAsIndex) {
/* 105 */           return new EnumSerializer(this._values, serializeAsIndex);
/*     */         }
/*     */       }
/*     */     }
/* 109 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EnumValues getEnumValues()
/*     */   {
/* 118 */     return this._values;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void serialize(Enum<?> en, JsonGenerator gen, SerializerProvider serializers)
/*     */     throws IOException
/*     */   {
/* 131 */     if (_serializeAsIndex(serializers)) {
/* 132 */       gen.writeNumber(en.ordinal());
/* 133 */       return;
/*     */     }
/*     */     
/* 136 */     if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/* 137 */       gen.writeString(en.toString());
/* 138 */       return;
/*     */     }
/* 140 */     gen.writeString(this._values.serializedValueFor(en));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*     */   {
/* 152 */     if (_serializeAsIndex(provider)) {
/* 153 */       return createSchemaNode("integer", true);
/*     */     }
/* 155 */     ObjectNode objectNode = createSchemaNode("string", true);
/* 156 */     ArrayNode enumNode; if (typeHint != null) {
/* 157 */       JavaType type = provider.constructType(typeHint);
/* 158 */       if (type.isEnumType()) {
/* 159 */         enumNode = objectNode.putArray("enum");
/* 160 */         for (SerializableString value : this._values.values()) {
/* 161 */           enumNode.add(value.getValue());
/*     */         }
/*     */       }
/*     */     }
/* 165 */     return objectNode;
/*     */   }
/*     */   
/*     */ 
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 172 */     SerializerProvider serializers = visitor.getProvider();
/* 173 */     if (_serializeAsIndex(serializers)) {
/* 174 */       visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
/* 175 */       return;
/*     */     }
/* 177 */     JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
/* 178 */     if (stringVisitor != null) {
/* 179 */       Set<String> enums = new LinkedHashSet();
/*     */       
/*     */ 
/* 182 */       if ((serializers != null) && (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)))
/*     */       {
/* 184 */         for (Enum<?> e : this._values.enums()) {
/* 185 */           enums.add(e.toString());
/*     */         }
/*     */         
/*     */       } else {
/* 189 */         for (SerializableString value : this._values.values()) {
/* 190 */           enums.add(value.getValue());
/*     */         }
/*     */       }
/* 193 */       stringVisitor.enumTypes(enums);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _serializeAsIndex(SerializerProvider serializers)
/*     */   {
/* 205 */     if (this._serializeAsIndex != null) {
/* 206 */       return this._serializeAsIndex.booleanValue();
/*     */     }
/* 208 */     return serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static Boolean _isShapeWrittenUsingIndex(Class<?> enumClass, JsonFormat.Value format, boolean fromClass)
/*     */   {
/* 217 */     JsonFormat.Shape shape = format == null ? null : format.getShape();
/* 218 */     if (shape == null) {
/* 219 */       return null;
/*     */     }
/*     */     
/* 222 */     if ((shape == JsonFormat.Shape.ANY) || (shape == JsonFormat.Shape.SCALAR)) {
/* 223 */       return null;
/*     */     }
/*     */     
/* 226 */     if ((shape == JsonFormat.Shape.STRING) || (shape == JsonFormat.Shape.NATURAL)) {
/* 227 */       return Boolean.FALSE;
/*     */     }
/*     */     
/* 230 */     if ((shape.isNumeric()) || (shape == JsonFormat.Shape.ARRAY)) {
/* 231 */       return Boolean.TRUE;
/*     */     }
/* 233 */     throw new IllegalArgumentException("Unsupported serialization shape (" + shape + ") for Enum " + enumClass.getName() + ", not supported as " + (fromClass ? "class" : "property") + " annotation");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\EnumSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */