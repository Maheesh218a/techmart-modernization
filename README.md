# TechMart Modernization 🚀

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-10-blue.svg)
![Payara Micro](https://img.shields.io/badge/Payara-Micro-yellow.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)

TechMart Modernization is a robust, highly scalable e-commerce backend built with **Jakarta EE (Java EE)**. It demonstrates enterprise-level patterns using **Stateless/Stateful/Singleton EJBs**, asynchronous messaging with **JMS (Java Message Service)**, and persistent data management via **JPA (EclipseLink)**. 

The system is optimized for high traffic with built-in database connection pooling and thread management via **Payara Micro**.

---

## 🌟 Key Features
* **Multi-tier Architecture:** Clean separation of Presentation (JAX-RS), Business Logic (EJB), and Data Access (JPA) layers.
* **Asynchronous Processing:** JMS-based Message-Driven Beans (MDB) for email/SMS notifications without blocking user requests.
* **Optimized EJB Lifecycles:** Efficient use of Stateless beans for performance, Stateful beans for checkout sessions, and Singleton beans for metrics.
* **RESTful APIs:** Complete JSON-based API for frontend integration.
* **Cloud-Ready:** Docker and `docker-compose` configurations included for seamless deployment.

---

## 🛠️ Prerequisites
Before you begin, ensure you have the following installed:
* **Java Development Kit (JDK) 17**
* **Apache Maven** (3.8+)
* **MySQL** (8.0+)
* **Git** (to clone the repository)
* *(Optional)* **Docker Desktop** (if you prefer running via Docker)

---

## 🗄️ Database Setup (Crucial)
This project comes with a pre-filled database containing realistic **sample data** (Products, Warehouses, Customers, and Orders) so you can test it immediately!

1. Open your MySQL client (e.g., MySQL Workbench, DBeaver, or CLI).
2. Create an empty database named `techmart_db`:
   ```sql
   CREATE DATABASE techmart_db;
   ```
3. Import the provided SQL dump file:
   * **File:** `techmart_db_with_dummy_data.sql`
   * Import it into the `techmart_db` database. 
   *(If using CLI: `mysql -u root -p techmart_db < techmart_db_with_dummy_data.sql`)*

---

## 🚀 Installation & Running

You can run this project in two ways: **Locally via Maven** or **Via Docker**.

### Option A: Running Locally with Payara Micro (Recommended for Dev)
1. Clone the repository:
   ```bash
   git clone https://github.com/Maheesh218a/techmart-modernization.git
   cd techmart-modernization
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application (this will automatically download and start Payara Micro):
   ```bash
   mvn payara-micro:start
   ```
4. The API will be available at: `http://localhost:8080/techmart-modernization/api`

### Option B: Running with Docker (Cloud Deployment)
If you have Docker installed, you can spin up both the App and Database instantly!
```bash
docker-compose up -d --build
```

---

## 🔐 Default Login Credentials
To help you test the system quickly, use the following sample accounts already present in the database dump:

### Admin Login
* **Email:** `admin@gmail.com`
* **Password:** `admin`

### Customer Login (Sample)
* **Email:** `saman.k99@example.com`
* **Password:** `pass`

*(Note: There are 10+ other dummy customers available in the database you can use).*

---

## 📝 Testing the API
You can test the endpoints using Postman or cURL. Here is a quick test to see all products:
```bash
curl -X GET http://localhost:8080/techmart-modernization/api/products
```

## 📄 License
This project was created for educational purposes. Feel free to fork and use it!
