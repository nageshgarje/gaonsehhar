import { useEffect, useState } from 'react'
import { myOrders } from '../api'
import type { Order } from '../types'
export default function OrdersPage(){
 const [orders,setOrders]=useState<Order[]>([])
 const [offline,setOffline]=useState(false)
 useEffect(()=>{myOrders().then(setOrders).catch(()=>{setOffline(true);setOrders([])})},[])
 const stage=(status:string)=>['PLACED','CONFIRMED','SHIPPED','DELIVERED'].indexOf(status)
 return <div className="dashboard"><h1>Your orders</h1>{offline&&<p className="notice">Unable to load orders. Make sure you are signed in and the API is running.</p>}
 {!orders.length&&!offline&&<p className="empty">You have not placed an order yet.</p>}
 <div className="orders">{orders.map(o=>{
  const current=Math.max(stage(o.status),0)
  return <article className="order-card" key={o.id}>
   <div className="order-summary"><div><small>ORDER #{o.id}</small><strong>{o.status}</strong></div><span>{o.items.map(i=>`${i.quantity} × ${i.productName}`).join(', ')}</span><small>{o.paymentMethod==='COD'?'Cash on delivery':o.paymentMethod} · {o.paymentStatus}</small><b>₹{o.total}</b></div>
   <div className="delivery-details"><b>Delivery address</b><span>{o.deliveryName} · {o.deliveryPhone}</span><span>{o.deliveryAddress}</span></div>
   <ol className="tracking">{['Placed','Confirmed','Shipped','Delivered'].map((label,index)=><li className={index<=current?'complete':''} key={label}><span>{index<=current?'✓':index+1}</span>{label}</li>)}</ol>
  </article>
 })}</div></div>
}
