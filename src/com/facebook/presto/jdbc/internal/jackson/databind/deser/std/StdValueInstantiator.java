/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedParameter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedWithParams;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ public class StdValueInstantiator
/*     */   extends ValueInstantiator
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final String _valueTypeDesc;
/*     */   protected final Class<?> _valueClass;
/*     */   protected AnnotatedWithParams _defaultCreator;
/*     */   protected AnnotatedWithParams _withArgsCreator;
/*     */   protected SettableBeanProperty[] _constructorArguments;
/*     */   protected JavaType _delegateType;
/*     */   protected AnnotatedWithParams _delegateCreator;
/*     */   protected SettableBeanProperty[] _delegateArguments;
/*     */   protected JavaType _arrayDelegateType;
/*     */   protected AnnotatedWithParams _arrayDelegateCreator;
/*     */   protected SettableBeanProperty[] _arrayDelegateArguments;
/*     */   protected AnnotatedWithParams _fromStringCreator;
/*     */   protected AnnotatedWithParams _fromIntCreator;
/*     */   protected AnnotatedWithParams _fromLongCreator;
/*     */   protected AnnotatedWithParams _fromDoubleCreator;
/*     */   protected AnnotatedWithParams _fromBooleanCreator;
/*     */   protected AnnotatedParameter _incompleteParameter;
/*     */   
/*     */   @Deprecated
/*     */   public StdValueInstantiator(DeserializationConfig config, Class<?> valueType)
/*     */   {
/*  82 */     this._valueTypeDesc = (valueType == null ? "UNKNOWN TYPE" : valueType.getName());
/*  83 */     this._valueClass = (valueType == null ? Object.class : valueType);
/*     */   }
/*     */   
/*     */   public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
/*  87 */     this._valueTypeDesc = (valueType == null ? "UNKNOWN TYPE" : valueType.toString());
/*  88 */     this._valueClass = (valueType == null ? Object.class : valueType.getRawClass());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected StdValueInstantiator(StdValueInstantiator src)
/*     */   {
/*  97 */     this._valueTypeDesc = src._valueTypeDesc;
/*  98 */     this._valueClass = src._valueClass;
/*     */     
/* 100 */     this._defaultCreator = src._defaultCreator;
/*     */     
/* 102 */     this._constructorArguments = src._constructorArguments;
/* 103 */     this._withArgsCreator = src._withArgsCreator;
/*     */     
/* 105 */     this._delegateType = src._delegateType;
/* 106 */     this._delegateCreator = src._delegateCreator;
/* 107 */     this._delegateArguments = src._delegateArguments;
/*     */     
/* 109 */     this._arrayDelegateType = src._arrayDelegateType;
/* 110 */     this._arrayDelegateCreator = src._arrayDelegateCreator;
/* 111 */     this._arrayDelegateArguments = src._arrayDelegateArguments;
/*     */     
/* 113 */     this._fromStringCreator = src._fromStringCreator;
/* 114 */     this._fromIntCreator = src._fromIntCreator;
/* 115 */     this._fromLongCreator = src._fromLongCreator;
/* 116 */     this._fromDoubleCreator = src._fromDoubleCreator;
/* 117 */     this._fromBooleanCreator = src._fromBooleanCreator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs)
/*     */   {
/* 129 */     this._defaultCreator = defaultCreator;
/* 130 */     this._delegateCreator = delegateCreator;
/* 131 */     this._delegateType = delegateType;
/* 132 */     this._delegateArguments = delegateArgs;
/* 133 */     this._withArgsCreator = withArgsCreator;
/* 134 */     this._constructorArguments = constructorArgs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs)
/*     */   {
/* 142 */     this._arrayDelegateCreator = arrayDelegateCreator;
/* 143 */     this._arrayDelegateType = arrayDelegateType;
/* 144 */     this._arrayDelegateArguments = arrayDelegateArgs;
/*     */   }
/*     */   
/*     */   public void configureFromStringCreator(AnnotatedWithParams creator) {
/* 148 */     this._fromStringCreator = creator;
/*     */   }
/*     */   
/*     */   public void configureFromIntCreator(AnnotatedWithParams creator) {
/* 152 */     this._fromIntCreator = creator;
/*     */   }
/*     */   
/*     */   public void configureFromLongCreator(AnnotatedWithParams creator) {
/* 156 */     this._fromLongCreator = creator;
/*     */   }
/*     */   
/*     */   public void configureFromDoubleCreator(AnnotatedWithParams creator) {
/* 160 */     this._fromDoubleCreator = creator;
/*     */   }
/*     */   
/*     */   public void configureFromBooleanCreator(AnnotatedWithParams creator) {
/* 164 */     this._fromBooleanCreator = creator;
/*     */   }
/*     */   
/*     */   public void configureIncompleteParameter(AnnotatedParameter parameter) {
/* 168 */     this._incompleteParameter = parameter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getValueTypeDesc()
/*     */   {
/* 179 */     return this._valueTypeDesc;
/*     */   }
/*     */   
/*     */   public Class<?> getValueClass()
/*     */   {
/* 184 */     return this._valueClass;
/*     */   }
/*     */   
/*     */   public boolean canCreateFromString()
/*     */   {
/* 189 */     return this._fromStringCreator != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateFromInt()
/*     */   {
/* 194 */     return this._fromIntCreator != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateFromLong()
/*     */   {
/* 199 */     return this._fromLongCreator != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateFromDouble()
/*     */   {
/* 204 */     return this._fromDoubleCreator != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateFromBoolean()
/*     */   {
/* 209 */     return this._fromBooleanCreator != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateUsingDefault()
/*     */   {
/* 214 */     return this._defaultCreator != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateUsingDelegate()
/*     */   {
/* 219 */     return this._delegateType != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateUsingArrayDelegate()
/*     */   {
/* 224 */     return this._arrayDelegateType != null;
/*     */   }
/*     */   
/*     */   public boolean canCreateFromObjectWith()
/*     */   {
/* 229 */     return this._withArgsCreator != null;
/*     */   }
/*     */   
/*     */   public JavaType getDelegateType(DeserializationConfig config)
/*     */   {
/* 234 */     return this._delegateType;
/*     */   }
/*     */   
/*     */   public JavaType getArrayDelegateType(DeserializationConfig config)
/*     */   {
/* 239 */     return this._arrayDelegateType;
/*     */   }
/*     */   
/*     */   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config)
/*     */   {
/* 244 */     return this._constructorArguments;
/*     */   }
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
/* 256 */     if (this._defaultCreator == null) {
/* 257 */       return super.createUsingDefault(ctxt);
/*     */     }
/*     */     try {
/* 260 */       return this._defaultCreator.call();
/*     */     } catch (Throwable t) {
/* 262 */       return ctxt.handleInstantiationProblem(this._defaultCreator.getDeclaringClass(), null, rewrapCtorProblem(ctxt, t));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args)
/*     */     throws IOException
/*     */   {
/* 270 */     if (this._withArgsCreator == null) {
/* 271 */       return super.createFromObjectWith(ctxt, args);
/*     */     }
/*     */     try {
/* 274 */       return this._withArgsCreator.call(args);
/*     */     } catch (Throwable t) {
/* 276 */       return ctxt.handleInstantiationProblem(this._withArgsCreator.getDeclaringClass(), args, rewrapCtorProblem(ctxt, t));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Object createUsingDelegate(DeserializationContext ctxt, Object delegate)
/*     */     throws IOException
/*     */   {
/* 284 */     return _createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
/*     */   }
/*     */   
/*     */   public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate)
/*     */     throws IOException
/*     */   {
/* 290 */     if (this._arrayDelegateCreator == null)
/*     */     {
/* 292 */       return createUsingDelegate(ctxt, delegate);
/*     */     }
/* 294 */     return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
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
/* 306 */     if (this._fromStringCreator == null) {
/* 307 */       return _createFromStringFallbacks(ctxt, value);
/*     */     }
/*     */     try {
/* 310 */       return this._fromStringCreator.call1(value);
/*     */     } catch (Throwable t) {
/* 312 */       return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, rewrapCtorProblem(ctxt, t));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object createFromInt(DeserializationContext ctxt, int value)
/*     */     throws IOException
/*     */   {
/* 321 */     if (this._fromIntCreator != null) {
/* 322 */       Object arg = Integer.valueOf(value);
/*     */       try {
/* 324 */         return this._fromIntCreator.call1(arg);
/*     */       } catch (Throwable t0) {
/* 326 */         return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 331 */     if (this._fromLongCreator != null) {
/* 332 */       Object arg = Long.valueOf(value);
/*     */       try {
/* 334 */         return this._fromLongCreator.call1(arg);
/*     */       } catch (Throwable t0) {
/* 336 */         return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
/*     */       }
/*     */     }
/*     */     
/* 340 */     return super.createFromInt(ctxt, value);
/*     */   }
/*     */   
/*     */   public Object createFromLong(DeserializationContext ctxt, long value)
/*     */     throws IOException
/*     */   {
/* 346 */     if (this._fromLongCreator == null) {
/* 347 */       return super.createFromLong(ctxt, value);
/*     */     }
/* 349 */     Object arg = Long.valueOf(value);
/*     */     try {
/* 351 */       return this._fromLongCreator.call1(arg);
/*     */     } catch (Throwable t0) {
/* 353 */       return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Object createFromDouble(DeserializationContext ctxt, double value)
/*     */     throws IOException
/*     */   {
/* 361 */     if (this._fromDoubleCreator == null) {
/* 362 */       return super.createFromDouble(ctxt, value);
/*     */     }
/* 364 */     Object arg = Double.valueOf(value);
/*     */     try {
/* 366 */       return this._fromDoubleCreator.call1(arg);
/*     */     } catch (Throwable t0) {
/* 368 */       return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Object createFromBoolean(DeserializationContext ctxt, boolean value)
/*     */     throws IOException
/*     */   {
/* 376 */     if (this._fromBooleanCreator == null) {
/* 377 */       return super.createFromBoolean(ctxt, value);
/*     */     }
/* 379 */     Boolean arg = Boolean.valueOf(value);
/*     */     try {
/* 381 */       return this._fromBooleanCreator.call1(arg);
/*     */     } catch (Throwable t0) {
/* 383 */       return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
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
/*     */   public AnnotatedWithParams getDelegateCreator()
/*     */   {
/* 396 */     return this._delegateCreator;
/*     */   }
/*     */   
/*     */   public AnnotatedWithParams getArrayDelegateCreator()
/*     */   {
/* 401 */     return this._arrayDelegateCreator;
/*     */   }
/*     */   
/*     */   public AnnotatedWithParams getDefaultCreator()
/*     */   {
/* 406 */     return this._defaultCreator;
/*     */   }
/*     */   
/*     */   public AnnotatedWithParams getWithArgsCreator()
/*     */   {
/* 411 */     return this._withArgsCreator;
/*     */   }
/*     */   
/*     */   public AnnotatedParameter getIncompleteParameter()
/*     */   {
/* 416 */     return this._incompleteParameter;
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
/*     */   @Deprecated
/*     */   protected JsonMappingException wrapException(Throwable t)
/*     */   {
/* 434 */     for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 435 */       if ((curr instanceof JsonMappingException)) {
/* 436 */         return (JsonMappingException)curr;
/*     */       }
/*     */     }
/* 439 */     return new JsonMappingException(null, "Instantiation of " + getValueTypeDesc() + " value failed: " + t.getMessage(), t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t)
/*     */   {
/* 450 */     for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 451 */       if ((curr instanceof JsonMappingException)) {
/* 452 */         return (JsonMappingException)curr;
/*     */       }
/*     */     }
/* 455 */     return ctxt.instantiationException(getValueClass(), t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t)
/*     */   {
/* 465 */     if ((t instanceof JsonMappingException)) {
/* 466 */       return (JsonMappingException)t;
/*     */     }
/* 468 */     return ctxt.instantiationException(getValueClass(), t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t)
/*     */   {
/* 479 */     if (((t instanceof ExceptionInInitializerError)) || ((t instanceof InvocationTargetException)))
/*     */     {
/*     */ 
/* 482 */       Throwable cause = t.getCause();
/* 483 */       if (cause != null) {
/* 484 */         t = cause;
/*     */       }
/*     */     }
/* 487 */     return wrapAsJsonMappingException(ctxt, t);
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
/*     */   private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate)
/*     */     throws IOException
/*     */   {
/* 503 */     if (delegateCreator == null) {
/* 504 */       throw new IllegalStateException("No delegate constructor for " + getValueTypeDesc());
/*     */     }
/*     */     try
/*     */     {
/* 508 */       if (delegateArguments == null) {
/* 509 */         return delegateCreator.call1(delegate);
/*     */       }
/*     */       
/* 512 */       int len = delegateArguments.length;
/* 513 */       Object[] args = new Object[len];
/* 514 */       for (int i = 0; i < len; i++) {
/* 515 */         SettableBeanProperty prop = delegateArguments[i];
/* 516 */         if (prop == null) {
/* 517 */           args[i] = delegate;
/*     */         } else {
/* 519 */           args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
/*     */         }
/*     */       }
/*     */       
/* 523 */       return delegateCreator.call(args);
/*     */     } catch (Throwable t) {
/* 525 */       throw rewrapCtorProblem(ctxt, t);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StdValueInstantiator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */