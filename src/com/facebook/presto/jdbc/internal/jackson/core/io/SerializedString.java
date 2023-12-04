/*     */ package com.facebook.presto.jdbc.internal.jackson.core.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SerializedString
/*     */   implements SerializableString, Serializable
/*     */ {
/*     */   protected final String _value;
/*     */   protected byte[] _quotedUTF8Ref;
/*     */   protected byte[] _unquotedUTF8Ref;
/*     */   protected char[] _quotedChars;
/*     */   protected transient String _jdkSerializeValue;
/*     */   
/*     */   public SerializedString(String v)
/*     */   {
/*  48 */     if (v == null) {
/*  49 */       throw new IllegalStateException("Null String illegal for SerializedString");
/*     */     }
/*  51 */     this._value = v;
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
/*     */   private void readObject(ObjectInputStream in)
/*     */     throws IOException
/*     */   {
/*  69 */     this._jdkSerializeValue = in.readUTF();
/*     */   }
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/*  73 */     out.writeUTF(this._value);
/*     */   }
/*     */   
/*     */   protected Object readResolve() {
/*  77 */     return new SerializedString(this._jdkSerializeValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String getValue()
/*     */   {
/*  87 */     return this._value;
/*     */   }
/*     */   
/*     */ 
/*     */   public final int charLength()
/*     */   {
/*  93 */     return this._value.length();
/*     */   }
/*     */   
/*     */   public final char[] asQuotedChars() {
/*  97 */     char[] result = this._quotedChars;
/*  98 */     if (result == null) {
/*  99 */       result = JsonStringEncoder.getInstance().quoteAsString(this._value);
/* 100 */       this._quotedChars = result;
/*     */     }
/* 102 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] asUnquotedUTF8()
/*     */   {
/* 111 */     byte[] result = this._unquotedUTF8Ref;
/* 112 */     if (result == null) {
/* 113 */       result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
/* 114 */       this._unquotedUTF8Ref = result;
/*     */     }
/* 116 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] asQuotedUTF8()
/*     */   {
/* 125 */     byte[] result = this._quotedUTF8Ref;
/* 126 */     if (result == null) {
/* 127 */       result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
/* 128 */       this._quotedUTF8Ref = result;
/*     */     }
/* 130 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int appendQuotedUTF8(byte[] buffer, int offset)
/*     */   {
/* 141 */     byte[] result = this._quotedUTF8Ref;
/* 142 */     if (result == null) {
/* 143 */       result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
/* 144 */       this._quotedUTF8Ref = result;
/*     */     }
/* 146 */     int length = result.length;
/* 147 */     if (offset + length > buffer.length) {
/* 148 */       return -1;
/*     */     }
/* 150 */     System.arraycopy(result, 0, buffer, offset, length);
/* 151 */     return length;
/*     */   }
/*     */   
/*     */   public int appendQuoted(char[] buffer, int offset)
/*     */   {
/* 156 */     char[] result = this._quotedChars;
/* 157 */     if (result == null) {
/* 158 */       result = JsonStringEncoder.getInstance().quoteAsString(this._value);
/* 159 */       this._quotedChars = result;
/*     */     }
/* 161 */     int length = result.length;
/* 162 */     if (offset + length > buffer.length) {
/* 163 */       return -1;
/*     */     }
/* 165 */     System.arraycopy(result, 0, buffer, offset, length);
/* 166 */     return length;
/*     */   }
/*     */   
/*     */   public int appendUnquotedUTF8(byte[] buffer, int offset)
/*     */   {
/* 171 */     byte[] result = this._unquotedUTF8Ref;
/* 172 */     if (result == null) {
/* 173 */       result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
/* 174 */       this._unquotedUTF8Ref = result;
/*     */     }
/* 176 */     int length = result.length;
/* 177 */     if (offset + length > buffer.length) {
/* 178 */       return -1;
/*     */     }
/* 180 */     System.arraycopy(result, 0, buffer, offset, length);
/* 181 */     return length;
/*     */   }
/*     */   
/*     */   public int appendUnquoted(char[] buffer, int offset)
/*     */   {
/* 186 */     String str = this._value;
/* 187 */     int length = str.length();
/* 188 */     if (offset + length > buffer.length) {
/* 189 */       return -1;
/*     */     }
/* 191 */     str.getChars(0, length, buffer, offset);
/* 192 */     return length;
/*     */   }
/*     */   
/*     */   public int writeQuotedUTF8(OutputStream out) throws IOException
/*     */   {
/* 197 */     byte[] result = this._quotedUTF8Ref;
/* 198 */     if (result == null) {
/* 199 */       result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
/* 200 */       this._quotedUTF8Ref = result;
/*     */     }
/* 202 */     int length = result.length;
/* 203 */     out.write(result, 0, length);
/* 204 */     return length;
/*     */   }
/*     */   
/*     */   public int writeUnquotedUTF8(OutputStream out) throws IOException
/*     */   {
/* 209 */     byte[] result = this._unquotedUTF8Ref;
/* 210 */     if (result == null) {
/* 211 */       result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
/* 212 */       this._unquotedUTF8Ref = result;
/*     */     }
/* 214 */     int length = result.length;
/* 215 */     out.write(result, 0, length);
/* 216 */     return length;
/*     */   }
/*     */   
/*     */   public int putQuotedUTF8(ByteBuffer buffer)
/*     */   {
/* 221 */     byte[] result = this._quotedUTF8Ref;
/* 222 */     if (result == null) {
/* 223 */       result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
/* 224 */       this._quotedUTF8Ref = result;
/*     */     }
/* 226 */     int length = result.length;
/* 227 */     if (length > buffer.remaining()) {
/* 228 */       return -1;
/*     */     }
/* 230 */     buffer.put(result, 0, length);
/* 231 */     return length;
/*     */   }
/*     */   
/*     */   public int putUnquotedUTF8(ByteBuffer buffer)
/*     */   {
/* 236 */     byte[] result = this._unquotedUTF8Ref;
/* 237 */     if (result == null) {
/* 238 */       result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
/* 239 */       this._unquotedUTF8Ref = result;
/*     */     }
/* 241 */     int length = result.length;
/* 242 */     if (length > buffer.remaining()) {
/* 243 */       return -1;
/*     */     }
/* 245 */     buffer.put(result, 0, length);
/* 246 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 257 */     return this._value;
/*     */   }
/*     */   
/* 260 */   public final int hashCode() { return this._value.hashCode(); }
/*     */   
/*     */   public final boolean equals(Object o)
/*     */   {
/* 264 */     if (o == this) return true;
/* 265 */     if ((o == null) || (o.getClass() != getClass())) return false;
/* 266 */     SerializedString other = (SerializedString)o;
/* 267 */     return this._value.equals(other._value);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\io\SerializedString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */