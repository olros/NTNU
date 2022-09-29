package com.ntnu.gidd.service.activity.expression;

import com.ntnu.gidd.model.GeoLocation;
import com.ntnu.gidd.model.QActivity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.querydsl.core.types.dsl.MathExpressions.*;
import static com.querydsl.core.types.dsl.MathExpressions.radians;

/**
 * Class used to filter activities based on location
 */

public class ActivityExpression {

    private static final int EARTH_RADIUS_IN_KM = 6371;
    private Predicate predicate;
    private Map<ActivityExpressionType, Expression<?>> expressions;

    /**
     * Constructor to instantiate a object of ActivityExpression
     * @param predicate Additional filtering to add to the ActivityExpression filtering
     */
    private ActivityExpression(Predicate predicate) {
        this.predicate = predicate;
        this.expressions = new HashMap<>();
    }

    /**
     * Method to create a ActivityExpression from the given predicate
     * @param predicate Additional filtering to add to the ActivityExpression filtering
     * @return ActivityExpression with all filtering applied
     */
    public static ActivityExpression of(Predicate predicate) {
        return new ActivityExpression(predicate);
    }

    /**
     * Method to add filtering by activities closest to given location
     * @param position the given location to filter on
     * @return the updated ActivityExpression with location filtering
     */

    public ActivityExpression closestTo(GeoLocation position) {
        QActivity activity = QActivity.activity;

        NumberPath<Double> lat = activity.geoLocation.id.lat;
        NumberPath<Double> lng = activity.geoLocation.id.lng;

        Expression<Double> positionLat = Expressions.constant(position.getLat());
        Expression<Double> positionLng = Expressions.constant(position.getLng());

        NumberExpression<Double> formula = acos(sin(radians(lat)).multiply(sin(radians(positionLat))).add(cos(radians(lat)).multiply(cos(radians(positionLat))).multiply(cos(radians(positionLng).subtract(radians(lng))))))
                .multiply(EARTH_RADIUS_IN_KM);

        expressions.put(ActivityExpressionType.CLOSEST_TO, formula);
        return this;
    }

    /**
     * Method to filter activities within a given range to a geolocation
     * @param range the given range
     * @return  the updated ActivityExpression with range filtering
     */
    public ActivityExpression range(Double range) {
        NumberExpression<Double> formula = (NumberExpression<Double>) expressions.get(ActivityExpressionType.CLOSEST_TO);
        predicate = ExpressionUtils.allOf(predicate, formula.lt(range));
        return this;
    }

    /**
     * Method to return the created predicate
     * @return predicate with filter applied from the class
     */
    public Predicate toPredicate() {
        return predicate;
    }

    @RequiredArgsConstructor
    private enum ActivityExpressionType {
        CLOSEST_TO("CLOSEST_TO");
        private final String type;
    }
}
