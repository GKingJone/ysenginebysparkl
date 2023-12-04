/*    */ package com.yisa.engine.spark.udf;
/*    */ 
/*    */ import org.apache.spark.sql.Row;
/*    */ import org.apache.spark.sql.expressions.MutableAggregationBuffer;
/*    */ import org.apache.spark.sql.types.DataType;
/*    */ import org.apache.spark.sql.types.LongType.;
/*    */ import org.apache.spark.sql.types.StringType.;
/*    */ import org.apache.spark.sql.types.StructField;
/*    */ import org.apache.spark.sql.types.StructField.;
/*    */ import org.apache.spark.sql.types.StructType;
/*    */ import org.apache.spark.sql.types.StructType.;
/*    */ import scala.collection.immutable.List;
/*    */ import scala.collection.immutable.Nil.;
/*    */ import scala.runtime.BoxesRunTime;
/*    */ 
/*    */ @scala.reflect.ScalaSignature(bytes="\006\001q3A!\001\002\001\033\tIr-\032;T_2\024\030\016\032\"z\033\006D8)\0319ukJ,G/[7f\025\t\031A!A\002vI\032T!!\002\004\002\013M\004\030M]6\013\005\035A\021AB3oO&tWM\003\002\n\025\005!\0210[:b\025\005Y\021aA2p[\016\0011C\001\001\017!\ty\021$D\001\021\025\t\t\"#A\006fqB\024Xm]:j_:\034(BA\n\025\003\r\031\030\017\034\006\003\013UQ!AF\f\002\r\005\004\030m\0315f\025\005A\022aA8sO&\021!\004\005\002\035+N,'\017R3gS:,G-Q4he\026<\027\r^3Gk:\034G/[8o\021\025a\002\001\"\001\036\003\031a\024N\\5u}Q\ta\004\005\002 \0015\t!\001C\003\"\001\021\005#%A\006j]B,HoU2iK6\fW#A\022\021\005\021:S\"A\023\013\005\031\022\022!\002;za\026\034\030B\001\025&\005)\031FO];diRK\b/\032\005\006U\001!\tEI\001\rEV4g-\032:TG\",W.\031\005\006Y\001!\t%L\001\tI\006$\030\rV=qKV\ta\006\005\002%_%\021\001'\n\002\t\t\006$\030\rV=qK\")!\007\001C!g\005iA-\032;fe6Lg.[:uS\016,\022\001\016\t\003kaj\021A\016\006\002o\005)1oY1mC&\021\021H\016\002\b\005>|G.Z1o\021\025Y\004\001\"\021=\003)Ig.\033;jC2L'0\032\013\003{\001\003\"!\016 \n\005}2$\001B+oSRDQ!\021\036A\002\t\013aAY;gM\026\024\bCA\bD\023\t!\005C\001\rNkR\f'\r\\3BO\036\024XmZ1uS>t')\0364gKJDQA\022\001\005B\035\013a!\0369eCR,GcA\037I\023\")\021)\022a\001\005\")!*\022a\001\027\006)\021N\0349viB\021A*T\007\002%%\021aJ\005\002\004%><\b\"\002)\001\t\003\n\026!B7fe\036,GcA\037S)\")1k\024a\001\005\0069!-\0364gKJ\f\004\"\002&P\001\004Y\005\"\002,\001\t\003:\026\001C3wC2,\030\r^3\025\005a[\006CA\033Z\023\tQfGA\002B]fDQ!Q+A\002-\003")
/*    */ public class getSolridByMaxCapturetime extends org.apache.spark.sql.expressions.UserDefinedAggregateFunction
/*    */ {
/*    */   public StructType inputSchema()
/*    */   {
/* 21 */     StructField localStructField1 = new StructField("solr", StringType..MODULE$, StructField..MODULE$.apply$default$3(), StructField..MODULE$.apply$default$4());StructField localStructField2 = new StructField("capturetime", LongType..MODULE$, StructField..MODULE$.apply$default$3(), StructField..MODULE$.apply$default$4());return StructType..MODULE$.apply(Nil..MODULE$.$colon$colon(localStructField2).$colon$colon(localStructField1));
/*    */   }
/*    */   
/*    */   public StructType bufferSchema() {
/* 25 */     StructField localStructField1 = new StructField("max_solr", StringType..MODULE$, StructField..MODULE$.apply$default$3(), StructField..MODULE$.apply$default$4());StructField localStructField2 = new StructField("max_capturetime", LongType..MODULE$, StructField..MODULE$.apply$default$3(), StructField..MODULE$.apply$default$4());return StructType..MODULE$.apply(Nil..MODULE$.$colon$colon(localStructField2).$colon$colon(localStructField1));
/*    */   }
/*    */   
/* 28 */   public DataType dataType() { return StringType..MODULE$; }
/*    */   
/*    */   public boolean deterministic() {
/* 31 */     return true;
/*    */   }
/*    */   
/*    */   public void initialize(MutableAggregationBuffer buffer)
/*    */   {
/* 36 */     buffer.update(0, "max_solr");
/* 37 */     buffer.update(1, BoxesRunTime.boxToLong(0L));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void update(MutableAggregationBuffer buffer, Row input)
/*    */   {
/* 44 */     if (BoxesRunTime.unboxToLong(input.getAs(1)) > BoxesRunTime.unboxToLong(buffer.getAs(1))) {
/* 45 */       buffer.update(0, input.getAs(0));
/* 46 */       buffer.update(1, input.getAs(1));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void merge(MutableAggregationBuffer buffer1, Row input)
/*    */   {
/* 56 */     if (BoxesRunTime.unboxToLong(input.getAs(1)) > BoxesRunTime.unboxToLong(buffer1.getAs(1))) {
/* 57 */       buffer1.update(0, input.getAs(0));
/* 58 */       buffer1.update(1, input.getAs(1));
/*    */     }
/*    */   }
/*    */   
/*    */   public Object evaluate(Row buffer)
/*    */   {
/* 64 */     return buffer.getAs(0);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\spark\udf\getSolridByMaxCapturetime.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */