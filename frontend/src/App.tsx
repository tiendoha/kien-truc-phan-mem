import { AppProvider } from './hooks/useAppContext';
import { Dashboard } from './components/Dashboard';
import { Toaster } from 'sonner';

function App() {
  return (
    <AppProvider>
      <Dashboard />
      <Toaster
        position="top-right"
        expand={false}
        richColors
        closeButton
      />
    </AppProvider>
  );
}

export default App;