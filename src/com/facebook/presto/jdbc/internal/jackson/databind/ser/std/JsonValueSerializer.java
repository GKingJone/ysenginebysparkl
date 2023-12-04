/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonschema.JsonSchema;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonschema.SchemaAware;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
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
/*     */ public class JsonValueSerializer
/*     */   extends StdSerializer<Object>
/*     */   implements ContextualSerializer, JsonFormatVisitable, SchemaAware
/*     */ {
/*     */   protected final AnnotatedMethod _accessorMethod;
/*     */   protected final JsonSerializer<Object> _valueSerializer;
/*     */   protected final BeanProperty _property;
/*     */   protected final boolean _forceTypeInformation;
/*     */   
/*     */   public JsonValueSerializer(AnnotatedMethod valueMethod, JsonSerializer<?> ser)
/*     */   {
/*  74 */     super(valueMethod.getType());
/*  75 */     this._accessorMethod = valueMethod;
/*  76 */     this._valueSerializer = ser;
/*  77 */     this._property = null;
/*  78 */     this._forceTypeInformation = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonValueSerializer(JsonValueSerializer src, BeanProperty property, JsonSerializer<?> ser, boolean forceTypeInfo)
/*     */   {
/*  85 */     super(_notNullClass(src.handledType()));
/*  86 */     this._accessorMethod = src._accessorMethod;
/*  87 */     this._valueSerializer = ser;
/*  88 */     this._property = property;
/*  89 */     this._forceTypeInformation = forceTypeInfo;
/*     */   }
/*     */   
/*     */   private static final Class<Object> _notNullClass(Class<?> cls)
/*     */   {
/*  94 */     return cls == null ? Object.class : cls;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonValueSerializer withResolved(BeanProperty property, JsonSerializer<?> ser, boolean forceTypeInfo)
/*     */   {
/* 100 */     if ((this._property == property) && (this._valueSerializer == ser) && (forceTypeInfo == this._forceTypeInformation))
/*     */     {
/* 102 */       return this;
/*     */     }
/* 104 */     return new JsonValueSerializer(this, property, ser, forceTypeInfo);
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 122 */     JsonSerializer<?> ser = this._valueSerializer;
/* 123 */     if (ser == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 128 */       JavaType t = this._accessorMethod.getType();
/* 129 */       if ((provider.isEnabled(MapperFeature.USE_STATIC_TYPING)) || (t.isFinal()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */         ser = provider.findPrimaryPropertySerializer(t, property);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 141 */         boolean forceTypeInformation = isNaturalTypeWithStdHandling(t.getRawClass(), ser);
/* 142 */         return withResolved(property, ser, forceTypeInformation);
/*     */       }
/*     */     }
/*     */     else {
/* 146 */       ser = provider.handlePrimaryContextualization(ser, property);
/* 147 */       return withResolved(property, ser, this._forceTypeInformation);
/*     */     }
/* 149 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialize(Object bean, JsonGenerator jgen, SerializerProvider prov)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 162 */       Object value = this._accessorMethod.getValue(bean);
/* 163 */       if (value == null) {
/* 164 */         prov.defaultSerializeNull(jgen);
/* 165 */         return;
/*     */       }
/* 167 */       JsonSerializer<Object> ser = this._valueSerializer;
/* 168 */       if (ser == null) {
/* 169 */         Class<?> c = value.getClass();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 175 */         ser = prov.findTypedValueSerializer(c, true, this._property);
/*     */       }
/* 177 */       ser.serialize(value, jgen, prov);
/*     */     } catch (IOException ioe) {
/* 179 */       throw ioe;
/*     */     } catch (Exception e) {
/* 181 */       Throwable t = e;
/*     */       
/* 183 */       while (((t instanceof InvocationTargetException)) && (t.getCause() != null)) {
/* 184 */         t = t.getCause();
/*     */       }
/*     */       
/* 187 */       if ((t instanceof Error)) {
/* 188 */         throw ((Error)t);
/*     */       }
/*     */       
/* 191 */       throw JsonMappingException.wrapWithPath(t, bean, this._accessorMethod.getName() + "()");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void serializeWithType(Object bean, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer0)
/*     */     throws IOException
/*     */   {
/* 200 */     Object value = null;
/*     */     try {
/* 202 */       value = this._accessorMethod.getValue(bean);
/*     */       
/* 204 */       if (value == null) {
/* 205 */         provider.defaultSerializeNull(jgen);
/* 206 */         return;
/*     */       }
/* 208 */       JsonSerializer<Object> ser = this._valueSerializer;
/* 209 */       if (ser == null)
/*     */       {
/* 211 */         ser = provider.findValueSerializer(value.getClass(), this._property);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 216 */       else if (this._forceTypeInformation) {
/* 217 */         typeSer0.writeTypePrefixForScalar(bean, jgen);
/* 218 */         ser.serialize(value, jgen, provider);
/* 219 */         typeSer0.writeTypeSuffixForScalar(bean, jgen);
/* 220 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 228 */       ser.serializeWithType(value, jgen, provider, typeSer0);
/*     */     } catch (IOException ioe) {
/* 230 */       throw ioe;
/*     */     } catch (Exception e) {
/* 232 */       Throwable t = e;
/*     */       
/* 234 */       while (((t instanceof InvocationTargetException)) && (t.getCause() != null)) {
/* 235 */         t = t.getCause();
/*     */       }
/*     */       
/* 238 */       if ((t instanceof Error)) {
/* 239 */         throw ((Error)t);
/*     */       }
/*     */       
/* 242 */       throw JsonMappingException.wrapWithPath(t, bean, this._accessorMethod.getName() + "()");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 251 */     if ((this._valueSerializer instanceof SchemaAware)) {
/* 252 */       return ((SchemaAware)this._valueSerializer).getSchema(provider, null);
/*     */     }
/* 254 */     return JsonSchema.getDefaultSchemaNode();
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
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 270 */     JavaType type = this._accessorMethod.getType();
/* 271 */     Class<?> declaring = this._accessorMethod.getDeclaringClass();
/* 272 */     if ((declaring != null) && (declaring.isEnum()) && 
/* 273 */       (_acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring))) {
/* 274 */       return;
/*     */     }
/*     */     
/* 277 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 278 */     if (ser == null) {
/* 279 */       ser = visitor.getProvider().findTypedValueSerializer(type, false, this._property);
/* 280 */       if (ser == null) {
/* 281 */         visitor.expectAnyFormat(typeHint);
/* 282 */         return;
/*     */       }
/*     */     }
/* 285 */     ser.acceptJsonFormatVisitor(visitor, null);
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
/*     */   protected boolean _acceptJsonFormatVisitorForEnum(JsonFormatVisitorWrapper visitor, JavaType typeHint, Class<?> enumType)
/*     */     throws JsonMappingException
/*     */   {
/* 302 */     JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
/* 303 */     if (stringVisitor != null) {
/* 304 */       Set<String> enums = new LinkedHashSet();
/* 305 */       for (Object en : enumType.getEnumConstants())
/*     */       {
/*     */         try
/*     */         {
/*     */ 
/* 310 */           enums.add(String.valueOf(this._accessorMethod.callOn(en)));
/*     */         } catch (Exception e) {
/* 312 */           Throwable t = e;
/* 313 */           while (((t instanceof InvocationTargetException)) && (t.getCause() != null)) {
/* 314 */             t = t.getCause();
/*     */           }
/* 316 */           if ((t instanceof Error)) {
/* 317 */             throw ((Error)t);
/*     */           }
/* 319 */           throw JsonMappingException.wrapWithPath(t, en, this._accessorMethod.getName() + "()");
/*     */         }
/*     */       }
/* 322 */       stringVisitor.enumTypes(enums);
/*     */     }
/* 324 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isNaturalTypeWithStdHandling(Class<?> rawType, JsonSerializer<?> ser)
/*     */   {
/* 330 */     if (rawType.isPrimitive()) {
/* 331 */       if ((rawType != Integer.TYPE) && (rawType != Boolean.TYPE) && (rawType != Double.TYPE)) {
/* 332 */         return false;
/*     */       }
/*     */     }
/* 335 */     else if ((rawType != String.class) && (rawType != Integer.class) && (rawType != Boolean.class) && (rawType != Double.class))
/*     */     {
/* 337 */       return false;
/*     */     }
/*     */     
/* 340 */     return isDefaultSerializer(ser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 351 */     return "(@JsonValue serializer for method " + this._accessorMethod.getDeclaringClass() + "#" + this._accessorMethod.getName() + ")";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\JsonValueSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */