/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty.Access;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector.ReferenceProperty;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class POJOPropertyBuilder
/*      */   extends BeanPropertyDefinition
/*      */   implements Comparable<POJOPropertyBuilder>
/*      */ {
/*      */   protected final boolean _forSerialization;
/*      */   protected final MapperConfig<?> _config;
/*      */   protected final AnnotationIntrospector _annotationIntrospector;
/*      */   protected final PropertyName _name;
/*      */   protected final PropertyName _internalName;
/*      */   protected Linked<AnnotatedField> _fields;
/*      */   protected Linked<AnnotatedParameter> _ctorParameters;
/*      */   protected Linked<AnnotatedMethod> _getters;
/*      */   protected Linked<AnnotatedMethod> _setters;
/*      */   
/*      */   public POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName)
/*      */   {
/*   52 */     this(config, ai, forSerialization, internalName, internalName);
/*      */   }
/*      */   
/*      */ 
/*      */   protected POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName, PropertyName name)
/*      */   {
/*   58 */     this._config = config;
/*   59 */     this._annotationIntrospector = ai;
/*   60 */     this._internalName = internalName;
/*   61 */     this._name = name;
/*   62 */     this._forSerialization = forSerialization;
/*      */   }
/*      */   
/*      */   public POJOPropertyBuilder(POJOPropertyBuilder src, PropertyName newName)
/*      */   {
/*   67 */     this._config = src._config;
/*   68 */     this._annotationIntrospector = src._annotationIntrospector;
/*   69 */     this._internalName = src._internalName;
/*   70 */     this._name = newName;
/*   71 */     this._fields = src._fields;
/*   72 */     this._ctorParameters = src._ctorParameters;
/*   73 */     this._getters = src._getters;
/*   74 */     this._setters = src._setters;
/*   75 */     this._forSerialization = src._forSerialization;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public POJOPropertyBuilder withName(PropertyName newName)
/*      */   {
/*   86 */     return new POJOPropertyBuilder(this, newName);
/*      */   }
/*      */   
/*      */ 
/*      */   public POJOPropertyBuilder withSimpleName(String newSimpleName)
/*      */   {
/*   92 */     PropertyName newName = this._name.withSimpleName(newSimpleName);
/*   93 */     return newName == this._name ? this : new POJOPropertyBuilder(this, newName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int compareTo(POJOPropertyBuilder other)
/*      */   {
/*  108 */     if (this._ctorParameters != null) {
/*  109 */       if (other._ctorParameters == null) {
/*  110 */         return -1;
/*      */       }
/*  112 */     } else if (other._ctorParameters != null) {
/*  113 */       return 1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  118 */     return getName().compareTo(other.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getName()
/*      */   {
/*  129 */     return this._name == null ? null : this._name.getSimpleName();
/*      */   }
/*      */   
/*      */   public PropertyName getFullName()
/*      */   {
/*  134 */     return this._name;
/*      */   }
/*      */   
/*      */   public boolean hasName(PropertyName name)
/*      */   {
/*  139 */     return this._name.equals(name);
/*      */   }
/*      */   
/*      */   public String getInternalName() {
/*  143 */     return this._internalName.getSimpleName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PropertyName getWrapperName()
/*      */   {
/*  152 */     AnnotatedMember member = getPrimaryMember();
/*  153 */     return (member == null) || (this._annotationIntrospector == null) ? null : this._annotationIntrospector.findWrapperName(member);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isExplicitlyIncluded()
/*      */   {
/*  167 */     return (_anyExplicits(this._fields)) || (_anyExplicits(this._getters)) || (_anyExplicits(this._setters)) || (_anyExplicits(this._ctorParameters));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isExplicitlyNamed()
/*      */   {
/*  176 */     return (_anyExplicitNames(this._fields)) || (_anyExplicitNames(this._getters)) || (_anyExplicitNames(this._setters)) || (_anyExplicitNames(this._ctorParameters));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasGetter()
/*      */   {
/*  190 */     return this._getters != null;
/*      */   }
/*      */   
/*  193 */   public boolean hasSetter() { return this._setters != null; }
/*      */   
/*      */   public boolean hasField() {
/*  196 */     return this._fields != null;
/*      */   }
/*      */   
/*  199 */   public boolean hasConstructorParameter() { return this._ctorParameters != null; }
/*      */   
/*      */   public boolean couldDeserialize()
/*      */   {
/*  203 */     return (this._ctorParameters != null) || (this._setters != null) || (this._fields != null);
/*      */   }
/*      */   
/*      */   public boolean couldSerialize()
/*      */   {
/*  208 */     return (this._getters != null) || (this._fields != null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public AnnotatedMethod getGetter()
/*      */   {
/*  215 */     Linked<AnnotatedMethod> curr = this._getters;
/*  216 */     if (curr == null) {
/*  217 */       return null;
/*      */     }
/*  219 */     Linked<AnnotatedMethod> next = curr.next;
/*  220 */     if (next == null) {
/*  221 */       return (AnnotatedMethod)curr.value;
/*      */     }
/*  224 */     for (; 
/*  224 */         next != null; next = next.next)
/*      */     {
/*      */ 
/*      */ 
/*  228 */       Class<?> currClass = ((AnnotatedMethod)curr.value).getDeclaringClass();
/*  229 */       Class<?> nextClass = ((AnnotatedMethod)next.value).getDeclaringClass();
/*  230 */       if (currClass != nextClass) {
/*  231 */         if (currClass.isAssignableFrom(nextClass)) {
/*  232 */           curr = next;
/*      */ 
/*      */         }
/*  235 */         else if (nextClass.isAssignableFrom(currClass)) {}
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  245 */         int priNext = _getterPriority((AnnotatedMethod)next.value);
/*  246 */         int priCurr = _getterPriority((AnnotatedMethod)curr.value);
/*      */         
/*  248 */         if (priNext != priCurr) {
/*  249 */           if (priNext < priCurr) {
/*  250 */             curr = next;
/*      */           }
/*      */         }
/*      */         else {
/*  254 */           throw new IllegalArgumentException("Conflicting getter definitions for property \"" + getName() + "\": " + ((AnnotatedMethod)curr.value).getFullName() + " vs " + ((AnnotatedMethod)next.value).getFullName());
/*      */         }
/*      */       }
/*      */     }
/*  258 */     this._getters = curr.withoutNext();
/*  259 */     return (AnnotatedMethod)curr.value;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public AnnotatedMethod getSetter()
/*      */   {
/*  266 */     Linked<AnnotatedMethod> curr = this._setters;
/*  267 */     if (curr == null) {
/*  268 */       return null;
/*      */     }
/*  270 */     Linked<AnnotatedMethod> next = curr.next;
/*  271 */     if (next == null) {
/*  272 */       return (AnnotatedMethod)curr.value;
/*      */     }
/*  275 */     for (; 
/*  275 */         next != null; next = next.next)
/*      */     {
/*  277 */       Class<?> currClass = ((AnnotatedMethod)curr.value).getDeclaringClass();
/*  278 */       Class<?> nextClass = ((AnnotatedMethod)next.value).getDeclaringClass();
/*  279 */       if (currClass != nextClass) {
/*  280 */         if (currClass.isAssignableFrom(nextClass)) {
/*  281 */           curr = next;
/*      */ 
/*      */         }
/*  284 */         else if (nextClass.isAssignableFrom(currClass)) {}
/*      */       }
/*      */       else
/*      */       {
/*  288 */         AnnotatedMethod nextM = (AnnotatedMethod)next.value;
/*  289 */         AnnotatedMethod currM = (AnnotatedMethod)curr.value;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  296 */         int priNext = _setterPriority(nextM);
/*  297 */         int priCurr = _setterPriority(currM);
/*      */         
/*  299 */         if (priNext != priCurr) {
/*  300 */           if (priNext < priCurr) {
/*  301 */             curr = next;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  306 */           if (this._annotationIntrospector != null) {
/*  307 */             AnnotatedMethod pref = this._annotationIntrospector.resolveSetterConflict(this._config, currM, nextM);
/*      */             
/*      */ 
/*      */ 
/*  311 */             if (pref == currM) {
/*      */               continue;
/*      */             }
/*  314 */             if (pref == nextM) {
/*  315 */               curr = next;
/*  316 */               continue;
/*      */             }
/*      */           }
/*  319 */           throw new IllegalArgumentException(String.format("Conflicting setter definitions for property \"%s\": %s vs %s", new Object[] { getName(), ((AnnotatedMethod)curr.value).getFullName(), ((AnnotatedMethod)next.value).getFullName() }));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  324 */     this._setters = curr.withoutNext();
/*  325 */     return (AnnotatedMethod)curr.value;
/*      */   }
/*      */   
/*      */ 
/*      */   public AnnotatedField getField()
/*      */   {
/*  331 */     if (this._fields == null) {
/*  332 */       return null;
/*      */     }
/*      */     
/*  335 */     AnnotatedField field = (AnnotatedField)this._fields.value;
/*  336 */     for (Linked<AnnotatedField> next = this._fields.next; 
/*  337 */         next != null; next = next.next) {
/*  338 */       AnnotatedField nextField = (AnnotatedField)next.value;
/*  339 */       Class<?> fieldClass = field.getDeclaringClass();
/*  340 */       Class<?> nextClass = nextField.getDeclaringClass();
/*  341 */       if (fieldClass != nextClass) {
/*  342 */         if (fieldClass.isAssignableFrom(nextClass)) {
/*  343 */           field = nextField;
/*      */ 
/*      */         }
/*  346 */         else if (nextClass.isAssignableFrom(fieldClass)) {}
/*      */ 
/*      */       }
/*      */       else {
/*  350 */         throw new IllegalArgumentException("Multiple fields representing property \"" + getName() + "\": " + field.getFullName() + " vs " + nextField.getFullName());
/*      */       }
/*      */     }
/*  353 */     return field;
/*      */   }
/*      */   
/*      */ 
/*      */   public AnnotatedParameter getConstructorParameter()
/*      */   {
/*  359 */     if (this._ctorParameters == null) {
/*  360 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  370 */     Linked<AnnotatedParameter> curr = this._ctorParameters;
/*      */     do {
/*  372 */       if ((((AnnotatedParameter)curr.value).getOwner() instanceof AnnotatedConstructor)) {
/*  373 */         return (AnnotatedParameter)curr.value;
/*      */       }
/*  375 */       curr = curr.next;
/*  376 */     } while (curr != null);
/*  377 */     return (AnnotatedParameter)this._ctorParameters.value;
/*      */   }
/*      */   
/*      */   public Iterator<AnnotatedParameter> getConstructorParameters()
/*      */   {
/*  382 */     if (this._ctorParameters == null) {
/*  383 */       return ClassUtil.emptyIterator();
/*      */     }
/*  385 */     return new MemberIterator(this._ctorParameters);
/*      */   }
/*      */   
/*      */ 
/*      */   public AnnotatedMember getAccessor()
/*      */   {
/*  391 */     AnnotatedMember m = getGetter();
/*  392 */     if (m == null) {
/*  393 */       m = getField();
/*      */     }
/*  395 */     return m;
/*      */   }
/*      */   
/*      */ 
/*      */   public AnnotatedMember getMutator()
/*      */   {
/*  401 */     AnnotatedMember m = getConstructorParameter();
/*  402 */     if (m == null) {
/*  403 */       m = getSetter();
/*  404 */       if (m == null) {
/*  405 */         m = getField();
/*      */       }
/*      */     }
/*  408 */     return m;
/*      */   }
/*      */   
/*      */   public AnnotatedMember getNonConstructorMutator()
/*      */   {
/*  413 */     AnnotatedMember m = getSetter();
/*  414 */     if (m == null) {
/*  415 */       m = getField();
/*      */     }
/*  417 */     return m;
/*      */   }
/*      */   
/*      */   public AnnotatedMember getPrimaryMember()
/*      */   {
/*  422 */     if (this._forSerialization) {
/*  423 */       return getAccessor();
/*      */     }
/*  425 */     return getMutator();
/*      */   }
/*      */   
/*      */   protected int _getterPriority(AnnotatedMethod m)
/*      */   {
/*  430 */     String name = m.getName();
/*      */     
/*  432 */     if ((name.startsWith("get")) && (name.length() > 3))
/*      */     {
/*  434 */       return 1;
/*      */     }
/*  436 */     if ((name.startsWith("is")) && (name.length() > 2)) {
/*  437 */       return 2;
/*      */     }
/*  439 */     return 3;
/*      */   }
/*      */   
/*      */   protected int _setterPriority(AnnotatedMethod m)
/*      */   {
/*  444 */     String name = m.getName();
/*  445 */     if ((name.startsWith("set")) && (name.length() > 3))
/*      */     {
/*  447 */       return 1;
/*      */     }
/*  449 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Class<?>[] findViews()
/*      */   {
/*  460 */     (Class[])fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public Class<?>[] withMember(AnnotatedMember member) {
/*  463 */         return POJOPropertyBuilder.this._annotationIntrospector.findViews(member);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public AnnotationIntrospector.ReferenceProperty findReferenceType()
/*      */   {
/*  470 */     (AnnotationIntrospector.ReferenceProperty)fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public AnnotationIntrospector.ReferenceProperty withMember(AnnotatedMember member) {
/*  473 */         return POJOPropertyBuilder.this._annotationIntrospector.findReferenceType(member);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public boolean isTypeId()
/*      */   {
/*  480 */     Boolean b = (Boolean)fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public Boolean withMember(AnnotatedMember member) {
/*  483 */         return POJOPropertyBuilder.this._annotationIntrospector.isTypeId(member);
/*      */       }
/*  485 */     });
/*  486 */     return (b != null) && (b.booleanValue());
/*      */   }
/*      */   
/*      */   public PropertyMetadata getMetadata()
/*      */   {
/*  491 */     Boolean b = _findRequired();
/*  492 */     String desc = _findDescription();
/*  493 */     Integer idx = _findIndex();
/*  494 */     String def = _findDefaultValue();
/*  495 */     if ((b == null) && (idx == null) && (def == null)) {
/*  496 */       return desc == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : PropertyMetadata.STD_REQUIRED_OR_OPTIONAL.withDescription(desc);
/*      */     }
/*      */     
/*  499 */     return PropertyMetadata.construct(b.booleanValue(), desc, idx, def);
/*      */   }
/*      */   
/*      */   protected Boolean _findRequired() {
/*  503 */     (Boolean)fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public Boolean withMember(AnnotatedMember member) {
/*  506 */         return POJOPropertyBuilder.this._annotationIntrospector.hasRequiredMarker(member);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected String _findDescription() {
/*  512 */     (String)fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public String withMember(AnnotatedMember member) {
/*  515 */         return POJOPropertyBuilder.this._annotationIntrospector.findPropertyDescription(member);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected Integer _findIndex() {
/*  521 */     (Integer)fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public Integer withMember(AnnotatedMember member) {
/*  524 */         return POJOPropertyBuilder.this._annotationIntrospector.findPropertyIndex(member);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected String _findDefaultValue() {
/*  530 */     (String)fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public String withMember(AnnotatedMember member) {
/*  533 */         return POJOPropertyBuilder.this._annotationIntrospector.findPropertyDefaultValue(member);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public ObjectIdInfo findObjectIdInfo()
/*      */   {
/*  540 */     (ObjectIdInfo)fromMemberAnnotations(new WithMember()
/*      */     {
/*      */       public ObjectIdInfo withMember(AnnotatedMember member) {
/*  543 */         ObjectIdInfo info = POJOPropertyBuilder.this._annotationIntrospector.findObjectIdInfo(member);
/*  544 */         if (info != null) {
/*  545 */           info = POJOPropertyBuilder.this._annotationIntrospector.findObjectReferenceInfo(member, info);
/*      */         }
/*  547 */         return info;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public JsonInclude.Value findInclusion()
/*      */   {
/*  554 */     AnnotatedMember a = getAccessor();
/*      */     
/*  556 */     JsonInclude.Value v = this._config.getDefaultPropertyInclusion(a.getRawType());
/*  557 */     if (this._annotationIntrospector != null) {
/*  558 */       JsonInclude.Value v2 = this._annotationIntrospector.findPropertyInclusion(a);
/*  559 */       if (v2 != null) {
/*  560 */         if (v == null) {
/*  561 */           v = v2;
/*      */         } else {
/*  563 */           v = v.withOverrides(v2);
/*      */         }
/*      */       }
/*      */     }
/*  567 */     return v == null ? JsonInclude.Value.empty() : v;
/*      */   }
/*      */   
/*      */   public JsonProperty.Access findAccess() {
/*  571 */     (JsonProperty.Access)fromMemberAnnotationsExcept(new WithMember()
/*      */     {
/*      */ 
/*  574 */       public JsonProperty.Access withMember(AnnotatedMember member) { return POJOPropertyBuilder.this._annotationIntrospector.findPropertyAccess(member); } }, JsonProperty.Access.AUTO);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addField(AnnotatedField a, PropertyName name, boolean explName, boolean visible, boolean ignored)
/*      */   {
/*  586 */     this._fields = new Linked(a, this._fields, name, explName, visible, ignored);
/*      */   }
/*      */   
/*      */   public void addCtor(AnnotatedParameter a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/*  590 */     this._ctorParameters = new Linked(a, this._ctorParameters, name, explName, visible, ignored);
/*      */   }
/*      */   
/*      */   public void addGetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/*  594 */     this._getters = new Linked(a, this._getters, name, explName, visible, ignored);
/*      */   }
/*      */   
/*      */   public void addSetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/*  598 */     this._setters = new Linked(a, this._setters, name, explName, visible, ignored);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addAll(POJOPropertyBuilder src)
/*      */   {
/*  607 */     this._fields = merge(this._fields, src._fields);
/*  608 */     this._ctorParameters = merge(this._ctorParameters, src._ctorParameters);
/*  609 */     this._getters = merge(this._getters, src._getters);
/*  610 */     this._setters = merge(this._setters, src._setters);
/*      */   }
/*      */   
/*      */   private static <T> Linked<T> merge(Linked<T> chain1, Linked<T> chain2)
/*      */   {
/*  615 */     if (chain1 == null) {
/*  616 */       return chain2;
/*      */     }
/*  618 */     if (chain2 == null) {
/*  619 */       return chain1;
/*      */     }
/*  621 */     return chain1.append(chain2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeIgnored()
/*      */   {
/*  636 */     this._fields = _removeIgnored(this._fields);
/*  637 */     this._getters = _removeIgnored(this._getters);
/*  638 */     this._setters = _removeIgnored(this._setters);
/*  639 */     this._ctorParameters = _removeIgnored(this._ctorParameters);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeNonVisible(boolean inferMutators)
/*      */   {
/*  652 */     JsonProperty.Access acc = findAccess();
/*  653 */     if (acc == null) {
/*  654 */       acc = JsonProperty.Access.AUTO;
/*      */     }
/*  656 */     switch (acc)
/*      */     {
/*      */     case READ_ONLY: 
/*  659 */       this._setters = null;
/*  660 */       this._ctorParameters = null;
/*  661 */       if (!this._forSerialization) {
/*  662 */         this._fields = null;
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case READ_WRITE: 
/*      */       break;
/*      */     case WRITE_ONLY: 
/*  670 */       this._getters = null;
/*  671 */       if (this._forSerialization) {
/*  672 */         this._fields = null;
/*      */       }
/*      */       break;
/*      */     case AUTO: 
/*      */     default: 
/*  677 */       this._getters = _removeNonVisible(this._getters);
/*  678 */       this._ctorParameters = _removeNonVisible(this._ctorParameters);
/*      */       
/*  680 */       if ((!inferMutators) || (this._getters == null)) {
/*  681 */         this._fields = _removeNonVisible(this._fields);
/*  682 */         this._setters = _removeNonVisible(this._setters);
/*      */       }
/*      */       
/*      */       break;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeConstructors()
/*      */   {
/*  693 */     this._ctorParameters = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void trimByVisibility()
/*      */   {
/*  703 */     this._fields = _trimByVisibility(this._fields);
/*  704 */     this._getters = _trimByVisibility(this._getters);
/*  705 */     this._setters = _trimByVisibility(this._setters);
/*  706 */     this._ctorParameters = _trimByVisibility(this._ctorParameters);
/*      */   }
/*      */   
/*      */ 
/*      */   public void mergeAnnotations(boolean forSerialization)
/*      */   {
/*  712 */     if (forSerialization) {
/*  713 */       if (this._getters != null) {
/*  714 */         AnnotationMap ann = _mergeAnnotations(0, new Linked[] { this._getters, this._fields, this._ctorParameters, this._setters });
/*  715 */         this._getters = _applyAnnotations(this._getters, ann);
/*  716 */       } else if (this._fields != null) {
/*  717 */         AnnotationMap ann = _mergeAnnotations(0, new Linked[] { this._fields, this._ctorParameters, this._setters });
/*  718 */         this._fields = _applyAnnotations(this._fields, ann);
/*      */       }
/*      */     }
/*  721 */     else if (this._ctorParameters != null) {
/*  722 */       AnnotationMap ann = _mergeAnnotations(0, new Linked[] { this._ctorParameters, this._setters, this._fields, this._getters });
/*  723 */       this._ctorParameters = _applyAnnotations(this._ctorParameters, ann);
/*  724 */     } else if (this._setters != null) {
/*  725 */       AnnotationMap ann = _mergeAnnotations(0, new Linked[] { this._setters, this._fields, this._getters });
/*  726 */       this._setters = _applyAnnotations(this._setters, ann);
/*  727 */     } else if (this._fields != null) {
/*  728 */       AnnotationMap ann = _mergeAnnotations(0, new Linked[] { this._fields, this._getters });
/*  729 */       this._fields = _applyAnnotations(this._fields, ann);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private AnnotationMap _mergeAnnotations(int index, Linked<? extends AnnotatedMember>... nodes)
/*      */   {
/*  737 */     AnnotationMap ann = _getAllAnnotations(nodes[index]);
/*  738 */     do { index++; if (index >= nodes.length) break;
/*  739 */     } while (nodes[index] == null);
/*  740 */     return AnnotationMap.merge(ann, _mergeAnnotations(index, nodes));
/*      */     
/*      */ 
/*  743 */     return ann;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private <T extends AnnotatedMember> AnnotationMap _getAllAnnotations(Linked<T> node)
/*      */   {
/*  756 */     AnnotationMap ann = ((AnnotatedMember)node.value).getAllAnnotations();
/*  757 */     if (node.next != null) {
/*  758 */       ann = AnnotationMap.merge(ann, _getAllAnnotations(node.next));
/*      */     }
/*  760 */     return ann;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private <T extends AnnotatedMember> Linked<T> _applyAnnotations(Linked<T> node, AnnotationMap ann)
/*      */   {
/*  774 */     T value = (AnnotatedMember)((AnnotatedMember)node.value).withAnnotations(ann);
/*  775 */     if (node.next != null) {
/*  776 */       node = node.withNext(_applyAnnotations(node.next, ann));
/*      */     }
/*  778 */     return node.withValue(value);
/*      */   }
/*      */   
/*      */   private <T> Linked<T> _removeIgnored(Linked<T> node)
/*      */   {
/*  783 */     if (node == null) {
/*  784 */       return node;
/*      */     }
/*  786 */     return node.withoutIgnored();
/*      */   }
/*      */   
/*      */   private <T> Linked<T> _removeNonVisible(Linked<T> node)
/*      */   {
/*  791 */     if (node == null) {
/*  792 */       return node;
/*      */     }
/*  794 */     return node.withoutNonVisible();
/*      */   }
/*      */   
/*      */   private <T> Linked<T> _trimByVisibility(Linked<T> node)
/*      */   {
/*  799 */     if (node == null) {
/*  800 */       return node;
/*      */     }
/*  802 */     return node.trimByVisibility();
/*      */   }
/*      */   
/*      */   private <T> boolean _anyExplicits(Linked<T> n)
/*      */   {
/*  813 */     for (; 
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  813 */         n != null; n = n.next) {
/*  814 */       if ((n.name != null) && (n.name.hasSimpleName())) {
/*  815 */         return true;
/*      */       }
/*      */     }
/*  818 */     return false;
/*      */   }
/*      */   
/*      */   private <T> boolean _anyExplicitNames(Linked<T> n)
/*      */   {
/*  823 */     for (; n != null; n = n.next) {
/*  824 */       if ((n.name != null) && (n.isNameExplicit)) {
/*  825 */         return true;
/*      */       }
/*      */     }
/*  828 */     return false;
/*      */   }
/*      */   
/*      */   public boolean anyVisible() {
/*  832 */     return (_anyVisible(this._fields)) || (_anyVisible(this._getters)) || (_anyVisible(this._setters)) || (_anyVisible(this._ctorParameters));
/*      */   }
/*      */   
/*      */   private <T> boolean _anyVisible(Linked<T> n)
/*      */   {
/*  841 */     for (; 
/*      */         
/*      */ 
/*      */ 
/*  841 */         n != null; n = n.next) {
/*  842 */       if (n.isVisible) {
/*  843 */         return true;
/*      */       }
/*      */     }
/*  846 */     return false;
/*      */   }
/*      */   
/*      */   public boolean anyIgnorals() {
/*  850 */     return (_anyIgnorals(this._fields)) || (_anyIgnorals(this._getters)) || (_anyIgnorals(this._setters)) || (_anyIgnorals(this._ctorParameters));
/*      */   }
/*      */   
/*      */   private <T> boolean _anyIgnorals(Linked<T> n)
/*      */   {
/*  859 */     for (; 
/*      */         
/*      */ 
/*      */ 
/*  859 */         n != null; n = n.next) {
/*  860 */       if (n.isMarkedIgnored) {
/*  861 */         return true;
/*      */       }
/*      */     }
/*  864 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<PropertyName> findExplicitNames()
/*      */   {
/*  875 */     Set<PropertyName> renamed = null;
/*  876 */     renamed = _findExplicitNames(this._fields, renamed);
/*  877 */     renamed = _findExplicitNames(this._getters, renamed);
/*  878 */     renamed = _findExplicitNames(this._setters, renamed);
/*  879 */     renamed = _findExplicitNames(this._ctorParameters, renamed);
/*  880 */     if (renamed == null) {
/*  881 */       return Collections.emptySet();
/*      */     }
/*  883 */     return renamed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Collection<POJOPropertyBuilder> explode(Collection<PropertyName> newNames)
/*      */   {
/*  896 */     HashMap<PropertyName, POJOPropertyBuilder> props = new HashMap();
/*  897 */     _explode(newNames, props, this._fields);
/*  898 */     _explode(newNames, props, this._getters);
/*  899 */     _explode(newNames, props, this._setters);
/*  900 */     _explode(newNames, props, this._ctorParameters);
/*  901 */     return props.values();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _explode(Collection<PropertyName> newNames, Map<PropertyName, POJOPropertyBuilder> props, Linked<?> accessors)
/*      */   {
/*  909 */     Linked<?> firstAcc = accessors;
/*  910 */     for (Linked<?> node = accessors; node != null; node = node.next) {
/*  911 */       PropertyName name = node.name;
/*  912 */       if ((!node.isNameExplicit) || (name == null))
/*      */       {
/*  914 */         if (node.isVisible)
/*      */         {
/*      */ 
/*      */ 
/*  918 */           throw new IllegalStateException("Conflicting/ambiguous property name definitions (implicit name '" + this._name + "'): found multiple explicit names: " + newNames + ", but also implicit accessor: " + node);
/*      */         }
/*      */       }
/*      */       else {
/*  922 */         POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(name);
/*  923 */         if (prop == null) {
/*  924 */           prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, this._internalName, name);
/*      */           
/*  926 */           props.put(name, prop);
/*      */         }
/*      */         
/*  929 */         if (firstAcc == this._fields) {
/*  930 */           Linked<AnnotatedField> n2 = node;
/*  931 */           prop._fields = n2.withNext(prop._fields);
/*  932 */         } else if (firstAcc == this._getters) {
/*  933 */           Linked<AnnotatedMethod> n2 = node;
/*  934 */           prop._getters = n2.withNext(prop._getters);
/*  935 */         } else if (firstAcc == this._setters) {
/*  936 */           Linked<AnnotatedMethod> n2 = node;
/*  937 */           prop._setters = n2.withNext(prop._setters);
/*  938 */         } else if (firstAcc == this._ctorParameters) {
/*  939 */           Linked<AnnotatedParameter> n2 = node;
/*  940 */           prop._ctorParameters = n2.withNext(prop._ctorParameters);
/*      */         } else {
/*  942 */           throw new IllegalStateException("Internal error: mismatched accessors, property: " + this);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private Set<PropertyName> _findExplicitNames(Linked<? extends AnnotatedMember> node, Set<PropertyName> renamed)
/*      */   {
/*  950 */     for (; node != null; node = node.next)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  957 */       if ((node.isNameExplicit) && (node.name != null))
/*      */       {
/*      */ 
/*  960 */         if (renamed == null) {
/*  961 */           renamed = new HashSet();
/*      */         }
/*  963 */         renamed.add(node.name);
/*      */       } }
/*  965 */     return renamed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  972 */     StringBuilder sb = new StringBuilder();
/*  973 */     sb.append("[Property '").append(this._name).append("'; ctors: ").append(this._ctorParameters).append(", field(s): ").append(this._fields).append(", getter(s): ").append(this._getters).append(", setter(s): ").append(this._setters);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  979 */     sb.append("]");
/*  980 */     return sb.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected <T> T fromMemberAnnotations(WithMember<T> func)
/*      */   {
/*  995 */     T result = null;
/*  996 */     if (this._annotationIntrospector != null) {
/*  997 */       if (this._forSerialization) {
/*  998 */         if (this._getters != null) {
/*  999 */           result = func.withMember((AnnotatedMember)this._getters.value);
/*      */         }
/*      */       } else {
/* 1002 */         if (this._ctorParameters != null) {
/* 1003 */           result = func.withMember((AnnotatedMember)this._ctorParameters.value);
/*      */         }
/* 1005 */         if ((result == null) && (this._setters != null)) {
/* 1006 */           result = func.withMember((AnnotatedMember)this._setters.value);
/*      */         }
/*      */       }
/* 1009 */       if ((result == null) && (this._fields != null)) {
/* 1010 */         result = func.withMember((AnnotatedMember)this._fields.value);
/*      */       }
/*      */     }
/* 1013 */     return result;
/*      */   }
/*      */   
/*      */   protected <T> T fromMemberAnnotationsExcept(WithMember<T> func, T defaultValue)
/*      */   {
/* 1018 */     if (this._annotationIntrospector == null) {
/* 1019 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1024 */     if (this._forSerialization) {
/* 1025 */       if (this._getters != null) {
/* 1026 */         T result = func.withMember((AnnotatedMember)this._getters.value);
/* 1027 */         if ((result != null) && (result != defaultValue)) {
/* 1028 */           return result;
/*      */         }
/*      */       }
/* 1031 */       if (this._fields != null) {
/* 1032 */         T result = func.withMember((AnnotatedMember)this._fields.value);
/* 1033 */         if ((result != null) && (result != defaultValue)) {
/* 1034 */           return result;
/*      */         }
/*      */       }
/* 1037 */       if (this._ctorParameters != null) {
/* 1038 */         T result = func.withMember((AnnotatedMember)this._ctorParameters.value);
/* 1039 */         if ((result != null) && (result != defaultValue)) {
/* 1040 */           return result;
/*      */         }
/*      */       }
/* 1043 */       if (this._setters != null) {
/* 1044 */         T result = func.withMember((AnnotatedMember)this._setters.value);
/* 1045 */         if ((result != null) && (result != defaultValue)) {
/* 1046 */           return result;
/*      */         }
/*      */       }
/* 1049 */       return null;
/*      */     }
/* 1051 */     if (this._ctorParameters != null) {
/* 1052 */       T result = func.withMember((AnnotatedMember)this._ctorParameters.value);
/* 1053 */       if ((result != null) && (result != defaultValue)) {
/* 1054 */         return result;
/*      */       }
/*      */     }
/* 1057 */     if (this._setters != null) {
/* 1058 */       T result = func.withMember((AnnotatedMember)this._setters.value);
/* 1059 */       if ((result != null) && (result != defaultValue)) {
/* 1060 */         return result;
/*      */       }
/*      */     }
/* 1063 */     if (this._fields != null) {
/* 1064 */       T result = func.withMember((AnnotatedMember)this._fields.value);
/* 1065 */       if ((result != null) && (result != defaultValue)) {
/* 1066 */         return result;
/*      */       }
/*      */     }
/* 1069 */     if (this._getters != null) {
/* 1070 */       T result = func.withMember((AnnotatedMember)this._getters.value);
/* 1071 */       if ((result != null) && (result != defaultValue)) {
/* 1072 */         return result;
/*      */       }
/*      */     }
/* 1075 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static abstract interface WithMember<T>
/*      */   {
/*      */     public abstract T withMember(AnnotatedMember paramAnnotatedMember);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class MemberIterator<T extends AnnotatedMember>
/*      */     implements Iterator<T>
/*      */   {
/*      */     private Linked<T> next;
/*      */     
/*      */ 
/*      */ 
/*      */     public MemberIterator(Linked<T> first)
/*      */     {
/* 1097 */       this.next = first;
/*      */     }
/*      */     
/*      */     public boolean hasNext()
/*      */     {
/* 1102 */       return this.next != null;
/*      */     }
/*      */     
/*      */     public T next()
/*      */     {
/* 1107 */       if (this.next == null) throw new NoSuchElementException();
/* 1108 */       T result = (AnnotatedMember)this.next.value;
/* 1109 */       this.next = this.next.next;
/* 1110 */       return result;
/*      */     }
/*      */     
/*      */     public void remove()
/*      */     {
/* 1115 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static final class Linked<T>
/*      */   {
/*      */     public final T value;
/*      */     
/*      */     public final Linked<T> next;
/*      */     
/*      */     public final PropertyName name;
/*      */     
/*      */     public final boolean isNameExplicit;
/*      */     
/*      */     public final boolean isVisible;
/*      */     
/*      */     public final boolean isMarkedIgnored;
/*      */     
/*      */ 
/*      */     public Linked(T v, Linked<T> n, PropertyName name, boolean explName, boolean visible, boolean ignored)
/*      */     {
/* 1137 */       this.value = v;
/* 1138 */       this.next = n;
/*      */       
/* 1140 */       this.name = ((name == null) || (name.isEmpty()) ? null : name);
/*      */       
/* 1142 */       if (explName) {
/* 1143 */         if (this.name == null) {
/* 1144 */           throw new IllegalArgumentException("Can not pass true for 'explName' if name is null/empty");
/*      */         }
/*      */         
/*      */ 
/* 1148 */         if (!name.hasSimpleName()) {
/* 1149 */           explName = false;
/*      */         }
/*      */       }
/*      */       
/* 1153 */       this.isNameExplicit = explName;
/* 1154 */       this.isVisible = visible;
/* 1155 */       this.isMarkedIgnored = ignored;
/*      */     }
/*      */     
/*      */     public Linked<T> withoutNext() {
/* 1159 */       if (this.next == null) {
/* 1160 */         return this;
/*      */       }
/* 1162 */       return new Linked(this.value, null, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
/*      */     }
/*      */     
/*      */     public Linked<T> withValue(T newValue) {
/* 1166 */       if (newValue == this.value) {
/* 1167 */         return this;
/*      */       }
/* 1169 */       return new Linked(newValue, this.next, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
/*      */     }
/*      */     
/*      */     public Linked<T> withNext(Linked<T> newNext) {
/* 1173 */       if (newNext == this.next) {
/* 1174 */         return this;
/*      */       }
/* 1176 */       return new Linked(this.value, newNext, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
/*      */     }
/*      */     
/*      */     public Linked<T> withoutIgnored() {
/* 1180 */       if (this.isMarkedIgnored) {
/* 1181 */         return this.next == null ? null : this.next.withoutIgnored();
/*      */       }
/* 1183 */       if (this.next != null) {
/* 1184 */         Linked<T> newNext = this.next.withoutIgnored();
/* 1185 */         if (newNext != this.next) {
/* 1186 */           return withNext(newNext);
/*      */         }
/*      */       }
/* 1189 */       return this;
/*      */     }
/*      */     
/*      */     public Linked<T> withoutNonVisible() {
/* 1193 */       Linked<T> newNext = this.next == null ? null : this.next.withoutNonVisible();
/* 1194 */       return this.isVisible ? withNext(newNext) : newNext;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Linked<T> append(Linked<T> appendable)
/*      */     {
/* 1202 */       if (this.next == null) {
/* 1203 */         return withNext(appendable);
/*      */       }
/* 1205 */       return withNext(this.next.append(appendable));
/*      */     }
/*      */     
/*      */     public Linked<T> trimByVisibility() {
/* 1209 */       if (this.next == null) {
/* 1210 */         return this;
/*      */       }
/* 1212 */       Linked<T> newNext = this.next.trimByVisibility();
/* 1213 */       if (this.name != null) {
/* 1214 */         if (newNext.name == null) {
/* 1215 */           return withNext(null);
/*      */         }
/*      */         
/* 1218 */         return withNext(newNext);
/*      */       }
/* 1220 */       if (newNext.name != null) {
/* 1221 */         return newNext;
/*      */       }
/*      */       
/* 1224 */       if (this.isVisible == newNext.isVisible) {
/* 1225 */         return withNext(newNext);
/*      */       }
/* 1227 */       return this.isVisible ? withNext(null) : newNext;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/* 1232 */       String msg = this.value.toString() + "[visible=" + this.isVisible + ",ignore=" + this.isMarkedIgnored + ",explicitName=" + this.isNameExplicit + "]";
/*      */       
/* 1234 */       if (this.next != null) {
/* 1235 */         msg = msg + ", " + this.next.toString();
/*      */       }
/* 1237 */       return msg;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\POJOPropertyBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */