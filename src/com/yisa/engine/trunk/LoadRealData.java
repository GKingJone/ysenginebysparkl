/*    */ package com.yisa.engine.trunk;
/*    */ 
/*    */ import org.apache.spark.sql.SparkSession;
/*    */ 
/*    */ @scala.reflect.ScalaSignature(bytes="\006\001]3A!\001\002\001\027\taAj\\1e%\026\fG\016R1uC*\0211\001B\001\006iJ,hn\033\006\003\013\031\ta!\0328hS:,'BA\004\t\003\021I\030n]1\013\003%\t1aY8n\007\001\0312\001\001\007\025!\ti!#D\001\017\025\ty\001#\001\003mC:<'\"A\t\002\t)\fg/Y\005\003'9\021aa\0242kK\016$\bCA\007\026\023\t1bB\001\005Sk:t\027M\0317f\021!A\002A!A!\002\023I\022\001D:qCJ\\7+Z:tS>t\007C\001\016$\033\005Y\"B\001\017\036\003\r\031\030\017\034\006\003=}\tQa\0359be.T!\001I\021\002\r\005\004\030m\0315f\025\005\021\023aA8sO&\021Ae\007\002\r'B\f'o[*fgNLwN\034\005\tM\001\021\t\021)A\005O\005A\001\016\0324t!\006$\b\016\005\002)]9\021\021\006L\007\002U)\t1&A\003tG\006d\027-\003\002.U\0051\001K]3eK\032L!a\f\031\003\rM#(/\0338h\025\ti#\006\003\0053\001\t\005\t\025!\003(\003!!\027\r^1QCRD\007\002\003\033\001\005\003\005\013\021B\033\002\023\r\f7\r[3ECf\034\bCA\0257\023\t9$FA\002J]RD\001\"\017\001\003\002\003\006IaJ\001\ni\006\024G.\032(b[\026D\001b\017\001\003\002\003\006IaJ\001\035MJ,\027/^3oi2L8)\031:SKN,H\016\036+bE2,g*Y7f\021!i\004A!A!\002\0239\023A\003>l\021>\034H\017U8si\")q\b\001C\001\001\0061A(\0338jiz\"\002\"Q\"E\013\032;\005*\023\t\003\005\002i\021A\001\005\0061y\002\r!\007\005\006My\002\ra\n\005\006ey\002\ra\n\005\006iy\002\r!\016\005\006sy\002\ra\n\005\006wy\002\ra\n\005\006{y\002\ra\n\005\006\027\002!\t\005T\001\004eVtG#A'\021\005%r\025BA(+\005\021)f.\033;\t\013E\003A\021\001*\002\031I,hNR5sgR$\026m]6\025\0135\033F+\026,\t\013a\001\006\031A\r\t\013e\002\006\031A\024\t\013m\002\006\031A\024\t\013u\002\006\031A\024")
/*    */ public class LoadRealData implements Runnable {
/*    */   private final SparkSession sparkSession;
/*    */   
/*  9 */   public void run() { boolean loadData = true;
/* 10 */     while (loadData)
/*    */     {
/* 12 */       org.apache.spark.sql.Dataset loadData = this.sparkSession.read().parquet(new scala.collection.mutable.StringBuilder().append(this.hdfsPath).append(this.dataPath).toString());
/* 13 */       org.apache.spark.sql.Dataset loadData_filter = loadData
/* 14 */         .filter("platenumber !=  '0' ")
/*    */         
/* 16 */         .filter("platenumber not like  '%未%' ")
/* 17 */         .filter(new scala.runtime.AbstractFunction1() { public final boolean apply(org.apache.spark.sql.Row x) { return ((String)x.getAs("platenumber")).length() > 1; }
/* 18 */         }).filter(new scala.runtime.AbstractFunction1() { public final boolean apply(org.apache.spark.sql.Row x) { return ((String)x.getAs("platenumber")).length() < 20; }
/* 19 */         }).filter(new scala.collection.mutable.StringBuilder().append("dateid >= ").append(com.yisa.engine.uitl.TimeUtil..MODULE$.getLCacheDataDateid(this.cacheDays)).toString());
/*    */       
/* 21 */       loadData_filter.createOrReplaceTempView("tmp");
/*    */       
/* 23 */       org.apache.spark.sql.Dataset loadData_filter2 = this.sparkSession.sql("select  * from tmp  order by capturetime desc");
/*    */       
/* 25 */       this.sparkSession.catalog().uncacheTable(this.tableName);
/*    */       
/* 27 */       this.sparkSession.catalog().clearCache();
/*    */       
/* 29 */       this.sparkSession.catalog().listTables();
/*    */       
/* 31 */       loadData_filter.createOrReplaceTempView(this.tableName);
/* 32 */       this.sparkSession.catalog().cacheTable(this.tableName);
/* 33 */       runFirstTask(this.sparkSession, this.tableName, this.frequentlyCarResultTableName, this.zkHostPort);
/* 34 */       Thread.sleep(10000L);
/*    */     } }
/*    */   
/*    */   public static final long serialVersionUID = 0L;
/*    */   public static final long serialVersionUID = 0L;
/* 39 */   public void runFirstTask(SparkSession sparkSession, String tableName, String frequentlyCarResultTableName, String zkHostPort) { com.yisa.engine.branch.SparkEngineV2ForFrequentlyCar fcar = new com.yisa.engine.branch.SparkEngineV2ForFrequentlyCar();
/* 40 */     fcar.FrequentlyCar(sparkSession, "01|test1|{\"count\":2}", tableName, frequentlyCarResultTableName, zkHostPort);
/*    */   }
/*    */   
/*    */   public LoadRealData(SparkSession sparkSession, String hdfsPath, String dataPath, int cacheDays, String tableName, String frequentlyCarResultTableName, String zkHostPort) {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\trunk\LoadRealData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */