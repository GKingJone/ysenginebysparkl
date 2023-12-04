package com.yisa.engine.uitl;

import java.sql.Timestamp;
import java.util.Date;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001u;Q!\001\002\t\002-\t\001\002V5nKV#\030\016\034\006\003\007\021\tA!^5uY*\021QAB\001\007K:<\027N\\3\013\005\035A\021\001B=jg\006T\021!C\001\004G>l7\001\001\t\003\0315i\021A\001\004\006\035\tA\ta\004\002\t)&lW-\026;jYN\021Q\002\005\t\003#Qi\021A\005\006\002'\005)1oY1mC&\021QC\005\002\007\003:L(+\0324\t\013]iA\021\001\r\002\rqJg.\033;?)\005Y\001\"\002\016\016\t\003Y\022aE4fi2\033\025m\0315f\t\006$\030\rR1uK&$GC\001\017$!\ti\002E\004\002\022=%\021qDE\001\007!J,G-\0324\n\005\005\022#AB*ue&twM\003\002 %!)A%\007a\001K\005!A-Y=t!\t\tb%\003\002(%\t\031\021J\034;\t\013%jA\021\001\026\002\023\035,G\017R1uK&#G#A\023\t\013%jA\021\001\027\025\005\025j\003\"\002\030,\001\004a\022!\001=\t\013AjA\021A\031\002\023\035,Go\0247e\t\006LHC\001\0173\021\025\031t\0061\001&\003\025!\030.\\3t\021\025)T\002\"\0017\003\0359W\r\036#bi\026$\"aN \021\005ajT\"A\035\013\005iZ\024\001B;uS2T\021\001P\001\005U\0064\030-\003\002?s\t!A)\031;f\021\025qC\0071\001\035\021\025\tU\002\"\001C\003M9W\r\036+j[\026\034FO]5oO\032{'/\\1u)\ta2\tC\003/\001\002\007A\004C\003F\033\021\005a)\001\fhKR$\026.\\3TiJLgn\032$pe6\fG/W'E)\tar\tC\003/\t\002\007A\004C\003J\033\021\005!*\001\007hKR$\026.\\3ti\006l\007\017\006\002L#B\021AjT\007\002\033*\021ajO\001\004gFd\027B\001)N\005%!\026.\\3ti\006l\007\017C\003/\021\002\007A\004C\003T\033\021\005A+\001\thKR$\026.\\3ti\006l\007\017T8oOR\021Q\013\027\t\003#YK!a\026\n\003\t1{gn\032\005\006]I\003\r\001\b\005\00656!\taW\001\033O\026$8\013\036:j]\0364%o\\7US6,7\017^1na2{gn\032\013\0039qCQAL-A\002U\003")
public final class TimeUtil
{
  public static String getStringFromTimestampLong(long paramLong)
  {
    return TimeUtil..MODULE$.getStringFromTimestampLong(paramLong);
  }
  
  public static long getTimestampLong(String paramString)
  {
    return TimeUtil..MODULE$.getTimestampLong(paramString);
  }
  
  public static Timestamp getTimestamp(String paramString)
  {
    return TimeUtil..MODULE$.getTimestamp(paramString);
  }
  
  public static String getTimeStringFormatYMD(String paramString)
  {
    return TimeUtil..MODULE$.getTimeStringFormatYMD(paramString);
  }
  
  public static String getTimeStringFormat(String paramString)
  {
    return TimeUtil..MODULE$.getTimeStringFormat(paramString);
  }
  
  public static Date getDate(String paramString)
  {
    return TimeUtil..MODULE$.getDate(paramString);
  }
  
  public static String getOldDay(int paramInt)
  {
    return TimeUtil..MODULE$.getOldDay(paramInt);
  }
  
  public static int getDateId(String paramString)
  {
    return TimeUtil..MODULE$.getDateId(paramString);
  }
  
  public static int getDateId()
  {
    return TimeUtil..MODULE$.getDateId();
  }
  
  public static String getLCacheDataDateid(int paramInt)
  {
    return TimeUtil..MODULE$.getLCacheDataDateid(paramInt);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\uitl\TimeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */