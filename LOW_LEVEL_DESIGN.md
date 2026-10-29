# GaonSeGhar Low-Level Design

## 1. Checkout flow

`CartPage` owns the client checkout state and sends one immutable order request after selecting a payment method. The flow is:

1. Customer changes quantities in the basket.
2. Customer selects UPI, card, or cash on delivery; selection immediately opens checkout.
3. Checkout displays delivery details and the method-specific payment reference field.
4. Frontend validates required delivery fields and payment references.
5. `POST /api/v1/orders` creates the order.
6. Successful checkout clears the basket and shows order confirmation.
7. The order appears in the customer's order history.
8. The orders page shows simulated payment status, order tracking stages, and the persisted delivery address.

## 2. API contract

### Create an order

`POST /api/v1/orders`

```json
{
  "items": [{ "productId": 1, "quantity": 2 }],
  "deliveryName": "Customer name",
  "deliveryPhone": "9999999999",
  "deliveryAddress": "Rampur delivery point",
  "paymentMethod": "UPI",
  "paymentReference": "customer@upi"
}
```

Allowed payment methods are `UPI`, `CARD`, and `COD`. Server prices are resolved from products; submitted frontend totals are never trusted.
UPI requires an identifier such as `customer@upi`, CARD requires a four-digit demo reference, and COD does not require a payment reference. Method-specific references are validated by the request contract before the order service executes.
UPI and CARD are recorded as `PAID`; COD remains `PENDING` until collection. Delivery name, phone, and address are persisted on the order and returned with order history.

## 3. Backend class responsibilities

- `OrderController`: validates and exposes customer order commands and queries.
- `OrderService`: aggregates quantities, reserves inventory, calculates server-side totals, and creates a payment-aware order.
- `CartPage`: owns payment and delivery form state, opens checkout directly after payment selection, and submits one order request.
- `Order`: aggregate root containing buyer, delivery details, payment method, status, items, and total.
- `OrderItem`: immutable product quantity and price snapshot.
- `ProductRepository`: pessimistic product lookup prevents concurrent overselling.
- `AdminProductController`: isolates catalog mutation operations from public catalog reads.

## 4. Persistence

Flyway migration `V4__add_order_payment_method.sql` adds a non-null payment method column to `orders`. Existing records default to `COD`. Hibernate maps the method as a database enum string through `@Enumerated(EnumType.STRING)`.

## 5. Security and authorization

- Public product read endpoints are anonymous.
- Cart and order endpoints require an authenticated principal.
- Product create/update/delete endpoints require `ADMIN`.
- Customer UI hides administrative product controls and customer requests cannot create or mutate products.

## 6. Error handling

Checkout blocks incomplete delivery data and method-specific references in the UI. The API independently validates every request field and returns a conflict for unavailable inventory, allowing the frontend to display the business error without clearing the basket.

## 7. Verification

Integration coverage must verify public catalog access, customer checkout with every supported payment method, and ADMIN-only product mutation. Frontend build and test commands cover the checkout interaction and role-specific navigation.
