/*     */ package com.mchange.v2.io;
/*     */ 
/*     */ import java.io.FilterWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndentedWriter
/*     */   extends FilterWriter
/*     */ {
/*     */   static final String EOL;
/*     */   
/*     */   static
/*     */   {
/*  34 */     String eol = System.getProperty("line.separator");
/*  35 */     EOL = eol != null ? eol : "\r\n";
/*     */   }
/*     */   
/*  38 */   int indent_level = 0;
/*  39 */   boolean at_line_start = true;
/*     */   
/*     */   public IndentedWriter(Writer out) {
/*  42 */     super(out);
/*     */   }
/*     */   
/*  45 */   private boolean isEol(char c) { return (c == '\r') || (c == '\n'); }
/*     */   
/*     */   public void upIndent() {
/*  48 */     this.indent_level += 1;
/*     */   }
/*     */   
/*  51 */   public void downIndent() { this.indent_level -= 1; }
/*     */   
/*     */   public void write(int c) throws IOException
/*     */   {
/*  55 */     this.out.write(c);
/*  56 */     this.at_line_start = isEol((char)c);
/*     */   }
/*     */   
/*     */   public void write(char[] chars, int off, int len) throws IOException
/*     */   {
/*  61 */     this.out.write(chars, off, len);
/*  62 */     this.at_line_start = isEol(chars[(off + len - 1)]);
/*     */   }
/*     */   
/*     */   public void write(String s, int off, int len) throws IOException
/*     */   {
/*  67 */     if (len > 0)
/*     */     {
/*  69 */       this.out.write(s, off, len);
/*  70 */       this.at_line_start = isEol(s.charAt(off + len - 1));
/*     */     }
/*     */   }
/*     */   
/*     */   private void printIndent() throws IOException
/*     */   {
/*  76 */     for (int i = 0; i < this.indent_level; i++) {
/*  77 */       this.out.write(9);
/*     */     }
/*     */   }
/*     */   
/*     */   public void print(String s) throws IOException {
/*  82 */     if (this.at_line_start)
/*  83 */       printIndent();
/*  84 */     this.out.write(s);
/*  85 */     char last = s.charAt(s.length() - 1);
/*  86 */     this.at_line_start = isEol(last);
/*     */   }
/*     */   
/*     */   public void println(String s) throws IOException
/*     */   {
/*  91 */     if (this.at_line_start)
/*  92 */       printIndent();
/*  93 */     this.out.write(s);
/*  94 */     this.out.write(EOL);
/*  95 */     this.at_line_start = true;
/*     */   }
/*     */   
/*     */   public void print(boolean x) throws IOException {
/*  99 */     print(String.valueOf(x));
/*     */   }
/*     */   
/* 102 */   public void print(byte x) throws IOException { print(String.valueOf(x)); }
/*     */   
/*     */   public void print(char x) throws IOException {
/* 105 */     print(String.valueOf(x));
/*     */   }
/*     */   
/* 108 */   public void print(short x) throws IOException { print(String.valueOf(x)); }
/*     */   
/*     */   public void print(int x) throws IOException {
/* 111 */     print(String.valueOf(x));
/*     */   }
/*     */   
/* 114 */   public void print(long x) throws IOException { print(String.valueOf(x)); }
/*     */   
/*     */   public void print(float x) throws IOException {
/* 117 */     print(String.valueOf(x));
/*     */   }
/*     */   
/* 120 */   public void print(double x) throws IOException { print(String.valueOf(x)); }
/*     */   
/*     */   public void print(Object x) throws IOException {
/* 123 */     print(String.valueOf(x));
/*     */   }
/*     */   
/* 126 */   public void println(boolean x) throws IOException { println(String.valueOf(x)); }
/*     */   
/*     */   public void println(byte x) throws IOException {
/* 129 */     println(String.valueOf(x));
/*     */   }
/*     */   
/* 132 */   public void println(char x) throws IOException { println(String.valueOf(x)); }
/*     */   
/*     */   public void println(short x) throws IOException {
/* 135 */     println(String.valueOf(x));
/*     */   }
/*     */   
/* 138 */   public void println(int x) throws IOException { println(String.valueOf(x)); }
/*     */   
/*     */   public void println(long x) throws IOException {
/* 141 */     println(String.valueOf(x));
/*     */   }
/*     */   
/* 144 */   public void println(float x) throws IOException { println(String.valueOf(x)); }
/*     */   
/*     */   public void println(double x) throws IOException {
/* 147 */     println(String.valueOf(x));
/*     */   }
/*     */   
/* 150 */   public void println(Object x) throws IOException { println(String.valueOf(x)); }
/*     */   
/*     */   public void println() throws IOException {
/* 153 */     println("");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\io\IndentedWriter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */