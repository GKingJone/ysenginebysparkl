/*    */ package com.mysql.fabric.proto.xmlrpc;
/*    */ 
/*    */ import com.mysql.fabric.FabricCommunicationException;
/*    */ import java.io.IOException;
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
/*    */ public class AuthenticatedXmlRpcMethodCaller
/*    */   implements XmlRpcMethodCaller
/*    */ {
/*    */   private XmlRpcMethodCaller underlyingCaller;
/*    */   private String url;
/*    */   private String username;
/*    */   private String password;
/*    */   
/*    */   public AuthenticatedXmlRpcMethodCaller(XmlRpcMethodCaller underlyingCaller, String url, String username, String password)
/*    */   {
/* 43 */     this.underlyingCaller = underlyingCaller;
/* 44 */     this.url = url;
/* 45 */     this.username = username;
/* 46 */     this.password = password;
/*    */   }
/*    */   
/*    */   public void setHeader(String name, String value) {
/* 50 */     this.underlyingCaller.setHeader(name, value);
/*    */   }
/*    */   
/*    */   public void clearHeader(String name) {
/* 54 */     this.underlyingCaller.clearHeader(name);
/*    */   }
/*    */   
/*    */   public List<?> call(String methodName, Object[] args) throws FabricCommunicationException
/*    */   {
/*    */     String authenticateHeader;
/*    */     try {
/* 61 */       authenticateHeader = DigestAuthentication.getChallengeHeader(this.url);
/*    */     } catch (IOException ex) {
/* 63 */       throw new FabricCommunicationException("Unable to obtain challenge header for authentication", ex);
/*    */     }
/*    */     
/* 66 */     Map<String, String> digestChallenge = DigestAuthentication.parseDigestChallenge(authenticateHeader);
/*    */     
/* 68 */     String authorizationHeader = DigestAuthentication.generateAuthorizationHeader(digestChallenge, this.username, this.password);
/*    */     
/* 70 */     this.underlyingCaller.setHeader("Authorization", authorizationHeader);
/*    */     
/* 72 */     return this.underlyingCaller.call(methodName, args);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\proto\xmlrpc\AuthenticatedXmlRpcMethodCaller.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */