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

type Badge = {
  color: string
  text: string
}

type BaseNavItem = {
  name: ReactNode
  icon?: ReactNode
  badge?: Badge
}

type NavItem = BaseNavItem & {
  component: typeof CNavItem
  to?: string
  href?: string
}

type NavGroup = BaseNavItem & {
  component: typeof CNavGroup
  to?: string
  items: NavNode[]
}

type NavTitle = {
  component: typeof CNavTitle
  name: string
}

type NavNode = NavItem | NavGroup | NavTitle

/* -------------------------------------------------------------------------- */
/*                                  HELPERS                                   */
/* -------------------------------------------------------------------------- */

const icon = (i: any) => <CIcon icon={i} customClassName="nav-icon" />

const external = (label: string): ReactNode => (
  <>
    {label}
    <CIcon icon={cilExternalLink} size="sm" className="ms-2" />
  </>
)

const proBadge: Badge = { color: 'danger', text: 'PRO' }
const newBadge: Badge = { color: 'info', text: 'NEW' }

/* -------------------------------------------------------------------------- */
/*                                   NAV DATA                                 */
/* -------------------------------------------------------------------------- */

const _nav: NavNode[] = [
  {
    component: CNavItem,
    name: 'Dashboard',
    to: '/dashboard',
    icon: icon(cilSpeedometer),
    badge: newBadge,
  },

  { component: CNavTitle, name: 'Theme' },

  {
    component: CNavItem,
    name: 'Colors',
    to: '/theme/colors',
    icon: icon(cilDrop),
  },
  {
    component: CNavItem,
    name: 'Typography',
    to: '/theme/typography',
    icon: icon(cilPencil),
  },

  { component: CNavTitle, name: 'Components' },

  {
    component: CNavGroup,
    name: 'Base',
    to: '/base',
    icon: icon(cilPuzzle),
    items: [
      { component: CNavItem, name: 'Accordion', to: '/base/accordion' },
      { component: CNavItem, name: 'Breadcrumb', to: '/base/breadcrumbs' },
      {
        component: CNavItem,
        name: external('Calendar'),
        href: 'https://coreui.io/react/docs/components/calendar/',
        badge: proBadge,
      },
      { component: CNavItem, name: 'Cards', to: '/base/cards' },
      { component: CNavItem, name: 'Carousel', to: '/base/carousels' },
      { component: CNavItem, name: 'Chip', to: '/base/chip' },
      { component: CNavItem, name: 'Collapse', to: '/base/collapses' },
      { component: CNavItem, name: 'List group', to: '/base/list-groups' },
      { component: CNavItem, name: 'Navs & Tabs', to: '/base/navs' },
      { component: CNavItem, name: 'Pagination', to: '/base/paginations' },
      { component: CNavItem, name: 'Placeholders', to: '/base/placeholders' },
      { component: CNavItem, name: 'Popovers', to: '/base/popovers' },
      { component: CNavItem, name: 'Progress', to: '/base/progress' },
      {
        component: CNavItem,
        name: 'Smart Pagination',
        href: 'https://coreui.io/react/docs/components/smart-pagination/',
        badge: proBadge,
      },
      {
        component: CNavItem,
        name: external('Smart Table'),
        href: 'https://coreui.io/react/docs/components/smart-table/',
        badge: proBadge,
      },
      { component: CNavItem, name: 'Spinners', to: '/base/spinners' },
      { component: CNavItem, name: 'Tables', to: '/base/tables' },
      { component: CNavItem, name: 'Tabs', to: '/base/tabs' },
      { component: CNavItem, name: 'Tooltips', to: '/base/tooltips' },
      {
        component: CNavItem,
        name: external('Virtual Scroller'),
        href: 'https://coreui.io/react/docs/components/virtual-scroller/',
        badge: proBadge,
      },
    ],
  },

  {
    component: CNavGroup,
    name: 'Buttons',
    to: '/buttons',
    icon: icon(cilCursor),
    items: [
      { component: CNavItem, name: 'Buttons', to: '/buttons/buttons' },
      { component: CNavItem, name: 'Buttons groups', to: '/buttons/button-groups' },
      { component: CNavItem, name: 'Dropdowns', to: '/buttons/dropdowns' },
      {
        component: CNavItem,
        name: external('Loading Button'),
        href: 'https://coreui.io/react/docs/components/loading-button/',
        badge: proBadge,
      },
    ],
  },

  {
    component: CNavGroup,
    name: 'Forms',
    icon: icon(cilNotes),
    items: [
      {
        component: CNavItem,
        name: external('Autocomplete'),
        href: 'https://coreui.io/react/docs/forms/autocomplete/',
        badge: proBadge,
      },
      { component: CNavItem, name: 'Checks & Radios', to: '/forms/checks-radios' },
      { component: CNavItem, name: 'Chip Input', to: '/forms/chip-input' },
      {
        component: CNavItem,
        name: external('Date Picker'),
        href: 'https://coreui.io/react/docs/forms/date-picker/',
        badge: proBadge,
      },
      {
        component: CNavItem,
        name: 'Date Range Picker',
        href: 'https://coreui.io/react/docs/forms/date-range-picker/',
        badge: proBadge,
      },
      { component: CNavItem, name: 'Floating Labels', to: '/forms/floating-labels' },
      { component: CNavItem, name: 'Form Control', to: '/forms/form-control' },
      { component: CNavItem, name: 'Input Group', to: '/forms/input-group' },
      {
        component: CNavItem,
        name: external('Multi Select'),
        href: 'https://coreui.io/react/docs/forms/multi-select/',
        badge: proBadge,
      },
      {
        component: CNavItem,
        name: external('OTP Input'),
        href: 'https://coreui.io/react/docs/forms/one-time-password-input/',
        badge: proBadge,
      },
      {
        component: CNavItem,
        name: external('Password Input'),
        href: 'https://coreui.io/react/docs/forms/password-input/',
        badge: proBadge,
      },
      { component: CNavItem, name: 'Range', to: '/forms/range' },
      {
        component: CNavItem,
        name: external('Range Slider'),
        href: 'https://coreui.io/react/docs/forms/range-slider/',
        badge: proBadge,
      },
      {
        component: CNavItem,
        name: external('Rating'),
        href: 'https://coreui.io/react/docs/forms/rating/',
        badge: proBadge,
      },
      { component: CNavItem, name: 'Select', to: '/forms/select' },
      {
        component: CNavItem,
        name: external('Stepper'),
        href: 'https://coreui.io/react/docs/forms/stepper/',
        badge: proBadge,
      },
      {
        component: CNavItem,
        name: external('Time Picker'),
        href: 'https://coreui.io/react/docs/forms/time-picker/',
        badge: proBadge,
      },
      { component: CNavItem, name: 'Layout', to: '/forms/layout' },
      { component: CNavItem, name: 'Validation', to: '/forms/validation' },
    ],
  },

  {
    component: CNavItem,
    name: 'Charts',
    to: '/charts',
    icon: icon(cilChartPie),
  },

  {
    component: CNavGroup,
    name: 'Icons',
    icon: icon(cilStar),
    items: [
      { component: CNavItem, name: 'CoreUI Free', to: '/icons/coreui-icons' },
      { component: CNavItem, name: 'CoreUI Flags', to: '/icons/flags' },
      { component: CNavItem, name: 'CoreUI Brands', to: '/icons/brands' },
    ],
  },

  {
    component: CNavGroup,
    name: 'Notifications',
    icon: icon(cilBell),
    items: [
      { component: CNavItem, name: 'Alerts', to: '/notifications/alerts' },
      { component: CNavItem, name: 'Badges', to: '/notifications/badges' },
      { component: CNavItem, name: 'Modal', to: '/notifications/modals' },
      { component: CNavItem, name: 'Toasts', to: '/notifications/toasts' },
    ],
  },

  {
    component: CNavItem,
    name: 'Widgets',
    to: '/widgets',
    icon: icon(cilCalculator),
    badge: newBadge,
  },

  { component: CNavTitle, name: 'Extras' },

  {
    component: CNavGroup,
    name: 'Pages',
    icon: icon(cilStar),
    items: [
      { component: CNavItem, name: 'Login', to: '/login' },
      { component: CNavItem, name: 'Register', to: '/register' },
      { component: CNavItem, name: 'Error 404', to: '/404' },
      { component: CNavItem, name: 'Error 500', to: '/500' },
    ],
  },

  {
    component: CNavItem,
    name: 'Docs',
    href: 'https://coreui.io/react/docs/templates/installation/',
    icon: icon(cilDescription),
  },
]

export default _nav
