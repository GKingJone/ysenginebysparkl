/*     */ package com.yisa.engine.branch;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.yisa.engine.common.InputBean;
/*     */ import com.yisa.engine.db.MySQLConnectManager.;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.util.Date;
/*     */ import org.apache.spark.sql.Dataset;
/*     */ import org.apache.spark.sql.Row;
/*     */ import org.apache.spark.sql.SparkSession;
/*     */ import scala.Predef.;
/*     */ import scala.Serializable;
/*     */ import scala.collection.Iterator;
/*     */ import scala.collection.mutable.StringBuilder;
/*     */ import scala.runtime.AbstractFunction1;
/*     */ import scala.runtime.BoxesRunTime;
/*     */ import scala.runtime.IntRef;
/*     */ import scala.runtime.LongRef;
/*     */ 
/*     */ @scala.reflect.ScalaSignature(bytes="\006\001\0055a\001B\001\003\001-\021ad\0259be.,enZ5oKZ\023di\034:TK\006\0248\r[\"be\nK\b+[2\013\005\r!\021A\0022sC:\034\007N\003\002\006\r\0051QM\\4j]\026T!a\002\005\002\teL7/\031\006\002\023\005\0311m\\7\004\001M\021\001\001\004\t\003\033Ai\021A\004\006\002\037\005)1oY1mC&\021\021C\004\002\007\003:L(+\0324\t\013M\001A\021\001\013\002\rqJg.\033;?)\005)\002C\001\f\001\033\005\021\001\"\002\r\001\t\003I\022AD*fCJ\034\007nQ1s\005f\004\026n\031\013\0065uYCG\016\t\003\033mI!\001\b\b\003\tUs\027\016\036\005\006=]\001\raH\001\013gFd7i\0348uKb$\bC\001\021*\033\005\t#B\001\022$\003\r\031\030\017\034\006\003I\025\nQa\0359be.T!AJ\024\002\r\005\004\030m\0315f\025\005A\023aA8sO&\021!&\t\002\r'B\f'o[*fgNLwN\034\005\006Y]\001\r!L\001\005Y&tW\r\005\002/c9\021QbL\005\003a9\ta\001\025:fI\0264\027B\001\0324\005\031\031FO]5oO*\021\001G\004\005\006k]\001\r!L\001\ni\006\024G.\032(b[\026DQaN\fA\0025\n!B_6I_N$\bo\034:u\021\025I\004\001\"\001;\003m)\0070Z2vi\026\034\006/\031:l'FcUk]3QCJ$\030\016^5p]R)1(\024(Q#B\021AH\023\b\003{!s!AP$\017\005}2eB\001!F\035\t\tE)D\001C\025\t\031%\"\001\004=e>|GOP\005\002Q%\021aeJ\005\003I\025J!AI\022\n\005%\013\023a\0029bG.\fw-Z\005\003\0272\023\021\002R1uC\032\023\030-\\3\013\005%\013\003\"\002\0209\001\004y\002\"B(9\001\004i\023!B:rY~\033\006\"B\0349\001\004i\003\"\002*9\001\004i\023A\0026pE~KG\rC\003U\001\021\005Q+\001\016fq\026\034W\017^3Ta\006\0248nU)M\035>\004\026M\035;ji&|g\016F\003W3j[F\f\005\002\016/&\021\001L\004\002\004\023:$\b\"\002\020T\001\004y\002\"B(T\001\004i\003\"B\034T\001\004i\003\"\002*T\001\004i\003\"\0020\001\t\003y\026AB4fiN\013F\n\006\003aO>\004\bCA1g\033\005\021'BA2e\003\021a\027M\\4\013\003\025\fAA[1wC&\021!G\031\005\006Qv\003\r![\001\004[\006\004\bC\0016n\033\005Y'B\0017\005\003\031\031w.\\7p]&\021an\033\002\n\023:\004X\017\036\"fC:DQ!N/A\0025BQ!]/A\002I\fqb]5nS2\f'/\033;z\031&l\027\016\036\t\003\033ML!\001\036\b\003\017\t{w\016\\3b]\")a\017\001C\001o\006y1+Z1sG\"\034\025M\035\"z!&\034'\007F\003\033qfT8\020C\003\037k\002\007q\004C\003-k\002\007Q\006C\0036k\002\007Q\006C\0038k\002\007Q\006C\003~\001\021\005a0\001\005hKR4\030\r\\;f)\025y\030QAA\005!\ri\021\021A\005\004\003\007q!\001\002'p]\036Da!a\002}\001\004i\023\001\002;fqRDa!a\003}\001\004i\023!\002;fgR\024\004")
/*     */ public class SparkEngineV2ForSearchCarByPic
/*     */ {
/*     */   public void SearchCarByPic(SparkSession sqlContext, String line, String tableName, String zkHostport)
/*     */   {
/*  26 */     long now1 = new Date().getTime();
/*  27 */     String[] line_arr = line.split("\\|");
/*  28 */     String job_id = line_arr[1];
/*  29 */     String[] line2 = line_arr[2].split(",");
/*     */     
/*  31 */     Gson gson = new Gson();
/*  32 */     java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken() {}.getType();
/*  33 */     InputBean map = (InputBean)gson.fromJson(line_arr[2], mapType);
/*     */     
/*     */ 
/*  36 */     String sql1 = getSQL(map, tableName, true);
/*     */     
/*  38 */     Predef..MODULE$.println(new StringBuilder().append("SQL--------------:").append(sql1).toString());
/*     */     
/*  40 */     Dataset resultData = executeSparkSQLUsePartition(sqlContext, sql1, zkHostport, job_id);
/*  41 */     long insertMySQLEnd = new Date().getTime();
/*     */     
/*  43 */     long tempCount = resultData.count();
/*     */     
/*     */ 
/*  46 */     Predef..MODULE$.println(new StringBuilder().append("have no result first time ,set similarityLimit false ,run again , tempCount :").append(BoxesRunTime.boxToLong(tempCount)).toString());
/*  47 */     String sql2 = getSQL(map, tableName, false);
/*  48 */     Predef..MODULE$.println(new StringBuilder().append("SQL--------------:").append(sql2).toString());
/*     */     
/*     */ 
/*  51 */     long now3 = new Date().getTime();
/*  52 */     Predef..MODULE$.println(new StringBuilder().append("have  result or not , time :").append(BoxesRunTime.boxToLong(now3 - insertMySQLEnd)).toString());
/*     */   }
/*     */   
/*     */   public Dataset<Row> executeSparkSQLUsePartition(SparkSession sqlContext, String sql_S, final String zkHostport, final String job_id)
/*     */   {
/*  57 */     long now1 = new Date().getTime();
/*     */     
/*  59 */     Dataset resultData = sqlContext.sql(sql_S);
/*  60 */     long sparkTaskEnd = new Date().getTime();
/*     */     
/*  62 */     Predef..MODULE$.println(new StringBuilder().append("get result time :").append(BoxesRunTime.boxToLong(sparkTaskEnd - now1)).toString());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  67 */     resultData.foreachPartition(new AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */       
/*  69 */       public final void apply(Iterator<Row> data) { Connection conn = MySQLConnectManager..MODULE$.getConnet(zkHostport);
/*  70 */         conn.setAutoCommit(false);
/*  71 */         String sql = "insert into gpu_index_job (job_id,solr_id,sort_value,plate_number) values(?,?,?,?)";
/*  72 */         final PreparedStatement pstmt = conn.prepareStatement(sql);
/*     */         
/*  74 */         data.foreach(new AbstractFunction1() {
/*     */           public static final long serialVersionUID = 0L;
/*     */           
/*  77 */           public final void apply(Row t) { pstmt.setString(1, SparkEngineV2ForSearchCarByPic..anonfun.executeSparkSQLUsePartition.1.this.job_id$1);
/*  78 */             pstmt.setString(2, t.apply(0).toString());
/*  79 */             pstmt.setString(3, t.apply(1).toString());
/*  80 */             pstmt.setString(4, t.apply(2).toString());
/*  81 */             pstmt.addBatch();
/*     */           }
/*  83 */         });
/*  84 */         int[] test = pstmt.executeBatch();
/*  85 */         conn.commit();
/*  86 */         pstmt.close();
/*  87 */         conn.close();
/*     */       }
/*     */       
/*  90 */     });
/*  91 */     long insertMySQLEnd = new Date().getTime();
/*  92 */     Predef..MODULE$.println(new StringBuilder().append("insert mysql  time :").append(BoxesRunTime.boxToLong(insertMySQLEnd - sparkTaskEnd)).toString());
/*  93 */     return resultData;
/*     */   }
/*     */   
/*     */   public int executeSparkSQLNoPartition(SparkSession sqlContext, String sql_S, String zkHostport, final String job_id)
/*     */   {
/*  98 */     long now1 = new Date().getTime();
/*  99 */     Dataset resultData = sqlContext.sql(sql_S);
/* 100 */     long sparkTaskEnd = new Date().getTime();
/*     */     
/* 102 */     Predef..MODULE$.println(new StringBuilder().append("get result time :").append(BoxesRunTime.boxToLong(sparkTaskEnd - now1)).toString());
/*     */     
/*     */ 
/*     */ 
/* 106 */     Connection conn = MySQLConnectManager..MODULE$.getConnet(zkHostport);
/* 107 */     conn.setAutoCommit(false);
/* 108 */     String sql = "insert into gpu_index_job (job_id,solr_id,sort_value,plate_number) values(?,?,?,?)";
/* 109 */     final PreparedStatement pstmt = conn.prepareStatement(sql);
/*     */     
/*     */ 
/* 112 */     Row[] resultData2 = (Row[])resultData.collect();
/*     */     
/* 114 */     final IntRef tempCount = IntRef.create(0);
/* 115 */     Predef..MODULE$.refArrayOps((Object[])resultData2).foreach(new AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */       
/* 117 */       public final void apply(Row t) { tempCount.elem += 1;
/* 118 */         pstmt.setString(1, job_id);
/* 119 */         pstmt.setString(2, t.apply(0).toString());
/* 120 */         pstmt.setString(3, t.apply(1).toString());
/* 121 */         pstmt.setString(4, t.apply(2).toString());
/* 122 */         pstmt.addBatch();
/*     */       }
/* 124 */     });
/* 125 */     int[] test = pstmt.executeBatch();
/* 126 */     conn.commit();
/* 127 */     pstmt.close();
/* 128 */     conn.close();
/* 129 */     long insertMySQLEnd = new Date().getTime();
/* 130 */     Predef..MODULE$.println(new StringBuilder().append("insert mysql  time :").append(BoxesRunTime.boxToLong(insertMySQLEnd - sparkTaskEnd)).toString());
/*     */     
/* 132 */     return tempCount.elem;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String getSQL(InputBean map, String tableName, boolean similarityLimit)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokevirtual 230	com/yisa/engine/common/InputBean:feature	()Ljava/lang/String;
/*     */     //   4: astore 4
/*     */     //   6: aload_1
/*     */     //   7: invokevirtual 233	com/yisa/engine/common/InputBean:carBrand	()Ljava/lang/String;
/*     */     //   10: astore 5
/*     */     //   12: aload_1
/*     */     //   13: invokevirtual 237	com/yisa/engine/common/InputBean:carModel	()[Ljava/lang/String;
/*     */     //   16: astore 6
/*     */     //   18: getstatic 242	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   21: aload_1
/*     */     //   22: invokevirtual 245	com/yisa/engine/common/InputBean:startTime	()Ljava/lang/String;
/*     */     //   25: invokevirtual 249	com/yisa/engine/uitl/TimeUtil$:getTimestampLong	(Ljava/lang/String;)J
/*     */     //   28: lstore 7
/*     */     //   30: getstatic 242	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   33: aload_1
/*     */     //   34: invokevirtual 252	com/yisa/engine/common/InputBean:endTime	()Ljava/lang/String;
/*     */     //   37: invokevirtual 249	com/yisa/engine/uitl/TimeUtil$:getTimestampLong	(Ljava/lang/String;)J
/*     */     //   40: lstore 9
/*     */     //   42: getstatic 242	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   45: aload_1
/*     */     //   46: invokevirtual 245	com/yisa/engine/common/InputBean:startTime	()Ljava/lang/String;
/*     */     //   49: invokevirtual 256	com/yisa/engine/uitl/TimeUtil$:getDateId	(Ljava/lang/String;)I
/*     */     //   52: istore 11
/*     */     //   54: getstatic 242	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   57: aload_1
/*     */     //   58: invokevirtual 252	com/yisa/engine/common/InputBean:endTime	()Ljava/lang/String;
/*     */     //   61: invokevirtual 256	com/yisa/engine/uitl/TimeUtil$:getDateId	(Ljava/lang/String;)I
/*     */     //   64: istore 12
/*     */     //   66: new 258	java/lang/StringBuffer
/*     */     //   69: dup
/*     */     //   70: invokespecial 259	java/lang/StringBuffer:<init>	()V
/*     */     //   73: astore 13
/*     */     //   75: aload 13
/*     */     //   77: ldc_w 261
/*     */     //   80: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   83: pop
/*     */     //   84: aload 13
/*     */     //   86: ldc_w 266
/*     */     //   89: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   92: pop
/*     */     //   93: aload 13
/*     */     //   95: new 60	scala/collection/mutable/StringBuilder
/*     */     //   98: dup
/*     */     //   99: invokespecial 61	scala/collection/mutable/StringBuilder:<init>	()V
/*     */     //   102: ldc_w 268
/*     */     //   105: invokevirtual 67	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   108: aload 4
/*     */     //   110: invokevirtual 67	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   113: ldc_w 270
/*     */     //   116: invokevirtual 67	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   119: invokevirtual 71	scala/collection/mutable/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   122: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   125: pop
/*     */     //   126: aload 13
/*     */     //   128: new 60	scala/collection/mutable/StringBuilder
/*     */     //   131: dup
/*     */     //   132: invokespecial 61	scala/collection/mutable/StringBuilder:<init>	()V
/*     */     //   135: ldc_w 272
/*     */     //   138: invokevirtual 67	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   141: aload_2
/*     */     //   142: invokevirtual 67	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   145: ldc_w 274
/*     */     //   148: invokevirtual 67	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   151: invokevirtual 71	scala/collection/mutable/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   154: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   157: pop
/*     */     //   158: aload_1
/*     */     //   159: invokevirtual 277	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   162: aconst_null
/*     */     //   163: if_acmpeq +120 -> 283
/*     */     //   166: aload_1
/*     */     //   167: invokevirtual 277	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   170: iconst_0
/*     */     //   171: aaload
/*     */     //   172: ldc_w 279
/*     */     //   175: astore 14
/*     */     //   177: dup
/*     */     //   178: ifnonnull +12 -> 190
/*     */     //   181: pop
/*     */     //   182: aload 14
/*     */     //   184: ifnull +99 -> 283
/*     */     //   187: goto +11 -> 198
/*     */     //   190: aload 14
/*     */     //   192: invokevirtual 283	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   195: ifne +88 -> 283
/*     */     //   198: aload_1
/*     */     //   199: invokevirtual 277	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   202: iconst_0
/*     */     //   203: aaload
/*     */     //   204: ldc_w 285
/*     */     //   207: astore 15
/*     */     //   209: dup
/*     */     //   210: ifnonnull +12 -> 222
/*     */     //   213: pop
/*     */     //   214: aload 15
/*     */     //   216: ifnull +67 -> 283
/*     */     //   219: goto +11 -> 230
/*     */     //   222: aload 15
/*     */     //   224: invokevirtual 283	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   227: ifne +56 -> 283
/*     */     //   230: getstatic 58	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   233: aload_1
/*     */     //   234: invokevirtual 277	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   237: checkcast 188	[Ljava/lang/Object;
/*     */     //   240: invokevirtual 192	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   243: new 287	com/yisa/engine/branch/SparkEngineV2ForSearchCarByPic$$anonfun$1
/*     */     //   246: dup
/*     */     //   247: aload_0
/*     */     //   248: invokespecial 288	com/yisa/engine/branch/SparkEngineV2ForSearchCarByPic$$anonfun$1:<init>	(Lcom/yisa/engine/branch/SparkEngineV2ForSearchCarByPic;)V
/*     */     //   251: invokeinterface 292 2 0
/*     */     //   256: checkcast 24	java/lang/String
/*     */     //   259: astore 16
/*     */     //   261: aload 13
/*     */     //   263: ldc_w 294
/*     */     //   266: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   269: aload 16
/*     */     //   271: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   274: ldc_w 296
/*     */     //   277: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   280: goto +6 -> 286
/*     */     //   283: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   286: pop
/*     */     //   287: lload 7
/*     */     //   289: lconst_0
/*     */     //   290: lcmp
/*     */     //   291: ifeq +19 -> 310
/*     */     //   294: aload 13
/*     */     //   296: ldc_w 298
/*     */     //   299: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   302: lload 7
/*     */     //   304: invokevirtual 301	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   307: goto +6 -> 313
/*     */     //   310: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   313: pop
/*     */     //   314: lload 9
/*     */     //   316: lconst_0
/*     */     //   317: lcmp
/*     */     //   318: ifeq +19 -> 337
/*     */     //   321: aload 13
/*     */     //   323: ldc_w 303
/*     */     //   326: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   329: lload 9
/*     */     //   331: invokevirtual 301	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   334: goto +6 -> 340
/*     */     //   337: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   340: pop
/*     */     //   341: lload 7
/*     */     //   343: lconst_0
/*     */     //   344: lcmp
/*     */     //   345: ifeq +19 -> 364
/*     */     //   348: aload 13
/*     */     //   350: ldc_w 305
/*     */     //   353: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   356: iload 11
/*     */     //   358: invokevirtual 308	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*     */     //   361: goto +6 -> 367
/*     */     //   364: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   367: pop
/*     */     //   368: lload 9
/*     */     //   370: lconst_0
/*     */     //   371: lcmp
/*     */     //   372: ifeq +19 -> 391
/*     */     //   375: aload 13
/*     */     //   377: ldc_w 310
/*     */     //   380: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   383: iload 12
/*     */     //   385: invokevirtual 308	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*     */     //   388: goto +6 -> 394
/*     */     //   391: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   394: pop
/*     */     //   395: aload 6
/*     */     //   397: aconst_null
/*     */     //   398: if_acmpeq +93 -> 491
/*     */     //   401: aload 6
/*     */     //   403: arraylength
/*     */     //   404: iconst_0
/*     */     //   405: if_icmple +86 -> 491
/*     */     //   408: aload_1
/*     */     //   409: invokevirtual 237	com/yisa/engine/common/InputBean:carModel	()[Ljava/lang/String;
/*     */     //   412: iconst_0
/*     */     //   413: aaload
/*     */     //   414: ldc_w 285
/*     */     //   417: astore 17
/*     */     //   419: dup
/*     */     //   420: ifnonnull +12 -> 432
/*     */     //   423: pop
/*     */     //   424: aload 17
/*     */     //   426: ifnull +65 -> 491
/*     */     //   429: goto +11 -> 440
/*     */     //   432: aload 17
/*     */     //   434: invokevirtual 283	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   437: ifne +54 -> 491
/*     */     //   440: getstatic 58	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   443: aload 6
/*     */     //   445: checkcast 188	[Ljava/lang/Object;
/*     */     //   448: invokevirtual 192	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   451: new 312	com/yisa/engine/branch/SparkEngineV2ForSearchCarByPic$$anonfun$2
/*     */     //   454: dup
/*     */     //   455: aload_0
/*     */     //   456: invokespecial 313	com/yisa/engine/branch/SparkEngineV2ForSearchCarByPic$$anonfun$2:<init>	(Lcom/yisa/engine/branch/SparkEngineV2ForSearchCarByPic;)V
/*     */     //   459: invokeinterface 292 2 0
/*     */     //   464: checkcast 24	java/lang/String
/*     */     //   467: astore 18
/*     */     //   469: aload 13
/*     */     //   471: ldc_w 315
/*     */     //   474: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   477: aload 18
/*     */     //   479: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   482: ldc_w 296
/*     */     //   485: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   488: goto +6 -> 494
/*     */     //   491: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   494: pop
/*     */     //   495: aload 5
/*     */     //   497: ifnull +59 -> 556
/*     */     //   500: aload 5
/*     */     //   502: ldc_w 279
/*     */     //   505: astore 19
/*     */     //   507: dup
/*     */     //   508: ifnonnull +12 -> 520
/*     */     //   511: pop
/*     */     //   512: aload 19
/*     */     //   514: ifnull +42 -> 556
/*     */     //   517: goto +11 -> 528
/*     */     //   520: aload 19
/*     */     //   522: invokevirtual 283	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   525: ifne +31 -> 556
/*     */     //   528: aload 5
/*     */     //   530: ldc_w 285
/*     */     //   533: astore 20
/*     */     //   535: dup
/*     */     //   536: ifnonnull +12 -> 548
/*     */     //   539: pop
/*     */     //   540: aload 20
/*     */     //   542: ifnull +14 -> 556
/*     */     //   545: goto +17 -> 562
/*     */     //   548: aload 20
/*     */     //   550: invokevirtual 283	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   553: ifeq +9 -> 562
/*     */     //   556: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   559: goto +16 -> 575
/*     */     //   562: aload 13
/*     */     //   564: ldc_w 317
/*     */     //   567: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   570: aload 5
/*     */     //   572: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   575: pop
/*     */     //   576: aload 13
/*     */     //   578: ldc_w 319
/*     */     //   581: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   584: pop
/*     */     //   585: iload_3
/*     */     //   586: ifeq +23 -> 609
/*     */     //   589: aload 13
/*     */     //   591: ldc_w 321
/*     */     //   594: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   597: pop
/*     */     //   598: aload 13
/*     */     //   600: ldc_w 323
/*     */     //   603: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   606: goto +6 -> 612
/*     */     //   609: getstatic 98	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   612: pop
/*     */     //   613: aload 13
/*     */     //   615: ldc_w 325
/*     */     //   618: invokevirtual 264	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   621: pop
/*     */     //   622: aload 13
/*     */     //   624: invokevirtual 326	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   627: areturn
/*     */     // Line number table:
/*     */     //   Java source line #136	-> byte code offset #0
/*     */     //   Java source line #138	-> byte code offset #6
/*     */     //   Java source line #140	-> byte code offset #12
/*     */     //   Java source line #144	-> byte code offset #18
/*     */     //   Java source line #145	-> byte code offset #30
/*     */     //   Java source line #147	-> byte code offset #42
/*     */     //   Java source line #148	-> byte code offset #54
/*     */     //   Java source line #150	-> byte code offset #66
/*     */     //   Java source line #152	-> byte code offset #75
/*     */     //   Java source line #153	-> byte code offset #84
/*     */     //   Java source line #154	-> byte code offset #93
/*     */     //   Java source line #156	-> byte code offset #126
/*     */     //   Java source line #158	-> byte code offset #158
/*     */     //   Java source line #159	-> byte code offset #230
/*     */     //   Java source line #160	-> byte code offset #261
/*     */     //   Java source line #158	-> byte code offset #283
/*     */     //   Java source line #163	-> byte code offset #287
/*     */     //   Java source line #164	-> byte code offset #294
/*     */     //   Java source line #163	-> byte code offset #310
/*     */     //   Java source line #167	-> byte code offset #314
/*     */     //   Java source line #168	-> byte code offset #321
/*     */     //   Java source line #167	-> byte code offset #337
/*     */     //   Java source line #171	-> byte code offset #341
/*     */     //   Java source line #172	-> byte code offset #348
/*     */     //   Java source line #171	-> byte code offset #364
/*     */     //   Java source line #175	-> byte code offset #368
/*     */     //   Java source line #176	-> byte code offset #375
/*     */     //   Java source line #175	-> byte code offset #391
/*     */     //   Java source line #187	-> byte code offset #395
/*     */     //   Java source line #188	-> byte code offset #440
/*     */     //   Java source line #189	-> byte code offset #469
/*     */     //   Java source line #187	-> byte code offset #491
/*     */     //   Java source line #192	-> byte code offset #495
/*     */     //   Java source line #193	-> byte code offset #562
/*     */     //   Java source line #192	-> byte code offset #575
/*     */     //   Java source line #197	-> byte code offset #576
/*     */     //   Java source line #199	-> byte code offset #585
/*     */     //   Java source line #200	-> byte code offset #589
/*     */     //   Java source line #201	-> byte code offset #598
/*     */     //   Java source line #199	-> byte code offset #609
/*     */     //   Java source line #204	-> byte code offset #613
/*     */     //   Java source line #205	-> byte code offset #622
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	628	0	this	SparkEngineV2ForSearchCarByPic
/*     */     //   0	628	1	map	InputBean
/*     */     //   0	628	2	tableName	String
/*     */     //   0	628	3	similarityLimit	boolean
/*     */     //   6	621	4	feature	String
/*     */     //   12	615	5	carBrand	String
/*     */     //   18	609	6	carModel	String[]
/*     */     //   30	597	7	startTime	long
/*     */     //   42	585	9	endTime	long
/*     */     //   54	573	11	startTimeDateid	int
/*     */     //   66	561	12	endTimeDateid	int
/*     */     //   75	552	13	sb	StringBuffer
/*     */     //   261	19	16	m	String
/*     */     //   469	19	18	m	String
/*     */   }
/*     */   
/*     */   public void SearchCarByPic2(SparkSession sqlContext, String line, String tableName, String zkHostport)
/*     */   {
/* 208 */     long now1 = new Date().getTime();
/* 209 */     String[] line_arr = line.split("\\|");
/* 210 */     String job_id = line_arr[1];
/* 211 */     String[] line2 = line_arr[2].split(",");
/*     */     
/* 213 */     Gson gson = new Gson();
/* 214 */     java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken() {}.getType();
/* 215 */     InputBean map = (InputBean)gson.fromJson(line_arr[2], mapType);
/*     */     
/*     */ 
/* 218 */     String sql1 = getSQL(map, tableName, true);
/*     */     
/*     */ 
/* 221 */     int tempCount = executeSparkSQLNoPartition(sqlContext, sql1, zkHostport, job_id);
/*     */     
/* 223 */     long insertMySQLEnd = new Date().getTime();
/*     */     
/* 225 */     Predef..MODULE$.println(new StringBuilder().append("have no result first time ,set similarityLimit false ,run again , tempCount :").append(BoxesRunTime.boxToInteger(tempCount)).toString());
/* 226 */     String sql2 = getSQL(map, tableName, false);
/*     */     
/*     */ 
/*     */ 
/* 230 */     long secondTime = new Date().getTime();
/* 231 */     Predef..MODULE$.println(new StringBuilder().append("have  result or not , time :").append(BoxesRunTime.boxToLong(secondTime - insertMySQLEnd)).toString());
/*     */   }
/*     */   
/* 234 */   public long getvalue(String text, final String test2) { final byte[] byteData = java.util.Base64.getDecoder().decode(text);
/* 235 */     final byte[] oldData = java.util.Base64.getDecoder().decode(test2);
/* 236 */     final LongRef num = LongRef.create(0L);scala.runtime.RichInt..MODULE$
/* 237 */       .until$extension0(Predef..MODULE$.intWrapper(0), byteData.length).withFilter(new AbstractFunction1.mcZI.sp() { public boolean apply$mcZI$sp(int i) { return test2.length() > 30; } public final boolean apply(int i) { return apply$mcZI$sp(i); } }).foreach(new AbstractFunction1.mcVI.sp() { public final void apply(int i) { apply$mcVI$sp(i); }
/* 238 */         public void apply$mcVI$sp(int i) { int n = (byteData[i] & 0xFF) - (oldData[i] & 0xFF);
/* 239 */           num.elem += n * n;
/* 240 */         } });
/* 241 */     return num.elem;
/*     */   }
/*     */   
/*     */   public static final long serialVersionUID = 0L;
/*     */   public static final long serialVersionUID = 0L;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\branch\SparkEngineV2ForSearchCarByPic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */