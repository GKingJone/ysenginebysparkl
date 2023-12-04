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
/*     */ import org.kohsuke.args4j.Option;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ZKUtilLocal
/*     */   implements Watcher
/*     */ {
/*     */   private static final int SESSION_TIMEOUT = 10000;
/*  23 */   private ZooKeeper zk = null;
/*  24 */   private CountDownLatch countDownLatch = new CountDownLatch(1);
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
/*  42 */   private String value = "default";
/*     */   
/*     */   @Option(name="-path", usage="")
/*  45 */   private String path = "/default";
/*     */   
/*     */ 
/*     */   @Option(name="-zk", usage="")
/*  49 */   private String zkconn = "cdh1:2181";
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  55 */     ZKUtilLocal sample = new ZKUtilLocal();
/*     */     
/*     */ 
/*  58 */     sample.doAction(args);
/*  59 */     sample.releaseConnection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void doAction(String[] args)
/*     */   {
/*  66 */     createConnection(this.zkconn, 10000);
/*     */     
/*  68 */     deleteNodeRecursion("/kafka");
/*  69 */     deleteNode("/kafka");
/*     */     
/*     */ 
/*  72 */     releaseConnection();
/*     */   }
/*     */   
/*     */   private void listData(String path)
/*     */   {
/*     */     try
/*     */     {
/*  79 */       if (this.zk.exists(path, null) != null) {
/*  80 */         readData(path);
/*  81 */         List<String> childs = this.zk.getChildren(path, null);
/*  82 */         if ((childs != null) && (childs.size() > 0)) {
/*  83 */           for (String path_c : childs) {
/*  84 */             listData(path + "/" + path_c);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (KeeperException e) {
/*  90 */       e.printStackTrace();
/*     */     }
/*     */     catch (InterruptedException e) {
/*  93 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void createConnection(String connectString, int sessionTimeout)
/*     */   {
/*     */     try
/*     */     {
/* 103 */       this.zk = new ZooKeeper(connectString, sessionTimeout, this);
/* 104 */       this.countDownLatch.await();
/*     */     } catch (Exception e) {
/* 106 */       System.out.println("连接创建失败，发生 IOException");
/* 107 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void releaseConnection()
/*     */   {
/* 116 */     if (this.zk != null) {
/*     */       try {
/* 118 */         this.zk.close();
/*     */       }
/*     */       catch (InterruptedException e) {
/* 121 */         e.printStackTrace();
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
/* 134 */       this.zk.create(path, data
/* 135 */         .getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
/*     */       
/*     */ 
/* 138 */       System.out.println("节点创建成功, Path: " + path + " value: " + data);
/*     */     } catch (KeeperException e) {
/* 140 */       System.out.println("节点创建失败，发生KeeperException");
/* 141 */       e.printStackTrace();
/*     */     } catch (InterruptedException e) {
/* 143 */       System.out.println("节点创建失败，发生 InterruptedException");
/* 144 */       e.printStackTrace();
/*     */     }
/* 146 */     return true;
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
/* 157 */       System.out.println("更新数据成功，path：" + path + ", stat: " + this.zk.setData(path, data.getBytes(), -1));
/* 158 */       return true;
/*     */     } catch (KeeperException e) {
/* 160 */       System.out.println("更新数据失败，发生KeeperException，path: " + path);
/* 161 */       e.printStackTrace();
/*     */     } catch (InterruptedException e) {
/* 163 */       System.out.println("更新数据失败，发生 InterruptedException，path: " + path);
/* 164 */       e.printStackTrace();
/*     */     }
/* 166 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void deleteNode(String path)
/*     */   {
/*     */     try
/*     */     {
/* 175 */       this.zk.delete(path, -1);
/* 176 */       System.out.println("删除节点成功，path：" + path);
/*     */     } catch (KeeperException e) {
/* 178 */       System.out.println("删除节点失败，发生KeeperException，path: " + path);
/* 179 */       e.printStackTrace();
/*     */     } catch (InterruptedException e) {
/* 181 */       System.out.println("删除节点失败，发生 InterruptedException，path: " + path);
/* 182 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deleteNodeRecursion(String path)
/*     */   {
/*     */     try
/*     */     {
/* 195 */       List<String> paths = this.zk.getChildren(path, false);
/* 196 */       for (String p : paths) {
/* 197 */         deleteNodeRecursion(path + "/" + p);
/* 198 */         System.out.println(path + "/" + p);
/*     */       }
/* 200 */       for (String p : paths) {
/* 201 */         this.zk.delete(path + "/" + p, -1);
/*     */       }
/*     */     }
/*     */     catch (KeeperException e) {
/* 205 */       System.out.println("删除节点失败，发生KeeperException，path: " + path);
/* 206 */       e.printStackTrace();
/*     */     } catch (InterruptedException e) {
/* 208 */       System.out.println("删除节点失败，发生 InterruptedException，path: " + path);
/* 209 */       e.printStackTrace();
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
/* 220 */       byte[] bbb = this.zk.getData(path, false, null);
/* 221 */       String rs = new String(bbb, "utf8");
/* 222 */       System.out.println("path  :" + path + "::" + rs);
/* 223 */       return rs;
/*     */     } catch (KeeperException e) {
/* 225 */       System.out.println("读取数据失败，发生KeeperException，path: " + path);
/* 226 */       e.printStackTrace();
/*     */     }
/*     */     catch (InterruptedException e) {
/* 229 */       System.out.println("读取数据失败，发生 InterruptedException，path: " + path);
/* 230 */       e.printStackTrace();
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 233 */       e.printStackTrace();
/*     */     }
/*     */     
/* 236 */     return "";
/*     */   }
/*     */   
/*     */   public void process(WatchedEvent event)
/*     */   {
/* 241 */     if (event.getState() == Watcher.Event.KeeperState.SyncConnected)
/*     */     {
/* 243 */       System.out.println("watcher received event");
/* 244 */       this.countDownLatch.countDown();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\wifi\zookeeper\ZKUtilLocal.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */