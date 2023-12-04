/*     */ package com.mysql.fabric.xmlrpc;
/*     */ 
/*     */ import com.mysql.fabric.xmlrpc.base.MethodCall;
/*     */ import com.mysql.fabric.xmlrpc.base.MethodResponse;
/*     */ import com.mysql.fabric.xmlrpc.base.ResponseParser;
/*     */ import com.mysql.fabric.xmlrpc.exceptions.MySQLFabricException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.SAXException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Client
/*     */ {
/*     */   private URL url;
/*  51 */   private Map<String, String> headers = new HashMap();
/*     */   
/*     */   public Client(String url) throws MalformedURLException {
/*  54 */     this.url = new URL(url);
/*     */   }
/*     */   
/*     */   public void setHeader(String name, String value) {
/*  58 */     this.headers.put(name, value);
/*     */   }
/*     */   
/*     */   public void clearHeader(String name) {
/*  62 */     this.headers.remove(name);
/*     */   }
/*     */   
/*     */   public MethodResponse execute(MethodCall methodCall) throws IOException, ParserConfigurationException, SAXException, MySQLFabricException {
/*  66 */     HttpURLConnection connection = null;
/*     */     try {
/*  68 */       connection = (HttpURLConnection)this.url.openConnection();
/*  69 */       connection.setRequestMethod("POST");
/*  70 */       connection.setRequestProperty("User-Agent", "MySQL XML-RPC");
/*  71 */       connection.setRequestProperty("Content-Type", "text/xml");
/*  72 */       connection.setUseCaches(false);
/*  73 */       connection.setDoInput(true);
/*  74 */       connection.setDoOutput(true);
/*     */       
/*     */ 
/*  77 */       for (Entry<String, String> entry : this.headers.entrySet()) {
/*  78 */         connection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
/*     */       }
/*     */       
/*  81 */       String out = methodCall.toString();
/*     */       
/*     */ 
/*  84 */       OutputStream os = connection.getOutputStream();
/*  85 */       os.write(out.getBytes());
/*  86 */       os.flush();
/*  87 */       os.close();
/*     */       
/*     */ 
/*  90 */       InputStream is = connection.getInputStream();
/*  91 */       SAXParserFactory factory = SAXParserFactory.newInstance();
/*  92 */       SAXParser parser = factory.newSAXParser();
/*  93 */       ResponseParser saxp = new ResponseParser();
/*     */       
/*  95 */       parser.parse(is, saxp);
/*     */       
/*  97 */       is.close();
/*     */       
/*  99 */       MethodResponse resp = saxp.getMethodResponse();
/* 100 */       if (resp.getFault() != null) {
/* 101 */         throw new MySQLFabricException(resp.getFault());
/*     */       }
/*     */       
/* 104 */       return resp;
/*     */     }
/*     */     finally {
/* 107 */       if (connection != null) {
/* 108 */         connection.disconnect();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\Client.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */