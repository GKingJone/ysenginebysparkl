/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.node;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import java.io.IOException;
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
/*     */ public final class MissingNode
/*     */   extends ValueNode
/*     */ {
/*  25 */   private static final MissingNode instance = new MissingNode();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  32 */   public <T extends JsonNode> T deepCopy() { return this; }
/*     */   
/*  34 */   public static MissingNode getInstance() { return instance; }
/*     */   
/*     */ 
/*     */   public JsonNodeType getNodeType()
/*     */   {
/*  39 */     return JsonNodeType.MISSING;
/*     */   }
/*     */   
/*  42 */   public JsonToken asToken() { return JsonToken.NOT_AVAILABLE; }
/*     */   
/*  44 */   public String asText() { return ""; }
/*     */   
/*  46 */   public String asText(String defaultValue) { return defaultValue; }
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
/*     */   public final void serialize(JsonGenerator jg, SerializerProvider provider)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/*  67 */     jg.writeNull();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/*  75 */     g.writeNull();
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
/*     */   public boolean equals(Object o)
/*     */   {
/*  89 */     return o == this;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  95 */     return "";
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 100 */     return JsonNodeType.MISSING.ordinal();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\node\MissingNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */