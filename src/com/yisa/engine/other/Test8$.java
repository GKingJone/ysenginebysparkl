/*    */ package com.yisa.engine.other;
/*    */ 
/*    */ import scala.reflect.api.Mirror;
/*    */ 
/*    */ public final class Test8$ { public static final  MODULE$;
/*    */   
/*    */   static { new ();
/*    */   }
/*    */   public static final long serialVersionUID = 0L;
/*    */   
/* 11 */   public void main(String[] args) { long now = new java.util.Date().getTime();
/*    */     
/* 13 */     org.apache.spark.SparkConf sprakConf = new org.apache.spark.SparkConf().setAppName("test").setMaster("local").set("spark.sql.warehouse.dir", "file:///D:/spark-warehouse");
/* 14 */     org.apache.spark.SparkContext sc = new org.apache.spark.SparkContext(sprakConf);
/*    */     
/* 16 */     org.apache.spark.sql.SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
/*    */     
/*    */ 
/* 19 */     org.apache.spark.SparkContext qual$1 = sc;String x$2 = "E://moma//people.txt";int x$3 = qual$1.textFile$default$2();scala.reflect.api.JavaUniverse $u = ;; { public final String[] apply(String x$1) { return x$1.split(","); } }, scala.reflect.ClassTag..MODULE$.apply(scala.runtime.ScalaRunTime..MODULE$.arrayClass(String.class))).map(new scala.runtime.AbstractFunction1() { public final Test8.Person apply(String[] p) { return new Test8.Person(p[0], new scala.collection.immutable.StringOps(scala.Predef..MODULE$.augmentString(p[1].trim())).toInt()); } }, scala.reflect.ClassTag..MODULE$.apply(Test8.Person.class)), sqlContext.implicits().newProductEncoder(((scala.reflect.api.TypeTags)$u).TypeTag().apply((Mirror)$m, new scala.reflect.api.TypeCreator() { public <U extends scala.reflect.api.Universe, > scala.reflect.api.Types.TypeApi apply(Mirror<U> $m$untyped) { scala.reflect.api.Universe $u = $m$untyped.universe();Mirror $m = $m$untyped;return $m.staticClass("com.yisa.engine.other.Test8.Person").asType().toTypeConstructor();
/*    */       }
/* 21 */     }))).toDF();
/* 22 */     parquetFile.registerTempTable("people");
/*    */     
/* 24 */     scala.reflect.api.JavaUniverse $u = ;;;; { public final int apply(String text, String test2) { return 2; } }, ((scala.reflect.api.TypeTags)scala.reflect.runtime.package..MODULE$.universe()).TypeTag().Int(), ((scala.reflect.api.TypeTags)$u).TypeTag().apply((Mirror)$m, new scala.reflect.api.TypeCreator() { public <U extends scala.reflect.api.Universe, > scala.reflect.api.Types.TypeApi apply(Mirror<U> $m$untyped) { scala.reflect.api.Universe $u = $m$untyped.universe();Mirror $m = $m$untyped;return $u.internal().reificationSupport().TypeRef($u.internal().reificationSupport().SingleType($u.internal().reificationSupport().ThisType($m.staticPackage("scala").asModule().moduleClass()), $m.staticModule("scala.Predef")), $u.internal().reificationSupport().selectType($m.staticModule("scala.Predef").asModule().moduleClass(), "String"), scala.collection.immutable.Nil..MODULE$); } }), ((scala.reflect.api.TypeTags)$u).TypeTag().apply((Mirror)$m, new scala.reflect.api.TypeCreator() { public static final long serialVersionUID = 0L; public <U extends scala.reflect.api.Universe, > scala.reflect.api.Types.TypeApi apply(Mirror<U> $m$untyped) { scala.reflect.api.Universe $u = $m$untyped.universe();Mirror $m = $m$untyped;return $u.internal().reificationSupport().TypeRef($u.internal().reificationSupport().SingleType($u.internal().reificationSupport().ThisType($m.staticPackage("scala").asModule().moduleClass()), $m.staticModule("scala.Predef")), $u.internal().reificationSupport().selectType($m.staticModule("scala.Predef").asModule().moduleClass(), "String"), scala.collection.immutable.Nil..MODULE$);
/* 25 */       } }));
/* 26 */     parquetFile.show();
/*    */     
/* 28 */     org.apache.spark.sql.Dataset teenagers = sqlContext.sql("SELECT name, getlen('bb','aa') FROM people WHERE age >= 13 AND age <= 19");
/*    */     
/* 30 */     scala.Predef..MODULE$.refArrayOps((Object[])teenagers.map(new scala.runtime.AbstractFunction1() { public final String apply(org.apache.spark.sql.Row t) { return new scala.collection.mutable.StringBuilder().append("Name: ").append(t.apply(0)).append(" len: ").append(t.apply(1)).toString(); } }, sqlContext.implicits().newStringEncoder()).collect()).foreach(new scala.runtime.AbstractFunction1() { public static final long serialVersionUID = 0L; public final void apply(Object x) { scala.Predef..MODULE$.println(x); } }); }
/*    */   
/* 32 */   private Test8$() { MODULE$ = this; }
/*    */   
/*    */   public static final long serialVersionUID = 0L;
/*    */   public static final long serialVersionUID = 0L;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\other\Test8$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */