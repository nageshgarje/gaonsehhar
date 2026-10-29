import { useState } from 'react'
import { Link, Navigate, Route, Routes } from 'react-router-dom'
import type { AuthResponse, CartItem, Product, User } from './types'
import Home from './pages/Home'
import AuthPage from './pages/AuthPage'
import ProductPage from './pages/ProductPage'
import CartPage from './pages/CartPage'
import OrdersPage from './pages/OrdersPage'
import FarmerPage from './pages/FarmerPage'
import AdminPage from './pages/AdminPage'
export default function App(){
 const [user,setUser]=useState<User|null>(()=>{try{return JSON.parse(localStorage.getItem('user')||'null')}catch{return null}})
 const [cart,setCart]=useState<CartItem[]>([])
 const saved=(value:AuthResponse)=>{localStorage.setItem('token',value.token);localStorage.setItem('user',JSON.stringify(value.user));setUser(value.user)}
 const logout=()=>{localStorage.clear();setUser(null)}
 const addToCart=(product:Product)=>setCart(old=>{const found=old.find(i=>i.product.id===product.id);return found?old.map(i=>i.product.id===product.id?{...i,quantity:i.quantity+1}:i):[...old,{product,quantity:1}]})
 const changeQty=(id:number,quantity:number)=>setCart(old=>quantity<=0?old.filter(i=>i.product.id!==id):old.map(i=>i.product.id===id?{...i,quantity}:i))
 const count=cart.reduce((sum,i)=>sum+i.quantity,0)
 return <><header><Link to="/" className="brand"><span>ग</span> GaonSeGhar</Link><nav><Link to="/">Marketplace</Link>{user?.role==='SELLER'&&<Link to="/farmer">Seller portal</Link>}{user?.role==='ADMIN'&&<Link to="/admin">Admin</Link>}{user&&<Link to="/orders">My orders</Link>}<Link className="cart-link" to="/cart">Basket <b>{count}</b></Link>{user?<><span className="user-name">{user.name}</span><button className="logout" onClick={logout}>Logout</button></>:<Link className="login" to="/login">Sign in</Link>}</nav></header>
 <main><Routes><Route path="/" element={<Home addToCart={addToCart}/>}/><Route path="/products/:id" element={<ProductPage addToCart={addToCart}/>}/><Route path="/cart" element={<CartPage cart={cart} changeQty={changeQty} clear={()=>setCart([])}/>}/><Route path="/orders" element={user?<OrdersPage/>:<Navigate to="/login"/>}/><Route path="/farmer" element={user?.role==='SELLER'?<FarmerPage/>:<Navigate to="/"/>}/><Route path="/admin" element={user?.role==='ADMIN'?<AdminPage/>:<Navigate to="/"/>}/><Route path="/login" element={user?<Navigate to="/"/>:<AuthPage onAuth={saved}/>}/><Route path="*" element={<Navigate to="/"/>}/></Routes></main><footer><div><b>GaonSeGhar</b><span>Growing livelihoods, nourishing homes.</span></div><div><b>Village direct</b><span>One marketplace. Many communities.</span></div></footer></>
}
