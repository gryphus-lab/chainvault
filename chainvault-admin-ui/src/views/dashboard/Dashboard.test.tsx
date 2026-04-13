/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { beforeEach, describe, expect, it } from 'vitest'
import { waitFor } from '@testing-library/react'
import Dashboard from './Dashboard'
import { renderWithProviders, resetStore } from '../../test/test-utils'

describe('Dashboard', () => {
  beforeEach(() => {
    resetStore()
  })

  it('renders without throwing', async () => {
    const { container } = renderWithProviders(<Dashboard />)
    await waitFor(() => {
      expect(container.firstChild).toBeTruthy()
    })
  })

  it('renders all table headers', () => {
    const { getByText } = renderWithProviders(<Dashboard />)

    // Verify each header is present
    expect(getByText('#')).toBeTruthy()
    expect(getByText('DocId')).toBeTruthy()
    expect(getByText('Title')).toBeTruthy()
    expect(getByText('Status')).toBeTruthy()
    expect(getByText('Created At')).toBeTruthy()
    expect(getByText('Updated At')).toBeTruthy()
    expect(getByText('View Details')).toBeTruthy()
  })

  it('renders empty state with correct attributes', () => {
    const { getByText } = renderWithProviders(<Dashboard />)

    // Verify empty state text is present
    const emptyStateCell = getByText('No documents available')
    expect(emptyStateCell).toBeTruthy()

    // Verify colSpan attribute
    expect(emptyStateCell.getAttribute('colspan')).toBe('7')

    // Verify className contains expected styling
    const className = emptyStateCell.className
    expect(className).toContain('text-center')
    expect(className).toContain('text-muted')
  })
})
