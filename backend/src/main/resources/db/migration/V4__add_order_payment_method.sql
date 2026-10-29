ALTER TABLE orders ADD COLUMN payment_method VARCHAR(20);
UPDATE orders SET payment_method = 'COD' WHERE payment_method IS NULL;
ALTER TABLE orders ALTER COLUMN payment_method SET NOT NULL;
