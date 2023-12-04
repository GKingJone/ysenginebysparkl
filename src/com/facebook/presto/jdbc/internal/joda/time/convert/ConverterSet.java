/*     */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ConverterSet
/*     */ {
/*     */   private final Converter[] iConverters;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Entry[] iSelectEntries;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ConverterSet(Converter[] paramArrayOfConverter)
/*     */   {
/*  35 */     this.iConverters = paramArrayOfConverter;
/*  36 */     this.iSelectEntries = new Entry[16];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Converter select(Class<?> paramClass)
/*     */     throws IllegalStateException
/*     */   {
/*  49 */     Entry[] arrayOfEntry1 = this.iSelectEntries;
/*  50 */     int i = arrayOfEntry1.length;
/*  51 */     int j = paramClass == null ? 0 : paramClass.hashCode() & i - 1;
/*     */     
/*     */ 
/*     */ 
/*  55 */     while ((localEntry = arrayOfEntry1[j]) != null) {
/*  56 */       if (localEntry.iType == paramClass) {
/*  57 */         return localEntry.iConverter;
/*     */       }
/*  59 */       j++; if (j >= i) {
/*  60 */         j = 0;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  66 */     Converter localConverter = selectSlow(this, paramClass);
/*  67 */     Entry localEntry = new Entry(paramClass, localConverter);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  79 */     arrayOfEntry1 = (Entry[])arrayOfEntry1.clone();
/*     */     
/*     */ 
/*  82 */     arrayOfEntry1[j] = localEntry;
/*     */     
/*     */ 
/*  85 */     for (int k = 0; k < i; k++) {
/*  86 */       if (arrayOfEntry1[k] == null)
/*     */       {
/*  88 */         this.iSelectEntries = arrayOfEntry1;
/*  89 */         return localConverter;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  95 */     k = i << 1;
/*  96 */     Entry[] arrayOfEntry2 = new Entry[k];
/*  97 */     for (int m = 0; m < i; m++) {
/*  98 */       localEntry = arrayOfEntry1[m];
/*  99 */       paramClass = localEntry.iType;
/* 100 */       j = paramClass == null ? 0 : paramClass.hashCode() & k - 1;
/* 101 */       while (arrayOfEntry2[j] != null) {
/* 102 */         j++; if (j >= k) {
/* 103 */           j = 0;
/*     */         }
/*     */       }
/* 106 */       arrayOfEntry2[j] = localEntry;
/*     */     }
/*     */     
/*     */ 
/* 110 */     this.iSelectEntries = arrayOfEntry2;
/* 111 */     return localConverter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int size()
/*     */   {
/* 118 */     return this.iConverters.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void copyInto(Converter[] paramArrayOfConverter)
/*     */   {
/* 125 */     System.arraycopy(this.iConverters, 0, paramArrayOfConverter, 0, this.iConverters.length);
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
/*     */   ConverterSet add(Converter paramConverter, Converter[] paramArrayOfConverter)
/*     */   {
/* 139 */     Converter[] arrayOfConverter1 = this.iConverters;
/* 140 */     int i = arrayOfConverter1.length;
/*     */     
/* 142 */     for (int j = 0; j < i; j++) {
/* 143 */       Converter localConverter = arrayOfConverter1[j];
/* 144 */       if (paramConverter.equals(localConverter))
/*     */       {
/* 146 */         if (paramArrayOfConverter != null) {
/* 147 */           paramArrayOfConverter[0] = null;
/*     */         }
/* 149 */         return this;
/*     */       }
/*     */       
/* 152 */       if (paramConverter.getSupportedType() == localConverter.getSupportedType())
/*     */       {
/* 154 */         Converter[] arrayOfConverter3 = new Converter[i];
/*     */         
/* 156 */         for (int k = 0; k < i; k++) {
/* 157 */           if (k != j) {
/* 158 */             arrayOfConverter3[k] = arrayOfConverter1[k];
/*     */           } else {
/* 160 */             arrayOfConverter3[k] = paramConverter;
/*     */           }
/*     */         }
/*     */         
/* 164 */         if (paramArrayOfConverter != null) {
/* 165 */           paramArrayOfConverter[0] = localConverter;
/*     */         }
/* 167 */         return new ConverterSet(arrayOfConverter3);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 172 */     Converter[] arrayOfConverter2 = new Converter[i + 1];
/* 173 */     System.arraycopy(arrayOfConverter1, 0, arrayOfConverter2, 0, i);
/* 174 */     arrayOfConverter2[i] = paramConverter;
/*     */     
/* 176 */     if (paramArrayOfConverter != null) {
/* 177 */       paramArrayOfConverter[0] = null;
/*     */     }
/* 179 */     return new ConverterSet(arrayOfConverter2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ConverterSet remove(Converter paramConverter, Converter[] paramArrayOfConverter)
/*     */   {
/* 191 */     Converter[] arrayOfConverter = this.iConverters;
/* 192 */     int i = arrayOfConverter.length;
/*     */     
/* 194 */     for (int j = 0; j < i; j++) {
/* 195 */       if (paramConverter.equals(arrayOfConverter[j])) {
/* 196 */         return remove(j, paramArrayOfConverter);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 201 */     if (paramArrayOfConverter != null) {
/* 202 */       paramArrayOfConverter[0] = null;
/*     */     }
/* 204 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ConverterSet remove(int paramInt, Converter[] paramArrayOfConverter)
/*     */   {
/* 216 */     Converter[] arrayOfConverter1 = this.iConverters;
/* 217 */     int i = arrayOfConverter1.length;
/* 218 */     if (paramInt >= i) {
/* 219 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 222 */     if (paramArrayOfConverter != null) {
/* 223 */       paramArrayOfConverter[0] = arrayOfConverter1[paramInt];
/*     */     }
/*     */     
/* 226 */     Converter[] arrayOfConverter2 = new Converter[i - 1];
/*     */     
/* 228 */     int j = 0;
/* 229 */     for (int k = 0; k < i; k++) {
/* 230 */       if (k != paramInt) {
/* 231 */         arrayOfConverter2[(j++)] = arrayOfConverter1[k];
/*     */       }
/*     */     }
/*     */     
/* 235 */     return new ConverterSet(arrayOfConverter2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Converter selectSlow(ConverterSet paramConverterSet, Class<?> paramClass)
/*     */   {
/* 243 */     Converter[] arrayOfConverter = paramConverterSet.iConverters;
/* 244 */     int i = arrayOfConverter.length;
/*     */     
/*     */ 
/* 247 */     int j = i; Converter localConverter; Class localClass1; for (;;) { j--; if (j < 0) break;
/* 248 */       localConverter = arrayOfConverter[j];
/* 249 */       localClass1 = localConverter.getSupportedType();
/*     */       
/* 251 */       if (localClass1 == paramClass)
/*     */       {
/* 253 */         return localConverter;
/*     */       }
/*     */       
/* 256 */       if ((localClass1 == null) || ((paramClass != null) && (!localClass1.isAssignableFrom(paramClass))))
/*     */       {
/* 258 */         paramConverterSet = paramConverterSet.remove(j, null);
/* 259 */         arrayOfConverter = paramConverterSet.iConverters;
/* 260 */         i = arrayOfConverter.length;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 266 */     if ((paramClass == null) || (i == 0)) {
/* 267 */       return null;
/*     */     }
/* 269 */     if (i == 1)
/*     */     {
/* 271 */       return arrayOfConverter[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 277 */     j = i; for (;;) { j--; if (j < 0) break;
/* 278 */       localConverter = arrayOfConverter[j];
/* 279 */       localClass1 = localConverter.getSupportedType();
/* 280 */       int m = i; for (;;) { m--; if (m < 0) break;
/* 281 */         if ((m != j) && (arrayOfConverter[m].getSupportedType().isAssignableFrom(localClass1)))
/*     */         {
/* 283 */           paramConverterSet = paramConverterSet.remove(m, null);
/* 284 */           arrayOfConverter = paramConverterSet.iConverters;
/* 285 */           i = arrayOfConverter.length;
/* 286 */           j = i - 1;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 293 */     if (i == 1)
/*     */     {
/* 295 */       return arrayOfConverter[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 301 */     StringBuilder localStringBuilder = new StringBuilder();
/* 302 */     localStringBuilder.append("Unable to find best converter for type \"");
/* 303 */     localStringBuilder.append(paramClass.getName());
/* 304 */     localStringBuilder.append("\" from remaining set: ");
/* 305 */     for (int k = 0; k < i; k++) {
/* 306 */       localConverter = arrayOfConverter[k];
/* 307 */       Class localClass2 = localConverter.getSupportedType();
/*     */       
/* 309 */       localStringBuilder.append(localConverter.getClass().getName());
/* 310 */       localStringBuilder.append('[');
/* 311 */       localStringBuilder.append(localClass2 == null ? null : localClass2.getName());
/* 312 */       localStringBuilder.append("], ");
/*     */     }
/*     */     
/* 315 */     throw new IllegalStateException(localStringBuilder.toString());
/*     */   }
/*     */   
/*     */   static class Entry {
/*     */     final Class<?> iType;
/*     */     final Converter iConverter;
/*     */     
/*     */     Entry(Class<?> paramClass, Converter paramConverter) {
/* 323 */       this.iType = paramClass;
/* 324 */       this.iConverter = paramConverter;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\ConverterSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */