CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL, phone VARCHAR(20), village VARCHAR(100), role VARCHAR(20) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY, seller_id BIGINT NOT NULL REFERENCES users(id), name VARCHAR(160) NOT NULL,
  description TEXT NOT NULL, category VARCHAR(60) NOT NULL, price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
  stock INTEGER NOT NULL CHECK (stock >= 0), unit VARCHAR(30) NOT NULL,
  image_url VARCHAR(500), village VARCHAR(100), active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY, buyer_id BIGINT NOT NULL REFERENCES users(id), status VARCHAR(20) NOT NULL,
  delivery_name VARCHAR(100) NOT NULL, delivery_phone VARCHAR(20) NOT NULL, delivery_address VARCHAR(500) NOT NULL,
  total NUMERIC(12,2) NOT NULL CHECK (total >= 0), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY, order_id BIGINT NOT NULL REFERENCES orders(id), product_id BIGINT NOT NULL REFERENCES products(id),
  seller_id BIGINT NOT NULL REFERENCES users(id), quantity INTEGER NOT NULL CHECK (quantity > 0),
  unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0), product_name VARCHAR(160) NOT NULL
);
CREATE INDEX idx_products_active_category ON products(active, category);
CREATE INDEX idx_products_seller ON products(seller_id);
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
