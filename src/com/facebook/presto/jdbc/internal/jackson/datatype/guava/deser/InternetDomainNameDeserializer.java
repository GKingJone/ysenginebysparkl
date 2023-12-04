/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.net.InternetDomainName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.FromStringDeserializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InternetDomainNameDeserializer
/*    */   extends FromStringDeserializer<InternetDomainName>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 14 */   public static final InternetDomainNameDeserializer std = new InternetDomainNameDeserializer();
/*    */   
/* 16 */   public InternetDomainNameDeserializer() { super(InternetDomainName.class); }
/*    */   
/*    */   protected InternetDomainName _deserialize(String value, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 21 */     return InternetDomainName.from(value);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\InternetDomainNameDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */