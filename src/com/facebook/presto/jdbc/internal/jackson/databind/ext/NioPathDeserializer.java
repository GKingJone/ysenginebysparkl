/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ext;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdScalarDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ 
/*    */ 
/*    */ public class NioPathDeserializer
/*    */   extends StdScalarDeserializer<Path>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public NioPathDeserializer()
/*    */   {
/* 21 */     super(Path.class);
/*    */   }
/*    */   
/*    */   public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 25 */     if (!p.hasToken(JsonToken.VALUE_STRING)) {
/* 26 */       return (Path)ctxt.handleUnexpectedToken(Path.class, p);
/*    */     }
/* 28 */     String value = p.getText();
/*    */     
/*    */ 
/* 31 */     if (value.indexOf(':') < 0) {
/* 32 */       return Paths.get(value, new String[0]);
/*    */     }
/*    */     try {
/* 35 */       URI uri = new URI(value);
/* 36 */       return Paths.get(uri);
/*    */     } catch (URISyntaxException e) {
/* 38 */       return (Path)ctxt.handleInstantiationProblem(handledType(), value, e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ext\NioPathDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */