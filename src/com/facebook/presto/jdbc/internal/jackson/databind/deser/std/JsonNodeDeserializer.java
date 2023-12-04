/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ArrayNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.JsonNodeFactory;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.NullNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonNodeDeserializer
/*     */   extends BaseNodeDeserializer<JsonNode>
/*     */ {
/*  24 */   private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();
/*     */   
/*  26 */   protected JsonNodeDeserializer() { super(JsonNode.class); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass)
/*     */   {
/*  33 */     if (nodeClass == ObjectNode.class) {
/*  34 */       return ObjectDeserializer.getInstance();
/*     */     }
/*  36 */     if (nodeClass == ArrayNode.class) {
/*  37 */       return ArrayDeserializer.getInstance();
/*     */     }
/*     */     
/*  40 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonNode getNullValue(DeserializationContext ctxt)
/*     */   {
/*  51 */     return NullNode.getInstance();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonNode getNullValue()
/*     */   {
/*  57 */     return NullNode.getInstance();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonNode deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/*  68 */     switch (p.getCurrentTokenId()) {
/*     */     case 1: 
/*  70 */       return deserializeObject(p, ctxt, ctxt.getNodeFactory());
/*     */     case 3: 
/*  72 */       return deserializeArray(p, ctxt, ctxt.getNodeFactory());
/*     */     }
/*  74 */     return deserializeAny(p, ctxt, ctxt.getNodeFactory());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final class ObjectDeserializer
/*     */     extends BaseNodeDeserializer<ObjectNode>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  89 */     protected static final ObjectDeserializer _instance = new ObjectDeserializer();
/*     */     
/*  91 */     protected ObjectDeserializer() { super(); }
/*     */     
/*  93 */     public static ObjectDeserializer getInstance() { return _instance; }
/*     */     
/*     */     public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/*  98 */       if ((p.isExpectedStartObjectToken()) || (p.hasToken(JsonToken.FIELD_NAME))) {
/*  99 */         return deserializeObject(p, ctxt, ctxt.getNodeFactory());
/*     */       }
/*     */       
/*     */ 
/* 103 */       if (p.hasToken(JsonToken.END_OBJECT)) {
/* 104 */         return ctxt.getNodeFactory().objectNode();
/*     */       }
/* 106 */       return (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static final class ArrayDeserializer
/*     */     extends BaseNodeDeserializer<ArrayNode>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 115 */     protected static final ArrayDeserializer _instance = new ArrayDeserializer();
/*     */     
/* 117 */     protected ArrayDeserializer() { super(); }
/*     */     
/* 119 */     public static ArrayDeserializer getInstance() { return _instance; }
/*     */     
/*     */     public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 124 */       if (p.isExpectedStartArrayToken()) {
/* 125 */         return deserializeArray(p, ctxt, ctxt.getNodeFactory());
/*     */       }
/* 127 */       return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\JsonNodeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */