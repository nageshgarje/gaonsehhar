import { describe, expect, it } from 'vitest'
export const formatPrice=(price:number)=>`₹${price.toFixed(2)}`
describe('marketplace formatting',()=>{it('formats village product prices',()=>{expect(formatPrice(350)).toBe('₹350.00')})})
