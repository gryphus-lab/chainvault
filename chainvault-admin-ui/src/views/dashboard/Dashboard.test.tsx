/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { render, screen, waitFor, fireEvent, within } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import Dashboard from './Dashboard'
import * as api from '../../lib/api'
import { Migration, MigrationPage, MigrationStats } from '../../types'

// Mock API module
vi.mock('../../lib/api', () => ({
  getMigrations: vi.fn(),
  getMigrationStats: vi.fn(),
}))

const mockStats: MigrationStats = {
  total: 25, // Forces 3 pages with pageSize 10
  success: 15,
  failed: 5,
  pending: 3,
  running: 2,
  last24h: 0,
}

const mockMigrationItems: Migration[] = [
  {
    id: 'M-001',
    docId: 'DOC-ABC',
    status: 'SUCCESS',
    createdAt: '2026-01-01T10:00:00Z',
    updatedAt: '2026-01-01T11:00:00Z',
    processInstanceKey: '',
    pageCount: 0,
    ocrAttempted: false,
  },
  {
    id: 'M-002',
    docId: 'DOC-XYZ',
    status: 'FAILED',
    createdAt: '2026-01-02T10:00:00Z',
    updatedAt: '2026-01-02T11:00:00Z',
    processInstanceKey: '',
    pageCount: 0,
    ocrAttempted: false,
  },
]

const mockMigrations: MigrationPage = {
  items: mockMigrationItems,
  total: 25,
}

describe('Dashboard Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // Default successful mocks
    vi.mocked(api.getMigrationStats).mockResolvedValue(mockStats)
    vi.mocked(api.getMigrations).mockResolvedValue(mockMigrations)
  })

  const renderDashboard = () =>
    render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>,
    )

  it('displays loading state and then renders statistics', async () => {
    renderDashboard()

    expect(screen.getByText(/loading migration records/i)).toBeInTheDocument()

    await waitFor(() => {
      // Target specific stat widgets by their labels
      const totalWidget = screen.getByText('Total Migrations').closest('.mb-4')
      expect(totalWidget).toHaveTextContent('25')

      const inProgressWidget = screen.getByText('In Progress').closest('.mb-4')
      expect(inProgressWidget).toHaveTextContent('5') // 3 pending + 2 running
    })
  })

  it('renders table data with correct status badges', async () => {
    renderDashboard()

    const row = await screen.findByRole('row', { name: /DOC-ABC/i })
    expect(within(row).getByText('SUCCESS')).toHaveClass('bg-success-light')
    expect(screen.getByText('DOC-XYZ')).toBeInTheDocument()
  })

  it('handles independent API failures using allSettled logic', async () => {
    // Stats fail, but Migrations succeed
    vi.mocked(api.getMigrationStats).mockRejectedValue(new Error('Stats Failed'))
    vi.mocked(api.getMigrations).mockResolvedValue(mockMigrations)

    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    try {
      renderDashboard()

      // Should see stats error alert
      expect(await screen.findByText(/failed to load migration statistics/i)).toBeInTheDocument()
      // Stats widgets should fallback to "Unavailable" per getDisplayValue
      expect(screen.getAllByText('Unavailable')).toHaveLength(4)
      // Table should still render successfully
      expect(screen.getByText('DOC-ABC')).toBeInTheDocument()
    } finally {
      errorSpy.mockRestore()
    }
  })

  it('triggers server-side pagination refetch on page change', async () => {
    renderDashboard()

    await screen.findByText('DOC-ABC')

    // Find the pagination item with the text "2"
    const page2 = screen.getByText('2')
    fireEvent.click(page2)

    // Wait for the second API call to complete
    await waitFor(() => expect(api.getMigrations).toHaveBeenCalledTimes(2))

    // Verify API called with offset 10 (Page 2) and the component's default sort parameters
    expect(api.getMigrations).toHaveBeenLastCalledWith({
      limit: 10,
      offset: 10,
      sortKey: 'createdAt',
      sortDir: 'desc',
    })
  })

  it('cycles through sort states and updates ARIA attributes', async () => {
    renderDashboard()

    // Find the header button for Doc ID
    const sortBtn = await screen.findByLabelText(/sort by doc id/i)
    const headerCell = sortBtn.closest('th')

    // Initial state (from code: default is createdAt desc, so docId is 'none')
    expect(headerCell).toHaveAttribute('aria-sort', 'none')

    // 1st Click: Ascending - should trigger server-side sort
    fireEvent.click(sortBtn)
    expect(headerCell).toHaveAttribute('aria-sort', 'ascending')

    // Verify server-side sorting was triggered with docId asc
    await waitFor(() => {
      expect(api.getMigrations).toHaveBeenCalledWith({
        limit: 10,
        offset: 0,
        sortKey: 'docId',
        sortDir: 'asc',
      })
    })

    // 2nd Click: Descending - should trigger server-side sort
    fireEvent.click(sortBtn)
    expect(headerCell).toHaveAttribute('aria-sort', 'descending')

    // Verify server-side sorting was triggered with docId desc
    await waitFor(() => {
      expect(api.getMigrations).toHaveBeenCalledWith({
        limit: 10,
        offset: 0,
        sortKey: 'docId',
        sortDir: 'desc',
      })
    })
  })

  it('renders ellipsis in pagination when many pages exist', async () => {
    // Mock high total to trigger ellipsis logic (maxPagesToShow = 7)
    vi.mocked(api.getMigrationStats).mockResolvedValue({
      ...mockStats,
      total: 200,
      last24h: 0,
    })
    vi.mocked(api.getMigrations).mockResolvedValue({
      items: mockMigrationItems,
      total: 200,
    })

    renderDashboard()

    await waitFor(() => {
      // Find the disabled ellipsis items
      const ellipses = screen.getAllByText('...')
      expect(ellipses.length).toBeGreaterThan(0)
    })
  })

  it('displays fallback message for empty result sets', async () => {
    vi.mocked(api.getMigrations).mockResolvedValue({ items: [], total: 0 })

    renderDashboard()

    expect(await screen.findByText(/no migration data found/i)).toBeInTheDocument()
  })
})