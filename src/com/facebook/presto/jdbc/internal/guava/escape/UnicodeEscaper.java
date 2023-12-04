/*     */ package com.facebook.presto.jdbc.internal.guava.escape;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ @GwtCompatible
/*     */ public abstract class UnicodeEscaper
/*     */   extends Escaper
/*     */ {
/*     */   private static final int DEST_PAD = 32;
/*     */   
/*     */   protected abstract char[] escape(int paramInt);
/*     */   
/*     */   protected int nextEscapeIndex(CharSequence csq, int start, int end)
/*     */   {
/* 117 */     int index = start;
/* 118 */     while (index < end) {
/* 119 */       int cp = codePointAt(csq, index, end);
/* 120 */       if ((cp < 0) || (escape(cp) != null)) {
/*     */         break;
/*     */       }
/* 123 */       index += (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
/*     */     }
/* 125 */     return index;
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
/*     */   public String escape(String string)
/*     */   {
/* 153 */     Preconditions.checkNotNull(string);
/* 154 */     int end = string.length();
/* 155 */     int index = nextEscapeIndex(string, 0, end);
/* 156 */     return index == end ? string : escapeSlow(string, index);
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
/*     */   protected final String escapeSlow(String s, int index)
/*     */   {
/* 177 */     int end = s.length();
/*     */     
/*     */ 
/* 180 */     char[] dest = Platform.charBufferFromThreadLocal();
/* 181 */     int destIndex = 0;
/* 182 */     int unescapedChunkStart = 0;
/*     */     
/* 184 */     while (index < end) {
/* 185 */       int cp = codePointAt(s, index, end);
/* 186 */       if (cp < 0) {
/* 187 */         throw new IllegalArgumentException("Trailing high surrogate at end of input");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 193 */       char[] escaped = escape(cp);
/* 194 */       int nextIndex = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
/* 195 */       if (escaped != null) {
/* 196 */         int charsSkipped = index - unescapedChunkStart;
/*     */         
/*     */ 
/*     */ 
/* 200 */         int sizeNeeded = destIndex + charsSkipped + escaped.length;
/* 201 */         if (dest.length < sizeNeeded) {
/* 202 */           int destLength = sizeNeeded + (end - index) + 32;
/* 203 */           dest = growBuffer(dest, destIndex, destLength);
/*     */         }
/*     */         
/* 206 */         if (charsSkipped > 0) {
/* 207 */           s.getChars(unescapedChunkStart, index, dest, destIndex);
/* 208 */           destIndex += charsSkipped;
/*     */         }
/* 210 */         if (escaped.length > 0) {
/* 211 */           System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
/* 212 */           destIndex += escaped.length;
/*     */         }
/*     */         
/* 215 */         unescapedChunkStart = nextIndex;
/*     */       }
/* 217 */       index = nextEscapeIndex(s, nextIndex, end);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 222 */     int charsSkipped = end - unescapedChunkStart;
/* 223 */     if (charsSkipped > 0) {
/* 224 */       int endIndex = destIndex + charsSkipped;
/* 225 */       if (dest.length < endIndex) {
/* 226 */         dest = growBuffer(dest, destIndex, endIndex);
/*     */       }
/* 228 */       s.getChars(unescapedChunkStart, end, dest, destIndex);
/* 229 */       destIndex = endIndex;
/*     */     }
/* 231 */     return new String(dest, 0, destIndex);
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
/*     */   protected static int codePointAt(CharSequence seq, int index, int end)
/*     */   {
/* 267 */     Preconditions.checkNotNull(seq);
/* 268 */     if (index < end) {
/* 269 */       char c1 = seq.charAt(index++);
/* 270 */       if ((c1 < 55296) || (c1 > 57343))
/*     */       {
/*     */ 
/* 273 */         return c1; }
/* 274 */       if (c1 <= 56319)
/*     */       {
/* 276 */         if (index == end) {
/* 277 */           return -c1;
/*     */         }
/*     */         
/* 280 */         c2 = seq.charAt(index);
/* 281 */         if (Character.isLowSurrogate(c2)) {
/* 282 */           return Character.toCodePoint(c1, c2);
/*     */         }
/* 284 */         c1 = c2;i = c2;int j = index;String str2 = String.valueOf(String.valueOf(seq));throw new IllegalArgumentException(89 + str2.length() + "Expected low surrogate but got char '" + c1 + "' with value " + i + " at index " + j + " in '" + str2 + "'");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 289 */       char c2 = c1;char c1 = c1;int i = index - 1;String str1 = String.valueOf(String.valueOf(seq));throw new IllegalArgumentException(88 + str1.length() + "Unexpected low surrogate character '" + c2 + "' with value " + c1 + " at index " + i + " in '" + str1 + "'");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 295 */     throw new IndexOutOfBoundsException("Index exceeds specified range");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static char[] growBuffer(char[] dest, int index, int size)
/*     */   {
/* 304 */     char[] copy = new char[size];
/* 305 */     if (index > 0) {
/* 306 */       System.arraycopy(dest, 0, copy, 0, index);
/*     */     }
/* 308 */     return copy;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\escape\UnicodeEscaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */