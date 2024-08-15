# Project Description

MailApp is an email management and classification system developed using Spring Boot and Angular. It connects to an email inbox using standard SMTP/IMAP protocols, extracts information, and categorizes it into a database. The application also integrates a machine learning model to detect spam emails and automatically classify incoming messages based on their content, making email management more efficient.

## Features

- **Email Sending and Receiving:** Send and receive emails with support for attachments.
- **Inbox Classification:** Automatically classify emails into predefined categories.
- **Spam Detection:** Integrated spam detection using a Python-based machine learning model.
- **Custom Email Templates:** Create and manage email templates for automated responses.
- **Real-Time Front-End:** User-friendly Angular-based front-end for managing emails.

## Technologies Used

- **Backend:**
  - Java Spring Boot
  - Spring Data JPA
  - MySQL Database
  - Python (Flask for ML API)
  
- **Frontend:**
  - Angular
  - Bootstrap

- **Other Tools:**
  - FontAwesome (for icons)
  - RESTful APIs
  - Maven (Build Management)

### Prerequisites

- Java 11 or higher
- Node.js and npm
- MySQL
- Python 3.x
- Angular CLI
