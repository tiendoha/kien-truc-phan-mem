import React, { createContext, useContext, useReducer, useCallback, ReactNode } from 'react';
import { ordersApi, creditsApi } from '../services/api';
import { HARDCODED_CUSTOMER_ID } from '../utils/constants';
import { AppContextType, Order, Payment, CreditBalance } from '../types';

// State interface
interface AppState {
  customerId: string;
  orders: Order[];
  payments: Payment[];
  creditBalance: CreditBalance | null;
  loading: boolean;
  error: string | null;
}

// Action types
type AppAction =
  | { type: 'SET_LOADING'; payload: boolean }
  | { type: 'SET_ERROR'; payload: string | null }
  | { type: 'SET_ORDERS'; payload: Order[] }
  | { type: 'SET_PAYMENTS'; payload: Payment[] }
  | { type: 'SET_CREDIT_BALANCE'; payload: CreditBalance | null }
  | { type: 'ADD_ORDER'; payload: Order }
  | { type: 'UPDATE_ORDER'; payload: Order }
  | { type: 'ADD_PAYMENT'; payload: Payment }
  | { type: 'UPDATE_CREDIT_BALANCE'; payload: CreditBalance }
  | { type: 'SET_CUSTOMER_ID'; payload: string };

// Get initial customer ID from localStorage or use hardcoded value
const getInitialCustomerId = (): string => {
  if (typeof window !== 'undefined') {
    const savedCustomerId = localStorage.getItem('customerId');
    if (savedCustomerId) {
      return savedCustomerId;
    }
  }
  return HARDCODED_CUSTOMER_ID;
};

// Initial state
const initialState: AppState = {
  customerId: getInitialCustomerId(),
  orders: [],
  payments: [],
  creditBalance: null,
  loading: false,
  error: null,
};

// Reducer
const appReducer = (state: AppState, action: AppAction): AppState => {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: action.payload };
    case 'SET_ERROR':
      return { ...state, error: action.payload, loading: false };
    case 'SET_ORDERS':
      return { ...state, orders: action.payload };
    case 'SET_PAYMENTS':
      return { ...state, payments: action.payload };
    case 'SET_CREDIT_BALANCE':
      return { ...state, creditBalance: action.payload };
    case 'ADD_ORDER':
      return { ...state, orders: [...state.orders, action.payload] };
    case 'UPDATE_ORDER':
      return {
        ...state,
        orders: state.orders.map(order =>
          order.id === action.payload.id ? action.payload : order
        ),
      };
    case 'ADD_PAYMENT':
      return { ...state, payments: [...state.payments, action.payload] };
    case 'UPDATE_CREDIT_BALANCE':
      return { ...state, creditBalance: action.payload };
    case 'SET_CUSTOMER_ID':
      return { ...state, customerId: action.payload };
    default:
      return state;
  }
};

// Context
const AppContext = createContext<AppContextType | undefined>(undefined);

// Provider component
export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [state, dispatch] = useReducer(appReducer, initialState);

  // Fetch total credit for a customer
  const fetchTotalCredit = useCallback(async (customerId: string) => {
    try {
      const creditBalance = await creditsApi.getTotal(customerId);

      // Update credit balance with the fetched data
      const updatedCreditBalance: CreditBalance = {
        customerId,
        totalCredit: creditBalance.totalCredit,
        lastUpdated: new Date().toISOString(),
      };

      dispatch({ type: 'SET_CREDIT_BALANCE', payload: updatedCreditBalance });
    } catch (error) {
      console.error('Failed to fetch total credit:', error);
      // Don't throw error, just log it as this is not critical functionality
    }
  }, []);

  // Refresh all data
  const refreshData = useCallback(async () => {
    try {
      dispatch({ type: 'SET_LOADING', payload: true });
      dispatch({ type: 'SET_ERROR', payload: null });

      // Fetch all data in parallel
      const [orders, creditBalance] = await Promise.all([
        ordersApi.getByCustomer(state.customerId),
        creditsApi.getTotal(state.customerId).catch(() => null), // Credit balance might not exist yet
      ]);

      dispatch({ type: 'SET_ORDERS', payload: orders });
      dispatch({ type: 'SET_CREDIT_BALANCE', payload: creditBalance });
    } catch (error) {
      dispatch({
        type: 'SET_ERROR',
        payload: error instanceof Error ? error.message : 'Failed to fetch data',
      });
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  }, [state.customerId]);

  // Fetch total credit whenever customer ID changes
  React.useEffect(() => {
    if (state.customerId) {
      fetchTotalCredit(state.customerId);
    }
  }, [state.customerId, fetchTotalCredit]);

  // Set loading state
  const setLoading = useCallback((loading: boolean) => {
    dispatch({ type: 'SET_LOADING', payload: loading });
  }, []);

  // Set error state
  const setError = useCallback((error: string | null) => {
    dispatch({ type: 'SET_ERROR', payload: error });
  }, []);

  // Set customer ID
  const setCustomerId = useCallback((customerId: string) => {
    dispatch({ type: 'SET_CUSTOMER_ID', payload: customerId });
    // Save to localStorage for persistence
    if (typeof window !== 'undefined') {
      localStorage.setItem('customerId', customerId);
    }
  }, []);

  // Context value
  const value: AppContextType = {
    ...state,
    refreshData,
    setLoading,
    setError,
    setCustomerId,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

// Hook to use the context
export const useAppContext = (): AppContextType => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
};