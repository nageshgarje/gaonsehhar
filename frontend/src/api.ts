import type { AuthResponse, Order, Product } from './types'
export class ApiError extends Error {
  constructor(message:string, public status:number) {
    super(message)
    this.name='ApiError'
  }
}
const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'
const dummyProducts: Product[] = [
 {id:101,sellerId:11,sellerName:'Mangal Patil',name:'Sona Wheat',description:'Golden, stone-ground ready wheat from rain-fed village farms.',category:'Grains',price:48,stock:120,unit:'kg',village:'Rampur',imageUrl:'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?auto=format&fit=crop&w=800&q=80'},
 {id:102,sellerId:12,sellerName:'Sita Devi',name:'Organic Red Rice',description:'Naturally grown heirloom rice with a rich earthy aroma.',category:'Grains',price:120,stock:80,unit:'kg',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=800&q=80'},
 {id:103,sellerId:13,sellerName:'Babu Pawar',name:'Maharashtra Jowar',description:'Cleaned and sorted sorghum millets from local family farms.',category:'Grains',price:65,stock:90,unit:'kg',village:'Sangvi',imageUrl:'https://images.unsplash.com/photo-1586444248902-2f64eddc13df?auto=format&fit=crop&w=800&q=80'},
 {id:104,sellerId:14,sellerName:'Anita Solanke',name:'Arhar Toor Dal',description:'Premium unpolished toor dal, naturally sundried.',category:'Pulses',price:165,stock:60,unit:'kg',village:'Rampur',imageUrl:'https://images.unsplash.com/photo-1585996748464-6f3e2e6e9f8e?auto=format&fit=crop&w=800&q=80'},
 {id:105,sellerId:15,sellerName:'Laxman More',name:'Yellow Moong Dal',description:'Light, protein-rich moong dal grown using natural methods.',category:'Pulses',price:150,stock:55,unit:'kg',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1613758235666-13be7ca4fca4?auto=format&fit=crop&w=800&q=80'},
 {id:106,sellerId:16,sellerName:'Kaveri Farm',name:'Fresh Farm Tomato',description:'Juicy vine tomatoes harvested at dawn for peak freshness.',category:'Vegetables',price:38,stock:100,unit:'kg',village:'Sangvi',imageUrl:'https://images.unsplash.com/photo-1546094096-0df4bcaaa337?auto=format&fit=crop&w=800&q=80'},
 {id:107,sellerId:17,sellerName:'Raju Gaikwad',name:'Nasik Red Onion',description:'Sun-cured red onions with strong flavour and long shelf life.',category:'Vegetables',price:32,stock:140,unit:'kg',village:'Rampur',imageUrl:'https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?auto=format&fit=crop&w=800&q=80'},
 {id:108,sellerId:18,sellerName:'Geeta Jadhav',name:'Farm Potato',description:'Fresh, clean potatoes from fertile black cotton soil.',category:'Vegetables',price:28,stock:160,unit:'kg',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&w=800&q=80'},
 {id:109,sellerId:19,sellerName:'Amruta Mango Farm',name:'Alphonso Mango',description:'Sun-ripened Ratnagiri Alphonso mangoes, naturally sweet.',category:'Fruits',price:680,stock:25,unit:'box',village:'Ratnagiri',imageUrl:'https://images.unsplash.com/photo-1553279768-865429fa0078?auto=format&fit=crop&w=800&q=80'},
 {id:110,sellerId:20,sellerName:'Meera Shinde',name:'Yelakki Banana',description:'Naturally ripened small bananas with creamy sweetness.',category:'Fruits',price:70,stock:45,unit:'bunch',village:'Sangvi',imageUrl:'https://images.unsplash.com/photo-1603833665858-e61d17a86224?auto=format&fit=crop&w=800&q=80'},
 {id:111,sellerId:21,sellerName:'Sunil Deshmukh',name:'Pink Guava',description:'Fragrant guavas, crisp and rich in natural goodness.',category:'Fruits',price:90,stock:35,unit:'kg',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1536511132770-e5058c7e8c46?auto=format&fit=crop&w=800&q=80'},
 {id:112,sellerId:22,sellerName:'Maya Wagh',name:'Lakadong Turmeric',description:'High-curcumin turmeric, cleaned and sun-dried in the village.',category:'Spices',price:240,stock:30,unit:'kg',village:'Rampur',imageUrl:'https://images.unsplash.com/photo-1615485500704-8e990f9900f7?auto=format&fit=crop&w=800&q=80'},
 {id:113,sellerId:23,sellerName:'Ganga Masala',name:'Byadgi Red Chilli',description:'Aromatic, deep-red chillies for balanced heat and colour.',category:'Spices',price:210,stock:40,unit:'kg',village:'Sangvi',imageUrl:'https://images.unsplash.com/photo-1583119022894-919a68a3d0e3?auto=format&fit=crop&w=800&q=80'},
 {id:114,sellerId:24,sellerName:'Dattu Kharade',name:'Farm Cumin',description:'Whole cumin seeds with an intense earthy fragrance.',category:'Spices',price:320,stock:28,unit:'kg',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=800&q=80'},
 {id:115,sellerId:25,sellerName:'Ramrao Oil Mills',name:'Native Groundnut Oil',description:'Cold-pressed groundnut oil made in small traditional batches.',category:'Oilseeds',price:280,stock:32,unit:'litre',village:'Rampur',imageUrl:'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=800&q=80'},
 {id:116,sellerId:26,sellerName:'Kisan Agro Farm',name:'Non-GMO Soybean',description:'Clean, whole soybean sourced directly from local growers.',category:'Oilseeds',price:95,stock:85,unit:'kg',village:'Sangvi',imageUrl:'https://images.unsplash.com/photo-1594489428504-5c0c6f5f8b8c?auto=format&fit=crop&w=800&q=80'},
 {id:117,sellerId:27,sellerName:'Maya Wagh',name:'Forest Wild Honey',description:'Raw, unheated forest honey collected from sustainable hives.',category:'Natural Products',price:280,stock:14,unit:'jar',village:'Rampur',imageUrl:'https://images.unsplash.com/photo-1587049352846-4a222e784d38?auto=format&fit=crop&w=800&q=80'},
 {id:118,sellerId:28,sellerName:'Babasaheb Jagtap',name:'Traditional Jaggery',description:'Chemical-free cane jaggery, handmade in earthen vats.',category:'Natural Products',price:120,stock:50,unit:'kg',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=800&q=80'},
 {id:119,sellerId:29,sellerName:'Ramrao Patil',name:'A2 Gir Cow Ghee',description:'Bilona-style A2 ghee prepared slowly over a wood fire.',category:'Natural Products',price:650,stock:18,unit:'500 g',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?auto=format&fit=crop&w=800&q=80'},
 {id:120,sellerId:30,sellerName:'Gokul Dairy',name:'Fresh Cow Milk',description:'Morning fresh milk from grass-fed indigenous cows.',category:'Natural Products',price:60,stock:40,unit:'litre',village:'Rampur',imageUrl:'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=800&q=80'},
 {id:121,sellerId:31,sellerName:'Hari Seeds',name:'Native Vegetable Seeds',description:'Open-pollinated seasonal seed pack for kitchen gardens.',category:'Farm Inputs',price:80,stock:100,unit:'pack',village:'Sangvi',imageUrl:'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?auto=format&fit=crop&w=800&q=80'},
 {id:122,sellerId:32,sellerName:'Green Roots Collective',name:'Organic Farm Manure',description:'Well-aged farm compost that restores soil health naturally.',category:'Farm Inputs',price:350,stock:45,unit:'25 kg bag',village:'Nandgaon',imageUrl:'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?auto=format&fit=crop&w=800&q=80'}
]
async function request<T>(path:string,init:RequestInit={}):Promise<T>{
  const token=localStorage.getItem('token'); const headers=new Headers(init.headers); headers.set('Content-Type','application/json'); if(token) headers.set('Authorization',`Bearer ${token}`)
 let response:Response
 try{
  response=await fetch(`${API}${path}`,{...init,headers})
 }catch{
  throw new Error(`Cannot reach the API at ${API}. Make sure the backend is running on port 8080.`)
 }
  if(!response.ok){const body=await response.json().catch(()=>({message:'Request failed'}));throw new ApiError(body.message||`Request failed (${response.status})`,response.status)}
  return response.status===204 ? undefined as T : response.json()
}
export const api=request
export const products=async()=>{try{const r=await request<{content:Product[]} | Product[]>('/api/v1/products?size=40'); return Array.isArray(r)?r:r.content}catch{return dummyProducts}}
export const product=async(id:number)=>{try{return await request<Product>(`/api/v1/products/${id}`)}catch{return dummyProducts.find(p=>p.id===id)!}}
export const login=(email:string,password:string)=>request<AuthResponse>('/api/v1/auth/login',{method:'POST',body:JSON.stringify({email,password})})
export const register=(body:{name:string,email:string,password:string,role:'CUSTOMER'|'SELLER'})=>request<AuthResponse>('/api/v1/auth/register',{method:'POST',body:JSON.stringify({...body, ...((body.role==='SELLER')?{village:'Rampur'}:{})})})
export const adminCreateProduct=(body:unknown)=>api<import("./types").Product>("/api/v1/admin/products",{method:"POST",body:JSON.stringify(body)})
export const adminUpdateProduct=(id:number,body:unknown)=>api<import("./types").Product>(`/api/v1/admin/products/${id}`,{method:"PUT",body:JSON.stringify(body)})
export const adminDeleteProduct=(id:number)=>api<void>(`/api/v1/admin/products/${id}`,{method:"DELETE"})
export const placeOrder=(body:unknown)=>request<Order>('/api/v1/orders',{method:'POST',body:JSON.stringify(body)})
export const myOrders=()=>request<Order[]>('/api/v1/orders')
