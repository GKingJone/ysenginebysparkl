/*     */ package com.yisa.engine.branch;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.yisa.engine.common.InputBean;
/*     */ import com.yisa.engine.db.MySQLConnectManager.;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Statement;
/*     */ import scala.Predef.;
/*     */ import scala.Serializable;
/*     */ import scala.collection.mutable.StringBuilder;
/*     */ import scala.runtime.AbstractFunction1;
/*     */ import scala.runtime.IntRef;
/*     */ import scala.runtime.ObjectRef;
/*     */ 
/*     */ @scala.reflect.ScalaSignature(bytes="\006\001%3A!\001\002\001\027\t\0013\013]1sW\026sw-\0338f-J2uN]#oIN#\030\r^5p]B\023Xm\035;p\025\t\031A!\001\004ce\006t7\r\033\006\003\013\031\ta!\0328hS:,'BA\004\t\003\021I\030n]1\013\003%\t1aY8n\007\001\0312\001\001\007\025!\ti!#D\001\017\025\ty\001#\001\003mC:<'\"A\t\002\t)\fg/Y\005\003'9\021aa\0242kK\016$\bCA\007\026\023\t1bB\001\005Sk:t\027M\0317f\021!A\002A!A!\002\023I\022\001\0027j]\026\004\"A\007\021\017\005mqR\"\001\017\013\003u\tQa]2bY\006L!a\b\017\002\rA\023X\rZ3g\023\t\t#E\001\004TiJLgn\032\006\003?qA\001\002\n\001\003\002\003\006I!G\001\ni\006\024G.\032(b[\026D\001B\n\001\003\002\003\006I!G\001\013u.Dun\035;q_J$\b\002\003\025\001\005\003\005\013\021B\r\002\035A\023Xm\035;p\021>\034H\017U8si\")!\006\001C\001W\0051A(\0338jiz\"R\001\f\0300aE\002\"!\f\001\016\003\tAQ\001G\025A\002eAQ\001J\025A\002eAQAJ\025A\002eAQ\001K\025A\002eAQa\r\001\005BQ\n1A];o)\005)\004CA\0167\023\t9DD\001\003V]&$\b\"B\035\001\t\003Q\024!D4t_:\f%O]1z\rVt7\rF\002\032w\rCQ\001\020\035A\002u\n\021\"\0338qkR\024U-\0318\021\005y\nU\"A \013\005\001#\021AB2p[6|g.\003\002C\tI\021J\0349vi\n+\027M\034\005\006Ia\002\r!\007\005\006\013\002!\tAR\001\nO\026$H)\031;fS\022$\"!G$\t\013!#\005\031A\r\002\025QLW.Z*ue&tw\r")
/*     */ public class SparkEngineV2ForEndStationPresto implements Runnable
/*     */ {
/*     */   private final String line;
/*     */   
/*     */   public void run()
/*     */   {
/*  25 */     String[] line_arr = this.line.split("\\|");
/*     */     
/*  27 */     String jobId = line_arr[1];
/*  28 */     String params = line_arr[2];
/*     */     
/*  30 */     Gson gson = new Gson();
/*  31 */     java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken() {}.getType();
/*  32 */     final InputBean[] map = (InputBean[])gson.fromJson(params, mapType);
/*     */     
/*  34 */     InputBean inputBeanRepair = null;
/*     */     
/*  36 */     scala.runtime.Null. resultData = null;
/*     */     
/*  38 */     final IntRef count = IntRef.create(0);
/*     */     
/*     */ 
/*  41 */     if (map.length > 0)
/*     */     {
/*  43 */       final ObjectRef sql = ObjectRef.create(new StringBuffer());
/*     */       
/*  45 */       ((StringBuffer)sql.elem).append("select max(lastcaptured) as lastcaptured,arbitrary(solrid) as solrid,count(1) as num from (");
/*     */       
/*  47 */       final IntRef i = IntRef.create(1);
/*     */       
/*  49 */       Predef..MODULE$.refArrayOps((Object[])map).foreach(new AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */         
/*  51 */         public final void apply(InputBean m) { count.elem = m.count();
/*     */           
/*  53 */           String sqlTem = SparkEngineV2ForEndStationPresto.this.gsonArrayFunc(m, SparkEngineV2ForEndStationPresto.this.com$yisa$engine$branch$SparkEngineV2ForEndStationPresto$$tableName);
/*     */           
/*  55 */           ((StringBuffer)sql.elem).append("(").append(sqlTem).append(")");
/*     */           
/*  57 */           if (i.elem < map.length)
/*     */           {
/*  59 */             ((StringBuffer)sql.elem).append(" union all ");
/*     */             
/*  61 */             i.elem += 1;
/*     */           }
/*     */           
/*     */         }
/*     */         
/*  66 */       });
/*  67 */       ((StringBuffer)sql.elem).append(")  group by platenumber");
/*     */       
/*  69 */       Predef..MODULE$.println(new StringBuilder().append("sql-all : ").append(((StringBuffer)sql.elem).toString()).toString());
/*     */       
/*  71 */       String PrestoURL = new StringBuilder().append("jdbc:presto://").append(this.PrestoHostPort).append("/hive/yisadata").toString();
/*  72 */       String PrestoUser = "spark";
/*  73 */       String PrestoPwd = "";
/*  74 */       String PrestoDriver = "com.facebook.presto.jdbc.PrestoDriver";
/*     */       
/*  76 */       Class.forName(PrestoDriver);
/*  77 */       Connection connP = DriverManager.getConnection(PrestoURL, PrestoUser, null);
/*     */       
/*  79 */       Statement stmtP = connP.createStatement();
/*     */       
/*  81 */       ResultSet rsP = stmtP.executeQuery(((StringBuffer)sql.elem).toString());
/*     */       
/*  83 */       Connection conn = MySQLConnectManager..MODULE$.getConnet(this.zkHostport);
/*  84 */       conn.setAutoCommit(false);
/*  85 */       String sqlI = "insert into zjwjz_result (s_id,j_id,t_n,l_c) values(?,?,?,?)";
/*  86 */       PreparedStatement pstmt = conn.prepareStatement(sqlI);
/*     */       
/*  88 */       int number = 0;
/*     */       
/*     */ 
/*  91 */       while (rsP.next())
/*     */       {
/*  93 */         String lastCaptured = rsP.getString(1);
/*  94 */         String solrid = rsP.getString(2);
/*  95 */         String num = rsP.getString(3);
/*     */         
/*  97 */         number += 1;
/*     */         
/*  99 */         pstmt.setString(1, solrid);
/* 100 */         pstmt.setString(2, jobId);
/* 101 */         pstmt.setString(3, num);
/* 102 */         pstmt.setString(4, lastCaptured);
/* 103 */         pstmt.addBatch();
/*     */       }
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
/* 115 */       String sql2 = "insert into zjwjz_progress (jobid,total) values(?,?)";
/* 116 */       PreparedStatement pstmt2 = conn.prepareStatement(sql2);
/*     */       
/* 118 */       if (number == 0) {
/* 119 */         number = -1;
/*     */       }
/*     */       
/* 122 */       pstmt2.setString(1, jobId);
/* 123 */       pstmt2.setInt(2, number);
/* 124 */       pstmt2.executeUpdate();
/* 125 */       pstmt2.close();
/*     */       
/* 127 */       pstmt.executeBatch();
/* 128 */       conn.commit();
/* 129 */       pstmt.close();
/* 130 */       conn.close();
/*     */       
/* 132 */       rsP.close();
/* 133 */       stmtP.close();
/* 134 */       connP.close();
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 140 */       Predef..MODULE$.println(new StringBuilder().append("参数异常 ：").append(line_arr).toString());
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String gsonArrayFunc(InputBean inputBean, String tableName)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 271	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   3: aload_1
/*     */     //   4: invokevirtual 276	com/yisa/engine/common/InputBean:startTime	()Ljava/lang/String;
/*     */     //   7: invokevirtual 280	com/yisa/engine/uitl/TimeUtil$:getTimeStringFormat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   10: astore_3
/*     */     //   11: getstatic 271	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   14: aload_1
/*     */     //   15: invokevirtual 283	com/yisa/engine/common/InputBean:endTime	()Ljava/lang/String;
/*     */     //   18: invokevirtual 280	com/yisa/engine/uitl/TimeUtil$:getTimeStringFormat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   21: astore 4
/*     */     //   23: aload_1
/*     */     //   24: invokevirtual 287	com/yisa/engine/common/InputBean:locationId	()[Ljava/lang/String;
/*     */     //   27: astore 5
/*     */     //   29: aload_1
/*     */     //   30: invokevirtual 290	com/yisa/engine/common/InputBean:plateNumber	()Ljava/lang/String;
/*     */     //   33: astore 6
/*     */     //   35: aload_1
/*     */     //   36: invokevirtual 293	com/yisa/engine/common/InputBean:differ	()I
/*     */     //   39: istore 7
/*     */     //   41: new 295	java/text/SimpleDateFormat
/*     */     //   44: dup
/*     */     //   45: ldc_w 297
/*     */     //   48: invokespecial 300	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   51: astore 8
/*     */     //   53: aload 8
/*     */     //   55: aload_1
/*     */     //   56: invokevirtual 276	com/yisa/engine/common/InputBean:startTime	()Ljava/lang/String;
/*     */     //   59: invokevirtual 304	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   62: invokevirtual 310	java/util/Date:getTime	()J
/*     */     //   65: ldc2_w 311
/*     */     //   68: ldiv
/*     */     //   69: lstore 9
/*     */     //   71: aload 8
/*     */     //   73: aload_1
/*     */     //   74: invokevirtual 283	com/yisa/engine/common/InputBean:endTime	()Ljava/lang/String;
/*     */     //   77: invokevirtual 304	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   80: invokevirtual 310	java/util/Date:getTime	()J
/*     */     //   83: ldc2_w 311
/*     */     //   86: ldiv
/*     */     //   87: lstore 11
/*     */     //   89: new 55	java/lang/StringBuffer
/*     */     //   92: dup
/*     */     //   93: invokespecial 56	java/lang/StringBuffer:<init>	()V
/*     */     //   96: astore 13
/*     */     //   98: aload 13
/*     */     //   100: ldc_w 314
/*     */     //   103: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   106: pop
/*     */     //   107: aload 13
/*     */     //   109: ldc_w 316
/*     */     //   112: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   115: aload_2
/*     */     //   116: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   119: pop
/*     */     //   120: aload 13
/*     */     //   122: ldc_w 318
/*     */     //   125: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   128: aload_0
/*     */     //   129: aload_3
/*     */     //   130: invokevirtual 321	com/yisa/engine/branch/SparkEngineV2ForEndStationPresto:getDateid	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   133: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   136: pop
/*     */     //   137: aload 13
/*     */     //   139: ldc_w 323
/*     */     //   142: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   145: aload_0
/*     */     //   146: aload 4
/*     */     //   148: invokevirtual 321	com/yisa/engine/branch/SparkEngineV2ForEndStationPresto:getDateid	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   151: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   154: pop
/*     */     //   155: aload 13
/*     */     //   157: ldc_w 325
/*     */     //   160: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   163: lload 9
/*     */     //   165: invokevirtual 328	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   168: pop
/*     */     //   169: aload 13
/*     */     //   171: ldc_w 330
/*     */     //   174: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   177: lload 11
/*     */     //   179: invokevirtual 328	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   182: pop
/*     */     //   183: aload 5
/*     */     //   185: aconst_null
/*     */     //   186: if_acmpeq +97 -> 283
/*     */     //   189: aload 5
/*     */     //   191: arraylength
/*     */     //   192: iconst_0
/*     */     //   193: if_icmple +90 -> 283
/*     */     //   196: getstatic 77	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   199: getstatic 77	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   202: aload 5
/*     */     //   204: checkcast 79	[Ljava/lang/Object;
/*     */     //   207: invokevirtual 83	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   210: new 332	com/yisa/engine/branch/SparkEngineV2ForEndStationPresto$$anonfun$1
/*     */     //   213: dup
/*     */     //   214: aload_0
/*     */     //   215: invokespecial 333	com/yisa/engine/branch/SparkEngineV2ForEndStationPresto$$anonfun$1:<init>	(Lcom/yisa/engine/branch/SparkEngineV2ForEndStationPresto;)V
/*     */     //   218: getstatic 338	scala/Array$:MODULE$	Lscala/Array$;
/*     */     //   221: getstatic 343	scala/reflect/ClassTag$:MODULE$	Lscala/reflect/ClassTag$;
/*     */     //   224: ldc 23
/*     */     //   226: invokevirtual 347	scala/reflect/ClassTag$:apply	(Ljava/lang/Class;)Lscala/reflect/ClassTag;
/*     */     //   229: invokevirtual 351	scala/Array$:canBuildFrom	(Lscala/reflect/ClassTag;)Lscala/collection/generic/CanBuildFrom;
/*     */     //   232: invokeinterface 354 3 0
/*     */     //   237: checkcast 79	[Ljava/lang/Object;
/*     */     //   240: invokevirtual 83	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   243: new 356	com/yisa/engine/branch/SparkEngineV2ForEndStationPresto$$anonfun$2
/*     */     //   246: dup
/*     */     //   247: aload_0
/*     */     //   248: invokespecial 357	com/yisa/engine/branch/SparkEngineV2ForEndStationPresto$$anonfun$2:<init>	(Lcom/yisa/engine/branch/SparkEngineV2ForEndStationPresto;)V
/*     */     //   251: invokeinterface 361 2 0
/*     */     //   256: checkcast 23	java/lang/String
/*     */     //   259: astore 14
/*     */     //   261: aload 13
/*     */     //   263: ldc_w 363
/*     */     //   266: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   269: aload 14
/*     */     //   271: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   274: ldc_w 365
/*     */     //   277: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   280: goto +6 -> 286
/*     */     //   283: getstatic 199	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   286: pop
/*     */     //   287: aload 6
/*     */     //   289: ifnull +30 -> 319
/*     */     //   292: aload 6
/*     */     //   294: ldc 123
/*     */     //   296: astore 15
/*     */     //   298: dup
/*     */     //   299: ifnonnull +12 -> 311
/*     */     //   302: pop
/*     */     //   303: aload 15
/*     */     //   305: ifnull +14 -> 319
/*     */     //   308: goto +17 -> 325
/*     */     //   311: aload 15
/*     */     //   313: invokevirtual 369	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   316: ifeq +9 -> 325
/*     */     //   319: getstatic 199	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   322: goto +40 -> 362
/*     */     //   325: aload 13
/*     */     //   327: ldc_w 371
/*     */     //   330: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   333: aload 6
/*     */     //   335: ldc_w 373
/*     */     //   338: ldc_w 375
/*     */     //   341: invokevirtual 379	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*     */     //   344: ldc_w 381
/*     */     //   347: ldc_w 383
/*     */     //   350: invokevirtual 379	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*     */     //   353: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   356: ldc_w 385
/*     */     //   359: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   362: pop
/*     */     //   363: aload 13
/*     */     //   365: ldc_w 387
/*     */     //   368: invokevirtual 71	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   371: pop
/*     */     //   372: getstatic 77	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   375: aload 13
/*     */     //   377: invokevirtual 108	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   380: invokevirtual 113	scala/Predef$:println	(Ljava/lang/Object;)V
/*     */     //   383: aload 13
/*     */     //   385: invokevirtual 108	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   388: areturn
/*     */     // Line number table:
/*     */     //   Java source line #148	-> byte code offset #0
/*     */     //   Java source line #149	-> byte code offset #11
/*     */     //   Java source line #150	-> byte code offset #23
/*     */     //   Java source line #151	-> byte code offset #29
/*     */     //   Java source line #152	-> byte code offset #35
/*     */     //   Java source line #154	-> byte code offset #41
/*     */     //   Java source line #156	-> byte code offset #53
/*     */     //   Java source line #157	-> byte code offset #71
/*     */     //   Java source line #159	-> byte code offset #89
/*     */     //   Java source line #161	-> byte code offset #98
/*     */     //   Java source line #163	-> byte code offset #107
/*     */     //   Java source line #165	-> byte code offset #120
/*     */     //   Java source line #167	-> byte code offset #137
/*     */     //   Java source line #169	-> byte code offset #155
/*     */     //   Java source line #171	-> byte code offset #169
/*     */     //   Java source line #173	-> byte code offset #183
/*     */     //   Java source line #174	-> byte code offset #196
/*     */     //   Java source line #176	-> byte code offset #261
/*     */     //   Java source line #173	-> byte code offset #283
/*     */     //   Java source line #179	-> byte code offset #287
/*     */     //   Java source line #180	-> byte code offset #325
/*     */     //   Java source line #179	-> byte code offset #362
/*     */     //   Java source line #183	-> byte code offset #363
/*     */     //   Java source line #185	-> byte code offset #372
/*     */     //   Java source line #187	-> byte code offset #383
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	389	0	this	SparkEngineV2ForEndStationPresto
/*     */     //   0	389	1	inputBean	InputBean
/*     */     //   0	389	2	tableName	String
/*     */     //   11	377	3	startTime1	String
/*     */     //   23	365	4	endTime1	String
/*     */     //   29	359	5	locationId	String[]
/*     */     //   35	353	6	plateNumber	String
/*     */     //   41	347	7	differ	int
/*     */     //   53	335	8	format2	java.text.SimpleDateFormat
/*     */     //   71	317	9	startTime	long
/*     */     //   89	299	11	endTime	long
/*     */     //   98	290	13	sb	StringBuffer
/*     */     //   261	19	14	l	String
/*     */   }
/*     */   
/*     */   public String getDateid(String timeString)
/*     */   {
/* 193 */     String timeLong = timeString.replaceAll("[-\\s:]", "");
/*     */     
/* 195 */     return timeLong.substring(0, 8);
/*     */   }
/*     */   
/*     */   public SparkEngineV2ForEndStationPresto(String line, String tableName, String zkHostport, String PrestoHostPort) {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\branch\SparkEngineV2ForEndStationPresto.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */