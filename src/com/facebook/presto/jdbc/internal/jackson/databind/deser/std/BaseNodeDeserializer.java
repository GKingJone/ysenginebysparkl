/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ArrayNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.JsonNodeFactory;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.RawValue;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class BaseNodeDeserializer<T extends JsonNode>
/*     */   extends StdDeserializer<T>
/*     */ {
/*     */   public BaseNodeDeserializer(Class<T> vc)
/*     */   {
/* 141 */     super(vc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 152 */     return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCachable()
/*     */   {
/* 160 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected void _reportProblem(JsonParser p, String msg)
/*     */     throws JsonMappingException
/*     */   {
/* 170 */     throw JsonMappingException.from(p, msg);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue)
/*     */     throws JsonProcessingException
/*     */   {
/* 193 */     if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
/* 194 */       ctxt.reportMappingException("Duplicate field '%s' for ObjectNode: not allowed when FAIL_ON_READING_DUP_TREE_KEY enabled", new Object[] { fieldName });
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
/*     */   protected final ObjectNode deserializeObject(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
/*     */     throws IOException
/*     */   {
/* 208 */     ObjectNode node = nodeFactory.objectNode();
/*     */     String key;
/* 210 */     if (p.isExpectedStartObjectToken()) {
/* 211 */       key = p.nextFieldName();
/*     */     } else {
/* 213 */       JsonToken t = p.getCurrentToken();
/* 214 */       if (t == JsonToken.END_OBJECT) {
/* 215 */         return node;
/*     */       }
/* 217 */       if (t != JsonToken.FIELD_NAME)
/* 218 */         return (ObjectNode)ctxt.handleUnexpectedToken(handledType(), p);
/*     */     }
/* 220 */     for (String key = p.getCurrentName(); 
/*     */         
/* 222 */         key != null; key = p.nextFieldName())
/*     */     {
/* 224 */       JsonToken t = p.nextToken();
/* 225 */       if (t == null)
/* 226 */         throw ctxt.mappingException("Unexpected end-of-input when binding data into ObjectNode");
/*     */       JsonNode value;
/* 228 */       switch (t.id()) {
/*     */       case 1: 
/* 230 */         value = deserializeObject(p, ctxt, nodeFactory);
/* 231 */         break;
/*     */       case 3: 
/* 233 */         value = deserializeArray(p, ctxt, nodeFactory);
/* 234 */         break;
/*     */       case 12: 
/* 236 */         value = _fromEmbedded(p, ctxt, nodeFactory);
/* 237 */         break;
/*     */       case 6: 
/* 239 */         value = nodeFactory.textNode(p.getText());
/* 240 */         break;
/*     */       case 7: 
/* 242 */         value = _fromInt(p, ctxt, nodeFactory);
/* 243 */         break;
/*     */       case 9: 
/* 245 */         value = nodeFactory.booleanNode(true);
/* 246 */         break;
/*     */       case 10: 
/* 248 */         value = nodeFactory.booleanNode(false);
/* 249 */         break;
/*     */       case 11: 
/* 251 */         value = nodeFactory.nullNode();
/* 252 */         break;
/*     */       case 2: case 4: case 5: case 8: default: 
/* 254 */         value = deserializeAny(p, ctxt, nodeFactory);
/*     */       }
/* 256 */       JsonNode old = node.replace(key, value);
/* 257 */       if (old != null) {
/* 258 */         _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
/*     */       }
/*     */     }
/*     */     
/* 262 */     return node;
/*     */   }
/*     */   
/*     */   protected final ArrayNode deserializeArray(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
/*     */     throws IOException
/*     */   {
/* 268 */     ArrayNode node = nodeFactory.arrayNode();
/*     */     for (;;) {
/* 270 */       JsonToken t = p.nextToken();
/* 271 */       switch (t.id()) {
/*     */       case 1: 
/* 273 */         node.add(deserializeObject(p, ctxt, nodeFactory));
/* 274 */         break;
/*     */       case 3: 
/* 276 */         node.add(deserializeArray(p, ctxt, nodeFactory));
/* 277 */         break;
/*     */       case 4: 
/* 279 */         return node;
/*     */       case 12: 
/* 281 */         node.add(_fromEmbedded(p, ctxt, nodeFactory));
/* 282 */         break;
/*     */       case 6: 
/* 284 */         node.add(nodeFactory.textNode(p.getText()));
/* 285 */         break;
/*     */       case 7: 
/* 287 */         node.add(_fromInt(p, ctxt, nodeFactory));
/* 288 */         break;
/*     */       case 9: 
/* 290 */         node.add(nodeFactory.booleanNode(true));
/* 291 */         break;
/*     */       case 10: 
/* 293 */         node.add(nodeFactory.booleanNode(false));
/* 294 */         break;
/*     */       case 11: 
/* 296 */         node.add(nodeFactory.nullNode());
/* 297 */         break;
/*     */       case 2: case 5: case 8: default: 
/* 299 */         node.add(deserializeAny(p, ctxt, nodeFactory));
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */   protected final JsonNode deserializeAny(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
/*     */     throws IOException
/*     */   {
/* 308 */     switch (p.getCurrentTokenId()) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 5: 
/* 312 */       return deserializeObject(p, ctxt, nodeFactory);
/*     */     case 3: 
/* 314 */       return deserializeArray(p, ctxt, nodeFactory);
/*     */     case 12: 
/* 316 */       return _fromEmbedded(p, ctxt, nodeFactory);
/*     */     case 6: 
/* 318 */       return nodeFactory.textNode(p.getText());
/*     */     case 7: 
/* 320 */       return _fromInt(p, ctxt, nodeFactory);
/*     */     case 8: 
/* 322 */       return _fromFloat(p, ctxt, nodeFactory);
/*     */     case 9: 
/* 324 */       return nodeFactory.booleanNode(true);
/*     */     case 10: 
/* 326 */       return nodeFactory.booleanNode(false);
/*     */     case 11: 
/* 328 */       return nodeFactory.nullNode();
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 337 */     return (JsonNode)ctxt.handleUnexpectedToken(handledType(), p);
/*     */   }
/*     */   
/*     */ 
/*     */   protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
/*     */     throws IOException
/*     */   {
/* 344 */     int feats = ctxt.getDeserializationFeatures();
/* 345 */     NumberType nt; NumberType nt; if ((feats & F_MASK_INT_COERCIONS) != 0) { NumberType nt;
/* 346 */       if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
/* 347 */         nt = NumberType.BIG_INTEGER; } else { NumberType nt;
/* 348 */         if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
/* 349 */           nt = NumberType.LONG;
/*     */         } else
/* 351 */           nt = p.getNumberType();
/*     */       }
/*     */     } else {
/* 354 */       nt = p.getNumberType();
/*     */     }
/* 356 */     if (nt == NumberType.INT) {
/* 357 */       return nodeFactory.numberNode(p.getIntValue());
/*     */     }
/* 359 */     if (nt == NumberType.LONG) {
/* 360 */       return nodeFactory.numberNode(p.getLongValue());
/*     */     }
/* 362 */     return nodeFactory.numberNode(p.getBigIntegerValue());
/*     */   }
/*     */   
/*     */   protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
/*     */     throws IOException
/*     */   {
/* 368 */     NumberType nt = p.getNumberType();
/* 369 */     if (nt == NumberType.BIG_DECIMAL) {
/* 370 */       return nodeFactory.numberNode(p.getDecimalValue());
/*     */     }
/* 372 */     if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS))
/*     */     {
/*     */ 
/* 375 */       double d = p.getDoubleValue();
/* 376 */       if ((Double.isInfinite(d)) || (Double.isNaN(d))) {
/* 377 */         return nodeFactory.numberNode(d);
/*     */       }
/* 379 */       return nodeFactory.numberNode(BigDecimal.valueOf(d));
/*     */     }
/* 381 */     return nodeFactory.numberNode(p.getDoubleValue());
/*     */   }
/*     */   
/*     */   protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
/*     */     throws IOException
/*     */   {
/* 387 */     Object ob = p.getEmbeddedObject();
/* 388 */     if (ob == null) {
/* 389 */       return nodeFactory.nullNode();
/*     */     }
/* 391 */     Class<?> type = ob.getClass();
/* 392 */     if (type == byte[].class) {
/* 393 */       return nodeFactory.binaryNode((byte[])ob);
/*     */     }
/*     */     
/* 396 */     if ((ob instanceof RawValue)) {
/* 397 */       return nodeFactory.rawValueNode((RawValue)ob);
/*     */     }
/* 399 */     if ((ob instanceof JsonNode))
/*     */     {
/* 401 */       return (JsonNode)ob;
/*     */     }
/*     */     
/* 404 */     return nodeFactory.pojoNode(ob);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\BaseNodeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */