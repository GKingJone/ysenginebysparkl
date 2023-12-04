/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeBindings;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeModifier;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Optional;
/*    */ import java.util.OptionalDouble;
/*    */ import java.util.OptionalInt;
/*    */ import java.util.OptionalLong;
/*    */ 
/*    */ 
/*    */ public class Jdk8TypeModifier
/*    */   extends TypeModifier
/*    */ {
/*    */   public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory)
/*    */   {
/* 20 */     if ((type.isReferenceType()) || (type.isContainerType())) {
/* 21 */       return type;
/*    */     }
/* 23 */     Class<?> raw = type.getRawClass();
/*    */     
/*    */     JavaType refType;
/*    */     
/* 27 */     if (raw == Optional.class)
/*    */     {
/*    */ 
/* 30 */       refType = type.containedTypeOrUnknown(0); } else { JavaType refType;
/* 31 */       if (raw == OptionalInt.class) {
/* 32 */         refType = typeFactory.constructType(Integer.TYPE); } else { JavaType refType;
/* 33 */         if (raw == OptionalLong.class) {
/* 34 */           refType = typeFactory.constructType(Long.TYPE); } else { JavaType refType;
/* 35 */           if (raw == OptionalDouble.class) {
/* 36 */             refType = typeFactory.constructType(Double.TYPE);
/*    */           } else
/* 38 */             return type; } } }
/*    */     JavaType refType;
/* 40 */     return ReferenceType.upgradeFrom(type, refType);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\Jdk8TypeModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */