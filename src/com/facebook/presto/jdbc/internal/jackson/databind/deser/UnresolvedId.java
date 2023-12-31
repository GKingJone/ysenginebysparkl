/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnresolvedId
/*    */ {
/*    */   private final Object _id;
/*    */   private final JsonLocation _location;
/*    */   private final Class<?> _type;
/*    */   
/*    */   public UnresolvedId(Object id, Class<?> type, JsonLocation where)
/*    */   {
/* 16 */     this._id = id;
/* 17 */     this._type = type;
/* 18 */     this._location = where;
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getId()
/*    */   {
/* 24 */     return this._id;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/* 29 */   public Class<?> getType() { return this._type; }
/* 30 */   public JsonLocation getLocation() { return this._location; }
/*    */   
/*    */   public String toString()
/*    */   {
/* 34 */     return String.format("Object id [%s] (for %s) at %s", new Object[] { this._id, this._type == null ? "NULL" : this._type.getName(), this._location });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\UnresolvedId.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */