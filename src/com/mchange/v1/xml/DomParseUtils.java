/*     */ package com.mchange.v1.xml;
/*     */ 
/*     */ import com.mchange.v1.util.DebugUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
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
/*     */ public final class DomParseUtils
/*     */ {
/*     */   static final boolean DEBUG = true;
/*     */   
/*     */   public static String allTextFromUniqueChild(Element elem, String childTagName)
/*     */     throws DOMException
/*     */   {
/*  40 */     return allTextFromUniqueChild(elem, childTagName, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String allTextFromUniqueChild(Element elem, String childTagName, boolean trim)
/*     */     throws DOMException
/*     */   {
/*  48 */     Element uniqueChild = uniqueChildByTagName(elem, childTagName);
/*  49 */     if (uniqueChild == null) {
/*  50 */       return null;
/*     */     }
/*  52 */     return allTextFromElement(uniqueChild, trim);
/*     */   }
/*     */   
/*     */   public static Element uniqueChild(Element elem, String childTagName) throws DOMException {
/*  56 */     return uniqueChildByTagName(elem, childTagName);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static Element uniqueChildByTagName(Element elem, String childTagName) throws DOMException {
/*  63 */     NodeList nl = elem.getElementsByTagName(childTagName);
/*  64 */     int len = nl.getLength();
/*     */     
/*  66 */     DebugUtils.myAssert(len <= 1, "There is more than one (" + len + ") child with tag name: " + childTagName + "!!!");
/*     */     
/*     */ 
/*  69 */     return len == 1 ? (Element)nl.item(0) : null;
/*     */   }
/*     */   
/*     */   public static String allText(Element elem) throws DOMException {
/*  73 */     return allTextFromElement(elem);
/*     */   }
/*     */   
/*  76 */   public static String allText(Element elem, boolean trim) throws DOMException { return allTextFromElement(elem, trim); }
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*  80 */   public static String allTextFromElement(Element elem) throws DOMException { return allTextFromElement(elem, false); }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*  85 */   public static String allTextFromElement(Element elem, boolean trim) throws DOMException { StringBuffer textBuf = new StringBuffer();
/*  86 */     NodeList nl = elem.getChildNodes();
/*  87 */     int j = 0; for (int len = nl.getLength(); j < len; j++)
/*     */     {
/*  89 */       Node node = nl.item(j);
/*  90 */       if ((node instanceof Text))
/*  91 */         textBuf.append(node.getNodeValue());
/*     */     }
/*  93 */     String out = textBuf.toString();
/*  94 */     return trim ? out.trim() : out;
/*     */   }
/*     */   
/*     */   public static String[] allTextFromImmediateChildElements(Element parent, String tagName) throws DOMException
/*     */   {
/*  99 */     return allTextFromImmediateChildElements(parent, tagName, false);
/*     */   }
/*     */   
/*     */   public static String[] allTextFromImmediateChildElements(Element parent, String tagName, boolean trim) throws DOMException
/*     */   {
/* 104 */     NodeList nl = immediateChildElementsByTagName(parent, tagName);
/* 105 */     int len = nl.getLength();
/* 106 */     String[] out = new String[len];
/* 107 */     for (int i = 0; i < len; i++)
/* 108 */       out[i] = allText((Element)nl.item(i), trim);
/* 109 */     return out;
/*     */   }
/*     */   
/*     */   public static NodeList immediateChildElementsByTagName(Element parent, String tagName)
/*     */     throws DOMException
/*     */   {
/* 115 */     return getImmediateChildElementsByTagName(parent, tagName);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static NodeList getImmediateChildElementsByTagName(Element parent, String tagName) throws DOMException
/*     */   {
/* 123 */     List nodes = new ArrayList();
/* 124 */     for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
/* 125 */       if (((child instanceof Element)) && (((Element)child).getTagName().equals(tagName)))
/* 126 */         nodes.add(child);
/* 127 */     new NodeList() {
/*     */       private final List val$nodes;
/*     */       
/* 130 */       public int getLength() { return this.val$nodes.size(); }
/*     */       
/*     */       public Node item(int i) {
/* 133 */         return (Node)this.val$nodes.get(i);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static String allTextFromUniqueImmediateChild(Element elem, String childTagName) throws DOMException
/*     */   {
/* 140 */     Element uniqueChild = uniqueImmediateChildByTagName(elem, childTagName);
/* 141 */     if (uniqueChild == null)
/* 142 */       return null;
/* 143 */     return allTextFromElement(uniqueChild);
/*     */   }
/*     */   
/*     */   public static Element uniqueImmediateChild(Element elem, String childTagName) throws DOMException
/*     */   {
/* 148 */     return uniqueImmediateChildByTagName(elem, childTagName);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static Element uniqueImmediateChildByTagName(Element elem, String childTagName) throws DOMException
/*     */   {
/* 156 */     NodeList nl = getImmediateChildElementsByTagName(elem, childTagName);
/* 157 */     int len = nl.getLength();
/*     */     
/* 159 */     DebugUtils.myAssert(len <= 1, "There is more than one (" + len + ") child with tag name: " + childTagName + "!!!");
/*     */     
/*     */ 
/* 162 */     return len == 1 ? (Element)nl.item(0) : null;
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static String attrValFromElement(Element element, String attrName)
/*     */     throws DOMException
/*     */   {
/* 171 */     Attr attr = element.getAttributeNode(attrName);
/* 172 */     return attr == null ? null : attr.getValue();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\xml\DomParseUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */