import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import App from './App';

// Mock the API calls to prevent real network requests during tests
vi.mock('@/lib/api', () => ({
  getMigrationStats: vi.fn().mockResolvedValue({
    total: 42,
    pending: 5,
    running: 3,
    success: 28,
    failed: 4,
    compensated: 2,
    last24h: 12,
  }),
  getMigrations: vi.fn().mockResolvedValue([
    {
      id: "DOC-INV-2026-001",
      docId: "DOC-TEST-001",
      title: "Invoice #8742 - Acme Solutions AG",
      status: "SUCCESS",
      createdAt: "2026-03-24T10:15:30Z",
      updatedAt: "2026-03-24T10:18:45Z",
      pageCount: 5,
      ocrAttempted: true,
      ocrSuccess: true,
    },
    {
      id: "DOC-INV-2026-002",
      docId: "DOC-TEST-002",
      title: "Contract #3921 - Global Pharma AG",
      status: "FAILED",
      createdAt: "2026-03-24T09:45:12Z",
      updatedAt: "2026-03-24T09:50:05Z",
      pageCount: 12,
      ocrAttempted: true,
      ocrSuccess: false,
      failureReason: "Timeout during OCR processing",
    }
  ]),
}));

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      staleTime: Infinity,
    },
  },
});

const renderWithProviders = (ui: React.ReactElement) => {
  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        {ui}
      </BrowserRouter>
    </QueryClientProvider>
  );
};

describe('App Component', () => {
  it('renders dashboard title', () => {
    renderWithProviders(<App />);
    
    expect(screen.getByText(/ChainVault Migration Dashboard/i)).toBeInTheDocument();
  });

  it('renders stats cards', async () => {
    renderWithProviders(<App />);

    expect(await screen.findByText('Total Migrations')).toBeInTheDocument();
    expect(await screen.findByText('Successful')).toBeInTheDocument();
    expect(await screen.findByText('Failed')).toBeInTheDocument();
  });

  it('renders recent migrations table', async () => {
    renderWithProviders(<App />);

    expect(await screen.findByText('Recent Migrations')).toBeInTheDocument();
    expect(await screen.findByText('DOC-INV-2026-001')).toBeInTheDocument();
    expect(await screen.findByText('Invoice #8742 - Acme Solutions AG')).toBeInTheDocument();
  });

  it('shows correct status badges', async () => {
    renderWithProviders(<App />);

    expect(await screen.findByText('SUCCESS')).toBeInTheDocument();
    expect(await screen.findByText('FAILED')).toBeInTheDocument();
  });
});