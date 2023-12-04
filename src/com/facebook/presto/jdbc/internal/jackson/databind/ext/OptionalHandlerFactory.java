/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ext;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.Deserializers;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.Serializers;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
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
/*     */ public class OptionalHandlerFactory
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
/*     */   private static final String SERIALIZERS_FOR_JAVAX_XML = "com.facebook.presto.jdbc.internal.jackson.databind.ext.CoreXMLSerializers";
/*     */   private static final String DESERIALIZERS_FOR_JAVAX_XML = "com.facebook.presto.jdbc.internal.jackson.databind.ext.CoreXMLDeserializers";
/*     */   private static final String SERIALIZER_FOR_DOM_NODE = "com.facebook.presto.jdbc.internal.jackson.databind.ext.DOMSerializer";
/*     */   private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "com.facebook.presto.jdbc.internal.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
/*     */   private static final String DESERIALIZER_FOR_DOM_NODE = "com.facebook.presto.jdbc.internal.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
/*     */   private static final Class<?> CLASS_DOM_NODE;
/*     */   private static final Class<?> CLASS_DOM_DOCUMENT;
/*     */   private static final Java7Support _jdk7Helper;
/*     */   
/*     */   static
/*     */   {
/*  46 */     Class<?> doc = null;Class<?> node = null;
/*     */     try {
/*  48 */       node = Node.class;
/*  49 */       doc = Document.class;
/*     */     }
/*     */     catch (Exception e) {
/*  52 */       System.err.println("WARNING: could not load DOM Node and/or Document classes");
/*     */     }
/*  54 */     CLASS_DOM_NODE = node;
/*  55 */     CLASS_DOM_DOCUMENT = doc;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */     Java7Support x = null;
/*     */     try {
/*  66 */       x = Java7Support.instance();
/*     */     } catch (Throwable t) {}
/*  68 */     _jdk7Helper = x;
/*     */   }
/*     */   
/*  71 */   public static final OptionalHandlerFactory instance = new OptionalHandlerFactory();
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
/*     */   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
/*     */   {
/*  84 */     Class<?> rawType = type.getRawClass();
/*     */     
/*  86 */     if (_jdk7Helper != null) {
/*  87 */       JsonSerializer<?> ser = _jdk7Helper.getSerializerForJavaNioFilePath(rawType);
/*  88 */       if (ser != null) {
/*  89 */         return ser;
/*     */       }
/*     */     }
/*  92 */     if ((CLASS_DOM_NODE != null) && (CLASS_DOM_NODE.isAssignableFrom(rawType))) {
/*  93 */       return (JsonSerializer)instantiate("com.facebook.presto.jdbc.internal.jackson.databind.ext.DOMSerializer");
/*     */     }
/*  95 */     String className = rawType.getName();
/*     */     String factoryName;
/*  97 */     if ((className.startsWith("javax.xml.")) || (hasSuperClassStartingWith(rawType, "javax.xml."))) {
/*  98 */       factoryName = "com.facebook.presto.jdbc.internal.jackson.databind.ext.CoreXMLSerializers";
/*     */     } else {
/* 100 */       return null;
/*     */     }
/*     */     String factoryName;
/* 103 */     Object ob = instantiate(factoryName);
/* 104 */     if (ob == null) {
/* 105 */       return null;
/*     */     }
/* 107 */     return ((Serializers)ob).findSerializer(config, type, beanDesc);
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonDeserializer<?> findDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 114 */     Class<?> rawType = type.getRawClass();
/*     */     
/* 116 */     if (_jdk7Helper != null) {
/* 117 */       JsonDeserializer<?> deser = _jdk7Helper.getDeserializerForJavaNioFilePath(rawType);
/* 118 */       if (deser != null) {
/* 119 */         return deser;
/*     */       }
/*     */     }
/* 122 */     if ((CLASS_DOM_NODE != null) && (CLASS_DOM_NODE.isAssignableFrom(rawType))) {
/* 123 */       return (JsonDeserializer)instantiate("com.facebook.presto.jdbc.internal.jackson.databind.ext.DOMDeserializer$NodeDeserializer");
/*     */     }
/* 125 */     if ((CLASS_DOM_DOCUMENT != null) && (CLASS_DOM_DOCUMENT.isAssignableFrom(rawType))) {
/* 126 */       return (JsonDeserializer)instantiate("com.facebook.presto.jdbc.internal.jackson.databind.ext.DOMDeserializer$DocumentDeserializer");
/*     */     }
/* 128 */     String className = rawType.getName();
/*     */     String factoryName;
/* 130 */     if ((className.startsWith("javax.xml.")) || (hasSuperClassStartingWith(rawType, "javax.xml.")))
/*     */     {
/* 132 */       factoryName = "com.facebook.presto.jdbc.internal.jackson.databind.ext.CoreXMLDeserializers";
/*     */     } else
/* 134 */       return null;
/*     */     String factoryName;
/* 136 */     Object ob = instantiate(factoryName);
/* 137 */     if (ob == null) {
/* 138 */       return null;
/*     */     }
/* 140 */     return ((Deserializers)ob).findBeanDeserializer(type, config, beanDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object instantiate(String className)
/*     */   {
/*     */     try
/*     */     {
/* 152 */       return Class.forName(className).newInstance();
/*     */     }
/*     */     catch (LinkageError e) {}catch (Exception e) {}
/*     */     
/* 156 */     return null;
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
/*     */   private boolean hasSuperClassStartingWith(Class<?> rawType, String prefix)
/*     */   {
/* 169 */     for (Class<?> supertype = rawType.getSuperclass(); supertype != null; supertype = supertype.getSuperclass()) {
/* 170 */       if (supertype == Object.class) {
/* 171 */         return false;
/*     */       }
/* 173 */       if (supertype.getName().startsWith(prefix)) {
/* 174 */         return true;
/*     */       }
/*     */     }
/* 177 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ext\OptionalHandlerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */