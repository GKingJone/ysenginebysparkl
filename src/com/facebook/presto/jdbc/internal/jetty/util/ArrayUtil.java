/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArrayUtil
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   public static <T> T[] removeFromArray(T[] array, Object item)
/*     */   {
/*  38 */     if ((item == null) || (array == null))
/*  39 */       return array;
/*  40 */     for (int i = array.length; i-- > 0;)
/*     */     {
/*  42 */       if (item.equals(array[i]))
/*     */       {
/*  44 */         Class<?> c = array == null ? item.getClass() : array.getClass().getComponentType();
/*     */         
/*  46 */         T[] na = (Object[])Array.newInstance(c, Array.getLength(array) - 1);
/*  47 */         if (i > 0)
/*  48 */           System.arraycopy(array, 0, na, 0, i);
/*  49 */         if (i + 1 < array.length)
/*  50 */           System.arraycopy(array, i + 1, na, i, array.length - (i + 1));
/*  51 */         return na;
/*     */       }
/*     */     }
/*  54 */     return array;
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
/*     */   public static <T> T[] addToArray(T[] array, T item, Class<?> type)
/*     */   {
/*  67 */     if (array == null)
/*     */     {
/*  69 */       if ((type == null) && (item != null)) {
/*  70 */         type = item.getClass();
/*     */       }
/*  72 */       T[] na = (Object[])Array.newInstance(type, 1);
/*  73 */       na[0] = item;
/*  74 */       return na;
/*     */     }
/*     */     
/*     */ 
/*  78 */     T[] na = Arrays.copyOf(array, array.length + 1);
/*  79 */     na[array.length] = item;
/*  80 */     return na;
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
/*     */   public static <T> T[] prependToArray(T item, T[] array, Class<?> type)
/*     */   {
/*  94 */     if (array == null)
/*     */     {
/*  96 */       if ((type == null) && (item != null)) {
/*  97 */         type = item.getClass();
/*     */       }
/*  99 */       T[] na = (Object[])Array.newInstance(type, 1);
/* 100 */       na[0] = item;
/* 101 */       return na;
/*     */     }
/*     */     
/*     */ 
/* 105 */     Class<?> c = array.getClass().getComponentType();
/*     */     
/* 107 */     T[] na = (Object[])Array.newInstance(c, Array.getLength(array) + 1);
/* 108 */     System.arraycopy(array, 0, na, 1, array.length);
/* 109 */     na[0] = item;
/* 110 */     return na;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> List<E> asMutableList(E[] array)
/*     */   {
/* 122 */     if ((array == null) || (array.length == 0))
/* 123 */       return new ArrayList();
/* 124 */     return new ArrayList(Arrays.asList(array));
/*     */   }
/*     */   
/*     */ 
/*     */   public static <T> T[] removeNulls(T[] array)
/*     */   {
/* 130 */     for (T t : array)
/*     */     {
/* 132 */       if (t == null)
/*     */       {
/* 134 */         List<T> list = new ArrayList();
/* 135 */         for (T t2 : array)
/* 136 */           if (t2 != null)
/* 137 */             list.add(t2);
/* 138 */         return list.toArray(Arrays.copyOf(array, list.size()));
/*     */       }
/*     */     }
/* 141 */     return array;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ArrayUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */