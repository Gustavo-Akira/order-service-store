package br.com.gustavoakira.store.orderservice.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
	@Id
	@Column(unique = true)
	public String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
}
