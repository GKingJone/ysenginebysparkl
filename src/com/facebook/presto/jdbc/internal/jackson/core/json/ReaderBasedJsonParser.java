/*      */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.base.ParserBase;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharTypes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.IOContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.sym.CharsToNameCanonicalizer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.ByteArrayBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.TextBuffer;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ 
/*      */ 
/*      */ public class ReaderBasedJsonParser
/*      */   extends ParserBase
/*      */ {
/*   24 */   protected static final int[] _icLatin1 = ;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Reader _reader;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected char[] _inputBuffer;
/*      */   
/*      */ 
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
/*      */ 
/*      */   protected ObjectCodec _objectCodec;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final CharsToNameCanonicalizer _symbols;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int _hashSeed;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _tokenIncomplete;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long _nameStartOffset;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _nameStartRow;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _nameStartCol;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st, char[] inputBuffer, int start, int end, boolean bufferRecyclable)
/*      */   {
/*  116 */     super(ctxt, features);
/*  117 */     this._reader = r;
/*  118 */     this._inputBuffer = inputBuffer;
/*  119 */     this._inputPtr = start;
/*  120 */     this._inputEnd = end;
/*  121 */     this._objectCodec = codec;
/*  122 */     this._symbols = st;
/*  123 */     this._hashSeed = st.hashSeed();
/*  124 */     this._bufferRecyclable = bufferRecyclable;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st)
/*      */   {
/*  134 */     super(ctxt, features);
/*  135 */     this._reader = r;
/*  136 */     this._inputBuffer = ctxt.allocTokenBuffer();
/*  137 */     this._inputPtr = 0;
/*  138 */     this._inputEnd = 0;
/*  139 */     this._objectCodec = codec;
/*  140 */     this._symbols = st;
/*  141 */     this._hashSeed = st.hashSeed();
/*  142 */     this._bufferRecyclable = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  151 */   public ObjectCodec getCodec() { return this._objectCodec; }
/*  152 */   public void setCodec(ObjectCodec c) { this._objectCodec = c; }
/*      */   
/*      */   public int releaseBuffered(Writer w) throws IOException
/*      */   {
/*  156 */     int count = this._inputEnd - this._inputPtr;
/*  157 */     if (count < 1) { return 0;
/*      */     }
/*  159 */     int origPtr = this._inputPtr;
/*  160 */     w.write(this._inputBuffer, origPtr, count);
/*  161 */     return count;
/*      */   }
/*      */   
/*  164 */   public Object getInputSource() { return this._reader; }
/*      */   
/*      */   @Deprecated
/*      */   protected char getNextChar(String eofMsg) throws IOException {
/*  168 */     return getNextChar(eofMsg, null);
/*      */   }
/*      */   
/*      */   protected char getNextChar(String eofMsg, JsonToken forToken) throws IOException {
/*  172 */     if ((this._inputPtr >= this._inputEnd) && 
/*  173 */       (!_loadMore())) {
/*  174 */       _reportInvalidEOF(eofMsg, forToken);
/*      */     }
/*      */     
/*  177 */     return this._inputBuffer[(this._inputPtr++)];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _closeInput()
/*      */     throws IOException
/*      */   {
/*  189 */     if (this._reader != null) {
/*  190 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE))) {
/*  191 */         this._reader.close();
/*      */       }
/*  193 */       this._reader = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */     throws IOException
/*      */   {
/*  205 */     super._releaseBuffers();
/*      */     
/*  207 */     this._symbols.release();
/*      */     
/*  209 */     if (this._bufferRecyclable) {
/*  210 */       char[] buf = this._inputBuffer;
/*  211 */       if (buf != null) {
/*  212 */         this._inputBuffer = null;
/*  213 */         this._ioContext.releaseTokenBuffer(buf);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _loadMoreGuaranteed()
/*      */     throws IOException
/*      */   {
/*  225 */     if (!_loadMore()) _reportInvalidEOF();
/*      */   }
/*      */   
/*      */   protected boolean _loadMore() throws IOException
/*      */   {
/*  230 */     int bufSize = this._inputEnd;
/*      */     
/*  232 */     this._currInputProcessed += bufSize;
/*  233 */     this._currInputRowStart -= bufSize;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  238 */     this._nameStartOffset -= bufSize;
/*      */     
/*  240 */     if (this._reader != null) {
/*  241 */       int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
/*  242 */       if (count > 0) {
/*  243 */         this._inputPtr = 0;
/*  244 */         this._inputEnd = count;
/*  245 */         return true;
/*      */       }
/*      */       
/*  248 */       _closeInput();
/*      */       
/*  250 */       if (count == 0) {
/*  251 */         throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
/*      */       }
/*      */     }
/*  254 */     return false;
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
/*      */   public final String getText()
/*      */     throws IOException
/*      */   {
/*  272 */     JsonToken t = this._currToken;
/*  273 */     if (t == JsonToken.VALUE_STRING) {
/*  274 */       if (this._tokenIncomplete) {
/*  275 */         this._tokenIncomplete = false;
/*  276 */         _finishString();
/*      */       }
/*  278 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  280 */     return _getText2(t);
/*      */   }
/*      */   
/*      */   public int getText(Writer writer)
/*      */     throws IOException
/*      */   {
/*  286 */     JsonToken t = this._currToken;
/*  287 */     if (t == JsonToken.VALUE_STRING) {
/*  288 */       if (this._tokenIncomplete) {
/*  289 */         this._tokenIncomplete = false;
/*  290 */         _finishString();
/*      */       }
/*  292 */       return this._textBuffer.contentsToWriter(writer);
/*      */     }
/*  294 */     if (t == JsonToken.FIELD_NAME) {
/*  295 */       String n = this._parsingContext.getCurrentName();
/*  296 */       writer.write(n);
/*  297 */       return n.length();
/*      */     }
/*  299 */     if (t != null) {
/*  300 */       if (t.isNumeric()) {
/*  301 */         return this._textBuffer.contentsToWriter(writer);
/*      */       }
/*  303 */       char[] ch = t.asCharArray();
/*  304 */       writer.write(ch);
/*  305 */       return ch.length;
/*      */     }
/*  307 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public final String getValueAsString()
/*      */     throws IOException
/*      */   {
/*  316 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  317 */       if (this._tokenIncomplete) {
/*  318 */         this._tokenIncomplete = false;
/*  319 */         _finishString();
/*      */       }
/*  321 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  323 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  324 */       return getCurrentName();
/*      */     }
/*  326 */     return super.getValueAsString(null);
/*      */   }
/*      */   
/*      */   public final String getValueAsString(String defValue)
/*      */     throws IOException
/*      */   {
/*  332 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  333 */       if (this._tokenIncomplete) {
/*  334 */         this._tokenIncomplete = false;
/*  335 */         _finishString();
/*      */       }
/*  337 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  339 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  340 */       return getCurrentName();
/*      */     }
/*  342 */     return super.getValueAsString(defValue);
/*      */   }
/*      */   
/*      */   protected final String _getText2(JsonToken t) {
/*  346 */     if (t == null) {
/*  347 */       return null;
/*      */     }
/*  349 */     switch (t.id()) {
/*      */     case 5: 
/*  351 */       return this._parsingContext.getCurrentName();
/*      */     
/*      */ 
/*      */     case 6: 
/*      */     case 7: 
/*      */     case 8: 
/*  357 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  359 */     return t.asString();
/*      */   }
/*      */   
/*      */ 
/*      */   public final char[] getTextCharacters()
/*      */     throws IOException
/*      */   {
/*  366 */     if (this._currToken != null) {
/*  367 */       switch (this._currToken.id()) {
/*      */       case 5: 
/*  369 */         if (!this._nameCopied) {
/*  370 */           String name = this._parsingContext.getCurrentName();
/*  371 */           int nameLen = name.length();
/*  372 */           if (this._nameCopyBuffer == null) {
/*  373 */             this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  374 */           } else if (this._nameCopyBuffer.length < nameLen) {
/*  375 */             this._nameCopyBuffer = new char[nameLen];
/*      */           }
/*  377 */           name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  378 */           this._nameCopied = true;
/*      */         }
/*  380 */         return this._nameCopyBuffer;
/*      */       case 6: 
/*  382 */         if (this._tokenIncomplete) {
/*  383 */           this._tokenIncomplete = false;
/*  384 */           _finishString();
/*      */         }
/*      */       
/*      */       case 7: 
/*      */       case 8: 
/*  389 */         return this._textBuffer.getTextBuffer();
/*      */       }
/*  391 */       return this._currToken.asCharArray();
/*      */     }
/*      */     
/*  394 */     return null;
/*      */   }
/*      */   
/*      */   public final int getTextLength()
/*      */     throws IOException
/*      */   {
/*  400 */     if (this._currToken != null) {
/*  401 */       switch (this._currToken.id()) {
/*      */       case 5: 
/*  403 */         return this._parsingContext.getCurrentName().length();
/*      */       case 6: 
/*  405 */         if (this._tokenIncomplete) {
/*  406 */           this._tokenIncomplete = false;
/*  407 */           _finishString();
/*      */         }
/*      */       
/*      */       case 7: 
/*      */       case 8: 
/*  412 */         return this._textBuffer.size();
/*      */       }
/*  414 */       return this._currToken.asCharArray().length;
/*      */     }
/*      */     
/*  417 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public final int getTextOffset()
/*      */     throws IOException
/*      */   {
/*  424 */     if (this._currToken != null) {
/*  425 */       switch (this._currToken.id()) {
/*      */       case 5: 
/*  427 */         return 0;
/*      */       case 6: 
/*  429 */         if (this._tokenIncomplete) {
/*  430 */           this._tokenIncomplete = false;
/*  431 */           _finishString();
/*      */         }
/*      */       
/*      */       case 7: 
/*      */       case 8: 
/*  436 */         return this._textBuffer.getTextOffset();
/*      */       }
/*      */       
/*      */     }
/*  440 */     return 0;
/*      */   }
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant)
/*      */     throws IOException
/*      */   {
/*  446 */     if ((this._currToken != JsonToken.VALUE_STRING) && ((this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT) || (this._binaryValue == null)))
/*      */     {
/*  448 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  453 */     if (this._tokenIncomplete) {
/*      */       try {
/*  455 */         this._binaryValue = _decodeBase64(b64variant);
/*      */       } catch (IllegalArgumentException iae) {
/*  457 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  462 */       this._tokenIncomplete = false;
/*      */     }
/*  464 */     else if (this._binaryValue == null)
/*      */     {
/*  466 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  467 */       _decodeBase64(getText(), builder, b64variant);
/*  468 */       this._binaryValue = builder.toByteArray();
/*      */     }
/*      */     
/*  471 */     return this._binaryValue;
/*      */   }
/*      */   
/*      */ 
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out)
/*      */     throws IOException
/*      */   {
/*  478 */     if ((!this._tokenIncomplete) || (this._currToken != JsonToken.VALUE_STRING)) {
/*  479 */       byte[] b = getBinaryValue(b64variant);
/*  480 */       out.write(b);
/*  481 */       return b.length;
/*      */     }
/*      */     
/*  484 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  486 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  488 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     }
/*      */   }
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException
/*      */   {
/*  494 */     int outputPtr = 0;
/*  495 */     int outputEnd = buffer.length - 3;
/*  496 */     int outputCount = 0;
/*      */     
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/*  502 */       if (this._inputPtr >= this._inputEnd) {
/*  503 */         _loadMoreGuaranteed();
/*      */       }
/*  505 */       char ch = this._inputBuffer[(this._inputPtr++)];
/*  506 */       if (ch > ' ') {
/*  507 */         int bits = b64variant.decodeBase64Char(ch);
/*  508 */         if (bits < 0) {
/*  509 */           if (ch == '"') {
/*      */             break;
/*      */           }
/*  512 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  513 */           if (bits < 0) {}
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  519 */           if (outputPtr > outputEnd) {
/*  520 */             outputCount += outputPtr;
/*  521 */             out.write(buffer, 0, outputPtr);
/*  522 */             outputPtr = 0;
/*      */           }
/*      */           
/*  525 */           int decodedData = bits;
/*      */           
/*      */ 
/*      */ 
/*  529 */           if (this._inputPtr >= this._inputEnd) {
/*  530 */             _loadMoreGuaranteed();
/*      */           }
/*  532 */           ch = this._inputBuffer[(this._inputPtr++)];
/*  533 */           bits = b64variant.decodeBase64Char(ch);
/*  534 */           if (bits < 0) {
/*  535 */             bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */           }
/*  537 */           decodedData = decodedData << 6 | bits;
/*      */           
/*      */ 
/*  540 */           if (this._inputPtr >= this._inputEnd) {
/*  541 */             _loadMoreGuaranteed();
/*      */           }
/*  543 */           ch = this._inputBuffer[(this._inputPtr++)];
/*  544 */           bits = b64variant.decodeBase64Char(ch);
/*      */           
/*      */ 
/*  547 */           if (bits < 0) {
/*  548 */             if (bits != -2)
/*      */             {
/*  550 */               if ((ch == '"') && (!b64variant.usesPadding())) {
/*  551 */                 decodedData >>= 4;
/*  552 */                 buffer[(outputPtr++)] = ((byte)decodedData);
/*  553 */                 break;
/*      */               }
/*  555 */               bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */             }
/*  557 */             if (bits == -2)
/*      */             {
/*  559 */               if (this._inputPtr >= this._inputEnd) {
/*  560 */                 _loadMoreGuaranteed();
/*      */               }
/*  562 */               ch = this._inputBuffer[(this._inputPtr++)];
/*  563 */               if (!b64variant.usesPaddingChar(ch)) {
/*  564 */                 throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */               }
/*      */               
/*  567 */               decodedData >>= 4;
/*  568 */               buffer[(outputPtr++)] = ((byte)decodedData);
/*  569 */               continue;
/*      */             }
/*      */           }
/*      */           
/*  573 */           decodedData = decodedData << 6 | bits;
/*      */           
/*  575 */           if (this._inputPtr >= this._inputEnd) {
/*  576 */             _loadMoreGuaranteed();
/*      */           }
/*  578 */           ch = this._inputBuffer[(this._inputPtr++)];
/*  579 */           bits = b64variant.decodeBase64Char(ch);
/*  580 */           if (bits < 0) {
/*  581 */             if (bits != -2)
/*      */             {
/*  583 */               if ((ch == '"') && (!b64variant.usesPadding())) {
/*  584 */                 decodedData >>= 2;
/*  585 */                 buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
/*  586 */                 buffer[(outputPtr++)] = ((byte)decodedData);
/*  587 */                 break;
/*      */               }
/*  589 */               bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */             }
/*  591 */             if (bits == -2)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  598 */               decodedData >>= 2;
/*  599 */               buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
/*  600 */               buffer[(outputPtr++)] = ((byte)decodedData);
/*  601 */               continue;
/*      */             }
/*      */           }
/*      */           
/*  605 */           decodedData = decodedData << 6 | bits;
/*  606 */           buffer[(outputPtr++)] = ((byte)(decodedData >> 16));
/*  607 */           buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
/*  608 */           buffer[(outputPtr++)] = ((byte)decodedData);
/*      */         } } }
/*  610 */     this._tokenIncomplete = false;
/*  611 */     if (outputPtr > 0) {
/*  612 */       outputCount += outputPtr;
/*  613 */       out.write(buffer, 0, outputPtr);
/*      */     }
/*  615 */     return outputCount;
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
/*      */   public final JsonToken nextToken()
/*      */     throws IOException
/*      */   {
/*  635 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  636 */       return _nextAfterName();
/*      */     }
/*      */     
/*      */ 
/*  640 */     this._numTypesValid = 0;
/*  641 */     if (this._tokenIncomplete) {
/*  642 */       _skipString();
/*      */     }
/*  644 */     int i = _skipWSOrEnd();
/*  645 */     if (i < 0)
/*      */     {
/*      */ 
/*  648 */       close();
/*  649 */       return this._currToken = null;
/*      */     }
/*      */     
/*  652 */     this._binaryValue = null;
/*      */     
/*      */ 
/*  655 */     if (i == 93) {
/*  656 */       _updateLocation();
/*  657 */       if (!this._parsingContext.inArray()) {
/*  658 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  660 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  661 */       return this._currToken = JsonToken.END_ARRAY;
/*      */     }
/*  663 */     if (i == 125) {
/*  664 */       _updateLocation();
/*  665 */       if (!this._parsingContext.inObject()) {
/*  666 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  668 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  669 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     }
/*      */     
/*      */ 
/*  673 */     if (this._parsingContext.expectComma()) {
/*  674 */       i = _skipComma(i);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  680 */     boolean inObject = this._parsingContext.inObject();
/*  681 */     if (inObject)
/*      */     {
/*  683 */       _updateNameLocation();
/*  684 */       String name = i == 34 ? _parseName() : _handleOddName(i);
/*  685 */       this._parsingContext.setCurrentName(name);
/*  686 */       this._currToken = JsonToken.FIELD_NAME;
/*  687 */       i = _skipColon();
/*      */     }
/*  689 */     _updateLocation();
/*      */     
/*      */ 
/*      */     JsonToken t;
/*      */     
/*      */ 
/*  695 */     switch (i) {
/*      */     case 34: 
/*  697 */       this._tokenIncomplete = true;
/*  698 */       t = JsonToken.VALUE_STRING;
/*  699 */       break;
/*      */     case 91: 
/*  701 */       if (!inObject) {
/*  702 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  704 */       t = JsonToken.START_ARRAY;
/*  705 */       break;
/*      */     case 123: 
/*  707 */       if (!inObject) {
/*  708 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  710 */       t = JsonToken.START_OBJECT;
/*  711 */       break;
/*      */     
/*      */ 
/*      */     case 125: 
/*  715 */       _reportUnexpectedChar(i, "expected a value");
/*      */     case 116: 
/*  717 */       _matchTrue();
/*  718 */       t = JsonToken.VALUE_TRUE;
/*  719 */       break;
/*      */     case 102: 
/*  721 */       _matchFalse();
/*  722 */       t = JsonToken.VALUE_FALSE;
/*  723 */       break;
/*      */     case 110: 
/*  725 */       _matchNull();
/*  726 */       t = JsonToken.VALUE_NULL;
/*  727 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 45: 
/*  734 */       t = _parseNegNumber();
/*  735 */       break;
/*      */     case 48: 
/*      */     case 49: 
/*      */     case 50: 
/*      */     case 51: 
/*      */     case 52: 
/*      */     case 53: 
/*      */     case 54: 
/*      */     case 55: 
/*      */     case 56: 
/*      */     case 57: 
/*  746 */       t = _parsePosNumber(i);
/*  747 */       break;
/*      */     default: 
/*  749 */       t = _handleOddValue(i);
/*      */     }
/*      */     
/*      */     
/*  753 */     if (inObject) {
/*  754 */       this._nextToken = t;
/*  755 */       return this._currToken;
/*      */     }
/*  757 */     this._currToken = t;
/*  758 */     return t;
/*      */   }
/*      */   
/*      */   private final JsonToken _nextAfterName()
/*      */   {
/*  763 */     this._nameCopied = false;
/*  764 */     JsonToken t = this._nextToken;
/*  765 */     this._nextToken = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  770 */     if (t == JsonToken.START_ARRAY) {
/*  771 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  772 */     } else if (t == JsonToken.START_OBJECT) {
/*  773 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     }
/*  775 */     return this._currToken = t;
/*      */   }
/*      */   
/*      */   public void finishToken() throws IOException
/*      */   {
/*  780 */     if (this._tokenIncomplete) {
/*  781 */       this._tokenIncomplete = false;
/*  782 */       _finishString();
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
/*      */   public boolean nextFieldName(SerializableString sstr)
/*      */     throws IOException
/*      */   {
/*  798 */     this._numTypesValid = 0;
/*  799 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  800 */       _nextAfterName();
/*  801 */       return false;
/*      */     }
/*  803 */     if (this._tokenIncomplete) {
/*  804 */       _skipString();
/*      */     }
/*  806 */     int i = _skipWSOrEnd();
/*  807 */     if (i < 0) {
/*  808 */       close();
/*  809 */       this._currToken = null;
/*  810 */       return false;
/*      */     }
/*  812 */     this._binaryValue = null;
/*      */     
/*  814 */     if (i == 93) {
/*  815 */       _updateLocation();
/*  816 */       if (!this._parsingContext.inArray()) {
/*  817 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  819 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  820 */       this._currToken = JsonToken.END_ARRAY;
/*  821 */       return false;
/*      */     }
/*  823 */     if (i == 125) {
/*  824 */       _updateLocation();
/*  825 */       if (!this._parsingContext.inObject()) {
/*  826 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  828 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  829 */       this._currToken = JsonToken.END_OBJECT;
/*  830 */       return false;
/*      */     }
/*  832 */     if (this._parsingContext.expectComma()) {
/*  833 */       i = _skipComma(i);
/*      */     }
/*      */     
/*  836 */     if (!this._parsingContext.inObject()) {
/*  837 */       _updateLocation();
/*  838 */       _nextTokenNotInObject(i);
/*  839 */       return false;
/*      */     }
/*      */     
/*  842 */     _updateNameLocation();
/*  843 */     if (i == 34)
/*      */     {
/*  845 */       char[] nameChars = sstr.asQuotedChars();
/*  846 */       int len = nameChars.length;
/*      */       
/*      */ 
/*  849 */       if (this._inputPtr + len + 4 < this._inputEnd)
/*      */       {
/*  851 */         int end = this._inputPtr + len;
/*  852 */         if (this._inputBuffer[end] == '"') {
/*  853 */           int offset = 0;
/*  854 */           int ptr = this._inputPtr;
/*      */           for (;;) {
/*  856 */             if (ptr == end) {
/*  857 */               this._parsingContext.setCurrentName(sstr.getValue());
/*  858 */               _isNextTokenNameYes(_skipColonFast(ptr + 1));
/*  859 */               return true;
/*      */             }
/*  861 */             if (nameChars[offset] != this._inputBuffer[ptr]) {
/*      */               break;
/*      */             }
/*  864 */             offset++;
/*  865 */             ptr++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  870 */     return _isNextTokenNameMaybe(i, sstr.getValue());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String nextFieldName()
/*      */     throws IOException
/*      */   {
/*  878 */     this._numTypesValid = 0;
/*  879 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  880 */       _nextAfterName();
/*  881 */       return null;
/*      */     }
/*  883 */     if (this._tokenIncomplete) {
/*  884 */       _skipString();
/*      */     }
/*  886 */     int i = _skipWSOrEnd();
/*  887 */     if (i < 0) {
/*  888 */       close();
/*  889 */       this._currToken = null;
/*  890 */       return null;
/*      */     }
/*  892 */     this._binaryValue = null;
/*  893 */     if (i == 93) {
/*  894 */       _updateLocation();
/*  895 */       if (!this._parsingContext.inArray()) {
/*  896 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  898 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  899 */       this._currToken = JsonToken.END_ARRAY;
/*  900 */       return null;
/*      */     }
/*  902 */     if (i == 125) {
/*  903 */       _updateLocation();
/*  904 */       if (!this._parsingContext.inObject()) {
/*  905 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  907 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  908 */       this._currToken = JsonToken.END_OBJECT;
/*  909 */       return null;
/*      */     }
/*  911 */     if (this._parsingContext.expectComma()) {
/*  912 */       i = _skipComma(i);
/*      */     }
/*  914 */     if (!this._parsingContext.inObject()) {
/*  915 */       _updateLocation();
/*  916 */       _nextTokenNotInObject(i);
/*  917 */       return null;
/*      */     }
/*      */     
/*  920 */     _updateNameLocation();
/*  921 */     String name = i == 34 ? _parseName() : _handleOddName(i);
/*  922 */     this._parsingContext.setCurrentName(name);
/*  923 */     this._currToken = JsonToken.FIELD_NAME;
/*  924 */     i = _skipColon();
/*      */     
/*  926 */     _updateLocation();
/*  927 */     if (i == 34) {
/*  928 */       this._tokenIncomplete = true;
/*  929 */       this._nextToken = JsonToken.VALUE_STRING;
/*  930 */       return name;
/*      */     }
/*      */     
/*      */ 
/*      */     JsonToken t;
/*      */     
/*      */ 
/*  937 */     switch (i) {
/*      */     case 45: 
/*  939 */       t = _parseNegNumber();
/*  940 */       break;
/*      */     case 48: 
/*      */     case 49: 
/*      */     case 50: 
/*      */     case 51: 
/*      */     case 52: 
/*      */     case 53: 
/*      */     case 54: 
/*      */     case 55: 
/*      */     case 56: 
/*      */     case 57: 
/*  951 */       t = _parsePosNumber(i);
/*  952 */       break;
/*      */     case 102: 
/*  954 */       _matchFalse();
/*  955 */       t = JsonToken.VALUE_FALSE;
/*  956 */       break;
/*      */     case 110: 
/*  958 */       _matchNull();
/*  959 */       t = JsonToken.VALUE_NULL;
/*  960 */       break;
/*      */     case 116: 
/*  962 */       _matchTrue();
/*  963 */       t = JsonToken.VALUE_TRUE;
/*  964 */       break;
/*      */     case 91: 
/*  966 */       t = JsonToken.START_ARRAY;
/*  967 */       break;
/*      */     case 123: 
/*  969 */       t = JsonToken.START_OBJECT;
/*  970 */       break;
/*      */     default: 
/*  972 */       t = _handleOddValue(i);
/*      */     }
/*      */     
/*  975 */     this._nextToken = t;
/*  976 */     return name;
/*      */   }
/*      */   
/*      */   private final void _isNextTokenNameYes(int i) throws IOException
/*      */   {
/*  981 */     this._currToken = JsonToken.FIELD_NAME;
/*  982 */     _updateLocation();
/*      */     
/*  984 */     switch (i) {
/*      */     case 34: 
/*  986 */       this._tokenIncomplete = true;
/*  987 */       this._nextToken = JsonToken.VALUE_STRING;
/*  988 */       return;
/*      */     case 91: 
/*  990 */       this._nextToken = JsonToken.START_ARRAY;
/*  991 */       return;
/*      */     case 123: 
/*  993 */       this._nextToken = JsonToken.START_OBJECT;
/*  994 */       return;
/*      */     case 116: 
/*  996 */       _matchToken("true", 1);
/*  997 */       this._nextToken = JsonToken.VALUE_TRUE;
/*  998 */       return;
/*      */     case 102: 
/* 1000 */       _matchToken("false", 1);
/* 1001 */       this._nextToken = JsonToken.VALUE_FALSE;
/* 1002 */       return;
/*      */     case 110: 
/* 1004 */       _matchToken("null", 1);
/* 1005 */       this._nextToken = JsonToken.VALUE_NULL;
/* 1006 */       return;
/*      */     case 45: 
/* 1008 */       this._nextToken = _parseNegNumber();
/* 1009 */       return;
/*      */     case 48: 
/*      */     case 49: 
/*      */     case 50: 
/*      */     case 51: 
/*      */     case 52: 
/*      */     case 53: 
/*      */     case 54: 
/*      */     case 55: 
/*      */     case 56: 
/*      */     case 57: 
/* 1020 */       this._nextToken = _parsePosNumber(i);
/* 1021 */       return;
/*      */     }
/* 1023 */     this._nextToken = _handleOddValue(i);
/*      */   }
/*      */   
/*      */   protected boolean _isNextTokenNameMaybe(int i, String nameToMatch)
/*      */     throws IOException
/*      */   {
/* 1029 */     String name = i == 34 ? _parseName() : _handleOddName(i);
/* 1030 */     this._parsingContext.setCurrentName(name);
/* 1031 */     this._currToken = JsonToken.FIELD_NAME;
/* 1032 */     i = _skipColon();
/* 1033 */     _updateLocation();
/* 1034 */     if (i == 34) {
/* 1035 */       this._tokenIncomplete = true;
/* 1036 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1037 */       return nameToMatch.equals(name);
/*      */     }
/*      */     
/*      */     JsonToken t;
/* 1041 */     switch (i) {
/*      */     case 45: 
/* 1043 */       t = _parseNegNumber();
/* 1044 */       break;
/*      */     case 48: 
/*      */     case 49: 
/*      */     case 50: 
/*      */     case 51: 
/*      */     case 52: 
/*      */     case 53: 
/*      */     case 54: 
/*      */     case 55: 
/*      */     case 56: 
/*      */     case 57: 
/* 1055 */       t = _parsePosNumber(i);
/* 1056 */       break;
/*      */     case 102: 
/* 1058 */       _matchFalse();
/* 1059 */       t = JsonToken.VALUE_FALSE;
/* 1060 */       break;
/*      */     case 110: 
/* 1062 */       _matchNull();
/* 1063 */       t = JsonToken.VALUE_NULL;
/* 1064 */       break;
/*      */     case 116: 
/* 1066 */       _matchTrue();
/* 1067 */       t = JsonToken.VALUE_TRUE;
/* 1068 */       break;
/*      */     case 91: 
/* 1070 */       t = JsonToken.START_ARRAY;
/* 1071 */       break;
/*      */     case 123: 
/* 1073 */       t = JsonToken.START_OBJECT;
/* 1074 */       break;
/*      */     default: 
/* 1076 */       t = _handleOddValue(i);
/*      */     }
/*      */     
/* 1079 */     this._nextToken = t;
/* 1080 */     return nameToMatch.equals(name);
/*      */   }
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException
/*      */   {
/* 1085 */     if (i == 34) {
/* 1086 */       this._tokenIncomplete = true;
/* 1087 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     }
/* 1089 */     switch (i) {
/*      */     case 91: 
/* 1091 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1092 */       return this._currToken = JsonToken.START_ARRAY;
/*      */     case 123: 
/* 1094 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/* 1095 */       return this._currToken = JsonToken.START_OBJECT;
/*      */     case 116: 
/* 1097 */       _matchToken("true", 1);
/* 1098 */       return this._currToken = JsonToken.VALUE_TRUE;
/*      */     case 102: 
/* 1100 */       _matchToken("false", 1);
/* 1101 */       return this._currToken = JsonToken.VALUE_FALSE;
/*      */     case 110: 
/* 1103 */       _matchToken("null", 1);
/* 1104 */       return this._currToken = JsonToken.VALUE_NULL;
/*      */     case 45: 
/* 1106 */       return this._currToken = _parseNegNumber();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 48: 
/*      */     case 49: 
/*      */     case 50: 
/*      */     case 51: 
/*      */     case 52: 
/*      */     case 53: 
/*      */     case 54: 
/*      */     case 55: 
/*      */     case 56: 
/*      */     case 57: 
/* 1121 */       return this._currToken = _parsePosNumber(i);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 44: 
/*      */     case 93: 
/* 1132 */       if (isEnabled(JsonParser.Feature.ALLOW_MISSING_VALUES)) {
/* 1133 */         this._inputPtr -= 1;
/* 1134 */         return this._currToken = JsonToken.VALUE_NULL;
/*      */       }
/*      */       break; }
/* 1137 */     return this._currToken = _handleOddValue(i);
/*      */   }
/*      */   
/*      */ 
/*      */   public final String nextTextValue()
/*      */     throws IOException
/*      */   {
/* 1144 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1145 */       this._nameCopied = false;
/* 1146 */       JsonToken t = this._nextToken;
/* 1147 */       this._nextToken = null;
/* 1148 */       this._currToken = t;
/* 1149 */       if (t == JsonToken.VALUE_STRING) {
/* 1150 */         if (this._tokenIncomplete) {
/* 1151 */           this._tokenIncomplete = false;
/* 1152 */           _finishString();
/*      */         }
/* 1154 */         return this._textBuffer.contentsAsString();
/*      */       }
/* 1156 */       if (t == JsonToken.START_ARRAY) {
/* 1157 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1158 */       } else if (t == JsonToken.START_OBJECT) {
/* 1159 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1161 */       return null;
/*      */     }
/*      */     
/* 1164 */     return nextToken() == JsonToken.VALUE_STRING ? getText() : null;
/*      */   }
/*      */   
/*      */ 
/*      */   public final int nextIntValue(int defaultValue)
/*      */     throws IOException
/*      */   {
/* 1171 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1172 */       this._nameCopied = false;
/* 1173 */       JsonToken t = this._nextToken;
/* 1174 */       this._nextToken = null;
/* 1175 */       this._currToken = t;
/* 1176 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1177 */         return getIntValue();
/*      */       }
/* 1179 */       if (t == JsonToken.START_ARRAY) {
/* 1180 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1181 */       } else if (t == JsonToken.START_OBJECT) {
/* 1182 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1184 */       return defaultValue;
/*      */     }
/*      */     
/* 1187 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */   public final long nextLongValue(long defaultValue)
/*      */     throws IOException
/*      */   {
/* 1194 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1195 */       this._nameCopied = false;
/* 1196 */       JsonToken t = this._nextToken;
/* 1197 */       this._nextToken = null;
/* 1198 */       this._currToken = t;
/* 1199 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1200 */         return getLongValue();
/*      */       }
/* 1202 */       if (t == JsonToken.START_ARRAY) {
/* 1203 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1204 */       } else if (t == JsonToken.START_OBJECT) {
/* 1205 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1207 */       return defaultValue;
/*      */     }
/*      */     
/* 1210 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */   public final Boolean nextBooleanValue()
/*      */     throws IOException
/*      */   {
/* 1217 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1218 */       this._nameCopied = false;
/* 1219 */       JsonToken t = this._nextToken;
/* 1220 */       this._nextToken = null;
/* 1221 */       this._currToken = t;
/* 1222 */       if (t == JsonToken.VALUE_TRUE) {
/* 1223 */         return Boolean.TRUE;
/*      */       }
/* 1225 */       if (t == JsonToken.VALUE_FALSE) {
/* 1226 */         return Boolean.FALSE;
/*      */       }
/* 1228 */       if (t == JsonToken.START_ARRAY) {
/* 1229 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1230 */       } else if (t == JsonToken.START_OBJECT) {
/* 1231 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1233 */       return null;
/*      */     }
/* 1235 */     JsonToken t = nextToken();
/* 1236 */     if (t != null) {
/* 1237 */       int id = t.id();
/* 1238 */       if (id == 9) return Boolean.TRUE;
/* 1239 */       if (id == 10) return Boolean.FALSE;
/*      */     }
/* 1241 */     return null;
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
/*      */   protected final JsonToken _parsePosNumber(int ch)
/*      */     throws IOException
/*      */   {
/* 1272 */     int ptr = this._inputPtr;
/* 1273 */     int startPtr = ptr - 1;
/* 1274 */     int inputLen = this._inputEnd;
/*      */     
/*      */ 
/* 1277 */     if (ch == 48) {
/* 1278 */       return _parseNumber2(false, startPtr);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1287 */     int intLen = 1;
/*      */     
/*      */ 
/*      */     for (;;)
/*      */     {
/* 1292 */       if (ptr >= inputLen) {
/* 1293 */         this._inputPtr = startPtr;
/* 1294 */         return _parseNumber2(false, startPtr);
/*      */       }
/* 1296 */       ch = this._inputBuffer[(ptr++)];
/* 1297 */       if ((ch < 48) || (ch > 57)) {
/*      */         break;
/*      */       }
/* 1300 */       intLen++;
/*      */     }
/* 1302 */     if ((ch == 46) || (ch == 101) || (ch == 69)) {
/* 1303 */       this._inputPtr = ptr;
/* 1304 */       return _parseFloat(ch, startPtr, ptr, false, intLen);
/*      */     }
/*      */     
/* 1307 */     ptr--;
/* 1308 */     this._inputPtr = ptr;
/*      */     
/* 1310 */     if (this._parsingContext.inRoot()) {
/* 1311 */       _verifyRootSpace(ch);
/*      */     }
/* 1313 */     int len = ptr - startPtr;
/* 1314 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/* 1315 */     return resetInt(false, intLen);
/*      */   }
/*      */   
/*      */   private final JsonToken _parseFloat(int ch, int startPtr, int ptr, boolean neg, int intLen)
/*      */     throws IOException
/*      */   {
/* 1321 */     int inputLen = this._inputEnd;
/* 1322 */     int fractLen = 0;
/*      */     
/*      */ 
/* 1325 */     if (ch == 46)
/*      */     {
/*      */       for (;;) {
/* 1328 */         if (ptr >= inputLen) {
/* 1329 */           return _parseNumber2(neg, startPtr);
/*      */         }
/* 1331 */         ch = this._inputBuffer[(ptr++)];
/* 1332 */         if ((ch < 48) || (ch > 57)) {
/*      */           break;
/*      */         }
/* 1335 */         fractLen++;
/*      */       }
/*      */       
/* 1338 */       if (fractLen == 0) {
/* 1339 */         reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */       }
/*      */     }
/* 1342 */     int expLen = 0;
/* 1343 */     if ((ch == 101) || (ch == 69)) {
/* 1344 */       if (ptr >= inputLen) {
/* 1345 */         this._inputPtr = startPtr;
/* 1346 */         return _parseNumber2(neg, startPtr);
/*      */       }
/*      */       
/* 1349 */       ch = this._inputBuffer[(ptr++)];
/* 1350 */       if ((ch == 45) || (ch == 43)) {
/* 1351 */         if (ptr >= inputLen) {
/* 1352 */           this._inputPtr = startPtr;
/* 1353 */           return _parseNumber2(neg, startPtr);
/*      */         }
/* 1355 */         ch = this._inputBuffer[(ptr++)];
/*      */       }
/* 1357 */       while ((ch <= 57) && (ch >= 48)) {
/* 1358 */         expLen++;
/* 1359 */         if (ptr >= inputLen) {
/* 1360 */           this._inputPtr = startPtr;
/* 1361 */           return _parseNumber2(neg, startPtr);
/*      */         }
/* 1363 */         ch = this._inputBuffer[(ptr++)];
/*      */       }
/*      */       
/* 1366 */       if (expLen == 0) {
/* 1367 */         reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     }
/* 1370 */     ptr--;
/* 1371 */     this._inputPtr = ptr;
/*      */     
/* 1373 */     if (this._parsingContext.inRoot()) {
/* 1374 */       _verifyRootSpace(ch);
/*      */     }
/* 1376 */     int len = ptr - startPtr;
/* 1377 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/*      */     
/* 1379 */     return resetFloat(neg, intLen, fractLen, expLen);
/*      */   }
/*      */   
/*      */   protected final JsonToken _parseNegNumber() throws IOException
/*      */   {
/* 1384 */     int ptr = this._inputPtr;
/* 1385 */     int startPtr = ptr - 1;
/* 1386 */     int inputLen = this._inputEnd;
/*      */     
/* 1388 */     if (ptr >= inputLen) {
/* 1389 */       return _parseNumber2(true, startPtr);
/*      */     }
/* 1391 */     int ch = this._inputBuffer[(ptr++)];
/*      */     
/* 1393 */     if ((ch > 57) || (ch < 48)) {
/* 1394 */       this._inputPtr = ptr;
/* 1395 */       return _handleInvalidNumberStart(ch, true);
/*      */     }
/*      */     
/* 1398 */     if (ch == 48) {
/* 1399 */       return _parseNumber2(true, startPtr);
/*      */     }
/* 1401 */     int intLen = 1;
/*      */     
/*      */ 
/*      */     for (;;)
/*      */     {
/* 1406 */       if (ptr >= inputLen) {
/* 1407 */         return _parseNumber2(true, startPtr);
/*      */       }
/* 1409 */       ch = this._inputBuffer[(ptr++)];
/* 1410 */       if ((ch < 48) || (ch > 57)) {
/*      */         break;
/*      */       }
/* 1413 */       intLen++;
/*      */     }
/*      */     
/* 1416 */     if ((ch == 46) || (ch == 101) || (ch == 69)) {
/* 1417 */       this._inputPtr = ptr;
/* 1418 */       return _parseFloat(ch, startPtr, ptr, true, intLen);
/*      */     }
/* 1420 */     ptr--;
/* 1421 */     this._inputPtr = ptr;
/* 1422 */     if (this._parsingContext.inRoot()) {
/* 1423 */       _verifyRootSpace(ch);
/*      */     }
/* 1425 */     int len = ptr - startPtr;
/* 1426 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/* 1427 */     return resetInt(true, intLen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final JsonToken _parseNumber2(boolean neg, int startPtr)
/*      */     throws IOException
/*      */   {
/* 1439 */     this._inputPtr = (neg ? startPtr + 1 : startPtr);
/* 1440 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1441 */     int outPtr = 0;
/*      */     
/*      */ 
/* 1444 */     if (neg) {
/* 1445 */       outBuf[(outPtr++)] = '-';
/*      */     }
/*      */     
/*      */ 
/* 1449 */     int intLen = 0;
/* 1450 */     char c = this._inputPtr < this._inputEnd ? this._inputBuffer[(this._inputPtr++)] : getNextChar("No digit following minus sign", JsonToken.VALUE_NUMBER_INT);
/*      */     
/* 1452 */     if (c == '0') {
/* 1453 */       c = _verifyNoLeadingZeroes();
/*      */     }
/* 1455 */     boolean eof = false;
/*      */     
/*      */ 
/*      */ 
/* 1459 */     while ((c >= '0') && (c <= '9')) {
/* 1460 */       intLen++;
/* 1461 */       if (outPtr >= outBuf.length) {
/* 1462 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1463 */         outPtr = 0;
/*      */       }
/* 1465 */       outBuf[(outPtr++)] = c;
/* 1466 */       if ((this._inputPtr >= this._inputEnd) && (!_loadMore()))
/*      */       {
/* 1468 */         c = '\000';
/* 1469 */         eof = true;
/* 1470 */         break;
/*      */       }
/* 1472 */       c = this._inputBuffer[(this._inputPtr++)];
/*      */     }
/*      */     
/* 1475 */     if (intLen == 0) {
/* 1476 */       return _handleInvalidNumberStart(c, neg);
/*      */     }
/*      */     
/* 1479 */     int fractLen = 0;
/*      */     
/* 1481 */     if (c == '.') {
/* 1482 */       outBuf[(outPtr++)] = c;
/*      */       
/*      */       for (;;)
/*      */       {
/* 1486 */         if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 1487 */           eof = true;
/* 1488 */           break;
/*      */         }
/* 1490 */         c = this._inputBuffer[(this._inputPtr++)];
/* 1491 */         if ((c < '0') || (c > '9')) {
/*      */           break;
/*      */         }
/* 1494 */         fractLen++;
/* 1495 */         if (outPtr >= outBuf.length) {
/* 1496 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1497 */           outPtr = 0;
/*      */         }
/* 1499 */         outBuf[(outPtr++)] = c;
/*      */       }
/*      */       
/* 1502 */       if (fractLen == 0) {
/* 1503 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/* 1507 */     int expLen = 0;
/* 1508 */     if ((c == 'e') || (c == 'E')) {
/* 1509 */       if (outPtr >= outBuf.length) {
/* 1510 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1511 */         outPtr = 0;
/*      */       }
/* 1513 */       outBuf[(outPtr++)] = c;
/*      */       
/* 1515 */       c = this._inputPtr < this._inputEnd ? this._inputBuffer[(this._inputPtr++)] : getNextChar("expected a digit for number exponent");
/*      */       
/*      */ 
/* 1518 */       if ((c == '-') || (c == '+')) {
/* 1519 */         if (outPtr >= outBuf.length) {
/* 1520 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1521 */           outPtr = 0;
/*      */         }
/* 1523 */         outBuf[(outPtr++)] = c;
/*      */         
/* 1525 */         c = this._inputPtr < this._inputEnd ? this._inputBuffer[(this._inputPtr++)] : getNextChar("expected a digit for number exponent");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1530 */       while ((c <= '9') && (c >= '0')) {
/* 1531 */         expLen++;
/* 1532 */         if (outPtr >= outBuf.length) {
/* 1533 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1534 */           outPtr = 0;
/*      */         }
/* 1536 */         outBuf[(outPtr++)] = c;
/* 1537 */         if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 1538 */           eof = true;
/* 1539 */           break;
/*      */         }
/* 1541 */         c = this._inputBuffer[(this._inputPtr++)];
/*      */       }
/*      */       
/* 1544 */       if (expLen == 0) {
/* 1545 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1550 */     if (!eof) {
/* 1551 */       this._inputPtr -= 1;
/* 1552 */       if (this._parsingContext.inRoot()) {
/* 1553 */         _verifyRootSpace(c);
/*      */       }
/*      */     }
/* 1556 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1558 */     return reset(neg, intLen, fractLen, expLen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final char _verifyNoLeadingZeroes()
/*      */     throws IOException
/*      */   {
/* 1568 */     if (this._inputPtr < this._inputEnd) {
/* 1569 */       char ch = this._inputBuffer[this._inputPtr];
/*      */       
/* 1571 */       if ((ch < '0') || (ch > '9')) {
/* 1572 */         return '0';
/*      */       }
/*      */     }
/*      */     
/* 1576 */     return _verifyNLZ2();
/*      */   }
/*      */   
/*      */   private char _verifyNLZ2() throws IOException
/*      */   {
/* 1581 */     if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 1582 */       return '0';
/*      */     }
/* 1584 */     char ch = this._inputBuffer[this._inputPtr];
/* 1585 */     if ((ch < '0') || (ch > '9')) {
/* 1586 */       return '0';
/*      */     }
/* 1588 */     if (!isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
/* 1589 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1592 */     this._inputPtr += 1;
/* 1593 */     if (ch == '0') {
/* 1594 */       while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 1595 */         ch = this._inputBuffer[this._inputPtr];
/* 1596 */         if ((ch < '0') || (ch > '9')) {
/* 1597 */           return '0';
/*      */         }
/* 1599 */         this._inputPtr += 1;
/* 1600 */         if (ch != '0') {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1605 */     return ch;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean negative)
/*      */     throws IOException
/*      */   {
/* 1614 */     if (ch == 73) {
/* 1615 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1616 */         (!_loadMore())) {
/* 1617 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */       }
/*      */       
/* 1620 */       ch = this._inputBuffer[(this._inputPtr++)];
/* 1621 */       if (ch == 78) {
/* 1622 */         String match = negative ? "-INF" : "+INF";
/* 1623 */         _matchToken(match, 3);
/* 1624 */         if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 1625 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 1627 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 1628 */       } else if (ch == 110) {
/* 1629 */         String match = negative ? "-Infinity" : "+Infinity";
/* 1630 */         _matchToken(match, 3);
/* 1631 */         if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 1632 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 1634 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */       }
/*      */     }
/* 1637 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 1638 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _verifyRootSpace(int ch)
/*      */     throws IOException
/*      */   {
/* 1651 */     this._inputPtr += 1;
/* 1652 */     switch (ch) {
/*      */     case 9: 
/*      */     case 32: 
/* 1655 */       return;
/*      */     case 13: 
/* 1657 */       _skipCR();
/* 1658 */       return;
/*      */     case 10: 
/* 1660 */       this._currInputRow += 1;
/* 1661 */       this._currInputRowStart = this._inputPtr;
/* 1662 */       return;
/*      */     }
/* 1664 */     _reportMissingRootWS(ch);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final String _parseName()
/*      */     throws IOException
/*      */   {
/* 1677 */     int ptr = this._inputPtr;
/* 1678 */     int hash = this._hashSeed;
/* 1679 */     int[] codes = _icLatin1;
/*      */     
/* 1681 */     while (ptr < this._inputEnd) {
/* 1682 */       int ch = this._inputBuffer[ptr];
/* 1683 */       if ((ch < codes.length) && (codes[ch] != 0)) {
/* 1684 */         if (ch != 34) break;
/* 1685 */         int start = this._inputPtr;
/* 1686 */         this._inputPtr = (ptr + 1);
/* 1687 */         return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */       }
/*      */       
/*      */ 
/* 1691 */       hash = hash * 33 + ch;
/* 1692 */       ptr++;
/*      */     }
/* 1694 */     int start = this._inputPtr;
/* 1695 */     this._inputPtr = ptr;
/* 1696 */     return _parseName2(start, hash, 34);
/*      */   }
/*      */   
/*      */   private String _parseName2(int startPtr, int hash, int endChar) throws IOException
/*      */   {
/* 1701 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1706 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1707 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     for (;;)
/*      */     {
/* 1710 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1711 */         (!_loadMore())) {
/* 1712 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 1715 */       char c = this._inputBuffer[(this._inputPtr++)];
/* 1716 */       int i = c;
/* 1717 */       if (i <= 92) {
/* 1718 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1723 */           c = _decodeEscaped();
/* 1724 */         } else if (i <= endChar) {
/* 1725 */           if (i == endChar) {
/*      */             break;
/*      */           }
/* 1728 */           if (i < 32) {
/* 1729 */             _throwUnquotedSpace(i, "name");
/*      */           }
/*      */         }
/*      */       }
/* 1733 */       hash = hash * 33 + c;
/*      */       
/* 1735 */       outBuf[(outPtr++)] = c;
/*      */       
/*      */ 
/* 1738 */       if (outPtr >= outBuf.length) {
/* 1739 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1740 */         outPtr = 0;
/*      */       }
/*      */     }
/* 1743 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1745 */     TextBuffer tb = this._textBuffer;
/* 1746 */     char[] buf = tb.getTextBuffer();
/* 1747 */     int start = tb.getTextOffset();
/* 1748 */     int len = tb.size();
/* 1749 */     return this._symbols.findSymbol(buf, start, len, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String _handleOddName(int i)
/*      */     throws IOException
/*      */   {
/* 1762 */     if ((i == 39) && (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES))) {
/* 1763 */       return _parseAposName();
/*      */     }
/*      */     
/* 1766 */     if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
/* 1767 */       _reportUnexpectedChar(i, "was expecting double-quote to start field name");
/*      */     }
/* 1769 */     int[] codes = CharTypes.getInputCodeLatin1JsNames();
/* 1770 */     int maxCode = codes.length;
/*      */     
/*      */     boolean firstOk;
/*      */     
/*      */     boolean firstOk;
/* 1775 */     if (i < maxCode) {
/* 1776 */       firstOk = codes[i] == 0;
/*      */     } else {
/* 1778 */       firstOk = Character.isJavaIdentifierPart((char)i);
/*      */     }
/* 1780 */     if (!firstOk) {
/* 1781 */       _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/* 1783 */     int ptr = this._inputPtr;
/* 1784 */     int hash = this._hashSeed;
/* 1785 */     int inputLen = this._inputEnd;
/*      */     
/* 1787 */     if (ptr < inputLen) {
/*      */       do {
/* 1789 */         int ch = this._inputBuffer[ptr];
/* 1790 */         if (ch < maxCode) {
/* 1791 */           if (codes[ch] != 0) {
/* 1792 */             int start = this._inputPtr - 1;
/* 1793 */             this._inputPtr = ptr;
/* 1794 */             return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */           }
/* 1796 */         } else if (!Character.isJavaIdentifierPart((char)ch)) {
/* 1797 */           int start = this._inputPtr - 1;
/* 1798 */           this._inputPtr = ptr;
/* 1799 */           return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */         }
/* 1801 */         hash = hash * 33 + ch;
/* 1802 */         ptr++;
/* 1803 */       } while (ptr < inputLen);
/*      */     }
/* 1805 */     int start = this._inputPtr - 1;
/* 1806 */     this._inputPtr = ptr;
/* 1807 */     return _handleOddName2(start, hash, codes);
/*      */   }
/*      */   
/*      */   protected String _parseAposName()
/*      */     throws IOException
/*      */   {
/* 1813 */     int ptr = this._inputPtr;
/* 1814 */     int hash = this._hashSeed;
/* 1815 */     int inputLen = this._inputEnd;
/*      */     
/* 1817 */     if (ptr < inputLen) {
/* 1818 */       int[] codes = _icLatin1;
/* 1819 */       int maxCode = codes.length;
/*      */       do
/*      */       {
/* 1822 */         int ch = this._inputBuffer[ptr];
/* 1823 */         if (ch == 39) {
/* 1824 */           int start = this._inputPtr;
/* 1825 */           this._inputPtr = (ptr + 1);
/* 1826 */           return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */         }
/* 1828 */         if ((ch < maxCode) && (codes[ch] != 0)) {
/*      */           break;
/*      */         }
/* 1831 */         hash = hash * 33 + ch;
/* 1832 */         ptr++;
/* 1833 */       } while (ptr < inputLen);
/*      */     }
/*      */     
/* 1836 */     int start = this._inputPtr;
/* 1837 */     this._inputPtr = ptr;
/*      */     
/* 1839 */     return _parseName2(start, hash, 39);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonToken _handleOddValue(int i)
/*      */     throws IOException
/*      */   {
/* 1849 */     switch (i)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 39: 
/* 1856 */       if (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/* 1857 */         return _handleApos();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       break;
/*      */     case 93: 
/* 1865 */       if (!this._parsingContext.inArray()) {
/*      */         break;
/*      */       }
/*      */     
/*      */     case 44: 
/* 1870 */       if (isEnabled(JsonParser.Feature.ALLOW_MISSING_VALUES)) {
/* 1871 */         this._inputPtr -= 1;
/* 1872 */         return JsonToken.VALUE_NULL;
/*      */       }
/*      */       break;
/*      */     case 78: 
/* 1876 */       _matchToken("NaN", 1);
/* 1877 */       if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 1878 */         return resetAsNaN("NaN", NaN.0D);
/*      */       }
/* 1880 */       _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 1881 */       break;
/*      */     case 73: 
/* 1883 */       _matchToken("Infinity", 1);
/* 1884 */       if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 1885 */         return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */       }
/* 1887 */       _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 1888 */       break;
/*      */     case 43: 
/* 1890 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1891 */         (!_loadMore())) {
/* 1892 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */       }
/*      */       
/* 1895 */       return _handleInvalidNumberStart(this._inputBuffer[(this._inputPtr++)], false);
/*      */     }
/*      */     
/* 1898 */     if (Character.isJavaIdentifierStart(i)) {
/* 1899 */       _reportInvalidToken("" + (char)i, "('true', 'false' or 'null')");
/*      */     }
/*      */     
/* 1902 */     _reportUnexpectedChar(i, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
/* 1903 */     return null;
/*      */   }
/*      */   
/*      */   protected JsonToken _handleApos() throws IOException
/*      */   {
/* 1908 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1909 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     for (;;)
/*      */     {
/* 1912 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1913 */         (!_loadMore())) {
/* 1914 */         _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/*      */ 
/* 1918 */       char c = this._inputBuffer[(this._inputPtr++)];
/* 1919 */       int i = c;
/* 1920 */       if (i <= 92) {
/* 1921 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1926 */           c = _decodeEscaped();
/* 1927 */         } else if (i <= 39) {
/* 1928 */           if (i == 39) {
/*      */             break;
/*      */           }
/* 1931 */           if (i < 32) {
/* 1932 */             _throwUnquotedSpace(i, "string value");
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1937 */       if (outPtr >= outBuf.length) {
/* 1938 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1939 */         outPtr = 0;
/*      */       }
/*      */       
/* 1942 */       outBuf[(outPtr++)] = c;
/*      */     }
/* 1944 */     this._textBuffer.setCurrentLength(outPtr);
/* 1945 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */   
/*      */   private String _handleOddName2(int startPtr, int hash, int[] codes) throws IOException
/*      */   {
/* 1950 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/* 1951 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1952 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 1953 */     int maxCode = codes.length;
/*      */     
/*      */ 
/* 1956 */     while ((this._inputPtr < this._inputEnd) || 
/* 1957 */       (_loadMore()))
/*      */     {
/*      */ 
/*      */ 
/* 1961 */       char c = this._inputBuffer[this._inputPtr];
/* 1962 */       int i = c;
/* 1963 */       if (i <= maxCode ? 
/* 1964 */         codes[i] != 0 : 
/*      */         
/*      */ 
/* 1967 */         !Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 1970 */       this._inputPtr += 1;
/* 1971 */       hash = hash * 33 + i;
/*      */       
/* 1973 */       outBuf[(outPtr++)] = c;
/*      */       
/*      */ 
/* 1976 */       if (outPtr >= outBuf.length) {
/* 1977 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1978 */         outPtr = 0;
/*      */       }
/*      */     }
/* 1981 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1983 */     TextBuffer tb = this._textBuffer;
/* 1984 */     char[] buf = tb.getTextBuffer();
/* 1985 */     int start = tb.getTextOffset();
/* 1986 */     int len = tb.size();
/*      */     
/* 1988 */     return this._symbols.findSymbol(buf, start, len, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _finishString()
/*      */     throws IOException
/*      */   {
/* 1999 */     int ptr = this._inputPtr;
/* 2000 */     int inputLen = this._inputEnd;
/*      */     
/* 2002 */     if (ptr < inputLen) {
/* 2003 */       int[] codes = _icLatin1;
/* 2004 */       int maxCode = codes.length;
/*      */       do
/*      */       {
/* 2007 */         int ch = this._inputBuffer[ptr];
/* 2008 */         if ((ch < maxCode) && (codes[ch] != 0)) {
/* 2009 */           if (ch != 34) break;
/* 2010 */           this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 2011 */           this._inputPtr = (ptr + 1);
/*      */           
/* 2013 */           return;
/*      */         }
/*      */         
/*      */ 
/* 2017 */         ptr++;
/* 2018 */       } while (ptr < inputLen);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2024 */     this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 2025 */     this._inputPtr = ptr;
/* 2026 */     _finishString2();
/*      */   }
/*      */   
/*      */   protected void _finishString2() throws IOException
/*      */   {
/* 2031 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 2032 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2033 */     int[] codes = _icLatin1;
/* 2034 */     int maxCode = codes.length;
/*      */     for (;;)
/*      */     {
/* 2037 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2038 */         (!_loadMore())) {
/* 2039 */         _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/*      */ 
/* 2043 */       char c = this._inputBuffer[(this._inputPtr++)];
/* 2044 */       int i = c;
/* 2045 */       if ((i < maxCode) && (codes[i] != 0)) {
/* 2046 */         if (i == 34)
/*      */           break;
/* 2048 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2053 */           c = _decodeEscaped();
/* 2054 */         } else if (i < 32) {
/* 2055 */           _throwUnquotedSpace(i, "string value");
/*      */         }
/*      */       }
/*      */       
/* 2059 */       if (outPtr >= outBuf.length) {
/* 2060 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2061 */         outPtr = 0;
/*      */       }
/*      */       
/* 2064 */       outBuf[(outPtr++)] = c;
/*      */     }
/* 2066 */     this._textBuffer.setCurrentLength(outPtr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _skipString()
/*      */     throws IOException
/*      */   {
/* 2076 */     this._tokenIncomplete = false;
/*      */     
/* 2078 */     int inPtr = this._inputPtr;
/* 2079 */     int inLen = this._inputEnd;
/* 2080 */     char[] inBuf = this._inputBuffer;
/*      */     for (;;)
/*      */     {
/* 2083 */       if (inPtr >= inLen) {
/* 2084 */         this._inputPtr = inPtr;
/* 2085 */         if (!_loadMore()) {
/* 2086 */           _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */         }
/*      */         
/* 2089 */         inPtr = this._inputPtr;
/* 2090 */         inLen = this._inputEnd;
/*      */       }
/* 2092 */       char c = inBuf[(inPtr++)];
/* 2093 */       int i = c;
/* 2094 */       if (i <= 92) {
/* 2095 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2100 */           this._inputPtr = inPtr;
/* 2101 */           c = _decodeEscaped();
/* 2102 */           inPtr = this._inputPtr;
/* 2103 */           inLen = this._inputEnd;
/* 2104 */         } else if (i <= 34) {
/* 2105 */           if (i == 34) {
/* 2106 */             this._inputPtr = inPtr;
/* 2107 */             break;
/*      */           }
/* 2109 */           if (i < 32) {
/* 2110 */             this._inputPtr = inPtr;
/* 2111 */             _throwUnquotedSpace(i, "string value");
/*      */           }
/*      */         }
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
/*      */   protected final void _skipCR()
/*      */     throws IOException
/*      */   {
/* 2129 */     if (((this._inputPtr < this._inputEnd) || (_loadMore())) && 
/* 2130 */       (this._inputBuffer[this._inputPtr] == '\n')) {
/* 2131 */       this._inputPtr += 1;
/*      */     }
/*      */     
/* 2134 */     this._currInputRow += 1;
/* 2135 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */   
/*      */   private final int _skipColon() throws IOException
/*      */   {
/* 2140 */     if (this._inputPtr + 4 >= this._inputEnd) {
/* 2141 */       return _skipColon2(false);
/*      */     }
/* 2143 */     char c = this._inputBuffer[this._inputPtr];
/* 2144 */     if (c == ':') {
/* 2145 */       int i = this._inputBuffer[(++this._inputPtr)];
/* 2146 */       if (i > 32) {
/* 2147 */         if ((i == 47) || (i == 35)) {
/* 2148 */           return _skipColon2(true);
/*      */         }
/* 2150 */         this._inputPtr += 1;
/* 2151 */         return i;
/*      */       }
/* 2153 */       if ((i == 32) || (i == 9)) {
/* 2154 */         i = this._inputBuffer[(++this._inputPtr)];
/* 2155 */         if (i > 32) {
/* 2156 */           if ((i == 47) || (i == 35)) {
/* 2157 */             return _skipColon2(true);
/*      */           }
/* 2159 */           this._inputPtr += 1;
/* 2160 */           return i;
/*      */         }
/*      */       }
/* 2163 */       return _skipColon2(true);
/*      */     }
/* 2165 */     if ((c == ' ') || (c == '\t')) {
/* 2166 */       c = this._inputBuffer[(++this._inputPtr)];
/*      */     }
/* 2168 */     if (c == ':') {
/* 2169 */       int i = this._inputBuffer[(++this._inputPtr)];
/* 2170 */       if (i > 32) {
/* 2171 */         if ((i == 47) || (i == 35)) {
/* 2172 */           return _skipColon2(true);
/*      */         }
/* 2174 */         this._inputPtr += 1;
/* 2175 */         return i;
/*      */       }
/* 2177 */       if ((i == 32) || (i == 9)) {
/* 2178 */         i = this._inputBuffer[(++this._inputPtr)];
/* 2179 */         if (i > 32) {
/* 2180 */           if ((i == 47) || (i == 35)) {
/* 2181 */             return _skipColon2(true);
/*      */           }
/* 2183 */           this._inputPtr += 1;
/* 2184 */           return i;
/*      */         }
/*      */       }
/* 2187 */       return _skipColon2(true);
/*      */     }
/* 2189 */     return _skipColon2(false);
/*      */   }
/*      */   
/*      */   private final int _skipColon2(boolean gotColon) throws IOException
/*      */   {
/* 2194 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 2195 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 2196 */       if (i > 32) {
/* 2197 */         if (i == 47) {
/* 2198 */           _skipComment();
/*      */ 
/*      */         }
/* 2201 */         else if ((i != 35) || 
/* 2202 */           (!_skipYAMLComment()))
/*      */         {
/*      */ 
/*      */ 
/* 2206 */           if (gotColon) {
/* 2207 */             return i;
/*      */           }
/* 2209 */           if (i != 58) {
/* 2210 */             if (i < 32) {
/* 2211 */               _throwInvalidSpace(i);
/*      */             }
/* 2213 */             _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */           }
/* 2215 */           gotColon = true;
/*      */         }
/*      */       }
/* 2218 */       else if (i < 32) {
/* 2219 */         if (i == 10) {
/* 2220 */           this._currInputRow += 1;
/* 2221 */           this._currInputRowStart = this._inputPtr;
/* 2222 */         } else if (i == 13) {
/* 2223 */           _skipCR();
/* 2224 */         } else if (i != 9) {
/* 2225 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2229 */     _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
/*      */     
/* 2231 */     return -1;
/*      */   }
/*      */   
/*      */   private final int _skipColonFast(int ptr)
/*      */     throws IOException
/*      */   {
/* 2237 */     int i = this._inputBuffer[(ptr++)];
/* 2238 */     if (i == 58) {
/* 2239 */       i = this._inputBuffer[(ptr++)];
/* 2240 */       if (i > 32) {
/* 2241 */         if ((i != 47) && (i != 35)) {
/* 2242 */           this._inputPtr = ptr;
/* 2243 */           return i;
/*      */         }
/* 2245 */       } else if ((i == 32) || (i == 9)) {
/* 2246 */         i = this._inputBuffer[(ptr++)];
/* 2247 */         if ((i > 32) && 
/* 2248 */           (i != 47) && (i != 35)) {
/* 2249 */           this._inputPtr = ptr;
/* 2250 */           return i;
/*      */         }
/*      */       }
/*      */       
/* 2254 */       this._inputPtr = (ptr - 1);
/* 2255 */       return _skipColon2(true);
/*      */     }
/* 2257 */     if ((i == 32) || (i == 9)) {
/* 2258 */       i = this._inputBuffer[(ptr++)];
/*      */     }
/* 2260 */     boolean gotColon = i == 58;
/* 2261 */     if (gotColon) {
/* 2262 */       i = this._inputBuffer[(ptr++)];
/* 2263 */       if (i > 32) {
/* 2264 */         if ((i != 47) && (i != 35)) {
/* 2265 */           this._inputPtr = ptr;
/* 2266 */           return i;
/*      */         }
/* 2268 */       } else if ((i == 32) || (i == 9)) {
/* 2269 */         i = this._inputBuffer[(ptr++)];
/* 2270 */         if ((i > 32) && 
/* 2271 */           (i != 47) && (i != 35)) {
/* 2272 */           this._inputPtr = ptr;
/* 2273 */           return i;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2278 */     this._inputPtr = (ptr - 1);
/* 2279 */     return _skipColon2(gotColon);
/*      */   }
/*      */   
/*      */   private final int _skipComma(int i)
/*      */     throws IOException
/*      */   {
/* 2285 */     if (i != 44) {
/* 2286 */       _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */     }
/* 2288 */     while (this._inputPtr < this._inputEnd) {
/* 2289 */       i = this._inputBuffer[(this._inputPtr++)];
/* 2290 */       if (i > 32) {
/* 2291 */         if ((i == 47) || (i == 35)) {
/* 2292 */           this._inputPtr -= 1;
/* 2293 */           return _skipAfterComma2();
/*      */         }
/* 2295 */         return i;
/*      */       }
/* 2297 */       if (i < 32) {
/* 2298 */         if (i == 10) {
/* 2299 */           this._currInputRow += 1;
/* 2300 */           this._currInputRowStart = this._inputPtr;
/* 2301 */         } else if (i == 13) {
/* 2302 */           _skipCR();
/* 2303 */         } else if (i != 9) {
/* 2304 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2308 */     return _skipAfterComma2();
/*      */   }
/*      */   
/*      */   private final int _skipAfterComma2() throws IOException
/*      */   {
/* 2313 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 2314 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 2315 */       if (i > 32) {
/* 2316 */         if (i == 47) {
/* 2317 */           _skipComment();
/*      */ 
/*      */         }
/* 2320 */         else if ((i != 35) || 
/* 2321 */           (!_skipYAMLComment()))
/*      */         {
/*      */ 
/*      */ 
/* 2325 */           return i;
/*      */         }
/* 2327 */       } else if (i < 32) {
/* 2328 */         if (i == 10) {
/* 2329 */           this._currInputRow += 1;
/* 2330 */           this._currInputRowStart = this._inputPtr;
/* 2331 */         } else if (i == 13) {
/* 2332 */           _skipCR();
/* 2333 */         } else if (i != 9) {
/* 2334 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2338 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
/*      */   }
/*      */   
/*      */ 
/*      */   private final int _skipWSOrEnd()
/*      */     throws IOException
/*      */   {
/* 2345 */     if ((this._inputPtr >= this._inputEnd) && 
/* 2346 */       (!_loadMore())) {
/* 2347 */       return _eofAsNextChar();
/*      */     }
/*      */     
/* 2350 */     int i = this._inputBuffer[(this._inputPtr++)];
/* 2351 */     if (i > 32) {
/* 2352 */       if ((i == 47) || (i == 35)) {
/* 2353 */         this._inputPtr -= 1;
/* 2354 */         return _skipWSOrEnd2();
/*      */       }
/* 2356 */       return i;
/*      */     }
/* 2358 */     if (i != 32) {
/* 2359 */       if (i == 10) {
/* 2360 */         this._currInputRow += 1;
/* 2361 */         this._currInputRowStart = this._inputPtr;
/* 2362 */       } else if (i == 13) {
/* 2363 */         _skipCR();
/* 2364 */       } else if (i != 9) {
/* 2365 */         _throwInvalidSpace(i);
/*      */       }
/*      */     }
/*      */     
/* 2369 */     while (this._inputPtr < this._inputEnd) {
/* 2370 */       i = this._inputBuffer[(this._inputPtr++)];
/* 2371 */       if (i > 32) {
/* 2372 */         if ((i == 47) || (i == 35)) {
/* 2373 */           this._inputPtr -= 1;
/* 2374 */           return _skipWSOrEnd2();
/*      */         }
/* 2376 */         return i;
/*      */       }
/* 2378 */       if (i != 32) {
/* 2379 */         if (i == 10) {
/* 2380 */           this._currInputRow += 1;
/* 2381 */           this._currInputRowStart = this._inputPtr;
/* 2382 */         } else if (i == 13) {
/* 2383 */           _skipCR();
/* 2384 */         } else if (i != 9) {
/* 2385 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2389 */     return _skipWSOrEnd2();
/*      */   }
/*      */   
/*      */   private int _skipWSOrEnd2() throws IOException
/*      */   {
/*      */     for (;;) {
/* 2395 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2396 */         (!_loadMore())) {
/* 2397 */         return _eofAsNextChar();
/*      */       }
/*      */       
/* 2400 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 2401 */       if (i > 32) {
/* 2402 */         if (i == 47) {
/* 2403 */           _skipComment();
/*      */ 
/*      */         }
/* 2406 */         else if ((i != 35) || 
/* 2407 */           (!_skipYAMLComment()))
/*      */         {
/*      */ 
/*      */ 
/* 2411 */           return i; }
/* 2412 */       } else if (i != 32) {
/* 2413 */         if (i == 10) {
/* 2414 */           this._currInputRow += 1;
/* 2415 */           this._currInputRowStart = this._inputPtr;
/* 2416 */         } else if (i == 13) {
/* 2417 */           _skipCR();
/* 2418 */         } else if (i != 9) {
/* 2419 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void _skipComment() throws IOException
/*      */   {
/* 2427 */     if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
/* 2428 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 2431 */     if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 2432 */       _reportInvalidEOF(" in a comment", null);
/*      */     }
/* 2434 */     char c = this._inputBuffer[(this._inputPtr++)];
/* 2435 */     if (c == '/') {
/* 2436 */       _skipLine();
/* 2437 */     } else if (c == '*') {
/* 2438 */       _skipCComment();
/*      */     } else {
/* 2440 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     }
/*      */   }
/*      */   
/*      */   private void _skipCComment()
/*      */     throws IOException
/*      */   {
/* 2447 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 2448 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 2449 */       if (i <= 42) {
/* 2450 */         if (i == 42) {
/* 2451 */           if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/*      */             break;
/*      */           }
/* 2454 */           if (this._inputBuffer[this._inputPtr] == '/') {
/* 2455 */             this._inputPtr += 1;
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 2460 */         else if (i < 32) {
/* 2461 */           if (i == 10) {
/* 2462 */             this._currInputRow += 1;
/* 2463 */             this._currInputRowStart = this._inputPtr;
/* 2464 */           } else if (i == 13) {
/* 2465 */             _skipCR();
/* 2466 */           } else if (i != 9) {
/* 2467 */             _throwInvalidSpace(i);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2472 */     _reportInvalidEOF(" in a comment", null);
/*      */   }
/*      */   
/*      */   private boolean _skipYAMLComment() throws IOException
/*      */   {
/* 2477 */     if (!isEnabled(JsonParser.Feature.ALLOW_YAML_COMMENTS)) {
/* 2478 */       return false;
/*      */     }
/* 2480 */     _skipLine();
/* 2481 */     return true;
/*      */   }
/*      */   
/*      */   private void _skipLine()
/*      */     throws IOException
/*      */   {
/* 2487 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 2488 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 2489 */       if (i < 32) {
/* 2490 */         if (i == 10) {
/* 2491 */           this._currInputRow += 1;
/* 2492 */           this._currInputRowStart = this._inputPtr;
/* 2493 */           break; }
/* 2494 */         if (i == 13) {
/* 2495 */           _skipCR();
/* 2496 */           break; }
/* 2497 */         if (i != 9) {
/* 2498 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected char _decodeEscaped()
/*      */     throws IOException
/*      */   {
/* 2507 */     if ((this._inputPtr >= this._inputEnd) && 
/* 2508 */       (!_loadMore())) {
/* 2509 */       _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */     }
/*      */     
/* 2512 */     char c = this._inputBuffer[(this._inputPtr++)];
/*      */     
/* 2514 */     switch (c)
/*      */     {
/*      */     case 'b': 
/* 2517 */       return '\b';
/*      */     case 't': 
/* 2519 */       return '\t';
/*      */     case 'n': 
/* 2521 */       return '\n';
/*      */     case 'f': 
/* 2523 */       return '\f';
/*      */     case 'r': 
/* 2525 */       return '\r';
/*      */     
/*      */ 
/*      */     case '"': 
/*      */     case '/': 
/*      */     case '\\': 
/* 2531 */       return c;
/*      */     
/*      */     case 'u': 
/*      */       break;
/*      */     
/*      */     default: 
/* 2537 */       return _handleUnrecognizedCharacterEscape(c);
/*      */     }
/*      */     
/*      */     
/* 2541 */     int value = 0;
/* 2542 */     for (int i = 0; i < 4; i++) {
/* 2543 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2544 */         (!_loadMore())) {
/* 2545 */         _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/* 2548 */       int ch = this._inputBuffer[(this._inputPtr++)];
/* 2549 */       int digit = CharTypes.charToHex(ch);
/* 2550 */       if (digit < 0) {
/* 2551 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2553 */       value = value << 4 | digit;
/*      */     }
/* 2555 */     return (char)value;
/*      */   }
/*      */   
/*      */   private final void _matchTrue() throws IOException {
/* 2559 */     int ptr = this._inputPtr;
/* 2560 */     if (ptr + 3 < this._inputEnd) {
/* 2561 */       char[] b = this._inputBuffer;
/* 2562 */       if ((b[ptr] == 'r') && (b[(++ptr)] == 'u') && (b[(++ptr)] == 'e')) {
/* 2563 */         char c = b[(++ptr)];
/* 2564 */         if ((c < '0') || (c == ']') || (c == '}')) {
/* 2565 */           this._inputPtr = ptr;
/* 2566 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2571 */     _matchToken("true", 1);
/*      */   }
/*      */   
/*      */   private final void _matchFalse() throws IOException {
/* 2575 */     int ptr = this._inputPtr;
/* 2576 */     if (ptr + 4 < this._inputEnd) {
/* 2577 */       char[] b = this._inputBuffer;
/* 2578 */       if ((b[ptr] == 'a') && (b[(++ptr)] == 'l') && (b[(++ptr)] == 's') && (b[(++ptr)] == 'e')) {
/* 2579 */         char c = b[(++ptr)];
/* 2580 */         if ((c < '0') || (c == ']') || (c == '}')) {
/* 2581 */           this._inputPtr = ptr;
/* 2582 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2587 */     _matchToken("false", 1);
/*      */   }
/*      */   
/*      */   private final void _matchNull() throws IOException {
/* 2591 */     int ptr = this._inputPtr;
/* 2592 */     if (ptr + 3 < this._inputEnd) {
/* 2593 */       char[] b = this._inputBuffer;
/* 2594 */       if ((b[ptr] == 'u') && (b[(++ptr)] == 'l') && (b[(++ptr)] == 'l')) {
/* 2595 */         char c = b[(++ptr)];
/* 2596 */         if ((c < '0') || (c == ']') || (c == '}')) {
/* 2597 */           this._inputPtr = ptr;
/* 2598 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2603 */     _matchToken("null", 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected final void _matchToken(String matchStr, int i)
/*      */     throws IOException
/*      */   {
/* 2611 */     int len = matchStr.length();
/*      */     do
/*      */     {
/* 2614 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2615 */         (!_loadMore())) {
/* 2616 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/*      */       
/* 2619 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 2620 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2622 */       this._inputPtr += 1;
/* 2623 */       i++; } while (i < len);
/*      */     
/*      */ 
/* 2626 */     if ((this._inputPtr >= this._inputEnd) && 
/* 2627 */       (!_loadMore())) {
/* 2628 */       return;
/*      */     }
/*      */     
/* 2631 */     char c = this._inputBuffer[this._inputPtr];
/* 2632 */     if ((c < '0') || (c == ']') || (c == '}')) {
/* 2633 */       return;
/*      */     }
/*      */     
/* 2636 */     if (Character.isJavaIdentifierPart(c)) {
/* 2637 */       _reportInvalidToken(matchStr.substring(0, i));
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
/*      */   protected byte[] _decodeBase64(Base64Variant b64variant)
/*      */     throws IOException
/*      */   {
/* 2655 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 2662 */       if (this._inputPtr >= this._inputEnd) {
/* 2663 */         _loadMoreGuaranteed();
/*      */       }
/* 2665 */       char ch = this._inputBuffer[(this._inputPtr++)];
/* 2666 */       if (ch > ' ') {
/* 2667 */         int bits = b64variant.decodeBase64Char(ch);
/* 2668 */         if (bits < 0) {
/* 2669 */           if (ch == '"') {
/* 2670 */             return builder.toByteArray();
/*      */           }
/* 2672 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 2673 */           if (bits < 0) {}
/*      */         }
/*      */         else
/*      */         {
/* 2677 */           int decodedData = bits;
/*      */           
/*      */ 
/*      */ 
/* 2681 */           if (this._inputPtr >= this._inputEnd) {
/* 2682 */             _loadMoreGuaranteed();
/*      */           }
/* 2684 */           ch = this._inputBuffer[(this._inputPtr++)];
/* 2685 */           bits = b64variant.decodeBase64Char(ch);
/* 2686 */           if (bits < 0) {
/* 2687 */             bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */           }
/* 2689 */           decodedData = decodedData << 6 | bits;
/*      */           
/*      */ 
/* 2692 */           if (this._inputPtr >= this._inputEnd) {
/* 2693 */             _loadMoreGuaranteed();
/*      */           }
/* 2695 */           ch = this._inputBuffer[(this._inputPtr++)];
/* 2696 */           bits = b64variant.decodeBase64Char(ch);
/*      */           
/*      */ 
/* 2699 */           if (bits < 0) {
/* 2700 */             if (bits != -2)
/*      */             {
/* 2702 */               if ((ch == '"') && (!b64variant.usesPadding())) {
/* 2703 */                 decodedData >>= 4;
/* 2704 */                 builder.append(decodedData);
/* 2705 */                 return builder.toByteArray();
/*      */               }
/* 2707 */               bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */             }
/* 2709 */             if (bits == -2)
/*      */             {
/* 2711 */               if (this._inputPtr >= this._inputEnd) {
/* 2712 */                 _loadMoreGuaranteed();
/*      */               }
/* 2714 */               ch = this._inputBuffer[(this._inputPtr++)];
/* 2715 */               if (!b64variant.usesPaddingChar(ch)) {
/* 2716 */                 throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */               }
/*      */               
/* 2719 */               decodedData >>= 4;
/* 2720 */               builder.append(decodedData);
/* 2721 */               continue;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 2726 */           decodedData = decodedData << 6 | bits;
/*      */           
/* 2728 */           if (this._inputPtr >= this._inputEnd) {
/* 2729 */             _loadMoreGuaranteed();
/*      */           }
/* 2731 */           ch = this._inputBuffer[(this._inputPtr++)];
/* 2732 */           bits = b64variant.decodeBase64Char(ch);
/* 2733 */           if (bits < 0) {
/* 2734 */             if (bits != -2)
/*      */             {
/* 2736 */               if ((ch == '"') && (!b64variant.usesPadding())) {
/* 2737 */                 decodedData >>= 2;
/* 2738 */                 builder.appendTwoBytes(decodedData);
/* 2739 */                 return builder.toByteArray();
/*      */               }
/* 2741 */               bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */             }
/* 2743 */             if (bits == -2)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2749 */               decodedData >>= 2;
/* 2750 */               builder.appendTwoBytes(decodedData);
/* 2751 */               continue;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 2756 */           decodedData = decodedData << 6 | bits;
/* 2757 */           builder.appendThreeBytes(decodedData);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonLocation getTokenLocation()
/*      */   {
/* 2770 */     Object src = this._ioContext.getSourceReference();
/* 2771 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 2772 */       long total = this._currInputProcessed + (this._nameStartOffset - 1L);
/* 2773 */       return new JsonLocation(src, -1L, total, this._nameStartRow, this._nameStartCol);
/*      */     }
/*      */     
/* 2776 */     return new JsonLocation(src, -1L, this._tokenInputTotal - 1L, this._tokenInputRow, this._tokenInputCol);
/*      */   }
/*      */   
/*      */ 
/*      */   public JsonLocation getCurrentLocation()
/*      */   {
/* 2782 */     int col = this._inputPtr - this._currInputRowStart + 1;
/* 2783 */     return new JsonLocation(this._ioContext.getSourceReference(), -1L, this._currInputProcessed + this._inputPtr, this._currInputRow, col);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _updateLocation()
/*      */   {
/* 2791 */     int ptr = this._inputPtr;
/* 2792 */     this._tokenInputTotal = (this._currInputProcessed + ptr);
/* 2793 */     this._tokenInputRow = this._currInputRow;
/* 2794 */     this._tokenInputCol = (ptr - this._currInputRowStart);
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _updateNameLocation()
/*      */   {
/* 2800 */     int ptr = this._inputPtr;
/* 2801 */     this._nameStartOffset = ptr;
/* 2802 */     this._nameStartRow = this._currInputRow;
/* 2803 */     this._nameStartCol = (ptr - this._currInputRowStart);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _reportInvalidToken(String matchedPart)
/*      */     throws IOException
/*      */   {
/* 2813 */     _reportInvalidToken(matchedPart, "'null', 'true', 'false' or NaN");
/*      */   }
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException
/*      */   {
/* 2818 */     StringBuilder sb = new StringBuilder(matchedPart);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2824 */     while ((this._inputPtr < this._inputEnd) || 
/* 2825 */       (_loadMore()))
/*      */     {
/*      */ 
/*      */ 
/* 2829 */       char c = this._inputBuffer[this._inputPtr];
/* 2830 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 2833 */       this._inputPtr += 1;
/* 2834 */       sb.append(c);
/*      */     }
/* 2836 */     _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\ReaderBasedJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */