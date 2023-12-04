/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v1.xml.DomParseUtils;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
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
/*     */ public class ParsedPropertyBeanDocument
/*     */ {
/*  32 */   static final String[] EMPTY_SA = new String[0];
/*     */   
/*     */   String packageName;
/*     */   int class_modifiers;
/*     */   String className;
/*     */   String superclassName;
/*  38 */   String[] interfaceNames = EMPTY_SA;
/*  39 */   String[] generalImports = EMPTY_SA;
/*  40 */   String[] specificImports = EMPTY_SA;
/*     */   Property[] properties;
/*     */   
/*     */   public ParsedPropertyBeanDocument(Document doc)
/*     */   {
/*  45 */     Element rootElem = doc.getDocumentElement();
/*  46 */     this.packageName = DomParseUtils.allTextFromUniqueChild(rootElem, "package");
/*  47 */     Element modifiersElem = DomParseUtils.uniqueImmediateChild(rootElem, "modifiers");
/*  48 */     if (modifiersElem != null) {
/*  49 */       this.class_modifiers = parseModifiers(modifiersElem);
/*     */     } else {
/*  51 */       this.class_modifiers = 1;
/*     */     }
/*  53 */     Element importsElem = DomParseUtils.uniqueChild(rootElem, "imports");
/*  54 */     if (importsElem != null)
/*     */     {
/*  56 */       this.generalImports = DomParseUtils.allTextFromImmediateChildElements(importsElem, "general");
/*  57 */       this.specificImports = DomParseUtils.allTextFromImmediateChildElements(importsElem, "specific");
/*     */     }
/*  59 */     this.className = DomParseUtils.allTextFromUniqueChild(rootElem, "output-class");
/*  60 */     this.superclassName = DomParseUtils.allTextFromUniqueChild(rootElem, "extends");
/*     */     
/*  62 */     Element implementsElem = DomParseUtils.uniqueChild(rootElem, "implements");
/*  63 */     if (implementsElem != null)
/*  64 */       this.interfaceNames = DomParseUtils.allTextFromImmediateChildElements(implementsElem, "interface");
/*  65 */     Element propertiesElem = DomParseUtils.uniqueChild(rootElem, "properties");
/*  66 */     this.properties = findProperties(propertiesElem);
/*     */   }
/*     */   
/*     */ 
/*     */   public ClassInfo getClassInfo()
/*     */   {
/*  72 */     new ClassInfo()
/*     */     {
/*     */       public String getPackageName() {
/*  75 */         return ParsedPropertyBeanDocument.this.packageName;
/*     */       }
/*     */       
/*  78 */       public int getModifiers() { return ParsedPropertyBeanDocument.this.class_modifiers; }
/*     */       
/*     */       public String getClassName() {
/*  81 */         return ParsedPropertyBeanDocument.this.className;
/*     */       }
/*     */       
/*  84 */       public String getSuperclassName() { return ParsedPropertyBeanDocument.this.superclassName; }
/*     */       
/*     */       public String[] getInterfaceNames() {
/*  87 */         return ParsedPropertyBeanDocument.this.interfaceNames;
/*     */       }
/*     */       
/*  90 */       public String[] getGeneralImports() { return ParsedPropertyBeanDocument.this.generalImports; }
/*     */       
/*     */       public String[] getSpecificImports() {
/*  93 */         return ParsedPropertyBeanDocument.this.specificImports;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*  98 */   public Property[] getProperties() { return (Property[])this.properties.clone(); }
/*     */   
/*     */   private Property[] findProperties(Element propertiesElem)
/*     */   {
/* 102 */     NodeList nl = DomParseUtils.immediateChildElementsByTagName(propertiesElem, "property");
/* 103 */     int len = nl.getLength();
/* 104 */     Property[] out = new Property[len];
/* 105 */     for (int i = 0; i < len; i++)
/*     */     {
/* 107 */       Element propertyElem = (Element)nl.item(i);
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
/* 120 */       int variable_modifiers = modifiersThroughParentElem(propertyElem, "variable", 2);
/* 121 */       String name = DomParseUtils.allTextFromUniqueChild(propertyElem, "name", true);
/* 122 */       String simpleTypeName = DomParseUtils.allTextFromUniqueChild(propertyElem, "type", true);
/* 123 */       String defensiveCopyExpression = DomParseUtils.allTextFromUniqueChild(propertyElem, "defensive-copy", true);
/* 124 */       String defaultValueExpression = DomParseUtils.allTextFromUniqueChild(propertyElem, "default-value", true);
/* 125 */       int getter_modifiers = modifiersThroughParentElem(propertyElem, "getter", 1);
/* 126 */       int setter_modifiers = modifiersThroughParentElem(propertyElem, "setter", 1);
/* 127 */       Element readOnlyElem = DomParseUtils.uniqueChild(propertyElem, "read-only");
/* 128 */       boolean is_read_only = readOnlyElem != null;
/* 129 */       Element isBoundElem = DomParseUtils.uniqueChild(propertyElem, "bound");
/* 130 */       boolean is_bound = isBoundElem != null;
/* 131 */       Element isConstrainedElem = DomParseUtils.uniqueChild(propertyElem, "constrained");
/* 132 */       boolean is_constrained = isConstrainedElem != null;
/* 133 */       out[i] = new SimpleProperty(variable_modifiers, name, simpleTypeName, defensiveCopyExpression, defaultValueExpression, getter_modifiers, setter_modifiers, is_read_only, is_bound, is_constrained);
/*     */     }
/*     */     
/*     */ 
/* 137 */     return out;
/*     */   }
/*     */   
/*     */   private static int modifiersThroughParentElem(Element grandparentElem, String parentElemName, int default_mods)
/*     */   {
/* 142 */     Element parentElem = DomParseUtils.uniqueChild(grandparentElem, parentElemName);
/* 143 */     if (parentElem != null)
/*     */     {
/* 145 */       Element modifiersElem = DomParseUtils.uniqueChild(parentElem, "modifiers");
/* 146 */       if (modifiersElem != null) {
/* 147 */         return parseModifiers(modifiersElem);
/*     */       }
/* 149 */       return default_mods;
/*     */     }
/*     */     
/* 152 */     return default_mods;
/*     */   }
/*     */   
/*     */   private static int parseModifiers(Element modifiersElem)
/*     */   {
/* 157 */     int out = 0;
/* 158 */     String[] all_modifiers = DomParseUtils.allTextFromImmediateChildElements(modifiersElem, "modifier", true);
/* 159 */     int i = 0; for (int len = all_modifiers.length; i < len; i++)
/*     */     {
/* 161 */       String modifier = all_modifiers[i];
/* 162 */       if ("public".equals(modifier)) { out |= 0x1;
/* 163 */       } else if ("protected".equals(modifier)) { out |= 0x4;
/* 164 */       } else if ("private".equals(modifier)) { out |= 0x2;
/* 165 */       } else if ("final".equals(modifier)) { out |= 0x10;
/* 166 */       } else if ("abstract".equals(modifier)) { out |= 0x400;
/* 167 */       } else if ("static".equals(modifier)) { out |= 0x8;
/* 168 */       } else if ("synchronized".equals(modifier)) { out |= 0x20;
/* 169 */       } else if ("volatile".equals(modifier)) { out |= 0x40;
/* 170 */       } else if ("transient".equals(modifier)) { out |= 0x80;
/* 171 */       } else if ("strictfp".equals(modifier)) { out |= 0x800;
/* 172 */       } else if ("native".equals(modifier)) { out |= 0x100;
/* 173 */       } else if ("interface".equals(modifier)) out |= 0x200; else
/* 174 */         throw new IllegalArgumentException("Bad modifier: " + modifier);
/*     */     }
/* 176 */     return out;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\ParsedPropertyBeanDocument.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */