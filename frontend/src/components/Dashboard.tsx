import React from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useAppContext } from '@/hooks/useAppContext';
import { OrderForm } from '@/components/forms/OrderForm';
import { OrderList } from '@/components/OrderList';
import { PaymentForm, PaymentHistory } from '@/components/PaymentProcessor';
import { CreditForm, CreditBalanceCard } from '@/components/CreditManager';
import { AlertCircle, ShoppingCart, CreditCard, Wallet, TrendingUp, Edit3, Save, X, RotateCcw } from 'lucide-react';

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};

const StatsCards: React.FC = () => {
  const { orders, creditBalance } = useAppContext();

  // Calculate statistics
  const totalOrders = orders.length;
  const pendingOrders = orders.filter(order => order.status === 'PENDING').length;
  const completedOrders = orders.filter(order => order.status === 'PAID').length;
  
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <Card>
        <CardContent className="p-4">
          <div className="flex items-center space-x-2">
            <ShoppingCart className="h-8 w-8 text-blue-600" />
            <div>
              <p className="text-sm font-medium text-muted-foreground">Total Orders</p>
              <p className="text-2xl font-bold">{totalOrders}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-4">
          <div className="flex items-center space-x-2">
            <TrendingUp className="h-8 w-8 text-yellow-600" />
            <div>
              <p className="text-sm font-medium text-muted-foreground">Pending</p>
              <p className="text-2xl font-bold">{pendingOrders}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-4">
          <div className="flex items-center space-x-2">
            <CreditCard className="h-8 w-8 text-green-600" />
            <div>
              <p className="text-sm font-medium text-muted-foreground">Completed</p>
              <p className="text-2xl font-bold">{completedOrders}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-4">
          <div className="flex items-center space-x-2">
            <Wallet className="h-8 w-8 text-purple-600" />
            <div>
              <p className="text-sm font-medium text-muted-foreground">Available Credit</p>
              <p className="text-2xl font-bold">{formatCurrency(creditBalance?.totalCredit || 0)}</p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

const ErrorAlert: React.FC = () => {
  const { error, setError } = useAppContext();

  if (!error) return null;

  return (
    <Alert className="mb-6 border-red-200 bg-red-50">
      <AlertCircle className="h-4 w-4 text-red-600" />
      <AlertDescription className="text-red-800">
        {error}
        <button
          onClick={() => setError(null)}
          className="ml-2 underline hover:no-underline"
        >
          Dismiss
        </button>
      </AlertDescription>
    </Alert>
  );
};

const WelcomeSection: React.FC = () => {
  const { customerId, refreshData, setCustomerId } = useAppContext();
  const [isEditingCustomerId, setIsEditingCustomerId] = React.useState(false);
  const [tempCustomerId, setTempCustomerId] = React.useState(customerId);
  const [isFromLocalStorage, setIsFromLocalStorage] = React.useState(false);

  React.useEffect(() => {
    // Load initial data when component mounts
    refreshData();

    // Check if customer ID came from localStorage
    if (typeof window !== 'undefined') {
      const savedCustomerId = localStorage.getItem('customerId');
      setIsFromLocalStorage(!!savedCustomerId);
    }
  }, [refreshData]);

  React.useEffect(() => {
    setTempCustomerId(customerId);
  }, [customerId]);

  const handleSaveCustomerId = () => {
    if (tempCustomerId.trim()) {
      setCustomerId(tempCustomerId.trim());
      setIsEditingCustomerId(false);
      setIsFromLocalStorage(true);
      // Refresh data for the new customer
      setTimeout(() => refreshData(), 100);
    }
  };

  const handleCancelEdit = () => {
    setTempCustomerId(customerId);
    setIsEditingCustomerId(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSaveCustomerId();
    } else if (e.key === 'Escape') {
      handleCancelEdit();
    }
  };

  const handleResetCustomerId = () => {
    // Remove from localStorage
    if (typeof window !== 'undefined') {
      localStorage.removeItem('customerId');
    }
    // Set to hardcoded value and refresh
    const hardcodedId = '550e8400-e29b-41d4-a716-446655440000';
    setCustomerId(hardcodedId);
    setIsFromLocalStorage(false);
    setTempCustomerId(hardcodedId);
  };

  return (
    <Card className="mb-6">
      <CardHeader>
        <CardTitle className="text-xl">Welcome to Order Management System</CardTitle>
        <CardDescription className="flex items-center gap-2">
          Customer ID:
          {isEditingCustomerId ? (
            <div className="flex items-center gap-2">
              <input
                type="text"
                value={tempCustomerId}
                onChange={(e) => setTempCustomerId(e.target.value)}
                onKeyDown={handleKeyDown}
                className="px-2 py-1 text-sm border border-input rounded bg-background focus:outline-none focus:ring-2 focus:ring-ring min-w-[200px]"
                placeholder="Enter customer ID"
                autoFocus
              />
              <button
                onClick={handleSaveCustomerId}
                className="p-1 text-green-600 hover:text-green-700 hover:bg-green-50 rounded"
                title="Save customer ID"
              >
                <Save className="h-4 w-4" />
              </button>
              <button
                onClick={handleCancelEdit}
                className="p-1 text-red-600 hover:text-red-700 hover:bg-red-50 rounded"
                title="Cancel"
              >
                <X className="h-4 w-4" />
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <span className={`font-mono text-sm px-2 py-1 rounded ${isFromLocalStorage ? 'bg-green-50 border border-green-200' : 'bg-muted'}`}>
                {customerId}
              </span>
              <button
                onClick={() => {
                  setIsEditingCustomerId(true);
                }}
                className="p-1 text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded"
                title="Edit customer ID"
              >
                <Edit3 className="h-4 w-4" />
              </button>
              {isFromLocalStorage && (
                <button
                  onClick={handleResetCustomerId}
                  className="p-1 text-orange-600 hover:text-orange-700 hover:bg-orange-50 rounded"
                  title="Reset to default"
                >
                  <RotateCcw className="h-4 w-4" />
                </button>
              )}
            </div>
          )}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <p className="text-muted-foreground">
          Manage your orders, process payments, and handle your account credits all in one place.
          Select a tab below to get started.
        </p>
        {isFromLocalStorage && !isEditingCustomerId && (
          <p className="text-xs text-green-600 mt-2">
            💾 Customer ID saved to browser storage (persists across page reloads)
          </p>
        )}
        {isEditingCustomerId && (
          <p className="text-xs text-muted-foreground mt-2">
            💡 Press Enter to save or Escape to cancel
          </p>
        )}
      </CardContent>
    </Card>
  );
};

export const Dashboard: React.FC = () => {
  const { loading } = useAppContext();

  if (loading) {
    return (
      <div className="min-h-screen bg-background p-4">
        <div className="max-w-7xl mx-auto">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
            <p className="text-muted-foreground">Loading your dashboard...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-4">
      <div className="max-w-7xl mx-auto">
        <header className="mb-6">
          <h1 className="text-3xl font-bold">Order Management Dashboard</h1>
          <p className="text-muted-foreground">Manage your orders, payments, and credits</p>
        </header>

        <WelcomeSection />
        <ErrorAlert />
        <StatsCards />

        <Tabs defaultValue="orders" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="orders" className="flex items-center gap-2">
              <ShoppingCart className="h-4 w-4" />
              Orders
            </TabsTrigger>
            <TabsTrigger value="payments" className="flex items-center gap-2">
              <CreditCard className="h-4 w-4" />
              Payments
            </TabsTrigger>
            <TabsTrigger value="credits" className="flex items-center gap-2">
              <Wallet className="h-4 w-4" />
              Credits
            </TabsTrigger>
          </TabsList>

          <TabsContent value="orders" className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div>
                <h2 className="text-xl font-semibold mb-4">Create New Order</h2>
                <OrderForm />
              </div>
              <div>
                <h2 className="text-xl font-semibold mb-4">Your Orders</h2>
                <OrderList />
              </div>
            </div>
          </TabsContent>

          <TabsContent value="payments" className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div>
                <h2 className="text-xl font-semibold mb-4">Process Payment</h2>
                <PaymentForm />
              </div>
              <div>
                <h2 className="text-xl font-semibold mb-4">Payment History</h2>
                <PaymentHistory />
              </div>
            </div>
          </TabsContent>

          <TabsContent value="credits" className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div>
                <h2 className="text-xl font-semibold mb-4">Add Credit</h2>
                <CreditForm />
              </div>
              <div>
                <h2 className="text-xl font-semibold mb-4">Credit Balance</h2>
                <CreditBalanceCard />
              </div>
            </div>
          </TabsContent>

        </Tabs>
      </div>
    </div>
  );
};