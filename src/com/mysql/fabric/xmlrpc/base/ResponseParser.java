/*     */ package com.mysql.fabric.xmlrpc.base;
/*     */ 
/*     */ import java.util.Stack;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
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
/*     */ public class ResponseParser
/*     */   extends DefaultHandler
/*     */ {
/*  35 */   private MethodResponse resp = null;
/*     */   
/*     */   public MethodResponse getMethodResponse() {
/*  38 */     return this.resp;
/*     */   }
/*     */   
/*  41 */   Stack<Object> elNames = new Stack();
/*  42 */   Stack<Object> objects = new Stack();
/*     */   
/*     */   public void startElement(String uri, String localName, String qName, Attributes attributes)
/*     */     throws SAXException
/*     */   {
/*  47 */     String thisElement = qName;
/*  48 */     if (thisElement != null) {
/*  49 */       this.elNames.push(thisElement);
/*     */       
/*  51 */       if (thisElement.equals("methodResponse")) {
/*  52 */         this.objects.push(new MethodResponse());
/*  53 */       } else if (thisElement.equals("params")) {
/*  54 */         this.objects.push(new Params());
/*  55 */       } else if (thisElement.equals("param")) {
/*  56 */         this.objects.push(new Param());
/*  57 */       } else if (thisElement.equals("value")) {
/*  58 */         this.objects.push(new Value());
/*  59 */       } else if (thisElement.equals("array")) {
/*  60 */         this.objects.push(new Array());
/*  61 */       } else if (thisElement.equals("data")) {
/*  62 */         this.objects.push(new Data());
/*  63 */       } else if (thisElement.equals("struct")) {
/*  64 */         this.objects.push(new Struct());
/*  65 */       } else if (thisElement.equals("member")) {
/*  66 */         this.objects.push(new Member());
/*  67 */       } else if (thisElement.equals("fault")) {
/*  68 */         this.objects.push(new Fault());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void endElement(String uri, String localName, String qName) throws SAXException
/*     */   {
/*  75 */     String thisElement = (String)this.elNames.pop();
/*  76 */     if (thisElement != null) {
/*  77 */       if (thisElement.equals("methodResponse")) {
/*  78 */         this.resp = ((MethodResponse)this.objects.pop());
/*  79 */       } else if (thisElement.equals("params")) {
/*  80 */         Params pms = (Params)this.objects.pop();
/*  81 */         MethodResponse parent = (MethodResponse)this.objects.peek();
/*  82 */         parent.setParams(pms);
/*  83 */       } else if (thisElement.equals("param")) {
/*  84 */         Param p = (Param)this.objects.pop();
/*  85 */         Params parent = (Params)this.objects.peek();
/*  86 */         parent.addParam(p);
/*  87 */       } else if (thisElement.equals("value")) {
/*  88 */         Value v = (Value)this.objects.pop();
/*  89 */         Object parent = this.objects.peek();
/*  90 */         if ((parent instanceof Data)) {
/*  91 */           ((Data)parent).addValue(v);
/*  92 */         } else if ((parent instanceof Param)) {
/*  93 */           ((Param)parent).setValue(v);
/*  94 */         } else if ((parent instanceof Member)) {
/*  95 */           ((Member)parent).setValue(v);
/*  96 */         } else if ((parent instanceof Fault)) {
/*  97 */           ((Fault)parent).setValue(v);
/*     */         }
/*  99 */       } else if (thisElement.equals("array")) {
/* 100 */         Array a = (Array)this.objects.pop();
/* 101 */         Value parent = (Value)this.objects.peek();
/* 102 */         parent.setArray(a);
/* 103 */       } else if (thisElement.equals("data")) {
/* 104 */         Data d = (Data)this.objects.pop();
/* 105 */         Array parent = (Array)this.objects.peek();
/* 106 */         parent.setData(d);
/* 107 */       } else if (thisElement.equals("struct")) {
/* 108 */         Struct s = (Struct)this.objects.pop();
/* 109 */         Value parent = (Value)this.objects.peek();
/* 110 */         parent.setStruct(s);
/* 111 */       } else if (thisElement.equals("member")) {
/* 112 */         Member m = (Member)this.objects.pop();
/* 113 */         Struct parent = (Struct)this.objects.peek();
/* 114 */         parent.addMember(m);
/* 115 */       } else if (thisElement.equals("fault")) {
/* 116 */         Fault f = (Fault)this.objects.pop();
/* 117 */         MethodResponse parent = (MethodResponse)this.objects.peek();
/* 118 */         parent.setFault(f);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void characters(char[] ch, int start, int length) throws SAXException
/*     */   {
/*     */     try {
/* 126 */       String thisElement = (String)this.elNames.peek();
/* 127 */       if (thisElement != null) {
/* 128 */         if (thisElement.equals("name")) {
/* 129 */           ((Member)this.objects.peek()).setName(new String(ch, start, length));
/* 130 */         } else if (thisElement.equals("value")) {
/* 131 */           ((Value)this.objects.peek()).appendString(new String(ch, start, length));
/* 132 */         } else if ((thisElement.equals("i4")) || (thisElement.equals("int"))) {
/* 133 */           ((Value)this.objects.peek()).setInt(new String(ch, start, length));
/* 134 */         } else if (thisElement.equals("boolean")) {
/* 135 */           ((Value)this.objects.peek()).setBoolean(new String(ch, start, length));
/* 136 */         } else if (thisElement.equals("string")) {
/* 137 */           ((Value)this.objects.peek()).appendString(new String(ch, start, length));
/* 138 */         } else if (thisElement.equals("double")) {
/* 139 */           ((Value)this.objects.peek()).setDouble(new String(ch, start, length));
/* 140 */         } else if (thisElement.equals("dateTime.iso8601")) {
/* 141 */           ((Value)this.objects.peek()).setDateTime(new String(ch, start, length));
/* 142 */         } else if (thisElement.equals("base64")) {
/* 143 */           ((Value)this.objects.peek()).setBase64(new String(ch, start, length).getBytes());
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 147 */       throw new SAXParseException(e.getMessage(), null, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\ResponseParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */