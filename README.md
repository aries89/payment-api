# Payment API 

##  Overview

This project is a simple **Payment Processing API** built using Java (Spring Boot).
It supports payment creation, secure card storage, and dynamic webhook notifications.

---

## Tech Stack

* Java 21
* Spring Boot 4.0.5
* Maven 3.9.11
* PostgreSQL
* REST APIs
* OpenAPI 3.0

---

##  Features

* Create payment API
* Card number encryption before storage
* Dynamic webhook registration
* Retry mechanism for failed webhooks (3 attempts)
* All webhooks are called **asynchronously** after a payment is created
* OpenAPI documentation included

---

## Security Implementation

### API Security

All payment APIs are secured using an **API Secret Key**.

* Requests must include:

```http
x-api-key: <API_SECRET_KEY>
```

* Unauthorized requests are rejected with **401 Unauthorized**

### Encryption

* Card numbers are encrypted using **AES encryption** before storing in DB
* Secret key is managed via environment variables

###  Future Improvement

In production systems, a more robust authentication mechanism such as:

* **JWT (JSON Web Tokens)**
* OAuth2

would be used instead of a static API key.

---

##  Environment Variables

The application uses the following environment variables:

| Variable       | Description |
| -------------- | ----------- |
| DB_PASSWORD    | Database password |
| API_SECRET_KEY | API authentication key (any plain text) |
| AES_SECRET_KEY | 16-character plain text AES key |

**Example for AES_SECRET_KEY:**  
```bash
AES_SECRET_KEY="0123456789012345"
```
**Example for API_SECRET_KEY:**  
```bash
API_SECRET_KEY="my-api-key"
```

Create a `.env` file:

```env
DB_PASSWORD=your_password
API_SECRET_KEY=your_secret
AES_SECRET_KEY=your_aes_key
```

---

##  Database Setup

Since PostgreSQL is used, you need to **create the database manually** before running the app:

```sql
CREATE DATABASE paymentdb;
```

Update `application.properties` or `application.yml` with your PostgreSQL credentials.

---

##  How to Run

### 1. Clone the repo

```bash
git clone https://github.com/<your-username>/payment-api.git
cd payment-api
```

### 2. Set environment variables

Load your `.env` file or set variables in your shell:

```bash
export DB_PASSWORD=your_password
export API_SECRET_KEY=your_secret
export AES_SECRET_KEY=your_aes_key
```

### 3. Run the application

```bash
mvn spring-boot:run
```

---

##  API Documentation

OpenAPI specification is available at:

```
/open-api.yaml
```

You can import it into Postman or Swagger UI.

---

##  API Endpoints

### Create Payment

```
POST /api/v1/payments
```

### Register Webhook

```
POST /api/v1/webhooks
```

---

##  Webhook Behavior

* Triggered after every successful payment
* Sends **HTTPS POST** with payment details
* Retries up to 3 times on failure

---

##  Notes

* Card data is never stored in plain text
* Input validation is implemented
* Error handling follows standard HTTP status codes

---
