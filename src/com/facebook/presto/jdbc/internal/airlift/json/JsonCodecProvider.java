/*    */ package com.facebook.presto.jdbc.internal.airlift.json;
/*    */ 
/*    */ import com.google.inject.Inject;
/*    */ import com.google.inject.Provider;
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
/*    */ 
/*    */ class JsonCodecProvider
/*    */   implements Provider<JsonCodec<?>>
/*    */ {
/*    */   private final Type type;
/*    */   private JsonCodecFactory jsonCodecFactory;
/*    */   
/*    */   public JsonCodecProvider(Type type)
/*    */   {
/* 30 */     this.type = type;
/*    */   }
/*    */   
/*    */   @Inject
/*    */   public void setJsonCodecFactory(JsonCodecFactory jsonCodecFactory)
/*    */   {
/* 36 */     this.jsonCodecFactory = jsonCodecFactory;
/*    */   }
/*    */   
/*    */ 
/*    */   public JsonCodec<?> get()
/*    */   {
/* 42 */     return this.jsonCodecFactory.jsonCodec(this.type);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 48 */     if (this == o) {
/* 49 */       return true;
/*    */     }
/* 51 */     if ((o == null) || (getClass() != o.getClass())) {
/* 52 */       return false;
/*    */     }
/*    */     
/* 55 */     JsonCodecProvider that = (JsonCodecProvider)o;
/*    */     
/* 57 */     if (!this.type.equals(that.type)) {
/* 58 */       return false;
/*    */     }
/*    */     
/* 61 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 67 */     return this.type.hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\JsonCodecProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */