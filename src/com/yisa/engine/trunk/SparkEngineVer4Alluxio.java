package com.yisa.engine.trunk;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001-<Q!\001\002\t\002-\tac\0259be.,enZ5oKZ+'\017N!mYVD\030n\034\006\003\007\021\tQ\001\036:v].T!!\002\004\002\r\025tw-\0338f\025\t9\001\"\001\003zSN\f'\"A\005\002\007\r|Wn\001\001\021\0051iQ\"\001\002\007\0139\021\001\022A\b\003-M\003\030M]6F]\036Lg.\032,feR\nE\016\\;yS>\034\"!\004\t\021\005E!R\"\001\n\013\003M\tQa]2bY\006L!!\006\n\003\r\005s\027PU3g\021\0259R\002\"\001\031\003\031a\024N\\5u}Q\t1\002C\003\033\033\021\0051$\001\003nC&tGC\001\017 !\t\tR$\003\002\037%\t!QK\\5u\021\025\001\023\0041\001\"\003\021\t'oZ:\021\007E\021C%\003\002$%\t)\021I\035:bsB\021Q\005\013\b\003#\031J!a\n\n\002\rA\023X\rZ3g\023\tI#F\001\004TiJLgn\032\006\003OIAQ\001L\007\005\0025\nq\002\\8bI\006cG\016^5nK\022\013G/\031\013\006]u\022EI\022\t\004_aRT\"\001\031\013\005E\022\024aA:rY*\0211\007N\001\006gB\f'o\033\006\003kY\na!\0319bG\",'\"A\034\002\007=\024x-\003\002:a\t9A)\031;bg\026$\bCA\030<\023\ta\004GA\002S_^DQAP\026A\002}\nAb\0359be.\034Vm]:j_:\004\"a\f!\n\005\005\003$\001D*qCJ\\7+Z:tS>t\007\"B\",\001\004!\023\001\0035eMN\004\026\r\0365\t\013\025[\003\031\001\023\002\021\021\fG/\031)bi\"DQaR\026A\002!\013\021bY1dQ\026$\025-_:\021\005EI\025B\001&\023\005\rIe\016\036\005\006\0316!\t!T\001\021Y>\fGMU3bYRLW.\032#bi\006$RA\f(P!FCQAP&A\002}BQaQ&A\002\021BQ!R&A\002\021BQaR&A\002!CQaU\007\005\002Q\013!bZ3u\t\006$X-T5e)\005)\006CA\tW\023\t9&C\001\003M_:<\007\"B-\016\t\003Q\026\001\004:v]\032K'o\035;UCN\\G#\002\017\\9z\003\007\"\002 Y\001\004y\004\"B/Y\001\004!\023!\003;bE2,g*Y7f\021\025y\006\f1\001%\003q1'/Z9vK:$H._\"beJ+7/\0367u)\006\024G.\032(b[\026DQ!\031-A\002\021\n!B_6I_N$\bk\034:u\021\025\031W\002\"\001e\003-\021XmZ5ti\026\024X\013\022$\025\005q)\007\"\002 c\001\004y\004\"B4\016\t\003A\027aE4fi2\033\025m\0315f\t\006$\030\rR1uK&$GC\001\023j\021\025Qg\r1\001I\003\021!\027-_:")
public final class SparkEngineVer4Alluxio
{
  public static String getLCacheDataDateid(int paramInt)
  {
    return SparkEngineVer4Alluxio..MODULE$.getLCacheDataDateid(paramInt);
  }
  
  public static void registerUDF(SparkSession paramSparkSession)
  {
    SparkEngineVer4Alluxio..MODULE$.registerUDF(paramSparkSession);
  }
  
  public static void runFirstTask(SparkSession paramSparkSession, String paramString1, String paramString2, String paramString3)
  {
    SparkEngineVer4Alluxio..MODULE$.runFirstTask(paramSparkSession, paramString1, paramString2, paramString3);
  }
  
  public static long getDateMid()
  {
    return SparkEngineVer4Alluxio..MODULE$.getDateMid();
  }
  
  public static Dataset<Row> loadRealtimeData(SparkSession paramSparkSession, String paramString1, String paramString2, int paramInt)
  {
    return SparkEngineVer4Alluxio..MODULE$.loadRealtimeData(paramSparkSession, paramString1, paramString2, paramInt);
  }
  
  public static Dataset<Row> loadAlltimeData(SparkSession paramSparkSession, String paramString1, String paramString2, int paramInt)
  {
    return SparkEngineVer4Alluxio..MODULE$.loadAlltimeData(paramSparkSession, paramString1, paramString2, paramInt);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    SparkEngineVer4Alluxio..MODULE$.main(paramArrayOfString);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\trunk\SparkEngineVer4Alluxio.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */