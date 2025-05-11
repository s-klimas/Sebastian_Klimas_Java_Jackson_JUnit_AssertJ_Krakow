package pl.sebastianklimas.services;

import org.junit.jupiter.api.Test;
import pl.sebastianklimas.models.Order;
import pl.sebastianklimas.models.PaymentMethod;
import pl.sebastianklimas.models.ValueCollector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    void testing_sortLists_whenUnsorted_ShouldSort() {
        // given
        Order o1 = new Order(
                "O1", BigDecimal.valueOf(20), List.of("P1, P2")
        );
        Order o2 = new Order(
                "O2", BigDecimal.valueOf(30)
        );
        Order o3 = new Order(
                "O3", BigDecimal.valueOf(10), List.of("P1, P3")
        );
        PaymentMethod p1 = new PaymentMethod(
                "P1", 5, BigDecimal.valueOf(100)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P2", 3, BigDecimal.valueOf(200)
        );
        PaymentMethod p3 = new PaymentMethod(
                "P3", 10, BigDecimal.valueOf(25)
        );
        PaymentMethod p4 = new PaymentMethod(
                "P4", 7, BigDecimal.valueOf(100)
        );
        List<Order> orders = new ArrayList<>();
        orders.add(o1);
        orders.add(o2);
        orders.add(o3);
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);
        paymentMethods.add(p3);
        paymentMethods.add(p4);

        // when
        Service.sortLists(orders, paymentMethods);

        // then
        assertEquals(orders.get(0), o2);
        assertEquals(orders.get(1), o1);
        assertEquals(orders.get(2), o3);
        assertEquals(paymentMethods.get(0), p3);
        assertEquals(paymentMethods.get(1), p4);
        assertEquals(paymentMethods.get(2), p1);
        assertEquals(paymentMethods.get(3), p2);
    }

    @Test
    void testing_processOrders_whenOfferBetterThenAllWithPoints_ShouldReturnValueCollector() {
        // given
        Order o1 = new Order(
                "O1", BigDecimal.valueOf(100), List.of("P1")
        );
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(100)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 20, BigDecimal.valueOf(100)
        );
        List<Order> orders = new ArrayList<>();
        orders.add(o1);
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        ValueCollector vc = Service.processOrders(orders, paymentMethods);

        // then
        assertThat(vc.getPaymentMethodsAndAmountUsed().size()).isEqualTo(1);
        assertThat(vc.getTotalLeftToPay().compareTo(BigDecimal.ZERO) == 0).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().containsKey(p2.getId())).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().get(p2.getId()).compareTo(BigDecimal.valueOf(80)) == 0).isTrue();
        assertThat(p2.getLimit().compareTo(BigDecimal.valueOf(20)) == 0).isTrue();
    }

    @Test
    void testing_processOrders_whenAllWithPointsIsBest_ShouldReturnValueCollector() {
        // given
        Order o1 = new Order(
                "O1", BigDecimal.valueOf(100), List.of("P1")
        );
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(100)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 3, BigDecimal.valueOf(100)
        );
        List<Order> orders = new ArrayList<>();
        orders.add(o1);
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        ValueCollector vc = Service.processOrders(orders, paymentMethods);

        // then
        assertThat(vc.getPaymentMethodsAndAmountUsed().size()).isEqualTo(1);
        assertThat(vc.getTotalLeftToPay().compareTo(BigDecimal.ZERO) == 0).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().containsKey(p1.getId())).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().get(p1.getId()).compareTo(BigDecimal.valueOf(85)) == 0).isTrue();
        assertThat(p1.getLimit().compareTo(BigDecimal.valueOf(15)) == 0).isTrue();
    }

    @Test
    void testing_processOrder_whenNotEnoughPointsForAllWIthPointsButBetterThen10PercentOff_ShouldReturnValueCollector() {
        // given
        Order o1 = new Order(
                "O1", BigDecimal.valueOf(100), List.of("P1")
        );
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(70)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 12, BigDecimal.valueOf(100)
        );
        List<Order> orders = new ArrayList<>();
        orders.add(o1);
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        ValueCollector vc = Service.processOrders(orders, paymentMethods);

        // then
        assertThat(vc.getPaymentMethodsAndAmountUsed().size()).isEqualTo(1);
        assertThat(vc.getTotalLeftToPay().compareTo(BigDecimal.ZERO) == 0).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().containsKey(p2.getId())).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().get(p2.getId()).compareTo(BigDecimal.valueOf(88)) == 0).isTrue();
        assertThat(p2.getLimit().compareTo(BigDecimal.valueOf(12)) == 0).isTrue();
    }

    @Test
    void testing_processOrders_whenBestMethodIs10PercentOff_ShouldReturnValueCollector() {
        // given
        Order o1 = new Order(
                "O1", BigDecimal.valueOf(100), List.of("P1")
        );
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(70)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 10, BigDecimal.valueOf(100)
        );
        List<Order> orders = new ArrayList<>();
        orders.add(o1);
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        ValueCollector vc = Service.processOrders(orders, paymentMethods);

        // then
        assertThat(vc.getPaymentMethodsAndAmountUsed().size()).isEqualTo(1);
        assertThat(vc.getTotalLeftToPay().compareTo(BigDecimal.valueOf(80)) == 0).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().containsKey(p1.getId())).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().get(p1.getId()).compareTo(BigDecimal.valueOf(10)) == 0).isTrue();
        assertThat(p1.getLimit().compareTo(BigDecimal.valueOf(60)) == 0).isTrue();
    }

    @Test
    void testing_processOrders_whenGettingDiscountFromPromotion_ShouldReturnValueCollector() {
        // given
        Order o1 = new Order(
                "O1", BigDecimal.valueOf(100), List.of("P2")
        );
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(5)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 5, BigDecimal.valueOf(100)
        );
        List<Order> orders = new ArrayList<>();
        orders.add(o1);
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        ValueCollector vc = Service.processOrders(orders, paymentMethods);

        // then
        assertThat(vc.getPaymentMethodsAndAmountUsed().size()).isEqualTo(0);
        assertThat(vc.getTotalLeftToPay().compareTo(BigDecimal.valueOf(100)) == 0).isTrue();
    }

    @Test
    void testing_checkIfNotEnoughLimits_whenNotEnoughLimits_ShouldThrowException() {
        // given
        ValueCollector vc = new ValueCollector(Map.of(), BigDecimal.valueOf(100));
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(10)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 5, BigDecimal.valueOf(10)
        );
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        boolean isNotEnough = Service.checkIfNotEnoughLimits(vc, paymentMethods);

        // then
        assertThat(isNotEnough).isTrue();
    }

    @Test
    void testing_checkIfNotEnoughLimits_whenEnoughLimits_ShouldNotThrowException() {
        // given
        ValueCollector vc = new ValueCollector(Map.of(), BigDecimal.valueOf(100));
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(50)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 5, BigDecimal.valueOf(50)
        );
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        boolean isNotEnough = Service.checkIfNotEnoughLimits(vc, paymentMethods);

        // then
        assertThat(isNotEnough).isFalse();
    }

    @Test
    void testing_getPointsMethod_whenPointMethodExists_ShouldReturnPointsPaymentMethod() {
        // given
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(50)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 5, BigDecimal.valueOf(50)
        );
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        PaymentMethod pointsMethod = Service.getPointsMethod(paymentMethods);

        // then
        assertThat(pointsMethod.getId()).isEqualTo("PUNKTY");
        assertThat(pointsMethod.getLimit().compareTo(BigDecimal.valueOf(50)) == 0).isTrue();
        assertEquals(15, pointsMethod.getDiscount());
    }

    @Test
    void testing_getPointsMethod_whenPointMethodNotExists_ShouldReturnPointsPaymentMethodWithoutDiscount() {
        // given
        PaymentMethod p2 = new PaymentMethod(
                "P1", 5, BigDecimal.valueOf(50)
        );
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p2);

        // when
        PaymentMethod pointsMethod = Service.getPointsMethod(paymentMethods);

        // then
        assertThat(pointsMethod.getId()).isEqualTo("PUNKTY");
        assertThat(pointsMethod.getLimit().compareTo(BigDecimal.valueOf(0)) == 0).isTrue();
        assertEquals(0, pointsMethod.getDiscount());
    }

    @Test
    void testing_clearRemainingPayments_ShouldClear() {
        // given
        Map<String, BigDecimal> emptyMap = new HashMap<>();
        ValueCollector vc = new ValueCollector(emptyMap, BigDecimal.valueOf(100));
        PaymentMethod p1 = new PaymentMethod(
                "PUNKTY", 15, BigDecimal.valueOf(50)
        );
        PaymentMethod p2 = new PaymentMethod(
                "P1", 5, BigDecimal.valueOf(50)
        );
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(p1);
        paymentMethods.add(p2);

        // when
        Service.clearRemainingPayments(vc, paymentMethods);

        // then
        assertThat(vc.getTotalLeftToPay().compareTo(BigDecimal.valueOf(0)) == 0).isTrue();
        assertThat(p1.getLimit().compareTo(BigDecimal.valueOf(0)) == 0).isTrue();
        assertThat(p2.getLimit().compareTo(BigDecimal.valueOf(0)) == 0).isTrue();
        assertThat(vc.getPaymentMethodsAndAmountUsed().size()).isEqualTo(2);
        assertThat(vc.getPaymentMethodsAndAmountUsed().get(p1.getId())).isEqualTo(BigDecimal.valueOf(50));
        assertThat(vc.getPaymentMethodsAndAmountUsed().get(p2.getId())).isEqualTo(BigDecimal.valueOf(50));
    }
}