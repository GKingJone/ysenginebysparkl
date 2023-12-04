/*     */ package com.facebook.presto.jdbc.internal.guava.reflect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Joiner;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicates;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList.Builder;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Iterables;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ final class Types
/*     */ {
/*  56 */   private static final Function<Type, String> TYPE_NAME = new Function()
/*     */   {
/*     */     public String apply(Type from) {
/*  59 */       return JavaVersion.CURRENT.typeName(from);
/*     */     }
/*     */   };
/*     */   
/*  63 */   private static final Joiner COMMA_JOINER = Joiner.on(", ").useForNull("null");
/*     */   
/*     */   static Type newArrayType(Type componentType)
/*     */   {
/*  67 */     if ((componentType instanceof WildcardType)) {
/*  68 */       WildcardType wildcard = (WildcardType)componentType;
/*  69 */       Type[] lowerBounds = wildcard.getLowerBounds();
/*  70 */       Preconditions.checkArgument(lowerBounds.length <= 1, "Wildcard cannot have more than one lower bounds.");
/*  71 */       if (lowerBounds.length == 1) {
/*  72 */         return supertypeOf(newArrayType(lowerBounds[0]));
/*     */       }
/*  74 */       Type[] upperBounds = wildcard.getUpperBounds();
/*  75 */       Preconditions.checkArgument(upperBounds.length == 1, "Wildcard should have only one upper bound.");
/*  76 */       return subtypeOf(newArrayType(upperBounds[0]));
/*     */     }
/*     */     
/*  79 */     return JavaVersion.CURRENT.newArrayType(componentType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static ParameterizedType newParameterizedTypeWithOwner(@Nullable Type ownerType, Class<?> rawType, Type... arguments)
/*     */   {
/*  88 */     if (ownerType == null) {
/*  89 */       return newParameterizedType(rawType, arguments);
/*     */     }
/*     */     
/*  92 */     Preconditions.checkNotNull(arguments);
/*  93 */     Preconditions.checkArgument(rawType.getEnclosingClass() != null, "Owner type for unenclosed %s", new Object[] { rawType });
/*  94 */     return new ParameterizedTypeImpl(ownerType, rawType, arguments);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */   static ParameterizedType newParameterizedType(Class<?> rawType, Type... arguments) { return new ParameterizedTypeImpl(ClassOwnership.JVM_BEHAVIOR.getOwnerType(rawType), rawType, arguments); }
/*     */   
/*     */   private static abstract enum ClassOwnership { private ClassOwnership() {}
/*     */     
/*     */     @Nullable
/*     */     abstract Class<?> getOwnerType(Class<?> paramClass);
/*     */     
/* 109 */     OWNED_BY_ENCLOSING_CLASS, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 116 */     LOCAL_CLASS_HAS_NO_OWNER;
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
/* 130 */     static final ClassOwnership JVM_BEHAVIOR = detectJvmBehavior();
/*     */     
/*     */     private static ClassOwnership detectJvmBehavior()
/*     */     {
/* 134 */       Class<?> subclass = new 1LocalClass() {}.getClass();
/* 135 */       ParameterizedType parameterizedType = (ParameterizedType)subclass.getGenericSuperclass();
/*     */       
/* 137 */       for (ClassOwnership behavior : values()) {
/* 138 */         if (behavior.getOwnerType(1LocalClass.class) == parameterizedType.getOwnerType()) {
/* 139 */           return behavior;
/*     */         }
/*     */       }
/* 142 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static <D extends GenericDeclaration> TypeVariable<D> newArtificialTypeVariable(D declaration, String name, Type... bounds)
/*     */   {
/* 152 */     return new TypeVariableImpl(declaration, name, bounds.length == 0 ? new Type[] { Object.class } : bounds);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @VisibleForTesting
/*     */   static WildcardType subtypeOf(Type upperBound)
/*     */   {
/* 162 */     return new WildcardTypeImpl(new Type[0], new Type[] { upperBound });
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static WildcardType supertypeOf(Type lowerBound) {
/* 167 */     return new WildcardTypeImpl(new Type[] { lowerBound }, new Type[] { Object.class });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static String toString(Type type)
/*     */   {
/* 180 */     return (type instanceof Class) ? ((Class)type).getName() : type.toString();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   static Type getComponentType(Type type)
/*     */   {
/* 186 */     Preconditions.checkNotNull(type);
/* 187 */     AtomicReference<Type> result = new AtomicReference();
/* 188 */     new TypeVisitor() {
/*     */       void visitTypeVariable(TypeVariable<?> t) {
/* 190 */         this.val$result.set(Types.subtypeOfComponentType(t.getBounds()));
/*     */       }
/*     */       
/* 193 */       void visitWildcardType(WildcardType t) { this.val$result.set(Types.subtypeOfComponentType(t.getUpperBounds())); }
/*     */       
/*     */       void visitGenericArrayType(GenericArrayType t) {
/* 196 */         this.val$result.set(t.getGenericComponentType());
/*     */       }
/*     */       
/* 199 */       void visitClass(Class<?> t) { this.val$result.set(t.getComponentType()); } }.visit(new Type[] { type });
/*     */     
/*     */ 
/* 202 */     return (Type)result.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Nullable
/*     */   private static Type subtypeOfComponentType(Type[] bounds)
/*     */   {
/* 210 */     for (Type bound : bounds) {
/* 211 */       Type componentType = getComponentType(bound);
/* 212 */       if (componentType != null)
/*     */       {
/*     */ 
/* 215 */         if ((componentType instanceof Class)) {
/* 216 */           Class<?> componentClass = (Class)componentType;
/* 217 */           if (componentClass.isPrimitive()) {
/* 218 */             return componentClass;
/*     */           }
/*     */         }
/* 221 */         return subtypeOf(componentType);
/*     */       }
/*     */     }
/* 224 */     return null;
/*     */   }
/*     */   
/*     */   private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable
/*     */   {
/*     */     private final Type componentType;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     GenericArrayTypeImpl(Type componentType) {
/* 233 */       this.componentType = JavaVersion.CURRENT.usedInGenericType(componentType);
/*     */     }
/*     */     
/*     */     public Type getGenericComponentType() {
/* 237 */       return this.componentType;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 241 */       return String.valueOf(Types.toString(this.componentType)).concat("[]");
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 245 */       return this.componentType.hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 249 */       if ((obj instanceof GenericArrayType)) {
/* 250 */         GenericArrayType that = (GenericArrayType)obj;
/* 251 */         return Objects.equal(getGenericComponentType(), that.getGenericComponentType());
/*     */       }
/*     */       
/* 254 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class ParameterizedTypeImpl
/*     */     implements ParameterizedType, Serializable
/*     */   {
/*     */     private final Type ownerType;
/*     */     private final ImmutableList<Type> argumentsList;
/*     */     private final Class<?> rawType;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ParameterizedTypeImpl(@Nullable Type ownerType, Class<?> rawType, Type[] typeArguments)
/*     */     {
/* 269 */       Preconditions.checkNotNull(rawType);
/* 270 */       Preconditions.checkArgument(typeArguments.length == rawType.getTypeParameters().length);
/* 271 */       Types.disallowPrimitiveType(typeArguments, "type parameter");
/* 272 */       this.ownerType = ownerType;
/* 273 */       this.rawType = rawType;
/* 274 */       this.argumentsList = JavaVersion.CURRENT.usedInGenericType(typeArguments);
/*     */     }
/*     */     
/*     */     public Type[] getActualTypeArguments() {
/* 278 */       return Types.toArray(this.argumentsList);
/*     */     }
/*     */     
/*     */     public Type getRawType() {
/* 282 */       return this.rawType;
/*     */     }
/*     */     
/*     */     public Type getOwnerType() {
/* 286 */       return this.ownerType;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 290 */       StringBuilder builder = new StringBuilder();
/* 291 */       if (this.ownerType != null) {
/* 292 */         builder.append(JavaVersion.CURRENT.typeName(this.ownerType)).append('.');
/*     */       }
/* 294 */       builder.append(this.rawType.getName()).append('<').append(Types.COMMA_JOINER.join(Iterables.transform(this.argumentsList, Types.TYPE_NAME))).append('>');
/*     */       
/*     */ 
/*     */ 
/* 298 */       return builder.toString();
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 302 */       return (this.ownerType == null ? 0 : this.ownerType.hashCode()) ^ this.argumentsList.hashCode() ^ this.rawType.hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object other)
/*     */     {
/* 307 */       if (!(other instanceof ParameterizedType)) {
/* 308 */         return false;
/*     */       }
/* 310 */       ParameterizedType that = (ParameterizedType)other;
/* 311 */       return (getRawType().equals(that.getRawType())) && (Objects.equal(getOwnerType(), that.getOwnerType())) && (Arrays.equals(getActualTypeArguments(), that.getActualTypeArguments()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class TypeVariableImpl<D extends GenericDeclaration>
/*     */     implements TypeVariable<D>
/*     */   {
/*     */     private final D genericDeclaration;
/*     */     
/*     */     private final String name;
/*     */     
/*     */     private final ImmutableList<Type> bounds;
/*     */     
/*     */ 
/*     */     TypeVariableImpl(D genericDeclaration, String name, Type[] bounds)
/*     */     {
/* 328 */       Types.disallowPrimitiveType(bounds, "bound for type variable");
/* 329 */       this.genericDeclaration = ((GenericDeclaration)Preconditions.checkNotNull(genericDeclaration));
/* 330 */       this.name = ((String)Preconditions.checkNotNull(name));
/* 331 */       this.bounds = ImmutableList.copyOf(bounds);
/*     */     }
/*     */     
/*     */     public Type[] getBounds() {
/* 335 */       return Types.toArray(this.bounds);
/*     */     }
/*     */     
/*     */     public D getGenericDeclaration() {
/* 339 */       return this.genericDeclaration;
/*     */     }
/*     */     
/*     */     public String getName() {
/* 343 */       return this.name;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 347 */       return this.name;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 351 */       return this.genericDeclaration.hashCode() ^ this.name.hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 355 */       if (NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY)
/*     */       {
/* 357 */         if ((obj instanceof TypeVariableImpl)) {
/* 358 */           TypeVariableImpl<?> that = (TypeVariableImpl)obj;
/* 359 */           return (this.name.equals(that.getName())) && (this.genericDeclaration.equals(that.getGenericDeclaration())) && (this.bounds.equals(that.bounds));
/*     */         }
/*     */         
/*     */ 
/* 363 */         return false;
/*     */       }
/*     */       
/* 366 */       if ((obj instanceof TypeVariable)) {
/* 367 */         TypeVariable<?> that = (TypeVariable)obj;
/* 368 */         return (this.name.equals(that.getName())) && (this.genericDeclaration.equals(that.getGenericDeclaration()));
/*     */       }
/*     */       
/* 371 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class WildcardTypeImpl implements WildcardType, Serializable
/*     */   {
/*     */     private final ImmutableList<Type> lowerBounds;
/*     */     private final ImmutableList<Type> upperBounds;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
/* 382 */       Types.disallowPrimitiveType(lowerBounds, "lower bound for wildcard");
/* 383 */       Types.disallowPrimitiveType(upperBounds, "upper bound for wildcard");
/* 384 */       this.lowerBounds = JavaVersion.CURRENT.usedInGenericType(lowerBounds);
/* 385 */       this.upperBounds = JavaVersion.CURRENT.usedInGenericType(upperBounds);
/*     */     }
/*     */     
/*     */     public Type[] getLowerBounds() {
/* 389 */       return Types.toArray(this.lowerBounds);
/*     */     }
/*     */     
/*     */     public Type[] getUpperBounds() {
/* 393 */       return Types.toArray(this.upperBounds);
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 397 */       if ((obj instanceof WildcardType)) {
/* 398 */         WildcardType that = (WildcardType)obj;
/* 399 */         return (this.lowerBounds.equals(Arrays.asList(that.getLowerBounds()))) && (this.upperBounds.equals(Arrays.asList(that.getUpperBounds())));
/*     */       }
/*     */       
/* 402 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 406 */       return this.lowerBounds.hashCode() ^ this.upperBounds.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 410 */       StringBuilder builder = new StringBuilder("?");
/* 411 */       for (Type lowerBound : this.lowerBounds) {
/* 412 */         builder.append(" super ").append(JavaVersion.CURRENT.typeName(lowerBound));
/*     */       }
/* 414 */       for (Type upperBound : Types.filterUpperBounds(this.upperBounds)) {
/* 415 */         builder.append(" extends ").append(JavaVersion.CURRENT.typeName(upperBound));
/*     */       }
/* 417 */       return builder.toString();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static Type[] toArray(Collection<Type> types)
/*     */   {
/* 424 */     return (Type[])types.toArray(new Type[types.size()]);
/*     */   }
/*     */   
/*     */   private static Iterable<Type> filterUpperBounds(Iterable<Type> bounds) {
/* 428 */     return Iterables.filter(bounds, Predicates.not(Predicates.equalTo(Object.class)));
/*     */   }
/*     */   
/*     */   private static void disallowPrimitiveType(Type[] types, String usedAs)
/*     */   {
/* 433 */     for (Type type : types) {
/* 434 */       if ((type instanceof Class)) {
/* 435 */         Class<?> cls = (Class)type;
/* 436 */         Preconditions.checkArgument(!cls.isPrimitive(), "Primitive type '%s' used as %s", new Object[] { cls, usedAs });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Class<?> getArrayClass(Class<?> componentType)
/*     */   {
/* 447 */     return Array.newInstance(componentType, 0).getClass();
/*     */   }
/*     */   
/*     */ 
/*     */   static abstract enum JavaVersion
/*     */   {
/* 453 */     JAVA6, 
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
/* 468 */     JAVA7, 
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
/* 480 */     JAVA8;
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
/*     */     static final JavaVersion CURRENT;
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
/*     */     static
/*     */     {
/* 504 */       if (AnnotatedElement.class.isAssignableFrom(TypeVariable.class)) {
/* 505 */         CURRENT = JAVA8;
/* 506 */       } else if ((new TypeCapture() {}.capture() instanceof Class)) {
/* 507 */         CURRENT = JAVA7;
/*     */       } else {
/* 509 */         CURRENT = JAVA6;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     String typeName(Type type)
/*     */     {
/* 516 */       return Types.toString(type);
/*     */     }
/*     */     
/*     */     final ImmutableList<Type> usedInGenericType(Type[] types) {
/* 520 */       Builder<Type> builder = ImmutableList.builder();
/* 521 */       for (Type type : types) {
/* 522 */         builder.add(usedInGenericType(type));
/*     */       }
/* 524 */       return builder.build();
/*     */     }
/*     */     
/*     */ 
/*     */     private JavaVersion() {}
/*     */     
/*     */ 
/*     */     abstract Type newArrayType(Type paramType);
/*     */     
/*     */ 
/*     */     abstract Type usedInGenericType(Type paramType);
/*     */   }
/*     */   
/*     */   static final class NativeTypeVariableEquals<X>
/*     */   {
/* 539 */     static final boolean NATIVE_TYPE_VARIABLE_ONLY = !NativeTypeVariableEquals.class.getTypeParameters()[0].equals(Types.newArtificialTypeVariable(NativeTypeVariableEquals.class, "X", new Type[0]));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\reflect\Types.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */