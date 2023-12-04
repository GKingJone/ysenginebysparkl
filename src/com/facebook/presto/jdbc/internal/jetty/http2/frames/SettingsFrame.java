/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ import java.util.Map;
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
/*    */ public class SettingsFrame
/*    */   extends Frame
/*    */ {
/*    */   public static final int HEADER_TABLE_SIZE = 1;
/*    */   public static final int ENABLE_PUSH = 2;
/*    */   public static final int MAX_CONCURRENT_STREAMS = 3;
/*    */   public static final int INITIAL_WINDOW_SIZE = 4;
/*    */   public static final int MAX_FRAME_SIZE = 5;
/*    */   public static final int MAX_HEADER_LIST_SIZE = 6;
/*    */   private final Map<Integer, Integer> settings;
/*    */   private final boolean reply;
/*    */   
/*    */   public SettingsFrame(Map<Integer, Integer> settings, boolean reply)
/*    */   {
/* 37 */     super(FrameType.SETTINGS);
/* 38 */     this.settings = settings;
/* 39 */     this.reply = reply;
/*    */   }
/*    */   
/*    */   public Map<Integer, Integer> getSettings()
/*    */   {
/* 44 */     return this.settings;
/*    */   }
/*    */   
/*    */   public boolean isReply()
/*    */   {
/* 49 */     return this.reply;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 55 */     return String.format("%s,reply=%b:%s", new Object[] { super.toString(), Boolean.valueOf(this.reply), this.settings });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\SettingsFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */