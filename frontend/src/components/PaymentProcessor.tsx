import React from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { usePaymentForm, usePaymentHistory } from '@/hooks/usePayments';
import { CreditCard, Clock, CheckCircle, AlertCircle } from 'lucide-react';

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};


const getStatusIcon = (status: string) => {
  switch (status) {
    case 'PENDING':
      return <Clock className="h-4 w-4 text-yellow-500" />;
    case 'COMPLETED':
      return <CheckCircle className="h-4 w-4 text-green-500" />;
    case 'FAILED':
      return <AlertCircle className="h-4 w-4 text-red-500" />;
    default:
      return <AlertCircle className="h-4 w-4 text-gray-500" />;
  }
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    case 'COMPLETED':
      return 'bg-green-100 text-green-800 border-green-200';
    case 'FAILED':
      return 'bg-red-100 text-red-800 border-red-200';
    default:
      return 'bg-gray-100 text-gray-800 border-gray-200';
  }
};

export const PaymentForm: React.FC = () => {
  const {
    form,
    submitPayment,
    availableOrders,
    updateAmountForOrder,
    isSubmitting,
  } = usePaymentForm();

  const selectedOrderId = form.watch('orderId');
  const selectedOrder = availableOrders.find(order => order.id === selectedOrderId);

  // Auto-update amount when order changes
  React.useEffect(() => {
    if (selectedOrderId) {
      updateAmountForOrder(selectedOrderId);
    }
  }, [selectedOrderId, updateAmountForOrder]);

  const onSubmit = form.handleSubmit(submitPayment);

  return (
    <Card className="w-full mx-auto">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <CreditCard className="h-5 w-5" />
          Process Payment
        </CardTitle>
        <CardDescription>
          Pay for your pending orders using available credits
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} className="space-y-4">
          {/* Order Selection */}
          <div className="space-y-2">
            <Label htmlFor="orderId">Select Order</Label>
            <select
              id="orderId"
              {...form.register('orderId')}
              className="w-full h-10 px-3 py-2 text-sm border border-input bg-background rounded-md ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              onChange={(e) => {
                form.setValue('orderId', e.target.value);
                updateAmountForOrder(e.target.value);
              }}
            >
              <option value="">Choose an order to pay</option>
              {availableOrders.map((order) => (
                <option key={order.id} value={order.id}>
                  Order #{order.id.slice(-8)} - {formatCurrency(order.totalAmount)} - {order.items.length} items
                </option>
              ))}
            </select>
            {form.formState.errors.orderId && (
              <p className="text-sm text-destructive">
                {form.formState.errors.orderId.message}
              </p>
            )}
          </div>

          {/* Customer ID (Hidden Field) */}
          <input
            type="hidden"
            {...form.register('customerId')}
          />

          {/* Payment Amount */}
          <div className="space-y-2">
            <Label htmlFor="price">Payment Amount</Label>
            <Input
              id="price"
              type="number"
              step="0.01"
              min="0"
              readOnly
              {...form.register('price', {
                valueAsNumber: true,
              })}
              className="bg-muted"
            />
            {form.formState.errors.price && (
              <p className="text-sm text-destructive">
                {form.formState.errors.price.message}
              </p>
            )}
          </div>

          {/* Order Details */}
          {selectedOrder && (
            <div className="border rounded-lg p-3 bg-muted/30">
              <h4 className="font-medium mb-2">Order Details</h4>
              <div className="text-sm space-y-1">
                <div className="flex justify-between">
                  <span>Items ({selectedOrder.items.length}):</span>
                  <span>{formatCurrency(selectedOrder.totalAmount)}</span>
                </div>
                {selectedOrder.voucher && (
                  <div className="flex justify-between text-green-600 text-xs">
                    <span>Discount ({selectedOrder.voucher.code}):</span>
                    <span>-
                      {selectedOrder.voucher.discountType === 'PERCENTAGE'
                        ? `${selectedOrder.voucher.discountValue}%`
                        : formatCurrency(selectedOrder.voucher.discountValue)}
                    </span>
                  </div>
                )}
                <div className="flex justify-between font-medium border-t pt-1">
                  <span>Total:</span>
                  <span>{formatCurrency(selectedOrder.totalAmount)}</span>
                </div>
              </div>
            </div>
          )}

          {/* Submit Button */}
          <Button
            type="submit"
            className="w-full"
            disabled={isSubmitting || !selectedOrderId}
          >
            {isSubmitting ? 'Processing Payment...' : `Pay ${selectedOrder ? formatCurrency(selectedOrder.totalAmount) : '$0.00'}`}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
};

export const PaymentHistory: React.FC = () => {
  const { payments, loading, error, refetch } = usePaymentHistory();

  if (loading) {
    return (
      <Card>
        <CardContent className="p-6">
          <div className="text-center">Loading payment history...</div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardContent className="p-6">
          <div className="text-center text-red-600">Error: {error}</div>
          <div className="text-center mt-2">
            <Button onClick={refetch} variant="outline">
              Try Again
            </Button>
          </div>
        </CardContent>
      </Card>
    );
  }

  if (payments.length === 0) {
    return (
      <Card>
        <CardContent className="p-6 text-center">
          <h3 className="text-lg font-medium mb-2">No payment history</h3>
          <p className="text-muted-foreground">
            Your completed payments will appear here.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Payment History</h2>
        <Button onClick={refetch} variant="outline">
          Refresh
        </Button>
      </div>
      <div className="grid gap-4">
        {payments.map((payment) => (
          <Card key={payment.paymentId}>
            <CardContent className="p-4">
              <div className="flex items-center justify-between">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="font-medium">Payment #{payment.paymentId.slice(-8)}</span>
                    <Badge className={`flex items-center gap-1 ${getStatusColor(payment.status)}`}>
                      {getStatusIcon(payment.status)}
                      {payment.status}
                    </Badge>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    Order #{payment.orderId.slice(-8)} • Customer #{payment.customerId.slice(-8)}
                  </p>
                  <p className="text-sm text-green-600">
                    {payment.message}
                  </p>
                </div>
                <div className="text-right">
                  <div className="text-lg font-semibold">
                    {formatCurrency(payment.price)}
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};