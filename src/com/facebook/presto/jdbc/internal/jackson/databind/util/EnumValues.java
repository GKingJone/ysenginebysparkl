/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class EnumValues
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<Enum<?>> _enumClass;
/*     */   private final Enum<?>[] _values;
/*     */   private final SerializableString[] _textual;
/*     */   private transient EnumMap<?, SerializableString> _asMap;
/*     */   
/*     */   private EnumValues(Class<Enum<?>> enumClass, SerializableString[] textual)
/*     */   {
/*  27 */     this._enumClass = enumClass;
/*  28 */     this._values = ((Enum[])enumClass.getEnumConstants());
/*  29 */     this._textual = textual;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EnumValues construct(SerializationConfig config, Class<Enum<?>> enumClass)
/*     */   {
/*  37 */     if (config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/*  38 */       return constructFromToString(config, enumClass);
/*     */     }
/*  40 */     return constructFromName(config, enumClass);
/*     */   }
/*     */   
/*     */ 
/*     */   public static EnumValues constructFromName(MapperConfig<?> config, Class<Enum<?>> enumClass)
/*     */   {
/*  46 */     Class<? extends Enum<?>> enumCls = ClassUtil.findEnumType(enumClass);
/*  47 */     Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
/*  48 */     if (enumValues == null) {
/*  49 */       throw new IllegalArgumentException("Can not determine enum constants for Class " + enumClass.getName());
/*     */     }
/*  51 */     String[] names = config.getAnnotationIntrospector().findEnumValues(enumCls, enumValues, new String[enumValues.length]);
/*  52 */     SerializableString[] textual = new SerializableString[enumValues.length];
/*  53 */     int i = 0; for (int len = enumValues.length; i < len; i++) {
/*  54 */       Enum<?> en = enumValues[i];
/*  55 */       String name = names[i];
/*  56 */       if (name == null) {
/*  57 */         name = en.name();
/*     */       }
/*  59 */       textual[en.ordinal()] = config.compileString(name);
/*     */     }
/*  61 */     return new EnumValues(enumClass, textual);
/*     */   }
/*     */   
/*     */   public static EnumValues constructFromToString(MapperConfig<?> config, Class<Enum<?>> enumClass)
/*     */   {
/*  66 */     Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
/*  67 */     Enum<?>[] values = (Enum[])cls.getEnumConstants();
/*  68 */     if (values != null) {
/*  69 */       SerializableString[] textual = new SerializableString[values.length];
/*  70 */       for (Enum<?> en : values) {
/*  71 */         textual[en.ordinal()] = config.compileString(en.toString());
/*     */       }
/*  73 */       return new EnumValues(enumClass, textual);
/*     */     }
/*  75 */     throw new IllegalArgumentException("Can not determine enum constants for Class " + enumClass.getName());
/*     */   }
/*     */   
/*     */   public SerializableString serializedValueFor(Enum<?> key) {
/*  79 */     return this._textual[key.ordinal()];
/*     */   }
/*     */   
/*     */   public Collection<SerializableString> values() {
/*  83 */     return Arrays.asList(this._textual);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<Enum<?>> enums()
/*     */   {
/*  92 */     return Arrays.asList(this._values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public EnumMap<?, SerializableString> internalMap()
/*     */   {
/* 100 */     EnumMap<?, SerializableString> result = this._asMap;
/* 101 */     if (result == null)
/*     */     {
/* 103 */       Map<Enum<?>, SerializableString> map = new LinkedHashMap();
/* 104 */       for (Enum<?> en : this._values) {
/* 105 */         map.put(en, this._textual[en.ordinal()]);
/*     */       }
/* 107 */       result = new EnumMap(map);
/*     */     }
/* 109 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public Class<Enum<?>> getEnumClass()
/*     */   {
/* 115 */     return this._enumClass;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\EnumValues.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */