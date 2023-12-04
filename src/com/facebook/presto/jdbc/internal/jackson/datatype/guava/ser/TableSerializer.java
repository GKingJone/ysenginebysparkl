/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Table;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContainerSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.MapSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
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
/*     */ public class TableSerializer
/*     */   extends ContainerSerializer<Table<?, ?, ?>>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = -1449462718192917949L;
/*     */   private final JavaType _type;
/*     */   private final BeanProperty _property;
/*     */   private final JsonSerializer<Object> _rowSerializer;
/*     */   private final JsonSerializer<Object> _columnSerializer;
/*     */   private final TypeSerializer _valueTypeSerializer;
/*     */   private final JsonSerializer<Object> _valueSerializer;
/*     */   private final MapSerializer _rowMapSerializer;
/*     */   
/*     */   public TableSerializer(JavaType type)
/*     */   {
/*  49 */     super(type);
/*  50 */     this._type = type;
/*  51 */     this._property = null;
/*  52 */     this._rowSerializer = null;
/*  53 */     this._columnSerializer = null;
/*  54 */     this._valueTypeSerializer = null;
/*  55 */     this._valueSerializer = null;
/*     */     
/*  57 */     this._rowMapSerializer = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TableSerializer(TableSerializer src, BeanProperty property, TypeFactory typeFactory, JsonSerializer<?> rowKeySerializer, JsonSerializer<?> columnKeySerializer, TypeSerializer valueTypeSerializer, JsonSerializer<?> valueSerializer)
/*     */   {
/*  69 */     super(src);
/*  70 */     this._type = src._type;
/*  71 */     this._property = property;
/*  72 */     this._rowSerializer = rowKeySerializer;
/*  73 */     this._columnSerializer = columnKeySerializer;
/*  74 */     this._valueTypeSerializer = valueTypeSerializer;
/*  75 */     this._valueSerializer = valueSerializer;
/*     */     
/*  77 */     MapType columnAndValueType = typeFactory.constructMapType(Map.class, this._type.containedTypeOrUnknown(1), this._type.containedTypeOrUnknown(2));
/*     */     
/*     */ 
/*  80 */     JsonSerializer<?> columnAndValueSerializer = MapSerializer.construct((Set)null, columnAndValueType, false, this._valueTypeSerializer, this._columnSerializer, this._valueSerializer, null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  89 */     MapType rowMapType = typeFactory.constructMapType(Map.class, this._type.containedTypeOrUnknown(0), columnAndValueType);
/*     */     
/*  91 */     this._rowMapSerializer = MapSerializer.construct((Set)null, rowMapType, false, null, this._rowSerializer, columnAndValueSerializer, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TableSerializer(TableSerializer src, TypeSerializer typeSer)
/*     */   {
/* 103 */     super(src);
/* 104 */     this._type = src._type;
/* 105 */     this._property = src._property;
/* 106 */     this._rowSerializer = src._rowSerializer;
/* 107 */     this._columnSerializer = src._columnSerializer;
/* 108 */     this._valueTypeSerializer = typeSer;
/* 109 */     this._valueSerializer = src._valueSerializer;
/*     */     
/* 111 */     this._rowMapSerializer = src._rowMapSerializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TableSerializer withResolved(BeanProperty property, TypeFactory typeFactory, JsonSerializer<?> rowKeySer, JsonSerializer<?> columnKeySer, TypeSerializer vts, JsonSerializer<?> valueSer)
/*     */   {
/* 122 */     return new TableSerializer(this, property, typeFactory, rowKeySer, columnKeySer, vts, valueSer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer)
/*     */   {
/* 129 */     return new TableSerializer(this, typeSer);
/*     */   }
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 135 */     JsonSerializer<?> valueSer = this._valueSerializer;
/* 136 */     if (valueSer == null) {
/* 137 */       JavaType valueType = this._type.containedTypeOrUnknown(2);
/* 138 */       if (valueType.isFinal()) {
/* 139 */         valueSer = provider.findValueSerializer(valueType, property);
/*     */       }
/*     */     }
/* 142 */     else if ((valueSer instanceof ContextualSerializer)) {
/* 143 */       valueSer = ((ContextualSerializer)valueSer).createContextual(provider, property);
/*     */     }
/* 145 */     JsonSerializer<?> rowKeySer = this._rowSerializer;
/* 146 */     if (rowKeySer == null) {
/* 147 */       rowKeySer = provider.findKeySerializer(this._type.containedTypeOrUnknown(0), property);
/*     */     }
/* 149 */     else if ((rowKeySer instanceof ContextualSerializer)) {
/* 150 */       rowKeySer = ((ContextualSerializer)rowKeySer).createContextual(provider, property);
/*     */     }
/* 152 */     JsonSerializer<?> columnKeySer = this._columnSerializer;
/* 153 */     if (columnKeySer == null) {
/* 154 */       columnKeySer = provider.findKeySerializer(this._type.containedTypeOrUnknown(1), property);
/*     */     }
/* 156 */     else if ((columnKeySer instanceof ContextualSerializer)) {
/* 157 */       columnKeySer = ((ContextualSerializer)columnKeySer).createContextual(provider, property);
/*     */     }
/*     */     
/* 160 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 161 */     if (typeSer != null) {
/* 162 */       typeSer = typeSer.forProperty(property);
/*     */     }
/* 164 */     return withResolved(property, provider.getTypeFactory(), rowKeySer, columnKeySer, typeSer, valueSer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType getContentType()
/*     */   {
/* 175 */     return this._type.getContentType();
/*     */   }
/*     */   
/*     */   public JsonSerializer<?> getContentSerializer()
/*     */   {
/* 180 */     return this._valueSerializer;
/*     */   }
/*     */   
/*     */   public boolean isEmpty(SerializerProvider provider, Table<?, ?, ?> table)
/*     */   {
/* 185 */     return table.isEmpty();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean isEmpty(Table<?, ?, ?> table)
/*     */   {
/* 191 */     return table.isEmpty();
/*     */   }
/*     */   
/*     */   public boolean hasSingleElement(Table<?, ?, ?> table)
/*     */   {
/* 196 */     return table.size() == 1;
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
/*     */   public void serialize(Table<?, ?, ?> value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 210 */     gen.writeStartObject();
/* 211 */     if (!value.isEmpty()) {
/* 212 */       serializeFields(value, gen, provider);
/*     */     }
/* 214 */     gen.writeEndObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serializeWithType(Table<?, ?, ?> value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException
/*     */   {
/* 223 */     typeSer.writeTypePrefixForObject(value, gen);
/* 224 */     serializeFields(value, gen, provider);
/* 225 */     typeSer.writeTypeSuffixForObject(value, gen);
/*     */   }
/*     */   
/*     */   private final void serializeFields(Table<?, ?, ?> table, JsonGenerator jgen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 231 */     this._rowMapSerializer.serializeFields(table.rowMap(), jgen, provider);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\ser\TableSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */