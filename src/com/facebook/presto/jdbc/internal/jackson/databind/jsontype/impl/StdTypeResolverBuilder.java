/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.Id;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.NoClass;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StdTypeResolverBuilder
/*     */   implements TypeResolverBuilder<StdTypeResolverBuilder>
/*     */ {
/*     */   protected JsonTypeInfo.Id _idType;
/*     */   protected JsonTypeInfo.As _includeAs;
/*     */   protected String _typeProperty;
/*  29 */   protected boolean _typeIdVisible = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class<?> _defaultImpl;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TypeIdResolver _customIdResolver;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static StdTypeResolverBuilder noTypeInfoBuilder()
/*     */   {
/*  50 */     return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes)
/*     */   {
/*  57 */     if (idType == null) {
/*  58 */       throw new IllegalArgumentException("idType can not be null");
/*     */     }
/*  60 */     this._idType = idType;
/*  61 */     this._customIdResolver = idRes;
/*     */     
/*  63 */     this._typeProperty = idType.getDefaultPropertyName();
/*  64 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
/*     */   {
/*  71 */     if (this._idType == JsonTypeInfo.Id.NONE) return null;
/*  72 */     TypeIdResolver idRes = idResolver(config, baseType, subtypes, true, false);
/*  73 */     switch (this._includeAs) {
/*     */     case WRAPPER_ARRAY: 
/*  75 */       return new AsArrayTypeSerializer(idRes, null);
/*     */     case PROPERTY: 
/*  77 */       return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
/*     */     case WRAPPER_OBJECT: 
/*  79 */       return new AsWrapperTypeSerializer(idRes, null);
/*     */     case EXTERNAL_PROPERTY: 
/*  81 */       return new AsExternalTypeSerializer(idRes, null, this._typeProperty);
/*     */     
/*     */     case EXISTING_PROPERTY: 
/*  84 */       return new AsExistingPropertyTypeSerializer(idRes, null, this._typeProperty);
/*     */     }
/*  86 */     throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
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
/*     */   public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
/*     */   {
/*  99 */     if (this._idType == JsonTypeInfo.Id.NONE) { return null;
/*     */     }
/* 101 */     TypeIdResolver idRes = idResolver(config, baseType, subtypes, false, true);
/*     */     
/*     */     JavaType defaultImpl;
/*     */     JavaType defaultImpl;
/* 105 */     if (this._defaultImpl == null) {
/* 106 */       defaultImpl = null;
/*     */     }
/*     */     else
/*     */     {
/*     */       JavaType defaultImpl;
/*     */       
/*     */ 
/*     */ 
/* 114 */       if ((this._defaultImpl == Void.class) || (this._defaultImpl == NoClass.class))
/*     */       {
/* 116 */         defaultImpl = config.getTypeFactory().constructType(this._defaultImpl);
/*     */       } else {
/* 118 */         defaultImpl = config.getTypeFactory().constructSpecializedType(baseType, this._defaultImpl);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 124 */     switch (this._includeAs) {
/*     */     case WRAPPER_ARRAY: 
/* 126 */       return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*     */     
/*     */     case PROPERTY: 
/*     */     case EXISTING_PROPERTY: 
/* 130 */       return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl, this._includeAs);
/*     */     
/*     */     case WRAPPER_OBJECT: 
/* 133 */       return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*     */     
/*     */     case EXTERNAL_PROPERTY: 
/* 136 */       return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*     */     }
/*     */     
/* 139 */     throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs)
/*     */   {
/* 150 */     if (includeAs == null) {
/* 151 */       throw new IllegalArgumentException("includeAs can not be null");
/*     */     }
/* 153 */     this._includeAs = includeAs;
/* 154 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StdTypeResolverBuilder typeProperty(String typeIdPropName)
/*     */   {
/* 164 */     if ((typeIdPropName == null) || (typeIdPropName.length() == 0)) {
/* 165 */       typeIdPropName = this._idType.getDefaultPropertyName();
/*     */     }
/* 167 */     this._typeProperty = typeIdPropName;
/* 168 */     return this;
/*     */   }
/*     */   
/*     */   public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl)
/*     */   {
/* 173 */     this._defaultImpl = defaultImpl;
/* 174 */     return this;
/*     */   }
/*     */   
/*     */   public StdTypeResolverBuilder typeIdVisibility(boolean isVisible)
/*     */   {
/* 179 */     this._typeIdVisible = isVisible;
/* 180 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */   public Class<?> getDefaultImpl() { return this._defaultImpl; }
/*     */   
/* 191 */   public String getTypeProperty() { return this._typeProperty; }
/* 192 */   public boolean isTypeIdVisible() { return this._typeIdVisible; }
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
/*     */   protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser)
/*     */   {
/* 209 */     if (this._customIdResolver != null) return this._customIdResolver;
/* 210 */     if (this._idType == null) throw new IllegalStateException("Can not build, 'init()' not yet called");
/* 211 */     switch (this._idType) {
/*     */     case CLASS: 
/* 213 */       return new ClassNameIdResolver(baseType, config.getTypeFactory());
/*     */     case MINIMAL_CLASS: 
/* 215 */       return new MinimalClassNameIdResolver(baseType, config.getTypeFactory());
/*     */     case NAME: 
/* 217 */       return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
/*     */     case NONE: 
/* 219 */       return null;
/*     */     }
/*     */     
/* 222 */     throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\impl\StdTypeResolverBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */