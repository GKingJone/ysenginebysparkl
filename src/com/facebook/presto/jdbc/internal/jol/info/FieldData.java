/*     */ package com.facebook.presto.jdbc.internal.jol.info;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.util.VMSupport;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FieldData
/*     */ {
/*     */   private final String name;
/*     */   private final String type;
/*     */   private final String klass;
/*     */   private final Field refField;
/*     */   private final int vmOffset;
/*     */   
/*     */   public static FieldData create(String hostKlass, String fieldName, String fieldType)
/*     */   {
/*  48 */     return new FieldData(null, -1, hostKlass, fieldName, fieldType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static FieldData parse(Field field)
/*     */   {
/*  58 */     return new FieldData(field, computeOffset(field), field.getDeclaringClass().getSimpleName(), field.getName(), field.getType().getSimpleName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private FieldData(Field refField, int vmOffset, String hostKlass, String fieldName, String fieldType)
/*     */   {
/*  68 */     this.klass = hostKlass;
/*  69 */     this.name = fieldName;
/*  70 */     this.type = fieldType;
/*  71 */     this.refField = refField;
/*  72 */     this.vmOffset = vmOffset;
/*     */   }
/*     */   
/*     */   private static int computeOffset(Field field) {
/*  76 */     if (Modifier.isStatic(field.getModifiers())) {
/*  77 */       return (int)VMSupport.U.staticFieldOffset(field);
/*     */     }
/*  79 */     return (int)VMSupport.U.objectFieldOffset(field);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String typeClass()
/*     */   {
/*  89 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String hostClass()
/*     */   {
/*  98 */     return this.klass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String name()
/*     */   {
/* 107 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String safeValue(Object object)
/*     */   {
/* 118 */     if (this.refField != null)
/*     */       try {
/* 120 */         return VMSupport.safeToString(this.refField.get(object));
/*     */       }
/*     */       catch (IllegalAccessException iae)
/*     */       {
/*     */         try
/*     */         {
/* 126 */           this.refField.setAccessible(true);
/* 127 */           return VMSupport.safeToString(this.refField.get(object));
/*     */         } catch (Exception e) {
/* 129 */           return "(access denied)";
/*     */         }
/*     */       }
/* 132 */     return "N/A";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int vmOffset()
/*     */   {
/* 143 */     if (this.vmOffset != -1) {
/* 144 */       return this.vmOffset;
/*     */     }
/* 146 */     throw new IllegalStateException("VM offset is not defined");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\info\FieldData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */