/*     */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Versioned;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.PackageVersion;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.DefaultSerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.TypeWrappedSerializer;
/*     */ import java.io.Closeable;
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SequenceWriter
/*     */   implements Versioned, Closeable, Flushable
/*     */ {
/*     */   protected final DefaultSerializerProvider _provider;
/*     */   protected final SerializationConfig _config;
/*     */   protected final JsonGenerator _generator;
/*     */   protected final JsonSerializer<Object> _rootSerializer;
/*     */   protected final TypeSerializer _typeSerializer;
/*     */   protected final boolean _closeGenerator;
/*     */   protected final boolean _cfgFlush;
/*     */   protected final boolean _cfgCloseCloseable;
/*     */   protected PropertySerializerMap _dynamicSerializers;
/*     */   protected boolean _openArray;
/*     */   protected boolean _closed;
/*     */   
/*     */   public SequenceWriter(DefaultSerializerProvider prov, JsonGenerator gen, boolean closeGenerator, ObjectWriter.Prefetch prefetch)
/*     */     throws IOException
/*     */   {
/*  82 */     this._provider = prov;
/*  83 */     this._generator = gen;
/*  84 */     this._closeGenerator = closeGenerator;
/*  85 */     this._rootSerializer = prefetch.getValueSerializer();
/*  86 */     this._typeSerializer = prefetch.getTypeSerializer();
/*     */     
/*  88 */     this._config = prov.getConfig();
/*  89 */     this._cfgFlush = this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
/*  90 */     this._cfgCloseCloseable = this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE);
/*     */     
/*     */ 
/*  93 */     this._dynamicSerializers = PropertySerializerMap.emptyForRootValues();
/*     */   }
/*     */   
/*     */   public SequenceWriter init(boolean wrapInArray) throws IOException
/*     */   {
/*  98 */     if (wrapInArray) {
/*  99 */       this._generator.writeStartArray();
/* 100 */       this._openArray = true;
/*     */     }
/* 102 */     return this;
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
/*     */   public Version version()
/*     */   {
/* 117 */     return PackageVersion.VERSION;
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
/*     */   public SequenceWriter write(Object value)
/*     */     throws IOException
/*     */   {
/* 133 */     if (value == null) {
/* 134 */       this._provider.serializeValue(this._generator, null);
/* 135 */       return this;
/*     */     }
/*     */     
/* 138 */     if ((this._cfgCloseCloseable) && ((value instanceof Closeable))) {
/* 139 */       return _writeCloseableValue(value);
/*     */     }
/* 141 */     JsonSerializer<Object> ser = this._rootSerializer;
/* 142 */     if (ser == null) {
/* 143 */       Class<?> type = value.getClass();
/* 144 */       ser = this._dynamicSerializers.serializerFor(type);
/* 145 */       if (ser == null) {
/* 146 */         ser = _findAndAddDynamic(type);
/*     */       }
/*     */     }
/* 149 */     this._provider.serializeValue(this._generator, value, null, ser);
/* 150 */     if (this._cfgFlush) {
/* 151 */       this._generator.flush();
/*     */     }
/* 153 */     return this;
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
/*     */   public SequenceWriter write(Object value, JavaType type)
/*     */     throws IOException
/*     */   {
/* 167 */     if (value == null) {
/* 168 */       this._provider.serializeValue(this._generator, null);
/* 169 */       return this;
/*     */     }
/*     */     
/* 172 */     if ((this._cfgCloseCloseable) && ((value instanceof Closeable))) {
/* 173 */       return _writeCloseableValue(value, type);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 180 */     JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
/* 181 */     if (ser == null) {
/* 182 */       ser = _findAndAddDynamic(type);
/*     */     }
/* 184 */     this._provider.serializeValue(this._generator, value, type, ser);
/* 185 */     if (this._cfgFlush) {
/* 186 */       this._generator.flush();
/*     */     }
/* 188 */     return this;
/*     */   }
/*     */   
/*     */   public SequenceWriter writeAll(Object[] value) throws IOException
/*     */   {
/* 193 */     int i = 0; for (int len = value.length; i < len; i++) {
/* 194 */       write(value[i]);
/*     */     }
/* 196 */     return this;
/*     */   }
/*     */   
/*     */   public <C extends Collection<?>> SequenceWriter writeAll(C container)
/*     */     throws IOException
/*     */   {
/* 202 */     for (Object value : container) {
/* 203 */       write(value);
/*     */     }
/* 205 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SequenceWriter writeAll(Iterable<?> iterable)
/*     */     throws IOException
/*     */   {
/* 213 */     for (Object value : iterable) {
/* 214 */       write(value);
/*     */     }
/* 216 */     return this;
/*     */   }
/*     */   
/*     */   public void flush() throws IOException
/*     */   {
/* 221 */     if (!this._closed) {
/* 222 */       this._generator.flush();
/*     */     }
/*     */   }
/*     */   
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 229 */     if (!this._closed) {
/* 230 */       this._closed = true;
/* 231 */       if (this._openArray) {
/* 232 */         this._openArray = false;
/* 233 */         this._generator.writeEndArray();
/*     */       }
/* 235 */       if (this._closeGenerator) {
/* 236 */         this._generator.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SequenceWriter _writeCloseableValue(Object value)
/*     */     throws IOException
/*     */   {
/* 249 */     Closeable toClose = (Closeable)value;
/*     */     try {
/* 251 */       JsonSerializer<Object> ser = this._rootSerializer;
/* 252 */       if (ser == null) {
/* 253 */         Class<?> type = value.getClass();
/* 254 */         ser = this._dynamicSerializers.serializerFor(type);
/* 255 */         if (ser == null) {
/* 256 */           ser = _findAndAddDynamic(type);
/*     */         }
/*     */       }
/* 259 */       this._provider.serializeValue(this._generator, value, null, ser);
/* 260 */       if (this._cfgFlush) {
/* 261 */         this._generator.flush();
/*     */       }
/* 263 */       Closeable tmpToClose = toClose;
/* 264 */       toClose = null;
/* 265 */       tmpToClose.close();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 273 */       return this;
/*     */     }
/*     */     finally
/*     */     {
/* 267 */       if (toClose != null) {
/*     */         try {
/* 269 */           toClose.close();
/*     */         }
/*     */         catch (IOException ioe) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected SequenceWriter _writeCloseableValue(Object value, JavaType type) throws IOException
/*     */   {
/* 278 */     Closeable toClose = (Closeable)value;
/*     */     try
/*     */     {
/* 281 */       JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
/* 282 */       if (ser == null) {
/* 283 */         ser = _findAndAddDynamic(type);
/*     */       }
/* 285 */       this._provider.serializeValue(this._generator, value, type, ser);
/* 286 */       if (this._cfgFlush) {
/* 287 */         this._generator.flush();
/*     */       }
/* 289 */       Closeable tmpToClose = toClose;
/* 290 */       toClose = null;
/* 291 */       tmpToClose.close();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 299 */       return this;
/*     */     }
/*     */     finally
/*     */     {
/* 293 */       if (toClose != null) {
/*     */         try {
/* 295 */           toClose.close();
/*     */         }
/*     */         catch (IOException ioe) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private final JsonSerializer<Object> _findAndAddDynamic(Class<?> type) throws JsonMappingException {
/*     */     SerializerAndMapResult result;
/*     */     SerializerAndMapResult result;
/* 305 */     if (this._typeSerializer == null) {
/* 306 */       result = this._dynamicSerializers.findAndAddRootValueSerializer(type, this._provider);
/*     */     } else {
/* 308 */       result = this._dynamicSerializers.addSerializer(type, new TypeWrappedSerializer(this._typeSerializer, this._provider.findValueSerializer(type, null)));
/*     */     }
/*     */     
/* 311 */     this._dynamicSerializers = result.map;
/* 312 */     return result.serializer;
/*     */   }
/*     */   
/*     */   private final JsonSerializer<Object> _findAndAddDynamic(JavaType type) throws JsonMappingException {
/*     */     SerializerAndMapResult result;
/*     */     SerializerAndMapResult result;
/* 318 */     if (this._typeSerializer == null) {
/* 319 */       result = this._dynamicSerializers.findAndAddRootValueSerializer(type, this._provider);
/*     */     } else {
/* 321 */       result = this._dynamicSerializers.addSerializer(type, new TypeWrappedSerializer(this._typeSerializer, this._provider.findValueSerializer(type, null)));
/*     */     }
/*     */     
/* 324 */     this._dynamicSerializers = result.map;
/* 325 */     return result.serializer;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\SequenceWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */