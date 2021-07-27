package br.com.gustavoakira.store.orderservice.core.event;

import br.com.gustavoakira.store.orderservice.core.data.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectedEvent {
	private final String orderId;
	private final String reason;
	private final OrderStatus orderStatus =OrderStatus.REJECTED;
}
