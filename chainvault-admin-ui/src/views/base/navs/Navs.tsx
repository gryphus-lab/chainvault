/*
 * Copyright (c) 2026. Gryphus Lab
 */
import React, { FC, ReactNode } from 'react'
import {
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CDropdown,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  CNav,
  CNavItem,
  CNavLink,
  CRow,
} from '@coreui/react'
import { DocsComponents, DocsExample } from '../../../components'

/* -------------------------------------------------------------------------- */
/*                                    TYPES                                   */
/* -------------------------------------------------------------------------- */

type NavLinkItem = {
  label: string
  active?: boolean
  disabled?: boolean
}

type NavVariant = 'tabs' | 'pills' | undefined
type NavLayout = 'fill' | 'justified' | undefined

/* -------------------------------------------------------------------------- */
/*                              REUSABLE COMPONENTS                           */
/* -------------------------------------------------------------------------- */

const navItems: NavLinkItem[] = [
  { label: 'Active', active: true },
  { label: 'Link' },
  { label: 'Link' },
  { label: 'Disabled', disabled: true },
]

const renderNavItems = (items: NavLinkItem[]): ReactNode =>
  items.map(({ label, active, disabled }, index) => (
    <CNavItem key={`${label}-${index}`}>
      <CNavLink href="#" active={active} disabled={disabled}>
        {label}
      </CNavLink>
    </CNavItem>
  ))

const NavExample: FC<{
  variant?: NavVariant
  layout?: NavLayout
  className?: string
  as?: 'nav'
  withDropdown?: boolean
}> = ({ variant, layout, className, as, withDropdown }) => (
  <CNav variant={variant} layout={layout} className={className} as={as}>
    {withDropdown ? (
      <>
        <CNavItem>
          <CNavLink href="#" active>
            Active
          </CNavLink>
        </CNavItem>

        <CDropdown variant="nav-item">
          <CDropdownToggle color="secondary">Dropdown</CDropdownToggle>
          <CDropdownMenu>
            <CDropdownItem href="#">Action</CDropdownItem>
            <CDropdownItem href="#">Another action</CDropdownItem>
            <CDropdownItem href="#">Something else</CDropdownItem>
          </CDropdownMenu>
        </CDropdown>

        <CNavItem>
          <CNavLink href="#">Link</CNavLink>
        </CNavItem>
        <CNavItem>
          <CNavLink href="#" disabled>
            Disabled
          </CNavLink>
        </CNavItem>
      </>
    ) : (
      renderNavItems(navItems)
    )}
  </CNav>
)

const Section: FC<{
  title: string
  subtitle?: string
  description?: ReactNode
  example: ReactNode
}> = ({ title, subtitle, description, example }) => (
  <CCol xs={12}>
    <CCard className="mb-4">
      <CCardHeader>
        <strong>{title}</strong> {subtitle && <small>{subtitle}</small>}
      </CCardHeader>
      <CCardBody>
        {description && <p className="text-body-secondary small">{description}</p>}
        {example}
      </CCardBody>
    </CCard>
  </CCol>
)

/* -------------------------------------------------------------------------- */
/*                                   MAIN                                     */
/* -------------------------------------------------------------------------- */

const Navs: FC = () => {
  return (
    <CRow>
      {/* Docs */}
      <CCol xs={12}>
        <DocsComponents href="components/nav-tabs/" />
      </CCol>

      {/* Base */}
      <Section
        title="React Navs"
        subtitle="Base navs"
        description={
          <>
            The base <code>.nav</code> uses flexbox and provides a strong foundation for navigation.
          </>
        }
        example={
          <>
            <DocsExample href="components/nav-tabs#base-nav">
              <NavExample />
            </DocsExample>

            <DocsExample href="components/nav-tabs#base-nav">
              <NavExample as="nav" />
            </DocsExample>
          </>
        }
      />

      {/* Alignment */}
      <Section
        title="React Navs"
        subtitle="Horizontal alignment"
        description="Use flex utilities to align navs."
        example={
          <>
            <DocsExample href="components/nav-tabs#horizontal-alignment">
              <NavExample className="justify-content-center" />
            </DocsExample>

            <DocsExample href="components/nav-tabs#horizontal-alignment">
              <NavExample className="justify-content-end" />
            </DocsExample>
          </>
        }
      />

      {/* Vertical */}
      <Section
        title="React Navs"
        subtitle="Vertical"
        description="Stack navigation using flex-column."
        example={
          <DocsExample href="components/nav-tabs#vertical">
            <NavExample className="flex-column" />
          </DocsExample>
        }
      />

      {/* Tabs */}
      <Section
        title="React Navs"
        subtitle="Tabs"
        description="Use variant='tabs' for tabbed navigation."
        example={
          <DocsExample href="components/nav-tabs#tabs">
            <NavExample variant="tabs" />
          </DocsExample>
        }
      />

      {/* Pills */}
      <Section
        title="React Navs"
        subtitle="Pills"
        description="Use variant='pills' for pill-style nav."
        example={
          <DocsExample href="components/nav-tabs#pills">
            <NavExample variant="pills" />
          </DocsExample>
        }
      />

      {/* Fill / Justified */}
      <Section
        title="React Navs"
        subtitle="Fill and justify"
        description="Use layout='fill' or 'justified' to control width."
        example={
          <>
            <DocsExample href="components/nav-tabs#fill">
              <NavExample variant="pills" layout="fill" />
            </DocsExample>

            <DocsExample href="components/nav-tabs#justify">
              <NavExample variant="pills" layout="justified" />
            </DocsExample>
          </>
        }
      />

      {/* Flex utilities */}
      <Section
        title="React Navs"
        subtitle="Flex utilities"
        description="Responsive stacking using flex utilities."
        example={
          <DocsExample href="components/nav-tabs#flex">
            <NavExample variant="pills" as="nav" className="flex-column flex-sm-row" />
          </DocsExample>
        }
      />

      {/* Tabs + dropdown */}
      <Section
        title="React Navs"
        subtitle="Tabs with dropdowns"
        example={
          <DocsExample href="components/nav-tabs#tabs-dropdown">
            <NavExample withDropdown variant="tabs" />
          </DocsExample>
        }
      />

      {/* Pills + dropdown */}
      <Section
        title="React Navs"
        subtitle="Pills with dropdowns"
        example={
          <DocsExample href="components/nav-tabs#pills-dropdown">
            <NavExample variant="pills" withDropdown />
          </DocsExample>
        }
      />
    </CRow>
  )
}

export default Navs
