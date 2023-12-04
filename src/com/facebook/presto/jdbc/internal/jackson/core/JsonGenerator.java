/*      */ package com.facebook.presto.jdbc.internal.jackson.core;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharacterEscapes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.VersionUtil;
/*      */ import java.io.Closeable;
/*      */ import java.io.Flushable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicLong;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class JsonGenerator
/*      */   implements Closeable, Flushable, Versioned
/*      */ {
/*      */   protected PrettyPrinter _cfgPrettyPrinter;
/*      */   public abstract JsonGenerator setCodec(ObjectCodec paramObjectCodec);
/*      */   
/*      */   public abstract ObjectCodec getCodec();
/*      */   
/*      */   public abstract Version version();
/*      */   
/*      */   public abstract JsonGenerator enable(Feature paramFeature);
/*      */   
/*      */   public abstract JsonGenerator disable(Feature paramFeature);
/*      */   
/*      */   public static enum Feature
/*      */   {
/*   49 */     AUTO_CLOSE_TARGET(true), 
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
/*   61 */     AUTO_CLOSE_JSON_CONTENT(true), 
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
/*   74 */     FLUSH_PASSED_TO_STREAM(true), 
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
/*   87 */     QUOTE_FIELD_NAMES(true), 
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
/*  101 */     QUOTE_NON_NUMERIC_NUMBERS(true), 
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
/*      */ 
/*  118 */     WRITE_NUMBERS_AS_STRINGS(false), 
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
/*  130 */     WRITE_BIGDECIMAL_AS_PLAIN(false), 
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
/*      */ 
/*  147 */     ESCAPE_NON_ASCII(false), 
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
/*  190 */     STRICT_DUPLICATE_DETECTION(false), 
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  212 */     IGNORE_UNKNOWN(false);
/*      */     
/*      */ 
/*      */ 
/*      */     private final boolean _defaultState;
/*      */     
/*      */ 
/*      */     private final int _mask;
/*      */     
/*      */ 
/*      */     public static int collectDefaults()
/*      */     {
/*  224 */       int flags = 0;
/*  225 */       for (Feature f : values()) {
/*  226 */         if (f.enabledByDefault()) {
/*  227 */           flags |= f.getMask();
/*      */         }
/*      */       }
/*  230 */       return flags;
/*      */     }
/*      */     
/*      */     private Feature(boolean defaultState) {
/*  234 */       this._defaultState = defaultState;
/*  235 */       this._mask = (1 << ordinal());
/*      */     }
/*      */     
/*  238 */     public boolean enabledByDefault() { return this._defaultState; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  243 */     public boolean enabledIn(int flags) { return (flags & this._mask) != 0; }
/*      */     
/*  245 */     public int getMask() { return this._mask; }
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
/*      */   public final JsonGenerator configure(Feature f, boolean state)
/*      */   {
/*  320 */     if (state) enable(f); else disable(f);
/*  321 */     return this;
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
/*      */   public abstract boolean isEnabled(Feature paramFeature);
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
/*      */   public abstract int getFeatureMask();
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
/*      */   public abstract JsonGenerator setFeatureMask(int paramInt);
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
/*      */   public JsonGenerator overrideStdFeatures(int values, int mask)
/*      */   {
/*  371 */     int oldState = getFeatureMask();
/*  372 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/*  373 */     return setFeatureMask(newState);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getFormatFeatures()
/*      */   {
/*  385 */     return 0;
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
/*      */   public JsonGenerator overrideFormatFeatures(int values, int mask)
/*      */   {
/*  402 */     throw new IllegalArgumentException("No FormatFeatures defined for generator of type " + getClass().getName());
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
/*      */   public void setSchema(FormatSchema schema)
/*      */   {
/*  432 */     throw new UnsupportedOperationException("Generator of type " + getClass().getName() + " does not support schema of type '" + schema.getSchemaType() + "'");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public FormatSchema getSchema()
/*      */   {
/*  442 */     return null;
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
/*      */ 
/*      */   public JsonGenerator setPrettyPrinter(PrettyPrinter pp)
/*      */   {
/*  462 */     this._cfgPrettyPrinter = pp;
/*  463 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PrettyPrinter getPrettyPrinter()
/*      */   {
/*  473 */     return this._cfgPrettyPrinter;
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
/*      */   public abstract JsonGenerator useDefaultPrettyPrinter();
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
/*      */   public JsonGenerator setHighestNonEscapedChar(int charCode)
/*      */   {
/*  506 */     return this;
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
/*      */   public int getHighestEscapedChar()
/*      */   {
/*  520 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public CharacterEscapes getCharacterEscapes()
/*      */   {
/*  526 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc)
/*      */   {
/*  534 */     return this;
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
/*      */   public JsonGenerator setRootValueSeparator(SerializableString sep)
/*      */   {
/*  548 */     throw new UnsupportedOperationException();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getOutputTarget()
/*      */   {
/*  573 */     return null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getOutputBuffered()
/*      */   {
/*  595 */     return -1;
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
/*      */   public Object getCurrentValue()
/*      */   {
/*  612 */     JsonStreamContext ctxt = getOutputContext();
/*  613 */     return ctxt == null ? null : ctxt.getCurrentValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCurrentValue(Object v)
/*      */   {
/*  625 */     JsonStreamContext ctxt = getOutputContext();
/*  626 */     if (ctxt != null) {
/*  627 */       ctxt.setCurrentValue(v);
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
/*      */   public boolean canUseSchema(FormatSchema schema)
/*      */   {
/*  645 */     return false;
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
/*      */   public boolean canWriteObjectId()
/*      */   {
/*  661 */     return false;
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
/*      */   public boolean canWriteTypeId()
/*      */   {
/*  677 */     return false;
/*      */   }
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
/*  689 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canOmitFields()
/*      */   {
/*  699 */     return true;
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
/*      */   public boolean canWriteFormattedNumbers()
/*      */   {
/*  713 */     return false;
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
/*      */   public abstract void writeStartArray()
/*      */     throws IOException;
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
/*      */   public void writeStartArray(int size)
/*      */     throws IOException
/*      */   {
/*  748 */     writeStartArray();
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
/*      */   public abstract void writeEndArray()
/*      */     throws IOException;
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
/*      */   public abstract void writeStartObject()
/*      */     throws IOException;
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
/*      */   public void writeStartObject(Object forValue)
/*      */     throws IOException
/*      */   {
/*  788 */     writeStartObject();
/*  789 */     setCurrentValue(forValue);
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
/*      */   public abstract void writeEndObject()
/*      */     throws IOException;
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
/*      */   public abstract void writeFieldName(String paramString)
/*      */     throws IOException;
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
/*      */   public abstract void writeFieldName(SerializableString paramSerializableString)
/*      */     throws IOException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeFieldId(long id)
/*      */     throws IOException
/*      */   {
/*  839 */     writeFieldName(Long.toString(id));
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
/*      */ 
/*      */ 
/*      */   public void writeArray(int[] array, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  861 */     if (array == null) {
/*  862 */       throw new IllegalArgumentException("null array");
/*      */     }
/*  864 */     _verifyOffsets(array.length, offset, length);
/*  865 */     writeStartArray();
/*  866 */     int i = offset; for (int end = offset + length; i < end; i++) {
/*  867 */       writeNumber(array[i]);
/*      */     }
/*  869 */     writeEndArray();
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
/*      */   public void writeArray(long[] array, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  885 */     if (array == null) {
/*  886 */       throw new IllegalArgumentException("null array");
/*      */     }
/*  888 */     _verifyOffsets(array.length, offset, length);
/*  889 */     writeStartArray();
/*  890 */     int i = offset; for (int end = offset + length; i < end; i++) {
/*  891 */       writeNumber(array[i]);
/*      */     }
/*  893 */     writeEndArray();
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
/*      */   public void writeArray(double[] array, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  909 */     if (array == null) {
/*  910 */       throw new IllegalArgumentException("null array");
/*      */     }
/*  912 */     _verifyOffsets(array.length, offset, length);
/*  913 */     writeStartArray();
/*  914 */     int i = offset; for (int end = offset + length; i < end; i++) {
/*  915 */       writeNumber(array[i]);
/*      */     }
/*  917 */     writeEndArray();
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
/*      */   public abstract void writeString(String paramString)
/*      */     throws IOException;
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
/*      */   public abstract void writeString(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */     throws IOException;
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
/*      */   public abstract void writeString(SerializableString paramSerializableString)
/*      */     throws IOException;
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
/*      */   public abstract void writeRawUTF8String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException;
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
/*      */   public abstract void writeUTF8String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException;
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
/*      */   public abstract void writeRaw(String paramString)
/*      */     throws IOException;
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
/*      */   public abstract void writeRaw(String paramString, int paramInt1, int paramInt2)
/*      */     throws IOException;
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
/*      */   public abstract void writeRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */     throws IOException;
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
/*      */   public abstract void writeRaw(char paramChar)
/*      */     throws IOException;
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
/*      */   public void writeRaw(SerializableString raw)
/*      */     throws IOException
/*      */   {
/* 1076 */     writeRaw(raw.getValue());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract void writeRawValue(String paramString)
/*      */     throws IOException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract void writeRawValue(String paramString, int paramInt1, int paramInt2)
/*      */     throws IOException;
/*      */   
/*      */ 
/*      */ 
/*      */   public abstract void writeRawValue(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */     throws IOException;
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeRawValue(SerializableString raw)
/*      */     throws IOException
/*      */   {
/* 1101 */     writeRawValue(raw.getValue());
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
/*      */   public abstract void writeBinary(Base64Variant paramBase64Variant, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException;
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
/*      */   public void writeBinary(byte[] data, int offset, int len)
/*      */     throws IOException
/*      */   {
/* 1134 */     writeBinary(Base64Variants.getDefaultVariant(), data, offset, len);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeBinary(byte[] data)
/*      */     throws IOException
/*      */   {
/* 1144 */     writeBinary(Base64Variants.getDefaultVariant(), data, 0, data.length);
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
/*      */   public int writeBinary(InputStream data, int dataLength)
/*      */     throws IOException
/*      */   {
/* 1162 */     return writeBinary(Base64Variants.getDefaultVariant(), data, dataLength);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract int writeBinary(Base64Variant paramBase64Variant, InputStream paramInputStream, int paramInt)
/*      */     throws IOException;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeNumber(short v)
/*      */     throws IOException
/*      */   {
/* 1206 */     writeNumber(v);
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
/*      */   public abstract void writeNumber(int paramInt)
/*      */     throws IOException;
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
/*      */   public abstract void writeNumber(long paramLong)
/*      */     throws IOException;
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
/*      */   public abstract void writeNumber(BigInteger paramBigInteger)
/*      */     throws IOException;
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
/*      */   public abstract void writeNumber(double paramDouble)
/*      */     throws IOException;
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
/*      */   public abstract void writeNumber(float paramFloat)
/*      */     throws IOException;
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
/*      */   public abstract void writeNumber(BigDecimal paramBigDecimal)
/*      */     throws IOException;
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
/*      */   public abstract void writeNumber(String paramString)
/*      */     throws IOException;
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
/*      */   public abstract void writeBoolean(boolean paramBoolean)
/*      */     throws IOException;
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
/*      */   public abstract void writeNull()
/*      */     throws IOException;
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
/*      */   public void writeEmbeddedObject(Object object)
/*      */     throws IOException
/*      */   {
/* 1329 */     throw new JsonGenerationException("No native support for writing embedded objects", this);
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
/*      */ 
/*      */ 
/*      */   public void writeObjectId(Object id)
/*      */     throws IOException
/*      */   {
/* 1351 */     throw new JsonGenerationException("No native support for writing Object Ids", this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeObjectRef(Object id)
/*      */     throws IOException
/*      */   {
/* 1364 */     throw new JsonGenerationException("No native support for writing Object Ids", this);
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
/*      */   public void writeTypeId(Object id)
/*      */     throws IOException
/*      */   {
/* 1379 */     throw new JsonGenerationException("No native support for writing Type Ids", this);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract void writeObject(Object paramObject)
/*      */     throws IOException;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract void writeTree(TreeNode paramTreeNode)
/*      */     throws IOException;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeStringField(String fieldName, String value)
/*      */     throws IOException
/*      */   {
/* 1495 */     writeFieldName(fieldName);
/* 1496 */     writeString(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeBooleanField(String fieldName, boolean value)
/*      */     throws IOException
/*      */   {
/* 1508 */     writeFieldName(fieldName);
/* 1509 */     writeBoolean(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeNullField(String fieldName)
/*      */     throws IOException
/*      */   {
/* 1521 */     writeFieldName(fieldName);
/* 1522 */     writeNull();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeNumberField(String fieldName, int value)
/*      */     throws IOException
/*      */   {
/* 1534 */     writeFieldName(fieldName);
/* 1535 */     writeNumber(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeNumberField(String fieldName, long value)
/*      */     throws IOException
/*      */   {
/* 1547 */     writeFieldName(fieldName);
/* 1548 */     writeNumber(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeNumberField(String fieldName, double value)
/*      */     throws IOException
/*      */   {
/* 1560 */     writeFieldName(fieldName);
/* 1561 */     writeNumber(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeNumberField(String fieldName, float value)
/*      */     throws IOException
/*      */   {
/* 1573 */     writeFieldName(fieldName);
/* 1574 */     writeNumber(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeNumberField(String fieldName, BigDecimal value)
/*      */     throws IOException
/*      */   {
/* 1587 */     writeFieldName(fieldName);
/* 1588 */     writeNumber(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeBinaryField(String fieldName, byte[] data)
/*      */     throws IOException
/*      */   {
/* 1601 */     writeFieldName(fieldName);
/* 1602 */     writeBinary(data);
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
/*      */   public final void writeArrayFieldStart(String fieldName)
/*      */     throws IOException
/*      */   {
/* 1619 */     writeFieldName(fieldName);
/* 1620 */     writeStartArray();
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
/*      */   public final void writeObjectFieldStart(String fieldName)
/*      */     throws IOException
/*      */   {
/* 1637 */     writeFieldName(fieldName);
/* 1638 */     writeStartObject();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeObjectField(String fieldName, Object pojo)
/*      */     throws IOException
/*      */   {
/* 1651 */     writeFieldName(fieldName);
/* 1652 */     writeObject(pojo);
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
/*      */   public void writeOmittedField(String fieldName)
/*      */     throws IOException
/*      */   {}
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
/*      */   public void copyCurrentEvent(JsonParser p)
/*      */     throws IOException
/*      */   {
/* 1684 */     JsonToken t = p.currentToken();
/*      */     
/* 1686 */     if (t == null) {
/* 1687 */       _reportError("No current event to copy");
/*      */     }
/* 1689 */     switch (t.id()) {
/*      */     case -1: 
/* 1691 */       _reportError("No current event to copy");
/*      */     case 1: 
/* 1693 */       writeStartObject();
/* 1694 */       break;
/*      */     case 2: 
/* 1696 */       writeEndObject();
/* 1697 */       break;
/*      */     case 3: 
/* 1699 */       writeStartArray();
/* 1700 */       break;
/*      */     case 4: 
/* 1702 */       writeEndArray();
/* 1703 */       break;
/*      */     case 5: 
/* 1705 */       writeFieldName(p.getCurrentName());
/* 1706 */       break;
/*      */     case 6: 
/* 1708 */       if (p.hasTextCharacters()) {
/* 1709 */         writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
/*      */       } else {
/* 1711 */         writeString(p.getText());
/*      */       }
/* 1713 */       break;
/*      */     
/*      */     case 7: 
/* 1716 */       JsonParser.NumberType n = p.getNumberType();
/* 1717 */       if (n == JsonParser.NumberType.INT) {
/* 1718 */         writeNumber(p.getIntValue());
/* 1719 */       } else if (n == JsonParser.NumberType.BIG_INTEGER) {
/* 1720 */         writeNumber(p.getBigIntegerValue());
/*      */       } else {
/* 1722 */         writeNumber(p.getLongValue());
/*      */       }
/* 1724 */       break;
/*      */     
/*      */ 
/*      */     case 8: 
/* 1728 */       JsonParser.NumberType n = p.getNumberType();
/* 1729 */       if (n == JsonParser.NumberType.BIG_DECIMAL) {
/* 1730 */         writeNumber(p.getDecimalValue());
/* 1731 */       } else if (n == JsonParser.NumberType.FLOAT) {
/* 1732 */         writeNumber(p.getFloatValue());
/*      */       } else {
/* 1734 */         writeNumber(p.getDoubleValue());
/*      */       }
/* 1736 */       break;
/*      */     
/*      */     case 9: 
/* 1739 */       writeBoolean(true);
/* 1740 */       break;
/*      */     case 10: 
/* 1742 */       writeBoolean(false);
/* 1743 */       break;
/*      */     case 11: 
/* 1745 */       writeNull();
/* 1746 */       break;
/*      */     case 12: 
/* 1748 */       writeObject(p.getEmbeddedObject());
/* 1749 */       break;
/*      */     case 0: default: 
/* 1751 */       _throwInternal();
/*      */     }
/*      */     
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
/*      */   public void copyCurrentStructure(JsonParser p)
/*      */     throws IOException
/*      */   {
/* 1787 */     JsonToken t = p.currentToken();
/* 1788 */     if (t == null) {
/* 1789 */       _reportError("No current event to copy");
/*      */     }
/*      */     
/* 1792 */     int id = t.id();
/* 1793 */     if (id == 5) {
/* 1794 */       writeFieldName(p.getCurrentName());
/* 1795 */       t = p.nextToken();
/* 1796 */       id = t.id();
/*      */     }
/*      */     
/* 1799 */     switch (id) {
/*      */     case 1: 
/* 1801 */       writeStartObject();
/* 1802 */       while (p.nextToken() != JsonToken.END_OBJECT) {
/* 1803 */         copyCurrentStructure(p);
/*      */       }
/* 1805 */       writeEndObject();
/* 1806 */       break;
/*      */     case 3: 
/* 1808 */       writeStartArray();
/* 1809 */       while (p.nextToken() != JsonToken.END_ARRAY) {
/* 1810 */         copyCurrentStructure(p);
/*      */       }
/* 1812 */       writeEndArray();
/* 1813 */       break;
/*      */     default: 
/* 1815 */       copyCurrentEvent(p);
/*      */     }
/*      */     
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
/*      */   public abstract JsonStreamContext getOutputContext();
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
/*      */   public abstract void flush()
/*      */     throws IOException;
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
/*      */   public abstract boolean isClosed();
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
/*      */   public abstract void close()
/*      */     throws IOException;
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
/*      */   protected void _reportError(String msg)
/*      */     throws JsonGenerationException
/*      */   {
/* 1886 */     throw new JsonGenerationException(msg, this);
/*      */   }
/*      */   
/*      */   protected final void _throwInternal() {}
/*      */   
/*      */   protected void _reportUnsupportedOperation() {
/* 1892 */     throw new UnsupportedOperationException("Operation not supported by generator of type " + getClass().getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _verifyOffsets(int arrayLength, int offset, int length)
/*      */   {
/* 1900 */     if ((offset < 0) || (offset + length > arrayLength)) {
/* 1901 */       throw new IllegalArgumentException(String.format("invalid argument(s) (offset=%d, length=%d) for input array of %d element", new Object[] { Integer.valueOf(offset), Integer.valueOf(length), Integer.valueOf(arrayLength) }));
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
/*      */   protected void _writeSimpleObject(Object value)
/*      */     throws IOException
/*      */   {
/* 1920 */     if (value == null) {
/* 1921 */       writeNull();
/* 1922 */       return;
/*      */     }
/* 1924 */     if ((value instanceof String)) {
/* 1925 */       writeString((String)value);
/* 1926 */       return;
/*      */     }
/* 1928 */     if ((value instanceof Number)) {
/* 1929 */       Number n = (Number)value;
/* 1930 */       if ((n instanceof Integer)) {
/* 1931 */         writeNumber(n.intValue());
/* 1932 */         return; }
/* 1933 */       if ((n instanceof Long)) {
/* 1934 */         writeNumber(n.longValue());
/* 1935 */         return; }
/* 1936 */       if ((n instanceof Double)) {
/* 1937 */         writeNumber(n.doubleValue());
/* 1938 */         return; }
/* 1939 */       if ((n instanceof Float)) {
/* 1940 */         writeNumber(n.floatValue());
/* 1941 */         return; }
/* 1942 */       if ((n instanceof Short)) {
/* 1943 */         writeNumber(n.shortValue());
/* 1944 */         return; }
/* 1945 */       if ((n instanceof Byte)) {
/* 1946 */         writeNumber((short)n.byteValue());
/* 1947 */         return; }
/* 1948 */       if ((n instanceof BigInteger)) {
/* 1949 */         writeNumber((BigInteger)n);
/* 1950 */         return; }
/* 1951 */       if ((n instanceof BigDecimal)) {
/* 1952 */         writeNumber((BigDecimal)n);
/* 1953 */         return;
/*      */       }
/*      */       
/* 1956 */       if ((n instanceof AtomicInteger)) {
/* 1957 */         writeNumber(((AtomicInteger)n).get());
/* 1958 */         return; }
/* 1959 */       if ((n instanceof AtomicLong)) {
/* 1960 */         writeNumber(((AtomicLong)n).get());
/* 1961 */         return;
/*      */       }
/* 1963 */     } else { if ((value instanceof byte[])) {
/* 1964 */         writeBinary((byte[])value);
/* 1965 */         return; }
/* 1966 */       if ((value instanceof Boolean)) {
/* 1967 */         writeBoolean(((Boolean)value).booleanValue());
/* 1968 */         return; }
/* 1969 */       if ((value instanceof AtomicBoolean)) {
/* 1970 */         writeBoolean(((AtomicBoolean)value).get());
/* 1971 */         return;
/*      */       } }
/* 1973 */     throw new IllegalStateException("No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed " + value.getClass().getName() + ")");
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\JsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */