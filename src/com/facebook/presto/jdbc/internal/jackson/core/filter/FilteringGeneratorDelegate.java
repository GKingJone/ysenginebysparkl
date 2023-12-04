/*     */ package com.facebook.presto.jdbc.internal.jackson.core.filter;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonStreamContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.util.JsonGeneratorDelegate;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ public class FilteringGeneratorDelegate
/*     */   extends JsonGeneratorDelegate
/*     */ {
/*     */   protected TokenFilter rootFilter;
/*     */   protected boolean _allowMultipleMatches;
/*     */   protected boolean _includePath;
/*     */   @Deprecated
/*     */   protected boolean _includeImmediateParent;
/*     */   protected TokenFilterContext _filterContext;
/*     */   protected TokenFilter _itemFilter;
/*     */   protected int _matchCount;
/*     */   
/*     */   public FilteringGeneratorDelegate(JsonGenerator d, TokenFilter f, boolean includePath, boolean allowMultipleMatches)
/*     */   {
/*  96 */     super(d, false);
/*  97 */     this.rootFilter = f;
/*     */     
/*  99 */     this._itemFilter = f;
/* 100 */     this._filterContext = TokenFilterContext.createRootContext(f);
/* 101 */     this._includePath = includePath;
/* 102 */     this._allowMultipleMatches = allowMultipleMatches;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TokenFilter getFilter()
/*     */   {
/* 111 */     return this.rootFilter;
/*     */   }
/*     */   
/* 114 */   public JsonStreamContext getFilterContext() { return this._filterContext; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMatchCount()
/*     */   {
/* 122 */     return this._matchCount;
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
/*     */   public JsonStreamContext getOutputContext()
/*     */   {
/* 137 */     return this._filterContext;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeStartArray()
/*     */     throws IOException
/*     */   {
/* 150 */     if (this._itemFilter == null) {
/* 151 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/* 152 */       return;
/*     */     }
/* 154 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 155 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/* 156 */       this.delegate.writeStartArray();
/* 157 */       return;
/*     */     }
/*     */     
/* 160 */     this._itemFilter = this._filterContext.checkValue(this._itemFilter);
/* 161 */     if (this._itemFilter == null) {
/* 162 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/* 163 */       return;
/*     */     }
/* 165 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 166 */       this._itemFilter = this._itemFilter.filterStartArray();
/*     */     }
/* 168 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 169 */       _checkParentPath();
/* 170 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/* 171 */       this.delegate.writeStartArray();
/*     */     } else {
/* 173 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeStartArray(int size)
/*     */     throws IOException
/*     */   {
/* 180 */     if (this._itemFilter == null) {
/* 181 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/* 182 */       return;
/*     */     }
/* 184 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 185 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/* 186 */       this.delegate.writeStartArray(size);
/* 187 */       return;
/*     */     }
/* 189 */     this._itemFilter = this._filterContext.checkValue(this._itemFilter);
/* 190 */     if (this._itemFilter == null) {
/* 191 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/* 192 */       return;
/*     */     }
/* 194 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 195 */       this._itemFilter = this._itemFilter.filterStartArray();
/*     */     }
/* 197 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 198 */       _checkParentPath();
/* 199 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/* 200 */       this.delegate.writeStartArray(size);
/*     */     } else {
/* 202 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeEndArray()
/*     */     throws IOException
/*     */   {
/* 209 */     this._filterContext = this._filterContext.closeArray(this.delegate);
/*     */     
/* 211 */     if (this._filterContext != null) {
/* 212 */       this._itemFilter = this._filterContext.getFilter();
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeStartObject()
/*     */     throws IOException
/*     */   {
/* 219 */     if (this._itemFilter == null) {
/* 220 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, false);
/* 221 */       return;
/*     */     }
/* 223 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 224 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, true);
/* 225 */       this.delegate.writeStartObject();
/* 226 */       return;
/*     */     }
/*     */     
/* 229 */     TokenFilter f = this._filterContext.checkValue(this._itemFilter);
/* 230 */     if (f == null) {
/* 231 */       return;
/*     */     }
/*     */     
/* 234 */     if (f != TokenFilter.INCLUDE_ALL) {
/* 235 */       f = f.filterStartObject();
/*     */     }
/* 237 */     if (f == TokenFilter.INCLUDE_ALL) {
/* 238 */       _checkParentPath();
/* 239 */       this._filterContext = this._filterContext.createChildObjectContext(f, true);
/* 240 */       this.delegate.writeStartObject();
/*     */     } else {
/* 242 */       this._filterContext = this._filterContext.createChildObjectContext(f, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeEndObject()
/*     */     throws IOException
/*     */   {
/* 249 */     this._filterContext = this._filterContext.closeObject(this.delegate);
/* 250 */     if (this._filterContext != null) {
/* 251 */       this._itemFilter = this._filterContext.getFilter();
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeFieldName(String name)
/*     */     throws IOException
/*     */   {
/* 258 */     TokenFilter state = this._filterContext.setFieldName(name);
/* 259 */     if (state == null) {
/* 260 */       this._itemFilter = null;
/* 261 */       return;
/*     */     }
/* 263 */     if (state == TokenFilter.INCLUDE_ALL) {
/* 264 */       this._itemFilter = state;
/* 265 */       this.delegate.writeFieldName(name);
/* 266 */       return;
/*     */     }
/* 268 */     state = state.includeProperty(name);
/* 269 */     this._itemFilter = state;
/* 270 */     if (state == TokenFilter.INCLUDE_ALL) {
/* 271 */       _checkPropertyParentPath();
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeFieldName(SerializableString name)
/*     */     throws IOException
/*     */   {
/* 278 */     TokenFilter state = this._filterContext.setFieldName(name.getValue());
/* 279 */     if (state == null) {
/* 280 */       this._itemFilter = null;
/* 281 */       return;
/*     */     }
/* 283 */     if (state == TokenFilter.INCLUDE_ALL) {
/* 284 */       this._itemFilter = state;
/* 285 */       this.delegate.writeFieldName(name);
/* 286 */       return;
/*     */     }
/* 288 */     state = state.includeProperty(name.getValue());
/* 289 */     this._itemFilter = state;
/* 290 */     if (state == TokenFilter.INCLUDE_ALL) {
/* 291 */       _checkPropertyParentPath();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeString(String value)
/*     */     throws IOException
/*     */   {
/* 304 */     if (this._itemFilter == null) {
/* 305 */       return;
/*     */     }
/* 307 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 308 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 309 */       if (state == null) {
/* 310 */         return;
/*     */       }
/* 312 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 313 */         (!state.includeString(value))) {
/* 314 */         return;
/*     */       }
/*     */       
/* 317 */       _checkParentPath();
/*     */     }
/* 319 */     this.delegate.writeString(value);
/*     */   }
/*     */   
/*     */   public void writeString(char[] text, int offset, int len)
/*     */     throws IOException
/*     */   {
/* 325 */     if (this._itemFilter == null) {
/* 326 */       return;
/*     */     }
/* 328 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 329 */       String value = new String(text, offset, len);
/* 330 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 331 */       if (state == null) {
/* 332 */         return;
/*     */       }
/* 334 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 335 */         (!state.includeString(value))) {
/* 336 */         return;
/*     */       }
/*     */       
/* 339 */       _checkParentPath();
/*     */     }
/* 341 */     this.delegate.writeString(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeString(SerializableString value)
/*     */     throws IOException
/*     */   {
/* 347 */     if (this._itemFilter == null) {
/* 348 */       return;
/*     */     }
/* 350 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 351 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 352 */       if (state == null) {
/* 353 */         return;
/*     */       }
/* 355 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 356 */         (!state.includeString(value.getValue()))) {
/* 357 */         return;
/*     */       }
/*     */       
/* 360 */       _checkParentPath();
/*     */     }
/* 362 */     this.delegate.writeString(value);
/*     */   }
/*     */   
/*     */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*     */     throws IOException
/*     */   {
/* 368 */     if (_checkRawValueWrite()) {
/* 369 */       this.delegate.writeRawUTF8String(text, offset, length);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeUTF8String(byte[] text, int offset, int length)
/*     */     throws IOException
/*     */   {
/* 377 */     if (_checkRawValueWrite()) {
/* 378 */       this.delegate.writeUTF8String(text, offset, length);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeRaw(String text)
/*     */     throws IOException
/*     */   {
/* 391 */     if (_checkRawValueWrite()) {
/* 392 */       this.delegate.writeRaw(text);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeRaw(String text, int offset, int len)
/*     */     throws IOException
/*     */   {
/* 399 */     if (_checkRawValueWrite()) {
/* 400 */       this.delegate.writeRaw(text);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeRaw(SerializableString text)
/*     */     throws IOException
/*     */   {
/* 407 */     if (_checkRawValueWrite()) {
/* 408 */       this.delegate.writeRaw(text);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeRaw(char[] text, int offset, int len)
/*     */     throws IOException
/*     */   {
/* 415 */     if (_checkRawValueWrite()) {
/* 416 */       this.delegate.writeRaw(text, offset, len);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeRaw(char c)
/*     */     throws IOException
/*     */   {
/* 423 */     if (_checkRawValueWrite()) {
/* 424 */       this.delegate.writeRaw(c);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text)
/*     */     throws IOException
/*     */   {
/* 431 */     if (_checkRawValueWrite()) {
/* 432 */       this.delegate.writeRaw(text);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text, int offset, int len)
/*     */     throws IOException
/*     */   {
/* 439 */     if (_checkRawValueWrite()) {
/* 440 */       this.delegate.writeRaw(text, offset, len);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeRawValue(char[] text, int offset, int len)
/*     */     throws IOException
/*     */   {
/* 447 */     if (_checkRawValueWrite()) {
/* 448 */       this.delegate.writeRaw(text, offset, len);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
/*     */     throws IOException
/*     */   {
/* 455 */     if (_checkBinaryWrite()) {
/* 456 */       this.delegate.writeBinary(b64variant, data, offset, len);
/*     */     }
/*     */   }
/*     */   
/*     */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
/*     */     throws IOException
/*     */   {
/* 463 */     if (_checkBinaryWrite()) {
/* 464 */       return this.delegate.writeBinary(b64variant, data, dataLength);
/*     */     }
/* 466 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeNumber(short v)
/*     */     throws IOException
/*     */   {
/* 478 */     if (this._itemFilter == null) {
/* 479 */       return;
/*     */     }
/* 481 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 482 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 483 */       if (state == null) {
/* 484 */         return;
/*     */       }
/* 486 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 487 */         (!state.includeNumber(v))) {
/* 488 */         return;
/*     */       }
/*     */       
/* 491 */       _checkParentPath();
/*     */     }
/* 493 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(int v)
/*     */     throws IOException
/*     */   {
/* 499 */     if (this._itemFilter == null) {
/* 500 */       return;
/*     */     }
/* 502 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 503 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 504 */       if (state == null) {
/* 505 */         return;
/*     */       }
/* 507 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 508 */         (!state.includeNumber(v))) {
/* 509 */         return;
/*     */       }
/*     */       
/* 512 */       _checkParentPath();
/*     */     }
/* 514 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(long v)
/*     */     throws IOException
/*     */   {
/* 520 */     if (this._itemFilter == null) {
/* 521 */       return;
/*     */     }
/* 523 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 524 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 525 */       if (state == null) {
/* 526 */         return;
/*     */       }
/* 528 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 529 */         (!state.includeNumber(v))) {
/* 530 */         return;
/*     */       }
/*     */       
/* 533 */       _checkParentPath();
/*     */     }
/* 535 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(BigInteger v)
/*     */     throws IOException
/*     */   {
/* 541 */     if (this._itemFilter == null) {
/* 542 */       return;
/*     */     }
/* 544 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 545 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 546 */       if (state == null) {
/* 547 */         return;
/*     */       }
/* 549 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 550 */         (!state.includeNumber(v))) {
/* 551 */         return;
/*     */       }
/*     */       
/* 554 */       _checkParentPath();
/*     */     }
/* 556 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(double v)
/*     */     throws IOException
/*     */   {
/* 562 */     if (this._itemFilter == null) {
/* 563 */       return;
/*     */     }
/* 565 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 566 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 567 */       if (state == null) {
/* 568 */         return;
/*     */       }
/* 570 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 571 */         (!state.includeNumber(v))) {
/* 572 */         return;
/*     */       }
/*     */       
/* 575 */       _checkParentPath();
/*     */     }
/* 577 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(float v)
/*     */     throws IOException
/*     */   {
/* 583 */     if (this._itemFilter == null) {
/* 584 */       return;
/*     */     }
/* 586 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 587 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 588 */       if (state == null) {
/* 589 */         return;
/*     */       }
/* 591 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 592 */         (!state.includeNumber(v))) {
/* 593 */         return;
/*     */       }
/*     */       
/* 596 */       _checkParentPath();
/*     */     }
/* 598 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(BigDecimal v)
/*     */     throws IOException
/*     */   {
/* 604 */     if (this._itemFilter == null) {
/* 605 */       return;
/*     */     }
/* 607 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 608 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 609 */       if (state == null) {
/* 610 */         return;
/*     */       }
/* 612 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 613 */         (!state.includeNumber(v))) {
/* 614 */         return;
/*     */       }
/*     */       
/* 617 */       _checkParentPath();
/*     */     }
/* 619 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(String encodedValue)
/*     */     throws IOException, UnsupportedOperationException
/*     */   {
/* 625 */     if (this._itemFilter == null) {
/* 626 */       return;
/*     */     }
/* 628 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 629 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 630 */       if (state == null) {
/* 631 */         return;
/*     */       }
/* 633 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 634 */         (!state.includeRawValue())) {
/* 635 */         return;
/*     */       }
/*     */       
/* 638 */       _checkParentPath();
/*     */     }
/* 640 */     this.delegate.writeNumber(encodedValue);
/*     */   }
/*     */   
/*     */   public void writeBoolean(boolean v)
/*     */     throws IOException
/*     */   {
/* 646 */     if (this._itemFilter == null) {
/* 647 */       return;
/*     */     }
/* 649 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 650 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 651 */       if (state == null) {
/* 652 */         return;
/*     */       }
/* 654 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 655 */         (!state.includeBoolean(v))) {
/* 656 */         return;
/*     */       }
/*     */       
/* 659 */       _checkParentPath();
/*     */     }
/* 661 */     this.delegate.writeBoolean(v);
/*     */   }
/*     */   
/*     */   public void writeNull()
/*     */     throws IOException
/*     */   {
/* 667 */     if (this._itemFilter == null) {
/* 668 */       return;
/*     */     }
/* 670 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/* 671 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/* 672 */       if (state == null) {
/* 673 */         return;
/*     */       }
/* 675 */       if ((state != TokenFilter.INCLUDE_ALL) && 
/* 676 */         (!state.includeNull())) {
/* 677 */         return;
/*     */       }
/*     */       
/* 680 */       _checkParentPath();
/*     */     }
/* 682 */     this.delegate.writeNull();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeOmittedField(String fieldName)
/*     */     throws IOException
/*     */   {
/* 694 */     if (this._itemFilter != null) {
/* 695 */       this.delegate.writeOmittedField(fieldName);
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
/*     */   public void writeObjectId(Object id)
/*     */     throws IOException
/*     */   {
/* 710 */     if (this._itemFilter != null) {
/* 711 */       this.delegate.writeObjectId(id);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeObjectRef(Object id) throws IOException
/*     */   {
/* 717 */     if (this._itemFilter != null) {
/* 718 */       this.delegate.writeObjectRef(id);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeTypeId(Object id) throws IOException
/*     */   {
/* 724 */     if (this._itemFilter != null) {
/* 725 */       this.delegate.writeTypeId(id);
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
/*     */   protected void _checkParentPath()
/*     */     throws IOException
/*     */   {
/* 804 */     this._matchCount += 1;
/*     */     
/* 806 */     if (this._includePath) {
/* 807 */       this._filterContext.writePath(this.delegate);
/*     */     }
/*     */     
/* 810 */     if (!this._allowMultipleMatches)
/*     */     {
/* 812 */       this._filterContext.skipParentChecks();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _checkPropertyParentPath()
/*     */     throws IOException
/*     */   {
/* 823 */     this._matchCount += 1;
/* 824 */     if (this._includePath) {
/* 825 */       this._filterContext.writePath(this.delegate);
/* 826 */     } else if (this._includeImmediateParent)
/*     */     {
/*     */ 
/* 829 */       this._filterContext.writeImmediatePath(this.delegate);
/*     */     }
/*     */     
/*     */ 
/* 833 */     if (!this._allowMultipleMatches)
/*     */     {
/* 835 */       this._filterContext.skipParentChecks();
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean _checkBinaryWrite() throws IOException
/*     */   {
/* 841 */     if (this._itemFilter == null) {
/* 842 */       return false;
/*     */     }
/* 844 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 845 */       return true;
/*     */     }
/* 847 */     if (this._itemFilter.includeBinary()) {
/* 848 */       _checkParentPath();
/* 849 */       return true;
/*     */     }
/* 851 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean _checkRawValueWrite() throws IOException
/*     */   {
/* 856 */     if (this._itemFilter == null) {
/* 857 */       return false;
/*     */     }
/* 859 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 860 */       return true;
/*     */     }
/* 862 */     if (this._itemFilter.includeRawValue()) {
/* 863 */       _checkParentPath();
/* 864 */       return true;
/*     */     }
/* 866 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\filter\FilteringGeneratorDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */