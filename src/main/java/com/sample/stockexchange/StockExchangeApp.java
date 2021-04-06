package com.sample.stockexchange;

import com.sample.stockexchange.adapter.OrderSetStore;
import com.sample.stockexchange.adapter.TransactionStore;
import com.sample.stockexchange.controller.CLIController;
import com.sample.stockexchange.usecase.OrderUsecasesRepo;

public class StockExchangeApp {
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Input File Path Needed");
            return;
        }
        // initialize usecase repo
        OrderUsecasesRepo repo = new OrderUsecasesRepo(OrderSetStore.getInstance(), TransactionStore.getInstance());

        // initialize controller
        CLIController controller = new CLIController(repo, args[0]);

        // execute
        controller.run();
    }
}
