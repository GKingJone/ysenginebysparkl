/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyBasedCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
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
/*     */ class FactoryBasedEnumDeserializer
/*     */   extends StdDeserializer<Object>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _inputType;
/*     */   protected final boolean _hasArgs;
/*     */   protected final AnnotatedMethod _factory;
/*     */   protected final JsonDeserializer<?> _deser;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final SettableBeanProperty[] _creatorProps;
/*     */   private transient PropertyBasedCreator _propCreator;
/*     */   
/*     */   public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f, JavaType paramType, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps)
/*     */   {
/*  54 */     super(cls);
/*  55 */     this._factory = f;
/*  56 */     this._hasArgs = true;
/*     */     
/*  58 */     this._inputType = (paramType.hasRawClass(String.class) ? null : paramType);
/*  59 */     this._deser = null;
/*  60 */     this._valueInstantiator = valueInstantiator;
/*  61 */     this._creatorProps = creatorProps;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f)
/*     */   {
/*  69 */     super(cls);
/*  70 */     this._factory = f;
/*  71 */     this._hasArgs = false;
/*  72 */     this._inputType = null;
/*  73 */     this._deser = null;
/*  74 */     this._valueInstantiator = null;
/*  75 */     this._creatorProps = null;
/*     */   }
/*     */   
/*     */   protected FactoryBasedEnumDeserializer(FactoryBasedEnumDeserializer base, JsonDeserializer<?> deser)
/*     */   {
/*  80 */     super(base._valueClass);
/*  81 */     this._inputType = base._inputType;
/*  82 */     this._factory = base._factory;
/*  83 */     this._hasArgs = base._hasArgs;
/*  84 */     this._valueInstantiator = base._valueInstantiator;
/*  85 */     this._creatorProps = base._creatorProps;
/*     */     
/*  87 */     this._deser = deser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  95 */     if ((this._deser == null) && (this._inputType != null)) {
/*  96 */       return new FactoryBasedEnumDeserializer(this, ctxt.findContextualValueDeserializer(this._inputType, property));
/*     */     }
/*     */     
/*  99 */     return this;
/*     */   }
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 105 */     Object value = null;
/* 106 */     if (this._deser != null) {
/* 107 */       value = this._deser.deserialize(p, ctxt);
/* 108 */     } else if (this._hasArgs) {
/* 109 */       JsonToken curr = p.getCurrentToken();
/*     */       
/*     */ 
/* 112 */       if ((curr == JsonToken.VALUE_STRING) || (curr == JsonToken.FIELD_NAME)) {
/* 113 */         value = p.getText();
/* 114 */       } else { if ((this._creatorProps != null) && (p.isExpectedStartObjectToken())) {
/* 115 */           if (this._propCreator == null) {
/* 116 */             this._propCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, this._creatorProps);
/*     */           }
/* 118 */           p.nextToken();
/* 119 */           return deserializeEnumUsingPropertyBased(p, ctxt, this._propCreator);
/*     */         }
/* 121 */         value = p.getValueAsString();
/*     */       }
/*     */     } else {
/* 124 */       p.skipChildren();
/*     */       try {
/* 126 */         return this._factory.call();
/*     */       } catch (Exception e) {
/* 128 */         Throwable t = ClassUtil.throwRootCauseIfIOE(e);
/* 129 */         return ctxt.handleInstantiationProblem(this._valueClass, null, t);
/*     */       }
/*     */     }
/*     */     try {
/* 133 */       return this._factory.callOnWith(this._valueClass, new Object[] { value });
/*     */     } catch (Exception e) {
/* 135 */       Throwable t = ClassUtil.throwRootCauseIfIOE(e);
/* 136 */       return ctxt.handleInstantiationProblem(this._valueClass, value, t);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException
/*     */   {
/* 142 */     if (this._deser == null) {
/* 143 */       return deserialize(p, ctxt);
/*     */     }
/* 145 */     return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Object deserializeEnumUsingPropertyBased(JsonParser p, DeserializationContext ctxt, PropertyBasedCreator creator)
/*     */     throws IOException
/*     */   {
/* 152 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
/*     */     
/* 154 */     for (JsonToken t = p.getCurrentToken(); 
/* 155 */         t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 156 */       String propName = p.getCurrentName();
/* 157 */       p.nextToken();
/*     */       
/* 159 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 160 */       if (creatorProp != null) {
/* 161 */         if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/* 162 */           p.nextToken();
/*     */         }
/*     */         
/*     */       }
/* 166 */       else if (buffer.readIdProperty(propName)) {}
/*     */     }
/*     */     
/*     */ 
/* 170 */     return creator.build(ctxt, buffer);
/*     */   }
/*     */   
/*     */   protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 178 */       return prop.deserialize(p, ctxt);
/*     */     } catch (Exception e) {
/* 180 */       wrapAndThrow(e, this._valueClass.getClass(), prop.getName(), ctxt);
/*     */     }
/* 182 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 189 */     throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
/*     */   }
/*     */   
/*     */   private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException
/*     */   {
/* 194 */     while (((t instanceof InvocationTargetException)) && (t.getCause() != null)) {
/* 195 */       t = t.getCause();
/*     */     }
/*     */     
/* 198 */     if ((t instanceof Error)) {
/* 199 */       throw ((Error)t);
/*     */     }
/* 201 */     boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/*     */     
/*     */ 
/* 204 */     if ((t instanceof IOException)) {
/* 205 */       if ((!wrap) || (!(t instanceof JsonProcessingException))) {
/* 206 */         throw ((IOException)t);
/*     */       }
/* 208 */     } else if ((!wrap) && 
/* 209 */       ((t instanceof RuntimeException))) {
/* 210 */       throw ((RuntimeException)t);
/*     */     }
/*     */     
/* 213 */     return t;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\FactoryBasedEnumDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */