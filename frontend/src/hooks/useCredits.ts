import { useState, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { creditsApi } from '../services/api';
import { useAppContext } from './useAppContext';
import { AddCreditRequest, CreditEntry } from '../types';

// Form validation schema
const creditFormSchema = z.object({
  amount: z.number().min(1, 'Amount must be at least 1'),
});

type CreditFormData = z.infer<typeof creditFormSchema>;

export const useCreditForm = () => {
  const { customerId, creditBalance, refreshData, setLoading, setError } = useAppContext();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const form = useForm<CreditFormData>({
    resolver: zodResolver(creditFormSchema),
    defaultValues: {
      amount: 0,
    },
  });

  // Add credit to customer account
  const addCredit = useCallback(async (data: CreditFormData) => {
    try {
      setIsSubmitting(true);
      setLoading(true);
      setError(null);

      const creditRequest: AddCreditRequest = {
        customerId,
        amount: data.amount,
        description: 'Credit added',
      };

      const newCreditEntry = await creditsApi.addCredit(creditRequest);

      // Also refresh other data to ensure consistency
      await refreshData();

      // Reset form
      form.reset({
        amount: 0,
      });

      return newCreditEntry;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to add credit';
      setError(errorMessage);
      throw error;
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  }, [customerId, form, refreshData, setLoading, setError]);

  return {
    form,
    addCredit,
    isSubmitting,
    currentBalance: creditBalance?.totalCredit || 0,
  };
};


