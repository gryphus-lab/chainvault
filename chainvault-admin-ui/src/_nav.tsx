/*
 * Copyright (c) 2026. Gryphus Lab
 */
import React, { ReactNode } from 'react'
import CIcon from '@coreui/icons-react'
import {
  cilBell,
  cilCalculator,
  cilChartPie,
  cilCursor,
  cilDescription,
  cilDrop,
  cilExternalLink,
  cilNotes,
  cilPencil,
  cilPuzzle,
  cilSpeedometer,
  cilStar,
} from '@coreui/icons'
import { CNavGroup, CNavItem, CNavTitle } from '@coreui/react'

/* -------------------------------------------------------------------------- */
/*                                   TYPES                                    */
/* -------------------------------------------------------------------------- */

type Badge = { color: string; text: string }

interface BaseNavItem {
  name: ReactNode
  icon?: ReactNode
  badge?: Badge
}

type NavItem = BaseNavItem & {
  component: typeof CNavItem
} & ({ to: string; href?: never } | { href: string; to?: never })

type NavGroup = BaseNavItem & {
  component: typeof CNavGroup
  items: NavNode[]
  to?: string
}

type NavTitle = {
  component: typeof CNavTitle
  name: string
}

export type NavNode = NavItem | NavGroup | NavTitle

/* -------------------------------------------------------------------------- */
/*                                  HELPERS                                   */
/* -------------------------------------------------------------------------- */

const navIcon = (name: string | string[]) => <CIcon icon={name} customClassName="nav-icon" />

/**
 * Appends an external link icon to labels
 */
const extLabel = (label: string): ReactNode => (
  <>
    {label}
    <CIcon icon={cilExternalLink} size="sm" className="ms-2" />
  </>
)

const BADGES = {
  PRO: { color: 'danger', text: 'PRO' },
  NEW: { color: 'info', text: 'NEW' },
}

/* -------------------------------------------------------------------------- */
/* FACTORY FUNCTIONS                            */
/* -------------------------------------------------------------------------- */

/**
 * Creates a standard Nav Item
 */
const createItem = (
  name: string,
  to: string,
  iconName?: string | string[],
  badgeKey?: keyof typeof BADGES,
): NavItem => ({
  component: CNavItem,
  name,
  to,
  // iconName is the raw icon object (e.g. cilDrop)
  // navIcon(iconName) returns a JSX Element (<CIcon ... />)
  icon: iconName ? navIcon(iconName) : undefined,
  ...(badgeKey && { badge: BADGES[badgeKey] }),
})


const _nav: NavNode[] = [
  createItem('Dashboard', '/dashboard', cilSpeedometer, 'NEW'),
]

export default _nav
