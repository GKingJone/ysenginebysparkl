/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ClassKey;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RootNameLookup
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected transient LRUMap<ClassKey, PropertyName> _rootNames;
/*    */   
/*    */   public RootNameLookup()
/*    */   {
/* 22 */     this._rootNames = new LRUMap(20, 200);
/*    */   }
/*    */   
/*    */   public PropertyName findRootName(JavaType rootType, MapperConfig<?> config) {
/* 26 */     return findRootName(rootType.getRawClass(), config);
/*    */   }
/*    */   
/*    */   public PropertyName findRootName(Class<?> rootType, MapperConfig<?> config)
/*    */   {
/* 31 */     ClassKey key = new ClassKey(rootType);
/* 32 */     PropertyName name = (PropertyName)this._rootNames.get(key);
/* 33 */     if (name != null) {
/* 34 */       return name;
/*    */     }
/* 36 */     BeanDescription beanDesc = config.introspectClassAnnotations(rootType);
/* 37 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/* 38 */     AnnotatedClass ac = beanDesc.getClassInfo();
/* 39 */     name = intr.findRootName(ac);
/*    */     
/* 41 */     if ((name == null) || (!name.hasSimpleName()))
/*    */     {
/* 43 */       name = PropertyName.construct(rootType.getSimpleName());
/*    */     }
/* 45 */     this._rootNames.put(key, name);
/* 46 */     return name;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Object readResolve()
/*    */   {
/* 60 */     return new RootNameLookup();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\RootNameLookup.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */