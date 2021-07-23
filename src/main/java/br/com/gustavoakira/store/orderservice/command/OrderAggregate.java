package br.com.gustavoakira.store.orderservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import br.com.gustavoakira.store.orderservice.core.data.OrderStatus;
import br.com.gustavoakira.store.orderservice.core.event.OrderCreatedEvent;

@Aggregate
public class OrderAggregate {
	@AggregateIdentifier
	private String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;
	
	public OrderAggregate() {
		// TODO Auto-generated constructor stub
	}
	
	@CommandHandler
	public OrderAggregate(CreateOrderCommand command) {
		OrderCreatedEvent event = new OrderCreatedEvent();
		BeanUtils.copyProperties(command, event);
		AggregateLifecycle.apply(event);
	}
	
	@EventSourcingHandler
	public void on(OrderCreatedEvent event) {
		this.orderId = event.getOrderId();
		this.productId = event.getProductId();
		this.userId = event.getUserId();
		this.quantity = event.getQuantity();
		this.addressId = event.getAddressId();
		this.orderStatus = event.getOrderStatus();
	}
}
