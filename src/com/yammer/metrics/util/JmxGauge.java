/*    */ package com.yammer.metrics.util;
/*    */ 
/*    */ import com.yammer.metrics.core.Gauge;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import javax.management.MBeanServer;
/*    */ import javax.management.MalformedObjectNameException;
/*    */ import javax.management.ObjectName;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JmxGauge
/*    */   extends Gauge<Object>
/*    */ {
/* 14 */   private static final MBeanServer SERVER = ;
/*    */   
/*    */ 
/*    */   private final ObjectName objectName;
/*    */   
/*    */ 
/*    */   private final String attribute;
/*    */   
/*    */ 
/*    */ 
/*    */   public JmxGauge(String objectName, String attribute)
/*    */     throws MalformedObjectNameException
/*    */   {
/* 27 */     this(new ObjectName(objectName), attribute);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public JmxGauge(ObjectName objectName, String attribute)
/*    */   {
/* 37 */     this.objectName = objectName;
/* 38 */     this.attribute = attribute;
/*    */   }
/*    */   
/*    */   public Object value()
/*    */   {
/*    */     try {
/* 44 */       return SERVER.getAttribute(this.objectName, this.attribute);
/*    */     } catch (Exception e) {
/* 46 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\util\JmxGauge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */