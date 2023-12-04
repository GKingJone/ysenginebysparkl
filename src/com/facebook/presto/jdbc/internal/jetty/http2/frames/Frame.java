/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Frame
/*    */ {
/*    */   public static final int HEADER_LENGTH = 9;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int DEFAULT_MAX_LENGTH = 16384;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int MAX_MAX_LENGTH = 16777215;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 26 */   public static final Frame[] EMPTY_ARRAY = new Frame[0];
/*    */   
/*    */   private final FrameType type;
/*    */   
/*    */   protected Frame(FrameType type)
/*    */   {
/* 32 */     this.type = type;
/*    */   }
/*    */   
/*    */   public FrameType getType()
/*    */   {
/* 37 */     return this.type;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 43 */     return String.format("%s@%x", new Object[] { getClass().getSimpleName(), Integer.valueOf(hashCode()) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\Frame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */