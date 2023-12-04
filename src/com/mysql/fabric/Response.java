/*    */ package com.mysql.fabric;
/*    */ 
/*    */ import com.mysql.fabric.proto.xmlrpc.ResultSetParser;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class Response
/*    */ {
/*    */   private int protocolVersion;
/*    */   private String fabricUuid;
/*    */   private int ttl;
/*    */   private String errorMessage;
/*    */   private List<Map<String, ?>> resultSet;
/*    */   
/*    */   public Response(List<?> responseData)
/*    */     throws FabricCommunicationException
/*    */   {
/* 44 */     this.protocolVersion = ((Integer)responseData.get(0)).intValue();
/* 45 */     if (this.protocolVersion != 1) {
/* 46 */       throw new FabricCommunicationException("Unknown protocol version: " + this.protocolVersion);
/*    */     }
/* 48 */     this.fabricUuid = ((String)responseData.get(1));
/* 49 */     this.ttl = ((Integer)responseData.get(2)).intValue();
/* 50 */     this.errorMessage = ((String)responseData.get(3));
/* 51 */     if ("".equals(this.errorMessage)) {
/* 52 */       this.errorMessage = null;
/*    */     }
/* 54 */     List<Map<String, ?>> resultSets = (List)responseData.get(4);
/* 55 */     if (resultSets.size() > 0) {
/* 56 */       Map<String, ?> resultData = (Map)resultSets.get(0);
/* 57 */       this.resultSet = new ResultSetParser().parse((Map)resultData.get("info"), (List)resultData.get("rows"));
/*    */     }
/*    */   }
/*    */   
/*    */   public int getProtocolVersion() {
/* 62 */     return this.protocolVersion;
/*    */   }
/*    */   
/*    */   public String getFabricUuid() {
/* 66 */     return this.fabricUuid;
/*    */   }
/*    */   
/*    */   public int getTtl() {
/* 70 */     return this.ttl;
/*    */   }
/*    */   
/*    */   public String getErrorMessage() {
/* 74 */     return this.errorMessage;
/*    */   }
/*    */   
/*    */   public List<Map<String, ?>> getResultSet() {
/* 78 */     return this.resultSet;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\Response.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */