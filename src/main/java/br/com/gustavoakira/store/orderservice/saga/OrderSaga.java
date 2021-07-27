package br.com.gustavoakira.store.orderservice.saga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.gustavoakira.store.core.command.CancelProductReservationCommand;
import br.com.gustavoakira.store.core.command.ProccessPaymentCommand;
import br.com.gustavoakira.store.core.command.ReserveProductCommand;
import br.com.gustavoakira.store.core.events.PaymentProcessedEvent;
import br.com.gustavoakira.store.core.events.ProductReservationCancelledEvent;
import br.com.gustavoakira.store.core.events.ProductReservedEvent;
import br.com.gustavoakira.store.core.models.User;
import br.com.gustavoakira.store.core.query.FetchUserPaymentDetailsQuery;
import br.com.gustavoakira.store.orderservice.command.ApproveOrderCommand;
import br.com.gustavoakira.store.orderservice.command.RejectOrderCommand;
import br.com.gustavoakira.store.orderservice.core.data.OrderSummary;
import br.com.gustavoakira.store.orderservice.core.event.OrderApprovedEvent;
import br.com.gustavoakira.store.orderservice.core.event.OrderCreatedEvent;
import br.com.gustavoakira.store.orderservice.core.event.OrderRejectedEvent;
import br.com.gustavoakira.store.orderservice.query.FindOrderQuery;

@Saga
public class OrderSaga {

	@Autowired
	private transient CommandGateway gateway;

	@Autowired
	private transient QueryGateway queryGateway;

	@Autowired
	private transient DeadlineManager deadlineManager;
	
	@Autowired
	private transient QueryUpdateEmitter emitter;

	private final String PAYMENT_DEADLINE = "payment-processing-deadline";

	private String scheduleId = null;

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent event) {
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder().orderId(event.getOrderId())
				.productId(event.getProductId()).quantity(event.getQuantity()).userId(event.getUserId()).build();
		gateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {
				if (commandResultMessage.isExceptional()) {

				}
			}

		});
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvent event) {
		FetchUserPaymentDetailsQuery detailsQuery = new FetchUserPaymentDetailsQuery(event.getUserId());
		User paymentDetails = null;
		try {
			paymentDetails = queryGateway.query(detailsQuery, ResponseTypes.instanceOf(User.class)).join();
		} catch (Exception e) {
			cancelProductReservation(event, e.getMessage());
			return;
		}

		if (paymentDetails == null) {
			cancelProductReservation(event, "Could not fetch user payment details");
			return;
		}

		scheduleId = deadlineManager.schedule(Duration.of(10, ChronoUnit.SECONDS), PAYMENT_DEADLINE, paymentDetails);

		ProccessPaymentCommand proccessPaymentCommand = ProccessPaymentCommand.builder().orderId(event.getOrderId())
				.paymentDetails(paymentDetails.getDetails()).paymentId(UUID.randomUUID().toString()).build();
		String result = "";
		try {
			result = gateway.sendAndWait(proccessPaymentCommand);
		} catch (Exception e) {
			cancelProductReservation(event, e.getMessage());
			return;
		}

		if (result.isEmpty()) {
			cancelProductReservation(event, "Could not process user payment with provided properties");
		}
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent event) {
		cancelDeadline();
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(event.getOrderId());
		gateway.send(approveOrderCommand);
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent event) {
		emitter.emit(FindOrderQuery.class, query->true,new OrderSummary(event.getOrderId(), event.getOrderStatus(),""));
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCancelledEvent event) {
		RejectOrderCommand command = new RejectOrderCommand(event.getOrderId(), event.getReason());
		gateway.send(command);
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderRejectedEvent event) {
		emitter.emit(FindOrderQuery.class, query->true,new OrderSummary(event.getOrderId(), event.getOrderStatus(), event.getReason()));
	}

	@DeadlineHandler(deadlineName = PAYMENT_DEADLINE)
	public void handlePaymentDeadLine(ProductReservedEvent event) {
		cancelProductReservation(event, "Payment timeout");
	}

	private void cancelProductReservation(ProductReservedEvent event, String reason) {
		cancelDeadline();
		CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
				.orderId(event.getOrderId()).productId(event.getProductId()).quantity(event.getQuantity())
				.userId(event.getUserId()).reason(reason).build();
		gateway.send(cancelProductReservationCommand);
	}

	private void cancelDeadline() {
		if(scheduleId != null) {
			deadlineManager.cancelSchedule(PAYMENT_DEADLINE, scheduleId);
			scheduleId = null;
		}
	}

}
