package br.com.gustavoakira.store.orderservice.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavoakira.store.orderservice.command.CreateOrderCommand;
import br.com.gustavoakira.store.orderservice.core.data.OrderStatus;


@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
	
	@Autowired
	private CommandGateway gateway;
	
	@PostMapping
	public String createOrder(@Valid @RequestBody CreateOrderRestModel command) {
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
				.orderId(UUID.randomUUID().toString())
				.userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
				.orderStatus(OrderStatus.CREATED)
				.quantity(command.getQuantity())
				.addressId(command.getAddressId())
				.productId(command.getProductId())
				.build();
		return gateway.sendAndWait(createOrderCommand);
	}
}
