/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.SubtypeResolver;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StdSubtypeResolver
/*     */   extends SubtypeResolver
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected LinkedHashSet<NamedType> _registeredSubtypes;
/*     */   
/*     */   public void registerSubtypes(NamedType... types)
/*     */   {
/*  33 */     if (this._registeredSubtypes == null) {
/*  34 */       this._registeredSubtypes = new LinkedHashSet();
/*     */     }
/*  36 */     for (NamedType type : types) {
/*  37 */       this._registeredSubtypes.add(type);
/*     */     }
/*     */   }
/*     */   
/*     */   public void registerSubtypes(Class<?>... classes)
/*     */   {
/*  43 */     NamedType[] types = new NamedType[classes.length];
/*  44 */     int i = 0; for (int len = classes.length; i < len; i++) {
/*  45 */       types[i] = new NamedType(classes[i]);
/*     */     }
/*  47 */     registerSubtypes(types);
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
/*     */   public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedMember property, JavaType baseType)
/*     */   {
/*  60 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*     */     
/*  62 */     Class<?> rawBase = baseType == null ? property.getRawType() : baseType.getRawClass();
/*     */     
/*  64 */     HashMap<NamedType, NamedType> collected = new HashMap();
/*     */     
/*  66 */     if (this._registeredSubtypes != null) {
/*  67 */       for (NamedType subtype : this._registeredSubtypes)
/*     */       {
/*  69 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/*  70 */           AnnotatedClass curr = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), config);
/*  71 */           _collectAndResolve(curr, subtype, config, ai, collected);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  77 */     Collection<NamedType> st = ai.findSubtypes(property);
/*  78 */     if (st != null) {
/*  79 */       for (NamedType nt : st) {
/*  80 */         AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(nt.getType(), config);
/*  81 */         _collectAndResolve(ac, nt, config, ai, collected);
/*     */       }
/*     */     }
/*     */     
/*  85 */     NamedType rootType = new NamedType(rawBase, null);
/*  86 */     AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(rawBase, config);
/*     */     
/*     */ 
/*  89 */     _collectAndResolve(ac, rootType, config, ai, collected);
/*     */     
/*  91 */     return new ArrayList(collected.values());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedClass type)
/*     */   {
/*  98 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*  99 */     HashMap<NamedType, NamedType> subtypes = new HashMap();
/*     */     Class<?> rawBase;
/* 101 */     if (this._registeredSubtypes != null) {
/* 102 */       rawBase = type.getRawType();
/* 103 */       for (NamedType subtype : this._registeredSubtypes)
/*     */       {
/* 105 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/* 106 */           AnnotatedClass curr = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), config);
/* 107 */           _collectAndResolve(curr, subtype, config, ai, subtypes);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 112 */     NamedType rootType = new NamedType(type.getRawType(), null);
/* 113 */     _collectAndResolve(type, rootType, config, ai, subtypes);
/* 114 */     return new ArrayList(subtypes.values());
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
/*     */   public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedMember property, JavaType baseType)
/*     */   {
/* 127 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 128 */     Class<?> rawBase = baseType == null ? property.getRawType() : baseType.getRawClass();
/*     */     
/*     */ 
/* 131 */     Set<Class<?>> typesHandled = new HashSet();
/* 132 */     Map<String, NamedType> byName = new LinkedHashMap();
/*     */     
/*     */ 
/* 135 */     NamedType rootType = new NamedType(rawBase, null);
/* 136 */     AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(rawBase, config);
/* 137 */     _collectAndResolveByTypeId(ac, rootType, config, typesHandled, byName);
/*     */     
/*     */ 
/* 140 */     Collection<NamedType> st = ai.findSubtypes(property);
/* 141 */     if (st != null) {
/* 142 */       for (NamedType nt : st) {
/* 143 */         ac = AnnotatedClass.constructWithoutSuperTypes(nt.getType(), config);
/* 144 */         _collectAndResolveByTypeId(ac, nt, config, typesHandled, byName);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 149 */     if (this._registeredSubtypes != null) {
/* 150 */       for (NamedType subtype : this._registeredSubtypes)
/*     */       {
/* 152 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/* 153 */           AnnotatedClass curr = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), config);
/* 154 */           _collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
/*     */         }
/*     */       }
/*     */     }
/* 158 */     return _combineNamedAndUnnamed(typesHandled, byName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedClass type)
/*     */   {
/* 165 */     Set<Class<?>> typesHandled = new HashSet();
/* 166 */     Map<String, NamedType> byName = new LinkedHashMap();
/*     */     
/* 168 */     NamedType rootType = new NamedType(type.getRawType(), null);
/* 169 */     _collectAndResolveByTypeId(type, rootType, config, typesHandled, byName);
/*     */     Class<?> rawBase;
/* 171 */     if (this._registeredSubtypes != null) {
/* 172 */       rawBase = type.getRawType();
/* 173 */       for (NamedType subtype : this._registeredSubtypes)
/*     */       {
/* 175 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/* 176 */           AnnotatedClass curr = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), config);
/* 177 */           _collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
/*     */         }
/*     */       }
/*     */     }
/* 181 */     return _combineNamedAndUnnamed(typesHandled, byName);
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
/*     */   @Deprecated
/*     */   public Collection<NamedType> collectAndResolveSubtypes(AnnotatedMember property, MapperConfig<?> config, AnnotationIntrospector ai, JavaType baseType)
/*     */   {
/* 195 */     return collectAndResolveSubtypesByClass(config, property, baseType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public Collection<NamedType> collectAndResolveSubtypes(AnnotatedClass type, MapperConfig<?> config, AnnotationIntrospector ai)
/*     */   {
/* 203 */     return collectAndResolveSubtypesByClass(config, type);
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
/*     */ 
/*     */ 
/*     */   protected void _collectAndResolve(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, AnnotationIntrospector ai, HashMap<NamedType, NamedType> collectedSubtypes)
/*     */   {
/* 220 */     if (!namedType.hasName()) {
/* 221 */       String name = ai.findTypeName(annotatedType);
/* 222 */       if (name != null) {
/* 223 */         namedType = new NamedType(namedType.getType(), name);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 228 */     if (collectedSubtypes.containsKey(namedType))
/*     */     {
/* 230 */       if (namedType.hasName()) {
/* 231 */         NamedType prev = (NamedType)collectedSubtypes.get(namedType);
/* 232 */         if (!prev.hasName()) {
/* 233 */           collectedSubtypes.put(namedType, namedType);
/*     */         }
/*     */       }
/* 236 */       return;
/*     */     }
/*     */     
/* 239 */     collectedSubtypes.put(namedType, namedType);
/* 240 */     Collection<NamedType> st = ai.findSubtypes(annotatedType);
/* 241 */     if ((st != null) && (!st.isEmpty())) {
/* 242 */       for (NamedType subtype : st) {
/* 243 */         AnnotatedClass subtypeClass = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), config);
/* 244 */         _collectAndResolve(subtypeClass, subtype, config, ai, collectedSubtypes);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _collectAndResolveByTypeId(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, Set<Class<?>> typesHandled, Map<String, NamedType> byName)
/*     */   {
/* 257 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 258 */     if (!namedType.hasName()) {
/* 259 */       String name = ai.findTypeName(annotatedType);
/* 260 */       if (name != null) {
/* 261 */         namedType = new NamedType(namedType.getType(), name);
/*     */       }
/*     */     }
/* 264 */     if (namedType.hasName()) {
/* 265 */       byName.put(namedType.getName(), namedType);
/*     */     }
/*     */     
/*     */ 
/* 269 */     if (typesHandled.add(namedType.getType())) {
/* 270 */       Collection<NamedType> st = ai.findSubtypes(annotatedType);
/* 271 */       if ((st != null) && (!st.isEmpty())) {
/* 272 */         for (NamedType subtype : st) {
/* 273 */           AnnotatedClass subtypeClass = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), config);
/* 274 */           _collectAndResolveByTypeId(subtypeClass, subtype, config, typesHandled, byName);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Collection<NamedType> _combineNamedAndUnnamed(Set<Class<?>> typesHandled, Map<String, NamedType> byName)
/*     */   {
/* 287 */     ArrayList<NamedType> result = new ArrayList(byName.values());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 292 */     for (NamedType t : byName.values()) {
/* 293 */       typesHandled.remove(t.getType());
/*     */     }
/* 295 */     for (Class<?> cls : typesHandled) {
/* 296 */       result.add(new NamedType(cls));
/*     */     }
/* 298 */     return result;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\impl\StdSubtypeResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */