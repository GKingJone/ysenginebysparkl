/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InetAddressSerializer
/*    */   extends StdScalarSerializer<InetAddress>
/*    */ {
/*    */   public InetAddressSerializer()
/*    */   {
/* 18 */     super(InetAddress.class);
/*    */   }
/*    */   
/*    */   public void serialize(InetAddress value, JsonGenerator jgen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 24 */     String str = value.toString().trim();
/* 25 */     int ix = str.indexOf('/');
/* 26 */     if (ix >= 0) {
/* 27 */       if (ix == 0) {
/* 28 */         str = str.substring(1);
/*    */       } else {
/* 30 */         str = str.substring(0, ix);
/*    */       }
/*    */     }
/* 33 */     jgen.writeString(str);
/*    */   }
/*    */   
/*    */ 
/*    */   public void serializeWithType(InetAddress value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException, JsonGenerationException
/*    */   {
/* 40 */     typeSer.writeTypePrefixForScalar(value, jgen, InetAddress.class);
/* 41 */     serialize(value, jgen, provider);
/* 42 */     typeSer.writeTypeSuffixForScalar(value, jgen);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\InetAddressSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */