package br.com.gustavoakira.store.orderservice.core.event;

import br.com.gustavoakira.store.orderservice.core.data.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
	private final String orderId;
	private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
