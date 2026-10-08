# 🧸 PoojaMart Toys - Full-Stack E-Commerce Website

A modern, high-performance, Flipkart and Amazon-inspired toy e-commerce platform built with **HTML5, CSS3, JavaScript**, **Java Spring Boot 2.7**, **Spring Data JPA**, and **MySQL**.

---

## 🌟 Key Features

### 1. 🔐 Authentication & Accounts
* **Login & Registration:** Secure authentication with credentials verification.
* **Branded Welcome:** Login page displays *"Welcome to PoojaMart Toys"* with Flipkart blue graphics.
* **Forgot Password:** Automated retrieval simulation and recovery OTP helper.
* **User Profile:** Manage shipping addresses, contact info, and profile settings.
* **Pre-configured Demo Accounts:**
  * **Admin:** `admin@poojamart.com` / `admin123`
  * **Customer:** `user@poojamart.com` / `user123`

### 2. 🧸 50 Realistic Toy Products across 10 Categories
Curated with realistic e-commerce product photos, specifications, pricing, stock levels, and ratings:
1. **Teddy Bears** (Giant Cuddle Bears, Fluffy Honey Bears, Plush Pandas, Vintage Knitted Bears)
2. **Racing Cars** (1:24 Alloy Die-cast GT Supercars, Formula 1 Racers, Vintage Roadsters)
3. **Remote Control Cars** (2.4GHz 4WD Monster Trucks, Drift Racers, Rock Crawlers, 360° Stunt Buggies)
4. **Building Blocks** (Medieval Castles, City Fire Station, STEM Mechanical Gears, Creative Bricks)
5. **Educational Toys** (Solar System Planetarium, Magnetic Math Boards, 1200x Microscope, Coding Robots)
6. **Puzzle Games** (1000-Piece World Map, 3D Wooden Clocks, Magnetic Speed Cubes, Tangrams)
7. **Action Figures** (Articulated Superheroes, Cyber Mecha Warriors, Dragon Knights, Astronauts)
8. **Toy Trains** (Electric Steam Locomotives with Smoke, High-Speed Bullet Trains, Wooden Railway)
9. **Baby Toys** (Silicone Teethers, Rainbow Stacking Towers, Kick & Play Activity Gym, Cloth Books)
10. **Outdoor Toys** (Foldable 3-Wheel LED Scooters, Pop-up Castle Play Tents, Archery Sets)

### 3. 🔍 Search, Filtering & Discovery
* **Instant Autocomplete Search:** Real-time search preview with thumbnails and prices.
* **Category Filtering:** Filter across 10 distinct toy categories.
* **Price Filtering:** Under ₹500, ₹500–₹1,000, ₹1,000–₹2,000, and Above ₹2,000.
* **Sort Options:** Newest first, Popularity / Customer ratings, Price (Low to High, High to Low).

### 4. 🛒 Flipkart-Style Cart & Checkout Flow
1. **Add to Cart & Instant Buy Now**
2. **Cart Management:** Increment/decrement quantity, real-time total, discount calculations, and free delivery thresholds.
3. **4-Step Accordion Checkout:**
   * Step 1: Login / Account verification
   * Step 2: Delivery address selection & form
   * Step 3: Order item review & delivery estimate
   * Step 4: Payment mode selection (Cash on Delivery, UPI simulation, Credit/Debit card simulation)
4. **Order Confirmation:** Green animated checkmark, unique order ID generation (e.g. `PMT-202610-xxxxx`), delivery details.

### 5. 📦 Order Tracking & History
* Visual 5-step status stepper: **Placed ➔ Confirmed ➔ Shipped ➔ Out for Delivery ➔ Delivered**.
* Detailed item-level breakdown and cancellation capabilities.

### 6. ⚙️ Admin & Seller Hub (`admin.html`)
* **KPI Metrics:** Total Revenue, Total Orders, Total Products, Total Users, Low Stock Alerts (≤5).
* **Product Catalog CRUD:** Add, Edit, and Delete toy products with interactive modal dialogs.
* **Order Status Manager:** Update fulfillment states live.
* **Customer Directory:** View registered users.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Frontend** | HTML5, CSS3, Modern JavaScript (ES6+ Fetch API) |
| **Backend** | Java 11, Spring Boot 2.7.18, Spring Web, Spring Data JPA |
| **Database** | MySQL (Connector/J) + H2 (Dev standalone profile) |
| **Build Tool** | Apache Maven |

---

## 📂 Project Directory Structure

```
c:\PoojaMart\
├── pom.xml                                   # Maven dependencies
├── run.bat                                   # One-click Windows runner
├── src/main/java/com/poojamart/
│   ├── PoojaMartApplication.java             # Main Application Entry Point
│   ├── config/
│   │   └── CorsConfig.java                   # CORS and Static Resource Configuration
│   ├── controller/
│   │   ├── AuthController.java               # /api/auth
│   │   ├── CategoryController.java           # /api/categories
│   │   ├── ProductController.java            # /api/products
│   │   ├── CartController.java               # /api/cart
│   │   ├── OrderController.java              # /api/orders
│   │   ├── UserController.java               # /api/users
│   │   └── AdminController.java              # /api/admin
│   ├── dto/                                  # Request/Response DTOs
│   ├── model/
│   │   ├── User.java                         # User entity
│   │   ├── Category.java                     # Category entity
│   │   ├── Product.java                      # Product entity
│   │   ├── CartItem.java                     # Cart item entity
│   │   ├── Order.java                        # Order header entity
│   │   └── OrderItem.java                    # Order items entity
│   ├── repository/                           # Spring Data JPA Repositories
│   └── service/                              # Services & DataInitializerService
├── src/main/resources/
│   ├── application.properties                # Primary MySQL configuration
│   ├── application-dev.properties            # Standalone Dev profile
│   ├── schema.sql                            # MySQL table schemas
│   └── static/                               # Modern Flipkart-inspired UI
│       ├── css/
│       │   ├── main.css                      # Base colors & typography
│       │   ├── navbar.css                    # Sticky header & search bar
│       │   ├── components.css                # Cards, badges, steppers, modals
│       │   └── admin.css                     # Admin panel styling
│       ├── js/
│       │   ├── api.js                        # Unified REST API client
│       │   ├── navbar.js                     # Header actions & live search
│       │   ├── app.js                        # Home page & hero banner
│       │   ├── products.js                   # Catalog search & filtering
│       │   ├── product-detail.js             # Single product view & specs
│       │   ├── cart.js                       # Cart logic & calculations
│       │   ├── checkout.js                   # 4-step Flipkart checkout
│       │   ├── orders.js                     # Order history & live tracking
│       │   ├── auth.js                       # Login, register, profile
│       │   └── admin.js                      # Admin KPIs & product CRUD
│       ├── index.html                        # Home Page
│       ├── products.html                     # Catalog & Filters
│       ├── product-detail.html               # Product Detail View
│       ├── cart.html                         # Shopping Cart
│       ├── checkout.html                     # Checkout Flow
│       ├── order-success.html                # Order Confirmation
│       ├── orders.html                       # Order Tracking
│       ├── login.html                        # Login Page
│       ├── register.html                     # Registration Page
│       ├── profile.html                      # Customer Profile
│       └── admin.html                        # Admin Dashboard
```

---

## 🚀 How to Run the Application

### Option 1: One-Click Startup (Recommended on Windows)
Simply double-click or run:
```powershell
.\run.bat
```
This script automatically sets `JAVA_HOME` to your installed JDK 11 and starts the Spring Boot server.

### Option 2: Run with Maven
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
& "C:\Program Files\JetBrains\IntelliJ IDEA 2023.1\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

### Option 3: Run with Dev Profile (Embedded DB - Zero Setup)
If your local MySQL service is not running yet and you want to preview everything immediately:
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2023.1\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 🌐 Accessing the Website

Once started, open your web browser to:
* **Home Page:** [http://localhost:8080/](http://localhost:8080/)
* **Catalog:** [http://localhost:8080/products.html](http://localhost:8080/products.html)
* **Login:** [http://localhost:8080/login.html](http://localhost:8080/login.html)
* **Cart:** [http://localhost:8080/cart.html](http://localhost:8080/cart.html)
* **Orders:** [http://localhost:8080/orders.html](http://localhost:8080/orders.html)
* **Admin Dashboard:** [http://localhost:8080/admin.html](http://localhost:8080/admin.html)

---

## 🔑 Demo Credentials

| Role | Email | Password |
|---|---|---|
| **Administrator** | `admin@poojamart.com` | `admin123` |
| **Customer** | `user@poojamart.com` | `user123` |

*(Quick-fill buttons are also provided on the Login page for instant one-click demo testing).*
