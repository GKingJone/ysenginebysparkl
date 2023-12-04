/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.BoundType;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ForwardingSetMultimap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.HashMultisetDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableListDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.TreeMultisetDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.multimap.list.ArrayListMultimapDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.multimap.list.LinkedListMultimapDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.multimap.set.HashMultimapDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.multimap.set.LinkedHashMultimapDeserializer;
/*     */ 
/*     */ public class GuavaDeserializers extends com.facebook.presto.jdbc.internal.jackson.databind.deser.Deserializers.Base
/*     */ {
/*     */   protected BoundType _defaultBoundType;
/*     */   
/*     */   public GuavaDeserializers()
/*     */   {
/*  30 */     this(null);
/*     */   }
/*     */   
/*     */   public GuavaDeserializers(BoundType defaultBoundType) {
/*  34 */     this._defaultBoundType = defaultBoundType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */     throws JsonMappingException
/*     */   {
/*  46 */     Class<?> raw = type.getRawClass();
/*     */     
/*     */ 
/*  49 */     if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection.class.isAssignableFrom(raw)) {
/*  50 */       if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableList.class.isAssignableFrom(raw)) {
/*  51 */         return new ImmutableListDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/*  54 */       if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableMultiset.class.isAssignableFrom(raw))
/*     */       {
/*  56 */         if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedMultiset.class.isAssignableFrom(raw))
/*     */         {
/*  58 */           requireCollectionOfComparableElements(type, "ImmutableSortedMultiset");
/*  59 */           return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableSortedMultisetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */         }
/*     */         
/*     */ 
/*  63 */         return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableMultisetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*  65 */       if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet.class.isAssignableFrom(raw))
/*     */       {
/*  67 */         if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedSet.class.isAssignableFrom(raw))
/*     */         {
/*     */ 
/*     */ 
/*  71 */           requireCollectionOfComparableElements(type, "ImmutableSortedSet");
/*  72 */           return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableSortedSetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */         }
/*     */         
/*     */ 
/*  76 */         return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableSetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/*     */ 
/*  80 */       return new ImmutableListDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */     }
/*     */     
/*     */ 
/*  84 */     if (com.facebook.presto.jdbc.internal.guava.collect.Multiset.class.isAssignableFrom(raw)) {
/*  85 */       if (com.facebook.presto.jdbc.internal.guava.collect.SortedMultiset.class.isAssignableFrom(raw)) {
/*  86 */         if (com.facebook.presto.jdbc.internal.guava.collect.TreeMultiset.class.isAssignableFrom(raw)) {
/*  87 */           return new TreeMultisetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */         }
/*     */         
/*     */ 
/*  91 */         return new TreeMultisetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/*     */ 
/*  95 */       if (com.facebook.presto.jdbc.internal.guava.collect.LinkedHashMultiset.class.isAssignableFrom(raw)) {
/*  96 */         return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.LinkedHashMultisetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*  98 */       if (com.facebook.presto.jdbc.internal.guava.collect.HashMultiset.class.isAssignableFrom(raw)) {
/*  99 */         return new HashMultisetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */       }
/* 101 */       if (com.facebook.presto.jdbc.internal.guava.collect.EnumMultiset.class.isAssignableFrom(raw)) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 106 */       return new HashMultisetDeserializer(type, elementTypeDeserializer, elementDeserializer);
/*     */     }
/*     */     
/* 109 */     return null;
/*     */   }
/*     */   
/*     */   private void requireCollectionOfComparableElements(CollectionType actualType, String targetType) {
/* 113 */     Class<?> elemType = actualType.getContentType().getRawClass();
/* 114 */     if (!Comparable.class.isAssignableFrom(elemType)) {
/* 115 */       throw new IllegalArgumentException("Can not handle " + targetType + " with elements that are not Comparable<?> (" + elemType.getName() + ")");
/*     */     }
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
/*     */   public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */     throws JsonMappingException
/*     */   {
/* 130 */     Class<?> raw = type.getRawClass();
/*     */     
/*     */ 
/* 133 */     if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap.class.isAssignableFrom(raw)) {
/* 134 */       if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedMap.class.isAssignableFrom(raw)) {
/* 135 */         return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableSortedMapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/* 138 */       if (com.facebook.presto.jdbc.internal.guava.collect.ImmutableBiMap.class.isAssignableFrom(raw)) {
/* 139 */         return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableBiMapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/*     */ 
/* 143 */       return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.ImmutableMapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */     }
/*     */     
/*     */ 
/* 147 */     if ((com.facebook.presto.jdbc.internal.guava.collect.BiMap.class.isAssignableFrom(raw)) && (
/* 148 */       (!com.facebook.presto.jdbc.internal.guava.collect.EnumBiMap.class.isAssignableFrom(raw)) || (
/*     */       
/*     */ 
/* 151 */       (!com.facebook.presto.jdbc.internal.guava.collect.EnumHashBiMap.class.isAssignableFrom(raw)) || 
/*     */       
/*     */ 
/* 154 */       (com.facebook.presto.jdbc.internal.guava.collect.HashBiMap.class.isAssignableFrom(raw))))) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 161 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */     throws JsonMappingException
/*     */   {
/* 171 */     Class<?> raw = type.getRawClass();
/*     */     
/*     */ 
/* 174 */     if (com.facebook.presto.jdbc.internal.guava.collect.ListMultimap.class.isAssignableFrom(raw)) {
/* 175 */       if ((!com.facebook.presto.jdbc.internal.guava.collect.ImmutableListMultimap.class.isAssignableFrom(raw)) || 
/*     */       
/*     */ 
/* 178 */         (com.facebook.presto.jdbc.internal.guava.collect.ArrayListMultimap.class.isAssignableFrom(raw))) {
/* 179 */         return new ArrayListMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/* 182 */       if (com.facebook.presto.jdbc.internal.guava.collect.LinkedListMultimap.class.isAssignableFrom(raw)) {
/* 183 */         return new LinkedListMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/* 186 */       if (com.facebook.presto.jdbc.internal.guava.collect.ForwardingListMultimap.class.isAssignableFrom(raw)) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 191 */       return new ArrayListMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 196 */     if (com.facebook.presto.jdbc.internal.guava.collect.SetMultimap.class.isAssignableFrom(raw))
/*     */     {
/*     */ 
/* 199 */       if (((!com.facebook.presto.jdbc.internal.guava.collect.SortedSetMultimap.class.isAssignableFrom(raw)) || (
/* 200 */         (com.facebook.presto.jdbc.internal.guava.collect.TreeMultimap.class.isAssignableFrom(raw)) && 
/*     */         
/*     */ 
/* 203 */         (!com.facebook.presto.jdbc.internal.guava.collect.ForwardingSortedSetMultimap.class.isAssignableFrom(raw)))) || 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 208 */         (com.facebook.presto.jdbc.internal.guava.collect.ImmutableSetMultimap.class.isAssignableFrom(raw)))
/*     */       {
/* 210 */         return new LinkedHashMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/* 213 */       if (com.facebook.presto.jdbc.internal.guava.collect.HashMultimap.class.isAssignableFrom(raw)) {
/* 214 */         return new HashMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/* 217 */       if (com.facebook.presto.jdbc.internal.guava.collect.LinkedHashMultimap.class.isAssignableFrom(raw)) {
/* 218 */         return new LinkedHashMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */       }
/*     */       
/* 221 */       if (ForwardingSetMultimap.class.isAssignableFrom(raw)) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 226 */       return new HashMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 231 */     if (com.facebook.presto.jdbc.internal.guava.collect.Multimap.class.isAssignableFrom(raw)) {
/* 232 */       return new LinkedListMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*     */     }
/*     */     
/*     */ 
/* 236 */     if (com.facebook.presto.jdbc.internal.guava.collect.Table.class.isAssignableFrom(raw)) {}
/*     */     
/*     */ 
/*     */ 
/* 240 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
/*     */   {
/* 251 */     if (refType.hasRawClass(com.facebook.presto.jdbc.internal.guava.base.Optional.class)) {
/* 252 */       return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.GuavaOptionalDeserializer(refType, contentTypeDeserializer, contentDeserializer);
/*     */     }
/* 254 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
/*     */   {
/* 261 */     if (type.hasRawClass(com.facebook.presto.jdbc.internal.guava.collect.Range.class)) {
/* 262 */       return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.RangeDeserializer(this._defaultBoundType, type);
/*     */     }
/* 264 */     if (type.hasRawClass(com.facebook.presto.jdbc.internal.guava.net.HostAndPort.class)) {
/* 265 */       return com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.HostAndPortDeserializer.std;
/*     */     }
/* 267 */     if (type.hasRawClass(com.facebook.presto.jdbc.internal.guava.net.InternetDomainName.class)) {
/* 268 */       return com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.InternetDomainNameDeserializer.std;
/*     */     }
/* 270 */     if (type.hasRawClass(com.facebook.presto.jdbc.internal.guava.hash.HashCode.class)) {
/* 271 */       return com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.HashCodeDeserializer.std;
/*     */     }
/* 273 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\GuavaDeserializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */