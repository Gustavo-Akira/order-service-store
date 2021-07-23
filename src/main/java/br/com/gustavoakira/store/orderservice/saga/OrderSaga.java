package br.com.gustavoakira.store.orderservice.saga;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.gustavoakira.store.core.command.ReserveProductCommand;
import br.com.gustavoakira.store.orderservice.core.event.OrderCreatedEvent;

@Saga
public class OrderSaga {
	
	@Autowired
	private transient CommandGateway gateway;
	
	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent event) {
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
				.orderId(event.getOrderId())
				.productId(event.getProductId())
				.quantity(event.getQuantity())
				.userId(event.getUserId())
				.build();
		gateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {
				if(commandResultMessage.isExceptional()) {
					
				}
			}
			
		});
	}
}
