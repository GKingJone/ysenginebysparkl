/*    */ package com.facebook.presto.jdbc.internal.jackson.core.sym;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Name2
/*    */   extends Name
/*    */ {
/*    */   private final int q1;
/*    */   
/*    */ 
/*    */   private final int q2;
/*    */   
/*    */ 
/*    */ 
/*    */   Name2(String name, int hash, int quad1, int quad2)
/*    */   {
/* 17 */     super(name, hash);
/* 18 */     this.q1 = quad1;
/* 19 */     this.q2 = quad2;
/*    */   }
/*    */   
/*    */   public boolean equals(int quad) {
/* 23 */     return false;
/*    */   }
/*    */   
/* 26 */   public boolean equals(int quad1, int quad2) { return (quad1 == this.q1) && (quad2 == this.q2); }
/*    */   
/* 28 */   public boolean equals(int quad1, int quad2, int q3) { return false; }
/*    */   
/*    */   public boolean equals(int[] quads, int qlen) {
/* 31 */     return (qlen == 2) && (quads[0] == this.q1) && (quads[1] == this.q2);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\sym\Name2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */