package thuchanh.ngohuuduc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import thuchanh.ngohuuduc.daos.Cart;
import thuchanh.ngohuuduc.daos.Item;
import thuchanh.ngohuuduc.entities.Category;
import thuchanh.ngohuuduc.entities.Invoice;
import thuchanh.ngohuuduc.services.BookService;
import thuchanh.ngohuuduc.services.CartService;
import thuchanh.ngohuuduc.services.CategoryService;
import thuchanh.ngohuuduc.services.InvoiceService;
import thuchanh.ngohuuduc.services.UserService;
import thuchanh.ngohuuduc.services.FileService;
import thuchanh.ngohuuduc.viewmodels.BookGetVm;
import thuchanh.ngohuuduc.viewmodels.BookPostVm;
import thuchanh.ngohuuduc.viewmodels.UserGetVm;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ApiController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final FileService fileService;

    // Books APIs
    @GetMapping("/books")
    public ResponseEntity<List<BookGetVm>> getAllBooks(Integer pageNo,
            Integer pageSize, String sortBy) {
        return ResponseEntity.ok(bookService.getAllBooks(
                pageNo == null ? 0 : pageNo, pageSize == null ? 20 : pageSize,
                sortBy == null ? "id" : sortBy)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    @GetMapping("/books/id/{id}")
    public ResponseEntity<BookGetVm> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id)
                .map(BookGetVm::from)
                .orElse(null));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<BookGetVm>> searchBooks(String keyword) {
        return ResponseEntity.ok(bookService.searchBook(keyword)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    @PostMapping("/books/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String url = fileService.saveFile(file);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/books")
    public ResponseEntity<BookGetVm> addBook(@RequestBody BookPostVm bookPostVm) {
        return ResponseEntity.ok(bookService.addBook(bookPostVm)
                .map(BookGetVm::from)
                .orElse(null));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookGetVm> updateBook(@PathVariable Long id, @RequestBody BookPostVm bookPostVm) {
        return ResponseEntity.ok(bookService.updateBook(id, bookPostVm)
                .map(BookGetVm::from)
                .orElse(null));
    }

    // Categories APIs
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<Void> addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }

    // User APIs
    @GetMapping("/users")
    public ResponseEntity<List<UserGetVm>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream()
                .map(UserGetVm::from)
                .toList());
    }

    // Invoice APIs
    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // Cart APIs
    @GetMapping("/cart")
    public ResponseEntity<Cart> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getCart(session));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<Void> addToCart(@RequestBody Item item, HttpSession session) {
        var cart = cartService.getCart(session);
        cart.addItems(item);
        cartService.updateCart(session, cart);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cart/update/{bookId}/{quantity}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long bookId, @PathVariable int quantity,
            HttpSession session) {
        var cart = cartService.getCart(session);
        cart.updateItems(bookId, quantity);
        cartService.updateCart(session, cart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart/remove/{bookId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long bookId, HttpSession session) {
        var cart = cartService.getCart(session);
        cart.removeItems(bookId);
        cartService.updateCart(session, cart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart/clear")
    public ResponseEntity<Void> clearCart(HttpSession session) {
        cartService.removeCart(session);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<Void> checkout(HttpSession session) {
        cartService.saveCart(session);
        return ResponseEntity.ok().build();
    }
}
