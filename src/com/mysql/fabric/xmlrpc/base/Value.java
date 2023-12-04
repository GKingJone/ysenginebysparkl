/*     */ package com.mysql.fabric.xmlrpc.base;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.GregorianCalendar;
/*     */ import javax.xml.datatype.DatatypeConfigurationException;
/*     */ import javax.xml.datatype.DatatypeFactory;
/*     */ import javax.xml.datatype.XMLGregorianCalendar;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Value
/*     */ {
/*     */   public static final byte TYPE_i4 = 0;
/*     */   public static final byte TYPE_int = 1;
/*     */   public static final byte TYPE_boolean = 2;
/*     */   public static final byte TYPE_string = 3;
/*     */   public static final byte TYPE_double = 4;
/*     */   public static final byte TYPE_dateTime_iso8601 = 5;
/*     */   public static final byte TYPE_base64 = 6;
/*     */   public static final byte TYPE_struct = 7;
/*     */   public static final byte TYPE_array = 8;
/*  45 */   protected Object objValue = "";
/*  46 */   protected byte objType = 3;
/*  47 */   private DatatypeFactory dtf = null;
/*     */   
/*     */   public Value() {}
/*     */   
/*     */   public Value(int value)
/*     */   {
/*  53 */     setInt(value);
/*     */   }
/*     */   
/*     */   public Value(String value) {
/*  57 */     setString(value);
/*     */   }
/*     */   
/*     */   public Value(boolean value) {
/*  61 */     setBoolean(value);
/*     */   }
/*     */   
/*     */   public Value(double value) {
/*  65 */     setDouble(value);
/*     */   }
/*     */   
/*     */   public Value(GregorianCalendar value) throws DatatypeConfigurationException {
/*  69 */     setDateTime(value);
/*     */   }
/*     */   
/*     */   public Value(byte[] value) {
/*  73 */     setBase64(value);
/*     */   }
/*     */   
/*     */   public Value(Struct value) {
/*  77 */     setStruct(value);
/*     */   }
/*     */   
/*     */   public Value(Array value) {
/*  81 */     setArray(value);
/*     */   }
/*     */   
/*     */   public Object getValue() {
/*  85 */     return this.objValue;
/*     */   }
/*     */   
/*     */   public byte getType() {
/*  89 */     return this.objType;
/*     */   }
/*     */   
/*     */   public void setInt(int value) {
/*  93 */     this.objValue = Integer.valueOf(value);
/*  94 */     this.objType = 1;
/*     */   }
/*     */   
/*     */   public void setInt(String value) {
/*  98 */     this.objValue = Integer.valueOf(value);
/*  99 */     this.objType = 1;
/*     */   }
/*     */   
/*     */   public void setString(String value) {
/* 103 */     this.objValue = value;
/* 104 */     this.objType = 3;
/*     */   }
/*     */   
/*     */   public void appendString(String value) {
/* 108 */     this.objValue = (this.objValue != null ? this.objValue + value : value);
/* 109 */     this.objType = 3;
/*     */   }
/*     */   
/*     */   public void setBoolean(boolean value) {
/* 113 */     this.objValue = Boolean.valueOf(value);
/* 114 */     this.objType = 2;
/*     */   }
/*     */   
/*     */   public void setBoolean(String value) {
/* 118 */     if ((value.trim().equals("1")) || (value.trim().equalsIgnoreCase("true"))) {
/* 119 */       this.objValue = Boolean.valueOf(true);
/*     */     } else {
/* 121 */       this.objValue = Boolean.valueOf(false);
/*     */     }
/* 123 */     this.objType = 2;
/*     */   }
/*     */   
/*     */   public void setDouble(double value) {
/* 127 */     this.objValue = Double.valueOf(value);
/* 128 */     this.objType = 4;
/*     */   }
/*     */   
/*     */   public void setDouble(String value) {
/* 132 */     this.objValue = Double.valueOf(value);
/* 133 */     this.objType = 4;
/*     */   }
/*     */   
/*     */   public void setDateTime(GregorianCalendar value) throws DatatypeConfigurationException {
/* 137 */     if (this.dtf == null) {
/* 138 */       this.dtf = DatatypeFactory.newInstance();
/*     */     }
/* 140 */     this.objValue = this.dtf.newXMLGregorianCalendar(value);
/* 141 */     this.objType = 5;
/*     */   }
/*     */   
/*     */   public void setDateTime(String value) throws DatatypeConfigurationException {
/* 145 */     if (this.dtf == null) {
/* 146 */       this.dtf = DatatypeFactory.newInstance();
/*     */     }
/* 148 */     this.objValue = this.dtf.newXMLGregorianCalendar(value);
/* 149 */     this.objType = 5;
/*     */   }
/*     */   
/*     */   public void setBase64(byte[] value) {
/* 153 */     this.objValue = value;
/* 154 */     this.objType = 6;
/*     */   }
/*     */   
/*     */   public void setStruct(Struct value) {
/* 158 */     this.objValue = value;
/* 159 */     this.objType = 7;
/*     */   }
/*     */   
/*     */   public void setArray(Array value) {
/* 163 */     this.objValue = value;
/* 164 */     this.objType = 8;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 169 */     StringBuilder sb = new StringBuilder("<value>");
/* 170 */     switch (this.objType) {
/*     */     case 0: 
/* 172 */       sb.append("<i4>" + ((Integer)this.objValue).toString() + "</i4>");
/* 173 */       break;
/*     */     case 1: 
/* 175 */       sb.append("<int>" + ((Integer)this.objValue).toString() + "</int>");
/* 176 */       break;
/*     */     
/*     */     case 2: 
/* 179 */       sb.append("<boolean>" + (((Boolean)this.objValue).booleanValue() ? 1 : 0) + "</boolean>");
/* 180 */       break;
/*     */     
/*     */     case 4: 
/* 183 */       sb.append("<double>" + ((Double)this.objValue).toString() + "</double>");
/* 184 */       break;
/*     */     
/*     */     case 5: 
/* 187 */       sb.append("<dateTime.iso8601>" + ((XMLGregorianCalendar)this.objValue).toString() + "</dateTime.iso8601>");
/* 188 */       break;
/*     */     
/*     */     case 6: 
/* 191 */       sb.append("<base64>" + Arrays.toString((byte[])this.objValue) + "</base64>");
/* 192 */       break;
/*     */     
/*     */     case 7: 
/* 195 */       sb.append(((Struct)this.objValue).toString());
/* 196 */       break;
/*     */     
/*     */     case 8: 
/* 199 */       sb.append(((Array)this.objValue).toString());
/* 200 */       break;
/*     */     case 3: 
/*     */     default: 
/* 203 */       sb.append("<string>" + escapeXMLChars(this.objValue.toString()) + "</string>");
/*     */     }
/* 205 */     sb.append("</value>");
/* 206 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private String escapeXMLChars(String s) {
/* 210 */     StringBuilder sb = new StringBuilder(s.length());
/*     */     
/* 212 */     for (int i = 0; i < s.length(); i++) {
/* 213 */       char c = s.charAt(i);
/* 214 */       switch (c) {
/*     */       case '&': 
/* 216 */         sb.append("&amp;");
/* 217 */         break;
/*     */       case '<': 
/* 219 */         sb.append("&lt;");
/* 220 */         break;
/*     */       case '>': 
/* 222 */         sb.append("&gt;");
/* 223 */         break;
/*     */       default: 
/* 225 */         sb.append(c);
/*     */       }
/*     */       
/*     */     }
/* 229 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\Value.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */