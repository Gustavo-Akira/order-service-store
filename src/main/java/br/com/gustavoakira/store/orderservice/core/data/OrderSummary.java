package br.com.gustavoakira.store.orderservice.core.data;

import lombok.Value;

@Value
public class OrderSummary {
	private final String orderId;
	private final OrderStatus orderStatus;
	private final String message;
}
