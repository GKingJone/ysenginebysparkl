/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
/*    */ import java.net.Inet6Address;
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InetSocketAddressSerializer
/*    */   extends StdScalarSerializer<InetSocketAddress>
/*    */ {
/*    */   public InetSocketAddressSerializer()
/*    */   {
/* 20 */     super(InetSocketAddress.class);
/*    */   }
/*    */   
/*    */   public void serialize(InetSocketAddress value, JsonGenerator jgen, SerializerProvider provider) throws IOException
/*    */   {
/* 25 */     InetAddress addr = value.getAddress();
/* 26 */     String str = addr == null ? value.getHostName() : addr.toString().trim();
/* 27 */     int ix = str.indexOf('/');
/* 28 */     if (ix >= 0) {
/* 29 */       if (ix == 0) {
/* 30 */         str = (addr instanceof Inet6Address) ? "[" + str.substring(1) + "]" : str.substring(1);
/*    */ 
/*    */       }
/*    */       else
/*    */       {
/* 35 */         str = str.substring(0, ix);
/*    */       }
/*    */     }
/*    */     
/* 39 */     jgen.writeString(str + ":" + value.getPort());
/*    */   }
/*    */   
/*    */ 
/*    */   public void serializeWithType(InetSocketAddress value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException, JsonGenerationException
/*    */   {
/* 46 */     typeSer.writeTypePrefixForScalar(value, jgen, InetSocketAddress.class);
/* 47 */     serialize(value, jgen, provider);
/* 48 */     typeSer.writeTypeSuffixForScalar(value, jgen);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\InetSocketAddressSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */