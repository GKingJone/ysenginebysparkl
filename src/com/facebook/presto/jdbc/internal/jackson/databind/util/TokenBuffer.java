/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParseException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonStreamContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.TreeNode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.base.ParserMinimalBase;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.json.JsonReadContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.json.JsonWriteContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.ByteArrayBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.PackageVersion;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class TokenBuffer extends JsonGenerator
/*      */ {
/*   33 */   protected static final int DEFAULT_GENERATOR_FEATURES = ;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectCodec _objectCodec;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _generatorFeatures;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _closed;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _hasNativeTypeIds;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _hasNativeObjectIds;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _mayHaveNativeIds;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _forceBigDecimal;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Segment _first;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Segment _last;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _appendAt;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object _typeId;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object _objectId;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  119 */   protected boolean _hasNativeId = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonWriteContext _writeContext;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public TokenBuffer(ObjectCodec codec)
/*      */   {
/*  144 */     this(codec, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TokenBuffer(ObjectCodec codec, boolean hasNativeIds)
/*      */   {
/*  156 */     this._objectCodec = codec;
/*  157 */     this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
/*  158 */     this._writeContext = JsonWriteContext.createRootContext(null);
/*      */     
/*  160 */     this._first = (this._last = new Segment());
/*  161 */     this._appendAt = 0;
/*  162 */     this._hasNativeTypeIds = hasNativeIds;
/*  163 */     this._hasNativeObjectIds = hasNativeIds;
/*      */     
/*  165 */     this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TokenBuffer(JsonParser p)
/*      */   {
/*  172 */     this(p, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TokenBuffer(JsonParser p, DeserializationContext ctxt)
/*      */   {
/*  180 */     this._objectCodec = p.getCodec();
/*  181 */     this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
/*  182 */     this._writeContext = JsonWriteContext.createRootContext(null);
/*      */     
/*  184 */     this._first = (this._last = new Segment());
/*  185 */     this._appendAt = 0;
/*  186 */     this._hasNativeTypeIds = p.canReadTypeId();
/*  187 */     this._hasNativeObjectIds = p.canReadObjectId();
/*  188 */     this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
/*  189 */     this._forceBigDecimal = (ctxt == null ? false : ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TokenBuffer forceUseOfBigDecimal(boolean b)
/*      */   {
/*  197 */     this._forceBigDecimal = b;
/*  198 */     return this;
/*      */   }
/*      */   
/*      */   public Version version()
/*      */   {
/*  203 */     return PackageVersion.VERSION;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonParser asParser()
/*      */   {
/*  218 */     return asParser(this._objectCodec);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonParser asParser(ObjectCodec codec)
/*      */   {
/*  236 */     return new Parser(this._first, codec, this._hasNativeTypeIds, this._hasNativeObjectIds);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonParser asParser(JsonParser src)
/*      */   {
/*  245 */     Parser p = new Parser(this._first, src.getCodec(), this._hasNativeTypeIds, this._hasNativeObjectIds);
/*  246 */     p.setLocation(src.getTokenLocation());
/*  247 */     return p;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonToken firstToken()
/*      */   {
/*  257 */     if (this._first != null) {
/*  258 */       return this._first.type(0);
/*      */     }
/*  260 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TokenBuffer append(TokenBuffer other)
/*      */     throws IOException
/*      */   {
/*  280 */     if (!this._hasNativeTypeIds) {
/*  281 */       this._hasNativeTypeIds = other.canWriteTypeId();
/*      */     }
/*  283 */     if (!this._hasNativeObjectIds) {
/*  284 */       this._hasNativeObjectIds = other.canWriteObjectId();
/*      */     }
/*  286 */     this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
/*      */     
/*  288 */     JsonParser p = other.asParser();
/*  289 */     while (p.nextToken() != null) {
/*  290 */       copyCurrentStructure(p);
/*      */     }
/*  292 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serialize(JsonGenerator gen)
/*      */     throws IOException
/*      */   {
/*  307 */     Segment segment = this._first;
/*  308 */     int ptr = -1;
/*      */     
/*  310 */     boolean checkIds = this._mayHaveNativeIds;
/*  311 */     boolean hasIds = (checkIds) && (segment.hasIds());
/*      */     for (;;)
/*      */     {
/*  314 */       ptr++; if (ptr >= 16) {
/*  315 */         ptr = 0;
/*  316 */         segment = segment.next();
/*  317 */         if (segment == null) break;
/*  318 */         hasIds = (checkIds) && (segment.hasIds());
/*      */       }
/*  320 */       JsonToken t = segment.type(ptr);
/*  321 */       if (t == null)
/*      */         break;
/*  323 */       if (hasIds) {
/*  324 */         Object id = segment.findObjectId(ptr);
/*  325 */         if (id != null) {
/*  326 */           gen.writeObjectId(id);
/*      */         }
/*  328 */         id = segment.findTypeId(ptr);
/*  329 */         if (id != null) {
/*  330 */           gen.writeTypeId(id);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  335 */       switch (t) {
/*      */       case START_OBJECT: 
/*  337 */         gen.writeStartObject();
/*  338 */         break;
/*      */       case END_OBJECT: 
/*  340 */         gen.writeEndObject();
/*  341 */         break;
/*      */       case START_ARRAY: 
/*  343 */         gen.writeStartArray();
/*  344 */         break;
/*      */       case END_ARRAY: 
/*  346 */         gen.writeEndArray();
/*  347 */         break;
/*      */       
/*      */ 
/*      */       case FIELD_NAME: 
/*  351 */         Object ob = segment.get(ptr);
/*  352 */         if ((ob instanceof SerializableString)) {
/*  353 */           gen.writeFieldName((SerializableString)ob);
/*      */         } else {
/*  355 */           gen.writeFieldName((String)ob);
/*      */         }
/*      */         
/*  358 */         break;
/*      */       
/*      */       case VALUE_STRING: 
/*  361 */         Object ob = segment.get(ptr);
/*  362 */         if ((ob instanceof SerializableString)) {
/*  363 */           gen.writeString((SerializableString)ob);
/*      */         } else {
/*  365 */           gen.writeString((String)ob);
/*      */         }
/*      */         
/*  368 */         break;
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*  371 */         Object n = segment.get(ptr);
/*  372 */         if ((n instanceof Integer)) {
/*  373 */           gen.writeNumber(((Integer)n).intValue());
/*  374 */         } else if ((n instanceof BigInteger)) {
/*  375 */           gen.writeNumber((BigInteger)n);
/*  376 */         } else if ((n instanceof Long)) {
/*  377 */           gen.writeNumber(((Long)n).longValue());
/*  378 */         } else if ((n instanceof Short)) {
/*  379 */           gen.writeNumber(((Short)n).shortValue());
/*      */         } else {
/*  381 */           gen.writeNumber(((Number)n).intValue());
/*      */         }
/*      */         
/*  384 */         break;
/*      */       
/*      */       case VALUE_NUMBER_FLOAT: 
/*  387 */         Object n = segment.get(ptr);
/*  388 */         if ((n instanceof Double)) {
/*  389 */           gen.writeNumber(((Double)n).doubleValue());
/*  390 */         } else if ((n instanceof BigDecimal)) {
/*  391 */           gen.writeNumber((BigDecimal)n);
/*  392 */         } else if ((n instanceof Float)) {
/*  393 */           gen.writeNumber(((Float)n).floatValue());
/*  394 */         } else if (n == null) {
/*  395 */           gen.writeNull();
/*  396 */         } else if ((n instanceof String)) {
/*  397 */           gen.writeNumber((String)n);
/*      */         } else {
/*  399 */           throw new JsonGenerationException(String.format("Unrecognized value type for VALUE_NUMBER_FLOAT: %s, can not serialize", new Object[] { n.getClass().getName() }), gen);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  404 */         break;
/*      */       case VALUE_TRUE: 
/*  406 */         gen.writeBoolean(true);
/*  407 */         break;
/*      */       case VALUE_FALSE: 
/*  409 */         gen.writeBoolean(false);
/*  410 */         break;
/*      */       case VALUE_NULL: 
/*  412 */         gen.writeNull();
/*  413 */         break;
/*      */       
/*      */       case VALUE_EMBEDDED_OBJECT: 
/*  416 */         Object value = segment.get(ptr);
/*  417 */         if ((value instanceof RawValue)) {
/*  418 */           ((RawValue)value).serialize(gen);
/*      */         } else {
/*  420 */           gen.writeObject(value);
/*      */         }
/*      */         
/*  423 */         break;
/*      */       default: 
/*  425 */         throw new RuntimeException("Internal error: should never end up through this code path");
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  437 */     if (p.getCurrentTokenId() != JsonToken.FIELD_NAME.id()) {
/*  438 */       copyCurrentStructure(p);
/*  439 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  446 */     writeStartObject();
/*      */     JsonToken t;
/*  448 */     do { copyCurrentStructure(p);
/*  449 */     } while ((t = p.nextToken()) == JsonToken.FIELD_NAME);
/*  450 */     if (t != JsonToken.END_OBJECT) {
/*  451 */       ctxt.reportWrongTokenException(p, JsonToken.END_OBJECT, "Expected END_OBJECT after copying contents of a JsonParser into TokenBuffer, got " + t, new Object[0]);
/*      */     }
/*      */     
/*      */ 
/*  455 */     writeEndObject();
/*  456 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  464 */     int MAX_COUNT = 100;
/*      */     
/*  466 */     StringBuilder sb = new StringBuilder();
/*  467 */     sb.append("[TokenBuffer: ");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  474 */     JsonParser jp = asParser();
/*  475 */     int count = 0;
/*  476 */     boolean hasNativeIds = (this._hasNativeTypeIds) || (this._hasNativeObjectIds);
/*      */     for (;;)
/*      */     {
/*      */       try
/*      */       {
/*  481 */         JsonToken t = jp.nextToken();
/*  482 */         if (t == null)
/*      */           break;
/*  484 */         if (hasNativeIds) {
/*  485 */           _appendNativeIds(sb);
/*      */         }
/*      */         
/*  488 */         if (count < 100) {
/*  489 */           if (count > 0) {
/*  490 */             sb.append(", ");
/*      */           }
/*  492 */           sb.append(t.toString());
/*  493 */           if (t == JsonToken.FIELD_NAME) {
/*  494 */             sb.append('(');
/*  495 */             sb.append(jp.getCurrentName());
/*  496 */             sb.append(')');
/*      */           }
/*      */         }
/*      */       } catch (IOException ioe) {
/*  500 */         throw new IllegalStateException(ioe);
/*      */       }
/*  502 */       count++;
/*      */     }
/*      */     
/*  505 */     if (count >= 100) {
/*  506 */       sb.append(" ... (truncated ").append(count - 100).append(" entries)");
/*      */     }
/*  508 */     sb.append(']');
/*  509 */     return sb.toString();
/*      */   }
/*      */   
/*      */   private final void _appendNativeIds(StringBuilder sb)
/*      */   {
/*  514 */     Object objectId = this._last.findObjectId(this._appendAt - 1);
/*  515 */     if (objectId != null) {
/*  516 */       sb.append("[objectId=").append(String.valueOf(objectId)).append(']');
/*      */     }
/*  518 */     Object typeId = this._last.findTypeId(this._appendAt - 1);
/*  519 */     if (typeId != null) {
/*  520 */       sb.append("[typeId=").append(String.valueOf(typeId)).append(']');
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonGenerator enable(Feature f)
/*      */   {
/*  532 */     this._generatorFeatures |= f.getMask();
/*  533 */     return this;
/*      */   }
/*      */   
/*      */   public JsonGenerator disable(Feature f)
/*      */   {
/*  538 */     this._generatorFeatures &= (f.getMask() ^ 0xFFFFFFFF);
/*  539 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isEnabled(Feature f)
/*      */   {
/*  546 */     return (this._generatorFeatures & f.getMask()) != 0;
/*      */   }
/*      */   
/*      */   public int getFeatureMask()
/*      */   {
/*  551 */     return this._generatorFeatures;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public JsonGenerator setFeatureMask(int mask)
/*      */   {
/*  557 */     this._generatorFeatures = mask;
/*  558 */     return this;
/*      */   }
/*      */   
/*      */   public JsonGenerator overrideStdFeatures(int values, int mask)
/*      */   {
/*  563 */     int oldState = getFeatureMask();
/*  564 */     this._generatorFeatures = (oldState & (mask ^ 0xFFFFFFFF) | values & mask);
/*  565 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   public JsonGenerator useDefaultPrettyPrinter()
/*      */   {
/*  571 */     return this;
/*      */   }
/*      */   
/*      */   public JsonGenerator setCodec(ObjectCodec oc)
/*      */   {
/*  576 */     this._objectCodec = oc;
/*  577 */     return this;
/*      */   }
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  581 */     return this._objectCodec;
/*      */   }
/*      */   
/*  584 */   public final JsonWriteContext getOutputContext() { return this._writeContext; }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canWriteBinaryNatively()
/*      */   {
/*  597 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void flush()
/*      */     throws IOException
/*      */   {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  611 */     this._closed = true;
/*      */   }
/*      */   
/*      */   public boolean isClosed() {
/*  615 */     return this._closed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeStartArray()
/*      */     throws IOException
/*      */   {
/*  626 */     this._writeContext.writeValue();
/*  627 */     _append(JsonToken.START_ARRAY);
/*  628 */     this._writeContext = this._writeContext.createChildArrayContext();
/*      */   }
/*      */   
/*      */   public final void writeEndArray()
/*      */     throws IOException
/*      */   {
/*  634 */     _append(JsonToken.END_ARRAY);
/*      */     
/*  636 */     JsonWriteContext c = this._writeContext.getParent();
/*  637 */     if (c != null) {
/*  638 */       this._writeContext = c;
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeStartObject()
/*      */     throws IOException
/*      */   {
/*  645 */     this._writeContext.writeValue();
/*  646 */     _append(JsonToken.START_OBJECT);
/*  647 */     this._writeContext = this._writeContext.createChildObjectContext();
/*      */   }
/*      */   
/*      */   public void writeStartObject(Object forValue)
/*      */     throws IOException
/*      */   {
/*  653 */     this._writeContext.writeValue();
/*  654 */     _append(JsonToken.START_OBJECT);
/*  655 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext();
/*  656 */     this._writeContext = ctxt;
/*  657 */     if (forValue != null) {
/*  658 */       ctxt.setCurrentValue(forValue);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeEndObject()
/*      */     throws IOException
/*      */   {
/*  665 */     _append(JsonToken.END_OBJECT);
/*      */     
/*  667 */     JsonWriteContext c = this._writeContext.getParent();
/*  668 */     if (c != null) {
/*  669 */       this._writeContext = c;
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeFieldName(String name)
/*      */     throws IOException
/*      */   {
/*  676 */     this._writeContext.writeFieldName(name);
/*  677 */     _append(JsonToken.FIELD_NAME, name);
/*      */   }
/*      */   
/*      */   public void writeFieldName(SerializableString name)
/*      */     throws IOException
/*      */   {
/*  683 */     this._writeContext.writeFieldName(name.getValue());
/*  684 */     _append(JsonToken.FIELD_NAME, name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeString(String text)
/*      */     throws IOException
/*      */   {
/*  695 */     if (text == null) {
/*  696 */       writeNull();
/*      */     } else {
/*  698 */       _appendValue(JsonToken.VALUE_STRING, text);
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException
/*      */   {
/*  704 */     writeString(new String(text, offset, len));
/*      */   }
/*      */   
/*      */   public void writeString(SerializableString text) throws IOException
/*      */   {
/*  709 */     if (text == null) {
/*  710 */       writeNull();
/*      */     } else {
/*  712 */       _appendValue(JsonToken.VALUE_STRING, text);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  720 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  727 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(String text) throws IOException
/*      */   {
/*  732 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(String text, int offset, int len) throws IOException
/*      */   {
/*  737 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(SerializableString text) throws IOException
/*      */   {
/*  742 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(char[] text, int offset, int len) throws IOException
/*      */   {
/*  747 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(char c) throws IOException
/*      */   {
/*  752 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRawValue(String text) throws IOException
/*      */   {
/*  757 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
/*      */   }
/*      */   
/*      */   public void writeRawValue(String text, int offset, int len) throws IOException
/*      */   {
/*  762 */     if ((offset > 0) || (len != text.length())) {
/*  763 */       text = text.substring(offset, offset + len);
/*      */     }
/*  765 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
/*      */   }
/*      */   
/*      */   public void writeRawValue(char[] text, int offset, int len) throws IOException
/*      */   {
/*  770 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new String(text, offset, len));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeNumber(short i)
/*      */     throws IOException
/*      */   {
/*  781 */     _appendValue(JsonToken.VALUE_NUMBER_INT, Short.valueOf(i));
/*      */   }
/*      */   
/*      */   public void writeNumber(int i) throws IOException
/*      */   {
/*  786 */     _appendValue(JsonToken.VALUE_NUMBER_INT, Integer.valueOf(i));
/*      */   }
/*      */   
/*      */   public void writeNumber(long l) throws IOException
/*      */   {
/*  791 */     _appendValue(JsonToken.VALUE_NUMBER_INT, Long.valueOf(l));
/*      */   }
/*      */   
/*      */   public void writeNumber(double d) throws IOException
/*      */   {
/*  796 */     _appendValue(JsonToken.VALUE_NUMBER_FLOAT, Double.valueOf(d));
/*      */   }
/*      */   
/*      */   public void writeNumber(float f) throws IOException
/*      */   {
/*  801 */     _appendValue(JsonToken.VALUE_NUMBER_FLOAT, Float.valueOf(f));
/*      */   }
/*      */   
/*      */   public void writeNumber(BigDecimal dec) throws IOException
/*      */   {
/*  806 */     if (dec == null) {
/*  807 */       writeNull();
/*      */     } else {
/*  809 */       _appendValue(JsonToken.VALUE_NUMBER_FLOAT, dec);
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeNumber(BigInteger v) throws IOException
/*      */   {
/*  815 */     if (v == null) {
/*  816 */       writeNull();
/*      */     } else {
/*  818 */       _appendValue(JsonToken.VALUE_NUMBER_INT, v);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeNumber(String encodedValue)
/*      */     throws IOException
/*      */   {
/*  827 */     _appendValue(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
/*      */   }
/*      */   
/*      */   public void writeBoolean(boolean state) throws IOException
/*      */   {
/*  832 */     _appendValue(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
/*      */   }
/*      */   
/*      */   public void writeNull() throws IOException
/*      */   {
/*  837 */     _appendValue(JsonToken.VALUE_NULL);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeObject(Object value)
/*      */     throws IOException
/*      */   {
/*  849 */     if (value == null) {
/*  850 */       writeNull();
/*  851 */       return;
/*      */     }
/*  853 */     Class<?> raw = value.getClass();
/*  854 */     if ((raw == byte[].class) || ((value instanceof RawValue))) {
/*  855 */       _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
/*  856 */       return;
/*      */     }
/*  858 */     if (this._objectCodec == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  863 */       _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
/*      */     } else {
/*  865 */       this._objectCodec.writeValue(this, value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeTree(TreeNode node)
/*      */     throws IOException
/*      */   {
/*  872 */     if (node == null) {
/*  873 */       writeNull();
/*  874 */       return;
/*      */     }
/*      */     
/*  877 */     if (this._objectCodec == null)
/*      */     {
/*  879 */       _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, node);
/*      */     } else {
/*  881 */       this._objectCodec.writeTree(this, node);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
/*      */     throws IOException
/*      */   {
/*  900 */     byte[] copy = new byte[len];
/*  901 */     System.arraycopy(data, offset, copy, 0, len);
/*  902 */     writeObject(copy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
/*      */   {
/*  913 */     throw new UnsupportedOperationException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canWriteTypeId()
/*      */   {
/*  924 */     return this._hasNativeTypeIds;
/*      */   }
/*      */   
/*      */   public boolean canWriteObjectId()
/*      */   {
/*  929 */     return this._hasNativeObjectIds;
/*      */   }
/*      */   
/*      */   public void writeTypeId(Object id)
/*      */   {
/*  934 */     this._typeId = id;
/*  935 */     this._hasNativeId = true;
/*      */   }
/*      */   
/*      */   public void writeObjectId(Object id)
/*      */   {
/*  940 */     this._objectId = id;
/*  941 */     this._hasNativeId = true;
/*      */   }
/*      */   
/*      */   public void writeEmbeddedObject(Object object) throws IOException
/*      */   {
/*  946 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, object);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyCurrentEvent(JsonParser p)
/*      */     throws IOException
/*      */   {
/*  958 */     if (this._mayHaveNativeIds) {
/*  959 */       _checkNativeIds(p);
/*      */     }
/*  961 */     switch (p.getCurrentToken()) {
/*      */     case START_OBJECT: 
/*  963 */       writeStartObject();
/*  964 */       break;
/*      */     case END_OBJECT: 
/*  966 */       writeEndObject();
/*  967 */       break;
/*      */     case START_ARRAY: 
/*  969 */       writeStartArray();
/*  970 */       break;
/*      */     case END_ARRAY: 
/*  972 */       writeEndArray();
/*  973 */       break;
/*      */     case FIELD_NAME: 
/*  975 */       writeFieldName(p.getCurrentName());
/*  976 */       break;
/*      */     case VALUE_STRING: 
/*  978 */       if (p.hasTextCharacters()) {
/*  979 */         writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
/*      */       } else {
/*  981 */         writeString(p.getText());
/*      */       }
/*  983 */       break;
/*      */     case VALUE_NUMBER_INT: 
/*  985 */       switch (p.getNumberType()) {
/*      */       case INT: 
/*  987 */         writeNumber(p.getIntValue());
/*  988 */         break;
/*      */       case BIG_INTEGER: 
/*  990 */         writeNumber(p.getBigIntegerValue());
/*  991 */         break;
/*      */       default: 
/*  993 */         writeNumber(p.getLongValue());
/*      */       }
/*  995 */       break;
/*      */     case VALUE_NUMBER_FLOAT: 
/*  997 */       if (this._forceBigDecimal)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1003 */         writeNumber(p.getDecimalValue());
/*      */       } else {
/* 1005 */         switch (p.getNumberType()) {
/*      */         case BIG_DECIMAL: 
/* 1007 */           writeNumber(p.getDecimalValue());
/* 1008 */           break;
/*      */         case FLOAT: 
/* 1010 */           writeNumber(p.getFloatValue());
/* 1011 */           break;
/*      */         default: 
/* 1013 */           writeNumber(p.getDoubleValue());
/*      */         }
/*      */       }
/* 1016 */       break;
/*      */     case VALUE_TRUE: 
/* 1018 */       writeBoolean(true);
/* 1019 */       break;
/*      */     case VALUE_FALSE: 
/* 1021 */       writeBoolean(false);
/* 1022 */       break;
/*      */     case VALUE_NULL: 
/* 1024 */       writeNull();
/* 1025 */       break;
/*      */     case VALUE_EMBEDDED_OBJECT: 
/* 1027 */       writeObject(p.getEmbeddedObject());
/* 1028 */       break;
/*      */     default: 
/* 1030 */       throw new RuntimeException("Internal error: should never end up through this code path");
/*      */     }
/*      */   }
/*      */   
/*      */   public void copyCurrentStructure(JsonParser p)
/*      */     throws IOException
/*      */   {
/* 1037 */     JsonToken t = p.getCurrentToken();
/*      */     
/*      */ 
/* 1040 */     if (t == JsonToken.FIELD_NAME) {
/* 1041 */       if (this._mayHaveNativeIds) {
/* 1042 */         _checkNativeIds(p);
/*      */       }
/* 1044 */       writeFieldName(p.getCurrentName());
/* 1045 */       t = p.nextToken();
/*      */     }
/*      */     
/*      */ 
/* 1049 */     if (this._mayHaveNativeIds) {
/* 1050 */       _checkNativeIds(p);
/*      */     }
/*      */     
/* 1053 */     switch (t) {
/*      */     case START_ARRAY: 
/* 1055 */       writeStartArray();
/* 1056 */       while (p.nextToken() != JsonToken.END_ARRAY) {
/* 1057 */         copyCurrentStructure(p);
/*      */       }
/* 1059 */       writeEndArray();
/* 1060 */       break;
/*      */     case START_OBJECT: 
/* 1062 */       writeStartObject();
/* 1063 */       while (p.nextToken() != JsonToken.END_OBJECT) {
/* 1064 */         copyCurrentStructure(p);
/*      */       }
/* 1066 */       writeEndObject();
/* 1067 */       break;
/*      */     default: 
/* 1069 */       copyCurrentEvent(p);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _checkNativeIds(JsonParser jp)
/*      */     throws IOException
/*      */   {
/* 1076 */     if ((this._typeId = jp.getTypeId()) != null) {
/* 1077 */       this._hasNativeId = true;
/*      */     }
/* 1079 */     if ((this._objectId = jp.getObjectId()) != null) {
/* 1080 */       this._hasNativeId = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _append(JsonToken type)
/*      */   {
/* 1092 */     Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
/*      */     
/*      */ 
/* 1095 */     if (next == null) {
/* 1096 */       this._appendAt += 1;
/*      */     } else {
/* 1098 */       this._last = next;
/* 1099 */       this._appendAt = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void _append(JsonToken type, Object value)
/*      */   {
/* 1105 */     Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, value, this._objectId, this._typeId) : this._last.append(this._appendAt, type, value);
/*      */     
/*      */ 
/* 1108 */     if (next == null) {
/* 1109 */       this._appendAt += 1;
/*      */     } else {
/* 1111 */       this._last = next;
/* 1112 */       this._appendAt = 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _appendValue(JsonToken type)
/*      */   {
/* 1124 */     this._writeContext.writeValue();
/* 1125 */     Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
/*      */     
/*      */ 
/* 1128 */     if (next == null) {
/* 1129 */       this._appendAt += 1;
/*      */     } else {
/* 1131 */       this._last = next;
/* 1132 */       this._appendAt = 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _appendValue(JsonToken type, Object value)
/*      */   {
/* 1144 */     this._writeContext.writeValue();
/* 1145 */     Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, value, this._objectId, this._typeId) : this._last.append(this._appendAt, type, value);
/*      */     
/*      */ 
/* 1148 */     if (next == null) {
/* 1149 */       this._appendAt += 1;
/*      */     } else {
/* 1151 */       this._last = next;
/* 1152 */       this._appendAt = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void _appendRaw(int rawType, Object value)
/*      */   {
/* 1158 */     Segment next = this._hasNativeId ? this._last.appendRaw(this._appendAt, rawType, value, this._objectId, this._typeId) : this._last.appendRaw(this._appendAt, rawType, value);
/*      */     
/*      */ 
/* 1161 */     if (next == null) {
/* 1162 */       this._appendAt += 1;
/*      */     } else {
/* 1164 */       this._last = next;
/* 1165 */       this._appendAt = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _reportUnsupportedOperation()
/*      */   {
/* 1171 */     throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static final class Parser
/*      */     extends ParserMinimalBase
/*      */   {
/*      */     protected ObjectCodec _codec;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected final boolean _hasNativeTypeIds;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected final boolean _hasNativeObjectIds;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected final boolean _hasNativeIds;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Segment _segment;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int _segmentPtr;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected JsonReadContext _parsingContext;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean _closed;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected transient ByteArrayBuilder _byteBuilder;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1229 */     protected JsonLocation _location = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Parser(Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds)
/*      */     {
/* 1241 */       super();
/* 1242 */       this._segment = firstSeg;
/* 1243 */       this._segmentPtr = -1;
/* 1244 */       this._codec = codec;
/* 1245 */       this._parsingContext = JsonReadContext.createRootContext(null);
/* 1246 */       this._hasNativeTypeIds = hasNativeTypeIds;
/* 1247 */       this._hasNativeObjectIds = hasNativeObjectIds;
/* 1248 */       this._hasNativeIds = (hasNativeTypeIds | hasNativeObjectIds);
/*      */     }
/*      */     
/*      */     public void setLocation(JsonLocation l) {
/* 1252 */       this._location = l;
/*      */     }
/*      */     
/*      */     public ObjectCodec getCodec() {
/* 1256 */       return this._codec;
/*      */     }
/*      */     
/* 1259 */     public void setCodec(ObjectCodec c) { this._codec = c; }
/*      */     
/*      */     public Version version()
/*      */     {
/* 1263 */       return PackageVersion.VERSION;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonToken peekNextToken()
/*      */       throws IOException
/*      */     {
/* 1275 */       if (this._closed) return null;
/* 1276 */       Segment seg = this._segment;
/* 1277 */       int ptr = this._segmentPtr + 1;
/* 1278 */       if (ptr >= 16) {
/* 1279 */         ptr = 0;
/* 1280 */         seg = seg == null ? null : seg.next();
/*      */       }
/* 1282 */       return seg == null ? null : seg.type(ptr);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void close()
/*      */       throws IOException
/*      */     {
/* 1293 */       if (!this._closed) {
/* 1294 */         this._closed = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonToken nextToken()
/*      */       throws IOException
/*      */     {
/* 1308 */       if ((this._closed) || (this._segment == null)) { return null;
/*      */       }
/*      */       
/* 1311 */       if (++this._segmentPtr >= 16) {
/* 1312 */         this._segmentPtr = 0;
/* 1313 */         this._segment = this._segment.next();
/* 1314 */         if (this._segment == null) {
/* 1315 */           return null;
/*      */         }
/*      */       }
/* 1318 */       this._currToken = this._segment.type(this._segmentPtr);
/*      */       
/* 1320 */       if (this._currToken == JsonToken.FIELD_NAME) {
/* 1321 */         Object ob = _currentObject();
/* 1322 */         String name = (ob instanceof String) ? (String)ob : ob.toString();
/* 1323 */         this._parsingContext.setCurrentName(name);
/* 1324 */       } else if (this._currToken == JsonToken.START_OBJECT) {
/* 1325 */         this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
/* 1326 */       } else if (this._currToken == JsonToken.START_ARRAY) {
/* 1327 */         this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
/* 1328 */       } else if ((this._currToken == JsonToken.END_OBJECT) || (this._currToken == JsonToken.END_ARRAY))
/*      */       {
/*      */ 
/* 1331 */         this._parsingContext = this._parsingContext.getParent();
/*      */         
/* 1333 */         if (this._parsingContext == null) {
/* 1334 */           this._parsingContext = JsonReadContext.createRootContext(null);
/*      */         }
/*      */       }
/* 1337 */       return this._currToken;
/*      */     }
/*      */     
/*      */ 
/*      */     public String nextFieldName()
/*      */       throws IOException
/*      */     {
/* 1344 */       if ((this._closed) || (this._segment == null)) { return null;
/*      */       }
/* 1346 */       int ptr = this._segmentPtr + 1;
/* 1347 */       if ((ptr < 16) && (this._segment.type(ptr) == JsonToken.FIELD_NAME)) {
/* 1348 */         this._segmentPtr = ptr;
/* 1349 */         Object ob = this._segment.get(ptr);
/* 1350 */         String name = (ob instanceof String) ? (String)ob : ob.toString();
/* 1351 */         this._parsingContext.setCurrentName(name);
/* 1352 */         return name;
/*      */       }
/* 1354 */       return nextToken() == JsonToken.FIELD_NAME ? getCurrentName() : null;
/*      */     }
/*      */     
/*      */     public boolean isClosed() {
/* 1358 */       return this._closed;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonStreamContext getParsingContext()
/*      */     {
/* 1367 */       return this._parsingContext;
/*      */     }
/*      */     
/* 1370 */     public JsonLocation getTokenLocation() { return getCurrentLocation(); }
/*      */     
/*      */     public JsonLocation getCurrentLocation()
/*      */     {
/* 1374 */       return this._location == null ? JsonLocation.NA : this._location;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getCurrentName()
/*      */     {
/* 1380 */       if ((this._currToken == JsonToken.START_OBJECT) || (this._currToken == JsonToken.START_ARRAY)) {
/* 1381 */         JsonReadContext parent = this._parsingContext.getParent();
/* 1382 */         return parent.getCurrentName();
/*      */       }
/* 1384 */       return this._parsingContext.getCurrentName();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void overrideCurrentName(String name)
/*      */     {
/* 1391 */       JsonReadContext ctxt = this._parsingContext;
/* 1392 */       if ((this._currToken == JsonToken.START_OBJECT) || (this._currToken == JsonToken.START_ARRAY)) {
/* 1393 */         ctxt = ctxt.getParent();
/*      */       }
/*      */       try {
/* 1396 */         ctxt.setCurrentName(name);
/*      */       } catch (IOException e) {
/* 1398 */         throw new RuntimeException(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getText()
/*      */     {
/* 1412 */       if ((this._currToken == JsonToken.VALUE_STRING) || (this._currToken == JsonToken.FIELD_NAME))
/*      */       {
/* 1414 */         Object ob = _currentObject();
/* 1415 */         if ((ob instanceof String)) {
/* 1416 */           return (String)ob;
/*      */         }
/* 1418 */         return ob == null ? null : ob.toString();
/*      */       }
/* 1420 */       if (this._currToken == null) {
/* 1421 */         return null;
/*      */       }
/* 1423 */       switch (TokenBuffer.1.$SwitchMap$com$fasterxml$jackson$core$JsonToken[this._currToken.ordinal()]) {
/*      */       case 7: 
/*      */       case 8: 
/* 1426 */         Object ob = _currentObject();
/* 1427 */         return ob == null ? null : ob.toString();
/*      */       }
/* 1429 */       return this._currToken.asString();
/*      */     }
/*      */     
/*      */ 
/*      */     public char[] getTextCharacters()
/*      */     {
/* 1435 */       String str = getText();
/* 1436 */       return str == null ? null : str.toCharArray();
/*      */     }
/*      */     
/*      */     public int getTextLength()
/*      */     {
/* 1441 */       String str = getText();
/* 1442 */       return str == null ? 0 : str.length();
/*      */     }
/*      */     
/*      */     public int getTextOffset() {
/* 1446 */       return 0;
/*      */     }
/*      */     
/*      */     public boolean hasTextCharacters()
/*      */     {
/* 1451 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public BigInteger getBigIntegerValue()
/*      */       throws IOException
/*      */     {
/* 1463 */       Number n = getNumberValue();
/* 1464 */       if ((n instanceof BigInteger)) {
/* 1465 */         return (BigInteger)n;
/*      */       }
/* 1467 */       if (getNumberType() == NumberType.BIG_DECIMAL) {
/* 1468 */         return ((BigDecimal)n).toBigInteger();
/*      */       }
/*      */       
/* 1471 */       return BigInteger.valueOf(n.longValue());
/*      */     }
/*      */     
/*      */     public BigDecimal getDecimalValue()
/*      */       throws IOException
/*      */     {
/* 1477 */       Number n = getNumberValue();
/* 1478 */       if ((n instanceof BigDecimal)) {
/* 1479 */         return (BigDecimal)n;
/*      */       }
/* 1481 */       switch (TokenBuffer.1.$SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[getNumberType().ordinal()]) {
/*      */       case 1: 
/*      */       case 5: 
/* 1484 */         return BigDecimal.valueOf(n.longValue());
/*      */       case 2: 
/* 1486 */         return new BigDecimal((BigInteger)n);
/*      */       }
/*      */       
/*      */       
/* 1490 */       return BigDecimal.valueOf(n.doubleValue());
/*      */     }
/*      */     
/*      */     public double getDoubleValue() throws IOException
/*      */     {
/* 1495 */       return getNumberValue().doubleValue();
/*      */     }
/*      */     
/*      */     public float getFloatValue() throws IOException
/*      */     {
/* 1500 */       return getNumberValue().floatValue();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getIntValue()
/*      */       throws IOException
/*      */     {
/* 1507 */       if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/* 1508 */         return ((Number)_currentObject()).intValue();
/*      */       }
/* 1510 */       return getNumberValue().intValue();
/*      */     }
/*      */     
/*      */     public long getLongValue() throws IOException
/*      */     {
/* 1515 */       return getNumberValue().longValue();
/*      */     }
/*      */     
/*      */     public NumberType getNumberType()
/*      */       throws IOException
/*      */     {
/* 1521 */       Number n = getNumberValue();
/* 1522 */       if ((n instanceof Integer)) return NumberType.INT;
/* 1523 */       if ((n instanceof Long)) return NumberType.LONG;
/* 1524 */       if ((n instanceof Double)) return NumberType.DOUBLE;
/* 1525 */       if ((n instanceof BigDecimal)) return NumberType.BIG_DECIMAL;
/* 1526 */       if ((n instanceof BigInteger)) return NumberType.BIG_INTEGER;
/* 1527 */       if ((n instanceof Float)) return NumberType.FLOAT;
/* 1528 */       if ((n instanceof Short)) return NumberType.INT;
/* 1529 */       return null;
/*      */     }
/*      */     
/*      */     public final Number getNumberValue() throws IOException
/*      */     {
/* 1534 */       _checkIsNumber();
/* 1535 */       Object value = _currentObject();
/* 1536 */       if ((value instanceof Number)) {
/* 1537 */         return (Number)value;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1542 */       if ((value instanceof String)) {
/* 1543 */         String str = (String)value;
/* 1544 */         if (str.indexOf('.') >= 0) {
/* 1545 */           return Double.valueOf(Double.parseDouble(str));
/*      */         }
/* 1547 */         return Long.valueOf(Long.parseLong(str));
/*      */       }
/* 1549 */       if (value == null) {
/* 1550 */         return null;
/*      */       }
/* 1552 */       throw new IllegalStateException("Internal error: entry should be a Number, but is of type " + value.getClass().getName());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Object getEmbeddedObject()
/*      */     {
/* 1565 */       if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 1566 */         return _currentObject();
/*      */       }
/* 1568 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getBinaryValue(Base64Variant b64variant)
/*      */       throws IOException, JsonParseException
/*      */     {
/* 1576 */       if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT)
/*      */       {
/* 1578 */         Object ob = _currentObject();
/* 1579 */         if ((ob instanceof byte[])) {
/* 1580 */           return (byte[])ob;
/*      */         }
/*      */       }
/*      */       
/* 1584 */       if (this._currToken != JsonToken.VALUE_STRING) {
/* 1585 */         throw _constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), can not access as binary");
/*      */       }
/* 1587 */       String str = getText();
/* 1588 */       if (str == null) {
/* 1589 */         return null;
/*      */       }
/* 1591 */       ByteArrayBuilder builder = this._byteBuilder;
/* 1592 */       if (builder == null) {
/* 1593 */         this._byteBuilder = (builder = new ByteArrayBuilder(100));
/*      */       } else {
/* 1595 */         this._byteBuilder.reset();
/*      */       }
/* 1597 */       _decodeBase64(str, builder, b64variant);
/* 1598 */       return builder.toByteArray();
/*      */     }
/*      */     
/*      */     public int readBinaryValue(Base64Variant b64variant, OutputStream out)
/*      */       throws IOException
/*      */     {
/* 1604 */       byte[] data = getBinaryValue(b64variant);
/* 1605 */       if (data != null) {
/* 1606 */         out.write(data, 0, data.length);
/* 1607 */         return data.length;
/*      */       }
/* 1609 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean canReadObjectId()
/*      */     {
/* 1620 */       return this._hasNativeObjectIds;
/*      */     }
/*      */     
/*      */     public boolean canReadTypeId()
/*      */     {
/* 1625 */       return this._hasNativeTypeIds;
/*      */     }
/*      */     
/*      */     public Object getTypeId()
/*      */     {
/* 1630 */       return this._segment.findTypeId(this._segmentPtr);
/*      */     }
/*      */     
/*      */     public Object getObjectId()
/*      */     {
/* 1635 */       return this._segment.findObjectId(this._segmentPtr);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected final Object _currentObject()
/*      */     {
/* 1645 */       return this._segment.get(this._segmentPtr);
/*      */     }
/*      */     
/*      */     protected final void _checkIsNumber() throws JsonParseException
/*      */     {
/* 1650 */       if ((this._currToken == null) || (!this._currToken.isNumeric())) {
/* 1651 */         throw _constructError("Current token (" + this._currToken + ") not numeric, can not use numeric value accessors");
/*      */       }
/*      */     }
/*      */     
/*      */     protected void _handleEOF() throws JsonParseException
/*      */     {
/* 1657 */       _throwInternal();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static final class Segment
/*      */   {
/*      */     public static final int TOKENS_PER_SEGMENT = 16;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1679 */     private static final JsonToken[] TOKEN_TYPES_BY_INDEX = new JsonToken[16];
/* 1680 */     static { JsonToken[] t = JsonToken.values();
/*      */       
/* 1682 */       System.arraycopy(t, 1, TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Segment _next;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected long _tokenTypes;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1700 */     protected final Object[] _tokens = new Object[16];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected TreeMap<Integer, Object> _nativeIds;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonToken type(int index)
/*      */     {
/* 1713 */       long l = this._tokenTypes;
/* 1714 */       if (index > 0) {
/* 1715 */         l >>= index << 2;
/*      */       }
/* 1717 */       int ix = (int)l & 0xF;
/* 1718 */       return TOKEN_TYPES_BY_INDEX[ix];
/*      */     }
/*      */     
/*      */     public int rawType(int index)
/*      */     {
/* 1723 */       long l = this._tokenTypes;
/* 1724 */       if (index > 0) {
/* 1725 */         l >>= index << 2;
/*      */       }
/* 1727 */       int ix = (int)l & 0xF;
/* 1728 */       return ix;
/*      */     }
/*      */     
/*      */     public Object get(int index) {
/* 1732 */       return this._tokens[index];
/*      */     }
/*      */     
/* 1735 */     public Segment next() { return this._next; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean hasIds()
/*      */     {
/* 1742 */       return this._nativeIds != null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Segment append(int index, JsonToken tokenType)
/*      */     {
/* 1749 */       if (index < 16) {
/* 1750 */         set(index, tokenType);
/* 1751 */         return null;
/*      */       }
/* 1753 */       this._next = new Segment();
/* 1754 */       this._next.set(0, tokenType);
/* 1755 */       return this._next;
/*      */     }
/*      */     
/*      */ 
/*      */     public Segment append(int index, JsonToken tokenType, Object objectId, Object typeId)
/*      */     {
/* 1761 */       if (index < 16) {
/* 1762 */         set(index, tokenType, objectId, typeId);
/* 1763 */         return null;
/*      */       }
/* 1765 */       this._next = new Segment();
/* 1766 */       this._next.set(0, tokenType, objectId, typeId);
/* 1767 */       return this._next;
/*      */     }
/*      */     
/*      */     public Segment append(int index, JsonToken tokenType, Object value)
/*      */     {
/* 1772 */       if (index < 16) {
/* 1773 */         set(index, tokenType, value);
/* 1774 */         return null;
/*      */       }
/* 1776 */       this._next = new Segment();
/* 1777 */       this._next.set(0, tokenType, value);
/* 1778 */       return this._next;
/*      */     }
/*      */     
/*      */ 
/*      */     public Segment append(int index, JsonToken tokenType, Object value, Object objectId, Object typeId)
/*      */     {
/* 1784 */       if (index < 16) {
/* 1785 */         set(index, tokenType, value, objectId, typeId);
/* 1786 */         return null;
/*      */       }
/* 1788 */       this._next = new Segment();
/* 1789 */       this._next.set(0, tokenType, value, objectId, typeId);
/* 1790 */       return this._next;
/*      */     }
/*      */     
/*      */     public Segment appendRaw(int index, int rawTokenType, Object value)
/*      */     {
/* 1795 */       if (index < 16) {
/* 1796 */         set(index, rawTokenType, value);
/* 1797 */         return null;
/*      */       }
/* 1799 */       this._next = new Segment();
/* 1800 */       this._next.set(0, rawTokenType, value);
/* 1801 */       return this._next;
/*      */     }
/*      */     
/*      */ 
/*      */     public Segment appendRaw(int index, int rawTokenType, Object value, Object objectId, Object typeId)
/*      */     {
/* 1807 */       if (index < 16) {
/* 1808 */         set(index, rawTokenType, value, objectId, typeId);
/* 1809 */         return null;
/*      */       }
/* 1811 */       this._next = new Segment();
/* 1812 */       this._next.set(0, rawTokenType, value, objectId, typeId);
/* 1813 */       return this._next;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void set(int index, JsonToken tokenType)
/*      */     {
/* 1821 */       long typeCode = tokenType.ordinal();
/* 1822 */       if (index > 0) {
/* 1823 */         typeCode <<= index << 2;
/*      */       }
/* 1825 */       this._tokenTypes |= typeCode;
/*      */     }
/*      */     
/*      */ 
/*      */     private void set(int index, JsonToken tokenType, Object objectId, Object typeId)
/*      */     {
/* 1831 */       long typeCode = tokenType.ordinal();
/* 1832 */       if (index > 0) {
/* 1833 */         typeCode <<= index << 2;
/*      */       }
/* 1835 */       this._tokenTypes |= typeCode;
/* 1836 */       assignNativeIds(index, objectId, typeId);
/*      */     }
/*      */     
/*      */     private void set(int index, JsonToken tokenType, Object value)
/*      */     {
/* 1841 */       this._tokens[index] = value;
/* 1842 */       long typeCode = tokenType.ordinal();
/* 1843 */       if (index > 0) {
/* 1844 */         typeCode <<= index << 2;
/*      */       }
/* 1846 */       this._tokenTypes |= typeCode;
/*      */     }
/*      */     
/*      */ 
/*      */     private void set(int index, JsonToken tokenType, Object value, Object objectId, Object typeId)
/*      */     {
/* 1852 */       this._tokens[index] = value;
/* 1853 */       long typeCode = tokenType.ordinal();
/* 1854 */       if (index > 0) {
/* 1855 */         typeCode <<= index << 2;
/*      */       }
/* 1857 */       this._tokenTypes |= typeCode;
/* 1858 */       assignNativeIds(index, objectId, typeId);
/*      */     }
/*      */     
/*      */     private void set(int index, int rawTokenType, Object value)
/*      */     {
/* 1863 */       this._tokens[index] = value;
/* 1864 */       long typeCode = rawTokenType;
/* 1865 */       if (index > 0) {
/* 1866 */         typeCode <<= index << 2;
/*      */       }
/* 1868 */       this._tokenTypes |= typeCode;
/*      */     }
/*      */     
/*      */     private void set(int index, int rawTokenType, Object value, Object objectId, Object typeId)
/*      */     {
/* 1873 */       this._tokens[index] = value;
/* 1874 */       long typeCode = rawTokenType;
/* 1875 */       if (index > 0) {
/* 1876 */         typeCode <<= index << 2;
/*      */       }
/* 1878 */       this._tokenTypes |= typeCode;
/* 1879 */       assignNativeIds(index, objectId, typeId);
/*      */     }
/*      */     
/*      */     private final void assignNativeIds(int index, Object objectId, Object typeId)
/*      */     {
/* 1884 */       if (this._nativeIds == null) {
/* 1885 */         this._nativeIds = new TreeMap();
/*      */       }
/* 1887 */       if (objectId != null) {
/* 1888 */         this._nativeIds.put(Integer.valueOf(_objectIdIndex(index)), objectId);
/*      */       }
/* 1890 */       if (typeId != null) {
/* 1891 */         this._nativeIds.put(Integer.valueOf(_typeIdIndex(index)), typeId);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object findObjectId(int index)
/*      */     {
/* 1899 */       return this._nativeIds == null ? null : this._nativeIds.get(Integer.valueOf(_objectIdIndex(index)));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object findTypeId(int index)
/*      */     {
/* 1906 */       return this._nativeIds == null ? null : this._nativeIds.get(Integer.valueOf(_typeIdIndex(index)));
/*      */     }
/*      */     
/* 1909 */     private final int _typeIdIndex(int i) { return i + i; }
/* 1910 */     private final int _objectIdIndex(int i) { return i + i + 1; }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\TokenBuffer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */