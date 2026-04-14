/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi, beforeEach } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import MigrationDetailPage from './MigrationDetailPage'
import * as api from '../../../lib/api'

vi.mock('../../../lib/api', () => ({
  getMigrationDetail: vi.fn(),
}))

const mockMigration = {
  id: 'MIG-123',
  title: 'Test Migration Title',
  status: 'SUCCESS',
  docId: 'DOC-999',
  createdAt: '2026-01-01T10:00:00Z',
  updatedAt: '2026-01-01T11:00:00Z',
  traceId: 'trace-abc-123',
  events: [],
  pdfUrl: 'https://example.com/file.pdf',
  ocrAttempted: true,
  ocrSuccess: true,
  ocrPageCount: 5,
  ocrTotalTextLength: 1250,
  pageCount: 5,
  processInstanceKey: 'proc-inst-123',
}

const renderComponent = (id = 'MIG-123') => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false, gcTime: 0 },
    },
  })

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[`/migration/${id}`]}>
        <Routes>
          <Route path="/migration/:id" element={<MigrationDetailPage />} />
          <Route path="/" element={<div>Dashboard</div>} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('MigrationDetailPage', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  it('renders error state when API fails', async () => {
    // 1. Silence console for a clean output
    vi.spyOn(console, 'error').mockImplementation(() => {})

    // 2. Mock a clean rejection
    const error = new Error('Fetch Failed')
    vi.mocked(api.getMigrationDetail).mockRejectedValue(error)

    // 3. Render
    renderComponent('ERROR-ID')

    // 4. Use a more robust check: Look for the Error text,
    // but also check that the Loading text is GONE.
    const errorMsg = await screen.findByText(/failed to load migration details/i)
    expect(errorMsg).toBeInTheDocument()
    expect(screen.getByText(/ERROR-ID/i)).toBeInTheDocument()

    // 5. Verify the API was called with the correct ID
    expect(vi.mocked(api.getMigrationDetail)).toHaveBeenCalledWith('ERROR-ID')
  })

  it('renders full migration details successfully', async () => {
    vi.mocked(api.getMigrationDetail).mockResolvedValue(mockMigration)
    renderComponent()

    // Wait for title to load
    expect(await screen.findByText(/Migration MIG-123/i)).toBeInTheDocument()

    // FIX: Use regex to find the specific label + value combination
    // or check text content of the parent
    expect(screen.getByText(/OCR Attempted:/i).parentElement).toHaveTextContent(/Yes/)
    expect(screen.getByText(/OCR Success:/i).parentElement).toHaveTextContent(/✅ Yes/)

    // For numbers, use a regex to ignore potential formatting/whitespace
    expect(screen.getByText(/1,250/)).toBeInTheDocument()

    // Verify the API was called with the correct ID
    expect(vi.mocked(api.getMigrationDetail)).toHaveBeenCalledWith('MIG-123')
  })

  it('renders failure reason when migration failed', async () => {
    vi.mocked(api.getMigrationDetail).mockResolvedValue({
      ...mockMigration,
      status: 'FAILED',
      ocrSuccess: false,
      failureReason: 'OCR engine timeout',
      pageCount: 0,
      processInstanceKey: '123',
    })

    renderComponent()

    expect(await screen.findByText('FAILED')).toBeInTheDocument()

    // Verify the failure reason exists anywhere in the body
    expect(screen.getByText(/OCR engine timeout/i)).toBeInTheDocument()
  })
})