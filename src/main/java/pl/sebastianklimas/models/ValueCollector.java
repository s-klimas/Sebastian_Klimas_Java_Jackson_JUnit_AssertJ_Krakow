package pl.sebastianklimas.models;

import java.math.BigDecimal;
import java.util.Map;

public class ValueCollector {
    Map<String, BigDecimal> paymentMethodsAndAmountUsed;
    BigDecimal totalLeftToPay;

    public ValueCollector() {
    }

    public ValueCollector(Map<String, BigDecimal> paymentMethodsAndAmountUsed, BigDecimal totalLeftToPay) {
        this.paymentMethodsAndAmountUsed = paymentMethodsAndAmountUsed;
        this.totalLeftToPay = totalLeftToPay;
    }

    public Map<String, BigDecimal> getPaymentMethodsAndAmountUsed() {
        return paymentMethodsAndAmountUsed;
    }

    public void setPaymentMethodsAndAmountUsed(Map<String, BigDecimal> paymentMethodsAndAmountUsed) {
        this.paymentMethodsAndAmountUsed = paymentMethodsAndAmountUsed;
    }

    public BigDecimal getTotalLeftToPay() {
        return totalLeftToPay;
    }

    public void setTotalLeftToPay(BigDecimal totalLeftToPay) {
        this.totalLeftToPay = totalLeftToPay;
    }

    public void addPaidAmount(String method, BigDecimal amountPaidByMethod) {
        if (paymentMethodsAndAmountUsed.containsKey(method)) {
            BigDecimal currentValue = paymentMethodsAndAmountUsed.get(method);
            paymentMethodsAndAmountUsed.put(method, currentValue.add(amountPaidByMethod));
        } else {
            paymentMethodsAndAmountUsed.put(method, amountPaidByMethod);
        }
    }

    public void addToLeftToPay(BigDecimal leftToPay) {
        totalLeftToPay = totalLeftToPay.add(leftToPay);
    }

    public void subtractFromLeftToPay(BigDecimal leftToPay) {
        totalLeftToPay = totalLeftToPay.subtract(leftToPay);
    }

    public void print() {
        System.out.println("ValueCollector");
        for (Map.Entry<String, BigDecimal> entry : paymentMethodsAndAmountUsed.entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("TotalLeftToPay: " + totalLeftToPay);
    }

    public void printSolution() {
        for (Map.Entry<String, BigDecimal> entry : paymentMethodsAndAmountUsed.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
