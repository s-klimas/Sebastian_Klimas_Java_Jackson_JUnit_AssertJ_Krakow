package pl.sebastianklimas.models;

import java.math.BigDecimal;

public class SuggestedBreakdown {
    private PaymentMethod method;
    private BigDecimal amountPaidByMethod;
    private BigDecimal leftToPay;

    public SuggestedBreakdown() {
    }

    public SuggestedBreakdown(PaymentMethod method, BigDecimal amountPaidByMethod, BigDecimal leftToPay) {
        this.method = method;
        this.amountPaidByMethod = amountPaidByMethod;
        this.leftToPay = leftToPay;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public BigDecimal getAmountPaidByMethod() {
        return amountPaidByMethod;
    }

    public void setAmountPaidByMethod(BigDecimal amountPaidByMethod) {
        this.amountPaidByMethod = amountPaidByMethod;
    }

    public BigDecimal getLeftToPay() {
        return leftToPay;
    }

    public void setLeftToPay(BigDecimal leftToPay) {
        this.leftToPay = leftToPay;
    }

    @Override
    public String toString() {
        return method == null ? "SuggestedBreakdown: Method = BRAK, amountPaidByMethod = " + amountPaidByMethod + ", leftToPay = " + leftToPay
        : "SuggestedBreakdown: Method = " + method.getId() + ", amountPaidByMethod = " + amountPaidByMethod + ", leftToPay = " + leftToPay;
    }
}
