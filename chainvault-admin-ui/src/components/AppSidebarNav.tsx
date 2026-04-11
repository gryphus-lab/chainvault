import React, { type ReactNode } from 'react'
import { NavLink } from 'react-router-dom'
import PropTypes from 'prop-types'

import SimpleBar from 'simplebar-react'
import 'simplebar-react/dist/simplebar.min.css'

import { CBadge, CNavLink, CSidebarNav } from '@coreui/react'

type BaseNavConfigItem = {
  component: React.ElementType
  name?: ReactNode
  icon?: ReactNode
  badge?: { color: string; text: string }
  items?: NavConfigItem[]
}

export type NavConfigItem = BaseNavConfigItem & (
  | {
      to: string
      href?: never
    }
  | {
      href: string
      to?: never
    }
  | {
      to?: never
      href?: never
    }
)

type AppSidebarNavProps = {
  items: NavConfigItem[]
}

export const AppSidebarNav = ({ items }: AppSidebarNavProps) => {
  const navLink = (
    name: ReactNode | undefined,
    icon: ReactNode | undefined,
    badge?: NavConfigItem['badge'],
    indent = false,
  ) => {
    return (
      <>
        {icon ||
          (indent && (
            <span className="nav-icon">
              <span className="nav-icon-bullet"></span>
            </span>
          ))}
        {name}
        {badge && (
          <CBadge color={badge.color} className="ms-auto" size="sm">
            {badge.text}
          </CBadge>
        )}
      </>
    )
  }

  const navItem = (item: NavConfigItem, index: number, indent = false) => {
    const { component: NavComponent, name, badge, icon, ...rest } = item
    return (
      <NavComponent as="div" key={index}>
        {rest.to || rest.href ? (
          <CNavLink
            {...(rest.to && { as: NavLink })}
            {...(rest.href && { target: '_blank', rel: 'noopener noreferrer' })}
            {...rest}
          >
            {navLink(name, icon, badge, indent)}
          </CNavLink>
        ) : (
          navLink(name, icon, badge, indent)
        )}
      </NavComponent>
    )
  }

  const navGroup = (item: NavConfigItem, index: number) => {
    const { component: NavComponent, name, icon, items: subItems, to: _to, ...rest } = item
    return (
      <NavComponent compact as="div" key={index} toggler={navLink(name, icon)} {...rest}>
        {subItems?.map((subItem, subIndex) =>
          subItem.items ? navGroup(subItem, subIndex) : navItem(subItem, subIndex, true),
        )}
      </NavComponent>
    )
  }

  return (
    <CSidebarNav as={SimpleBar}>
      {items?.map((item, index) => (item.items ? navGroup(item, index) : navItem(item, index)))}
    </CSidebarNav>
  )
}

AppSidebarNav.propTypes = {
  items: PropTypes.arrayOf(PropTypes.any).isRequired,
}