/*     */ package com.facebook.presto.jdbc.internal.jackson.core.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonStreamContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.PrettyPrinter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.TreeNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.json.DupDetector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.json.JsonWriteContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.json.PackageVersion;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.util.DefaultPrettyPrinter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GeneratorBase
/*     */   extends JsonGenerator
/*     */ {
/*     */   public static final int SURR1_FIRST = 55296;
/*     */   public static final int SURR1_LAST = 56319;
/*     */   public static final int SURR2_FIRST = 56320;
/*     */   public static final int SURR2_LAST = 57343;
/*  29 */   protected static final int DERIVED_FEATURES_MASK = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.getMask() | JsonGenerator.Feature.ESCAPE_NON_ASCII.getMask() | JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.getMask();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  37 */   protected final String WRITE_BINARY = "write a binary value";
/*  38 */   protected final String WRITE_BOOLEAN = "write a boolean value";
/*  39 */   protected final String WRITE_NULL = "write a null";
/*  40 */   protected final String WRITE_NUMBER = "write a number";
/*  41 */   protected final String WRITE_RAW = "write a raw (unencoded) value";
/*  42 */   protected final String WRITE_STRING = "write a string";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ObjectCodec _objectCodec;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _features;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _cfgNumbersAsStrings;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonWriteContext _writeContext;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _closed;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GeneratorBase(int features, ObjectCodec codec)
/*     */   {
/*  93 */     this._features = features;
/*  94 */     this._objectCodec = codec;
/*  95 */     DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
/*     */     
/*  97 */     this._writeContext = JsonWriteContext.createRootContext(dups);
/*  98 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt)
/*     */   {
/* 106 */     this._features = features;
/* 107 */     this._objectCodec = codec;
/* 108 */     this._writeContext = ctxt;
/* 109 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Version version()
/*     */   {
/* 117 */     return PackageVersion.VERSION;
/*     */   }
/*     */   
/*     */   public Object getCurrentValue() {
/* 121 */     return this._writeContext.getCurrentValue();
/*     */   }
/*     */   
/*     */   public void setCurrentValue(Object v)
/*     */   {
/* 126 */     this._writeContext.setCurrentValue(v);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */   public final boolean isEnabled(JsonGenerator.Feature f) { return (this._features & f.getMask()) != 0; }
/* 137 */   public int getFeatureMask() { return this._features; }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonGenerator enable(JsonGenerator.Feature f)
/*     */   {
/* 143 */     int mask = f.getMask();
/* 144 */     this._features |= mask;
/* 145 */     if ((mask & DERIVED_FEATURES_MASK) != 0)
/*     */     {
/* 147 */       if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 148 */         this._cfgNumbersAsStrings = true;
/* 149 */       } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 150 */         setHighestNonEscapedChar(127);
/* 151 */       } else if ((f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) && 
/* 152 */         (this._writeContext.getDupDetector() == null)) {
/* 153 */         this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
/*     */       }
/*     */     }
/*     */     
/* 157 */     return this;
/*     */   }
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f)
/*     */   {
/* 162 */     int mask = f.getMask();
/* 163 */     this._features &= (mask ^ 0xFFFFFFFF);
/* 164 */     if ((mask & DERIVED_FEATURES_MASK) != 0) {
/* 165 */       if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 166 */         this._cfgNumbersAsStrings = false;
/* 167 */       } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 168 */         setHighestNonEscapedChar(0);
/* 169 */       } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) {
/* 170 */         this._writeContext = this._writeContext.withDupDetector(null);
/*     */       }
/*     */     }
/* 173 */     return this;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonGenerator setFeatureMask(int newMask)
/*     */   {
/* 179 */     int changed = newMask ^ this._features;
/* 180 */     this._features = newMask;
/* 181 */     if (changed != 0) {
/* 182 */       _checkStdFeatureChanges(newMask, changed);
/*     */     }
/* 184 */     return this;
/*     */   }
/*     */   
/*     */   public JsonGenerator overrideStdFeatures(int values, int mask)
/*     */   {
/* 189 */     int oldState = this._features;
/* 190 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/* 191 */     int changed = oldState ^ newState;
/* 192 */     if (changed != 0) {
/* 193 */       this._features = newState;
/* 194 */       _checkStdFeatureChanges(newState, changed);
/*     */     }
/* 196 */     return this;
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
/*     */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures)
/*     */   {
/* 210 */     if ((changedFeatures & DERIVED_FEATURES_MASK) == 0) {
/* 211 */       return;
/*     */     }
/* 213 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(newFeatureFlags);
/* 214 */     if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(changedFeatures)) {
/* 215 */       if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(newFeatureFlags)) {
/* 216 */         setHighestNonEscapedChar(127);
/*     */       } else {
/* 218 */         setHighestNonEscapedChar(0);
/*     */       }
/*     */     }
/* 221 */     if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(changedFeatures)) {
/* 222 */       if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(newFeatureFlags)) {
/* 223 */         if (this._writeContext.getDupDetector() == null) {
/* 224 */           this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
/*     */         }
/*     */       } else {
/* 227 */         this._writeContext = this._writeContext.withDupDetector(null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public JsonGenerator useDefaultPrettyPrinter()
/*     */   {
/* 234 */     if (getPrettyPrinter() != null) {
/* 235 */       return this;
/*     */     }
/* 237 */     return setPrettyPrinter(_constructDefaultPrettyPrinter());
/*     */   }
/*     */   
/*     */   public JsonGenerator setCodec(ObjectCodec oc) {
/* 241 */     this._objectCodec = oc;
/* 242 */     return this;
/*     */   }
/*     */   
/* 245 */   public ObjectCodec getCodec() { return this._objectCodec; }
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
/*     */   public JsonStreamContext getOutputContext()
/*     */   {
/* 258 */     return this._writeContext;
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
/*     */   public void writeStartObject(Object forValue)
/*     */     throws IOException
/*     */   {
/* 274 */     writeStartObject();
/* 275 */     if ((this._writeContext != null) && (forValue != null)) {
/* 276 */       this._writeContext.setCurrentValue(forValue);
/*     */     }
/* 278 */     setCurrentValue(forValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeFieldName(SerializableString name)
/*     */     throws IOException
/*     */   {
/* 288 */     writeFieldName(name.getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeString(SerializableString text)
/*     */     throws IOException
/*     */   {
/* 301 */     writeString(text.getValue());
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text) throws IOException {
/* 305 */     _verifyValueWrite("write raw value");
/* 306 */     writeRaw(text);
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text, int offset, int len) throws IOException {
/* 310 */     _verifyValueWrite("write raw value");
/* 311 */     writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 315 */     _verifyValueWrite("write raw value");
/* 316 */     writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRawValue(SerializableString text) throws IOException {
/* 320 */     _verifyValueWrite("write raw value");
/* 321 */     writeRaw(text);
/*     */   }
/*     */   
/*     */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
/*     */     throws IOException
/*     */   {
/* 327 */     _reportUnsupportedOperation();
/* 328 */     return 0;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeObject(Object value)
/*     */     throws IOException
/*     */   {
/* 357 */     if (value == null)
/*     */     {
/* 359 */       writeNull();
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 366 */       if (this._objectCodec != null) {
/* 367 */         this._objectCodec.writeValue(this, value);
/* 368 */         return;
/*     */       }
/* 370 */       _writeSimpleObject(value);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeTree(TreeNode rootNode)
/*     */     throws IOException
/*     */   {
/* 377 */     if (rootNode == null) {
/* 378 */       writeNull();
/*     */     } else {
/* 380 */       if (this._objectCodec == null) {
/* 381 */         throw new IllegalStateException("No ObjectCodec defined");
/*     */       }
/* 383 */       this._objectCodec.writeValue(this, rootNode);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void flush()
/*     */     throws IOException;
/*     */   
/*     */ 
/* 394 */   public void close()
/* 394 */     throws IOException { this._closed = true; }
/* 395 */   public boolean isClosed() { return this._closed; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void _releaseBuffers();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void _verifyValueWrite(String paramString)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PrettyPrinter _constructDefaultPrettyPrinter()
/*     */   {
/* 426 */     return new DefaultPrettyPrinter();
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
/*     */   protected final int _decodeSurrogate(int surr1, int surr2)
/*     */     throws IOException
/*     */   {
/* 441 */     if ((surr2 < 56320) || (surr2 > 57343)) {
/* 442 */       String msg = "Incomplete surrogate pair: first char 0x" + Integer.toHexString(surr1) + ", second 0x" + Integer.toHexString(surr2);
/* 443 */       _reportError(msg);
/*     */     }
/* 445 */     int c = 65536 + (surr1 - 55296 << 10) + (surr2 - 56320);
/* 446 */     return c;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\base\GeneratorBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */