package com.yisa.engine.db;

import java.sql.Connection;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001\001;Q!\001\002\t\002-\tA\003\025:fgR|7i\0348oK\016$X*\0318bO\026\024(BA\002\005\003\t!'M\003\002\006\r\0051QM\\4j]\026T!a\002\005\002\teL7/\031\006\002\023\005\0311m\\7\004\001A\021A\"D\007\002\005\031)aB\001E\001\037\t!\002K]3ti>\034uN\0348fGRl\025M\\1hKJ\0342!\004\t\027!\t\tB#D\001\023\025\005\031\022!B:dC2\f\027BA\013\023\005\031\te.\037*fMB\021\021cF\005\0031I\021AbU3sS\006d\027N_1cY\026DQAG\007\005\002m\ta\001P5oSRtD#A\006\t\013uiA\021\001\020\002\023\035,GoQ8o]\026$HCA\020(!\t\001S%D\001\"\025\t\0213%A\002tc2T\021\001J\001\005U\0064\030-\003\002'C\tQ1i\0348oK\016$\030n\0348\t\013!b\002\031A\025\002\025i\\\007j\\:ua>\024H\017\005\002+[9\021\021cK\005\003YI\ta\001\025:fI\0264\027B\001\0300\005\031\031FO]5oO*\021AF\005\005\006c5!\tAM\001\023S:LGoQ8o]\026\034G/[8o!>|G\016\006\0024mA\021\021\003N\005\003kI\021A!\0268ji\")\001\006\ra\001S!9\001(DA\001\n\023I\024a\003:fC\022\024Vm]8mm\026$\022A\017\t\003wyj\021\001\020\006\003{\r\nA\001\\1oO&\021q\b\020\002\007\037\nTWm\031;")
public final class PrestoConnectManager
{
  public static void initConnectionPool(String paramString)
  {
    PrestoConnectManager..MODULE$.initConnectionPool(paramString);
  }
  
  public static Connection getConnet(String paramString)
  {
    return PrestoConnectManager..MODULE$.getConnet(paramString);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\db\PrestoConnectManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */