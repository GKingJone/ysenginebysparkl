/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ext;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ import org.w3c.dom.Node;
/*    */ import org.w3c.dom.bootstrap.DOMImplementationRegistry;
/*    */ import org.w3c.dom.ls.DOMImplementationLS;
/*    */ import org.w3c.dom.ls.LSSerializer;
/*    */ 
/*    */ public class DOMSerializer extends StdSerializer<Node>
/*    */ {
/*    */   protected final DOMImplementationLS _domImpl;
/*    */   
/*    */   public DOMSerializer()
/*    */   {
/* 24 */     super(Node.class);
/*    */     DOMImplementationRegistry registry;
/*    */     try {
/* 27 */       registry = DOMImplementationRegistry.newInstance();
/*    */     } catch (Exception e) {
/* 29 */       throw new IllegalStateException("Could not instantiate DOMImplementationRegistry: " + e.getMessage(), e);
/*    */     }
/* 31 */     this._domImpl = ((DOMImplementationLS)registry.getDOMImplementation("LS"));
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(Node value, JsonGenerator jgen, SerializerProvider provider)
/*    */     throws IOException, JsonGenerationException
/*    */   {
/* 38 */     if (this._domImpl == null) throw new IllegalStateException("Could not find DOM LS");
/* 39 */     LSSerializer writer = this._domImpl.createLSSerializer();
/* 40 */     jgen.writeString(writer.writeToString(value));
/*    */   }
/*    */   
/*    */ 
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 46 */     return createSchemaNode("string", true);
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
/*    */   {
/* 51 */     if (visitor != null) visitor.expectAnyFormat(typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ext\DOMSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */