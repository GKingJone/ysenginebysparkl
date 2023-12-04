package com.yisa.engine.modules;

import java.util.concurrent.ExecutorService;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001\005\025s!B\001\003\021\003Y\021AG*qCJ\\WI\\4j]\026lu\016Z;mKN4uN\035,feJ\n$BA\002\005\003\035iw\016Z;mKNT!!\002\004\002\r\025tw-\0338f\025\t9\001\"\001\003zSN\f'\"A\005\002\007\r|Wn\001\001\021\0051iQ\"\001\002\007\0139\021\001\022A\b\0035M\003\030M]6F]\036Lg.Z'pIVdWm\035$peZ+'OM\031\024\0055\001\002CA\t\025\033\005\021\"\"A\n\002\013M\034\027\r\\1\n\005U\021\"AB!osJ+g\rC\003\030\033\021\005\001$\001\004=S:LGO\020\013\002\027!)!$\004C\0017\005iaI]3rk\026tG\017\\=DCJ$\"\002H\020)ma\"e\t\023&P!\t\tR$\003\002\037%\t!QK\\5u\021\025\001\023\0041\001\"\003\021a\027N\\3\021\005\t*cBA\t$\023\t!##\001\004Qe\026$WMZ\005\003M\035\022aa\025;sS:<'B\001\023\023\021\025I\023\0041\001+\0031\031\b/\031:l'\026\0348/[8o!\tYC'D\001-\025\tic&A\002tc2T!a\f\031\002\013M\004\030M]6\013\005E\022\024AB1qC\016DWMC\0014\003\ry'oZ\005\003k1\022Ab\0259be.\034Vm]:j_:DQaN\rA\002\005\n\021\002^1cY\026t\025-\\3\t\013eJ\002\031\001\036\002\025QD'/Z1e!>|G\016\005\002<\0056\tAH\003\002>}\005Q1m\0348dkJ\024XM\034;\013\005}\002\025\001B;uS2T\021!Q\001\005U\0064\030-\003\002Dy\tyQ\t_3dkR|'oU3sm&\034W\rC\003F3\001\007\021%A\bqe\026\034Ho\034+bE2,g*Y7f\021\0259\025\0041\001\"\003q1'/Z9vK:$H._\"beJ+7/\0367u)\006\024G.\032(b[\026DQ!S\rA\002\005\n!B_6I_N$\bk\034:u\021\025Y\025\0041\001M\003%\031\027m\0315f\t\006L8\017\005\002\022\033&\021aJ\005\002\004\023:$\b\"\002)\032\001\004\t\023A\0049sKN$x\016S8tiB{'\017\036\005\006%6!\taU\001\017'\026\f'o\0315DCJ\024\025\020U5d))aB+\026,X1f[F,\030\005\006AE\003\r!\t\005\006SE\003\rA\013\005\006oE\003\r!\t\005\006\023F\003\r!\t\005\006\027F\003\r\001\024\005\0065F\003\r!I\001\ri\006\024G.\032(b[\026\fE\016\034\005\006sE\003\rA\017\005\006!F\003\r!\t\005\006\013F\003\r!\t\005\006?6!\t\001Y\001\023'\026\f'o\0315TS6LG.\031:QY\006$X\rF\006\035C&T7\016\\7o_B\f\b\"\0022_\001\004\031\027!C:qCJ\\G)\031;b!\rYCMZ\005\003K2\022q\001R1uCN,G\017\005\002,O&\021\001\016\f\002\004%><\b\"\002\021_\001\004\t\003\"B\025_\001\004Q\003\"B\035_\001\004Q\004\"B\034_\001\004\t\003\"B#_\001\004\t\003\"B%_\001\004\t\003\"B&_\001\004a\005\"\002._\001\004\t\003\"\002)_\001\004\t\003\"B:\016\t\003!\030AC'vYRL\007k\\5oiRQA$\036<xqfT8\020`?\t\013\t\024\b\031A2\t\013\001\022\b\031A\021\t\013%\022\b\031\001\026\t\013e\022\b\031\001\036\t\013]\022\b\031A\021\t\013\025\023\b\031A\021\t\013%\023\b\031A\021\t\013-\023\b\031\001'\t\013A\023\b\031A\021\t\r}lA\021AA\001\003\035\031\025m]3DCJ$2\003HA\002\003\013\t9!!\003\002\f\0055\021qBA\t\003'AQA\031@A\002\rDQ\001\t@A\002\005BQ!\013@A\002)BQ!\017@A\002iBQa\016@A\002\005BQ!\022@A\002\005BQ!\023@A\002\005BQa\023@A\0021CQ\001\025@A\002\005Bq!a\006\016\t\003\tI\"\001\006F]\022\034F/\031;j_:$2\003HA\016\003;\ty\"!\t\002$\005\025\022qEA\025\003WAaAYA\013\001\004\031\007B\002\021\002\026\001\007\021\005\003\004*\003+\001\rA\013\005\007s\005U\001\031\001\036\t\r]\n)\0021\001\"\021\031)\025Q\003a\001C!1\021*!\006A\002\005BaaSA\013\001\004a\005B\002)\002\026\001\007\021\005C\004\00205!\t!!\r\002\027Q{w-\032;iKJ\034\025M\035\013\n9\005M\022QGA\034\003sAa!KA\027\001\004Q\003B\002\021\002.\001\007\021\005\003\0048\003[\001\r!\t\005\007\023\0065\002\031A\021\t\017\005uR\002\"\001\002@\005\031r-\032;M\007\006\034\007.\032#bi\006$\025\r^3jIR\031\021%!\021\t\017\005\r\0231\ba\001\031\006!A-Y=t\001")
public final class SparkEngineModulesForVer21
{
  public static String getLCacheDataDateid(int paramInt)
  {
    return SparkEngineModulesForVer21..MODULE$.getLCacheDataDateid(paramInt);
  }
  
  public static void TogetherCar(SparkSession paramSparkSession, String paramString1, String paramString2, String paramString3)
  {
    SparkEngineModulesForVer21..MODULE$.TogetherCar(paramSparkSession, paramString1, paramString2, paramString3);
  }
  
  public static void EndStation(Dataset<Row> paramDataset, String paramString1, SparkSession paramSparkSession, ExecutorService paramExecutorService, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5)
  {
    SparkEngineModulesForVer21..MODULE$.EndStation(paramDataset, paramString1, paramSparkSession, paramExecutorService, paramString2, paramString3, paramString4, paramInt, paramString5);
  }
  
  public static void CaseCar(Dataset<Row> paramDataset, String paramString1, SparkSession paramSparkSession, ExecutorService paramExecutorService, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5)
  {
    SparkEngineModulesForVer21..MODULE$.CaseCar(paramDataset, paramString1, paramSparkSession, paramExecutorService, paramString2, paramString3, paramString4, paramInt, paramString5);
  }
  
  public static void MultiPoint(Dataset<Row> paramDataset, String paramString1, SparkSession paramSparkSession, ExecutorService paramExecutorService, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5)
  {
    SparkEngineModulesForVer21..MODULE$.MultiPoint(paramDataset, paramString1, paramSparkSession, paramExecutorService, paramString2, paramString3, paramString4, paramInt, paramString5);
  }
  
  public static void SearchSimilarPlate(Dataset<Row> paramDataset, String paramString1, SparkSession paramSparkSession, ExecutorService paramExecutorService, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5, String paramString6)
  {
    SparkEngineModulesForVer21..MODULE$.SearchSimilarPlate(paramDataset, paramString1, paramSparkSession, paramExecutorService, paramString2, paramString3, paramString4, paramInt, paramString5, paramString6);
  }
  
  public static void SearchCarByPic(String paramString1, SparkSession paramSparkSession, String paramString2, String paramString3, int paramInt, String paramString4, ExecutorService paramExecutorService, String paramString5, String paramString6)
  {
    SparkEngineModulesForVer21..MODULE$.SearchCarByPic(paramString1, paramSparkSession, paramString2, paramString3, paramInt, paramString4, paramExecutorService, paramString5, paramString6);
  }
  
  public static void FrequentlyCar(String paramString1, SparkSession paramSparkSession, String paramString2, ExecutorService paramExecutorService, String paramString3, String paramString4, String paramString5, int paramInt, String paramString6)
  {
    SparkEngineModulesForVer21..MODULE$.FrequentlyCar(paramString1, paramSparkSession, paramString2, paramExecutorService, paramString3, paramString4, paramString5, paramInt, paramString6);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\modules\SparkEngineModulesForVer21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */