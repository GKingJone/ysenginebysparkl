/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Utf8Appendable
/*     */ {
/*  53 */   protected static final Logger LOG = Log.getLogger(Utf8Appendable.class);
/*     */   public static final char REPLACEMENT = '�';
/*  55 */   public static final byte[] REPLACEMENT_UTF8 = { -17, -65, -67 };
/*     */   
/*     */   private static final int UTF8_ACCEPT = 0;
/*     */   private static final int UTF8_REJECT = 12;
/*     */   protected final Appendable _appendable;
/*  60 */   protected int _state = 0;
/*     */   
/*  62 */   private static final byte[] BYTE_TABLE = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */   private static final byte[] TRANS_TABLE = { 0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int _codep;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Utf8Appendable(Appendable appendable)
/*     */   {
/*  91 */     this._appendable = appendable;
/*     */   }
/*     */   
/*     */   public abstract int length();
/*     */   
/*     */   protected void reset()
/*     */   {
/*  98 */     this._state = 0;
/*     */   }
/*     */   
/*     */   private void checkCharAppend()
/*     */     throws IOException
/*     */   {
/* 104 */     if (this._state != 0)
/*     */     {
/* 106 */       this._appendable.append(65533);
/* 107 */       int state = this._state;
/* 108 */       this._state = 0;
/* 109 */       throw new NotUtf8Exception("char appended in state " + state);
/*     */     }
/*     */   }
/*     */   
/*     */   public void append(char c)
/*     */   {
/*     */     try
/*     */     {
/* 117 */       checkCharAppend();
/* 118 */       this._appendable.append(c);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 122 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void append(String s)
/*     */   {
/*     */     try
/*     */     {
/* 130 */       checkCharAppend();
/* 131 */       this._appendable.append(s);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 135 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void append(String s, int offset, int length)
/*     */   {
/*     */     try
/*     */     {
/* 143 */       checkCharAppend();
/* 144 */       this._appendable.append(s, offset, offset + length);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 148 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void append(byte b)
/*     */   {
/*     */     try
/*     */     {
/* 157 */       appendByte(b);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 161 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void append(ByteBuffer buf)
/*     */   {
/*     */     try
/*     */     {
/* 169 */       while (buf.remaining() > 0)
/*     */       {
/* 171 */         appendByte(buf.get());
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 176 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void append(byte[] b, int offset, int length)
/*     */   {
/*     */     try
/*     */     {
/* 184 */       int end = offset + length;
/* 185 */       for (int i = offset; i < end; i++) {
/* 186 */         appendByte(b[i]);
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 190 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean append(byte[] b, int offset, int length, int maxChars)
/*     */   {
/*     */     try
/*     */     {
/* 198 */       int end = offset + length;
/* 199 */       for (int i = offset; i < end; i++)
/*     */       {
/* 201 */         if (length() > maxChars)
/* 202 */           return false;
/* 203 */         appendByte(b[i]);
/*     */       }
/* 205 */       return true;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 209 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void appendByte(byte b)
/*     */     throws IOException
/*     */   {
/* 216 */     if ((b > 0) && (this._state == 0))
/*     */     {
/* 218 */       this._appendable.append((char)(b & 0xFF));
/*     */     }
/*     */     else
/*     */     {
/* 222 */       int i = b & 0xFF;
/* 223 */       int type = BYTE_TABLE[i];
/* 224 */       this._codep = (this._state == 0 ? 255 >> type & i : i & 0x3F | this._codep << 6);
/* 225 */       int next = TRANS_TABLE[(this._state + type)];
/*     */       
/* 227 */       switch (next)
/*     */       {
/*     */       case 0: 
/* 230 */         this._state = next;
/* 231 */         if (this._codep < 55296)
/*     */         {
/* 233 */           this._appendable.append((char)this._codep);
/*     */         }
/*     */         else
/*     */         {
/* 237 */           for (char c : Character.toChars(this._codep))
/* 238 */             this._appendable.append(c);
/*     */         }
/* 240 */         break;
/*     */       
/*     */       case 12: 
/* 243 */         String reason = "byte " + TypeUtil.toHexString(b) + " in state " + this._state / 12;
/* 244 */         this._codep = 0;
/* 245 */         this._state = 0;
/* 246 */         this._appendable.append(65533);
/* 247 */         throw new NotUtf8Exception(reason);
/*     */       
/*     */       default: 
/* 250 */         this._state = next;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isUtf8SequenceComplete()
/*     */   {
/* 258 */     return this._state == 0;
/*     */   }
/*     */   
/*     */   public static class NotUtf8Exception
/*     */     extends IllegalArgumentException
/*     */   {
/*     */     public NotUtf8Exception(String reason)
/*     */     {
/* 266 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void checkState()
/*     */   {
/* 272 */     if (!isUtf8SequenceComplete())
/*     */     {
/* 274 */       this._codep = 0;
/* 275 */       this._state = 0;
/*     */       try
/*     */       {
/* 278 */         this._appendable.append(65533);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 282 */         throw new RuntimeException(e);
/*     */       }
/* 284 */       throw new NotUtf8Exception("incomplete UTF8 sequence");
/*     */     }
/*     */   }
/*     */   
/*     */   public String toReplacedString()
/*     */   {
/* 290 */     if (!isUtf8SequenceComplete())
/*     */     {
/* 292 */       this._codep = 0;
/* 293 */       this._state = 0;
/*     */       try
/*     */       {
/* 296 */         this._appendable.append(65533);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 300 */         throw new RuntimeException(e);
/*     */       }
/* 302 */       Throwable th = new NotUtf8Exception("incomplete UTF8 sequence");
/* 303 */       LOG.warn(th.toString(), new Object[0]);
/* 304 */       LOG.debug(th);
/*     */     }
/* 306 */     return this._appendable.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Utf8Appendable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */