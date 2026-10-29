INSERT INTO users (name, email, password_hash, village, role)
VALUES ('GaonSeGhar Admin', 'admin@gaonsehhar.local', '$2b$10$avm4W1PS2VW/SgyGAd7kje/HaJDCupo9WdBgw6a/NNkFdP7.juQ0a', 'Rampur', 'ADMIN')
ON CONFLICT (email) DO UPDATE SET password_hash = EXCLUDED.password_hash, role = 'ADMIN';
