/*     */ package com.facebook.presto.jdbc.internal.jackson.core.filter;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonStreamContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.util.JsonParserDelegate;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ public class FilteringParserDelegate
/*     */   extends JsonParserDelegate
/*     */ {
/*     */   protected TokenFilter rootFilter;
/*     */   protected boolean _allowMultipleMatches;
/*     */   protected boolean _includePath;
/*     */   @Deprecated
/*     */   protected boolean _includeImmediateParent;
/*     */   protected JsonToken _currToken;
/*     */   protected JsonToken _lastClearedToken;
/*     */   protected TokenFilterContext _headContext;
/*     */   protected TokenFilterContext _exposedContext;
/*     */   protected TokenFilter _itemFilter;
/*     */   protected int _matchCount;
/*     */   
/*     */   public FilteringParserDelegate(JsonParser p, TokenFilter f, boolean includePath, boolean allowMultipleMatches)
/*     */   {
/* 117 */     super(p);
/* 118 */     this.rootFilter = f;
/*     */     
/* 120 */     this._itemFilter = f;
/* 121 */     this._headContext = TokenFilterContext.createRootContext(f);
/* 122 */     this._includePath = includePath;
/* 123 */     this._allowMultipleMatches = allowMultipleMatches;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TokenFilter getFilter()
/*     */   {
/* 132 */     return this.rootFilter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMatchCount()
/*     */   {
/* 139 */     return this._matchCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 148 */   public JsonToken getCurrentToken() { return this._currToken; }
/* 149 */   public JsonToken currentToken() { return this._currToken; }
/*     */   
/*     */   public final int getCurrentTokenId() {
/* 152 */     JsonToken t = this._currToken;
/* 153 */     return t == null ? 0 : t.id();
/*     */   }
/*     */   
/* 156 */   public final int currentTokenId() { JsonToken t = this._currToken;
/* 157 */     return t == null ? 0 : t.id();
/*     */   }
/*     */   
/* 160 */   public boolean hasCurrentToken() { return this._currToken != null; }
/*     */   
/* 162 */   public boolean hasTokenId(int id) { JsonToken t = this._currToken;
/* 163 */     if (t == null) {
/* 164 */       return 0 == id;
/*     */     }
/* 166 */     return t.id() == id;
/*     */   }
/*     */   
/*     */   public final boolean hasToken(JsonToken t) {
/* 170 */     return this._currToken == t;
/*     */   }
/*     */   
/* 173 */   public boolean isExpectedStartArrayToken() { return this._currToken == JsonToken.START_ARRAY; }
/* 174 */   public boolean isExpectedStartObjectToken() { return this._currToken == JsonToken.START_OBJECT; }
/*     */   
/* 176 */   public JsonLocation getCurrentLocation() { return this.delegate.getCurrentLocation(); }
/*     */   
/*     */   public JsonStreamContext getParsingContext()
/*     */   {
/* 180 */     return _filterContext();
/*     */   }
/*     */   
/*     */   public String getCurrentName()
/*     */     throws IOException
/*     */   {
/* 186 */     JsonStreamContext ctxt = _filterContext();
/* 187 */     if ((this._currToken == JsonToken.START_OBJECT) || (this._currToken == JsonToken.START_ARRAY)) {
/* 188 */       JsonStreamContext parent = ctxt.getParent();
/* 189 */       return parent == null ? null : parent.getCurrentName();
/*     */     }
/* 191 */     return ctxt.getCurrentName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearCurrentToken()
/*     */   {
/* 202 */     if (this._currToken != null) {
/* 203 */       this._lastClearedToken = this._currToken;
/* 204 */       this._currToken = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public JsonToken getLastClearedToken() {
/* 209 */     return this._lastClearedToken;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void overrideCurrentName(String name)
/*     */   {
/* 217 */     throw new UnsupportedOperationException("Can not currently override name during filtering read");
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
/*     */   public JsonToken nextToken()
/*     */     throws IOException
/*     */   {
/* 232 */     if ((!this._allowMultipleMatches) && (this._currToken != null) && (this._exposedContext == null))
/*     */     {
/* 234 */       if ((this._currToken.isStructEnd()) && (this._headContext.isStartHandled())) {
/* 235 */         return this._currToken = null;
/*     */       }
/*     */       
/*     */ 
/* 239 */       if ((this._currToken.isScalarValue()) && (!this._headContext.isStartHandled()) && (!this._includePath) && (this._itemFilter == TokenFilter.INCLUDE_ALL))
/*     */       {
/* 241 */         return this._currToken = null;
/*     */       }
/*     */     }
/*     */     
/* 245 */     TokenFilterContext ctxt = this._exposedContext;
/*     */     
/* 247 */     if (ctxt != null) {
/*     */       for (;;) {
/* 249 */         JsonToken t = ctxt.nextTokenToRead();
/* 250 */         if (t != null) {
/* 251 */           this._currToken = t;
/* 252 */           return t;
/*     */         }
/*     */         
/* 255 */         if (ctxt == this._headContext) {
/* 256 */           this._exposedContext = null;
/* 257 */           if (!ctxt.inArray()) break;
/* 258 */           t = this.delegate.getCurrentToken();
/*     */           
/*     */ 
/* 261 */           this._currToken = t;
/* 262 */           return t;
/*     */         }
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
/* 277 */         ctxt = this._headContext.findChildOf(ctxt);
/* 278 */         this._exposedContext = ctxt;
/* 279 */         if (ctxt == null) {
/* 280 */           throw _constructError("Unexpected problem: chain of filtered context broken");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 286 */     JsonToken t = this.delegate.nextToken();
/* 287 */     if (t == null)
/*     */     {
/* 289 */       return this._currToken = t;
/*     */     }
/*     */     
/*     */ 
/*     */     TokenFilter f;
/*     */     
/* 295 */     switch (t.id()) {
/*     */     case 3: 
/* 297 */       f = this._itemFilter;
/* 298 */       if (f == TokenFilter.INCLUDE_ALL) {
/* 299 */         this._headContext = this._headContext.createChildArrayContext(f, true);
/* 300 */         return this._currToken = t;
/*     */       }
/* 302 */       if (f == null) {
/* 303 */         this.delegate.skipChildren();
/*     */       }
/*     */       else
/*     */       {
/* 307 */         f = this._headContext.checkValue(f);
/* 308 */         if (f == null) {
/* 309 */           this.delegate.skipChildren();
/*     */         }
/*     */         else {
/* 312 */           if (f != TokenFilter.INCLUDE_ALL) {
/* 313 */             f = f.filterStartArray();
/*     */           }
/* 315 */           this._itemFilter = f;
/* 316 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 317 */             this._headContext = this._headContext.createChildArrayContext(f, true);
/* 318 */             return this._currToken = t;
/*     */           }
/* 320 */           this._headContext = this._headContext.createChildArrayContext(f, false);
/*     */           
/*     */ 
/* 323 */           if (this._includePath) {
/* 324 */             t = _nextTokenWithBuffering(this._headContext);
/* 325 */             if (t != null) {
/* 326 */               this._currToken = t;
/* 327 */               return t;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       break;
/* 333 */     case 1:  f = this._itemFilter;
/* 334 */       if (f == TokenFilter.INCLUDE_ALL) {
/* 335 */         this._headContext = this._headContext.createChildObjectContext(f, true);
/* 336 */         return this._currToken = t;
/*     */       }
/* 338 */       if (f == null) {
/* 339 */         this.delegate.skipChildren();
/*     */       }
/*     */       else
/*     */       {
/* 343 */         f = this._headContext.checkValue(f);
/* 344 */         if (f == null) {
/* 345 */           this.delegate.skipChildren();
/*     */         }
/*     */         else {
/* 348 */           if (f != TokenFilter.INCLUDE_ALL) {
/* 349 */             f = f.filterStartObject();
/*     */           }
/* 351 */           this._itemFilter = f;
/* 352 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 353 */             this._headContext = this._headContext.createChildObjectContext(f, true);
/* 354 */             return this._currToken = t;
/*     */           }
/* 356 */           this._headContext = this._headContext.createChildObjectContext(f, false);
/*     */           
/* 358 */           if (this._includePath) {
/* 359 */             t = _nextTokenWithBuffering(this._headContext);
/* 360 */             if (t != null) {
/* 361 */               this._currToken = t;
/* 362 */               return t;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     case 2: 
/*     */     case 4: 
/* 372 */       boolean returnEnd = this._headContext.isStartHandled();
/* 373 */       f = this._headContext.getFilter();
/* 374 */       if ((f != null) && (f != TokenFilter.INCLUDE_ALL)) {
/* 375 */         f.filterFinishArray();
/*     */       }
/* 377 */       this._headContext = this._headContext.getParent();
/* 378 */       this._itemFilter = this._headContext.getFilter();
/* 379 */       if (returnEnd) {
/* 380 */         return this._currToken = t;
/*     */       }
/*     */       
/* 383 */       break;
/*     */     
/*     */ 
/*     */     case 5: 
/* 387 */       String name = this.delegate.getCurrentName();
/*     */       
/* 389 */       f = this._headContext.setFieldName(name);
/* 390 */       if (f == TokenFilter.INCLUDE_ALL) {
/* 391 */         this._itemFilter = f;
/* 392 */         if (!this._includePath)
/*     */         {
/*     */ 
/* 395 */           if ((this._includeImmediateParent) && (!this._headContext.isStartHandled())) {
/* 396 */             t = this._headContext.nextTokenToRead();
/* 397 */             this._exposedContext = this._headContext;
/*     */           }
/*     */         }
/* 400 */         return this._currToken = t;
/*     */       }
/* 402 */       if (f == null) {
/* 403 */         this.delegate.nextToken();
/* 404 */         this.delegate.skipChildren();
/*     */       }
/*     */       else {
/* 407 */         f = f.includeProperty(name);
/* 408 */         if (f == null) {
/* 409 */           this.delegate.nextToken();
/* 410 */           this.delegate.skipChildren();
/*     */         }
/*     */         else {
/* 413 */           this._itemFilter = f;
/* 414 */           if ((f == TokenFilter.INCLUDE_ALL) && 
/* 415 */             (this._includePath)) {
/* 416 */             return this._currToken = t;
/*     */           }
/*     */           
/* 419 */           if (this._includePath) {
/* 420 */             t = _nextTokenWithBuffering(this._headContext);
/* 421 */             if (t != null) {
/* 422 */               this._currToken = t;
/* 423 */               return t;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       break;
/*     */     default: 
/* 430 */       f = this._itemFilter;
/* 431 */       if (f == TokenFilter.INCLUDE_ALL) {
/* 432 */         return this._currToken = t;
/*     */       }
/* 434 */       if (f != null) {
/* 435 */         f = this._headContext.checkValue(f);
/* 436 */         if ((f == TokenFilter.INCLUDE_ALL) || ((f != null) && (f.includeValue(this.delegate))))
/*     */         {
/* 438 */           return this._currToken = t;
/*     */         }
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/*     */     
/* 446 */     return _nextToken2();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final JsonToken _nextToken2()
/*     */     throws IOException
/*     */   {
/*     */     for (;;)
/*     */     {
/* 459 */       JsonToken t = this.delegate.nextToken();
/* 460 */       if (t == null) {
/* 461 */         return this._currToken = t;
/*     */       }
/*     */       
/*     */       TokenFilter f;
/* 465 */       switch (t.id()) {
/*     */       case 3: 
/* 467 */         f = this._itemFilter;
/* 468 */         if (f == TokenFilter.INCLUDE_ALL) {
/* 469 */           this._headContext = this._headContext.createChildArrayContext(f, true);
/* 470 */           return this._currToken = t;
/*     */         }
/* 472 */         if (f == null) {
/* 473 */           this.delegate.skipChildren();
/*     */         }
/*     */         else
/*     */         {
/* 477 */           f = this._headContext.checkValue(f);
/* 478 */           if (f == null) {
/* 479 */             this.delegate.skipChildren();
/*     */           }
/*     */           else {
/* 482 */             if (f != TokenFilter.INCLUDE_ALL) {
/* 483 */               f = f.filterStartArray();
/*     */             }
/* 485 */             this._itemFilter = f;
/* 486 */             if (f == TokenFilter.INCLUDE_ALL) {
/* 487 */               this._headContext = this._headContext.createChildArrayContext(f, true);
/* 488 */               return this._currToken = t;
/*     */             }
/* 490 */             this._headContext = this._headContext.createChildArrayContext(f, false);
/*     */             
/* 492 */             if (this._includePath) {
/* 493 */               t = _nextTokenWithBuffering(this._headContext);
/* 494 */               if (t != null) {
/* 495 */                 this._currToken = t;
/* 496 */                 return t;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         break;
/* 502 */       case 1:  f = this._itemFilter;
/* 503 */         if (f == TokenFilter.INCLUDE_ALL) {
/* 504 */           this._headContext = this._headContext.createChildObjectContext(f, true);
/* 505 */           return this._currToken = t;
/*     */         }
/* 507 */         if (f == null) {
/* 508 */           this.delegate.skipChildren();
/*     */         }
/*     */         else
/*     */         {
/* 512 */           f = this._headContext.checkValue(f);
/* 513 */           if (f == null) {
/* 514 */             this.delegate.skipChildren();
/*     */           }
/*     */           else {
/* 517 */             if (f != TokenFilter.INCLUDE_ALL) {
/* 518 */               f = f.filterStartObject();
/*     */             }
/* 520 */             this._itemFilter = f;
/* 521 */             if (f == TokenFilter.INCLUDE_ALL) {
/* 522 */               this._headContext = this._headContext.createChildObjectContext(f, true);
/* 523 */               return this._currToken = t;
/*     */             }
/* 525 */             this._headContext = this._headContext.createChildObjectContext(f, false);
/* 526 */             if (this._includePath) {
/* 527 */               t = _nextTokenWithBuffering(this._headContext);
/* 528 */               if (t != null) {
/* 529 */                 this._currToken = t;
/* 530 */                 return t;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         break;
/*     */       case 2: 
/*     */       case 4: 
/* 538 */         boolean returnEnd = this._headContext.isStartHandled();
/* 539 */         f = this._headContext.getFilter();
/* 540 */         if ((f != null) && (f != TokenFilter.INCLUDE_ALL)) {
/* 541 */           f.filterFinishArray();
/*     */         }
/* 543 */         this._headContext = this._headContext.getParent();
/* 544 */         this._itemFilter = this._headContext.getFilter();
/* 545 */         if (returnEnd) {
/* 546 */           return this._currToken = t;
/*     */         }
/*     */         
/* 549 */         break;
/*     */       
/*     */ 
/*     */       case 5: 
/* 553 */         String name = this.delegate.getCurrentName();
/* 554 */         f = this._headContext.setFieldName(name);
/* 555 */         if (f == TokenFilter.INCLUDE_ALL) {
/* 556 */           this._itemFilter = f;
/* 557 */           return this._currToken = t;
/*     */         }
/* 559 */         if (f == null) {
/* 560 */           this.delegate.nextToken();
/* 561 */           this.delegate.skipChildren();
/*     */         }
/*     */         else {
/* 564 */           f = f.includeProperty(name);
/* 565 */           if (f == null) {
/* 566 */             this.delegate.nextToken();
/* 567 */             this.delegate.skipChildren();
/*     */           }
/*     */           else {
/* 570 */             this._itemFilter = f;
/* 571 */             if (f == TokenFilter.INCLUDE_ALL) {
/* 572 */               if (this._includePath) {
/* 573 */                 return this._currToken = t;
/*     */               }
/*     */               
/*     */ 
/*     */             }
/* 578 */             else if (this._includePath) {
/* 579 */               t = _nextTokenWithBuffering(this._headContext);
/* 580 */               if (t != null) {
/* 581 */                 this._currToken = t;
/* 582 */                 return t;
/*     */               }
/*     */             }
/*     */           } }
/* 586 */         break;
/*     */       
/*     */       default: 
/* 589 */         f = this._itemFilter;
/* 590 */         if (f == TokenFilter.INCLUDE_ALL) {
/* 591 */           return this._currToken = t;
/*     */         }
/* 593 */         if (f != null) {
/* 594 */           f = this._headContext.checkValue(f);
/* 595 */           if ((f == TokenFilter.INCLUDE_ALL) || ((f != null) && (f.includeValue(this.delegate))))
/*     */           {
/* 597 */             return this._currToken = t;
/*     */           }
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected final JsonToken _nextTokenWithBuffering(TokenFilterContext buffRoot) throws IOException
/*     */   {
/*     */     TokenFilter f;
/*     */     do
/*     */     {
/*     */       do
/*     */       {
/*     */         for (;;)
/*     */         {
/* 614 */           JsonToken t = this.delegate.nextToken();
/* 615 */           if (t == null) {
/* 616 */             return t;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 624 */           switch (t.id()) {
/*     */           case 3: 
/* 626 */             f = this._headContext.checkValue(this._itemFilter);
/* 627 */             if (f == null) {
/* 628 */               this.delegate.skipChildren();
/*     */             }
/*     */             else {
/* 631 */               if (f != TokenFilter.INCLUDE_ALL) {
/* 632 */                 f = f.filterStartArray();
/*     */               }
/* 634 */               this._itemFilter = f;
/* 635 */               if (f == TokenFilter.INCLUDE_ALL) {
/* 636 */                 this._headContext = this._headContext.createChildArrayContext(f, true);
/* 637 */                 return _nextBuffered(buffRoot);
/*     */               }
/* 639 */               this._headContext = this._headContext.createChildArrayContext(f, false); }
/* 640 */             break;
/*     */           
/*     */           case 1: 
/* 643 */             f = this._itemFilter;
/* 644 */             if (f == TokenFilter.INCLUDE_ALL) {
/* 645 */               this._headContext = this._headContext.createChildObjectContext(f, true);
/* 646 */               return t;
/*     */             }
/* 648 */             if (f == null) {
/* 649 */               this.delegate.skipChildren();
/*     */             }
/*     */             else
/*     */             {
/* 653 */               f = this._headContext.checkValue(f);
/* 654 */               if (f == null) {
/* 655 */                 this.delegate.skipChildren();
/*     */               }
/*     */               else {
/* 658 */                 if (f != TokenFilter.INCLUDE_ALL) {
/* 659 */                   f = f.filterStartObject();
/*     */                 }
/* 661 */                 this._itemFilter = f;
/* 662 */                 if (f == TokenFilter.INCLUDE_ALL) {
/* 663 */                   this._headContext = this._headContext.createChildObjectContext(f, true);
/* 664 */                   return _nextBuffered(buffRoot);
/*     */                 }
/* 666 */                 this._headContext = this._headContext.createChildObjectContext(f, false); } }
/* 667 */             break;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           case 2: 
/*     */           case 4: 
/* 674 */             f = this._headContext.getFilter();
/* 675 */             if ((f != null) && (f != TokenFilter.INCLUDE_ALL)) {
/* 676 */               f.filterFinishArray();
/*     */             }
/* 678 */             boolean gotEnd = this._headContext == buffRoot;
/* 679 */             boolean returnEnd = (gotEnd) && (this._headContext.isStartHandled());
/*     */             
/* 681 */             this._headContext = this._headContext.getParent();
/* 682 */             this._itemFilter = this._headContext.getFilter();
/*     */             
/* 684 */             if (returnEnd) {
/* 685 */               return t;
/*     */             }
/*     */             
/* 688 */             if ((gotEnd) || (this._headContext == buffRoot)) {
/* 689 */               return null;
/*     */             }
/*     */             
/* 692 */             break;
/*     */           
/*     */ 
/*     */           case 5: 
/* 696 */             String name = this.delegate.getCurrentName();
/* 697 */             f = this._headContext.setFieldName(name);
/* 698 */             if (f == TokenFilter.INCLUDE_ALL) {
/* 699 */               this._itemFilter = f;
/* 700 */               return _nextBuffered(buffRoot);
/*     */             }
/* 702 */             if (f == null) {
/* 703 */               this.delegate.nextToken();
/* 704 */               this.delegate.skipChildren();
/*     */             }
/*     */             else {
/* 707 */               f = f.includeProperty(name);
/* 708 */               if (f == null) {
/* 709 */                 this.delegate.nextToken();
/* 710 */                 this.delegate.skipChildren();
/*     */               }
/*     */               else {
/* 713 */                 this._itemFilter = f;
/* 714 */                 if (f == TokenFilter.INCLUDE_ALL)
/* 715 */                   return _nextBuffered(buffRoot);
/*     */               }
/*     */             }
/*     */             break;
/*     */           }
/*     */         }
/* 721 */         f = this._itemFilter;
/* 722 */         if (f == TokenFilter.INCLUDE_ALL) {
/* 723 */           return _nextBuffered(buffRoot);
/*     */         }
/* 725 */       } while (f == null);
/* 726 */       f = this._headContext.checkValue(f);
/* 727 */     } while ((f != TokenFilter.INCLUDE_ALL) && ((f == null) || (!f.includeValue(this.delegate))));
/*     */     
/* 729 */     return _nextBuffered(buffRoot);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private JsonToken _nextBuffered(TokenFilterContext buffRoot)
/*     */     throws IOException
/*     */   {
/* 740 */     this._exposedContext = buffRoot;
/* 741 */     TokenFilterContext ctxt = buffRoot;
/* 742 */     JsonToken t = ctxt.nextTokenToRead();
/* 743 */     if (t != null) {
/* 744 */       return t;
/*     */     }
/*     */     do
/*     */     {
/* 748 */       if (ctxt == this._headContext) {
/* 749 */         throw _constructError("Internal error: failed to locate expected buffered tokens");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 756 */       ctxt = this._exposedContext.findChildOf(ctxt);
/* 757 */       this._exposedContext = ctxt;
/* 758 */       if (ctxt == null) {
/* 759 */         throw _constructError("Unexpected problem: chain of filtered context broken");
/*     */       }
/* 761 */       t = this._exposedContext.nextTokenToRead();
/* 762 */     } while (t == null);
/* 763 */     return t;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonToken nextValue()
/*     */     throws IOException
/*     */   {
/* 771 */     JsonToken t = nextToken();
/* 772 */     if (t == JsonToken.FIELD_NAME) {
/* 773 */       t = nextToken();
/*     */     }
/* 775 */     return t;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonParser skipChildren()
/*     */     throws IOException
/*     */   {
/* 786 */     if ((this._currToken != JsonToken.START_OBJECT) && (this._currToken != JsonToken.START_ARRAY))
/*     */     {
/* 788 */       return this;
/*     */     }
/* 790 */     int open = 1;
/*     */     
/*     */ 
/*     */     for (;;)
/*     */     {
/* 795 */       JsonToken t = nextToken();
/* 796 */       if (t == null) {
/* 797 */         return this;
/*     */       }
/* 799 */       if (t.isStructStart()) {
/* 800 */         open++;
/* 801 */       } else if (t.isStructEnd()) {
/* 802 */         open--; if (open == 0) {
/* 803 */           return this;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 815 */   public String getText()
/* 815 */     throws IOException { return this.delegate.getText(); }
/* 816 */   public boolean hasTextCharacters() { return this.delegate.hasTextCharacters(); }
/* 817 */   public char[] getTextCharacters() throws IOException { return this.delegate.getTextCharacters(); }
/* 818 */   public int getTextLength() throws IOException { return this.delegate.getTextLength(); }
/* 819 */   public int getTextOffset() throws IOException { return this.delegate.getTextOffset(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getBigIntegerValue()
/*     */     throws IOException
/*     */   {
/* 828 */     return this.delegate.getBigIntegerValue();
/*     */   }
/*     */   
/* 831 */   public boolean getBooleanValue() throws IOException { return this.delegate.getBooleanValue(); }
/*     */   
/*     */   public byte getByteValue() throws IOException {
/* 834 */     return this.delegate.getByteValue();
/*     */   }
/*     */   
/* 837 */   public short getShortValue() throws IOException { return this.delegate.getShortValue(); }
/*     */   
/*     */   public BigDecimal getDecimalValue() throws IOException {
/* 840 */     return this.delegate.getDecimalValue();
/*     */   }
/*     */   
/* 843 */   public double getDoubleValue() throws IOException { return this.delegate.getDoubleValue(); }
/*     */   
/*     */   public float getFloatValue() throws IOException {
/* 846 */     return this.delegate.getFloatValue();
/*     */   }
/*     */   
/* 849 */   public int getIntValue() throws IOException { return this.delegate.getIntValue(); }
/*     */   
/*     */   public long getLongValue() throws IOException {
/* 852 */     return this.delegate.getLongValue();
/*     */   }
/*     */   
/* 855 */   public JsonParser.NumberType getNumberType() throws IOException { return this.delegate.getNumberType(); }
/*     */   
/*     */   public Number getNumberValue() throws IOException {
/* 858 */     return this.delegate.getNumberValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 866 */   public int getValueAsInt()
/* 866 */     throws IOException { return this.delegate.getValueAsInt(); }
/* 867 */   public int getValueAsInt(int defaultValue) throws IOException { return this.delegate.getValueAsInt(defaultValue); }
/* 868 */   public long getValueAsLong() throws IOException { return this.delegate.getValueAsLong(); }
/* 869 */   public long getValueAsLong(long defaultValue) throws IOException { return this.delegate.getValueAsLong(defaultValue); }
/* 870 */   public double getValueAsDouble() throws IOException { return this.delegate.getValueAsDouble(); }
/* 871 */   public double getValueAsDouble(double defaultValue) throws IOException { return this.delegate.getValueAsDouble(defaultValue); }
/* 872 */   public boolean getValueAsBoolean() throws IOException { return this.delegate.getValueAsBoolean(); }
/* 873 */   public boolean getValueAsBoolean(boolean defaultValue) throws IOException { return this.delegate.getValueAsBoolean(defaultValue); }
/* 874 */   public String getValueAsString() throws IOException { return this.delegate.getValueAsString(); }
/* 875 */   public String getValueAsString(String defaultValue) throws IOException { return this.delegate.getValueAsString(defaultValue); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 883 */   public Object getEmbeddedObject()
/* 883 */     throws IOException { return this.delegate.getEmbeddedObject(); }
/* 884 */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException { return this.delegate.getBinaryValue(b64variant); }
/* 885 */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException { return this.delegate.readBinaryValue(b64variant, out); }
/* 886 */   public JsonLocation getTokenLocation() { return this.delegate.getTokenLocation(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonStreamContext _filterContext()
/*     */   {
/* 895 */     if (this._exposedContext != null) {
/* 896 */       return this._exposedContext;
/*     */     }
/* 898 */     return this._headContext;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\filter\FilteringParserDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */