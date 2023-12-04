/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.BeanDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableAnyProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.BeanPropertyMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ThrowableDeserializer
/*     */   extends BeanDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected static final String PROP_NAME_MESSAGE = "message";
/*     */   
/*     */   public ThrowableDeserializer(BeanDeserializer baseDeserializer)
/*     */   {
/*  30 */     super(baseDeserializer);
/*     */     
/*  32 */     this._vanillaProcessing = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ThrowableDeserializer(BeanDeserializer src, NameTransformer unwrapper)
/*     */   {
/*  39 */     super(src, unwrapper);
/*     */   }
/*     */   
/*     */   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper)
/*     */   {
/*  44 */     if (getClass() != ThrowableDeserializer.class) {
/*  45 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  51 */     return new ThrowableDeserializer(this, unwrapper);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/*  64 */     if (this._propertyBasedCreator != null) {
/*  65 */       return _deserializeUsingPropertyBased(p, ctxt);
/*     */     }
/*  67 */     if (this._delegateDeserializer != null) {
/*  68 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*     */     }
/*     */     
/*  71 */     if (this._beanType.isAbstract()) {
/*  72 */       return ctxt.handleMissingInstantiator(handledType(), p, "abstract type (need to add/enable type information?)", new Object[0]);
/*     */     }
/*     */     
/*  75 */     boolean hasStringCreator = this._valueInstantiator.canCreateFromString();
/*  76 */     boolean hasDefaultCtor = this._valueInstantiator.canCreateUsingDefault();
/*     */     
/*  78 */     if ((!hasStringCreator) && (!hasDefaultCtor)) {
/*  79 */       return ctxt.handleMissingInstantiator(handledType(), p, "Throwable needs a default contructor, a single-String-arg constructor; or explicit @JsonCreator", new Object[0]);
/*     */     }
/*     */     
/*     */ 
/*  83 */     Object throwable = null;
/*  84 */     Object[] pending = null;
/*  85 */     int pendingIx = 0;
/*  87 */     for (; 
/*  87 */         p.getCurrentToken() != JsonToken.END_OBJECT; p.nextToken()) {
/*  88 */       String propName = p.getCurrentName();
/*  89 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*  90 */       p.nextToken();
/*     */       
/*  92 */       if (prop != null) {
/*  93 */         if (throwable != null) {
/*  94 */           prop.deserializeAndSet(p, ctxt, throwable);
/*     */         }
/*     */         else
/*     */         {
/*  98 */           if (pending == null) {
/*  99 */             int len = this._beanProperties.size();
/* 100 */             pending = new Object[len + len];
/*     */           }
/* 102 */           pending[(pendingIx++)] = prop;
/* 103 */           pending[(pendingIx++)] = prop.deserialize(p, ctxt);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 108 */       else if (("message".equals(propName)) && 
/* 109 */         (hasStringCreator)) {
/* 110 */         throwable = this._valueInstantiator.createFromString(ctxt, p.getText());
/*     */         
/* 112 */         if (pending != null) {
/* 113 */           int i = 0; for (int len = pendingIx; i < len; i += 2) {
/* 114 */             prop = (SettableBeanProperty)pending[i];
/* 115 */             prop.set(throwable, pending[(i + 1)]);
/*     */           }
/* 117 */           pending = null;
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/* 125 */       else if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 126 */         p.skipChildren();
/*     */ 
/*     */       }
/* 129 */       else if (this._anySetter != null) {
/* 130 */         this._anySetter.deserializeAndSet(p, ctxt, throwable, propName);
/*     */       }
/*     */       else
/*     */       {
/* 134 */         handleUnknownProperty(p, ctxt, throwable, propName);
/*     */       }
/*     */     }
/* 137 */     if (throwable == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */       if (hasStringCreator) {
/* 145 */         throwable = this._valueInstantiator.createFromString(ctxt, null);
/*     */       } else {
/* 147 */         throwable = this._valueInstantiator.createUsingDefault(ctxt);
/*     */       }
/*     */       
/* 150 */       if (pending != null) {
/* 151 */         int i = 0; for (int len = pendingIx; i < len; i += 2) {
/* 152 */           SettableBeanProperty prop = (SettableBeanProperty)pending[i];
/* 153 */           prop.set(throwable, pending[(i + 1)]);
/*     */         }
/*     */       }
/*     */     }
/* 157 */     return throwable;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\ThrowableDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */