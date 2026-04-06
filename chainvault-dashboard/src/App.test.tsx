/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { createTheme } from '@mui/material/styles'

import App from './App'

// --- mocks ---

// mock useMode
/* eslint-disable @typescript-eslint/no-explicit-any */
vi.mock('./theme', async () => {
  const actual = await vi.importActual<any>('./theme')

  return {
    ...actual,
    useMode: vi.fn(),
    ColorModeContext: {
      Provider: ({ children }: any) => <div>{children}</div>,
    },
  }
})

// mock scenes
vi.mock('./scenes', () => ({
  Navbar: () => <div>Navbar Component</div>,
  Dashboard: () => <div>Dashboard Component</div>,
}))

// mock toggled context
vi.mock('./context/ToggledContext', () => ({
  ToggledContext: {
    Provider: ({ children }: any) => <div>{children}</div>,
  },
}))

import { useMode } from './theme'

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    ;(useMode as any).mockReturnValue([createTheme(), { toggleColorMode: vi.fn() }])
  })

  it('renders without crashing', () => {
    render(<App />)

    expect(screen.getByText('Navbar Component')).toBeInTheDocument()
    expect(screen.getByText('Dashboard Component')).toBeInTheDocument()
  })

  it('renders layout container', () => {
    const { container } = render(<App />)

    // MUI Box renders a div — just ensure structure exists
    expect(container.querySelector('div')).toBeTruthy()
  })

  it('uses useMode hook to provide theme', () => {
    render(<App />)

    expect(useMode).toHaveBeenCalled()
  })
})
