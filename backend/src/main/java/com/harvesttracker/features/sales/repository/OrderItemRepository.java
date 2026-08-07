package com.harvesttracker.features.sales.repository;

import com.harvesttracker.features.sales.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderIdAndDeletedAtIsNull(Long orderId);

    Optional<OrderItem> findByIdAndDeletedAtIsNull(Long id);
}
