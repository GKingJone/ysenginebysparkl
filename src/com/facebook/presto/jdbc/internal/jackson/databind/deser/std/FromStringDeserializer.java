/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.exc.InvalidFormatException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Currency;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ public abstract class FromStringDeserializer<T>
/*     */   extends StdScalarDeserializer<T>
/*     */ {
/*     */   public static Class<?>[] types()
/*     */   {
/*  30 */     return new Class[] { File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, InetAddress.class, InetSocketAddress.class };
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
/*     */   protected FromStringDeserializer(Class<?> vc)
/*     */   {
/*  53 */     super(vc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Std findDeserializer(Class<?> rawType)
/*     */   {
/*  62 */     int kind = 0;
/*  63 */     if (rawType == File.class) {
/*  64 */       kind = 1;
/*  65 */     } else if (rawType == URL.class) {
/*  66 */       kind = 2;
/*  67 */     } else if (rawType == URI.class) {
/*  68 */       kind = 3;
/*  69 */     } else if (rawType == Class.class) {
/*  70 */       kind = 4;
/*  71 */     } else if (rawType == JavaType.class) {
/*  72 */       kind = 5;
/*  73 */     } else if (rawType == Currency.class) {
/*  74 */       kind = 6;
/*  75 */     } else if (rawType == Pattern.class) {
/*  76 */       kind = 7;
/*  77 */     } else if (rawType == Locale.class) {
/*  78 */       kind = 8;
/*  79 */     } else if (rawType == Charset.class) {
/*  80 */       kind = 9;
/*  81 */     } else if (rawType == TimeZone.class) {
/*  82 */       kind = 10;
/*  83 */     } else if (rawType == InetAddress.class) {
/*  84 */       kind = 11;
/*  85 */     } else if (rawType == InetSocketAddress.class) {
/*  86 */       kind = 12;
/*     */     } else {
/*  88 */       return null;
/*     */     }
/*  90 */     return new Std(rawType, kind);
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
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 104 */     String text = p.getValueAsString();
/* 105 */     if (text != null) {
/* 106 */       if ((text.length() == 0) || ((text = text.trim()).length() == 0))
/*     */       {
/* 108 */         return (T)_deserializeFromEmptyString();
/*     */       }
/* 110 */       Exception cause = null;
/*     */       try {
/* 112 */         T result = _deserialize(text, ctxt);
/* 113 */         if (result != null) {
/* 114 */           return result;
/*     */         }
/*     */       } catch (IllegalArgumentException iae) {
/* 117 */         cause = iae;
/*     */       }
/* 119 */       String msg = "not a valid textual representation";
/* 120 */       if (cause != null) {
/* 121 */         String m2 = cause.getMessage();
/* 122 */         if (m2 != null) {
/* 123 */           msg = msg + ", problem: " + m2;
/*     */         }
/*     */       }
/*     */       
/* 127 */       JsonMappingException e = ctxt.weirdStringException(text, this._valueClass, msg);
/* 128 */       if (cause != null) {
/* 129 */         e.initCause(cause);
/*     */       }
/* 131 */       throw e;
/*     */     }
/*     */     
/* 134 */     JsonToken t = p.getCurrentToken();
/*     */     
/* 136 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/* 137 */       p.nextToken();
/* 138 */       T value = deserialize(p, ctxt);
/* 139 */       if (p.nextToken() != JsonToken.END_ARRAY) {
/* 140 */         handleMissingEndArrayForSingle(p, ctxt);
/*     */       }
/* 142 */       return value;
/*     */     }
/* 144 */     if (t == JsonToken.VALUE_EMBEDDED_OBJECT)
/*     */     {
/* 146 */       Object ob = p.getEmbeddedObject();
/* 147 */       if (ob == null) {
/* 148 */         return null;
/*     */       }
/* 150 */       if (this._valueClass.isAssignableFrom(ob.getClass())) {
/* 151 */         return (T)ob;
/*     */       }
/* 153 */       return (T)_deserializeEmbedded(ob, ctxt);
/*     */     }
/* 155 */     return (T)ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */   }
/*     */   
/*     */   protected abstract T _deserialize(String paramString, DeserializationContext paramDeserializationContext) throws IOException;
/*     */   
/*     */   protected T _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException
/*     */   {
/* 162 */     ctxt.reportMappingException("Don't know how to convert embedded Object of type %s into %s", new Object[] { ob.getClass().getName(), this._valueClass.getName() });
/*     */     
/* 164 */     return null;
/*     */   }
/*     */   
/*     */   protected T _deserializeFromEmptyString() throws IOException {
/* 168 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class Std
/*     */     extends FromStringDeserializer<Object>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     public static final int STD_FILE = 1;
/*     */     
/*     */     public static final int STD_URL = 2;
/*     */     
/*     */     public static final int STD_URI = 3;
/*     */     
/*     */     public static final int STD_CLASS = 4;
/*     */     
/*     */     public static final int STD_JAVA_TYPE = 5;
/*     */     
/*     */     public static final int STD_CURRENCY = 6;
/*     */     
/*     */     public static final int STD_PATTERN = 7;
/*     */     
/*     */     public static final int STD_LOCALE = 8;
/*     */     
/*     */     public static final int STD_CHARSET = 9;
/*     */     
/*     */     public static final int STD_TIME_ZONE = 10;
/*     */     
/*     */     public static final int STD_INET_ADDRESS = 11;
/*     */     public static final int STD_INET_SOCKET_ADDRESS = 12;
/*     */     protected final int _kind;
/*     */     
/*     */     protected Std(Class<?> valueType, int kind)
/*     */     {
/* 203 */       super();
/* 204 */       this._kind = kind;
/*     */     }
/*     */     
/*     */     protected Object _deserialize(String value, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 210 */       switch (this._kind) {
/*     */       case 1: 
/* 212 */         return new File(value);
/*     */       case 2: 
/* 214 */         return new URL(value);
/*     */       case 3: 
/* 216 */         return URI.create(value);
/*     */       case 4: 
/*     */         try {
/* 219 */           return ctxt.findClass(value);
/*     */         } catch (Exception e) {
/* 221 */           return ctxt.handleInstantiationProblem(this._valueClass, value, ClassUtil.getRootCause(e));
/*     */         }
/*     */       
/*     */       case 5: 
/* 225 */         return ctxt.getTypeFactory().constructFromCanonical(value);
/*     */       
/*     */       case 6: 
/* 228 */         return Currency.getInstance(value);
/*     */       
/*     */       case 7: 
/* 231 */         return Pattern.compile(value);
/*     */       
/*     */       case 8: 
/* 234 */         int ix = value.indexOf('_');
/* 235 */         if (ix < 0) {
/* 236 */           return new Locale(value);
/*     */         }
/* 238 */         String first = value.substring(0, ix);
/* 239 */         value = value.substring(ix + 1);
/* 240 */         ix = value.indexOf('_');
/* 241 */         if (ix < 0) {
/* 242 */           return new Locale(first, value);
/*     */         }
/* 244 */         String second = value.substring(0, ix);
/* 245 */         return new Locale(first, second, value.substring(ix + 1));
/*     */       
/*     */       case 9: 
/* 248 */         return Charset.forName(value);
/*     */       case 10: 
/* 250 */         return TimeZone.getTimeZone(value);
/*     */       case 11: 
/* 252 */         return InetAddress.getByName(value);
/*     */       case 12: 
/* 254 */         if (value.startsWith("["))
/*     */         {
/*     */ 
/* 257 */           int i = value.lastIndexOf(']');
/* 258 */           if (i == -1) {
/* 259 */             throw new InvalidFormatException(ctxt.getParser(), "Bracketed IPv6 address must contain closing bracket", value, InetSocketAddress.class);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 264 */           int j = value.indexOf(':', i);
/* 265 */           int port = j > -1 ? Integer.parseInt(value.substring(j + 1)) : 0;
/* 266 */           return new InetSocketAddress(value.substring(0, i + 1), port);
/*     */         }
/* 268 */         int ix = value.indexOf(':');
/* 269 */         if ((ix >= 0) && (value.indexOf(':', ix + 1) < 0))
/*     */         {
/* 271 */           int port = Integer.parseInt(value.substring(ix + 1));
/* 272 */           return new InetSocketAddress(value.substring(0, ix), port);
/*     */         }
/*     */         
/* 275 */         return new InetSocketAddress(value, 0);
/*     */       }
/*     */       
/* 278 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*     */     protected Object _deserializeFromEmptyString()
/*     */       throws IOException
/*     */     {
/* 284 */       if (this._kind == 3) {
/* 285 */         return URI.create("");
/*     */       }
/*     */       
/* 288 */       if (this._kind == 8) {
/* 289 */         return Locale.ROOT;
/*     */       }
/* 291 */       return super._deserializeFromEmptyString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\FromStringDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */