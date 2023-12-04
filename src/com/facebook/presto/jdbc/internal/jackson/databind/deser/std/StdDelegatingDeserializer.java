/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ResolvableDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
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
/*     */ public class StdDelegatingDeserializer<T>
/*     */   extends StdDeserializer<T>
/*     */   implements ContextualDeserializer, ResolvableDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Converter<Object, T> _converter;
/*     */   protected final JavaType _delegateType;
/*     */   protected final JsonDeserializer<Object> _delegateDeserializer;
/*     */   
/*     */   public StdDelegatingDeserializer(Converter<?, T> converter)
/*     */   {
/*  61 */     super(Object.class);
/*  62 */     this._converter = converter;
/*  63 */     this._delegateType = null;
/*  64 */     this._delegateDeserializer = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public StdDelegatingDeserializer(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer)
/*     */   {
/*  71 */     super(delegateType);
/*  72 */     this._converter = converter;
/*  73 */     this._delegateType = delegateType;
/*  74 */     this._delegateDeserializer = delegateDeserializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected StdDelegatingDeserializer(StdDelegatingDeserializer<T> src)
/*     */   {
/*  82 */     super(src);
/*  83 */     this._converter = src._converter;
/*  84 */     this._delegateType = src._delegateType;
/*  85 */     this._delegateDeserializer = src._delegateDeserializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected StdDelegatingDeserializer<T> withDelegate(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer)
/*     */   {
/*  95 */     if (getClass() != StdDelegatingDeserializer.class) {
/*  96 */       throw new IllegalStateException("Sub-class " + getClass().getName() + " must override 'withDelegate'");
/*     */     }
/*  98 */     return new StdDelegatingDeserializer(converter, delegateType, delegateDeserializer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resolve(DeserializationContext ctxt)
/*     */     throws JsonMappingException
/*     */   {
/* 111 */     if ((this._delegateDeserializer != null) && ((this._delegateDeserializer instanceof ResolvableDeserializer))) {
/* 112 */       ((ResolvableDeserializer)this._delegateDeserializer).resolve(ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 121 */     if (this._delegateDeserializer != null) {
/* 122 */       JsonDeserializer<?> deser = ctxt.handleSecondaryContextualization(this._delegateDeserializer, property, this._delegateType);
/*     */       
/* 124 */       if (deser != this._delegateDeserializer) {
/* 125 */         return withDelegate(this._converter, this._delegateType, deser);
/*     */       }
/* 127 */       return this;
/*     */     }
/*     */     
/* 130 */     JavaType delegateType = this._converter.getInputType(ctxt.getTypeFactory());
/* 131 */     return withDelegate(this._converter, delegateType, ctxt.findContextualValueDeserializer(delegateType, property));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> getDelegatee()
/*     */   {
/* 143 */     return this._delegateDeserializer;
/*     */   }
/*     */   
/*     */   public Class<?> handledType()
/*     */   {
/* 148 */     return this._delegateDeserializer.handledType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 160 */     Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
/* 161 */     if (delegateValue == null) {
/* 162 */       return null;
/*     */     }
/* 164 */     return (T)convertValue(delegateValue);
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
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 182 */     Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
/* 183 */     if (delegateValue == null) {
/* 184 */       return null;
/*     */     }
/* 186 */     return convertValue(delegateValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue)
/*     */     throws IOException
/*     */   {
/* 194 */     if (this._delegateType.getRawClass().isAssignableFrom(intoValue.getClass())) {
/* 195 */       return (T)this._delegateDeserializer.deserialize(p, ctxt, intoValue);
/*     */     }
/* 197 */     return (T)_handleIncompatibleUpdateValue(p, ctxt, intoValue);
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
/*     */   protected Object _handleIncompatibleUpdateValue(JsonParser p, DeserializationContext ctxt, Object intoValue)
/*     */     throws IOException
/*     */   {
/* 213 */     throw new UnsupportedOperationException(String.format("Can not update object of type %s (using deserializer for type %s)" + intoValue.getClass().getName(), new Object[] { this._delegateType }));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected T convertValue(Object delegateValue)
/*     */   {
/* 237 */     return (T)this._converter.convert(delegateValue);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StdDelegatingDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */