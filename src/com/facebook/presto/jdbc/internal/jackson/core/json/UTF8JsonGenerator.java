/*      */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.PrettyPrinter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharacterEscapes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.IOContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.NumberOutput;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.math.BigDecimal;
/*      */ 
/*      */ public class UTF8JsonGenerator extends JsonGeneratorImpl
/*      */ {
/*      */   private static final byte BYTE_u = 117;
/*      */   private static final byte BYTE_0 = 48;
/*      */   private static final byte BYTE_LBRACKET = 91;
/*      */   private static final byte BYTE_RBRACKET = 93;
/*      */   private static final byte BYTE_LCURLY = 123;
/*      */   private static final byte BYTE_RCURLY = 125;
/*      */   private static final byte BYTE_BACKSLASH = 92;
/*      */   private static final byte BYTE_COMMA = 44;
/*      */   private static final byte BYTE_COLON = 58;
/*      */   private static final int MAX_BYTES_TO_BUFFER = 512;
/*   29 */   private static final byte[] HEX_CHARS = ;
/*      */   
/*   31 */   private static final byte[] NULL_BYTES = { 110, 117, 108, 108 };
/*   32 */   private static final byte[] TRUE_BYTES = { 116, 114, 117, 101 };
/*   33 */   private static final byte[] FALSE_BYTES = { 102, 97, 108, 115, 101 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final OutputStream _outputStream;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   52 */   protected byte _quoteChar = 34;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] _outputBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _outputTail;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int _outputEnd;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int _outputMaxContiguous;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected char[] _charBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int _charBufferLength;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] _entityBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _bufferRecyclable;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out)
/*      */   {
/*  116 */     super(ctxt, features, codec);
/*  117 */     this._outputStream = out;
/*  118 */     this._bufferRecyclable = true;
/*  119 */     this._outputBuffer = ctxt.allocWriteEncodingBuffer();
/*  120 */     this._outputEnd = this._outputBuffer.length;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  126 */     this._outputMaxContiguous = (this._outputEnd >> 3);
/*  127 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  128 */     this._charBufferLength = this._charBuffer.length;
/*      */     
/*      */ 
/*  131 */     if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
/*  132 */       setHighestNonEscapedChar(127);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable)
/*      */   {
/*  141 */     super(ctxt, features, codec);
/*  142 */     this._outputStream = out;
/*  143 */     this._bufferRecyclable = bufferRecyclable;
/*  144 */     this._outputTail = outputOffset;
/*  145 */     this._outputBuffer = outputBuffer;
/*  146 */     this._outputEnd = this._outputBuffer.length;
/*      */     
/*  148 */     this._outputMaxContiguous = (this._outputEnd >> 3);
/*  149 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  150 */     this._charBufferLength = this._charBuffer.length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getOutputTarget()
/*      */   {
/*  161 */     return this._outputStream;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOutputBuffered()
/*      */   {
/*  167 */     return this._outputTail;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeFieldName(String name)
/*      */     throws IOException
/*      */   {
/*  179 */     if (this._cfgPrettyPrinter != null) {
/*  180 */       _writePPFieldName(name);
/*  181 */       return;
/*      */     }
/*  183 */     int status = this._writeContext.writeFieldName(name);
/*  184 */     if (status == 4) {
/*  185 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  187 */     if (status == 1) {
/*  188 */       if (this._outputTail >= this._outputEnd) {
/*  189 */         _flushBuffer();
/*      */       }
/*  191 */       this._outputBuffer[(this._outputTail++)] = 44;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  196 */     if (this._cfgUnqNames) {
/*  197 */       _writeStringSegments(name, false);
/*  198 */       return;
/*      */     }
/*  200 */     int len = name.length();
/*      */     
/*  202 */     if (len > this._charBufferLength) {
/*  203 */       _writeStringSegments(name, true);
/*  204 */       return;
/*      */     }
/*  206 */     if (this._outputTail >= this._outputEnd) {
/*  207 */       _flushBuffer();
/*      */     }
/*  209 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     
/*  211 */     if (len <= this._outputMaxContiguous) {
/*  212 */       if (this._outputTail + len > this._outputEnd) {
/*  213 */         _flushBuffer();
/*      */       }
/*  215 */       _writeStringSegment(name, 0, len);
/*      */     } else {
/*  217 */       _writeStringSegments(name, 0, len);
/*      */     }
/*      */     
/*  220 */     if (this._outputTail >= this._outputEnd) {
/*  221 */       _flushBuffer();
/*      */     }
/*  223 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeFieldName(SerializableString name)
/*      */     throws IOException
/*      */   {
/*  229 */     if (this._cfgPrettyPrinter != null) {
/*  230 */       _writePPFieldName(name);
/*  231 */       return;
/*      */     }
/*  233 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  234 */     if (status == 4) {
/*  235 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  237 */     if (status == 1) {
/*  238 */       if (this._outputTail >= this._outputEnd) {
/*  239 */         _flushBuffer();
/*      */       }
/*  241 */       this._outputBuffer[(this._outputTail++)] = 44;
/*      */     }
/*  243 */     if (this._cfgUnqNames) {
/*  244 */       _writeUnq(name);
/*  245 */       return;
/*      */     }
/*  247 */     if (this._outputTail >= this._outputEnd) {
/*  248 */       _flushBuffer();
/*      */     }
/*  250 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  251 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  252 */     if (len < 0) {
/*  253 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  255 */       this._outputTail += len;
/*      */     }
/*  257 */     if (this._outputTail >= this._outputEnd) {
/*  258 */       _flushBuffer();
/*      */     }
/*  260 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   private final void _writeUnq(SerializableString name) throws IOException {
/*  264 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  265 */     if (len < 0) {
/*  266 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  268 */       this._outputTail += len;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeStartArray()
/*      */     throws IOException
/*      */   {
/*  281 */     _verifyValueWrite("start an array");
/*  282 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  283 */     if (this._cfgPrettyPrinter != null) {
/*  284 */       this._cfgPrettyPrinter.writeStartArray(this);
/*      */     } else {
/*  286 */       if (this._outputTail >= this._outputEnd) {
/*  287 */         _flushBuffer();
/*      */       }
/*  289 */       this._outputBuffer[(this._outputTail++)] = 91;
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeEndArray()
/*      */     throws IOException
/*      */   {
/*  296 */     if (!this._writeContext.inArray()) {
/*  297 */       _reportError("Current context not Array but " + this._writeContext.typeDesc());
/*      */     }
/*  299 */     if (this._cfgPrettyPrinter != null) {
/*  300 */       this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  302 */       if (this._outputTail >= this._outputEnd) {
/*  303 */         _flushBuffer();
/*      */       }
/*  305 */       this._outputBuffer[(this._outputTail++)] = 93;
/*      */     }
/*  307 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */   
/*      */   public final void writeStartObject()
/*      */     throws IOException
/*      */   {
/*  313 */     _verifyValueWrite("start an object");
/*  314 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  315 */     if (this._cfgPrettyPrinter != null) {
/*  316 */       this._cfgPrettyPrinter.writeStartObject(this);
/*      */     } else {
/*  318 */       if (this._outputTail >= this._outputEnd) {
/*  319 */         _flushBuffer();
/*      */       }
/*  321 */       this._outputBuffer[(this._outputTail++)] = 123;
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeStartObject(Object forValue)
/*      */     throws IOException
/*      */   {
/*  328 */     _verifyValueWrite("start an object");
/*  329 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext();
/*  330 */     this._writeContext = ctxt;
/*  331 */     if (forValue != null) {
/*  332 */       ctxt.setCurrentValue(forValue);
/*      */     }
/*  334 */     if (this._cfgPrettyPrinter != null) {
/*  335 */       this._cfgPrettyPrinter.writeStartObject(this);
/*      */     } else {
/*  337 */       if (this._outputTail >= this._outputEnd) {
/*  338 */         _flushBuffer();
/*      */       }
/*  340 */       this._outputBuffer[(this._outputTail++)] = 123;
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeEndObject()
/*      */     throws IOException
/*      */   {
/*  347 */     if (!this._writeContext.inObject()) {
/*  348 */       _reportError("Current context not Object but " + this._writeContext.typeDesc());
/*      */     }
/*  350 */     if (this._cfgPrettyPrinter != null) {
/*  351 */       this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  353 */       if (this._outputTail >= this._outputEnd) {
/*  354 */         _flushBuffer();
/*      */       }
/*  356 */       this._outputBuffer[(this._outputTail++)] = 125;
/*      */     }
/*  358 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _writePPFieldName(String name)
/*      */     throws IOException
/*      */   {
/*  367 */     int status = this._writeContext.writeFieldName(name);
/*  368 */     if (status == 4) {
/*  369 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  371 */     if (status == 1) {
/*  372 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  374 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*  376 */     if (this._cfgUnqNames) {
/*  377 */       _writeStringSegments(name, false);
/*  378 */       return;
/*      */     }
/*  380 */     int len = name.length();
/*  381 */     if (len > this._charBufferLength) {
/*  382 */       _writeStringSegments(name, true);
/*  383 */       return;
/*      */     }
/*  385 */     if (this._outputTail >= this._outputEnd) {
/*  386 */       _flushBuffer();
/*      */     }
/*  388 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  389 */     name.getChars(0, len, this._charBuffer, 0);
/*      */     
/*  391 */     if (len <= this._outputMaxContiguous) {
/*  392 */       if (this._outputTail + len > this._outputEnd) {
/*  393 */         _flushBuffer();
/*      */       }
/*  395 */       _writeStringSegment(this._charBuffer, 0, len);
/*      */     } else {
/*  397 */       _writeStringSegments(this._charBuffer, 0, len);
/*      */     }
/*  399 */     if (this._outputTail >= this._outputEnd) {
/*  400 */       _flushBuffer();
/*      */     }
/*  402 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   protected final void _writePPFieldName(SerializableString name) throws IOException
/*      */   {
/*  407 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  408 */     if (status == 4) {
/*  409 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  411 */     if (status == 1) {
/*  412 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  414 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*      */     
/*  417 */     boolean addQuotes = !this._cfgUnqNames;
/*  418 */     if (addQuotes) {
/*  419 */       if (this._outputTail >= this._outputEnd) {
/*  420 */         _flushBuffer();
/*      */       }
/*  422 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     }
/*  424 */     _writeBytes(name.asQuotedUTF8());
/*  425 */     if (addQuotes) {
/*  426 */       if (this._outputTail >= this._outputEnd) {
/*  427 */         _flushBuffer();
/*      */       }
/*  429 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeString(String text)
/*      */     throws IOException
/*      */   {
/*  442 */     _verifyValueWrite("write a string");
/*  443 */     if (text == null) {
/*  444 */       _writeNull();
/*  445 */       return;
/*      */     }
/*      */     
/*  448 */     int len = text.length();
/*  449 */     if (len > this._outputMaxContiguous) {
/*  450 */       _writeStringSegments(text, true);
/*  451 */       return;
/*      */     }
/*  453 */     if (this._outputTail + len >= this._outputEnd) {
/*  454 */       _flushBuffer();
/*      */     }
/*  456 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  457 */     _writeStringSegment(text, 0, len);
/*  458 */     if (this._outputTail >= this._outputEnd) {
/*  459 */       _flushBuffer();
/*      */     }
/*  461 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeString(char[] text, int offset, int len)
/*      */     throws IOException
/*      */   {
/*  467 */     _verifyValueWrite("write a string");
/*  468 */     if (this._outputTail >= this._outputEnd) {
/*  469 */       _flushBuffer();
/*      */     }
/*  471 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     
/*  473 */     if (len <= this._outputMaxContiguous) {
/*  474 */       if (this._outputTail + len > this._outputEnd) {
/*  475 */         _flushBuffer();
/*      */       }
/*  477 */       _writeStringSegment(text, offset, len);
/*      */     } else {
/*  479 */       _writeStringSegments(text, offset, len);
/*      */     }
/*      */     
/*  482 */     if (this._outputTail >= this._outputEnd) {
/*  483 */       _flushBuffer();
/*      */     }
/*  485 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public final void writeString(SerializableString text)
/*      */     throws IOException
/*      */   {
/*  491 */     _verifyValueWrite("write a string");
/*  492 */     if (this._outputTail >= this._outputEnd) {
/*  493 */       _flushBuffer();
/*      */     }
/*  495 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  496 */     int len = text.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  497 */     if (len < 0) {
/*  498 */       _writeBytes(text.asQuotedUTF8());
/*      */     } else {
/*  500 */       this._outputTail += len;
/*      */     }
/*  502 */     if (this._outputTail >= this._outputEnd) {
/*  503 */       _flushBuffer();
/*      */     }
/*  505 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  511 */     _verifyValueWrite("write a string");
/*  512 */     if (this._outputTail >= this._outputEnd) {
/*  513 */       _flushBuffer();
/*      */     }
/*  515 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  516 */     _writeBytes(text, offset, length);
/*  517 */     if (this._outputTail >= this._outputEnd) {
/*  518 */       _flushBuffer();
/*      */     }
/*  520 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int len)
/*      */     throws IOException
/*      */   {
/*  526 */     _verifyValueWrite("write a string");
/*  527 */     if (this._outputTail >= this._outputEnd) {
/*  528 */       _flushBuffer();
/*      */     }
/*  530 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     
/*  532 */     if (len <= this._outputMaxContiguous) {
/*  533 */       _writeUTF8Segment(text, offset, len);
/*      */     } else {
/*  535 */       _writeUTF8Segments(text, offset, len);
/*      */     }
/*  537 */     if (this._outputTail >= this._outputEnd) {
/*  538 */       _flushBuffer();
/*      */     }
/*  540 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeRaw(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  553 */     int start = 0;
/*  554 */     int len = text.length();
/*  555 */     while (len > 0) {
/*  556 */       char[] buf = this._charBuffer;
/*  557 */       int blen = buf.length;
/*  558 */       int len2 = len < blen ? len : blen;
/*  559 */       text.getChars(start, start + len2, buf, 0);
/*  560 */       writeRaw(buf, 0, len2);
/*  561 */       start += len2;
/*  562 */       len -= len2;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRaw(String text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  570 */     while (len > 0) {
/*  571 */       char[] buf = this._charBuffer;
/*  572 */       int blen = buf.length;
/*  573 */       int len2 = len < blen ? len : blen;
/*  574 */       text.getChars(offset, offset + len2, buf, 0);
/*  575 */       writeRaw(buf, 0, len2);
/*  576 */       offset += len2;
/*  577 */       len -= len2;
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeRaw(SerializableString text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  584 */     byte[] raw = text.asUnquotedUTF8();
/*  585 */     if (raw.length > 0) {
/*  586 */       _writeBytes(raw);
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeRawValue(SerializableString text)
/*      */     throws IOException
/*      */   {
/*  593 */     _verifyValueWrite("write a raw (unencoded) value");
/*  594 */     byte[] raw = text.asUnquotedUTF8();
/*  595 */     if (raw.length > 0) {
/*  596 */       _writeBytes(raw);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeRaw(char[] cbuf, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  607 */     int len3 = len + len + len;
/*  608 */     if (this._outputTail + len3 > this._outputEnd)
/*      */     {
/*  610 */       if (this._outputEnd < len3) {
/*  611 */         _writeSegmentedRaw(cbuf, offset, len);
/*  612 */         return;
/*      */       }
/*      */       
/*  615 */       _flushBuffer();
/*      */     }
/*      */     
/*  618 */     len += offset;
/*      */     
/*      */ 
/*      */ 
/*  622 */     while (offset < len)
/*      */     {
/*      */       for (;;) {
/*  625 */         int ch = cbuf[offset];
/*  626 */         if (ch > 127) {
/*      */           break;
/*      */         }
/*  629 */         this._outputBuffer[(this._outputTail++)] = ((byte)ch);
/*  630 */         offset++; if (offset >= len) {
/*      */           return;
/*      */         }
/*      */       }
/*  634 */       char ch = cbuf[(offset++)];
/*  635 */       if (ch < 'ࠀ') {
/*  636 */         this._outputBuffer[(this._outputTail++)] = ((byte)(0xC0 | ch >> '\006'));
/*  637 */         this._outputBuffer[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/*  639 */         offset = _outputRawMultiByteChar(ch, cbuf, offset, len);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRaw(char ch)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  648 */     if (this._outputTail + 3 >= this._outputEnd) {
/*  649 */       _flushBuffer();
/*      */     }
/*  651 */     byte[] bbuf = this._outputBuffer;
/*  652 */     if (ch <= '') {
/*  653 */       bbuf[(this._outputTail++)] = ((byte)ch);
/*  654 */     } else if (ch < 'ࠀ') {
/*  655 */       bbuf[(this._outputTail++)] = ((byte)(0xC0 | ch >> '\006'));
/*  656 */       bbuf[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/*      */     } else {
/*  658 */       _outputRawMultiByteChar(ch, null, 0, 0);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeSegmentedRaw(char[] cbuf, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  669 */     int end = this._outputEnd;
/*  670 */     byte[] bbuf = this._outputBuffer;
/*      */     
/*      */ 
/*  673 */     while (offset < len)
/*      */     {
/*      */       for (;;) {
/*  676 */         int ch = cbuf[offset];
/*  677 */         if (ch >= 128) {
/*      */           break;
/*      */         }
/*      */         
/*  681 */         if (this._outputTail >= end) {
/*  682 */           _flushBuffer();
/*      */         }
/*  684 */         bbuf[(this._outputTail++)] = ((byte)ch);
/*  685 */         offset++; if (offset >= len) {
/*      */           return;
/*      */         }
/*      */       }
/*  689 */       if (this._outputTail + 3 >= this._outputEnd) {
/*  690 */         _flushBuffer();
/*      */       }
/*  692 */       char ch = cbuf[(offset++)];
/*  693 */       if (ch < 'ࠀ') {
/*  694 */         bbuf[(this._outputTail++)] = ((byte)(0xC0 | ch >> '\006'));
/*  695 */         bbuf[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/*  697 */         offset = _outputRawMultiByteChar(ch, cbuf, offset, len);
/*      */       }
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
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  713 */     _verifyValueWrite("write a binary value");
/*      */     
/*  715 */     if (this._outputTail >= this._outputEnd) {
/*  716 */       _flushBuffer();
/*      */     }
/*  718 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  719 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  721 */     if (this._outputTail >= this._outputEnd) {
/*  722 */       _flushBuffer();
/*      */     }
/*  724 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  732 */     _verifyValueWrite("write a binary value");
/*      */     
/*  734 */     if (this._outputTail >= this._outputEnd) {
/*  735 */       _flushBuffer();
/*      */     }
/*  737 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  738 */     byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
/*      */     int bytes;
/*      */     try { int bytes;
/*  741 */       if (dataLength < 0) {
/*  742 */         bytes = _writeBinary(b64variant, data, encodingBuffer);
/*      */       } else {
/*  744 */         int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
/*  745 */         if (missing > 0) {
/*  746 */           _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
/*      */         }
/*  748 */         bytes = dataLength;
/*      */       }
/*      */     } finally {
/*  751 */       this._ioContext.releaseBase64Buffer(encodingBuffer);
/*      */     }
/*      */     
/*  754 */     if (this._outputTail >= this._outputEnd) {
/*  755 */       _flushBuffer();
/*      */     }
/*  757 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  758 */     return bytes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeNumber(short s)
/*      */     throws IOException
/*      */   {
/*  770 */     _verifyValueWrite("write a number");
/*      */     
/*  772 */     if (this._outputTail + 6 >= this._outputEnd) {
/*  773 */       _flushBuffer();
/*      */     }
/*  775 */     if (this._cfgNumbersAsStrings) {
/*  776 */       _writeQuotedShort(s);
/*  777 */       return;
/*      */     }
/*  779 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedShort(short s) throws IOException {
/*  783 */     if (this._outputTail + 8 >= this._outputEnd) {
/*  784 */       _flushBuffer();
/*      */     }
/*  786 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  787 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*  788 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeNumber(int i)
/*      */     throws IOException
/*      */   {
/*  794 */     _verifyValueWrite("write a number");
/*      */     
/*  796 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  797 */       _flushBuffer();
/*      */     }
/*  799 */     if (this._cfgNumbersAsStrings) {
/*  800 */       _writeQuotedInt(i);
/*  801 */       return;
/*      */     }
/*  803 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedInt(int i) throws IOException
/*      */   {
/*  808 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  809 */       _flushBuffer();
/*      */     }
/*  811 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  812 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  813 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeNumber(long l)
/*      */     throws IOException
/*      */   {
/*  819 */     _verifyValueWrite("write a number");
/*  820 */     if (this._cfgNumbersAsStrings) {
/*  821 */       _writeQuotedLong(l);
/*  822 */       return;
/*      */     }
/*  824 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  826 */       _flushBuffer();
/*      */     }
/*  828 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedLong(long l) throws IOException
/*      */   {
/*  833 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  834 */       _flushBuffer();
/*      */     }
/*  836 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  837 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*  838 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeNumber(java.math.BigInteger value)
/*      */     throws IOException
/*      */   {
/*  844 */     _verifyValueWrite("write a number");
/*  845 */     if (value == null) {
/*  846 */       _writeNull();
/*  847 */     } else if (this._cfgNumbersAsStrings) {
/*  848 */       _writeQuotedRaw(value.toString());
/*      */     } else {
/*  850 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(double d)
/*      */     throws IOException
/*      */   {
/*  858 */     if ((this._cfgNumbersAsStrings) || (((Double.isNaN(d)) || (Double.isInfinite(d))) && (JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS.enabledIn(this._features))))
/*      */     {
/*      */ 
/*  861 */       writeString(String.valueOf(d));
/*  862 */       return;
/*      */     }
/*      */     
/*  865 */     _verifyValueWrite("write a number");
/*  866 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */   
/*      */   public void writeNumber(float f)
/*      */     throws IOException
/*      */   {
/*  872 */     if ((this._cfgNumbersAsStrings) || (((Float.isNaN(f)) || (Float.isInfinite(f))) && (JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS.enabledIn(this._features))))
/*      */     {
/*      */ 
/*      */ 
/*  876 */       writeString(String.valueOf(f));
/*  877 */       return;
/*      */     }
/*      */     
/*  880 */     _verifyValueWrite("write a number");
/*  881 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(BigDecimal value)
/*      */     throws IOException
/*      */   {
/*  888 */     _verifyValueWrite("write a number");
/*  889 */     if (value == null) {
/*  890 */       _writeNull();
/*  891 */     } else if (this._cfgNumbersAsStrings) {
/*  892 */       String raw = JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features) ? value.toPlainString() : value.toString();
/*      */       
/*  894 */       _writeQuotedRaw(raw);
/*  895 */     } else if (JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features)) {
/*  896 */       writeRaw(value.toPlainString());
/*      */     } else {
/*  898 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeNumber(String encodedValue)
/*      */     throws IOException
/*      */   {
/*  905 */     _verifyValueWrite("write a number");
/*  906 */     if (this._cfgNumbersAsStrings) {
/*  907 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/*  909 */       writeRaw(encodedValue);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _writeQuotedRaw(String value) throws IOException
/*      */   {
/*  915 */     if (this._outputTail >= this._outputEnd) {
/*  916 */       _flushBuffer();
/*      */     }
/*  918 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  919 */     writeRaw(value);
/*  920 */     if (this._outputTail >= this._outputEnd) {
/*  921 */       _flushBuffer();
/*      */     }
/*  923 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeBoolean(boolean state)
/*      */     throws IOException
/*      */   {
/*  929 */     _verifyValueWrite("write a boolean value");
/*  930 */     if (this._outputTail + 5 >= this._outputEnd) {
/*  931 */       _flushBuffer();
/*      */     }
/*  933 */     byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
/*  934 */     int len = keyword.length;
/*  935 */     System.arraycopy(keyword, 0, this._outputBuffer, this._outputTail, len);
/*  936 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */   public void writeNull()
/*      */     throws IOException
/*      */   {
/*  942 */     _verifyValueWrite("write a null");
/*  943 */     _writeNull();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _verifyValueWrite(String typeMsg)
/*      */     throws IOException
/*      */   {
/*  955 */     int status = this._writeContext.writeValue();
/*  956 */     if (status == 5) {
/*  957 */       _reportError("Can not " + typeMsg + ", expecting field name");
/*      */     }
/*  959 */     if (this._cfgPrettyPrinter == null) {
/*      */       byte b;
/*  961 */       switch (status) {
/*      */       case 1: 
/*  963 */         b = 44;
/*  964 */         break;
/*      */       case 2: 
/*  966 */         b = 58;
/*  967 */         break;
/*      */       case 3: 
/*  969 */         if (this._rootValueSeparator != null) {
/*  970 */           byte[] raw = this._rootValueSeparator.asUnquotedUTF8();
/*  971 */           if (raw.length > 0) {
/*  972 */             _writeBytes(raw);
/*      */           }
/*      */         }
/*  975 */         return;
/*      */       case 0: 
/*      */       default: 
/*  978 */         return;
/*      */       }
/*  980 */       if (this._outputTail >= this._outputEnd) {
/*  981 */         _flushBuffer();
/*      */       }
/*  983 */       this._outputBuffer[this._outputTail] = b;
/*  984 */       this._outputTail += 1;
/*  985 */       return;
/*      */     }
/*      */     
/*  988 */     _verifyPrettyValueWrite(typeMsg, status);
/*      */   }
/*      */   
/*      */   protected final void _verifyPrettyValueWrite(String typeMsg, int status)
/*      */     throws IOException
/*      */   {
/*  994 */     switch (status) {
/*      */     case 1: 
/*  996 */       this._cfgPrettyPrinter.writeArrayValueSeparator(this);
/*  997 */       break;
/*      */     case 2: 
/*  999 */       this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
/* 1000 */       break;
/*      */     case 3: 
/* 1002 */       this._cfgPrettyPrinter.writeRootValueSeparator(this);
/* 1003 */       break;
/*      */     
/*      */     case 0: 
/* 1006 */       if (this._writeContext.inArray()) {
/* 1007 */         this._cfgPrettyPrinter.beforeArrayValues(this);
/* 1008 */       } else if (this._writeContext.inObject()) {
/* 1009 */         this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */       }
/*      */       break;
/*      */     default: 
/* 1013 */       _throwInternal();
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
/*      */   public void flush()
/*      */     throws IOException
/*      */   {
/* 1027 */     _flushBuffer();
/* 1028 */     if ((this._outputStream != null) && 
/* 1029 */       (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))) {
/* 1030 */       this._outputStream.flush();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/* 1038 */     super.close();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1044 */     if ((this._outputBuffer != null) && (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT))) {
/*      */       for (;;)
/*      */       {
/* 1047 */         com.facebook.presto.jdbc.internal.jackson.core.JsonStreamContext ctxt = getOutputContext();
/* 1048 */         if (ctxt.inArray()) {
/* 1049 */           writeEndArray();
/* 1050 */         } else { if (!ctxt.inObject()) break;
/* 1051 */           writeEndObject();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1057 */     _flushBuffer();
/* 1058 */     this._outputTail = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1066 */     if (this._outputStream != null) {
/* 1067 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET))) {
/* 1068 */         this._outputStream.close();
/* 1069 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))
/*      */       {
/* 1071 */         this._outputStream.flush();
/*      */       }
/*      */     }
/*      */     
/* 1075 */     _releaseBuffers();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */   {
/* 1081 */     byte[] buf = this._outputBuffer;
/* 1082 */     if ((buf != null) && (this._bufferRecyclable)) {
/* 1083 */       this._outputBuffer = null;
/* 1084 */       this._ioContext.releaseWriteEncodingBuffer(buf);
/*      */     }
/* 1086 */     char[] cbuf = this._charBuffer;
/* 1087 */     if (cbuf != null) {
/* 1088 */       this._charBuffer = null;
/* 1089 */       this._ioContext.releaseConcatBuffer(cbuf);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeBytes(byte[] bytes)
/*      */     throws IOException
/*      */   {
/* 1101 */     int len = bytes.length;
/* 1102 */     if (this._outputTail + len > this._outputEnd) {
/* 1103 */       _flushBuffer();
/*      */       
/* 1105 */       if (len > 512) {
/* 1106 */         this._outputStream.write(bytes, 0, len);
/* 1107 */         return;
/*      */       }
/*      */     }
/* 1110 */     System.arraycopy(bytes, 0, this._outputBuffer, this._outputTail, len);
/* 1111 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */   private final void _writeBytes(byte[] bytes, int offset, int len) throws IOException
/*      */   {
/* 1116 */     if (this._outputTail + len > this._outputEnd) {
/* 1117 */       _flushBuffer();
/*      */       
/* 1119 */       if (len > 512) {
/* 1120 */         this._outputStream.write(bytes, offset, len);
/* 1121 */         return;
/*      */       }
/*      */     }
/* 1124 */     System.arraycopy(bytes, offset, this._outputBuffer, this._outputTail, len);
/* 1125 */     this._outputTail += len;
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
/*      */   private final void _writeStringSegments(String text, boolean addQuotes)
/*      */     throws IOException
/*      */   {
/* 1143 */     if (addQuotes) {
/* 1144 */       if (this._outputTail >= this._outputEnd) {
/* 1145 */         _flushBuffer();
/*      */       }
/* 1147 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     }
/*      */     
/* 1150 */     int left = text.length();
/* 1151 */     int offset = 0;
/*      */     
/* 1153 */     while (left > 0) {
/* 1154 */       int len = Math.min(this._outputMaxContiguous, left);
/* 1155 */       if (this._outputTail + len > this._outputEnd) {
/* 1156 */         _flushBuffer();
/*      */       }
/* 1158 */       _writeStringSegment(text, offset, len);
/* 1159 */       offset += len;
/* 1160 */       left -= len;
/*      */     }
/*      */     
/* 1163 */     if (addQuotes) {
/* 1164 */       if (this._outputTail >= this._outputEnd) {
/* 1165 */         _flushBuffer();
/*      */       }
/* 1167 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeStringSegments(char[] cbuf, int offset, int totalLen)
/*      */     throws IOException
/*      */   {
/*      */     do
/*      */     {
/* 1180 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1181 */       if (this._outputTail + len > this._outputEnd) {
/* 1182 */         _flushBuffer();
/*      */       }
/* 1184 */       _writeStringSegment(cbuf, offset, len);
/* 1185 */       offset += len;
/* 1186 */       totalLen -= len;
/* 1187 */     } while (totalLen > 0);
/*      */   }
/*      */   
/*      */   private final void _writeStringSegments(String text, int offset, int totalLen) throws IOException
/*      */   {
/*      */     do {
/* 1193 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1194 */       if (this._outputTail + len > this._outputEnd) {
/* 1195 */         _flushBuffer();
/*      */       }
/* 1197 */       _writeStringSegment(text, offset, len);
/* 1198 */       offset += len;
/* 1199 */       totalLen -= len;
/* 1200 */     } while (totalLen > 0);
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
/*      */   private final void _writeStringSegment(char[] cbuf, int offset, int len)
/*      */     throws IOException
/*      */   {
/* 1223 */     len += offset;
/*      */     
/* 1225 */     int outputPtr = this._outputTail;
/* 1226 */     byte[] outputBuffer = this._outputBuffer;
/* 1227 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1229 */     while (offset < len) {
/* 1230 */       int ch = cbuf[offset];
/*      */       
/* 1232 */       if ((ch > 127) || (escCodes[ch] != 0)) {
/*      */         break;
/*      */       }
/* 1235 */       outputBuffer[(outputPtr++)] = ((byte)ch);
/* 1236 */       offset++;
/*      */     }
/* 1238 */     this._outputTail = outputPtr;
/* 1239 */     if (offset < len)
/*      */     {
/* 1241 */       if (this._characterEscapes != null) {
/* 1242 */         _writeCustomStringSegment2(cbuf, offset, len);
/*      */       }
/* 1244 */       else if (this._maximumNonEscapedChar == 0) {
/* 1245 */         _writeStringSegment2(cbuf, offset, len);
/*      */       } else {
/* 1247 */         _writeStringSegmentASCII2(cbuf, offset, len);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final void _writeStringSegment(String text, int offset, int len)
/*      */     throws IOException
/*      */   {
/* 1257 */     len += offset;
/*      */     
/* 1259 */     int outputPtr = this._outputTail;
/* 1260 */     byte[] outputBuffer = this._outputBuffer;
/* 1261 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1263 */     while (offset < len) {
/* 1264 */       int ch = text.charAt(offset);
/*      */       
/* 1266 */       if ((ch > 127) || (escCodes[ch] != 0)) {
/*      */         break;
/*      */       }
/* 1269 */       outputBuffer[(outputPtr++)] = ((byte)ch);
/* 1270 */       offset++;
/*      */     }
/* 1272 */     this._outputTail = outputPtr;
/* 1273 */     if (offset < len) {
/* 1274 */       if (this._characterEscapes != null) {
/* 1275 */         _writeCustomStringSegment2(text, offset, len);
/* 1276 */       } else if (this._maximumNonEscapedChar == 0) {
/* 1277 */         _writeStringSegment2(text, offset, len);
/*      */       } else {
/* 1279 */         _writeStringSegmentASCII2(text, offset, len);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeStringSegment2(char[] cbuf, int offset, int end)
/*      */     throws IOException
/*      */   {
/* 1291 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1292 */       _flushBuffer();
/*      */     }
/*      */     
/* 1295 */     int outputPtr = this._outputTail;
/*      */     
/* 1297 */     byte[] outputBuffer = this._outputBuffer;
/* 1298 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1300 */     while (offset < end) {
/* 1301 */       int ch = cbuf[(offset++)];
/* 1302 */       if (ch <= 127) {
/* 1303 */         if (escCodes[ch] == 0) {
/* 1304 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1307 */           int escape = escCodes[ch];
/* 1308 */           if (escape > 0) {
/* 1309 */             outputBuffer[(outputPtr++)] = 92;
/* 1310 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */           }
/*      */           else {
/* 1313 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1317 */       else if (ch <= 2047) {
/* 1318 */         outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1319 */         outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/* 1321 */         outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */       }
/*      */     }
/* 1324 */     this._outputTail = outputPtr;
/*      */   }
/*      */   
/*      */   private final void _writeStringSegment2(String text, int offset, int end) throws IOException
/*      */   {
/* 1329 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1330 */       _flushBuffer();
/*      */     }
/*      */     
/* 1333 */     int outputPtr = this._outputTail;
/*      */     
/* 1335 */     byte[] outputBuffer = this._outputBuffer;
/* 1336 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1338 */     while (offset < end) {
/* 1339 */       int ch = text.charAt(offset++);
/* 1340 */       if (ch <= 127) {
/* 1341 */         if (escCodes[ch] == 0) {
/* 1342 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1345 */           int escape = escCodes[ch];
/* 1346 */           if (escape > 0) {
/* 1347 */             outputBuffer[(outputPtr++)] = 92;
/* 1348 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */           }
/*      */           else {
/* 1351 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1355 */       else if (ch <= 2047) {
/* 1356 */         outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1357 */         outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/* 1359 */         outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */       }
/*      */     }
/* 1362 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeStringSegmentASCII2(char[] cbuf, int offset, int end)
/*      */     throws IOException
/*      */   {
/* 1379 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1380 */       _flushBuffer();
/*      */     }
/*      */     
/* 1383 */     int outputPtr = this._outputTail;
/*      */     
/* 1385 */     byte[] outputBuffer = this._outputBuffer;
/* 1386 */     int[] escCodes = this._outputEscapes;
/* 1387 */     int maxUnescaped = this._maximumNonEscapedChar;
/*      */     
/* 1389 */     while (offset < end) {
/* 1390 */       int ch = cbuf[(offset++)];
/* 1391 */       if (ch <= 127) {
/* 1392 */         if (escCodes[ch] == 0) {
/* 1393 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1396 */           int escape = escCodes[ch];
/* 1397 */           if (escape > 0) {
/* 1398 */             outputBuffer[(outputPtr++)] = 92;
/* 1399 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */           }
/*      */           else {
/* 1402 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1406 */       else if (ch > maxUnescaped) {
/* 1407 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */ 
/*      */       }
/* 1410 */       else if (ch <= 2047) {
/* 1411 */         outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1412 */         outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/* 1414 */         outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */       }
/*      */     }
/* 1417 */     this._outputTail = outputPtr;
/*      */   }
/*      */   
/*      */   private final void _writeStringSegmentASCII2(String text, int offset, int end)
/*      */     throws IOException
/*      */   {
/* 1423 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1424 */       _flushBuffer();
/*      */     }
/*      */     
/* 1427 */     int outputPtr = this._outputTail;
/*      */     
/* 1429 */     byte[] outputBuffer = this._outputBuffer;
/* 1430 */     int[] escCodes = this._outputEscapes;
/* 1431 */     int maxUnescaped = this._maximumNonEscapedChar;
/*      */     
/* 1433 */     while (offset < end) {
/* 1434 */       int ch = text.charAt(offset++);
/* 1435 */       if (ch <= 127) {
/* 1436 */         if (escCodes[ch] == 0) {
/* 1437 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1440 */           int escape = escCodes[ch];
/* 1441 */           if (escape > 0) {
/* 1442 */             outputBuffer[(outputPtr++)] = 92;
/* 1443 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */           }
/*      */           else {
/* 1446 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1450 */       else if (ch > maxUnescaped) {
/* 1451 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */ 
/*      */       }
/* 1454 */       else if (ch <= 2047) {
/* 1455 */         outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1456 */         outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/* 1458 */         outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */       }
/*      */     }
/* 1461 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeCustomStringSegment2(char[] cbuf, int offset, int end)
/*      */     throws IOException
/*      */   {
/* 1478 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1479 */       _flushBuffer();
/*      */     }
/* 1481 */     int outputPtr = this._outputTail;
/*      */     
/* 1483 */     byte[] outputBuffer = this._outputBuffer;
/* 1484 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1486 */     int maxUnescaped = this._maximumNonEscapedChar <= 0 ? 65535 : this._maximumNonEscapedChar;
/* 1487 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1489 */     while (offset < end) {
/* 1490 */       int ch = cbuf[(offset++)];
/* 1491 */       if (ch <= 127) {
/* 1492 */         if (escCodes[ch] == 0) {
/* 1493 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1496 */           int escape = escCodes[ch];
/* 1497 */           if (escape > 0) {
/* 1498 */             outputBuffer[(outputPtr++)] = 92;
/* 1499 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/* 1500 */           } else if (escape == -2) {
/* 1501 */             SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1502 */             if (esc == null) {
/* 1503 */               _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(ch) + ", although was supposed to have one");
/*      */             }
/*      */             
/* 1506 */             outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */           }
/*      */           else {
/* 1509 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1513 */       else if (ch > maxUnescaped) {
/* 1514 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */       }
/*      */       else {
/* 1517 */         SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1518 */         if (esc != null) {
/* 1519 */           outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */ 
/*      */         }
/* 1522 */         else if (ch <= 2047) {
/* 1523 */           outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1524 */           outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */         } else {
/* 1526 */           outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */         }
/*      */       } }
/* 1529 */     this._outputTail = outputPtr;
/*      */   }
/*      */   
/*      */   private final void _writeCustomStringSegment2(String text, int offset, int end)
/*      */     throws IOException
/*      */   {
/* 1535 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1536 */       _flushBuffer();
/*      */     }
/* 1538 */     int outputPtr = this._outputTail;
/*      */     
/* 1540 */     byte[] outputBuffer = this._outputBuffer;
/* 1541 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1543 */     int maxUnescaped = this._maximumNonEscapedChar <= 0 ? 65535 : this._maximumNonEscapedChar;
/* 1544 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1546 */     while (offset < end) {
/* 1547 */       int ch = text.charAt(offset++);
/* 1548 */       if (ch <= 127) {
/* 1549 */         if (escCodes[ch] == 0) {
/* 1550 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1553 */           int escape = escCodes[ch];
/* 1554 */           if (escape > 0) {
/* 1555 */             outputBuffer[(outputPtr++)] = 92;
/* 1556 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/* 1557 */           } else if (escape == -2) {
/* 1558 */             SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1559 */             if (esc == null) {
/* 1560 */               _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(ch) + ", although was supposed to have one");
/*      */             }
/*      */             
/* 1563 */             outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */           }
/*      */           else {
/* 1566 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1570 */       else if (ch > maxUnescaped) {
/* 1571 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */       }
/*      */       else {
/* 1574 */         SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1575 */         if (esc != null) {
/* 1576 */           outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */ 
/*      */         }
/* 1579 */         else if (ch <= 2047) {
/* 1580 */           outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1581 */           outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */         } else {
/* 1583 */           outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */         }
/*      */       } }
/* 1586 */     this._outputTail = outputPtr;
/*      */   }
/*      */   
/*      */   private final int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1592 */     byte[] raw = esc.asUnquotedUTF8();
/* 1593 */     int len = raw.length;
/* 1594 */     if (len > 6) {
/* 1595 */       return _handleLongCustomEscape(outputBuffer, outputPtr, this._outputEnd, raw, remainingChars);
/*      */     }
/*      */     
/* 1598 */     System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1599 */     return outputPtr + len;
/*      */   }
/*      */   
/*      */ 
/*      */   private final int _handleLongCustomEscape(byte[] outputBuffer, int outputPtr, int outputEnd, byte[] raw, int remainingChars)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1606 */     int len = raw.length;
/* 1607 */     if (outputPtr + len > outputEnd) {
/* 1608 */       this._outputTail = outputPtr;
/* 1609 */       _flushBuffer();
/* 1610 */       outputPtr = this._outputTail;
/* 1611 */       if (len > outputBuffer.length) {
/* 1612 */         this._outputStream.write(raw, 0, len);
/* 1613 */         return outputPtr;
/*      */       }
/* 1615 */       System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1616 */       outputPtr += len;
/*      */     }
/*      */     
/* 1619 */     if (outputPtr + 6 * remainingChars > outputEnd) {
/* 1620 */       _flushBuffer();
/* 1621 */       return this._outputTail;
/*      */     }
/* 1623 */     return outputPtr;
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
/*      */   private final void _writeUTF8Segments(byte[] utf8, int offset, int totalLen)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*      */     do
/*      */     {
/* 1641 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1642 */       _writeUTF8Segment(utf8, offset, len);
/* 1643 */       offset += len;
/* 1644 */       totalLen -= len;
/* 1645 */     } while (totalLen > 0);
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _writeUTF8Segment(byte[] utf8, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1652 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1654 */     int ptr = offset; for (int end = offset + len; ptr < end;)
/*      */     {
/* 1656 */       int ch = utf8[(ptr++)];
/* 1657 */       if ((ch >= 0) && (escCodes[ch] != 0)) {
/* 1658 */         _writeUTF8Segment2(utf8, offset, len);
/* 1659 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1664 */     if (this._outputTail + len > this._outputEnd) {
/* 1665 */       _flushBuffer();
/*      */     }
/* 1667 */     System.arraycopy(utf8, offset, this._outputBuffer, this._outputTail, len);
/* 1668 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */   private final void _writeUTF8Segment2(byte[] utf8, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1674 */     int outputPtr = this._outputTail;
/*      */     
/*      */ 
/* 1677 */     if (outputPtr + len * 6 > this._outputEnd) {
/* 1678 */       _flushBuffer();
/* 1679 */       outputPtr = this._outputTail;
/*      */     }
/*      */     
/* 1682 */     byte[] outputBuffer = this._outputBuffer;
/* 1683 */     int[] escCodes = this._outputEscapes;
/* 1684 */     len += offset;
/*      */     
/* 1686 */     while (offset < len) {
/* 1687 */       byte b = utf8[(offset++)];
/* 1688 */       int ch = b;
/* 1689 */       if ((ch < 0) || (escCodes[ch] == 0)) {
/* 1690 */         outputBuffer[(outputPtr++)] = b;
/*      */       }
/*      */       else {
/* 1693 */         int escape = escCodes[ch];
/* 1694 */         if (escape > 0) {
/* 1695 */           outputBuffer[(outputPtr++)] = 92;
/* 1696 */           outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */         }
/*      */         else {
/* 1699 */           outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         }
/*      */       } }
/* 1702 */     this._outputTail = outputPtr;
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
/*      */   protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1716 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1718 */     int safeOutputEnd = this._outputEnd - 6;
/* 1719 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/*      */ 
/* 1722 */     while (inputPtr <= safeInputEnd) {
/* 1723 */       if (this._outputTail > safeOutputEnd) {
/* 1724 */         _flushBuffer();
/*      */       }
/*      */       
/* 1727 */       int b24 = input[(inputPtr++)] << 8;
/* 1728 */       b24 |= input[(inputPtr++)] & 0xFF;
/* 1729 */       b24 = b24 << 8 | input[(inputPtr++)] & 0xFF;
/* 1730 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1731 */       chunksBeforeLF--; if (chunksBeforeLF <= 0)
/*      */       {
/* 1733 */         this._outputBuffer[(this._outputTail++)] = 92;
/* 1734 */         this._outputBuffer[(this._outputTail++)] = 110;
/* 1735 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1740 */     int inputLeft = inputEnd - inputPtr;
/* 1741 */     if (inputLeft > 0) {
/* 1742 */       if (this._outputTail > safeOutputEnd) {
/* 1743 */         _flushBuffer();
/*      */       }
/* 1745 */       int b24 = input[(inputPtr++)] << 16;
/* 1746 */       if (inputLeft == 2) {
/* 1747 */         b24 |= (input[(inputPtr++)] & 0xFF) << 8;
/*      */       }
/* 1749 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1758 */     int inputPtr = 0;
/* 1759 */     int inputEnd = 0;
/* 1760 */     int lastFullOffset = -3;
/*      */     
/*      */ 
/* 1763 */     int safeOutputEnd = this._outputEnd - 6;
/* 1764 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/* 1766 */     while (bytesLeft > 2) {
/* 1767 */       if (inputPtr > lastFullOffset) {
/* 1768 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1769 */         inputPtr = 0;
/* 1770 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1773 */         lastFullOffset = inputEnd - 3;
/*      */       }
/* 1775 */       if (this._outputTail > safeOutputEnd) {
/* 1776 */         _flushBuffer();
/*      */       }
/* 1778 */       int b24 = readBuffer[(inputPtr++)] << 8;
/* 1779 */       b24 |= readBuffer[(inputPtr++)] & 0xFF;
/* 1780 */       b24 = b24 << 8 | readBuffer[(inputPtr++)] & 0xFF;
/* 1781 */       bytesLeft -= 3;
/* 1782 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1783 */       chunksBeforeLF--; if (chunksBeforeLF <= 0) {
/* 1784 */         this._outputBuffer[(this._outputTail++)] = 92;
/* 1785 */         this._outputBuffer[(this._outputTail++)] = 110;
/* 1786 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1791 */     if (bytesLeft > 0) {
/* 1792 */       inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1793 */       inputPtr = 0;
/* 1794 */       if (inputEnd > 0) {
/* 1795 */         if (this._outputTail > safeOutputEnd) {
/* 1796 */           _flushBuffer();
/*      */         }
/* 1798 */         int b24 = readBuffer[(inputPtr++)] << 16;
/*      */         int amount;
/* 1800 */         int amount; if (inputPtr < inputEnd) {
/* 1801 */           b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1802 */           amount = 2;
/*      */         } else {
/* 1804 */           amount = 1;
/*      */         }
/* 1806 */         this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/* 1807 */         bytesLeft -= amount;
/*      */       }
/*      */     }
/* 1810 */     return bytesLeft;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1818 */     int inputPtr = 0;
/* 1819 */     int inputEnd = 0;
/* 1820 */     int lastFullOffset = -3;
/* 1821 */     int bytesDone = 0;
/*      */     
/*      */ 
/* 1824 */     int safeOutputEnd = this._outputEnd - 6;
/* 1825 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/*      */     for (;;)
/*      */     {
/* 1829 */       if (inputPtr > lastFullOffset) {
/* 1830 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
/* 1831 */         inputPtr = 0;
/* 1832 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1835 */         lastFullOffset = inputEnd - 3;
/*      */       }
/* 1837 */       if (this._outputTail > safeOutputEnd) {
/* 1838 */         _flushBuffer();
/*      */       }
/*      */       
/* 1841 */       int b24 = readBuffer[(inputPtr++)] << 8;
/* 1842 */       b24 |= readBuffer[(inputPtr++)] & 0xFF;
/* 1843 */       b24 = b24 << 8 | readBuffer[(inputPtr++)] & 0xFF;
/* 1844 */       bytesDone += 3;
/* 1845 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1846 */       chunksBeforeLF--; if (chunksBeforeLF <= 0) {
/* 1847 */         this._outputBuffer[(this._outputTail++)] = 92;
/* 1848 */         this._outputBuffer[(this._outputTail++)] = 110;
/* 1849 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1854 */     if (inputPtr < inputEnd) {
/* 1855 */       if (this._outputTail > safeOutputEnd) {
/* 1856 */         _flushBuffer();
/*      */       }
/* 1858 */       int b24 = readBuffer[(inputPtr++)] << 16;
/* 1859 */       int amount = 1;
/* 1860 */       if (inputPtr < inputEnd) {
/* 1861 */         b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1862 */         amount = 2;
/*      */       }
/* 1864 */       bytesDone += amount;
/* 1865 */       this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*      */     }
/* 1867 */     return bytesDone;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead)
/*      */     throws IOException
/*      */   {
/* 1875 */     int i = 0;
/* 1876 */     while (inputPtr < inputEnd) {
/* 1877 */       readBuffer[(i++)] = readBuffer[(inputPtr++)];
/*      */     }
/* 1879 */     inputPtr = 0;
/* 1880 */     inputEnd = i;
/* 1881 */     maxRead = Math.min(maxRead, readBuffer.length);
/*      */     do
/*      */     {
/* 1884 */       int length = maxRead - inputEnd;
/* 1885 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 1888 */       int count = in.read(readBuffer, inputEnd, length);
/* 1889 */       if (count < 0) {
/* 1890 */         return inputEnd;
/*      */       }
/* 1892 */       inputEnd += count;
/* 1893 */     } while (inputEnd < 3);
/* 1894 */     return inputEnd;
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
/*      */   private final int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputLen)
/*      */     throws IOException
/*      */   {
/* 1912 */     if ((ch >= 55296) && 
/* 1913 */       (ch <= 57343))
/*      */     {
/* 1915 */       if ((inputOffset >= inputLen) || (cbuf == null)) {
/* 1916 */         _reportError("Split surrogate on writeRaw() input (last character)");
/*      */       }
/* 1918 */       _outputSurrogates(ch, cbuf[inputOffset]);
/* 1919 */       return inputOffset + 1;
/*      */     }
/*      */     
/* 1922 */     byte[] bbuf = this._outputBuffer;
/* 1923 */     bbuf[(this._outputTail++)] = ((byte)(0xE0 | ch >> 12));
/* 1924 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | ch >> 6 & 0x3F));
/* 1925 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/* 1926 */     return inputOffset;
/*      */   }
/*      */   
/*      */   protected final void _outputSurrogates(int surr1, int surr2) throws IOException
/*      */   {
/* 1931 */     int c = _decodeSurrogate(surr1, surr2);
/* 1932 */     if (this._outputTail + 4 > this._outputEnd) {
/* 1933 */       _flushBuffer();
/*      */     }
/* 1935 */     byte[] bbuf = this._outputBuffer;
/* 1936 */     bbuf[(this._outputTail++)] = ((byte)(0xF0 | c >> 18));
/* 1937 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | c >> 12 & 0x3F));
/* 1938 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 1939 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | c & 0x3F));
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
/*      */   private final int _outputMultiByteChar(int ch, int outputPtr)
/*      */     throws IOException
/*      */   {
/* 1953 */     byte[] bbuf = this._outputBuffer;
/* 1954 */     if ((ch >= 55296) && (ch <= 57343))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1959 */       bbuf[(outputPtr++)] = 92;
/* 1960 */       bbuf[(outputPtr++)] = 117;
/*      */       
/* 1962 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch >> 12 & 0xF)];
/* 1963 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch >> 8 & 0xF)];
/* 1964 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch >> 4 & 0xF)];
/* 1965 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch & 0xF)];
/*      */     }
/*      */     else {
/* 1968 */       bbuf[(outputPtr++)] = ((byte)(0xE0 | ch >> 12));
/* 1969 */       bbuf[(outputPtr++)] = ((byte)(0x80 | ch >> 6 & 0x3F));
/* 1970 */       bbuf[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */     }
/* 1972 */     return outputPtr;
/*      */   }
/*      */   
/*      */   private final void _writeNull() throws IOException
/*      */   {
/* 1977 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 1978 */       _flushBuffer();
/*      */     }
/* 1980 */     System.arraycopy(NULL_BYTES, 0, this._outputBuffer, this._outputTail, 4);
/* 1981 */     this._outputTail += 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int _writeGenericEscape(int charToEscape, int outputPtr)
/*      */     throws IOException
/*      */   {
/* 1991 */     byte[] bbuf = this._outputBuffer;
/* 1992 */     bbuf[(outputPtr++)] = 92;
/* 1993 */     bbuf[(outputPtr++)] = 117;
/* 1994 */     if (charToEscape > 255) {
/* 1995 */       int hi = charToEscape >> 8 & 0xFF;
/* 1996 */       bbuf[(outputPtr++)] = HEX_CHARS[(hi >> 4)];
/* 1997 */       bbuf[(outputPtr++)] = HEX_CHARS[(hi & 0xF)];
/* 1998 */       charToEscape &= 0xFF;
/*      */     } else {
/* 2000 */       bbuf[(outputPtr++)] = 48;
/* 2001 */       bbuf[(outputPtr++)] = 48;
/*      */     }
/*      */     
/* 2004 */     bbuf[(outputPtr++)] = HEX_CHARS[(charToEscape >> 4)];
/* 2005 */     bbuf[(outputPtr++)] = HEX_CHARS[(charToEscape & 0xF)];
/* 2006 */     return outputPtr;
/*      */   }
/*      */   
/*      */   protected final void _flushBuffer() throws IOException
/*      */   {
/* 2011 */     int len = this._outputTail;
/* 2012 */     if (len > 0) {
/* 2013 */       this._outputTail = 0;
/* 2014 */       this._outputStream.write(this._outputBuffer, 0, len);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\UTF8JsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */