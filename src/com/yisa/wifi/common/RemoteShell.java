/*    */ package com.yisa.wifi.common;
/*    */ 
/*    */ import ch.ethz.ssh2.Connection;
/*    */ import ch.ethz.ssh2.Session;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RemoteShell
/*    */ {
/*    */   private Connection conn;
/*    */   private String ipAddr;
/*    */   private String userName;
/*    */   private String password;
/* 22 */   private String charset = Charset.defaultCharset().toString();
/*    */   private static final int TIME_OUT = 300000;
/*    */   
/*    */   public RemoteShell(String ipAddr, String userName, String password)
/*    */   {
/* 27 */     this.ipAddr = ipAddr;
/* 28 */     this.userName = userName;
/* 29 */     this.password = password;
/*    */   }
/*    */   
/*    */   public boolean login()
/*    */     throws IOException
/*    */   {
/* 35 */     this.conn = new Connection(this.ipAddr);
/* 36 */     this.conn.connect();
/* 37 */     return this.conn.authenticateWithPassword(this.userName, this.password);
/*    */   }
/*    */   
/*    */   public String exec(String cmds)
/*    */   {
/* 42 */     InputStream in = null;
/* 43 */     String result = "";
/*    */     try {
/* 45 */       if (login()) {
/* 46 */         Session session = this.conn.openSession();
/* 47 */         session.execCommand("source /etc/profile;" + cmds);
/*    */         
/* 49 */         session.waitForCondition(32, 300000L);
/* 50 */         in = session.getStdout();
/* 51 */         result = processStdout(in, this.charset);
/* 52 */         session.close();
/* 53 */         this.conn.close();
/*    */       }
/*    */     } catch (IOException e1) {
/* 56 */       e1.printStackTrace();
/*    */     }
/* 58 */     return result;
/*    */   }
/*    */   
/*    */ 
/*    */   public String processStdout(InputStream in, String charset)
/*    */   {
/* 64 */     byte[] buf = new byte['Ѐ'];
/* 65 */     StringBuffer sb = new StringBuffer();
/*    */     try {
/* 67 */       while (in.read(buf) != -1) {
/* 68 */         sb.append(new String(buf, charset));
/*    */       }
/*    */     } catch (IOException e) {
/* 71 */       e.printStackTrace();
/*    */     }
/* 73 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\wifi\common\RemoteShell.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */