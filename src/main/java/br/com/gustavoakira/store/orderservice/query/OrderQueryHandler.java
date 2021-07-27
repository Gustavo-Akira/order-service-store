package br.com.gustavoakira.store.orderservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.gustavoakira.store.orderservice.core.data.OrderEntity;
import br.com.gustavoakira.store.orderservice.core.data.OrderRepository;
import br.com.gustavoakira.store.orderservice.core.data.OrderSummary;

@Component
public class OrderQueryHandler {
	
	OrderRepository orderRepository;
	
	@Autowired
	public OrderQueryHandler(OrderRepository repository) {
		this.orderRepository = repository;
	}
	
	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
		OrderEntity entity = orderRepository.getById(findOrderQuery.getOrderId());
		return new OrderSummary(entity.getOrderId(), entity.getOrderStatus(),"");
	}
}
