/*     */ package com.facebook.presto.jdbc.internal.jol.heap;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassData;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.FieldData;
/*     */ import com.facebook.presto.jdbc.internal.jol.util.Multiset;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HeapDumpReader
/*     */ {
/*     */   private final InputStream is;
/*     */   private final Map<Long, String> strings;
/*     */   private final Map<Long, String> classNames;
/*     */   private final Multiset<ClassData> classCounts;
/*     */   private final Map<Long, ClassData> classDatas;
/*     */   private int idSize;
/*     */   private int readBytes;
/*     */   private final byte[] buf;
/*     */   private final ByteBuffer wrapBuf;
/*     */   
/*     */   public HeapDumpReader(File file)
/*     */     throws FileNotFoundException
/*     */   {
/*  63 */     this.is = new BufferedInputStream(new FileInputStream(file));
/*  64 */     this.strings = new HashMap();
/*  65 */     this.classNames = new HashMap();
/*  66 */     this.classCounts = new Multiset();
/*  67 */     this.classDatas = new HashMap();
/*  68 */     this.buf = new byte['က'];
/*  69 */     this.wrapBuf = ByteBuffer.wrap(this.buf);
/*     */   }
/*     */   
/*     */   private int read() throws IOException {
/*  73 */     int v = this.is.read();
/*  74 */     if (v != -1) {
/*  75 */       this.readBytes += 1;
/*  76 */       return v;
/*     */     }
/*  78 */     throw new EOFException();
/*     */   }
/*     */   
/*     */   private int read(byte[] b, int size) throws IOException
/*     */   {
/*  83 */     int read = this.is.read(b, 0, size);
/*  84 */     this.readBytes += read;
/*  85 */     return read;
/*     */   }
/*     */   
/*     */   public Multiset<ClassData> parse() throws IOException {
/*  89 */     readNullTerminated();
/*     */     
/*  91 */     this.idSize = read_U4();
/*     */     
/*  93 */     read_U4();
/*  94 */     read_U4();
/*     */     
/*     */ 
/*  97 */     while (this.is.available() != 0)
/*     */     {
/*  99 */       int tag = read_U1();
/* 100 */       read_U4();
/* 101 */       int len = read_U4();
/*     */       
/* 103 */       int lastCount = this.readBytes;
/*     */       
/* 105 */       switch (tag) {
/*     */       case 1: 
/* 107 */         long id = read_ID();
/* 108 */         String s = readString(len - this.idSize);
/* 109 */         this.strings.put(Long.valueOf(id), s);
/* 110 */         break;
/*     */       
/*     */ 
/*     */       case 2: 
/* 114 */         read_U4();
/* 115 */         long id = read_ID();
/* 116 */         read_U4();
/* 117 */         long nameID = read_ID();
/*     */         
/* 119 */         this.classNames.put(Long.valueOf(id), this.strings.get(Long.valueOf(nameID)));
/* 120 */         break;
/*     */       
/*     */       case 12: 
/*     */       case 28: 
/*     */       default: 
/* 125 */         while (this.readBytes - lastCount < len) {
/* 126 */           digestHeapDump(); continue;
/*     */           
/*     */ 
/*     */ 
/* 130 */           read_null(len);
/*     */         }
/*     */       }
/* 133 */       if (this.readBytes - lastCount != len) {
/* 134 */         throw new IllegalStateException("Expected to read " + len + " bytes, but read " + (this.readBytes - lastCount) + " bytes");
/*     */       }
/*     */     }
/*     */     
/* 138 */     return this.classCounts;
/*     */   }
/*     */   
/*     */   private void digestHeapDump() throws IOException {
/* 142 */     int subTag = read_U1();
/* 143 */     switch (subTag) {
/*     */     case 1: 
/* 145 */       read_ID();
/* 146 */       read_ID();
/* 147 */       return;
/*     */     case 2: 
/* 149 */       read_ID();
/* 150 */       read_U4();
/* 151 */       read_U4();
/* 152 */       return;
/*     */     case 3: 
/* 154 */       read_ID();
/* 155 */       read_U4();
/* 156 */       read_U4();
/* 157 */       return;
/*     */     case 4: 
/* 159 */       read_ID();
/* 160 */       read_U4();
/* 161 */       return;
/*     */     case 5: 
/* 163 */       read_ID();
/* 164 */       return;
/*     */     case 6: 
/* 166 */       read_ID();
/* 167 */       read_U4();
/* 168 */       return;
/*     */     case 7: 
/* 170 */       read_ID();
/* 171 */       return;
/*     */     case 8: 
/* 173 */       read_ID();
/* 174 */       read_U4();
/* 175 */       read_U4();
/* 176 */       return;
/*     */     case 32: 
/* 178 */       digestClass();
/* 179 */       return;
/*     */     case 33: 
/* 181 */       digestInstance();
/* 182 */       return;
/*     */     case 34: 
/* 184 */       digestObjArray();
/* 185 */       return;
/*     */     case 35: 
/* 187 */       digestPrimArray();
/* 188 */       return;
/*     */     }
/* 190 */     throw new IllegalStateException("Unknown subtag: " + subTag);
/*     */   }
/*     */   
/*     */   private void digestPrimArray() throws IOException
/*     */   {
/* 195 */     read_ID();
/* 196 */     read_U4();
/* 197 */     int elements = read_U4();
/* 198 */     int typeClass = read_U1();
/* 199 */     read_null(elements * getSize(typeClass));
/*     */     
/* 201 */     this.classCounts.add(new ClassData(getTypeString(typeClass) + "[]", getTypeString(typeClass), elements));
/*     */   }
/*     */   
/*     */   private void digestObjArray() throws IOException {
/* 205 */     read_ID();
/* 206 */     read_U4();
/* 207 */     int elements = read_U4();
/* 208 */     read_ID();
/* 209 */     read_null(elements * this.idSize);
/*     */     
/*     */ 
/* 212 */     this.classCounts.add(new ClassData("Object[]", "Object", elements));
/*     */   }
/*     */   
/*     */   private void digestInstance() throws IOException {
/* 216 */     read_ID();
/* 217 */     read_U4();
/* 218 */     long klassID = read_ID();
/*     */     
/* 220 */     this.classCounts.add(this.classDatas.get(Long.valueOf(klassID)));
/*     */     
/* 222 */     int instanceBytes = read_U4();
/* 223 */     read_null(instanceBytes);
/*     */   }
/*     */   
/*     */   private void digestClass() throws IOException {
/* 227 */     long klassID = read_ID();
/*     */     
/* 229 */     String name = (String)this.classNames.get(Long.valueOf(klassID));
/*     */     
/* 231 */     ClassData cd = new ClassData(name);
/* 232 */     cd.addSuperClass(name);
/*     */     
/* 234 */     read_U4();
/*     */     
/* 236 */     long superKlassID = read_ID();
/* 237 */     ClassData superCd = (ClassData)this.classDatas.get(Long.valueOf(superKlassID));
/* 238 */     if (superCd != null) {
/* 239 */       cd.merge(superCd);
/*     */     }
/*     */     
/* 242 */     read_ID();
/* 243 */     read_ID();
/* 244 */     read_ID();
/* 245 */     read_ID();
/* 246 */     read_ID();
/* 247 */     read_U4();
/*     */     
/* 249 */     int cpCount = read_U2();
/* 250 */     for (int c = 0; c < cpCount; c++) {
/* 251 */       read_U2();
/* 252 */       int type = read_U1();
/* 253 */       readValue(type);
/*     */     }
/*     */     
/* 256 */     int cpStatics = read_U2();
/* 257 */     for (int c = 0; c < cpStatics; c++) {
/* 258 */       read_ID();
/* 259 */       int type = read_U1();
/* 260 */       readValue(type);
/*     */     }
/*     */     
/* 263 */     int cpInstance = read_U2();
/* 264 */     for (int c = 0; c < cpInstance; c++) {
/* 265 */       long index = read_ID();
/* 266 */       int type = read_U1();
/*     */       
/* 268 */       cd.addField(FieldData.create(name, (String)this.strings.get(Long.valueOf(index)), getTypeString(type)));
/*     */     }
/*     */     
/* 271 */     this.classDatas.put(Long.valueOf(klassID), cd);
/*     */   }
/*     */   
/*     */   private long readValue(int type) throws IOException {
/* 275 */     int size = getSize(type);
/* 276 */     switch (size) {
/*     */     case 1: 
/* 278 */       return read_U1();
/*     */     case 2: 
/* 280 */       return read_U2();
/*     */     case 4: 
/* 282 */       return read_U4();
/*     */     case 8: 
/* 284 */       return read_U8();
/*     */     }
/* 286 */     throw new IllegalStateException("Unknown size: " + size);
/*     */   }
/*     */   
/*     */   private int getSize(int type) throws IOException
/*     */   {
/* 291 */     switch (type) {
/*     */     case 2: 
/* 293 */       if (this.idSize == 4)
/* 294 */         return 4;
/* 295 */       if (this.idSize == 8)
/* 296 */         return 8;
/* 297 */       throw new IllegalStateException();
/*     */     case 4: 
/*     */     case 8: 
/* 300 */       return 1;
/*     */     
/*     */     case 5: 
/*     */     case 9: 
/* 304 */       return 2;
/*     */     
/*     */     case 6: 
/*     */     case 10: 
/* 308 */       return 4;
/*     */     
/*     */     case 7: 
/*     */     case 11: 
/* 312 */       return 8;
/*     */     }
/*     */     
/* 315 */     throw new IllegalStateException("Unknown type: " + type);
/*     */   }
/*     */   
/*     */   private String getTypeString(int type) throws IOException
/*     */   {
/* 320 */     switch (type) {
/*     */     case 2: 
/* 322 */       return "Object";
/*     */     case 4: 
/* 324 */       return "boolean";
/*     */     case 8: 
/* 326 */       return "byte";
/*     */     case 9: 
/* 328 */       return "short";
/*     */     case 5: 
/* 330 */       return "char";
/*     */     case 10: 
/* 332 */       return "int";
/*     */     case 6: 
/* 334 */       return "float";
/*     */     case 7: 
/* 336 */       return "double";
/*     */     case 11: 
/* 338 */       return "long";
/*     */     }
/* 340 */     throw new IllegalStateException("Unknown type: " + type);
/*     */   }
/*     */   
/*     */   private long read_ID() throws IOException
/*     */   {
/* 345 */     int read = read(this.buf, this.idSize);
/* 346 */     if (read == this.idSize) {
/* 347 */       if (this.idSize <= 4) {
/* 348 */         return this.wrapBuf.get(0);
/*     */       }
/* 350 */       return this.wrapBuf.getLong(0);
/*     */     }
/*     */     
/* 353 */     throw new IllegalStateException("Unable to read 1 bytes");
/*     */   }
/*     */   
/*     */   void read_null(int len) throws IOException {
/* 357 */     int rem = len;
/*     */     do
/*     */     {
/* 360 */       int read = read(this.buf, Math.min(this.buf.length, rem));
/* 361 */       rem -= read;
/* 362 */     } while (rem > 0);
/*     */   }
/*     */   
/*     */   String readNullTerminated() throws IOException
/*     */   {
/* 367 */     StringBuilder sb = new StringBuilder();
/* 368 */     int r; while (((r = read()) != -1) && 
/* 369 */       (r != 0)) {
/* 370 */       sb.append((char)(r & 0xFF));
/*     */     }
/* 372 */     return sb.toString();
/*     */   }
/*     */   
/*     */   String readString(int len) throws IOException {
/* 376 */     StringBuilder sb = new StringBuilder();
/* 377 */     for (int l = 0; l < len; l++) {
/* 378 */       int r = read();
/* 379 */       if (r == -1) break;
/* 380 */       sb.append((char)(r & 0xFF));
/*     */     }
/* 382 */     return sb.toString();
/*     */   }
/*     */   
/*     */   long read_U8() throws IOException {
/* 386 */     int read = read(this.buf, 8);
/* 387 */     if (read == 8) {
/* 388 */       return this.wrapBuf.getLong(0);
/*     */     }
/* 390 */     throw new IllegalStateException("Unable to read 8 bytes");
/*     */   }
/*     */   
/*     */   int read_U4() throws IOException {
/* 394 */     int read = read(this.buf, 4);
/* 395 */     if (read == 4) {
/* 396 */       return this.wrapBuf.getInt(0);
/*     */     }
/* 398 */     throw new IllegalStateException("Unable to read 4 bytes");
/*     */   }
/*     */   
/*     */   int read_U2() throws IOException {
/* 402 */     int read = read(this.buf, 2);
/* 403 */     if (read == 2) {
/* 404 */       return this.wrapBuf.getShort(0);
/*     */     }
/* 406 */     throw new IllegalStateException("Unable to read 2 bytes");
/*     */   }
/*     */   
/*     */   int read_U1() throws IOException {
/* 410 */     int read = read(this.buf, 1);
/* 411 */     if (read == 1) {
/* 412 */       return this.wrapBuf.get(0);
/*     */     }
/* 414 */     throw new IllegalStateException("Unable to read 1 bytes");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\heap\HeapDumpReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */