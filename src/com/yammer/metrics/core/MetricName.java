/*     */ package com.yammer.metrics.core;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MetricName
/*     */   implements Comparable<MetricName>
/*     */ {
/*     */   private final String group;
/*     */   private final String type;
/*     */   private final String name;
/*     */   private final String scope;
/*     */   private final String mBeanName;
/*     */   
/*     */   public MetricName(Class<?> klass, String name)
/*     */   {
/*  23 */     this(klass, name, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MetricName(String group, String type, String name)
/*     */   {
/*  34 */     this(group, type, name, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MetricName(Class<?> klass, String name, String scope)
/*     */   {
/*  45 */     this(klass.getPackage() == null ? "" : klass.getPackage().getName(), klass.getSimpleName().replaceAll("\\$$", ""), name, scope);
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
/*     */   public MetricName(String group, String type, String name, String scope)
/*     */   {
/*  60 */     this(group, type, name, scope, createMBeanName(group, type, name, scope));
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
/*     */   public MetricName(String group, String type, String name, String scope, String mBeanName)
/*     */   {
/*  74 */     if ((group == null) || (type == null)) {
/*  75 */       throw new IllegalArgumentException("Both group and type need to be specified");
/*     */     }
/*  77 */     if (name == null) {
/*  78 */       throw new IllegalArgumentException("Name needs to be specified");
/*     */     }
/*  80 */     this.group = group;
/*  81 */     this.type = type;
/*  82 */     this.name = name;
/*  83 */     this.scope = scope;
/*  84 */     this.mBeanName = mBeanName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getGroup()
/*     */   {
/*  94 */     return this.group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getType()
/*     */   {
/* 104 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 113 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getScope()
/*     */   {
/* 122 */     return this.scope;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasScope()
/*     */   {
/* 131 */     return this.scope != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMBeanName()
/*     */   {
/* 140 */     return this.mBeanName;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 145 */     if (this == o) return true;
/* 146 */     if ((o == null) || (getClass() != o.getClass())) return false;
/* 147 */     MetricName that = (MetricName)o;
/* 148 */     return this.mBeanName.equals(that.mBeanName);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 153 */     return this.mBeanName.hashCode();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 158 */     return this.mBeanName;
/*     */   }
/*     */   
/*     */   public int compareTo(MetricName o)
/*     */   {
/* 163 */     return this.mBeanName.compareTo(o.mBeanName);
/*     */   }
/*     */   
/*     */   private static String createMBeanName(String group, String type, String name, String scope) {
/* 167 */     StringBuilder nameBuilder = new StringBuilder();
/* 168 */     nameBuilder.append(ObjectName.quote(group));
/* 169 */     nameBuilder.append(":type=");
/* 170 */     nameBuilder.append(ObjectName.quote(type));
/* 171 */     if (scope != null) {
/* 172 */       nameBuilder.append(",scope=");
/* 173 */       nameBuilder.append(ObjectName.quote(scope));
/*     */     }
/* 175 */     if (name.length() > 0) {
/* 176 */       nameBuilder.append(",name=");
/* 177 */       nameBuilder.append(ObjectName.quote(name));
/*     */     }
/* 179 */     return nameBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String chooseGroup(String group, Class<?> klass)
/*     */   {
/* 189 */     if ((group == null) || (group.isEmpty())) {
/* 190 */       group = klass.getPackage() == null ? "" : klass.getPackage().getName();
/*     */     }
/* 192 */     return group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String chooseType(String type, Class<?> klass)
/*     */   {
/* 202 */     if ((type == null) || (type.isEmpty())) {
/* 203 */       type = klass.getSimpleName().replaceAll("\\$$", "");
/*     */     }
/* 205 */     return type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String chooseName(String name, Method method)
/*     */   {
/* 215 */     if ((name == null) || (name.isEmpty())) {
/* 216 */       name = method.getName();
/*     */     }
/* 218 */     return name;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\MetricName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */