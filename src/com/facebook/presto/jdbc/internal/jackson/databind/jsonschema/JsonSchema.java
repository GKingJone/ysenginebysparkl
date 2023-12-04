/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.jsonschema;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.node.JsonNodeFactory;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class JsonSchema
/*    */ {
/*    */   private final ObjectNode schema;
/*    */   
/*    */   @JsonCreator
/*    */   public JsonSchema(ObjectNode schema)
/*    */   {
/* 38 */     this.schema = schema;
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
/*    */   @JsonValue
/*    */   public ObjectNode getSchemaNode()
/*    */   {
/* 53 */     return this.schema;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 59 */     return this.schema.toString();
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 65 */     return this.schema.hashCode();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 71 */     if (o == this) return true;
/* 72 */     if (o == null) return false;
/* 73 */     if (!(o instanceof JsonSchema)) { return false;
/*    */     }
/* 75 */     JsonSchema other = (JsonSchema)o;
/* 76 */     if (this.schema == null) {
/* 77 */       return other.schema == null;
/*    */     }
/* 79 */     return this.schema.equals(other.schema);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static JsonNode getDefaultSchemaNode()
/*    */   {
/* 89 */     ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
/* 90 */     objectNode.put("type", "any");
/*    */     
/*    */ 
/* 93 */     return objectNode;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonschema\JsonSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */