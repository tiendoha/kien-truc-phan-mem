// Hardcoded values for simplicity
export const HARDCODED_CUSTOMER_ID = '550e8400-e29b-41d4-a716-446655440000';
export const HARDCODED_RESTAURANT_ID = '660e8400-e29b-41d4-a716-446655440000';

// Sample products for demo
export const SAMPLE_PRODUCTS: Product[] = [
  { id: '5ab21ca4-c15d-4834-bbe4-20d72134cc4a', name: 'Pizza Margherita', price: 12.99, category: 'Main' },
  { id: '7b32e1f5-a6d8-4b9c-9e2f-3a8b1c9d7e0f', name: 'Cheeseburger', price: 8.99, category: 'Main' },
  { id: '9c84f3a2-b7e1-4d5a-8f6b-4c9d2e1a5b6c', name: 'Caesar Salad', price: 6.99, category: 'Salad' },
  { id: '1d57a4b8-c8f2-4e6b-9a7c-5d8e3f2a1b9c', name: 'Chicken Wings', price: 9.99, category: 'Appetizer' },
  { id: '6e79b5c3-d9a3-4f7c-be8d-6e9f4a3b2c1d', name: 'French Fries', price: 3.99, category: 'Side' },
  { id: '2f68c6d4-ea8b-4c9d-bf9e-7f0a5c4d3e2f', name: 'Coca Cola', price: 2.99, category: 'Beverage' },
];

// API endpoints
export const API_ENDPOINTS = {
  ORDERS: {
    BASE: '/orders',
    CREATE: '/orders',
    GET_BY_CUSTOMER: (customerId: string) => `/orders?customerId=${customerId}`,
    UPDATE: (orderId: string) => `/orders/${orderId}`,
    ADD_RATING: (orderId: string) => `/orders/${orderId}/rating`,
    GET_STATISTICS: (customerId: string, startDate?: string, endDate?: string) => {
      const params = new URLSearchParams({ customerId });
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);
      return `/orders/statistics?${params.toString()}`;
    }
  },
  PAYMENTS: {
    BASE: '/payments',
    PROCESS: '/orders/payment', // Updated to use Order Service endpoint
    GET_STATUS: (paymentId: string) => `/payments/${paymentId}`,
    SSE_STATUS: (orderId: string) => `/orders/payment/status/${orderId}` // SSE endpoint for payment status
  },
  CREDITS: {
    BASE: '/credits',
    GET_BALANCE: (customerId: string) => `/credits/total?customerId=${customerId}`,
    ADD: '/credits/add',
  }
} as const;

// Voucher codes for demo
export const SAMPLE_VOUCHERS = {
  'SAVE10': { discountType: 'PERCENTAGE' as const, discountValue: 10 },
  'SAVE5': { discountType: 'AMOUNT' as const, discountValue: 5 },
  'SAVE20': { discountType: 'PERCENTAGE' as const, discountValue: 20 }
};

import type { Product } from '../types';