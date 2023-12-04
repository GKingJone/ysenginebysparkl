/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.net.HostAndPort;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.FromStringDeserializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class HostAndPortDeserializer
/*    */   extends FromStringDeserializer<HostAndPort>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 15 */   public static final HostAndPortDeserializer std = new HostAndPortDeserializer();
/*    */   
/* 17 */   public HostAndPortDeserializer() { super(HostAndPort.class); }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public HostAndPort deserialize(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 25 */     if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
/* 26 */       JsonNode root = (JsonNode)jp.readValueAsTree();
/* 27 */       String host = root.path("hostText").asText();
/* 28 */       JsonNode n = root.get("port");
/* 29 */       if (n == null) {
/* 30 */         return HostAndPort.fromString(host);
/*    */       }
/* 32 */       return HostAndPort.fromParts(host, n.asInt());
/*    */     }
/* 34 */     return (HostAndPort)super.deserialize(jp, ctxt);
/*    */   }
/*    */   
/*    */   protected HostAndPort _deserialize(String value, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 40 */     return HostAndPort.fromString(value);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\HostAndPortDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */