package thuchanh.ngohuuduc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import thuchanh.ngohuuduc.services.BookService;
import thuchanh.ngohuuduc.services.CategoryService;
import thuchanh.ngohuuduc.services.InvoiceService;
import thuchanh.ngohuuduc.services.UserService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final BookService bookService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final InvoiceService invoiceService;

    @GetMapping
    public String dashboard() {
        return "redirect:/admin/books";
    }

    @GetMapping("/books")
    public String manageBooks(Model model) {
        // Fetch all books for management (pagination could be added if needed)
        model.addAttribute("books", bookService.getAllBooks(0, 1000, "id"));
        return "admin/books/index";
    }

    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/books/add";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(Model model, @PathVariable Long id) {
        var book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/books/edit";
    }

    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories/index";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users/index";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", invoiceService.getAllInvoices());
        return "admin/orders/index";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(Model model, @PathVariable Long id) {
        var invoice = invoiceService.getInvoiceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        model.addAttribute("order", invoice);
        return "admin/orders/detail";
    }
}
