package br.com.gustavoakira.store.orderservice.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavoakira.store.orderservice.command.CreateOrderCommand;
import br.com.gustavoakira.store.orderservice.core.data.OrderStatus;
import br.com.gustavoakira.store.orderservice.core.data.OrderSummary;
import br.com.gustavoakira.store.orderservice.query.FindOrderQuery;


@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
	
	@Autowired
	private CommandGateway gateway;
	
	@Autowired
	private QueryGateway queryGateway;
	
	@PostMapping
	public OrderSummary createOrder(@Valid @RequestBody CreateOrderRestModel command) {
		String orderId = UUID.randomUUID().toString();
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
				.orderId(orderId)
				.userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
				.orderStatus(OrderStatus.CREATED)
				.quantity(command.getQuantity())
				.addressId(command.getAddressId())
				.productId(command.getProductId())
				.build();
		SubscriptionQueryResult<OrderSummary, OrderSummary>  queryResult =  queryGateway.subscriptionQuery(new FindOrderQuery(orderId), ResponseTypes.instanceOf(OrderSummary.class), ResponseTypes.instanceOf(OrderSummary.class));
		try {
			gateway.sendAndWait(createOrderCommand);
			return queryResult.updates().blockFirst();
		}finally{
			queryResult.close();
		}
	}
}
