/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ResolvableDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class StdNodeBasedDeserializer<T>
/*    */   extends StdDeserializer<T>
/*    */   implements ResolvableDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected JsonDeserializer<Object> _treeDeserializer;
/*    */   
/*    */   protected StdNodeBasedDeserializer(JavaType targetType)
/*    */   {
/* 35 */     super(targetType);
/*    */   }
/*    */   
/*    */   protected StdNodeBasedDeserializer(Class<T> targetType) {
/* 39 */     super(targetType);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected StdNodeBasedDeserializer(StdNodeBasedDeserializer<?> src)
/*    */   {
/* 47 */     super(src);
/* 48 */     this._treeDeserializer = src._treeDeserializer;
/*    */   }
/*    */   
/*    */   public void resolve(DeserializationContext ctxt) throws JsonMappingException
/*    */   {
/* 53 */     this._treeDeserializer = ctxt.findRootValueDeserializer(ctxt.constructType(JsonNode.class));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract T convert(JsonNode paramJsonNode, DeserializationContext paramDeserializationContext)
/*    */     throws IOException;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public T deserialize(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 72 */     JsonNode n = (JsonNode)this._treeDeserializer.deserialize(jp, ctxt);
/* 73 */     return (T)convert(n, ctxt);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer td)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 84 */     JsonNode n = (JsonNode)this._treeDeserializer.deserializeWithType(jp, ctxt, td);
/* 85 */     return convert(n, ctxt);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StdNodeBasedDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */