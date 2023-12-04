/*    */ package com.mchange.v2.management;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import javax.management.MBeanOperationInfo;
/*    */ import javax.management.MBeanParameterInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ManagementUtils
/*    */ {
/* 31 */   public static final Comparator PARAM_INFO_COMPARATOR = new Comparator()
/*    */   {
/*    */     public int compare(Object a, Object b)
/*    */     {
/* 35 */       MBeanParameterInfo aa = (MBeanParameterInfo)a;
/* 36 */       MBeanParameterInfo bb = (MBeanParameterInfo)b;
/* 37 */       int out = aa.getType().compareTo(bb.getType());
/* 38 */       if (out == 0)
/*    */       {
/* 40 */         out = aa.getName().compareTo(bb.getName());
/* 41 */         if (out == 0)
/*    */         {
/* 43 */           String aDesc = aa.getDescription();
/* 44 */           String bDesc = bb.getDescription();
/* 45 */           if ((aDesc == null) && (bDesc == null)) {
/* 46 */             out = 0;
/* 47 */           } else if (aDesc == null) {
/* 48 */             out = -1;
/* 49 */           } else if (bDesc == null) {
/* 50 */             out = 1;
/*    */           } else
/* 52 */             out = aDesc.compareTo(bDesc);
/*    */         }
/*    */       }
/* 55 */       return out;
/*    */     }
/*    */   };
/*    */   
/* 59 */   public static final Comparator OP_INFO_COMPARATOR = new Comparator()
/*    */   {
/*    */     public int compare(Object a, Object b)
/*    */     {
/* 63 */       MBeanOperationInfo aa = (MBeanOperationInfo)a;
/* 64 */       MBeanOperationInfo bb = (MBeanOperationInfo)b;
/* 65 */       String aName = aa.getName();
/* 66 */       String bName = bb.getName();
/* 67 */       int out = String.CASE_INSENSITIVE_ORDER.compare(aName, bName);
/* 68 */       if (out == 0)
/*    */       {
/* 70 */         if (aName.equals(bName))
/*    */         {
/* 72 */           MBeanParameterInfo[] aParams = aa.getSignature();
/* 73 */           MBeanParameterInfo[] bParams = bb.getSignature();
/* 74 */           if (aParams.length < bParams.length) {
/* 75 */             out = -1;
/* 76 */           } else if (aParams.length > bParams.length) {
/* 77 */             out = 1;
/*    */           }
/*    */           else {
/* 80 */             int i = 0; for (int len = aParams.length; i < len; i++)
/*    */             {
/* 82 */               out = ManagementUtils.PARAM_INFO_COMPARATOR.compare(aParams[i], bParams[i]);
/* 83 */               if (out != 0) {
/*    */                 break;
/*    */               }
/*    */             }
/*    */           }
/*    */         }
/*    */         else {
/* 90 */           out = aName.compareTo(bName);
/*    */         }
/*    */       }
/* 93 */       return out;
/*    */     }
/*    */   };
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\management\ManagementUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */