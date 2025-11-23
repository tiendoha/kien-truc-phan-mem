import axios, { AxiosInstance, AxiosError } from 'axios';
import {
  Order,
  CreateOrderRequest,
  UpdateOrderRequest,
  RatingRequest,
  Payment,
  PaymentRequest,
  CreditEntry,
  CreditBalance,
  AddCreditRequest,
  Statistics,
  StatisticsParams,
  OrderStatus
} from '../types';
import { API_ENDPOINTS } from '../utils/constants';

// Create separate axios instances for different services
const orderApiClient: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/vnd.api.v1+json',
  },
});

const paymentApiClient: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8082/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/vnd.api.v1+json',
  },
});

// Common interceptor function
const setupInterceptors = (client: AxiosInstance, serviceName: string) => {
  // Request interceptor for debugging
  client.interceptors.request.use(
    (config) => {
      console.log(`${serviceName} Request: ${config.method?.toUpperCase()} ${config.url}`, config.data);
      return config;
    },
    (error) => {
      console.error(`${serviceName} Request Error:`, error);
      return Promise.reject(error);
    }
  );

  // Response interceptor for debugging and error handling
  client.interceptors.response.use(
    (response) => {
      console.log(`${serviceName} Response: ${response.config.method?.toUpperCase()} ${response.config.url}`, response.data);
      return response;
    },
    (error: AxiosError) => {
      console.error(`${serviceName} Response Error:`, error.response?.data || error.message);

      // Handle common error scenarios
      if (error.code === 'ECONNABORTED') {
        throw new Error('Request timeout. Please try again.');
      }

      if (error.response) {
        const status = error.response.status;
        const data = error.response.data as any;

        switch (status) {
          case 400:
            throw new Error(data?.message || 'Bad request. Please check your input.');
          case 404:
            throw new Error('Resource not found.');
          case 422:
            throw new Error(data?.message || 'Validation failed. Please check your input.');
          case 500:
            throw new Error('Server error. Please try again later.');
          default:
            throw new Error(data?.message || `Request failed with status ${status}`);
        }
      }

      if (error.code === 'ERR_NETWORK') {
        throw new Error('Network error. Please check your connection.');
      }

      throw new Error('An unexpected error occurred. Please try again.');
    }
  );
};

// Setup interceptors for both clients
setupInterceptors(orderApiClient, 'Order');
setupInterceptors(paymentApiClient, 'Payment');

// Helper function to transform backend OrderResponse to frontend Order
const transformOrderResponse = (backendOrder: any): Order => ({
  id: backendOrder.id,
  customerId: backendOrder.customerId,
  restaurantId: backendOrder.restaurantId,
  items: backendOrder.items?.map((item: any) => ({
    productId: item.productId,
    quantity: item.quantity,
    price: Number(item.price),
    subTotal: Number(item.subTotal)
  })) || [],
  voucher: backendOrder.voucherCode ? {
    code: backendOrder.voucherCode,
    discountType: backendOrder.discountType || 'AMOUNT',
    discountValue: Number(backendOrder.discount) || 0
  } : undefined,
  status: backendOrder.orderStatus as OrderStatus,
  totalAmount: Number(backendOrder.price) || 0,
  createdAt: backendOrder.createdAt,
  updatedAt: backendOrder.updatedAt || backendOrder.createdAt
});

// Order API
export const ordersApi = {
  // Create a new order
  create: async (data: CreateOrderRequest): Promise<Order> => {
    const response = await orderApiClient.post(API_ENDPOINTS.ORDERS.CREATE, data);
    return transformOrderResponse(response.data);
  },

  // Get orders by customer ID
  getByCustomer: async (customerId: string): Promise<Order[]> => {
    const response = await orderApiClient.get(API_ENDPOINTS.ORDERS.GET_BY_CUSTOMER(customerId));
    return response.data.map(transformOrderResponse);
  },

  // Update an existing order
  update: async (orderId: string, data: UpdateOrderRequest): Promise<Order> => {
    const response = await orderApiClient.put(API_ENDPOINTS.ORDERS.UPDATE(orderId), data);
    return transformOrderResponse(response.data);
  },

  // Add rating to an order
  addRating: async (orderId: string, data: RatingRequest): Promise<void> => {
    await orderApiClient.post(API_ENDPOINTS.ORDERS.ADD_RATING(orderId), data);
  },

  // Get order statistics
  getStatistics: async (params: StatisticsParams): Promise<Statistics> => {
    const response = await orderApiClient.get<Statistics>(API_ENDPOINTS.ORDERS.GET_STATISTICS(params.customerId, params.startDate, params.endDate));
    return response.data;
  },
};

// Payment API - Now using Order Service endpoints
export const paymentsApi = {
  // Process a payment using Order Service
  processPayment: async (data: PaymentRequest): Promise<any> => {
    const response = await orderApiClient.post<any>('/orders/payment', data);
    return response.data;
  },

  // Get payment status
  getPaymentStatus: async (paymentId: string): Promise<Payment> => {
    const response = await paymentApiClient.get<Payment>(API_ENDPOINTS.PAYMENTS.GET_STATUS(paymentId));
    return response.data;
  },

  // SSE payment status monitoring
  monitorPaymentStatus: (orderId: string, onStatusUpdate: (status: string, data?: any) => void, onError?: (error: Event) => void) => {
    const eventSource = new EventSource(`http://localhost:8081/orders/payment/status/${orderId}`);

    eventSource.onopen = () => {
      console.log('SSE connection opened for order:', orderId);
    };

      // Listen for named 'status' events from backend
    eventSource.addEventListener('status', (event) => {
      try {
        const data = JSON.parse(event.data);
        console.log('Payment status update:', data);
        onStatusUpdate(data.status, data);
      } catch (error) {
        console.error('Error parsing SSE message:', error);
        onStatusUpdate('ERROR', { message: 'Invalid status format' });
      }
    });

    eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      if (onError) {
        onError(error);
      } else {
        onStatusUpdate('ERROR', { message: 'Connection error' });
      }
    };

    return eventSource; // Return the EventSource so it can be closed when done
  },
};

// Credit API
export const creditsApi = {
  // Get total credit for a customer
  getTotal: async (customerId: string): Promise<CreditBalance> => {
    const response = await paymentApiClient.get<CreditBalance>(API_ENDPOINTS.CREDITS.GET_BALANCE(customerId));
    return response.data;
  },

  // Add credit to customer account
  addCredit: async (data: AddCreditRequest): Promise<CreditEntry> => {
    const response = await paymentApiClient.post<CreditEntry>(
      `${API_ENDPOINTS.CREDITS.ADD}?customerId=${data.customerId}&amount=${data.amount}`
    );
    return response.data;
  },

  };

// Export API clients for external usage
export { orderApiClient, paymentApiClient };