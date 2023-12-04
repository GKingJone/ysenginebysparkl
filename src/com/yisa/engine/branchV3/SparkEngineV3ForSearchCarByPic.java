/*     */ package com.yisa.engine.branchV3;
/*     */ 
/*     */ import com.yisa.engine.common.InputBean;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.util.Date;
/*     */ import org.apache.spark.sql.Row;
/*     */ import org.apache.spark.sql.SparkSession;
/*     */ import scala.Predef.;
/*     */ import scala.Serializable;
/*     */ import scala.collection.mutable.StringBuilder;
/*     */ import scala.runtime.BoxesRunTime;
/*     */ import scala.runtime.LongRef;
/*     */ 
/*     */ @scala.reflect.ScalaSignature(bytes="\006\001\005ma\001B\001\003\001-\021ad\0259be.,enZ5oKZ\033di\034:TK\006\0248\r[\"be\nK\b+[2\013\005\r!\021\001\0032sC:\034\007NV\032\013\005\0251\021AB3oO&tWM\003\002\b\021\005!\0210[:b\025\005I\021aA2p[\016\0011c\001\001\r)A\021QBE\007\002\035)\021q\002E\001\005Y\006twMC\001\022\003\021Q\027M^1\n\005Mq!AB(cU\026\034G\017\005\002\016+%\021aC\004\002\t%Vtg.\0312mK\"A\001\004\001B\001B\003%\021$\001\006tc2\034uN\034;fqR\004\"AG\022\016\003mQ!\001H\017\002\007M\fHN\003\002\037?\005)1\017]1sW*\021\001%I\001\007CB\f7\r[3\013\003\t\n1a\034:h\023\t!3D\001\007Ta\006\0248nU3tg&|g\016\003\005'\001\t\005\t\025!\003(\003\021a\027N\\3\021\005!rcBA\025-\033\005Q#\"A\026\002\013M\034\027\r\\1\n\0055R\023A\002)sK\022,g-\003\0020a\t11\013\036:j]\036T!!\f\026\t\021I\002!\021!Q\001\n\035\n\021\002^1cY\026t\025-\\3\t\021Q\002!\021!Q\001\n\035\n!B_6I_N$\bo\034:u\021\0251\004\001\"\0018\003\031a\024N\\5u}Q)\001HO\036={A\021\021\bA\007\002\005!)\001$\016a\0013!)a%\016a\001O!)!'\016a\001O!)A'\016a\001O!)q\b\001C!\001\006\031!/\0368\025\003\005\003\"!\013\"\n\005\rS#\001B+oSRDQ!\022\001\005\002\031\0131$\032=fGV$Xm\0259be.\034\026\013T+tKB\013'\017^5uS>tG#B$Z5rk\006C\001%W\035\tIEK\004\002K':\0211J\025\b\003\031Fs!!\024)\016\0039S!a\024\006\002\rq\022xn\034;?\023\005\021\023B\001\021\"\023\tqr$\003\002\035;%\021QkG\001\ba\006\0347.Y4f\023\t9\006LA\005ECR\fgI]1nK*\021Qk\007\005\0061\021\003\r!\007\005\0067\022\003\raJ\001\006gFdwl\025\005\006i\021\003\ra\n\005\006=\022\003\raJ\001\007U>\024w,\0333\t\013\001\004A\021A1\0025\025DXmY;uKN\003\030M]6T#2su\016U1si&$\030n\0348\025\013\t,gm\0325\021\005%\032\027B\0013+\005\rIe\016\036\005\0061}\003\r!\007\005\0067~\003\ra\n\005\006i}\003\ra\n\005\006=~\003\ra\n\005\006U\002!\ta[\001\007O\026$8+\025'\025\t1tgo\036\t\003\0335L!a\f\b\t\013=L\007\031\0019\002\0075\f\007\017\005\002ri6\t!O\003\002t\t\00511m\\7n_:L!!\036:\003\023%s\007/\036;CK\006t\007\"\002\032j\001\0049\003\"\002=j\001\004I\030aD:j[&d\027M]5us2KW.\033;\021\005%R\030BA>+\005\035\021un\0347fC:DQ! \001\005\002y\fqbU3be\016D7)\031:CsBK7M\r\013\t\003~\f\t!a\001\002\006!)\001\004 a\0013!)a\005 a\001O!)!\007 a\001O!)A\007 a\001O!9\021\021\002\001\005\002\005-\021\001C4fiZ\fG.^3\025\r\0055\0211CA\f!\rI\023qB\005\004\003#Q#\001\002'p]\036Dq!!\006\002\b\001\007q%\001\003uKb$\bbBA\r\003\017\001\raJ\001\006i\026\034HO\r")
/*     */ public class SparkEngineV3ForSearchCarByPic implements Runnable
/*     */ {
/*     */   private final SparkSession sqlContext;
/*     */   
/*     */   public void run()
/*     */   {
/*  22 */     long now1 = new Date().getTime();
/*  23 */     String[] line_arr = this.line.split("\\|");
/*  24 */     String job_id = line_arr[1];
/*     */     
/*  26 */     com.google.gson.Gson gson = new com.google.gson.Gson();
/*  27 */     java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken() {}.getType();
/*  28 */     InputBean map = (InputBean)gson.fromJson(line_arr[2], mapType);
/*     */     
/*     */ 
/*  31 */     String sql1 = getSQL(map, this.tableName, true);
/*     */     
/*  33 */     Predef..MODULE$.println(new StringBuilder().append("SQL--------------:").append(sql1).toString());
/*     */     
/*  35 */     int resultData = executeSparkSQLNoPartition(this.sqlContext, sql1, this.zkHostport, job_id);
/*  36 */     long insertMySQLEnd = new Date().getTime();
/*     */     
/*  38 */     int tempCount = resultData;
/*     */     
/*     */ 
/*  41 */     Predef..MODULE$.println(new StringBuilder().append("have no result first time ,set similarityLimit false ,run again , tempCount :").append(BoxesRunTime.boxToInteger(tempCount)).toString());
/*  42 */     String sql2 = getSQL(map, this.tableName, false);
/*  43 */     Predef..MODULE$.println(new StringBuilder().append("SQL--------------:").append(sql2).toString());
/*     */     
/*     */ 
/*  46 */     long now3 = new Date().getTime();
/*  47 */     Predef..MODULE$.println(new StringBuilder().append("have  result or not , time :").append(BoxesRunTime.boxToLong(now3 - insertMySQLEnd)).toString());
/*     */   }
/*     */   
/*     */   public org.apache.spark.sql.Dataset<Row> executeSparkSQLUsePartition(SparkSession sqlContext, String sql_S, final String zkHostport, final String job_id)
/*     */   {
/*  52 */     long now1 = new Date().getTime();
/*     */     
/*  54 */     org.apache.spark.sql.Dataset resultData = sqlContext.sql(sql_S);
/*  55 */     long sparkTaskEnd = new Date().getTime();
/*     */     
/*  57 */     Predef..MODULE$.println(new StringBuilder().append("get result time :").append(BoxesRunTime.boxToLong(sparkTaskEnd - now1)).toString());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  62 */     resultData.foreachPartition(new scala.runtime.AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */       
/*  64 */       public final void apply(scala.collection.Iterator<Row> data) { Connection conn = com.yisa.engine.db.MySQLConnectManager..MODULE$.getConnet(zkHostport);
/*  65 */         conn.setAutoCommit(false);
/*  66 */         String sql = "insert into gpu_index_job (job_id,solr_id,sort_value,plate_number) values(?,?,?,?)";
/*  67 */         final PreparedStatement pstmt = conn.prepareStatement(sql);
/*     */         
/*  69 */         data.foreach(new scala.runtime.AbstractFunction1() {
/*     */           public static final long serialVersionUID = 0L;
/*     */           
/*  72 */           public final void apply(Row t) { pstmt.setString(1, SparkEngineV3ForSearchCarByPic..anonfun.executeSparkSQLUsePartition.1.this.job_id$2);
/*  73 */             pstmt.setString(2, t.apply(0).toString());
/*  74 */             pstmt.setString(3, t.apply(1).toString());
/*  75 */             pstmt.setString(4, t.apply(2).toString());
/*  76 */             pstmt.addBatch();
/*     */           }
/*  78 */         });
/*  79 */         int[] test = pstmt.executeBatch();
/*  80 */         conn.commit();
/*  81 */         pstmt.close();
/*  82 */         conn.close();
/*     */       }
/*     */       
/*  85 */     });
/*  86 */     long insertMySQLEnd = new Date().getTime();
/*  87 */     Predef..MODULE$.println(new StringBuilder().append("insert mysql  time :").append(BoxesRunTime.boxToLong(insertMySQLEnd - sparkTaskEnd)).toString());
/*  88 */     return resultData;
/*     */   }
/*     */   
/*     */   public int executeSparkSQLNoPartition(SparkSession sqlContext, String sql_S, String zkHostport, final String job_id)
/*     */   {
/*  93 */     long now1 = new Date().getTime();
/*  94 */     org.apache.spark.sql.Dataset resultData = sqlContext.sql(sql_S);
/*  95 */     long sparkTaskEnd = new Date().getTime();
/*     */     
/*  97 */     Predef..MODULE$.println(new StringBuilder().append("get result time :").append(BoxesRunTime.boxToLong(sparkTaskEnd - now1)).toString());
/*     */     
/*     */ 
/*     */ 
/* 101 */     Connection conn = com.yisa.engine.db.MySQLConnectManager..MODULE$.getConnet(zkHostport);
/* 102 */     conn.setAutoCommit(false);
/* 103 */     String sql = "insert into gpu_index_job (job_id,solr_id,sort_value,plate_number) values(?,?,?,?)";
/* 104 */     final PreparedStatement pstmt = conn.prepareStatement(sql);
/*     */     
/*     */ 
/* 107 */     Row[] resultData2 = (Row[])resultData.collect();
/*     */     
/* 109 */     final scala.runtime.IntRef tempCount = scala.runtime.IntRef.create(0);
/* 110 */     Predef..MODULE$.refArrayOps((Object[])resultData2).foreach(new scala.runtime.AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */       
/* 112 */       public final void apply(Row t) { tempCount.elem += 1;
/* 113 */         pstmt.setString(1, job_id);
/* 114 */         pstmt.setString(2, t.apply(0).toString());
/* 115 */         pstmt.setString(3, t.apply(1).toString());
/* 116 */         pstmt.setString(4, t.apply(2).toString());
/* 117 */         pstmt.addBatch();
/*     */       }
/* 119 */     });
/* 120 */     int[] test = pstmt.executeBatch();
/* 121 */     conn.commit();
/* 122 */     pstmt.close();
/* 123 */     conn.close();
/* 124 */     long insertMySQLEnd = new Date().getTime();
/* 125 */     Predef..MODULE$.println(new StringBuilder().append("insert mysql  time :").append(BoxesRunTime.boxToLong(insertMySQLEnd - sparkTaskEnd)).toString());
/*     */     
/* 127 */     return tempCount.elem;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String getSQL(InputBean map, String tableName, boolean similarityLimit)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokevirtual 237	com/yisa/engine/common/InputBean:feature	()Ljava/lang/String;
/*     */     //   4: astore 4
/*     */     //   6: aload_1
/*     */     //   7: invokevirtual 240	com/yisa/engine/common/InputBean:carBrand	()Ljava/lang/String;
/*     */     //   10: astore 5
/*     */     //   12: aload_1
/*     */     //   13: invokevirtual 244	com/yisa/engine/common/InputBean:carModel	()[Ljava/lang/String;
/*     */     //   16: astore 6
/*     */     //   18: getstatic 249	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   21: aload_1
/*     */     //   22: invokevirtual 252	com/yisa/engine/common/InputBean:startTime	()Ljava/lang/String;
/*     */     //   25: invokevirtual 256	com/yisa/engine/uitl/TimeUtil$:getTimestampLong	(Ljava/lang/String;)J
/*     */     //   28: lstore 7
/*     */     //   30: getstatic 249	com/yisa/engine/uitl/TimeUtil$:MODULE$	Lcom/yisa/engine/uitl/TimeUtil$;
/*     */     //   33: aload_1
/*     */     //   34: invokevirtual 259	com/yisa/engine/common/InputBean:endTime	()Ljava/lang/String;
/*     */     //   37: invokevirtual 256	com/yisa/engine/uitl/TimeUtil$:getTimestampLong	(Ljava/lang/String;)J
/*     */     //   40: lstore 9
/*     */     //   42: new 261	java/lang/StringBuffer
/*     */     //   45: dup
/*     */     //   46: invokespecial 262	java/lang/StringBuffer:<init>	()V
/*     */     //   49: astore 11
/*     */     //   51: aload 11
/*     */     //   53: ldc_w 264
/*     */     //   56: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   59: pop
/*     */     //   60: aload 11
/*     */     //   62: ldc_w 269
/*     */     //   65: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   68: pop
/*     */     //   69: aload 11
/*     */     //   71: new 69	scala/collection/mutable/StringBuilder
/*     */     //   74: dup
/*     */     //   75: invokespecial 70	scala/collection/mutable/StringBuilder:<init>	()V
/*     */     //   78: ldc_w 271
/*     */     //   81: invokevirtual 76	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   84: aload 4
/*     */     //   86: invokevirtual 76	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   89: ldc_w 273
/*     */     //   92: invokevirtual 76	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   95: invokevirtual 80	scala/collection/mutable/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   98: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   101: pop
/*     */     //   102: aload 11
/*     */     //   104: new 69	scala/collection/mutable/StringBuilder
/*     */     //   107: dup
/*     */     //   108: invokespecial 70	scala/collection/mutable/StringBuilder:<init>	()V
/*     */     //   111: ldc_w 275
/*     */     //   114: invokevirtual 76	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   117: aload_2
/*     */     //   118: invokevirtual 76	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   121: ldc_w 277
/*     */     //   124: invokevirtual 76	scala/collection/mutable/StringBuilder:append	(Ljava/lang/Object;)Lscala/collection/mutable/StringBuilder;
/*     */     //   127: invokevirtual 80	scala/collection/mutable/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   130: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   133: pop
/*     */     //   134: aload_1
/*     */     //   135: invokevirtual 280	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   138: aconst_null
/*     */     //   139: if_acmpeq +120 -> 259
/*     */     //   142: aload_1
/*     */     //   143: invokevirtual 280	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   146: iconst_0
/*     */     //   147: aaload
/*     */     //   148: ldc_w 282
/*     */     //   151: astore 12
/*     */     //   153: dup
/*     */     //   154: ifnonnull +12 -> 166
/*     */     //   157: pop
/*     */     //   158: aload 12
/*     */     //   160: ifnull +99 -> 259
/*     */     //   163: goto +11 -> 174
/*     */     //   166: aload 12
/*     */     //   168: invokevirtual 286	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   171: ifne +88 -> 259
/*     */     //   174: aload_1
/*     */     //   175: invokevirtual 280	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   178: iconst_0
/*     */     //   179: aaload
/*     */     //   180: ldc_w 288
/*     */     //   183: astore 13
/*     */     //   185: dup
/*     */     //   186: ifnonnull +12 -> 198
/*     */     //   189: pop
/*     */     //   190: aload 13
/*     */     //   192: ifnull +67 -> 259
/*     */     //   195: goto +11 -> 206
/*     */     //   198: aload 13
/*     */     //   200: invokevirtual 286	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   203: ifne +56 -> 259
/*     */     //   206: getstatic 67	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   209: aload_1
/*     */     //   210: invokevirtual 280	com/yisa/engine/common/InputBean:carYear	()[Ljava/lang/String;
/*     */     //   213: checkcast 196	[Ljava/lang/Object;
/*     */     //   216: invokevirtual 200	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   219: new 290	com/yisa/engine/branchV3/SparkEngineV3ForSearchCarByPic$$anonfun$1
/*     */     //   222: dup
/*     */     //   223: aload_0
/*     */     //   224: invokespecial 291	com/yisa/engine/branchV3/SparkEngineV3ForSearchCarByPic$$anonfun$1:<init>	(Lcom/yisa/engine/branchV3/SparkEngineV3ForSearchCarByPic;)V
/*     */     //   227: invokeinterface 295 2 0
/*     */     //   232: checkcast 33	java/lang/String
/*     */     //   235: astore 14
/*     */     //   237: aload 11
/*     */     //   239: ldc_w 297
/*     */     //   242: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   245: aload 14
/*     */     //   247: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   250: ldc_w 299
/*     */     //   253: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   256: goto +6 -> 262
/*     */     //   259: getstatic 106	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   262: pop
/*     */     //   263: lload 7
/*     */     //   265: lconst_0
/*     */     //   266: lcmp
/*     */     //   267: ifeq +19 -> 286
/*     */     //   270: aload 11
/*     */     //   272: ldc_w 301
/*     */     //   275: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   278: lload 7
/*     */     //   280: invokevirtual 304	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   283: goto +6 -> 289
/*     */     //   286: getstatic 106	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   289: pop
/*     */     //   290: lload 9
/*     */     //   292: lconst_0
/*     */     //   293: lcmp
/*     */     //   294: ifeq +19 -> 313
/*     */     //   297: aload 11
/*     */     //   299: ldc_w 306
/*     */     //   302: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   305: lload 9
/*     */     //   307: invokevirtual 304	java/lang/StringBuffer:append	(J)Ljava/lang/StringBuffer;
/*     */     //   310: goto +6 -> 316
/*     */     //   313: getstatic 106	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   316: pop
/*     */     //   317: aload 6
/*     */     //   319: aconst_null
/*     */     //   320: if_acmpeq +93 -> 413
/*     */     //   323: aload 6
/*     */     //   325: arraylength
/*     */     //   326: iconst_0
/*     */     //   327: if_icmple +86 -> 413
/*     */     //   330: aload_1
/*     */     //   331: invokevirtual 244	com/yisa/engine/common/InputBean:carModel	()[Ljava/lang/String;
/*     */     //   334: iconst_0
/*     */     //   335: aaload
/*     */     //   336: ldc_w 288
/*     */     //   339: astore 15
/*     */     //   341: dup
/*     */     //   342: ifnonnull +12 -> 354
/*     */     //   345: pop
/*     */     //   346: aload 15
/*     */     //   348: ifnull +65 -> 413
/*     */     //   351: goto +11 -> 362
/*     */     //   354: aload 15
/*     */     //   356: invokevirtual 286	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   359: ifne +54 -> 413
/*     */     //   362: getstatic 67	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   365: aload 6
/*     */     //   367: checkcast 196	[Ljava/lang/Object;
/*     */     //   370: invokevirtual 200	scala/Predef$:refArrayOps	([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
/*     */     //   373: new 308	com/yisa/engine/branchV3/SparkEngineV3ForSearchCarByPic$$anonfun$2
/*     */     //   376: dup
/*     */     //   377: aload_0
/*     */     //   378: invokespecial 309	com/yisa/engine/branchV3/SparkEngineV3ForSearchCarByPic$$anonfun$2:<init>	(Lcom/yisa/engine/branchV3/SparkEngineV3ForSearchCarByPic;)V
/*     */     //   381: invokeinterface 295 2 0
/*     */     //   386: checkcast 33	java/lang/String
/*     */     //   389: astore 16
/*     */     //   391: aload 11
/*     */     //   393: ldc_w 311
/*     */     //   396: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   399: aload 16
/*     */     //   401: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   404: ldc_w 299
/*     */     //   407: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   410: goto +6 -> 416
/*     */     //   413: getstatic 106	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   416: pop
/*     */     //   417: aload 5
/*     */     //   419: ifnull +59 -> 478
/*     */     //   422: aload 5
/*     */     //   424: ldc_w 282
/*     */     //   427: astore 17
/*     */     //   429: dup
/*     */     //   430: ifnonnull +12 -> 442
/*     */     //   433: pop
/*     */     //   434: aload 17
/*     */     //   436: ifnull +42 -> 478
/*     */     //   439: goto +11 -> 450
/*     */     //   442: aload 17
/*     */     //   444: invokevirtual 286	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   447: ifne +31 -> 478
/*     */     //   450: aload 5
/*     */     //   452: ldc_w 288
/*     */     //   455: astore 18
/*     */     //   457: dup
/*     */     //   458: ifnonnull +12 -> 470
/*     */     //   461: pop
/*     */     //   462: aload 18
/*     */     //   464: ifnull +14 -> 478
/*     */     //   467: goto +17 -> 484
/*     */     //   470: aload 18
/*     */     //   472: invokevirtual 286	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   475: ifeq +9 -> 484
/*     */     //   478: getstatic 106	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   481: goto +16 -> 497
/*     */     //   484: aload 11
/*     */     //   486: ldc_w 313
/*     */     //   489: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   492: aload 5
/*     */     //   494: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   497: pop
/*     */     //   498: aload 11
/*     */     //   500: ldc_w 315
/*     */     //   503: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   506: pop
/*     */     //   507: iload_3
/*     */     //   508: ifeq +23 -> 531
/*     */     //   511: aload 11
/*     */     //   513: ldc_w 317
/*     */     //   516: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   519: pop
/*     */     //   520: aload 11
/*     */     //   522: ldc_w 319
/*     */     //   525: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   528: goto +6 -> 534
/*     */     //   531: getstatic 106	scala/runtime/BoxedUnit:UNIT	Lscala/runtime/BoxedUnit;
/*     */     //   534: pop
/*     */     //   535: aload 11
/*     */     //   537: ldc_w 321
/*     */     //   540: invokevirtual 267	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   543: pop
/*     */     //   544: aload 11
/*     */     //   546: invokevirtual 322	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   549: areturn
/*     */     // Line number table:
/*     */     //   Java source line #131	-> byte code offset #0
/*     */     //   Java source line #133	-> byte code offset #6
/*     */     //   Java source line #135	-> byte code offset #12
/*     */     //   Java source line #139	-> byte code offset #18
/*     */     //   Java source line #140	-> byte code offset #30
/*     */     //   Java source line #142	-> byte code offset #42
/*     */     //   Java source line #144	-> byte code offset #51
/*     */     //   Java source line #145	-> byte code offset #60
/*     */     //   Java source line #146	-> byte code offset #69
/*     */     //   Java source line #148	-> byte code offset #102
/*     */     //   Java source line #150	-> byte code offset #134
/*     */     //   Java source line #151	-> byte code offset #206
/*     */     //   Java source line #152	-> byte code offset #237
/*     */     //   Java source line #150	-> byte code offset #259
/*     */     //   Java source line #155	-> byte code offset #263
/*     */     //   Java source line #156	-> byte code offset #270
/*     */     //   Java source line #155	-> byte code offset #286
/*     */     //   Java source line #159	-> byte code offset #290
/*     */     //   Java source line #160	-> byte code offset #297
/*     */     //   Java source line #159	-> byte code offset #313
/*     */     //   Java source line #171	-> byte code offset #317
/*     */     //   Java source line #172	-> byte code offset #362
/*     */     //   Java source line #173	-> byte code offset #391
/*     */     //   Java source line #171	-> byte code offset #413
/*     */     //   Java source line #176	-> byte code offset #417
/*     */     //   Java source line #177	-> byte code offset #484
/*     */     //   Java source line #176	-> byte code offset #497
/*     */     //   Java source line #181	-> byte code offset #498
/*     */     //   Java source line #183	-> byte code offset #507
/*     */     //   Java source line #184	-> byte code offset #511
/*     */     //   Java source line #185	-> byte code offset #520
/*     */     //   Java source line #183	-> byte code offset #531
/*     */     //   Java source line #188	-> byte code offset #535
/*     */     //   Java source line #189	-> byte code offset #544
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	550	0	this	SparkEngineV3ForSearchCarByPic
/*     */     //   0	550	1	map	InputBean
/*     */     //   0	550	2	tableName	String
/*     */     //   0	550	3	similarityLimit	boolean
/*     */     //   6	543	4	feature	String
/*     */     //   12	537	5	carBrand	String
/*     */     //   18	531	6	carModel	String[]
/*     */     //   30	519	7	startTime	long
/*     */     //   42	507	9	endTime	long
/*     */     //   51	498	11	sb	StringBuffer
/*     */     //   237	19	14	m	String
/*     */     //   391	19	16	m	String
/*     */   }
/*     */   
/*     */   public void SearchCarByPic2(SparkSession sqlContext, String line, String tableName, String zkHostport)
/*     */   {
/* 192 */     long now1 = new Date().getTime();
/* 193 */     String[] line_arr = line.split("\\|");
/* 194 */     String job_id = line_arr[1];
/* 195 */     String[] line2 = line_arr[2].split(",");
/*     */     
/* 197 */     com.google.gson.Gson gson = new com.google.gson.Gson();
/* 198 */     java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken() {}.getType();
/* 199 */     InputBean map = (InputBean)gson.fromJson(line_arr[2], mapType);
/*     */     
/*     */ 
/* 202 */     String sql1 = getSQL(map, tableName, true);
/*     */     
/*     */ 
/* 205 */     int tempCount = executeSparkSQLNoPartition(sqlContext, sql1, zkHostport, job_id);
/*     */     
/* 207 */     long insertMySQLEnd = new Date().getTime();
/*     */     
/* 209 */     Predef..MODULE$.println(new StringBuilder().append("have no result first time ,set similarityLimit false ,run again , tempCount :").append(BoxesRunTime.boxToInteger(tempCount)).toString());
/* 210 */     String sql2 = getSQL(map, tableName, false);
/*     */     
/*     */ 
/*     */ 
/* 214 */     long secondTime = new Date().getTime();
/* 215 */     Predef..MODULE$.println(new StringBuilder().append("have  result or not , time :").append(BoxesRunTime.boxToLong(secondTime - insertMySQLEnd)).toString());
/*     */   }
/*     */   
/* 218 */   public long getvalue(String text, final String test2) { final byte[] byteData = java.util.Base64.getDecoder().decode(text);
/* 219 */     final byte[] oldData = java.util.Base64.getDecoder().decode(test2);
/* 220 */     final LongRef num = LongRef.create(0L);scala.runtime.RichInt..MODULE$
/* 221 */       .until$extension0(Predef..MODULE$.intWrapper(0), byteData.length).withFilter(new scala.runtime.AbstractFunction1.mcZI.sp() { public boolean apply$mcZI$sp(int i) { return test2.length() > 30; } public final boolean apply(int i) { return apply$mcZI$sp(i); } }).foreach(new scala.runtime.AbstractFunction1.mcVI.sp() { public final void apply(int i) { apply$mcVI$sp(i); }
/* 222 */         public void apply$mcVI$sp(int i) { int n = (byteData[i] & 0xFF) - (oldData[i] & 0xFF);
/* 223 */           num.elem += n * n;
/* 224 */         } });
/* 225 */     return num.elem;
/*     */   }
/*     */   
/*     */   public static final long serialVersionUID = 0L;
/*     */   public static final long serialVersionUID = 0L;
/*     */   public SparkEngineV3ForSearchCarByPic(SparkSession sqlContext, String line, String tableName, String zkHostport) {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\branchV3\SparkEngineV3ForSearchCarByPic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */