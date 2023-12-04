/*    */ package com.facebook.presto.jdbc.internal.airlift.units;
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
/*    */ final class Preconditions
/*    */ {
/*    */   static void checkArgument(boolean expression, String errorMessage)
/*    */   {
/* 25 */     if (!expression) {
/* 26 */       throw new IllegalArgumentException(errorMessage);
/*    */     }
/*    */   }
/*    */   
/*    */   static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs)
/*    */   {
/* 32 */     if (!expression) {
/* 33 */       throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
/*    */     }
/*    */   }
/*    */   
/*    */   static void checkState(boolean expression, String errorMessage)
/*    */   {
/* 39 */     if (!expression) {
/* 40 */       throw new IllegalStateException(errorMessage);
/*    */     }
/*    */   }
/*    */   
/*    */   static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs)
/*    */   {
/* 46 */     if (!expression) {
/* 47 */       throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
/*    */     }
/*    */   }
/*    */   
/*    */   private static String format(String template, Object... args)
/*    */   {
/* 53 */     template = String.valueOf(template);
/*    */     
/*    */ 
/* 56 */     StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
/* 57 */     int templateStart = 0;
/* 58 */     int i = 0;
/* 59 */     while (i < args.length) {
/* 60 */       int placeholderStart = template.indexOf("%s", templateStart);
/* 61 */       if (placeholderStart == -1) {
/*    */         break;
/*    */       }
/* 64 */       builder.append(template.substring(templateStart, placeholderStart));
/* 65 */       builder.append(args[(i++)]);
/* 66 */       templateStart = placeholderStart + 2;
/*    */     }
/* 68 */     builder.append(template.substring(templateStart));
/*    */     
/*    */ 
/* 71 */     if (i < args.length) {
/* 72 */       builder.append(" [");
/* 73 */       builder.append(args[(i++)]);
/* 74 */       while (i < args.length) {
/* 75 */         builder.append(", ");
/* 76 */         builder.append(args[(i++)]);
/*    */       }
/* 78 */       builder.append(']');
/*    */     }
/*    */     
/* 81 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\Preconditions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */