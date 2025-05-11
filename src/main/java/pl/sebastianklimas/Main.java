package pl.sebastianklimas;


import com.fasterxml.jackson.core.type.TypeReference;
import pl.sebastianklimas.IO.FileLoader;
import pl.sebastianklimas.exceptions.NotEnoughLimitException;
import pl.sebastianklimas.models.Order;
import pl.sebastianklimas.models.PaymentMethod;
import pl.sebastianklimas.models.ValueCollector;
import pl.sebastianklimas.services.Service;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Number of arguments is not correct, please provide path for orders.json and paymentmethods.json");
            System.exit(1);
        }

        List<Order> orders = FileLoader.loadFile(args[0], new TypeReference<>() {

        });
        List<PaymentMethod> paymentMethods = FileLoader.loadFile(args[1], new TypeReference<>() {
        });

        Service.sortLists(orders, paymentMethods);

        ValueCollector vc = Service.processOrders(orders, paymentMethods);

        if (Service.checkIfNotEnoughLimits(vc, paymentMethods)) throw new NotEnoughLimitException();

        if (vc.getTotalLeftToPay().compareTo(BigDecimal.ZERO) > 0) {
            Service.clearRemainingPayments(vc, paymentMethods);
        }

        vc.printSolution();
    }
}