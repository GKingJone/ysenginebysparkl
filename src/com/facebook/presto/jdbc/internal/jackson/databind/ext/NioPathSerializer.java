/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ext;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdScalarSerializer;
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import java.nio.file.Path;
/*    */ 
/*    */ 
/*    */ public class NioPathSerializer
/*    */   extends StdScalarSerializer<Path>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public NioPathSerializer()
/*    */   {
/* 18 */     super(Path.class);
/*    */   }
/*    */   
/*    */   public void serialize(Path value, JsonGenerator gen, SerializerProvider serializers) throws IOException
/*    */   {
/* 23 */     gen.writeString(value.toUri().toString());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ext\NioPathSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */