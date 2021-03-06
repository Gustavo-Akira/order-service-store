package br.com.gustavoakira.store.orderservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
	OrderEntity findByOrderId(String orderId);
}
