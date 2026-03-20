package org.example.service;

import org.example.domain.Cart;
import org.example.domain.Item;
import org.example.domain.Order;
import org.example.domain.OrderLine;
import org.example.domain.User;
import org.example.repo.CartRepo;
import org.example.repo.ItemRepo;
import org.example.repo.OrderRepo;
import org.example.repo.UserRepo;
import org.example.rest.dto.OrderLineResponseDto;
import org.example.rest.dto.OrderResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for order checkout and user order history.
 */
@Service
public class OrderServices {

    private static final DateTimeFormatter ORDER_NUMBER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd")
            .withZone(ZoneOffset.UTC);

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final CartRepo cartRepo;
    private final ItemRepo itemRepo;

    public OrderServices(OrderRepo orderRepo, UserRepo userRepo, CartRepo cartRepo, ItemRepo itemRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
    }

    @Transactional
    public ResponseEntity<OrderResponseDto> checkout(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with id: " + userId));

        List<Cart> cartEntries = cartRepo.findByUser_Id(userId);
        if (cartEntries.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "cart is empty");
        }

        List<Integer> itemIds = cartEntries.stream()
                .map(cart -> cart.getItem().getId())
                .distinct()
                .toList();

        Map<Integer, Item> lockedItemsById = itemRepo.findAllByIdInForUpdate(itemIds).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());

        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Cart cartEntry : cartEntries) {
            int itemId = cartEntry.getItem().getId();
            Item lockedItem = lockedItemsById.get(itemId);
            if (lockedItem == null) {
                throw new ResponseStatusException(CONFLICT, "Item not available with id: " + itemId);
            }

            int quantity = cartEntry.getQuantity();
            if (quantity <= 0) {
                throw new ResponseStatusException(BAD_REQUEST, "cart contains invalid quantity for item id: " + itemId);
            }

            int currentStock = lockedItem.getStockLevel() != null ? lockedItem.getStockLevel() : 0;
            if (currentStock < quantity) {
                throw new ResponseStatusException(CONFLICT,
                        "Not enough stock for item '" + lockedItem.getItemName() + "'. Requested " + quantity + " but only " + currentStock + " left");
            }

            OrderLine line = new OrderLine();
            line.setItemId(lockedItem.getId());
            line.setItemName(lockedItem.getItemName());
            line.setConsole(lockedItem.getConsole());
            line.setGenre(lockedItem.getGenre());
            line.setImageUrl(lockedItem.getImageUrl());
            line.setUnitPrice(normalizePrice(lockedItem.getPrice()));
            line.setOnSale(Boolean.TRUE.equals(lockedItem.getOnSale()));
            line.setSaleDiscountPercent(lockedItem.getSaleDiscountPercent());
            line.setQuantity(quantity);
            order.addLine(line);

            lockedItem.setStockLevel(currentStock - quantity);
            totalQuantity += quantity;
            totalAmount = totalAmount.add(calculateLineTotal(line.getUnitPrice(), line.getOnSale(), line.getSaleDiscountPercent(), quantity));
        }

        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP).doubleValue());

        Order savedOrder = orderRepo.save(order);
        cartRepo.deleteAll(cartEntries);

        return new ResponseEntity<>(toDto(savedOrder), HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrderHistory(int userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "User not found with id: " + userId);
        }

        return orderRepo.findByUser_IdOrderByCreatedAtDescIdDesc(userId).stream()
                .map(OrderServices::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepo.findAllByOrderByCreatedAtDescIdDesc().stream()
                .map(OrderServices::toDto)
                .toList();
    }

    private static OrderResponseDto toDto(Order order) {
        List<OrderLineResponseDto> items = order.getLines().stream()
                .map(OrderServices::toDto)
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getOrderNumber(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                order.getCreatedAt(),
                items.size(),
                order.getTotalQuantity() != null ? order.getTotalQuantity() : 0,
                order.getTotalAmount() != null ? order.getTotalAmount() : 0.0,
                items
        );
    }

    private String generateOrderNumber() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String orderNumber = "RGM-" + ORDER_NUMBER_DATE_FORMAT.format(Instant.now()) + "-"
                    + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            if (!orderRepo.existsByOrderNumber(orderNumber)) {
                return orderNumber;
            }
        }

        throw new ResponseStatusException(CONFLICT, "Could not generate a unique order number");
    }

    private static OrderLineResponseDto toDto(OrderLine line) {
        double unitPrice = normalizePrice(line.getUnitPrice());
        double discountedUnitPrice = calculateDiscountedUnitPrice(unitPrice, Boolean.TRUE.equals(line.getOnSale()), line.getSaleDiscountPercent());
        double lineTotal = calculateLineTotal(unitPrice, Boolean.TRUE.equals(line.getOnSale()), line.getSaleDiscountPercent(), line.getQuantity() != null ? line.getQuantity() : 0)
                .doubleValue();

        return new OrderLineResponseDto(
                line.getItemId() != null ? line.getItemId() : 0,
                line.getItemName(),
                line.getConsole(),
                line.getGenre(),
                line.getImageUrl(),
                unitPrice,
                line.getOnSale(),
                line.getSaleDiscountPercent(),
                discountedUnitPrice,
                line.getQuantity() != null ? line.getQuantity() : 0,
                lineTotal
        );
    }

    private static double normalizePrice(Double price) {
        if (price == null) {
            return 0.0;
        }
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static double calculateDiscountedUnitPrice(double unitPrice, boolean onSale, Double saleDiscountPercent) {
        if (!onSale || saleDiscountPercent == null) {
            return unitPrice;
        }

        BigDecimal multiplier = BigDecimal.ONE.subtract(BigDecimal.valueOf(saleDiscountPercent).movePointLeft(2));
        return BigDecimal.valueOf(unitPrice)
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static BigDecimal calculateLineTotal(double unitPrice, boolean onSale, Double saleDiscountPercent, int quantity) {
        return BigDecimal.valueOf(calculateDiscountedUnitPrice(unitPrice, onSale, saleDiscountPercent))
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }
}


