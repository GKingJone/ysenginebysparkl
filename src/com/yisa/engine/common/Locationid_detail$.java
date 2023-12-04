/*   */ package com.yisa.engine.common; import scala.Tuple2;
/*   */ 
/* 3 */ public final class Locationid_detail$ extends scala.runtime.AbstractFunction2<String, Object, Locationid_detail> implements scala.Serializable { private Locationid_detail$() { MODULE$ = this; } private Object readResolve() { return MODULE$; } public scala.Option<Tuple2<String, Object>> unapply(Locationid_detail x$0) { return x$0 == null ? scala.None..MODULE$ : new scala.Some(new Tuple2(x$0.locationid(), scala.runtime.BoxesRunTime.boxToInteger(x$0.times()))); } public Locationid_detail apply(String locationid, int times) { return new Locationid_detail(locationid, times); } public final String toString() { return "Locationid_detail"; }
/*   */   
/*   */   public static final  MODULE$;
/*   */   static
/*   */   {
/*   */     new ();
/*   */   }
/*   */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\common\Locationid_detail$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */