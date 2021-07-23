package br.com.gustavoakira.store.orderservice.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import br.com.gustavoakira.store.orderservice.core.data.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderCommand {
	@TargetAggregateIdentifier
	private String orderId;
	private String userId;
	private String productId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;
}
