import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useAppContext } from '@/hooks/useAppContext';
import { useOrderRating } from '@/hooks/useOrders';
import { Order, OrderStatus } from '@/types';
import { SAMPLE_PRODUCTS } from '@/utils/constants';
import { Star, Clock, CheckCircle, XCircle, AlertCircle } from 'lucide-react';

const getStatusIcon = (status: OrderStatus) => {
  switch (status) {
    case 'PENDING':
      return <Clock className="h-4 w-4 text-yellow-500" />;
    case 'PAID':
      return <CheckCircle className="h-4 w-4 text-blue-500" />;
    case 'APPROVED':
      return <CheckCircle className="h-4 w-4 text-green-500" />;
    case 'CANCELLED':
      return <XCircle className="h-4 w-4 text-red-500" />;
    default:
      return <AlertCircle className="h-4 w-4 text-gray-500" />;
  }
};

// Helper function to get product name by ID
const getProductName = (productId: string): string => {
  const product = SAMPLE_PRODUCTS.find(p => p.id === productId);
  return product?.name || 'Unknown Product';
};

const getStatusColor = (status: OrderStatus) => {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    case 'PAID':
      return 'bg-blue-100 text-blue-800 border-blue-200';
    case 'APPROVED':
      return 'bg-green-100 text-green-800 border-green-200';
    case 'CANCELLED':
      return 'bg-red-100 text-red-800 border-red-200';
    default:
      return 'bg-gray-100 text-gray-800 border-gray-200';
  }
};

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

interface RatingFormProps {
  orderId: string;
  onRatingAdded: () => void;
}

const RatingForm: React.FC<RatingFormProps> = ({ orderId, onRatingAdded }) => {
  const { rateOrder, isSubmitting } = useOrderRating();
  const [rating, setRating] = React.useState(5);
  const [comment, setComment] = React.useState('');
  const [isExpanded, setIsExpanded] = React.useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await rateOrder(orderId, rating, comment || undefined);
      setComment('');
      setIsExpanded(false);
      onRatingAdded();
    } catch (error) {
      // Error is handled by the hook
    }
  };

  if (!isExpanded) {
    return (
      <Button
        variant="outline"
        size="sm"
        onClick={() => setIsExpanded(true)}
        className="flex items-center gap-2"
      >
        <Star className="h-4 w-4" />
        Add Rating
      </Button>
    );
  }

  return (
    <div className="border rounded-lg p-4 space-y-3 bg-muted/30">
      <h4 className="font-medium">Rate Your Order</h4>
      <div className="flex items-center gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`h-5 w-5 cursor-pointer transition-colors ${
              star <= rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
            }`}
            onClick={() => setRating(star)}
          />
        ))}
        <span className="ml-2 text-sm text-muted-foreground">({rating}/5)</span>
      </div>
      <textarea
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        placeholder="Share your experience (optional)"
        className="w-full p-2 border border-input rounded-md resize-none h-20 text-sm"
      />
      <div className="flex gap-2">
        <Button
          size="sm"
          onClick={handleSubmit}
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Submitting...' : 'Submit Rating'}
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={() => setIsExpanded(false)}
        >
          Cancel
        </Button>
      </div>
    </div>
  );
};

const OrderCard: React.FC<{ order: Order }> = ({ order }) => {
  const [showRating, setShowRating] = React.useState(false);

  return (
    <Card className="w-full">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <CardTitle className="text-lg">Order #{order.id.slice(-8)}</CardTitle>
            <CardDescription>
              Placed on {formatDate(order.createdAt)}
            </CardDescription>
          </div>
          <Badge className={`flex items-center gap-1 ${getStatusColor(order.status)} hover:bg-yellow-200 cursor-pointer`}>
            {getStatusIcon(order.status)}
            {order.status}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Order Items */}
        <div className="space-y-2">
          <h4 className="font-medium">Items</h4>
          <div className="space-y-1">
            {order.items.map((item, index) => (
              <div key={index} className="flex justify-between text-sm">
                <span>{item.quantity}x {getProductName(item.productId)}</span>
                <span>{formatCurrency(item.subTotal)}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Voucher */}
        {order.voucher && (
          <div className="flex justify-between text-sm text-green-600">
            <span>Voucher ({order.voucher.code})</span>
            <span>-
              {order.voucher.discountType === 'PERCENTAGE'
                ? `${order.voucher.discountValue}%`
                : formatCurrency(order.voucher.discountValue)}
            </span>
          </div>
        )}

        {/* Total */}
        <div className="flex justify-between font-semibold text-base border-t pt-2">
          <span>Total</span>
          <span>{formatCurrency(order.totalAmount)}</span>
        </div>

        {/* Actions */}
        <div className="space-y-3">
          {order.status === 'APPROVED' && !showRating && (
            <RatingForm
              orderId={order.id}
              onRatingAdded={() => setShowRating(true)}
            />
          )}
          {order.status === 'APPROVED' && showRating && (
            <div className="text-sm text-green-600 font-medium">
              ✓ Thank you for rating!
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
};

export const OrderList: React.FC = () => {
  const { orders, loading, error, refreshData } = useAppContext();

  if (loading) {
    return (
      <Card>
        <CardContent className="p-6">
          <div className="text-center">Loading orders...</div>
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
            <Button onClick={refreshData} variant="outline">
              Try Again
            </Button>
          </div>
        </CardContent>
      </Card>
    );
  }

  if (orders.length === 0) {
    return (
      <Card>
        <CardContent className="p-6 text-center">
          <h3 className="text-lg font-medium mb-2">No orders yet</h3>
          <p className="text-muted-foreground mb-4">
            Start by creating your first order!
          </p>
        </CardContent>
      </Card>
    );
  }

  // Sort orders by creation date (newest first)
  const sortedOrders = [...orders].sort((a, b) =>
    new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  );

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Your Orders</h2>
        <Button onClick={refreshData} variant="outline">
          Refresh
        </Button>
      </div>
      <div className="grid gap-4">
        {sortedOrders.map((order) => (
          <OrderCard key={order.id} order={order} />
        ))}
      </div>
    </div>
  );
};