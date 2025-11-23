import { useState, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { ordersApi } from '../services/api';
import { useAppContext } from './useAppContext';
import { CreateOrderRequest, CreateOrderItemRequest } from '../types';
import { HARDCODED_RESTAURANT_ID, SAMPLE_VOUCHERS } from '../utils/constants';

// Form validation schema
const orderFormSchema = z.object({
  items: z.array(z.object({
    productId: z.string().min(1, 'Product is required'),
    quantity: z.number().min(1, 'Quantity must be at least 1'),
    price: z.number().min(0, 'Price must be positive'),
  })).min(1, 'At least one item is required'),
  voucherCode: z.string().optional(),
});

type OrderFormData = z.infer<typeof orderFormSchema>;

export const useOrderForm = () => {
  const { customerId, refreshData, setLoading, setError } = useAppContext();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const form = useForm<OrderFormData>({
    resolver: zodResolver(orderFormSchema),
    defaultValues: {
      items: [{ productId: '', quantity: 1, price: 0 }],
      voucherCode: '',
    },
  });

  
  // Remove an item from the form
  const removeItem = useCallback((index: number) => {
    const currentItems = form.getValues('items');
    if (currentItems.length > 1) {
      form.setValue('items', currentItems.filter((_, i) => i !== index));
    }
  }, [form]);

  // Calculate total amount
  const calculateTotal = useCallback((items: CreateOrderItemRequest[], voucherCode?: string): number => {
    const itemsTotal = items.reduce((sum, item) => sum + (item.quantity * item.price), 0);

    if (!voucherCode || !SAMPLE_VOUCHERS[voucherCode as keyof typeof SAMPLE_VOUCHERS]) {
      return itemsTotal;
    }

    const voucher = SAMPLE_VOUCHERS[voucherCode as keyof typeof SAMPLE_VOUCHERS];
    let discount = 0;

    if (voucher.discountType === 'PERCENTAGE') {
      discount = itemsTotal * (voucher.discountValue / 100);
    } else {
      discount = Math.min(voucher.discountValue, itemsTotal);
    }

    return Math.max(0, itemsTotal - discount);
  }, []);

  // Submit the order
  const submitOrder = useCallback(async (data: OrderFormData) => {
    try {
      setIsSubmitting(true);
      setLoading(true);
      setError(null);

      // Validate voucher code
      if (data.voucherCode && !SAMPLE_VOUCHERS[data.voucherCode as keyof typeof SAMPLE_VOUCHERS]) {
        setError('Invalid voucher code');
        return;
      }

      const orderRequest: CreateOrderRequest = {
        customerId,
        restaurantId: HARDCODED_RESTAURANT_ID,
        items: data.items.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          price: item.price,
        })),
        voucherCode: data.voucherCode || undefined,
      };

      const newOrder = await ordersApi.create(orderRequest);

      // Refresh data to get the updated order list
      await refreshData();

      // Reset form
      form.reset({
        items: [{ productId: '', quantity: 1, price: 0 }],
        voucherCode: '',
      });

      return newOrder;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to create order';
      setError(errorMessage);
      throw error;
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  }, [customerId, form, refreshData, setLoading, setError]);

  // Update an existing order
  const updateOrder = useCallback(async (orderId: string, data: OrderFormData) => {
    try {
      setIsSubmitting(true);
      setLoading(true);
      setError(null);

      // Validate voucher code
      if (data.voucherCode && !SAMPLE_VOUCHERS[data.voucherCode as keyof typeof SAMPLE_VOUCHERS]) {
        setError('Invalid voucher code');
        return;
      }

      const updateRequest = {
        items: data.items.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          price: item.price,
        })),
        voucherCode: data.voucherCode || undefined,
      };

      const updatedOrder = await ordersApi.update(orderId, updateRequest);

      // Refresh data to get the updated order list
      await refreshData();

      return updatedOrder;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to update order';
      setError(errorMessage);
      throw error;
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  }, [form, refreshData, setLoading, setError]);

  return {
    form,
    removeItem,
    calculateTotal,
    submitOrder,
    updateOrder,
    isSubmitting,
  };
};

// Hook for rating an order
export const useOrderRating = () => {
  const { refreshData, setLoading, setError } = useAppContext();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const rateOrder = useCallback(async (orderId: string, score: number, comment?: string) => {
    try {
      setIsSubmitting(true);
      setLoading(true);
      setError(null);

      await ordersApi.addRating(orderId, { score, comment });
      await refreshData();
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to add rating';
      setError(errorMessage);
      throw error;
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  }, [refreshData, setLoading, setError]);

  return {
    rateOrder,
    isSubmitting,
  };
};