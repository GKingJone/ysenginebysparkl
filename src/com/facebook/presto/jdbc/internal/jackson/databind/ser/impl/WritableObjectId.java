/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.io.IOException;
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
/*    */ public final class WritableObjectId
/*    */ {
/*    */   public final ObjectIdGenerator<?> generator;
/*    */   public Object id;
/* 26 */   protected boolean idWritten = false;
/*    */   
/*    */   public WritableObjectId(ObjectIdGenerator<?> generator) {
/* 29 */     this.generator = generator;
/*    */   }
/*    */   
/*    */   public boolean writeAsId(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException
/*    */   {
/* 34 */     if ((this.id != null) && ((this.idWritten) || (w.alwaysAsId)))
/*    */     {
/* 36 */       if (gen.canWriteObjectId()) {
/* 37 */         gen.writeObjectRef(String.valueOf(this.id));
/*    */       } else {
/* 39 */         w.serializer.serialize(this.id, gen, provider);
/*    */       }
/* 41 */       return true;
/*    */     }
/* 43 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Object generateId(Object forPojo)
/*    */   {
/* 50 */     if (this.id == null) {
/* 51 */       this.id = this.generator.generateId(forPojo);
/*    */     }
/* 53 */     return this.id;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void writeAsField(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w)
/*    */     throws IOException
/*    */   {
/* 62 */     this.idWritten = true;
/*    */     
/*    */ 
/* 65 */     if (gen.canWriteObjectId())
/*    */     {
/* 67 */       gen.writeObjectId(String.valueOf(this.id));
/* 68 */       return;
/*    */     }
/*    */     
/* 71 */     SerializableString name = w.propertyName;
/* 72 */     if (name != null) {
/* 73 */       gen.writeFieldName(name);
/* 74 */       w.serializer.serialize(this.id, gen, provider);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\WritableObjectId.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */