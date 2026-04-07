/*
 * Copyright (c) 2026. Gryphus Lab
 */
import React from 'react'
import { useLocation } from 'react-router-dom'

import { routes } from '../routes'

import { CBreadcrumb, CBreadcrumbItem } from '@coreui/react'

const AppBreadcrumb = () => {
  const currentLocation = useLocation().pathname

  const getRouteName = (pathname: string, routes: any[]) => {
    const currentRoute = routes.find((route: { path: any }) => route.path === pathname)
    return currentRoute ? currentRoute.name : false
  }

  const getBreadcrumbs = (location: string) => {
    const breadcrumbs = location.split('/').reduce(
      (acc, curr, index, array) => {
        // Skip empty path segments to avoid duplicate Home breadcrumb
        if (curr === '') {
          return acc
        }

        const prevPath = acc.currentPath
        const currentPathname = prevPath === '/' ? `/${curr}` : `${prevPath}/${curr}`

        const routeName = getRouteName(currentPathname, routes)
        if (routeName) {
          acc.list.push({
            pathname: currentPathname,
            name: routeName,
            active: index + 1 === array.length,
          })
        }

        return {
          list: acc.list,
          currentPath: currentPathname,
        }
      },
      { list: [] as { pathname: string; name: any; active: boolean }[], currentPath: '' },
    )

    return breadcrumbs.list
  }

  const breadcrumbs = getBreadcrumbs(currentLocation)

  return (
    <CBreadcrumb className="my-0">
      {/* Explicitly unique key for the Home item */}
      <CBreadcrumbItem key="breadcrumb-home" href="/">
        Home
      </CBreadcrumbItem>

      {breadcrumbs.map((breadcrumb, index) => {
        return (
          <CBreadcrumbItem
            {...(breadcrumb.active ? { active: true } : { href: breadcrumb.pathname })}
            // Combining index and path guarantees uniqueness even if paths repeat
            key={`${breadcrumb.pathname}-${index}`}
          >
            {breadcrumb.name}
          </CBreadcrumbItem>
        )
      })}
    </CBreadcrumb>
  )
}

export default React.memo(AppBreadcrumb)