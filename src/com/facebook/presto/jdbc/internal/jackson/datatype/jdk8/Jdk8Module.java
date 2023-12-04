/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.Module;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.Module.SetupContext;
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
/*    */ public class Jdk8Module
/*    */   extends Module
/*    */ {
/* 21 */   protected boolean _cfgHandleAbsentAsNull = false;
/*    */   
/*    */   public void setupModule(SetupContext context)
/*    */   {
/* 25 */     context.addSerializers(new Jdk8Serializers());
/* 26 */     context.addDeserializers(new Jdk8Deserializers());
/*    */     
/* 28 */     context.addTypeModifier(new Jdk8TypeModifier());
/*    */     
/*    */ 
/* 31 */     if (this._cfgHandleAbsentAsNull) {
/* 32 */       context.addBeanSerializerModifier(new Jdk8BeanSerializerModifier());
/*    */     }
/*    */   }
/*    */   
/*    */   public Version version()
/*    */   {
/* 38 */     return PackageVersion.VERSION;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Jdk8Module configureAbsentsAsNulls(boolean state)
/*    */   {
/* 55 */     this._cfgHandleAbsentAsNull = state;
/* 56 */     return this;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 61 */     return getClass().hashCode();
/*    */   }
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 66 */     return this == o;
/*    */   }
/*    */   
/*    */   public String getModuleName()
/*    */   {
/* 71 */     return "Jdk8Module";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\Jdk8Module.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */