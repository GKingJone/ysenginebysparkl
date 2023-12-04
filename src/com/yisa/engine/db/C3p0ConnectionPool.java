package com.yisa.engine.db;

import java.sql.Connection;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001a;a!\001\002\t\002\tQ\021AE\"4aB\032uN\0348fGRLwN\034)p_2T!a\001\003\002\005\021\024'BA\003\007\003\031)gnZ5oK*\021q\001C\001\005s&\034\030MC\001\n\003\r\031w.\034\t\003\0271i\021A\001\004\007\033\tA\tA\001\b\003%\r\033\004\017M\"p]:,7\r^5p]B{w\016\\\n\004\031=)\002C\001\t\024\033\005\t\"\"\001\n\002\013M\034\027\r\\1\n\005Q\t\"AB!osJ+g\r\005\002\021-%\021q#\005\002\r'\026\024\030.\0317ju\006\024G.\032\005\00631!\taG\001\007y%t\027\016\036 \004\001Q\t!\002C\004\036\031\001\007I\021\002\020\002\005\021\034X#A\020\021\005\001:S\"A\021\013\005\t\032\023\001B24aBR!\001J\023\002\005Y\024$B\001\024\t\003\035i7\r[1oO\026L!\001K\021\003+\r{WNY8Q_>dW\r\032#bi\006\034v.\036:dK\"9!\006\004a\001\n\023Y\023A\0023t?\022*\027\017\006\002-_A\021\001#L\005\003]E\021A!\0268ji\"9\001'KA\001\002\004y\022a\001=%c!1!\007\004Q!\n}\t1\001Z:!Q\t\tD\007\005\002\021k%\021a'\005\002\tm>d\027\r^5mK\")\001\b\004C\001s\005\021\022N\\5u\007>tg.Z2uS>t\007k\\8m)\ta#\bC\003<o\001\007A(\001\006{W\"{7\017\0369peR\004\"!\020!\017\005Aq\024BA \022\003\031\001&/\0323fM&\021\021I\021\002\007'R\024\030N\\4\013\005}\n\002\"\002#\r\t\003)\025!C4fi\016{gN\\3u)\t1e\n\005\002H\0316\t\001J\003\002J\025\006\0311/\0357\013\003-\013AA[1wC&\021Q\n\023\002\013\007>tg.Z2uS>t\007\"B\036D\001\004a\004b\002)\r\003\003%I!U\001\fe\026\fGMU3t_24X\rF\001S!\t\031f+D\001U\025\t)&*\001\003mC:<\027BA,U\005\031y%M[3di\002")
public final class C3p0ConnectionPool
{
  public static Connection getConnet(String paramString)
  {
    return C3p0ConnectionPool..MODULE$.getConnet(paramString);
  }
  
  public static void initConnectionPool(String paramString)
  {
    C3p0ConnectionPool..MODULE$.initConnectionPool(paramString);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\db\C3p0ConnectionPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */