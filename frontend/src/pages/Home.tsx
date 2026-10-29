import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { products } from '../api'
import type { CartItem, Product } from '../types'
export default function Home({addToCart}:{addToCart:(p:Product)=>void}){
 const [items,setItems]=useState<Product[]>([]); const [query,setQuery]=useState(''); const [category,setCategory]=useState('All'); const [error,setError]=useState('')
 useEffect(()=>{products().then(setItems).catch(e=>setError(e.message))},[])
 const categories=['All',...Array.from(new Set(items.map(p=>p.category)))]
 const visible=useMemo(()=>items.filter(p=>(category==='All'||p.category===category)&&`${p.name} ${p.description} ${p.sellerName}`.toLowerCase().includes(query.toLowerCase())),[items,category,query])
 return <>
  <section className="hero"><div><span className="eyebrow">Freshly gathered from local farms</span><h1>Goodness from<br/><i>our gaon.</i></h1><p>Buy honest food, staples and essentials directly from the farmers and makers of nearby villages.</p><a className="primary" href="#shop">Shop village produce <b>→</b></a><div className="hero-proof"><span><strong>40+</strong> village makers</span><span><strong>8</strong> product categories</span><span><strong>100%</strong> producer traceable</span></div></div><div className="hero-art"><div className="farm-sun"></div><div className="farm-hills">🌾</div><div className="hero-badge">FROM<br/><strong>GAON</strong><br/>WITH LOVE</div></div></section>
  <section className="category-strip"><button className={category==='All'?'active':''} onClick={()=>setCategory('All')}><span>🌾</span><b>All products</b></button><button onClick={()=>setCategory('Grains')}><span>🌾</span><b>Grains</b></button><button onClick={()=>setCategory('Pulses')}><span>🫘</span><b>Pulses</b></button><button onClick={()=>setCategory('Vegetables')}><span>🥕</span><b>Vegetables</b></button><button onClick={()=>setCategory('Fruits')}><span>🥭</span><b>Fruits</b></button><button onClick={()=>setCategory('Spices')}><span>🌶️</span><b>Spices</b></button><button onClick={()=>setCategory('Oilseeds')}><span>🫘</span><b>Oilseeds</b></button><button onClick={()=>setCategory('Natural Products')}><span>🍯</span><b>Natural products</b></button><button onClick={()=>setCategory('Farm Inputs')}><span>🌱</span><b>Farm inputs</b></button></section>
  <section className="trust"><span>✓ Verified village makers</span><span>✓ Producer-direct prices</span><span>✓ Fresh & traceable</span><span>✓ Local delivery</span></section>
  <section id="shop" className="market"><div className="section-head"><div><span className="eyebrow">Village marketplace</span><h2>Fresh from nearby farms</h2></div><input className="search" value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search products or farmers"/></div>
  <div className="filters">{categories.map(c=><button key={c} className={category===c?'active':''} onClick={()=>setCategory(c)}>{c}</button>)}</div>
  {error&&<p className="error">{error}</p>}<div className="grid">{visible.map(p=><article className="card" key={p.id}><Link to={`/products/${p.id}`} className="visual">{p.imageUrl?<img src={p.imageUrl} alt={p.name}/>:<span>🌾</span>}<b>{p.category}</b></Link><div className="card-body"><small>{p.village} · by {p.sellerName}</small><h3><Link to={`/products/${p.id}`}>{p.name}</Link></h3><p>{p.description}</p><div className="price-row"><strong>₹{p.price}</strong><span>/ {p.unit}</span></div><button onClick={()=>addToCart(p)} disabled={!p.stock}>Add to basket</button></div></article>)}</div>
  {!visible.length&&<p className="empty">No village products match your search.</p>}</section>
 </>
}
