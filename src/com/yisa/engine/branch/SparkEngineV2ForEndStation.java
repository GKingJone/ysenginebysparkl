/*     */ package com.yisa.engine.branch;
/*     */ 
/*     */ import com.yisa.engine.common.InputBean;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import org.apache.spark.sql.Dataset;
/*     */ import org.apache.spark.sql.Row;
/*     */ import org.apache.spark.sql.SparkSession;
/*     */ import scala.Predef.;
/*     */ import scala.Serializable;
/*     */ import scala.collection.immutable.StringOps;
/*     */ import scala.collection.mutable.StringBuilder;
/*     */ import scala.runtime.IntRef;
/*     */ import scala.runtime.ObjectRef;
/*     */ 
/*     */ @scala.reflect.ScalaSignature(bytes="\006\001Y3A!\001\002\001\027\tQ2\013]1sW\026sw-\0338f-J2uN]#oIN#\030\r^5p]*\0211\001B\001\007EJ\fgn\0315\013\005\0251\021AB3oO&tWM\003\002\b\021\005!\0210[:b\025\005I\021aA2p[\016\0011C\001\001\r!\ti\001#D\001\017\025\005y\021!B:dC2\f\027BA\t\017\005\031\te.\037*fM\")1\003\001C\001)\0051A(\0338jiz\"\022!\006\t\003-\001i\021A\001\005\0061\001!\t!G\001\021g\026\f'o\0315F]\022\034F/\031;j_:$bAG\017/gqr\004CA\007\034\023\tabB\001\003V]&$\b\"\002\020\030\001\004y\022!C:qCJ\\G)\031;b!\r\001\023fK\007\002C)\021!eI\001\004gFd'B\001\023&\003\025\031\b/\031:l\025\t1s%\001\004ba\006\034\007.\032\006\002Q\005\031qN]4\n\005)\n#a\002#bi\006\034X\r\036\t\003A1J!!L\021\003\007I{w\017C\0030/\001\007\001'\001\006tc2\034uN\034;fqR\004\"\001I\031\n\005I\n#\001D*qCJ\\7+Z:tS>t\007\"\002\033\030\001\004)\024\001\0027j]\026\004\"AN\035\017\00559\024B\001\035\017\003\031\001&/\0323fM&\021!h\017\002\007'R\024\030N\\4\013\005ar\001\"B\037\030\001\004)\024!\003;bE2,g*Y7f\021\025yt\0031\0016\003)Q8\016S8tiB|'\017\036\005\006\003\002!\tAQ\001\nO\026$x\n\0343ECf$\022!\016\005\006\t\002!\t!R\001\016ON|g.\021:sCf4UO\\2\025\tU2e\n\025\005\006\017\016\003\r\001S\001\nS:\004X\017\036\"fC:\004\"!\023'\016\003)S!a\023\003\002\r\r|W.\\8o\023\ti%JA\005J]B,HOQ3b]\")qj\021a\001k\005)A/\0379f\007\")Qh\021a\001k!)!\013\001C\001'\006Iq-\032;ECR,\027\016\032\013\003kQCQ!V)A\002U\n!\002^5nKN#(/\0338h\001")
/*     */ public class SparkEngineV2ForEndStation
/*     */ {
/*     */   public void searchEndStation(Dataset<Row> sparkData, final SparkSession sqlContext, String line, final String tableName, String zkHostport)
/*     */   {
/*  23 */     Connection conn = com.yisa.engine.db.MySQLConnectManager..MODULE$.getConnet(zkHostport);
/*  24 */     conn.setAutoCommit(false);
/*  25 */     String sql = "insert into zjwjz_result (s_id,j_id,t_n,l_c) values(?,?,?,?)";
/*  26 */     final ObjectRef pstmt = ObjectRef.create(conn.prepareStatement(sql));
/*     */     
/*  28 */     String[] line_arr = line.split("\\|");
/*     */     
/*  30 */     final String jobId = line_arr[1];
/*  31 */     String params = line_arr[2];
/*     */     
/*  33 */     com.google.gson.Gson gson = new com.google.gson.Gson();
/*  34 */     java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken() {}.getType();
/*  35 */     InputBean[] map = (InputBean[])gson.fromJson(params, mapType);
/*     */     
/*  37 */     InputBean inputBeanRepair = null;
/*     */     
/*  39 */     scala.runtime.Null. resultData = null;
/*     */     
/*  41 */     int count = 0;
/*     */     
/*     */ 
/*  44 */     if (map.length > 0)
/*     */     {
/*  46 */       final IntRef i = IntRef.create(1);
/*  47 */       final ObjectRef df = ObjectRef.create(null);
/*     */       
/*  49 */       Predef..MODULE$.refArrayOps((Object[])map).foreach(new scala.runtime.AbstractFunction1()
/*     */       {
/*     */         public static final long serialVersionUID = 0L;
/*     */         
/*     */ 
/*     */         public final void apply(InputBean m)
/*     */         {
/*  56 */           if (i.elem == 1) {
/*  57 */             df.elem = sqlContext.sql(SparkEngineV2ForEndStation.this.gsonArrayFunc(m, new StringBuilder().append(i.elem).append("").toString(), tableName));
/*     */             
/*     */ 
/*     */ 
/*  61 */             i.elem += 1;
/*     */           }
/*     */           else
/*     */           {
/*  65 */             Dataset dfTemp = sqlContext.sql(SparkEngineV2ForEndStation.this.gsonArrayFunc(m, new StringBuilder().append(i.elem).append("").toString(), tableName));
/*     */             
/*     */ 
/*     */ 
/*  69 */             df.elem = ((Dataset)df.elem).union(dfTemp);
/*     */             
/*  71 */             i.elem += 1;
/*     */           }
/*     */           
/*     */         }
/*  75 */       });
/*  76 */       Dataset dfIs = null;
/*  77 */       if ((Dataset)df.elem != null)
/*     */       {
/*  79 */         ((Dataset)df.elem).createOrReplaceTempView("df");
/*     */         
/*  81 */         String sqlUnion = "SELECT first(solrid) as solrid, count(1) as number,first(lastcaptured) as lastcaptured from df group by platenumber";
/*  82 */         dfIs = sqlContext.sql(sqlUnion);
/*     */         
/*  84 */         dfIs.createOrReplaceTempView("dfIs");
/*     */         
/*  86 */         final IntRef number = IntRef.create(0);
/*  87 */         Predef..MODULE$.refArrayOps((Object[])dfIs.collect()).foreach(new scala.runtime.AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */           
/*  89 */           public final void apply(Row data) { number.elem += 1;
/*     */             
/*  91 */             ((PreparedStatement)pstmt.elem).setString(1, data.apply(0).toString());
/*  92 */             ((PreparedStatement)pstmt.elem).setString(2, jobId);
/*  93 */             ((PreparedStatement)pstmt.elem).setInt(3, new StringOps(Predef..MODULE$.augmentString(data.apply(1).toString())).toInt());
/*  94 */             ((PreparedStatement)pstmt.elem).setInt(4, new StringOps(Predef..MODULE$.augmentString(data.apply(2).toString())).toInt());
/*  95 */             ((PreparedStatement)pstmt.elem).addBatch();
/*     */           }
/*     */           
/*  98 */         });
/*  99 */         String sql2 = "insert into zjwjz_progress (jobid,total) values(?,?)";
/* 100 */         PreparedStatement pstmt2 = conn.prepareStatement(sql2);
/*     */         
/* 102 */         if (number.elem == 0) {
/* 103 */           number.elem = -1;
/*     */         }
/* 105 */         pstmt2.setString(1, jobId);
/* 106 */         pstmt2.setInt(2, number.elem);
/* 107 */         pstmt2.executeUpdate();
/* 108 */         pstmt2.close();
/*     */         
/* 110 */         ((PreparedStatement)pstmt.elem).executeBatch();
/* 111 */         conn.commit();
/* 112 */         ((PreparedStatement)pstmt.elem).close();
/* 113 */         conn.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getOldDay()
/*     */   {
/* 122 */     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
/* 123 */     Calendar cal = Calendar.getInstance();
/* 124 */     cal.add(5, -3);
/* 125 */     String yesterday = dateFormat.format(cal.getTime());
/*     */     
/* 127 */     return yesterday;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String gsonArrayFunc(InputBean inputBean, String typeC, String tableName)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 230	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   3: aload_1
/*     */     //   4: invokevirtual 235	com/yisa/engine/common/InputBean:startTime	()Ljava/lang/String;
/*     */     //   7: invokevirtual 239	com/yisa/engine/uitl/TimeUtil$:getTimeStringFormat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   10: astore 4
/*     */     //   12: getstatic 230	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   15: aload_1
/*     */     //   16: invokevirtual 242	com/yisa/engine/common/InputBean:endTime	()Ljava/lang/String;
/*     */     //   19: invokevirtual 239	com/yisa/engine/uitl/TimeUtil$:getTimeStringFormat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   22: astore 5
/*     */     //   24: aload_1
/*     */     //   25: invokevirtual 246	com/yisa/engine/common/InputBean:locationId	()[Ljava/lang/String;
/*     */     //   28: astore 6
/*     */     //   30: aload_1
/*     */     //   31: invokevirtual 249	com/yisa/engine/common/InputBean:plateNumber	()Ljava/lang/String;
/*     */     //   34: astore 7
/*     */     //   36: new 197	java/text/SimpleDateFormat
/*     */     //   39: dup
/*     */     //   40: ldc -5
/*     */     //   42: invokespecial 201	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   45: astore 8
/*     */     //   47: aload 8
/*     */     //   49: aload_1
/*     */     //   50: invokevirtual 235	com/yisa/engine/common/InputBean:startTime	()Ljava/lang/String;
/*     */     //   53: invokevirtual 255	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   56: invokevirtual 260	java/util/Date:getTime	()J
/*     */     //   59: ldc2_w 261
/*     */     //   62: ldiv
/*     */     //   63: lstore 9
/*     */     //   65: aload 8
/*     */     //   67: aload_1
/*     */     //   68: invokevirtual 242	com/yisa/engine/common/InputBean:endTime	()Ljava/lang/String;
/*     */     //   71: invokevirtual 255	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   74: invokevirtual 260	java/util/Date:getTime	()J
/*     */     //   77: ldc2_w 261
/*     */     //   80: ldiv
/*     */     //   81: lstore 11
/*     */     //   83: new 264	java/lang/StringBuffer
/*     */     //   86: dup
/*     */     //   87: invokespecial 265	java/lang/StringBuffer:<init>	()V
/*     */     //   90: astore 13
/*     */     //   92: aload 13
/*     */     //   94: ldc_w 267
/*     */     //   97: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   100: pop
/*     */     //   101: aload 13
/*     */     //   103: aload_2
/*     */     //   104: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   107: ldc_w 273
/*     */     //   110: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   113: aload_3
/*     */     //   114: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   117: pop
/*     */     //   118: aload 13
/*     */     //   120: ldc_w 275
/*     */     //   123: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   126: aload_0
/*     */     //   127: aload 4
/*     */     //   129: invokevirtual 278	com/yisa/engine/branch/SparkEngineV2ForEndStation:getDateid	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   132: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   135: pop
/*     */     //   136: aload 13
/*     */     //   138: ldc_w 280
/*     */     //   141: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   144: aload_0
/*     */     //   145: aload 5
/*     */     //   147: invokevirtual 278	com/yisa/engine/branch/SparkEngineV2ForEndStation:getDateid	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   150: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   153: pop
/*     */     //   154: aload 13
/*     */     //   156: ldc_w 282
/*     */     //   159: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   162: lload 9
/*     */     //   164: invokevirtual 285	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   167: pop
/*     */     //   168: aload 13
/*     */     //   170: ldc_w 287
/*     */     //   173: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   176: lload 11
/*     */     //   178: invokevirtual 285	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   181: pop
/*     */     //   182: aload 6
/*     */     //   184: aconst_null
/*     */     //   185: if_acmpeq +97 -> 282
/*     */     //   188: aload 6
/*     */     //   190: arraylength
/*     */     //   191: iconst_0
/*     */     //   192: if_icmple +90 -> 282
/*     */     //   195: getstatic 77	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   198: getstatic 77	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   201: aload 6
/*     */     //   203: checkcast 79	[Ljava/lang/Object;
/*     */     //   206: invokevirtual 83	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   209: new 289	com/yisa/engine/branch/SparkEngineV2ForEndStation$$anonfun$1
/*     */     //   212: dup
/*     */     //   213: aload_0
/*     */     //   214: invokespecial 290	com/yisa/engine/branch/SparkEngineV2ForEndStation$$anonfun$1:<init>	(Lcom/yisa/engine/branch/SparkEngineV2ForEndStation;)V
/*     */     //   217: getstatic 295	scala/Array$:MODULE$	Lscala/Array$;
/*     */     //   220: getstatic 300	scala/reflect/ClassTag$:MODULE$	Lscala/reflect/ClassTag$;
/*     */     //   223: ldc 42
/*     */     //   225: invokevirtual 304	scala/reflect/ClassTag$:apply	(Ljava/lang/Class;)Lscala/reflect/ClassTag;
/*     */     //   228: invokevirtual 308	scala/Array$:canBuildFrom	(Lscala/reflect/ClassTag;)Lscala/collection/generic/CanBuildFrom;
/*     */     //   231: invokeinterface 311 3 0
/*     */     //   236: checkcast 79	[Ljava/lang/Object;
/*     */     //   239: invokevirtual 83	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   242: new 313	com/yisa/engine/branch/SparkEngineV2ForEndStation$$anonfun$2
/*     */     //   245: dup
/*     */     //   246: aload_0
/*     */     //   247: invokespecial 314	com/yisa/engine/branch/SparkEngineV2ForEndStation$$anonfun$2:<init>	(Lcom/yisa/engine/branch/SparkEngineV2ForEndStation;)V
/*     */     //   250: invokeinterface 318 2 0
/*     */     //   255: checkcast 42	java/lang/String
/*     */     //   258: astore 14
/*     */     //   260: aload 13
/*     */     //   262: ldc_w 320
/*     */     //   265: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   268: aload 14
/*     */     //   270: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   273: ldc_w 322
/*     */     //   276: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   279: goto +6 -> 285
/*     */     //   282: getstatic 328	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   285: pop
/*     */     //   286: aload 7
/*     */     //   288: ifnull +31 -> 319
/*     */     //   291: aload 7
/*     */     //   293: ldc_w 330
/*     */     //   296: astore 15
/*     */     //   298: dup
/*     */     //   299: ifnonnull +12 -> 311
/*     */     //   302: pop
/*     */     //   303: aload 15
/*     */     //   305: ifnull +14 -> 319
/*     */     //   308: goto +17 -> 325
/*     */     //   311: aload 15
/*     */     //   313: invokevirtual 334	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   316: ifeq +9 -> 325
/*     */     //   319: getstatic 328	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   322: goto +40 -> 362
/*     */     //   325: aload 13
/*     */     //   327: ldc_w 336
/*     */     //   330: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   333: aload 7
/*     */     //   335: ldc_w 338
/*     */     //   338: ldc_w 340
/*     */     //   341: invokevirtual 344	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*     */     //   344: ldc_w 346
/*     */     //   347: ldc_w 348
/*     */     //   350: invokevirtual 344	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*     */     //   353: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   356: ldc_w 350
/*     */     //   359: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   362: pop
/*     */     //   363: aload 13
/*     */     //   365: ldc_w 352
/*     */     //   368: invokevirtual 271	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   371: pop
/*     */     //   372: getstatic 77	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   375: aload 13
/*     */     //   377: invokevirtual 355	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   380: invokevirtual 359	scala/Predef$:println	(Ljava/lang/Object;)V
/*     */     //   383: aload 13
/*     */     //   385: invokevirtual 355	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   388: areturn
/*     */     // Line number table:
/*     */     //   Java source line #135	-> byte code offset #0
/*     */     //   Java source line #136	-> byte code offset #12
/*     */     //   Java source line #137	-> byte code offset #24
/*     */     //   Java source line #138	-> byte code offset #30
/*     */     //   Java source line #140	-> byte code offset #36
/*     */     //   Java source line #142	-> byte code offset #47
/*     */     //   Java source line #143	-> byte code offset #65
/*     */     //   Java source line #145	-> byte code offset #83
/*     */     //   Java source line #147	-> byte code offset #92
/*     */     //   Java source line #149	-> byte code offset #101
/*     */     //   Java source line #151	-> byte code offset #118
/*     */     //   Java source line #153	-> byte code offset #136
/*     */     //   Java source line #155	-> byte code offset #154
/*     */     //   Java source line #157	-> byte code offset #168
/*     */     //   Java source line #159	-> byte code offset #182
/*     */     //   Java source line #160	-> byte code offset #195
/*     */     //   Java source line #162	-> byte code offset #260
/*     */     //   Java source line #159	-> byte code offset #282
/*     */     //   Java source line #165	-> byte code offset #286
/*     */     //   Java source line #166	-> byte code offset #325
/*     */     //   Java source line #165	-> byte code offset #362
/*     */     //   Java source line #169	-> byte code offset #363
/*     */     //   Java source line #171	-> byte code offset #372
/*     */     //   Java source line #173	-> byte code offset #383
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	389	0	this	SparkEngineV2ForEndStation
/*     */     //   0	389	1	inputBean	InputBean
/*     */     //   0	389	2	typeC	String
/*     */     //   0	389	3	tableName	String
/*     */     //   12	376	4	startTime1	String
/*     */     //   24	364	5	endTime1	String
/*     */     //   30	358	6	locationId	String[]
/*     */     //   36	352	7	plateNumber	String
/*     */     //   47	341	8	format2	SimpleDateFormat
/*     */     //   65	323	9	startTime	long
/*     */     //   83	305	11	endTime	long
/*     */     //   92	296	13	sb	StringBuffer
/*     */     //   260	19	14	l	String
/*     */   }
/*     */   
/*     */   public String getDateid(String timeString)
/*     */   {
/* 179 */     String timeLong = timeString.replaceAll("[-\\s:]", "");
/*     */     
/* 181 */     return timeLong.substring(0, 8);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\branch\SparkEngineV2ForEndStation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */