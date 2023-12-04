package com.yisa.engine.db;

import java.sql.Connection;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001\001;Q!\001\002\t\002-\t1#T=T#2\033uN\0348fGRl\025M\\1hKJT!a\001\003\002\005\021\024'BA\003\007\003\031)gnZ5oK*\021q\001C\001\005s&\034\030MC\001\n\003\r\031w.\\\002\001!\taQ\"D\001\003\r\025q!\001#\001\020\005Mi\025pU)M\007>tg.Z2u\033\006t\027mZ3s'\ri\001C\006\t\003#Qi\021A\005\006\002'\005)1oY1mC&\021QC\005\002\007\003:L(+\0324\021\005E9\022B\001\r\023\0051\031VM]5bY&T\030M\0317f\021\025QR\002\"\001\034\003\031a\024N\\5u}Q\t1\002C\003\036\033\021\005a$A\005hKR\034uN\0348fiR\021qd\n\t\003A\025j\021!\t\006\003E\r\n1a]9m\025\005!\023\001\0026bm\006L!AJ\021\003\025\r{gN\\3di&|g\016C\003)9\001\007\021&\001\006{W\"{7\017\0369peR\004\"AK\027\017\005EY\023B\001\027\023\003\031\001&/\0323fM&\021af\f\002\007'R\024\030N\\4\013\0051\022\002\"B\031\016\t\003\021\024AE5oSR\034uN\0348fGRLwN\034)p_2$\"a\r\034\021\005E!\024BA\033\023\005\021)f.\033;\t\013!\002\004\031A\025\t\017aj\021\021!C\005s\005Y!/Z1e%\026\034x\016\034<f)\005Q\004CA\036?\033\005a$BA\037$\003\021a\027M\\4\n\005}b$AB(cU\026\034G\017")
public final class MySQLConnectManager
{
  public static void initConnectionPool(String paramString)
  {
    MySQLConnectManager..MODULE$.initConnectionPool(paramString);
  }
  
  public static Connection getConnet(String paramString)
  {
    return MySQLConnectManager..MODULE$.getConnet(paramString);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\db\MySQLConnectManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */