/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator.IdKey;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnresolvedForwardReference
/*    */   extends JsonMappingException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ReadableObjectId _roid;
/*    */   private List<UnresolvedId> _unresolvedIds;
/*    */   
/*    */   public UnresolvedForwardReference(JsonParser p, String msg, JsonLocation loc, ReadableObjectId roid)
/*    */   {
/* 28 */     super(p, msg, loc);
/* 29 */     this._roid = roid;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public UnresolvedForwardReference(JsonParser p, String msg)
/*    */   {
/* 36 */     super(p, msg);
/* 37 */     this._unresolvedIds = new ArrayList();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public UnresolvedForwardReference(String msg, JsonLocation loc, ReadableObjectId roid)
/*    */   {
/* 45 */     super(msg, loc);
/* 46 */     this._roid = roid;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public UnresolvedForwardReference(String msg)
/*    */   {
/* 54 */     super(msg);
/* 55 */     this._unresolvedIds = new ArrayList();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ReadableObjectId getRoid()
/*    */   {
/* 65 */     return this._roid;
/*    */   }
/*    */   
/*    */   public Object getUnresolvedId() {
/* 69 */     return this._roid.getKey().key;
/*    */   }
/*    */   
/*    */   public void addUnresolvedId(Object id, Class<?> type, JsonLocation where) {
/* 73 */     this._unresolvedIds.add(new UnresolvedId(id, type, where));
/*    */   }
/*    */   
/*    */   public List<UnresolvedId> getUnresolvedIds() {
/* 77 */     return this._unresolvedIds;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 83 */     String msg = super.getMessage();
/* 84 */     if (this._unresolvedIds == null) {
/* 85 */       return msg;
/*    */     }
/*    */     
/* 88 */     StringBuilder sb = new StringBuilder(msg);
/* 89 */     Iterator<UnresolvedId> iterator = this._unresolvedIds.iterator();
/* 90 */     while (iterator.hasNext()) {
/* 91 */       UnresolvedId unresolvedId = (UnresolvedId)iterator.next();
/* 92 */       sb.append(unresolvedId.toString());
/* 93 */       if (iterator.hasNext()) {
/* 94 */         sb.append(", ");
/*    */       }
/*    */     }
/* 97 */     sb.append('.');
/* 98 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\UnresolvedForwardReference.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */