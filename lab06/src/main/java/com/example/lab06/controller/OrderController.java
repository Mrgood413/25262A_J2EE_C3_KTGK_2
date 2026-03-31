package com.example.lab06.controller;

import com.example.lab06.model.Order;
import com.example.lab06.model.OrderDetail;
import com.example.lab06.model.Product;
import com.example.lab06.repository.OrderDetailRepository;
import com.example.lab06.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.example.lab06.controller.ProductController.CartItem;

@Controller
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderController(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/products";
        }
        Collection<CartItem> items = cart.values();
        long total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    @Transactional
    public String checkout(HttpSession session) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/products";
        }

        Collection<CartItem> items = cart.values();
        long total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(total);
        order = orderRepository.save(order);

        for (CartItem item : items) {
            Product product = item.getProduct();
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(product.getPrice());
            orderDetailRepository.save(detail);
        }

        session.removeAttribute("cart");
        return "cart/order-success";
    }
}

