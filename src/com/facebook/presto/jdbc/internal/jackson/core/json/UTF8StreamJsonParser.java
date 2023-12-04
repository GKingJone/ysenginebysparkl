/*      */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParseException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.base.ParserBase;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharTypes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.IOContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.ByteArrayBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.TextBuffer;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Writer;
/*      */ import java.util.Arrays;
/*      */ 
/*      */ public class UTF8StreamJsonParser extends ParserBase
/*      */ {
/*      */   static final byte BYTE_LF = 10;
/*   25 */   private static final int[] _icUTF8 = ;
/*      */   
/*      */ 
/*      */ 
/*   29 */   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
/*      */   
/*      */ 
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
/*      */ 
/*      */ 
/*      */   protected final ByteQuadsCanonicalizer _symbols;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   58 */   protected int[] _quadBuffer = new int[16];
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
/*      */   private int _quad1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _nameStartOffset;
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
/*      */   protected InputStream _inputStream;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] _inputBuffer;
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
/*      */ 
/*      */   public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable)
/*      */   {
/*  133 */     super(ctxt, features);
/*  134 */     this._inputStream = in;
/*  135 */     this._objectCodec = codec;
/*  136 */     this._symbols = sym;
/*  137 */     this._inputBuffer = inputBuffer;
/*  138 */     this._inputPtr = start;
/*  139 */     this._inputEnd = end;
/*  140 */     this._currInputRowStart = start;
/*      */     
/*  142 */     this._currInputProcessed = (-start);
/*  143 */     this._bufferRecyclable = bufferRecyclable;
/*      */   }
/*      */   
/*      */   public ObjectCodec getCodec()
/*      */   {
/*  148 */     return this._objectCodec;
/*      */   }
/*      */   
/*      */   public void setCodec(ObjectCodec c)
/*      */   {
/*  153 */     this._objectCodec = c;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int releaseBuffered(OutputStream out)
/*      */     throws IOException
/*      */   {
/*  165 */     int count = this._inputEnd - this._inputPtr;
/*  166 */     if (count < 1) {
/*  167 */       return 0;
/*      */     }
/*      */     
/*  170 */     int origPtr = this._inputPtr;
/*  171 */     out.write(this._inputBuffer, origPtr, count);
/*  172 */     return count;
/*      */   }
/*      */   
/*      */   public Object getInputSource()
/*      */   {
/*  177 */     return this._inputStream;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _loadMore()
/*      */     throws IOException
/*      */   {
/*  188 */     int bufSize = this._inputEnd;
/*      */     
/*  190 */     this._currInputProcessed += this._inputEnd;
/*  191 */     this._currInputRowStart -= this._inputEnd;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  196 */     this._nameStartOffset -= bufSize;
/*      */     
/*  198 */     if (this._inputStream != null) {
/*  199 */       int space = this._inputBuffer.length;
/*  200 */       if (space == 0) {
/*  201 */         return false;
/*      */       }
/*      */       
/*  204 */       int count = this._inputStream.read(this._inputBuffer, 0, space);
/*  205 */       if (count > 0) {
/*  206 */         this._inputPtr = 0;
/*  207 */         this._inputEnd = count;
/*  208 */         return true;
/*      */       }
/*      */       
/*  211 */       _closeInput();
/*      */       
/*  213 */       if (count == 0) {
/*  214 */         throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
/*      */       }
/*      */     }
/*  217 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _loadToHaveAtLeast(int minAvailable)
/*      */     throws IOException
/*      */   {
/*  227 */     if (this._inputStream == null) {
/*  228 */       return false;
/*      */     }
/*      */     
/*  231 */     int amount = this._inputEnd - this._inputPtr;
/*  232 */     if ((amount > 0) && (this._inputPtr > 0)) {
/*  233 */       int ptr = this._inputPtr;
/*      */       
/*  235 */       this._currInputProcessed += ptr;
/*  236 */       this._currInputRowStart -= ptr;
/*      */       
/*      */ 
/*  239 */       this._nameStartOffset -= ptr;
/*      */       
/*  241 */       System.arraycopy(this._inputBuffer, ptr, this._inputBuffer, 0, amount);
/*  242 */       this._inputEnd = amount;
/*      */     } else {
/*  244 */       this._inputEnd = 0;
/*      */     }
/*  246 */     this._inputPtr = 0;
/*  247 */     while (this._inputEnd < minAvailable) {
/*  248 */       int count = this._inputStream.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
/*  249 */       if (count < 1)
/*      */       {
/*  251 */         _closeInput();
/*      */         
/*  253 */         if (count == 0) {
/*  254 */           throw new IOException("InputStream.read() returned 0 characters when trying to read " + amount + " bytes");
/*      */         }
/*  256 */         return false;
/*      */       }
/*  258 */       this._inputEnd += count;
/*      */     }
/*  260 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _closeInput()
/*      */     throws IOException
/*      */   {
/*  270 */     if (this._inputStream != null) {
/*  271 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE))) {
/*  272 */         this._inputStream.close();
/*      */       }
/*  274 */       this._inputStream = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */     throws IOException
/*      */   {
/*  287 */     super._releaseBuffers();
/*      */     
/*  289 */     this._symbols.release();
/*  290 */     if (this._bufferRecyclable) {
/*  291 */       byte[] buf = this._inputBuffer;
/*  292 */       if (buf != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  297 */         this._inputBuffer = ByteArrayBuilder.NO_BYTES;
/*  298 */         this._ioContext.releaseReadIOBuffer(buf);
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
/*      */   public String getText()
/*      */     throws IOException
/*      */   {
/*  312 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  313 */       if (this._tokenIncomplete) {
/*  314 */         this._tokenIncomplete = false;
/*  315 */         return _finishAndReturnString();
/*      */       }
/*  317 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  319 */     return _getText2(this._currToken);
/*      */   }
/*      */   
/*      */   public int getText(Writer writer)
/*      */     throws IOException
/*      */   {
/*  325 */     JsonToken t = this._currToken;
/*  326 */     if (t == JsonToken.VALUE_STRING) {
/*  327 */       if (this._tokenIncomplete) {
/*  328 */         this._tokenIncomplete = false;
/*  329 */         _finishString();
/*      */       }
/*  331 */       return this._textBuffer.contentsToWriter(writer);
/*      */     }
/*  333 */     if (t == JsonToken.FIELD_NAME) {
/*  334 */       String n = this._parsingContext.getCurrentName();
/*  335 */       writer.write(n);
/*  336 */       return n.length();
/*      */     }
/*  338 */     if (t != null) {
/*  339 */       if (t.isNumeric()) {
/*  340 */         return this._textBuffer.contentsToWriter(writer);
/*      */       }
/*  342 */       char[] ch = t.asCharArray();
/*  343 */       writer.write(ch);
/*  344 */       return ch.length;
/*      */     }
/*  346 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getValueAsString()
/*      */     throws IOException
/*      */   {
/*  355 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  356 */       if (this._tokenIncomplete) {
/*  357 */         this._tokenIncomplete = false;
/*  358 */         return _finishAndReturnString();
/*      */       }
/*  360 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  362 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  363 */       return getCurrentName();
/*      */     }
/*  365 */     return super.getValueAsString(null);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getValueAsString(String defValue)
/*      */     throws IOException
/*      */   {
/*  372 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  373 */       if (this._tokenIncomplete) {
/*  374 */         this._tokenIncomplete = false;
/*  375 */         return _finishAndReturnString();
/*      */       }
/*  377 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  379 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  380 */       return getCurrentName();
/*      */     }
/*  382 */     return super.getValueAsString(defValue);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getValueAsInt()
/*      */     throws IOException
/*      */   {
/*  389 */     JsonToken t = this._currToken;
/*  390 */     if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT))
/*      */     {
/*  392 */       if ((this._numTypesValid & 0x1) == 0) {
/*  393 */         if (this._numTypesValid == 0) {
/*  394 */           return _parseIntValue();
/*      */         }
/*  396 */         if ((this._numTypesValid & 0x1) == 0) {
/*  397 */           convertNumberToInt();
/*      */         }
/*      */       }
/*  400 */       return this._numberInt;
/*      */     }
/*  402 */     return super.getValueAsInt(0);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getValueAsInt(int defValue)
/*      */     throws IOException
/*      */   {
/*  409 */     JsonToken t = this._currToken;
/*  410 */     if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT))
/*      */     {
/*  412 */       if ((this._numTypesValid & 0x1) == 0) {
/*  413 */         if (this._numTypesValid == 0) {
/*  414 */           return _parseIntValue();
/*      */         }
/*  416 */         if ((this._numTypesValid & 0x1) == 0) {
/*  417 */           convertNumberToInt();
/*      */         }
/*      */       }
/*  420 */       return this._numberInt;
/*      */     }
/*  422 */     return super.getValueAsInt(defValue);
/*      */   }
/*      */   
/*      */   protected final String _getText2(JsonToken t)
/*      */   {
/*  427 */     if (t == null) {
/*  428 */       return null;
/*      */     }
/*  430 */     switch (t.id()) {
/*      */     case 5: 
/*  432 */       return this._parsingContext.getCurrentName();
/*      */     
/*      */ 
/*      */     case 6: 
/*      */     case 7: 
/*      */     case 8: 
/*  438 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  440 */     return t.asString();
/*      */   }
/*      */   
/*      */ 
/*      */   public char[] getTextCharacters()
/*      */     throws IOException
/*      */   {
/*  447 */     if (this._currToken != null) {
/*  448 */       switch (this._currToken.id())
/*      */       {
/*      */       case 5: 
/*  451 */         if (!this._nameCopied) {
/*  452 */           String name = this._parsingContext.getCurrentName();
/*  453 */           int nameLen = name.length();
/*  454 */           if (this._nameCopyBuffer == null) {
/*  455 */             this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  456 */           } else if (this._nameCopyBuffer.length < nameLen) {
/*  457 */             this._nameCopyBuffer = new char[nameLen];
/*      */           }
/*  459 */           name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  460 */           this._nameCopied = true;
/*      */         }
/*  462 */         return this._nameCopyBuffer;
/*      */       
/*      */       case 6: 
/*  465 */         if (this._tokenIncomplete) {
/*  466 */           this._tokenIncomplete = false;
/*  467 */           _finishString();
/*      */         }
/*      */       
/*      */       case 7: 
/*      */       case 8: 
/*  472 */         return this._textBuffer.getTextBuffer();
/*      */       }
/*      */       
/*  475 */       return this._currToken.asCharArray();
/*      */     }
/*      */     
/*  478 */     return null;
/*      */   }
/*      */   
/*      */   public int getTextLength()
/*      */     throws IOException
/*      */   {
/*  484 */     if (this._currToken != null) {
/*  485 */       switch (this._currToken.id())
/*      */       {
/*      */       case 5: 
/*  488 */         return this._parsingContext.getCurrentName().length();
/*      */       case 6: 
/*  490 */         if (this._tokenIncomplete) {
/*  491 */           this._tokenIncomplete = false;
/*  492 */           _finishString();
/*      */         }
/*      */       
/*      */       case 7: 
/*      */       case 8: 
/*  497 */         return this._textBuffer.size();
/*      */       }
/*      */       
/*  500 */       return this._currToken.asCharArray().length;
/*      */     }
/*      */     
/*  503 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTextOffset()
/*      */     throws IOException
/*      */   {
/*  510 */     if (this._currToken != null) {
/*  511 */       switch (this._currToken.id()) {
/*      */       case 5: 
/*  513 */         return 0;
/*      */       case 6: 
/*  515 */         if (this._tokenIncomplete) {
/*  516 */           this._tokenIncomplete = false;
/*  517 */           _finishString();
/*      */         }
/*      */       
/*      */       case 7: 
/*      */       case 8: 
/*  522 */         return this._textBuffer.getTextOffset();
/*      */       }
/*      */       
/*      */     }
/*  526 */     return 0;
/*      */   }
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant)
/*      */     throws IOException
/*      */   {
/*  532 */     if ((this._currToken != JsonToken.VALUE_STRING) && ((this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT) || (this._binaryValue == null)))
/*      */     {
/*  534 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  539 */     if (this._tokenIncomplete) {
/*      */       try {
/*  541 */         this._binaryValue = _decodeBase64(b64variant);
/*      */       } catch (IllegalArgumentException iae) {
/*  543 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  548 */       this._tokenIncomplete = false;
/*      */     }
/*  550 */     else if (this._binaryValue == null)
/*      */     {
/*  552 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  553 */       _decodeBase64(getText(), builder, b64variant);
/*  554 */       this._binaryValue = builder.toByteArray();
/*      */     }
/*      */     
/*  557 */     return this._binaryValue;
/*      */   }
/*      */   
/*      */ 
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out)
/*      */     throws IOException
/*      */   {
/*  564 */     if ((!this._tokenIncomplete) || (this._currToken != JsonToken.VALUE_STRING)) {
/*  565 */       byte[] b = getBinaryValue(b64variant);
/*  566 */       out.write(b);
/*  567 */       return b.length;
/*      */     }
/*      */     
/*  570 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  572 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  574 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     }
/*      */   }
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer)
/*      */     throws IOException
/*      */   {
/*  581 */     int outputPtr = 0;
/*  582 */     int outputEnd = buffer.length - 3;
/*  583 */     int outputCount = 0;
/*      */     
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/*  589 */       if (this._inputPtr >= this._inputEnd) {
/*  590 */         _loadMoreGuaranteed();
/*      */       }
/*  592 */       int ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*  593 */       if (ch > 32) {
/*  594 */         int bits = b64variant.decodeBase64Char(ch);
/*  595 */         if (bits < 0) {
/*  596 */           if (ch == 34) {
/*      */             break;
/*      */           }
/*  599 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  600 */           if (bits < 0) {}
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  606 */           if (outputPtr > outputEnd) {
/*  607 */             outputCount += outputPtr;
/*  608 */             out.write(buffer, 0, outputPtr);
/*  609 */             outputPtr = 0;
/*      */           }
/*      */           
/*  612 */           int decodedData = bits;
/*      */           
/*      */ 
/*      */ 
/*  616 */           if (this._inputPtr >= this._inputEnd) {
/*  617 */             _loadMoreGuaranteed();
/*      */           }
/*  619 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*  620 */           bits = b64variant.decodeBase64Char(ch);
/*  621 */           if (bits < 0) {
/*  622 */             bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */           }
/*  624 */           decodedData = decodedData << 6 | bits;
/*      */           
/*      */ 
/*  627 */           if (this._inputPtr >= this._inputEnd) {
/*  628 */             _loadMoreGuaranteed();
/*      */           }
/*  630 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*  631 */           bits = b64variant.decodeBase64Char(ch);
/*      */           
/*      */ 
/*  634 */           if (bits < 0) {
/*  635 */             if (bits != -2)
/*      */             {
/*  637 */               if ((ch == 34) && (!b64variant.usesPadding())) {
/*  638 */                 decodedData >>= 4;
/*  639 */                 buffer[(outputPtr++)] = ((byte)decodedData);
/*  640 */                 break;
/*      */               }
/*  642 */               bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */             }
/*  644 */             if (bits == -2)
/*      */             {
/*  646 */               if (this._inputPtr >= this._inputEnd) {
/*  647 */                 _loadMoreGuaranteed();
/*      */               }
/*  649 */               ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*  650 */               if (!b64variant.usesPaddingChar(ch)) {
/*  651 */                 throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */               }
/*      */               
/*  654 */               decodedData >>= 4;
/*  655 */               buffer[(outputPtr++)] = ((byte)decodedData);
/*  656 */               continue;
/*      */             }
/*      */           }
/*      */           
/*  660 */           decodedData = decodedData << 6 | bits;
/*      */           
/*  662 */           if (this._inputPtr >= this._inputEnd) {
/*  663 */             _loadMoreGuaranteed();
/*      */           }
/*  665 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*  666 */           bits = b64variant.decodeBase64Char(ch);
/*  667 */           if (bits < 0) {
/*  668 */             if (bits != -2)
/*      */             {
/*  670 */               if ((ch == 34) && (!b64variant.usesPadding())) {
/*  671 */                 decodedData >>= 2;
/*  672 */                 buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
/*  673 */                 buffer[(outputPtr++)] = ((byte)decodedData);
/*  674 */                 break;
/*      */               }
/*  676 */               bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */             }
/*  678 */             if (bits == -2)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  685 */               decodedData >>= 2;
/*  686 */               buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
/*  687 */               buffer[(outputPtr++)] = ((byte)decodedData);
/*  688 */               continue;
/*      */             }
/*      */           }
/*      */           
/*  692 */           decodedData = decodedData << 6 | bits;
/*  693 */           buffer[(outputPtr++)] = ((byte)(decodedData >> 16));
/*  694 */           buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
/*  695 */           buffer[(outputPtr++)] = ((byte)decodedData);
/*      */         } } }
/*  697 */     this._tokenIncomplete = false;
/*  698 */     if (outputPtr > 0) {
/*  699 */       outputCount += outputPtr;
/*  700 */       out.write(buffer, 0, outputPtr);
/*      */     }
/*  702 */     return outputCount;
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
/*      */   public JsonToken nextToken()
/*      */     throws IOException
/*      */   {
/*  722 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  723 */       return _nextAfterName();
/*      */     }
/*      */     
/*      */ 
/*  727 */     this._numTypesValid = 0;
/*  728 */     if (this._tokenIncomplete) {
/*  729 */       _skipString();
/*      */     }
/*  731 */     int i = _skipWSOrEnd();
/*  732 */     if (i < 0)
/*      */     {
/*  734 */       close();
/*  735 */       return this._currToken = null;
/*      */     }
/*      */     
/*  738 */     this._binaryValue = null;
/*      */     
/*      */ 
/*  741 */     if (i == 93) {
/*  742 */       _updateLocation();
/*  743 */       if (!this._parsingContext.inArray()) {
/*  744 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  746 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  747 */       return this._currToken = JsonToken.END_ARRAY;
/*      */     }
/*  749 */     if (i == 125) {
/*  750 */       _updateLocation();
/*  751 */       if (!this._parsingContext.inObject()) {
/*  752 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  754 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  755 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     }
/*      */     
/*      */ 
/*  759 */     if (this._parsingContext.expectComma()) {
/*  760 */       if (i != 44) {
/*  761 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  763 */       i = _skipWS();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  770 */     if (!this._parsingContext.inObject()) {
/*  771 */       _updateLocation();
/*  772 */       return _nextTokenNotInObject(i);
/*      */     }
/*      */     
/*  775 */     _updateNameLocation();
/*  776 */     String n = _parseName(i);
/*  777 */     this._parsingContext.setCurrentName(n);
/*  778 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/*  780 */     i = _skipColon();
/*  781 */     _updateLocation();
/*      */     
/*      */ 
/*  784 */     if (i == 34) {
/*  785 */       this._tokenIncomplete = true;
/*  786 */       this._nextToken = JsonToken.VALUE_STRING;
/*  787 */       return this._currToken;
/*      */     }
/*      */     
/*      */     JsonToken t;
/*  791 */     switch (i) {
/*      */     case 45: 
/*  793 */       t = _parseNegNumber();
/*  794 */       break;
/*      */     
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
/*  810 */       t = _parsePosNumber(i);
/*  811 */       break;
/*      */     case 102: 
/*  813 */       _matchToken("false", 1);
/*  814 */       t = JsonToken.VALUE_FALSE;
/*  815 */       break;
/*      */     case 110: 
/*  817 */       _matchToken("null", 1);
/*  818 */       t = JsonToken.VALUE_NULL;
/*  819 */       break;
/*      */     case 116: 
/*  821 */       _matchToken("true", 1);
/*  822 */       t = JsonToken.VALUE_TRUE;
/*  823 */       break;
/*      */     case 91: 
/*  825 */       t = JsonToken.START_ARRAY;
/*  826 */       break;
/*      */     case 123: 
/*  828 */       t = JsonToken.START_OBJECT;
/*  829 */       break;
/*      */     
/*      */     default: 
/*  832 */       t = _handleUnexpectedValue(i);
/*      */     }
/*  834 */     this._nextToken = t;
/*  835 */     return this._currToken;
/*      */   }
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException
/*      */   {
/*  840 */     if (i == 34) {
/*  841 */       this._tokenIncomplete = true;
/*  842 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     }
/*  844 */     switch (i) {
/*      */     case 91: 
/*  846 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  847 */       return this._currToken = JsonToken.START_ARRAY;
/*      */     case 123: 
/*  849 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*  850 */       return this._currToken = JsonToken.START_OBJECT;
/*      */     case 116: 
/*  852 */       _matchToken("true", 1);
/*  853 */       return this._currToken = JsonToken.VALUE_TRUE;
/*      */     case 102: 
/*  855 */       _matchToken("false", 1);
/*  856 */       return this._currToken = JsonToken.VALUE_FALSE;
/*      */     case 110: 
/*  858 */       _matchToken("null", 1);
/*  859 */       return this._currToken = JsonToken.VALUE_NULL;
/*      */     case 45: 
/*  861 */       return this._currToken = _parseNegNumber();
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
/*  876 */       return this._currToken = _parsePosNumber(i);
/*      */     }
/*  878 */     return this._currToken = _handleUnexpectedValue(i);
/*      */   }
/*      */   
/*      */   private final JsonToken _nextAfterName()
/*      */   {
/*  883 */     this._nameCopied = false;
/*  884 */     JsonToken t = this._nextToken;
/*  885 */     this._nextToken = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  890 */     if (t == JsonToken.START_ARRAY) {
/*  891 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  892 */     } else if (t == JsonToken.START_OBJECT) {
/*  893 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     }
/*  895 */     return this._currToken = t;
/*      */   }
/*      */   
/*      */   public void finishToken() throws IOException
/*      */   {
/*  900 */     if (this._tokenIncomplete) {
/*  901 */       this._tokenIncomplete = false;
/*  902 */       _finishString();
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
/*      */   public boolean nextFieldName(SerializableString str)
/*      */     throws IOException
/*      */   {
/*  916 */     this._numTypesValid = 0;
/*  917 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  918 */       _nextAfterName();
/*  919 */       return false;
/*      */     }
/*  921 */     if (this._tokenIncomplete) {
/*  922 */       _skipString();
/*      */     }
/*  924 */     int i = _skipWSOrEnd();
/*  925 */     if (i < 0) {
/*  926 */       close();
/*  927 */       this._currToken = null;
/*  928 */       return false;
/*      */     }
/*  930 */     this._binaryValue = null;
/*      */     
/*      */ 
/*  933 */     if (i == 93) {
/*  934 */       _updateLocation();
/*  935 */       if (!this._parsingContext.inArray()) {
/*  936 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  938 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  939 */       this._currToken = JsonToken.END_ARRAY;
/*  940 */       return false;
/*      */     }
/*  942 */     if (i == 125) {
/*  943 */       _updateLocation();
/*  944 */       if (!this._parsingContext.inObject()) {
/*  945 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  947 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/*  948 */       this._currToken = JsonToken.END_OBJECT;
/*  949 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  953 */     if (this._parsingContext.expectComma()) {
/*  954 */       if (i != 44) {
/*  955 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  957 */       i = _skipWS();
/*      */     }
/*      */     
/*  960 */     if (!this._parsingContext.inObject()) {
/*  961 */       _updateLocation();
/*  962 */       _nextTokenNotInObject(i);
/*  963 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  967 */     _updateNameLocation();
/*  968 */     if (i == 34)
/*      */     {
/*  970 */       byte[] nameBytes = str.asQuotedUTF8();
/*  971 */       int len = nameBytes.length;
/*      */       
/*      */ 
/*  974 */       if (this._inputPtr + len + 4 < this._inputEnd)
/*      */       {
/*  976 */         int end = this._inputPtr + len;
/*  977 */         if (this._inputBuffer[end] == 34) {
/*  978 */           int offset = 0;
/*  979 */           int ptr = this._inputPtr;
/*      */           for (;;) {
/*  981 */             if (ptr == end) {
/*  982 */               this._parsingContext.setCurrentName(str.getValue());
/*  983 */               i = _skipColonFast(ptr + 1);
/*  984 */               _isNextTokenNameYes(i);
/*  985 */               return true;
/*      */             }
/*  987 */             if (nameBytes[offset] != this._inputBuffer[ptr]) {
/*      */               break;
/*      */             }
/*  990 */             offset++;
/*  991 */             ptr++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  996 */     return _isNextTokenNameMaybe(i, str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String nextFieldName()
/*      */     throws IOException
/*      */   {
/* 1004 */     this._numTypesValid = 0;
/* 1005 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1006 */       _nextAfterName();
/* 1007 */       return null;
/*      */     }
/* 1009 */     if (this._tokenIncomplete) {
/* 1010 */       _skipString();
/*      */     }
/* 1012 */     int i = _skipWSOrEnd();
/* 1013 */     if (i < 0) {
/* 1014 */       close();
/* 1015 */       this._currToken = null;
/* 1016 */       return null;
/*      */     }
/* 1018 */     this._binaryValue = null;
/*      */     
/* 1020 */     if (i == 93) {
/* 1021 */       _updateLocation();
/* 1022 */       if (!this._parsingContext.inArray()) {
/* 1023 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/* 1025 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 1026 */       this._currToken = JsonToken.END_ARRAY;
/* 1027 */       return null;
/*      */     }
/* 1029 */     if (i == 125) {
/* 1030 */       _updateLocation();
/* 1031 */       if (!this._parsingContext.inObject()) {
/* 1032 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/* 1034 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 1035 */       this._currToken = JsonToken.END_OBJECT;
/* 1036 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 1040 */     if (this._parsingContext.expectComma()) {
/* 1041 */       if (i != 44) {
/* 1042 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/* 1044 */       i = _skipWS();
/*      */     }
/* 1046 */     if (!this._parsingContext.inObject()) {
/* 1047 */       _updateLocation();
/* 1048 */       _nextTokenNotInObject(i);
/* 1049 */       return null;
/*      */     }
/*      */     
/* 1052 */     _updateNameLocation();
/* 1053 */     String nameStr = _parseName(i);
/* 1054 */     this._parsingContext.setCurrentName(nameStr);
/* 1055 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/* 1057 */     i = _skipColon();
/* 1058 */     _updateLocation();
/* 1059 */     if (i == 34) {
/* 1060 */       this._tokenIncomplete = true;
/* 1061 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1062 */       return nameStr;
/*      */     }
/*      */     JsonToken t;
/* 1065 */     switch (i) {
/*      */     case 45: 
/* 1067 */       t = _parseNegNumber();
/* 1068 */       break;
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
/* 1079 */       t = _parsePosNumber(i);
/* 1080 */       break;
/*      */     case 102: 
/* 1082 */       _matchToken("false", 1);
/* 1083 */       t = JsonToken.VALUE_FALSE;
/* 1084 */       break;
/*      */     case 110: 
/* 1086 */       _matchToken("null", 1);
/* 1087 */       t = JsonToken.VALUE_NULL;
/* 1088 */       break;
/*      */     case 116: 
/* 1090 */       _matchToken("true", 1);
/* 1091 */       t = JsonToken.VALUE_TRUE;
/* 1092 */       break;
/*      */     case 91: 
/* 1094 */       t = JsonToken.START_ARRAY;
/* 1095 */       break;
/*      */     case 123: 
/* 1097 */       t = JsonToken.START_OBJECT;
/* 1098 */       break;
/*      */     
/*      */     default: 
/* 1101 */       t = _handleUnexpectedValue(i);
/*      */     }
/* 1103 */     this._nextToken = t;
/* 1104 */     return nameStr;
/*      */   }
/*      */   
/*      */   private final int _skipColonFast(int ptr)
/*      */     throws IOException
/*      */   {
/* 1110 */     int i = this._inputBuffer[(ptr++)];
/* 1111 */     if (i == 58) {
/* 1112 */       i = this._inputBuffer[(ptr++)];
/* 1113 */       if (i > 32) {
/* 1114 */         if ((i != 47) && (i != 35)) {
/* 1115 */           this._inputPtr = ptr;
/* 1116 */           return i;
/*      */         }
/* 1118 */       } else if ((i == 32) || (i == 9)) {
/* 1119 */         i = this._inputBuffer[(ptr++)];
/* 1120 */         if ((i > 32) && 
/* 1121 */           (i != 47) && (i != 35)) {
/* 1122 */           this._inputPtr = ptr;
/* 1123 */           return i;
/*      */         }
/*      */       }
/*      */       
/* 1127 */       this._inputPtr = (ptr - 1);
/* 1128 */       return _skipColon2(true);
/*      */     }
/* 1130 */     if ((i == 32) || (i == 9)) {
/* 1131 */       i = this._inputBuffer[(ptr++)];
/*      */     }
/* 1133 */     if (i == 58) {
/* 1134 */       i = this._inputBuffer[(ptr++)];
/* 1135 */       if (i > 32) {
/* 1136 */         if ((i != 47) && (i != 35)) {
/* 1137 */           this._inputPtr = ptr;
/* 1138 */           return i;
/*      */         }
/* 1140 */       } else if ((i == 32) || (i == 9)) {
/* 1141 */         i = this._inputBuffer[(ptr++)];
/* 1142 */         if ((i > 32) && 
/* 1143 */           (i != 47) && (i != 35)) {
/* 1144 */           this._inputPtr = ptr;
/* 1145 */           return i;
/*      */         }
/*      */       }
/*      */       
/* 1149 */       this._inputPtr = (ptr - 1);
/* 1150 */       return _skipColon2(true);
/*      */     }
/* 1152 */     this._inputPtr = (ptr - 1);
/* 1153 */     return _skipColon2(false);
/*      */   }
/*      */   
/*      */   private final void _isNextTokenNameYes(int i) throws IOException
/*      */   {
/* 1158 */     this._currToken = JsonToken.FIELD_NAME;
/* 1159 */     _updateLocation();
/*      */     
/* 1161 */     switch (i) {
/*      */     case 34: 
/* 1163 */       this._tokenIncomplete = true;
/* 1164 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1165 */       return;
/*      */     case 91: 
/* 1167 */       this._nextToken = JsonToken.START_ARRAY;
/* 1168 */       return;
/*      */     case 123: 
/* 1170 */       this._nextToken = JsonToken.START_OBJECT;
/* 1171 */       return;
/*      */     case 116: 
/* 1173 */       _matchToken("true", 1);
/* 1174 */       this._nextToken = JsonToken.VALUE_TRUE;
/* 1175 */       return;
/*      */     case 102: 
/* 1177 */       _matchToken("false", 1);
/* 1178 */       this._nextToken = JsonToken.VALUE_FALSE;
/* 1179 */       return;
/*      */     case 110: 
/* 1181 */       _matchToken("null", 1);
/* 1182 */       this._nextToken = JsonToken.VALUE_NULL;
/* 1183 */       return;
/*      */     case 45: 
/* 1185 */       this._nextToken = _parseNegNumber();
/* 1186 */       return;
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
/* 1197 */       this._nextToken = _parsePosNumber(i);
/* 1198 */       return;
/*      */     }
/* 1200 */     this._nextToken = _handleUnexpectedValue(i);
/*      */   }
/*      */   
/*      */ 
/*      */   private final boolean _isNextTokenNameMaybe(int i, SerializableString str)
/*      */     throws IOException
/*      */   {
/* 1207 */     String n = _parseName(i);
/* 1208 */     this._parsingContext.setCurrentName(n);
/* 1209 */     boolean match = n.equals(str.getValue());
/* 1210 */     this._currToken = JsonToken.FIELD_NAME;
/* 1211 */     i = _skipColon();
/* 1212 */     _updateLocation();
/*      */     
/*      */ 
/* 1215 */     if (i == 34) {
/* 1216 */       this._tokenIncomplete = true;
/* 1217 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1218 */       return match;
/*      */     }
/*      */     
/*      */     JsonToken t;
/* 1222 */     switch (i) {
/*      */     case 91: 
/* 1224 */       t = JsonToken.START_ARRAY;
/* 1225 */       break;
/*      */     case 123: 
/* 1227 */       t = JsonToken.START_OBJECT;
/* 1228 */       break;
/*      */     case 116: 
/* 1230 */       _matchToken("true", 1);
/* 1231 */       t = JsonToken.VALUE_TRUE;
/* 1232 */       break;
/*      */     case 102: 
/* 1234 */       _matchToken("false", 1);
/* 1235 */       t = JsonToken.VALUE_FALSE;
/* 1236 */       break;
/*      */     case 110: 
/* 1238 */       _matchToken("null", 1);
/* 1239 */       t = JsonToken.VALUE_NULL;
/* 1240 */       break;
/*      */     case 45: 
/* 1242 */       t = _parseNegNumber();
/* 1243 */       break;
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
/* 1254 */       t = _parsePosNumber(i);
/* 1255 */       break;
/*      */     default: 
/* 1257 */       t = _handleUnexpectedValue(i);
/*      */     }
/* 1259 */     this._nextToken = t;
/* 1260 */     return match;
/*      */   }
/*      */   
/*      */ 
/*      */   public String nextTextValue()
/*      */     throws IOException
/*      */   {
/* 1267 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1268 */       this._nameCopied = false;
/* 1269 */       JsonToken t = this._nextToken;
/* 1270 */       this._nextToken = null;
/* 1271 */       this._currToken = t;
/* 1272 */       if (t == JsonToken.VALUE_STRING) {
/* 1273 */         if (this._tokenIncomplete) {
/* 1274 */           this._tokenIncomplete = false;
/* 1275 */           return _finishAndReturnString();
/*      */         }
/* 1277 */         return this._textBuffer.contentsAsString();
/*      */       }
/* 1279 */       if (t == JsonToken.START_ARRAY) {
/* 1280 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1281 */       } else if (t == JsonToken.START_OBJECT) {
/* 1282 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1284 */       return null;
/*      */     }
/*      */     
/* 1287 */     return nextToken() == JsonToken.VALUE_STRING ? getText() : null;
/*      */   }
/*      */   
/*      */ 
/*      */   public int nextIntValue(int defaultValue)
/*      */     throws IOException
/*      */   {
/* 1294 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1295 */       this._nameCopied = false;
/* 1296 */       JsonToken t = this._nextToken;
/* 1297 */       this._nextToken = null;
/* 1298 */       this._currToken = t;
/* 1299 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1300 */         return getIntValue();
/*      */       }
/* 1302 */       if (t == JsonToken.START_ARRAY) {
/* 1303 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1304 */       } else if (t == JsonToken.START_OBJECT) {
/* 1305 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1307 */       return defaultValue;
/*      */     }
/*      */     
/* 1310 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */   public long nextLongValue(long defaultValue)
/*      */     throws IOException
/*      */   {
/* 1317 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1318 */       this._nameCopied = false;
/* 1319 */       JsonToken t = this._nextToken;
/* 1320 */       this._nextToken = null;
/* 1321 */       this._currToken = t;
/* 1322 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1323 */         return getLongValue();
/*      */       }
/* 1325 */       if (t == JsonToken.START_ARRAY) {
/* 1326 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1327 */       } else if (t == JsonToken.START_OBJECT) {
/* 1328 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1330 */       return defaultValue;
/*      */     }
/*      */     
/* 1333 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */   public Boolean nextBooleanValue()
/*      */     throws IOException
/*      */   {
/* 1340 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1341 */       this._nameCopied = false;
/* 1342 */       JsonToken t = this._nextToken;
/* 1343 */       this._nextToken = null;
/* 1344 */       this._currToken = t;
/* 1345 */       if (t == JsonToken.VALUE_TRUE) {
/* 1346 */         return Boolean.TRUE;
/*      */       }
/* 1348 */       if (t == JsonToken.VALUE_FALSE) {
/* 1349 */         return Boolean.FALSE;
/*      */       }
/* 1351 */       if (t == JsonToken.START_ARRAY) {
/* 1352 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1353 */       } else if (t == JsonToken.START_OBJECT) {
/* 1354 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/* 1356 */       return null;
/*      */     }
/*      */     
/* 1359 */     JsonToken t = nextToken();
/* 1360 */     if (t == JsonToken.VALUE_TRUE) {
/* 1361 */       return Boolean.TRUE;
/*      */     }
/* 1363 */     if (t == JsonToken.VALUE_FALSE) {
/* 1364 */       return Boolean.FALSE;
/*      */     }
/* 1366 */     return null;
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
/*      */   protected JsonToken _parsePosNumber(int c)
/*      */     throws IOException
/*      */   {
/* 1392 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */     
/* 1394 */     if (c == 48) {
/* 1395 */       c = _verifyNoLeadingZeroes();
/*      */     }
/*      */     
/* 1398 */     outBuf[0] = ((char)c);
/* 1399 */     int intLen = 1;
/* 1400 */     int outPtr = 1;
/*      */     
/*      */ 
/* 1403 */     int end = this._inputPtr + outBuf.length - 1;
/* 1404 */     if (end > this._inputEnd) {
/* 1405 */       end = this._inputEnd;
/*      */     }
/*      */     for (;;)
/*      */     {
/* 1409 */       if (this._inputPtr >= end) {
/* 1410 */         return _parseNumber2(outBuf, outPtr, false, intLen);
/*      */       }
/* 1412 */       c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1413 */       if ((c < 48) || (c > 57)) {
/*      */         break;
/*      */       }
/* 1416 */       intLen++;
/* 1417 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 1419 */     if ((c == 46) || (c == 101) || (c == 69)) {
/* 1420 */       return _parseFloat(outBuf, outPtr, c, false, intLen);
/*      */     }
/* 1422 */     this._inputPtr -= 1;
/* 1423 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1425 */     if (this._parsingContext.inRoot()) {
/* 1426 */       _verifyRootSpace(c);
/*      */     }
/*      */     
/* 1429 */     return resetInt(false, intLen);
/*      */   }
/*      */   
/*      */   protected JsonToken _parseNegNumber() throws IOException
/*      */   {
/* 1434 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1435 */     int outPtr = 0;
/*      */     
/*      */ 
/* 1438 */     outBuf[(outPtr++)] = '-';
/*      */     
/* 1440 */     if (this._inputPtr >= this._inputEnd) {
/* 1441 */       _loadMoreGuaranteed();
/*      */     }
/* 1443 */     int c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */     
/* 1445 */     if ((c < 48) || (c > 57)) {
/* 1446 */       return _handleInvalidNumberStart(c, true);
/*      */     }
/*      */     
/*      */ 
/* 1450 */     if (c == 48) {
/* 1451 */       c = _verifyNoLeadingZeroes();
/*      */     }
/*      */     
/*      */ 
/* 1455 */     outBuf[(outPtr++)] = ((char)c);
/* 1456 */     int intLen = 1;
/*      */     
/*      */ 
/*      */ 
/* 1460 */     int end = this._inputPtr + outBuf.length - outPtr;
/* 1461 */     if (end > this._inputEnd) {
/* 1462 */       end = this._inputEnd;
/*      */     }
/*      */     
/*      */     for (;;)
/*      */     {
/* 1467 */       if (this._inputPtr >= end)
/*      */       {
/* 1469 */         return _parseNumber2(outBuf, outPtr, true, intLen);
/*      */       }
/* 1471 */       c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1472 */       if ((c < 48) || (c > 57)) {
/*      */         break;
/*      */       }
/* 1475 */       intLen++;
/* 1476 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 1478 */     if ((c == 46) || (c == 101) || (c == 69)) {
/* 1479 */       return _parseFloat(outBuf, outPtr, c, true, intLen);
/*      */     }
/*      */     
/* 1482 */     this._inputPtr -= 1;
/* 1483 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1485 */     if (this._parsingContext.inRoot()) {
/* 1486 */       _verifyRootSpace(c);
/*      */     }
/*      */     
/*      */ 
/* 1490 */     return resetInt(true, intLen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final JsonToken _parseNumber2(char[] outBuf, int outPtr, boolean negative, int intPartLength)
/*      */     throws IOException
/*      */   {
/*      */     for (;;)
/*      */     {
/* 1502 */       if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 1503 */         this._textBuffer.setCurrentLength(outPtr);
/* 1504 */         return resetInt(negative, intPartLength);
/*      */       }
/* 1506 */       int c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1507 */       if ((c > 57) || (c < 48)) {
/* 1508 */         if ((c != 46) && (c != 101) && (c != 69)) break;
/* 1509 */         return _parseFloat(outBuf, outPtr, c, negative, intPartLength);
/*      */       }
/*      */       
/*      */ 
/* 1513 */       if (outPtr >= outBuf.length) {
/* 1514 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1515 */         outPtr = 0;
/*      */       }
/* 1517 */       outBuf[(outPtr++)] = ((char)c);
/* 1518 */       intPartLength++;
/*      */     }
/* 1520 */     this._inputPtr -= 1;
/* 1521 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1523 */     if (this._parsingContext.inRoot()) {
/* 1524 */       _verifyRootSpace(this._inputBuffer[(this._inputPtr++)] & 0xFF);
/*      */     }
/*      */     
/*      */ 
/* 1528 */     return resetInt(negative, intPartLength);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _verifyNoLeadingZeroes()
/*      */     throws IOException
/*      */   {
/* 1539 */     if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 1540 */       return 48;
/*      */     }
/* 1542 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     
/* 1544 */     if ((ch < 48) || (ch > 57)) {
/* 1545 */       return 48;
/*      */     }
/*      */     
/* 1548 */     if (!isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
/* 1549 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1552 */     this._inputPtr += 1;
/* 1553 */     if (ch == 48) {
/* 1554 */       while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 1555 */         ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 1556 */         if ((ch < 48) || (ch > 57)) {
/* 1557 */           return 48;
/*      */         }
/* 1559 */         this._inputPtr += 1;
/* 1560 */         if (ch != 48) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1565 */     return ch;
/*      */   }
/*      */   
/*      */   private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength)
/*      */     throws IOException
/*      */   {
/* 1571 */     int fractLen = 0;
/* 1572 */     boolean eof = false;
/*      */     
/*      */ 
/* 1575 */     if (c == 46) {
/* 1576 */       outBuf[(outPtr++)] = ((char)c);
/*      */       
/*      */       for (;;)
/*      */       {
/* 1580 */         if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 1581 */           eof = true;
/* 1582 */           break;
/*      */         }
/* 1584 */         c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1585 */         if ((c < 48) || (c > 57)) {
/*      */           break;
/*      */         }
/* 1588 */         fractLen++;
/* 1589 */         if (outPtr >= outBuf.length) {
/* 1590 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1591 */           outPtr = 0;
/*      */         }
/* 1593 */         outBuf[(outPtr++)] = ((char)c);
/*      */       }
/*      */       
/* 1596 */       if (fractLen == 0) {
/* 1597 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/* 1601 */     int expLen = 0;
/* 1602 */     if ((c == 101) || (c == 69)) {
/* 1603 */       if (outPtr >= outBuf.length) {
/* 1604 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1605 */         outPtr = 0;
/*      */       }
/* 1607 */       outBuf[(outPtr++)] = ((char)c);
/*      */       
/* 1609 */       if (this._inputPtr >= this._inputEnd) {
/* 1610 */         _loadMoreGuaranteed();
/*      */       }
/* 1612 */       c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       
/* 1614 */       if ((c == 45) || (c == 43)) {
/* 1615 */         if (outPtr >= outBuf.length) {
/* 1616 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1617 */           outPtr = 0;
/*      */         }
/* 1619 */         outBuf[(outPtr++)] = ((char)c);
/*      */         
/* 1621 */         if (this._inputPtr >= this._inputEnd) {
/* 1622 */           _loadMoreGuaranteed();
/*      */         }
/* 1624 */         c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       }
/*      */       
/*      */ 
/* 1628 */       while ((c <= 57) && (c >= 48)) {
/* 1629 */         expLen++;
/* 1630 */         if (outPtr >= outBuf.length) {
/* 1631 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1632 */           outPtr = 0;
/*      */         }
/* 1634 */         outBuf[(outPtr++)] = ((char)c);
/* 1635 */         if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 1636 */           eof = true;
/* 1637 */           break;
/*      */         }
/* 1639 */         c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       }
/*      */       
/* 1642 */       if (expLen == 0) {
/* 1643 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1648 */     if (!eof) {
/* 1649 */       this._inputPtr -= 1;
/*      */       
/* 1651 */       if (this._parsingContext.inRoot()) {
/* 1652 */         _verifyRootSpace(c);
/*      */       }
/*      */     }
/* 1655 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/*      */ 
/* 1658 */     return resetFloat(negative, integerPartLength, fractLen, expLen);
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
/* 1671 */     this._inputPtr += 1;
/*      */     
/* 1673 */     switch (ch) {
/*      */     case 9: 
/*      */     case 32: 
/* 1676 */       return;
/*      */     case 13: 
/* 1678 */       _skipCR();
/* 1679 */       return;
/*      */     case 10: 
/* 1681 */       this._currInputRow += 1;
/* 1682 */       this._currInputRowStart = this._inputPtr;
/* 1683 */       return;
/*      */     }
/* 1685 */     _reportMissingRootWS(ch);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final String _parseName(int i)
/*      */     throws IOException
/*      */   {
/* 1696 */     if (i != 34) {
/* 1697 */       return _handleOddName(i);
/*      */     }
/*      */     
/* 1700 */     if (this._inputPtr + 13 > this._inputEnd) {
/* 1701 */       return slowParseName();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1710 */     byte[] input = this._inputBuffer;
/* 1711 */     int[] codes = _icLatin1;
/*      */     
/* 1713 */     int q = input[(this._inputPtr++)] & 0xFF;
/*      */     
/* 1715 */     if (codes[q] == 0) {
/* 1716 */       i = input[(this._inputPtr++)] & 0xFF;
/* 1717 */       if (codes[i] == 0) {
/* 1718 */         q = q << 8 | i;
/* 1719 */         i = input[(this._inputPtr++)] & 0xFF;
/* 1720 */         if (codes[i] == 0) {
/* 1721 */           q = q << 8 | i;
/* 1722 */           i = input[(this._inputPtr++)] & 0xFF;
/* 1723 */           if (codes[i] == 0) {
/* 1724 */             q = q << 8 | i;
/* 1725 */             i = input[(this._inputPtr++)] & 0xFF;
/* 1726 */             if (codes[i] == 0) {
/* 1727 */               this._quad1 = q;
/* 1728 */               return parseMediumName(i);
/*      */             }
/* 1730 */             if (i == 34) {
/* 1731 */               return findName(q, 4);
/*      */             }
/* 1733 */             return parseName(q, i, 4);
/*      */           }
/* 1735 */           if (i == 34) {
/* 1736 */             return findName(q, 3);
/*      */           }
/* 1738 */           return parseName(q, i, 3);
/*      */         }
/* 1740 */         if (i == 34) {
/* 1741 */           return findName(q, 2);
/*      */         }
/* 1743 */         return parseName(q, i, 2);
/*      */       }
/* 1745 */       if (i == 34) {
/* 1746 */         return findName(q, 1);
/*      */       }
/* 1748 */       return parseName(q, i, 1);
/*      */     }
/* 1750 */     if (q == 34) {
/* 1751 */       return "";
/*      */     }
/* 1753 */     return parseName(0, q, 0);
/*      */   }
/*      */   
/*      */   protected final String parseMediumName(int q2) throws IOException
/*      */   {
/* 1758 */     byte[] input = this._inputBuffer;
/* 1759 */     int[] codes = _icLatin1;
/*      */     
/*      */ 
/* 1762 */     int i = input[(this._inputPtr++)] & 0xFF;
/* 1763 */     if (codes[i] != 0) {
/* 1764 */       if (i == 34) {
/* 1765 */         return findName(this._quad1, q2, 1);
/*      */       }
/* 1767 */       return parseName(this._quad1, q2, i, 1);
/*      */     }
/* 1769 */     q2 = q2 << 8 | i;
/* 1770 */     i = input[(this._inputPtr++)] & 0xFF;
/* 1771 */     if (codes[i] != 0) {
/* 1772 */       if (i == 34) {
/* 1773 */         return findName(this._quad1, q2, 2);
/*      */       }
/* 1775 */       return parseName(this._quad1, q2, i, 2);
/*      */     }
/* 1777 */     q2 = q2 << 8 | i;
/* 1778 */     i = input[(this._inputPtr++)] & 0xFF;
/* 1779 */     if (codes[i] != 0) {
/* 1780 */       if (i == 34) {
/* 1781 */         return findName(this._quad1, q2, 3);
/*      */       }
/* 1783 */       return parseName(this._quad1, q2, i, 3);
/*      */     }
/* 1785 */     q2 = q2 << 8 | i;
/* 1786 */     i = input[(this._inputPtr++)] & 0xFF;
/* 1787 */     if (codes[i] != 0) {
/* 1788 */       if (i == 34) {
/* 1789 */         return findName(this._quad1, q2, 4);
/*      */       }
/* 1791 */       return parseName(this._quad1, q2, i, 4);
/*      */     }
/* 1793 */     return parseMediumName2(i, q2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected final String parseMediumName2(int q3, int q2)
/*      */     throws IOException
/*      */   {
/* 1801 */     byte[] input = this._inputBuffer;
/* 1802 */     int[] codes = _icLatin1;
/*      */     
/*      */ 
/* 1805 */     int i = input[(this._inputPtr++)] & 0xFF;
/* 1806 */     if (codes[i] != 0) {
/* 1807 */       if (i == 34) {
/* 1808 */         return findName(this._quad1, q2, q3, 1);
/*      */       }
/* 1810 */       return parseName(this._quad1, q2, q3, i, 1);
/*      */     }
/* 1812 */     q3 = q3 << 8 | i;
/* 1813 */     i = input[(this._inputPtr++)] & 0xFF;
/* 1814 */     if (codes[i] != 0) {
/* 1815 */       if (i == 34) {
/* 1816 */         return findName(this._quad1, q2, q3, 2);
/*      */       }
/* 1818 */       return parseName(this._quad1, q2, q3, i, 2);
/*      */     }
/* 1820 */     q3 = q3 << 8 | i;
/* 1821 */     i = input[(this._inputPtr++)] & 0xFF;
/* 1822 */     if (codes[i] != 0) {
/* 1823 */       if (i == 34) {
/* 1824 */         return findName(this._quad1, q2, q3, 3);
/*      */       }
/* 1826 */       return parseName(this._quad1, q2, q3, i, 3);
/*      */     }
/* 1828 */     q3 = q3 << 8 | i;
/* 1829 */     i = input[(this._inputPtr++)] & 0xFF;
/* 1830 */     if (codes[i] != 0) {
/* 1831 */       if (i == 34) {
/* 1832 */         return findName(this._quad1, q2, q3, 4);
/*      */       }
/* 1834 */       return parseName(this._quad1, q2, q3, i, 4);
/*      */     }
/* 1836 */     return parseLongName(i, q2, q3);
/*      */   }
/*      */   
/*      */   protected final String parseLongName(int q, int q2, int q3) throws IOException
/*      */   {
/* 1841 */     this._quadBuffer[0] = this._quad1;
/* 1842 */     this._quadBuffer[1] = q2;
/* 1843 */     this._quadBuffer[2] = q3;
/*      */     
/*      */ 
/* 1846 */     byte[] input = this._inputBuffer;
/* 1847 */     int[] codes = _icLatin1;
/* 1848 */     int qlen = 3;
/*      */     
/* 1850 */     while (this._inputPtr + 4 <= this._inputEnd) {
/* 1851 */       int i = input[(this._inputPtr++)] & 0xFF;
/* 1852 */       if (codes[i] != 0) {
/* 1853 */         if (i == 34) {
/* 1854 */           return findName(this._quadBuffer, qlen, q, 1);
/*      */         }
/* 1856 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 1);
/*      */       }
/*      */       
/* 1859 */       q = q << 8 | i;
/* 1860 */       i = input[(this._inputPtr++)] & 0xFF;
/* 1861 */       if (codes[i] != 0) {
/* 1862 */         if (i == 34) {
/* 1863 */           return findName(this._quadBuffer, qlen, q, 2);
/*      */         }
/* 1865 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 2);
/*      */       }
/*      */       
/* 1868 */       q = q << 8 | i;
/* 1869 */       i = input[(this._inputPtr++)] & 0xFF;
/* 1870 */       if (codes[i] != 0) {
/* 1871 */         if (i == 34) {
/* 1872 */           return findName(this._quadBuffer, qlen, q, 3);
/*      */         }
/* 1874 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 3);
/*      */       }
/*      */       
/* 1877 */       q = q << 8 | i;
/* 1878 */       i = input[(this._inputPtr++)] & 0xFF;
/* 1879 */       if (codes[i] != 0) {
/* 1880 */         if (i == 34) {
/* 1881 */           return findName(this._quadBuffer, qlen, q, 4);
/*      */         }
/* 1883 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 4);
/*      */       }
/*      */       
/*      */ 
/* 1887 */       if (qlen >= this._quadBuffer.length) {
/* 1888 */         this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
/*      */       }
/* 1890 */       this._quadBuffer[(qlen++)] = q;
/* 1891 */       q = i;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1898 */     return parseEscapedName(this._quadBuffer, qlen, 0, q, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String slowParseName()
/*      */     throws IOException
/*      */   {
/* 1908 */     if ((this._inputPtr >= this._inputEnd) && 
/* 1909 */       (!_loadMore())) {
/* 1910 */       _reportInvalidEOF(": was expecting closing '\"' for name", JsonToken.FIELD_NAME);
/*      */     }
/*      */     
/* 1913 */     int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1914 */     if (i == 34) {
/* 1915 */       return "";
/*      */     }
/* 1917 */     return parseEscapedName(this._quadBuffer, 0, 0, i, 0);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
/* 1921 */     return parseEscapedName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
/* 1925 */     this._quadBuffer[0] = q1;
/* 1926 */     return parseEscapedName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
/* 1930 */     this._quadBuffer[0] = q1;
/* 1931 */     this._quadBuffer[1] = q2;
/* 1932 */     return parseEscapedName(this._quadBuffer, 2, q3, ch, lastQuadBytes);
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
/*      */   protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes)
/*      */     throws IOException
/*      */   {
/* 1949 */     int[] codes = _icLatin1;
/*      */     for (;;)
/*      */     {
/* 1952 */       if (codes[ch] != 0) {
/* 1953 */         if (ch == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 1957 */         if (ch != 92)
/*      */         {
/* 1959 */           _throwUnquotedSpace(ch, "name");
/*      */         }
/*      */         else {
/* 1962 */           ch = _decodeEscaped();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1969 */         if (ch > 127)
/*      */         {
/* 1971 */           if (currQuadBytes >= 4) {
/* 1972 */             if (qlen >= quads.length) {
/* 1973 */               this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */             }
/* 1975 */             quads[(qlen++)] = currQuad;
/* 1976 */             currQuad = 0;
/* 1977 */             currQuadBytes = 0;
/*      */           }
/* 1979 */           if (ch < 2048) {
/* 1980 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1981 */             currQuadBytes++;
/*      */           }
/*      */           else {
/* 1984 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1985 */             currQuadBytes++;
/*      */             
/* 1987 */             if (currQuadBytes >= 4) {
/* 1988 */               if (qlen >= quads.length) {
/* 1989 */                 this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */               }
/* 1991 */               quads[(qlen++)] = currQuad;
/* 1992 */               currQuad = 0;
/* 1993 */               currQuadBytes = 0;
/*      */             }
/* 1995 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1996 */             currQuadBytes++;
/*      */           }
/*      */           
/* 1999 */           ch = 0x80 | ch & 0x3F;
/*      */         }
/*      */       }
/*      */       
/* 2003 */       if (currQuadBytes < 4) {
/* 2004 */         currQuadBytes++;
/* 2005 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2007 */         if (qlen >= quads.length) {
/* 2008 */           this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */         }
/* 2010 */         quads[(qlen++)] = currQuad;
/* 2011 */         currQuad = ch;
/* 2012 */         currQuadBytes = 1;
/*      */       }
/* 2014 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2015 */         (!_loadMore())) {
/* 2016 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2019 */       ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */     }
/*      */     
/* 2022 */     if (currQuadBytes > 0) {
/* 2023 */       if (qlen >= quads.length) {
/* 2024 */         this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */       }
/* 2026 */       quads[(qlen++)] = pad(currQuad, currQuadBytes);
/*      */     }
/* 2028 */     String name = this._symbols.findName(quads, qlen);
/* 2029 */     if (name == null) {
/* 2030 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2032 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String _handleOddName(int ch)
/*      */     throws IOException
/*      */   {
/* 2044 */     if ((ch == 39) && (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES))) {
/* 2045 */       return _parseAposName();
/*      */     }
/*      */     
/* 2048 */     if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
/* 2049 */       char c = (char)_decodeCharForError(ch);
/* 2050 */       _reportUnexpectedChar(c, "was expecting double-quote to start field name");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2056 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 2058 */     if (codes[ch] != 0) {
/* 2059 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2066 */     int[] quads = this._quadBuffer;
/* 2067 */     int qlen = 0;
/* 2068 */     int currQuad = 0;
/* 2069 */     int currQuadBytes = 0;
/*      */     
/*      */     for (;;)
/*      */     {
/* 2073 */       if (currQuadBytes < 4) {
/* 2074 */         currQuadBytes++;
/* 2075 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2077 */         if (qlen >= quads.length) {
/* 2078 */           this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */         }
/* 2080 */         quads[(qlen++)] = currQuad;
/* 2081 */         currQuad = ch;
/* 2082 */         currQuadBytes = 1;
/*      */       }
/* 2084 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2085 */         (!_loadMore())) {
/* 2086 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2089 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2090 */       if (codes[ch] != 0) {
/*      */         break;
/*      */       }
/* 2093 */       this._inputPtr += 1;
/*      */     }
/*      */     
/* 2096 */     if (currQuadBytes > 0) {
/* 2097 */       if (qlen >= quads.length) {
/* 2098 */         this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */       }
/* 2100 */       quads[(qlen++)] = currQuad;
/*      */     }
/* 2102 */     String name = this._symbols.findName(quads, qlen);
/* 2103 */     if (name == null) {
/* 2104 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2106 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String _parseAposName()
/*      */     throws IOException
/*      */   {
/* 2116 */     if ((this._inputPtr >= this._inputEnd) && 
/* 2117 */       (!_loadMore())) {
/* 2118 */       _reportInvalidEOF(": was expecting closing ''' for field name", JsonToken.FIELD_NAME);
/*      */     }
/*      */     
/* 2121 */     int ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2122 */     if (ch == 39) {
/* 2123 */       return "";
/*      */     }
/* 2125 */     int[] quads = this._quadBuffer;
/* 2126 */     int qlen = 0;
/* 2127 */     int currQuad = 0;
/* 2128 */     int currQuadBytes = 0;
/*      */     
/*      */ 
/*      */ 
/* 2132 */     int[] codes = _icLatin1;
/*      */     
/*      */ 
/* 2135 */     while (ch != 39)
/*      */     {
/*      */ 
/*      */ 
/* 2139 */       if ((ch != 34) && (codes[ch] != 0)) {
/* 2140 */         if (ch != 92)
/*      */         {
/*      */ 
/* 2143 */           _throwUnquotedSpace(ch, "name");
/*      */         }
/*      */         else {
/* 2146 */           ch = _decodeEscaped();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2153 */         if (ch > 127)
/*      */         {
/* 2155 */           if (currQuadBytes >= 4) {
/* 2156 */             if (qlen >= quads.length) {
/* 2157 */               this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */             }
/* 2159 */             quads[(qlen++)] = currQuad;
/* 2160 */             currQuad = 0;
/* 2161 */             currQuadBytes = 0;
/*      */           }
/* 2163 */           if (ch < 2048) {
/* 2164 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2165 */             currQuadBytes++;
/*      */           }
/*      */           else {
/* 2168 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2169 */             currQuadBytes++;
/*      */             
/* 2171 */             if (currQuadBytes >= 4) {
/* 2172 */               if (qlen >= quads.length) {
/* 2173 */                 this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */               }
/* 2175 */               quads[(qlen++)] = currQuad;
/* 2176 */               currQuad = 0;
/* 2177 */               currQuadBytes = 0;
/*      */             }
/* 2179 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2180 */             currQuadBytes++;
/*      */           }
/*      */           
/* 2183 */           ch = 0x80 | ch & 0x3F;
/*      */         }
/*      */       }
/*      */       
/* 2187 */       if (currQuadBytes < 4) {
/* 2188 */         currQuadBytes++;
/* 2189 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2191 */         if (qlen >= quads.length) {
/* 2192 */           this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */         }
/* 2194 */         quads[(qlen++)] = currQuad;
/* 2195 */         currQuad = ch;
/* 2196 */         currQuadBytes = 1;
/*      */       }
/* 2198 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2199 */         (!_loadMore())) {
/* 2200 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2203 */       ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */     }
/*      */     
/* 2206 */     if (currQuadBytes > 0) {
/* 2207 */       if (qlen >= quads.length) {
/* 2208 */         this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */       }
/* 2210 */       quads[(qlen++)] = pad(currQuad, currQuadBytes);
/*      */     }
/* 2212 */     String name = this._symbols.findName(quads, qlen);
/* 2213 */     if (name == null) {
/* 2214 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2216 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final String findName(int q1, int lastQuadBytes)
/*      */     throws JsonParseException
/*      */   {
/* 2227 */     q1 = pad(q1, lastQuadBytes);
/*      */     
/* 2229 */     String name = this._symbols.findName(q1);
/* 2230 */     if (name != null) {
/* 2231 */       return name;
/*      */     }
/*      */     
/* 2234 */     this._quadBuffer[0] = q1;
/* 2235 */     return addName(this._quadBuffer, 1, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException
/*      */   {
/* 2240 */     q2 = pad(q2, lastQuadBytes);
/*      */     
/* 2242 */     String name = this._symbols.findName(q1, q2);
/* 2243 */     if (name != null) {
/* 2244 */       return name;
/*      */     }
/*      */     
/* 2247 */     this._quadBuffer[0] = q1;
/* 2248 */     this._quadBuffer[1] = q2;
/* 2249 */     return addName(this._quadBuffer, 2, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException
/*      */   {
/* 2254 */     q3 = pad(q3, lastQuadBytes);
/* 2255 */     String name = this._symbols.findName(q1, q2, q3);
/* 2256 */     if (name != null) {
/* 2257 */       return name;
/*      */     }
/* 2259 */     int[] quads = this._quadBuffer;
/* 2260 */     quads[0] = q1;
/* 2261 */     quads[1] = q2;
/* 2262 */     quads[2] = pad(q3, lastQuadBytes);
/* 2263 */     return addName(quads, 3, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException
/*      */   {
/* 2268 */     if (qlen >= quads.length) {
/* 2269 */       this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */     }
/* 2271 */     quads[(qlen++)] = pad(lastQuad, lastQuadBytes);
/* 2272 */     String name = this._symbols.findName(quads, qlen);
/* 2273 */     if (name == null) {
/* 2274 */       return addName(quads, qlen, lastQuadBytes);
/*      */     }
/* 2276 */     return name;
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
/*      */   private final String addName(int[] quads, int qlen, int lastQuadBytes)
/*      */     throws JsonParseException
/*      */   {
/* 2292 */     int byteLen = (qlen << 2) - 4 + lastQuadBytes;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     int lastQuad;
/*      */     
/*      */ 
/*      */ 
/* 2301 */     if (lastQuadBytes < 4) {
/* 2302 */       int lastQuad = quads[(qlen - 1)];
/*      */       
/* 2304 */       quads[(qlen - 1)] = (lastQuad << (4 - lastQuadBytes << 3));
/*      */     } else {
/* 2306 */       lastQuad = 0;
/*      */     }
/*      */     
/*      */ 
/* 2310 */     char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2311 */     int cix = 0;
/*      */     
/* 2313 */     for (int ix = 0; ix < byteLen;) {
/* 2314 */       int ch = quads[(ix >> 2)];
/* 2315 */       int byteIx = ix & 0x3;
/* 2316 */       ch = ch >> (3 - byteIx << 3) & 0xFF;
/* 2317 */       ix++;
/*      */       
/* 2319 */       if (ch > 127) { int needed;
/*      */         int needed;
/* 2321 */         if ((ch & 0xE0) == 192) {
/* 2322 */           ch &= 0x1F;
/* 2323 */           needed = 1; } else { int needed;
/* 2324 */           if ((ch & 0xF0) == 224) {
/* 2325 */             ch &= 0xF;
/* 2326 */             needed = 2; } else { int needed;
/* 2327 */             if ((ch & 0xF8) == 240) {
/* 2328 */               ch &= 0x7;
/* 2329 */               needed = 3;
/*      */             } else {
/* 2331 */               _reportInvalidInitial(ch);
/* 2332 */               needed = ch = 1;
/*      */             } } }
/* 2334 */         if (ix + needed > byteLen) {
/* 2335 */           _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */         }
/*      */         
/*      */ 
/* 2339 */         int ch2 = quads[(ix >> 2)];
/* 2340 */         byteIx = ix & 0x3;
/* 2341 */         ch2 >>= 3 - byteIx << 3;
/* 2342 */         ix++;
/*      */         
/* 2344 */         if ((ch2 & 0xC0) != 128) {
/* 2345 */           _reportInvalidOther(ch2);
/*      */         }
/* 2347 */         ch = ch << 6 | ch2 & 0x3F;
/* 2348 */         if (needed > 1) {
/* 2349 */           ch2 = quads[(ix >> 2)];
/* 2350 */           byteIx = ix & 0x3;
/* 2351 */           ch2 >>= 3 - byteIx << 3;
/* 2352 */           ix++;
/*      */           
/* 2354 */           if ((ch2 & 0xC0) != 128) {
/* 2355 */             _reportInvalidOther(ch2);
/*      */           }
/* 2357 */           ch = ch << 6 | ch2 & 0x3F;
/* 2358 */           if (needed > 2) {
/* 2359 */             ch2 = quads[(ix >> 2)];
/* 2360 */             byteIx = ix & 0x3;
/* 2361 */             ch2 >>= 3 - byteIx << 3;
/* 2362 */             ix++;
/* 2363 */             if ((ch2 & 0xC0) != 128) {
/* 2364 */               _reportInvalidOther(ch2 & 0xFF);
/*      */             }
/* 2366 */             ch = ch << 6 | ch2 & 0x3F;
/*      */           }
/*      */         }
/* 2369 */         if (needed > 2) {
/* 2370 */           ch -= 65536;
/* 2371 */           if (cix >= cbuf.length) {
/* 2372 */             cbuf = this._textBuffer.expandCurrentSegment();
/*      */           }
/* 2374 */           cbuf[(cix++)] = ((char)(55296 + (ch >> 10)));
/* 2375 */           ch = 0xDC00 | ch & 0x3FF;
/*      */         }
/*      */       }
/* 2378 */       if (cix >= cbuf.length) {
/* 2379 */         cbuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 2381 */       cbuf[(cix++)] = ((char)ch);
/*      */     }
/*      */     
/*      */ 
/* 2385 */     String baseName = new String(cbuf, 0, cix);
/*      */     
/* 2387 */     if (lastQuadBytes < 4) {
/* 2388 */       quads[(qlen - 1)] = lastQuad;
/*      */     }
/* 2390 */     return this._symbols.addName(baseName, quads, qlen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _loadMoreGuaranteed()
/*      */     throws IOException
/*      */   {
/* 2400 */     if (!_loadMore()) { _reportInvalidEOF();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _finishString()
/*      */     throws IOException
/*      */   {
/* 2407 */     int ptr = this._inputPtr;
/* 2408 */     if (ptr >= this._inputEnd) {
/* 2409 */       _loadMoreGuaranteed();
/* 2410 */       ptr = this._inputPtr;
/*      */     }
/* 2412 */     int outPtr = 0;
/* 2413 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2414 */     int[] codes = _icUTF8;
/*      */     
/* 2416 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2417 */     byte[] inputBuffer = this._inputBuffer;
/* 2418 */     while (ptr < max) {
/* 2419 */       int c = inputBuffer[ptr] & 0xFF;
/* 2420 */       if (codes[c] != 0) {
/* 2421 */         if (c != 34) break;
/* 2422 */         this._inputPtr = (ptr + 1);
/* 2423 */         this._textBuffer.setCurrentLength(outPtr);
/* 2424 */         return;
/*      */       }
/*      */       
/*      */ 
/* 2428 */       ptr++;
/* 2429 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 2431 */     this._inputPtr = ptr;
/* 2432 */     _finishString2(outBuf, outPtr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String _finishAndReturnString()
/*      */     throws IOException
/*      */   {
/* 2441 */     int ptr = this._inputPtr;
/* 2442 */     if (ptr >= this._inputEnd) {
/* 2443 */       _loadMoreGuaranteed();
/* 2444 */       ptr = this._inputPtr;
/*      */     }
/* 2446 */     int outPtr = 0;
/* 2447 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2448 */     int[] codes = _icUTF8;
/*      */     
/* 2450 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2451 */     byte[] inputBuffer = this._inputBuffer;
/* 2452 */     while (ptr < max) {
/* 2453 */       int c = inputBuffer[ptr] & 0xFF;
/* 2454 */       if (codes[c] != 0) {
/* 2455 */         if (c != 34) break;
/* 2456 */         this._inputPtr = (ptr + 1);
/* 2457 */         return this._textBuffer.setCurrentAndReturn(outPtr);
/*      */       }
/*      */       
/*      */ 
/* 2461 */       ptr++;
/* 2462 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 2464 */     this._inputPtr = ptr;
/* 2465 */     _finishString2(outBuf, outPtr);
/* 2466 */     return this._textBuffer.contentsAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _finishString2(char[] outBuf, int outPtr)
/*      */     throws IOException
/*      */   {
/* 2475 */     int[] codes = _icUTF8;
/* 2476 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 2483 */       int ptr = this._inputPtr;
/* 2484 */       if (ptr >= this._inputEnd) {
/* 2485 */         _loadMoreGuaranteed();
/* 2486 */         ptr = this._inputPtr;
/*      */       }
/* 2488 */       if (outPtr >= outBuf.length) {
/* 2489 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2490 */         outPtr = 0;
/*      */       }
/* 2492 */       int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr));
/* 2493 */       while (ptr < max) {
/* 2494 */         int c = inputBuffer[(ptr++)] & 0xFF;
/* 2495 */         if (codes[c] != 0) {
/* 2496 */           this._inputPtr = ptr;
/*      */           break label125;
/*      */         }
/* 2499 */         outBuf[(outPtr++)] = ((char)c);
/*      */       }
/* 2501 */       this._inputPtr = ptr;
/* 2502 */       continue;
/*      */       label125:
/* 2504 */       int c; if (c == 34) {
/*      */         break;
/*      */       }
/*      */       
/* 2508 */       switch (codes[c]) {
/*      */       case 1: 
/* 2510 */         c = _decodeEscaped();
/* 2511 */         break;
/*      */       case 2: 
/* 2513 */         c = _decodeUtf8_2(c);
/* 2514 */         break;
/*      */       case 3: 
/* 2516 */         if (this._inputEnd - this._inputPtr >= 2) {
/* 2517 */           c = _decodeUtf8_3fast(c);
/*      */         } else {
/* 2519 */           c = _decodeUtf8_3(c);
/*      */         }
/* 2521 */         break;
/*      */       case 4: 
/* 2523 */         c = _decodeUtf8_4(c);
/*      */         
/* 2525 */         outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
/* 2526 */         if (outPtr >= outBuf.length) {
/* 2527 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 2528 */           outPtr = 0;
/*      */         }
/* 2530 */         c = 0xDC00 | c & 0x3FF;
/*      */         
/* 2532 */         break;
/*      */       default: 
/* 2534 */         if (c < 32)
/*      */         {
/* 2536 */           _throwUnquotedSpace(c, "string value");
/*      */         }
/*      */         else {
/* 2539 */           _reportInvalidChar(c);
/*      */         }
/*      */         break;
/*      */       }
/* 2543 */       if (outPtr >= outBuf.length) {
/* 2544 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2545 */         outPtr = 0;
/*      */       }
/*      */       
/* 2548 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 2550 */     this._textBuffer.setCurrentLength(outPtr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _skipString()
/*      */     throws IOException
/*      */   {
/* 2560 */     this._tokenIncomplete = false;
/*      */     
/*      */ 
/* 2563 */     int[] codes = _icUTF8;
/* 2564 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 2572 */       int ptr = this._inputPtr;
/* 2573 */       int max = this._inputEnd;
/* 2574 */       if (ptr >= max) {
/* 2575 */         _loadMoreGuaranteed();
/* 2576 */         ptr = this._inputPtr;
/* 2577 */         max = this._inputEnd;
/*      */       }
/* 2579 */       while (ptr < max) {
/* 2580 */         int c = inputBuffer[(ptr++)] & 0xFF;
/* 2581 */         if (codes[c] != 0) {
/* 2582 */           this._inputPtr = ptr;
/*      */           break label87;
/*      */         }
/*      */       }
/* 2586 */       this._inputPtr = ptr;
/* 2587 */       continue;
/*      */       label87:
/* 2589 */       int c; if (c == 34) {
/*      */         break;
/*      */       }
/*      */       
/* 2593 */       switch (codes[c]) {
/*      */       case 1: 
/* 2595 */         _decodeEscaped();
/* 2596 */         break;
/*      */       case 2: 
/* 2598 */         _skipUtf8_2(c);
/* 2599 */         break;
/*      */       case 3: 
/* 2601 */         _skipUtf8_3(c);
/* 2602 */         break;
/*      */       case 4: 
/* 2604 */         _skipUtf8_4(c);
/* 2605 */         break;
/*      */       default: 
/* 2607 */         if (c < 32)
/*      */         {
/* 2609 */           _throwUnquotedSpace(c, "string value");
/*      */         }
/*      */         else {
/* 2612 */           _reportInvalidChar(c);
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected JsonToken _handleUnexpectedValue(int c)
/*      */     throws IOException
/*      */   {
/* 2626 */     switch (c)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 93: 
/* 2636 */       if (!this._parsingContext.inArray()) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       break;
/*      */     case 44: 
/* 2645 */       if (isEnabled(JsonParser.Feature.ALLOW_MISSING_VALUES)) {
/* 2646 */         this._inputPtr -= 1;
/* 2647 */         return JsonToken.VALUE_NULL;
/*      */       }
/*      */     
/*      */ 
/*      */ 
/*      */     case 125: 
/* 2653 */       _reportUnexpectedChar(c, "expected a value");
/*      */     case 39: 
/* 2655 */       if (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/* 2656 */         return _handleApos();
/*      */       }
/*      */       break;
/*      */     case 78: 
/* 2660 */       _matchToken("NaN", 1);
/* 2661 */       if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 2662 */         return resetAsNaN("NaN", NaN.0D);
/*      */       }
/* 2664 */       _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 2665 */       break;
/*      */     case 73: 
/* 2667 */       _matchToken("Infinity", 1);
/* 2668 */       if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 2669 */         return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */       }
/* 2671 */       _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 2672 */       break;
/*      */     case 43: 
/* 2674 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2675 */         (!_loadMore())) {
/* 2676 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */       }
/*      */       
/* 2679 */       return _handleInvalidNumberStart(this._inputBuffer[(this._inputPtr++)] & 0xFF, false);
/*      */     }
/*      */     
/* 2682 */     if (Character.isJavaIdentifierStart(c)) {
/* 2683 */       _reportInvalidToken("" + (char)c, "('true', 'false' or 'null')");
/*      */     }
/*      */     
/* 2686 */     _reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
/* 2687 */     return null;
/*      */   }
/*      */   
/*      */   protected JsonToken _handleApos()
/*      */     throws IOException
/*      */   {
/* 2693 */     int c = 0;
/*      */     
/* 2695 */     int outPtr = 0;
/* 2696 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */     
/*      */ 
/* 2699 */     int[] codes = _icUTF8;
/* 2700 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 2707 */       if (this._inputPtr >= this._inputEnd) {
/* 2708 */         _loadMoreGuaranteed();
/*      */       }
/* 2710 */       if (outPtr >= outBuf.length) {
/* 2711 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2712 */         outPtr = 0;
/*      */       }
/* 2714 */       int max = this._inputEnd;
/*      */       
/* 2716 */       int max2 = this._inputPtr + (outBuf.length - outPtr);
/* 2717 */       if (max2 < max) {
/* 2718 */         max = max2;
/*      */       }
/*      */       
/* 2721 */       while (this._inputPtr < max) {
/* 2722 */         c = inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2723 */         if ((c == 39) || (codes[c] != 0)) {
/*      */           break label140;
/*      */         }
/* 2726 */         outBuf[(outPtr++)] = ((char)c);
/*      */       }
/* 2728 */       continue;
/*      */       
/*      */       label140:
/* 2731 */       if (c == 39) {
/*      */         break;
/*      */       }
/*      */       
/* 2735 */       switch (codes[c]) {
/*      */       case 1: 
/* 2737 */         if (c != 39) {
/* 2738 */           c = _decodeEscaped();
/*      */         }
/*      */         break;
/*      */       case 2: 
/* 2742 */         c = _decodeUtf8_2(c);
/* 2743 */         break;
/*      */       case 3: 
/* 2745 */         if (this._inputEnd - this._inputPtr >= 2) {
/* 2746 */           c = _decodeUtf8_3fast(c);
/*      */         } else {
/* 2748 */           c = _decodeUtf8_3(c);
/*      */         }
/* 2750 */         break;
/*      */       case 4: 
/* 2752 */         c = _decodeUtf8_4(c);
/*      */         
/* 2754 */         outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
/* 2755 */         if (outPtr >= outBuf.length) {
/* 2756 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 2757 */           outPtr = 0;
/*      */         }
/* 2759 */         c = 0xDC00 | c & 0x3FF;
/*      */         
/* 2761 */         break;
/*      */       default: 
/* 2763 */         if (c < 32) {
/* 2764 */           _throwUnquotedSpace(c, "string value");
/*      */         }
/*      */         
/* 2767 */         _reportInvalidChar(c);
/*      */       }
/*      */       
/* 2770 */       if (outPtr >= outBuf.length) {
/* 2771 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2772 */         outPtr = 0;
/*      */       }
/*      */       
/* 2775 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 2777 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 2779 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean neg)
/*      */     throws IOException
/*      */   {
/* 2789 */     while (ch == 73) {
/* 2790 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2791 */         (!_loadMore())) {
/* 2792 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_FLOAT);
/*      */       }
/*      */       
/* 2795 */       ch = this._inputBuffer[(this._inputPtr++)];
/*      */       String match;
/* 2797 */       String match; if (ch == 78) {
/* 2798 */         match = neg ? "-INF" : "+INF";
/* 2799 */       } else { if (ch != 110) break;
/* 2800 */         match = neg ? "-Infinity" : "+Infinity";
/*      */       }
/*      */       
/*      */ 
/* 2804 */       _matchToken(match, 3);
/* 2805 */       if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 2806 */         return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */       }
/* 2808 */       _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */     }
/* 2810 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 2811 */     return null;
/*      */   }
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i) throws IOException
/*      */   {
/* 2816 */     int len = matchStr.length();
/* 2817 */     if (this._inputPtr + len >= this._inputEnd) {
/* 2818 */       _matchToken2(matchStr, i);
/*      */     }
/*      */     else {
/*      */       do {
/* 2822 */         if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 2823 */           _reportInvalidToken(matchStr.substring(0, i));
/*      */         }
/* 2825 */         this._inputPtr += 1;
/* 2826 */         i++; } while (i < len);
/*      */       
/* 2828 */       int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2829 */       if ((ch >= 48) && (ch != 93) && (ch != 125)) {
/* 2830 */         _checkMatchEnd(matchStr, i, ch);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _matchToken2(String matchStr, int i) throws IOException {
/* 2836 */     int len = matchStr.length();
/*      */     do {
/* 2838 */       if (((this._inputPtr >= this._inputEnd) && (!_loadMore())) || (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)))
/*      */       {
/* 2840 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2842 */       this._inputPtr += 1;
/* 2843 */       i++; } while (i < len);
/*      */     
/*      */ 
/* 2846 */     if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 2847 */       return;
/*      */     }
/* 2849 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2850 */     if ((ch >= 48) && (ch != 93) && (ch != 125)) {
/* 2851 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException
/*      */   {
/* 2857 */     char c = (char)_decodeCharForError(ch);
/* 2858 */     if (Character.isJavaIdentifierPart(c)) {
/* 2859 */       _reportInvalidToken(matchStr.substring(0, i));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _skipWS()
/*      */     throws IOException
/*      */   {
/* 2871 */     while (this._inputPtr < this._inputEnd) {
/* 2872 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2873 */       if (i > 32) {
/* 2874 */         if ((i == 47) || (i == 35)) {
/* 2875 */           this._inputPtr -= 1;
/* 2876 */           return _skipWS2();
/*      */         }
/* 2878 */         return i;
/*      */       }
/* 2880 */       if (i != 32) {
/* 2881 */         if (i == 10) {
/* 2882 */           this._currInputRow += 1;
/* 2883 */           this._currInputRowStart = this._inputPtr;
/* 2884 */         } else if (i == 13) {
/* 2885 */           _skipCR();
/* 2886 */         } else if (i != 9) {
/* 2887 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2891 */     return _skipWS2();
/*      */   }
/*      */   
/*      */   private final int _skipWS2() throws IOException
/*      */   {
/* 2896 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 2897 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2898 */       if (i > 32) {
/* 2899 */         if (i == 47) {
/* 2900 */           _skipComment();
/*      */ 
/*      */         }
/* 2903 */         else if ((i != 35) || 
/* 2904 */           (!_skipYAMLComment()))
/*      */         {
/*      */ 
/*      */ 
/* 2908 */           return i;
/*      */         }
/* 2910 */       } else if (i != 32) {
/* 2911 */         if (i == 10) {
/* 2912 */           this._currInputRow += 1;
/* 2913 */           this._currInputRowStart = this._inputPtr;
/* 2914 */         } else if (i == 13) {
/* 2915 */           _skipCR();
/* 2916 */         } else if (i != 9) {
/* 2917 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2921 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
/*      */   }
/*      */   
/*      */ 
/*      */   private final int _skipWSOrEnd()
/*      */     throws IOException
/*      */   {
/* 2928 */     if ((this._inputPtr >= this._inputEnd) && 
/* 2929 */       (!_loadMore())) {
/* 2930 */       return _eofAsNextChar();
/*      */     }
/*      */     
/* 2933 */     int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2934 */     if (i > 32) {
/* 2935 */       if ((i == 47) || (i == 35)) {
/* 2936 */         this._inputPtr -= 1;
/* 2937 */         return _skipWSOrEnd2();
/*      */       }
/* 2939 */       return i;
/*      */     }
/* 2941 */     if (i != 32) {
/* 2942 */       if (i == 10) {
/* 2943 */         this._currInputRow += 1;
/* 2944 */         this._currInputRowStart = this._inputPtr;
/* 2945 */       } else if (i == 13) {
/* 2946 */         _skipCR();
/* 2947 */       } else if (i != 9) {
/* 2948 */         _throwInvalidSpace(i);
/*      */       }
/*      */     }
/*      */     
/* 2952 */     while (this._inputPtr < this._inputEnd) {
/* 2953 */       i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2954 */       if (i > 32) {
/* 2955 */         if ((i == 47) || (i == 35)) {
/* 2956 */           this._inputPtr -= 1;
/* 2957 */           return _skipWSOrEnd2();
/*      */         }
/* 2959 */         return i;
/*      */       }
/* 2961 */       if (i != 32) {
/* 2962 */         if (i == 10) {
/* 2963 */           this._currInputRow += 1;
/* 2964 */           this._currInputRowStart = this._inputPtr;
/* 2965 */         } else if (i == 13) {
/* 2966 */           _skipCR();
/* 2967 */         } else if (i != 9) {
/* 2968 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2972 */     return _skipWSOrEnd2();
/*      */   }
/*      */   
/*      */   private final int _skipWSOrEnd2() throws IOException
/*      */   {
/* 2977 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 2978 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2979 */       if (i > 32) {
/* 2980 */         if (i == 47) {
/* 2981 */           _skipComment();
/*      */ 
/*      */         }
/* 2984 */         else if ((i != 35) || 
/* 2985 */           (!_skipYAMLComment()))
/*      */         {
/*      */ 
/*      */ 
/* 2989 */           return i; }
/* 2990 */       } else if (i != 32) {
/* 2991 */         if (i == 10) {
/* 2992 */           this._currInputRow += 1;
/* 2993 */           this._currInputRowStart = this._inputPtr;
/* 2994 */         } else if (i == 13) {
/* 2995 */           _skipCR();
/* 2996 */         } else if (i != 9) {
/* 2997 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3002 */     return _eofAsNextChar();
/*      */   }
/*      */   
/*      */   private final int _skipColon() throws IOException
/*      */   {
/* 3007 */     if (this._inputPtr + 4 >= this._inputEnd) {
/* 3008 */       return _skipColon2(false);
/*      */     }
/*      */     
/* 3011 */     int i = this._inputBuffer[this._inputPtr];
/* 3012 */     if (i == 58) {
/* 3013 */       i = this._inputBuffer[(++this._inputPtr)];
/* 3014 */       if (i > 32) {
/* 3015 */         if ((i == 47) || (i == 35)) {
/* 3016 */           return _skipColon2(true);
/*      */         }
/* 3018 */         this._inputPtr += 1;
/* 3019 */         return i;
/*      */       }
/* 3021 */       if ((i == 32) || (i == 9)) {
/* 3022 */         i = this._inputBuffer[(++this._inputPtr)];
/* 3023 */         if (i > 32) {
/* 3024 */           if ((i == 47) || (i == 35)) {
/* 3025 */             return _skipColon2(true);
/*      */           }
/* 3027 */           this._inputPtr += 1;
/* 3028 */           return i;
/*      */         }
/*      */       }
/* 3031 */       return _skipColon2(true);
/*      */     }
/* 3033 */     if ((i == 32) || (i == 9)) {
/* 3034 */       i = this._inputBuffer[(++this._inputPtr)];
/*      */     }
/* 3036 */     if (i == 58) {
/* 3037 */       i = this._inputBuffer[(++this._inputPtr)];
/* 3038 */       if (i > 32) {
/* 3039 */         if ((i == 47) || (i == 35)) {
/* 3040 */           return _skipColon2(true);
/*      */         }
/* 3042 */         this._inputPtr += 1;
/* 3043 */         return i;
/*      */       }
/* 3045 */       if ((i == 32) || (i == 9)) {
/* 3046 */         i = this._inputBuffer[(++this._inputPtr)];
/* 3047 */         if (i > 32) {
/* 3048 */           if ((i == 47) || (i == 35)) {
/* 3049 */             return _skipColon2(true);
/*      */           }
/* 3051 */           this._inputPtr += 1;
/* 3052 */           return i;
/*      */         }
/*      */       }
/* 3055 */       return _skipColon2(true);
/*      */     }
/* 3057 */     return _skipColon2(false);
/*      */   }
/*      */   
/*      */   private final int _skipColon2(boolean gotColon) throws IOException
/*      */   {
/* 3062 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 3063 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       
/* 3065 */       if (i > 32) {
/* 3066 */         if (i == 47) {
/* 3067 */           _skipComment();
/*      */ 
/*      */         }
/* 3070 */         else if ((i != 35) || 
/* 3071 */           (!_skipYAMLComment()))
/*      */         {
/*      */ 
/*      */ 
/* 3075 */           if (gotColon) {
/* 3076 */             return i;
/*      */           }
/* 3078 */           if (i != 58) {
/* 3079 */             if (i < 32) {
/* 3080 */               _throwInvalidSpace(i);
/*      */             }
/* 3082 */             _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */           }
/* 3084 */           gotColon = true;
/* 3085 */         } } else if (i != 32) {
/* 3086 */         if (i == 10) {
/* 3087 */           this._currInputRow += 1;
/* 3088 */           this._currInputRowStart = this._inputPtr;
/* 3089 */         } else if (i == 13) {
/* 3090 */           _skipCR();
/* 3091 */         } else if (i != 9) {
/* 3092 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 3096 */     _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
/*      */     
/* 3098 */     return -1;
/*      */   }
/*      */   
/*      */   private final void _skipComment() throws IOException
/*      */   {
/* 3103 */     if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
/* 3104 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 3107 */     if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/* 3108 */       _reportInvalidEOF(" in a comment", null);
/*      */     }
/* 3110 */     int c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3111 */     if (c == 47) {
/* 3112 */       _skipLine();
/* 3113 */     } else if (c == 42) {
/* 3114 */       _skipCComment();
/*      */     } else {
/* 3116 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _skipCComment()
/*      */     throws IOException
/*      */   {
/* 3123 */     int[] codes = CharTypes.getInputCodeComment();
/*      */     
/*      */ 
/*      */ 
/* 3127 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 3128 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3129 */       int code = codes[i];
/* 3130 */       if (code != 0)
/* 3131 */         switch (code) {
/*      */         case 42: 
/* 3133 */           if ((this._inputPtr >= this._inputEnd) && (!_loadMore())) {
/*      */             break label218;
/*      */           }
/* 3136 */           if (this._inputBuffer[this._inputPtr] == 47) {
/* 3137 */             this._inputPtr += 1; return;
/*      */           }
/*      */           
/*      */           break;
/*      */         case 10: 
/* 3142 */           this._currInputRow += 1;
/* 3143 */           this._currInputRowStart = this._inputPtr;
/* 3144 */           break;
/*      */         case 13: 
/* 3146 */           _skipCR();
/* 3147 */           break;
/*      */         case 2: 
/* 3149 */           _skipUtf8_2(i);
/* 3150 */           break;
/*      */         case 3: 
/* 3152 */           _skipUtf8_3(i);
/* 3153 */           break;
/*      */         case 4: 
/* 3155 */           _skipUtf8_4(i);
/* 3156 */           break;
/*      */         
/*      */         default: 
/* 3159 */           _reportInvalidChar(i);
/*      */         }
/*      */     }
/*      */     label218:
/* 3163 */     _reportInvalidEOF(" in a comment", null);
/*      */   }
/*      */   
/*      */   private final boolean _skipYAMLComment() throws IOException
/*      */   {
/* 3168 */     if (!isEnabled(JsonParser.Feature.ALLOW_YAML_COMMENTS)) {
/* 3169 */       return false;
/*      */     }
/* 3171 */     _skipLine();
/* 3172 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _skipLine()
/*      */     throws IOException
/*      */   {
/* 3182 */     int[] codes = CharTypes.getInputCodeComment();
/* 3183 */     while ((this._inputPtr < this._inputEnd) || (_loadMore())) {
/* 3184 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3185 */       int code = codes[i];
/* 3186 */       if (code != 0) {
/* 3187 */         switch (code) {
/*      */         case 10: 
/* 3189 */           this._currInputRow += 1;
/* 3190 */           this._currInputRowStart = this._inputPtr;
/* 3191 */           return;
/*      */         case 13: 
/* 3193 */           _skipCR(); return;
/*      */         case 42: 
/*      */           break;
/*      */         
/*      */         case 2: 
/* 3198 */           _skipUtf8_2(i);
/* 3199 */           break;
/*      */         case 3: 
/* 3201 */           _skipUtf8_3(i);
/* 3202 */           break;
/*      */         case 4: 
/* 3204 */           _skipUtf8_4(i);
/* 3205 */           break;
/*      */         default: 
/* 3207 */           if (code < 0)
/*      */           {
/* 3209 */             _reportInvalidChar(i);
/*      */           }
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected char _decodeEscaped() throws IOException
/*      */   {
/* 3219 */     if ((this._inputPtr >= this._inputEnd) && 
/* 3220 */       (!_loadMore())) {
/* 3221 */       _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */     }
/*      */     
/* 3224 */     int c = this._inputBuffer[(this._inputPtr++)];
/*      */     
/* 3226 */     switch (c)
/*      */     {
/*      */     case 98: 
/* 3229 */       return '\b';
/*      */     case 116: 
/* 3231 */       return '\t';
/*      */     case 110: 
/* 3233 */       return '\n';
/*      */     case 102: 
/* 3235 */       return '\f';
/*      */     case 114: 
/* 3237 */       return '\r';
/*      */     
/*      */ 
/*      */     case 34: 
/*      */     case 47: 
/*      */     case 92: 
/* 3243 */       return (char)c;
/*      */     
/*      */     case 117: 
/*      */       break;
/*      */     
/*      */     default: 
/* 3249 */       return _handleUnrecognizedCharacterEscape((char)_decodeCharForError(c));
/*      */     }
/*      */     
/*      */     
/* 3253 */     int value = 0;
/* 3254 */     for (int i = 0; i < 4; i++) {
/* 3255 */       if ((this._inputPtr >= this._inputEnd) && 
/* 3256 */         (!_loadMore())) {
/* 3257 */         _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/* 3260 */       int ch = this._inputBuffer[(this._inputPtr++)];
/* 3261 */       int digit = CharTypes.charToHex(ch);
/* 3262 */       if (digit < 0) {
/* 3263 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 3265 */       value = value << 4 | digit;
/*      */     }
/* 3267 */     return (char)value;
/*      */   }
/*      */   
/*      */   protected int _decodeCharForError(int firstByte) throws IOException
/*      */   {
/* 3272 */     int c = firstByte & 0xFF;
/* 3273 */     if (c > 127)
/*      */     {
/*      */       int needed;
/*      */       int needed;
/* 3277 */       if ((c & 0xE0) == 192) {
/* 3278 */         c &= 0x1F;
/* 3279 */         needed = 1; } else { int needed;
/* 3280 */         if ((c & 0xF0) == 224) {
/* 3281 */           c &= 0xF;
/* 3282 */           needed = 2; } else { int needed;
/* 3283 */           if ((c & 0xF8) == 240)
/*      */           {
/* 3285 */             c &= 0x7;
/* 3286 */             needed = 3;
/*      */           } else {
/* 3288 */             _reportInvalidInitial(c & 0xFF);
/* 3289 */             needed = 1;
/*      */           }
/*      */         } }
/* 3292 */       int d = nextByte();
/* 3293 */       if ((d & 0xC0) != 128) {
/* 3294 */         _reportInvalidOther(d & 0xFF);
/*      */       }
/* 3296 */       c = c << 6 | d & 0x3F;
/*      */       
/* 3298 */       if (needed > 1) {
/* 3299 */         d = nextByte();
/* 3300 */         if ((d & 0xC0) != 128) {
/* 3301 */           _reportInvalidOther(d & 0xFF);
/*      */         }
/* 3303 */         c = c << 6 | d & 0x3F;
/* 3304 */         if (needed > 2) {
/* 3305 */           d = nextByte();
/* 3306 */           if ((d & 0xC0) != 128) {
/* 3307 */             _reportInvalidOther(d & 0xFF);
/*      */           }
/* 3309 */           c = c << 6 | d & 0x3F;
/*      */         }
/*      */       }
/*      */     }
/* 3313 */     return c;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _decodeUtf8_2(int c)
/*      */     throws IOException
/*      */   {
/* 3324 */     if (this._inputPtr >= this._inputEnd) {
/* 3325 */       _loadMoreGuaranteed();
/*      */     }
/* 3327 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 3328 */     if ((d & 0xC0) != 128) {
/* 3329 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3331 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */   
/*      */   private final int _decodeUtf8_3(int c1) throws IOException
/*      */   {
/* 3336 */     if (this._inputPtr >= this._inputEnd) {
/* 3337 */       _loadMoreGuaranteed();
/*      */     }
/* 3339 */     c1 &= 0xF;
/* 3340 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 3341 */     if ((d & 0xC0) != 128) {
/* 3342 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3344 */     int c = c1 << 6 | d & 0x3F;
/* 3345 */     if (this._inputPtr >= this._inputEnd) {
/* 3346 */       _loadMoreGuaranteed();
/*      */     }
/* 3348 */     d = this._inputBuffer[(this._inputPtr++)];
/* 3349 */     if ((d & 0xC0) != 128) {
/* 3350 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3352 */     c = c << 6 | d & 0x3F;
/* 3353 */     return c;
/*      */   }
/*      */   
/*      */   private final int _decodeUtf8_3fast(int c1) throws IOException
/*      */   {
/* 3358 */     c1 &= 0xF;
/* 3359 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 3360 */     if ((d & 0xC0) != 128) {
/* 3361 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3363 */     int c = c1 << 6 | d & 0x3F;
/* 3364 */     d = this._inputBuffer[(this._inputPtr++)];
/* 3365 */     if ((d & 0xC0) != 128) {
/* 3366 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3368 */     c = c << 6 | d & 0x3F;
/* 3369 */     return c;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _decodeUtf8_4(int c)
/*      */     throws IOException
/*      */   {
/* 3378 */     if (this._inputPtr >= this._inputEnd) {
/* 3379 */       _loadMoreGuaranteed();
/*      */     }
/* 3381 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 3382 */     if ((d & 0xC0) != 128) {
/* 3383 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3385 */     c = (c & 0x7) << 6 | d & 0x3F;
/*      */     
/* 3387 */     if (this._inputPtr >= this._inputEnd) {
/* 3388 */       _loadMoreGuaranteed();
/*      */     }
/* 3390 */     d = this._inputBuffer[(this._inputPtr++)];
/* 3391 */     if ((d & 0xC0) != 128) {
/* 3392 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3394 */     c = c << 6 | d & 0x3F;
/* 3395 */     if (this._inputPtr >= this._inputEnd) {
/* 3396 */       _loadMoreGuaranteed();
/*      */     }
/* 3398 */     d = this._inputBuffer[(this._inputPtr++)];
/* 3399 */     if ((d & 0xC0) != 128) {
/* 3400 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3406 */     return (c << 6 | d & 0x3F) - 65536;
/*      */   }
/*      */   
/*      */   private final void _skipUtf8_2(int c) throws IOException
/*      */   {
/* 3411 */     if (this._inputPtr >= this._inputEnd) {
/* 3412 */       _loadMoreGuaranteed();
/*      */     }
/* 3414 */     c = this._inputBuffer[(this._inputPtr++)];
/* 3415 */     if ((c & 0xC0) != 128) {
/* 3416 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final void _skipUtf8_3(int c)
/*      */     throws IOException
/*      */   {
/* 3425 */     if (this._inputPtr >= this._inputEnd) {
/* 3426 */       _loadMoreGuaranteed();
/*      */     }
/*      */     
/* 3429 */     c = this._inputBuffer[(this._inputPtr++)];
/* 3430 */     if ((c & 0xC0) != 128) {
/* 3431 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/* 3433 */     if (this._inputPtr >= this._inputEnd) {
/* 3434 */       _loadMoreGuaranteed();
/*      */     }
/* 3436 */     c = this._inputBuffer[(this._inputPtr++)];
/* 3437 */     if ((c & 0xC0) != 128) {
/* 3438 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _skipUtf8_4(int c) throws IOException
/*      */   {
/* 3444 */     if (this._inputPtr >= this._inputEnd) {
/* 3445 */       _loadMoreGuaranteed();
/*      */     }
/* 3447 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 3448 */     if ((d & 0xC0) != 128) {
/* 3449 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3451 */     if (this._inputPtr >= this._inputEnd) {
/* 3452 */       _loadMoreGuaranteed();
/*      */     }
/* 3454 */     d = this._inputBuffer[(this._inputPtr++)];
/* 3455 */     if ((d & 0xC0) != 128) {
/* 3456 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3458 */     if (this._inputPtr >= this._inputEnd) {
/* 3459 */       _loadMoreGuaranteed();
/*      */     }
/* 3461 */     d = this._inputBuffer[(this._inputPtr++)];
/* 3462 */     if ((d & 0xC0) != 128) {
/* 3463 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
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
/*      */   protected final void _skipCR()
/*      */     throws IOException
/*      */   {
/* 3479 */     if (((this._inputPtr < this._inputEnd) || (_loadMore())) && 
/* 3480 */       (this._inputBuffer[this._inputPtr] == 10)) {
/* 3481 */       this._inputPtr += 1;
/*      */     }
/*      */     
/* 3484 */     this._currInputRow += 1;
/* 3485 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */   
/*      */   private int nextByte() throws IOException
/*      */   {
/* 3490 */     if (this._inputPtr >= this._inputEnd) {
/* 3491 */       _loadMoreGuaranteed();
/*      */     }
/* 3493 */     return this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _reportInvalidToken(String matchedPart)
/*      */     throws IOException
/*      */   {
/* 3504 */     _reportInvalidToken(matchedPart, "'null', 'true', 'false' or NaN");
/*      */   }
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException
/*      */   {
/* 3509 */     StringBuilder sb = new StringBuilder(matchedPart);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3516 */     while ((this._inputPtr < this._inputEnd) || (_loadMore()))
/*      */     {
/*      */ 
/* 3519 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 3520 */       char c = (char)_decodeCharForError(i);
/* 3521 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 3524 */       sb.append(c);
/*      */     }
/* 3526 */     _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _reportInvalidChar(int c)
/*      */     throws JsonParseException
/*      */   {
/* 3533 */     if (c < 32) {
/* 3534 */       _throwInvalidSpace(c);
/*      */     }
/* 3536 */     _reportInvalidInitial(c);
/*      */   }
/*      */   
/*      */   protected void _reportInvalidInitial(int mask)
/*      */     throws JsonParseException
/*      */   {
/* 3542 */     _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */   
/*      */   protected void _reportInvalidOther(int mask)
/*      */     throws JsonParseException
/*      */   {
/* 3548 */     _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */   
/*      */   protected void _reportInvalidOther(int mask, int ptr)
/*      */     throws JsonParseException
/*      */   {
/* 3554 */     this._inputPtr = ptr;
/* 3555 */     _reportInvalidOther(mask);
/*      */   }
/*      */   
/*      */   public static int[] growArrayBy(int[] arr, int more)
/*      */   {
/* 3560 */     if (arr == null) {
/* 3561 */       return new int[more];
/*      */     }
/* 3563 */     return Arrays.copyOf(arr, arr.length + more);
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
/*      */   protected final byte[] _decodeBase64(Base64Variant b64variant)
/*      */     throws IOException
/*      */   {
/* 3579 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 3586 */       if (this._inputPtr >= this._inputEnd) {
/* 3587 */         _loadMoreGuaranteed();
/*      */       }
/* 3589 */       int ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3590 */       if (ch > 32) {
/* 3591 */         int bits = b64variant.decodeBase64Char(ch);
/* 3592 */         if (bits < 0) {
/* 3593 */           if (ch == 34) {
/* 3594 */             return builder.toByteArray();
/*      */           }
/* 3596 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 3597 */           if (bits < 0) {}
/*      */         }
/*      */         else
/*      */         {
/* 3601 */           int decodedData = bits;
/*      */           
/*      */ 
/*      */ 
/* 3605 */           if (this._inputPtr >= this._inputEnd) {
/* 3606 */             _loadMoreGuaranteed();
/*      */           }
/* 3608 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3609 */           bits = b64variant.decodeBase64Char(ch);
/* 3610 */           if (bits < 0) {
/* 3611 */             bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */           }
/* 3613 */           decodedData = decodedData << 6 | bits;
/*      */           
/*      */ 
/* 3616 */           if (this._inputPtr >= this._inputEnd) {
/* 3617 */             _loadMoreGuaranteed();
/*      */           }
/* 3619 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3620 */           bits = b64variant.decodeBase64Char(ch);
/*      */           
/*      */ 
/* 3623 */           if (bits < 0) {
/* 3624 */             if (bits != -2)
/*      */             {
/* 3626 */               if ((ch == 34) && (!b64variant.usesPadding())) {
/* 3627 */                 decodedData >>= 4;
/* 3628 */                 builder.append(decodedData);
/* 3629 */                 return builder.toByteArray();
/*      */               }
/* 3631 */               bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */             }
/* 3633 */             if (bits == -2)
/*      */             {
/* 3635 */               if (this._inputPtr >= this._inputEnd) {
/* 3636 */                 _loadMoreGuaranteed();
/*      */               }
/* 3638 */               ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3639 */               if (!b64variant.usesPaddingChar(ch)) {
/* 3640 */                 throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */               }
/*      */               
/* 3643 */               decodedData >>= 4;
/* 3644 */               builder.append(decodedData);
/* 3645 */               continue;
/*      */             }
/*      */           }
/*      */           
/* 3649 */           decodedData = decodedData << 6 | bits;
/*      */           
/* 3651 */           if (this._inputPtr >= this._inputEnd) {
/* 3652 */             _loadMoreGuaranteed();
/*      */           }
/* 3654 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 3655 */           bits = b64variant.decodeBase64Char(ch);
/* 3656 */           if (bits < 0) {
/* 3657 */             if (bits != -2)
/*      */             {
/* 3659 */               if ((ch == 34) && (!b64variant.usesPadding())) {
/* 3660 */                 decodedData >>= 2;
/* 3661 */                 builder.appendTwoBytes(decodedData);
/* 3662 */                 return builder.toByteArray();
/*      */               }
/* 3664 */               bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */             }
/* 3666 */             if (bits == -2)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3673 */               decodedData >>= 2;
/* 3674 */               builder.appendTwoBytes(decodedData);
/* 3675 */               continue;
/*      */             }
/*      */           }
/*      */           
/* 3679 */           decodedData = decodedData << 6 | bits;
/* 3680 */           builder.appendThreeBytes(decodedData);
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
/*      */   public JsonLocation getTokenLocation()
/*      */   {
/* 3694 */     Object src = this._ioContext.getSourceReference();
/* 3695 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 3696 */       long total = this._currInputProcessed + (this._nameStartOffset - 1);
/* 3697 */       return new JsonLocation(src, total, -1L, this._nameStartRow, this._nameStartCol);
/*      */     }
/*      */     
/* 3700 */     return new JsonLocation(src, this._tokenInputTotal - 1L, -1L, this._tokenInputRow, this._tokenInputCol);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonLocation getCurrentLocation()
/*      */   {
/* 3708 */     int col = this._inputPtr - this._currInputRowStart + 1;
/* 3709 */     return new JsonLocation(this._ioContext.getSourceReference(), this._currInputProcessed + this._inputPtr, -1L, this._currInputRow, col);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _updateLocation()
/*      */   {
/* 3717 */     this._tokenInputRow = this._currInputRow;
/* 3718 */     int ptr = this._inputPtr;
/* 3719 */     this._tokenInputTotal = (this._currInputProcessed + ptr);
/* 3720 */     this._tokenInputCol = (ptr - this._currInputRowStart);
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _updateNameLocation()
/*      */   {
/* 3726 */     this._nameStartRow = this._currInputRow;
/* 3727 */     int ptr = this._inputPtr;
/* 3728 */     this._nameStartOffset = ptr;
/* 3729 */     this._nameStartCol = (ptr - this._currInputRowStart);
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
/*      */   private static final int pad(int q, int bytes)
/*      */   {
/* 3742 */     return bytes == 4 ? q : q | -1 << (bytes << 3);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\UTF8StreamJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */