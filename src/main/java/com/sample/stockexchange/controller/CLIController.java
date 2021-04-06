package com.sample.stockexchange.controller;

import com.google.common.base.Splitter;
import com.sample.stockexchange.entity.Order;
import com.sample.stockexchange.entity.OrderEntry;
import com.sample.stockexchange.entity.OrderType;
import com.sample.stockexchange.entity.Stock;
import com.sample.stockexchange.usecase.AddOrderException;
import com.sample.stockexchange.usecase.OrderUsecasesRepo;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class CLIController {
    private final OrderUsecasesRepo repo;
    private final String filePath;

    public CLIController(OrderUsecasesRepo repo, String filePath) {
        this.repo = repo;
        this.filePath = filePath;
    }

    /**
     * parse parsers a single order from a string. format:<order-id> <time> <stock>
     * <buy/sell> <qty> <price>
     */
    public Order parse(String orderLine) {
        Splitter spaceSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();
        Iterator<String> tokenItr = spaceSplitter.split(orderLine).iterator();

        String orderId = tokenItr.next();

        String timeStr = tokenItr.next();
        LocalTime orderTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()));

        String stockName = tokenItr.next();
        Stock stock = new Stock(stockName);

        String typeStr = tokenItr.next();
        OrderType type = OrderType.valueOf(typeStr.toUpperCase());

        BigDecimal price = new BigDecimal(tokenItr.next());
        int quantity = Integer.parseInt(tokenItr.next());

        return new Order(orderId, orderTime, type, quantity, stock, price);
    }

    public List<Order> readFromFile() {
        List<Order> orders = new ArrayList<>();
        try(BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filePath))))
        {
            String line;
            while ((line = input.readLine()) != null) {
                orders.add(parse(line));
            }
        } catch (DateTimeParseException | NoSuchElementException | NumberFormatException e) {
            System.out.println("Invalid input format! Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to get input! Exception: " + e.getMessage());
        }

        return orders;
    }

    public void writeToCLI(List<OrderEntry> entries) {
        entries.forEach((entry) -> {
            String output = String.format("%s %.2f %d %s",
                    entry.getCounterParty().getId(),
                    entry.getExecutionPrice(), entry.getQuantity(),
                    entry.getParty().getId(), entry.getCounterParty().getId());
            System.out.println(output);
        });
    }

    public void run() {
        repo.cleanup();
        try {
            repo.addOrders(readFromFile());

            writeToCLI(repo.processOrders());
        } catch (AddOrderException e) {
            System.out.println("Invalid input orders! Exception: " + e.getMessage());
        }
    }
}