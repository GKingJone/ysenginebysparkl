/*     */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonEncoding;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonFactory.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.format.InputAccessor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.format.MatchStrength;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.IOContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.MergedStream;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.UTF32Reader;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.sym.ByteQuadsCanonicalizer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.sym.CharsToNameCanonicalizer;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.DataInput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
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
/*     */ public final class ByteSourceJsonBootstrapper
/*     */ {
/*     */   static final byte UTF8_BOM_1 = -17;
/*     */   static final byte UTF8_BOM_2 = -69;
/*     */   static final byte UTF8_BOM_3 = -65;
/*     */   protected final IOContext _context;
/*     */   protected final InputStream _in;
/*     */   protected final byte[] _inputBuffer;
/*     */   private int _inputPtr;
/*     */   private int _inputEnd;
/*     */   private final boolean _bufferRecyclable;
/*     */   protected int _inputProcessed;
/*  74 */   protected boolean _bigEndian = true;
/*     */   
/*     */ 
/*     */ 
/*     */   protected int _bytesPerChar;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteSourceJsonBootstrapper(IOContext ctxt, InputStream in)
/*     */   {
/*  85 */     this._context = ctxt;
/*  86 */     this._in = in;
/*  87 */     this._inputBuffer = ctxt.allocReadIOBuffer();
/*  88 */     this._inputEnd = (this._inputPtr = 0);
/*  89 */     this._inputProcessed = 0;
/*  90 */     this._bufferRecyclable = true;
/*     */   }
/*     */   
/*     */   public ByteSourceJsonBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen) {
/*  94 */     this._context = ctxt;
/*  95 */     this._in = null;
/*  96 */     this._inputBuffer = inputBuffer;
/*  97 */     this._inputPtr = inputStart;
/*  98 */     this._inputEnd = (inputStart + inputLen);
/*     */     
/* 100 */     this._inputProcessed = (-inputStart);
/* 101 */     this._bufferRecyclable = false;
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
/*     */   public JsonEncoding detectEncoding()
/*     */     throws IOException
/*     */   {
/* 117 */     boolean foundEncoding = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */     if (ensureLoaded(4)) {
/* 127 */       int quad = this._inputBuffer[this._inputPtr] << 24 | (this._inputBuffer[(this._inputPtr + 1)] & 0xFF) << 16 | (this._inputBuffer[(this._inputPtr + 2)] & 0xFF) << 8 | this._inputBuffer[(this._inputPtr + 3)] & 0xFF;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 132 */       if (handleBOM(quad)) {
/* 133 */         foundEncoding = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 141 */       else if (checkUTF32(quad)) {
/* 142 */         foundEncoding = true;
/* 143 */       } else if (checkUTF16(quad >>> 16)) {
/* 144 */         foundEncoding = true;
/*     */       }
/*     */     }
/* 147 */     else if (ensureLoaded(2)) {
/* 148 */       int i16 = (this._inputBuffer[this._inputPtr] & 0xFF) << 8 | this._inputBuffer[(this._inputPtr + 1)] & 0xFF;
/*     */       
/* 150 */       if (checkUTF16(i16)) {
/* 151 */         foundEncoding = true;
/*     */       }
/*     */     }
/*     */     
/*     */     JsonEncoding enc;
/*     */     
/*     */     JsonEncoding enc;
/* 158 */     if (!foundEncoding) {
/* 159 */       enc = JsonEncoding.UTF8;
/*     */     } else {
/* 161 */       switch (this._bytesPerChar) {
/* 162 */       case 1:  enc = JsonEncoding.UTF8;
/* 163 */         break;
/* 164 */       case 2:  enc = this._bigEndian ? JsonEncoding.UTF16_BE : JsonEncoding.UTF16_LE;
/* 165 */         break;
/* 166 */       case 4:  enc = this._bigEndian ? JsonEncoding.UTF32_BE : JsonEncoding.UTF32_LE;
/* 167 */         break;
/* 168 */       case 3: default:  throw new RuntimeException("Internal error");
/*     */       }
/*     */     }
/* 171 */     this._context.setEncoding(enc);
/* 172 */     return enc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int skipUTF8BOM(DataInput input)
/*     */     throws IOException
/*     */   {
/* 184 */     int b = input.readUnsignedByte();
/* 185 */     if (b != 239) {
/* 186 */       return b;
/*     */     }
/*     */     
/*     */ 
/* 190 */     b = input.readUnsignedByte();
/* 191 */     if (b != 187) {
/* 192 */       throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF; should get 0xBB as part of UTF-8 BOM");
/*     */     }
/*     */     
/* 195 */     b = input.readUnsignedByte();
/* 196 */     if (b != 191) {
/* 197 */       throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF 0xBB; should get 0xBF as part of UTF-8 BOM");
/*     */     }
/*     */     
/* 200 */     return input.readUnsignedByte();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Reader constructReader()
/*     */     throws IOException
/*     */   {
/* 212 */     JsonEncoding enc = this._context.getEncoding();
/* 213 */     switch (enc.bits())
/*     */     {
/*     */ 
/*     */     case 8: 
/*     */     case 16: 
/* 218 */       InputStream in = this._in;
/*     */       
/* 220 */       if (in == null) {
/* 221 */         in = new ByteArrayInputStream(this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 226 */       else if (this._inputPtr < this._inputEnd) {
/* 227 */         in = new MergedStream(this._context, in, this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */       }
/*     */       
/* 230 */       return new InputStreamReader(in, enc.getJavaName());
/*     */     
/*     */     case 32: 
/* 233 */       return new UTF32Reader(this._context, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._context.getEncoding().isBigEndian());
/*     */     }
/*     */     
/* 236 */     throw new RuntimeException("Internal error");
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonParser constructParser(int parserFeatures, ObjectCodec codec, ByteQuadsCanonicalizer rootByteSymbols, CharsToNameCanonicalizer rootCharSymbols, int factoryFeatures)
/*     */     throws IOException
/*     */   {
/* 243 */     JsonEncoding enc = detectEncoding();
/*     */     
/* 245 */     if (enc == JsonEncoding.UTF8)
/*     */     {
/*     */ 
/*     */ 
/* 249 */       if (JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(factoryFeatures)) {
/* 250 */         ByteQuadsCanonicalizer can = rootByteSymbols.makeChild(factoryFeatures);
/* 251 */         return new UTF8StreamJsonParser(this._context, parserFeatures, this._in, codec, can, this._inputBuffer, this._inputPtr, this._inputEnd, this._bufferRecyclable);
/*     */       }
/*     */     }
/*     */     
/* 255 */     return new ReaderBasedJsonParser(this._context, parserFeatures, constructReader(), codec, rootCharSymbols.makeChild(factoryFeatures));
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
/*     */   public static MatchStrength hasJSONFormat(InputAccessor acc)
/*     */     throws IOException
/*     */   {
/* 276 */     if (!acc.hasMoreBytes()) {
/* 277 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/* 279 */     byte b = acc.nextByte();
/*     */     
/* 281 */     if (b == -17) {
/* 282 */       if (!acc.hasMoreBytes()) {
/* 283 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 285 */       if (acc.nextByte() != -69) {
/* 286 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 288 */       if (!acc.hasMoreBytes()) {
/* 289 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 291 */       if (acc.nextByte() != -65) {
/* 292 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 294 */       if (!acc.hasMoreBytes()) {
/* 295 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 297 */       b = acc.nextByte();
/*     */     }
/*     */     
/* 300 */     int ch = skipSpace(acc, b);
/* 301 */     if (ch < 0) {
/* 302 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/*     */     
/* 305 */     if (ch == 123)
/*     */     {
/* 307 */       ch = skipSpace(acc);
/* 308 */       if (ch < 0) {
/* 309 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 311 */       if ((ch == 34) || (ch == 125)) {
/* 312 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/*     */       
/* 315 */       return MatchStrength.NO_MATCH;
/*     */     }
/*     */     
/*     */ 
/* 319 */     if (ch == 91) {
/* 320 */       ch = skipSpace(acc);
/* 321 */       if (ch < 0) {
/* 322 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/*     */       
/* 325 */       if ((ch == 93) || (ch == 91)) {
/* 326 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/* 328 */       return MatchStrength.SOLID_MATCH;
/*     */     }
/*     */     
/* 331 */     MatchStrength strength = MatchStrength.WEAK_MATCH;
/*     */     
/*     */ 
/* 334 */     if (ch == 34) {
/* 335 */       return strength;
/*     */     }
/* 337 */     if ((ch <= 57) && (ch >= 48)) {
/* 338 */       return strength;
/*     */     }
/* 340 */     if (ch == 45) {
/* 341 */       ch = skipSpace(acc);
/* 342 */       if (ch < 0) {
/* 343 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 345 */       return (ch <= 57) && (ch >= 48) ? strength : MatchStrength.NO_MATCH;
/*     */     }
/*     */     
/* 348 */     if (ch == 110) {
/* 349 */       return tryMatch(acc, "ull", strength);
/*     */     }
/* 351 */     if (ch == 116) {
/* 352 */       return tryMatch(acc, "rue", strength);
/*     */     }
/* 354 */     if (ch == 102) {
/* 355 */       return tryMatch(acc, "alse", strength);
/*     */     }
/* 357 */     return MatchStrength.NO_MATCH;
/*     */   }
/*     */   
/*     */   private static MatchStrength tryMatch(InputAccessor acc, String matchStr, MatchStrength fullMatchStrength)
/*     */     throws IOException
/*     */   {
/* 363 */     int i = 0; for (int len = matchStr.length(); i < len; i++) {
/* 364 */       if (!acc.hasMoreBytes()) {
/* 365 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 367 */       if (acc.nextByte() != matchStr.charAt(i)) {
/* 368 */         return MatchStrength.NO_MATCH;
/*     */       }
/*     */     }
/* 371 */     return fullMatchStrength;
/*     */   }
/*     */   
/*     */   private static int skipSpace(InputAccessor acc) throws IOException
/*     */   {
/* 376 */     if (!acc.hasMoreBytes()) {
/* 377 */       return -1;
/*     */     }
/* 379 */     return skipSpace(acc, acc.nextByte());
/*     */   }
/*     */   
/*     */   private static int skipSpace(InputAccessor acc, byte b) throws IOException
/*     */   {
/*     */     for (;;) {
/* 385 */       int ch = b & 0xFF;
/* 386 */       if ((ch != 32) && (ch != 13) && (ch != 10) && (ch != 9)) {
/* 387 */         return ch;
/*     */       }
/* 389 */       if (!acc.hasMoreBytes()) {
/* 390 */         return -1;
/*     */       }
/* 392 */       b = acc.nextByte();
/* 393 */       ch = b & 0xFF;
/*     */     }
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
/*     */   private boolean handleBOM(int quad)
/*     */     throws IOException
/*     */   {
/* 412 */     switch (quad) {
/*     */     case 65279: 
/* 414 */       this._bigEndian = true;
/* 415 */       this._inputPtr += 4;
/* 416 */       this._bytesPerChar = 4;
/* 417 */       return true;
/*     */     case -131072: 
/* 419 */       this._inputPtr += 4;
/* 420 */       this._bytesPerChar = 4;
/* 421 */       this._bigEndian = false;
/* 422 */       return true;
/*     */     case 65534: 
/* 424 */       reportWeirdUCS4("2143");
/*     */     case -16842752: 
/* 426 */       reportWeirdUCS4("3412");
/*     */     }
/*     */     
/* 429 */     int msw = quad >>> 16;
/* 430 */     if (msw == 65279) {
/* 431 */       this._inputPtr += 2;
/* 432 */       this._bytesPerChar = 2;
/* 433 */       this._bigEndian = true;
/* 434 */       return true;
/*     */     }
/* 436 */     if (msw == 65534) {
/* 437 */       this._inputPtr += 2;
/* 438 */       this._bytesPerChar = 2;
/* 439 */       this._bigEndian = false;
/* 440 */       return true;
/*     */     }
/*     */     
/* 443 */     if (quad >>> 8 == 15711167) {
/* 444 */       this._inputPtr += 3;
/* 445 */       this._bytesPerChar = 1;
/* 446 */       this._bigEndian = true;
/* 447 */       return true;
/*     */     }
/* 449 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean checkUTF32(int quad)
/*     */     throws IOException
/*     */   {
/* 457 */     if (quad >> 8 == 0) {
/* 458 */       this._bigEndian = true;
/* 459 */     } else if ((quad & 0xFFFFFF) == 0) {
/* 460 */       this._bigEndian = false;
/* 461 */     } else if ((quad & 0xFF00FFFF) == 0) {
/* 462 */       reportWeirdUCS4("3412");
/* 463 */     } else if ((quad & 0xFFFF00FF) == 0) {
/* 464 */       reportWeirdUCS4("2143");
/*     */     }
/*     */     else {
/* 467 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 471 */     this._bytesPerChar = 4;
/* 472 */     return true;
/*     */   }
/*     */   
/*     */   private boolean checkUTF16(int i16)
/*     */   {
/* 477 */     if ((i16 & 0xFF00) == 0) {
/* 478 */       this._bigEndian = true;
/* 479 */     } else if ((i16 & 0xFF) == 0) {
/* 480 */       this._bigEndian = false;
/*     */     } else {
/* 482 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 486 */     this._bytesPerChar = 2;
/* 487 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportWeirdUCS4(String type)
/*     */     throws IOException
/*     */   {
/* 497 */     throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean ensureLoaded(int minimum)
/*     */     throws IOException
/*     */   {
/* 510 */     int gotten = this._inputEnd - this._inputPtr;
/* 511 */     while (gotten < minimum) {
/*     */       int count;
/*     */       int count;
/* 514 */       if (this._in == null) {
/* 515 */         count = -1;
/*     */       } else {
/* 517 */         count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
/*     */       }
/* 519 */       if (count < 1) {
/* 520 */         return false;
/*     */       }
/* 522 */       this._inputEnd += count;
/* 523 */       gotten += count;
/*     */     }
/* 525 */     return true;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\ByteSourceJsonBootstrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */