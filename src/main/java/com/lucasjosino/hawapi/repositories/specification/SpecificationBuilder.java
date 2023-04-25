package com.lucasjosino.hawapi.repositories.specification;

import com.lucasjosino.hawapi.enums.specification.SegmentationType;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.filters.base.BaseFilter;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

/**
 * SpecificationBuilder provides functionality for convert all Filters (extended from {@link BaseFilter}) into a
 * {@link Specification}.
 * <p>
 * The builder will receive X values but only fields from the filter (extended from {@link BaseFilter}) will be used.
 */
@SuppressWarnings({"NullableProblems", "unchecked", "rawtypes"})
public class SpecificationBuilder<T extends BaseModel> implements Specification<T> {

    private CriteriaBuilder builder;

    private Map<String, String> params;

    private Class<? extends BaseFilter> filterClass;

    private List<UUID> uuids;

    public SpecificationBuilder() {

    }

    public <S extends BaseFilter> SpecificationBuilder<T> with(
            Map<String, String> params,
            Class<S> filterClass,
            List<UUID> uuids
    ) {
        this.params = params;
        this.filterClass = filterClass;
        this.uuids = uuids;
        return this;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            this.builder = builder;

            // Every model will require the 'translation' table.
            Join<T, Object> translation = root.join("translation", JoinType.INNER);

            // Get/Define the language value.
            //
            // By default, 'getLanguage' will always return 'en-US'. (Configured on application properties)
            String language = params.get("language");
            // With language equals to '*', the default language will be ignored. Returning all languages.
            if (!language.equals("*")) {
                predicates.add(builder.equal(translation.get("language"), language));
            }

            // Loop over all fields from 'filterClass'. If not null, create a query.
            for (Field field : filterClass.getDeclaredFields()) {
                Class<?> fieldType = field.getType();

                String fieldName = field.getName();
                String fieldValue = params.get(fieldName);

                if (fieldValue == null) continue;

                // All models will have the 'static' and 'translation' tables and all related fields.
                // Filters will contain all fields from 'static' AND 'translation'.
                //
                // Calling 'root.get' using a non-existing 'fieldName' will throw 'IllegalArgumentException'.
                // If this happens, try to get from 'translation' table.
                Path<?> expression;
                try {
                    expression = root.get(fieldName);
                } catch (IllegalArgumentException argumentException) {
                    expression = translation.get(fieldName);
                } catch (Exception exception) {
                    throw new InternalServerErrorException(exception.getMessage());
                }

                Predicate predicate = createPredicate(expression, fieldValue, fieldType);
                predicates.add(predicate);
            }
        } catch (Exception exception) {
            throw new InternalServerErrorException(exception.getMessage());
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
        predicates.add(uuidsIn);

        // The class will be reused so, clear the current 'params' map.
        params.clear();
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate createInPredicate(
            Expression expression,
            String fieldValue,
            Class<?> fieldType,
            boolean include
    ) {
        // Convert 'fieldValue' in a list of strings.
        //
        // E.g:
        //  * IN(:)      = [..]?nicknames=:filter1,filter2 -> where <nicknames> in ('field1', 'field2')
        //  * NOT_IN(!:) = [..]?gender=:!0                 -> where <gender> not in ('0')
        String[] values = fieldValue.split(",");
        Predicate predicate = builder.conjunction();

        // Check if the field type is a list. If so, we need a different approach to search words in
        // PostgreSQL array.
        if (fieldType.isAssignableFrom(String[].class)) {
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

        // Normal 'in/not in' query.
        predicate = expression.in(Arrays.stream(values).toArray());
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
            Class<?> field
    ) {
        SegmentationType operator = SegmentationType.get(fieldValue);

        // Remove the operator from 'fieldValue'.
        //
        // E.g:
        //  * LIKE(*)          = "*John"  -> "John"
        //  * GREATER_THAN(>)  = ">0"     -> "0"
        //  * NOT_EQUALS(!)    = "!Lorem" -> "Lorem"
        String value = fieldValue.substring(operator.getValue().length());

        switch (operator) {
            case LIKE:
                return builder.like(expression, "%" + value + "%");
            case NOT_LIKE:
                return builder.notLike(expression, "%" + value + "%");
            case BETWEEN:
                return createBetweenPredicate(expression, fieldValue, field);
            case NOT_IN:
                return createInPredicate(expression, fieldValue, field, false);
            case IN:
                return createInPredicate(expression, fieldValue, field, true);
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