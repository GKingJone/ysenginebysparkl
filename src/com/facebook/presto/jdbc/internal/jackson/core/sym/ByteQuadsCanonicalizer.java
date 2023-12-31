/*      */ package com.facebook.presto.jdbc.internal.jackson.core.sym;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonFactory.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.InternCache;
/*      */ import java.util.Arrays;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ByteQuadsCanonicalizer
/*      */ {
/*      */   private static final int DEFAULT_T_SIZE = 64;
/*      */   private static final int MAX_T_SIZE = 65536;
/*      */   static final int MIN_HASH_SIZE = 16;
/*      */   static final int MAX_ENTRIES_FOR_REUSE = 6000;
/*      */   protected final ByteQuadsCanonicalizer _parent;
/*      */   protected final AtomicReference<TableInfo> _tableInfo;
/*      */   private final int _seed;
/*      */   protected boolean _intern;
/*      */   protected final boolean _failOnDoS;
/*      */   protected int[] _hashArea;
/*      */   protected int _hashSize;
/*      */   protected int _secondaryStart;
/*      */   protected int _tertiaryStart;
/*      */   protected int _tertiaryShift;
/*      */   protected int _count;
/*      */   protected String[] _names;
/*      */   protected int _spilloverEnd;
/*      */   protected int _longNameOffset;
/*      */   private transient boolean _needRehash;
/*      */   private boolean _hashShared;
/*      */   private static final int MULT = 33;
/*      */   private static final int MULT2 = 65599;
/*      */   private static final int MULT3 = 31;
/*      */   
/*      */   private ByteQuadsCanonicalizer(int sz, boolean intern, int seed, boolean failOnDoS)
/*      */   {
/*  223 */     this._parent = null;
/*  224 */     this._seed = seed;
/*  225 */     this._intern = intern;
/*  226 */     this._failOnDoS = failOnDoS;
/*      */     
/*  228 */     if (sz < 16) {
/*  229 */       sz = 16;
/*      */ 
/*      */ 
/*      */     }
/*  233 */     else if ((sz & sz - 1) != 0) {
/*  234 */       int curr = 16;
/*  235 */       while (curr < sz) {
/*  236 */         curr += curr;
/*      */       }
/*  238 */       sz = curr;
/*      */     }
/*      */     
/*  241 */     this._tableInfo = new AtomicReference(TableInfo.createInitial(sz));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ByteQuadsCanonicalizer(ByteQuadsCanonicalizer parent, boolean intern, int seed, boolean failOnDoS, TableInfo state)
/*      */   {
/*  250 */     this._parent = parent;
/*  251 */     this._seed = seed;
/*  252 */     this._intern = intern;
/*  253 */     this._failOnDoS = failOnDoS;
/*  254 */     this._tableInfo = null;
/*      */     
/*      */ 
/*  257 */     this._count = state.count;
/*  258 */     this._hashSize = state.size;
/*  259 */     this._secondaryStart = (this._hashSize << 2);
/*  260 */     this._tertiaryStart = (this._secondaryStart + (this._secondaryStart >> 1));
/*  261 */     this._tertiaryShift = state.tertiaryShift;
/*      */     
/*  263 */     this._hashArea = state.mainHash;
/*  264 */     this._names = state.names;
/*      */     
/*  266 */     this._spilloverEnd = state.spilloverEnd;
/*  267 */     this._longNameOffset = state.longNameOffset;
/*      */     
/*      */ 
/*  270 */     this._needRehash = false;
/*  271 */     this._hashShared = true;
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
/*      */   public static ByteQuadsCanonicalizer createRoot()
/*      */   {
/*  288 */     long now = System.currentTimeMillis();
/*      */     
/*  290 */     int seed = (int)now + (int)(now >>> 32) | 0x1;
/*  291 */     return createRoot(seed);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static ByteQuadsCanonicalizer createRoot(int seed)
/*      */   {
/*  299 */     return new ByteQuadsCanonicalizer(64, true, seed, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ByteQuadsCanonicalizer makeChild(int flags)
/*      */   {
/*  307 */     return new ByteQuadsCanonicalizer(this, JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(flags), this._seed, JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(flags), (TableInfo)this._tableInfo.get());
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
/*      */   public void release()
/*      */   {
/*  324 */     if ((this._parent != null) && (maybeDirty())) {
/*  325 */       this._parent.mergeChild(new TableInfo(this));
/*      */       
/*      */ 
/*      */ 
/*  329 */       this._hashShared = true;
/*      */     }
/*      */   }
/*      */   
/*      */   private void mergeChild(TableInfo childState)
/*      */   {
/*  335 */     int childCount = childState.count;
/*  336 */     TableInfo currState = (TableInfo)this._tableInfo.get();
/*      */     
/*      */ 
/*      */ 
/*  340 */     if (childCount == currState.count) {
/*  341 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  348 */     if (childCount > 6000)
/*      */     {
/*  350 */       childState = TableInfo.createInitial(64);
/*      */     }
/*  352 */     this._tableInfo.compareAndSet(currState, childState);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int size()
/*      */   {
/*  363 */     if (this._tableInfo != null) {
/*  364 */       return ((TableInfo)this._tableInfo.get()).count;
/*      */     }
/*      */     
/*  367 */     return this._count;
/*      */   }
/*      */   
/*      */ 
/*      */   public int bucketCount()
/*      */   {
/*  373 */     return this._hashSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  380 */   public boolean maybeDirty() { return !this._hashShared; }
/*      */   
/*  382 */   public int hashSeed() { return this._seed; }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int primaryCount()
/*      */   {
/*  391 */     int count = 0;
/*  392 */     int offset = 3; for (int end = this._secondaryStart; offset < end; offset += 4) {
/*  393 */       if (this._hashArea[offset] != 0) {
/*  394 */         count++;
/*      */       }
/*      */     }
/*  397 */     return count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int secondaryCount()
/*      */   {
/*  405 */     int count = 0;
/*  406 */     int offset = this._secondaryStart + 3;
/*  407 */     for (int end = this._tertiaryStart; offset < end; offset += 4) {
/*  408 */       if (this._hashArea[offset] != 0) {
/*  409 */         count++;
/*      */       }
/*      */     }
/*  412 */     return count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int tertiaryCount()
/*      */   {
/*  420 */     int count = 0;
/*  421 */     int offset = this._tertiaryStart + 3;
/*  422 */     for (int end = offset + this._hashSize; offset < end; offset += 4) {
/*  423 */       if (this._hashArea[offset] != 0) {
/*  424 */         count++;
/*      */       }
/*      */     }
/*  427 */     return count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int spilloverCount()
/*      */   {
/*  436 */     return this._spilloverEnd - _spilloverStart() >> 2;
/*      */   }
/*      */   
/*      */   public int totalCount()
/*      */   {
/*  441 */     int count = 0;
/*  442 */     int offset = 3; for (int end = this._hashSize << 3; offset < end; offset += 4) {
/*  443 */       if (this._hashArea[offset] != 0) {
/*  444 */         count++;
/*      */       }
/*      */     }
/*  447 */     return count;
/*      */   }
/*      */   
/*      */   public String toString()
/*      */   {
/*  452 */     int pri = primaryCount();
/*  453 */     int sec = secondaryCount();
/*  454 */     int tert = tertiaryCount();
/*  455 */     int spill = spilloverCount();
/*  456 */     int total = totalCount();
/*  457 */     return String.format("[%s: size=%d, hashSize=%d, %d/%d/%d/%d pri/sec/ter/spill (=%s), total:%d]", new Object[] { getClass().getName(), Integer.valueOf(this._count), Integer.valueOf(this._hashSize), Integer.valueOf(pri), Integer.valueOf(sec), Integer.valueOf(tert), Integer.valueOf(spill), Integer.valueOf(total), Integer.valueOf(pri + sec + tert + spill), Integer.valueOf(total) });
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
/*      */   public String findName(int q1)
/*      */   {
/*  470 */     int offset = _calcOffset(calcHash(q1));
/*      */     
/*  472 */     int[] hashArea = this._hashArea;
/*      */     
/*  474 */     int len = hashArea[(offset + 3)];
/*      */     
/*  476 */     if (len == 1) {
/*  477 */       if (hashArea[offset] == q1) {
/*  478 */         return this._names[(offset >> 2)];
/*      */       }
/*  480 */     } else if (len == 0) {
/*  481 */       return null;
/*      */     }
/*      */     
/*  484 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  486 */     len = hashArea[(offset2 + 3)];
/*      */     
/*  488 */     if (len == 1) {
/*  489 */       if (hashArea[offset2] == q1) {
/*  490 */         return this._names[(offset2 >> 2)];
/*      */       }
/*  492 */     } else if (len == 0) {
/*  493 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  497 */     return _findSecondary(offset, q1);
/*      */   }
/*      */   
/*      */   public String findName(int q1, int q2)
/*      */   {
/*  502 */     int offset = _calcOffset(calcHash(q1, q2));
/*      */     
/*  504 */     int[] hashArea = this._hashArea;
/*      */     
/*  506 */     int len = hashArea[(offset + 3)];
/*      */     
/*  508 */     if (len == 2) {
/*  509 */       if ((q1 == hashArea[offset]) && (q2 == hashArea[(offset + 1)])) {
/*  510 */         return this._names[(offset >> 2)];
/*      */       }
/*  512 */     } else if (len == 0) {
/*  513 */       return null;
/*      */     }
/*      */     
/*  516 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  518 */     len = hashArea[(offset2 + 3)];
/*      */     
/*  520 */     if (len == 2) {
/*  521 */       if ((q1 == hashArea[offset2]) && (q2 == hashArea[(offset2 + 1)])) {
/*  522 */         return this._names[(offset2 >> 2)];
/*      */       }
/*  524 */     } else if (len == 0) {
/*  525 */       return null;
/*      */     }
/*  527 */     return _findSecondary(offset, q1, q2);
/*      */   }
/*      */   
/*      */   public String findName(int q1, int q2, int q3)
/*      */   {
/*  532 */     int offset = _calcOffset(calcHash(q1, q2, q3));
/*  533 */     int[] hashArea = this._hashArea;
/*  534 */     int len = hashArea[(offset + 3)];
/*      */     
/*  536 */     if (len == 3) {
/*  537 */       if ((q1 == hashArea[offset]) && (hashArea[(offset + 1)] == q2) && (hashArea[(offset + 2)] == q3)) {
/*  538 */         return this._names[(offset >> 2)];
/*      */       }
/*  540 */     } else if (len == 0) {
/*  541 */       return null;
/*      */     }
/*      */     
/*  544 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  546 */     len = hashArea[(offset2 + 3)];
/*      */     
/*  548 */     if (len == 3) {
/*  549 */       if ((q1 == hashArea[offset2]) && (hashArea[(offset2 + 1)] == q2) && (hashArea[(offset2 + 2)] == q3)) {
/*  550 */         return this._names[(offset2 >> 2)];
/*      */       }
/*  552 */     } else if (len == 0) {
/*  553 */       return null;
/*      */     }
/*  555 */     return _findSecondary(offset, q1, q2, q3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String findName(int[] q, int qlen)
/*      */   {
/*  564 */     if (qlen < 4) {
/*  565 */       if (qlen == 3) {
/*  566 */         return findName(q[0], q[1], q[2]);
/*      */       }
/*  568 */       if (qlen == 2) {
/*  569 */         return findName(q[0], q[1]);
/*      */       }
/*  571 */       return findName(q[0]);
/*      */     }
/*  573 */     int hash = calcHash(q, qlen);
/*  574 */     int offset = _calcOffset(hash);
/*      */     
/*  576 */     int[] hashArea = this._hashArea;
/*      */     
/*  578 */     int len = hashArea[(offset + 3)];
/*      */     
/*  580 */     if ((hash == hashArea[offset]) && (len == qlen))
/*      */     {
/*  582 */       if (_verifyLongName(q, qlen, hashArea[(offset + 1)])) {
/*  583 */         return this._names[(offset >> 2)];
/*      */       }
/*      */     }
/*  586 */     if (len == 0) {
/*  587 */       return null;
/*      */     }
/*      */     
/*  590 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  592 */     int len2 = hashArea[(offset2 + 3)];
/*  593 */     if ((hash == hashArea[offset2]) && (len2 == qlen) && 
/*  594 */       (_verifyLongName(q, qlen, hashArea[(offset2 + 1)]))) {
/*  595 */       return this._names[(offset2 >> 2)];
/*      */     }
/*      */     
/*  598 */     if (len == 0) {
/*  599 */       return null;
/*      */     }
/*  601 */     return _findSecondary(offset, hash, q, qlen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _calcOffset(int hash)
/*      */   {
/*  609 */     int ix = hash & this._hashSize - 1;
/*      */     
/*  611 */     return ix << 2;
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
/*      */   private String _findSecondary(int origOffset, int q1)
/*      */   {
/*  626 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  627 */     int[] hashArea = this._hashArea;
/*  628 */     int bucketSize = 1 << this._tertiaryShift;
/*  629 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  630 */       int len = hashArea[(offset + 3)];
/*  631 */       if ((q1 == hashArea[offset]) && (1 == len)) {
/*  632 */         return this._names[(offset >> 2)];
/*      */       }
/*  634 */       if (len == 0) {
/*  635 */         return null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  641 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  642 */       if ((q1 == hashArea[offset]) && (1 == hashArea[(offset + 3)])) {
/*  643 */         return this._names[(offset >> 2)];
/*      */       }
/*      */     }
/*  646 */     return null;
/*      */   }
/*      */   
/*      */   private String _findSecondary(int origOffset, int q1, int q2)
/*      */   {
/*  651 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  652 */     int[] hashArea = this._hashArea;
/*      */     
/*  654 */     int bucketSize = 1 << this._tertiaryShift;
/*  655 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  656 */       int len = hashArea[(offset + 3)];
/*  657 */       if ((q1 == hashArea[offset]) && (q2 == hashArea[(offset + 1)]) && (2 == len)) {
/*  658 */         return this._names[(offset >> 2)];
/*      */       }
/*  660 */       if (len == 0) {
/*  661 */         return null;
/*      */       }
/*      */     }
/*  664 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  665 */       if ((q1 == hashArea[offset]) && (q2 == hashArea[(offset + 1)]) && (2 == hashArea[(offset + 3)])) {
/*  666 */         return this._names[(offset >> 2)];
/*      */       }
/*      */     }
/*  669 */     return null;
/*      */   }
/*      */   
/*      */   private String _findSecondary(int origOffset, int q1, int q2, int q3)
/*      */   {
/*  674 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  675 */     int[] hashArea = this._hashArea;
/*      */     
/*  677 */     int bucketSize = 1 << this._tertiaryShift;
/*  678 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  679 */       int len = hashArea[(offset + 3)];
/*  680 */       if ((q1 == hashArea[offset]) && (q2 == hashArea[(offset + 1)]) && (q3 == hashArea[(offset + 2)]) && (3 == len)) {
/*  681 */         return this._names[(offset >> 2)];
/*      */       }
/*  683 */       if (len == 0) {
/*  684 */         return null;
/*      */       }
/*      */     }
/*  687 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  688 */       if ((q1 == hashArea[offset]) && (q2 == hashArea[(offset + 1)]) && (q3 == hashArea[(offset + 2)]) && (3 == hashArea[(offset + 3)]))
/*      */       {
/*  690 */         return this._names[(offset >> 2)];
/*      */       }
/*      */     }
/*  693 */     return null;
/*      */   }
/*      */   
/*      */   private String _findSecondary(int origOffset, int hash, int[] q, int qlen)
/*      */   {
/*  698 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  699 */     int[] hashArea = this._hashArea;
/*      */     
/*  701 */     int bucketSize = 1 << this._tertiaryShift;
/*  702 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  703 */       int len = hashArea[(offset + 3)];
/*  704 */       if ((hash == hashArea[offset]) && (qlen == len) && 
/*  705 */         (_verifyLongName(q, qlen, hashArea[(offset + 1)]))) {
/*  706 */         return this._names[(offset >> 2)];
/*      */       }
/*      */       
/*  709 */       if (len == 0) {
/*  710 */         return null;
/*      */       }
/*      */     }
/*  713 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  714 */       if ((hash == hashArea[offset]) && (qlen == hashArea[(offset + 3)]) && 
/*  715 */         (_verifyLongName(q, qlen, hashArea[(offset + 1)]))) {
/*  716 */         return this._names[(offset >> 2)];
/*      */       }
/*      */     }
/*      */     
/*  720 */     return null;
/*      */   }
/*      */   
/*      */   private boolean _verifyLongName(int[] q, int qlen, int spillOffset)
/*      */   {
/*  725 */     int[] hashArea = this._hashArea;
/*      */     
/*  727 */     int ix = 0;
/*      */     
/*  729 */     switch (qlen) {
/*      */     default: 
/*  731 */       return _verifyLongName2(q, qlen, spillOffset);
/*      */     case 8: 
/*  733 */       if (q[(ix++)] != hashArea[(spillOffset++)]) return false;
/*      */     case 7: 
/*  735 */       if (q[(ix++)] != hashArea[(spillOffset++)]) return false;
/*      */     case 6: 
/*  737 */       if (q[(ix++)] != hashArea[(spillOffset++)]) return false;
/*      */     case 5: 
/*  739 */       if (q[(ix++)] != hashArea[(spillOffset++)]) return false;
/*      */       break; }
/*  741 */     if (q[(ix++)] != hashArea[(spillOffset++)]) return false;
/*  742 */     if (q[(ix++)] != hashArea[(spillOffset++)]) return false;
/*  743 */     if (q[(ix++)] != hashArea[(spillOffset++)]) return false;
/*  744 */     if (q[(ix++)] != hashArea[(spillOffset++)]) { return false;
/*      */     }
/*  746 */     return true;
/*      */   }
/*      */   
/*      */   private boolean _verifyLongName2(int[] q, int qlen, int spillOffset)
/*      */   {
/*  751 */     int ix = 0;
/*      */     do {
/*  753 */       if (q[(ix++)] != this._hashArea[(spillOffset++)]) {
/*  754 */         return false;
/*      */       }
/*  756 */     } while (ix < qlen);
/*  757 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String addName(String name, int q1)
/*      */   {
/*  767 */     _verifySharing();
/*  768 */     if (this._intern) {
/*  769 */       name = InternCache.instance.intern(name);
/*      */     }
/*  771 */     int offset = _findOffsetForAdd(calcHash(q1));
/*  772 */     this._hashArea[offset] = q1;
/*  773 */     this._hashArea[(offset + 3)] = 1;
/*  774 */     this._names[(offset >> 2)] = name;
/*  775 */     this._count += 1;
/*  776 */     _verifyNeedForRehash();
/*  777 */     return name;
/*      */   }
/*      */   
/*      */   public String addName(String name, int q1, int q2) {
/*  781 */     _verifySharing();
/*  782 */     if (this._intern) {
/*  783 */       name = InternCache.instance.intern(name);
/*      */     }
/*  785 */     int hash = q2 == 0 ? calcHash(q1) : calcHash(q1, q2);
/*  786 */     int offset = _findOffsetForAdd(hash);
/*  787 */     this._hashArea[offset] = q1;
/*  788 */     this._hashArea[(offset + 1)] = q2;
/*  789 */     this._hashArea[(offset + 3)] = 2;
/*  790 */     this._names[(offset >> 2)] = name;
/*  791 */     this._count += 1;
/*  792 */     _verifyNeedForRehash();
/*  793 */     return name;
/*      */   }
/*      */   
/*      */   public String addName(String name, int q1, int q2, int q3) {
/*  797 */     _verifySharing();
/*  798 */     if (this._intern) {
/*  799 */       name = InternCache.instance.intern(name);
/*      */     }
/*  801 */     int offset = _findOffsetForAdd(calcHash(q1, q2, q3));
/*  802 */     this._hashArea[offset] = q1;
/*  803 */     this._hashArea[(offset + 1)] = q2;
/*  804 */     this._hashArea[(offset + 2)] = q3;
/*  805 */     this._hashArea[(offset + 3)] = 3;
/*  806 */     this._names[(offset >> 2)] = name;
/*  807 */     this._count += 1;
/*  808 */     _verifyNeedForRehash();
/*  809 */     return name;
/*      */   }
/*      */   
/*      */   public String addName(String name, int[] q, int qlen)
/*      */   {
/*  814 */     _verifySharing();
/*  815 */     if (this._intern) {
/*  816 */       name = InternCache.instance.intern(name);
/*      */     }
/*      */     
/*      */     int offset;
/*  820 */     switch (qlen)
/*      */     {
/*      */     case 1: 
/*  823 */       offset = _findOffsetForAdd(calcHash(q[0]));
/*  824 */       this._hashArea[offset] = q[0];
/*  825 */       this._hashArea[(offset + 3)] = 1;
/*      */       
/*  827 */       break;
/*      */     
/*      */     case 2: 
/*  830 */       offset = _findOffsetForAdd(calcHash(q[0], q[1]));
/*  831 */       this._hashArea[offset] = q[0];
/*  832 */       this._hashArea[(offset + 1)] = q[1];
/*  833 */       this._hashArea[(offset + 3)] = 2;
/*      */       
/*  835 */       break;
/*      */     
/*      */     case 3: 
/*  838 */       offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2]));
/*  839 */       this._hashArea[offset] = q[0];
/*  840 */       this._hashArea[(offset + 1)] = q[1];
/*  841 */       this._hashArea[(offset + 2)] = q[2];
/*  842 */       this._hashArea[(offset + 3)] = 3;
/*      */       
/*  844 */       break;
/*      */     default: 
/*  846 */       int hash = calcHash(q, qlen);
/*  847 */       offset = _findOffsetForAdd(hash);
/*      */       
/*  849 */       this._hashArea[offset] = hash;
/*  850 */       int longStart = _appendLongName(q, qlen);
/*  851 */       this._hashArea[(offset + 1)] = longStart;
/*  852 */       this._hashArea[(offset + 3)] = qlen;
/*      */     }
/*      */     
/*  855 */     this._names[(offset >> 2)] = name;
/*      */     
/*      */ 
/*  858 */     this._count += 1;
/*  859 */     _verifyNeedForRehash();
/*  860 */     return name;
/*      */   }
/*      */   
/*      */   private void _verifyNeedForRehash()
/*      */   {
/*  865 */     if (this._count > this._hashSize >> 1) {
/*  866 */       int spillCount = this._spilloverEnd - _spilloverStart() >> 2;
/*  867 */       if ((spillCount > 1 + this._count >> 7) || (this._count > this._hashSize * 0.8D))
/*      */       {
/*  869 */         this._needRehash = true;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void _verifySharing()
/*      */   {
/*  876 */     if (this._hashShared) {
/*  877 */       this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
/*  878 */       this._names = ((String[])Arrays.copyOf(this._names, this._names.length));
/*  879 */       this._hashShared = false;
/*      */       
/*      */ 
/*  882 */       _verifyNeedForRehash();
/*      */     }
/*  884 */     if (this._needRehash) {
/*  885 */       rehash();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int _findOffsetForAdd(int hash)
/*      */   {
/*  895 */     int offset = _calcOffset(hash);
/*  896 */     int[] hashArea = this._hashArea;
/*  897 */     if (hashArea[(offset + 3)] == 0)
/*      */     {
/*  899 */       return offset;
/*      */     }
/*      */     
/*  902 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*  903 */     if (hashArea[(offset2 + 3)] == 0)
/*      */     {
/*  905 */       return offset2;
/*      */     }
/*      */     
/*      */ 
/*  909 */     offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  910 */     int bucketSize = 1 << this._tertiaryShift;
/*  911 */     for (int end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/*  912 */       if (hashArea[(offset2 + 3)] == 0)
/*      */       {
/*  914 */         return offset2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  919 */     offset = this._spilloverEnd;
/*  920 */     this._spilloverEnd += 4;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  930 */     int end = this._hashSize << 3;
/*  931 */     if (this._spilloverEnd >= end) {
/*  932 */       if (this._failOnDoS) {
/*  933 */         _reportTooManyCollisions();
/*      */       }
/*      */       
/*      */ 
/*  937 */       this._needRehash = true;
/*      */     }
/*  939 */     return offset;
/*      */   }
/*      */   
/*      */   private int _appendLongName(int[] quads, int qlen)
/*      */   {
/*  944 */     int start = this._longNameOffset;
/*      */     
/*      */ 
/*  947 */     if (start + qlen > this._hashArea.length)
/*      */     {
/*  949 */       int toAdd = start + qlen - this._hashArea.length;
/*      */       
/*  951 */       int minAdd = Math.min(4096, this._hashSize);
/*      */       
/*  953 */       int newSize = this._hashArea.length + Math.max(toAdd, minAdd);
/*  954 */       this._hashArea = Arrays.copyOf(this._hashArea, newSize);
/*      */     }
/*  956 */     System.arraycopy(quads, 0, this._hashArea, start, qlen);
/*  957 */     this._longNameOffset += qlen;
/*  958 */     return start;
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
/*      */   public int calcHash(int q1)
/*      */   {
/*  983 */     int hash = q1 ^ this._seed;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  989 */     hash += (hash >>> 16);
/*  990 */     hash ^= hash << 3;
/*  991 */     hash += (hash >>> 12);
/*  992 */     return hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int calcHash(int q1, int q2)
/*      */   {
/*  999 */     int hash = q1;
/*      */     
/* 1001 */     hash += (hash >>> 15);
/* 1002 */     hash ^= hash >>> 9;
/* 1003 */     hash += q2 * 33;
/* 1004 */     hash ^= this._seed;
/* 1005 */     hash += (hash >>> 16);
/* 1006 */     hash ^= hash >>> 4;
/* 1007 */     hash += (hash << 3);
/*      */     
/* 1009 */     return hash;
/*      */   }
/*      */   
/*      */   public int calcHash(int q1, int q2, int q3)
/*      */   {
/* 1014 */     int hash = q1 ^ this._seed;
/* 1015 */     hash += (hash >>> 9);
/* 1016 */     hash *= 31;
/* 1017 */     hash += q2;
/* 1018 */     hash *= 33;
/* 1019 */     hash += (hash >>> 15);
/* 1020 */     hash ^= q3;
/*      */     
/* 1022 */     hash += (hash >>> 4);
/*      */     
/* 1024 */     hash += (hash >>> 15);
/* 1025 */     hash ^= hash << 9;
/*      */     
/* 1027 */     return hash;
/*      */   }
/*      */   
/*      */   public int calcHash(int[] q, int qlen)
/*      */   {
/* 1032 */     if (qlen < 4) {
/* 1033 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1040 */     int hash = q[0] ^ this._seed;
/* 1041 */     hash += (hash >>> 9);
/* 1042 */     hash += q[1];
/* 1043 */     hash += (hash >>> 15);
/* 1044 */     hash *= 33;
/* 1045 */     hash ^= q[2];
/* 1046 */     hash += (hash >>> 4);
/*      */     
/* 1048 */     for (int i = 3; i < qlen; i++) {
/* 1049 */       int next = q[i];
/* 1050 */       next ^= next >> 21;
/* 1051 */       hash += next;
/*      */     }
/* 1053 */     hash *= 65599;
/*      */     
/*      */ 
/* 1056 */     hash += (hash >>> 19);
/* 1057 */     hash ^= hash << 5;
/* 1058 */     return hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void rehash()
/*      */   {
/* 1069 */     this._needRehash = false;
/*      */     
/* 1071 */     this._hashShared = false;
/*      */     
/*      */ 
/*      */ 
/* 1075 */     int[] oldHashArea = this._hashArea;
/* 1076 */     String[] oldNames = this._names;
/* 1077 */     int oldSize = this._hashSize;
/* 1078 */     int oldCount = this._count;
/* 1079 */     int newSize = oldSize + oldSize;
/* 1080 */     int oldEnd = this._spilloverEnd;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1085 */     if (newSize > 65536) {
/* 1086 */       nukeSymbols(true);
/* 1087 */       return;
/*      */     }
/*      */     
/* 1090 */     this._hashArea = new int[oldHashArea.length + (oldSize << 3)];
/* 1091 */     this._hashSize = newSize;
/* 1092 */     this._secondaryStart = (newSize << 2);
/* 1093 */     this._tertiaryStart = (this._secondaryStart + (this._secondaryStart >> 1));
/* 1094 */     this._tertiaryShift = _calcTertiaryShift(newSize);
/*      */     
/*      */ 
/* 1097 */     this._names = new String[oldNames.length << 1];
/* 1098 */     nukeSymbols(false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1105 */     int copyCount = 0;
/* 1106 */     int[] q = new int[16];
/* 1107 */     int offset = 0; for (int end = oldEnd; offset < end; offset += 4) {
/* 1108 */       int len = oldHashArea[(offset + 3)];
/* 1109 */       if (len != 0)
/*      */       {
/*      */ 
/* 1112 */         copyCount++;
/* 1113 */         String name = oldNames[(offset >> 2)];
/* 1114 */         switch (len) {
/*      */         case 1: 
/* 1116 */           q[0] = oldHashArea[offset];
/* 1117 */           addName(name, q, 1);
/* 1118 */           break;
/*      */         case 2: 
/* 1120 */           q[0] = oldHashArea[offset];
/* 1121 */           q[1] = oldHashArea[(offset + 1)];
/* 1122 */           addName(name, q, 2);
/* 1123 */           break;
/*      */         case 3: 
/* 1125 */           q[0] = oldHashArea[offset];
/* 1126 */           q[1] = oldHashArea[(offset + 1)];
/* 1127 */           q[2] = oldHashArea[(offset + 2)];
/* 1128 */           addName(name, q, 3);
/* 1129 */           break;
/*      */         default: 
/* 1131 */           if (len > q.length) {
/* 1132 */             q = new int[len];
/*      */           }
/*      */           
/* 1135 */           int qoff = oldHashArea[(offset + 1)];
/* 1136 */           System.arraycopy(oldHashArea, qoff, q, 0, len);
/* 1137 */           addName(name, q, len);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1144 */     if (copyCount != oldCount) {
/* 1145 */       throw new IllegalStateException("Failed rehash(): old count=" + oldCount + ", copyCount=" + copyCount);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void nukeSymbols(boolean fill)
/*      */   {
/* 1154 */     this._count = 0;
/*      */     
/* 1156 */     this._spilloverEnd = _spilloverStart();
/*      */     
/* 1158 */     this._longNameOffset = (this._hashSize << 3);
/* 1159 */     if (fill) {
/* 1160 */       Arrays.fill(this._hashArea, 0);
/* 1161 */       Arrays.fill(this._names, null);
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
/*      */   private final int _spilloverStart()
/*      */   {
/* 1177 */     int offset = this._hashSize;
/* 1178 */     return (offset << 3) - offset;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _reportTooManyCollisions()
/*      */   {
/* 1184 */     if (this._hashSize <= 1024) {
/* 1185 */       return;
/*      */     }
/* 1187 */     throw new IllegalStateException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions." + " You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static int _calcTertiaryShift(int primarySlots)
/*      */   {
/* 1196 */     int tertSlots = primarySlots >> 2;
/*      */     
/*      */ 
/* 1199 */     if (tertSlots < 64) {
/* 1200 */       return 4;
/*      */     }
/* 1202 */     if (tertSlots <= 256) {
/* 1203 */       return 5;
/*      */     }
/* 1205 */     if (tertSlots <= 1024) {
/* 1206 */       return 6;
/*      */     }
/*      */     
/* 1209 */     return 7;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static final class TableInfo
/*      */   {
/*      */     public final int size;
/*      */     
/*      */ 
/*      */     public final int count;
/*      */     
/*      */ 
/*      */     public final int tertiaryShift;
/*      */     
/*      */ 
/*      */     public final int[] mainHash;
/*      */     
/*      */ 
/*      */     public final String[] names;
/*      */     
/*      */ 
/*      */     public final int spilloverEnd;
/*      */     
/*      */     public final int longNameOffset;
/*      */     
/*      */ 
/*      */     public TableInfo(int size, int count, int tertiaryShift, int[] mainHash, String[] names, int spilloverEnd, int longNameOffset)
/*      */     {
/* 1238 */       this.size = size;
/* 1239 */       this.count = count;
/* 1240 */       this.tertiaryShift = tertiaryShift;
/* 1241 */       this.mainHash = mainHash;
/* 1242 */       this.names = names;
/* 1243 */       this.spilloverEnd = spilloverEnd;
/* 1244 */       this.longNameOffset = longNameOffset;
/*      */     }
/*      */     
/*      */     public TableInfo(ByteQuadsCanonicalizer src)
/*      */     {
/* 1249 */       this.size = src._hashSize;
/* 1250 */       this.count = src._count;
/* 1251 */       this.tertiaryShift = src._tertiaryShift;
/* 1252 */       this.mainHash = src._hashArea;
/* 1253 */       this.names = src._names;
/* 1254 */       this.spilloverEnd = src._spilloverEnd;
/* 1255 */       this.longNameOffset = src._longNameOffset;
/*      */     }
/*      */     
/*      */     public static TableInfo createInitial(int sz) {
/* 1259 */       int hashAreaSize = sz << 3;
/* 1260 */       int tertShift = ByteQuadsCanonicalizer._calcTertiaryShift(sz);
/*      */       
/* 1262 */       return new TableInfo(sz, 0, tertShift, new int[hashAreaSize], new String[sz << 1], hashAreaSize - sz, hashAreaSize);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\sym\ByteQuadsCanonicalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */