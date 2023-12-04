/*     */ package com.yisa.engine.modules;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.yisa.engine.branch.SparkEngineV2ForFrequentlyCar;
/*     */ import com.yisa.engine.branch.SparkEngineV2ForSimilarPlate;
/*     */ import com.yisa.engine.branch.SparkEngineV2ForTravelTogether;
/*     */ import com.yisa.engine.common.InputBean;
/*     */ import com.yisa.engine.uitl.TimeUtil.;
/*     */ import java.lang.reflect.Type;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import org.apache.spark.sql.Dataset;
/*     */ import org.apache.spark.sql.Row;
/*     */ import org.apache.spark.sql.SparkSession;
/*     */ import scala.Predef.;
/*     */ import scala.Serializable;
/*     */ import scala.collection.immutable.StringOps;
/*     */ import scala.runtime.AbstractFunction1;
/*     */ import scala.runtime.BooleanRef;
/*     */ import scala.runtime.BoxedUnit;
/*     */ 
/*     */ public final class SparkEngineModulesForVer21$
/*     */ {
/*     */   public static final  MODULE$;
/*     */   
/*     */   static
/*     */   {
/*     */     new ();
/*     */   }
/*     */   
/*     */   public void FrequentlyCar(String line, SparkSession sparkSession, String tableName, ExecutorService threadPool, String prestoTableName, String frequentlyCarResultTableName, String zkHostPort, int cacheDays, String prestoHostPort)
/*     */   {
/*  35 */     String[] line_arr = line.split("\\|");
/*     */     
/*  37 */     Gson gson = new Gson();
/*  38 */     Type mapType = new TypeToken() {}.getType();
/*  39 */     InputBean map = (InputBean)gson.fromJson(line_arr[2], mapType);
/*     */     
/*  41 */     String startTime = map.startTime();
/*  42 */     int startTimeDateid = TimeUtil..MODULE$.getDateId(map.startTime());
/*     */     
/*  44 */     SparkEngineV2ForFrequentlyCar fcar = new SparkEngineV2ForFrequentlyCar();
/*     */     
/*  46 */     if (startTimeDateid < new StringOps(Predef..MODULE$.augmentString(getLCacheDataDateid(cacheDays))).toInt()) {
/*  47 */       Predef..MODULE$.println("all time data!!");
/*  48 */       threadPool.execute(new com.yisa.engine.branchV3.SparkEngineV3ForFrequentlyCarPresto(line, prestoTableName, frequentlyCarResultTableName, zkHostPort, prestoHostPort));
/*     */     } else {
/*  50 */       Predef..MODULE$.println("cache time data!!");
/*  51 */       fcar.FrequentlyCar(sparkSession, line, tableName, frequentlyCarResultTableName, zkHostPort);
/*     */     }
/*     */   }
/*     */   
/*     */   public void SearchCarByPic(String line, SparkSession sparkSession, String tableName, String zkHostPort, int cacheDays, String tableNameAll, ExecutorService threadPool, String prestoHostPort, String prestoTableName) {
/*  56 */     String[] line_arr = line.split("\\|");
/*     */     
/*  58 */     Gson gson = new Gson();
/*  59 */     Type mapType = new TypeToken() {}.getType();
/*  60 */     InputBean map = (InputBean)gson.fromJson(line_arr[2], mapType);
/*     */     
/*  62 */     String startTime = map.startTime();
/*  63 */     int startTimeDateid = TimeUtil..MODULE$.getDateId(map.startTime());
/*     */     
/*  65 */     com.yisa.engine.branch.SparkEngineV2ForSearchCarByPic fcar = new com.yisa.engine.branch.SparkEngineV2ForSearchCarByPic();
/*  66 */     if (startTimeDateid < new StringOps(Predef..MODULE$.augmentString(getLCacheDataDateid(cacheDays))).toInt()) {
/*  67 */       Predef..MODULE$.println("all time data!!");
/*     */       
/*  69 */       threadPool.execute(new com.yisa.engine.branchV3.SparkEngineV3ForSearchCarByPicPresto(sparkSession, line, prestoTableName, zkHostPort, prestoHostPort));
/*     */     }
/*     */     else {
/*  72 */       Predef..MODULE$.println("cache time data!!");
/*     */       
/*     */ 
/*  75 */       fcar.SearchCarByPic(sparkSession, line, tableName, zkHostPort);
/*     */     }
/*     */   }
/*     */   
/*     */   public void SearchSimilarPlate(Dataset<Row> sparkData, String line, SparkSession sparkSession, ExecutorService threadPool, String tableName, String prestoTableName, String zkHostPort, int cacheDays, String tableNameAll, String prestoHostPort)
/*     */   {
/*  81 */     String[] line_arr = line.split("\\|");
/*  82 */     Gson gson = new Gson();
/*  83 */     Type mapType = new TypeToken() {}.getType();
/*  84 */     InputBean map = (InputBean)gson.fromJson(line_arr[2], mapType);
/*     */     
/*  86 */     String startTime = map.startTime();
/*  87 */     int startTimeDateid = TimeUtil..MODULE$.getDateId(map.startTime());
/*     */     
/*  89 */     if (startTimeDateid < new StringOps(Predef..MODULE$.augmentString(getLCacheDataDateid(cacheDays))).toInt()) {
/*  90 */       Predef..MODULE$.println("all time data!!");
/*  91 */       threadPool.execute(new com.yisa.engine.branch.SparkEngineV2ForSimilarPlatePresto(line, prestoTableName, zkHostPort, prestoHostPort));
/*     */     } else {
/*  93 */       Predef..MODULE$.println("cache time data!!");
/*  94 */       SparkEngineV2ForSimilarPlate scar = new SparkEngineV2ForSimilarPlate();
/*  95 */       scar.searchSimilarPlateNumber(sparkData, sparkSession, line, tableName, zkHostPort);
/*     */     }
/*     */   }
/*     */   
/*     */   public void MultiPoint(Dataset<Row> sparkData, String line, SparkSession sparkSession, ExecutorService threadPool, String tableName, String prestoTableName, String zkHostPort, int cacheDays, String prestoHostPort) {
/* 100 */     String[] line_arr = line.split("\\|");
/* 101 */     Gson gson = new Gson();
/* 102 */     Type mapType = new TypeToken() {}.getType();
/* 103 */     InputBean[] maps = (InputBean[])gson.fromJson(line_arr[2], mapType);
/*     */     
/* 105 */     final BooleanRef allTime = BooleanRef.create(false);
/* 106 */     Predef..MODULE$.refArrayOps((Object[])maps).foreach(new AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */       private final int cacheDays$1;
/*     */       
/* 109 */       public final void apply(InputBean map) { String startTime = map.startTime();
/* 110 */         int startTimeDateid = TimeUtil..MODULE$.getDateId(map.startTime());
/* 111 */         if (startTimeDateid < new StringOps(Predef..MODULE$.augmentString(SparkEngineModulesForVer21..MODULE$.getLCacheDataDateid(this.cacheDays$1))).toInt()) {
/* 112 */           allTime.elem = true;
/*     */         }
/*     */       }
/*     */     });
/* 116 */     if (allTime.elem) {
/* 117 */       Predef..MODULE$.println("all time data!!");
/* 118 */       threadPool.execute(new com.yisa.engine.branch.SparkEngineV2ForMultiPointPresto(line, prestoTableName, zkHostPort, prestoHostPort));
/*     */     } else {
/* 120 */       Predef..MODULE$.println("cache time data!!");
/* 121 */       com.yisa.engine.branch.SparkEngineV2ForMultiPoint mp = new com.yisa.engine.branch.SparkEngineV2ForMultiPoint();
/* 122 */       mp.searchMultiPoint(sparkData, sparkSession, line, tableName, zkHostPort);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void CaseCar(Dataset<Row> sparkData, String line, SparkSession sparkSession, ExecutorService threadPool, String tableName, String prestoTableName, String zkHostPort, int cacheDays, String prestoHostPort)
/*     */   {
/* 129 */     String[] line_arr = line.split("\\|");
/*     */     
/* 131 */     Gson gson = new Gson();
/* 132 */     Type mapType = new TypeToken() {}.getType();
/* 133 */     InputBean[] maps = (InputBean[])gson.fromJson(line_arr[2], mapType);
/*     */     
/* 135 */     final BooleanRef allTime = BooleanRef.create(false);
/* 136 */     Predef..MODULE$.refArrayOps((Object[])maps).foreach(new AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */       private final int cacheDays$2;
/*     */       
/* 139 */       public final void apply(InputBean map) { String startTime = map.startTime();
/* 140 */         int startTimeDateid = TimeUtil..MODULE$.getDateId(map.startTime());
/* 141 */         if (startTimeDateid < new StringOps(Predef..MODULE$.augmentString(SparkEngineModulesForVer21..MODULE$.getLCacheDataDateid(this.cacheDays$2))).toInt()) {
/* 142 */           allTime.elem = true;
/*     */         }
/*     */       }
/*     */     });
/* 146 */     if (allTime.elem) {
/* 147 */       Predef..MODULE$.println("all time data!!");
/* 148 */       threadPool.execute(new com.yisa.engine.branch.SparkEngineV2ForCaseCarPresto(line, prestoTableName, zkHostPort, prestoHostPort));
/*     */     } else {
/* 150 */       Predef..MODULE$.println("cache time data!!");
/* 151 */       com.yisa.engine.branch.SparkEngineV2ForCaseCar mp = new com.yisa.engine.branch.SparkEngineV2ForCaseCar();
/* 152 */       mp.searchCaseCar(sparkData, sparkSession, line, tableName, zkHostPort);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void EndStation(Dataset<Row> sparkData, String line, SparkSession sparkSession, ExecutorService threadPool, String tableName, String prestoTableName, String zkHostPort, int cacheDays, String prestoHostPort)
/*     */   {
/* 159 */     String[] line_arr = line.split("\\|");
/* 160 */     Gson gson = new Gson();
/* 161 */     Type mapType = new TypeToken() {}.getType();
/* 162 */     InputBean[] maps = (InputBean[])gson.fromJson(line_arr[2], mapType);
/*     */     
/* 164 */     final BooleanRef allTime = BooleanRef.create(false);
/* 165 */     Predef..MODULE$.refArrayOps((Object[])maps).foreach(new AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */       private final int cacheDays$3;
/*     */       
/* 168 */       public final void apply(InputBean map) { String startTime = map.startTime();
/* 169 */         int startTimeDateid = TimeUtil..MODULE$.getDateId(map.startTime());
/* 170 */         if (startTimeDateid < new StringOps(Predef..MODULE$.augmentString(SparkEngineModulesForVer21..MODULE$.getLCacheDataDateid(this.cacheDays$3))).toInt()) {
/* 171 */           allTime.elem = true;
/*     */         }
/*     */       }
/*     */     });
/* 175 */     if (allTime.elem) {
/* 176 */       Predef..MODULE$.println("all time data!!");
/* 177 */       threadPool.execute(new com.yisa.engine.branch.SparkEngineV2ForEndStationPresto(line, prestoTableName, zkHostPort, prestoHostPort));
/*     */     } else {
/* 179 */       Predef..MODULE$.println("cache time data!!");
/* 180 */       com.yisa.engine.branch.SparkEngineV2ForEndStation mp = new com.yisa.engine.branch.SparkEngineV2ForEndStation();
/* 181 */       mp.searchEndStation(sparkData, sparkSession, line, tableName, zkHostPort);
/*     */     }
/*     */   }
/*     */   
/*     */   public void TogetherCar(SparkSession sparkSession, String line, String tableName, String zkHostPort)
/*     */   {
/* 187 */     SparkEngineV2ForTravelTogether tc = new SparkEngineV2ForTravelTogether();
/* 188 */     tc.TravelTogether(sparkSession, line, tableName, zkHostPort);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getLCacheDataDateid(int days)
/*     */   {
/* 194 */     Calendar cal = Calendar.getInstance();
/* 195 */     cal.setTime(new java.util.Date());
/* 196 */     cal.add(5, -days);
/* 197 */     SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
/* 198 */     return format.format(cal.getTime()); }
/*     */   
/* 200 */   private SparkEngineModulesForVer21$() { MODULE$ = this; }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\modules\SparkEngineModulesForVer21$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */