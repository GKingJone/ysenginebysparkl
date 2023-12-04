/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.io.SerializedString;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ObjectIdWriter
/*    */ {
/*    */   public final JavaType idType;
/*    */   public final SerializableString propertyName;
/*    */   public final ObjectIdGenerator<?> generator;
/*    */   public final JsonSerializer<Object> serializer;
/*    */   public final boolean alwaysAsId;
/*    */   
/*    */   protected ObjectIdWriter(JavaType t, SerializableString propName, ObjectIdGenerator<?> gen, JsonSerializer<?> ser, boolean alwaysAsId)
/*    */   {
/* 53 */     this.idType = t;
/* 54 */     this.propertyName = propName;
/* 55 */     this.generator = gen;
/* 56 */     this.serializer = ser;
/* 57 */     this.alwaysAsId = alwaysAsId;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ObjectIdWriter construct(JavaType idType, PropertyName propName, ObjectIdGenerator<?> generator, boolean alwaysAsId)
/*    */   {
/* 70 */     String simpleName = propName == null ? null : propName.getSimpleName();
/* 71 */     return construct(idType, simpleName, generator, alwaysAsId);
/*    */   }
/*    */   
/*    */ 
/*    */   @Deprecated
/*    */   public static ObjectIdWriter construct(JavaType idType, String propName, ObjectIdGenerator<?> generator, boolean alwaysAsId)
/*    */   {
/* 78 */     SerializableString serName = propName == null ? null : new SerializedString(propName);
/* 79 */     return new ObjectIdWriter(idType, serName, generator, null, alwaysAsId);
/*    */   }
/*    */   
/*    */   public ObjectIdWriter withSerializer(JsonSerializer<?> ser) {
/* 83 */     return new ObjectIdWriter(this.idType, this.propertyName, this.generator, ser, this.alwaysAsId);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ObjectIdWriter withAlwaysAsId(boolean newState)
/*    */   {
/* 90 */     if (newState == this.alwaysAsId) {
/* 91 */       return this;
/*    */     }
/* 93 */     return new ObjectIdWriter(this.idType, this.propertyName, this.generator, this.serializer, newState);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\ObjectIdWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */