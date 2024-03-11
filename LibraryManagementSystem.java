import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nLibrary Management System Menu:");
            System.out.println("1. Add Book");
            System.out.println("2. Display Books");
            System.out.println("3. Add Patron");
            System.out.println("4. Borrow Book");
            System.out.println("5. Return Book");
            System.out.println("6. Calculate Fine");
            System.out.println("7. Search");
            System.out.println("8. Generate Reports");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        Book newBook = getUserInputForBook(scanner);
                        library.addBook(newBook);
                        break;
                    case 2:
                        library.displayBooks();
                        break;
                    case 3:
                        Patron newPatron = getUserInputForPatron(scanner);
                        library.addPatron(newPatron);
                        break;
                    case 4:
                        library.borrowBook(scanner);
                        break;
                    case 5:
                        library.returnBook(scanner);
                        break;
                    case 6:
                        library.calculateFine(scanner);
                        break;
                    case 7:
                        library.search(scanner);
                        break;
                    case 8:
                        library.generateReports();
                        break;
                    case 0:
                        System.out.println("Exiting Library Management System. Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input. Please enter a number.");
                scanner.nextLine(); 
            }
        }
    }

    private static Book getUserInputForBook(Scanner scanner) {
        System.out.println("Enter Book Details:");
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("Genre: ");
        String genre = scanner.nextLine();

        return new Book(title, author, genre, true);
    }

    private static Patron getUserInputForPatron(Scanner scanner) {
        System.out.println("Enter Patron Details:");
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Contact Information: ");
        String contactInfo = scanner.nextLine();

        return new Patron(name, contactInfo);
    }
}

class Book implements Serializable {
    private String title;
    private String author;
    private String genre;
    private boolean available;

    public Book(String title, String author, String genre, boolean available) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.available = available;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Genre: " + genre + ", Available: " + available;
    }
}

class Patron implements Serializable {
    private String name;
    private String contactInfo;

    public Patron(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Contact Information: " + contactInfo;
    }
}

class Library {
    private List<Book> books;
    private List<Patron> patrons;

    private static final String BOOKS_FILE = "books.txt";
    private static final String PATRONS_FILE = "patrons.txt";

    public Library() {
        this.books = loadBooksFromFile();
        this.patrons = loadPatronsFromFile();
    }

    public void addBook(Book book) {
        books.add(book);
        saveBooksToFile();
    }

    public void displayBooks() {
        System.out.println("Library Books:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void addPatron(Patron patron) {
        patrons.add(patron);
        savePatronsToFile();
    }

    public void borrowBook(Scanner scanner) {
        System.out.println("Enter Patron Name:");
        String patronName = scanner.nextLine();

        Patron patron = findPatronByName(patronName);
        if (patron == null) {
            System.out.println("Patron not found. Please add the patron first.");
            return;
        }

        displayBooks();
        System.out.println("Enter Book Title to Borrow:");
        String bookTitle = scanner.nextLine();

        Book book = findBookByTitle(bookTitle);
        if (book == null) {
            System.out.println("Book not found. Please add the book first.");
            return;
        }

        if (!book.isAvailable()) {
            System.out.println("Sorry, the book is not available for borrowing.");
            return;
        }

        book.setAvailable(false);
        System.out.println("Book borrowed successfully by " + patron.getName());
        saveBooksToFile();
    }

    public void returnBook(Scanner scanner) {
        displayBooks();
        System.out.println("Enter Book Title to Return:");
        String bookTitle = scanner.nextLine();

        Book book = findBookByTitle(bookTitle);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        if (book.isAvailable()) {
            System.out.println("The book is already available. No need to return.");
            return;
        }

        book.setAvailable(true);
        System.out.println("Book returned successfully.");
        saveBooksToFile();
    }

    public void calculateFine(Scanner scanner) {
        System.out.println("Enter Patron Name:");
        String patronName = scanner.nextLine();

        Patron patron = findPatronByName(patronName);
        if (patron == null) {
            System.out.println("Patron not found. Please add the patron first.");
            return;
        }

        System.out.println("Enter Book Title:");
        String bookTitle = scanner.nextLine();

        Book book = findBookByTitle(bookTitle);
        if (book == null) {
            System.out.println("Book not found. Please add the book first.");
            return;
        }

        int daysOverdue = calculateDaysOverdue(book);
        if (daysOverdue > 0) {
            double fineAmount = calculateFineAmount(daysOverdue);
            System.out.println("Fine for " + patron.getName() + ": $" + fineAmount);
        } else {
            System.out.println("No fines for " + patron.getName() + ".");
        }
    }

    private int calculateDaysOverdue(Book book) {
        int daysFromBorrowing = 10;
        int dueDays = 7;
        return Math.max(daysFromBorrowing - dueDays, 0);
    }

    private double calculateFineAmount(int daysOverdue) {
        double fineRate = 1.0;
        return daysOverdue * fineRate;
    }

    public void search(Scanner scanner) {
        System.out.println("Search Options:");
        System.out.println("1. Search Books by Title");
        System.out.println("2. Search Books by Author");
        System.out.println("3. Search Books by Genre");
        System.out.println("4. Search Patrons by Name");
        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");

        int searchChoice = scanner.nextInt();
        scanner.nextLine(); 

        switch (searchChoice) {
            case 1:
                System.out.println("Enter Book Title to Search:");
                String titleToSearch = scanner.nextLine();
                List<Book> booksWithTitle = findBooksByTitle(titleToSearch);
                displaySearchResults(booksWithTitle);
                break;
            case 2:
                System.out.println("Enter Author Name to Search:");
                String authorToSearch = scanner.nextLine();
                List<Book> booksWithAuthor = findBooksByAuthor(authorToSearch);
                displaySearchResults(booksWithAuthor);
                break;
            case 3:
                System.out.println("Enter Genre to Search:");
                String genreToSearch = scanner.nextLine();
                List<Book> booksWithGenre = findBooksByGenre(genreToSearch);
                displaySearchResults(booksWithGenre);
                break;
            case 4:
                System.out.println("Enter Patron Name to Search:");
                String nameToSearch = scanner.nextLine();
                List<Patron> patronsWithName = findPatronsByName(nameToSearch);
                displaySearchResults(patronsWithName);
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
    }

    public void generateReports() {
        System.out.println("Report Options:");
        System.out.println("1. Book Availability Report");
        System.out.println("2. Borrowing History Report");
        System.out.println("3. Fine Report");
        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");

        Scanner scanner = new Scanner(System.in);
        int reportChoice = scanner.nextInt();
        scanner.nextLine(); 

        switch (reportChoice) {
            case 1:
                generateBookAvailabilityReport();
                break;
            case 2:
                generateBorrowingHistoryReport();
                break;
            case 3:
                generateFineReport();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
    }

    private List<Book> loadBooksFromFile() {
        List<Book> loadedBooks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String title = parts[0].trim();
                String author = parts[1].trim();
                String genre = parts[2].trim();
                boolean available = Boolean.parseBoolean(parts[3].trim());
                loadedBooks.add(new Book(title, author, genre, available));
            }
        } catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
        return loadedBooks;
    }

    private void saveBooksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                writer.write(book.getTitle() + ", " + book.getAuthor() + ", " + book.getGenre() + ", " + book.isAvailable());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }
    }

    private List<Patron> loadPatronsFromFile() {
        List<Patron> loadedPatrons = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PATRONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                String contactInfo = parts[1].trim();
                loadedPatrons.add(new Patron(name, contactInfo));
            }
        } catch (IOException e) {
            System.out.println("Error reading patrons file: " + e.getMessage());
        }
        return loadedPatrons;
    }

    private void savePatronsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATRONS_FILE))) {
            for (Patron patron : patrons) {
                writer.write(patron.getName() + ", " + patron.getContactInfo());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to patrons file: " + e.getMessage());
        }
    }

    private Patron findPatronByName(String name) {
        for (Patron patron : patrons) {
            if (patron.getName().equalsIgnoreCase(name)) {
                return patron;
            }
        }
        return null;
    }

    private Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    private List<Book> findBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    private List<Book> findBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    private List<Book> findBooksByGenre(String genre) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    private List<Patron> findPatronsByName(String name) {
        List<Patron> result = new ArrayList<>();
        for (Patron patron : patrons) {
            if (patron.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(patron);
            }
        }
        return result;
    }

    private void displaySearchResults(List<?> searchResults) {
        if (searchResults.isEmpty()) {
            System.out.println("No matching results found.");
        } else {
            System.out.println("Search Results:");
            for (Object result : searchResults) {
                System.out.println(result);
            }
        }
    }

    private void generateBookAvailabilityReport() {
        System.out.println("Book Availability Report:");
        for (Book book : books) {
            System.out.println(book.getTitle() + ": " + (book.isAvailable() ? "Available" : "Not Available"));
        }
    }

    private void generateBorrowingHistoryReport() {
        System.out.println("Borrowing History Report: (Not implemented in this example)");
    }

    private void generateFineReport() {
        System.out.println("Fine Report: (Not implemented in this example)");
    }
}
