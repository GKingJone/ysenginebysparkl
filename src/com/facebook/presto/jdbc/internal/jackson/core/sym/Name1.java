/*    */ package com.facebook.presto.jdbc.internal.jackson.core.sym;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Name1
/*    */   extends Name
/*    */ {
/* 14 */   private static final Name1 EMPTY = new Name1("", 0, 0);
/*    */   private final int q;
/*    */   
/*    */   Name1(String name, int hash, int quad) {
/* 18 */     super(name, hash);
/* 19 */     this.q = quad;
/*    */   }
/*    */   
/* 22 */   public static Name1 getEmptyName() { return EMPTY; }
/*    */   
/* 24 */   public boolean equals(int quad) { return quad == this.q; }
/* 25 */   public boolean equals(int quad1, int quad2) { return (quad1 == this.q) && (quad2 == 0); }
/* 26 */   public boolean equals(int q1, int q2, int q3) { return false; }
/*    */   
/* 28 */   public boolean equals(int[] quads, int qlen) { return (qlen == 1) && (quads[0] == this.q); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\sym\Name1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */