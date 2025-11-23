import React from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { useCreditForm } from '@/hooks/useCredits';
import { Wallet } from 'lucide-react';

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};

const formatDate = (dateString: string): string => {
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(dateString));
};

export const CreditForm: React.FC = () => {
  const { form, addCredit, isSubmitting, currentBalance } = useCreditForm();

  const onSubmit = form.handleSubmit((data) => {
    addCredit(data);
  });

  return (
    <Card className="w-full mx-auto">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Wallet className="h-5 w-5" />
          Add Credit
        </CardTitle>
        <CardDescription>
          Add funds to your account for easy ordering
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} className="space-y-4">
          {/* Current Balance */}
          <div className="bg-muted/50 rounded-lg p-4">
            <div className="text-sm text-muted-foreground">Current Balance</div>
            <div className="text-2xl font-bold text-green-600">
              {formatCurrency(currentBalance)}
            </div>
          </div>

          {/* Amount */}
          <div className="space-y-2">
            <Label htmlFor="amount">Amount to Add ($)</Label>
            <Input
              id="amount"
              type="number"
              step="0.01"
              min="1"
              placeholder="Enter amount"
              {...form.register('amount', {
                valueAsNumber: true,
              })}
            />
            {form.formState.errors.amount && (
              <p className="text-sm text-destructive">
                {form.formState.errors.amount.message}
              </p>
            )}
          </div>

  
          {/* Quick Amount Buttons */}
          <div className="space-y-2">
            <Label className="text-sm">Quick Add</Label>
            <div className="grid grid-cols-3 gap-2">
              {[10, 25, 50].map((amount) => (
                <Button
                  key={amount}
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => form.setValue('amount', amount)}
                >
                  ${amount}
                </Button>
              ))}
            </div>
          </div>

          {/* Submit Button */}
          <Button
            type="submit"
            className="w-full"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Adding Credit...' : 'Add Credit'}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
};

export const CreditBalanceCard: React.FC = () => {
  const { currentBalance } = useCreditForm();

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Wallet className="h-5 w-5" />
          Available Credit
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-center space-y-2">
          <div className="text-3xl font-bold text-green-600">
            {formatCurrency(currentBalance)}
          </div>
          <p className="text-sm text-muted-foreground">
            Available for ordering
          </p>
        </div>
      </CardContent>
    </Card>
  );
};

