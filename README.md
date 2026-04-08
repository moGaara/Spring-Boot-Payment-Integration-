# 💳 Payment Integration System — PayPal + Strategy Pattern

> A clean, extensible Spring Boot payment backend using the **Strategy Pattern** and direct PayPal REST API integration (no SDK).

---

## 📌 Overview

This project demonstrates how to:

- Integrate with **PayPal** using direct REST APIs (no SDK)
- Apply the **Strategy Pattern** to support multiple payment providers (e.g., PayPal, Stripe)
- Build a clean, extensible architecture for future payment gateways

---

## 🧠 Key Concepts

### 🔹 Strategy Pattern

The system uses the Strategy Pattern to dynamically select the payment provider at runtime.

```
Controller → PaymentService → StrategyFactory → PaymentStrategy (PayPal / Stripe)
```

Each payment provider implements a common interface:

```java
public interface PaymentStrategy {
    PaymentCreateResponse createPayment(PaymentRequest request);
    PaymentCaptureResponse capturePayment(String paymentId);
}
```

**Example strategies:**
- `PayPalStrategy`
- `StripeStrategy` *(extensible)*

---

## ⚙️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| Spring Boot | Application framework |
| Spring WebClient (WebFlux) | Reactive HTTP client |
| PayPal REST API (Sandbox) | Payment provider |
| Lombok | Boilerplate reduction |

---

## 📦 API Endpoints

### 🔹 1. Create Payment

```http
POST /api/payments/{provider}/create
```

**Example:**

```http
POST /api/payments/paypal/create
```

**Request Body:**

```json
{
  "amount": "10.00",
  "currency": "USD"
}
```

**Response:**

```json
{
  "paymentId": "ORDER_ID",
  "approvalUrl": "https://www.sandbox.paypal.com/checkoutnow?token=..."
}
```

---

### 🔹 2. Capture Payment

**Option A — Postman / Browser redirect:**

```http
GET /api/payments/paypal/capture?token=ORDER_ID
```

**Option B — Manual:**

```http
POST /api/payments/paypal/capture/{orderId}
```

**Response:**

```json
{
  "paymentId": "ORDER_ID",
  "status": "COMPLETED"
}
```

---

### 🔹 3. Cancel Payment

```http
GET /api/payments/paypal/cancel
```

---

## 🧪 How to Test PayPal Integration

### ✅ Step 1 — Create Payment (Postman)

```http
POST http://localhost:8080/api/payments/paypal/create
```

Copy the `approvalUrl` from the response.

---

### ✅ Step 2 — Approve Payment (Browser)

1. Open the `approvalUrl` in your browser
2. Log in using your **PayPal Sandbox Buyer Account**
3. Click **Approve**

---

### ✅ Step 3 — Capture Payment

After approval, you will be automatically redirected to:

```
/api/payments/paypal/capture?token=ORDER_ID
```

Or test manually via Postman:

```http
GET http://localhost:8080/api/payments/paypal/capture?token=ORDER_ID
```

---

## 🔄 Payment Flow

```
1. Create Order  →  Backend calls PayPal API
2. User Approval →  User logs in to PayPal and approves
3. Capture       →  Backend captures payment (money is transferred)
```

---

## ⚠️ Important Notes

- **Capture is required** to complete the payment — without it, money is **NOT** transferred
- PayPal requires the following header even for empty-body requests (like capture):
  ```
  Content-Type: application/json
  ```
- Always **approve** the payment before calling capture — otherwise you'll receive: `ORDER_NOT_APPROVED`

---

## 🚀 Extending the System (Adding Stripe)

To add a new payment provider:

**1. Create a new strategy:**

```java
public class StripeStrategy implements PaymentStrategy {
    // implement createPayment and capturePayment
}
```

**2. Register it in the factory:**

```java
// In StrategyFactory
case "stripe" -> new StripeStrategy(...);
```

**3. Call it via the unified endpoint:**

```http
POST /api/payments/stripe/create
```

No changes needed in the controller or service layer. ✅

---

## 🎯 Project Goals

- Demonstrate clean architecture using the **Strategy Pattern**
- Avoid SDK dependency — direct API integration only
- Make payment providers **easily pluggable**
- Provide a testable backend using Postman

---

## 🧩 Future Improvements

- [ ] Add full **Stripe** integration
- [ ] Add **Webhooks** for production reliability
- [ ] Add **Database** for payment tracking & idempotency
- [ ] Add **Frontend** (React / Angular)

---

## 💡 Summary

This project shows how to:

- Build a **flexible payment system** with minimal coupling
- Integrate PayPal using **modern REST APIs**
- Use **design patterns** in real-world backend scenarios

---

## 👨‍💻 Author

**Mohamed Sayed**

---

> ⭐ If you found this project useful, consider giving it a star!
