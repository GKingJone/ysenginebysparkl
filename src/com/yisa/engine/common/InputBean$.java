/*   */ package com.yisa.engine.common; import scala.runtime.BoxesRunTime;
/*   */ 
/* 3 */ public final class InputBean$ extends scala.runtime.AbstractFunction14<String, String, String[], String[], String, String[], String, String, Object, String, Object, Object, String[], String, InputBean> implements scala.Serializable { private InputBean$() { MODULE$ = this; } private Object readResolve() { return MODULE$; } public scala.Option<scala.Tuple14<String, String, String[], String[], String, String[], String, String, Object, String, Object, Object, String[], String>> unapply(InputBean x$0) { return x$0 == null ? scala.None..MODULE$ : new scala.Some(new scala.Tuple14(x$0.startTime(), x$0.endTime(), x$0.locationId(), x$0.carModel(), x$0.carBrand(), x$0.carYear(), x$0.carColor(), x$0.plateNumber(), BoxesRunTime.boxToInteger(x$0.count()), x$0.feature(), BoxesRunTime.boxToInteger(x$0.differ()), BoxesRunTime.boxToInteger(x$0.isRepair()), x$0.carLevel(), x$0.direction())); } public InputBean apply(String startTime, String endTime, String[] locationId, String[] carModel, String carBrand, String[] carYear, String carColor, String plateNumber, int count, String feature, int differ, int isRepair, String[] carLevel, String direction) { return new InputBean(startTime, endTime, locationId, carModel, carBrand, carYear, carColor, plateNumber, count, feature, differ, isRepair, carLevel, direction); } public final String toString() { return "InputBean"; }
/*   */   
/*   */   public static final  MODULE$;
/*   */   static
/*   */   {
/*   */     new ();
/*   */   }
/*   */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\common\InputBean$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */