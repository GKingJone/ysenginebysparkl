/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.PropertyWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import java.io.IOException;
/*     */ import java.lang.annotation.Annotation;
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
/*     */ public class MapProperty
/*     */   extends PropertyWriter
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final TypeSerializer _typeSerializer;
/*     */   protected final BeanProperty _property;
/*     */   protected Object _key;
/*     */   protected JsonSerializer<Object> _keySerializer;
/*     */   protected JsonSerializer<Object> _valueSerializer;
/*     */   
/*     */   public MapProperty(TypeSerializer typeSer, BeanProperty prop)
/*     */   {
/*  41 */     super(prop == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : prop.getMetadata());
/*  42 */     this._typeSerializer = typeSer;
/*  43 */     this._property = prop;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset(Object key, JsonSerializer<Object> keySer, JsonSerializer<Object> valueSer)
/*     */   {
/*  53 */     this._key = key;
/*  54 */     this._keySerializer = keySer;
/*  55 */     this._valueSerializer = valueSer;
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/*  60 */     if ((this._key instanceof String)) {
/*  61 */       return (String)this._key;
/*     */     }
/*  63 */     return String.valueOf(this._key);
/*     */   }
/*     */   
/*     */   public PropertyName getFullName()
/*     */   {
/*  68 */     return new PropertyName(getName());
/*     */   }
/*     */   
/*     */   public <A extends Annotation> A getAnnotation(Class<A> acls)
/*     */   {
/*  73 */     return this._property == null ? null : this._property.getAnnotation(acls);
/*     */   }
/*     */   
/*     */   public <A extends Annotation> A getContextAnnotation(Class<A> acls)
/*     */   {
/*  78 */     return this._property == null ? null : this._property.getContextAnnotation(acls);
/*     */   }
/*     */   
/*     */ 
/*     */   public void serializeAsField(Object value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*  85 */     this._keySerializer.serialize(this._key, gen, provider);
/*  86 */     if (this._typeSerializer == null) {
/*  87 */       this._valueSerializer.serialize(value, gen, provider);
/*     */     } else {
/*  89 */       this._valueSerializer.serializeWithType(value, gen, provider, this._typeSerializer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void serializeAsOmittedField(Object value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws Exception
/*     */   {
/*  97 */     if (!gen.canOmitFields()) {
/*  98 */       gen.writeOmittedField(getName());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void serializeAsElement(Object value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws Exception
/*     */   {
/* 106 */     if (this._typeSerializer == null) {
/* 107 */       this._valueSerializer.serialize(value, gen, provider);
/*     */     } else {
/* 109 */       this._valueSerializer.serializeWithType(value, gen, provider, this._typeSerializer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void serializeAsPlaceholder(Object value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws Exception
/*     */   {
/* 117 */     gen.writeNull();
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
/*     */   public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/* 131 */     if (this._property != null) {
/* 132 */       this._property.depositSchemaProperty(objectVisitor, provider);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {}
/*     */   
/*     */ 
/*     */   public JavaType getType()
/*     */   {
/* 145 */     return this._property == null ? TypeFactory.unknownType() : this._property.getType();
/*     */   }
/*     */   
/*     */   public PropertyName getWrapperName()
/*     */   {
/* 150 */     return this._property == null ? null : this._property.getWrapperName();
/*     */   }
/*     */   
/*     */   public AnnotatedMember getMember()
/*     */   {
/* 155 */     return this._property == null ? null : this._property.getMember();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\MapProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */