/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Buffer
/*     */ {
/*     */   static final int MAX_BYTES_TO_DUMP = 512;
/*     */   static final int NO_LENGTH_LIMIT = -1;
/*     */   static final long NULL_LENGTH = -1L;
/*  40 */   private int bufLength = 0;
/*     */   
/*     */   private byte[] byteBuffer;
/*     */   
/*  44 */   private int position = 0;
/*     */   
/*  46 */   protected boolean wasMultiPacket = false;
/*     */   
/*     */   public static final short TYPE_ID_ERROR = 255;
/*     */   
/*     */   public static final short TYPE_ID_EOF = 254;
/*     */   public static final short TYPE_ID_AUTH_SWITCH = 254;
/*     */   public static final short TYPE_ID_LOCAL_INFILE = 251;
/*     */   public static final short TYPE_ID_OK = 0;
/*     */   
/*     */   public Buffer(byte[] buf)
/*     */   {
/*  57 */     this.byteBuffer = buf;
/*  58 */     setBufLength(buf.length);
/*     */   }
/*     */   
/*     */   Buffer(int size) {
/*  62 */     this.byteBuffer = new byte[size];
/*  63 */     setBufLength(this.byteBuffer.length);
/*  64 */     this.position = 4;
/*     */   }
/*     */   
/*     */   final void clear() {
/*  68 */     this.position = 4;
/*     */   }
/*     */   
/*     */   final void dump() {
/*  72 */     dump(getBufLength());
/*     */   }
/*     */   
/*     */   final String dump(int numBytes) {
/*  76 */     return StringUtils.dumpAsHex(getBytes(0, numBytes > getBufLength() ? getBufLength() : numBytes), numBytes > getBufLength() ? getBufLength() : numBytes);
/*     */   }
/*     */   
/*     */   final String dumpClampedBytes(int numBytes) {
/*  80 */     int numBytesToDump = numBytes < 512 ? numBytes : 512;
/*     */     
/*  82 */     String dumped = StringUtils.dumpAsHex(getBytes(0, numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump), numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump);
/*     */     
/*     */ 
/*  85 */     if (numBytesToDump < numBytes) {
/*  86 */       return dumped + " ....(packet exceeds max. dump length)";
/*     */     }
/*     */     
/*  89 */     return dumped;
/*     */   }
/*     */   
/*     */   final void dumpHeader() {
/*  93 */     for (int i = 0; i < 4; i++) {
/*  94 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */       
/*  96 */       if (hexVal.length() == 1) {
/*  97 */         hexVal = "0" + hexVal;
/*     */       }
/*     */       
/* 100 */       System.out.print(hexVal + " ");
/*     */     }
/*     */   }
/*     */   
/*     */   final void dumpNBytes(int start, int nBytes) {
/* 105 */     StringBuilder asciiBuf = new StringBuilder();
/*     */     
/* 107 */     for (int i = start; (i < start + nBytes) && (i < getBufLength()); i++) {
/* 108 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */       
/* 110 */       if (hexVal.length() == 1) {
/* 111 */         hexVal = "0" + hexVal;
/*     */       }
/*     */       
/* 114 */       System.out.print(hexVal + " ");
/*     */       
/* 116 */       if ((readByte(i) > 32) && (readByte(i) < Byte.MAX_VALUE)) {
/* 117 */         asciiBuf.append((char)readByte(i));
/*     */       } else {
/* 119 */         asciiBuf.append(".");
/*     */       }
/*     */       
/* 122 */       asciiBuf.append(" ");
/*     */     }
/*     */     
/* 125 */     System.out.println("    " + asciiBuf.toString());
/*     */   }
/*     */   
/*     */   final void ensureCapacity(int additionalData) throws SQLException {
/* 129 */     if (this.position + additionalData > getBufLength()) {
/* 130 */       if (this.position + additionalData < this.byteBuffer.length)
/*     */       {
/*     */ 
/*     */ 
/* 134 */         setBufLength(this.byteBuffer.length);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 139 */         int newLength = (int)(this.byteBuffer.length * 1.25D);
/*     */         
/* 141 */         if (newLength < this.byteBuffer.length + additionalData) {
/* 142 */           newLength = this.byteBuffer.length + (int)(additionalData * 1.25D);
/*     */         }
/*     */         
/* 145 */         if (newLength < this.byteBuffer.length) {
/* 146 */           newLength = this.byteBuffer.length + additionalData;
/*     */         }
/*     */         
/* 149 */         byte[] newBytes = new byte[newLength];
/*     */         
/* 151 */         System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
/* 152 */         this.byteBuffer = newBytes;
/* 153 */         setBufLength(this.byteBuffer.length);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int fastSkipLenString()
/*     */   {
/* 164 */     long len = readFieldLength();
/*     */     
/* 166 */     this.position = ((int)(this.position + len));
/*     */     
/* 168 */     return (int)len;
/*     */   }
/*     */   
/*     */   public void fastSkipLenByteArray() {
/* 172 */     long len = readFieldLength();
/*     */     
/* 174 */     if ((len == -1L) || (len == 0L)) {
/* 175 */       return;
/*     */     }
/*     */     
/* 178 */     this.position = ((int)(this.position + len));
/*     */   }
/*     */   
/*     */   protected final byte[] getBufferSource() {
/* 182 */     return this.byteBuffer;
/*     */   }
/*     */   
/*     */   public int getBufLength() {
/* 186 */     return this.bufLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getByteBuffer()
/*     */   {
/* 195 */     return this.byteBuffer;
/*     */   }
/*     */   
/*     */   final byte[] getBytes(int len) {
/* 199 */     byte[] b = new byte[len];
/* 200 */     System.arraycopy(this.byteBuffer, this.position, b, 0, len);
/* 201 */     this.position += len;
/*     */     
/* 203 */     return b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   byte[] getBytes(int offset, int len)
/*     */   {
/* 212 */     byte[] dest = new byte[len];
/* 213 */     System.arraycopy(this.byteBuffer, offset, dest, 0, len);
/*     */     
/* 215 */     return dest;
/*     */   }
/*     */   
/*     */   int getCapacity() {
/* 219 */     return this.byteBuffer.length;
/*     */   }
/*     */   
/*     */   public ByteBuffer getNioBuffer() {
/* 223 */     throw new IllegalArgumentException(Messages.getString("ByteArrayBuffer.0"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPosition()
/*     */   {
/* 232 */     return this.position;
/*     */   }
/*     */   
/*     */   final boolean isEOFPacket() {
/* 236 */     return ((this.byteBuffer[0] & 0xFF) == 254) && (getBufLength() <= 5);
/*     */   }
/*     */   
/*     */   final boolean isAuthMethodSwitchRequestPacket() {
/* 240 */     return (this.byteBuffer[0] & 0xFF) == 254;
/*     */   }
/*     */   
/*     */   final boolean isOKPacket() {
/* 244 */     return (this.byteBuffer[0] & 0xFF) == 0;
/*     */   }
/*     */   
/*     */   final boolean isResultSetOKPacket() {
/* 248 */     return ((this.byteBuffer[0] & 0xFF) == 254) && (getBufLength() < 16777215);
/*     */   }
/*     */   
/*     */   final boolean isRawPacket() {
/* 252 */     return (this.byteBuffer[0] & 0xFF) == 1;
/*     */   }
/*     */   
/*     */   final long newReadLength() {
/* 256 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */     
/* 258 */     switch (sw) {
/*     */     case 251: 
/* 260 */       return 0L;
/*     */     
/*     */     case 252: 
/* 263 */       return readInt();
/*     */     
/*     */     case 253: 
/* 266 */       return readLongInt();
/*     */     
/*     */     case 254: 
/* 269 */       return readLongLong();
/*     */     }
/*     */     
/* 272 */     return sw;
/*     */   }
/*     */   
/*     */   final byte readByte()
/*     */   {
/* 277 */     return this.byteBuffer[(this.position++)];
/*     */   }
/*     */   
/*     */   final byte readByte(int readAt) {
/* 281 */     return this.byteBuffer[readAt];
/*     */   }
/*     */   
/*     */   final long readFieldLength() {
/* 285 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */     
/* 287 */     switch (sw) {
/*     */     case 251: 
/* 289 */       return -1L;
/*     */     
/*     */     case 252: 
/* 292 */       return readInt();
/*     */     
/*     */     case 253: 
/* 295 */       return readLongInt();
/*     */     
/*     */     case 254: 
/* 298 */       return readLongLong();
/*     */     }
/*     */     
/* 301 */     return sw;
/*     */   }
/*     */   
/*     */   final int readInt()
/*     */   {
/* 306 */     byte[] b = this.byteBuffer;
/*     */     
/* 308 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8;
/*     */   }
/*     */   
/*     */   final int readIntAsLong() {
/* 312 */     byte[] b = this.byteBuffer;
/*     */     
/* 314 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24;
/*     */   }
/*     */   
/*     */   final byte[] readLenByteArray(int offset) {
/* 318 */     long len = readFieldLength();
/*     */     
/* 320 */     if (len == -1L) {
/* 321 */       return null;
/*     */     }
/*     */     
/* 324 */     if (len == 0L) {
/* 325 */       return Constants.EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 328 */     this.position += offset;
/*     */     
/* 330 */     return getBytes((int)len);
/*     */   }
/*     */   
/*     */   final long readLength() {
/* 334 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */     
/* 336 */     switch (sw) {
/*     */     case 251: 
/* 338 */       return 0L;
/*     */     
/*     */     case 252: 
/* 341 */       return readInt();
/*     */     
/*     */     case 253: 
/* 344 */       return readLongInt();
/*     */     
/*     */     case 254: 
/* 347 */       return readLong();
/*     */     }
/*     */     
/* 350 */     return sw;
/*     */   }
/*     */   
/*     */   final long readLong()
/*     */   {
/* 355 */     byte[] b = this.byteBuffer;
/*     */     
/* 357 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24;
/*     */   }
/*     */   
/*     */   final int readLongInt()
/*     */   {
/* 362 */     byte[] b = this.byteBuffer;
/*     */     
/* 364 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16;
/*     */   }
/*     */   
/*     */   final long readLongLong() {
/* 368 */     byte[] b = this.byteBuffer;
/*     */     
/* 370 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24 | (b[(this.position++)] & 0xFF) << 32 | (b[(this.position++)] & 0xFF) << 40 | (b[(this.position++)] & 0xFF) << 48 | (b[(this.position++)] & 0xFF) << 56;
/*     */   }
/*     */   
/*     */ 
/*     */   final int readnBytes()
/*     */   {
/* 376 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */     
/* 378 */     switch (sw) {
/*     */     case 1: 
/* 380 */       return this.byteBuffer[(this.position++)] & 0xFF;
/*     */     
/*     */     case 2: 
/* 383 */       return readInt();
/*     */     
/*     */     case 3: 
/* 386 */       return readLongInt();
/*     */     
/*     */     case 4: 
/* 389 */       return (int)readLong();
/*     */     }
/*     */     
/* 392 */     return 255;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String readString()
/*     */   {
/* 402 */     int i = this.position;
/* 403 */     int len = 0;
/* 404 */     int maxLen = getBufLength();
/*     */     
/* 406 */     while ((i < maxLen) && (this.byteBuffer[i] != 0)) {
/* 407 */       len++;
/* 408 */       i++;
/*     */     }
/*     */     
/* 411 */     String s = StringUtils.toString(this.byteBuffer, this.position, len);
/* 412 */     this.position += len + 1;
/*     */     
/* 414 */     return s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final String readString(String encoding, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/* 425 */     int i = this.position;
/* 426 */     int len = 0;
/* 427 */     int maxLen = getBufLength();
/*     */     
/* 429 */     while ((i < maxLen) && (this.byteBuffer[i] != 0)) {
/* 430 */       len++;
/* 431 */       i++;
/*     */     }
/*     */     try
/*     */     {
/* 435 */       return StringUtils.toString(this.byteBuffer, this.position, len, encoding);
/*     */     } catch (UnsupportedEncodingException uEE) {
/* 437 */       throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.1") + encoding + "'", "S1009", exceptionInterceptor);
/*     */     }
/*     */     finally {
/* 440 */       this.position += len + 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   final String readString(String encoding, ExceptionInterceptor exceptionInterceptor, int expectedLength)
/*     */     throws SQLException
/*     */   {
/* 448 */     if (this.position + expectedLength > getBufLength()) {
/* 449 */       throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.2"), "S1009", exceptionInterceptor);
/*     */     }
/*     */     try
/*     */     {
/* 453 */       return StringUtils.toString(this.byteBuffer, this.position, expectedLength, encoding);
/*     */     } catch (UnsupportedEncodingException uEE) {
/* 455 */       throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.1") + encoding + "'", "S1009", exceptionInterceptor);
/*     */     }
/*     */     finally {
/* 458 */       this.position += expectedLength;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBufLength(int bufLengthToSet) {
/* 463 */     this.bufLength = bufLengthToSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setByteBuffer(byte[] byteBufferToSet)
/*     */   {
/* 473 */     this.byteBuffer = byteBufferToSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPosition(int positionToSet)
/*     */   {
/* 483 */     this.position = positionToSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setWasMultiPacket(boolean flag)
/*     */   {
/* 493 */     this.wasMultiPacket = flag;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 498 */     return dumpClampedBytes(getPosition());
/*     */   }
/*     */   
/*     */   public String toSuperString() {
/* 502 */     return super.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean wasMultiPacket()
/*     */   {
/* 511 */     return this.wasMultiPacket;
/*     */   }
/*     */   
/*     */   public final void writeByte(byte b) throws SQLException {
/* 515 */     ensureCapacity(1);
/*     */     
/* 517 */     this.byteBuffer[(this.position++)] = b;
/*     */   }
/*     */   
/*     */   public final void writeBytesNoNull(byte[] bytes) throws SQLException
/*     */   {
/* 522 */     int len = bytes.length;
/* 523 */     ensureCapacity(len);
/* 524 */     System.arraycopy(bytes, 0, this.byteBuffer, this.position, len);
/* 525 */     this.position += len;
/*     */   }
/*     */   
/*     */   final void writeBytesNoNull(byte[] bytes, int offset, int length) throws SQLException
/*     */   {
/* 530 */     ensureCapacity(length);
/* 531 */     System.arraycopy(bytes, offset, this.byteBuffer, this.position, length);
/* 532 */     this.position += length;
/*     */   }
/*     */   
/*     */   final void writeDouble(double d) throws SQLException {
/* 536 */     long l = Double.doubleToLongBits(d);
/* 537 */     writeLongLong(l);
/*     */   }
/*     */   
/*     */   final void writeFieldLength(long length) throws SQLException {
/* 541 */     if (length < 251L) {
/* 542 */       writeByte((byte)(int)length);
/* 543 */     } else if (length < 65536L) {
/* 544 */       ensureCapacity(3);
/* 545 */       writeByte((byte)-4);
/* 546 */       writeInt((int)length);
/* 547 */     } else if (length < 16777216L) {
/* 548 */       ensureCapacity(4);
/* 549 */       writeByte((byte)-3);
/* 550 */       writeLongInt((int)length);
/*     */     } else {
/* 552 */       ensureCapacity(9);
/* 553 */       writeByte((byte)-2);
/* 554 */       writeLongLong(length);
/*     */     }
/*     */   }
/*     */   
/*     */   final void writeFloat(float f) throws SQLException {
/* 559 */     ensureCapacity(4);
/*     */     
/* 561 */     int i = Float.floatToIntBits(f);
/* 562 */     byte[] b = this.byteBuffer;
/* 563 */     b[(this.position++)] = ((byte)(i & 0xFF));
/* 564 */     b[(this.position++)] = ((byte)(i >>> 8));
/* 565 */     b[(this.position++)] = ((byte)(i >>> 16));
/* 566 */     b[(this.position++)] = ((byte)(i >>> 24));
/*     */   }
/*     */   
/*     */   final void writeInt(int i) throws SQLException {
/* 570 */     ensureCapacity(2);
/*     */     
/* 572 */     byte[] b = this.byteBuffer;
/* 573 */     b[(this.position++)] = ((byte)(i & 0xFF));
/* 574 */     b[(this.position++)] = ((byte)(i >>> 8));
/*     */   }
/*     */   
/*     */   final void writeLenBytes(byte[] b) throws SQLException
/*     */   {
/* 579 */     int len = b.length;
/* 580 */     ensureCapacity(len + 9);
/* 581 */     writeFieldLength(len);
/* 582 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 583 */     this.position += len;
/*     */   }
/*     */   
/*     */   final void writeLenString(String s, String encoding, String serverEncoding, SingleByteCharsetConverter converter, boolean parserKnowsUnicode, MySQLConnection conn)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 589 */     byte[] b = null;
/*     */     
/* 591 */     if (converter != null) {
/* 592 */       b = converter.toBytes(s);
/*     */     } else {
/* 594 */       b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn, conn.getExceptionInterceptor());
/*     */     }
/*     */     
/* 597 */     int len = b.length;
/* 598 */     ensureCapacity(len + 9);
/* 599 */     writeFieldLength(len);
/* 600 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 601 */     this.position += len;
/*     */   }
/*     */   
/*     */   final void writeLong(long i) throws SQLException {
/* 605 */     ensureCapacity(4);
/*     */     
/* 607 */     byte[] b = this.byteBuffer;
/* 608 */     b[(this.position++)] = ((byte)(int)(i & 0xFF));
/* 609 */     b[(this.position++)] = ((byte)(int)(i >>> 8));
/* 610 */     b[(this.position++)] = ((byte)(int)(i >>> 16));
/* 611 */     b[(this.position++)] = ((byte)(int)(i >>> 24));
/*     */   }
/*     */   
/*     */   final void writeLongInt(int i) throws SQLException {
/* 615 */     ensureCapacity(3);
/* 616 */     byte[] b = this.byteBuffer;
/* 617 */     b[(this.position++)] = ((byte)(i & 0xFF));
/* 618 */     b[(this.position++)] = ((byte)(i >>> 8));
/* 619 */     b[(this.position++)] = ((byte)(i >>> 16));
/*     */   }
/*     */   
/*     */   final void writeLongLong(long i) throws SQLException {
/* 623 */     ensureCapacity(8);
/* 624 */     byte[] b = this.byteBuffer;
/* 625 */     b[(this.position++)] = ((byte)(int)(i & 0xFF));
/* 626 */     b[(this.position++)] = ((byte)(int)(i >>> 8));
/* 627 */     b[(this.position++)] = ((byte)(int)(i >>> 16));
/* 628 */     b[(this.position++)] = ((byte)(int)(i >>> 24));
/* 629 */     b[(this.position++)] = ((byte)(int)(i >>> 32));
/* 630 */     b[(this.position++)] = ((byte)(int)(i >>> 40));
/* 631 */     b[(this.position++)] = ((byte)(int)(i >>> 48));
/* 632 */     b[(this.position++)] = ((byte)(int)(i >>> 56));
/*     */   }
/*     */   
/*     */   final void writeString(String s) throws SQLException
/*     */   {
/* 637 */     ensureCapacity(s.length() * 3 + 1);
/* 638 */     writeStringNoNull(s);
/* 639 */     this.byteBuffer[(this.position++)] = 0;
/*     */   }
/*     */   
/*     */   final void writeString(String s, String encoding, MySQLConnection conn) throws SQLException
/*     */   {
/* 644 */     ensureCapacity(s.length() * 3 + 1);
/*     */     try {
/* 646 */       writeStringNoNull(s, encoding, encoding, false, conn);
/*     */     } catch (UnsupportedEncodingException ue) {
/* 648 */       throw new SQLException(ue.toString(), "S1000");
/*     */     }
/*     */     
/* 651 */     this.byteBuffer[(this.position++)] = 0;
/*     */   }
/*     */   
/*     */   final void writeStringNoNull(String s) throws SQLException
/*     */   {
/* 656 */     int len = s.length();
/* 657 */     ensureCapacity(len * 3);
/* 658 */     System.arraycopy(StringUtils.getBytes(s), 0, this.byteBuffer, this.position, len);
/* 659 */     this.position += len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final void writeStringNoNull(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 670 */     byte[] b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn, conn.getExceptionInterceptor());
/*     */     
/* 672 */     int len = b.length;
/* 673 */     ensureCapacity(len);
/* 674 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 675 */     this.position += len;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Buffer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */