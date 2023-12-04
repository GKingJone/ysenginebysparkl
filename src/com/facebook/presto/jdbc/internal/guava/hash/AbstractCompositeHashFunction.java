/*    */ package com.facebook.presto.jdbc.internal.guava.hash;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class AbstractCompositeHashFunction
/*    */   extends AbstractStreamingHashFunction
/*    */ {
/*    */   final HashFunction[] functions;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   AbstractCompositeHashFunction(HashFunction... functions)
/*    */   {
/* 34 */     for (HashFunction function : functions) {
/* 35 */       Preconditions.checkNotNull(function);
/*    */     }
/* 37 */     this.functions = functions;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   abstract HashCode makeHash(Hasher[] paramArrayOfHasher);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public Hasher newHasher()
/*    */   {
/* 50 */     final Hasher[] hashers = new Hasher[this.functions.length];
/* 51 */     for (int i = 0; i < hashers.length; i++) {
/* 52 */       hashers[i] = this.functions[i].newHasher();
/*    */     }
/* 54 */     new Hasher() {
/*    */       public Hasher putByte(byte b) {
/* 56 */         for (Hasher hasher : hashers) {
/* 57 */           hasher.putByte(b);
/*    */         }
/* 59 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putBytes(byte[] bytes) {
/* 63 */         for (Hasher hasher : hashers) {
/* 64 */           hasher.putBytes(bytes);
/*    */         }
/* 66 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putBytes(byte[] bytes, int off, int len) {
/* 70 */         for (Hasher hasher : hashers) {
/* 71 */           hasher.putBytes(bytes, off, len);
/*    */         }
/* 73 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putShort(short s) {
/* 77 */         for (Hasher hasher : hashers) {
/* 78 */           hasher.putShort(s);
/*    */         }
/* 80 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putInt(int i) {
/* 84 */         for (Hasher hasher : hashers) {
/* 85 */           hasher.putInt(i);
/*    */         }
/* 87 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putLong(long l) {
/* 91 */         for (Hasher hasher : hashers) {
/* 92 */           hasher.putLong(l);
/*    */         }
/* 94 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putFloat(float f) {
/* 98 */         for (Hasher hasher : hashers) {
/* 99 */           hasher.putFloat(f);
/*    */         }
/* :1 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putDouble(double d) {
/* :5 */         for (Hasher hasher : hashers) {
/* :6 */           hasher.putDouble(d);
/*    */         }
/* :8 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putBoolean(boolean b) {
/* ;2 */         for (Hasher hasher : hashers) {
/* ;3 */           hasher.putBoolean(b);
/*    */         }
/* ;5 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putChar(char c) {
/* ;9 */         for (Hasher hasher : hashers) {
/* <0 */           hasher.putChar(c);
/*    */         }
/* <2 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putUnencodedChars(CharSequence chars) {
/* <6 */         for (Hasher hasher : hashers) {
/* <7 */           hasher.putUnencodedChars(chars);
/*    */         }
/* <9 */         return this;
/*    */       }
/*    */       
/*    */       public Hasher putString(CharSequence chars, Charset charset) {
/* =3 */         for (Hasher hasher : hashers) {
/* =4 */           hasher.putString(chars, charset);
/*    */         }
/* =6 */         return this;
/*    */       }
/*    */       
/*    */       public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
/* >0 */         for (Hasher hasher : hashers) {
/* >1 */           hasher.putObject(instance, funnel);
/*    */         }
/* >3 */         return this;
/*    */       }
/*    */       
/*    */       public HashCode hash() {
/* >7 */         return AbstractCompositeHashFunction.this.makeHash(hashers);
/*    */       }
/*    */     };
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\hash\AbstractCompositeHashFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */