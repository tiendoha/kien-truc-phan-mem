import React from 'react';
import { useFieldArray } from 'react-hook-form';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { useOrderForm } from '@/hooks/useOrders';
import { SAMPLE_PRODUCTS } from '@/utils/constants';
import { Plus, Trash2, Calculator } from 'lucide-react';

export const OrderForm: React.FC = () => {
  const {
    form,
    removeItem,
    calculateTotal,
    submitOrder,
    isSubmitting,
  } = useOrderForm();

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: 'items',
  });

  const items = form.watch('items');
  const voucherCode = form.watch('voucherCode');
  const totalAmount = calculateTotal(items, voucherCode);

  const onSubmit = form.handleSubmit(submitOrder);

  const addNewItem = () => {
    append({ productId: '', quantity: 1, price: 0 });
  };

  const removeItemHandler = (index: number) => {
    remove(index);
    removeItem(index);
  };

  return (
    <Card className="w-full mx-auto">
      <CardHeader>
        <CardTitle>Create New Order</CardTitle>
        <CardDescription>
          Add items to your order and optionally apply a voucher code
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} className="space-y-6">
          {/* Items Section */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <Label className="text-base font-semibold">Order Items</Label>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={addNewItem}
                className="flex items-center gap-2"
              >
                <Plus className="h-4 w-4" />
                Add Item
              </Button>
            </div>

            {fields.map((field, index) => (
              <div key={field.id} className="border rounded-lg p-4 space-y-4 bg-muted/30">
                <div className="flex items-center justify-between">
                  <h4 className="font-medium">Item {index + 1}</h4>
                  {fields.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      onClick={() => removeItemHandler(index)}
                      className="text-destructive hover:text-destructive"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor={`items.${index}.productId`}>Product</Label>
                    <select
                      id={`items.${index}.productId`}
                      {...form.register(`items.${index}.productId`)}
                      className="w-full h-10 px-3 py-2 text-sm border border-input bg-background rounded-md ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                      onChange={(e) => {
                        form.setValue(`items.${index}.productId`, e.target.value);
                        // Update price based on selected product
                        const selectedProduct = SAMPLE_PRODUCTS.find(p => p.id === e.target.value);
                        if (selectedProduct) {
                          form.setValue(`items.${index}.price`, selectedProduct.price);
                        } else {
                          form.setValue(`items.${index}.price`, 0);
                        }
                      }}
                    >
                      <option value="">Select a product</option>
                      {SAMPLE_PRODUCTS.map((product) => (
                        <option key={product.id} value={product.id}>
                          {product.name} - ${product.price.toFixed(2)}
                        </option>
                      ))}
                    </select>
                    {form.formState.errors.items?.[index]?.productId && (
                      <p className="text-sm text-destructive">
                        {form.formState.errors.items[index]?.productId?.message}
                      </p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor={`items.${index}.quantity`}>Quantity</Label>
                    <Input
                      id={`items.${index}.quantity`}
                      type="number"
                      min="1"
                      {...form.register(`items.${index}.quantity`, {
                        valueAsNumber: true,
                      })}
                      onChange={(e) => {
                        const value = parseInt(e.target.value) || 1;
                        form.setValue(`items.${index}.quantity`, value);
                      }}
                    />
                    {form.formState.errors.items?.[index]?.quantity && (
                      <p className="text-sm text-destructive">
                        {form.formState.errors.items[index]?.quantity?.message}
                      </p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor={`items.${index}.price`}>Price ($)</Label>
                    <Input
                      id={`items.${index}.price`}
                      type="number"
                      step="0.01"
                      min="0"
                      readOnly
                      {...form.register(`items.${index}.price`, {
                        valueAsNumber: true,
                      })}
                      className="bg-muted"
                    />
                    <p className="text-xs text-muted-foreground">
                      Subtotal: ${((items[index]?.quantity || 0) * (items[index]?.price || 0)).toFixed(2)}
                    </p>
                  </div>
                </div>
              </div>
            ))}

            {form.formState.errors.items && (
              <p className="text-sm text-destructive">
                {form.formState.errors.items.message}
              </p>
            )}
          </div>

          {/* Voucher Section */}
          <div className="space-y-2">
            <Label htmlFor="voucherCode">Voucher Code (Optional)</Label>
            <div className="flex gap-2">
              <Input
                id="voucherCode"
                placeholder="Enter voucher code"
                {...form.register('voucherCode')}
              />
              <Button
                type="button"
                variant="outline"
                size="icon"
                title="Available vouchers: SAVE10, SAVE5, SAVE20"
              >
                <Calculator className="h-4 w-4" />
              </Button>
            </div>
            <p className="text-xs text-muted-foreground">
              Try: SAVE10 (10% off), SAVE5 ($5 off), or SAVE20 (20% off)
            </p>
          </div>

          {/* Order Summary */}
          <div className="border rounded-lg p-4 bg-muted/20">
            <h4 className="font-semibold mb-2">Order Summary</h4>
            <div className="space-y-1 text-sm">
              <div className="flex justify-between">
                <span>Items ({items.length}):</span>
                <span>${items.reduce((sum, item) => sum + ((item?.quantity || 0) * (item?.price || 0)), 0).toFixed(2)}</span>
              </div>
              {voucherCode && SAMPLE_PRODUCTS.length > 0 && (
                <div className="flex justify-between text-green-600">
                  <span>Discount ({voucherCode}):</span>
                  <span>-${(items.reduce((sum, item) => sum + ((item?.quantity || 0) * (item?.price || 0)), 0) - totalAmount).toFixed(2)}</span>
                </div>
              )}
              <div className="flex justify-between font-semibold text-base border-t pt-2">
                <span>Total:</span>
                <span>${totalAmount.toFixed(2)}</span>
              </div>
            </div>
          </div>

          {/* Submit Button */}
          <Button
            type="submit"
            className="w-full"
            disabled={isSubmitting || items.length === 0 || items.some(item => !item.productId || item.quantity <= 0)}
          >
            {isSubmitting ? 'Creating Order...' : `Create Order - $${totalAmount.toFixed(2)}`}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
};