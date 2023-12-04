/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.hash.HashCode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.FromStringDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ public class HashCodeDeserializer
/*    */   extends FromStringDeserializer<HashCode>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 14 */   public static final HashCodeDeserializer std = new HashCodeDeserializer();
/*    */   
/* 16 */   public HashCodeDeserializer() { super(HashCode.class); }
/*    */   
/*    */   protected HashCode _deserialize(String value, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 21 */     return HashCode.fromString(value.toLowerCase(Locale.ENGLISH));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\HashCodeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */