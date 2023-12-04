/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId.Referring;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedField;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
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
/*     */ public class SettableAnyProperty
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final BeanProperty _property;
/*     */   protected final AnnotatedMember _setter;
/*     */   final boolean _setterIsField;
/*     */   protected final JavaType _type;
/*     */   protected JsonDeserializer<Object> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   
/*     */   public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser)
/*     */   {
/*  55 */     this._property = property;
/*  56 */     this._setter = setter;
/*  57 */     this._type = type;
/*  58 */     this._valueDeserializer = valueDeser;
/*  59 */     this._valueTypeDeserializer = typeDeser;
/*  60 */     this._setterIsField = (setter instanceof AnnotatedField);
/*     */   }
/*     */   
/*     */   public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
/*  64 */     return new SettableAnyProperty(this._property, this._setter, this._type, deser, this._valueTypeDeserializer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableAnyProperty(SettableAnyProperty src)
/*     */   {
/*  73 */     this._property = src._property;
/*  74 */     this._setter = src._setter;
/*  75 */     this._type = src._type;
/*  76 */     this._valueDeserializer = src._valueDeserializer;
/*  77 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/*  78 */     this._setterIsField = src._setterIsField;
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
/*     */   Object readResolve()
/*     */   {
/*  92 */     if ((this._setter == null) || (this._setter.getAnnotated() == null)) {
/*  93 */       throw new IllegalArgumentException("Missing method (broken JDK (de)serialization?)");
/*     */     }
/*  95 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 104 */   public BeanProperty getProperty() { return this._property; }
/*     */   
/* 106 */   public boolean hasValueDeserializer() { return this._valueDeserializer != null; }
/*     */   
/* 108 */   public JavaType getType() { return this._type; }
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
/*     */   public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance, String propName)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 125 */       set(instance, propName, deserialize(p, ctxt));
/*     */     } catch (UnresolvedForwardReference reference) {
/* 127 */       if (this._valueDeserializer.getObjectIdReader() == null) {
/* 128 */         throw JsonMappingException.from(p, "Unresolved forward reference but no identity info.", reference);
/*     */       }
/* 130 */       AnySetterReferring referring = new AnySetterReferring(this, reference, this._type.getRawClass(), instance, propName);
/*     */       
/* 132 */       reference.getRoid().appendReferring(referring);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
/*     */   {
/* 138 */     JsonToken t = p.getCurrentToken();
/* 139 */     if (t == JsonToken.VALUE_NULL) {
/* 140 */       return this._valueDeserializer.getNullValue(ctxt);
/*     */     }
/* 142 */     if (this._valueTypeDeserializer != null) {
/* 143 */       return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/*     */     }
/* 145 */     return this._valueDeserializer.deserialize(p, ctxt);
/*     */   }
/*     */   
/*     */   public void set(Object instance, String propName, Object value)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 153 */       if (this._setterIsField) {
/* 154 */         AnnotatedField field = (AnnotatedField)this._setter;
/* 155 */         Map<Object, Object> val = (Map)field.getValue(instance);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 161 */         if (val != null)
/*     */         {
/* 163 */           val.put(propName, value);
/*     */         }
/*     */       }
/*     */       else {
/* 167 */         ((AnnotatedMethod)this._setter).callOnWith(instance, new Object[] { propName, value });
/*     */       }
/*     */     } catch (Exception e) {
/* 170 */       _throwAsIOE(e, propName, value);
/*     */     }
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
/*     */   protected void _throwAsIOE(Exception e, String propName, Object value)
/*     */     throws IOException
/*     */   {
/* 188 */     if ((e instanceof IllegalArgumentException)) {
/* 189 */       String actType = value == null ? "[NULL]" : value.getClass().getName();
/* 190 */       StringBuilder msg = new StringBuilder("Problem deserializing \"any\" property '").append(propName);
/* 191 */       msg.append("' of class " + getClassName() + " (expected type: ").append(this._type);
/* 192 */       msg.append("; actual type: ").append(actType).append(")");
/* 193 */       String origMsg = e.getMessage();
/* 194 */       if (origMsg != null) {
/* 195 */         msg.append(", problem: ").append(origMsg);
/*     */       } else {
/* 197 */         msg.append(" (no error message provided)");
/*     */       }
/* 199 */       throw new JsonMappingException(null, msg.toString(), e);
/*     */     }
/* 201 */     if ((e instanceof IOException)) {
/* 202 */       throw ((IOException)e);
/*     */     }
/* 204 */     if ((e instanceof RuntimeException)) {
/* 205 */       throw ((RuntimeException)e);
/*     */     }
/*     */     
/* 208 */     Throwable t = e;
/* 209 */     while (t.getCause() != null) {
/* 210 */       t = t.getCause();
/*     */     }
/* 212 */     throw new JsonMappingException(null, t.getMessage(), t);
/*     */   }
/*     */   
/* 215 */   private String getClassName() { return this._setter.getDeclaringClass().getName(); }
/*     */   
/* 217 */   public String toString() { return "[any property on class " + getClassName() + "]"; }
/*     */   
/*     */   private static class AnySetterReferring extends Referring
/*     */   {
/*     */     private final SettableAnyProperty _parent;
/*     */     private final Object _pojo;
/*     */     private final String _propName;
/*     */     
/*     */     public AnySetterReferring(SettableAnyProperty parent, UnresolvedForwardReference reference, Class<?> type, Object instance, String propName)
/*     */     {
/* 227 */       super(type);
/* 228 */       this._parent = parent;
/* 229 */       this._pojo = instance;
/* 230 */       this._propName = propName;
/*     */     }
/*     */     
/*     */ 
/*     */     public void handleResolvedForwardReference(Object id, Object value)
/*     */       throws IOException
/*     */     {
/* 237 */       if (!hasId(id)) {
/* 238 */         throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
/*     */       }
/*     */       
/* 241 */       this._parent.set(this._pojo, this._propName, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\SettableAnyProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */