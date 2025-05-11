package pl.sebastianklimas.services;

import pl.sebastianklimas.exceptions.NotFoundException;
import pl.sebastianklimas.models.Order;
import pl.sebastianklimas.models.PaymentMethod;
import pl.sebastianklimas.models.SuggestedBreakdown;
import pl.sebastianklimas.models.ValueCollector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Service {
    public static void sortLists(List<Order> orders, List<PaymentMethod> paymentMethods) {
        orders.sort(Comparator.comparing(Order::getValue).reversed());
        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
    }

    public static ValueCollector processOrders(List<Order> orders, List<PaymentMethod> paymentMethods) {
        ValueCollector vc = new ValueCollector(new HashMap<>(), BigDecimal.ZERO);

        for (Order order : orders) {
            SuggestedBreakdown suggestedBreakdown = simulate(order, paymentMethods);

            if (suggestedBreakdown.getMethod() != null) {
                vc.addPaidAmount(suggestedBreakdown.getMethod().getId(), suggestedBreakdown.getAmountPaidByMethod());
                reduceLimits(suggestedBreakdown, paymentMethods);
            }

            vc.addToLeftToPay(suggestedBreakdown.getLeftToPay());
        }

        return vc;
    }

    private static SuggestedBreakdown simulate(Order order, List<PaymentMethod> paymentMethods) {
        BigDecimal orderValue = order.getValue();
        BigDecimal tenPercentOfOrderValue = order.getValue().multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        PaymentMethod pointsMethod = getPointsMethod(paymentMethods);

        // Methods with better or equal discount then from all with points
        Optional<PaymentMethod> methodWithHighestDiscount = paymentMethods.stream()
                .filter(pm -> pm.getDiscount() >= pointsMethod.getDiscount())
                .filter(pm -> !order.getPromotions().isEmpty() && order.getPromotions().contains(pm.getId()))
                .filter(pm -> pm.getLimit().compareTo(order.getValue().multiply(BigDecimal.valueOf((100 - pm.getDiscount()) / 100.0).setScale(2, RoundingMode.HALF_UP))) >= 0)
                .max(Comparator.comparing(PaymentMethod::getDiscount));

        if (methodWithHighestDiscount.isPresent()) {
            return applyPromoMethod(methodWithHighestDiscount.get(), orderValue);
        }

        // All with points
        if (pointsMethod.getLimit().compareTo(orderValue.multiply(BigDecimal.valueOf((100 - pointsMethod.getDiscount()) / 100.0)).setScale(2, RoundingMode.HALF_UP)) >= 0) {
            BigDecimal discountValue = orderValue.multiply(BigDecimal.valueOf(pointsMethod.getDiscount() / 100.0)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal valueToPay = orderValue.subtract(discountValue);

            return new SuggestedBreakdown(
                    pointsMethod,
                    valueToPay,
                    BigDecimal.ZERO
            );
        }

        // Methods with better discount then pay 10% by points for 10% off and rest with any method
        Optional<PaymentMethod> methodBetterThen10Off = paymentMethods.stream()
                .filter(pm -> pm.getDiscount() > 10)
                .filter(pm -> !order.getPromotions().isEmpty() && order.getPromotions().contains(pm.getId()))
                .filter(pm -> pm.getLimit().compareTo(order.getValue().multiply(BigDecimal.valueOf((100 - pm.getDiscount()) / 100.0)).setScale(2, RoundingMode.HALF_UP)) >= 0)
                .max(Comparator.comparing(PaymentMethod::getDiscount));

        if (methodBetterThen10Off.isPresent()) {
            return applyPromoMethod(methodBetterThen10Off.get(), orderValue);
        }

        // Pay 10% by points for 10% off and rest with any method
        if (pointsMethod.getLimit().compareTo(tenPercentOfOrderValue) >= 0) {
            BigDecimal discountValue = tenPercentOfOrderValue;
            BigDecimal valueToPay = orderValue.subtract(discountValue);
            BigDecimal valueToPayByPoints = tenPercentOfOrderValue;
            BigDecimal restToPay = valueToPay.subtract(valueToPayByPoints);

            return new SuggestedBreakdown(
                    pointsMethod,
                    valueToPayByPoints,
                    restToPay
            );
        }

        // All with one payment method OR add all to left to pay
        Optional<PaymentMethod> bestMethod = paymentMethods.stream()
                .filter(pm -> !order.getPromotions().isEmpty() && order.getPromotions().contains(pm.getId()))
                .filter(pm -> pm.getLimit().compareTo(orderValue.multiply(BigDecimal.valueOf((100 - pm.getDiscount()) / 100.0).setScale(2, RoundingMode.HALF_UP))) >= 0)
                .max(Comparator.comparing(PaymentMethod::getDiscount));

        return bestMethod.map(paymentMethod -> applyPromoMethod(paymentMethod, orderValue)).orElseGet(() -> new SuggestedBreakdown(
                null,
                BigDecimal.ZERO,
                order.getValue()
        ));
    }

    private static SuggestedBreakdown applyPromoMethod(PaymentMethod method, BigDecimal orderValue) {
        BigDecimal discountValue = orderValue.multiply(BigDecimal.valueOf(method.getDiscount() / 100.0)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal paid = orderValue.subtract(discountValue);

        return new SuggestedBreakdown(
                method,
                paid,
                BigDecimal.ZERO
        );
    }

    private static void reduceLimits(SuggestedBreakdown suggestedBreakdown, List<PaymentMethod> paymentMethods) {
        PaymentMethod paymentMethod = paymentMethods.stream()
                .filter(pm -> pm != null && pm.equals(suggestedBreakdown.getMethod()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Method not found."));

        paymentMethod.decreaseLimit(suggestedBreakdown.getAmountPaidByMethod());
    }

    public static boolean checkIfNotEnoughLimits(ValueCollector vc, List<PaymentMethod> paymentMethods) {
        double sumOfLimits = paymentMethods.stream()
                .mapToDouble(pm -> pm.getLimit().doubleValue())
                .sum();

        return sumOfLimits < vc.getTotalLeftToPay().doubleValue();
    }

    public static PaymentMethod getPointsMethod(List<PaymentMethod> pml) {
        try {
            return pml.stream()
                    .filter(pm -> pm.getId().equals("PUNKTY"))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Method not found."));
        } catch (NotFoundException e) {
            System.err.println("W pliku paymentmethods.json nie ma metody 'PUNKTY'");
            return new PaymentMethod("PUNKTY", 0, BigDecimal.ZERO);
        }
    }

    public static void clearRemainingPayments(ValueCollector vc, List<PaymentMethod> paymentMethods) {
        PaymentMethod points = Service.getPointsMethod(paymentMethods);
        if (points.getLimit().compareTo(BigDecimal.ZERO) > 0) {
            clearPayment(vc, points);
        }

        for (PaymentMethod pm : paymentMethods) {
            if (pm.getLimit().compareTo(BigDecimal.ZERO) > 0) {
                if (vc.getTotalLeftToPay().compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
                clearPayment(vc, pm);
            }
        }
    }

    private static void clearPayment(ValueCollector vc, PaymentMethod paymentMethod) {
        if (paymentMethod.getLimit().compareTo(vc.getTotalLeftToPay()) <= 0) {
            vc.subtractFromLeftToPay(paymentMethod.getLimit());
            vc.addPaidAmount(paymentMethod.getId(), paymentMethod.getLimit());
            paymentMethod.setLimit(BigDecimal.ZERO);
        } else {
            paymentMethod.setLimit(paymentMethod.getLimit().subtract(vc.getTotalLeftToPay()));
            vc.addPaidAmount(paymentMethod.getId(), vc.getTotalLeftToPay());
            vc.setTotalLeftToPay(BigDecimal.ZERO);
        }
    }
}
