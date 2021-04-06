package com.sample.stockexchange.adapter;

import com.sample.stockexchange.entity.BuyOrderSet;
import com.sample.stockexchange.entity.SellOrderSet;
import com.sample.stockexchange.entity.Stock;

import java.util.Map;

/**
 * Interface for persisting incoming orders based stock. For simplicity's sake,
 * just a map for this implementation
 */
public interface IOrderSetStore {
    public Map<Stock, BuyOrderSet> getBuyOrderStore();

    public Map<Stock, SellOrderSet> getSellOrderStore();
}