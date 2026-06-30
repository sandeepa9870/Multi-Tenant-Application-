# Quick Start Guide - Role-Based Access Control

## 📋 Prerequisites
- JDK 17 or higher
- Gradle
- Postman (for API testing)
- Running database (H2 if using default configuration)

---

## 🚀 Getting Started

### 1. Start the Application
```bash
cd "Multi-tenant-application"
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 2. Database Initialization
- H2 console: http://localhost:8080/h2-console
- Roles are auto-seeded on startup
- Default Super Admin user is created

---

## 🔐 Default Credentials

### Super Admin
```
Email:    superadmin@example.com
Password: ChangeMe123!
Role:     ROLE_SUPER_ADMIN
```

---

## 📱 API Testing Workflow

### Step 1: Login as Super Admin
**POST** `/api/auth/login`
```json
{
  "email": "superadmin@example.com",
  "password": "ChangeMe123!"
}
```
**Response** will contain:
- `accessToken`: Use this in Authorization header
- `refreshToken`: For refreshing tokens

### Step 2: Create Products (as Admin/SuperAdmin)
**POST** `/api/admin/products`
```json
{
  "name": "Laptop Pro",
  "description": "High-performance laptop",
  "price": 1299.99,
  "stock": 50,
  "sku": "LAPTOP-001",
  "category": "Electronics"
}
```

Save the product ID from response.

### Step 3: Register a New User
**POST** `/api/auth/register`
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "Password123!",
  "role": "ROLE_USER"
}
```

### Step 4: Login as User
**POST** `/api/auth/login`
```json
{
  "email": "john@example.com",
  "password": "Password123!"
}
```

Save the new `accessToken`.

### Step 5: Browse Public Products (No Auth Required)
**GET** `/api/public/products`
- No authentication header needed
- View all active products

### Step 6: Create an Order (as User)
**POST** `/api/user/orders`
```json
{
  "productId": "{product-uuid}",
  "quantity": 2
}
```

Use the `accessToken` from Step 4 in Authorization header.

### Step 7: View My Orders
**GET** `/api/user/orders`
- Shows only your orders
- Include `accessToken` in Authorization header

### Step 8: Manage Orders (as Super Admin)
**GET** `/api/super-admin/orders`
- View all orders in system
- Use Super Admin token from Step 1

**PUT** `/api/super-admin/orders/{orderId}/status`
```json
{
  "status": "SHIPPED"
}
```

---

## 🔗 Using Postman Collection

### Import Collection
1. Open Postman
2. Click "Import" → Select `postman_collection.json`
3. Collection is organized by role and functionality

### Set Environment Variables
```
base_url = http://localhost:8080
access_token = (auto-populated after login)
refresh_token = (auto-populated after login)
```

Tokens are automatically extracted after login/register requests.

---

## 🎯 Testing Different Roles

### Test as Super Admin
1. Use "Auth - Login" with superadmin credentials
2. Try all endpoints under "Super Admin - Management"
3. You should have access to all operations

### Test as Admin
1. Register with `role: "ROLE_ADMIN"` (requires Super Admin approval first)
2. As Super Admin, enable the admin user via `/api/super-admin/users/{id}/enable`
3. Login as admin
4. Try creating/updating products via `/api/admin/products`
5. Verify you cannot access `/api/super-admin` or `/api/user` endpoints

### Test as User
1. Register with `role: "ROLE_USER"`
2. Login as user
3. Browse products via `/api/public/products`
4. Create order via `/api/user/orders`
5. Verify you cannot access admin or super-admin endpoints

---

## 📊 API Permissions Matrix

| Endpoint | Public | User | Admin | SuperAdmin |
|----------|--------|------|-------|-----------|
| /api/public/products | ✅ | ✅ | ✅ | ✅ |
| /api/user/orders | ❌ | ✅ | ❌ | ✅ |
| /api/admin/products | ❌ | ❌ | ✅ | ✅ |
| /api/super-admin/* | ❌ | ❌ | ❌ | ✅ |
| /api/auth/* | ✅ | ✅ | ✅ | ✅ |

---

## ⚠️ Common Issues & Solutions

### Issue 1: "Invalid credentials"
- Ensure email and password are correct
- For Super Admin: `superadmin@example.com` / `ChangeMe123!`

### Issue 2: "Unauthorized" (401)
- Token might be expired
- Use `/api/auth/refresh` with your refresh token
- Ensure Authorization header format: `Bearer {token}`

### Issue 3: "Forbidden" (403)
- Your role doesn't have permission
- Check endpoint role requirements
- Try with different role or Super Admin

### Issue 4: "Product not found"
- Verify product UUID is correct
- For User ordering: Use product ID from `/api/public/products`

### Issue 5: "Insufficient stock"
- Product doesn't have enough inventory
- Check stock quantity: `stock >= quantity`

---

## 🔄 Order Status Lifecycle

```
PENDING      → User creates order
    ↓
CONFIRMED    → Super Admin confirms
    ↓
SHIPPED      → Super Admin marks as shipped
    ↓
DELIVERED    → Super Admin marks as delivered
    ↓
(CANCELLED)  → Can only cancel before shipped
```

---

## 📝 Request Headers

### All Protected Endpoints
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Public Endpoints
```
Content-Type: application/json
```

---

## 🧪 Sample Test Scenarios

### Scenario 1: Complete Purchase Flow
1. Login as Super Admin
2. Create 3 products
3. Register 2 users
4. Login as user 1, create orders
5. Login as user 2, create orders
6. Logout, browse products publicly
7. Login as Super Admin, view all orders
8. Update order statuses

### Scenario 2: User Data Privacy
1. Register user A and user B
2. Login as user A, create order
3. Try to access user B's orders (should fail)
4. Login as Super Admin, verify access to all orders

### Scenario 3: Admin Product Management
1. Register admin user
2. Super Admin enables the admin
3. Login as admin
4. Create/update/delete products
5. Verify users can see updated products

---

## 🔍 SQL Queries (H2 Console)

### View All Products
```sql
SELECT * FROM products WHERE active = true;
```

### View All Orders
```sql
SELECT * FROM orders;
```

### View All Users
```sql
SELECT * FROM users;
```

### View User Roles
```sql
SELECT u.email, r.name FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id;
```

---

## 📚 Documentation Files

- `IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- `RBAC_DOCUMENTATION.md` - Role permissions and API reference
- `postman_collection.json` - API testing collection
- `MODULE1_DOCUMENTATION.md` - Original module documentation

---

## 🎓 Learning Path

1. **Understand RBAC** - Read RBAC_DOCUMENTATION.md
2. **Review Code** - Check service implementations
3. **Test Manually** - Use Postman collection
4. **Experiment** - Try different role combinations
5. **Extend** - Add more features as needed

---

## 🚀 Deployment Checklist

- [ ] Update database credentials in `application.properties`
- [ ] Change default Super Admin password
- [ ] Enable HTTPS in production
- [ ] Set JWT token expiry appropriately
- [ ] Configure email notifications
- [ ] Set up logging and monitoring
- [ ] Test all endpoints in staging
- [ ] Document custom configurations
- [ ] Backup database before deployment
- [ ] Review security settings

---

## 📞 Support

For issues or questions:
1. Check RBAC_DOCUMENTATION.md
2. Review error logs
3. Verify API endpoint format
4. Ensure Authorization header is correct
5. Check role permissions in matrix above

---

**Status**: Ready for Testing ✅
**Last Updated**: June 30, 2026

