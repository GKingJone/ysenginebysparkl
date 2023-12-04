/*     */ package com.facebook.presto.jdbc.internal.joda.time.tz;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ZoneInfoProvider
/*     */   implements Provider
/*     */ {
/*     */   private final File iFileDir;
/*     */   private final String iResourcePath;
/*     */   private final ClassLoader iLoader;
/*     */   private final Map<String, Object> iZoneInfoMap;
/*     */   private final Set<String> iZoneInfoKeys;
/*     */   
/*     */   public ZoneInfoProvider(File paramFile)
/*     */     throws IOException
/*     */   {
/*  60 */     if (paramFile == null) {
/*  61 */       throw new IllegalArgumentException("No file directory provided");
/*     */     }
/*  63 */     if (!paramFile.exists()) {
/*  64 */       throw new IOException("File directory doesn't exist: " + paramFile);
/*     */     }
/*  66 */     if (!paramFile.isDirectory()) {
/*  67 */       throw new IOException("File doesn't refer to a directory: " + paramFile);
/*     */     }
/*     */     
/*  70 */     this.iFileDir = paramFile;
/*  71 */     this.iResourcePath = null;
/*  72 */     this.iLoader = null;
/*     */     
/*  74 */     this.iZoneInfoMap = loadZoneInfoMap(openResource("ZoneInfoMap"));
/*  75 */     this.iZoneInfoKeys = Collections.unmodifiableSortedSet(new TreeSet(this.iZoneInfoMap.keySet()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ZoneInfoProvider(String paramString)
/*     */     throws IOException
/*     */   {
/*  86 */     this(paramString, null, false);
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
/*     */   public ZoneInfoProvider(String paramString, ClassLoader paramClassLoader)
/*     */     throws IOException
/*     */   {
/* 100 */     this(paramString, paramClassLoader, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ZoneInfoProvider(String paramString, ClassLoader paramClassLoader, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 111 */     if (paramString == null) {
/* 112 */       throw new IllegalArgumentException("No resource path provided");
/*     */     }
/* 114 */     if (!paramString.endsWith("/")) {
/* 115 */       paramString = paramString + '/';
/*     */     }
/*     */     
/* 118 */     this.iFileDir = null;
/* 119 */     this.iResourcePath = paramString;
/*     */     
/* 121 */     if ((paramClassLoader == null) && (!paramBoolean)) {
/* 122 */       paramClassLoader = getClass().getClassLoader();
/*     */     }
/*     */     
/* 125 */     this.iLoader = paramClassLoader;
/*     */     
/* 127 */     this.iZoneInfoMap = loadZoneInfoMap(openResource("ZoneInfoMap"));
/* 128 */     this.iZoneInfoKeys = Collections.unmodifiableSortedSet(new TreeSet(this.iZoneInfoMap.keySet()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeZone getZone(String paramString)
/*     */   {
/* 140 */     if (paramString == null) {
/* 141 */       return null;
/*     */     }
/*     */     
/* 144 */     Object localObject = this.iZoneInfoMap.get(paramString);
/* 145 */     if (localObject == null) {
/* 146 */       return null;
/*     */     }
/*     */     
/* 149 */     if ((localObject instanceof SoftReference))
/*     */     {
/* 151 */       SoftReference localSoftReference = (SoftReference)localObject;
/* 152 */       DateTimeZone localDateTimeZone = (DateTimeZone)localSoftReference.get();
/* 153 */       if (localDateTimeZone != null) {
/* 154 */         return localDateTimeZone;
/*     */       }
/*     */       
/* 157 */       return loadZoneData(paramString); }
/* 158 */     if (paramString.equals(localObject))
/*     */     {
/* 160 */       return loadZoneData(paramString);
/*     */     }
/*     */     
/*     */ 
/* 164 */     return getZone((String)localObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<String> getAvailableIDs()
/*     */   {
/* 173 */     return this.iZoneInfoKeys;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void uncaughtException(Exception paramException)
/*     */   {
/* 182 */     paramException.printStackTrace();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private InputStream openResource(String paramString)
/*     */     throws IOException
/*     */   {
/*     */     Object localObject;
/*     */     
/*     */ 
/*     */ 
/* 195 */     if (this.iFileDir != null) {
/* 196 */       localObject = new FileInputStream(new File(this.iFileDir, paramString));
/*     */     } else {
/* 198 */       String str = this.iResourcePath.concat(paramString);
/* 199 */       if (this.iLoader != null) {
/* 200 */         localObject = this.iLoader.getResourceAsStream(str);
/*     */       } else {
/* 202 */         localObject = ClassLoader.getSystemResourceAsStream(str);
/*     */       }
/* 204 */       if (localObject == null) {
/* 205 */         StringBuilder localStringBuilder = new StringBuilder(40).append("Resource not found: \"").append(str).append("\" ClassLoader: ").append(this.iLoader != null ? this.iLoader.toString() : "system");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 210 */         throw new IOException(localStringBuilder.toString());
/*     */       }
/*     */     }
/* 213 */     return (InputStream)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private DateTimeZone loadZoneData(String paramString)
/*     */   {
/* 223 */     InputStream localInputStream = null;
/*     */     try {
/* 225 */       localInputStream = openResource(paramString);
/* 226 */       DateTimeZone localDateTimeZone1 = DateTimeZoneBuilder.readFrom(localInputStream, paramString);
/* 227 */       this.iZoneInfoMap.put(paramString, new SoftReference(localDateTimeZone1));
/* 228 */       return localDateTimeZone1;
/*     */     } catch (IOException localIOException1) { DateTimeZone localDateTimeZone2;
/* 230 */       uncaughtException(localIOException1);
/* 231 */       this.iZoneInfoMap.remove(paramString);
/* 232 */       return null;
/*     */     } finally {
/*     */       try {
/* 235 */         if (localInputStream != null) {
/* 236 */           localInputStream.close();
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException4) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Map<String, Object> loadZoneInfoMap(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 251 */     ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
/* 252 */     DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/*     */     try {
/* 254 */       readZoneInfoMap(localDataInputStream, localConcurrentHashMap);
/*     */       try
/*     */       {
/* 257 */         localDataInputStream.close();
/*     */       }
/*     */       catch (IOException localIOException1) {}
/*     */       
/* 261 */       localConcurrentHashMap.put("UTC", new SoftReference(DateTimeZone.UTC));
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 257 */         localDataInputStream.close();
/*     */       }
/*     */       catch (IOException localIOException2) {}
/*     */     }
/*     */     
/* 262 */     return localConcurrentHashMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void readZoneInfoMap(DataInputStream paramDataInputStream, Map<String, Object> paramMap)
/*     */     throws IOException
/*     */   {
/* 273 */     int i = paramDataInputStream.readUnsignedShort();
/* 274 */     String[] arrayOfString = new String[i];
/* 275 */     for (int j = 0; j < i; j++) {
/* 276 */       arrayOfString[j] = paramDataInputStream.readUTF().intern();
/*     */     }
/*     */     
/*     */ 
/* 280 */     i = paramDataInputStream.readUnsignedShort();
/* 281 */     for (j = 0; j < i; j++) {
/*     */       try {
/* 283 */         paramMap.put(arrayOfString[paramDataInputStream.readUnsignedShort()], arrayOfString[paramDataInputStream.readUnsignedShort()]);
/*     */       } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 285 */         throw new IOException("Corrupt zone info map");
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\ZoneInfoProvider.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */