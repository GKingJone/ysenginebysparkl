/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedParameter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedWithParams;
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
/*     */ public abstract class ValueInstantiator
/*     */ {
/*     */   public Class<?> getValueClass()
/*     */   {
/*  50 */     return Object.class;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getValueTypeDesc()
/*     */   {
/*  58 */     Class<?> cls = getValueClass();
/*  59 */     if (cls == null) {
/*  60 */       return "UNKNOWN";
/*     */     }
/*  62 */     return cls.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canInstantiate()
/*     */   {
/*  71 */     return (canCreateUsingDefault()) || (canCreateUsingDelegate()) || (canCreateFromObjectWith()) || (canCreateFromString()) || (canCreateFromInt()) || (canCreateFromLong()) || (canCreateFromDouble()) || (canCreateFromBoolean());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canCreateFromString()
/*     */   {
/*  81 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canCreateFromInt()
/*     */   {
/*  87 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canCreateFromLong()
/*     */   {
/*  93 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canCreateFromDouble()
/*     */   {
/*  99 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canCreateFromBoolean()
/*     */   {
/* 105 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canCreateUsingDefault()
/*     */   {
/* 112 */     return getDefaultCreator() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canCreateUsingDelegate()
/*     */   {
/* 119 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canCreateUsingArrayDelegate()
/*     */   {
/* 126 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canCreateFromObjectWith()
/*     */   {
/* 133 */     return false;
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
/*     */   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config)
/*     */   {
/* 146 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType getDelegateType(DeserializationConfig config)
/*     */   {
/* 156 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType getArrayDelegateType(DeserializationConfig config)
/*     */   {
/* 165 */     return null;
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
/*     */   public Object createUsingDefault(DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 184 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no default no-arguments constructor found", new Object[0]);
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
/*     */   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args)
/*     */     throws IOException
/*     */   {
/* 198 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no creator with arguments specified", new Object[0]);
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
/*     */ 
/*     */   public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 224 */     return createFromObjectWith(ctxt, buffer.getParameters(props));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object createUsingDelegate(DeserializationContext ctxt, Object delegate)
/*     */     throws IOException
/*     */   {
/* 232 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no delegate creator specified", new Object[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate)
/*     */     throws IOException
/*     */   {
/* 241 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no array delegate creator specified", new Object[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object createFromString(DeserializationContext ctxt, String value)
/*     */     throws IOException
/*     */   {
/* 253 */     return _createFromStringFallbacks(ctxt, value);
/*     */   }
/*     */   
/*     */   public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
/* 257 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no int/Int-argument constructor/factory method to deserialize from Number value (%s)", new Object[] { Integer.valueOf(value) });
/*     */   }
/*     */   
/*     */   public Object createFromLong(DeserializationContext ctxt, long value)
/*     */     throws IOException
/*     */   {
/* 263 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", new Object[] { Long.valueOf(value) });
/*     */   }
/*     */   
/*     */   public Object createFromDouble(DeserializationContext ctxt, double value)
/*     */     throws IOException
/*     */   {
/* 269 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", new Object[] { Double.valueOf(value) });
/*     */   }
/*     */   
/*     */   public Object createFromBoolean(DeserializationContext ctxt, boolean value)
/*     */     throws IOException
/*     */   {
/* 275 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", new Object[] { Boolean.valueOf(value) });
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
/*     */   public AnnotatedWithParams getDefaultCreator()
/*     */   {
/* 296 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AnnotatedWithParams getDelegateCreator()
/*     */   {
/* 306 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AnnotatedWithParams getArrayDelegateCreator()
/*     */   {
/* 316 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AnnotatedWithParams getWithArgsCreator()
/*     */   {
/* 327 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public AnnotatedParameter getIncompleteParameter()
/*     */   {
/* 333 */     return null;
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
/*     */   protected Object _createFromStringFallbacks(DeserializationContext ctxt, String value)
/*     */     throws IOException
/*     */   {
/* 351 */     if (canCreateFromBoolean()) {
/* 352 */       String str = value.trim();
/* 353 */       if ("true".equals(str)) {
/* 354 */         return createFromBoolean(ctxt, true);
/*     */       }
/* 356 */       if ("false".equals(str)) {
/* 357 */         return createFromBoolean(ctxt, false);
/*     */       }
/*     */     }
/*     */     
/* 361 */     if ((value.length() == 0) && 
/* 362 */       (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT))) {
/* 363 */       return null;
/*     */     }
/*     */     
/* 366 */     return ctxt.handleMissingInstantiator(getValueClass(), ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", new Object[] { value });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class Base
/*     */     extends ValueInstantiator
/*     */   {
/*     */     protected final Class<?> _valueType;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Base(Class<?> type)
/*     */     {
/* 386 */       this._valueType = type;
/*     */     }
/*     */     
/*     */     public Base(JavaType type) {
/* 390 */       this._valueType = type.getRawClass();
/*     */     }
/*     */     
/*     */     public String getValueTypeDesc()
/*     */     {
/* 395 */       return this._valueType.getName();
/*     */     }
/*     */     
/*     */     public Class<?> getValueClass()
/*     */     {
/* 400 */       return this._valueType;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\ValueInstantiator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */