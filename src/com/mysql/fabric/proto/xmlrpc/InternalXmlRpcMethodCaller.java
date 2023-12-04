/*     */ package com.mysql.fabric.proto.xmlrpc;
/*     */ 
/*     */ import com.mysql.fabric.FabricCommunicationException;
/*     */ import com.mysql.fabric.xmlrpc.Client;
/*     */ import com.mysql.fabric.xmlrpc.base.Array;
/*     */ import com.mysql.fabric.xmlrpc.base.Data;
/*     */ import com.mysql.fabric.xmlrpc.base.Member;
/*     */ import com.mysql.fabric.xmlrpc.base.MethodCall;
/*     */ import com.mysql.fabric.xmlrpc.base.MethodResponse;
/*     */ import com.mysql.fabric.xmlrpc.base.Param;
/*     */ import com.mysql.fabric.xmlrpc.base.Params;
/*     */ import com.mysql.fabric.xmlrpc.base.Struct;
/*     */ import com.mysql.fabric.xmlrpc.base.Value;
/*     */ import java.net.MalformedURLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InternalXmlRpcMethodCaller
/*     */   implements XmlRpcMethodCaller
/*     */ {
/*     */   private Client xmlRpcClient;
/*     */   
/*     */   public InternalXmlRpcMethodCaller(String url)
/*     */     throws FabricCommunicationException
/*     */   {
/*     */     try
/*     */     {
/*  53 */       this.xmlRpcClient = new Client(url);
/*     */     } catch (MalformedURLException ex) {
/*  55 */       throw new FabricCommunicationException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object unwrapValue(Value v)
/*     */   {
/*  63 */     if (v.getType() == 8)
/*  64 */       return methodResponseArrayToList((Array)v.getValue());
/*  65 */     if (v.getType() == 7) {
/*  66 */       Map<String, Object> s = new HashMap();
/*  67 */       for (Member m : ((Struct)v.getValue()).getMember()) {
/*  68 */         s.put(m.getName(), unwrapValue(m.getValue()));
/*     */       }
/*  70 */       return s;
/*     */     }
/*  72 */     return v.getValue();
/*     */   }
/*     */   
/*     */   private List<Object> methodResponseArrayToList(Array array) {
/*  76 */     List<Object> result = new ArrayList();
/*  77 */     for (Value v : array.getData().getValue()) {
/*  78 */       result.add(unwrapValue(v));
/*     */     }
/*  80 */     return result;
/*     */   }
/*     */   
/*     */   public void setHeader(String name, String value) {
/*  84 */     this.xmlRpcClient.setHeader(name, value);
/*     */   }
/*     */   
/*     */   public void clearHeader(String name) {
/*  88 */     this.xmlRpcClient.clearHeader(name);
/*     */   }
/*     */   
/*     */   public List<Object> call(String methodName, Object[] args) throws FabricCommunicationException {
/*  92 */     MethodCall methodCall = new MethodCall();
/*  93 */     Params p = new Params();
/*  94 */     if (args == null) {
/*  95 */       args = new Object[0];
/*     */     }
/*  97 */     for (int i = 0; i < args.length; i++) {
/*  98 */       if (args[i] == null)
/*  99 */         throw new NullPointerException("nil args unsupported");
/* 100 */       if (String.class.isAssignableFrom(args[i].getClass())) {
/* 101 */         p.addParam(new Param(new Value((String)args[i])));
/* 102 */       } else if (Double.class.isAssignableFrom(args[i].getClass())) {
/* 103 */         p.addParam(new Param(new Value(((Double)args[i]).doubleValue())));
/* 104 */       } else if (Integer.class.isAssignableFrom(args[i].getClass())) {
/* 105 */         p.addParam(new Param(new Value(((Integer)args[i]).intValue())));
/*     */       } else {
/* 107 */         throw new IllegalArgumentException("Unknown argument type: " + args[i].getClass());
/*     */       }
/*     */     }
/* 110 */     methodCall.setMethodName(methodName);
/* 111 */     methodCall.setParams(p);
/*     */     try {
/* 113 */       MethodResponse resp = this.xmlRpcClient.execute(methodCall);
/* 114 */       return methodResponseArrayToList((Array)((Param)resp.getParams().getParam().get(0)).getValue().getValue());
/*     */     } catch (Exception ex) {
/* 116 */       throw new FabricCommunicationException("Error during call to `" + methodName + "' (args=" + Arrays.toString(args) + ")", ex);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\proto\xmlrpc\InternalXmlRpcMethodCaller.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */