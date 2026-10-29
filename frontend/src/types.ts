export type Role = 'CUSTOMER' | 'SELLER' | 'ADMIN'
export interface User { id:number; name:string; email:string; role:Role; village?:string }
export interface Product { id:number; sellerId:number; sellerName:string; name:string; description:string; category:string; price:number; stock:number; unit:string; imageUrl?:string; village?:string }
export interface AuthResponse { token:string; user:User }
export interface CartItem { product:Product; quantity:number }
export interface Order { id:number; status:string; paymentMethod:'UPI'|'CARD'|'COD'; paymentStatus:'PENDING'|'PAID'|'FAILED'; total:number; createdAt:string; deliveryName:string; deliveryPhone:string; deliveryAddress:string; items:{productName:string; quantity:number; unitPrice:number}[] }
