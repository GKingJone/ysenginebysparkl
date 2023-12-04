/*     */ package com.mysql.jdbc.profiler;
/*     */ 
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProfilerEvent
/*     */ {
/*     */   public static final byte TYPE_WARN = 0;
/*     */   public static final byte TYPE_OBJECT_CREATION = 1;
/*     */   public static final byte TYPE_PREPARE = 2;
/*     */   public static final byte TYPE_QUERY = 3;
/*     */   public static final byte TYPE_EXECUTE = 4;
/*     */   public static final byte TYPE_FETCH = 5;
/*     */   public static final byte TYPE_SLOW_QUERY = 6;
/*     */   protected byte eventType;
/*     */   protected long connectionId;
/*     */   protected int statementId;
/*     */   protected int resultSetId;
/*     */   protected long eventCreationTime;
/*     */   protected long eventDuration;
/*     */   protected String durationUnits;
/*     */   protected int hostNameIndex;
/*     */   protected String hostName;
/*     */   protected int catalogIndex;
/*     */   protected String catalog;
/*     */   protected int eventCreationPointIndex;
/*     */   protected String eventCreationPointDesc;
/*     */   protected String message;
/*     */   
/*     */   public ProfilerEvent(byte eventType, String hostName, String catalog, long connectionId, int statementId, int resultSetId, long eventCreationTime, long eventDuration, String durationUnits, String eventCreationPointDesc, String eventCreationPoint, String message)
/*     */   {
/* 169 */     this.eventType = eventType;
/* 170 */     this.connectionId = connectionId;
/* 171 */     this.statementId = statementId;
/* 172 */     this.resultSetId = resultSetId;
/* 173 */     this.eventCreationTime = eventCreationTime;
/* 174 */     this.eventDuration = eventDuration;
/* 175 */     this.durationUnits = durationUnits;
/* 176 */     this.eventCreationPointDesc = eventCreationPointDesc;
/* 177 */     this.message = message;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getEventCreationPointAsString()
/*     */   {
/* 186 */     return this.eventCreationPointDesc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 196 */     StringBuilder buf = new StringBuilder(32);
/*     */     
/* 198 */     switch (this.eventType) {
/*     */     case 4: 
/* 200 */       buf.append("EXECUTE");
/* 201 */       break;
/*     */     
/*     */     case 5: 
/* 204 */       buf.append("FETCH");
/* 205 */       break;
/*     */     
/*     */     case 1: 
/* 208 */       buf.append("CONSTRUCT");
/* 209 */       break;
/*     */     
/*     */     case 2: 
/* 212 */       buf.append("PREPARE");
/* 213 */       break;
/*     */     
/*     */     case 3: 
/* 216 */       buf.append("QUERY");
/* 217 */       break;
/*     */     
/*     */     case 0: 
/* 220 */       buf.append("WARN");
/* 221 */       break;
/*     */     case 6: 
/* 223 */       buf.append("SLOW QUERY");
/* 224 */       break;
/*     */     default: 
/* 226 */       buf.append("UNKNOWN");
/*     */     }
/*     */     
/* 229 */     buf.append(" created: ");
/* 230 */     buf.append(new Date(this.eventCreationTime));
/* 231 */     buf.append(" duration: ");
/* 232 */     buf.append(this.eventDuration);
/* 233 */     buf.append(" connection: ");
/* 234 */     buf.append(this.connectionId);
/* 235 */     buf.append(" statement: ");
/* 236 */     buf.append(this.statementId);
/* 237 */     buf.append(" resultset: ");
/* 238 */     buf.append(this.resultSetId);
/*     */     
/* 240 */     if (this.message != null) {
/* 241 */       buf.append(" message: ");
/* 242 */       buf.append(this.message);
/*     */     }
/*     */     
/*     */ 
/* 246 */     if (this.eventCreationPointDesc != null) {
/* 247 */       buf.append("\n\nEvent Created at:\n");
/* 248 */       buf.append(this.eventCreationPointDesc);
/*     */     }
/*     */     
/* 251 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ProfilerEvent unpack(byte[] buf)
/*     */     throws Exception
/*     */   {
/* 264 */     int pos = 0;
/*     */     
/* 266 */     byte eventType = buf[(pos++)];
/* 267 */     long connectionId = readInt(buf, pos);
/* 268 */     pos += 8;
/* 269 */     int statementId = readInt(buf, pos);
/* 270 */     pos += 4;
/* 271 */     int resultSetId = readInt(buf, pos);
/* 272 */     pos += 4;
/* 273 */     long eventCreationTime = readLong(buf, pos);
/* 274 */     pos += 8;
/* 275 */     long eventDuration = readLong(buf, pos);
/* 276 */     pos += 4;
/*     */     
/* 278 */     byte[] eventDurationUnits = readBytes(buf, pos);
/* 279 */     pos += 4;
/*     */     
/* 281 */     if (eventDurationUnits != null) {
/* 282 */       pos += eventDurationUnits.length;
/*     */     }
/*     */     
/* 285 */     readInt(buf, pos);
/* 286 */     pos += 4;
/* 287 */     byte[] eventCreationAsBytes = readBytes(buf, pos);
/* 288 */     pos += 4;
/*     */     
/* 290 */     if (eventCreationAsBytes != null) {
/* 291 */       pos += eventCreationAsBytes.length;
/*     */     }
/*     */     
/* 294 */     byte[] message = readBytes(buf, pos);
/* 295 */     pos += 4;
/*     */     
/* 297 */     if (message != null) {
/* 298 */       pos += message.length;
/*     */     }
/*     */     
/* 301 */     return new ProfilerEvent(eventType, "", "", connectionId, statementId, resultSetId, eventCreationTime, eventDuration, StringUtils.toString(eventDurationUnits, "ISO8859_1"), StringUtils.toString(eventCreationAsBytes, "ISO8859_1"), null, StringUtils.toString(message, "ISO8859_1"));
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
/*     */   public byte[] pack()
/*     */     throws Exception
/*     */   {
/* 315 */     int len = 29;
/*     */     
/* 317 */     byte[] eventCreationAsBytes = null;
/*     */     
/* 319 */     getEventCreationPointAsString();
/*     */     
/* 321 */     if (this.eventCreationPointDesc != null) {
/* 322 */       eventCreationAsBytes = StringUtils.getBytes(this.eventCreationPointDesc, "ISO8859_1");
/* 323 */       len += 4 + eventCreationAsBytes.length;
/*     */     } else {
/* 325 */       len += 4;
/*     */     }
/*     */     
/* 328 */     byte[] messageAsBytes = null;
/*     */     
/* 330 */     if (this.message != null) {
/* 331 */       messageAsBytes = StringUtils.getBytes(this.message, "ISO8859_1");
/* 332 */       len += 4 + messageAsBytes.length;
/*     */     } else {
/* 334 */       len += 4;
/*     */     }
/*     */     
/* 337 */     byte[] durationUnitsAsBytes = null;
/*     */     
/* 339 */     if (this.durationUnits != null) {
/* 340 */       durationUnitsAsBytes = StringUtils.getBytes(this.durationUnits, "ISO8859_1");
/* 341 */       len += 4 + durationUnitsAsBytes.length;
/*     */     } else {
/* 343 */       len += 4;
/* 344 */       durationUnitsAsBytes = StringUtils.getBytes("", "ISO8859_1");
/*     */     }
/*     */     
/* 347 */     byte[] buf = new byte[len];
/*     */     
/* 349 */     int pos = 0;
/*     */     
/* 351 */     buf[(pos++)] = this.eventType;
/* 352 */     pos = writeLong(this.connectionId, buf, pos);
/* 353 */     pos = writeInt(this.statementId, buf, pos);
/* 354 */     pos = writeInt(this.resultSetId, buf, pos);
/* 355 */     pos = writeLong(this.eventCreationTime, buf, pos);
/* 356 */     pos = writeLong(this.eventDuration, buf, pos);
/* 357 */     pos = writeBytes(durationUnitsAsBytes, buf, pos);
/* 358 */     pos = writeInt(this.eventCreationPointIndex, buf, pos);
/*     */     
/* 360 */     if (eventCreationAsBytes != null) {
/* 361 */       pos = writeBytes(eventCreationAsBytes, buf, pos);
/*     */     } else {
/* 363 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */     
/* 366 */     if (messageAsBytes != null) {
/* 367 */       pos = writeBytes(messageAsBytes, buf, pos);
/*     */     } else {
/* 369 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */     
/* 372 */     return buf;
/*     */   }
/*     */   
/*     */   private static int writeInt(int i, byte[] buf, int pos)
/*     */   {
/* 377 */     buf[(pos++)] = ((byte)(i & 0xFF));
/* 378 */     buf[(pos++)] = ((byte)(i >>> 8));
/* 379 */     buf[(pos++)] = ((byte)(i >>> 16));
/* 380 */     buf[(pos++)] = ((byte)(i >>> 24));
/*     */     
/* 382 */     return pos;
/*     */   }
/*     */   
/*     */   private static int writeLong(long l, byte[] buf, int pos) {
/* 386 */     buf[(pos++)] = ((byte)(int)(l & 0xFF));
/* 387 */     buf[(pos++)] = ((byte)(int)(l >>> 8));
/* 388 */     buf[(pos++)] = ((byte)(int)(l >>> 16));
/* 389 */     buf[(pos++)] = ((byte)(int)(l >>> 24));
/* 390 */     buf[(pos++)] = ((byte)(int)(l >>> 32));
/* 391 */     buf[(pos++)] = ((byte)(int)(l >>> 40));
/* 392 */     buf[(pos++)] = ((byte)(int)(l >>> 48));
/* 393 */     buf[(pos++)] = ((byte)(int)(l >>> 56));
/*     */     
/* 395 */     return pos;
/*     */   }
/*     */   
/*     */   private static int writeBytes(byte[] msg, byte[] buf, int pos) {
/* 399 */     pos = writeInt(msg.length, buf, pos);
/*     */     
/* 401 */     System.arraycopy(msg, 0, buf, pos, msg.length);
/*     */     
/* 403 */     return pos + msg.length;
/*     */   }
/*     */   
/*     */   private static int readInt(byte[] buf, int pos) {
/* 407 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24;
/*     */   }
/*     */   
/*     */   private static long readLong(byte[] buf, int pos)
/*     */   {
/* 412 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24 | (buf[(pos++)] & 0xFF) << 32 | (buf[(pos++)] & 0xFF) << 40 | (buf[(pos++)] & 0xFF) << 48 | (buf[(pos++)] & 0xFF) << 56;
/*     */   }
/*     */   
/*     */ 
/*     */   private static byte[] readBytes(byte[] buf, int pos)
/*     */   {
/* 418 */     int length = readInt(buf, pos);
/*     */     
/* 420 */     pos += 4;
/*     */     
/* 422 */     byte[] msg = new byte[length];
/* 423 */     System.arraycopy(buf, pos, msg, 0, length);
/*     */     
/* 425 */     return msg;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getCatalog()
/*     */   {
/* 434 */     return this.catalog;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getConnectionId()
/*     */   {
/* 443 */     return this.connectionId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getEventCreationTime()
/*     */   {
/* 453 */     return this.eventCreationTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getEventDuration()
/*     */   {
/* 462 */     return this.eventDuration;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDurationUnits()
/*     */   {
/* 469 */     return this.durationUnits;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte getEventType()
/*     */   {
/* 478 */     return this.eventType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getResultSetId()
/*     */   {
/* 487 */     return this.resultSetId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getStatementId()
/*     */   {
/* 496 */     return this.statementId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 505 */     return this.message;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\profiler\ProfilerEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */