/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Fields
/*     */   implements Iterable<Field>
/*     */ {
/*     */   private final boolean caseSensitive;
/*     */   private final Map<String, Field> fields;
/*     */   
/*     */   public Fields()
/*     */   {
/*  50 */     this(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Fields(boolean caseSensitive)
/*     */   {
/*  60 */     this.caseSensitive = caseSensitive;
/*  61 */     this.fields = new LinkedHashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Fields(Fields original, boolean immutable)
/*     */   {
/*  73 */     this.caseSensitive = original.caseSensitive;
/*  74 */     Map<String, Field> copy = new LinkedHashMap();
/*  75 */     copy.putAll(original.fields);
/*  76 */     this.fields = (immutable ? Collections.unmodifiableMap(copy) : copy);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  82 */     if (this == obj)
/*  83 */       return true;
/*  84 */     if ((obj == null) || (getClass() != obj.getClass()))
/*  85 */       return false;
/*  86 */     Fields that = (Fields)obj;
/*  87 */     if (getSize() != that.getSize())
/*  88 */       return false;
/*  89 */     if (this.caseSensitive != that.caseSensitive)
/*  90 */       return false;
/*  91 */     for (Entry<String, Field> entry : this.fields.entrySet())
/*     */     {
/*  93 */       String name = (String)entry.getKey();
/*  94 */       Field value = (Field)entry.getValue();
/*  95 */       if (!value.equals(that.get(name), this.caseSensitive))
/*  96 */         return false;
/*     */     }
/*  98 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 104 */     return this.fields.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<String> getNames()
/*     */   {
/* 112 */     Set<String> result = new LinkedHashSet();
/* 113 */     for (Field field : this.fields.values())
/* 114 */       result.add(field.getName());
/* 115 */     return result;
/*     */   }
/*     */   
/*     */   private String normalizeName(String name)
/*     */   {
/* 120 */     return this.caseSensitive ? name : name.toLowerCase(Locale.ENGLISH);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Field get(String name)
/*     */   {
/* 129 */     return (Field)this.fields.get(normalizeName(name));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(String name, String value)
/*     */   {
/* 141 */     Field field = new Field(name, value);
/* 142 */     this.fields.put(normalizeName(name), field);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(Field field)
/*     */   {
/* 152 */     if (field != null) {
/* 153 */       this.fields.put(normalizeName(field.getName()), field);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(String name, String value)
/*     */   {
/* 165 */     String key = normalizeName(name);
/* 166 */     Field field = (Field)this.fields.get(key);
/* 167 */     if (field == null)
/*     */     {
/*     */ 
/* 170 */       field = new Field(name, value);
/* 171 */       this.fields.put(key, field);
/*     */     }
/*     */     else
/*     */     {
/* 175 */       field = new Field(field.getName(), field.getValues(), new String[] { value }, null);
/* 176 */       this.fields.put(key, field);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Field remove(String name)
/*     */   {
/* 188 */     return (Field)this.fields.remove(normalizeName(name));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 197 */     this.fields.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 205 */     return this.fields.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 213 */     return this.fields.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<Field> iterator()
/*     */   {
/* 222 */     return this.fields.values().iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 228 */     return this.fields.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class Field
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final List<String> values;
/*     */     
/*     */ 
/*     */     public Field(String name, String value)
/*     */     {
/* 242 */       this(name, Collections.singletonList(value), new String[0]);
/*     */     }
/*     */     
/*     */     private Field(String name, List<String> values, String... moreValues)
/*     */     {
/* 247 */       this.name = name;
/* 248 */       List<String> list = new ArrayList(values.size() + moreValues.length);
/* 249 */       list.addAll(values);
/* 250 */       list.addAll(Arrays.asList(moreValues));
/* 251 */       this.values = Collections.unmodifiableList(list);
/*     */     }
/*     */     
/*     */     public boolean equals(Field that, boolean caseSensitive)
/*     */     {
/* 256 */       if (this == that)
/* 257 */         return true;
/* 258 */       if (that == null)
/* 259 */         return false;
/* 260 */       if (caseSensitive)
/* 261 */         return equals(that);
/* 262 */       return (this.name.equalsIgnoreCase(that.name)) && (this.values.equals(that.values));
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 268 */       if (this == obj)
/* 269 */         return true;
/* 270 */       if ((obj == null) || (getClass() != obj.getClass()))
/* 271 */         return false;
/* 272 */       Field that = (Field)obj;
/* 273 */       return (this.name.equals(that.name)) && (this.values.equals(that.values));
/*     */     }
/*     */     
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 279 */       int result = this.name.hashCode();
/* 280 */       result = 31 * result + this.values.hashCode();
/* 281 */       return result;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getName()
/*     */     {
/* 289 */       return this.name;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getValue()
/*     */     {
/* 297 */       return (String)this.values.get(0);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Integer getValueAsInt()
/*     */     {
/* 310 */       String value = getValue();
/* 311 */       return value == null ? null : Integer.valueOf(value);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public List<String> getValues()
/*     */     {
/* 319 */       return this.values;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean hasMultipleValues()
/*     */     {
/* 327 */       return this.values.size() > 1;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 333 */       return String.format("%s=%s", new Object[] { this.name, this.values });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Fields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */