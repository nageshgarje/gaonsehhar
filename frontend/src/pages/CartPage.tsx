import { useState } from 'react'
import { placeOrder } from '../api'
import type { CartItem } from '../types'

type PaymentMethod = 'UPI' | 'CARD' | 'COD'

export default function CartPage({cart,changeQty,clear}:{cart:CartItem[],changeQty:(id:number,n:number)=>void,clear:()=>void}){
 const [done,setDone]=useState(false)
 const [loading,setLoading]=useState(false)
 const [error,setError]=useState('')
 const [payment,setPayment]=useState<PaymentMethod|null>(null)
 const [checkout,setCheckout]=useState(false)
 const [deliveryName,setDeliveryName]=useState('Village Customer')
 const [deliveryPhone,setDeliveryPhone]=useState('9999999999')
 const [deliveryAddress,setDeliveryAddress]=useState('Rampur cluster, delivery point')
 const [paymentReference,setPaymentReference]=useState('')
 const total=cart.reduce((s,i)=>s+i.product.price*i.quantity,0)

 const selectPayment=(method:PaymentMethod)=>{
  setPayment(method)
  setPaymentReference('')
  setError('')
  setCheckout(true)
 }

 const place=async()=>{
  if(!payment){setError('Select a payment method');return}
  if(!deliveryName.trim() || !deliveryPhone.trim() || !deliveryAddress.trim()){setError('Please complete the delivery details');return}
  if(payment==='UPI' && !paymentReference.trim()){setError('Enter your UPI ID');return}
  if(payment==='CARD' && !paymentReference.trim()){setError('Enter the card reference');return}
  setLoading(true);setError('')
  try{
   await placeOrder({items:cart.map(i=>({productId:i.product.id,quantity:i.quantity})),deliveryName,deliveryPhone,deliveryAddress,paymentMethod:payment,paymentReference:paymentReference||undefined})
   clear();setDone(true)
  }catch(e){setError((e as Error).message)}finally{setLoading(false)}
 }

 if(done)return <div className="dashboard success"><span>✓</span><h1>Order placed</h1><p>Your producer-direct order has been received.</p><button onClick={()=>setDone(false)}>Continue shopping</button></div>
 return <div className="dashboard"><h1>Your basket</h1>{!cart.length?<p className="empty">Your basket is empty.</p>:<div className="cart-layout">
  <div className="cart-list">{cart.map(i=><article key={i.product.id}><div className="cart-thumb">{i.product.imageUrl?<img src={i.product.imageUrl} alt={i.product.name}/>: '🌾'}</div><div><strong>{i.product.name}</strong><small>{i.product.sellerName}</small></div><div className="qty"><button onClick={()=>changeQty(i.product.id,i.quantity-1)}>−</button><span>{i.quantity}</span><button onClick={()=>changeQty(i.product.id,i.quantity+1)}>+</button></div><b>₹{i.product.price*i.quantity}</b></article>)}</div>
  {!checkout?
   <aside className="summary checkout-payment">
    <h3>Select payment method</h3>
    <div className="payment-methods">
     <button type="button" className={payment==='UPI'?'active':''} onClick={()=>selectPayment('UPI')}><b>UPI</b><span>Pay instantly using UPI ID</span></button>
     <button type="button" className={payment==='CARD'?'active':''} onClick={()=>selectPayment('CARD')}><b>Card</b><span>Credit or debit card</span></button>
     <button type="button" className={payment==='COD'?'active':''} onClick={()=>selectPayment('COD')}><b>Cash on delivery</b><span>Pay when your order arrives</span></button>
    </div>
    <p className="total"><span>Amount payable</span><b>₹{total}</b></p>
   </aside>:
   <aside className="summary checkout-form">
    <button type="button" className="back-link" onClick={()=>setCheckout(false)}>← Change payment</button>
    <h3>Checkout details</h3>
    <label>Full name<input value={deliveryName} onChange={e=>setDeliveryName(e.target.value)}/></label>
    <label>Phone number<input value={deliveryPhone} onChange={e=>setDeliveryPhone(e.target.value)}/></label>
    <label>Delivery address<textarea value={deliveryAddress} onChange={e=>setDeliveryAddress(e.target.value)}/></label>
    {payment==='UPI'&&<label>UPI ID<input placeholder="name@upi" value={paymentReference} onChange={e=>setPaymentReference(e.target.value)}/></label>}
    {payment==='CARD'&&<label>Card reference (demo)<input placeholder="Last 4 digits" maxLength={4} value={paymentReference} onChange={e=>setPaymentReference(e.target.value.replace(/\D/g,''))}/></label>}
    <div className="checkout-method">Selected payment: <b>{payment==='COD'?'Cash on delivery':payment}</b></div>
    <p className="total"><span>Total</span><b>₹{total}</b></p>
    <button type="button" className="checkout-action" disabled={loading} onClick={place}>{loading?'Processing…':'Place order'}</button>
    {error&&<small className="error">{error}</small>}
   </aside>
  }
 </div>}</div>
}
