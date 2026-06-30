# Role-Based Access Control Implementation

## Overview
This document describes the role-based access control (RBAC) implementation for the Multi-Tenant E-Commerce SaaS application.

## Roles & Permissions

### 1. **ROLE_SUPER_ADMIN** - Full Access
Super Admins have complete control over the system:
- **User Management**: Create, enable, disable, lock, unlock users, assign roles
- **Role Management**: Create and manage all roles
- **Product Management**: Create, update, delete all products
- **Order Management**: View all orders, update order status, cancel orders
- **Endpoints**: `/api/super-admin/**`

### 2. **ROLE_ADMIN** - Product Management
Admins can manage products and view related data:
- **Product Management**: Create, update, delete, view all products
- **Access**: Cannot manage users or modify order status
- **Endpoints**: `/api/admin/products/**`

### 3. **ROLE_USER** - Purchase Products
Users can browse and purchase products:
- **Browse Products**: View all active products (public endpoint)
- **Purchase**: Create orders, view own orders, cancel own orders
- **Restrictions**: Cannot view other users' orders or manage products
- **Endpoints**: 
  - Public: `/api/public/products/**`
  - Private: `/api/user/orders/**`

---

## API Endpoints

### Authentication Endpoints (No Auth Required)
```
POST   /api/auth/register          - Register new user
POST   /api/auth/login             - Login user
POST   /api/auth/refresh           - Refresh JWT token
```

### Public Product Endpoints (No Auth Required)
```
GET    /api/public/products        - Get all active products
GET    /api/public/products/{id}   - Get product by ID
GET    /api/public/products/category/{category} - Get products by category
```

### Admin Product Endpoints (ROLE_ADMIN, ROLE_SUPER_ADMIN)
```
POST   /api/admin/products                      - Create product
GET    /api/admin/products                      - Get all products
GET    /api/admin/products/{id}                 - Get product by ID
PUT    /api/admin/products/{id}                 - Update product
DELETE /api/admin/products/{id}                 - Delete (deactivate) product
GET    /api/admin/products/category/{category}  - Get products by category
```

### User Order Endpoints (ROLE_USER)
```
POST   /api/user/orders                              - Create order
GET    /api/user/orders                              - Get my orders
GET    /api/user/orders/{orderId}                    - Get order by ID
GET    /api/user/orders/order-number/{orderNumber}  - Get order by order number
DELETE /api/user/orders/{orderId}                    - Cancel order
```

### Super Admin Management Endpoints (ROLE_SUPER_ADMIN)

#### Role Management
```
POST   /api/super-admin/roles          - Create role
GET    /api/super-admin/roles          - List all roles
```

#### User Management
```
GET    /api/super-admin/users                    - Get all users
GET    /api/super-admin/users/{id}               - Get user by ID
PUT    /api/super-admin/users/{id}/enable       - Enable user
PUT    /api/super-admin/users/{id}/disable      - Disable user
PUT    /api/super-admin/users/{id}/lock         - Lock user
PUT    /api/super-admin/users/{id}/unlock       - Unlock user
POST   /api/super-admin/users/{userId}/roles/{roleId} - Assign role to user
```

#### Order Management
```
GET    /api/super-admin/orders                  - Get all orders
GET    /api/super-admin/orders/{id}             - Get order by ID
PUT    /api/super-admin/orders/{id}/status      - Update order status
GET    /api/super-admin/orders/status/{status}  - Get orders by status
GET    /api/super-admin/users/{userId}/orders   - Get user's orders
```

---

## Data Models

### Product Entity
```java
id (Long) - Primary key
name (String) - Product name
description (String) - Product description
price (Double) - Product price
stock (Integer) - Available stock
sku (String) - SKU (unique)
category (String) - Product category
active (Boolean) - Is product active?
createdAt (Timestamp) - Creation timestamp
updatedAt (Timestamp) - Last update timestamp
```

### Order Entity
```java
id (Long) - Primary key
user (User) - User who placed the order
product (Product) - Ordered product
quantity (Integer) - Order quantity
totalPrice (Double) - Total order price
status (String) - PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
orderNumber (String) - Unique order number
createdAt (Timestamp) - Order creation time
updatedAt (Timestamp) - Last update time
```

### User Roles Mapping
- Users can have multiple roles
- Default role for new registrations: ROLE_USER
- Admin registrations require super admin approval (disabled by default)

---

## Security Features

1. **JWT Authentication**: All protected endpoints use JWT tokens
2. **Role-Based Authorization**: Method-level security with @PreAuthorize
3. **Request Validation**: All DTOs include validation constraints
4. **Stock Management**: Automatic stock deduction on order creation
5. **Order Tracking**: Unique order numbers for tracking purchases
6. **User Data Privacy**: Users can only see their own orders

---

## Testing with Postman

1. **Default Super Admin Credentials**:
   - Email: `superadmin@example.com`
   - Password: `ChangeMe123!`

2. **Create Sample Data**:
   - Login as Super Admin
   - Use admin endpoints to create products
   - Register a regular user to test ordering

3. **Test Workflow**:
   1. Login as Super Admin → Get token
   2. Create products via `/api/admin/products`
   3. Register a new user
   4. Login as user → Get token
   5. Create order via `/api/user/orders`
   6. View own orders
   7. As Super Admin: View all orders and update status

---

## Default Roles (Auto-seeded on startup)

1. **ROLE_SUPER_ADMIN** - Super administrator with full access
2. **ROLE_ADMIN** - Organization admin (product management)
3. **ROLE_USER** - End user (product purchase)

---

## File Structure

```
src/main/java/com/example/multitenant/
├── controller/
│   ├── AuthController.java
│   ├── ProductController.java (Admin)
│   ├── OrderController.java (User)
│   ├── PublicProductController.java
│   └── SuperAdminRoleController.java
├── service/
│   ├── ProductService.java (Interface)
│   ├── OrderService.java (Interface)
│   └── impl/
│       ├── ProductServiceImpl.java
│       └── OrderServiceImpl.java
├── entity/
│   ├── Product.java
│   ├── Order.java
│   ├── User.java
│   └── Role.java
├── repository/
│   ├── ProductRepository.java
│   └── OrderRepository.java
├── dto/
│   ├── product/
│   │   └── CreateProductRequest.java
│   └── order/
│       └── CreateOrderRequest.java
└── security/
    └── SecurityConfig.java
```

---

## Error Handling

Common API error responses:
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - User doesn't have required role
- `404 Not Found` - Resource not found
- `400 Bad Request` - Invalid input data
- `409 Conflict` - Duplicate resource (e.g., duplicate SKU)

---

## Future Enhancements

1. Implement payment gateway integration
2. Add order history and analytics
3. Implement product reviews and ratings
4. Add inventory management notifications
5. Implement order tracking with email notifications
6. Add multi-tenant support with tenant isolation
7. Implement soft deletes for audit trails

---

## Contact & Support

For issues or questions, contact the development team.

