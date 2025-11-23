# Order Management Frontend

A React frontend for the Order Management and Payment System built with TypeScript, Tailwind CSS, and shadcn/ui components.

## Features

- **Order Management**: Create, view, and update orders with dynamic item management
- **Payment Processing**: Process payments for pending orders using available credits
- **Credit Management**: Add credits to customer account and view transaction history
- **Real-time Updates**: Automatic data refresh and error handling
- **Responsive Design**: Mobile-first design with Tailwind CSS
- **Modern UI**: Beautiful shadcn/ui components with dark mode support

## Tech Stack

- **React 18** with TypeScript
- **Vite** for build tooling
- **Tailwind CSS** for styling
- **shadcn/ui** for UI components
- **React Hook Form** with Zod validation
- **Axios** for API communication
- **Lucide React** for icons

## Prerequisites

- Node.js 18+
- npm or yarn
- Backend services running (Order Service on port 8081, Payment Service on port 8082)

## Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

4. Open your browser and navigate to `http://localhost:3000`

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── ui/              # shadcn/ui components
│   │   ├── forms/           # Form components
│   │   ├── Dashboard.tsx    # Main dashboard
│   │   ├── OrderList.tsx    # Order display component
│   │   ├── PaymentProcessor.tsx  # Payment components
│   │   └── CreditManager.tsx     # Credit management
│   ├── hooks/               # Custom React hooks
│   ├── services/            # API client
│   ├── types/               # TypeScript definitions
│   ├── utils/               # Utility functions
│   ├── App.tsx              # Root component
│   └── main.tsx             # Entry point
├── package.json
├── tailwind.config.js
├── vite.config.ts
└── README.md
```

## Configuration

### API Endpoints

The frontend is configured to connect to backend services:

- **Order Service**: `http://localhost:8081` (proxied via `/api/orders`)
- **Payment Service**: `http://localhost:8082` (proxied via `/api/payments`)

### Environment Variables

You can override default configuration with environment variables:

```bash
# For different backend URLs
VITE_ORDER_SERVICE_URL=http://localhost:8081
VITE_PAYMENT_SERVICE_URL=http://localhost:8082
```

## Features in Detail

### 1. Order Management
- Create new orders with multiple items
- Dynamic add/remove items
- Voucher code support (SAVE10, SAVE5, SAVE20)
- Real-time price calculation
- Order status tracking
- Rating system for completed orders

### 2. Payment Processing
- Process payments for pending orders
- Credit-based payment system
- Payment history tracking
- Real-time status updates

### 3. Credit Management
- Add credits to customer account
- View current credit balance
- Complete credit transaction history
- Quick add buttons for common amounts

### 4. Dashboard Features
- Statistics overview (orders, pending, completed, credits)
- Tab-based navigation
- Real-time data synchronization
- Error handling and loading states
- Responsive design

## Hardcoded Values

For demo purposes, the following values are hardcoded:

- **Customer ID**: `550e8400-e29b-41d4-a716-446655440000`
- **Restaurant ID**: `660e8400-e29b-41d4-a716-446655440000`
- **Sample Products**: Pizza, Burger, Salad, Wings, Fries, Drinks
- **Voucher Codes**: SAVE10 (10% off), SAVE5 ($5 off), SAVE20 (20% off)

## API Integration

The frontend integrates with the following backend endpoints:

### Order Service (Port 8081)
- `POST /orders` - Create new order
- `GET /orders?customerId={id}` - Get customer orders
- `PUT /orders/{id}` - Update order
- `POST /orders/{id}/rating` - Add rating to order
- `GET /orders/statistics` - Get order statistics

### Payment Service (Port 8082)
- `POST /payments` - Process payment
- `GET /payments/{id}` - Get payment status
- `GET /credits/balance/{customerId}` - Get credit balance
- `POST /credits/add` - Add credit to account
- `GET /credits/history/{customerId}` - Get credit history

## Development

### Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

### Build Configuration

- **Vite**: Fast development server and optimized builds
- **TypeScript**: Full type safety
- **Tailwind CSS**: Utility-first styling
- **Proxy Configuration**: API requests are proxied to backend services

### Code Quality

- **ESLint**: Code linting and formatting
- **TypeScript**: Type checking and autocomplete
- **React Hook Form**: Form validation and state management
- **Error Boundaries**: Graceful error handling

## Deployment

### Production Build

1. Build the application:
```bash
npm run build
```

2. The build artifacts will be in the `dist/` directory
3. Serve the static files using any web server (Nginx, Apache, etc.)

### Environment Configuration

For production, ensure the following environment variables are set:

```bash
# Backend service URLs
VITE_ORDER_SERVICE_URL=https://your-order-service.com
VITE_PAYMENT_SERVICE_URL=https://your-payment-service.com
```

## Troubleshooting

### Common Issues

1. **API Connection Errors**: Ensure backend services are running on the correct ports
2. **CORS Issues**: Backend services should allow requests from `http://localhost:3000`
3. **Build Errors**: Ensure all dependencies are installed with `npm install`

### Debug Mode

The application includes comprehensive logging for API requests and responses. Check the browser console for detailed debugging information.

## Contributing

1. Follow the existing code style and patterns
2. Use TypeScript for all new components
3. Ensure responsive design with Tailwind CSS
4. Add proper error handling and loading states
5. Test with different screen sizes and devices

## License

This project is part of the Order Management and Payment System.