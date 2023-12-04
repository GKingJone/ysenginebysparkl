/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.node;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NullNode
/*    */   extends ValueNode
/*    */ {
/* 18 */   public static final NullNode instance = new NullNode();
/*    */   
/*    */   public static NullNode getInstance()
/*    */   {
/* 22 */     return instance;
/*    */   }
/*    */   
/*    */   public JsonNodeType getNodeType() {
/* 26 */     return JsonNodeType.NULL;
/*    */   }
/*    */   
/* 29 */   public JsonToken asToken() { return JsonToken.VALUE_NULL; }
/*    */   
/* 31 */   public String asText(String defaultValue) { return defaultValue; }
/* 32 */   public String asText() { return "null"; }
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
/*    */   public final void serialize(JsonGenerator g, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 47 */     provider.defaultSerializeNull(g);
/*    */   }
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 52 */     return o == this;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 57 */     return JsonNodeType.NULL.ordinal();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\node\NullNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */