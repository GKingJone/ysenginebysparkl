/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.HashSet;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JdkDeserializers
/*    */ {
/* 15 */   private static final HashSet<String> _classNames = new HashSet();
/*    */   
/*    */   static {
/* 18 */     Class<?>[] types = { UUID.class, AtomicBoolean.class, StackTraceElement.class, ByteBuffer.class };
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 24 */     for (Class<?> cls : types) _classNames.add(cls.getName());
/* 25 */     for (Class<?> cls : FromStringDeserializer.types()) _classNames.add(cls.getName());
/*    */   }
/*    */   
/*    */   public static JsonDeserializer<?> find(Class<?> rawType, String clsName)
/*    */   {
/* 30 */     if (_classNames.contains(clsName)) {
/* 31 */       JsonDeserializer<?> d = FromStringDeserializer.findDeserializer(rawType);
/* 32 */       if (d != null) {
/* 33 */         return d;
/*    */       }
/* 35 */       if (rawType == UUID.class) {
/* 36 */         return new UUIDDeserializer();
/*    */       }
/* 38 */       if (rawType == StackTraceElement.class) {
/* 39 */         return new StackTraceElementDeserializer();
/*    */       }
/* 41 */       if (rawType == AtomicBoolean.class)
/*    */       {
/* 43 */         return new AtomicBooleanDeserializer();
/*    */       }
/* 45 */       if (rawType == ByteBuffer.class) {
/* 46 */         return new ByteBufferDeserializer();
/*    */       }
/*    */     }
/* 49 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\JdkDeserializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */