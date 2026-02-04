package thuchanh.ngohuuduc.services;

import thuchanh.ngohuuduc.entities.Book;
import thuchanh.ngohuuduc.repositories.IBookRepository;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
//mport org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
public class BookService {
    private final IBookRepository bookRepository;
    private final thuchanh.ngohuuduc.repositories.ICategoryRepository categoryRepository;

    public List<Book> getAllBooks(Integer pageNo,
            Integer pageSize,
            String sortBy) {
        return bookRepository.findAllBooks(pageNo, pageSize, sortBy);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public Optional<Book> addBook(thuchanh.ngohuuduc.viewmodels.BookPostVm bookPostVm) {
        var category = categoryRepository.findById(bookPostVm.categoryId());
        if (category.isEmpty()) {
            return Optional.empty(); // Or throw exception
        }
        Book book = new Book();
        book.setTitle(bookPostVm.title());
        book.setAuthor(bookPostVm.author());
        book.setPrice(bookPostVm.price());
        book.setCategory(category.get());
        book.setImgUrl(bookPostVm.imgUrl());
        return Optional.of(bookRepository.save(book));
    }

    public void updateBook(@NotNull Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElse(null);
        Objects.requireNonNull(existingBook).setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());
        existingBook.setImgUrl(book.getImgUrl());
        bookRepository.save(existingBook);
    }

    public Optional<Book> updateBook(Long id, thuchanh.ngohuuduc.viewmodels.BookPostVm bookPostVm) {
        var existingBookOpt = bookRepository.findById(id);
        if (existingBookOpt.isEmpty()) {
            return Optional.empty();
        }
        var category = categoryRepository.findById(bookPostVm.categoryId());
        if (category.isEmpty()) {
            return Optional.empty(); // Or throw exception
        }

        Book existingBook = existingBookOpt.get();
        existingBook.setTitle(bookPostVm.title());
        existingBook.setAuthor(bookPostVm.author());
        existingBook.setPrice(bookPostVm.price());
        existingBook.setCategory(category.get());
        existingBook.setImgUrl(bookPostVm.imgUrl());
        return Optional.of(bookRepository.save(existingBook));
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBook(String keyword) {
        return bookRepository.searchBook(keyword);
    }
}
