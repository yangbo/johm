package redis.clients.johm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import redis.clients.johm.collections.RedisList;
import redis.clients.johm.collections.RedisMap;
import redis.clients.johm.collections.RedisSet;
import redis.clients.johm.collections.RedisSortedSet;

public final class JOhmUtils {

    public static Converter converter = new ConverterImpl();

    static String getReferenceKeyName(final Field field) {
        return field.getName() + "_id";
    }

    public static String getId(final Object model) {
        return getId(model, true);
    }

    public static String getId(final Object model, boolean checkValidity) {
        String id = null;
        if (model != null) {
            if (checkValidity) {
                Validator.checkValidModel(model);
            }
            id = Validator.checkValidId(model);
        }
        return id;
    }

    static boolean isNew(final Object model) {
        return getId(model) == null;
    }

    @SuppressWarnings("unchecked")
    static void initCollections(final Object model, final Nest<?> nest) {
        if (model == null || nest == null) {
            return;
        }
        for (Field field : model.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(CollectionList.class)) {
                    Validator.checkValidCollection(field);
                    List<Object> list = (List<Object>) field.get(model);
                    if (list == null) {
                        CollectionList annotation = field
                                .getAnnotation(CollectionList.class);
                        RedisList<Object> redisList = new RedisList<Object>(
                                annotation.of(), nest, field, model);
                        field.set(model, redisList);
                    }
                }
                if (field.isAnnotationPresent(CollectionSet.class)) {
                    Validator.checkValidCollection(field);
                    Set<Object> set = (Set<Object>) field.get(model);
                    if (set == null) {
                        CollectionSet annotation = field
                                .getAnnotation(CollectionSet.class);
                        RedisSet<Object> redisSet = new RedisSet<Object>(annotation.of(),
                                nest, field, model);
                        field.set(model, redisSet);
                    }
                }
                if (field.isAnnotationPresent(CollectionSortedSet.class)) {
                    Validator.checkValidCollection(field);
                    Set<Object> sortedSet = (Set<Object>) field.get(model);
                    if (sortedSet == null) {
                        CollectionSortedSet annotation = field
                                .getAnnotation(CollectionSortedSet.class);
                        RedisSortedSet<Object> redisSortedSet = new RedisSortedSet<Object>(
                                annotation.of(), annotation.by(), nest, field, model);
                        field.set(model, redisSortedSet);
                    }
                }
                if (field.isAnnotationPresent(CollectionMap.class)) {
                    Validator.checkValidCollection(field);
                    Map<Object, Object> map = (Map<Object, Object>) field.get(model);
                    if (map == null) {
                        CollectionMap annotation = field
                                .getAnnotation(CollectionMap.class);
                        RedisMap<Object, Object> redisMap = new RedisMap<Object, Object>(
                                annotation.key(), annotation.value(), nest, field, model);
                        field.set(model, redisMap);
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidFieldException();
            } catch (IllegalAccessException e) {
                throw new InvalidFieldException();
            }
        }
    }

    static Field getIdField(final Object model) {
        for (Field field : model.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                Validator.checkValidIdType(field);
                return field;
            }
        }
        throw new JOhmException("JOhm does not support a Model without an Id",
                JOhmExceptionMeta.MISSING_MODEL_ID);
    }

    static void loadId(final Object model, final String id) {
        if (model != null) {
            boolean idFieldPresent = false;
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Id.class)) {
                    idFieldPresent = true;
                    Validator.checkValidIdType(field);
                    try {
                        Class<?> type = field.getType();
                        if (type.isAssignableFrom(Long.class)
                                || type.isAssignableFrom(long.class)) {
                            field.set(model, Long.parseLong(id));
                            break;
                        }
                        if (type.isAssignableFrom(String.class)) {
                            field.set(model, id);
                            break;
                        }
                        if (type.isAssignableFrom(UUID.class)) {
                            field.set(model, UUID.fromString(id));
                            break;
                        }
                        throw new JOhmException(
                                "Unknown Id field type. The field annotated "
                                        + "with @Id should be an a Long, a String or an UUID.",
                                JOhmExceptionMeta.ILLEGAL_ARGUMENT_EXCEPTION);
                    } catch (IllegalArgumentException e) {
                        throw new JOhmException(e,
                                JOhmExceptionMeta.ILLEGAL_ARGUMENT_EXCEPTION);
                    } catch (IllegalAccessException e) {
                        throw new JOhmException(e,
                                JOhmExceptionMeta.ILLEGAL_ACCESS_EXCEPTION);
                    }
                }
            }
            if (!idFieldPresent) {
                throw new JOhmException("JOhm does not support a Model without an Id",
                        JOhmExceptionMeta.MISSING_MODEL_ID);
            }
        }
    }

    static boolean detectJOhmCollection(final Field field) {
        boolean isJOhmCollection = false;
        if (field.isAnnotationPresent(CollectionList.class)
                || field.isAnnotationPresent(CollectionSet.class)
                || field.isAnnotationPresent(CollectionSortedSet.class)
                || field.isAnnotationPresent(CollectionMap.class)) {
            isJOhmCollection = true;
        }
        return isJOhmCollection;
    }

    public static JOhmCollectionDataType detectJOhmCollectionDataType(
            final Class<?> dataClazz) {
        JOhmCollectionDataType type = null;
        if (Validator.checkSupportedPrimitiveClazz(dataClazz)) {
            type = JOhmCollectionDataType.PRIMITIVE;
        } else {
            try {
                Validator.checkValidModelClazz(dataClazz);
                type = JOhmCollectionDataType.MODEL;
            } catch (JOhmException exception) {
                // drop it
            }
        }

        if (type == null) {
            throw new JOhmException(dataClazz.getSimpleName()
                    + " is not a supported JOhm Collection Data Type",
                    JOhmExceptionMeta.UNSUPPORTED_JOHM_COLLECTION);
        }

        return type;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNullOrEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj.getClass().equals(Collection.class)) {
            return ((Collection) obj).size() == 0;
        } else {
            if (obj.toString().trim().length() == 0) {
                return true;
            }
        }

        return false;
    }

    static List<Field> gatherAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<Field>();
        Collections.addAll(allFields, clazz.getDeclaredFields());
        while ((clazz = clazz.getSuperclass()) != null) {
            allFields.addAll(gatherAllFields(clazz));
        }

        return Collections.unmodifiableList(allFields);
    }

    public static enum JOhmCollectionDataType {
        PRIMITIVE, MODEL
    }

    static final class Validator {
        static void checkValidAttribute(final Field field) {
            Class<?> type = field.getType();
            if (!converter.isSupportedPrimitive(type)) {

                throw new JOhmException(field.getType().getSimpleName()
                        + " is not a JOhm-supported Attribute",
                        JOhmExceptionMeta.UNSUPPORTED_JOHM_ATTRIBUTE);

            }
        }

        static void checkValidReference(final Field field) {
            if (!field.getType().getClass().isInstance(Model.class)) {
                throw new JOhmException(field.getType().getSimpleName()
                        + " is not a subclass of Model",
                        JOhmExceptionMeta.MISSING_MODEL_ANNOTATION);
            }
        }

        static String checkValidId(final Object model) {
            String id = null;
            boolean idFieldPresent = false;
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Id.class)) {
                    Validator.checkValidIdType(field);
                    try {
                        Class<?> type = field.getType();
                        Object idRawValue = field.get(model);
                        // primitive type long should not be valid
                        if (type.isAssignableFrom(Long.class)) {
                            id = (idRawValue == null) ? null : ((Long) idRawValue)
                                    .toString();
                        } else if (type.isAssignableFrom(String.class)) {
                            id = (String) idRawValue;
                        } else if (type.isAssignableFrom(UUID.class)) {
                            id = (idRawValue == null) ? null : ((UUID) idRawValue)
                                    .toString();
                        } else {
                            throw new JOhmException(
                                    "Unknown Id field type. The field annotated "
                                            + "with @Id should be a Long, a String or an UUID.",
                                    JOhmExceptionMeta.ILLEGAL_ARGUMENT_EXCEPTION);
                        }
                        idFieldPresent = true;
                    } catch (IllegalArgumentException e) {
                        throw new JOhmException(e,
                                JOhmExceptionMeta.ILLEGAL_ARGUMENT_EXCEPTION);
                    } catch (IllegalAccessException e) {
                        throw new JOhmException(e,
                                JOhmExceptionMeta.ILLEGAL_ACCESS_EXCEPTION);
                    }
                    break;
                }
            }
            if (!idFieldPresent) {
                throw new JOhmException("JOhm does not support a Model without an Id",
                        JOhmExceptionMeta.MISSING_MODEL_ID);
            }
            return id;
        }

        static void checkValidIdType(final Field field) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations.length > 1) {
                for (Annotation annotation : annotations) {
                    Class<?> annotationType = annotation.annotationType();
                    if (annotationType.equals(Id.class)) {
                        continue;
                    }
                    // FIXME(rpoittevin) Should handle a @AutoInc annotation
                    if (JOHM_SUPPORTED_ANNOTATIONS.contains(annotationType)) {
                        throw new JOhmException(
                                "Element annotated @Id cannot have any other JOhm annotations",
                                JOhmExceptionMeta.INVALID_MODEL_ID_ANNOTATIONS);
                    }
                }
            }
            Class<?> type = field.getType();
            if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
                return;
            }
            if (type.isAssignableFrom(String.class)) {
                return;
            }
            if (type.isAssignableFrom(UUID.class)) {
                return;
            }
            throw new JOhmException(
                    field.getType().getSimpleName()
                            + " is annotated an Id but is not a valid type. Valid types are long, String and UUID",
                    JOhmExceptionMeta.INVALID_MODEL_ID_TYPE);
        }

        static boolean isIndexable(final String attributeName) {
            // Prevent null/empty keys and null/empty values
            return !isNullOrEmpty(attributeName);
        }

        static void checkValidModel(final Object model) {
            checkValidModelClazz(model.getClass());
        }

        static void checkValidModelClazz(final Class<?> modelClazz) {
            if (!modelClazz.isAnnotationPresent(Model.class)) {
                throw new JOhmException(
                        "Class pretending to be a Model but is not really annotated",
                        JOhmExceptionMeta.MISSING_MODEL_ANNOTATION);
            }
            if (modelClazz.isInterface()) {
                throw new JOhmException("An interface cannot be annotated as a Model",
                        JOhmExceptionMeta.INVALID_MODEL_ANNOTATION);
            }
        }

        static void checkSupportAll(final Class<?> modelClazz) {
            if (!modelClazz.isAnnotationPresent(SupportAll.class)) {
                throw new JOhmException(
                        "This Model doesn't support getAll(). Please annotate with @SupportAll",
                        JOhmExceptionMeta.MISSING_MODEL_ANNOTATION);

            }
        }

        static void checkValidCollection(final Field field) {
            boolean isList = false, isSet = false, isMap = false, isSortedSet = false;
            if (field.isAnnotationPresent(CollectionList.class)) {
                checkValidCollectionList(field);
                isList = true;
            }
            if (field.isAnnotationPresent(CollectionSet.class)) {
                checkValidCollectionSet(field);
                isSet = true;
            }
            if (field.isAnnotationPresent(CollectionSortedSet.class)) {
                checkValidCollectionSortedSet(field);
                isSortedSet = true;
            }
            if (field.isAnnotationPresent(CollectionMap.class)) {
                checkValidCollectionMap(field);
                isMap = true;
            }
            if (isList && isSet && isMap && isSortedSet) {
                throw new JOhmException(
                        field.getName()
                                + " can be declared a List or a Set or a SortedSet or a Map but not more than one type",
                        JOhmExceptionMeta.INVALID_COLLECTION_ANNOTATION);
            }
        }

        static void checkValidCollectionList(final Field field) {
            if (!field.getType().isAssignableFrom(List.class)) {
                throw new JOhmException(field.getType().getSimpleName()
                        + " is not a subclass of List",
                        JOhmExceptionMeta.INVALID_COLLECTION_SUBTYPE);
            }
        }

        static void checkValidCollectionSet(final Field field) {
            if (!field.getType().isAssignableFrom(Set.class)) {
                throw new JOhmException(field.getType().getSimpleName()
                        + " is not a subclass of Set",
                        JOhmExceptionMeta.INVALID_COLLECTION_SUBTYPE);
            }
        }

        static void checkValidCollectionSortedSet(final Field field) {
            if (!field.getType().isAssignableFrom(Set.class)) {
                throw new JOhmException(field.getType().getSimpleName()
                        + " is not a subclass of Set",
                        JOhmExceptionMeta.INVALID_COLLECTION_SUBTYPE);
            }
        }

        static void checkValidCollectionMap(final Field field) {
            if (!field.getType().isAssignableFrom(Map.class)) {
                throw new JOhmException(field.getType().getSimpleName()
                        + " is not a subclass of Map",
                        JOhmExceptionMeta.INVALID_COLLECTION_SUBTYPE);
            }
        }

        static void checkValidArrayBounds(final Field field, int actualLength) {
            if (field.getAnnotation(Array.class).length() < actualLength) {
                throw new JOhmException(
                        field.getType().getSimpleName()
                                + " has an actual length greater than the expected annotated array bounds",
                        JOhmExceptionMeta.INVALID_ARRAY_BOUNDS);
            }
        }

        static void checkAttributeReferenceIndexRules(final Field field) {
            boolean isAttribute = field.isAnnotationPresent(Attribute.class);
            boolean isReference = field.isAnnotationPresent(Reference.class);
            boolean isIndexed = field.isAnnotationPresent(Indexed.class);
            if (isAttribute) {
                if (isReference) {
                    throw new JOhmException(field.getName()
                            + " is both an Attribute and a Reference which is invalid",
                            JOhmExceptionMeta.INVALID_ATTRIBUTE_AND_REFERENCE);
                }
                if (isIndexed) {
                    if (!isIndexable(field.getName())) {
                        throw new InvalidFieldException();
                    }
                }
                if (field.getType().equals(Model.class)) {
                    throw new JOhmException(field.getType().getSimpleName()
                            + " is an Attribute and a Model which is invalid",
                            JOhmExceptionMeta.INVALID_ATTRIBUTE_AND_MODEL);
                }
                checkValidAttribute(field);
            }
            if (isReference) {
                checkValidReference(field);
            }
        }

        public static boolean checkSupportedPrimitiveClazz(final Class<?> primitiveClazz) {
            return converter.isSupportedPrimitive(primitiveClazz);
        }
    }

    private static final Set<Class<?>> JOHM_SUPPORTED_ANNOTATIONS = new HashSet<Class<?>>();
    static {

        JOHM_SUPPORTED_ANNOTATIONS.add(Array.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(Attribute.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(CollectionList.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(CollectionMap.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(CollectionSet.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(CollectionSortedSet.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(Id.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(Indexed.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(Model.class);
        JOHM_SUPPORTED_ANNOTATIONS.add(Reference.class);
    }
}