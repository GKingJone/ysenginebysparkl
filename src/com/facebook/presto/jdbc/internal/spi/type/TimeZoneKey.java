/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Objects;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TimeZoneKey
/*     */ {
/*  39 */   public static final TimeZoneKey UTC_KEY = new TimeZoneKey("UTC", (short)0);
/*     */   
/*     */   public static final short MAX_TIME_ZONE_KEY;
/*     */   
/*     */   private static final Map<String, TimeZoneKey> ZONE_ID_TO_KEY;
/*     */   private static final Set<TimeZoneKey> ZONE_KEYS;
/*     */   private static final TimeZoneKey[] TIME_ZONE_KEYS;
/*     */   private static final short OFFSET_TIME_ZONE_MIN = -840;
/*     */   private static final short OFFSET_TIME_ZONE_MAX = 840;
/*  48 */   private static final TimeZoneKey[] OFFSET_TIME_ZONE_KEYS = new TimeZoneKey['ڑ'];
/*     */   private final String id;
/*     */   
/*  51 */   static { try { InputStream in = TimeZoneIndex.class.getResourceAsStream("zone-index.properties");Throwable localThrowable3 = null;
/*     */       try
/*     */       {
/*  54 */         Properties data = new Properties()
/*     */         {
/*     */ 
/*     */           public synchronized Object put(Object key, Object value)
/*     */           {
/*  59 */             Object existingEntry = super.put(key, value);
/*  60 */             if (existingEntry != null) {
/*  61 */               throw new AssertionError("Zone file has duplicate entries for " + key);
/*     */             }
/*  63 */             return null;
/*     */           }
/*  65 */         };
/*  66 */         data.load(in);
/*     */         
/*  68 */         if (data.containsKey("0")) {
/*  69 */           throw new AssertionError("Zone file should not contain a mapping for key 0");
/*     */         }
/*     */         
/*  72 */         Map<String, TimeZoneKey> zoneIdToKey = new TreeMap();
/*  73 */         zoneIdToKey.put(UTC_KEY.getId().toLowerCase(Locale.ENGLISH), UTC_KEY);
/*     */         
/*  75 */         short maxZoneKey = 0;
/*  76 */         for (Entry<Object, Object> entry : data.entrySet()) {
/*  77 */           short zoneKey = Short.valueOf(((String)entry.getKey()).trim()).shortValue();
/*  78 */           String zoneId = ((String)entry.getValue()).trim();
/*     */           
/*  80 */           maxZoneKey = (short)Math.max(maxZoneKey, zoneKey);
/*  81 */           zoneIdToKey.put(zoneId.toLowerCase(Locale.ENGLISH), new TimeZoneKey(zoneId, zoneKey));
/*     */         }
/*     */         
/*  84 */         MAX_TIME_ZONE_KEY = maxZoneKey;
/*  85 */         ZONE_ID_TO_KEY = Collections.unmodifiableMap(new LinkedHashMap(zoneIdToKey));
/*  86 */         ZONE_KEYS = Collections.unmodifiableSet(new LinkedHashSet(zoneIdToKey.values()));
/*     */         
/*  88 */         TIME_ZONE_KEYS = new TimeZoneKey[maxZoneKey + 1];
/*  89 */         for (TimeZoneKey timeZoneKey : zoneIdToKey.values()) {
/*  90 */           TIME_ZONE_KEYS[timeZoneKey.getKey()] = timeZoneKey;
/*     */         }
/*     */         
/*  93 */         for (short offset = 64696; offset <= 840; offset = (short)(offset + 1)) {
/*  94 */           if (offset != 0)
/*     */           {
/*     */ 
/*  97 */             String zoneId = zoneIdForOffset(offset);
/*  98 */             TimeZoneKey zoneKey = (TimeZoneKey)ZONE_ID_TO_KEY.get(zoneId);
/*  99 */             OFFSET_TIME_ZONE_KEYS[(offset - 64696)] = zoneKey;
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable1)
/*     */       {
/*  51 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */         if (in != null) if (localThrowable3 != null) try { in.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else in.close();
/*     */       }
/* 103 */     } catch (IOException e) { throw new AssertionError("Error loading time zone index file", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Set<TimeZoneKey> getTimeZoneKeys()
/*     */   {
/* 109 */     return ZONE_KEYS;
/*     */   }
/*     */   
/*     */   @JsonCreator
/*     */   public static TimeZoneKey getTimeZoneKey(short timeZoneKey)
/*     */   {
/* 115 */     checkArgument((timeZoneKey < TIME_ZONE_KEYS.length) && (TIME_ZONE_KEYS[timeZoneKey] != null), "Invalid time zone key %d", new Object[] { Short.valueOf(timeZoneKey) });
/* 116 */     return TIME_ZONE_KEYS[timeZoneKey];
/*     */   }
/*     */   
/*     */   public static TimeZoneKey getTimeZoneKey(String zoneId)
/*     */   {
/* 121 */     Objects.requireNonNull(zoneId, "Zone id is null");
/* 122 */     checkArgument(!zoneId.isEmpty(), "Zone id is an empty string", new Object[0]);
/*     */     
/* 124 */     TimeZoneKey zoneKey = (TimeZoneKey)ZONE_ID_TO_KEY.get(zoneId.toLowerCase(Locale.ENGLISH));
/* 125 */     if (zoneKey == null) {
/* 126 */       zoneKey = (TimeZoneKey)ZONE_ID_TO_KEY.get(normalizeZoneId(zoneId));
/*     */     }
/* 128 */     if (zoneKey == null) {
/* 129 */       throw new TimeZoneNotSupportedException(zoneId);
/*     */     }
/* 131 */     return zoneKey;
/*     */   }
/*     */   
/*     */   public static TimeZoneKey getTimeZoneKeyForOffset(long offsetMinutes)
/*     */   {
/* 136 */     if (offsetMinutes == 0L) {
/* 137 */       return UTC_KEY;
/*     */     }
/*     */     
/* 140 */     checkArgument((offsetMinutes >= -840L) && (offsetMinutes <= 840L), "Invalid offset minutes %s", new Object[] { Long.valueOf(offsetMinutes) });
/* 141 */     TimeZoneKey timeZoneKey = OFFSET_TIME_ZONE_KEYS[((int)offsetMinutes - 64696)];
/* 142 */     if (timeZoneKey == null) {
/* 143 */       throw new TimeZoneNotSupportedException(zoneIdForOffset(offsetMinutes));
/*     */     }
/* 145 */     return timeZoneKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private final short key;
/*     */   
/*     */   TimeZoneKey(String id, short key)
/*     */   {
/* 154 */     this.id = ((String)Objects.requireNonNull(id, "id is null"));
/* 155 */     if (key < 0) {
/* 156 */       throw new IllegalArgumentException("key is negative");
/*     */     }
/* 158 */     this.key = key;
/*     */   }
/*     */   
/*     */   public String getId()
/*     */   {
/* 163 */     return this.id;
/*     */   }
/*     */   
/*     */   @JsonValue
/*     */   public short getKey()
/*     */   {
/* 169 */     return this.key;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 175 */     return Objects.hash(new Object[] { this.id, Short.valueOf(this.key) });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 181 */     if (this == obj) {
/* 182 */       return true;
/*     */     }
/* 184 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 185 */       return false;
/*     */     }
/* 187 */     TimeZoneKey other = (TimeZoneKey)obj;
/* 188 */     return (Objects.equals(this.id, other.id)) && (Objects.equals(Short.valueOf(this.key), Short.valueOf(other.key)));
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 194 */     return this.id;
/*     */   }
/*     */   
/*     */   public static boolean isUtcZoneId(String zoneId)
/*     */   {
/* 199 */     return normalizeZoneId(zoneId).equals("utc");
/*     */   }
/*     */   
/*     */   private static String normalizeZoneId(String originalZoneId)
/*     */   {
/* 204 */     String zoneId = originalZoneId.toLowerCase(Locale.ENGLISH);
/*     */     
/* 206 */     if (zoneId.startsWith("etc/")) {
/* 207 */       zoneId = zoneId.substring(4);
/*     */     }
/*     */     
/* 210 */     if (isUtcEquivalentName(zoneId)) {
/* 211 */       return "utc";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 219 */     int length = zoneId.length();
/* 220 */     if ((length > 3) && ((zoneId.startsWith("utc")) || (zoneId.startsWith("gmt")))) {
/* 221 */       zoneId = zoneId.substring(3);
/* 222 */       length = zoneId.length();
/*     */     }
/* 224 */     else if ((length > 2) && (zoneId.startsWith("ut"))) {
/* 225 */       zoneId = zoneId.substring(2);
/* 226 */       length = zoneId.length();
/*     */     }
/*     */     
/*     */ 
/* 230 */     if (("+00:00".equals(zoneId)) || ("-00:00".equals(zoneId))) {
/* 231 */       return "utc";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 236 */     if ((length == 6) && (zoneId.charAt(3) == ':')) {
/* 237 */       return zoneId;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 243 */     if ((length != 2) && (length != 3)) {
/* 244 */       return originalZoneId;
/*     */     }
/*     */     
/*     */ 
/* 248 */     char signChar = zoneId.charAt(0);
/* 249 */     if ((signChar != '+') && (signChar != '-')) {
/* 250 */       return originalZoneId;
/*     */     }
/*     */     
/*     */     char hourOnes;
/*     */     char hourTens;
/*     */     char hourOnes;
/* 256 */     if (length == 2) {
/* 257 */       char hourTens = '0';
/* 258 */       hourOnes = zoneId.charAt(1);
/*     */     }
/*     */     else {
/* 261 */       hourTens = zoneId.charAt(1);
/* 262 */       hourOnes = zoneId.charAt(2);
/*     */     }
/*     */     
/*     */ 
/* 266 */     if ((!Character.isDigit(hourTens)) || (!Character.isDigit(hourOnes))) {
/* 267 */       return originalZoneId;
/*     */     }
/*     */     
/*     */ 
/* 271 */     if ((hourTens == '0') && (hourOnes == '0')) {
/* 272 */       return "utc";
/*     */     }
/*     */     
/* 275 */     return "" + signChar + hourTens + hourOnes + ":00";
/*     */   }
/*     */   
/*     */   private static boolean isUtcEquivalentName(String zoneId)
/*     */   {
/* 280 */     return (zoneId.equals("utc")) || 
/* 281 */       (zoneId.equals("z")) || 
/* 282 */       (zoneId.equals("ut")) || 
/* 283 */       (zoneId.equals("uct")) || 
/* 284 */       (zoneId.equals("ut")) || 
/* 285 */       (zoneId.equals("gmt")) || 
/* 286 */       (zoneId.equals("gmt0")) || 
/* 287 */       (zoneId.equals("greenwich")) || 
/* 288 */       (zoneId.equals("universal")) || 
/* 289 */       (zoneId.equals("zulu"));
/*     */   }
/*     */   
/*     */   private static String zoneIdForOffset(long offset)
/*     */   {
/* 294 */     return String.format("%s%02d:%02d", new Object[] { offset < 0L ? "-" : "+", Long.valueOf(Math.abs(offset / 60L)), Long.valueOf(Math.abs(offset % 60L)) });
/*     */   }
/*     */   
/*     */   private static void checkArgument(boolean check, String message, Object... args)
/*     */   {
/* 299 */     if (!check) {
/* 300 */       throw new IllegalArgumentException(String.format(message, args));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TimeZoneKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */