package pl.sebastianklimas.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private String id;
    private BigDecimal value;
    private List<String> promotions = new ArrayList<>();

    public Order() {
    }

    public Order(String id, BigDecimal value) {
        this.id = id;
        this.value = value;
    }

    public Order(String id, BigDecimal value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<String> promotions) {
        this.promotions = promotions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(value, order.value) && Objects.equals(promotions, order.promotions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, promotions);
    }

    //    @Override
//    public String toString() {
//        return "Order: id='" + id + '\'' + " value=" + value + " promotions=" + promotions;
//    }
}
