/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Ascii;
/*     */ import com.google.common.base.CharMatcher;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.math.IntMath;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Arrays;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
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
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class BaseEncoding
/*     */ {
/*     */   public static final class DecodingException
/*     */     extends IOException
/*     */   {
/*     */     DecodingException(String message)
/*     */     {
/* 146 */       super();
/*     */     }
/*     */     
/*     */     DecodingException(Throwable cause) {
/* 150 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String encode(byte[] bytes)
/*     */   {
/* 158 */     return encode((byte[])Preconditions.checkNotNull(bytes), 0, bytes.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String encode(byte[] bytes, int off, int len)
/*     */   {
/* 166 */     Preconditions.checkNotNull(bytes);
/* 167 */     Preconditions.checkPositionIndexes(off, off + len, bytes.length);
/* 168 */     GwtWorkarounds.CharOutput result = GwtWorkarounds.stringBuilderOutput(maxEncodedSize(len));
/* 169 */     GwtWorkarounds.ByteOutput byteOutput = encodingStream(result);
/*     */     try {
/* 171 */       for (int i = 0; i < len; i++) {
/* 172 */         byteOutput.write(bytes[(off + i)]);
/*     */       }
/* 174 */       byteOutput.close();
/*     */     } catch (IOException impossible) {
/* 176 */       throw new AssertionError("impossible");
/*     */     }
/* 178 */     return result.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("Writer,OutputStream")
/*     */   public final OutputStream encodingStream(Writer writer)
/*     */   {
/* 188 */     return GwtWorkarounds.asOutputStream(encodingStream(GwtWorkarounds.asCharOutput(writer)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("ByteSink,CharSink")
/*     */   public final ByteSink encodingSink(final CharSink encodedSink)
/*     */   {
/* 196 */     Preconditions.checkNotNull(encodedSink);
/* 197 */     new ByteSink()
/*     */     {
/*     */       public OutputStream openStream() throws IOException {
/* 200 */         return BaseEncoding.this.encodingStream(encodedSink.openStream());
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   private static byte[] extract(byte[] result, int length)
/*     */   {
/* 208 */     if (length == result.length) {
/* 209 */       return result;
/*     */     }
/* 211 */     byte[] trunc = new byte[length];
/* 212 */     System.arraycopy(result, 0, trunc, 0, length);
/* 213 */     return trunc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] decode(CharSequence chars)
/*     */   {
/*     */     try
/*     */     {
/* 226 */       return decodeChecked(chars);
/*     */     } catch (DecodingException badInput) {
/* 228 */       throw new IllegalArgumentException(badInput);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final byte[] decodeChecked(CharSequence chars)
/*     */     throws DecodingException
/*     */   {
/* 240 */     chars = padding().trimTrailingFrom(chars);
/* 241 */     GwtWorkarounds.ByteInput decodedInput = decodingStream(GwtWorkarounds.asCharInput(chars));
/* 242 */     byte[] tmp = new byte[maxDecodedSize(chars.length())];
/* 243 */     int index = 0;
/*     */     try {
/* 245 */       for (int i = decodedInput.read(); i != -1; i = decodedInput.read()) {
/* 246 */         tmp[(index++)] = ((byte)i);
/*     */       }
/*     */     } catch (DecodingException badInput) {
/* 249 */       throw badInput;
/*     */     } catch (IOException impossible) {
/* 251 */       throw new AssertionError(impossible);
/*     */     }
/* 253 */     return extract(tmp, index);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("Reader,InputStream")
/*     */   public final InputStream decodingStream(Reader reader)
/*     */   {
/* 263 */     return GwtWorkarounds.asInputStream(decodingStream(GwtWorkarounds.asCharInput(reader)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("ByteSource,CharSource")
/*     */   public final ByteSource decodingSource(final CharSource encodedSource)
/*     */   {
/* 272 */     Preconditions.checkNotNull(encodedSource);
/* 273 */     new ByteSource()
/*     */     {
/*     */       public InputStream openStream() throws IOException {
/* 276 */         return BaseEncoding.this.decodingStream(encodedSource.openStream());
/*     */       }
/*     */     };
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
/*     */   abstract int maxEncodedSize(int paramInt);
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
/*     */   abstract GwtWorkarounds.ByteOutput encodingStream(GwtWorkarounds.CharOutput paramCharOutput);
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
/*     */   abstract int maxDecodedSize(int paramInt);
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
/*     */   abstract GwtWorkarounds.ByteInput decodingStream(GwtWorkarounds.CharInput paramCharInput);
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
/*     */   abstract CharMatcher padding();
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
/* 345 */   private static final BaseEncoding BASE64 = new StandardBaseEncoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", Character.valueOf('='));
/*     */   
/*     */   @CheckReturnValue
/*     */   public abstract BaseEncoding omitPadding();
/*     */   
/*     */   @CheckReturnValue
/*     */   public abstract BaseEncoding withPadChar(char paramChar);
/*     */   
/*     */   @CheckReturnValue
/*     */   public abstract BaseEncoding withSeparator(String paramString, int paramInt);
/*     */   
/*     */   @CheckReturnValue
/*     */   public abstract BaseEncoding upperCase();
/*     */   
/*     */   @CheckReturnValue
/*     */   public abstract BaseEncoding lowerCase();
/*     */   
/* 362 */   public static BaseEncoding base64() { return BASE64; }
/*     */   
/*     */ 
/* 365 */   private static final BaseEncoding BASE64_URL = new StandardBaseEncoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", Character.valueOf('='));
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
/*     */   public static BaseEncoding base64Url()
/*     */   {
/* 383 */     return BASE64_URL;
/*     */   }
/*     */   
/* 386 */   private static final BaseEncoding BASE32 = new StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", Character.valueOf('='));
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
/*     */   public static BaseEncoding base32()
/*     */   {
/* 403 */     return BASE32;
/*     */   }
/*     */   
/* 406 */   private static final BaseEncoding BASE32_HEX = new StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", Character.valueOf('='));
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
/*     */   public static BaseEncoding base32Hex()
/*     */   {
/* 422 */     return BASE32_HEX;
/*     */   }
/*     */   
/* 425 */   private static final BaseEncoding BASE16 = new StandardBaseEncoding("base16()", "0123456789ABCDEF", null);
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
/*     */   public static BaseEncoding base16()
/*     */   {
/* 443 */     return BASE16;
/*     */   }
/*     */   
/*     */   private static final class Alphabet extends CharMatcher
/*     */   {
/*     */     private final String name;
/*     */     private final char[] chars;
/*     */     final int mask;
/*     */     final int bitsPerChar;
/*     */     final int charsPerChunk;
/*     */     final int bytesPerChunk;
/*     */     private final byte[] decodabet;
/*     */     private final boolean[] validPadding;
/*     */     
/*     */     Alphabet(String name, char[] chars) {
/* 458 */       this.name = ((String)Preconditions.checkNotNull(name));
/* 459 */       this.chars = ((char[])Preconditions.checkNotNull(chars));
/*     */       try {
/* 461 */         this.bitsPerChar = IntMath.log2(chars.length, RoundingMode.UNNECESSARY);
/*     */       } catch (ArithmeticException e) {
/* 463 */         throw new IllegalArgumentException("Illegal alphabet length " + chars.length, e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 470 */       int gcd = Math.min(8, Integer.lowestOneBit(this.bitsPerChar));
/* 471 */       this.charsPerChunk = (8 / gcd);
/* 472 */       this.bytesPerChunk = (this.bitsPerChar / gcd);
/*     */       
/* 474 */       this.mask = (chars.length - 1);
/*     */       
/* 476 */       byte[] decodabet = new byte[''];
/* 477 */       Arrays.fill(decodabet, (byte)-1);
/* 478 */       for (int i = 0; i < chars.length; i++) {
/* 479 */         char c = chars[i];
/* 480 */         Preconditions.checkArgument(CharMatcher.ASCII.matches(c), "Non-ASCII character: %s", new Object[] { Character.valueOf(c) });
/* 481 */         Preconditions.checkArgument(decodabet[c] == -1, "Duplicate character: %s", new Object[] { Character.valueOf(c) });
/* 482 */         decodabet[c] = ((byte)i);
/*     */       }
/* 484 */       this.decodabet = decodabet;
/*     */       
/* 486 */       boolean[] validPadding = new boolean[this.charsPerChunk];
/* 487 */       for (int i = 0; i < this.bytesPerChunk; i++) {
/* 488 */         validPadding[IntMath.divide(i * 8, this.bitsPerChar, RoundingMode.CEILING)] = true;
/*     */       }
/* 490 */       this.validPadding = validPadding;
/*     */     }
/*     */     
/*     */     char encode(int bits) {
/* 494 */       return this.chars[bits];
/*     */     }
/*     */     
/*     */     boolean isValidPaddingStartPosition(int index) {
/* 498 */       return this.validPadding[(index % this.charsPerChunk)];
/*     */     }
/*     */     
/*     */     int decode(char ch) throws IOException {
/* 502 */       if ((ch > '') || (this.decodabet[ch] == -1)) {
/* 503 */         throw new DecodingException("Unrecognized character: " + ch);
/*     */       }
/* 505 */       return this.decodabet[ch];
/*     */     }
/*     */     
/*     */     private boolean hasLowerCase() {
/* 509 */       for (char c : this.chars) {
/* 510 */         if (Ascii.isLowerCase(c)) {
/* 511 */           return true;
/*     */         }
/*     */       }
/* 514 */       return false;
/*     */     }
/*     */     
/*     */     private boolean hasUpperCase() {
/* 518 */       for (char c : this.chars) {
/* 519 */         if (Ascii.isUpperCase(c)) {
/* 520 */           return true;
/*     */         }
/*     */       }
/* 523 */       return false;
/*     */     }
/*     */     
/*     */     Alphabet upperCase() {
/* 527 */       if (!hasLowerCase()) {
/* 528 */         return this;
/*     */       }
/* 530 */       Preconditions.checkState(!hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
/* 531 */       char[] upperCased = new char[this.chars.length];
/* 532 */       for (int i = 0; i < this.chars.length; i++) {
/* 533 */         upperCased[i] = Ascii.toUpperCase(this.chars[i]);
/*     */       }
/* 535 */       return new Alphabet(this.name + ".upperCase()", upperCased);
/*     */     }
/*     */     
/*     */     Alphabet lowerCase()
/*     */     {
/* 540 */       if (!hasUpperCase()) {
/* 541 */         return this;
/*     */       }
/* 543 */       Preconditions.checkState(!hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
/* 544 */       char[] lowerCased = new char[this.chars.length];
/* 545 */       for (int i = 0; i < this.chars.length; i++) {
/* 546 */         lowerCased[i] = Ascii.toLowerCase(this.chars[i]);
/*     */       }
/* 548 */       return new Alphabet(this.name + ".lowerCase()", lowerCased);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean matches(char c)
/*     */     {
/* 554 */       return (CharMatcher.ASCII.matches(c)) && (this.decodabet[c] != -1);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 559 */       return this.name;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class StandardBaseEncoding extends BaseEncoding {
/*     */     private final Alphabet alphabet;
/*     */     @Nullable
/*     */     private final Character paddingChar;
/*     */     private transient BaseEncoding upperCase;
/*     */     private transient BaseEncoding lowerCase;
/*     */     
/*     */     StandardBaseEncoding(String name, String alphabetChars, @Nullable Character paddingChar) {
/* 571 */       this(new Alphabet(name, alphabetChars.toCharArray()), paddingChar);
/*     */     }
/*     */     
/*     */     StandardBaseEncoding(Alphabet alphabet, @Nullable Character paddingChar) {
/* 575 */       this.alphabet = ((Alphabet)Preconditions.checkNotNull(alphabet));
/* 576 */       Preconditions.checkArgument((paddingChar == null) || (!alphabet.matches(paddingChar.charValue())), "Padding character %s was already in alphabet", new Object[] { paddingChar });
/*     */       
/* 578 */       this.paddingChar = paddingChar;
/*     */     }
/*     */     
/*     */     CharMatcher padding()
/*     */     {
/* 583 */       return this.paddingChar == null ? CharMatcher.NONE : CharMatcher.is(this.paddingChar.charValue());
/*     */     }
/*     */     
/*     */     int maxEncodedSize(int bytes)
/*     */     {
/* 588 */       return this.alphabet.charsPerChunk * IntMath.divide(bytes, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
/*     */     }
/*     */     
/*     */     GwtWorkarounds.ByteOutput encodingStream(final GwtWorkarounds.CharOutput out)
/*     */     {
/* 593 */       Preconditions.checkNotNull(out);
/* 594 */       new GwtWorkarounds.ByteOutput() {
/* 595 */         int bitBuffer = 0;
/* 596 */         int bitBufferLength = 0;
/* 597 */         int writtenChars = 0;
/*     */         
/*     */         public void write(byte b) throws IOException
/*     */         {
/* 601 */           this.bitBuffer <<= 8;
/* 602 */           this.bitBuffer |= b & 0xFF;
/* 603 */           this.bitBufferLength += 8;
/* 604 */           while (this.bitBufferLength >= StandardBaseEncoding.this.alphabet.bitsPerChar) {
/* 605 */             int charIndex = this.bitBuffer >> this.bitBufferLength - StandardBaseEncoding.this.alphabet.bitsPerChar & StandardBaseEncoding.this.alphabet.mask;
/*     */             
/* 607 */             out.write(StandardBaseEncoding.this.alphabet.encode(charIndex));
/* 608 */             this.writtenChars += 1;
/* 609 */             this.bitBufferLength -= StandardBaseEncoding.this.alphabet.bitsPerChar;
/*     */           }
/*     */         }
/*     */         
/*     */         public void flush() throws IOException
/*     */         {
/* 615 */           out.flush();
/*     */         }
/*     */         
/*     */         public void close() throws IOException
/*     */         {
/* 620 */           if (this.bitBufferLength > 0) {
/* 621 */             int charIndex = this.bitBuffer << StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength & StandardBaseEncoding.this.alphabet.mask;
/*     */             
/* 623 */             out.write(StandardBaseEncoding.this.alphabet.encode(charIndex));
/* 624 */             this.writtenChars += 1;
/* 625 */             if (StandardBaseEncoding.this.paddingChar != null) {
/* 626 */               while (this.writtenChars % StandardBaseEncoding.this.alphabet.charsPerChunk != 0) {
/* 627 */                 out.write(StandardBaseEncoding.this.paddingChar.charValue());
/* 628 */                 this.writtenChars += 1;
/*     */               }
/*     */             }
/*     */           }
/* 632 */           out.close();
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     int maxDecodedSize(int chars)
/*     */     {
/* 639 */       return (int)((this.alphabet.bitsPerChar * chars + 7L) / 8L);
/*     */     }
/*     */     
/*     */     GwtWorkarounds.ByteInput decodingStream(final GwtWorkarounds.CharInput reader)
/*     */     {
/* 644 */       Preconditions.checkNotNull(reader);
/* 645 */       new GwtWorkarounds.ByteInput() {
/* 646 */         int bitBuffer = 0;
/* 647 */         int bitBufferLength = 0;
/* 648 */         int readChars = 0;
/* 649 */         boolean hitPadding = false;
/* 650 */         final CharMatcher paddingMatcher = StandardBaseEncoding.this.padding();
/*     */         
/*     */         public int read() throws IOException
/*     */         {
/*     */           for (;;) {
/* 655 */             int readChar = reader.read();
/* 656 */             if (readChar == -1) {
/* 657 */               if ((!this.hitPadding) && (!StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars))) {
/* 658 */                 throw new DecodingException("Invalid input length " + this.readChars);
/*     */               }
/* 660 */               return -1;
/*     */             }
/* 662 */             this.readChars += 1;
/* 663 */             char ch = (char)readChar;
/* 664 */             if (this.paddingMatcher.matches(ch)) {
/* 665 */               if ((!this.hitPadding) && ((this.readChars == 1) || (!StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1))))
/*     */               {
/* 667 */                 throw new DecodingException("Padding cannot start at index " + this.readChars);
/*     */               }
/* 669 */               this.hitPadding = true;
/* 670 */             } else { if (this.hitPadding) {
/* 671 */                 throw new DecodingException("Expected padding character but found '" + ch + "' at index " + this.readChars);
/*     */               }
/*     */               
/* 674 */               this.bitBuffer <<= StandardBaseEncoding.this.alphabet.bitsPerChar;
/* 675 */               this.bitBuffer |= StandardBaseEncoding.this.alphabet.decode(ch);
/* 676 */               this.bitBufferLength += StandardBaseEncoding.this.alphabet.bitsPerChar;
/*     */               
/* 678 */               if (this.bitBufferLength >= 8) {
/* 679 */                 this.bitBufferLength -= 8;
/* 680 */                 return this.bitBuffer >> this.bitBufferLength & 0xFF;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         public void close() throws IOException
/*     */         {
/* 688 */           reader.close();
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public BaseEncoding omitPadding()
/*     */     {
/* 695 */       return this.paddingChar == null ? this : new StandardBaseEncoding(this.alphabet, null);
/*     */     }
/*     */     
/*     */     public BaseEncoding withPadChar(char padChar)
/*     */     {
/* 700 */       if ((8 % this.alphabet.bitsPerChar == 0) || ((this.paddingChar != null) && (this.paddingChar.charValue() == padChar)))
/*     */       {
/* 702 */         return this;
/*     */       }
/* 704 */       return new StandardBaseEncoding(this.alphabet, Character.valueOf(padChar));
/*     */     }
/*     */     
/*     */ 
/*     */     public BaseEncoding withSeparator(String separator, int afterEveryChars)
/*     */     {
/* 710 */       Preconditions.checkNotNull(separator);
/* 711 */       Preconditions.checkArgument(padding().or(this.alphabet).matchesNoneOf(separator), "Separator cannot contain alphabet or padding characters");
/*     */       
/* 713 */       return new SeparatedBaseEncoding(this, separator, afterEveryChars);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public BaseEncoding upperCase()
/*     */     {
/* 721 */       BaseEncoding result = this.upperCase;
/* 722 */       if (result == null) {
/* 723 */         Alphabet upper = this.alphabet.upperCase();
/* 724 */         result = this.upperCase = upper == this.alphabet ? this : new StandardBaseEncoding(upper, this.paddingChar);
/*     */       }
/*     */       
/* 727 */       return result;
/*     */     }
/*     */     
/*     */     public BaseEncoding lowerCase()
/*     */     {
/* 732 */       BaseEncoding result = this.lowerCase;
/* 733 */       if (result == null) {
/* 734 */         Alphabet lower = this.alphabet.lowerCase();
/* 735 */         result = this.lowerCase = lower == this.alphabet ? this : new StandardBaseEncoding(lower, this.paddingChar);
/*     */       }
/*     */       
/* 738 */       return result;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 743 */       StringBuilder builder = new StringBuilder("BaseEncoding.");
/* 744 */       builder.append(this.alphabet.toString());
/* 745 */       if (8 % this.alphabet.bitsPerChar != 0) {
/* 746 */         if (this.paddingChar == null) {
/* 747 */           builder.append(".omitPadding()");
/*     */         } else {
/* 749 */           builder.append(".withPadChar(").append(this.paddingChar).append(')');
/*     */         }
/*     */       }
/* 752 */       return builder.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   static GwtWorkarounds.CharInput ignoringInput(GwtWorkarounds.CharInput delegate, final CharMatcher toIgnore) {
/* 757 */     Preconditions.checkNotNull(delegate);
/* 758 */     Preconditions.checkNotNull(toIgnore);
/* 759 */     new GwtWorkarounds.CharInput()
/*     */     {
/*     */       public int read() throws IOException {
/*     */         int readChar;
/*     */         do {
/* 764 */           readChar = this.val$delegate.read();
/* 765 */         } while ((readChar != -1) && (toIgnore.matches((char)readChar)));
/* 766 */         return readChar;
/*     */       }
/*     */       
/*     */       public void close() throws IOException
/*     */       {
/* 771 */         this.val$delegate.close();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   static GwtWorkarounds.CharOutput separatingOutput(final GwtWorkarounds.CharOutput delegate, final String separator, int afterEveryChars)
/*     */   {
/* 778 */     Preconditions.checkNotNull(delegate);
/* 779 */     Preconditions.checkNotNull(separator);
/* 780 */     Preconditions.checkArgument(afterEveryChars > 0);
/* 781 */     new GwtWorkarounds.CharOutput() {
/* 782 */       int charsUntilSeparator = this.val$afterEveryChars;
/*     */       
/*     */       public void write(char c) throws IOException
/*     */       {
/* 786 */         if (this.charsUntilSeparator == 0) {
/* 787 */           for (int i = 0; i < separator.length(); i++) {
/* 788 */             delegate.write(separator.charAt(i));
/*     */           }
/* 790 */           this.charsUntilSeparator = this.val$afterEveryChars;
/*     */         }
/* 792 */         delegate.write(c);
/* 793 */         this.charsUntilSeparator -= 1;
/*     */       }
/*     */       
/*     */       public void flush() throws IOException
/*     */       {
/* 798 */         delegate.flush();
/*     */       }
/*     */       
/*     */       public void close() throws IOException
/*     */       {
/* 803 */         delegate.close();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   static final class SeparatedBaseEncoding extends BaseEncoding {
/*     */     private final BaseEncoding delegate;
/*     */     private final String separator;
/*     */     private final int afterEveryChars;
/*     */     private final CharMatcher separatorChars;
/*     */     
/*     */     SeparatedBaseEncoding(BaseEncoding delegate, String separator, int afterEveryChars) {
/* 815 */       this.delegate = ((BaseEncoding)Preconditions.checkNotNull(delegate));
/* 816 */       this.separator = ((String)Preconditions.checkNotNull(separator));
/* 817 */       this.afterEveryChars = afterEveryChars;
/* 818 */       Preconditions.checkArgument(afterEveryChars > 0, "Cannot add a separator after every %s chars", new Object[] { Integer.valueOf(afterEveryChars) });
/*     */       
/* 820 */       this.separatorChars = CharMatcher.anyOf(separator).precomputed();
/*     */     }
/*     */     
/*     */     CharMatcher padding()
/*     */     {
/* 825 */       return this.delegate.padding();
/*     */     }
/*     */     
/*     */     int maxEncodedSize(int bytes)
/*     */     {
/* 830 */       int unseparatedSize = this.delegate.maxEncodedSize(bytes);
/* 831 */       return unseparatedSize + this.separator.length() * IntMath.divide(Math.max(0, unseparatedSize - 1), this.afterEveryChars, RoundingMode.FLOOR);
/*     */     }
/*     */     
/*     */ 
/*     */     GwtWorkarounds.ByteOutput encodingStream(GwtWorkarounds.CharOutput output)
/*     */     {
/* 837 */       return this.delegate.encodingStream(separatingOutput(output, this.separator, this.afterEveryChars));
/*     */     }
/*     */     
/*     */     int maxDecodedSize(int chars)
/*     */     {
/* 842 */       return this.delegate.maxDecodedSize(chars);
/*     */     }
/*     */     
/*     */     GwtWorkarounds.ByteInput decodingStream(GwtWorkarounds.CharInput input)
/*     */     {
/* 847 */       return this.delegate.decodingStream(ignoringInput(input, this.separatorChars));
/*     */     }
/*     */     
/*     */     public BaseEncoding omitPadding()
/*     */     {
/* 852 */       return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
/*     */     }
/*     */     
/*     */     public BaseEncoding withPadChar(char padChar)
/*     */     {
/* 857 */       return this.delegate.withPadChar(padChar).withSeparator(this.separator, this.afterEveryChars);
/*     */     }
/*     */     
/*     */     public BaseEncoding withSeparator(String separator, int afterEveryChars)
/*     */     {
/* 862 */       throw new UnsupportedOperationException("Already have a separator");
/*     */     }
/*     */     
/*     */     public BaseEncoding upperCase()
/*     */     {
/* 867 */       return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
/*     */     }
/*     */     
/*     */     public BaseEncoding lowerCase()
/*     */     {
/* 872 */       return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 877 */       return this.delegate.toString() + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\io\BaseEncoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */