/*      */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*      */ 
/*      */ import java.util.OptionalInt;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class SliceUtf8
/*      */ {
/*      */   private static final int REPLACEMENT_CODE_POINT = 65533;
/*      */   private static final int TOP_MASK32 = -2139062144;
/*      */   private static final long TOP_MASK64 = -9187201950435737472L;
/*      */   private static final int[] LOWER_CODE_POINTS;
/*      */   private static final int[] UPPER_CODE_POINTS;
/*      */   private static final boolean[] WHITESPACE_CODE_POINTS;
/*      */   
/*      */   static
/*      */   {
/*   43 */     LOWER_CODE_POINTS = new int[1114112];
/*   44 */     UPPER_CODE_POINTS = new int[1114112];
/*   45 */     WHITESPACE_CODE_POINTS = new boolean[1114112];
/*   46 */     for (int codePoint = 0; codePoint <= 1114111; codePoint++) {
/*   47 */       int type = Character.getType(codePoint);
/*   48 */       if (type != 19) {
/*   49 */         LOWER_CODE_POINTS[codePoint] = Character.toLowerCase(codePoint);
/*   50 */         UPPER_CODE_POINTS[codePoint] = Character.toUpperCase(codePoint);
/*   51 */         WHITESPACE_CODE_POINTS[codePoint] = Character.isWhitespace(codePoint);
/*      */       }
/*      */       else {
/*   54 */         LOWER_CODE_POINTS[codePoint] = 65533;
/*   55 */         UPPER_CODE_POINTS[codePoint] = 65533;
/*   56 */         WHITESPACE_CODE_POINTS[codePoint] = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isAscii(Slice utf8)
/*      */   {
/*   66 */     int length = utf8.length();
/*   67 */     int offset = 0;
/*      */     
/*      */ 
/*   70 */     int length8 = length & 0x7FFFFFF8;
/*   71 */     for (; offset < length8; offset += 8) {
/*   72 */       if ((utf8.getLongUnchecked(offset) & 0x8080808080808080) != 0L) {
/*   73 */         return false;
/*      */       }
/*      */     }
/*      */     
/*   77 */     if (offset + 4 < length) {
/*   78 */       if ((utf8.getIntUnchecked(offset) & 0x80808080) != 0) {
/*   79 */         return false;
/*      */       }
/*      */       
/*   82 */       offset += 4;
/*      */     }
/*   85 */     for (; 
/*   85 */         offset < length; offset++) {
/*   86 */       if ((utf8.getByteUnchecked(offset) & 0x80) != 0) {
/*   87 */         return false;
/*      */       }
/*      */     }
/*      */     
/*   91 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int countCodePoints(Slice utf8)
/*      */   {
/*  102 */     return countCodePoints(utf8, 0, utf8.length());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int countCodePoints(Slice utf8, int offset, int length)
/*      */   {
/*  113 */     Preconditions.checkPositionIndexes(offset, offset + length, utf8.length());
/*      */     
/*      */ 
/*  116 */     if (length == 0) {
/*  117 */       return 0;
/*      */     }
/*      */     
/*  120 */     int continuationBytesCount = 0;
/*      */     
/*  122 */     int length8 = length & 0x7FFFFFF8;
/*  123 */     for (; offset < length8; offset += 8)
/*      */     {
/*  125 */       continuationBytesCount += countContinuationBytes(utf8.getLongUnchecked(offset));
/*      */     }
/*      */     
/*  128 */     if (offset + 4 < length)
/*      */     {
/*  130 */       continuationBytesCount += countContinuationBytes(utf8.getIntUnchecked(offset));
/*      */       
/*  132 */       offset += 4;
/*      */     }
/*  135 */     for (; 
/*  135 */         offset < length; offset++)
/*      */     {
/*  137 */       continuationBytesCount += countContinuationBytes(utf8.getByteUnchecked(offset));
/*      */     }
/*      */     
/*  140 */     assert (continuationBytesCount <= length);
/*  141 */     return length - continuationBytesCount;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice substring(Slice utf8, int codePointStart, int codePointLength)
/*      */   {
/*  153 */     Preconditions.checkArgument(codePointStart >= 0, "codePointStart is negative");
/*  154 */     Preconditions.checkArgument(codePointLength >= 0, "codePointLength is negative");
/*      */     
/*  156 */     int indexStart = offsetOfCodePoint(utf8, codePointStart);
/*  157 */     if (indexStart < 0) {
/*  158 */       throw new IllegalArgumentException("UTF-8 does not contain " + codePointStart + " code points");
/*      */     }
/*  160 */     if (codePointLength == 0) {
/*  161 */       return Slices.EMPTY_SLICE;
/*      */     }
/*  163 */     int indexEnd = offsetOfCodePoint(utf8, indexStart, codePointLength - 1);
/*  164 */     if (indexEnd < 0) {
/*  165 */       throw new IllegalArgumentException("UTF-8 does not contain " + (codePointStart + codePointLength) + " code points");
/*      */     }
/*  167 */     indexEnd += lengthOfCodePoint(utf8, indexEnd);
/*  168 */     if (indexEnd > utf8.length()) {
/*  169 */       throw new InvalidUtf8Exception("UTF-8 is not well formed");
/*      */     }
/*  171 */     return utf8.slice(indexStart, indexEnd - indexStart);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice reverse(Slice utf8)
/*      */   {
/*  181 */     int length = utf8.length();
/*  182 */     Slice reverse = Slices.allocate(length);
/*      */     
/*  184 */     int forwardPosition = 0;
/*  185 */     int reversePosition = length;
/*  186 */     while (forwardPosition < length) {
/*  187 */       int codePointLength = lengthOfCodePointSafe(utf8, forwardPosition);
/*      */       
/*      */ 
/*  190 */       reversePosition -= codePointLength;
/*  191 */       if (reversePosition < 0)
/*      */       {
/*  193 */         throw new InvalidUtf8Exception("UTF-8 is not well formed");
/*      */       }
/*      */       
/*  196 */       copyUtf8SequenceUnsafe(utf8, forwardPosition, reverse, reversePosition, codePointLength);
/*      */       
/*  198 */       forwardPosition += codePointLength;
/*      */     }
/*  200 */     return reverse;
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
/*      */   public static Slice toUpperCase(Slice utf8)
/*      */   {
/*  213 */     return translateCodePoints(utf8, UPPER_CODE_POINTS);
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
/*      */   public static Slice toLowerCase(Slice utf8)
/*      */   {
/*  226 */     return translateCodePoints(utf8, LOWER_CODE_POINTS);
/*      */   }
/*      */   
/*      */   private static Slice translateCodePoints(Slice utf8, int[] codePointTranslationMap)
/*      */   {
/*  231 */     int length = utf8.length();
/*  232 */     Slice newUtf8 = Slices.allocate(length);
/*      */     
/*  234 */     int position = 0;
/*  235 */     int upperPosition = 0;
/*  236 */     while (position < length) {
/*  237 */       int codePoint = tryGetCodePointAt(utf8, position);
/*  238 */       if (codePoint >= 0) {
/*  239 */         int upperCodePoint = codePointTranslationMap[codePoint];
/*      */         
/*      */ 
/*  242 */         int nextUpperPosition = upperPosition + lengthOfCodePoint(upperCodePoint);
/*  243 */         if (nextUpperPosition > length) {
/*  244 */           newUtf8 = Slices.ensureSize(newUtf8, nextUpperPosition);
/*      */         }
/*      */         
/*      */ 
/*  248 */         setCodePointAt(upperCodePoint, newUtf8, upperPosition);
/*      */         
/*  250 */         position += lengthOfCodePoint(codePoint);
/*  251 */         upperPosition = nextUpperPosition;
/*      */       }
/*      */       else {
/*  254 */         int skipLength = -codePoint;
/*      */         
/*      */ 
/*  257 */         int nextUpperPosition = upperPosition + skipLength;
/*  258 */         if (nextUpperPosition > length) {
/*  259 */           newUtf8 = Slices.ensureSize(newUtf8, nextUpperPosition);
/*      */         }
/*      */         
/*  262 */         copyUtf8SequenceUnsafe(utf8, position, newUtf8, upperPosition, skipLength);
/*  263 */         position += skipLength;
/*  264 */         upperPosition = nextUpperPosition;
/*      */       }
/*      */     }
/*  267 */     return newUtf8.slice(0, upperPosition);
/*      */   }
/*      */   
/*      */   private static void copyUtf8SequenceUnsafe(Slice source, int sourcePosition, Slice destination, int destinationPosition, int length)
/*      */   {
/*  272 */     switch (length) {
/*      */     case 1: 
/*  274 */       destination.setByteUnchecked(destinationPosition, source.getByteUnchecked(sourcePosition));
/*  275 */       break;
/*      */     case 2: 
/*  277 */       destination.setShortUnchecked(destinationPosition, source.getShortUnchecked(sourcePosition));
/*  278 */       break;
/*      */     case 3: 
/*  280 */       destination.setShortUnchecked(destinationPosition, source.getShortUnchecked(sourcePosition));
/*  281 */       destination.setByteUnchecked(destinationPosition + 2, source.getByteUnchecked(sourcePosition + 2));
/*  282 */       break;
/*      */     case 4: 
/*  284 */       destination.setIntUnchecked(destinationPosition, source.getIntUnchecked(sourcePosition));
/*  285 */       break;
/*      */     case 5: 
/*  287 */       destination.setIntUnchecked(destinationPosition, source.getIntUnchecked(sourcePosition));
/*  288 */       destination.setByteUnchecked(destinationPosition + 4, source.getByteUnchecked(sourcePosition + 4));
/*  289 */       break;
/*      */     case 6: 
/*  291 */       destination.setIntUnchecked(destinationPosition, source.getIntUnchecked(sourcePosition));
/*  292 */       destination.setShortUnchecked(destinationPosition + 4, source.getShortUnchecked(sourcePosition + 4));
/*  293 */       break;
/*      */     default: 
/*  295 */       throw new IllegalStateException("Invalid code point length " + length);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice leftTrim(Slice utf8)
/*      */   {
/*  306 */     int length = utf8.length();
/*      */     
/*  308 */     int position = firstNonWhitespacePosition(utf8);
/*  309 */     return utf8.slice(position, length - position);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice leftTrim(Slice utf8, int[] whiteSpaceCodePoints)
/*      */   {
/*  319 */     int length = utf8.length();
/*      */     
/*  321 */     int position = firstNonMatchPosition(utf8, whiteSpaceCodePoints);
/*  322 */     return utf8.slice(position, length - position);
/*      */   }
/*      */   
/*      */   private static int firstNonWhitespacePosition(Slice utf8)
/*      */   {
/*  327 */     int length = utf8.length();
/*      */     
/*  329 */     int position = 0;
/*  330 */     while (position < length) {
/*  331 */       int codePoint = tryGetCodePointAt(utf8, position);
/*  332 */       if (codePoint < 0) {
/*      */         break;
/*      */       }
/*  335 */       if (WHITESPACE_CODE_POINTS[codePoint] == 0) {
/*      */         break;
/*      */       }
/*  338 */       position += lengthOfCodePoint(codePoint);
/*      */     }
/*  340 */     return position;
/*      */   }
/*      */   
/*      */ 
/*      */   private static int firstNonMatchPosition(Slice utf8, int[] codePointsToMatch)
/*      */   {
/*  346 */     int length = utf8.length();
/*      */     
/*  348 */     int position = 0;
/*  349 */     while (position < length) {
/*  350 */       int codePoint = tryGetCodePointAt(utf8, position);
/*  351 */       if (codePoint < 0) {
/*      */         break;
/*      */       }
/*  354 */       if (!matches(codePoint, codePointsToMatch)) {
/*      */         break;
/*      */       }
/*  357 */       position += lengthOfCodePoint(codePoint);
/*      */     }
/*  359 */     return position;
/*      */   }
/*      */   
/*      */   private static boolean matches(int codePoint, int[] codePoints)
/*      */   {
/*  364 */     for (int codePointToTrim : codePoints) {
/*  365 */       if (codePoint == codePointToTrim) {
/*  366 */         return true;
/*      */       }
/*      */     }
/*  369 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice rightTrim(Slice utf8)
/*      */   {
/*  379 */     int position = lastNonWhitespacePosition(utf8, 0);
/*  380 */     return utf8.slice(0, position);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice rightTrim(Slice utf8, int[] whiteSpaceCodePoints)
/*      */   {
/*  390 */     int position = lastNonMatchPosition(utf8, 0, whiteSpaceCodePoints);
/*  391 */     return utf8.slice(0, position);
/*      */   }
/*      */   
/*      */   private static int lastNonWhitespacePosition(Slice utf8, int minPosition)
/*      */   {
/*  396 */     int position = utf8.length();
/*  397 */     while (minPosition < position)
/*      */     {
/*      */ 
/*      */ 
/*  401 */       byte unsignedByte = utf8.getByte(position - 1);
/*  402 */       int codePointLength; int codePoint; int codePointLength; if (!isContinuationByte(unsignedByte)) {
/*  403 */         int codePoint = unsignedByte & 0xFF;
/*  404 */         codePointLength = 1;
/*      */       } else { int codePointLength;
/*  406 */         if ((minPosition <= position - 2) && (!isContinuationByte(utf8.getByte(position - 2)))) {
/*  407 */           int codePoint = tryGetCodePointAt(utf8, position - 2);
/*  408 */           codePointLength = 2;
/*      */         } else { int codePointLength;
/*  410 */           if ((minPosition <= position - 3) && (!isContinuationByte(utf8.getByte(position - 3)))) {
/*  411 */             int codePoint = tryGetCodePointAt(utf8, position - 3);
/*  412 */             codePointLength = 3;
/*      */           } else {
/*  414 */             if ((minPosition > position - 4) || (isContinuationByte(utf8.getByte(position - 4)))) break;
/*  415 */             codePoint = tryGetCodePointAt(utf8, position - 4);
/*  416 */             codePointLength = 4;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  421 */       if ((codePoint < 0) || (codePointLength != lengthOfCodePoint(codePoint))) {
/*      */         break;
/*      */       }
/*  424 */       if (WHITESPACE_CODE_POINTS[codePoint] == 0) {
/*      */         break;
/*      */       }
/*  427 */       position -= codePointLength;
/*      */     }
/*  429 */     return position;
/*      */   }
/*      */   
/*      */ 
/*      */   private static int lastNonMatchPosition(Slice utf8, int minPosition, int[] codePointsToMatch)
/*      */   {
/*  435 */     int position = utf8.length();
/*  436 */     while (position > minPosition)
/*      */     {
/*      */ 
/*      */ 
/*  440 */       byte unsignedByte = utf8.getByte(position - 1);
/*  441 */       int codePointLength; int codePoint; int codePointLength; if (!isContinuationByte(unsignedByte)) {
/*  442 */         int codePoint = unsignedByte & 0xFF;
/*  443 */         codePointLength = 1;
/*      */       } else { int codePointLength;
/*  445 */         if ((minPosition <= position - 2) && (!isContinuationByte(utf8.getByte(position - 2)))) {
/*  446 */           int codePoint = tryGetCodePointAt(utf8, position - 2);
/*  447 */           codePointLength = 2;
/*      */         } else { int codePointLength;
/*  449 */           if ((minPosition <= position - 3) && (!isContinuationByte(utf8.getByte(position - 3)))) {
/*  450 */             int codePoint = tryGetCodePointAt(utf8, position - 3);
/*  451 */             codePointLength = 3;
/*      */           } else {
/*  453 */             if ((minPosition > position - 4) || (isContinuationByte(utf8.getByte(position - 4)))) break;
/*  454 */             codePoint = tryGetCodePointAt(utf8, position - 4);
/*  455 */             codePointLength = 4;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  460 */       if ((codePoint < 0) || (codePointLength != lengthOfCodePoint(codePoint))) {
/*      */         break;
/*      */       }
/*  463 */       if (!matches(codePoint, codePointsToMatch)) {
/*      */         break;
/*      */       }
/*  466 */       position -= codePointLength;
/*      */     }
/*  468 */     return position;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice trim(Slice utf8)
/*      */   {
/*  478 */     int start = firstNonWhitespacePosition(utf8);
/*  479 */     int end = lastNonWhitespacePosition(utf8, start);
/*  480 */     return utf8.slice(start, end - start);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice trim(Slice utf8, int[] whiteSpaceCodePoints)
/*      */   {
/*  490 */     int start = firstNonMatchPosition(utf8, whiteSpaceCodePoints);
/*  491 */     int end = lastNonMatchPosition(utf8, start, whiteSpaceCodePoints);
/*  492 */     return utf8.slice(start, end - start);
/*      */   }
/*      */   
/*      */   public static Slice fixInvalidUtf8(Slice slice)
/*      */   {
/*  497 */     return fixInvalidUtf8(slice, OptionalInt.of(65533));
/*      */   }
/*      */   
/*      */   public static Slice fixInvalidUtf8(Slice slice, OptionalInt replacementCodePoint)
/*      */   {
/*  502 */     if (isAscii(slice)) {
/*  503 */       return slice;
/*      */     }
/*      */     
/*  506 */     int replacementCodePointValue = -1;
/*  507 */     int replacementCodePointLength = 0;
/*  508 */     if (replacementCodePoint.isPresent()) {
/*  509 */       replacementCodePointValue = replacementCodePoint.getAsInt();
/*  510 */       replacementCodePointLength = lengthOfCodePoint(replacementCodePointValue);
/*      */     }
/*      */     
/*  513 */     int length = slice.length();
/*  514 */     Slice utf8 = Slices.allocate(length);
/*      */     
/*  516 */     int dataPosition = 0;
/*  517 */     int utf8Position = 0;
/*  518 */     while (dataPosition < length) {
/*  519 */       int codePoint = tryGetCodePointAt(slice, dataPosition);
/*      */       int codePointLength;
/*  521 */       if (codePoint >= 0) {
/*  522 */         int codePointLength = lengthOfCodePoint(codePoint);
/*  523 */         dataPosition += codePointLength;
/*      */       }
/*      */       else
/*      */       {
/*  527 */         dataPosition += -codePoint;
/*  528 */         if (replacementCodePointValue < 0) {
/*      */           continue;
/*      */         }
/*  531 */         codePoint = replacementCodePointValue;
/*  532 */         codePointLength = replacementCodePointLength;
/*      */       }
/*  534 */       utf8 = Slices.ensureSize(utf8, utf8Position + codePointLength);
/*  535 */       utf8Position += setCodePointAt(codePoint, utf8, utf8Position);
/*      */     }
/*  537 */     return utf8.slice(0, utf8Position);
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
/*      */   public static int tryGetCodePointAt(Slice utf8, int position)
/*      */   {
/*  552 */     byte firstByte = utf8.getByte(position);
/*      */     
/*  554 */     int length = lengthOfCodePointFromStartByteSafe(firstByte);
/*  555 */     if (length < 0) {
/*  556 */       return length;
/*      */     }
/*      */     
/*  559 */     if (length == 1)
/*      */     {
/*      */ 
/*  562 */       return firstByte;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  567 */     if (position + 1 >= utf8.length()) {
/*  568 */       return -1;
/*      */     }
/*      */     
/*  571 */     byte secondByte = utf8.getByteUnchecked(position + 1);
/*  572 */     if (!isContinuationByte(secondByte)) {
/*  573 */       return -1;
/*      */     }
/*      */     
/*  576 */     if (length == 2)
/*      */     {
/*  578 */       int codePoint = (firstByte & 0x1F) << 6 | secondByte & 0x3F;
/*      */       
/*      */ 
/*  581 */       return codePoint < 128 ? -2 : codePoint;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  586 */     if (position + 2 >= utf8.length()) {
/*  587 */       return -2;
/*      */     }
/*      */     
/*  590 */     byte thirdByte = utf8.getByteUnchecked(position + 2);
/*  591 */     if (!isContinuationByte(thirdByte)) {
/*  592 */       return -2;
/*      */     }
/*      */     
/*  595 */     if (length == 3)
/*      */     {
/*  597 */       int codePoint = (firstByte & 0xF) << 12 | (secondByte & 0x3F) << 6 | thirdByte & 0x3F;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  602 */       if ((55296 <= codePoint) && (codePoint <= 57343)) {
/*  603 */         return -3;
/*      */       }
/*      */       
/*  606 */       return codePoint < 2048 ? -3 : codePoint;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  611 */     if (position + 3 >= utf8.length()) {
/*  612 */       return -3;
/*      */     }
/*      */     
/*  615 */     byte forthByte = utf8.getByteUnchecked(position + 3);
/*  616 */     if (!isContinuationByte(forthByte)) {
/*  617 */       return -3;
/*      */     }
/*      */     
/*  620 */     if (length == 4)
/*      */     {
/*  622 */       int codePoint = (firstByte & 0x7) << 18 | (secondByte & 0x3F) << 12 | (thirdByte & 0x3F) << 6 | forthByte & 0x3F;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  627 */       if ((codePoint < 1114112) && (codePoint >= 65536)) {
/*  628 */         return codePoint;
/*      */       }
/*  630 */       return -4;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  635 */     if (position + 4 >= utf8.length()) {
/*  636 */       return -4;
/*      */     }
/*      */     
/*  639 */     byte fifthByte = utf8.getByteUnchecked(position + 4);
/*  640 */     if (!isContinuationByte(fifthByte)) {
/*  641 */       return -4;
/*      */     }
/*      */     
/*  644 */     if (length == 5)
/*      */     {
/*  646 */       return -5;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  651 */     if (position + 5 >= utf8.length()) {
/*  652 */       return -5;
/*      */     }
/*      */     
/*  655 */     byte sixthByte = utf8.getByteUnchecked(position + 5);
/*  656 */     if (!isContinuationByte(sixthByte)) {
/*  657 */       return -5;
/*      */     }
/*      */     
/*  660 */     if (length == 6)
/*      */     {
/*  662 */       return -6;
/*      */     }
/*      */     
/*      */ 
/*  666 */     return -1;
/*      */   }
/*      */   
/*      */   static int lengthOfCodePointFromStartByteSafe(byte startByte)
/*      */   {
/*  671 */     int unsignedStartByte = startByte & 0xFF;
/*  672 */     if (unsignedStartByte < 128)
/*      */     {
/*      */ 
/*  675 */       return 1;
/*      */     }
/*  677 */     if (unsignedStartByte < 192)
/*      */     {
/*      */ 
/*  680 */       return -1;
/*      */     }
/*  682 */     if (unsignedStartByte < 224)
/*      */     {
/*  684 */       return 2;
/*      */     }
/*  686 */     if (unsignedStartByte < 240)
/*      */     {
/*  688 */       return 3;
/*      */     }
/*  690 */     if (unsignedStartByte < 248)
/*      */     {
/*  692 */       return 4;
/*      */     }
/*  694 */     if (unsignedStartByte < 252)
/*      */     {
/*  696 */       return 5;
/*      */     }
/*  698 */     if (unsignedStartByte < 254)
/*      */     {
/*  700 */       return 6;
/*      */     }
/*  702 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int offsetOfCodePoint(Slice utf8, int codePointCount)
/*      */   {
/*  714 */     return offsetOfCodePoint(utf8, 0, codePointCount);
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
/*      */   public static int offsetOfCodePoint(Slice utf8, int position, int codePointCount)
/*      */   {
/*  729 */     Preconditions.checkPositionIndex(position, utf8.length());
/*  730 */     Preconditions.checkArgument(codePointCount >= 0, "codePointPosition is negative");
/*      */     
/*      */ 
/*  733 */     if (utf8.length() - position <= codePointCount) {
/*  734 */       return -1;
/*      */     }
/*  736 */     if (codePointCount == 0) {
/*  737 */       return position;
/*      */     }
/*      */     
/*  740 */     int correctIndex = codePointCount + position;
/*      */     
/*  742 */     int length8 = utf8.length() & 0x7FFFFFF8;
/*      */     
/*  744 */     while ((position < length8) && (correctIndex >= position + 8))
/*      */     {
/*  746 */       correctIndex += countContinuationBytes(utf8.getLongUnchecked(position));
/*      */       
/*  748 */       position += 8;
/*      */     }
/*      */     
/*  751 */     int length4 = utf8.length() & 0x7FFFFFFC;
/*      */     
/*  753 */     while ((position < length4) && (correctIndex >= position + 4))
/*      */     {
/*  755 */       correctIndex += countContinuationBytes(utf8.getIntUnchecked(position));
/*      */       
/*  757 */       position += 4;
/*      */     }
/*      */     
/*  760 */     while (position < utf8.length())
/*      */     {
/*  762 */       correctIndex += countContinuationBytes(utf8.getByteUnchecked(position));
/*  763 */       if (position == correctIndex) {
/*      */         break;
/*      */       }
/*      */       
/*  767 */       position++;
/*      */     }
/*      */     
/*  770 */     if ((position == correctIndex) && (correctIndex < utf8.length())) {
/*  771 */       return correctIndex;
/*      */     }
/*  773 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int lengthOfCodePoint(Slice utf8, int position)
/*      */   {
/*  784 */     return lengthOfCodePointFromStartByte(utf8.getByte(position));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int lengthOfCodePointSafe(Slice utf8, int position)
/*      */   {
/*  795 */     int length = lengthOfCodePointFromStartByteSafe(utf8.getByte(position));
/*  796 */     if (length < 0) {
/*  797 */       return -length;
/*      */     }
/*      */     
/*  800 */     if ((length == 1) || (position + 1 >= utf8.length()) || (!isContinuationByte(utf8.getByteUnchecked(position + 1)))) {
/*  801 */       return 1;
/*      */     }
/*      */     
/*  804 */     if ((length == 2) || (position + 2 >= utf8.length()) || (!isContinuationByte(utf8.getByteUnchecked(position + 2)))) {
/*  805 */       return 2;
/*      */     }
/*      */     
/*  808 */     if ((length == 3) || (position + 3 >= utf8.length()) || (!isContinuationByte(utf8.getByteUnchecked(position + 3)))) {
/*  809 */       return 3;
/*      */     }
/*      */     
/*  812 */     if ((length == 4) || (position + 4 >= utf8.length()) || (!isContinuationByte(utf8.getByteUnchecked(position + 4)))) {
/*  813 */       return 4;
/*      */     }
/*      */     
/*  816 */     if ((length == 5) || (position + 5 >= utf8.length()) || (!isContinuationByte(utf8.getByteUnchecked(position + 5)))) {
/*  817 */       return 5;
/*      */     }
/*      */     
/*  820 */     if (length == 6) {
/*  821 */       return 6;
/*      */     }
/*      */     
/*  824 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int lengthOfCodePoint(int codePoint)
/*      */   {
/*  834 */     if (codePoint < 0) {
/*  835 */       throw new InvalidCodePointException(codePoint);
/*      */     }
/*  837 */     if (codePoint < 128)
/*      */     {
/*      */ 
/*  840 */       return 1;
/*      */     }
/*  842 */     if (codePoint < 2048) {
/*  843 */       return 2;
/*      */     }
/*  845 */     if (codePoint < 65536) {
/*  846 */       return 3;
/*      */     }
/*  848 */     if (codePoint < 1114112) {
/*  849 */       return 4;
/*      */     }
/*      */     
/*  852 */     throw new InvalidCodePointException(codePoint);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int lengthOfCodePointFromStartByte(byte startByte)
/*      */   {
/*  863 */     int unsignedStartByte = startByte & 0xFF;
/*  864 */     if (unsignedStartByte < 128)
/*      */     {
/*      */ 
/*  867 */       return 1;
/*      */     }
/*  869 */     if (unsignedStartByte < 192)
/*      */     {
/*      */ 
/*  872 */       throw new InvalidUtf8Exception("Illegal start 0x" + Integer.toHexString(unsignedStartByte).toUpperCase() + " of code point");
/*      */     }
/*  874 */     if (unsignedStartByte < 224)
/*      */     {
/*  876 */       return 2;
/*      */     }
/*  878 */     if (unsignedStartByte < 240)
/*      */     {
/*  880 */       return 3;
/*      */     }
/*  882 */     if (unsignedStartByte < 248)
/*      */     {
/*  884 */       return 4;
/*      */     }
/*      */     
/*  887 */     throw new InvalidUtf8Exception("Illegal start 0x" + Integer.toHexString(unsignedStartByte).toUpperCase() + " of code point");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int getCodePointAt(Slice utf8, int position)
/*      */   {
/*  898 */     int unsignedStartByte = utf8.getByte(position) & 0xFF;
/*  899 */     if (unsignedStartByte < 128)
/*      */     {
/*      */ 
/*  902 */       return unsignedStartByte;
/*      */     }
/*  904 */     if (unsignedStartByte < 192)
/*      */     {
/*      */ 
/*  907 */       throw new InvalidUtf8Exception("Illegal start 0x" + Integer.toHexString(unsignedStartByte).toUpperCase() + " of code point");
/*      */     }
/*  909 */     if (unsignedStartByte < 224)
/*      */     {
/*  911 */       if (position + 1 >= utf8.length()) {
/*  912 */         throw new InvalidUtf8Exception("UTF-8 sequence truncated");
/*      */       }
/*  914 */       return 
/*  915 */         (unsignedStartByte & 0x1F) << 6 | utf8.getByte(position + 1) & 0x3F;
/*      */     }
/*  917 */     if (unsignedStartByte < 240)
/*      */     {
/*  919 */       if (position + 2 >= utf8.length()) {
/*  920 */         throw new InvalidUtf8Exception("UTF-8 sequence truncated");
/*      */       }
/*  922 */       return 
/*      */       
/*  924 */         (unsignedStartByte & 0xF) << 12 | (utf8.getByteUnchecked(position + 1) & 0x3F) << 6 | utf8.getByteUnchecked(position + 2) & 0x3F;
/*      */     }
/*  926 */     if (unsignedStartByte < 248)
/*      */     {
/*  928 */       if (position + 3 >= utf8.length()) {
/*  929 */         throw new InvalidUtf8Exception("UTF-8 sequence truncated");
/*      */       }
/*  931 */       return 
/*      */       
/*      */ 
/*  934 */         (unsignedStartByte & 0x7) << 18 | (utf8.getByteUnchecked(position + 1) & 0x3F) << 12 | (utf8.getByteUnchecked(position + 2) & 0x3F) << 6 | utf8.getByteUnchecked(position + 3) & 0x3F;
/*      */     }
/*      */     
/*  937 */     throw new InvalidUtf8Exception("Illegal start 0x" + Integer.toHexString(unsignedStartByte).toUpperCase() + " of code point");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int getCodePointBefore(Slice utf8, int position)
/*      */   {
/*  948 */     byte unsignedByte = utf8.getByte(position - 1);
/*  949 */     if (!isContinuationByte(unsignedByte)) {
/*  950 */       return unsignedByte & 0xFF;
/*      */     }
/*  952 */     if (!isContinuationByte(utf8.getByte(position - 2))) {
/*  953 */       return getCodePointAt(utf8, position - 2);
/*      */     }
/*  955 */     if (!isContinuationByte(utf8.getByte(position - 3))) {
/*  956 */       return getCodePointAt(utf8, position - 3);
/*      */     }
/*  958 */     if (!isContinuationByte(utf8.getByte(position - 4))) {
/*  959 */       return getCodePointAt(utf8, position - 4);
/*      */     }
/*      */     
/*      */ 
/*  963 */     throw new InvalidUtf8Exception("UTF-8 is not well formed");
/*      */   }
/*      */   
/*      */   private static boolean isContinuationByte(byte b)
/*      */   {
/*  968 */     return (b & 0xC0) == 128;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Slice codePointToUtf8(int codePoint)
/*      */   {
/*  979 */     Slice utf8 = Slices.allocate(lengthOfCodePoint(codePoint));
/*  980 */     setCodePointAt(codePoint, utf8, 0);
/*  981 */     return utf8;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int setCodePointAt(int codePoint, Slice utf8, int position)
/*      */   {
/*  991 */     if (codePoint < 0) {
/*  992 */       throw new InvalidCodePointException(codePoint);
/*      */     }
/*  994 */     if (codePoint < 128)
/*      */     {
/*      */ 
/*  997 */       utf8.setByte(position, codePoint);
/*  998 */       return 1;
/*      */     }
/* 1000 */     if (codePoint < 2048)
/*      */     {
/* 1002 */       utf8.setByte(position, 0xC0 | codePoint >>> 6);
/* 1003 */       utf8.setByte(position + 1, 0x80 | codePoint & 0x3F);
/* 1004 */       return 2;
/*      */     }
/* 1006 */     if ((55296 <= codePoint) && (codePoint <= 57343)) {
/* 1007 */       throw new InvalidCodePointException(codePoint);
/*      */     }
/* 1009 */     if (codePoint < 65536)
/*      */     {
/* 1011 */       utf8.setByte(position, 0xE0 | codePoint >>> 12 & 0xF);
/* 1012 */       utf8.setByte(position + 1, 0x80 | codePoint >>> 6 & 0x3F);
/* 1013 */       utf8.setByte(position + 2, 0x80 | codePoint & 0x3F);
/* 1014 */       return 3;
/*      */     }
/* 1016 */     if (codePoint < 1114112)
/*      */     {
/* 1018 */       utf8.setByte(position, 0xF0 | codePoint >>> 18 & 0x7);
/* 1019 */       utf8.setByte(position + 1, 0x80 | codePoint >>> 12 & 0x3F);
/* 1020 */       utf8.setByte(position + 2, 0x80 | codePoint >>> 6 & 0x3F);
/* 1021 */       utf8.setByte(position + 3, 0x80 | codePoint & 0x3F);
/* 1022 */       return 4;
/*      */     }
/*      */     
/* 1025 */     throw new InvalidCodePointException(codePoint);
/*      */   }
/*      */   
/*      */ 
/*      */   private static int countContinuationBytes(byte i8)
/*      */   {
/* 1031 */     int value = i8 & 0xFF;
/* 1032 */     return value >>> 7 & (value ^ 0xFFFFFFFF) >>> 6;
/*      */   }
/*      */   
/*      */ 
/*      */   private static int countContinuationBytes(int i32)
/*      */   {
/* 1038 */     i32 = (i32 & 0x80808080) >>> 1 & (i32 ^ 0xFFFFFFFF);
/* 1039 */     return Integer.bitCount(i32);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int countContinuationBytes(long i64)
/*      */   {
/* 1051 */     i64 = (i64 & 0x8080808080808080) >>> 1 & (i64 ^ 0xFFFFFFFFFFFFFFFF);
/* 1052 */     return Long.bitCount(i64);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\SliceUtf8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */