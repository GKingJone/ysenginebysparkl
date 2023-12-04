/*     */ package com.facebook.presto.jdbc.internal.joda.time.tz;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
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
/*     */ public class DefaultNameProvider
/*     */   implements NameProvider
/*     */ {
/*  37 */   private HashMap<Locale, Map<String, Map<String, Object>>> iByLocaleCache = createCache();
/*  38 */   private HashMap<Locale, Map<String, Map<Boolean, Object>>> iByLocaleCache2 = createCache();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getShortName(Locale paramLocale, String paramString1, String paramString2)
/*     */   {
/*  47 */     String[] arrayOfString = getNameSet(paramLocale, paramString1, paramString2);
/*  48 */     return arrayOfString == null ? null : arrayOfString[0];
/*     */   }
/*     */   
/*     */   public String getName(Locale paramLocale, String paramString1, String paramString2) {
/*  52 */     String[] arrayOfString = getNameSet(paramLocale, paramString1, paramString2);
/*  53 */     return arrayOfString == null ? null : arrayOfString[1];
/*     */   }
/*     */   
/*     */   private synchronized String[] getNameSet(Locale paramLocale, String paramString1, String paramString2) {
/*  57 */     if ((paramLocale == null) || (paramString1 == null) || (paramString2 == null)) {
/*  58 */       return null;
/*     */     }
/*     */     
/*  61 */     Object localObject1 = (Map)this.iByLocaleCache.get(paramLocale);
/*  62 */     if (localObject1 == null) {
/*  63 */       this.iByLocaleCache.put(paramLocale, localObject1 = createCache());
/*     */     }
/*     */     
/*  66 */     Object localObject2 = (Map)((Map)localObject1).get(paramString1);
/*  67 */     if (localObject2 == null) {
/*  68 */       ((Map)localObject1).put(paramString1, localObject2 = createCache());
/*     */       
/*  70 */       String[][] arrayOfString1 = DateTimeUtils.getDateFormatSymbols(Locale.ENGLISH).getZoneStrings();
/*  71 */       Object localObject3 = null;
/*  72 */       for (String[] arrayOfString4 : arrayOfString1) {
/*  73 */         if ((arrayOfString4 != null) && (arrayOfString4.length >= 5) && (paramString1.equals(arrayOfString4[0]))) {
/*  74 */           localObject3 = arrayOfString4;
/*  75 */           break;
/*     */         }
/*     */       }
/*  78 */       ??? = DateTimeUtils.getDateFormatSymbols(paramLocale).getZoneStrings();
/*  79 */       Object localObject4 = null;
/*  80 */       for (String[] arrayOfString5 : ???) {
/*  81 */         if ((arrayOfString5 != null) && (arrayOfString5.length >= 5) && (paramString1.equals(arrayOfString5[0]))) {
/*  82 */           localObject4 = arrayOfString5;
/*  83 */           break;
/*     */         }
/*     */       }
/*     */       
/*  87 */       if ((localObject3 != null) && (localObject4 != null)) {
/*  88 */         ((Map)localObject2).put(localObject3[2], new String[] { localObject4[2], localObject4[1] });
/*     */         
/*     */ 
/*     */ 
/*  92 */         if (localObject3[2].equals(localObject3[4])) {
/*  93 */           ((Map)localObject2).put(localObject3[4] + "-Summer", new String[] { localObject4[4], localObject4[3] });
/*     */         } else {
/*  95 */           ((Map)localObject2).put(localObject3[4], new String[] { localObject4[4], localObject4[3] });
/*     */         }
/*     */       }
/*     */     }
/*  99 */     return (String[])((Map)localObject2).get(paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getShortName(Locale paramLocale, String paramString1, String paramString2, boolean paramBoolean)
/*     */   {
/* 106 */     String[] arrayOfString = getNameSet(paramLocale, paramString1, paramString2, paramBoolean);
/* 107 */     return arrayOfString == null ? null : arrayOfString[0];
/*     */   }
/*     */   
/*     */   public String getName(Locale paramLocale, String paramString1, String paramString2, boolean paramBoolean) {
/* 111 */     String[] arrayOfString = getNameSet(paramLocale, paramString1, paramString2, paramBoolean);
/* 112 */     return arrayOfString == null ? null : arrayOfString[1];
/*     */   }
/*     */   
/*     */   private synchronized String[] getNameSet(Locale paramLocale, String paramString1, String paramString2, boolean paramBoolean) {
/* 116 */     if ((paramLocale == null) || (paramString1 == null) || (paramString2 == null)) {
/* 117 */       return null;
/*     */     }
/* 119 */     if (paramString1.startsWith("Etc/")) {
/* 120 */       paramString1 = paramString1.substring(4);
/*     */     }
/*     */     
/* 123 */     Object localObject1 = (Map)this.iByLocaleCache2.get(paramLocale);
/* 124 */     if (localObject1 == null) {
/* 125 */       this.iByLocaleCache2.put(paramLocale, localObject1 = createCache());
/*     */     }
/*     */     
/* 128 */     Object localObject2 = (Map)((Map)localObject1).get(paramString1);
/* 129 */     if (localObject2 == null) {
/* 130 */       ((Map)localObject1).put(paramString1, localObject2 = createCache());
/*     */       
/* 132 */       String[][] arrayOfString1 = DateTimeUtils.getDateFormatSymbols(Locale.ENGLISH).getZoneStrings();
/* 133 */       Object localObject3 = null;
/* 134 */       for (String[] arrayOfString4 : arrayOfString1) {
/* 135 */         if ((arrayOfString4 != null) && (arrayOfString4.length >= 5) && (paramString1.equals(arrayOfString4[0]))) {
/* 136 */           localObject3 = arrayOfString4;
/* 137 */           break;
/*     */         }
/*     */       }
/* 140 */       ??? = DateTimeUtils.getDateFormatSymbols(paramLocale).getZoneStrings();
/* 141 */       Object localObject4 = null;
/* 142 */       for (String[] arrayOfString5 : ???) {
/* 143 */         if ((arrayOfString5 != null) && (arrayOfString5.length >= 5) && (paramString1.equals(arrayOfString5[0]))) {
/* 144 */           localObject4 = arrayOfString5;
/* 145 */           break;
/*     */         }
/*     */       }
/*     */       
/* 149 */       if ((localObject3 != null) && (localObject4 != null)) {
/* 150 */         ((Map)localObject2).put(Boolean.TRUE, new String[] { localObject4[2], localObject4[1] });
/* 151 */         ((Map)localObject2).put(Boolean.FALSE, new String[] { localObject4[4], localObject4[3] });
/*     */       }
/*     */     }
/* 154 */     return (String[])((Map)localObject2).get(Boolean.valueOf(paramBoolean));
/*     */   }
/*     */   
/*     */   private HashMap createCache()
/*     */   {
/* 159 */     return new HashMap(7);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\DefaultNameProvider.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */