/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  CCol,
  CPagination,
  CPaginationItem,
  CRow,
  CSpinner,
  CTable,
  CTableBody,
  CTableDataCell,
  CTableHead,
  CTableHeaderCell,
  CTableRow,
  CWidgetStatsB,
} from '@coreui/react'
import { ChevronDown, ChevronsUpDown, ChevronUp } from 'lucide-react'
import { getMigrations, getMigrationStats } from '../../lib/api'
import { Migration, MigrationStats } from '../../types'
import { safeFormat } from '../../lib/utils'

type SortDirection = 'asc' | 'desc' | null

function getBadgeColor(m: any) {
  switch (m.status) {
    case 'SUCCESS':
      return 'bg-success-light text-success'
    case 'FAILED':
      return 'bg-danger-light text-danger'
    default:
      return 'bg-light text-dark'
  }
}

function getTableRows(currentMigrations: any[] | Migration[]) {
  return currentMigrations.length === 0 ? (
    <CTableRow>
      <CTableDataCell colSpan={6} className="text-center py-5 text-muted">
        No migration data found.
      </CTableDataCell>
    </CTableRow>
  ) : (
    currentMigrations.map((m) => (
      <CTableRow key={m.id}>
        <CTableDataCell className="small text-muted">{m.id}</CTableDataCell>
        <CTableDataCell className="fw-semibold">{m.docId}</CTableDataCell>
        <CTableDataCell>
          <span className={`badge ${getBadgeColor(m)}`}>{m.status}</span>
        </CTableDataCell>
        <CTableDataCell>{safeFormat(m.createdAt)}</CTableDataCell>
        <CTableDataCell>{safeFormat(m.updatedAt)}</CTableDataCell>
        <CTableDataCell className="text-center">
          <Link to={`/migration/${m.id}`} className="btn btn-sm btn-outline-primary px-3">
            View Details
          </Link>
        </CTableDataCell>
      </CTableRow>
    ))
  )
}

const Dashboard = () => {
  // Data State
  const [migrations, setMigrations] = useState<Migration[] | null>(null)
  const [migrationStats, setMigrationStats] = useState<MigrationStats | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Pagination & Sort State
  const [currentPage, setCurrentPage] = useState(1)
  const [sortKey, setSortKey] = useState<keyof Migration | null>('createdAt')
  const [sortDir, setSortDir] = useState<SortDirection>('desc')
  const pageSize = 10

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true)
        setError(null)
        // Parallel fetch for dashboard performance
        const [stats, data] = await Promise.all([getMigrationStats(), getMigrations()])
        setMigrationStats(stats)
        setMigrations(data)
      } catch (err) {
        console.error('Dashboard data fetch error:', err)
        setError('Failed to load dashboard data. Please try again later.')
      } finally {
        setIsLoading(false)
      }
    }
    fetchData()
  }, [])

  const getDisplayValue = (value: number | undefined) => {
    if (isLoading) return '—'
    if (error) return 'Unavailable'
    return value?.toString() ?? '0'
  }

  const sortedMigrations = useMemo(() => {
    if (!migrations) return []
    if (!sortKey || !sortDir) return migrations

    return [...migrations].sort((a, b) => {
      const aValue = a[sortKey] ?? ''
      const bValue = b[sortKey] ?? ''

      if (aValue < bValue) return sortDir === 'asc' ? -1 : 1
      if (aValue > bValue) return sortDir === 'asc' ? 1 : -1
      return 0
    })
  }, [migrations, sortKey, sortDir])

  const totalMigrations = sortedMigrations.length
  const totalPages = Math.ceil(totalMigrations / pageSize)
  const currentMigrations = useMemo(() => {
    const start = (currentPage - 1) * pageSize
    return sortedMigrations.slice(start, start + pageSize)
  }, [sortedMigrations, currentPage])

  // Handlers
  const handleSort = (key: keyof Migration) => {
    if (sortKey === key) {
      if (sortDir === 'asc') setSortDir('desc')
      else if (sortDir === 'desc') {
        setSortKey(null)
        setSortDir(null)
      }
    } else {
      setSortKey(key)
      setSortDir('asc')
    }
    setCurrentPage(1) // Reset to first page on new sort
  }

  const getSortIcon = (key: keyof Migration) => {
    if (sortKey !== key) return <ChevronsUpDown size={14} className="ms-1 text-muted opacity-50" />
    return sortDir === 'asc' ? (
      <ChevronUp size={14} className="ms-1 text-primary" />
    ) : (
      <ChevronDown size={14} className="ms-1 text-primary" />
    )
  }

  // Stats Calculations
  const inProgress = (migrationStats?.pending ?? 0) + (migrationStats?.running ?? 0)
  const getPercent = (val?: number) =>
    migrationStats?.total ? ((val ?? 0) / migrationStats.total) * 100 : 0

  return (
    <>
      <CRow className="mb-4">
        <CCol>
          <h2 className="h4 fw-bold">Migration Overview</h2>
        </CCol>
      </CRow>

      {/* Stats Widgets */}
      <CRow>
        <CCol sm={6} xl={3}>
          <CWidgetStatsB
            className="mb-4 shadow-sm"
            progress={{ color: 'primary', value: 100 }}
            title="Total Migrations"
            value={getDisplayValue(migrationStats?.total)}
          />
        </CCol>
        <CCol sm={6} xl={3}>
          <CWidgetStatsB
            className="mb-4 shadow-sm"
            progress={{ color: 'warning', value: getPercent(inProgress) }}
            title="In Progress"
            value={getDisplayValue(inProgress)}
          />
        </CCol>
        <CCol sm={6} xl={3}>
          <CWidgetStatsB
            className="mb-4 shadow-sm"
            progress={{ color: 'success', value: getPercent(migrationStats?.success) }}
            title="Successful"
            value={getDisplayValue(migrationStats?.success)}
          />
        </CCol>
        <CCol sm={6} xl={3}>
          <CWidgetStatsB
            className="mb-4 shadow-sm"
            progress={{ color: 'danger', value: getPercent(migrationStats?.failed) }}
            title="Failed"
            value={getDisplayValue(migrationStats?.failed)}
          />
        </CCol>
      </CRow>

      {/* Main Table Container */}
      <div>
        {error && (
          <div className="alert alert-danger mb-4" role="alert">
            {error}
          </div>
        )}

        <CTable align="middle" responsive hover striped className="mb-0">
          <CTableHead>
            <CTableRow>
              <CTableHeaderCell
                onClick={() => handleSort('id')}
                style={{ cursor: 'pointer', width: '10%' }}
                className="user-select-none"
              >
                ID {getSortIcon('id')}
              </CTableHeaderCell>
              <CTableHeaderCell
                onClick={() => handleSort('docId')}
                style={{ cursor: 'pointer' }}
                className="user-select-none"
              >
                Doc ID {getSortIcon('docId')}
              </CTableHeaderCell>
              <CTableHeaderCell
                onClick={() => handleSort('status')}
                style={{ cursor: 'pointer' }}
                className="user-select-none"
              >
                Status {getSortIcon('status')}
              </CTableHeaderCell>
              <CTableHeaderCell
                onClick={() => handleSort('createdAt')}
                style={{ cursor: 'pointer' }}
                className="user-select-none"
              >
                Created At {getSortIcon('createdAt')}
              </CTableHeaderCell>
              <CTableHeaderCell
                onClick={() => handleSort('updatedAt')}
                style={{ cursor: 'pointer' }}
                className="user-select-none"
              >
                Updated At {getSortIcon('updatedAt')}
              </CTableHeaderCell>
              <CTableHeaderCell className="text-center">Action</CTableHeaderCell>
            </CTableRow>
          </CTableHead>
          <CTableBody>
            {isLoading ? (
              <CTableRow>
                <CTableDataCell colSpan={6} className="text-center py-5">
                  <CSpinner color="primary" size="sm" className="me-2" />
                  Loading migration records...
                </CTableDataCell>
              </CTableRow>
            ) : (
              getTableRows(currentMigrations)
            )}
          </CTableBody>
        </CTable>

        {/* Pagination Footer */}
        {!isLoading && totalPages > 1 && (
          <div className="d-flex justify-content-between align-items-center mt-4">
            <div className="small text-muted">
              Showing <strong>{(currentPage - 1) * pageSize + 1}</strong> to{' '}
              <strong>{Math.min(currentPage * pageSize, totalMigrations)}</strong> of{' '}
              <strong>{totalMigrations}</strong> migrations
            </div>
            <CPagination className="mb-0">
              <CPaginationItem
                disabled={currentPage === 1}
                onClick={() => setCurrentPage(currentPage - 1)}
                style={{ cursor: currentPage === 1 ? 'default' : 'pointer' }}
              >
                Previous
              </CPaginationItem>

              {[...new Array(totalPages)].map((_, i) => (
                <CPaginationItem
                  key={i + 1}
                  active={currentPage === i + 1}
                  onClick={() => setCurrentPage(i + 1)}
                  style={{ cursor: 'pointer' }}
                >
                  {i + 1}
                </CPaginationItem>
              ))}

              <CPaginationItem
                disabled={currentPage === totalPages}
                onClick={() => setCurrentPage(currentPage + 1)}
                style={{ cursor: currentPage === totalPages ? 'default' : 'pointer' }}
              >
                Next
              </CPaginationItem>
            </CPagination>
          </div>
        )}
      </div>
    </>
  )
}

export default Dashboard
