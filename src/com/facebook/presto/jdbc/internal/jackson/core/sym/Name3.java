/*    */ package com.facebook.presto.jdbc.internal.jackson.core.sym;
/*    */ 
/*    */ 
/*    */ public final class Name3
/*    */   extends Name
/*    */ {
/*    */   private final int q1;
/*    */   private final int q2;
/*    */   private final int q3;
/*    */   
/*    */   Name3(String name, int hash, int i1, int i2, int i3)
/*    */   {
/* 13 */     super(name, hash);
/* 14 */     this.q1 = i1;
/* 15 */     this.q2 = i2;
/* 16 */     this.q3 = i3;
/*    */   }
/*    */   
/*    */   public boolean equals(int quad)
/*    */   {
/* 21 */     return false;
/*    */   }
/*    */   
/*    */   public boolean equals(int quad1, int quad2) {
/* 25 */     return false;
/*    */   }
/*    */   
/*    */   public boolean equals(int quad1, int quad2, int quad3) {
/* 29 */     return (this.q1 == quad1) && (this.q2 == quad2) && (this.q3 == quad3);
/*    */   }
/*    */   
/*    */   public boolean equals(int[] quads, int qlen)
/*    */   {
/* 34 */     return (qlen == 3) && (quads[0] == this.q1) && (quads[1] == this.q2) && (quads[2] == this.q3);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\sym\Name3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */