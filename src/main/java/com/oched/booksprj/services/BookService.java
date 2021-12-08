package com.oched.booksprj.services;

import com.oched.booksprj.entities.AuthorEntity;
import com.oched.booksprj.entities.BookContentEntity;
import com.oched.booksprj.entities.BookDescriptionEntity;
import com.oched.booksprj.repositories.BookContentRepository;
import com.oched.booksprj.requests.AddBookRequest;
import com.oched.booksprj.repositories.AuthorRepository;
import com.oched.booksprj.repositories.BookRepository;
import com.oched.booksprj.requests.DeleteBookRequest;
import com.oched.booksprj.requests.UpdateBookRequest;
import com.oched.booksprj.responses.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookContentRepository contentRepository;

    public void addBook(AddBookRequest request) {
        Optional<AuthorEntity> optionalAuthor = authorRepository.findByFirstNameAndLastName(
                request.getAuthorFirstName(),
                request.getAuthorLastName()
        );

        AuthorEntity author;

        if(optionalAuthor.isPresent()) {
            author = optionalAuthor.get();
        } else {
            author = new AuthorEntity(
                    request.getAuthorFirstName(),
                    request.getAuthorLastName()
            );

            authorRepository.save(author);
        }

        BookContentEntity bookContent = new BookContentEntity(request.getContent());

        contentRepository.save(bookContent);

        BookDescriptionEntity newBook = new BookDescriptionEntity(
                request.getTitle(),
                request.getYear(),
                author,
                bookContent
        );

        bookRepository.save(newBook);
    }

    public void updateBook(UpdateBookRequest request) {
        Optional<AuthorEntity> optionalAuthor = authorRepository.findByFirstNameAndLastName(
                request.getAuthorFirstName(),
                request.getAuthorLastName()
        );
        AuthorEntity author;
        if(optionalAuthor.isPresent()) {
            author = optionalAuthor.get();
        } else {
            author = new AuthorEntity(
                    request.getAuthorFirstName(),
                    request.getAuthorLastName()
            );
            authorRepository.save(author);
        }
        BookContentEntity bookContent = new BookContentEntity(request.getContent());
        contentRepository.save(bookContent);

        Optional<BookDescriptionEntity> optionalBook = bookRepository.findByTitle(request.getTitle());
        BookDescriptionEntity book;
        if(optionalBook.isPresent()) {
            book = optionalBook.get();
            book.setYear(request.getYear());
            book.setAuthor(author);
            book.setContent(bookContent);
            bookRepository.save(book);
        }

    }

    public void deleteBook(DeleteBookRequest request) {
        Optional<BookDescriptionEntity> optionalBook = bookRepository.findByTitle(request.getTitle());
        BookDescriptionEntity book;
        if(optionalBook.isPresent()) {
            book = optionalBook.get();
            bookRepository.delete(book);
        }

    }

    public List<BookResponse> getAll() {
        List<BookDescriptionEntity> list = bookRepository.findAll();

        return list.stream().map(book -> new BookResponse(
                    book.getTitle(),
                    book.getYear(),
                    book.getAuthor().getFirstName(),
                    book.getAuthor().getLastName()
                )).collect(Collectors.toList());
    }
}
