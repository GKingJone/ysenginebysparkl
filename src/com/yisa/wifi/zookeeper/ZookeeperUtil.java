/*     */ package com.yisa.wifi.zookeeper;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.curator.framework.CuratorFramework;
/*     */ import org.apache.curator.framework.CuratorFrameworkFactory;
/*     */ import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
/*     */ import org.apache.curator.framework.api.CreateBuilder;
/*     */ import org.apache.curator.framework.api.ExistsBuilder;
/*     */ import org.apache.curator.framework.api.GetChildrenBuilder;
/*     */ import org.apache.curator.framework.api.GetDataBuilder;
/*     */ import org.apache.curator.framework.api.SetDataBuilder;
/*     */ import org.apache.curator.retry.ExponentialBackoffRetry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ZookeeperUtil
/*     */ {
/*     */   public void updatePaths(String hostname, Map<String, String> keyValues)
/*     */     throws Exception
/*     */   {
/*  30 */     CuratorFramework client = CuratorFrameworkFactory.builder()
/*  31 */       .connectString(hostname)
/*  32 */       .sessionTimeoutMs(5000)
/*  33 */       .connectionTimeoutMs(3000)
/*  34 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3))
/*  35 */       .build();
/*  36 */     client.start();
/*  37 */     for (Entry<String, String> entry : keyValues.entrySet()) {
/*  38 */       client.setData().forPath((String)entry.getKey(), ((String)entry.getValue()).getBytes());
/*     */     }
/*     */     
/*     */ 
/*  42 */     client.close();
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
/*     */ 
/*     */   public boolean updateData(String hostname, String key, String value)
/*     */   {
/*  56 */     String path = key;
/*  57 */     CuratorFramework client = CuratorFrameworkFactory.builder()
/*  58 */       .connectString(hostname)
/*  59 */       .sessionTimeoutMs(5000)
/*  60 */       .connectionTimeoutMs(3000)
/*  61 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3))
/*  62 */       .build();
/*  63 */     client.start();
/*     */     try {
/*  65 */       if (client.checkExists().forPath(path) == null) {
/*  66 */         client.create().forPath(path, value.getBytes());
/*     */       }
/*  68 */       client.setData().forPath(path, value.getBytes());
/*     */     } catch (Exception e) {
/*  70 */       e.printStackTrace();
/*  71 */       return false;
/*     */     }
/*  73 */     client.close();
/*  74 */     return true;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean createOrupdateData(String hostname, String type, String key, String value)
/*     */   {
/*  90 */     String path = "/yisaconfig/" + type + "/" + key;
/*  91 */     CuratorFramework client = CuratorFrameworkFactory.builder()
/*  92 */       .connectString(hostname)
/*  93 */       .sessionTimeoutMs(5000)
/*  94 */       .connectionTimeoutMs(1000)
/*  95 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3))
/*  96 */       .build();
/*  97 */     client.start();
/*     */     try {
/*  99 */       if (client.checkExists().forPath(path) == null) {
/* 100 */         if (client.checkExists().forPath("/yisaconfig/" + type) == null) {
/* 101 */           client.create().forPath("/yisaconfig/" + type, type.getBytes());
/*     */         }
/* 103 */         client.create().forPath(path, value.getBytes());
/*     */       }
/* 105 */       client.setData().forPath(path, value.getBytes());
/*     */     } catch (Exception e) {
/* 107 */       e.printStackTrace();
/* 108 */       return false;
/*     */     }
/* 110 */     client.close();
/* 111 */     return true;
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
/*     */   public String getData(String hostname, String key)
/*     */   {
/* 124 */     CuratorFramework client = CuratorFrameworkFactory.builder()
/* 125 */       .connectString(hostname)
/* 126 */       .sessionTimeoutMs(5000)
/* 127 */       .connectionTimeoutMs(3000)
/* 128 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3))
/* 129 */       .build();
/* 130 */     client.start();
/* 131 */     String data = "";
/*     */     try {
/* 133 */       data = new String((byte[])client.getData().forPath(key), "utf8");
/*     */     } catch (Exception e) {
/* 135 */       e.printStackTrace();
/*     */     }
/* 137 */     client.close();
/* 138 */     return data;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, String> getAllConfig(String hostname, String type, boolean withCommon)
/*     */     throws UnsupportedEncodingException, Exception
/*     */   {
/* 155 */     String rootPath = "/yisaconfig/" + type;
/*     */     
/* 157 */     CuratorFramework client = CuratorFrameworkFactory.builder()
/* 158 */       .connectString(hostname)
/* 159 */       .sessionTimeoutMs(5000)
/* 160 */       .connectionTimeoutMs(3000)
/* 161 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3))
/* 162 */       .build();
/* 163 */     client.start();
/*     */     
/* 165 */     Map<String, String> configs = new HashMap();
/* 166 */     List<String> children = (List)client.getChildren().forPath(rootPath);
/* 167 */     for (String path : children) {
/* 168 */       String data = new String((byte[])client.getData().forPath(rootPath + "/" + path), "utf8");
/* 169 */       configs.put(path, data);
/*     */     }
/* 171 */     if (withCommon) {
/* 172 */       String commonPath = "/yisaconfig/common";
/* 173 */       Object childrenCommon = (List)client.getChildren().forPath(commonPath);
/* 174 */       for (String path : (List)childrenCommon) {
/* 175 */         String data = new String((byte[])client.getData().forPath(commonPath + "/" + path), "utf8");
/* 176 */         configs.put(path, data);
/*     */       }
/*     */     }
/*     */     
/* 180 */     client.close();
/* 181 */     return configs;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getConfig(String hostname, String type, String key)
/*     */     throws UnsupportedEncodingException, Exception
/*     */   {
/* 199 */     String path = "/yisaconfig/" + type + "/" + key;
/*     */     
/* 201 */     CuratorFramework client = CuratorFrameworkFactory.builder()
/* 202 */       .connectString(hostname)
/* 203 */       .sessionTimeoutMs(5000)
/* 204 */       .connectionTimeoutMs(3000)
/* 205 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3))
/* 206 */       .build();
/* 207 */     client.start();
/* 208 */     String data = "";
/* 209 */     data = new String((byte[])client.getData().forPath(path), "utf8");
/*     */     
/* 211 */     client.close();
/* 212 */     return data;
/*     */   }
/*     */   
/*     */   private Map<String, String> getCommonConfig(String hostname, String type, boolean withCommon)
/*     */   {
/* 217 */     String rootPath = "/yisaconfig/" + type;
/*     */     
/* 219 */     CuratorFramework client = CuratorFrameworkFactory.builder()
/* 220 */       .connectString(hostname)
/* 221 */       .sessionTimeoutMs(5000)
/* 222 */       .connectionTimeoutMs(3000)
/* 223 */       .retryPolicy(new ExponentialBackoffRetry(1000, 3))
/* 224 */       .build();
/* 225 */     client.start();
/*     */     
/* 227 */     Map<String, String> configs = new HashMap();
/*     */     try {
/* 229 */       List<String> children = (List)client.getChildren().forPath(rootPath);
/* 230 */       for (String path : children) {
/* 231 */         String data = new String((byte[])client.getData().forPath(rootPath + "/" + path), "utf8");
/* 232 */         configs.put(path, data);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 236 */       e.printStackTrace();
/*     */     }
/* 238 */     client.close();
/* 239 */     return configs;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\wifi\zookeeper\ZookeeperUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */