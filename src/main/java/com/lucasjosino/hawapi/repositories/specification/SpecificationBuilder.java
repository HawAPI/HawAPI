package com.lucasjosino.hawapi.repositories.specification;

import com.lucasjosino.hawapi.enums.specification.SegmentationType;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.filters.base.BaseFilter;
import com.lucasjosino.hawapi.filters.base.BaseTranslationFilter;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

/**
 * SpecificationBuilder provides functionality for convert all Filters (extended from {@link BaseFilter}) into a
 * {@link Specification}.
 * <p> The builder will receive X values but only fields from the filter (extended from {@link BaseFilter}
 * or {@link BaseTranslationFilter}) will be used.
 *
 * @author Lucas Josino
 * @see Specification
 * @see BaseModel
 * @since 1.0.0
 */
@SuppressWarnings({"NullableProblems", "unchecked", "rawtypes"})
public class SpecificationBuilder<T extends BaseModel> implements Specification<T> {

    private static final Logger log = LoggerFactory.getLogger(SpecificationBuilder.class);

    private CriteriaBuilder builder;

    private Map<String, String> params;

    private Class<? extends BaseFilter> fClass;

    private List<UUID> uuids;

    public SpecificationBuilder() {}

    /**
     * Initialize all the required params to build the query filter.
     *
     * @param params All filters
     * @param fClass The filter class
     * @param uuids  All uuids to be filtered
     * @return The specification builder with defined params
     * @since 1.0.0
     */
    public <S extends BaseFilter> SpecificationBuilder<T> with(
            Map<String, String> params,
            Class<S> fClass,
            List<UUID> uuids
    ) {
        this.params = params;
        this.fClass = fClass;
        this.uuids = uuids;
        return this;
    }

    /**
     * Create a specification filter with specific item translation language.
     *
     * @param language The item language to be filtered.
     * @return The specification filter with defined language.
     * @throws InternalServerErrorException If table doesn't have translation table.
     * @since 1.0.0
     */
    public Specification<T> withTranslation(String language) {
        return (root, query, builder) -> {
            try {
                // Get 'translation' table.
                Join<T, Object> translation = root.join("translation", JoinType.INNER);
                // Add translation language filter.
                return builder.and(builder.equal(translation.get("language"), language));
            } catch (Exception exception) {
                String message = "Something went wrong while trying to build specification";
                log.error(message + ": {}", exception.getMessage());
                throw new InternalServerErrorException(message, exception);
            }
        };
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            this.builder = builder;

            // Models with multi-languages will require the 'translation' table.
            Join<T, Object> translation = null;
            if (BaseTranslationFilter.class.isAssignableFrom(fClass)) {
                log.debug("Filter class '{}' is assignable from 'BaseTranslationFilter'", fClass.getSimpleName());
                translation = root.join("translation", JoinType.INNER);

                // By default, 'language' is defined as 'en-US'. (Configured on the application properties)
                String language = params.get("language");

                // Language field defined as '*' will return all languages.
                if (!language.equals("*")) {
                    predicates.add(builder.equal(translation.get("language"), language));
                }

                log.debug("Defined language: {}", language);
            }

            // Loop over all fields from 'fClass'.
            //
            // Field from superclass will be ignored using 'getDeclaredFields'. E.g: language
            for (Field field : fClass.getDeclaredFields()) {
                String fieldName = field.getName();
                String fieldValue = params.get(fieldName);

                if (fieldValue == null || fieldValue.isEmpty()) {
                    log.debug("Field '{}' has null or empty value. Skipping...", fieldName);
                    continue;
                }

                log.debug("Field '{}' has value: {}", fieldName, fieldValue);

                // Calling 'root.get' using a non-existing 'fieldName' will throw 'IllegalArgumentException'.
                // If this happens, try to get from 'translation' table.
                Path<?> expression;
                try {
                    expression = root.get(fieldName);
                    log.debug("Getting value from root");
                } catch (IllegalArgumentException argumentException) {
                    log.debug("Field '{}' not found on root. Getting value from translation", fieldName);
                    // If 'fieldName' is unknown from 'root' and 'translation' is null, skip this field.
                    if (translation == null) {
                        log.warn(
                                "Couldn't find field name neither from root and translation, skipping field: {}",
                                fieldName
                        );
                        continue;
                    }

                    expression = translation.get(fieldName);
                    log.debug("Getting value from translation");
                }

                Predicate predicate = createPredicate(expression, fieldValue, field.getType());
                predicates.add(predicate);
            }
        } catch (Exception exception) {
            String message = "Something went wrong while trying to build specification";
            log.error(message + ": {}", exception.getMessage());
            throw new InternalServerErrorException(message, exception);
        }

        // Hibernate will block the usage of pagination (page and size) when using join.
        //
        // To avoid this, first select all ids(uuids) using the 'filter' pageable(page, size, sort) and then
        // add a 'where in (...)' filter.
        //
        // Ref¹: https://github.com/HawAPI/HawAPI/issues/46
        // Ref²: https://stackoverflow.com/a/62782899
        CriteriaBuilder.In<UUID> uuidsIn = builder.in(root.get("uuid"));
        uuids.forEach(uuidsIn::value);

        log.debug("Items count: {}", uuids.size());
        predicates.add(uuidsIn);

        params.clear();
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate createInPredicate(Expression expression, String fieldValue, Class<?> fieldType, boolean include) {
        // Convert 'fieldValue' in a list of strings.
        //
        // E.g:
        //  * IN(:)      = [..]?nicknames=:filter1,filter2 -> where <nicknames> in ('field1', 'field2')
        //  * NOT_IN(!:) = [..]?gender=:!0                 -> where <gender> not in ('0')
        String[] values = fieldValue.split(",");
        Predicate predicate = builder.conjunction();
        log.debug("In predicate should include: '{}'", include);

        // Check if the field type is a list. If so, we need a different approach to search words in
        // PostgreSQL array.
        if (fieldType.isAssignableFrom(String[].class)) {
            log.debug("Creating a postgres 'array_position' with predicate values: {}", fieldValue);

            for (String value : values) {
                // Function: array_position(anyarray, anyelement [, int])
                //
                // "Returns the subscript of the first occurrence of the second argument in
                // the array, starting at the element indicated by the third argument or at
                // the first element (array must be one-dimensional)"
                //
                // Ref¹: https://www.postgresql.org/docs/9.5/functions-array.html
                // Ref²: https://stackoverflow.com/a/67372002/14500144
                Expression<?> pos = builder.function(
                        "array_position",
                        String.class,
                        expression,
                        builder.literal(value)
                );

                // Join predicates.
                predicate = builder.and(predicate, include ? builder.isNotNull(pos) : builder.isNull(pos));
            }

            return predicate;
        }

        log.debug("Creating predicate with values: {}", fieldValue);
        // Normal 'in/not in' query.
        predicate = expression.in(Arrays.asList(values));
        return include ? predicate : builder.not(predicate);
    }

    private <Y extends Comparable<? super Y>> Predicate createBetweenPredicate(
            Expression expression,
            String fieldValue,
            Class<?> fieldType
    ) {
        String[] values = fieldValue.split(SegmentationType.BETWEEN.getValue());

        // By default, both 'start' and 'end' values will be strings.
        Comparable<?> startValue = values[0];
        Comparable<?> endValue = values[1];
        log.debug("Between predicate values: Start '{}' - End '{}'", startValue, endValue);

        // Convert the 'startValue' and 'endValue' into predefined field filter.

        if (fieldType.isAssignableFrom(Integer.class)) {
            startValue = Integer.parseInt(values[0]);
            endValue = Integer.parseInt(values[1]);
        }

        if (fieldType.isAssignableFrom(Byte.class)) {
            startValue = Byte.parseByte(values[0]);
            endValue = Byte.parseByte(values[1]);
        }

        if (fieldType.isAssignableFrom(LocalDate.class)) {
            startValue = LocalDate.parse(values[0]);
            endValue = LocalDate.parse(values[1]);
        }

        return builder.between(expression, (Y) startValue, (Y) endValue);
    }

    private <Y extends Comparable<? super Y>> Predicate createPredicate(
            Expression expression,
            String fieldValue,
            Class<?> fieldType
    ) {
        log.debug("Creating predicate with class type '{}'", fieldType.getSimpleName());
        SegmentationType operator = SegmentationType.get(fieldValue);

        // Remove the operator from 'fieldValue'.
        //
        // E.g:
        //  * LIKE(*)          = "*John"  -> "John"
        //  * GREATER_THAN(>)  = ">0"     -> "0"
        //  * NOT_EQUALS(!)    = "!Lorem" -> "Lorem"
        String value = fieldValue.substring(operator.getValue().length());

        log.debug("Predicate type '{}' defined with '{}' and value '{}'", operator, operator.getValue(), value);
        switch (operator) {
            case LIKE:
                return builder.like(expression, "%" + value + "%");
            case NOT_LIKE:
                return builder.notLike(expression, "%" + value + "%");
            case BETWEEN:
                return createBetweenPredicate(expression, fieldValue, fieldType);
            case NOT_IN:
                return createInPredicate(expression, value, fieldType, false);
            case IN:
                return createInPredicate(expression, value, fieldType, true);
            case GREATER_THAN:
                return builder.greaterThan(expression, (Y) value);
            case LESS_THAN:
                return builder.lessThan(expression, (Y) value);
            case GREATER_OR_EQUALS_TO:
                return builder.greaterThanOrEqualTo(expression, (Y) value);
            case LESS_OR_EQUALS_TO:
                return builder.lessThanOrEqualTo(expression, (Y) value);
            case NOT_EQUALS:
                return builder.notEqual(expression, value);
            case EQUALS:
            default:
                return builder.equal(expression, value);
        }
    }
}