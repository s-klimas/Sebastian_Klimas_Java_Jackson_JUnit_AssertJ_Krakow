package pl.sebastianklimas.models;

import java.math.BigDecimal;
import java.util.Objects;

public class PaymentMethod {
    private String id;
    private int discount;
    private BigDecimal limit;

    public PaymentMethod() {
    }

    public PaymentMethod(String id, int discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentMethod that = (PaymentMethod) o;
        return discount == that.discount && Objects.equals(id, that.id) && Objects.equals(limit, that.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, discount, limit);
    }

//    @Override
//    public String toString() {
//        return "PaymentMethod; id='" + id + '\'' +" discount=" + discount + " limit=" + limit;
//    }

    public void decreaseLimit(BigDecimal amount) {
        this.limit = this.limit.subtract(amount);
    }
}
