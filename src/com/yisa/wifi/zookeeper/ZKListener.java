/*     */ package com.yisa.wifi.zookeeper;
/*     */ 
/*     */ import com.yisa.wifi.common.RemoteShell;
/*     */ import com.yisa.wifi.common.SQLUtil;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Connection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
/*     */ import org.apache.curator.framework.CuratorFramework;
/*     */ import org.apache.curator.framework.CuratorFrameworkFactory;
/*     */ import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
/*     */ import org.apache.curator.framework.listen.ListenerContainer;
/*     */ import org.apache.curator.framework.recipes.cache.ChildData;
/*     */ import org.apache.curator.framework.recipes.cache.NodeCache;
/*     */ import org.apache.curator.framework.recipes.cache.NodeCacheListener;
/*     */ import org.apache.curator.retry.ExponentialBackoffRetry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ZKListener
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  30 */     HashMap<String, String> conf = new HashMap();
/*  31 */     String zkAddr = "";
/*  32 */     String dbAddr = "";
/*  33 */     String dbUser = "";
/*  34 */     String dbPassword = "";
/*  35 */     for (int i = 0; i < args.length; i++) {
/*  36 */       String[] temp1 = args[i].split("=");
/*  37 */       conf.put(temp1[0], temp1[1]);
/*     */     }
/*     */     
/*  40 */     Set<String> confKeySet = conf.keySet();
/*  41 */     if (confKeySet.size() != 2)
/*     */     {
/*  43 */       System.out.println("Wrong number of parameters: Two parameters are needed");
/*  44 */       System.exit(1);
/*  45 */     } else if (!confKeySet.contains("listenTo")) {
/*  46 */       System.out.println("Need parameter: listenTo");
/*  47 */       System.exit(1);
/*  48 */     } else if (!confKeySet.contains("getConfFrom")) {
/*  49 */       System.out.println("Need parameter: getConfFrom");
/*  50 */       System.exit(1);
/*     */     } else {
/*  52 */       zkAddr = (String)conf.get("listenTo");
/*  53 */       String[] temp2 = ((String)conf.get("getConfFrom")).split(",");
/*  54 */       dbAddr = temp2[0];
/*  55 */       dbUser = temp2[1];
/*  56 */       dbPassword = temp2[2];
/*     */     }
/*  58 */     System.out.println("listenTo=" + zkAddr + " getConfFrom=" + dbAddr + 
/*  59 */       ",user:" + dbUser + ",password:" + dbPassword);
/*     */     
/*  61 */     CuratorFramework client = 
/*  62 */       CuratorFrameworkFactory.builder()
/*     */       
/*  64 */       .connectString(zkAddr).sessionTimeoutMs(500000)
/*  65 */       .connectionTimeoutMs(300000)
/*  66 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
/*  67 */     client.start();
/*  68 */     System.out.println("Connected to the ZK!");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */     NodeCache nodeCache = new NodeCache(client, "/updateconfig", 
/*  78 */       false);
/*  79 */     final String sub_dbAddr = dbAddr;
/*  80 */     final String sub_dbUser = dbUser;
/*  81 */     final String sub_dbPassword = dbPassword;
/*  82 */     final SQLUtil sqlUtil = new SQLUtil();
/*  83 */     nodeCache.start(true);
/*  84 */     System.out.println("listening.....");
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
/*     */     try
/*     */     {
/*  97 */       nodeCache.getListenable().addListener(new NodeCacheListener()
/*     */       {
/*     */         public void nodeChanged() throws Exception {
/* 100 */           System.out.println("Node data has been changed, new data: " + 
/* 101 */             new String(ZKListener.this.getCurrentData().getData()));
/*     */           
/* 103 */           Connection dbconn = sqlUtil.connectDB(sub_dbAddr, 
/* 104 */             sub_dbUser, sub_dbPassword);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 109 */           HashMap<String, String> nodeid_nodeip = null;
/* 110 */           ArrayList<HashMap<String, String>> rs_list = null;
/* 111 */           String query = null;
/*     */           
/* 113 */           nodeid_nodeip = new HashMap();
/* 114 */           query = "SELECT NODE_ID as NODE_ID , hostport as HOSTPORT FROM zk_node";
/* 115 */           rs_list = sqlUtil.executeSql(query, dbconn);
/* 116 */           for (HashMap<String, String> rs : rs_list) {
/* 117 */             System.out.println((String)rs.get("NODE_ID") + ":" + 
/* 118 */               (String)rs.get("HOSTPORT"));
/* 119 */             nodeid_nodeip.put((String)rs.get("NODE_ID"), (String)rs.get("HOSTPORT"));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */           String[] newZKvalueList = new String(ZKListener.this
/* 127 */             .getCurrentData().getData()).split(",");
/* 128 */           String node_id = null;String module = null;
/* 129 */           boolean needRestart = false;
/*     */           
/* 131 */           for (int i = 0; i < newZKvalueList.length; i++) {
/* 132 */             rs_list = new ArrayList();
/* 133 */             String theNewValue = newZKvalueList[i];
/* 134 */             query = "SELECT t1.node_id as NODE_ID ,t2.module as MODULE,t2.key_name as KEY_NAME,t3.value_name as VALUE_NAME,t2.is_restart as IS_RESTART FROM sys_properties_nodeid t1 INNER JOIN sys_properties_value t3 ON t1.templet_id = t3.value_templet_id INNER JOIN sys_properties_key t2 ON t2.key_id = t3.value_key_id WHERE ID = " + 
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 140 */               theNewValue;
/* 141 */             System.out.println(query);
/*     */             
/*     */ 
/* 144 */             rs_list = sqlUtil.executeSql(query, dbconn);
/* 145 */             HashMap<String, String> rs = null;
/* 146 */             System.out.println(rs_list.size());
/* 147 */             if (rs_list.size() != 1)
/*     */             {
/* 149 */               System.out.println("Error:Database query results are not unique! Please check the query:\n" + 
/* 150 */                 query);
/*     */             } else {
/* 152 */               rs = (HashMap)rs_list.get(0);
/*     */             }
/*     */             
/*     */ 
/* 156 */             node_id = (String)rs.get("NODE_ID");
/* 157 */             module = (String)rs.get("MODULE");
/* 158 */             String key_name = (String)rs.get("KEY_NAME");
/* 159 */             String value_name = (String)rs.get("VALUE_NAME");
/* 160 */             String is_restart = (String)rs.get("IS_RESTART");
/*     */             
/* 162 */             if ((!needRestart) && (is_restart.equals("1"))) {
/* 163 */               needRestart = true;
/*     */             }
/*     */             
/* 166 */             System.out.println(node_id + "/" + module + key_name + 
/* 167 */               value_name);
/*     */             try
/*     */             {
/* 170 */               ZookeeperUtil zkut = new ZookeeperUtil();
/* 171 */               zkut.createOrupdateData((String)nodeid_nodeip.get(node_id), 
/* 172 */                 module, key_name, value_name);
/*     */             }
/*     */             catch (Exception e) {
/* 175 */               needRestart = false;
/* 176 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 184 */           if (needRestart) {
/* 185 */             HashMap<String, String> remoteshell_rs = null;
/* 186 */             ArrayList<HashMap<String, String>> remoteshell_list = null;
/*     */             
/* 188 */             String remoteshell_query = "SELECT hostport as HOSTPORT , path as PATH, username as USERNAME, password as PASSWORD FROM zk_remoteshell WHERE module = '" + 
/*     */             
/* 190 */               module + 
/* 191 */               "' AND node_id = '" + node_id + "'";
/* 192 */             System.out.println(remoteshell_query);
/* 193 */             remoteshell_list = sqlUtil.executeSql(
/* 194 */               remoteshell_query, dbconn);
/*     */             
/* 196 */             if (remoteshell_list.size() != 1)
/*     */             {
/* 198 */               System.out.println("Error:Database query results are not unique! Please check the remoteshell_query:\n" + 
/* 199 */                 remoteshell_query);
/*     */             } else {
/* 201 */               remoteshell_rs = (HashMap)remoteshell_list.get(0);
/*     */             }
/* 203 */             RemoteShell shell = new RemoteShell(
/* 204 */               (String)remoteshell_rs.get("HOSTPORT"), 
/* 205 */               (String)remoteshell_rs.get("USERNAME"), 
/* 206 */               (String)remoteshell_rs.get("PASSWORD"));
/* 207 */             String shellOutPut = shell.exec("python " + 
/* 208 */               (String)remoteshell_rs.get("PATH"));
/* 209 */             System.out.println("####Restarting server: ####\n" + 
/* 210 */               shellOutPut);
/*     */           }
/*     */           
/*     */ 
/* 214 */           System.out.println("Config changing succeed!!");
/*     */         }
/*     */         
/*     */       });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 221 */       e.printStackTrace();
/*     */     }
/*     */     
/* 224 */     Thread.sleep(Long.MAX_VALUE);
/*     */     
/* 226 */     nodeCache.close();
/* 227 */     client.close();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\wifi\zookeeper\ZKListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */