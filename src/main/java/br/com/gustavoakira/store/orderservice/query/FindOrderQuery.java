package br.com.gustavoakira.store.orderservice.query;

import lombok.Value;

@Value
public class FindOrderQuery {
	private final String orderId;
}
