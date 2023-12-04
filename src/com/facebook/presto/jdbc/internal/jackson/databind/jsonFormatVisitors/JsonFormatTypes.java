/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public enum JsonFormatTypes
/*    */ {
/* 10 */   STRING, 
/* 11 */   NUMBER, 
/* 12 */   INTEGER, 
/* 13 */   BOOLEAN, 
/* 14 */   OBJECT, 
/* 15 */   ARRAY, 
/* 16 */   NULL, 
/* 17 */   ANY;
/*    */   
/* 19 */   static { _byLCName = new HashMap();
/*    */     
/* 21 */     for (JsonFormatTypes t : values())
/* 22 */       _byLCName.put(t.name().toLowerCase(), t);
/*    */   }
/*    */   
/*    */   private static final Map<String, JsonFormatTypes> _byLCName;
/*    */   @JsonValue
/*    */   public String value() {
/* 28 */     return name().toLowerCase();
/*    */   }
/*    */   
/*    */   @JsonCreator
/*    */   public static JsonFormatTypes forValue(String s) {
/* 33 */     return (JsonFormatTypes)_byLCName.get(s);
/*    */   }
/*    */   
/*    */   private JsonFormatTypes() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonFormatTypes.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */