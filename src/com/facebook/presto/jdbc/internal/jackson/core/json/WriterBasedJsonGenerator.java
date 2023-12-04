/*      */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.PrettyPrinter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharacterEscapes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.IOContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.NumberOutput;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Writer;
/*      */ import java.math.BigDecimal;
/*      */ 
/*      */ public final class WriterBasedJsonGenerator extends JsonGeneratorImpl
/*      */ {
/*      */   protected static final int SHORT_WRITE = 32;
/*   19 */   protected static final char[] HEX_CHARS = ;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Writer _writer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   35 */   protected char _quoteChar = '"';
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected char[] _outputBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _outputHead;
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
/*      */   protected int _outputEnd;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected char[] _entityBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SerializableString _currentEscape;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public WriterBasedJsonGenerator(IOContext ctxt, int features, com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec codec, Writer w)
/*      */   {
/*   87 */     super(ctxt, features, codec);
/*   88 */     this._writer = w;
/*   89 */     this._outputBuffer = ctxt.allocConcatBuffer();
/*   90 */     this._outputEnd = this._outputBuffer.length;
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
/*  101 */     return this._writer;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOutputBuffered()
/*      */   {
/*  107 */     int len = this._outputTail - this._outputHead;
/*  108 */     return Math.max(0, len);
/*      */   }
/*      */   
/*      */   public boolean canWriteFormattedNumbers()
/*      */   {
/*  113 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeFieldName(String name)
/*      */     throws IOException
/*      */   {
/*  124 */     int status = this._writeContext.writeFieldName(name);
/*  125 */     if (status == 4) {
/*  126 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  128 */     _writeFieldName(name, status == 1);
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeFieldName(SerializableString name)
/*      */     throws IOException
/*      */   {
/*  135 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  136 */     if (status == 4) {
/*  137 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  139 */     _writeFieldName(name, status == 1);
/*      */   }
/*      */   
/*      */   protected void _writeFieldName(String name, boolean commaBefore) throws IOException
/*      */   {
/*  144 */     if (this._cfgPrettyPrinter != null) {
/*  145 */       _writePPFieldName(name, commaBefore);
/*  146 */       return;
/*      */     }
/*      */     
/*  149 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  150 */       _flushBuffer();
/*      */     }
/*  152 */     if (commaBefore) {
/*  153 */       this._outputBuffer[(this._outputTail++)] = ',';
/*      */     }
/*      */     
/*  156 */     if (this._cfgUnqNames) {
/*  157 */       _writeString(name);
/*  158 */       return;
/*      */     }
/*      */     
/*  161 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     
/*  163 */     _writeString(name);
/*      */     
/*  165 */     if (this._outputTail >= this._outputEnd) {
/*  166 */       _flushBuffer();
/*      */     }
/*  168 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   protected void _writeFieldName(SerializableString name, boolean commaBefore) throws IOException
/*      */   {
/*  173 */     if (this._cfgPrettyPrinter != null) {
/*  174 */       _writePPFieldName(name, commaBefore);
/*  175 */       return;
/*      */     }
/*      */     
/*  178 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  179 */       _flushBuffer();
/*      */     }
/*  181 */     if (commaBefore) {
/*  182 */       this._outputBuffer[(this._outputTail++)] = ',';
/*      */     }
/*      */     
/*  185 */     char[] quoted = name.asQuotedChars();
/*  186 */     if (this._cfgUnqNames) {
/*  187 */       writeRaw(quoted, 0, quoted.length);
/*  188 */       return;
/*      */     }
/*      */     
/*  191 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     
/*  193 */     int qlen = quoted.length;
/*  194 */     if (this._outputTail + qlen + 1 >= this._outputEnd) {
/*  195 */       writeRaw(quoted, 0, qlen);
/*      */       
/*  197 */       if (this._outputTail >= this._outputEnd) {
/*  198 */         _flushBuffer();
/*      */       }
/*  200 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     } else {
/*  202 */       System.arraycopy(quoted, 0, this._outputBuffer, this._outputTail, qlen);
/*  203 */       this._outputTail += qlen;
/*  204 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeStartArray()
/*      */     throws IOException
/*      */   {
/*  217 */     _verifyValueWrite("start an array");
/*  218 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  219 */     if (this._cfgPrettyPrinter != null) {
/*  220 */       this._cfgPrettyPrinter.writeStartArray(this);
/*      */     } else {
/*  222 */       if (this._outputTail >= this._outputEnd) {
/*  223 */         _flushBuffer();
/*      */       }
/*  225 */       this._outputBuffer[(this._outputTail++)] = '[';
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeEndArray()
/*      */     throws IOException
/*      */   {
/*  232 */     if (!this._writeContext.inArray()) {
/*  233 */       _reportError("Current context not Array but " + this._writeContext.typeDesc());
/*      */     }
/*  235 */     if (this._cfgPrettyPrinter != null) {
/*  236 */       this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  238 */       if (this._outputTail >= this._outputEnd) {
/*  239 */         _flushBuffer();
/*      */       }
/*  241 */       this._outputBuffer[(this._outputTail++)] = ']';
/*      */     }
/*  243 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */   
/*      */   public void writeStartObject(Object forValue)
/*      */     throws IOException
/*      */   {
/*  249 */     _verifyValueWrite("start an object");
/*  250 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext();
/*  251 */     this._writeContext = ctxt;
/*  252 */     if (forValue != null) {
/*  253 */       ctxt.setCurrentValue(forValue);
/*      */     }
/*  255 */     if (this._cfgPrettyPrinter != null) {
/*  256 */       this._cfgPrettyPrinter.writeStartObject(this);
/*      */     } else {
/*  258 */       if (this._outputTail >= this._outputEnd) {
/*  259 */         _flushBuffer();
/*      */       }
/*  261 */       this._outputBuffer[(this._outputTail++)] = '{';
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeStartObject()
/*      */     throws IOException
/*      */   {
/*  268 */     _verifyValueWrite("start an object");
/*  269 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  270 */     if (this._cfgPrettyPrinter != null) {
/*  271 */       this._cfgPrettyPrinter.writeStartObject(this);
/*      */     } else {
/*  273 */       if (this._outputTail >= this._outputEnd) {
/*  274 */         _flushBuffer();
/*      */       }
/*  276 */       this._outputBuffer[(this._outputTail++)] = '{';
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeEndObject()
/*      */     throws IOException
/*      */   {
/*  283 */     if (!this._writeContext.inObject()) {
/*  284 */       _reportError("Current context not Object but " + this._writeContext.typeDesc());
/*      */     }
/*  286 */     if (this._cfgPrettyPrinter != null) {
/*  287 */       this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  289 */       if (this._outputTail >= this._outputEnd) {
/*  290 */         _flushBuffer();
/*      */       }
/*  292 */       this._outputBuffer[(this._outputTail++)] = '}';
/*      */     }
/*  294 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _writePPFieldName(String name, boolean commaBefore)
/*      */     throws IOException
/*      */   {
/*  303 */     if (commaBefore) {
/*  304 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  306 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*      */     
/*  309 */     if (this._cfgUnqNames) {
/*  310 */       _writeString(name);
/*      */     } else {
/*  312 */       if (this._outputTail >= this._outputEnd) {
/*  313 */         _flushBuffer();
/*      */       }
/*  315 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  316 */       _writeString(name);
/*  317 */       if (this._outputTail >= this._outputEnd) {
/*  318 */         _flushBuffer();
/*      */       }
/*  320 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _writePPFieldName(SerializableString name, boolean commaBefore) throws IOException
/*      */   {
/*  326 */     if (commaBefore) {
/*  327 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  329 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*      */     
/*  332 */     char[] quoted = name.asQuotedChars();
/*  333 */     if (this._cfgUnqNames) {
/*  334 */       writeRaw(quoted, 0, quoted.length);
/*      */     } else {
/*  336 */       if (this._outputTail >= this._outputEnd) {
/*  337 */         _flushBuffer();
/*      */       }
/*  339 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  340 */       writeRaw(quoted, 0, quoted.length);
/*  341 */       if (this._outputTail >= this._outputEnd) {
/*  342 */         _flushBuffer();
/*      */       }
/*  344 */       this._outputBuffer[(this._outputTail++)] = this._quoteChar;
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
/*  357 */     _verifyValueWrite("write a string");
/*  358 */     if (text == null) {
/*  359 */       _writeNull();
/*  360 */       return;
/*      */     }
/*  362 */     if (this._outputTail >= this._outputEnd) {
/*  363 */       _flushBuffer();
/*      */     }
/*  365 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  366 */     _writeString(text);
/*      */     
/*  368 */     if (this._outputTail >= this._outputEnd) {
/*  369 */       _flushBuffer();
/*      */     }
/*  371 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeString(char[] text, int offset, int len)
/*      */     throws IOException
/*      */   {
/*  377 */     _verifyValueWrite("write a string");
/*  378 */     if (this._outputTail >= this._outputEnd) {
/*  379 */       _flushBuffer();
/*      */     }
/*  381 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  382 */     _writeString(text, offset, len);
/*      */     
/*  384 */     if (this._outputTail >= this._outputEnd) {
/*  385 */       _flushBuffer();
/*      */     }
/*  387 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeString(SerializableString sstr)
/*      */     throws IOException
/*      */   {
/*  393 */     _verifyValueWrite("write a string");
/*  394 */     if (this._outputTail >= this._outputEnd) {
/*  395 */       _flushBuffer();
/*      */     }
/*  397 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */     
/*  399 */     char[] text = sstr.asQuotedChars();
/*  400 */     int len = text.length;
/*      */     
/*  402 */     if (len < 32) {
/*  403 */       int room = this._outputEnd - this._outputTail;
/*  404 */       if (len > room) {
/*  405 */         _flushBuffer();
/*      */       }
/*  407 */       System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
/*  408 */       this._outputTail += len;
/*      */     }
/*      */     else {
/*  411 */       _flushBuffer();
/*  412 */       this._writer.write(text, 0, len);
/*      */     }
/*  414 */     if (this._outputTail >= this._outputEnd) {
/*  415 */       _flushBuffer();
/*      */     }
/*  417 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  423 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  429 */     _reportUnsupportedOperation();
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
/*      */     throws IOException
/*      */   {
/*  442 */     int len = text.length();
/*  443 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  445 */     if (room == 0) {
/*  446 */       _flushBuffer();
/*  447 */       room = this._outputEnd - this._outputTail;
/*      */     }
/*      */     
/*  450 */     if (room >= len) {
/*  451 */       text.getChars(0, len, this._outputBuffer, this._outputTail);
/*  452 */       this._outputTail += len;
/*      */     } else {
/*  454 */       writeRawLong(text);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRaw(String text, int start, int len)
/*      */     throws IOException
/*      */   {
/*  462 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  464 */     if (room < len) {
/*  465 */       _flushBuffer();
/*  466 */       room = this._outputEnd - this._outputTail;
/*      */     }
/*      */     
/*  469 */     if (room >= len) {
/*  470 */       text.getChars(start, start + len, this._outputBuffer, this._outputTail);
/*  471 */       this._outputTail += len;
/*      */     } else {
/*  473 */       writeRawLong(text.substring(start, start + len));
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeRaw(SerializableString text)
/*      */     throws IOException
/*      */   {
/*  480 */     writeRaw(text.getValue());
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRaw(char[] text, int offset, int len)
/*      */     throws IOException
/*      */   {
/*  487 */     if (len < 32) {
/*  488 */       int room = this._outputEnd - this._outputTail;
/*  489 */       if (len > room) {
/*  490 */         _flushBuffer();
/*      */       }
/*  492 */       System.arraycopy(text, offset, this._outputBuffer, this._outputTail, len);
/*  493 */       this._outputTail += len;
/*  494 */       return;
/*      */     }
/*      */     
/*  497 */     _flushBuffer();
/*  498 */     this._writer.write(text, offset, len);
/*      */   }
/*      */   
/*      */   public void writeRaw(char c)
/*      */     throws IOException
/*      */   {
/*  504 */     if (this._outputTail >= this._outputEnd) {
/*  505 */       _flushBuffer();
/*      */     }
/*  507 */     this._outputBuffer[(this._outputTail++)] = c;
/*      */   }
/*      */   
/*      */   private void writeRawLong(String text) throws IOException
/*      */   {
/*  512 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  514 */     text.getChars(0, room, this._outputBuffer, this._outputTail);
/*  515 */     this._outputTail += room;
/*  516 */     _flushBuffer();
/*  517 */     int offset = room;
/*  518 */     int len = text.length() - room;
/*      */     
/*  520 */     while (len > this._outputEnd) {
/*  521 */       int amount = this._outputEnd;
/*  522 */       text.getChars(offset, offset + amount, this._outputBuffer, 0);
/*  523 */       this._outputHead = 0;
/*  524 */       this._outputTail = amount;
/*  525 */       _flushBuffer();
/*  526 */       offset += amount;
/*  527 */       len -= amount;
/*      */     }
/*      */     
/*  530 */     text.getChars(offset, offset + len, this._outputBuffer, 0);
/*  531 */     this._outputHead = 0;
/*  532 */     this._outputTail = len;
/*      */   }
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
/*  545 */     _verifyValueWrite("write a binary value");
/*      */     
/*  547 */     if (this._outputTail >= this._outputEnd) {
/*  548 */       _flushBuffer();
/*      */     }
/*  550 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  551 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  553 */     if (this._outputTail >= this._outputEnd) {
/*  554 */       _flushBuffer();
/*      */     }
/*  556 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  564 */     _verifyValueWrite("write a binary value");
/*      */     
/*  566 */     if (this._outputTail >= this._outputEnd) {
/*  567 */       _flushBuffer();
/*      */     }
/*  569 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  570 */     byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
/*      */     int bytes;
/*      */     try { int bytes;
/*  573 */       if (dataLength < 0) {
/*  574 */         bytes = _writeBinary(b64variant, data, encodingBuffer);
/*      */       } else {
/*  576 */         int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
/*  577 */         if (missing > 0) {
/*  578 */           _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
/*      */         }
/*  580 */         bytes = dataLength;
/*      */       }
/*      */     } finally {
/*  583 */       this._ioContext.releaseBase64Buffer(encodingBuffer);
/*      */     }
/*      */     
/*  586 */     if (this._outputTail >= this._outputEnd) {
/*  587 */       _flushBuffer();
/*      */     }
/*  589 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  590 */     return bytes;
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
/*  602 */     _verifyValueWrite("write a number");
/*  603 */     if (this._cfgNumbersAsStrings) {
/*  604 */       _writeQuotedShort(s);
/*  605 */       return;
/*      */     }
/*      */     
/*  608 */     if (this._outputTail + 6 >= this._outputEnd) {
/*  609 */       _flushBuffer();
/*      */     }
/*  611 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedShort(short s) throws IOException {
/*  615 */     if (this._outputTail + 8 >= this._outputEnd) {
/*  616 */       _flushBuffer();
/*      */     }
/*  618 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  619 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*  620 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeNumber(int i)
/*      */     throws IOException
/*      */   {
/*  626 */     _verifyValueWrite("write a number");
/*  627 */     if (this._cfgNumbersAsStrings) {
/*  628 */       _writeQuotedInt(i);
/*  629 */       return;
/*      */     }
/*      */     
/*  632 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  633 */       _flushBuffer();
/*      */     }
/*  635 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedInt(int i) throws IOException {
/*  639 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  640 */       _flushBuffer();
/*      */     }
/*  642 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  643 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  644 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeNumber(long l)
/*      */     throws IOException
/*      */   {
/*  650 */     _verifyValueWrite("write a number");
/*  651 */     if (this._cfgNumbersAsStrings) {
/*  652 */       _writeQuotedLong(l);
/*  653 */       return;
/*      */     }
/*  655 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  657 */       _flushBuffer();
/*      */     }
/*  659 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedLong(long l) throws IOException {
/*  663 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  664 */       _flushBuffer();
/*      */     }
/*  666 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  667 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*  668 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeNumber(java.math.BigInteger value)
/*      */     throws IOException
/*      */   {
/*  676 */     _verifyValueWrite("write a number");
/*  677 */     if (value == null) {
/*  678 */       _writeNull();
/*  679 */     } else if (this._cfgNumbersAsStrings) {
/*  680 */       _writeQuotedRaw(value.toString());
/*      */     } else {
/*  682 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(double d)
/*      */     throws IOException
/*      */   {
/*  690 */     if ((this._cfgNumbersAsStrings) || ((isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS)) && ((Double.isNaN(d)) || (Double.isInfinite(d)))))
/*      */     {
/*      */ 
/*  693 */       writeString(String.valueOf(d));
/*  694 */       return;
/*      */     }
/*      */     
/*  697 */     _verifyValueWrite("write a number");
/*  698 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */   
/*      */   public void writeNumber(float f)
/*      */     throws IOException
/*      */   {
/*  704 */     if ((this._cfgNumbersAsStrings) || ((isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS)) && ((Float.isNaN(f)) || (Float.isInfinite(f)))))
/*      */     {
/*      */ 
/*  707 */       writeString(String.valueOf(f));
/*  708 */       return;
/*      */     }
/*      */     
/*  711 */     _verifyValueWrite("write a number");
/*  712 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(BigDecimal value)
/*      */     throws IOException
/*      */   {
/*  719 */     _verifyValueWrite("write a number");
/*  720 */     if (value == null) {
/*  721 */       _writeNull();
/*  722 */     } else if (this._cfgNumbersAsStrings) {
/*  723 */       String raw = isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) ? value.toPlainString() : value.toString();
/*  724 */       _writeQuotedRaw(raw);
/*  725 */     } else if (isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)) {
/*  726 */       writeRaw(value.toPlainString());
/*      */     } else {
/*  728 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeNumber(String encodedValue)
/*      */     throws IOException
/*      */   {
/*  735 */     _verifyValueWrite("write a number");
/*  736 */     if (this._cfgNumbersAsStrings) {
/*  737 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/*  739 */       writeRaw(encodedValue);
/*      */     }
/*      */   }
/*      */   
/*      */   private void _writeQuotedRaw(String value) throws IOException
/*      */   {
/*  745 */     if (this._outputTail >= this._outputEnd) {
/*  746 */       _flushBuffer();
/*      */     }
/*  748 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*  749 */     writeRaw(value);
/*  750 */     if (this._outputTail >= this._outputEnd) {
/*  751 */       _flushBuffer();
/*      */     }
/*  753 */     this._outputBuffer[(this._outputTail++)] = this._quoteChar;
/*      */   }
/*      */   
/*      */   public void writeBoolean(boolean state)
/*      */     throws IOException
/*      */   {
/*  759 */     _verifyValueWrite("write a boolean value");
/*  760 */     if (this._outputTail + 5 >= this._outputEnd) {
/*  761 */       _flushBuffer();
/*      */     }
/*  763 */     int ptr = this._outputTail;
/*  764 */     char[] buf = this._outputBuffer;
/*  765 */     if (state) {
/*  766 */       buf[ptr] = 't';
/*  767 */       buf[(++ptr)] = 'r';
/*  768 */       buf[(++ptr)] = 'u';
/*  769 */       buf[(++ptr)] = 'e';
/*      */     } else {
/*  771 */       buf[ptr] = 'f';
/*  772 */       buf[(++ptr)] = 'a';
/*  773 */       buf[(++ptr)] = 'l';
/*  774 */       buf[(++ptr)] = 's';
/*  775 */       buf[(++ptr)] = 'e';
/*      */     }
/*  777 */     this._outputTail = (ptr + 1);
/*      */   }
/*      */   
/*      */   public void writeNull() throws IOException
/*      */   {
/*  782 */     _verifyValueWrite("write a null");
/*  783 */     _writeNull();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _verifyValueWrite(String typeMsg)
/*      */     throws IOException
/*      */   {
/*  795 */     if (this._cfgPrettyPrinter != null)
/*      */     {
/*  797 */       _verifyPrettyValueWrite(typeMsg);
/*  798 */       return;
/*      */     }
/*      */     
/*  801 */     int status = this._writeContext.writeValue();
/*  802 */     if (status == 5)
/*  803 */       _reportError("Can not " + typeMsg + ", expecting field name");
/*      */     char c;
/*  805 */     switch (status) {
/*      */     case 1: 
/*  807 */       c = ',';
/*  808 */       break;
/*      */     case 2: 
/*  810 */       c = ':';
/*  811 */       break;
/*      */     case 3: 
/*  813 */       if (this._rootValueSeparator != null) {
/*  814 */         writeRaw(this._rootValueSeparator.getValue());
/*      */       }
/*  816 */       return;
/*      */     case 0: 
/*      */     default: 
/*  819 */       return;
/*      */     }
/*  821 */     if (this._outputTail >= this._outputEnd) {
/*  822 */       _flushBuffer();
/*      */     }
/*  824 */     this._outputBuffer[this._outputTail] = c;
/*  825 */     this._outputTail += 1;
/*      */   }
/*      */   
/*      */   protected void _verifyPrettyValueWrite(String typeMsg) throws IOException
/*      */   {
/*  830 */     int status = this._writeContext.writeValue();
/*  831 */     if (status == 5) {
/*  832 */       _reportError("Can not " + typeMsg + ", expecting field name");
/*      */     }
/*      */     
/*      */ 
/*  836 */     switch (status) {
/*      */     case 1: 
/*  838 */       this._cfgPrettyPrinter.writeArrayValueSeparator(this);
/*  839 */       break;
/*      */     case 2: 
/*  841 */       this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
/*  842 */       break;
/*      */     case 3: 
/*  844 */       this._cfgPrettyPrinter.writeRootValueSeparator(this);
/*  845 */       break;
/*      */     
/*      */     case 0: 
/*  848 */       if (this._writeContext.inArray()) {
/*  849 */         this._cfgPrettyPrinter.beforeArrayValues(this);
/*  850 */       } else if (this._writeContext.inObject()) {
/*  851 */         this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */       }
/*      */       break;
/*      */     default: 
/*  855 */       _throwInternal();
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
/*  869 */     _flushBuffer();
/*  870 */     if ((this._writer != null) && 
/*  871 */       (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))) {
/*  872 */       this._writer.flush();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  880 */     super.close();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  886 */     if ((this._outputBuffer != null) && (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT))) {
/*      */       for (;;)
/*      */       {
/*  889 */         com.facebook.presto.jdbc.internal.jackson.core.JsonStreamContext ctxt = getOutputContext();
/*  890 */         if (ctxt.inArray()) {
/*  891 */           writeEndArray();
/*  892 */         } else { if (!ctxt.inObject()) break;
/*  893 */           writeEndObject();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  899 */     _flushBuffer();
/*  900 */     this._outputHead = 0;
/*  901 */     this._outputTail = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  909 */     if (this._writer != null) {
/*  910 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET))) {
/*  911 */         this._writer.close();
/*  912 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))
/*      */       {
/*  914 */         this._writer.flush();
/*      */       }
/*      */     }
/*      */     
/*  918 */     _releaseBuffers();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */   {
/*  924 */     char[] buf = this._outputBuffer;
/*  925 */     if (buf != null) {
/*  926 */       this._outputBuffer = null;
/*  927 */       this._ioContext.releaseConcatBuffer(buf);
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
/*      */   private void _writeString(String text)
/*      */     throws IOException
/*      */   {
/*  944 */     int len = text.length();
/*  945 */     if (len > this._outputEnd) {
/*  946 */       _writeLongString(text);
/*  947 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  952 */     if (this._outputTail + len > this._outputEnd) {
/*  953 */       _flushBuffer();
/*      */     }
/*  955 */     text.getChars(0, len, this._outputBuffer, this._outputTail);
/*      */     
/*  957 */     if (this._characterEscapes != null) {
/*  958 */       _writeStringCustom(len);
/*  959 */     } else if (this._maximumNonEscapedChar != 0) {
/*  960 */       _writeStringASCII(len, this._maximumNonEscapedChar);
/*      */     } else {
/*  962 */       _writeString2(len);
/*      */     }
/*      */   }
/*      */   
/*      */   private void _writeString2(int len)
/*      */     throws IOException
/*      */   {
/*  969 */     int end = this._outputTail + len;
/*  970 */     int[] escCodes = this._outputEscapes;
/*  971 */     int escLen = escCodes.length;
/*      */     
/*      */ 
/*  974 */     while (this._outputTail < end)
/*      */     {
/*      */       for (;;)
/*      */       {
/*  978 */         char c = this._outputBuffer[this._outputTail];
/*  979 */         if ((c < escLen) && (escCodes[c] != 0)) {
/*      */           break;
/*      */         }
/*  982 */         if (++this._outputTail >= end) {
/*      */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  991 */       int flushLen = this._outputTail - this._outputHead;
/*  992 */       if (flushLen > 0) {
/*  993 */         this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  998 */       char c = this._outputBuffer[(this._outputTail++)];
/*  999 */       _prependOrWriteCharacterEscape(c, escCodes[c]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _writeLongString(String text)
/*      */     throws IOException
/*      */   {
/* 1010 */     _flushBuffer();
/*      */     
/*      */ 
/* 1013 */     int textLen = text.length();
/* 1014 */     int offset = 0;
/*      */     do {
/* 1016 */       int max = this._outputEnd;
/* 1017 */       int segmentLen = offset + max > textLen ? textLen - offset : max;
/*      */       
/* 1019 */       text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
/* 1020 */       if (this._characterEscapes != null) {
/* 1021 */         _writeSegmentCustom(segmentLen);
/* 1022 */       } else if (this._maximumNonEscapedChar != 0) {
/* 1023 */         _writeSegmentASCII(segmentLen, this._maximumNonEscapedChar);
/*      */       } else {
/* 1025 */         _writeSegment(segmentLen);
/*      */       }
/* 1027 */       offset += segmentLen;
/* 1028 */     } while (offset < textLen);
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
/*      */   private void _writeSegment(int end)
/*      */     throws IOException
/*      */   {
/* 1042 */     int[] escCodes = this._outputEscapes;
/* 1043 */     int escLen = escCodes.length;
/*      */     
/* 1045 */     int ptr = 0;
/* 1046 */     int start = ptr;
/*      */     
/*      */ 
/* 1049 */     while (ptr < end)
/*      */     {
/*      */       char c;
/*      */       for (;;) {
/* 1053 */         c = this._outputBuffer[ptr];
/* 1054 */         if ((c >= escLen) || (escCodes[c] == 0))
/*      */         {
/*      */ 
/* 1057 */           ptr++; if (ptr >= end) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1066 */       int flushLen = ptr - start;
/* 1067 */       if (flushLen > 0) {
/* 1068 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1069 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1073 */       ptr++;
/*      */       
/* 1075 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCodes[c]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _writeString(char[] text, int offset, int len)
/*      */     throws IOException
/*      */   {
/* 1085 */     if (this._characterEscapes != null) {
/* 1086 */       _writeStringCustom(text, offset, len);
/* 1087 */       return;
/*      */     }
/* 1089 */     if (this._maximumNonEscapedChar != 0) {
/* 1090 */       _writeStringASCII(text, offset, len, this._maximumNonEscapedChar);
/* 1091 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1098 */     len += offset;
/* 1099 */     int[] escCodes = this._outputEscapes;
/* 1100 */     int escLen = escCodes.length;
/* 1101 */     while (offset < len) {
/* 1102 */       int start = offset;
/*      */       for (;;)
/*      */       {
/* 1105 */         char c = text[offset];
/* 1106 */         if ((c < escLen) && (escCodes[c] != 0)) {
/*      */           break;
/*      */         }
/* 1109 */         offset++; if (offset >= len) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1115 */       int newAmount = offset - start;
/* 1116 */       if (newAmount < 32)
/*      */       {
/* 1118 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1119 */           _flushBuffer();
/*      */         }
/* 1121 */         if (newAmount > 0) {
/* 1122 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1123 */           this._outputTail += newAmount;
/*      */         }
/*      */       } else {
/* 1126 */         _flushBuffer();
/* 1127 */         this._writer.write(text, start, newAmount);
/*      */       }
/*      */       
/* 1130 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1134 */       char c = text[(offset++)];
/* 1135 */       _appendCharacterEscape(c, escCodes[c]);
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
/*      */   private void _writeStringASCII(int len, int maxNonEscaped)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1153 */     int end = this._outputTail + len;
/* 1154 */     int[] escCodes = this._outputEscapes;
/* 1155 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1156 */     int escCode = 0;
/*      */     
/*      */ 
/* 1159 */     while (this._outputTail < end)
/*      */     {
/*      */       char c;
/*      */       do
/*      */       {
/* 1164 */         c = this._outputBuffer[this._outputTail];
/* 1165 */         if (c < escLimit) {
/* 1166 */           escCode = escCodes[c];
/* 1167 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1170 */         } else if (c > maxNonEscaped) {
/* 1171 */           escCode = -1;
/* 1172 */           break;
/*      */         }
/* 1174 */       } while (++this._outputTail < end);
/* 1175 */       break;
/*      */       
/*      */ 
/* 1178 */       int flushLen = this._outputTail - this._outputHead;
/* 1179 */       if (flushLen > 0) {
/* 1180 */         this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */       }
/* 1182 */       this._outputTail += 1;
/* 1183 */       _prependOrWriteCharacterEscape(c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */   private void _writeSegmentASCII(int end, int maxNonEscaped)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1190 */     int[] escCodes = this._outputEscapes;
/* 1191 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1193 */     int ptr = 0;
/* 1194 */     int escCode = 0;
/* 1195 */     int start = ptr;
/*      */     
/*      */ 
/* 1198 */     while (ptr < end)
/*      */     {
/*      */       char c;
/*      */       for (;;) {
/* 1202 */         c = this._outputBuffer[ptr];
/* 1203 */         if (c < escLimit) {
/* 1204 */           escCode = escCodes[c];
/* 1205 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1208 */         } else if (c > maxNonEscaped) {
/* 1209 */           escCode = -1;
/* 1210 */           break;
/*      */         }
/* 1212 */         ptr++; if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1216 */       int flushLen = ptr - start;
/* 1217 */       if (flushLen > 0) {
/* 1218 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1219 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1223 */       ptr++;
/* 1224 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void _writeStringASCII(char[] text, int offset, int len, int maxNonEscaped)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1232 */     len += offset;
/* 1233 */     int[] escCodes = this._outputEscapes;
/* 1234 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1236 */     int escCode = 0;
/*      */     
/* 1238 */     while (offset < len) {
/* 1239 */       int start = offset;
/*      */       char c;
/*      */       for (;;)
/*      */       {
/* 1243 */         c = text[offset];
/* 1244 */         if (c < escLimit) {
/* 1245 */           escCode = escCodes[c];
/* 1246 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1249 */         } else if (c > maxNonEscaped) {
/* 1250 */           escCode = -1;
/* 1251 */           break;
/*      */         }
/* 1253 */         offset++; if (offset >= len) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1259 */       int newAmount = offset - start;
/* 1260 */       if (newAmount < 32)
/*      */       {
/* 1262 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1263 */           _flushBuffer();
/*      */         }
/* 1265 */         if (newAmount > 0) {
/* 1266 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1267 */           this._outputTail += newAmount;
/*      */         }
/*      */       } else {
/* 1270 */         _flushBuffer();
/* 1271 */         this._writer.write(text, start, newAmount);
/*      */       }
/*      */       
/* 1274 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1278 */       offset++;
/* 1279 */       _appendCharacterEscape(c, escCode);
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
/*      */   private void _writeStringCustom(int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1297 */     int end = this._outputTail + len;
/* 1298 */     int[] escCodes = this._outputEscapes;
/* 1299 */     int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
/* 1300 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1301 */     int escCode = 0;
/* 1302 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/*      */ 
/* 1305 */     while (this._outputTail < end)
/*      */     {
/*      */       char c;
/*      */       do
/*      */       {
/* 1310 */         c = this._outputBuffer[this._outputTail];
/* 1311 */         if (c < escLimit) {
/* 1312 */           escCode = escCodes[c];
/* 1313 */           if (escCode != 0)
/*      */             break;
/*      */         } else {
/* 1316 */           if (c > maxNonEscaped) {
/* 1317 */             escCode = -1;
/* 1318 */             break;
/*      */           }
/* 1320 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1321 */             escCode = -2;
/* 1322 */             break;
/*      */           }
/*      */         }
/* 1325 */       } while (++this._outputTail < end);
/* 1326 */       break;
/*      */       
/*      */ 
/* 1329 */       int flushLen = this._outputTail - this._outputHead;
/* 1330 */       if (flushLen > 0) {
/* 1331 */         this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */       }
/* 1333 */       this._outputTail += 1;
/* 1334 */       _prependOrWriteCharacterEscape(c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */   private void _writeSegmentCustom(int end)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1341 */     int[] escCodes = this._outputEscapes;
/* 1342 */     int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
/* 1343 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1344 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1346 */     int ptr = 0;
/* 1347 */     int escCode = 0;
/* 1348 */     int start = ptr;
/*      */     
/*      */ 
/* 1351 */     while (ptr < end)
/*      */     {
/*      */       char c;
/*      */       for (;;) {
/* 1355 */         c = this._outputBuffer[ptr];
/* 1356 */         if (c < escLimit) {
/* 1357 */           escCode = escCodes[c];
/* 1358 */           if (escCode != 0)
/*      */             break;
/*      */         } else {
/* 1361 */           if (c > maxNonEscaped) {
/* 1362 */             escCode = -1;
/* 1363 */             break;
/*      */           }
/* 1365 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1366 */             escCode = -2;
/* 1367 */             break;
/*      */           }
/*      */         }
/* 1370 */         ptr++; if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1374 */       int flushLen = ptr - start;
/* 1375 */       if (flushLen > 0) {
/* 1376 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1377 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1381 */       ptr++;
/* 1382 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */   private void _writeStringCustom(char[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1389 */     len += offset;
/* 1390 */     int[] escCodes = this._outputEscapes;
/* 1391 */     int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
/* 1392 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1393 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1395 */     int escCode = 0;
/*      */     
/* 1397 */     while (offset < len) {
/* 1398 */       int start = offset;
/*      */       char c;
/*      */       for (;;)
/*      */       {
/* 1402 */         c = text[offset];
/* 1403 */         if (c < escLimit) {
/* 1404 */           escCode = escCodes[c];
/* 1405 */           if (escCode != 0)
/*      */             break;
/*      */         } else {
/* 1408 */           if (c > maxNonEscaped) {
/* 1409 */             escCode = -1;
/* 1410 */             break;
/*      */           }
/* 1412 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1413 */             escCode = -2;
/* 1414 */             break;
/*      */           }
/*      */         }
/* 1417 */         offset++; if (offset >= len) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1423 */       int newAmount = offset - start;
/* 1424 */       if (newAmount < 32)
/*      */       {
/* 1426 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1427 */           _flushBuffer();
/*      */         }
/* 1429 */         if (newAmount > 0) {
/* 1430 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1431 */           this._outputTail += newAmount;
/*      */         }
/*      */       } else {
/* 1434 */         _flushBuffer();
/* 1435 */         this._writer.write(text, start, newAmount);
/*      */       }
/*      */       
/* 1438 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1442 */       offset++;
/* 1443 */       _appendCharacterEscape(c, escCode);
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
/*      */   protected void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1457 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1459 */     int safeOutputEnd = this._outputEnd - 6;
/* 1460 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/*      */ 
/* 1463 */     while (inputPtr <= safeInputEnd) {
/* 1464 */       if (this._outputTail > safeOutputEnd) {
/* 1465 */         _flushBuffer();
/*      */       }
/*      */       
/* 1468 */       int b24 = input[(inputPtr++)] << 8;
/* 1469 */       b24 |= input[(inputPtr++)] & 0xFF;
/* 1470 */       b24 = b24 << 8 | input[(inputPtr++)] & 0xFF;
/* 1471 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1472 */       chunksBeforeLF--; if (chunksBeforeLF <= 0)
/*      */       {
/* 1474 */         this._outputBuffer[(this._outputTail++)] = '\\';
/* 1475 */         this._outputBuffer[(this._outputTail++)] = 'n';
/* 1476 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1481 */     int inputLeft = inputEnd - inputPtr;
/* 1482 */     if (inputLeft > 0) {
/* 1483 */       if (this._outputTail > safeOutputEnd) {
/* 1484 */         _flushBuffer();
/*      */       }
/* 1486 */       int b24 = input[(inputPtr++)] << 16;
/* 1487 */       if (inputLeft == 2) {
/* 1488 */         b24 |= (input[(inputPtr++)] & 0xFF) << 8;
/*      */       }
/* 1490 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1499 */     int inputPtr = 0;
/* 1500 */     int inputEnd = 0;
/* 1501 */     int lastFullOffset = -3;
/*      */     
/*      */ 
/* 1504 */     int safeOutputEnd = this._outputEnd - 6;
/* 1505 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/* 1507 */     while (bytesLeft > 2) {
/* 1508 */       if (inputPtr > lastFullOffset) {
/* 1509 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1510 */         inputPtr = 0;
/* 1511 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1514 */         lastFullOffset = inputEnd - 3;
/*      */       }
/* 1516 */       if (this._outputTail > safeOutputEnd) {
/* 1517 */         _flushBuffer();
/*      */       }
/* 1519 */       int b24 = readBuffer[(inputPtr++)] << 8;
/* 1520 */       b24 |= readBuffer[(inputPtr++)] & 0xFF;
/* 1521 */       b24 = b24 << 8 | readBuffer[(inputPtr++)] & 0xFF;
/* 1522 */       bytesLeft -= 3;
/* 1523 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1524 */       chunksBeforeLF--; if (chunksBeforeLF <= 0) {
/* 1525 */         this._outputBuffer[(this._outputTail++)] = '\\';
/* 1526 */         this._outputBuffer[(this._outputTail++)] = 'n';
/* 1527 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1532 */     if (bytesLeft > 0) {
/* 1533 */       inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1534 */       inputPtr = 0;
/* 1535 */       if (inputEnd > 0) {
/* 1536 */         if (this._outputTail > safeOutputEnd) {
/* 1537 */           _flushBuffer();
/*      */         }
/* 1539 */         int b24 = readBuffer[(inputPtr++)] << 16;
/*      */         int amount;
/* 1541 */         int amount; if (inputPtr < inputEnd) {
/* 1542 */           b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1543 */           amount = 2;
/*      */         } else {
/* 1545 */           amount = 1;
/*      */         }
/* 1547 */         this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/* 1548 */         bytesLeft -= amount;
/*      */       }
/*      */     }
/* 1551 */     return bytesLeft;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1559 */     int inputPtr = 0;
/* 1560 */     int inputEnd = 0;
/* 1561 */     int lastFullOffset = -3;
/* 1562 */     int bytesDone = 0;
/*      */     
/*      */ 
/* 1565 */     int safeOutputEnd = this._outputEnd - 6;
/* 1566 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/*      */     for (;;)
/*      */     {
/* 1570 */       if (inputPtr > lastFullOffset) {
/* 1571 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
/* 1572 */         inputPtr = 0;
/* 1573 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1576 */         lastFullOffset = inputEnd - 3;
/*      */       }
/* 1578 */       if (this._outputTail > safeOutputEnd) {
/* 1579 */         _flushBuffer();
/*      */       }
/*      */       
/* 1582 */       int b24 = readBuffer[(inputPtr++)] << 8;
/* 1583 */       b24 |= readBuffer[(inputPtr++)] & 0xFF;
/* 1584 */       b24 = b24 << 8 | readBuffer[(inputPtr++)] & 0xFF;
/* 1585 */       bytesDone += 3;
/* 1586 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1587 */       chunksBeforeLF--; if (chunksBeforeLF <= 0) {
/* 1588 */         this._outputBuffer[(this._outputTail++)] = '\\';
/* 1589 */         this._outputBuffer[(this._outputTail++)] = 'n';
/* 1590 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1595 */     if (inputPtr < inputEnd) {
/* 1596 */       if (this._outputTail > safeOutputEnd) {
/* 1597 */         _flushBuffer();
/*      */       }
/* 1599 */       int b24 = readBuffer[(inputPtr++)] << 16;
/* 1600 */       int amount = 1;
/* 1601 */       if (inputPtr < inputEnd) {
/* 1602 */         b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1603 */         amount = 2;
/*      */       }
/* 1605 */       bytesDone += amount;
/* 1606 */       this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*      */     }
/* 1608 */     return bytesDone;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead)
/*      */     throws IOException
/*      */   {
/* 1616 */     int i = 0;
/* 1617 */     while (inputPtr < inputEnd) {
/* 1618 */       readBuffer[(i++)] = readBuffer[(inputPtr++)];
/*      */     }
/* 1620 */     inputPtr = 0;
/* 1621 */     inputEnd = i;
/* 1622 */     maxRead = Math.min(maxRead, readBuffer.length);
/*      */     do
/*      */     {
/* 1625 */       int length = maxRead - inputEnd;
/* 1626 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 1629 */       int count = in.read(readBuffer, inputEnd, length);
/* 1630 */       if (count < 0) {
/* 1631 */         return inputEnd;
/*      */       }
/* 1633 */       inputEnd += count;
/* 1634 */     } while (inputEnd < 3);
/* 1635 */     return inputEnd;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeNull()
/*      */     throws IOException
/*      */   {
/* 1646 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 1647 */       _flushBuffer();
/*      */     }
/* 1649 */     int ptr = this._outputTail;
/* 1650 */     char[] buf = this._outputBuffer;
/* 1651 */     buf[ptr] = 'n';
/* 1652 */     buf[(++ptr)] = 'u';
/* 1653 */     buf[(++ptr)] = 'l';
/* 1654 */     buf[(++ptr)] = 'l';
/* 1655 */     this._outputTail = (ptr + 1);
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
/*      */   private void _prependOrWriteCharacterEscape(char ch, int escCode)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1672 */     if (escCode >= 0) {
/* 1673 */       if (this._outputTail >= 2) {
/* 1674 */         int ptr = this._outputTail - 2;
/* 1675 */         this._outputHead = ptr;
/* 1676 */         this._outputBuffer[(ptr++)] = '\\';
/* 1677 */         this._outputBuffer[ptr] = ((char)escCode);
/* 1678 */         return;
/*      */       }
/*      */       
/* 1681 */       char[] buf = this._entityBuffer;
/* 1682 */       if (buf == null) {
/* 1683 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1685 */       this._outputHead = this._outputTail;
/* 1686 */       buf[1] = ((char)escCode);
/* 1687 */       this._writer.write(buf, 0, 2);
/* 1688 */       return;
/*      */     }
/* 1690 */     if (escCode != -2) {
/* 1691 */       if (this._outputTail >= 6) {
/* 1692 */         char[] buf = this._outputBuffer;
/* 1693 */         int ptr = this._outputTail - 6;
/* 1694 */         this._outputHead = ptr;
/* 1695 */         buf[ptr] = '\\';
/* 1696 */         buf[(++ptr)] = 'u';
/*      */         
/* 1698 */         if (ch > 'ÿ') {
/* 1699 */           int hi = ch >> '\b' & 0xFF;
/* 1700 */           buf[(++ptr)] = HEX_CHARS[(hi >> 4)];
/* 1701 */           buf[(++ptr)] = HEX_CHARS[(hi & 0xF)];
/* 1702 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1704 */           buf[(++ptr)] = '0';
/* 1705 */           buf[(++ptr)] = '0';
/*      */         }
/* 1707 */         buf[(++ptr)] = HEX_CHARS[(ch >> '\004')];
/* 1708 */         buf[(++ptr)] = HEX_CHARS[(ch & 0xF)];
/* 1709 */         return;
/*      */       }
/*      */       
/* 1712 */       char[] buf = this._entityBuffer;
/* 1713 */       if (buf == null) {
/* 1714 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1716 */       this._outputHead = this._outputTail;
/* 1717 */       if (ch > 'ÿ') {
/* 1718 */         int hi = ch >> '\b' & 0xFF;
/* 1719 */         int lo = ch & 0xFF;
/* 1720 */         buf[10] = HEX_CHARS[(hi >> 4)];
/* 1721 */         buf[11] = HEX_CHARS[(hi & 0xF)];
/* 1722 */         buf[12] = HEX_CHARS[(lo >> 4)];
/* 1723 */         buf[13] = HEX_CHARS[(lo & 0xF)];
/* 1724 */         this._writer.write(buf, 8, 6);
/*      */       } else {
/* 1726 */         buf[6] = HEX_CHARS[(ch >> '\004')];
/* 1727 */         buf[7] = HEX_CHARS[(ch & 0xF)];
/* 1728 */         this._writer.write(buf, 2, 6);
/*      */       }
/*      */       return;
/*      */     }
/*      */     String escape;
/*      */     String escape;
/* 1734 */     if (this._currentEscape == null) {
/* 1735 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1737 */       escape = this._currentEscape.getValue();
/* 1738 */       this._currentEscape = null;
/*      */     }
/* 1740 */     int len = escape.length();
/* 1741 */     if (this._outputTail >= len) {
/* 1742 */       int ptr = this._outputTail - len;
/* 1743 */       this._outputHead = ptr;
/* 1744 */       escape.getChars(0, len, this._outputBuffer, ptr);
/* 1745 */       return;
/*      */     }
/*      */     
/* 1748 */     this._outputHead = this._outputTail;
/* 1749 */     this._writer.write(escape);
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
/*      */   private int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1763 */     if (escCode >= 0) {
/* 1764 */       if ((ptr > 1) && (ptr < end)) {
/* 1765 */         ptr -= 2;
/* 1766 */         buffer[ptr] = '\\';
/* 1767 */         buffer[(ptr + 1)] = ((char)escCode);
/*      */       } else {
/* 1769 */         char[] ent = this._entityBuffer;
/* 1770 */         if (ent == null) {
/* 1771 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1773 */         ent[1] = ((char)escCode);
/* 1774 */         this._writer.write(ent, 0, 2);
/*      */       }
/* 1776 */       return ptr;
/*      */     }
/* 1778 */     if (escCode != -2) {
/* 1779 */       if ((ptr > 5) && (ptr < end)) {
/* 1780 */         ptr -= 6;
/* 1781 */         buffer[(ptr++)] = '\\';
/* 1782 */         buffer[(ptr++)] = 'u';
/*      */         
/* 1784 */         if (ch > 'ÿ') {
/* 1785 */           int hi = ch >> '\b' & 0xFF;
/* 1786 */           buffer[(ptr++)] = HEX_CHARS[(hi >> 4)];
/* 1787 */           buffer[(ptr++)] = HEX_CHARS[(hi & 0xF)];
/* 1788 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1790 */           buffer[(ptr++)] = '0';
/* 1791 */           buffer[(ptr++)] = '0';
/*      */         }
/* 1793 */         buffer[(ptr++)] = HEX_CHARS[(ch >> '\004')];
/* 1794 */         buffer[ptr] = HEX_CHARS[(ch & 0xF)];
/* 1795 */         ptr -= 5;
/*      */       }
/*      */       else {
/* 1798 */         char[] ent = this._entityBuffer;
/* 1799 */         if (ent == null) {
/* 1800 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1802 */         this._outputHead = this._outputTail;
/* 1803 */         if (ch > 'ÿ') {
/* 1804 */           int hi = ch >> '\b' & 0xFF;
/* 1805 */           int lo = ch & 0xFF;
/* 1806 */           ent[10] = HEX_CHARS[(hi >> 4)];
/* 1807 */           ent[11] = HEX_CHARS[(hi & 0xF)];
/* 1808 */           ent[12] = HEX_CHARS[(lo >> 4)];
/* 1809 */           ent[13] = HEX_CHARS[(lo & 0xF)];
/* 1810 */           this._writer.write(ent, 8, 6);
/*      */         } else {
/* 1812 */           ent[6] = HEX_CHARS[(ch >> '\004')];
/* 1813 */           ent[7] = HEX_CHARS[(ch & 0xF)];
/* 1814 */           this._writer.write(ent, 2, 6);
/*      */         }
/*      */       }
/* 1817 */       return ptr; }
/*      */     String escape;
/*      */     String escape;
/* 1820 */     if (this._currentEscape == null) {
/* 1821 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1823 */       escape = this._currentEscape.getValue();
/* 1824 */       this._currentEscape = null;
/*      */     }
/* 1826 */     int len = escape.length();
/* 1827 */     if ((ptr >= len) && (ptr < end)) {
/* 1828 */       ptr -= len;
/* 1829 */       escape.getChars(0, len, buffer, ptr);
/*      */     } else {
/* 1831 */       this._writer.write(escape);
/*      */     }
/* 1833 */     return ptr;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _appendCharacterEscape(char ch, int escCode)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1843 */     if (escCode >= 0) {
/* 1844 */       if (this._outputTail + 2 > this._outputEnd) {
/* 1845 */         _flushBuffer();
/*      */       }
/* 1847 */       this._outputBuffer[(this._outputTail++)] = '\\';
/* 1848 */       this._outputBuffer[(this._outputTail++)] = ((char)escCode);
/* 1849 */       return;
/*      */     }
/* 1851 */     if (escCode != -2) {
/* 1852 */       if (this._outputTail + 5 >= this._outputEnd) {
/* 1853 */         _flushBuffer();
/*      */       }
/* 1855 */       int ptr = this._outputTail;
/* 1856 */       char[] buf = this._outputBuffer;
/* 1857 */       buf[(ptr++)] = '\\';
/* 1858 */       buf[(ptr++)] = 'u';
/*      */       
/* 1860 */       if (ch > 'ÿ') {
/* 1861 */         int hi = ch >> '\b' & 0xFF;
/* 1862 */         buf[(ptr++)] = HEX_CHARS[(hi >> 4)];
/* 1863 */         buf[(ptr++)] = HEX_CHARS[(hi & 0xF)];
/* 1864 */         ch = (char)(ch & 0xFF);
/*      */       } else {
/* 1866 */         buf[(ptr++)] = '0';
/* 1867 */         buf[(ptr++)] = '0';
/*      */       }
/* 1869 */       buf[(ptr++)] = HEX_CHARS[(ch >> '\004')];
/* 1870 */       buf[(ptr++)] = HEX_CHARS[(ch & 0xF)];
/* 1871 */       this._outputTail = ptr; return;
/*      */     }
/*      */     String escape;
/*      */     String escape;
/* 1875 */     if (this._currentEscape == null) {
/* 1876 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1878 */       escape = this._currentEscape.getValue();
/* 1879 */       this._currentEscape = null;
/*      */     }
/* 1881 */     int len = escape.length();
/* 1882 */     if (this._outputTail + len > this._outputEnd) {
/* 1883 */       _flushBuffer();
/* 1884 */       if (len > this._outputEnd) {
/* 1885 */         this._writer.write(escape);
/* 1886 */         return;
/*      */       }
/*      */     }
/* 1889 */     escape.getChars(0, len, this._outputBuffer, this._outputTail);
/* 1890 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */   private char[] _allocateEntityBuffer()
/*      */   {
/* 1895 */     char[] buf = new char[14];
/*      */     
/* 1897 */     buf[0] = '\\';
/*      */     
/* 1899 */     buf[2] = '\\';
/* 1900 */     buf[3] = 'u';
/* 1901 */     buf[4] = '0';
/* 1902 */     buf[5] = '0';
/*      */     
/* 1904 */     buf[8] = '\\';
/* 1905 */     buf[9] = 'u';
/* 1906 */     this._entityBuffer = buf;
/* 1907 */     return buf;
/*      */   }
/*      */   
/*      */   protected void _flushBuffer() throws IOException
/*      */   {
/* 1912 */     int len = this._outputTail - this._outputHead;
/* 1913 */     if (len > 0) {
/* 1914 */       int offset = this._outputHead;
/* 1915 */       this._outputTail = (this._outputHead = 0);
/* 1916 */       this._writer.write(this._outputBuffer, offset, len);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\WriterBasedJsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */