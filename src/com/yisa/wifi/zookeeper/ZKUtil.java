/*     */ package com.yisa.wifi.zookeeper;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import org.apache.zookeeper.CreateMode;
/*     */ import org.apache.zookeeper.KeeperException;
/*     */ import org.apache.zookeeper.WatchedEvent;
/*     */ import org.apache.zookeeper.Watcher;
/*     */ import org.apache.zookeeper.Watcher.Event.KeeperState;
/*     */ import org.apache.zookeeper.ZooDefs.Ids;
/*     */ import org.apache.zookeeper.ZooKeeper;
/*     */ import org.kohsuke.args4j.CmdLineException;
/*     */ import org.kohsuke.args4j.CmdLineParser;
/*     */ import org.kohsuke.args4j.Option;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ZKUtil
/*     */   implements Watcher
/*     */ {
/*     */   private static final int SESSION_TIMEOUT = 10000;
/*  25 */   private ZooKeeper zk = null;
/*  26 */   private CountDownLatch countDownLatch = new CountDownLatch(1);
/*     */   
/*     */   @Option(name="--create", usage="")
/*     */   private boolean create;
/*     */   
/*     */   @Option(name="--set", usage="")
/*     */   private boolean set;
/*     */   
/*     */   @Option(name="--del", usage="")
/*     */   private boolean del;
/*     */   
/*     */   @Option(name="--get", usage="")
/*     */   private boolean get;
/*     */   
/*     */   @Option(name="--list", usage="")
/*     */   private boolean list;
/*     */   
/*     */   @Option(name="-value", usage="")
/*  44 */   private String value = "default";
/*     */   
/*     */   @Option(name="-path", usage="")
/*  47 */   private String path = "/default";
/*     */   
/*     */ 
/*     */   @Option(name="-zk", usage="")
/*  51 */   private String zkconn = "gpu3:2181";
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  57 */     ZKUtil sample = new ZKUtil();
/*     */     
/*     */ 
/*  60 */     sample.doAction(args);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void doAction(String[] args)
/*     */   {
/*  73 */     CmdLineParser parser = new CmdLineParser(this);
/*     */     try {
/*  75 */       parser.parseArgument(args);
/*     */     } catch (CmdLineException e) {
/*  77 */       e.printStackTrace();
/*     */     }
/*     */     
/*     */ 
/*  81 */     createConnection(this.zkconn);
/*     */     
/*  83 */     if (this.create) {
/*  84 */       createPath(this.path, this.value);
/*     */     }
/*     */     
/*  87 */     if (this.set) {
/*  88 */       writeData(this.path, this.value);
/*     */     }
/*     */     
/*     */ 
/*  92 */     if (this.get) {
/*  93 */       readData(this.path);
/*     */     }
/*     */     
/*  96 */     if (this.del) {
/*  97 */       deleteNode(this.path);
/*     */     }
/*     */     
/* 100 */     if (this.list) {
/* 101 */       listData(this.path);
/*     */     }
/*     */     
/* 104 */     releaseConnection();
/*     */   }
/*     */   
/*     */   private void listData(String path) {
/*     */     try {
/* 109 */       if (this.zk.exists(path, null) != null) {
/* 110 */         readData(path);
/* 111 */         List<String> childs = this.zk.getChildren(path, null);
/* 112 */         if ((childs != null) && (childs.size() > 0)) {
/* 113 */           for (String path_c : childs) {
/* 114 */             listData(path + "/" + path_c);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (KeeperException e) {
/* 120 */       e.printStackTrace();
/*     */     }
/*     */     catch (InterruptedException e) {
/* 123 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void createConnection(String connectString)
/*     */   {
/*     */     try
/*     */     {
/* 133 */       this.zk = new ZooKeeper(connectString, 10000, this);
/* 134 */       this.countDownLatch.await();
/*     */     } catch (Exception e) {
/* 136 */       System.out.println("连接创建失败，发生 IOException");
/* 137 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void releaseConnection()
/*     */   {
/* 146 */     if (this.zk != null) {
/*     */       try {
/* 148 */         this.zk.close();
/*     */       }
/*     */       catch (InterruptedException e) {
/* 151 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean createPath(String path, String data)
/*     */   {
/*     */     try
/*     */     {
/* 164 */       this.zk.create(path, data
/* 165 */         .getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
/*     */       
/*     */ 
/* 168 */       System.out.println("节点创建成功, Path: " + path + " value: " + data);
/*     */     } catch (KeeperException e) {
/* 170 */       System.out.println("节点创建失败，发生KeeperException");
/* 171 */       e.printStackTrace();
/*     */     } catch (InterruptedException e) {
/* 173 */       System.out.println("节点创建失败，发生 InterruptedException");
/* 174 */       e.printStackTrace();
/*     */     }
/* 176 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean writeData(String path, String data)
/*     */   {
/*     */     try
/*     */     {
/* 187 */       System.out.println("更新数据成功，path：" + path + ", stat: " + this.zk.setData(path, data.getBytes(), -1));
/* 188 */       return true;
/*     */     } catch (KeeperException e) {
/* 190 */       System.out.println("更新数据失败，发生KeeperException，path: " + path);
/* 191 */       e.printStackTrace();
/*     */     } catch (InterruptedException e) {
/* 193 */       System.out.println("更新数据失败，发生 InterruptedException，path: " + path);
/* 194 */       e.printStackTrace();
/*     */     }
/* 196 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void deleteNode(String path)
/*     */   {
/*     */     try
/*     */     {
/* 205 */       this.zk.delete(path, -1);
/* 206 */       System.out.println("删除节点成功，path：" + path);
/*     */     } catch (KeeperException e) {
/* 208 */       System.out.println("删除节点失败，发生KeeperException，path: " + path);
/* 209 */       e.printStackTrace();
/*     */     } catch (InterruptedException e) {
/* 211 */       System.out.println("删除节点失败，发生 InterruptedException，path: " + path);
/* 212 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String readData(String path)
/*     */   {
/*     */     try
/*     */     {
/* 223 */       byte[] bbb = this.zk.getData(path, false, null);
/* 224 */       String rs = new String(bbb, "utf8");
/* 225 */       System.out.println("path  :" + path + "::" + rs);
/* 226 */       return rs;
/*     */     } catch (KeeperException e) {
/* 228 */       System.out.println("读取数据失败，发生KeeperException，path: " + path);
/* 229 */       e.printStackTrace();
/*     */     }
/*     */     catch (InterruptedException e) {
/* 232 */       System.out.println("读取数据失败，发生 InterruptedException，path: " + path);
/* 233 */       e.printStackTrace();
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 236 */       e.printStackTrace();
/*     */     }
/*     */     
/* 239 */     return "";
/*     */   }
/*     */   
/*     */   public void process(WatchedEvent event)
/*     */   {
/* 244 */     if (event.getState() == Watcher.Event.KeeperState.SyncConnected)
/*     */     {
/* 246 */       System.out.println("watcher received event");
/* 247 */       this.countDownLatch.countDown();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\wifi\zookeeper\ZKUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */