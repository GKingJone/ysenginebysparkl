package com.yisa.engine.trunk;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001-<Q!\001\002\t\002-\t\001c\0259be.,enZ5oKZ+'OM\031\013\005\r!\021!\002;sk:\\'BA\003\007\003\031)gnZ5oK*\021q\001C\001\005s&\034\030MC\001\n\003\r\031w.\\\002\001!\taQ\"D\001\003\r\025q!\001#\001\020\005A\031\006/\031:l\013:<\027N\\3WKJ\024\024g\005\002\016!A\021\021\003F\007\002%)\t1#A\003tG\006d\027-\003\002\026%\t1\021I\\=SK\032DQaF\007\005\002a\ta\001P5oSRtD#A\006\t\013iiA\021A\016\002\t5\f\027N\034\013\0039}\001\"!E\017\n\005y\021\"\001B+oSRDQ\001I\rA\002\005\nA!\031:hgB\031\021C\t\023\n\005\r\022\"!B!se\006L\bCA\023)\035\t\tb%\003\002(%\0051\001K]3eK\032L!!\013\026\003\rM#(/\0338h\025\t9#\003C\003-\033\021\005Q&A\bm_\006$\027\t\0347uS6,G)\031;b)\025qSH\021#G!\ry\003HO\007\002a)\021\021GM\001\004gFd'BA\0325\003\025\031\b/\031:l\025\t)d'\001\004ba\006\034\007.\032\006\002o\005\031qN]4\n\005e\002$a\002#bi\006\034X\r\036\t\003_mJ!\001\020\031\003\007I{w\017C\003?W\001\007q(\001\007ta\006\0248nU3tg&|g\016\005\0020\001&\021\021\t\r\002\r'B\f'o[*fgNLwN\034\005\006\007.\002\r\001J\001\tQ\02247\017U1uQ\")Qi\013a\001I\005AA-\031;b!\006$\b\016C\003HW\001\007\001*A\005dC\016DW\rR1zgB\021\021#S\005\003\025J\0211!\0238u\021\025aU\002\"\001N\003Aaw.\0313SK\006dG/[7f\t\006$\030\rF\003/\035>\003\026\013C\003?\027\002\007q\bC\003D\027\002\007A\005C\003F\027\002\007A\005C\003H\027\002\007\001\nC\003T\033\021\005A+\001\006hKR$\025\r^3NS\022$\022!\026\t\003#YK!a\026\n\003\t1{gn\032\005\00636!\tAW\001\reVtg)\033:tiR\0137o\033\013\0069mcf\f\031\005\006}a\003\ra\020\005\006;b\003\r\001J\001\ni\006\024G.\032(b[\026DQa\030-A\002\021\nAD\032:fcV,g\016\0367z\007\006\024(+Z:vYR$\026M\0317f\035\006lW\rC\003b1\002\007A%\001\006{W\"{7\017\036)peRDQaY\007\005\002\021\f1B]3hSN$XM]+E\rR\021A$\032\005\006}\t\004\ra\020\005\006O6!\t\001[\001\024O\026$HjQ1dQ\026$\025\r^1ECR,\027\016\032\013\003I%DQA\0334A\002!\013A\001Z1zg\002")
public final class SparkEngineVer21
{
  public static String getLCacheDataDateid(int paramInt)
  {
    return SparkEngineVer21..MODULE$.getLCacheDataDateid(paramInt);
  }
  
  public static void registerUDF(SparkSession paramSparkSession)
  {
    SparkEngineVer21..MODULE$.registerUDF(paramSparkSession);
  }
  
  public static void runFirstTask(SparkSession paramSparkSession, String paramString1, String paramString2, String paramString3)
  {
    SparkEngineVer21..MODULE$.runFirstTask(paramSparkSession, paramString1, paramString2, paramString3);
  }
  
  public static long getDateMid()
  {
    return SparkEngineVer21..MODULE$.getDateMid();
  }
  
  public static Dataset<Row> loadRealtimeData(SparkSession paramSparkSession, String paramString1, String paramString2, int paramInt)
  {
    return SparkEngineVer21..MODULE$.loadRealtimeData(paramSparkSession, paramString1, paramString2, paramInt);
  }
  
  public static Dataset<Row> loadAlltimeData(SparkSession paramSparkSession, String paramString1, String paramString2, int paramInt)
  {
    return SparkEngineVer21..MODULE$.loadAlltimeData(paramSparkSession, paramString1, paramString2, paramInt);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    SparkEngineVer21..MODULE$.main(paramArrayOfString);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\trunk\SparkEngineVer21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */