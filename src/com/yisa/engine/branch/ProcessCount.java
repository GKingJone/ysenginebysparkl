package com.yisa.engine.branch;

import org.apache.spark.SparkContext;
import org.apache.spark.util.LongAccumulator;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes="\006\001}:Q!\001\002\t\002-\tA\002\025:pG\026\0348oQ8v]RT!a\001\003\002\r\t\024\030M\\2i\025\t)a!\001\004f]\036Lg.\032\006\003\017!\tA!_5tC*\t\021\"A\002d_6\034\001\001\005\002\r\0335\t!AB\003\017\005!\005qB\001\007Qe>\034Wm]:D_VtGo\005\002\016!A\021\021\003F\007\002%)\t1#A\003tG\006d\027-\003\002\026%\t1\021I\\=SK\032DQaF\007\005\002a\ta\001P5oSRtD#A\006\t\017ii\001\031!C\0057\005A\021N\\:uC:\034W-F\001\035!\tib%D\001\037\025\ty\002%\001\003vi&d'BA\021#\003\025\031\b/\031:l\025\t\031C%\001\004ba\006\034\007.\032\006\002K\005\031qN]4\n\005\035r\"a\004'p]\036\f5mY;nk2\fGo\034:\t\017%j\001\031!C\005U\005a\021N\\:uC:\034Wm\030\023fcR\0211F\f\t\003#1J!!\f\n\003\tUs\027\016\036\005\b_!\n\t\0211\001\035\003\rAH%\r\005\007c5\001\013\025\002\017\002\023%t7\017^1oG\026\004\003F\001\0314!\t\tB'\003\0026%\tAao\0347bi&dW\rC\0038\033\021\005\001(A\006hKRLen\035;b]\016,GC\001\017:\021\025Qd\0071\001<\003\t\0318\r\005\002={5\t\001%\003\002?A\ta1\013]1sW\016{g\016^3yi\002")
public final class ProcessCount
{
  public static LongAccumulator getInstance(SparkContext paramSparkContext)
  {
    return ProcessCount..MODULE$.getInstance(paramSparkContext);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\branch\ProcessCount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */