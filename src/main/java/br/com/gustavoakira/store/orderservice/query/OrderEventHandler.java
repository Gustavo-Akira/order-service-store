package br.com.gustavoakira.store.orderservice.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.gustavoakira.store.orderservice.core.data.OrderEntity;
import br.com.gustavoakira.store.orderservice.core.data.OrderRepository;
import br.com.gustavoakira.store.orderservice.core.event.OrderApprovedEvent;
import br.com.gustavoakira.store.orderservice.core.event.OrderCreatedEvent;
import br.com.gustavoakira.store.orderservice.core.event.OrderRejectedEvent;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {
	private final OrderRepository repository;
	
	@Autowired
	public OrderEventHandler(OrderRepository repository) {
		this.repository = repository;
	}
	
	@EventHandler
	public void on(OrderCreatedEvent event) throws Exception {
		OrderEntity entity = new OrderEntity();
		BeanUtils.copyProperties(event, entity);
		this.repository.save(entity);
	}
	
	@EventHandler
	public void on(OrderApprovedEvent approvedEvent) {
		OrderEntity entity = repository.findByOrderId(approvedEvent.getOrderId());
		if(entity == null) {
			return;
		}
		
		entity.setOrderStatus(approvedEvent.getOrderStatus());
		repository.save(entity);
	}
	
	@EventHandler
	public void on(OrderRejectedEvent event) {
		OrderEntity entity = repository.getById(event.getOrderId());
		entity.setOrderStatus(event.getOrderStatus());
		repository.save(entity);
	}
}
