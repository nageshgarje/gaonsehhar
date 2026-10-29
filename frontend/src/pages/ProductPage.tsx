import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { product } from '../api'
import type { Product } from '../types'
export default function ProductPage({addToCart}:{addToCart:(p:Product)=>void}){const {id}=useParams();const [item,setItem]=useState<Product>();useEffect(()=>{if(id)product(Number(id)).then(setItem)},[id]);if(!item)return <p className="empty">Loading product…</p>
return <div className="detail"><div className="detail-image">{item.imageUrl?<img src={item.imageUrl} alt={item.name}/>:<span>🌾</span>}</div><div className="detail-info"><Link to="/">← Back to marketplace</Link><small>{item.category} · {item.village}</small><h1>{item.name}</h1><p>{item.description}</p><div className="farmer">👩🏽‍🌾<span><small>Made by</small><strong>{item.sellerName}</strong><small>{item.village} village</small></span></div><strong className="detail-price">₹{item.price}<span> / {item.unit}</span></strong><p>{item.stock} {item.unit} available</p><button disabled={!item.stock} onClick={()=>addToCart(item)}>Add to basket</button></div></div>}
