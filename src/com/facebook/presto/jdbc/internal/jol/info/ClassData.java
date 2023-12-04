/*     */ package com.facebook.presto.jdbc.internal.jol.info;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassData
/*     */ {
/*     */   private final String name;
/*     */   private final List<FieldData> fields;
/*     */   private final List<String> classNames;
/*     */   private final String arrayKlass;
/*     */   private final String arrayComponentKlass;
/*     */   private final int length;
/*     */   private final boolean isArray;
/*     */   
/*     */   public static ClassData parseInstance(Object o)
/*     */   {
/*  49 */     Class<?> k = o.getClass();
/*  50 */     if (k.isArray()) {
/*  51 */       if (k == byte[].class) return parseArray(k, ((byte[])o).length);
/*  52 */       if (k == boolean[].class) return parseArray(k, ((boolean[])o).length);
/*  53 */       if (k == short[].class) return parseArray(k, ((short[])o).length);
/*  54 */       if (k == char[].class) return parseArray(k, ((char[])o).length);
/*  55 */       if (k == int[].class) return parseArray(k, ((int[])o).length);
/*  56 */       if (k == float[].class) return parseArray(k, ((float[])o).length);
/*  57 */       if (k == double[].class) return parseArray(k, ((double[])o).length);
/*  58 */       if (k == long[].class) return parseArray(k, ((long[])o).length);
/*  59 */       return parseArray(k, ((Object[])o).length);
/*     */     }
/*  61 */     return parseClass(k);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ClassData parseArray(Class arrayClass, int length)
/*     */   {
/*  72 */     return new ClassData(arrayClass.getName(), arrayClass.getComponentType().getName(), length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ClassData parseClass(Class klass)
/*     */   {
/*  82 */     ClassData cd = new ClassData(klass.getCanonicalName());
/*     */     do
/*     */     {
/*  85 */       for (Field f : klass.getDeclaredFields()) {
/*  86 */         if (!Modifier.isStatic(f.getModifiers())) {
/*  87 */           cd.addField(FieldData.parse(f));
/*     */         }
/*     */       }
/*  90 */       cd.addSuperClass(klass.getSimpleName());
/*  91 */     } while ((klass = klass.getSuperclass()) != null);
/*     */     
/*  93 */     return cd;
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
/*     */ 
/*     */   public ClassData(String name)
/*     */   {
/* 108 */     this.name = name;
/* 109 */     this.fields = new ArrayList();
/* 110 */     this.classNames = new ArrayList();
/* 111 */     this.length = -1;
/* 112 */     this.arrayKlass = null;
/* 113 */     this.arrayComponentKlass = null;
/* 114 */     this.isArray = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ClassData(String arrayKlass, String componentKlass, int length)
/*     */   {
/* 125 */     this.name = arrayKlass;
/* 126 */     this.arrayKlass = arrayKlass;
/* 127 */     this.arrayComponentKlass = componentKlass;
/* 128 */     this.fields = null;
/* 129 */     this.classNames = null;
/* 130 */     this.length = length;
/* 131 */     this.isArray = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addSuperClass(String superClass)
/*     */   {
/* 140 */     this.classNames.add(0, superClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addField(FieldData fieldData)
/*     */   {
/* 149 */     this.fields.add(fieldData);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<FieldData> fields()
/*     */   {
/* 159 */     return Collections.unmodifiableList(this.fields);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<FieldData> fieldsFor(String klass)
/*     */   {
/* 169 */     List<FieldData> r = new ArrayList();
/* 170 */     for (FieldData f : this.fields) {
/* 171 */       if (f.hostClass().equals(klass)) {
/* 172 */         r.add(f);
/*     */       }
/*     */     }
/* 175 */     return r;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> classHierarchy()
/*     */   {
/* 185 */     return Collections.unmodifiableList(this.classNames);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String name()
/*     */   {
/* 194 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isArray()
/*     */   {
/* 203 */     return this.isArray;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String arrayClass()
/*     */   {
/* 212 */     if (!this.isArray) {
/* 213 */       throw new IllegalStateException("Asking array class for non-array ClassData");
/*     */     }
/* 215 */     return this.arrayKlass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String arrayComponentType()
/*     */   {
/* 224 */     if (!this.isArray) {
/* 225 */       throw new IllegalStateException("Asking array component type for non-array ClassData");
/*     */     }
/* 227 */     return this.arrayComponentKlass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int arrayLength()
/*     */   {
/* 236 */     if (!this.isArray) {
/* 237 */       throw new IllegalStateException("Asking array length for non-array ClassData");
/*     */     }
/* 239 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void merge(ClassData superClassData)
/*     */   {
/* 248 */     this.fields.addAll(superClassData.fields);
/* 249 */     this.classNames.addAll(0, superClassData.classNames);
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 254 */     if (this == o) return true;
/* 255 */     if ((o == null) || (getClass() != o.getClass())) { return false;
/*     */     }
/* 257 */     ClassData classData = (ClassData)o;
/*     */     
/* 259 */     if (this.isArray != classData.isArray) return false;
/* 260 */     if (this.length != classData.length) return false;
/* 261 */     if (this.arrayComponentKlass != null ? !this.arrayComponentKlass.equals(classData.arrayComponentKlass) : classData.arrayComponentKlass != null)
/* 262 */       return false;
/* 263 */     if (this.arrayKlass != null ? !this.arrayKlass.equals(classData.arrayKlass) : classData.arrayKlass != null) return false;
/* 264 */     if (this.classNames != null ? !this.classNames.equals(classData.classNames) : classData.classNames != null) return false;
/* 265 */     if (this.fields != null ? !this.fields.equals(classData.fields) : classData.fields != null) { return false;
/*     */     }
/* 267 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 272 */     int result = this.fields != null ? this.fields.hashCode() : 0;
/* 273 */     result = 31 * result + (this.classNames != null ? this.classNames.hashCode() : 0);
/* 274 */     result = 31 * result + (this.arrayKlass != null ? this.arrayKlass.hashCode() : 0);
/* 275 */     result = 31 * result + (this.arrayComponentKlass != null ? this.arrayComponentKlass.hashCode() : 0);
/* 276 */     result = 31 * result + this.length;
/* 277 */     result = 31 * result + (this.isArray ? 1 : 0);
/* 278 */     return result;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\info\ClassData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */