package com.sample.stockexchange.controller;

import com.sample.stockexchange.adapter.OrderSetStore;
import com.sample.stockexchange.adapter.TransactionStore;
import com.sample.stockexchange.entity.Order;
import com.sample.stockexchange.usecase.OrderUsecasesRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CLIControllerTest {
    private CLIController controller;

    @BeforeEach
    void instantiateController() {
        OrderUsecasesRepo repo = new OrderUsecasesRepo(OrderSetStore.getInstance(), TransactionStore.getInstance());
        this.controller = new CLIController(repo, "src/test/resources/emptyfile.txt");
    }

    @Test
    void parseValidInputFormat() {
        Order o = controller.parse("   #9   10:02 BAC buy 242.70  150   ");

        assertEquals("#9", o.getId());
        assertEquals("10:02", o.getTime().toString());
        assertEquals("BAC", o.getStock().getName());
        assertEquals("buy", o.getType().name().toLowerCase());
        assertEquals(150, o.getQuantity());
        assertEquals(new BigDecimal("242.70"), o.getAskingPrice());
    }

    @Test
    void parseInvalidInputFormat() {
        assertThrows(NoSuchElementException.class, () -> {
            controller.parse("#9 10:02 BAC buy");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            controller.parse("#9 10:02 BAC 1234");
        });

        assertThrows(DateTimeParseException.class, () -> {
            controller.parse("#9 BAC 1234");
        });

        assertThrows(NumberFormatException.class, () -> {
            controller.parse("#9 12:12 BAC buy asdf 1234");
        });
    }

    @Test
    void parseEmptyInput() {
        assertThrows(NoSuchElementException.class, () -> {
            controller.parse("");
        });
    }
}