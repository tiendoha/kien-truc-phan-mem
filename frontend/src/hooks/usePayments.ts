import React, { useState, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { toast } from 'sonner';
import { paymentsApi } from '../services/api';
import { useAppContext } from './useAppContext';
import { PaymentRequest, Payment } from '../types';

// Form validation schema
const paymentFormSchema = z.object({
  orderId: z.string().min(1, 'Order ID is required'),
  customerId: z.string().min(1, 'Customer ID is required'),
  price: z.number().min(0.01, 'Price must be greater than 0'),
});

type PaymentFormData = z.infer<typeof paymentFormSchema>;

export const usePaymentForm = () => {
  const { customerId, orders, refreshData, setLoading, setError } = useAppContext();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const form = useForm<PaymentFormData>({
    resolver: zodResolver(paymentFormSchema),
    defaultValues: {
      orderId: '',
      customerId: customerId || '',
      price: 0,
    },
  });

  // Get available orders for payment (pending orders only)
  const availableOrders = orders.filter(order => order.status === 'PENDING');

  // Update price when order changes
  const updateAmountForOrder = useCallback((orderId: string) => {
    const order = orders.find(o => o.id === orderId);
    if (order) {
      form.setValue('price', order.totalAmount);
    }
  }, [orders, form]);

  
  // Submit payment with SSE status monitoring
  const submitPayment = useCallback(async (data: PaymentFormData) => {
    let toastId: string | number | undefined;

    try {
      setIsSubmitting(true);
      setLoading(true);
      setError(null);

      // Validate that the order belongs to the customer
      const order = orders.find(o => o.id === data.orderId);
      if (!order || order.customerId !== customerId) {
        setError('Invalid order selected');
        toast.error('Invalid order selected');
        return;
      }

      // Validate payment amount
      if (data.price !== order.totalAmount) {
        setError('Payment amount must match order total');
        toast.error('Payment amount must match order total');
        return;
      }

      const paymentRequest: PaymentRequest = {
        orderId: data.orderId,
        customerId: data.customerId,
        price: data.price,
      };

      // Show initial toast when payment starts
      toastId = toast.loading('Processing payment...', {
        description: `Order #${data.orderId.slice(-8)} • Amount: $${data.price.toFixed(2)}`
      });

      // Initiate payment using Order Service endpoint
      const paymentResponse = await paymentsApi.processPayment(paymentRequest);

      // Update toast to show payment is being processed by backend
      toast.loading('Payment submitted, processing...', {
        id: toastId,
        description: 'Connecting to payment service...'
      });

      // Set up SSE connection to monitor payment status
      const eventSource = paymentsApi.monitorPaymentStatus(
        data.orderId,
        (status: string, statusData?: any) => {
          console.log(`Payment status for order ${data.orderId}: ${status}`);

          if (status === 'PROCESSING') {
            // Update toast to show processing status
            toast.loading('Processing payment...', {
              id: toastId,
              description: statusData?.message || 'Payment is being processed...'
            });
          } else if (status === 'COMPLETED') {
            // Payment completed successfully - show success toast
            toast.success('Payment completed successfully!', {
              id: toastId,
              description: `Order #${data.orderId.slice(-8)} • Amount: $${data.price.toFixed(2)}`
            });

            refreshData(); // Refresh orders to get updated status
            eventSource.close();
          } else if (status === 'FAILED') {
            // Payment failed - show error toast
            toast.error('Payment failed', {
              id: toastId,
              description: statusData?.message || 'Payment processing failed'
            });

            setError(statusData?.message || 'Payment failed');
            eventSource.close();
          } else if (status === 'ERROR') {
            // Connection or parsing error - show error toast
            toast.error('Payment status error', {
              id: toastId,
              description: statusData?.message || 'Connection error while monitoring payment status'
            });

            setError(statusData?.message || 'Payment status monitoring error');
            eventSource.close();
          }
        },
        (error: Event) => {
          console.error('SSE connection error:', error);

          // Show connection error toast
          toast.error('Connection error', {
            id: toastId,
            description: 'Lost connection to payment service'
          });

          setError('Connection error while monitoring payment status');
          eventSource.close();
        }
      );

      // Set timeout for SSE connection (e.g., 5 minutes)
      const timeout = setTimeout(() => {
        // Show timeout error toast
        toast.error('Payment processing timeout', {
          id: toastId,
          description: 'Payment took too long to process. Please try again.'
        });

        setError('Payment status monitoring timeout');
        eventSource.close();
      }, 5 * 60 * 1000);

      // Close connection and clear timeout when component unmounts or payment completes
      eventSource.addEventListener('close', () => {
        clearTimeout(timeout);
      });

      // Refresh data to get immediate updates
      await refreshData();

      // Reset form
      form.reset({
        orderId: '',
        customerId: customerId || '',
        price: 0,
      });

      return paymentResponse;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to process payment';

      // Show error toast for general payment failures
      if (toastId) {
        toast.error('Payment failed', {
          id: toastId,
          description: errorMessage
        });
      } else {
        toast.error('Payment failed', {
          description: errorMessage
        });
      }

      setError(errorMessage);
      throw error;
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  }, [customerId, orders, form, refreshData, setLoading, setError]);

  return {
    form,
    submitPayment,
    availableOrders,
    updateAmountForOrder,
    isSubmitting,
  };
};

// Hook for getting payment history
export const usePaymentHistory = () => {
  const { orders } = useAppContext();
  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchPaymentHistory = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      // For now, we'll derive payment info from orders since we don't have a direct endpoint
      // In a real implementation, you would have a payment history endpoint
      const paymentHistory: Payment[] = orders
        .filter(order => order.status === 'PAID' || order.status === 'APPROVED')
        .map(order => ({
          paymentId: `payment-${order.id}`,
          orderId: order.id,
          customerId: order.customerId,
          price: order.totalAmount,
          status: 'COMPLETED' as const,
          message: 'Payment processed successfully',
        }));

      setPayments(paymentHistory);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch payment history';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, [orders]);

  // Auto-fetch when orders change
  React.useEffect(() => {
    fetchPaymentHistory();
  }, [fetchPaymentHistory]);

  return {
    payments,
    loading,
    error,
    refetch: fetchPaymentHistory,
  };
};