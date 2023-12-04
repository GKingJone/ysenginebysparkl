/*     */ package com.facebook.presto.jdbc.internal.guava.escape;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class Escapers
/*     */ {
/*     */   public static Escaper nullEscaper()
/*     */   {
/*  46 */     return NULL_ESCAPER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  51 */   private static final Escaper NULL_ESCAPER = new CharEscaper() {
/*     */     public String escape(String string) {
/*  53 */       return (String)Preconditions.checkNotNull(string);
/*     */     }
/*     */     
/*     */     protected char[] escape(char c)
/*     */     {
/*  58 */       return null;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Builder builder()
/*     */   {
/*  78 */     return new Builder(null);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   static UnicodeEscaper asUnicodeEscaper(Escaper escaper)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokestatic 39	com/facebook/presto/jdbc/internal/guava/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   4: pop
/*     */     //   5: aload_0
/*     */     //   6: instanceof 41
/*     */     //   9: ifeq +8 -> 17
/*     */     //   12: aload_0
/*     */     //   13: checkcast 41	com/facebook/presto/jdbc/internal/guava/escape/UnicodeEscaper
/*     */     //   16: areturn
/*     */     //   17: aload_0
/*     */     //   18: instanceof 43
/*     */     //   21: ifeq +11 -> 32
/*     */     //   24: aload_0
/*     */     //   25: checkcast 43	com/facebook/presto/jdbc/internal/guava/escape/CharEscaper
/*     */     //   28: invokestatic 47	com/facebook/presto/jdbc/internal/guava/escape/Escapers:wrap	(Lcom/facebook/presto/jdbc/internal/guava/escape/CharEscaper;)Lcom/facebook/presto/jdbc/internal/guava/escape/UnicodeEscaper;
/*     */     //   31: areturn
/*     */     //   32: new 49	java/lang/IllegalArgumentException
/*     */     //   35: dup
/*     */     //   36: ldc 51
/*     */     //   38: aload_0
/*     */     //   39: invokevirtual 55	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   42: invokevirtual 61	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   45: invokestatic 67	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   48: dup
/*     */     //   49: invokevirtual 71	java/lang/String:length	()I
/*     */     //   52: ifeq +9 -> 61
/*     */     //   55: invokevirtual 75	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   58: goto +12 -> 70
/*     */     //   61: pop
/*     */     //   62: new 63	java/lang/String
/*     */     //   65: dup_x1
/*     */     //   66: swap
/*     */     //   67: invokespecial 80	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */     //   70: invokespecial 81	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
/*     */     //   73: athrow
/*     */     // Line number table:
/*     */     //   Java source line #183	-> byte code offset #0
/*     */     //   Java source line #184	-> byte code offset #5
/*     */     //   Java source line #185	-> byte code offset #12
/*     */     //   Java source line #186	-> byte code offset #17
/*     */     //   Java source line #187	-> byte code offset #24
/*     */     //   Java source line #191	-> byte code offset #32
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	74	0	escaper	Escaper
/*     */   }
/*     */   
/*     */   @Beta
/*     */   public static final class Builder
/*     */   {
/*  95 */     private final Map<Character, String> replacementMap = new HashMap();
/*     */     
/*  97 */     private char safeMin = '\000';
/*  98 */     private char safeMax = 65535;
/*  99 */     private String unsafeReplacement = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder setSafeRange(char safeMin, char safeMax)
/*     */     {
/* 115 */       this.safeMin = safeMin;
/* 116 */       this.safeMax = safeMax;
/* 117 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder setUnsafeReplacement(@Nullable String unsafeReplacement)
/*     */     {
/* 130 */       this.unsafeReplacement = unsafeReplacement;
/* 131 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder addEscape(char c, String replacement)
/*     */     {
/* 146 */       Preconditions.checkNotNull(replacement);
/*     */       
/* 148 */       this.replacementMap.put(Character.valueOf(c), replacement);
/* 149 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Escaper build()
/*     */     {
/* 156 */       new ArrayBasedCharEscaper(this.replacementMap, this.safeMin, this.safeMax) {
/* 157 */         private final char[] replacementChars = Builder.this.unsafeReplacement != null ? Builder.this.unsafeReplacement.toCharArray() : null;
/*     */         
/*     */         protected char[] escapeUnsafe(char c) {
/* 160 */           return this.replacementChars;
/*     */         }
/*     */       };
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
/*     */   public static String computeReplacement(CharEscaper escaper, char c)
/*     */   {
/* 206 */     return stringOrNull(escaper.escape(c));
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
/*     */   public static String computeReplacement(UnicodeEscaper escaper, int cp)
/*     */   {
/* 220 */     return stringOrNull(escaper.escape(cp));
/*     */   }
/*     */   
/*     */   private static String stringOrNull(char[] in) {
/* 224 */     return in == null ? null : new String(in);
/*     */   }
/*     */   
/*     */   private static UnicodeEscaper wrap(CharEscaper escaper)
/*     */   {
/* 229 */     new UnicodeEscaper()
/*     */     {
/*     */       protected char[] escape(int cp) {
/* 232 */         if (cp < 65536) {
/* 233 */           return this.val$escaper.escape((char)cp);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 239 */         char[] surrogateChars = new char[2];
/* 240 */         Character.toChars(cp, surrogateChars, 0);
/* 241 */         char[] hiChars = this.val$escaper.escape(surrogateChars[0]);
/* 242 */         char[] loChars = this.val$escaper.escape(surrogateChars[1]);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 248 */         if ((hiChars == null) && (loChars == null))
/*     */         {
/* 250 */           return null;
/*     */         }
/*     */         
/* 253 */         int hiCount = hiChars != null ? hiChars.length : 1;
/* 254 */         int loCount = loChars != null ? loChars.length : 1;
/* 255 */         char[] output = new char[hiCount + loCount];
/* 256 */         if (hiChars != null)
/*     */         {
/* 258 */           for (int n = 0; n < hiChars.length; n++) {
/* 259 */             output[n] = hiChars[n];
/*     */           }
/*     */         } else {
/* 262 */           output[0] = surrogateChars[0];
/*     */         }
/* 264 */         if (loChars != null) {
/* 265 */           for (int n = 0; n < loChars.length; n++) {
/* 266 */             output[(hiCount + n)] = loChars[n];
/*     */           }
/*     */         } else {
/* 269 */           output[hiCount] = surrogateChars[1];
/*     */         }
/* 271 */         return output;
/*     */       }
/*     */     };
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\escape\Escapers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */