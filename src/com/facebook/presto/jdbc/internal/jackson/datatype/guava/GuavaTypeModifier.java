/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.Multimap;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeBindings;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeModifier;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GuavaTypeModifier
/*    */   extends TypeModifier
/*    */ {
/*    */   public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory)
/*    */   {
/* 32 */     if ((type.isReferenceType()) || (type.isContainerType())) {
/* 33 */       return type;
/*    */     }
/*    */     
/* 36 */     Class<?> raw = type.getRawClass();
/*    */     
/* 38 */     if (raw == Multimap.class) {
/* 39 */       return MapLikeType.upgradeFrom(type, type.containedTypeOrUnknown(0), type.containedTypeOrUnknown(1));
/*    */     }
/*    */     
/*    */ 
/* 43 */     if (raw == Optional.class) {
/* 44 */       return ReferenceType.upgradeFrom(type, type.containedTypeOrUnknown(0));
/*    */     }
/* 46 */     return type;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\GuavaTypeModifier.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */