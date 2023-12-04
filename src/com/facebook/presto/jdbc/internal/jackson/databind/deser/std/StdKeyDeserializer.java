/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.NumberInput;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.EnumResolver;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Calendar;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class StdKeyDeserializer
/*     */   extends KeyDeserializer
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int TYPE_BOOLEAN = 1;
/*     */   public static final int TYPE_BYTE = 2;
/*     */   public static final int TYPE_SHORT = 3;
/*     */   public static final int TYPE_CHAR = 4;
/*     */   public static final int TYPE_INT = 5;
/*     */   public static final int TYPE_LONG = 6;
/*     */   public static final int TYPE_FLOAT = 7;
/*     */   public static final int TYPE_DOUBLE = 8;
/*     */   public static final int TYPE_LOCALE = 9;
/*     */   public static final int TYPE_DATE = 10;
/*     */   public static final int TYPE_CALENDAR = 11;
/*     */   public static final int TYPE_UUID = 12;
/*     */   public static final int TYPE_URI = 13;
/*     */   public static final int TYPE_URL = 14;
/*     */   public static final int TYPE_CLASS = 15;
/*     */   public static final int TYPE_CURRENCY = 16;
/*     */   protected final int _kind;
/*     */   protected final Class<?> _keyClass;
/*     */   protected final FromStringDeserializer<?> _deser;
/*     */   
/*     */   protected StdKeyDeserializer(int kind, Class<?> cls)
/*     */   {
/*  58 */     this(kind, cls, null);
/*     */   }
/*     */   
/*     */   protected StdKeyDeserializer(int kind, Class<?> cls, FromStringDeserializer<?> deser) {
/*  62 */     this._kind = kind;
/*  63 */     this._keyClass = cls;
/*  64 */     this._deser = deser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static StdKeyDeserializer forType(Class<?> raw)
/*     */   {
/*  72 */     if ((raw == String.class) || (raw == Object.class))
/*  73 */       return StringKD.forType(raw);
/*  74 */     int kind; if (raw == UUID.class) {
/*  75 */       kind = 12; } else { int kind;
/*  76 */       if (raw == Integer.class) {
/*  77 */         kind = 5; } else { int kind;
/*  78 */         if (raw == Long.class) {
/*  79 */           kind = 6; } else { int kind;
/*  80 */           if (raw == Date.class) {
/*  81 */             kind = 10; } else { int kind;
/*  82 */             if (raw == Calendar.class) {
/*  83 */               kind = 11;
/*     */             } else { int kind;
/*  85 */               if (raw == Boolean.class) {
/*  86 */                 kind = 1; } else { int kind;
/*  87 */                 if (raw == Byte.class) {
/*  88 */                   kind = 2; } else { int kind;
/*  89 */                   if (raw == Character.class) {
/*  90 */                     kind = 4; } else { int kind;
/*  91 */                     if (raw == Short.class) {
/*  92 */                       kind = 3; } else { int kind;
/*  93 */                       if (raw == Float.class) {
/*  94 */                         kind = 7; } else { int kind;
/*  95 */                         if (raw == Double.class) {
/*  96 */                           kind = 8; } else { int kind;
/*  97 */                           if (raw == URI.class) {
/*  98 */                             kind = 13; } else { int kind;
/*  99 */                             if (raw == URL.class) {
/* 100 */                               kind = 14; } else { int kind;
/* 101 */                               if (raw == Class.class) {
/* 102 */                                 kind = 15;
/* 103 */                               } else { if (raw == Locale.class) {
/* 104 */                                   FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Locale.class);
/* 105 */                                   return new StdKeyDeserializer(9, raw, deser); }
/* 106 */                                 if (raw == Currency.class) {
/* 107 */                                   FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Currency.class);
/* 108 */                                   return new StdKeyDeserializer(16, raw, deser);
/*     */                                 }
/* 110 */                                 return null; } } } } } } } } } } } } } }
/*     */     int kind;
/* 112 */     return new StdKeyDeserializer(kind, raw);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object deserializeKey(String key, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 119 */     if (key == null) {
/* 120 */       return null;
/*     */     }
/*     */     try {
/* 123 */       Object result = _parse(key, ctxt);
/* 124 */       if (result != null) {
/* 125 */         return result;
/*     */       }
/*     */     } catch (Exception re) {
/* 128 */       return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation, problem: %s", new Object[] { re.getMessage() });
/*     */     }
/* 130 */     if ((this._keyClass.isEnum()) && (ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL))) {
/* 131 */       return null;
/*     */     }
/* 133 */     return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
/*     */   }
/*     */   
/* 136 */   public Class<?> getKeyClass() { return this._keyClass; }
/*     */   
/*     */   protected Object _parse(String key, DeserializationContext ctxt) throws Exception
/*     */   {
/* 140 */     switch (this._kind) {
/*     */     case 1: 
/* 142 */       if ("true".equals(key)) {
/* 143 */         return Boolean.TRUE;
/*     */       }
/* 145 */       if ("false".equals(key)) {
/* 146 */         return Boolean.FALSE;
/*     */       }
/* 148 */       return ctxt.handleWeirdKey(this._keyClass, key, "value not 'true' or 'false'", new Object[0]);
/*     */     
/*     */     case 2: 
/* 151 */       int value = _parseInt(key);
/*     */       
/* 153 */       if ((value < -128) || (value > 255)) {
/* 154 */         return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value can not be represented as 8-bit value", new Object[0]);
/*     */       }
/* 156 */       return Byte.valueOf((byte)value);
/*     */     
/*     */ 
/*     */     case 3: 
/* 160 */       int value = _parseInt(key);
/* 161 */       if ((value < 32768) || (value > 32767)) {
/* 162 */         return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value can not be represented as 16-bit value", new Object[0]);
/*     */       }
/*     */       
/* 165 */       return Short.valueOf((short)value);
/*     */     
/*     */     case 4: 
/* 168 */       if (key.length() == 1) {
/* 169 */         return Character.valueOf(key.charAt(0));
/*     */       }
/* 171 */       return ctxt.handleWeirdKey(this._keyClass, key, "can only convert 1-character Strings", new Object[0]);
/*     */     case 5: 
/* 173 */       return Integer.valueOf(_parseInt(key));
/*     */     
/*     */     case 6: 
/* 176 */       return Long.valueOf(_parseLong(key));
/*     */     
/*     */ 
/*     */     case 7: 
/* 180 */       return Float.valueOf((float)_parseDouble(key));
/*     */     case 8: 
/* 182 */       return Double.valueOf(_parseDouble(key));
/*     */     case 9: 
/*     */       try {
/* 185 */         return this._deser._deserialize(key, ctxt);
/*     */       } catch (IOException e) {
/* 187 */         return ctxt.handleWeirdKey(this._keyClass, key, "unable to parse key as locale", new Object[0]);
/*     */       }
/*     */     case 16: 
/*     */       try {
/* 191 */         return this._deser._deserialize(key, ctxt);
/*     */       } catch (IOException e) {
/* 193 */         return ctxt.handleWeirdKey(this._keyClass, key, "unable to parse key as currency", new Object[0]);
/*     */       }
/*     */     case 10: 
/* 196 */       return ctxt.parseDate(key);
/*     */     case 11: 
/* 198 */       Date date = ctxt.parseDate(key);
/* 199 */       return date == null ? null : ctxt.constructCalendar(date);
/*     */     case 12: 
/*     */       try {
/* 202 */         return UUID.fromString(key);
/*     */       } catch (Exception e) {
/* 204 */         return ctxt.handleWeirdKey(this._keyClass, key, "problem: %s", new Object[] { e.getMessage() });
/*     */       }
/*     */     case 13: 
/*     */       try {
/* 208 */         return URI.create(key);
/*     */       } catch (Exception e) {
/* 210 */         return ctxt.handleWeirdKey(this._keyClass, key, "problem: %s", new Object[] { e.getMessage() });
/*     */       }
/*     */     case 14: 
/*     */       try {
/* 214 */         return new URL(key);
/*     */       } catch (MalformedURLException e) {
/* 216 */         return ctxt.handleWeirdKey(this._keyClass, key, "problem: %s", new Object[] { e.getMessage() });
/*     */       }
/*     */     case 15: 
/*     */       try {
/* 220 */         return ctxt.findClass(key);
/*     */       } catch (Exception e) {
/* 222 */         return ctxt.handleWeirdKey(this._keyClass, key, "unable to parse key as Class", new Object[0]);
/*     */       }
/*     */     }
/* 225 */     throw new IllegalStateException("Internal error: unknown key type " + this._keyClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _parseInt(String key)
/*     */     throws IllegalArgumentException
/*     */   {
/* 236 */     return Integer.parseInt(key);
/*     */   }
/*     */   
/*     */   protected long _parseLong(String key) throws IllegalArgumentException {
/* 240 */     return Long.parseLong(key);
/*     */   }
/*     */   
/*     */   protected double _parseDouble(String key) throws IllegalArgumentException {
/* 244 */     return NumberInput.parseDouble(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @JacksonStdImpl
/*     */   static final class StringKD
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/* 257 */     private static final StringKD sString = new StringKD(String.class);
/* 258 */     private static final StringKD sObject = new StringKD(Object.class);
/*     */     
/* 260 */     private StringKD(Class<?> nominalType) { super(nominalType); }
/*     */     
/*     */     public static StringKD forType(Class<?> nominalType)
/*     */     {
/* 264 */       if (nominalType == String.class) {
/* 265 */         return sString;
/*     */       }
/* 267 */       if (nominalType == Object.class) {
/* 268 */         return sObject;
/*     */       }
/* 270 */       return new StringKD(nominalType);
/*     */     }
/*     */     
/*     */     public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException
/*     */     {
/* 275 */       return key;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static final class DelegatingKD
/*     */     extends KeyDeserializer
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/*     */ 
/*     */     protected final Class<?> _keyClass;
/*     */     
/*     */ 
/*     */ 
/*     */     protected final JsonDeserializer<?> _delegate;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected DelegatingKD(Class<?> cls, JsonDeserializer<?> deser)
/*     */     {
/* 301 */       this._keyClass = cls;
/* 302 */       this._delegate = deser;
/*     */     }
/*     */     
/*     */ 
/*     */     public final Object deserializeKey(String key, DeserializationContext ctxt)
/*     */       throws IOException, JsonProcessingException
/*     */     {
/* 309 */       if (key == null) {
/* 310 */         return null;
/*     */       }
/*     */       try
/*     */       {
/* 314 */         Object result = this._delegate.deserialize(ctxt.getParser(), ctxt);
/* 315 */         if (result != null) {
/* 316 */           return result;
/*     */         }
/* 318 */         return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
/*     */       } catch (Exception re) {}
/* 320 */       return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation: %s", tmp54_51);
/*     */     }
/*     */     
/*     */     public Class<?> getKeyClass() {
/* 324 */       return this._keyClass;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @JacksonStdImpl
/*     */   static final class EnumKD
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     protected final EnumResolver _byNameResolver;
/*     */     
/*     */     protected final AnnotatedMethod _factory;
/*     */     
/*     */     protected EnumResolver _byToStringResolver;
/*     */     
/*     */ 
/*     */     protected EnumKD(EnumResolver er, AnnotatedMethod factory)
/*     */     {
/* 345 */       super(er.getEnumClass());
/* 346 */       this._byNameResolver = er;
/* 347 */       this._factory = factory;
/*     */     }
/*     */     
/*     */     public Object _parse(String key, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 353 */       if (this._factory != null) {
/*     */         try {
/* 355 */           return this._factory.call1(key);
/*     */         } catch (Exception e) {
/* 357 */           ClassUtil.unwrapAndThrowAsIAE(e);
/*     */         }
/*     */       }
/* 360 */       EnumResolver res = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? _getToStringResolver(ctxt) : this._byNameResolver;
/*     */       
/* 362 */       Enum<?> e = res.findEnum(key);
/* 363 */       if ((e == null) && (!ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL))) {
/* 364 */         return ctxt.handleWeirdKey(this._keyClass, key, "not one of values excepted for Enum class: %s", new Object[] { res.getEnumIds() });
/*     */       }
/*     */       
/*     */ 
/* 368 */       return e;
/*     */     }
/*     */     
/*     */     private EnumResolver _getToStringResolver(DeserializationContext ctxt)
/*     */     {
/* 373 */       EnumResolver res = this._byToStringResolver;
/* 374 */       if (res == null) {
/* 375 */         synchronized (this) {
/* 376 */           res = EnumResolver.constructUnsafeUsingToString(this._byNameResolver.getEnumClass(), ctxt.getAnnotationIntrospector());
/*     */         }
/*     */       }
/*     */       
/* 380 */       return res;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static final class StringCtorKeyDeserializer
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     protected final Constructor<?> _ctor;
/*     */     
/*     */ 
/*     */     public StringCtorKeyDeserializer(Constructor<?> ctor)
/*     */     {
/* 395 */       super(ctor.getDeclaringClass());
/* 396 */       this._ctor = ctor;
/*     */     }
/*     */     
/*     */     public Object _parse(String key, DeserializationContext ctxt)
/*     */       throws Exception
/*     */     {
/* 402 */       return this._ctor.newInstance(new Object[] { key });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static final class StringFactoryKeyDeserializer
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     final Method _factoryMethod;
/*     */     
/*     */ 
/*     */     public StringFactoryKeyDeserializer(Method fm)
/*     */     {
/* 417 */       super(fm.getDeclaringClass());
/* 418 */       this._factoryMethod = fm;
/*     */     }
/*     */     
/*     */     public Object _parse(String key, DeserializationContext ctxt)
/*     */       throws Exception
/*     */     {
/* 424 */       return this._factoryMethod.invoke(null, new Object[] { key });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StdKeyDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */