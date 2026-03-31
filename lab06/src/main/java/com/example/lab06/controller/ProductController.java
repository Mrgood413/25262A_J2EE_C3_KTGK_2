package com.example.lab06.controller;

import com.example.lab06.model.Category;
import com.example.lab06.model.Product;
import com.example.lab06.service.CategoryService;
import com.example.lab06.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String index(@RequestParam(value = "keyword", required = false) String keyword,
                        @RequestParam(value = "categoryId", required = false) Integer categoryId,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        Model model) {

        int size = 5;
        Page<Product> productPage = productService.getPage(page, size);

        // Nếu có keyword thì ưu tiên tìm kiếm theo tên
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("listproduct", productService.searchByName(keyword));
        } else if (categoryId != null) {
            model.addAttribute("listproduct", productService.findByCategoryId(categoryId));
        } else {
            model.addAttribute("listproduct", productPage.getContent());
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("selectedCategoryId", categoryId);

        return "product/products";
    }

    @GetMapping("/sort")
    public String sort(@RequestParam("direction") String direction, Model model) {
        model.addAttribute("listproduct", productService.getAllSortedByPrice(direction));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("direction", direction);
        return "product/products";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAll());
        return "product/create";
    }

    @PostMapping("/create")
    public String create(@Valid Product newProduct,
                         BindingResult result,
                         @RequestParam("category.id") int categoryId,
                         @RequestParam("imageProduct") MultipartFile imageProduct,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", newProduct);
            model.addAttribute("categories", categoryService.getAll());
            return "product/create";
        }

        if (imageProduct != null && !imageProduct.isEmpty()) {
            productService.updateImage(newProduct, imageProduct);
        }
        Category selectedCategory = categoryService.get(categoryId);
        newProduct.setCategory(selectedCategory);
        productService.add(newProduct);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        Product find = productService.get(id);
        if (find == null) {
            return "error/404";
        }
        model.addAttribute("product", find);
        model.addAttribute("categories", categoryService.getAll());
        return "product/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid Product editProduct,
                       BindingResult result,
                       @RequestParam("category.id") int categoryId,
                       @RequestParam("imageProduct") MultipartFile imageProduct,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", editProduct);
            model.addAttribute("categories", categoryService.getAll());
            return "product/edit";
        }

        editProduct.setCategory(categoryService.get(categoryId));
        if (imageProduct != null && !imageProduct.isEmpty()) {
            productService.updateImage(editProduct, imageProduct);
        }
        productService.update(editProduct);
        return "redirect:/products";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") int productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session) {
        Product product = productService.get(productId);
        if (product == null) {
            return "error/404";
        }

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        CartItem item = cart.get(productId);
        if (item == null) {
            item = new CartItem(product, quantity);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        cart.put(productId, item);
        session.setAttribute("cart", cart);
        return "redirect:/products/cart";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        Collection<CartItem> items = cart.values();
        long total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);
        return "cart/cart";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        productService.delete(id);
        return "redirect:/products";
    }

    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}

