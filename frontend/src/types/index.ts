// Domain Types
export interface Order {
  id: string;
  customerId: string;
  restaurantId: string;
  items: OrderItem[];
  voucher?: Voucher;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  productId: string;
  quantity: number;
  price: number;
  subTotal: number;
}

export interface Voucher {
  code: string;
  discountType: 'PERCENTAGE' | 'AMOUNT';
  discountValue: number;
}

export type OrderStatus = 'PENDING' | 'PAID' | 'APPROVED' | 'CANCELLED';

export interface Payment {
  paymentId: string;
  orderId: string;
  customerId: string;
  price: number;
  status: PaymentStatus;
  message: string;
}

export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED';

export interface CreditEntry {
  id: string;
  customerId: string;
  amount: number;
  type: 'ADD' | 'DEDUCT';
  description: string;
  createdAt: string;
}

export interface CreditBalance {
  customerId: string;
  totalCredit: number;
  lastUpdated: string;
}

export interface Statistics {
  customerId: string;
  totalOrders: number;
  totalRevenue: number;
  averageOrderValue: number;
  orderStatusCounts: Record<OrderStatus, number>;
  startDate: string;
  endDate: string;
}

// API Request/Response Types
export interface CreateOrderRequest {
  customerId: string;
  restaurantId: string;
  items: CreateOrderItemRequest[];
  voucherCode?: string;
}

export interface CreateOrderItemRequest {
  productId: string;
  quantity: number;
  price: number;
}

export interface UpdateOrderRequest {
  items: CreateOrderItemRequest[];
  voucherCode?: string;
}

export interface RatingRequest {
  score: number; // 1-5
  comment?: string;
}

export interface PaymentRequest {
  orderId: string;
  customerId: string;
  price: number;
}

export interface AddCreditRequest {
  customerId: string;
  amount: number;
  description: string;
}

export interface StatisticsParams {
  customerId: string;
  startDate?: string;
  endDate?: string;
}

// UI Types
export interface Product {
  id: string;
  name: string;
  price: number;
  category?: string;
}

export interface AppContextType {
  customerId: string;
  orders: Order[];
  payments: Payment[];
  creditBalance: CreditBalance | null;
  loading: boolean;
  error: string | null;
  refreshData: () => Promise<void>;
  setError: (error: string | null) => void;
  setLoading: (loading: boolean) => void;
  setCustomerId: (customerId: string) => void;
}

// Form Types
export interface OrderFormData {
  restaurantId: string;
  items: CreateOrderItemRequest[];
  voucherCode?: string;
}

export interface CreditFormData {
  amount: number;
  description: string;
}

export interface PaymentFormData {
  orderId: string;
  amount: number;
  paymentMethod: 'CREDIT' | 'OTHER';
}